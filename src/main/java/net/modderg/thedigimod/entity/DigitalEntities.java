package net.modderg.thedigimod.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.managers.EvolutionCondition;
import net.modderg.thedigimod.item.DigiItems;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings({"unused","SpellCheckingInspection"})
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

    public static final RegistryObject<EntityType<CustomDigimon>> KOROMON = DIGIMONS.register("koromon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "bullet";}}
                                    .setSpecies("Koromon")
                                    .setBabyAndXpDrop(DigiItems.DRAGON_DATA, DigiItems.BOTAMON)
                                    .setAnimations("idle6", "sit7", "walk4",null,"attack2","shoot4")
                                    .setEvos("agumon","agumonblack",null,null,null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan(),
                                            new EvolutionCondition().moodCheck("Sad")
                                    ), MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "koromon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> MOCHIMON = DIGIMONS.register("mochimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "bullet";}}
                                    .setSpecies("Mochimon")
                                    .setBabyAndXpDrop(DigiItems.PLANTINSECT_DATA, DigiItems.BUBBMON)
                                    .setAnimations("idle6", "sit7", "walk8",null,"attack2","shoot4")
                                    .setEvos("kunemon","tentomon",null,null,null,null)
                                    .setEvoConditions(
                                        new EvolutionCondition().alwaysCan(),
                                        new EvolutionCondition().moodCheck("Joyfull")
                                    ), MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mochimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TSUNOMON = DIGIMONS.register("tsunomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "bullet";}}
                                    .setSpecies("Tsunomon")
                                    .setBabyAndXpDrop(DigiItems.BEAST_DATA, DigiItems.PUNIMON)
                            .setAnimations("idle6", "sit7", "walk4",null,"attack2","shoot4")
                                    .setEvos("bearmon",null,null,null,null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan()
                                    ), MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tsunomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GIGIMON = DIGIMONS.register("gigimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "bullet";}}
                                    .setSpecies("Gigimon")
                                    .setBabyAndXpDrop(DigiItems.DRAGON_DATA, DigiItems.JYARIMON)
                            .setAnimations("idle6", "sit7", "walk4",null,"attack2","shoot4")
                                    .setEvos("guilmon",null,null,null,null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan()
                                    ), MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gigimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BABYDMON = DIGIMONS.register("babydmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "bullet";}}
                                    .setSpecies("Babydmon")
                                    .setFlyingDigimon()
                                    .setBabyAndXpDrop(DigiItems.DRAGON_DATA, DigiItems.PETITMON)
                                    .setAnimations("idle6", "sit7", "walk4","bug_fly","attack2","shoot4")
                                    .setEvos("dracomon",null,null,null,null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan()
                                    ), MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "babydmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> PUYOYOMON = DIGIMONS.register("puyoyomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "bullet";}}
                                    .setSpecies("Puyoyomon")
                                    .setSwimmerDigimon()
                                    .setBabyAndXpDrop(DigiItems.AQUAN_DATA, DigiItems.PUYOMON)
                                    .setAnimations("idle6", "sit7", "walk4","swim","attack2","shoot4")
                                    .setEvos("jellymon",null,null,null,null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan()
                                    ), MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "puyoyomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BIBIMON = DIGIMONS.register("bibimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "bullet";}}
                                    .setSpecies("Bibimon")
                                    .setBabyAndXpDrop(DigiItems.MACHINE_DATA, DigiItems.DOKIMON)
                                    .setAnimations("idle6", "sit7", "walk4",null,"attack2","shoot4")
                                    .setEvos("pulsemon",null,null,null,null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan()
                                    ), MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bibimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> YOKOMON = DIGIMONS.register("yokomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "bullet";}}
                                    .setSpecies("Yokomon")
                                    .setBabyAndXpDrop(DigiItems.WIND_DATA, DigiItems.NYOKIMON)
                                    .setAnimations("idle6", "sit7", "walk4",null,"attack2","shoot4")
                                    .setEvos("piyomon",null,null,null,null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan()
                                    ), MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "yokomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> KEEMON = DIGIMONS.register("keemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "bullet";}}
                                    .setSpecies("Keemon")
                                    .setBabyAndXpDrop(DigiItems.NIGHTMARE_DATA, DigiItems.KIIMON)
                                    .setAnimations("idle6", "sit7", "walk4",null,"attack2","shoot4")
                                    .setEvos("impmon",null,null,null,null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan()
                                    ), MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "keemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TOKOMON = DIGIMONS.register("tokomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "bullet";}}
                                    .setSpecies("Tokomon")
                                    .setBabyAndXpDrop(DigiItems.HOLY_DATA, DigiItems.POYOMON)
                                    .setAnimations("idle6", "sit7", "walk4",null,"attack2","shoot4")
                                    .setEvos("patamon",null,null,null,null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan()
                                    ), MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tokomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GOROMON = DIGIMONS.register("goromon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "bullet";}}
                                    .setSpecies("Goromon")
                                    .setBabyAndXpDrop(DigiItems.EARTH_DATA, DigiItems.SUNAMON)
                                    .setAnimations("idle6", "sit7", "walk4",null,"attack2","shoot4")
                                    .setEvos("sunarizamon",null,null,null,null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan()
                                    ), MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "goromon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> CHOCOMON = DIGIMONS.register("chocomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "bullet";}}
                                    .setSpecies("Chocomon")
                                    .setBabyAndXpDrop(DigiItems.HOLY_DATA, DigiItems.CONOMON)
                            .setAnimations("idle6", "sit7", "walk4",null,"attack2","shoot4")
                                    .setEvos("lopmon",null,null,null,null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan()
                                    ), MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "chocomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> CHAPMON = DIGIMONS.register("chapmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "bullet";}}
                                    .setSpecies("Chapmon")
                                    .setBabyAndXpDrop(DigiItems.AQUAN_DATA, DigiItems.DATIRIMON)
                                    .setAnimations("idle6", "sit7", "walk4",null,"attack2","shoot4")
                                    .setEvos("kamemon",null,null,null,null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan()
                                    ), MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "chapmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> KAMEMON = DIGIMONS.register("kamemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "petit_thunder";}}
                                    .setSpecies("Kamemon")
                                    .setEvoStage(1)
                                    .setBabyAndXpDrop(DigiItems.AQUAN_DATA, DigiItems.DATIRIMON)
                                    .setAnimations("idle3", null, "walk7",null,"attack8",null)
                                    .setEvos("archelomon","karatukinumemon","tortamon","gawappamon",null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan(),
                                            new EvolutionCondition().moodCheck("Sad"),
                                            new EvolutionCondition().maxMistakes(10).minWins(10).xpOver(6,50),
                                            new EvolutionCondition().maxMistakes(0).minWins(15).xpOver(3,50)
                                    ), MobCategory.CREATURE)
                    .sized(0.75f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "kamemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GAWAPPAMON = DIGIMONS.register("gawappamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "disc_attack";}}
                                    .setSpecies("Gawappamon")
                                    .setEvoStage(2)
                                    .setRank("super")
                                    .setBabyAndXpDrop(DigiItems.AQUAN_DATA, DigiItems.DATIRIMON)
                                    .setAnimations("idle8", "sit6", "walk7",null,"attack8","shoot5")
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2.2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gawappamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> NUMEMON = DIGIMONS.register("numemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "poop_throw";}}
                                    .setSpecies("Numemon")
                                    .setMountDigimon(-0.3d)
                                    .setEvoStage(2)
                                    .setRank("fail")
                                    .setBabyAndXpDrop(DigiItems.EARTH_DATA, DigiItems.BUBBMON)
                                    .setAnimations("idle3", "sit7", "walk8",null,"attack2","shoot4")
                                    , MobCategory.CREATURE)
                    .sized(1f,1.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "numemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> KARATUKINUMEMON = DIGIMONS.register("karatukinumemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "poop_throw";}}
                                    .setSpecies("Karatukinumemon")
                                    .setMountDigimon(-0.5d)
                                    .setEvoStage(2)
                                    .setRank("fail")
                                    .setBabyAndXpDrop(DigiItems.AQUAN_DATA, DigiItems.PUYOMON,DigiItems.DATIRIMON)
                                    .setAnimations("idle3", "sit7", "walk8",null,"attack2","shoot4")
                                    , MobCategory.CREATURE)
                    .sized(1f,1.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "karatukinumemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> AGUMON = DIGIMONS.register("agumon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "pepper_breath";}}
                                    .setSpecies("Agumon")
                                    .setEvoStage(1)
                                    .setBabyAndXpDrop(DigiItems.DRAGON_DATA, DigiItems.BOTAMON)
                                    .setAnimations("idle3", null, "walk5",null,"attack7",null)
                                    .setEvos("tyrannomon", "numemon", "greymon","veedramon","flarerizamon",null)
                                    .setDigitronEvo("agumonblack")
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan(),
                                            new EvolutionCondition().moodCheck("Sad"),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(10).minWins(10).xpOver(0,50),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(0,50),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(5).minWins(10).xpOver(6,50)
                                    ), MobCategory.CREATURE)
                    .sized(0.75f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "agumon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TYRANNOMON = DIGIMONS.register("tyrannomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "mega_flame";}}
                                    .setSpecies("Tyrannomon")
                                    .setMountDigimon(0)
                                    .setEvoStage(2)
                                    .setBabyAndXpDrop(DigiItems.DRAGON_DATA, DigiItems.BOTAMON)
                                    .setAnimations("idle9", "sit9", "walk9",null,"attack7",null)
                                    .setDigitronEvo("darktyrannomon")
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2.2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tyrannomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> FLARERIZAMON = DIGIMONS.register("flarerizamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "mega_flame";}}
                                    .setSpecies("Flarerizamon")
                                    .setEvoStage(2)
                                    .setBabyAndXpDrop(DigiItems.DRAGON_DATA, DigiItems.BOTAMON)
                                    .setAnimations("idle7", "sit5", "walk9",null,"attack6","attack6")
                                    .setDigitronEvo("darklizardmon")
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2.0f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "flarerizamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GREYMON = DIGIMONS.register("greymon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "mega_flame";}}
                                    .setSpecies("Greymon")
                                    .setRank("super")
                                    .setMountDigimon(0.4d)
                                    .setEvoStage(2)
                                    .setBabyAndXpDrop(DigiItems.DRAGON_DATA, DigiItems.BOTAMON)
                                    .setAnimations("idle5", "sit4", "walk7",null,null,"attack6")
                                    .setDigitronEvo("greymonvirus")
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "greymon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> VEEDRAMON = DIGIMONS.register("veedramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "v_arrow";}}
                                    .setSpecies("Veedramon")
                                    .setRank("super")
                                    .setEvoStage(2)
                                    .setBabyAndXpDrop(DigiItems.DRAGON_DATA, DigiItems.BOTAMON)
                                    .setAnimations("idle3",null,"walk7",null,null,"attack6")
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "veedramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> AGUMONBLACK = DIGIMONS.register("agumonblack",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "pepper_breath";}}
                                    .setSpecies("Agumon(Black)")
                                    .setEvoStage(1)
                                    .setBabyAndXpDrop(DigiItems.NIGHTMARE_DATA, DigiItems.BOTAMON)
                                    .setAnimations("idle3", null, "walk5",null,null,null)
                                    .setEvos("darklizardmon", "numemon", "blackgrowlmon","greymonvirus","darktyrannomon",null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan(),
                                            new EvolutionCondition().moodCheck("Sad"),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(10).minWins(10).xpOver(0,50),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(0).minWins(25).xpOver(7,25).xpOver(0,25),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(5).minWins(10).xpOver(7,50)
                                    ), MobCategory.CREATURE)
                    .sized(0.75f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "agumonblack").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> DARKLIZARDMON = DIGIMONS.register("darklizardmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "mega_flame";}}
                                    .setSpecies("Darklizardmon")
                                    .setEvoStage(2)
                                    .setBabyAndXpDrop(DigiItems.NIGHTMARE_DATA, DigiItems.BOTAMON)
                                    .setAnimations("idle7", "sit5", "walk9",null,"attack6","attack6")
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2.0f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "darklizardmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GREYMONVIRUS = DIGIMONS.register("greymonvirus",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "mega_flame";}}
                                    .setSpecies("Greymon(Virus)")
                                    .setRank("super")
                                    .setMountDigimon(0.4d)
                                    .setEvoStage(2)
                                    .setBabyAndXpDrop(DigiItems.NIGHTMARE_DATA, DigiItems.BOTAMON)
                                    .setAnimations("idle5", "sit4", "walk7",null,null,"attack6")
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "greymonvirus").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> DARKTYRANNOMON = DIGIMONS.register("darktyrannomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "tron_flame";}}
                                    .setSpecies("Darktyrannomon")
                                    .setRank("super")
                                    .setMountDigimon(0)
                                    .setEvoStage(2)
                                    .setBabyAndXpDrop(DigiItems.NIGHTMARE_DATA, DigiItems.BOTAMON)
                                    .setAnimations("idle9", "sit9", "walk9",null,"attack7",null)
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2.2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "darktyrannomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TENTOMON = DIGIMONS.register("tentomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "petit_thunder";}}
                                    .setSpecies("Tentomon")
                                    .setEvoStage(1)
                                    .setBabyAndXpDrop(DigiItems.PLANTINSECT_DATA, DigiItems.BUBBMON)
                                    .setAnimations("idle9", "sit3", "walk7",null,"attack5",null)
                                    .setEvos("kuwagamon", "roachmon", "kabuterimon",null,null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan(),
                                            new EvolutionCondition().moodCheck("Sad"),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(2,50)
                                    ), MobCategory.CREATURE)
                    .sized(0.75f,1.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tentomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> º = DIGIMONS.register("kuwagamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "deadly_sting";}}
                                    .setSpecies("Kuwagamon")
                                    .setMountDigimon(0)
                                    .setEvoStage(2)
                                    .setFlyingDigimon()
                                    .setBabyAndXpDrop(DigiItems.PLANTINSECT_DATA, DigiItems.BUBBMON)
                                    .setAnimations("idle3", "sit6","walk9","bug_fly","attack6",null)
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "kuwagamon").toString()));

    //hi copilot
    public static final RegistryObject<EntityType<CustomDigimon>> KUNEMON = DIGIMONS.register("kunemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "petit_thunder";}}
                                    .setSpecies("Kunemon")
                                    .setEvoStage(1)
                                    .setBabyAndXpDrop(DigiItems.PLANTINSECT_DATA, DigiItems.BUBBMON)
                                    .setAnimations("idle3", "sit2", "walk8",null,"attack3",null)
                                    .setEvos("kuwagamon", "roachmon", "flymon",null,null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan(),
                                            new EvolutionCondition().moodCheck("Sad"),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(2,50)
                                    ), MobCategory.CREATURE)
                    .sized(1.25f,1.55f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "kunemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> ROACHMON = DIGIMONS.register("roachmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "poop_throw";}}
                                    .setSpecies("Roachmon")
                                    .setRank("fail")
                                    .setEvoStage(2)
                                    .setBabyAndXpDrop(DigiItems.PLANTINSECT_DATA, DigiItems.BUBBMON)
                                    .setAnimations("idle8", "sit3", "walk9",null,"attack5","attack")
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "roachmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> FLYMON = DIGIMONS.register("flymon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "deadly_sting";}}
                                    .setSpecies("Flymon")
                                    .setRank("super")
                                    .setEvoStage(2)
                                    .setFlyingDigimon()
                                    .setBabyAndXpDrop(DigiItems.PLANTINSECT_DATA, DigiItems.BUBBMON)
                                    .setAnimations("idle3", null,"walk9","bug_fly","attack6",null)
                                    , MobCategory.CREATURE)
                    .sized(1.5f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "flymon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> KABUTERIMON = DIGIMONS.register("kabuterimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "mega_blaster";}}
                                    .setSpecies("Kabuterimon")
                                    .setRank("super")
                                    .setMountDigimon(0.2)
                                    .setEvoStage(2)
                                    .setFlyingDigimon()
                                    .setBabyAndXpDrop(DigiItems.PLANTINSECT_DATA, DigiItems.BUBBMON)
                                    .setAnimations("idle3", "sit6","walk7","bug_fly","attack5",null)
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "kabuterimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BEARMON = DIGIMONS.register("bearmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "bear_punch";}}
                                    .setSpecies("Bearmon")
                                    .setEvoStage(1)
                                    .setBabyAndXpDrop(DigiItems.BEAST_DATA, DigiItems.PUNIMON)
                                    .setAnimations("idle3", "sit", "walk7",null,"attack7","shoot5")
                                    .setEvos("grizzlymon", "numemon", "blackgaogamon","chakmon",null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan(),
                                            new EvolutionCondition().moodCheck("Sad"),
                                            new EvolutionCondition().moodCheck("Sad").maxMistakes(10).minWins(10).xpOver(7,50),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(3,50)
                                    ), MobCategory.CREATURE)
                    .sized(1f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bearmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GRIZZLYMON = DIGIMONS.register("grizzlymon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "beast_slash";}}
                                    .setSpecies("Grizzlymon")
                                    .setMountDigimon(0.1)
                                    .setEvoStage(2)
                                    .setBabyAndXpDrop(DigiItems.BEAST_DATA, DigiItems.PUNIMON)
                                    .setAnimations("idle8", "sit2", "walk7",null,"attack3","shoot6")
                                    , MobCategory.CREATURE)
                    .sized(1.75f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "grizzlymon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> CHAKMON = DIGIMONS.register("chakmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "snow_bullet";}}
                                    .setSpecies("Chakmon")
                                    .setEvoStage(2)
                                    .setRank("super")
                                    .setBabyAndXpDrop(DigiItems.AQUAN_DATA, DigiItems.PUNIMON)
                                    .setAnimations("idle3", null, "walk7",null,"attack8",null)
                                    , MobCategory.CREATURE)
                    .sized(1f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "chakmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BLACKGAOGAMON = DIGIMONS.register("blackgaogamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "tron_flame";}}
                                    .setSpecies("BlackGaogamon")
                                    .setRank("super")
                                    .setEvoStage(2)
                                    .setMountDigimon(0)
                                    .setBabyAndXpDrop(DigiItems.NIGHTMARE_DATA, DigiItems.PUNIMON)
                                    .setAnimations("idle8", "sit2", "walk7",null,"attack3","shoot6")
                                    , MobCategory.CREATURE)
                    .sized(1.5f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "blackgaogamon").toString()));


    public static final RegistryObject<EntityType<CustomDigimon>> GUILMON = DIGIMONS.register("guilmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "pepper_breath";}}
                                    .setSpecies("Guilmon")
                                    .setEvoStage(1)
                                    .setBabyAndXpDrop(DigiItems.DRAGON_DATA, DigiItems.JYARIMON)
                                    .setAnimations("idle3", "sit3", "walk7",null,"attack7","attack6")
                                    .setEvos("growlmondata", "numemon", "growlmon","blackgrowlmon",null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan(),
                                            new EvolutionCondition().moodCheck("Sad"),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(0,50),
                                            new EvolutionCondition().moodCheck("Sad").maxMistakes(10).minWins(10).xpOver(7,50)
                    ), MobCategory.CREATURE)
                    .sized(1f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "guilmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GROWLMON = DIGIMONS.register("growlmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "mega_flame";}}
                                    .setSpecies("Growlmon")
                                    .setRank("super")
                                    .setMountDigimon(0)
                                    .setEvoStage(2)
                                    .setBabyAndXpDrop(DigiItems.DRAGON_DATA, DigiItems.JYARIMON)
                                    .setAnimations("idle9", "sit6", "walk9",null,null,null)
                                    .setDigitronEvo("blackgrowlmon")
                            , MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "growlmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GROWLMONDATA = DIGIMONS.register("growlmondata",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "mega_flame";}}
                                    .setSpecies("Growlmon(Data)")
                                    .setMountDigimon(0)
                                    .setEvoStage(2)
                                    .setBabyAndXpDrop(DigiItems.DRAGON_DATA, DigiItems.JYARIMON)
                                    .setAnimations("idle9", "sit6", "walk9",null,null,null)
                                    , MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "growlmondata").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BLACKGROWLMON = DIGIMONS.register("blackgrowlmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "tron_flame";}}
                                    .setSpecies("BlackGrowlmon")
                                    .setEvoStage(2)
                                    .setMountDigimon(0)
                                    .setBabyAndXpDrop(DigiItems.NIGHTMARE_DATA, DigiItems.JYARIMON)
                                    .setAnimations("idle9", "sit6", "walk9",null,null,null)
                                    , MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "blackgrowlmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> JELLYMON = DIGIMONS.register("jellymon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "petit_thunder";}}
                                    .setSpecies("Jellymon")
                                    .setEvoStage(1)
                                    .setBabyAndXpDrop(DigiItems.AQUAN_DATA, DigiItems.PUYOMON)
                                    .setFlyingDigimon()
                                    .setAnimations("idle3",null,"walk7","float","shoot5","shoot5")
                                    .setEvos("octomon", "karatukinumemon", "gesomon","teslajellymon",null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan(),
                                            new EvolutionCondition().moodCheck("Sad"),
                                            new EvolutionCondition().moodCheck("Sad").maxMistakes(5).minWins(10).xpOver(7,50),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(3,50)
                                    ), MobCategory.CREATURE)
                    .sized(1f,1.55f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "jellymon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TESLAJELLYMON = DIGIMONS.register("teslajellymon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "thunderbolt";}}
                                    .setSpecies("Teslajellymon")
                                    .setEvoStage(2)
                                    .setRank("super")
                                    .setBabyAndXpDrop(DigiItems.AQUAN_DATA, DigiItems.PUYOMON)
                                    .setFlyingDigimon()
                                    .setAnimations("idle3","sit9","walk5","fly4","shoot",null)
                                    , MobCategory.CREATURE)
                    .sized(1.0f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "teslajellymon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> OCTOMON = DIGIMONS.register("octomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "ink_gun";}}
                                    .setSpecies("Octomon")
                                    .setEvoStage(2)
                                    .setSwimmerDigimon()
                                    .setBabyAndXpDrop(DigiItems.AQUAN_DATA, DigiItems.PUYOMON)
                                    .setAnimations("idle6", "sit2", "walk8","swim","attack9","shoot3")
                                    , MobCategory.CREATURE)
                    .sized(1f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "octomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> ARCHELOMON = DIGIMONS.register("archelomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "ocean_storm";}}
                                    .setSpecies("Archelomon")
                                    .setEvoStage(2)
                                    .setSwimmerDigimon()
                                    .setBabyAndXpDrop(DigiItems.AQUAN_DATA, DigiItems.DATIRIMON)
                                    .setAnimations("idle3", "sit2", "walk5","swim2","attack5","shoot3")
                            , MobCategory.CREATURE)
                    .sized(2f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "archelomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GESOMON = DIGIMONS.register("gesomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "ink_gun";}}
                                    .setSpecies("Gesomon")
                                    .setSwimmerDigimon()
                                    .setEvoStage(2)
                    .setBabyAndXpDrop(DigiItems.NIGHTMARE_DATA, DigiItems.PUYOMON)
                    .setAnimations("idle6","sit2","walk8","swim","attack10","shoot5")
                            , MobCategory.CREATURE)
                    .sized(1f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gesomon").toString()));


    public static final RegistryObject<EntityType<CustomDigimon>> DRACOMON = DIGIMONS.register("dracomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "pepper_breath";}}
                                    .setSpecies("Dracomon")
                    .setEvoStage(1)
                    .setBabyAndXpDrop(DigiItems.DRAGON_DATA, DigiItems.PETITMON)
                    .setAnimations("idle8", "sit3", "walk7",null,"attack8","attack6")
                    .setEvos("airdramon", "numemon", "growlmondata","coredramongreen","coredramon",null)
                    .setEvoConditions(
                            new EvolutionCondition().alwaysCan(),
                            new EvolutionCondition().moodCheck("Sad"),
                            new EvolutionCondition().moodCheck("Sad").maxMistakes(10).minWins(10).xpOver(0,50),
                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(5).minWins(15).xpOver(6,50),
                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(0,50)
                    ), MobCategory.CREATURE)
                    .sized(1f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "dracomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> AIRDRAMON = DIGIMONS.register("airdramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "mega_flame";}}
                                    .setSpecies("Airdramon")
                                    .setEvoStage(2)
                                    .setMountDigimon(-0.3)
                                    .setBabyAndXpDrop(DigiItems.DRAGON_DATA, DigiItems.PETITMON)
                                    .setFlyingDigimon()
                                    .setAnimations("idle3","sit2","fly3","fly2",null, "shoot5")
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "airdramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> COREDRAMONGREEN = DIGIMONS.register("coredramongreen",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "mega_flame";}}
                                    .setSpecies("Coredramon(Green)")
                                    .setEvoStage(2)
                                    .setRank("super")
                                    .setMountDigimon(0.3)
                                    .setBabyAndXpDrop(DigiItems.EARTH_DATA, DigiItems.PETITMON)
                                    .setAnimations("idle3", "sit6", "walk9",null,"attack6",null)
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2.35f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "coredramongreen").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> COREDRAMON = DIGIMONS.register("coredramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "mega_flame";}}
                                    .setSpecies("Coredramon")
                                    .setEvoStage(2)
                                    .setRank("super")
                                    .setMountDigimon(0.3)
                                    .setBabyAndXpDrop(DigiItems.DRAGON_DATA, DigiItems.PETITMON)
                                    .setAnimations("idle3", "sit6", "walk9",null,"attack6",null)
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2.35f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "coredramon").toString()));


    public static final RegistryObject<EntityType<CustomDigimon>> PULSEMON = DIGIMONS.register("pulsemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "petit_thunder";}}
                                    .setSpecies("Pulsemon")
                                    .setEvoStage(1)
                                    .setBabyAndXpDrop(DigiItems.MACHINE_DATA, DigiItems.DOKIMON)
                                    .setAnimations("idle8", "sit", "walk2",null,"attack7","shoot2")
                                    .setEvos("runnermon", "namakemon", "exermon","thunderballmon","bulkmon",null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan(),
                                            new EvolutionCondition().moodCheck("Sad"),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(5).minWins(10).xpOver(2,50),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(10).minWins(10).xpOver(5,50),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(0,50)
                                    ), MobCategory.CREATURE)
                    .sized(0.75f,1.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "pulsemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BULKMON = DIGIMONS.register("bulkmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "thunderbolt";}}
                                    .setSpecies("Bulkmon")
                                    .setEvoStage(2)
                                    .setRank("super")
                                    .setMountDigimon(0.2d)
                                    .setBabyAndXpDrop(DigiItems.MACHINE_DATA, DigiItems.DOKIMON)
                                    .setAnimations("idle7", "sit5", "walk9",null,"attack7",null)
                                    , MobCategory.CREATURE)
                                    .sized(1.0f,2.35f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bulkmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> THUNDERBALLMON = DIGIMONS.register("thunderballmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "thunderbolt";}}
                                    .setSpecies("Thunderballmon")
                                    .setEvoStage(2)
                                    .setRank("super")
                                    .setBabyAndXpDrop(DigiItems.MACHINE_DATA, DigiItems.DOKIMON)
                                    .setAnimations("idle9", null, "walk5",null,null,"shoot5")
                                    , MobCategory.CREATURE)
                                    .sized(1f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "thunderballmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> NAMAKEMON = DIGIMONS.register("namakemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "poop_throw";}}
                                    .setSpecies("Namakemon")
                                    .setEvoStage(2)
                                    .setRank("fail")
                                    .setBabyAndXpDrop(DigiItems.BEAST_DATA, DigiItems.DOKIMON)
                                    .setAnimations("idle8",null,"walk7",null,"attack5","shoot5")
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "namakemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> EXERMON = DIGIMONS.register("exermon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "thunderbolt";}}
                                    .setSpecies("Exermon")
                                    .setEvoStage(2)
                                    .setBabyAndXpDrop(DigiItems.PLANTINSECT_DATA, DigiItems.DOKIMON)
                                    .setAnimations("idle8","sit7","walk8",null,"attack5","shoot6")
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "exermon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> RUNNERMON = DIGIMONS.register("runnermon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "thunderbolt";}}
                                    .setSpecies("Runnermon")
                                    .setEvoStage(2)
                                    .setMountDigimon(-0.25d)
                                    .setBabyAndXpDrop(DigiItems.BEAST_DATA, DigiItems.DOKIMON)
                                    .setAnimations("idle9","sit2","walk7",null,"attack3","shoot3")
                                    , MobCategory.CREATURE)
                                    .sized(1.5f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "runnermon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> PIYOMON = DIGIMONS.register("piyomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "petit_twister";}}
                                    .setSpecies("Piyomon")
                                    .setEvoStage(1)
                                    .setBabyAndXpDrop(DigiItems.WIND_DATA, DigiItems.NYOKIMON)
                                    .setFlyingDigimon()
                                    .setAnimations("idle3",null,"walk7","fly4",null,"shoot5")
                                    .setEvos("saberdramon", "akatorimon", "birdramon",null,null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan(),
                                            new EvolutionCondition().moodCheck("Sad"),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(4,50)
                    ), MobCategory.CREATURE)
                    .sized(1f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "piyomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BIRDRAMON = DIGIMONS.register("birdramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "petit_twister";}}
                                    .setSpecies("Birdramon")
                                    .setEvoStage(2)
                                    .setMountDigimon(-0.4)
                                    .setRank("super")
                                    .setBabyAndXpDrop(DigiItems.WIND_DATA, DigiItems.NYOKIMON)
                                    .setFlyingDigimon()
                                    .setAnimations("idle7","sit9","walk9",null,"attack6","shoot5")
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "birdramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> SABERDRAMON = DIGIMONS.register("saberdramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "tron_flame";}}
                                    .setSpecies("Saberdramon")
                                    .setEvoStage(2)
                                    .setMountDigimon(-0.4)
                                    .setBabyAndXpDrop(DigiItems.NIGHTMARE_DATA, DigiItems.NYOKIMON)
                                    .setFlyingDigimon()
                                    .setAnimations("idle7","sit9","walk9",null,"attack6","shoot5")
                                    , MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "saberdramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> AKATORIMON = DIGIMONS.register("akatorimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "petit_twister";}}
                                    .setSpecies("Akatorimon")
                    .setEvoStage(2)
                    .setRank("fail")
                    .setBabyAndXpDrop(DigiItems.WIND_DATA, DigiItems.NYOKIMON)
                    .setAnimations("idle8","sit6","walk7",null,"attack5",null)
                    , MobCategory.CREATURE)
                    .sized(1.0f,2.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "akatorimon").toString()));


    public static final RegistryObject<EntityType<CustomDigimon>> LOPMON = DIGIMONS.register("lopmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "petit_twister";}}
                                    .setSpecies("Lopmon")
                                    .setEvoStage(1)
                                    .setBabyAndXpDrop(DigiItems.HOLY_DATA, DigiItems.CONOMON)
                                    .setAnimations("idle3","sit8","walk7",null,"attack8","shoot5")
                                    .setEvos("blackgalgomon", "numemon", "wendimon","turuiemon",null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan(),
                                            new EvolutionCondition().moodCheck("Sad"),
                                            new EvolutionCondition().moodCheck("Sad").maxMistakes(10).minWins(10).xpOver(7,50),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(8,25).xpOver(1,25)
                                    ), MobCategory.CREATURE)
                    .sized(0.75f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "lopmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BLACKGALGOMON = DIGIMONS.register("blackgalgomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "gatling_arm";}}
                                    .setSpecies("BlackGalgomon")
                    .setEvoStage(2)
                    .setBabyAndXpDrop(DigiItems.NIGHTMARE_DATA, DigiItems.CONOMON)
                    .setAnimations(null,null,"walk6",null,null,"shoot5")
                    , MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "blackgalgomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TURUIEMON = DIGIMONS.register("turuiemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "heavens_knuckle";}}
                                    .setSpecies("Turuiemon")
                    .setEvoStage(2)
                    .setRank("super")
                    .setBabyAndXpDrop(DigiItems.HOLY_DATA, DigiItems.CONOMON)
                    .setAnimations("idle6","sit6","walk7",null,null,"attack")
                    , MobCategory.CREATURE)
                    .sized(1.0f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "turuiemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> WENDIMON = DIGIMONS.register("wendimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "death_clow";}}
                                    .setSpecies("Wendimon")
                                    .setEvoStage(2)
                                    .setMountDigimon(0.3)
                                    .setRank("super")
                                    .setBabyAndXpDrop(DigiItems.NIGHTMARE_DATA, DigiItems.CONOMON)
                                    .setAnimations("idle3","sit5","walk9",null,"attack6",null)
                                    , MobCategory.CREATURE)
                    .sized(1.25f,2.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "wendimon").toString()));



    public static final RegistryObject<EntityType<CustomDigimon>> IMPMON = DIGIMONS.register("impmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return "night_of_fire";}}
                                    .setSpecies("Impmon")
                                    .setEvoStage(1)
                                    .setBabyAndXpDrop(DigiItems.NIGHTMARE_DATA, DigiItems.KIIMON)
                                    .setAnimations("idle3","sit9","walk5",null,"attack7","shoot2")
                                    .setEvos("boogiemon", "bakemon", "icedevimon","wizardmon",null,null)
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan(),
                                            new EvolutionCondition().moodCheck("Sad"),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(10).minWins(10).xpOver(3,25).xpOver(7,25),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(7,50)
                                    ), MobCategory.CREATURE)
                    .sized(0.75f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "impmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BAKEMON = DIGIMONS.register("bakemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "poison_breath";}}
                                    .setSpecies("Bakemon")
                                    .setEvoStage(2)
                                    .setRank("fail")
                                    .setBabyAndXpDrop(DigiItems.NIGHTMARE_DATA, DigiItems.KIIMON)
                                    .setAnimations("idle8","sit7","fly4","fly4","attack7",null)
                                    , MobCategory.CREATURE)
                    .sized(1f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bakemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> ICEDEVIMON = DIGIMONS.register("icedevimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "death_claw";}}
                                    .setSpecies("Icedevimon")
                                    .setEvoStage(2)
                                    .setRank("super")
                                    .setBabyAndXpDrop(DigiItems.NIGHTMARE_DATA, DigiItems.KIIMON)
                                    .setAnimations("idle3","sit6","walk7","fly5","attack5","shoot6")
                                    , MobCategory.CREATURE)
                    .sized(1f,2.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "icedevimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BOOGIEMON = DIGIMONS.register("boogiemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "poison_breath";}}
                                    .setSpecies("Boogiemon")
                                    .setEvoStage(2)
                                    .setBabyAndXpDrop(DigiItems.NIGHTMARE_DATA, DigiItems.KIIMON)
                                    .setFlyingDigimon()
                                    .setAnimations("idle3","sit6","walk7","fly5","attack5","shoot6")
                                    , MobCategory.CREATURE)
                    .sized(1f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "boogiemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> WIZARDMON = DIGIMONS.register("wizardmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "meteor_wing";}}
                                    .setSpecies("Wizardmon")
                                    .setEvoStage(2)
                                    .setRank("super")
                                    .setBabyAndXpDrop(DigiItems.NIGHTMARE_DATA, DigiItems.KIIMON)
                                    .setAnimations("idle4","sit6","walk6",null,"attack5","shoot2")
                    , MobCategory.CREATURE)
                    .sized(1f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "wizardmon").toString()));



    public static final RegistryObject<EntityType<CustomDigimon>> SUNARIZAMON = DIGIMONS.register("sunarizamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "sand_blast";}}
                                    .setSpecies("Sunarizamon")
                                    .setEvoStage(1)
                                    .setBabyAndXpDrop(DigiItems.EARTH_DATA, DigiItems.SUNAMON)
                                    .setAnimations("idle3","sit7","walk7",null,"attack3","shoot6")
                                    .setEvos("tortamon", "numemon", "flarerizamon","cyclomon","baboongamon","golemon")
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan(),
                                            new EvolutionCondition().moodCheck("Sad"),
                                            new EvolutionCondition().moodCheck("Meh").maxMistakes(10).minWins(10).xpOver(0,50),
                                            new EvolutionCondition().moodCheck("Sad").maxMistakes(10).minWins(10).xpOver(0,50),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(6,25).xpOver(1,25),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(6,50)
                                    ), MobCategory.CREATURE)
                    .sized(1.25f,0.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "sunarizamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GOLEMON = DIGIMONS.register("golemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "gliding_rocks";}}
                                    .setSpecies("Golemon")
                                    .setEvoStage(2)
                                    .setMountDigimon(0.45d)
                                    .setRank("super")
                                    .setBabyAndXpDrop(DigiItems.EARTH_DATA, DigiItems.SUNAMON)
                                    .setAnimations("idle5","sit3",null,null,"attack6",null)
                                    , MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "golemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BABOONGAMON = DIGIMONS.register("baboongamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "gliding_rocks";}}
                                    .setSpecies("Baboongamon")
                                    .setEvoStage(2)
                                    .setMountDigimon(0.1d)
                                    .setRank("super")
                                    .setBabyAndXpDrop(DigiItems.EARTH_DATA, DigiItems.SUNAMON)
                                    .setAnimations("idle8","sit5","walk9",null,"attack7",null)
                    , MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "baboongamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> CYCLOMON = DIGIMONS.register("cyclomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "gliding_rocks";}}
                                    .setSpecies("Cyclomon")
                                    .setEvoStage(2)
                                    .setMountDigimon(0.3d)
                                    .setBabyAndXpDrop(DigiItems.EARTH_DATA, DigiItems.POYOMON)
                                    .setAnimations("idle5","sit7","walk7",null,null,null)
                                    , MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "cyclomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TORTAMON = DIGIMONS.register("tortamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "gliding_rocks";}}
                                    .setSpecies("Tortamon")
                                    .setEvoStage(2)
                                    .setBabyAndXpDrop(DigiItems.EARTH_DATA, DigiItems.POYOMON)
                                    .setAnimations("idle5","sit2","walk7",null,"attack3","shoot6")
                                    , MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tortamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> PATAMON = DIGIMONS.register("patamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "bullet";}}
                                    .setSpecies("Patamon")
                                    .setEvoStage(1)
                                    .setBabyAndXpDrop(DigiItems.EARTH_DATA, DigiItems.POYOMON)
                                    .setFlyingDigimon()
                                    .setAnimations("idle5","sit2",null,"fly5","attack10","attack10")
                                    .setEvos("unimon", "numemon", "centalmon","mimicmon","pegasmon","angemon")
                                    .setEvoConditions(
                                            new EvolutionCondition().alwaysCan(),
                                            new EvolutionCondition().moodCheck("Sad"),
                                            new EvolutionCondition().moodCheck("Sad").maxMistakes(5).minWins(10).xpOver(5,50),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(5).minWins(10).xpOver(7,25).xpOver(8,25),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(5).minWins(10).xpOver(8,50),
                                            new EvolutionCondition().moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(8,50)
                                    )
                    , MobCategory.CREATURE)
                    .sized(1.25f,1.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "patamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> UNIMON = DIGIMONS.register("unimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "holy_shoot";}}
                                    .setSpecies("Unimon")
                                    .setEvoStage(2)
                                    .setMountDigimon(-0.1)
                                    .setBabyAndXpDrop(DigiItems.HOLY_DATA, DigiItems.POYOMON)
                                    .setFlyingDigimon()
                                    .setAnimations("idle3","sit2","walk7","fly4","attack3","shoot6")
                                    , MobCategory.CREATURE)
                    .sized(1.5f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "unimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> PEGASMON = DIGIMONS.register("pegasmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "holy_shoot";}}
                                    .setSpecies("Pegasmon")
                                    .setEvoStage(2)
                                    .setMountDigimon(-0.2)
                                    .setBabyAndXpDrop(DigiItems.HOLY_DATA, DigiItems.POYOMON)
                                    .setFlyingDigimon()
                                    .setAnimations("idle3","sit2","walk7","fly4","attack3","shoot6")
                                    , MobCategory.CREATURE)
                    .sized(1.5f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "pegasmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> MIMICMON = DIGIMONS.register("mimicmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "gatling_arm";}}
                                    .setSpecies("Mimicmon")
                                    .setEvoStage(2)
                                    .setRank("super")
                                    .setBabyAndXpDrop(DigiItems.HOLY_DATA, DigiItems.POYOMON)
                                    .setAnimations("idle8","sit5","walk9",null,null,null)
                                    , MobCategory.CREATURE)
                    .sized(1.5f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mimicmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> CENTALMON = DIGIMONS.register("centalmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "mega_blaster";}}
                                    .setSpecies("Centalmon")
                                    .setMountDigimon(-0.35)
                                    .setEvoStage(2)
                                    .setBabyAndXpDrop(DigiItems.MACHINE_DATA, DigiItems.POYOMON)
                                    .setAnimations("idle3","sit2","walk7",null,"attack3","shoot6")
                                    , MobCategory.CREATURE)
                    .sized(1.5f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "centalmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> ANGEMON = DIGIMONS.register("angemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                                    String getDefaultSpMove(){return  "heavens_knuckle";}}
                                    .setSpecies("Angemon")
                                    .setEvoStage(2)
                                    .setFlyingDigimon()
                                    .setRank("super")
                                    .setBabyAndXpDrop(DigiItems.HOLY_DATA, DigiItems.POYOMON)
                                    .setAnimations("idle8","sit9","walk7","float","attack8","attack8")
                    , MobCategory.CREATURE)
                    .sized(1.25f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "angemon").toString()));
}