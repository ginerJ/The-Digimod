package net.modderg.thedigimod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.modderg.thedigimod.server.block.TDBlocks;
import net.modderg.thedigimod.client.TDClientConfig;
import net.modderg.thedigimod.server.TDConfig;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import net.modderg.thedigimod.server.entity.TDEntities;
import net.modderg.thedigimod.client.gui.inventory.InitMenu;
import net.modderg.thedigimod.server.events.EventsBus;
import net.modderg.thedigimod.server.goods.AbstractTrainingGood;
import net.modderg.thedigimod.server.goods.InitGoods;
import net.modderg.thedigimod.server.item.*;
import net.modderg.thedigimod.server.packet.PacketInit;
import net.modderg.thedigimod.client.particles.DigitalParticles;
import net.modderg.thedigimod.server.projectiles.InitProjectiles;
import net.modderg.thedigimod.server.sound.DigiSounds;
import net.modderg.thedigimod.server.worldgen.DMBiomeModifier;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.antlr.runtime.debug.DebugEventListener.PROTOCOL_VERSION;

@Mod(TheDigiMod.MOD_ID)
public class TheDigiMod {
    public static final String MOD_ID = "thedigimod";

    public static final int MAX_DIGIMON_STAGE = 4;

    public TheDigiMod() {

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);
        bus.addListener(this::setAttributes);

        DigitalCreativeTab.CREATIVE_TABS.register(bus);

        TDBlocks.BLOCKS.register(bus);

        TDEntities.register(bus);

        TDItems.register(bus);
        TDItemsDigivices.register(bus);
        TDItemsAdmin.register(bus);

        InitProjectiles.register(bus);

        InitGoods.register(bus);

        DMBiomeModifier.BIOME_MODIFIERS.register(bus);

        DigiSounds.SOUNDS.register(bus);

        DigitalParticles.PARTICLE_TYPES.register(bus);

        MinecraftForge.EVENT_BUS.register(this);

        InitMenu.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TDClientConfig.SPEC, "the-digimod-client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TDConfig.SPEC, "the-digimod-common.toml");

        bus.addListener(this::addCreativeTab);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(PacketInit::register);
        ComposterBlock.COMPOSTABLES.put(TDItems.POOP.get(), 0.7f);
        ComposterBlock.COMPOSTABLES.put(TDItems.GOLD_POOP.get(), 1f);

