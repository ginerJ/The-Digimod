package net.modderg.thedigimod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
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
        DigitalEntities.init();
        DigitalProjectiles.PROJECTILES.register(bus);
        DigitalProjectiles.init();

        DigitalParticles.PARTICLE_TYPES.register(bus);

        MinecraftForge.EVENT_BUS.register(this);
        bus.addListener(this::addCreativeTab);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.KOROMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.MOCHIMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.AGUMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.TENTOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.KABUTERIMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.ROACHMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.FLYMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GREYMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.TSUNOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GRIZZLYMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.BEARMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.KUNEMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GIGIMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GUILMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.PUYOYOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.JELLYMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.TESLAJELLYMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GROWLMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.BLACKGROWLMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.KUWAGAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.BABYDMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.DRACOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.COREDRAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.BIBIMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.PULSEMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.AGUMONBLACK.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.DARKTYRANNOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.TYRANNOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.VEEDRAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.CHAKMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.BLACKGAOGAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.YOKOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.PIYOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.BIRDRAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.SABERDRAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.AKATORIMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.NAMAKEMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.EXERMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GREYMONVIRUS.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.RUNNERMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.THUNDERBALLMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GESOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.OCTOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.COREDRAMONGREEN.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkMobSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.AIRDRAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GROWLMONDATA.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.CHOCOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.LOPMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.BLACKGALGOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.TURUIEMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.WENDIMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.KEEMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.IMPMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.NUMEMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.BAKEMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.ICEDEVIMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.WIZARDMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.BOOGIEMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.TOKOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GOROMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.SUNARIZAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GOLEMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.BABOONGAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.CYCLOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.TORTAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.PATAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.UNIMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.PEGASMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.MIMICMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.CENTALMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.ANGEMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.DARKLIZARDMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.FLARERIZAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkDigimonSpawnRules);});
    }

    private void setAttributes(final EntityAttributeCreationEvent event) {
        event.put(DigitalEntities.KOROMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.CHOCOMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.MOCHIMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.AGUMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.TENTOMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.KABUTERIMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.ROACHMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.FLYMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.GREYMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.TSUNOMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.BEARMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.GRIZZLYMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.KUNEMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.GIGIMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.GUILMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.PUYOYOMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.JELLYMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.TESLAJELLYMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.GROWLMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.GROWLMONDATA.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.BLACKGROWLMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.KUWAGAMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.BABYDMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.DRACOMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.COREDRAMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.COREDRAMONGREEN.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.BIBIMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.PULSEMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.BULKMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.AGUMONBLACK.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.DARKTYRANNOMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.TYRANNOMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.VEEDRAMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.CHAKMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.BLACKGAOGAMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.YOKOMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.PIYOMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.BIRDRAMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.SABERDRAMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.AKATORIMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.NAMAKEMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.EXERMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.GREYMONVIRUS.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.DARKLIZARDMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.FLARERIZAMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.THUNDERBALLMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.RUNNERMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.OCTOMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.GESOMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.AIRDRAMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.LOPMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.BLACKGALGOMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.TURUIEMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.WENDIMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.KEEMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.IMPMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.NUMEMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.BAKEMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.ICEDEVIMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.WIZARDMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.BOOGIEMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.TOKOMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.GOROMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.SUNARIZAMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.GOLEMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.BABOONGAMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.CYCLOMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.TORTAMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.PATAMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.UNIMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.PEGASMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.MIMICMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.CENTALMON.get(), CustomDigimon.setCustomAttributes().build());
        event.put(DigitalEntities.ANGEMON.get(), CustomDigimon.setCustomAttributes().build());

        event.put(DigitalEntities.PUNCHING_BAG.get(), PunchingBag.setCustomAttributes().build());
        event.put(DigitalEntities.SP_TARGET.get(), SpTarget.setCustomAttributes().build());
        event.put(DigitalEntities.SP_TABLE.get(), SpTableBook.setCustomAttributes().build());
        event.put(DigitalEntities.SHIELD_STAND.get(), ShieldStand.setCustomAttributes().build());
        event.put(DigitalEntities.UPDATE_GOOD.get(), UpdateGood.setCustomAttributes().build());
        event.put(DigitalEntities.DRAGON_BONE.get(), DragonBone.setCustomAttributes().build());
        event.put(DigitalEntities.BALL_GOOD.get(), BallGood.setCustomAttributes().build());
        event.put(DigitalEntities.CLOWN_BOX.get(), ClownBox.setCustomAttributes().build());
        event.put(DigitalEntities.FLYTRAP_GOOD.get(), FlytrapGood.setCustomAttributes().build());
        event.put(DigitalEntities.OLD_PC_GOOD.get(), OldPc.setCustomAttributes().build());
        event.put(DigitalEntities.LIRA_GOOD.get(), LiraGood.setCustomAttributes().build());
        event.put(DigitalEntities.RED_FREEZER.get(), RedFreezer.setCustomAttributes().build());
        event.put(DigitalEntities.WIND_VANE.get(), RedFreezer.setCustomAttributes().build());
        event.put(DigitalEntities.TRAINING_ROCK.get(), RedFreezer.setCustomAttributes().build());
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
            event.accept(DigiItems.BLACK_DIGITRON);
            event.accept(DigiItems.DARK_TOWER_SHARD);

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
