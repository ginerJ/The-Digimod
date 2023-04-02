package net.modderg.thedigimod.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.digimons.*;
import net.modderg.thedigimod.entity.goods.CustomTrainingGood;
import net.modderg.thedigimod.entity.goods.PunchingBag;
import net.modderg.thedigimod.projectiles.CustomProjectile;

public class DigitalEntities {

    public static DeferredRegister<EntityType<?>> DIGIMONS = DeferredRegister.create(
            ForgeRegistries.ENTITY_TYPES, TheDigiMod.MOD_ID);

    public static final RegistryObject<EntityType<DigimonKoromon>> KOROMON = DIGIMONS.register("koromon",
            () -> EntityType.Builder.of(DigimonKoromon:: new, MobCategory.CREATURE)
                    .sized(1f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "koromon").toString()));

    public static final RegistryObject<EntityType<DigimonKoromonB>> KOROMONB = DIGIMONS.register("koromonb",
            () -> EntityType.Builder.of(DigimonKoromonB:: new, MobCategory.CREATURE)
                    .sized(1f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "koromonb").toString()));

    public static final RegistryObject<EntityType<DigimonMochimon>> MOCHIMON = DIGIMONS.register("mochimon",
            () -> EntityType.Builder.of(DigimonMochimon:: new, MobCategory.CREATURE)
                    .sized(0.75f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mochimon").toString()));

    public static final RegistryObject<EntityType<DigimonMochimonK>> MOCHIMONK = DIGIMONS.register("mochimonk",
            () -> EntityType.Builder.of(DigimonMochimonK:: new, MobCategory.CREATURE)
                    .sized(0.75f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mochimonk").toString()));

    public static final RegistryObject<EntityType<DigimonTsunomon>> TSUNOMON = DIGIMONS.register("tsunomon",
            () -> EntityType.Builder.of(DigimonTsunomon:: new, MobCategory.CREATURE)
                    .sized(1.25f,1.25f)
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
                    .sized(1f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gigimon").toString()));

    public static final RegistryObject<EntityType<DigimonGuilmon>> GUILMON = DIGIMONS.register("guilmon",
            () -> EntityType.Builder.of(DigimonGuilmon:: new, MobCategory.CREATURE)
                    .sized(1f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "guilmon").toString()));

    public static final RegistryObject<EntityType<DigimonPuyoyomon>> PUYOYOMON = DIGIMONS.register("puyoyomon",
            () -> EntityType.Builder.of(DigimonPuyoyomon:: new, MobCategory.CREATURE)
                    .sized(1f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "puyoyomon").toString()));

    public static final RegistryObject<EntityType<DigimonJellymon>> JELLYMON = DIGIMONS.register("jellymon",
            () -> EntityType.Builder.of(DigimonJellymon:: new, MobCategory.CREATURE)
                    .sized(1f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "jellymon").toString()));

    public static final RegistryObject<EntityType<DigimonTeslajellymon>> TESLAJELLYMON = DIGIMONS.register("teslajellymon",
            () -> EntityType.Builder.of(DigimonTeslajellymon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "teslajellymon").toString()));

    public static final RegistryObject<EntityType<DigimonGrowlmon>> GROWLMON = DIGIMONS.register("growlmon",
            () -> EntityType.Builder.of(DigimonGrowlmon:: new, MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "growlmon").toString()));

    public static final RegistryObject<EntityType<DigimonKuwagamon>> KUWAGAMON = DIGIMONS.register("kuwagamon",
            () -> EntityType.Builder.of(DigimonKuwagamon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.0f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "kuwagamon").toString()));

    public static final RegistryObject<EntityType<DigimonBabydmon>> BABYDMON = DIGIMONS.register("babydmon",
            () -> EntityType.Builder.of(DigimonBabydmon:: new, MobCategory.CREATURE)
                    .sized(1.0f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "babydmon").toString()));

    public static final RegistryObject<EntityType<DigimonDracomon>> DRACOMON = DIGIMONS.register("dracomon",
            () -> EntityType.Builder.of(DigimonDracomon:: new, MobCategory.CREATURE)
                    .sized(1.0f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "dracomon").toString()));

    public static final RegistryObject<EntityType<DigimonCoredramon>> COREDRAMON = DIGIMONS.register("coredramon",
            () -> EntityType.Builder.of(DigimonCoredramon:: new, MobCategory.CREATURE)
                    .sized(1.0f,2.35f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "coredramon").toString()));

    public static final RegistryObject<EntityType<DigimonBibimon>> BIBIMON = DIGIMONS.register("bibimon",
            () -> EntityType.Builder.of(DigimonBibimon:: new, MobCategory.CREATURE)
                    .sized(1f,1f)
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
                    .sized(1.0f,2.0f)
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
                    .sized(1f,1.5f)
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

    //training goods
    public static final RegistryObject<EntityType<PunchingBag>> PUNCHING_BAG = DIGIMONS.register("punching_bag",
            () -> EntityType.Builder.of(PunchingBag:: new, MobCategory.MISC)
                    .sized(1.0f,1.0f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "punching_bag").toString()));

    //attacks
    public static final RegistryObject<EntityType<CustomProjectile>> BULLET = DIGIMONS.register("bullet",
            () -> EntityType.Builder.of(CustomProjectile:: new, MobCategory.MISC)
                    .sized(1.0f,2.0f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bullet").toString()));
}