        TheDigiMod.addNetworkMessage(EventsBus.PlayerVariablesSyncMessage.class, EventsBus.PlayerVariablesSyncMessage::buffer, EventsBus.PlayerVariablesSyncMessage::new, EventsBus.PlayerVariablesSyncMessage::handler);
    }

    private void setAttributes(final EntityAttributeCreationEvent event) {

        TDEntities.DIGIMONS.getEntries()
                .forEach(digimon -> event.put((EntityType<? extends LivingEntity>) digimon.get(),
                        DigimonEntity.setCustomAttributes().build()));

        InitGoods.GOODS.getEntries()
                .forEach(good -> event.put((EntityType<? extends LivingEntity>) good.get(),
                        AbstractTrainingGood.setCustomAttributes().build()));
    }

    private void addCreativeTab(BuildCreativeModeTabContentsEvent event){
        if(event.getTab() == DigitalCreativeTab.DIGITAL_TAB.get()){

            TDItemsDigivices.DIGIVICES.getEntries().forEach(event::accept);

            event.accept(TDBlocks.CARD_ORE_ITEM);
            event.accept(TDBlocks.CARD_DEEPSLATE_ORE_ITEM);
            event.accept(TDBlocks.HUANGLONG_DEEPSLATE_ORE_ITEM);
            event.accept(TDItems.HUANGLONG_ORE);
            event.accept(TDItems.CHRONDIGIZOIT);
            event.accept(TDItems.CHROME_DIGIZOID);
            event.accept(TDItems.DIGIMON_CARD);
            event.accept(TDItems.DIGIMON_BLUE_CARD);

            TDEntities.BABIES.getEntries().forEach(event::accept);

            event.accept(TDItems.DIGI_MEAT_ROTTEN);
            event.accept(TDItems.DIGI_MEAT);
            event.accept(TDItems.DIGI_MEAT_BIG);
            event.accept(TDItems.DIGI_RIBS);
            event.accept(TDItems.GUILMON_BREAD);
            event.accept(TDItems.DIGI_CAKE);
            event.accept(TDItems.DIGI_SUSHI);
            event.accept(TDBlocks.LED_SHROOM_ITEM);
            event.accept(TDItems.POOP);
            event.accept(TDItems.GOLD_POOP);

            event.accept(TDItems.BLACK_DIGITRON);
            event.accept(TDItems.DARK_TOWER_SHARD);

            event.accept(TDItems.ATTACK_BYTE);
            event.accept(TDItems.DEFENSE_BYTE);
            event.accept(TDItems.SPATTACK_BYTE);
            event.accept(TDItems.SPDEFENSE_BYTE);
            event.accept(TDItems.HEALTH_BYTE);

            event.accept(TDItems.TRAINING_BAG);
            event.accept(TDItems.BAG_ITEM);
            event.accept(TDItems.TABLE_ITEM);
            event.accept(TDItems.TARGET_ITEM);
            event.accept(TDItems.SHIELD_ITEM);
            event.accept(TDItems.UPDATE_ITEM);
            event.accept(TDItems.DRAGON_BONE_ITEM);
            event.accept(TDItems.BALL_GOOD_ITEM);
            event.accept(TDItems.CLOWN_BOX);
            event.accept(TDItems.FLYTRAP_GOOD);
            event.accept(TDItems.OLD_PC_GOOD);
            event.accept(TDItems.LIRA_GOOD);
            event.accept(TDItems.RED_FREEZER);
            event.accept(TDItems.WIND_VANE);
            event.accept(TDItems.TRAINING_ROCK);
            event.accept(TDItems.M2_HEALTH_DISK);

            event.accept(TDItems.DIGI_CORE);

            event.accept(TDItems.CHIP_BULLET);
            event.accept(TDItems.CHIP_PEPPER_BREATH);
            event.accept(TDItems.CHIP_MEGA_FLAME);
            event.accept(TDItems.CHIP_V_ARROW);
            event.accept(TDItems.CHIP_GOLD_ARROW);
            event.accept(TDItems.CHIP_HYPER_HEAT);
            event.accept(TDItems.CHIP_METEOR_WING);
            event.accept(TDItems.CHIP_BEAST_SLASH);
            event.accept(TDItems.CHIP_INK_GUN);
            event.accept(TDItems.CHIP_SNOW_BULLET);
            event.accept(TDItems.CHIP_PETIT_THUNDER);
            event.accept(TDItems.CHIP_OCEAN_STORM);
            event.accept(TDItems.CHIP_MEGA_BLASTER);
            event.accept(TDItems.CHIP_THUNDERBOLT);
            event.accept(TDItems.CHIP_GATLING_ARM);
            event.accept(TDItems.CHIP_DEADLY_STING);
            event.accept(TDItems.CHIP_TRON_FLAME);
            event.accept(TDItems.CHIP_DEATH_CLAW);
            event.accept(TDItems.CHIP_POISON_BREATH);
            event.accept(TDItems.CHIP_HEAVENS_KNUCKLE);
            event.accept(TDItems.CHIP_HOLY_SHOOT);
            event.accept(TDItems.CHIP_GLIDING_ROCKS);
            event.accept(TDItems.CHIP_POOP_THROW);
            event.accept(TDItems.CHIP_SAND_BLAST);
            event.accept(TDItems.CHIP_BEAR_PUNCH);
            event.accept(TDItems.CHIP_PETIT_TWISTER);
            event.accept(TDItems.CHIP_NIGHT_OF_FIRE);
            event.accept(TDItems.CHIP_DISC_ATTACK);
            event.accept(TDItems.CHIP_SMILEY_BOMB);
            event.accept(TDItems.CHIP_GIGA_DESTROYER);
            event.accept(TDItems.CHIP_DOCTASE);
            event.accept(TDItems.CHIP_MAGMA_SPIT);
            event.accept(TDItems.CHIP_CRYSTAL_RAIN);
            event.accept(TDItems.CHIP_LOVE_SONG);
            event.accept(TDItems.CHIP_MACH_TORNADO);
            event.accept(TDItems.CHIP_DIVINE_AXE);
            event.accept(TDItems.CHIP_CRUEL_SISSORS);
            event.accept(TDItems.CHIP_X_ATTACK);
            event.accept(TDItems.CHIP_WORLD_SLASH);
            event.accept(TDItems.CHIP_SECRET_IDENTITY);

            event.accept(TDBlocks.DIGITAMA_DRAGON_ITEM);
            event.accept(TDBlocks.DIGITAMA_BEAST_ITEM);
            event.accept(TDBlocks.DIGITAMA_HOLY_ITEM);
            event.accept(TDBlocks.DIGITAMA_PLANTINSECT_ITEM);
            event.accept(TDBlocks.DIGITAMA_NIGHTMARE_ITEM);
            event.accept(TDBlocks.DIGITAMA_WIND_ITEM);
            event.accept(TDBlocks.DIGITAMA_EARTH_ITEM);
            event.accept(TDBlocks.DIGITAMA_AQUAN_ITEM);
            event.accept(TDBlocks.DIGITAMA_MACHINE_ITEM);
        }
        if(event.getTab() == DigitalCreativeTab.ADMIN_TAB.get()){
            event.accept(TDItemsAdmin.ATTACK_GB);
            event.accept(TDItemsAdmin.SPATTACK_GB);
            event.accept(TDItemsAdmin.DEFENCE_GB);
            event.accept(TDItemsAdmin.SPDEFENCE_GB);
            event.accept(TDItemsAdmin.HEALTH_DRIVES);
            event.accept(TDItemsAdmin.BATTLE_CHIP);
            event.accept(TDItemsAdmin.TAMER_LEASH);
            event.accept(TDItemsAdmin.GOBLIMON_BAT);
            event.accept(TDItemsAdmin.BOSS_CUBE);

            event.accept(TDItemsAdmin.DRAGON_DATA);
            event.accept(TDItemsAdmin.BEAST_DATA);
            event.accept(TDItemsAdmin.PLANTINSECT_DATA);
            event.accept(TDItemsAdmin.AQUAN_DATA);
            event.accept(TDItemsAdmin.WIND_DATA);
            event.accept(TDItemsAdmin.MACHINE_DATA);
            event.accept(TDItemsAdmin.EARTH_DATA);
            event.accept(TDItemsAdmin.NIGHTMARE_DATA);
            event.accept(TDItemsAdmin.HOLY_DATA);
            event.accept(TDItemsAdmin.POOP_DATA);
            event.accept(TDItemsAdmin.DRAGON_PACK);
            event.accept(TDItemsAdmin.BEAST_PACK);
            event.accept(TDItemsAdmin.PLANTINSECT_PACK);
            event.accept(TDItemsAdmin.AQUAN_PACK);
            event.accept(TDItemsAdmin.WIND_PACK);
            event.accept(TDItemsAdmin.MACHINE_PACK);
            event.accept(TDItemsAdmin.EARTH_PACK);
            event.accept(TDItemsAdmin.NIGHTMARE_PACK);
            event.accept(TDItemsAdmin.HOLY_PACK);
            event.accept(TDItemsAdmin.POOP_PACK);
        }
    }

    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, MOD_ID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int messageID = 0;

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        messageID++;
    }
}
