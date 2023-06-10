package net.modderg.thedigimod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
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
import net.modderg.thedigimod.entity.digimons.*;
import net.modderg.thedigimod.entity.DigitalEntities;
import net.modderg.thedigimod.entity.goods.PunchingBag;
import net.modderg.thedigimod.entity.goods.ShieldStand;
import net.modderg.thedigimod.entity.goods.SpTableBook;
import net.modderg.thedigimod.entity.goods.UpdateGood;
import net.modderg.thedigimod.item.DigiItems;
import net.modderg.thedigimod.item.DigitalCreativeTab;
import net.modderg.thedigimod.particles.DigitalParticles;

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

        DigiItems.ITEMS.register(bus);
        DigiBlocks.BLOCKS.register(bus);
        DigitalEntities.DIGIMONS.register(bus);
        DigitalParticles.PARTICLE_TYPES.register(bus);

        MinecraftForge.EVENT_BUS.register(this);
        bus.addListener(this::addCreativeTab);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.KOROMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.KOROMONB.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.MOCHIMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.MOCHIMONK.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.AGUMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.TENTOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.KABUTERIMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.ROACHMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.FLYMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GREYMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.TSUNOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GRIZZLYMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.BEARMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.KUNEMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GIGIMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GUILMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.PUYOYOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.JELLYMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.TESLAJELLYMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GROWLMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.BLACK_GROWLMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.KUWAGAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.BABYDMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.DRACOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.COREDRAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.BIBIMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.PULSEMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.AGUMONBLACK.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.DARKTYRANNOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.TYRANNOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.VEEDRAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.CHAKMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.BLACKGAOGAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.YOKOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.BIYOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkBirdDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.BIRDRAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkBirdDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.SABERDRAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkBirdDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.AKATORIMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, CustomDigimon::checkBirdDigimonSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.NAMAKEMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.EXERMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GREYMONVIRUS.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.DARKTYLIZZARDMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.RUNNERMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.THUNDERBALLMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GESOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.OCTOMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.COREDRAMONGREEN.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkMobSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.AIRDRAMON.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});

        event.enqueueWork(() -> {
            SpawnPlacements.register(DigitalEntities.GROWLMONDATA.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);});
    }

    private void addCreativeTab(CreativeModeTabEvent.BuildContents event){
        if(event.getTab() == DigitalCreativeTab.DIGITAL_TAB){
            event.accept(DigiItems.VITALBRACELET);
            event.accept(DigiItems.DIGIVICE);
            event.accept(DigiItems.VPET);
            event.accept(DigiItems.DIGIVICE_BURST);
            event.accept(DigiItems.DIGIVICE_IC);
            event.accept(DigiItems.BOTAMOND);
            event.accept(DigiItems.BOTAMON);
            event.accept(DigiItems.BUBBMON);
            event.accept(DigiItems.PUNIMON);
            event.accept(DigiItems.BUBBMONK);
            event.accept(DigiItems.JYARIMON);
            event.accept(DigiItems.PETITMON);
            event.accept(DigiItems.PUYOMON);
            event.accept(DigiItems.DOKIMON);
            event.accept(DigiItems.NYOKIMON);
            event.accept(DigiItems.DRAGON_DATA);
            event.accept(DigiItems.BEAST_DATA);
            event.accept(DigiItems.PLANTINSECT_DATA);
            event.accept(DigiItems.AQUAN_DATA);
            event.accept(DigiItems.WIND_DATA);
            event.accept(DigiItems.MACHINE_DATA);
            event.accept(DigiItems.EARTH_DATA);
            event.accept(DigiItems.NIGHTMARE_DATA);
            event.accept(DigiItems.HOLY_DATA);
            event.accept(DigiItems.DIGI_MEAT);
            event.accept(DigiItems.BLACK_DIGITRON);
            event.accept(DigiItems.BAG_ITEM);
            event.accept(DigiItems.TABLE_ITEM);
            event.accept(DigiItems.TARGET_ITEM);
            event.accept(DigiItems.SHIELD_ITEM);
            event.accept(DigiItems.UPDATE_ITEM);
        }
        if(event.getTab() == DigitalCreativeTab.ADMIN_TAB){
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

    private void setAttributes(final EntityAttributeCreationEvent event) {
        event.put(DigitalEntities.KOROMON.get(), DigimonKoromon.setCustomAttributes().build());
        event.put(DigitalEntities.KOROMONB.get(), DigimonKoromonB.setCustomAttributes().build());
        event.put(DigitalEntities.MOCHIMON.get(), DigimonMochimon.setCustomAttributes().build());
        event.put(DigitalEntities.MOCHIMONK.get(), DigimonMochimonK.setCustomAttributes().build());
        event.put(DigitalEntities.AGUMON.get(), DigimonAgumon.setCustomAttributes().build());
        event.put(DigitalEntities.TENTOMON.get(), DigimonTentomon.setCustomAttributes().build());
        event.put(DigitalEntities.KABUTERIMON.get(), DigimonTentomon.setCustomAttributes().build());
        event.put(DigitalEntities.ROACHMON.get(), DigimonTentomon.setCustomAttributes().build());
        event.put(DigitalEntities.FLYMON.get(), DigimonTentomon.setCustomAttributes().build());
        event.put(DigitalEntities.GREYMON.get(), DigimonGreymon.setCustomAttributes().build());
        event.put(DigitalEntities.TSUNOMON.get(), DigimonTsunomon.setCustomAttributes().build());
        event.put(DigitalEntities.BEARMON.get(), DigimonTsunomon.setCustomAttributes().build());
        event.put(DigitalEntities.GRIZZLYMON.get(), DigimonTsunomon.setCustomAttributes().build());
        event.put(DigitalEntities.KUNEMON.get(), DigimonKunemon.setCustomAttributes().build());
        event.put(DigitalEntities.GIGIMON.get(), DigimonGigimon.setCustomAttributes().build());
        event.put(DigitalEntities.GUILMON.get(), DigimonGuilmon.setCustomAttributes().build());
        event.put(DigitalEntities.PUYOYOMON.get(), DigimonPuyoyomon.setCustomAttributes().build());
        event.put(DigitalEntities.JELLYMON.get(), DigimonJellymon.setCustomAttributes().build());
        event.put(DigitalEntities.TESLAJELLYMON.get(), DigimonTeslajellymon.setCustomAttributes().build());
        event.put(DigitalEntities.GROWLMON.get(), DigimonGrowlmon.setCustomAttributes().build());
        event.put(DigitalEntities.GROWLMONDATA.get(), DigimonGrowlmonData.setCustomAttributes().build());
        event.put(DigitalEntities.BLACK_GROWLMON.get(), DigimonBlackGrowlmon.setCustomAttributes().build());
        event.put(DigitalEntities.KUWAGAMON.get(), DigimonKuwagamon.setCustomAttributes().build());
        event.put(DigitalEntities.BABYDMON.get(), DigimonBabydmon.setCustomAttributes().build());
        event.put(DigitalEntities.DRACOMON.get(), DigimonDracomon.setCustomAttributes().build());
        event.put(DigitalEntities.COREDRAMON.get(), DigimonCoredramon.setCustomAttributes().build());
        event.put(DigitalEntities.COREDRAMONGREEN.get(), DigimonCoredramonGreen.setCustomAttributes().build());
        event.put(DigitalEntities.BIBIMON.get(), DigimonBibimon.setCustomAttributes().build());
        event.put(DigitalEntities.PULSEMON.get(), DigimonPulsemon.setCustomAttributes().build());
        event.put(DigitalEntities.BULKMON.get(), DigimonBulkmon.setCustomAttributes().build());
        event.put(DigitalEntities.AGUMONBLACK.get(), DigimonAgumonBlack.setCustomAttributes().build());
        event.put(DigitalEntities.DARKTYRANNOMON.get(), DigimonDarkTyrannomon.setCustomAttributes().build());
        event.put(DigitalEntities.TYRANNOMON.get(), DigimonTyrannomon.setCustomAttributes().build());
        event.put(DigitalEntities.VEEDRAMON.get(), DigimonVeedramon.setCustomAttributes().build());
        event.put(DigitalEntities.CHAKMON.get(), DigimonChakmon.setCustomAttributes().build());
        event.put(DigitalEntities.BLACKGAOGAMON.get(), DigimonBlackGaogamon.setCustomAttributes().build());
        event.put(DigitalEntities.YOKOMON.get(), DigimonYokomon.setCustomAttributes().build());
        event.put(DigitalEntities.BIYOMON.get(), DigimonBiyomon.setCustomAttributes().build());
        event.put(DigitalEntities.BIRDRAMON.get(), DigimonBirdramon.setCustomAttributes().build());
        event.put(DigitalEntities.SABERDRAMON.get(), DigimonSaberdramon.setCustomAttributes().build());
        event.put(DigitalEntities.AKATORIMON.get(), DigimonAkatorimon.setCustomAttributes().build());
        event.put(DigitalEntities.NAMAKEMON.get(), DigimonNamakemon.setCustomAttributes().build());
        event.put(DigitalEntities.EXERMON.get(), DigimonExermon.setCustomAttributes().build());
        event.put(DigitalEntities.GREYMONVIRUS.get(), DigimonGreymonVirus.setCustomAttributes().build());
        event.put(DigitalEntities.DARKTYLIZZARDMON.get(), DigimonDarkLizzardmon.setCustomAttributes().build());
        event.put(DigitalEntities.THUNDERBALLMON.get(), DigimonThunderballmon.setCustomAttributes().build());
        event.put(DigitalEntities.RUNNERMON.get(), DigimonRunnermon.setCustomAttributes().build());
        event.put(DigitalEntities.OCTOMON.get(), DigimonOctomon.setCustomAttributes().build());
        event.put(DigitalEntities.GESOMON.get(), DigimonGesomon.setCustomAttributes().build());
        event.put(DigitalEntities.AIRDRAMON.get(), DigimonAirdramon.setCustomAttributes().build());

        event.put(DigitalEntities.PUNCHING_BAG.get(), PunchingBag.setCustomAttributes().build());
        event.put(DigitalEntities.SP_TARGET.get(), PunchingBag.setCustomAttributes().build());
        event.put(DigitalEntities.SP_TABLE.get(), SpTableBook.setCustomAttributes().build());
        event.put(DigitalEntities.SHIELD_STAND.get(), ShieldStand.setCustomAttributes().build());
        event.put(DigitalEntities.UPDATE_GOOD.get(), UpdateGood.setCustomAttributes().build());
    }
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, MOD_ID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int messageID = 0;

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        messageID++;
    }
}
