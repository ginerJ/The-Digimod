package net.modderg.thedigimod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.block.DigiBlocks;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.DigitalEntities;
import net.modderg.thedigimod.goods.*;
import net.modderg.thedigimod.item.DigiItems;
import net.modderg.thedigimod.item.DigitalCreativeTab;
import net.modderg.thedigimod.particles.DigitalParticles;
import net.modderg.thedigimod.projectiles.DigitalProjectiles;

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
        DigiItems.ITEMS.register(bus);
        DigiBlocks.BLOCKS.register(bus);
        DigitalEntities.DIGIMONS.register(bus);
        DigitalProjectiles.PROJECTILES.register(bus);
        DigitalEntities.init();
        DigiItems.init();
        DigitalProjectiles.init();

        DigitalParticles.PARTICLE_TYPES.register(bus);

        MinecraftForge.EVENT_BUS.register(this);
        bus.addListener(this::addCreativeTab);
    }

    private void setup(final FMLCommonSetupEvent event) {
        for (RegistryObject<EntityType<?>> d :DigitalEntities.DIGIMONS.getEntries()) {
            EntityType<CustomDigimon> digimon = (EntityType<CustomDigimon>) d.get();
            event.enqueueWork(() -> {
                SpawnPlacements.register(digimon, SpawnPlacements.Type.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);
            });
        }
    }

    private void setAttributes(final EntityAttributeCreationEvent event) {
        for (RegistryObject<EntityType<?>> digimon : DigitalEntities.DIGIMONS.getEntries()) {
            event.put((EntityType<? extends LivingEntity>) digimon.get(), CustomDigimon.setCustomAttributes().build());
        }

        event.put(DigitalProjectiles.PUNCHING_BAG.get(), PunchingBag.setCustomAttributes().build());
        event.put(DigitalProjectiles.SP_TARGET.get(), SpTarget.setCustomAttributes().build());
        event.put(DigitalProjectiles.SP_TABLE.get(), SpTableBook.setCustomAttributes().build());
        event.put(DigitalProjectiles.SHIELD_STAND.get(), ShieldStand.setCustomAttributes().build());
        event.put(DigitalProjectiles.UPDATE_GOOD.get(), UpdateGood.setCustomAttributes().build());
        event.put(DigitalProjectiles.DRAGON_BONE.get(), DragonBone.setCustomAttributes().build());
        event.put(DigitalProjectiles.BALL_GOOD.get(), BallGood.setCustomAttributes().build());
        event.put(DigitalProjectiles.CLOWN_BOX.get(), ClownBox.setCustomAttributes().build());
        event.put(DigitalProjectiles.FLYTRAP_GOOD.get(), FlytrapGood.setCustomAttributes().build());
        event.put(DigitalProjectiles.OLD_PC_GOOD.get(), OldPc.setCustomAttributes().build());
        event.put(DigitalProjectiles.LIRA_GOOD.get(), LiraGood.setCustomAttributes().build());
        event.put(DigitalProjectiles.RED_FREEZER.get(), RedFreezer.setCustomAttributes().build());
        event.put(DigitalProjectiles.WIND_VANE.get(), RedFreezer.setCustomAttributes().build());
        event.put(DigitalProjectiles.TRAINING_ROCK.get(), RedFreezer.setCustomAttributes().build());
    }

    private void addCreativeTab(BuildCreativeModeTabContentsEvent event){
        if(event.getTab() == DigitalCreativeTab.DIGITAL_TAB.get()){
            event.accept(DigiItems.VITALBRACELET);
            event.accept(DigiItems.DIGIVICE);
            event.accept(DigiItems.VPET);
            event.accept(DigiItems.DIGIVICE_BURST);
            event.accept(DigiItems.DIGIVICE_IC);

            event.accept(DigiItems.BOTAMON);
            event.accept(DigiItems.BUBBMON);
            event.accept(DigiItems.PUNIMON);
            event.accept(DigiItems.JYARIMON);
            event.accept(DigiItems.PETITMON);
            event.accept(DigiItems.PUYOMON);
            event.accept(DigiItems.DOKIMON);
            event.accept(DigiItems.NYOKIMON);
            event.accept(DigiItems.CONOMON);
            event.accept(DigiItems.KIIMON);
            event.accept(DigiItems.POYOMON);
            event.accept(DigiItems.SUNAMON);
            event.accept(DigiItems.DATIRIMON);

            event.accept(DigiItems.DRAGON_DATA);
            event.accept(DigiItems.BEAST_DATA);
            event.accept(DigiItems.PLANTINSECT_DATA);
            event.accept(DigiItems.AQUAN_DATA);
            event.accept(DigiItems.WIND_DATA);
            event.accept(DigiItems.MACHINE_DATA);
            event.accept(DigiItems.EARTH_DATA);
            event.accept(DigiItems.NIGHTMARE_DATA);
            event.accept(DigiItems.HOLY_DATA);
            event.accept(DigiItems.DRAGON_PACK);
            event.accept(DigiItems.BEAST_PACK);
            event.accept(DigiItems.PLANTINSECT_PACK);
            event.accept(DigiItems.AQUAN_PACK);
            event.accept(DigiItems.WIND_PACK);
            event.accept(DigiItems.MACHINE_PACK);
            event.accept(DigiItems.EARTH_PACK);
            event.accept(DigiItems.NIGHTMARE_PACK);
            event.accept(DigiItems.HOLY_PACK);

            event.accept(DigiItems.DIGI_MEAT);
            event.accept(DigiItems.GUILMON_BREAD);

            event.accept(DigiItems.BLACK_DIGITRON);
            event.accept(DigiItems.DARK_TOWER_SHARD);

            event.accept(DigiItems.ATTACK_BYTE);
            event.accept(DigiItems.DEFENSE_BYTE);
            event.accept(DigiItems.SPATTACK_BYTE);
            event.accept(DigiItems.SPDEFENSE_BYTE);
            event.accept(DigiItems.HEALTH_BYTE);

            event.accept(DigiItems.TRAINING_BAG);
            event.accept(DigiItems.BAG_ITEM);
            event.accept(DigiItems.TABLE_ITEM);
            event.accept(DigiItems.TARGET_ITEM);
            event.accept(DigiItems.SHIELD_ITEM);
            event.accept(DigiItems.UPDATE_ITEM);
            event.accept(DigiItems.DRAGON_BONE_ITEM);
            event.accept(DigiItems.BALL_GOOD_ITEM);
            event.accept(DigiItems.CLOWN_BOX);
            event.accept(DigiItems.FLYTRAP_GOOD);
            event.accept(DigiItems.OLD_PC_GOOD);
            event.accept(DigiItems.LIRA_GOOD);
            event.accept(DigiItems.RED_FREEZER);
            event.accept(DigiItems.WIND_VANE);
            event.accept(DigiItems.TRAINING_ROCK);

            event.accept(DigiItems.DIGI_CORE);

            event.accept(DigiItems.CHIP_BULLET);
            event.accept(DigiItems.CHIP_PEPPER_BREATH);
            event.accept(DigiItems.CHIP_MEGA_FLAME);
            event.accept(DigiItems.CHIP_V_ARROW);
            event.accept(DigiItems.CHIP_HYPER_HEAT);
            event.accept(DigiItems.CHIP_METEOR_WING);
            event.accept(DigiItems.CHIP_BEAST_SLASH);
            event.accept(DigiItems.CHIP_INK_GUN);
            event.accept(DigiItems.CHIP_SNOW_BULLET);
            event.accept(DigiItems.CHIP_PETIT_THUNDER);
            event.accept(DigiItems.CHIP_MEGA_BLASTER);
            event.accept(DigiItems.CHIP_THUNDERBOLT);
            event.accept(DigiItems.CHIP_GATLING_ARM);
            event.accept(DigiItems.CHIP_DEADLY_STING);
            event.accept(DigiItems.CHIP_TRON_FLAME);
            event.accept(DigiItems.CHIP_DEATH_CLAW);
            event.accept(DigiItems.CHIP_POISON_BREATH);
            event.accept(DigiItems.CHIP_HEAVENS_KNUCKLE);
            event.accept(DigiItems.CHIP_HOLY_SHOOT);
            event.accept(DigiItems.CHIP_GLIDING_ROCKS);
            event.accept(DigiItems.CHIP_POOP_THROW);
            event.accept(DigiItems.CHIP_SAND_BLAST);
            event.accept(DigiItems.CHIP_BEAR_PUNCH);
            event.accept(DigiItems.CHIP_PETIT_TWISTER);
            event.accept(DigiItems.CHIP_NIGHT_OF_FIRE);
            event.accept(DigiItems.CHIP_DISC_ATTACK);
        }
        if(event.getTab() == DigitalCreativeTab.ADMIN_TAB.get()){
            event.accept(DigiItems.ATTACK_GB);
            event.accept(DigiItems.SPATTACK_GB);
            event.accept(DigiItems.DEFENCE_GB);
            event.accept(DigiItems.SPDEFENCE_GB);
            event.accept(DigiItems.HEALTH_DRIVES);
            event.accept(DigiItems.BATTLE_CHIP);
            event.accept(DigiItems.TAMER_LEASH);
            event.accept(DigiItems.GOBLIMON_BAT);
        }
    }

    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, MOD_ID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int messageID = 0;

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        messageID++;
    }
}
