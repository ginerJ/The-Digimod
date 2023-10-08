package net.modderg.thedigimod.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.digimons.*;
import net.modderg.thedigimod.goods.*;
import net.modderg.thedigimod.projectiles.CustomProjectile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DigitalEntities {

    public static DeferredRegister<EntityType<?>> DIGIMONS = DeferredRegister.create(
            ForgeRegistries.ENTITY_TYPES, TheDigiMod.MOD_ID);

    public static Map<String, RegistryObject<EntityType<?>>> digimonMap;

    public static void init() {
        List<RegistryObject<EntityType<?>>> digimonList = DIGIMONS.getEntries().stream().toList();
        List<String> digimonNameList = DIGIMONS.getEntries().stream().map(e -> e.getId().getPath()).toList();

        digimonMap = IntStream.range(0, digimonNameList.size())
                .boxed()
                .collect(Collectors.toMap(digimonNameList::get, digimonList::get));
    }
    public static final RegistryObject<EntityType<DigimonKoromon>> KOROMON = DIGIMONS.register("koromon",
            () -> EntityType.Builder.of(DigimonKoromon:: new, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "koromon").toString()));

    public static final RegistryObject<EntityType<DigimonKokomon>> KOKOMON = DIGIMONS.register("kokomon",
            () -> EntityType.Builder.of(DigimonKokomon:: new, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "kokomon").toString()));

    public static final RegistryObject<EntityType<DigimonYaamon>> YAAMON = DIGIMONS.register("yaamon",
            () -> EntityType.Builder.of(DigimonYaamon:: new, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "yaamon").toString()));

    public static final RegistryObject<EntityType<DigimonMochimon>> MOCHIMON = DIGIMONS.register("mochimon",
            () -> EntityType.Builder.of(DigimonMochimon:: new, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mochimon").toString()));

    public static final RegistryObject<EntityType<DigimonTsunomon>> TSUNOMON = DIGIMONS.register("tsunomon",
            () -> EntityType.Builder.of(DigimonTsunomon:: new, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tsunomon").toString()));

    public static final RegistryObject<EntityType<DigimonAgumon>> AGUMON = DIGIMONS.register("agumon",
            () -> EntityType.Builder.of(DigimonAgumon:: new, MobCategory.CREATURE)
                    .sized(0.75f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "agumon").toString()));

    public static final RegistryObject<EntityType<DigimonTentomon>> TENTOMON = DIGIMONS.register("tentomon",
            () -> EntityType.Builder.of(DigimonTentomon:: new, MobCategory.CREATURE)
                    .sized(0.75f,1.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tentomon").toString()));

    public static final RegistryObject<EntityType<DigimonKabuterimon>> KABUTERIMON = DIGIMONS.register("kabuterimon",
            () -> EntityType.Builder.of(DigimonKabuterimon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "kabuterimon").toString()));

    public static final RegistryObject<EntityType<DigimonRoachmon>> ROACHMON = DIGIMONS.register("roachmon",
            () -> EntityType.Builder.of(DigimonRoachmon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "roachmon").toString()));

    public static final RegistryObject<EntityType<DigimonFlymon>> FLYMON = DIGIMONS.register("flymon",
            () -> EntityType.Builder.of(DigimonFlymon:: new, MobCategory.CREATURE)
                    .sized(1.5f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "flymon").toString()));

    public static final RegistryObject<EntityType<DigimonGreymon>> GREYMON = DIGIMONS.register("greymon",
            () -> EntityType.Builder.of(DigimonGreymon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "greymon").toString()));

    public static final RegistryObject<EntityType<DigimonGreymonVirus>> GREYMONVIRUS = DIGIMONS.register("greymonvirus",
            () -> EntityType.Builder.of(DigimonGreymonVirus:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "greymonvirus").toString()));

    public static final RegistryObject<EntityType<DigimonGrizzlymon>> GRIZZLYMON = DIGIMONS.register("grizzlymon",
            () -> EntityType.Builder.of(DigimonGrizzlymon:: new, MobCategory.CREATURE)
                    .sized(1.75f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "grizzlymon").toString()));

    public static final RegistryObject<EntityType<DigimonBearmon>> BEARMON = DIGIMONS.register("bearmon",
            () -> EntityType.Builder.of(DigimonBearmon:: new, MobCategory.CREATURE)
                    .sized(1f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bearmon").toString()));

    public static final RegistryObject<EntityType<DigimonKunemon>> KUNEMON = DIGIMONS.register("kunemon",
            () -> EntityType.Builder.of(DigimonKunemon:: new, MobCategory.CREATURE)
                    .sized(1.25f,1.55f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "kunemon").toString()));

    public static final RegistryObject<EntityType<DigimonGigimon>> GIGIMON = DIGIMONS.register("gigimon",
            () -> EntityType.Builder.of(DigimonGigimon:: new, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gigimon").toString()));

    public static final RegistryObject<EntityType<DigimonGuilmon>> GUILMON = DIGIMONS.register("guilmon",
            () -> EntityType.Builder.of(DigimonGuilmon:: new, MobCategory.CREATURE)
                    .sized(1f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "guilmon").toString()));

    public static final RegistryObject<EntityType<DigimonPuyoyomon>> PUYOYOMON = DIGIMONS.register("puyoyomon",
            () -> EntityType.Builder.of(DigimonPuyoyomon:: new, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "puyoyomon").toString()));

    public static final RegistryObject<EntityType<DigimonJellymon>> JELLYMON = DIGIMONS.register("jellymon",
            () -> EntityType.Builder.of(DigimonJellymon:: new, MobCategory.CREATURE)
                    .sized(1f,1.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "jellymon").toString()));

    public static final RegistryObject<EntityType<DigimonTeslajellymon>> TESLAJELLYMON = DIGIMONS.register("teslajellymon",
            () -> EntityType.Builder.of(DigimonTeslajellymon:: new, MobCategory.CREATURE)
                    .sized(1.0f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "teslajellymon").toString()));

    public static final RegistryObject<EntityType<DigimonGrowlmon>> GROWLMON = DIGIMONS.register("growlmon",
            () -> EntityType.Builder.of(DigimonGrowlmon:: new, MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "growlmon").toString()));

    public static final RegistryObject<EntityType<DigimonGrowlmonData>> GROWLMONDATA = DIGIMONS.register("growlmondata",
            () -> EntityType.Builder.of(DigimonGrowlmonData:: new, MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "growlmondata").toString()));

    public static final RegistryObject<EntityType<DigimonBlackGrowlmon>> BLACK_GROWLMON = DIGIMONS.register("blackgrowlmon",
            () -> EntityType.Builder.of(DigimonBlackGrowlmon:: new, MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "blackgrowlmon").toString()));

    public static final RegistryObject<EntityType<DigimonKuwagamon>> KUWAGAMON = DIGIMONS.register("kuwagamon",
            () -> EntityType.Builder.of(DigimonKuwagamon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.0f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "kuwagamon").toString()));

    public static final RegistryObject<EntityType<DigimonBabydmon>> BABYDMON = DIGIMONS.register("babydmon",
            () -> EntityType.Builder.of(DigimonBabydmon:: new, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "babydmon").toString()));

    public static final RegistryObject<EntityType<DigimonDracomon>> DRACOMON = DIGIMONS.register("dracomon",
            () -> EntityType.Builder.of(DigimonDracomon:: new, MobCategory.CREATURE)
                    .sized(1.0f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "dracomon").toString()));

    public static final RegistryObject<EntityType<DigimonAirdramon>> AIRDRAMON = DIGIMONS.register("airdramon",
            () -> EntityType.Builder.of(DigimonAirdramon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.0f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "airdramon").toString()));

    public static final RegistryObject<EntityType<DigimonCoredramonGreen>> COREDRAMONGREEN = DIGIMONS.register("coredramongreen",
            () -> EntityType.Builder.of(DigimonCoredramonGreen:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.35f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "coredramongreen").toString()));

    public static final RegistryObject<EntityType<DigimonCoredramon>> COREDRAMON = DIGIMONS.register("coredramon",
            () -> EntityType.Builder.of(DigimonCoredramon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.35f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "coredramon").toString()));

    public static final RegistryObject<EntityType<DigimonBibimon>> BIBIMON = DIGIMONS.register("bibimon",
            () -> EntityType.Builder.of(DigimonBibimon:: new, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bibimon").toString()));

    public static final RegistryObject<EntityType<DigimonPulsemon>> PULSEMON = DIGIMONS.register("pulsemon",
            () -> EntityType.Builder.of(DigimonPulsemon:: new, MobCategory.CREATURE)
                    .sized(0.75f,1.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "pulsemon").toString()));

    public static final RegistryObject<EntityType<DigimonBulkmon>> BULKMON = DIGIMONS.register("bulkmon",
            () -> EntityType.Builder.of(DigimonBulkmon:: new, MobCategory.CREATURE)
                    .sized(1.25f,2.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bulkmon").toString()));

    public static final RegistryObject<EntityType<DigimonAgumonBlack>> AGUMONBLACK = DIGIMONS.register("agumonblack",
            () -> EntityType.Builder.of(DigimonAgumonBlack:: new, MobCategory.CREATURE)
                    .sized(1f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "agumonblack").toString()));

    public static final RegistryObject<EntityType<DigimonDarkTyrannomon>> DARKTYRANNOMON = DIGIMONS.register("darktyrannomon",
            () -> EntityType.Builder.of(DigimonDarkTyrannomon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "darktyrannomon").toString()));

    public static final RegistryObject<EntityType<DigimonTyrannomon>> TYRANNOMON = DIGIMONS.register("tyrannomon",
            () -> EntityType.Builder.of(DigimonTyrannomon:: new, MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tyrannomon").toString()));

    public static final RegistryObject<EntityType<DigimonVeedramon>> VEEDRAMON = DIGIMONS.register("veedramon",
            () -> EntityType.Builder.of(DigimonVeedramon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "veedramon").toString()));

    public static final RegistryObject<EntityType<DigimonChakmon>> CHAKMON = DIGIMONS.register("chakmon",
            () -> EntityType.Builder.of(DigimonChakmon:: new, MobCategory.CREATURE)
                    .sized(1f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "chakmon").toString()));

    public static final RegistryObject<EntityType<DigimonBlackGaogamon>> BLACKGAOGAMON = DIGIMONS.register("blackgaogamon",
            () -> EntityType.Builder.of(DigimonBlackGaogamon:: new, MobCategory.CREATURE)
                    .sized(1.5f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "blackgaogamon").toString()));

    public static final RegistryObject<EntityType<DigimonYokomon>> YOKOMON = DIGIMONS.register("yokomon",
            () -> EntityType.Builder.of(DigimonYokomon:: new, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "yokomon").toString()));

    public static final RegistryObject<EntityType<DigimonBiyomon>> BIYOMON = DIGIMONS.register("biyomon",
            () -> EntityType.Builder.of(DigimonBiyomon:: new, MobCategory.CREATURE)
                    .sized(1f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "biyomon").toString()));

    public static final RegistryObject<EntityType<DigimonBirdramon>> BIRDRAMON = DIGIMONS.register("birdramon",
            () -> EntityType.Builder.of(DigimonBirdramon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "birdramon").toString()));

    public static final RegistryObject<EntityType<DigimonSaberdramon>> SABERDRAMON = DIGIMONS.register("saberdramon",
            () -> EntityType.Builder.of(DigimonSaberdramon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "saberdramon").toString()));

    public static final RegistryObject<EntityType<DigimonAkatorimon>> AKATORIMON = DIGIMONS.register("akatorimon",
            () -> EntityType.Builder.of(DigimonAkatorimon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "akatorimon").toString()));

    public static final RegistryObject<EntityType<DigimonNamakemon>> NAMAKEMON = DIGIMONS.register("namakemon",
            () -> EntityType.Builder.of(DigimonNamakemon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "namakemon").toString()));

    public static final RegistryObject<EntityType<DigimonExermon>> EXERMON = DIGIMONS.register("exermon",
            () -> EntityType.Builder.of(DigimonExermon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "exermon").toString()));

    public static final RegistryObject<EntityType<DigimonDarkLizzardmon>> DARKTYLIZZARDMON = DIGIMONS.register("darklizzardmon",
            () -> EntityType.Builder.of(DigimonDarkLizzardmon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.0f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "darklizzardmon").toString()));

    public static final RegistryObject<EntityType<DigimonRunnermon>> RUNNERMON = DIGIMONS.register("runnermon",
            () -> EntityType.Builder.of(DigimonRunnermon:: new, MobCategory.CREATURE)
                    .sized(1.5f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "runnermon").toString()));

    public static final RegistryObject<EntityType<DigimonThunderballmon>> THUNDERBALLMON = DIGIMONS.register("thunderballmon",
            () -> EntityType.Builder.of(DigimonThunderballmon:: new, MobCategory.CREATURE)
                    .sized(1f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "thunderballmon").toString()));

    public static final RegistryObject<EntityType<DigimonOctomon>> OCTOMON = DIGIMONS.register("octomon",
            () -> EntityType.Builder.of(DigimonOctomon:: new, MobCategory.CREATURE)
                    .sized(1f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "octomon").toString()));

    public static final RegistryObject<EntityType<DigimonGesomon>> GESOMON = DIGIMONS.register("gesomon",
            () -> EntityType.Builder.of(DigimonGesomon:: new, MobCategory.CREATURE)
                    .sized(1f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gesomon").toString()));

    public static final RegistryObject<EntityType<DigimonLopmon>> LOPMON = DIGIMONS.register("lopmon",
            () -> EntityType.Builder.of(DigimonLopmon:: new, MobCategory.CREATURE)
                    .sized(0.75f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "lopmon").toString()));

    public static final RegistryObject<EntityType<DigimonBlackGalgomon>> BLACKGALGOMON = DIGIMONS.register("blackgalgomon",
            () -> EntityType.Builder.of(DigimonBlackGalgomon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "blackgalgomon").toString()));

    public static final RegistryObject<EntityType<DigimonTuruiemon>> TURUIEMON = DIGIMONS.register("turuiemon",
            () -> EntityType.Builder.of(DigimonTuruiemon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "turuiemon").toString()));

    public static final RegistryObject<EntityType<DigimonWendimon>> WENDIMON = DIGIMONS.register("wendimon",
            () -> EntityType.Builder.of(DigimonWendimon:: new, MobCategory.CREATURE)
                    .sized(1.25f,2.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "wendimon").toString()));

    public static final RegistryObject<EntityType<DigimonImpmon>> IMPMON = DIGIMONS.register("impmon",
            () -> EntityType.Builder.of(DigimonImpmon:: new, MobCategory.CREATURE)
                    .sized(0.75f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "impmon").toString()));

    public static final RegistryObject<EntityType<DigimonNumemon>> NUMEMON = DIGIMONS.register("numemon",
            () -> EntityType.Builder.of(DigimonNumemon:: new, MobCategory.CREATURE)
                    .sized(1f,1.8f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "numemon").toString()));

    public static final RegistryObject<EntityType<DigimonBakemon>> BAKEMON = DIGIMONS.register("bakemon",
            () -> EntityType.Builder.of(DigimonBakemon:: new, MobCategory.CREATURE)
                    .sized(1f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bakemon").toString()));

    public static final RegistryObject<EntityType<DigimonIceDevimon>> ICEDEVIMON = DIGIMONS.register("icedevimon",
            () -> EntityType.Builder.of(DigimonIceDevimon:: new, MobCategory.CREATURE)
                    .sized(1f,2.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "icedevimon").toString()));

    public static final RegistryObject<EntityType<DigimonWizardmon>> WIZARDMON = DIGIMONS.register("wizardmon",
            () -> EntityType.Builder.of(DigimonWizardmon:: new, MobCategory.CREATURE)
                    .sized(1f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "wizardmon").toString()));

    public static final RegistryObject<EntityType<DigimonBoogiemon>> BOOGIEMON = DIGIMONS.register("boogiemon",
            () -> EntityType.Builder.of(DigimonBoogiemon:: new, MobCategory.CREATURE)
                    .sized(1f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "boogiemon").toString()));

    public static final RegistryObject<EntityType<DigimonGoromon>> GOROMON = DIGIMONS.register("goromon",
            () -> EntityType.Builder.of(DigimonGoromon:: new, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "goromon").toString()));

    public static final RegistryObject<EntityType<DigimonSunarizamon>> SUNARIZAMON = DIGIMONS.register("sunarizamon",
            () -> EntityType.Builder.of(DigimonSunarizamon:: new, MobCategory.CREATURE)
                    .sized(0.75f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "sunarizamon").toString()));

    public static final RegistryObject<EntityType<DigimonGolemon>> GOLEMON = DIGIMONS.register("golemon",
            () -> EntityType.Builder.of(DigimonGolemon:: new, MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "golemon").toString()));

    public static final RegistryObject<EntityType<DigimonBaboongamon>> BABOONGAMON = DIGIMONS.register("baboongamon",
            () -> EntityType.Builder.of(DigimonBaboongamon:: new, MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "baboongamon").toString()));

    public static final RegistryObject<EntityType<DigimonCyclomon>> CYCLOMON = DIGIMONS.register("cyclomon",
            () -> EntityType.Builder.of(DigimonCyclomon:: new, MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "cyclomon").toString()));

    public static final RegistryObject<EntityType<DigimonTortamon>> TORTAMON = DIGIMONS.register("tortamon",
            () -> EntityType.Builder.of(DigimonTortamon:: new, MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tortamon").toString()));

    public static final RegistryObject<EntityType<DigimonTokomon>> TOKOMON = DIGIMONS.register("tokomon",
            () -> EntityType.Builder.of(DigimonTokomon:: new, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tokomon").toString()));

    public static final RegistryObject<EntityType<DigimonPatamon>> PATAMON = DIGIMONS.register("patamon",
            () -> EntityType.Builder.of(DigimonPatamon:: new, MobCategory.CREATURE)
                    .sized(1.25f,1.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "patamon").toString()));

    public static final RegistryObject<EntityType<DigimonUnimon>> UNIMON = DIGIMONS.register("unimon",
            () -> EntityType.Builder.of(DigimonUnimon:: new, MobCategory.CREATURE)
                    .sized(1.5f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "unimon").toString()));

    public static final RegistryObject<EntityType<DigimonPegasmon>> PEGASMON = DIGIMONS.register("pegasmon",
            () -> EntityType.Builder.of(DigimonPegasmon:: new, MobCategory.CREATURE)
                    .sized(1.5f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "pegasmon").toString()));

    public static final RegistryObject<EntityType<DigimonMimicmon>> MIMICMON = DIGIMONS.register("mimicmon",
            () -> EntityType.Builder.of(DigimonMimicmon:: new, MobCategory.CREATURE)
                    .sized(1.5f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mimicmon").toString()));

    public static final RegistryObject<EntityType<DigimonCentalmon>> CENTALMON = DIGIMONS.register("centalmon",
            () -> EntityType.Builder.of(DigimonCentalmon:: new, MobCategory.CREATURE)
                    .sized(1.5f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "centalmon").toString()));

    public static final RegistryObject<EntityType<DigimonAngemon>> ANGEMON = DIGIMONS.register("angemon",
            () -> EntityType.Builder.of(DigimonAngemon:: new, MobCategory.CREATURE)
                    .sized(1.25f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "angemon").toString()));


    //training goods
    public static final RegistryObject<EntityType<PunchingBag>> PUNCHING_BAG = DIGIMONS.register("punching_bag",
            () -> EntityType.Builder.of(PunchingBag:: new, MobCategory.MISC)
                    .sized(1.0f,2.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "punching_bag").toString()));

    public static final RegistryObject<EntityType<SpTarget>> SP_TARGET = DIGIMONS.register("target",
            () -> EntityType.Builder.of(SpTarget:: new, MobCategory.MISC)
                    .sized(1.0f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "target").toString()));

    public static final RegistryObject<EntityType<SpTableBook>> SP_TABLE = DIGIMONS.register("defence_table",
            () -> EntityType.Builder.of(SpTableBook:: new, MobCategory.MISC)
                    .sized(1.0f,1.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "defence_table").toString()));

    public static final RegistryObject<EntityType<ShieldStand>> SHIELD_STAND = DIGIMONS.register("shield",
            () -> EntityType.Builder.of(ShieldStand:: new, MobCategory.MISC)
                    .sized(1.0f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "shield").toString()));

    public static final RegistryObject<EntityType<UpdateGood>> UPDATE_GOOD = DIGIMONS.register("update",
            () -> EntityType.Builder.of(UpdateGood:: new, MobCategory.MISC)
                    .sized(1.0f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "update").toString()));

    public static final RegistryObject<EntityType<DragonBone>> DRAGON_BONE = DIGIMONS.register("dragon_bone",
            () -> EntityType.Builder.of(DragonBone:: new, MobCategory.MISC)
                    .sized(1.25f,0.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "dragon_bone").toString()));

  public static final RegistryObject<EntityType<BallGood>> BALL_GOOD = DIGIMONS.register("ball_good",
            () -> EntityType.Builder.of(BallGood:: new, MobCategory.MISC)
                    .sized(1.25f,1.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "ball_good").toString()));

  public static final RegistryObject<EntityType<ClownBox>> CLOWN_BOX = DIGIMONS.register("clown_box",
            () -> EntityType.Builder.of(ClownBox:: new, MobCategory.MISC)
                    .sized(1f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "clown_box").toString()));

  public static final RegistryObject<EntityType<FlytrapGood>> FLYTRAP_GOOD = DIGIMONS.register("flytrap_good",
            () -> EntityType.Builder.of(FlytrapGood:: new, MobCategory.MISC)
                    .sized(0.75f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "flytrap_good").toString()));

 public static final RegistryObject<EntityType<OldPc>> OLD_PC_GOOD = DIGIMONS.register("old_pc",
            () -> EntityType.Builder.of(OldPc:: new, MobCategory.MISC)
                    .sized(1.5f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "old_pc").toString()));

 public static final RegistryObject<EntityType<LiraGood>> LIRA_GOOD = DIGIMONS.register("lira_good",
            () -> EntityType.Builder.of(LiraGood:: new, MobCategory.MISC)
                    .sized(1f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "lira_good").toString()));

 public static final RegistryObject<EntityType<RedFreezer>> RED_FREEZER = DIGIMONS.register("red_freezer",
            () -> EntityType.Builder.of(RedFreezer:: new, MobCategory.MISC)
                    .sized(1f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "red_freezer").toString()));

 public static final RegistryObject<EntityType<WindVane>> WIND_VANE = DIGIMONS.register("wind_vane",
            () -> EntityType.Builder.of(WindVane:: new, MobCategory.MISC)
                    .sized(1f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "wind_vane").toString()));

 public static final RegistryObject<EntityType<TrainingRock>> TRAINING_ROCK = DIGIMONS.register("training_rock",
            () -> EntityType.Builder.of(TrainingRock:: new, MobCategory.MISC)
                    .sized(1.75f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "training_rock").toString()));


    //attacks
    public static final RegistryObject<EntityType<CustomProjectile>> BULLET = DIGIMONS.register("bullet",
            () -> EntityType.Builder.of(CustomProjectile:: new, MobCategory.MISC)
                    .sized(1.0f,1.0f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bullet").toString()));
}