package net.modderg.thedigimod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
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
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.block.BlocksInit;
import net.modderg.thedigimod.config.ModClientConfigs;
import net.modderg.thedigimod.config.ModCommonConfigs;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.InitDigimons;
import net.modderg.thedigimod.gui.inventory.InitMenu;
import net.modderg.thedigimod.goods.AbstractTrainingGood;
import net.modderg.thedigimod.goods.InitGoods;
import net.modderg.thedigimod.item.*;
import net.modderg.thedigimod.packet.PacketInit;
import net.modderg.thedigimod.particles.DigitalParticles;
import net.modderg.thedigimod.projectiles.InitProjectiles;
import net.modderg.thedigimod.sound.DigiSounds;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.antlr.runtime.debug.DebugEventListener.PROTOCOL_VERSION;

@Mod(TheDigiMod.MOD_ID)
public class TheDigiMod {
    public static final String MOD_ID = "thedigimod";

    public TheDigiMod() {

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);
        bus.addListener(this::setAttributes);

        DigitalCreativeTab.CREATIVE_TABS.register(bus);

        InitItems.register(bus);
        BabyDigimonItems.register(bus);
        DigivicesItems.register(bus);
        AdminItems.register(bus);

        BlocksInit.BLOCKS.register(bus);

        InitProjectiles.register(bus);

        InitGoods.register(bus);

        InitDigimons.register(bus);

        DigiSounds.SOUNDS.register(bus);

        DigitalParticles.PARTICLE_TYPES.register(bus);

        MinecraftForge.EVENT_BUS.register(this);

        InitMenu.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ModClientConfigs.SPEC, "the-digimod-client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModCommonConfigs.SPEC, "the-digimod-common.toml");

