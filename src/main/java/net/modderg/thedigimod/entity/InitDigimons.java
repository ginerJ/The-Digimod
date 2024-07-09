package net.modderg.thedigimod.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.item.BabyDigimonItems;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings({"unused","SpellCheckingInspection"})
public class InitDigimons {

    public static void register(IEventBus bus) {
        DIGIMONS.register(bus);
        init();
    }

    public static DeferredRegister<EntityType<?>> DIGIMONS = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TheDigiMod.MOD_ID);

    public static Map<String, RegistryObject<EntityType<?>>> digimonMap;

    public static void init() {

        List<RegistryObject<EntityType<?>>> digimonList = DIGIMONS.getEntries().stream().toList();
        List<String> digimonNameList = DIGIMONS.getEntries().stream().map(e -> e.getId().getPath()).toList();

        digimonMap = IntStream.range(0, digimonNameList.size())
                .boxed()
                .collect(Collectors.toMap(digimonNameList::get, digimonList::get));
    }

    public static final RegistryObject<EntityType<CustomDigimon>> KOROMON = DIGIMONS.register("koromon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("bullet")
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.BOTAMON)
                                    .setAnimations("idle6", "sit7", "walk4", null, "attack2", "shoot4")
                            , MobCategory.CREATURE)
                    .sized(0.9f, 0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "koromon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> CHIBIMON = DIGIMONS.register("chibimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("bullet")
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.CHICOMON)
                                    .setAnimations("idle5", "sit7", "walk7", null, "attack8", null)
                            , MobCategory.CREATURE)
                    .sized(0.9f, 0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "chibimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> MOCHIMON = DIGIMONS.register("mochimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("bullet")
                                    .setXpDrop(2).setBabyAndXpDrop(BabyDigimonItems.BUBBMON)
                                    .setAnimations("idle6", "sit7", "walk8", null, "attack2", "shoot4")
                            , MobCategory.CREATURE)
                    .sized(0.9f, 0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mochimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TSUNOMON = DIGIMONS.register("tsunomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("bullet")
                                    .setXpDrop(1).setBabyAndXpDrop(BabyDigimonItems.PUNIMON)
                                    .setAnimations("idle6", "sit7", "walk4", null, "attack2", "shoot4")
                            , MobCategory.CREATURE)
                    .sized(0.9f, 0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tsunomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GIGIMON = DIGIMONS.register("gigimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("bullet")
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.JYARIMON)
                                    .setAnimations("idle6", "sit7", "walk4", null, "attack2", "shoot4")
                            , MobCategory.CREATURE)
                    .sized(0.9f, 0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gigimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BABYDMON = DIGIMONS.register("babydmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("bullet")
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.PETITMON)
                                    .setAnimations("idle6", "sit7", "walk4", "fly7", "attack2", "shoot4")
                            , MobCategory.CREATURE)
                    .sized(0.9f, 0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "babydmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> PUYOYOMON = DIGIMONS.register("puyoyomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> (CustomSwimmerDigimon) new CustomSwimmerDigimon(type, world)
                                    .setDefaultSpMove("bullet")
                                    .setXpDrop(3).setBabyAndXpDrop(BabyDigimonItems.PUYOMON)
                                    .setAnimations("idle6", "sit7", "walk4", "swim", "attack2", "shoot4")
                            , MobCategory.CREATURE)
                    .sized(0.9f, 0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "puyoyomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BIBIMON = DIGIMONS.register("bibimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("bullet")
                                    .setXpDrop(5).setBabyAndXpDrop(BabyDigimonItems.DOKIMON)
                                    .setAnimations("idle6", "sit7", "walk4", null, "attack2", "shoot4")
                            , MobCategory.CREATURE)
                    .sized(0.9f, 0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bibimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> YOKOMON = DIGIMONS.register("yokomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("bullet")
                                    .setXpDrop(4).setBabyAndXpDrop(BabyDigimonItems.NYOKIMON)
                                    .setAnimations("idle6", "sit7", "walk4", null, "attack2", "shoot4")
                            , MobCategory.CREATURE)
                    .sized(0.9f, 0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "yokomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> YARMON = DIGIMONS.register("keemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("bullet")
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.KIIMON)
                                    .setAnimations("idle6", "sit7", "walk4", null, "attack2", "shoot4")
                            , MobCategory.CREATURE)
                    .sized(0.9f, 0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "keemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TOKOMON = DIGIMONS.register("tokomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("bullet")
                                    .setXpDrop(8).setBabyAndXpDrop(BabyDigimonItems.POYOMON)
                                    .setAnimations("idle6", "sit7", "walk4", null, "attack2", "shoot4")
                            , MobCategory.CREATURE)
                    .sized(0.9f, 0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tokomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GOROMON = DIGIMONS.register("goromon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("bullet")
                                    .setXpDrop(6).setBabyAndXpDrop(BabyDigimonItems.SUNAMON)
                                    .setAnimations("idle6", "sit7", "walk4", null, "attack2", "shoot4")
                            , MobCategory.CREATURE)
                    .sized(0.9f, 0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "goromon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> CHOCOMON = DIGIMONS.register("chocomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("bullet")
                                    .setXpDrop(8).setBabyAndXpDrop(BabyDigimonItems.CONOMON)
                                    .setAnimations("idle6", "sit7", "walk4", null, "attack2", "shoot4")
                            , MobCategory.CREATURE)
                    .sized(0.9f, 0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "chocomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> CHAPMON = DIGIMONS.register("chapmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("bullet")
                                    .setXpDrop(3).setBabyAndXpDrop(BabyDigimonItems.DATIRIMON)
                                    .setAnimations("idle6", "sit7", "walk4", null, "attack2", "shoot4")
                            , MobCategory.CREATURE)
                    .sized(0.9f, 0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "chapmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> KAMEMON = DIGIMONS.register("kamemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("petit_thunder")
                                    .setEvoStage(1)
                                    .setXpDrop(3).setBabyAndXpDrop(BabyDigimonItems.DATIRIMON)
                                    .setAnimations("idle3", null, "walk7", null, "attack8", null)
                            , MobCategory.CREATURE)
                    .sized(0.75f, 1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "kamemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GAWAPPAMON = DIGIMONS.register("gawappamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("disc_attack")
                                    .setEvoStage(2)
                                    .setXpDrop(3).setBabyAndXpDrop(BabyDigimonItems.DATIRIMON)
                                    .setAnimations("idle8", "sit6", "walk7", null, "attack8", "shoot5")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gawappamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> NUMEMON = DIGIMONS.register("numemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("poop_throw")
                                    .setMountDigimon(-0.3d)
                                    .setEvoStage(2)
                                    .setXpDrop(6).setBabyAndXpDrop(
                                            BabyDigimonItems.BOTAMON, BabyDigimonItems.DOKIMON, BabyDigimonItems.POYOMON, BabyDigimonItems.SUNAMON,
                                            BabyDigimonItems.CONOMON, BabyDigimonItems.JYARIMON, BabyDigimonItems.PUNIMON, BabyDigimonItems.PETITMON)
                                    .setAnimations("idle3", "sit7", "walk8", null, "attack2", "shoot4")
                            , MobCategory.CREATURE)
                    .sized(1f, 1.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "numemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> KARATUKINUMEMON = DIGIMONS.register("karatukinumemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomSwimmerDigimon(type, world)
                                    .setDefaultSpMove("poop_throw")
                                    .setMountDigimon(-0.5d)
                                    .setEvoStage(2)
                                    .setXpDrop(3).setBabyAndXpDrop(BabyDigimonItems.PUYOMON, BabyDigimonItems.DATIRIMON)
                                    .setAnimations("idle3", "sit7", "walk8", "fly6", "attack2", "shoot4")
                            , MobCategory.CREATURE)
                    .sized(1f, 1.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "karatukinumemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> AGUMON = DIGIMONS.register("agumon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("pepper_breath")
                                    .setEvoStage(1)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.BOTAMON)
                                    .setAnimations("idle3", null, "walk5", null, null, null)
                                    .setDigitronEvo("agumonblack")
                            , MobCategory.CREATURE)
                    .sized(0.75f, 1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "agumon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> VMON = DIGIMONS.register("vmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("pepper_breath")
                                    .setEvoStage(1)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.CHICOMON)
                                    .setAnimations("idle8", null, "walk9", null, "attack7", null)
                                    .setDigitronEvo("vmon")
                            , MobCategory.CREATURE)
                    .sized(0.75f, 1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "vmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TYRANNOMON = DIGIMONS.register("tyrannomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("mega_flame")
                                    .setMountDigimon(0.2)
                                    .setEvoStage(2)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.BOTAMON)
                                    .setAnimations("idle9", "sit6", "walk9", null, "attack7", null)
                                    .setDigitronEvo("darktyrannomon")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tyrannomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> FLARERIZAMON = DIGIMONS.register("flarerizamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("mega_flame")
                                    .setEvoStage(2)
                                    .setXpDrop(0)
                                    .setBabyAndXpDrop(BabyDigimonItems.BOTAMON, BabyDigimonItems.SUNAMON)
                                    .setAnimations("idle7", "sit5", "walk9", null, "attack6", "attack6")
                                    .setDigitronEvo("darklizardmon")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.0f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "flarerizamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GREYMON = DIGIMONS.register("greymon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("mega_flame")
                                    .setMountDigimon(0.25d)
                                    .setEvoStage(2)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.BOTAMON)
                                    .setAnimations("idle5", "sit4", "walk7", null, null, "attack6")
                                    .setDigitronEvo("greymonvirus")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "greymon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> VEEDRAMON = DIGIMONS.register("veedramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("v_arrow")
                                    .setEvoStage(2)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.BOTAMON, BabyDigimonItems.CHICOMON)
                                    .setAnimations("idle3", null, "walk7", null, null, "attack6")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "veedramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> XVMON = DIGIMONS.register("xvmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("x_attack")
                                    .setEvoStage(2)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.CHICOMON)
                                    .setAnimations("idle8", "sit5", "walk9", null, "attack13", null)
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "xvmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> FLADRAMON = DIGIMONS.register("fladramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("mega_flame")
                                    .setEvoStage(2)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.CHICOMON)
                                    .setAnimations("idle7", "sit6", "walk11", null, "attack5", "shoot5")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "fladramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> LIGHDRAMON = DIGIMONS.register("lighdramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("mega_blaster")
                                    .setEvoStage(2)
                                    .setMountDigimon(-0.6)
                                    .setXpDrop(5).setBabyAndXpDrop(BabyDigimonItems.CHICOMON)
                                    .setAnimations("idle5", "sit11", "walk7", null, "attack3", null)
                            , MobCategory.CREATURE)
                    .sized(1.5f, 1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "lighdramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TUSKMON = DIGIMONS.register("tuskmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("mega_flame")
                                    .setMountDigimon(-0.4d)
                                    .setEvoStage(2)
                                    .setXpDrop(6).setBabyAndXpDrop(BabyDigimonItems.BOTAMON)
                                    .setAnimations("idle9", "sit6", "walk7", null, "attack6", null)
                                    .setDigitronEvo("tuskmon")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tuskmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> AGUMONBLACK = DIGIMONS.register("agumonblack",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("pepper_breath")
                                    .setEvoStage(1)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.BOTAMON)
                                    .setAnimations("idle3", null, "walk5", null, null, null)
                            , MobCategory.CREATURE)
                    .sized(0.75f, 1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "agumonblack").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> DARKLIZARDMON = DIGIMONS.register("darklizardmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("mega_flame")
                                    .setEvoStage(2)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.BOTAMON)
                                    .setAnimations("idle7", "sit5", "walk9", null, "attack6", "attack6")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.0f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "darklizardmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GREYMONVIRUS = DIGIMONS.register("greymonvirus",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("tron_flame")
                                    .setMountDigimon(0.25d)
                                    .setEvoStage(2)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.BOTAMON)
                                    .setAnimations("idle5", "sit4", "walk7", null, null, "attack6")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "greymonvirus").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> DARKTYRANNOMON = DIGIMONS.register("darktyrannomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("tron_flame")
                                    .setMountDigimon(0.2)
                                    .setEvoStage(2)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.BOTAMON)
                                    .setAnimations("idle9", "sit6", "walk9", null, "attack7", null)
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "darktyrannomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> DEVIDRAMON = DIGIMONS.register("devidramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("death_claw")
                                    .setEvoStage(2)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.JYARIMON)
                                    .setAnimations("idle8", "sit6", "walk9", "fly6", "attack13", "shoot7")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "devidramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GARGOMON = DIGIMONS.register("gargomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("holy_shoot")
                                    .setEvoStage(2)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.CHICOMON)
                                    .setAnimations("idle8", "sit4", "walk10", "fly4", "attack13", null)
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gargomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TENTOMON = DIGIMONS.register("tentomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("petit_thunder")
                                    .setEvoStage(1)
                                    .setXpDrop(2).setBabyAndXpDrop(BabyDigimonItems.BUBBMON)
                                    .setAnimations("idle9", "sit3", "walk7", null, "attack5", null)
                            , MobCategory.CREATURE)
                    .sized(0.75f, 1.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tentomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> KUWAGAMON = DIGIMONS.register("kuwagamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("deadly_sting")
                                    .setMountDigimon(0)
                                    .setEvoStage(2)
                                    .setXpDrop(2).setBabyAndXpDrop(BabyDigimonItems.BUBBMON)
                                    .setAnimations("idle3", "sit6", "walk9", "fly7", "attack6", null)
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "kuwagamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> KUNEMON = DIGIMONS.register("kunemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("petit_thunder")
                                    .setEvoStage(1)
                                    .setXpDrop(2).setBabyAndXpDrop(BabyDigimonItems.BUBBMON)
                                    .setAnimations("idle3", "sit2", "walk8", null, "attack3", null)
                            , MobCategory.CREATURE)
                    .sized(1.50f, 1.55f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "kunemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> ROACHMON = DIGIMONS.register("roachmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("poop_throw")
                                    .setEvoStage(2)
                                    .setXpDrop(2).setBabyAndXpDrop(BabyDigimonItems.BUBBMON)
                                    .setAnimations("idle8", "sit3", "walk9", null, "attack5", "attack")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "roachmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> FLYMON = DIGIMONS.register("flymon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("deadly_sting")
                                    .setEvoStage(2)
                                    .setMountDigimon(-0.25)
                                    .setXpDrop(2).setBabyAndXpDrop(BabyDigimonItems.BUBBMON)
                                    .setAnimations("idle3", null, "walk9", "fly7", "attack6", null)
                            , MobCategory.CREATURE)
                    .sized(1.5f, 1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "flymon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> KABUTERIMON = DIGIMONS.register("kabuterimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("mega_blaster")
                                    .setMountDigimon(0)
                                    .setEvoStage(2)
                                    .setXpDrop(2).setBabyAndXpDrop(BabyDigimonItems.BUBBMON)
                                    .setAnimations("idle3", "sit6", "walk7", "fly7", "attack5", null)
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "kabuterimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BEARMON = DIGIMONS.register("bearmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("bear_punch")
                                    .setEvoStage(1)
                                    .setXpDrop(1).setBabyAndXpDrop(BabyDigimonItems.PUNIMON)
                                    .setAnimations("idle3", "sit", "walk7", null, "attack7", "shoot5")
                            , MobCategory.CREATURE)
                    .sized(1f, 1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bearmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GRIZZLYMON = DIGIMONS.register("grizzlymon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("beast_slash")
                                    .setMountDigimon(0)
                                    .setEvoStage(2)
                                    .setXpDrop(1).setBabyAndXpDrop(BabyDigimonItems.PUNIMON)
                                    .setAnimations("idle8", "sit10", "walk7", null, "attack3", "shoot6")
                            , MobCategory.CREATURE)
                    .sized(1.75f, 1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "grizzlymon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> CHAKMON = DIGIMONS.register("chakmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("snow_bullet")
                                    .setEvoStage(2)
                                    .setXpDrop(3).setBabyAndXpDrop(BabyDigimonItems.PUNIMON)
                                    .setAnimations("idle3", null, "walk7", null, "attack8", null)
                            , MobCategory.CREATURE)
                    .sized(1f, 1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "chakmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BLACKGAOGAMON = DIGIMONS.register("blackgaogamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("tron_flame")
                                    .setEvoStage(2)
                                    .setMountDigimon(0)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.PUNIMON)
                                    .setAnimations("idle8", "sit11", "walk7", null, "attack3", "shoot6")
                            , MobCategory.CREATURE)
                    .sized(1.5f, 1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "blackgaogamon").toString()));


    public static final RegistryObject<EntityType<CustomDigimon>> GABUMON = DIGIMONS.register("gabumon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("bear_punch")
                                    .setEvoStage(1)
                                    .setXpDrop(1).setBabyAndXpDrop(BabyDigimonItems.PUNIMON)
                                    .setAnimations("idle3", "sit3", "walk5", null, "attack5", "shoot5")
                            , MobCategory.CREATURE)
                    .sized(1f, 1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gabumon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BLACKGABUMON = DIGIMONS.register("blackgabumon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("bear_punch")
                                    .setEvoStage(1)
                                    .setXpDrop(1).setBabyAndXpDrop(BabyDigimonItems.PUNIMON)
                                    .setAnimations("idle3", "sit3", "walk5", null, "attack5", "shoot5")
                            , MobCategory.CREATURE)
                    .sized(1f, 1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "blackgabumon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GARURUMON = DIGIMONS.register("garurumon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("snow_bullet")
                                    .setMountDigimon(-0.15)
                                    .setEvoStage(2)
                                    .setXpDrop(1).setBabyAndXpDrop(BabyDigimonItems.PUNIMON)
                                    .setDigitronEvo("garurumonblack")
                                    .setAnimations("idle8", "sit11", "walk7", null, "attack3", "shoot6")
                            , MobCategory.CREATURE)
                    .sized(1.75f, 1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "garurumon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GARURUMONBLACK = DIGIMONS.register("garurumonblack",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("tron_flame")
                                    .setMountDigimon(-0.15)
                                    .setEvoStage(2)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.PUNIMON)
                                    .setAnimations("idle8", "sit11", "walk7", null, "attack3", "shoot6")
                            , MobCategory.CREATURE)
                    .sized(1.75f, 1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "garurumonblack").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> DOLURUMON = DIGIMONS.register("dolurumon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("sand_blast")
                                    .setMountDigimon(-0.15)
                                    .setEvoStage(2)
                                    .setXpDrop(6).setBabyAndXpDrop(BabyDigimonItems.PUNIMON)
                                    .setAnimations("idle9", "sit6", "walk7", null, "attack3", "shoot6")
                            , MobCategory.CREATURE)
                    .sized(1.75f, 1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "dolurumon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> DRIMOGEMON = DIGIMONS.register("drimogemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("deadly_sting")
                                    .setEvoStage(2)
                                    .setXpDrop(6).setBabyAndXpDrop(BabyDigimonItems.PUNIMON)
                                    .setAnimations("idle6", "sit2", "walk7", null, "attack3", "shoot3")
                            , MobCategory.CREATURE)
                    .sized(1.75f, 1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "drimogemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GUILMON = DIGIMONS.register("guilmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("pepper_breath")
                                    .setEvoStage(1)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.JYARIMON)
                                    .setAnimations("idle3", "sit3", "walk7", null, "attack7", "attack6")
                            , MobCategory.CREATURE)
                    .sized(1f, 1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "guilmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GROWLMON = DIGIMONS.register("growlmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("mega_flame")
                                    .setMountDigimon(-0.2)
                                    .setEvoStage(2)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.JYARIMON)
                                    .setAnimations("idle9", "sit6", "walk9", null, null, null)
                                    .setDigitronEvo("blackgrowlmon")
                            , MobCategory.CREATURE)
                    .sized(1.50f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "growlmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GROWLMONDATA = DIGIMONS.register("growlmondata",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("mega_flame")
                                    .setMountDigimon(-0.2)
                                    .setEvoStage(2)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.JYARIMON, BabyDigimonItems.PETITMON)
                                    .setAnimations("idle9", "sit6", "walk9", null, null, null)
                            , MobCategory.CREATURE)
                    .sized(1.50f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "growlmondata").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BLACKGROWLMON = DIGIMONS.register("blackgrowlmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("tron_flame")
                                    .setEvoStage(2)
                                    .setMountDigimon(-0.2)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.JYARIMON, BabyDigimonItems.BOTAMON)
                                    .setAnimations("idle9", "sit6", "walk9", null, null, null)
                            , MobCategory.CREATURE)
                    .sized(1.50f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "blackgrowlmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> JELLYMON = DIGIMONS.register("jellymon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("petit_thunder")
                                    .setEvoStage(1)
                                    .setXpDrop(3).setBabyAndXpDrop(BabyDigimonItems.PUYOMON)
                                    .setAnimations("idle3", null, "walk7", "float", "shoot5", "shoot5")
                            , MobCategory.CREATURE)
                    .sized(1f, 1.55f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "jellymon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TESLAJELLYMON = DIGIMONS.register("teslajellymon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("thunderbolt")
                                    .setEvoStage(2)
                                    .setXpDrop(3).setBabyAndXpDrop(BabyDigimonItems.PUYOMON)
                                    .setAnimations("idle3", "sit6", "walk5", "fly4", "shoot", null)
                            , MobCategory.CREATURE)
                    .sized(1.0f, 1.8f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "teslajellymon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> OCTOMON = DIGIMONS.register("octomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> (CustomSwimmerDigimon) new CustomSwimmerDigimon(type, world)
                                    .setDefaultSpMove("ink_gun")
                                    .setEvoStage(2)
                                    .setMountDigimon(-0.2)
                                    .setXpDrop(3).setBabyAndXpDrop(BabyDigimonItems.PUYOMON)
                                    .setAnimations("idle6", "sit2", "walk8", "swim", "attack9", "shoot3")
                            , MobCategory.CREATURE)
                    .sized(1f, 2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "octomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> ARCHELOMON = DIGIMONS.register("archelomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> (CustomSwimmerDigimon) new CustomSwimmerDigimon(type, world)
                                    .setDefaultSpMove("ocean_storm")
                                    .setEvoStage(2)
                                    .setXpDrop(3).setBabyAndXpDrop(BabyDigimonItems.DATIRIMON)
                                    .setMountDigimon(0)
                                    .setAnimations("idle3", "sit2", "walk5", "swim2", "attack5", "shoot3")
                            , MobCategory.CREATURE)
                    .sized(1.5f, 1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "archelomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GESOMON = DIGIMONS.register("gesomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> (CustomSwimmerDigimon) new CustomSwimmerDigimon(type, world)
                                    .setDefaultSpMove("ink_gun")
                                    .setEvoStage(2)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.PUYOMON)
                                    .setAnimations("idle6", "sit2", "walk8", "swim", "attack10", "shoot5")
                            , MobCategory.CREATURE)
                    .sized(1f, 2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gesomon").toString()));


    public static final RegistryObject<EntityType<CustomDigimon>> DRACOMON = DIGIMONS.register("dracomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("pepper_breath")
                                    .setEvoStage(1)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.PETITMON)
                                    .setAnimations("idle8", "sit3", "walk7", null, "attack8", "attack6")
                            , MobCategory.CREATURE)
                    .sized(1f, 1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "dracomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> AIRDRAMON = DIGIMONS.register("airdramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("mega_flame")
                                    .setEvoStage(2)
                                    .setMountDigimon(-0.4)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.PETITMON)
                                    .setAnimations("idle3", "sit2", "fly3", "fly2", null, "shoot5")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "airdramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> COREDRAMONGREEN = DIGIMONS.register("coredramongreen",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("mega_flame")
                                    .setEvoStage(2)
                                    .setMountDigimon(0.15)
                                    .setXpDrop(6).setBabyAndXpDrop(BabyDigimonItems.PETITMON)
                                    .setAnimations("idle3", "sit6", "walk9", null, "attack6", null)
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.35f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "coredramongreen").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> COREDRAMON = DIGIMONS.register("coredramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("mega_flame")
                                    .setEvoStage(2)
                                    .setMountDigimon(0.15)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.PETITMON)
                                    .setAnimations("idle3", "sit6", "walk9", null, "attack6", null)
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.35f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "coredramon").toString()));


    public static final RegistryObject<EntityType<CustomDigimon>> PULSEMON = DIGIMONS.register("pulsemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("petit_thunder")
                                    .setEvoStage(1)
                                    .setXpDrop(5).setBabyAndXpDrop(BabyDigimonItems.DOKIMON)
                                    .setAnimations("idle8", "sit", "walk2", null, "attack7", "shoot2")
                            , MobCategory.CREATURE)
                    .sized(0.75f, 1.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "pulsemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BULKMON = DIGIMONS.register("bulkmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("thunderbolt")
                                    .setEvoStage(2)
                                    .setMountDigimon(-0.1d)
                                    .setXpDrop(5).setBabyAndXpDrop(BabyDigimonItems.DOKIMON)
                                    .setAnimations("idle7", "sit5", "walk9", null, "attack7", null)
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.35f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bulkmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> THUNDERBALLMON = DIGIMONS.register("thunderballmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("thunderbolt")
                                    .setEvoStage(2)
                                    .setXpDrop(5).setBabyAndXpDrop(BabyDigimonItems.DOKIMON)
                                    .setAnimations("idle9", null, "walk5", null, null, "shoot5")
                            , MobCategory.CREATURE)
                    .sized(1f, 1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "thunderballmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> NAMAKEMON = DIGIMONS.register("namakemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("poop_throw")
                                    .setEvoStage(2)
                                    .setXpDrop(1).setBabyAndXpDrop(BabyDigimonItems.DOKIMON)
                                    .setAnimations("idle8", null, "walk7", null, "attack5", "shoot5")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "namakemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> EXERMON = DIGIMONS.register("exermon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("thunderbolt")
                                    .setEvoStage(2)
                                    .setXpDrop(2).setBabyAndXpDrop(BabyDigimonItems.DOKIMON)
                                    .setAnimations("idle8", "sit7", "walk8", null, "attack5", "shoot6")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "exermon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> RUNNERMON = DIGIMONS.register("runnermon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("thunderbolt")
                                    .setEvoStage(2)
                                    .setMountDigimon(0d)
                                    .setXpDrop(1).setBabyAndXpDrop(BabyDigimonItems.DOKIMON)
                                    .setAnimations("idle9", "sit11", "walk7", null, "attack3", "shoot5")
                            , MobCategory.CREATURE)
                    .sized(1.5f, 1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "runnermon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> PIYOMON = DIGIMONS.register("piyomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("petit_twister")
                                    .setEvoStage(1)
                                    .setXpDrop(4).setBabyAndXpDrop(BabyDigimonItems.NYOKIMON)
                                    .setAnimations("idle3", null, "walk7", "fly4", null, "shoot5")
                            , MobCategory.CREATURE)
                    .sized(1f, 1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "piyomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BIRDRAMON = DIGIMONS.register("birdramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("petit_twister")
                                    .setEvoStage(2)
                                    .setMountDigimon(-0.6)
                                    .setXpDrop(4).setBabyAndXpDrop(BabyDigimonItems.NYOKIMON)
                                    .setDigitronEvo("saberdramon")
                                    .setAnimations("idle7", "sit6", "walk9", null, "attack6", "shoot5")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "birdramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> SABERDRAMON = DIGIMONS.register("saberdramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("tron_flame")
                                    .setEvoStage(2)
                                    .setMountDigimon(-0.6)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.NYOKIMON)
                                    .setAnimations("idle7", "sit6", "walk9", null, "attack6", "shoot5")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "saberdramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> MAILBIRDRAMON = DIGIMONS.register("mailbirdramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("thunderbolt")
                                    .setEvoStage(2)
                                    .setMountDigimon(-0.6)
                                    .setXpDrop(5).setBabyAndXpDrop(BabyDigimonItems.NYOKIMON)
                                    .setAnimations("idle8", "sit5", "walk13", null, "attack6", "shoot8")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mailbirdramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> AKATORIMON = DIGIMONS.register("akatorimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("petit_twister")
                                    .setEvoStage(2)
                                    .setXpDrop(4).setBabyAndXpDrop(BabyDigimonItems.NYOKIMON)
                                    .setAnimations("idle8", "sit6", "walk7", null, "attack5", null)
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "akatorimon").toString()));


    public static final RegistryObject<EntityType<CustomDigimon>> LOPMON = DIGIMONS.register("lopmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("petit_twister")
                                    .setEvoStage(1)
                                    .setXpDrop(8).setBabyAndXpDrop(BabyDigimonItems.CONOMON)
                                    .setAnimations("idle3", "sit8", "walk7", null, "attack8", "shoot5")
                            , MobCategory.CREATURE)
                    .sized(0.75f, 1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "lopmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BLACKGALGOMON = DIGIMONS.register("blackgalgomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("gatling_arm")
                                    .setEvoStage(2)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.CONOMON)
                                    .setAnimations(null, null, "walk6", null, null, "shoot5")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "blackgalgomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TURUIEMON = DIGIMONS.register("turuiemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("heavens_knuckle")
                                    .setEvoStage(2)
                                    .setXpDrop(8).setBabyAndXpDrop(BabyDigimonItems.CONOMON)
                                    .setAnimations("idle6", "sit6", "walk7", null, null, "attack")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "turuiemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> WENDIMON = DIGIMONS.register("wendimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("death_claw")
                                    .setEvoStage(2)
                                    .setMountDigimon(0.2)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.CONOMON)
                                    .setAnimations("idle3", "sit5", "walk9", null, "attack6", null)
                            , MobCategory.CREATURE)
                    .sized(1.50f, 2.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "wendimon").toString()));


    public static final RegistryObject<EntityType<CustomDigimon>> IMPMON = DIGIMONS.register("impmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("night_of_fire")
                                    .setEvoStage(1)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.KIIMON)
                                    .setAnimations("idle3", "sit3", "walk5", null, "attack7", "shoot2")
                            , MobCategory.CREATURE)
                    .sized(0.75f, 1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "impmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BAKEMON = DIGIMONS.register("bakemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("poison_breath")
                                    .setEvoStage(2)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.KIIMON)
                                    .setAnimations("idle8", "sit7", "fly4", "fly4", "attack7", null)
                            , MobCategory.CREATURE)
                    .sized(1f, 1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bakemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> ICEDEVIMON = DIGIMONS.register("icedevimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("death_claw")
                                    .setEvoStage(2)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.KIIMON)
                                    .setAnimations("idle3", "sit6", "walk7", "fly5", "attack5", "shoot6")
                            , MobCategory.CREATURE)
                    .sized(1f, 2.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "icedevimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BOOGIEMON = DIGIMONS.register("boogiemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("poison_breath")
                                    .setEvoStage(2)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.KIIMON)
                                    .setAnimations("idle3", "sit6", "walk7", "fly5", "attack5", "shoot6")
                            , MobCategory.CREATURE)
                    .sized(1f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "boogiemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> WIZARDMON = DIGIMONS.register("wizardmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("meteor_wing")
                                    .setEvoStage(2)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.KIIMON)
                                    .setAnimations("idle4", "sit6", "walk6", null, "attack5", "shoot2")
                            , MobCategory.CREATURE)
                    .sized(1f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "wizardmon").toString()));


    public static final RegistryObject<EntityType<CustomDigimon>> SUNARIZAMON = DIGIMONS.register("sunarizamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("sand_blast")
                                    .setEvoStage(1)
                                    .setXpDrop(6).setBabyAndXpDrop(BabyDigimonItems.SUNAMON)
                                    .setAnimations("idle3", "sit12", "walk7", null, "attack3", "shoot6")
                            , MobCategory.CREATURE)
                    .sized(1.50f, 0.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "sunarizamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GOLEMON = DIGIMONS.register("golemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("gliding_rocks")
                                    .setEvoStage(2)
                                    .setMountDigimon(0d)
                                    .setXpDrop(6).setBabyAndXpDrop(BabyDigimonItems.SUNAMON)
                                    .setAnimations("idle5", "sit3", null, null, "attack6", null)
                            , MobCategory.CREATURE)
                    .sized(1.50f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "golemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BABOONGAMON = DIGIMONS.register("baboongamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("gliding_rocks")
                                    .setEvoStage(2)
                                    .setMountDigimon(0.1d)
                                    .setXpDrop(6).setBabyAndXpDrop(BabyDigimonItems.SUNAMON)
                                    .setAnimations("idle8", "sit5", "walk9", null, "attack7", null)
                            , MobCategory.CREATURE)
                    .sized(1.50f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "baboongamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> CYCLOMON = DIGIMONS.register("cyclomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("hyper_heat")
                                    .setEvoStage(2)
                                    .setMountDigimon(0)
                                    .setXpDrop(6).setBabyAndXpDrop(BabyDigimonItems.SUNAMON, BabyDigimonItems.PUNIMON)
                                    .setAnimations("idle5", "sit5", "walk7", null, null, null)
                            , MobCategory.CREATURE)
                    .sized(1.50f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "cyclomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TORTAMON = DIGIMONS.register("tortamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("gliding_rocks")
                                    .setEvoStage(2)
                                    .setXpDrop(6).setBabyAndXpDrop(BabyDigimonItems.SUNAMON, BabyDigimonItems.DATIRIMON)
                                    .setAnimations("idle5", "sit6", "walk7", null, "attack3", "shoot6")
                            , MobCategory.CREATURE)
                    .sized(1.50f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tortamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> PATAMON = DIGIMONS.register("patamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("bullet")
                                    .setEvoStage(1)
                                    .setXpDrop(8).setBabyAndXpDrop(BabyDigimonItems.POYOMON)
                                    .setAnimations("idle5", "sit2", null, "fly5", "attack10", "attack10")
                            , MobCategory.CREATURE)
                    .sized(1.50f, 1.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "patamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> UNIMON = DIGIMONS.register("unimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("holy_shoot")
                                    .setEvoStage(2)
                                    .setMountDigimon(0)
                                    .setXpDrop(8).setBabyAndXpDrop(BabyDigimonItems.POYOMON)
                                    .setAnimations("idle3", "sit11", "walk7", "fly4", "attack3", "shoot6")
                            , MobCategory.CREATURE)
                    .sized(1.5f, 2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "unimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> PEGASMON = DIGIMONS.register("pegasmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("holy_shoot")
                                    .setEvoStage(2)
                                    .setMountDigimon(-0.15)
                                    .setXpDrop(8).setBabyAndXpDrop(BabyDigimonItems.POYOMON)
                                    .setAnimations("idle3", "sit11", "walk7", "fly4", "attack3", "shoot6")
                            , MobCategory.CREATURE)
                    .sized(1.5f, 2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "pegasmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> MIMICMON = DIGIMONS.register("mimicmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("gatling_arm")
                                    .setEvoStage(2)
                                    .setXpDrop(8).setBabyAndXpDrop(BabyDigimonItems.POYOMON)
                                    .setAnimations("idle8", "sit5", "walk9", null, null, null)
                            , MobCategory.CREATURE)
                    .sized(1.5f, 2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mimicmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> CENTALMON = DIGIMONS.register("centalmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("mega_blaster")
                                    .setMountDigimon(-0.3)
                                    .setEvoStage(2)
                                    .setXpDrop(5).setBabyAndXpDrop(BabyDigimonItems.POYOMON)
                                    .setAnimations("idle3", "sit11", "walk7", null, "attack3", "shoot6")
                            , MobCategory.CREATURE)
                    .sized(1.5f, 2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "centalmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> ANGEMON = DIGIMONS.register("angemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("heavens_knuckle")
                                    .setEvoStage(2)
                                    .setXpDrop(8).setBabyAndXpDrop(BabyDigimonItems.POYOMON)
                                    .setAnimations("idle8", "sit9", "walk7", "float", "attack8", "attack8")
                            , MobCategory.CREATURE)
                    .sized(1.50f, 2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "angemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> SHOUTMON = DIGIMONS.register("shoutmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("pepper_breath")
                                    .setEvoStage(1)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.JYARIMON)
                                    .setAnimations("idle3", null, "walk12", null, "attack7", "shoot7")
                            , MobCategory.CREATURE)
                    .sized(1f, 1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "shoutmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BALLISTAMON = DIGIMONS.register("ballistamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("mega_blaster")
                                    .setEvoStage(2)
                                    .setXpDrop(5).setBabyAndXpDrop(BabyDigimonItems.BUBBMON)
                                    .setAnimations("idle3", "sit6", "walk12", null, "attack12", null)
                            , MobCategory.CREATURE)
                    .sized(1.5f, 2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "ballistamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> REDVEEDRAMON = DIGIMONS.register("redveedramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("v_arrow")
                                    .setEvoStage(2)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.JYARIMON)
                                    .setAnimations("idle3", null, "walk7", null, null, "attack6")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 2.50f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "redveedramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> SHOUTMONX2 = DIGIMONS.register("shoutmonx2",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("v_arrow")
                                    .setEvoStage(2)
                                    .setXpDrop(5).setBabyAndXpDrop(BabyDigimonItems.BUBBMON, BabyDigimonItems.JYARIMON)
                                    .setAnimations("idle3", "sit6", "walk12", null, "attack12", null)
                            , MobCategory.CREATURE)
                    .sized(1.5f, 2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "shoutmonx2").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> KINGSHOUTMON = DIGIMONS.register("kingshoutmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("disc_attack")
                                    .setEvoStage(2)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.JYARIMON)
                                    .setAnimations("idle8", "sit6", "walk7", null, "attack5", "attack12")
                            , MobCategory.CREATURE)
                    .sized(1.5f, 2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "kingshoutmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> SHOUTMONX3 = DIGIMONS.register("shoutmonx3",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("x_attack")
                                    .setEvoStage(3)
                                    .setXpDrop(5).setBabyAndXpDrop(BabyDigimonItems.BUBBMON, BabyDigimonItems.JYARIMON, BabyDigimonItems.PUNIMON)
                                    .setAnimations("idle5", "sit14", "walk5", null, "attack12", "attack6")
                            , MobCategory.CREATURE)
                    .sized(1.5f, 3.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "shoutmonx3").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> OMEGASHOUTMON = DIGIMONS.register("omegashoutmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("divine_axe")
                                    .setEvoStage(3)
                                    .setXpDrop(8).setBabyAndXpDrop(BabyDigimonItems.JYARIMON)
                                    .setAnimations("idle8", "sit15", "walk12", null, "attack13", null)
                            , MobCategory.CREATURE)
                    .sized(1.5f, 3f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "omegashoutmon").toString()));



    public static final RegistryObject<EntityType<CustomDigimon>> MAMEMON = DIGIMONS.register("mamemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                .setDefaultSpMove("smiley_bomb")
                                    .setEvoStage(3)
                                    .setXpDrop(5).setBabyAndXpDrop(BabyDigimonItems.DOKIMON)
                                    .setAnimations("idle3", "sit4", "walk7",null,null,"attack")
                            , MobCategory.CREATURE)
                    .sized(0.8f,0.95f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mamemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> MAMETYRAMON = DIGIMONS.register("mametyramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("beast_slash")
                                    .setEvoStage(3)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.BOTAMON, BabyDigimonItems.DOKIMON)
                                    .setAnimations("idle9", "sit3", "walk7",null,"attack12","shoot8")
                            , MobCategory.CREATURE)
                    .sized(0.8f,0.95f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mametyramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> AEROVEEDRAMON = DIGIMONS.register("aeroveedramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                .setDefaultSpMove("v_arrow")
                                    .setEvoStage(3)
                                    .setMountDigimon(0.25)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.BOTAMON)
                                    .setAnimations("idle5","sit6","walk9","float","attack7","attack6")
                            , MobCategory.CREATURE)
                    .sized(1.3f,2.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "aeroveedramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> WEREGARURUMON = DIGIMONS.register("weregarurumon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                .setDefaultSpMove("beast_slash")
                                    .setEvoStage(3)
                                    .setXpDrop(1).setBabyAndXpDrop(BabyDigimonItems.PUNIMON)
                                    .setAnimations("idle3","sit13","walk10",null,null,null)
                            , MobCategory.CREATURE)
                    .sized(1.50f,2.4f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "weregarurumon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BLACKWEREGARURUMON = DIGIMONS.register("blackweregarurumon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("tron_flame")
                                    .setEvoStage(3)
                                    .setXpDrop(1).setBabyAndXpDrop(BabyDigimonItems.PUNIMON)
                                    .setAnimations("idle3","sit13","walk10",null,null,null)
                            , MobCategory.CREATURE)
                    .sized(1.50f,2.4f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "blackweregarurumon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> METALGREYMON = DIGIMONS.register("metalgreymon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                .setDefaultSpMove("giga_destroyer")
                                    .setEvoStage(3)
                                    .setMountDigimon(0.35)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.BOTAMON)
                                    .setDigitronEvo("metalgreymonvirus")
                                    .setAnimations("idle9","sit6","walk9","float2","attack11",null)
                            , MobCategory.CREATURE)
                    .sized(1.5f,2.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "metalgreymon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> MEGADRAMON = DIGIMONS.register("megadramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("giga_destroyer")
                                    .setEvoStage(3)
                                    .setMountDigimon(0.35)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.PETITMON, BabyDigimonItems.JYARIMON)
                                    .setDigitronEvo("gigadramon")
                                    .setAnimations("idle8","sit6","float2","float2","attack12","shoot8")
                            , MobCategory.CREATURE)
                    .sized(1.5f,2.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "megadramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GIGADRAMON = DIGIMONS.register("gigadramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                    .setDefaultSpMove("giga_destroyer")
                                    .setEvoStage(3)
                                    .setMountDigimon(0.35)
                                    .setXpDrop(5).setBabyAndXpDrop(BabyDigimonItems.PETITMON, BabyDigimonItems.BOTAMON)
                                    .setAnimations("idle8",null,"float2","float2","attack12","shoot8")
                            , MobCategory.CREATURE)
                    .sized(1.5f,2.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gigadramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> MEGALOGROWLMON = DIGIMONS.register("megalogrowlmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                .setDefaultSpMove("hyper_heat")
                                    .setEvoStage(3)
                                    .setMountDigimon(-0.2)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.JYARIMON)
                                    .setDigitronEvo("blackmegalogrowlmon")
                                    .setAnimations("idle9",null,"walk9",null,"attack7","shoot5")
                            , MobCategory.CREATURE)
                    .sized(1.5f,2.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "megalogrowlmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> MEGALOGROWLMONDATA = DIGIMONS.register("megalogrowlmondata",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                .setDefaultSpMove("mega_flame")
                                    .setEvoStage(3)
                                    .setMountDigimon(-0.2)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.JYARIMON, BabyDigimonItems.PETITMON, BabyDigimonItems.SUNAMON)
                                    .setAnimations("idle9","sit6","walk9",null,"attack7","shoot5")
                            , MobCategory.CREATURE)
                    .sized(1.5f,2.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "megalogrowlmondata").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BLACKMEGALOGROWLMON = DIGIMONS.register("blackmegalogrowlmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                .setDefaultSpMove("poison_breath")
                                    .setEvoStage(3)
                                    .setMountDigimon(-0.2)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.JYARIMON, BabyDigimonItems.BOTAMON)
                                    .setAnimations("idle9","sit6","walk9",null,"attack7","shoot5")
                            , MobCategory.CREATURE)
                    .sized(1.5f,2.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "blackmegalogrowlmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> METALTYRANNOMON = DIGIMONS.register("metaltyrannomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                .setDefaultSpMove("hyper_heat")
                                    .setEvoStage(3)
                                    .setMountDigimon(0.1)
                                    .setXpDrop(6).setBabyAndXpDrop(BabyDigimonItems.BOTAMON)
                                    .setAnimations("idle5","sit6","walk9",null,"attack6",null)
                            , MobCategory.CREATURE)
                    .sized(1.5f,2.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "metaltyrannomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> THETISMON = DIGIMONS.register("thetismon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                .setDefaultSpMove("doctase")
                                    .setEvoStage(3)
                                    .setXpDrop(3).setBabyAndXpDrop(BabyDigimonItems.PUYOMON)
                                    .setAnimations("idle3","sit6","walk10","fly6",null,"shoot7")
                            , MobCategory.CREATURE)
                    .sized(1.50f,2.8f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "thetismon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> METALGREYMONVIRUS = DIGIMONS.register("metalgreymonvirus",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                .setDefaultSpMove("poison_breath")
                                    .setEvoStage(3)
                                    .setMountDigimon(0.35)
                                    .setXpDrop(5).setBabyAndXpDrop(BabyDigimonItems.BOTAMON)
                                    .setAnimations("idle9","sit6","walk9","float2","attack11",null)
                            , MobCategory.CREATURE)
                    .sized(1.5f,2.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "metalgreymonvirus").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GROUNDRAMON = DIGIMONS.register("groundramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                .setDefaultSpMove("magma_spit")
                                    .setEvoStage(3)
                                    .setMountDigimon(0)
                                    .setXpDrop(6).setBabyAndXpDrop(BabyDigimonItems.BOTAMON, BabyDigimonItems.PETITMON)
                                    .setAnimations("idle5","sit5","walk7",null,"attack4","shoot3")
                            , MobCategory.CREATURE)
                    .sized(1.7f,1.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "groundramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BRACHIMON = DIGIMONS.register("brachimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                .setDefaultSpMove("magma_spit")
                                    .setEvoStage(3)
                                    .setMountDigimon(0)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.BOTAMON, BabyDigimonItems.SUNAMON, BabyDigimonItems.DATIRIMON)
                                    .setAnimations("idle5","sit5",null,null,"attack4","shoot2")
                            , MobCategory.CREATURE)
                    .sized(1.5f,3.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "brachimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GOGMAMON = DIGIMONS.register("gogmamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                .setDefaultSpMove("crystal_rain")
                                    .setEvoStage(3)
                                    .setMountDigimon(0.1)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.SUNAMON)
                                    .setAnimations("idle8","sit5","walk9",null,"attack8","attack11")
                            , MobCategory.CREATURE)
                    .sized(1.5f,2.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gogmamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> ALTURKABUTERIMONBLUE = DIGIMONS.register("alturkabuterimonblue",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                .setDefaultSpMove("mega_blaster")
                                    .setEvoStage(3)
                                    .setXpDrop(2).setBabyAndXpDrop(BabyDigimonItems.BUBBMON)
                                    .setAnimations("idle9","sit5","walk9",null,"attack7","shoot5")
                            , MobCategory.CREATURE)
                    .sized(1.5f,2.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "alturkabuterimonblue").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> ALTURKABUTERIMON = DIGIMONS.register("alturkabuterimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("mega_blaster")
                                    .setEvoStage(3)
                                    .setXpDrop(2).setBabyAndXpDrop(BabyDigimonItems.BUBBMON)
                                    .setAnimations("idle9","sit5","walk9",null,"attack7","shoot5")
                            , MobCategory.CREATURE)
                    .sized(1.5f,2.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "alturkabuterimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> MONZAEMON = DIGIMONS.register("monzaemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                .setDefaultSpMove("love_song")
                                    .setEvoStage(3)
                                    .setXpDrop(7).setBabyAndXpDrop(
                                            BabyDigimonItems.BOTAMON, BabyDigimonItems.BUBBMON, BabyDigimonItems.POYOMON, BabyDigimonItems.SUNAMON,
                                            BabyDigimonItems.CONOMON, BabyDigimonItems.JYARIMON, BabyDigimonItems.PUNIMON, BabyDigimonItems.PUYOMON,
                                            BabyDigimonItems.PETITMON, BabyDigimonItems.DATIRIMON, BabyDigimonItems.NYOKIMON, BabyDigimonItems.KIIMON,
                                            BabyDigimonItems.DOKIMON)
                                    .setAnimations("idle3","sit6","walk7",null,null,"attack5")
                            , MobCategory.CREATURE)
                    .sized(1.5f,2.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "monzaemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BLACKMACHGAOGAMON = DIGIMONS.register("blackmachgaogamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                .setDefaultSpMove("mach_tornado")
                                    .setEvoStage(3)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.PUNIMON)
                                    .setAnimations("idle3","sit13","walk10",null,null,"shoot5")
                            , MobCategory.CREATURE)
                    .sized(1.50f,2.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "blackmachgaogamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GARUDAMON = DIGIMONS.register("garudamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                .setDefaultSpMove("mach_tornado")
                                    .setEvoStage(3)
                                    .setMountDigimon(1)
                                    .setXpDrop(4).setBabyAndXpDrop(BabyDigimonItems.NYOKIMON)
                                    .setAnimations("idle3","sit13","walk9","float2","attack6","shoot7")
                            , MobCategory.CREATURE)
                    .sized(1.9f,3.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "garudamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> OKUWAMON = DIGIMONS.register("okuwamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                .setDefaultSpMove("cruel_sissors")
                                    .setEvoStage(3)
                                    .setMountDigimon(0.2)
                                    .setXpDrop(2).setBabyAndXpDrop(BabyDigimonItems.BUBBMON, BabyDigimonItems.DOKIMON)
                                    .setAnimations("idle8","sit5","walk7","fly7","attack7","attack6")
                            , MobCategory.CREATURE)
                    .sized(1.5f,2.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "okuwamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> SHAWUJINMON = DIGIMONS.register("shawujinmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                .setDefaultSpMove("gatling_arm")
                                    .setEvoStage(3)
                                    .setXpDrop(3).setBabyAndXpDrop(BabyDigimonItems.DATIRIMON)
                                    .setAnimations("idle4","sit14","walk6",null,"attack5","shoot5")
                            , MobCategory.CREATURE)
                    .sized(1.25f,3.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "shawujinmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> ANDIRAMON = DIGIMONS.register("andiramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                .setDefaultSpMove("divine_axe")
                                    .setEvoStage(3)
                                    .setXpDrop(8).setBabyAndXpDrop(BabyDigimonItems.CONOMON)
                                    .setAnimations("idle8","sit13","walk10",null,"attack7",null)
                            , MobCategory.CREATURE)
                    .sized(1.25f,2.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "andiramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> HOLYANGEMON = DIGIMONS.register("holyangemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomFlyingDigimon(type, world)
                                .setDefaultSpMove("divine_axe")
                                    .setEvoStage(3)
                                    .setXpDrop(8).setBabyAndXpDrop(BabyDigimonItems.POYOMON)
                                    .setAnimations("idle8","sit9","walk6","fly6","attack11","shoot5")
                            , MobCategory.CREATURE)
                    .sized(1.25f,3.3f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "holyangemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> MARINEDEVIM0N = DIGIMONS.register("marinedevimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                .setDefaultSpMove("doctase")
                                    .setEvoStage(3)
                                    .setXpDrop(7).setBabyAndXpDrop(BabyDigimonItems.PUYOMON, BabyDigimonItems.KIIMON)
                                    .setAnimations(null,"sit9","walk9",null,"attack6","shoot")
                            , MobCategory.CREATURE)
                    .sized(1.25f,3.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "marinedevimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> PAILDRAMON = DIGIMONS.register("paildramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("gatling_arm")
                                    .setEvoStage(3)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.CHICOMON)
                                    .setAnimations("idle3", "sit13", "walk10", null, "attack13", "shoot3")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 3f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "paildramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> CYBERDRAMON = DIGIMONS.register("cyberdramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("mach_tornado")
                                    .setEvoStage(3)
                                    .setXpDrop(5).setBabyAndXpDrop(BabyDigimonItems.CHICOMON, BabyDigimonItems.JYARIMON)
                                    .setAnimations("idle8", "sit9", "walk5", null, "attack12", "shoot8")
                            , MobCategory.CREATURE)
                    .sized(1.0f, 3f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "cyberdramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GOLDVEEDRAMON = DIGIMONS.register("goldveedramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("gold_arrow")
                                    .setEvoStage(3)
                                    .setXpDrop(0).setBabyAndXpDrop(BabyDigimonItems.BOTAMON, BabyDigimonItems.CHICOMON)
                                    .setAnimations("idle3", null, "walk7", null, null, "attack6")
                            , MobCategory.CREATURE)
                    .sized(1.25f, 2.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "goldveedramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> MANTICOREMON = DIGIMONS.register("manticoremon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("death_claw")
                                    .setEvoStage(3)
                                    .setMountDigimon(-0.2f)
                                    .setXpDrop(8).setBabyAndXpDrop(BabyDigimonItems.POYOMON, BabyDigimonItems.CHICOMON)
                                    .setAnimations("idle8", "sit6", "walk9", null, "attack7", "shoot8")
                            , MobCategory.CREATURE)
                    .sized(1.25f, 2.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "manticoremon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> SAGGITARIMON = DIGIMONS.register("saggitarimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world)
                                    .setDefaultSpMove("gold_arrow")
                                    .setEvoStage(3)
                                    .setMountDigimon(-0.2f)
                                    .setXpDrop(8).setBabyAndXpDrop(BabyDigimonItems.CHICOMON)
                                    .setAnimations("idle9", "sit5", "walk7", null, "attack3", "shoot10")
                            , MobCategory.CREATURE)
                    .sized(1.25f, 3.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "saggitarimon").toString()));
}