        bus.addListener(this::addCreativeTab);
    }

    private void setup(final FMLCommonSetupEvent event) {

        event.enqueueWork(PacketInit::register);
    }

    private void setAttributes(final EntityAttributeCreationEvent event) {

        InitDigimons.DIGIMONS.getEntries()
                .forEach(digimon -> event.put((EntityType<? extends LivingEntity>) digimon.get(),
                        CustomDigimon.setCustomAttributes().build()));

        InitGoods.GOODS.getEntries()
                .forEach(good -> event.put((EntityType<? extends LivingEntity>) good.get(),
                        AbstractTrainingGood.setCustomAttributes().build()));
    }

    private void addCreativeTab(BuildCreativeModeTabContentsEvent event){
        if(event.getTab() == DigitalCreativeTab.DIGITAL_TAB.get()){

            for(RegistryObject<Item> item : DigivicesItems.vicesMap.values())
                event.accept(item.get());

            event.accept(InitItems.DIGIMON_CARD);
            event.accept(InitItems.DIGIMON_BLUE_CARD);

            for(RegistryObject<Item> item : BabyDigimonItems.babiesMap.values())
                event.accept(item.get());

            event.accept(InitItems.DIGI_MEAT);
            event.accept(InitItems.BIG_DIGI_MEAT);
            event.accept(InitItems.ROTTEN_DIGI_MEAT);
            event.accept(InitItems.GUILMON_BREAD);
            event.accept(InitItems.DIGI_CAKE);

            event.accept(InitItems.BLACK_DIGITRON);
            event.accept(InitItems.DARK_TOWER_SHARD);

            event.accept(InitItems.ATTACK_BYTE);
            event.accept(InitItems.DEFENSE_BYTE);
            event.accept(InitItems.SPATTACK_BYTE);
            event.accept(InitItems.SPDEFENSE_BYTE);
            event.accept(InitItems.HEALTH_BYTE);

            event.accept(InitItems.TRAINING_BAG);
            event.accept(InitItems.BAG_ITEM);
            event.accept(InitItems.TABLE_ITEM);
            event.accept(InitItems.TARGET_ITEM);
            event.accept(InitItems.SHIELD_ITEM);
            event.accept(InitItems.UPDATE_ITEM);
            event.accept(InitItems.DRAGON_BONE_ITEM);
            event.accept(InitItems.BALL_GOOD_ITEM);
            event.accept(InitItems.CLOWN_BOX);
            event.accept(InitItems.FLYTRAP_GOOD);
            event.accept(InitItems.OLD_PC_GOOD);
            event.accept(InitItems.LIRA_GOOD);
            event.accept(InitItems.RED_FREEZER);
            event.accept(InitItems.WIND_VANE);
            event.accept(InitItems.TRAINING_ROCK);
            event.accept(InitItems.M2_HEALTH_DISK);

            event.accept(InitItems.DIGI_CORE);

            event.accept(InitItems.CHIP_BULLET);
            event.accept(InitItems.CHIP_PEPPER_BREATH);
            event.accept(InitItems.CHIP_MEGA_FLAME);
            event.accept(InitItems.CHIP_V_ARROW);
            event.accept(InitItems.CHIP_GOLD_ARROW);
            event.accept(InitItems.CHIP_HYPER_HEAT);
            event.accept(InitItems.CHIP_METEOR_WING);
            event.accept(InitItems.CHIP_BEAST_SLASH);
            event.accept(InitItems.CHIP_INK_GUN);
            event.accept(InitItems.CHIP_SNOW_BULLET);
            event.accept(InitItems.CHIP_PETIT_THUNDER);
            event.accept(InitItems.CHIP_OCEAN_STORM);
            event.accept(InitItems.CHIP_MEGA_BLASTER);
            event.accept(InitItems.CHIP_THUNDERBOLT);
            event.accept(InitItems.CHIP_GATLING_ARM);
            event.accept(InitItems.CHIP_DEADLY_STING);
            event.accept(InitItems.CHIP_TRON_FLAME);
            event.accept(InitItems.CHIP_DEATH_CLAW);
            event.accept(InitItems.CHIP_POISON_BREATH);
            event.accept(InitItems.CHIP_HEAVENS_KNUCKLE);
            event.accept(InitItems.CHIP_HOLY_SHOOT);
            event.accept(InitItems.CHIP_GLIDING_ROCKS);
            event.accept(InitItems.CHIP_POOP_THROW);
            event.accept(InitItems.CHIP_SAND_BLAST);
            event.accept(InitItems.CHIP_BEAR_PUNCH);
            event.accept(InitItems.CHIP_PETIT_TWISTER);
            event.accept(InitItems.CHIP_NIGHT_OF_FIRE);
            event.accept(InitItems.CHIP_DISC_ATTACK);
            event.accept(InitItems.CHIP_SMILEY_BOMB);
            event.accept(InitItems.CHIP_GIGA_DESTROYER);
            event.accept(InitItems.CHIP_DOCTASE);
            event.accept(InitItems.CHIP_MAGMA_SPIT);
            event.accept(InitItems.CHIP_CRYSTAL_RAIN);
            event.accept(InitItems.CHIP_LOVE_SONG);
            event.accept(InitItems.CHIP_MACH_TORNADO);
            event.accept(InitItems.CHIP_DIVINE_AXE);
            event.accept(InitItems.CHIP_CRUEL_SISSORS);
            event.accept(InitItems.CHIP_X_ATTACK);
            event.accept(InitItems.CHIP_WORLD_SLASH);

            event.accept(BlocksInit.DIGITAMA_DRAGON_ITEM);
            event.accept(BlocksInit.DIGITAMA_BEAST_ITEM);
            event.accept(BlocksInit.DIGITAMA_HOLY_ITEM);
            event.accept(BlocksInit.DIGITAMA_PLANTINSECT_ITEM);
            event.accept(BlocksInit.DIGITAMA_NIGHTMARE_ITEM);
            event.accept(BlocksInit.DIGITAMA_WIND_ITEM);
            event.accept(BlocksInit.DIGITAMA_EARTH_ITEM);
            event.accept(BlocksInit.DIGITAMA_AQUAN_ITEM);
            event.accept(BlocksInit.DIGITAMA_MACHINE_ITEM);
        }
        if(event.getTab() == DigitalCreativeTab.ADMIN_TAB.get()){
            event.accept(AdminItems.ATTACK_GB);
            event.accept(AdminItems.SPATTACK_GB);
            event.accept(AdminItems.DEFENCE_GB);
            event.accept(AdminItems.SPDEFENCE_GB);
            event.accept(AdminItems.HEALTH_DRIVES);
            event.accept(AdminItems.BATTLE_CHIP);
            event.accept(AdminItems.TAMER_LEASH);
            event.accept(AdminItems.GOBLIMON_BAT);
            event.accept(AdminItems.BOSS_CUBE);

            event.accept(AdminItems.DRAGON_DATA);
            event.accept(AdminItems.BEAST_DATA);
            event.accept(AdminItems.PLANTINSECT_DATA);
            event.accept(AdminItems.AQUAN_DATA);
            event.accept(AdminItems.WIND_DATA);
            event.accept(AdminItems.MACHINE_DATA);
            event.accept(AdminItems.EARTH_DATA);
            event.accept(AdminItems.NIGHTMARE_DATA);
            event.accept(AdminItems.HOLY_DATA);
            event.accept(AdminItems.DRAGON_PACK);
            event.accept(AdminItems.BEAST_PACK);
            event.accept(AdminItems.PLANTINSECT_PACK);
            event.accept(AdminItems.AQUAN_PACK);
            event.accept(AdminItems.WIND_PACK);
            event.accept(AdminItems.MACHINE_PACK);
            event.accept(AdminItems.EARTH_PACK);
            event.accept(AdminItems.NIGHTMARE_PACK);
            event.accept(AdminItems.HOLY_PACK);
        }
    }

    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, MOD_ID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int messageID = 0;

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        messageID++;
    }
}
