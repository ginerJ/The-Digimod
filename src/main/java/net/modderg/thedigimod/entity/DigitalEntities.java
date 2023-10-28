package net.modderg.thedigimod.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.managers.EvolutionCondition;
import net.modderg.thedigimod.goods.*;
import net.modderg.thedigimod.item.DigiItems;

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

    public static final RegistryObject<EntityType<CustomDigimon>> KOROMON = DIGIMONS.register("koromon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BOTAMON}, DigiItems.DRAGON_DATA);

                            this.setAnimations("idle3", "sit7", "walk4",null,"attack2","shoot4");

                            this.setEvos("agumon","agumonblack",null,null,null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                            evoCondition2 = new EvolutionCondition(this).moodCheck("Sad");
                        }
                        @Override public String getSpecies() {return "Koromon";}
                    }, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "koromon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> MOCHIMON = DIGIMONS.register("mochimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BUBBMON}, DigiItems.PLANTINSECT_DATA);

                            this.setAnimations("idle3", "sit7", "walk4",null,"attack2","shoot4");

                            this.setEvos("kunemon","tentomon",null,null,null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                            evoCondition2 = new EvolutionCondition(this).moodCheck("Joyful");
                        }
                        @Override public String getSpecies() {return "Mochimon";}
                    }, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mochimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TSUNOMON = DIGIMONS.register("tsunomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.PUNIMON}, DigiItems.BEAST_DATA);

                            this.setAnimations("idle3", "sit7", "walk4",null,"attack2","shoot4");

                            this.setEvos("bearmon",null,null,null,null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                        }
                        @Override public String getSpecies() {return "Tsunomon";}
                    }, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tsunomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GIGIMON = DIGIMONS.register("gigimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.JYARIMON}, DigiItems.DRAGON_DATA);

                            this.setAnimations("idle3", "sit7", "walk4",null,"attack2","shoot4");

                            this.setEvos("guilmon",null,null,null,null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                        }
                        @Override public String getSpecies() {return "Gigimon";}
                    }, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gigimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BABYDMON = DIGIMONS.register("babydmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.PETITMON}, DigiItems.DRAGON_DATA);
                            this.canFlyDigimon = true;

                            this.setAnimations("idle8", "sit7", "walk4","bug_fly","attack2","shoot4");

                            this.setEvos("dracomon",null,null,null,null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                        }
                        @Override public String getSpecies() {return "Babydmon";}
                    }, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "babydmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> PUYOYOMON = DIGIMONS.register("puyoyomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.PUYOMON}, DigiItems.AQUAN_DATA);

                            this.setAnimations("idle3", "sit7", "walk4",null,"attack2","shoot4");

                            this.setEvos("jellymon",null,null,null,null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                        }
                        @Override public String getSpecies() {return "Puyoyomon";}
                    }, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "puyoyomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BIBIMON = DIGIMONS.register("bibimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.DOKIMON}, DigiItems.MACHINE_DATA);

                            this.setAnimations("idle3", "sit7", "walk4",null,"attack2","shoot4");

                            this.setEvos("pulsemon",null,null,null,null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                        }
                        @Override public String getSpecies() {return "Bibimon";}
                    }, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bibimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> YOKOMON = DIGIMONS.register("yokomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.NYOKIMON}, DigiItems.WIND_DATA);

                            this.setAnimations("idle3", "sit7", "walk4",null,"attack2","shoot4");

                            this.setEvos("piyomon",null,null,null,null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                        }
                        @Override public String getSpecies() {return "Yokomon";}
                    }, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "yokomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> KEEMON = DIGIMONS.register("keemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.KIIMON}, DigiItems.NIGHTMARE_DATA);

                            this.setAnimations("idle3", "sit7", "walk4",null,"attack2","shoot4");

                            this.setEvos("impmon",null,null,null,null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                        }
                        @Override public String getSpecies() {return "Keemon";}
                    }, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "keemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TOKOMON = DIGIMONS.register("tokomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.POYOMON}, DigiItems.HOLY_DATA);

                            this.setAnimations("idle3", "sit7", "walk4",null,"attack2","shoot4");

                            this.setEvos("patamon",null,null,null,null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                        }
                        @Override public String getSpecies() {return "Tokomon";}
                    }, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tokomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GOROMON = DIGIMONS.register("goromon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.SUNAMON}, DigiItems.EARTH_DATA);

                            this.setAnimations("idle3", "sit7", "walk4",null,"attack2","shoot4");

                            this.setEvos("sunarizamon",null,null,null,null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                        }
                        @Override public String getSpecies() {return "Goromon";}
                    }, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "goromon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> CHOCOMON = DIGIMONS.register("chocomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.CONOMON}, DigiItems.HOLY_DATA);

                            this.setAnimations("idle3", "sit7", "walk4",null,"attack2","shoot4");

                            this.setEvos("lopmon",null,null,null,null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                        }
                        @Override public String getSpecies() {return "Chocomon";}
                    }, MobCategory.CREATURE)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "chocomon").toString()));
    
    public static final RegistryObject<EntityType<CustomDigimon>> NUMEMON = DIGIMONS.register("numemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BUBBMON}, DigiItems.PLANTINSECT_DATA);

                            this.setAnimations("idle6","sit7","walk8",null,"attack2","shoot4");
                        }
                        @Override public String getSpecies() {return "Numemon";}

                    }, MobCategory.CREATURE)
                    .sized(1f,1.8f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "numemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> AGUMON = DIGIMONS.register("agumon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "pepper_breath");
                            this.evoStage = 1;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BOTAMON}, DigiItems.DRAGON_DATA);

                            this.setAnimations(null, null, "walk5",null,null,null);

                            this.setEvos("tyrannomon", "numemon", "flarerizamon","veedramon","greymon",null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                            evoCondition2 = new EvolutionCondition(this).moodCheck("Sad");
                            evoCondition3 = new EvolutionCondition(this).moodCheck("Meh").maxMistakes(10).minWins(10).xpOver(0,50);
                            evoCondition4 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(5).minWins(10).xpOver(6,50);
                            evoCondition5 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(0,50);
                        }
                        @Override public String getSpecies() {return "Agumon";}
                    }, MobCategory.CREATURE)
                    .sized(0.75f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "agumon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TYRANNOMON = DIGIMONS.register("tyrannomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BOTAMON}, DigiItems.DRAGON_DATA);
                            this.isMountDigimon = true;

                            this.sitAnim = "sit3";
                        }
                        @Override public String getSpecies() {return "Tyrannomon";}
                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {if (this.hasPassenger(entity)) {
                            moveF.accept(entity, this.getX(), this.getY() + this.getPassengersRidingOffset() + 0.1f, this.getZ());}}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2.2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tyrannomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> FLARERIZAMON = DIGIMONS.register("flarerizamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BOTAMON}, DigiItems.DRAGON_DATA);

                            this.setAnimations(null, "sit5", "walk9",null,"attack7","shoot5");
                        }
                        @Override public String getSpecies() {return "Flarerizamon";}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2.0f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "flarerizamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GREYMON = DIGIMONS.register("greymon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "mega_flame");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BOTAMON}, DigiItems.DRAGON_DATA);
                            this.isMountDigimon = true;

                            this.sitAnim = "sit4";
                        }
                        @Override public String getSpecies() {return "Greymon";}
                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(
                                    entity, this.getX(), this.getY() + this.getPassengersRidingOffset() + 0.1f, this.getZ());}}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "greymon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> VEEDRAMON = DIGIMONS.register("veedramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "v_arrow");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BOTAMON}, DigiItems.DRAGON_DATA);
                            this.isMountDigimon = true;

                        }
                        @Override public String getSpecies() {return "Veedramon";}
                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(
                                    entity, this.getX(), this.getY() + this.getPassengersRidingOffset() + 0.1f, this.getZ());}}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "veedramon").toString()));



    public static final RegistryObject<EntityType<CustomDigimon>> AGUMONBLACK = DIGIMONS.register("agumonblack",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "pepper_breath");
                            this.evoStage = 1;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BOTAMON}, DigiItems.DRAGON_DATA);

                            this.setAnimations(null, null, "walk5",null,null,null);

                            this.setEvos("darklizardmon", "numemon", "blackgrowlmon","greymonvirus","darktyrannomon",null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                            evoCondition2 = new EvolutionCondition(this).moodCheck("Sad");
                            evoCondition3 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(10).minWins(10).xpOver(0,50);
                            evoCondition4 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(0).minWins(25).xpOver(7,25).xpOver(0,25);
                            evoCondition5 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(5).minWins(10).xpOver(7,50);
                        }
                        @Override public String getSpecies() {return "Agumon(Black)";}
                    }, MobCategory.CREATURE)
                    .sized(0.75f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "agumonblack").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> DARKLIZARDMON = DIGIMONS.register("darklizardmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BOTAMON}, DigiItems.NIGHTMARE_DATA);

                            this.setAnimations(null, "sit5", "walk9",null,"attack7","shoot5");
                        }
                        @Override public String getSpecies() {return "Darklizardmon";}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2.0f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "darklizardmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GREYMONVIRUS = DIGIMONS.register("greymonvirus",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BOTAMON}, DigiItems.NIGHTMARE_DATA);
                            this.isMountDigimon = true;

                            this.sitAnim = "sit4";
                        }
                        @Override public String getSpecies() {return "Greymon(Virus)";}
                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(
                                    entity, this.getX(), this.getY() + this.getPassengersRidingOffset() + 0.1f, this.getZ());}}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "greymonvirus").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> DARKTYRANNOMON = DIGIMONS.register("darktyrannomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BOTAMON}, DigiItems.NIGHTMARE_DATA);
                            this.isMountDigimon = true;

                            this.sitAnim = "sit3";
                        }
                        @Override public String getSpecies() {return "DarkTyrannomon";}
                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {if (this.hasPassenger(entity)) {
                            moveF.accept(entity, this.getX(), this.getY() + this.getPassengersRidingOffset() + 0.1f, this.getZ());}}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2.2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "darktyrannomon").toString()));



    public static final RegistryObject<EntityType<CustomDigimon>> TENTOMON = DIGIMONS.register("tentomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "petit_thunder");
                            this.evoStage = 1;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BUBBMON}, DigiItems.PLANTINSECT_DATA);

                            this.sitAnim = "sit3";

                            this.setEvos("kuwagamon", "roachmon", "kabuterimon",null,null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                            evoCondition2 = new EvolutionCondition(this).moodCheck("Sad");
                            evoCondition3 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(2,50);
                        }
                        @Override public String getSpecies() {return "Tentomon";}
                    }, MobCategory.CREATURE)
                    .sized(0.75f,1.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tentomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> KUWAGAMON = DIGIMONS.register("kuwagamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BUBBMON}, DigiItems.PLANTINSECT_DATA);
                            this.isMountDigimon = true;
                            this.canFlyDigimon = true;

                            this.sitAnim = "sit6";
                            this.flyAnim = "bug_fly";
                        }
                        @Override public String getSpecies() {return "Kuwagamon";}
                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(
                                    entity, this.getX(), this.getY() + this.getPassengersRidingOffset() - 0.3, this.getZ());}}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "kuwagamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> KUNEMON = DIGIMONS.register("kunemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "petit_thunder");
                            this.evoStage = 1;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BUBBMON}, DigiItems.PLANTINSECT_DATA);

                            this.walkAnim = "walk3";
                            this.sitAnim = "sit2";
                            this.attackAnim = "attack3";

                            this.setEvos("kuwagamon", "roachmon", "flymon",null,null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                            evoCondition2 = new EvolutionCondition(this).moodCheck("Sad");
                            evoCondition3 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(2,50);
                        }
                        @Override public String getSpecies() {return "Kunemon";}
                    }, MobCategory.CREATURE)
                    .sized(1.25f,1.55f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "kunemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> ROACHMON = DIGIMONS.register("roachmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BUBBMON}, DigiItems.PLANTINSECT_DATA);

                            this.walkAnim = "walk2";
                            this.sitAnim = "sit6";
                        }
                        @Override public String getSpecies() {return "Roachmon";}

                    }, MobCategory.CREATURE)
                    .sized(1.0f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "roachmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> FLYMON = DIGIMONS.register("flymon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BUBBMON}, DigiItems.PLANTINSECT_DATA);
                            this.canFlyDigimon = true;
                            this.isMountDigimon = true;

                            this.walkAnim = "walk2";
                            this.flyAnim = "bug_fly2";
                        }
                        @Override public String getSpecies() {return "Flymon";}

                    }, MobCategory.CREATURE)
                    .sized(1.5f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "flymon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> KABUTERIMON = DIGIMONS.register("kabuterimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.BUBBMON}, DigiItems.PLANTINSECT_DATA);
                            this.isMountDigimon = true;
                            this.canFlyDigimon = true;

                            this.sitAnim = "sit5";
                            this.flyAnim = "bug_fly";
                        }
                        @Override public String getSpecies() {return "Kabuterimon";}
                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(
                                    entity, this.getX(), this.getY() + this.getPassengersRidingOffset() - 0.3, this.getZ());}}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "kabuterimon").toString()));



    public static final RegistryObject<EntityType<CustomDigimon>> BEARMON = DIGIMONS.register("bearmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 1;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.PUNIMON}, DigiItems.BEAST_DATA);

                            this.walkAnim = "walk7";
                            this.sitAnim = "sit";
                            this.shootAnim = "shoot5";

                            this.setEvos("grizzlymon", "numemon", "blackgaogamon","chakmon",null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                            evoCondition2 = new EvolutionCondition(this).moodCheck("Sad");
                            evoCondition3 = new EvolutionCondition(this).moodCheck("Sad").maxMistakes(10).minWins(10).xpOver(7,50);
                            evoCondition4 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(3,50);
                        }
                        @Override public String getSpecies() {return "Bearmon";}
                    }, MobCategory.CREATURE)
                    .sized(1f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bearmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GRIZZLYMON = DIGIMONS.register("grizzlymon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.PUNIMON}, DigiItems.BEAST_DATA);
                            this.isMountDigimon = true;

                            this.setAnimations(null,"sit2","walk7",null,"attack3","shoot6");
                        }
                        @Override public String getSpecies() {return "Grizzlymon";}
                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(
                                    entity, this.getX(), this.getY() + this.getPassengersRidingOffset() + entity.getMyRidingOffset() + 0.1, this.getZ());}}
                    }, MobCategory.CREATURE)
                    .sized(1.75f,2.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "grizzlymon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> CHAKMON = DIGIMONS.register("chakmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.PUNIMON}, DigiItems.AQUAN_DATA);

                            this.shootAnim = "shoot3";
                        }
                        @Override public String getSpecies() {return "Chakmon";}
                       }, MobCategory.CREATURE)
                    .sized(1f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "chakmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BLACKGAOGAMON = DIGIMONS.register("blackgaogamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.PUNIMON}, DigiItems.NIGHTMARE_DATA);
                            this.isMountDigimon = true;

                            this.setAnimations(null,"sit2","walk7",null,"attack3","shoot6");
                        }
                        @Override public String getSpecies() {return "BlackGaogamon";}
                        }, MobCategory.CREATURE)
                    .sized(1.5f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "blackgaogamon").toString()));



    public static final RegistryObject<EntityType<CustomDigimon>> GUILMON = DIGIMONS.register("guilmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "pepper_breath");
                            this.evoStage = 1;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.JYARIMON}, DigiItems.DRAGON_DATA);

                            this.idleAnim = "idle6";
                            this.walkAnim = "walk7";
                            this.sitAnim = "sit3";

                            this.setEvos("growlmondata", "numemon", "growlmon","blackgrowlmon",null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                            evoCondition2 = new EvolutionCondition(this).moodCheck("Sad");
                            evoCondition3 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(0,50);
                            evoCondition4 = new EvolutionCondition(this).moodCheck("Sad").maxMistakes(10).minWins(10).xpOver(7,50);
                        }
                        @Override public String getSpecies() {return "Guilmon";}
                    }, MobCategory.CREATURE)
                    .sized(1f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "guilmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GROWLMON = DIGIMONS.register("growlmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.JYARIMON}, DigiItems.DRAGON_DATA);
                            this.isMountDigimon = true;

                            this.sitAnim = "sit6";
                        }
                        @Override public String getSpecies() {return "Growlmon";}

                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(entity, this.getX(), this.getY() + this.getPassengersRidingOffset()/2, this.getZ());}
                        }
                    }, MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "growlmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GROWLMONDATA = DIGIMONS.register("growlmondata",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.JYARIMON}, DigiItems.DRAGON_DATA);
                            this.isMountDigimon = true;

                            this.sitAnim = "sit6";
                        }
                        @Override public String getSpecies() {return "Growlmon(Data)";}

                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(entity, this.getX(), this.getY() + this.getPassengersRidingOffset()/2, this.getZ());}
                        }
                    }, MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "growlmondata").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BLACKGROWLMON = DIGIMONS.register("blackgrowlmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.JYARIMON}, DigiItems.NIGHTMARE_DATA);
                            this.isMountDigimon = true;

                            this.sitAnim = "sit6";
                        }
                        @Override public String getSpecies() {return "BlackGrowlmon";}

                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(entity, this.getX(), this.getY() + this.getPassengersRidingOffset()/2, this.getZ());}
                        }
                    }, MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "blackgrowlmon").toString()));



    public static final RegistryObject<EntityType<CustomDigimon>> JELLYMON = DIGIMONS.register("jellymon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 1;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.PUYOMON}, DigiItems.AQUAN_DATA);
                            this.canFlyDigimon = true;

                            this.walkAnim = "walk7";
                            this.flyAnim = "float";

                            this.setEvos("octomon", "numemon", "gesomon","teslajellymon",null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                            evoCondition2 = new EvolutionCondition(this).moodCheck("Sad");
                            evoCondition3 = new EvolutionCondition(this).moodCheck("Sad").maxMistakes(5).minWins(10).xpOver(7,50);
                            evoCondition4 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(3,50);
                        }
                        @Override public String getSpecies() {return "Jellymon";}
                    }, MobCategory.CREATURE)
                    .sized(1f,1.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "jellymon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TESLAJELLYMON = DIGIMONS.register("teslajellymon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "petit_thunder");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.PUYOMON}, DigiItems.AQUAN_DATA);
                            this.canFlyDigimon = true;

                            this.setAnimations("idle3","sit6","walk5","fly4",null,null);
                        }
                        @Override public String getSpecies() {return "Teslajellymon";}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "teslajellymon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> OCTOMON = DIGIMONS.register("octomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.PUYOMON}, DigiItems.AQUAN_DATA);

                            this.setAnimations("idle6","sit2","walk8",null,"attack9","shoot3");
                        }
                        @Override public String getSpecies() {return "Octomon";}
                    }, MobCategory.CREATURE)
                    .sized(1f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "octomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GESOMON = DIGIMONS.register("gesomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.PUYOMON}, DigiItems.NIGHTMARE_DATA);

                            this.setAnimations("idle6","sit2","walk8",null,null,"shoot5");
                        }
                        @Override public String getSpecies() {return "Gesomon";}
                    }, MobCategory.CREATURE)
                    .sized(1f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gesomon").toString()));



    public static final RegistryObject<EntityType<CustomDigimon>> DRACOMON = DIGIMONS.register("dracomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "pepper_breath");
                            this.evoStage = 1;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.PETITMON}, DigiItems.DRAGON_DATA);

                            this.sitAnim = "sit3";

                            this.setEvos("airdramon", "numemon", "growlmondata","coredramongreen","coredramon",null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                            evoCondition2 = new EvolutionCondition(this).moodCheck("Sad");
                            evoCondition3 = new EvolutionCondition(this).moodCheck("Sad").maxMistakes(10).minWins(10);
                            evoCondition4 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(5).minWins(15).xpOver(6,50);
                            evoCondition5 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(0,50);
                        }
                        @Override public String getSpecies() {return "Dracomon";}
                    }, MobCategory.CREATURE)
                    .sized(1f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "dracomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> AIRDRAMON = DIGIMONS.register("airdramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.PETITMON}, DigiItems.WIND_DATA);
                            this.isMountDigimon = true;
                            this.canFlyDigimon = true;

                            this.setAnimations("idle3","sit2","fly3","fly2",null, "shoot5");
                        }
                        @Override public String getSpecies() {return "Airdramon";}
                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(
                                    entity, this.getX(), this.getY() + this.getPassengersRidingOffset()/1.8f, this.getZ());}}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "airdramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> COREDRAMONGREEN = DIGIMONS.register("coredramongreen",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.PETITMON}, DigiItems.EARTH_DATA);
                            this.isMountDigimon = true;

                            this.sitAnim = "sit6";
                        }
                        @Override public String getSpecies() {return "Coredramon(Green)";}

                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(entity, this.getX(), this.getY() + this.getPassengersRidingOffset()*1.01, this.getZ());}
                        }
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2.35f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "coredramongreen").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> COREDRAMON = DIGIMONS.register("coredramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.PETITMON}, DigiItems.DRAGON_DATA);
                            this.isMountDigimon = true;

                            this.sitAnim = "sit6";
                        }
                        @Override public String getSpecies() {return "Coredramon";}

                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(entity, this.getX(), this.getY() + this.getPassengersRidingOffset()*1.01, this.getZ());}
                        }
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2.35f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "coredramon").toString()));



    public static final RegistryObject<EntityType<CustomDigimon>> PULSEMON = DIGIMONS.register("pulsemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "petit_thunder");
                            this.evoStage = 1;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.DOKIMON}, DigiItems.MACHINE_DATA);

                            this.idleAnim = "idle3";
                            this.walkAnim = "walk2";
                            this.shootAnim = "shoot2";

                            this.setEvos("runnermon", "namakemon", "exermon","thunderballmon","bulkmon",null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                            evoCondition2 = new EvolutionCondition(this).moodCheck("Sad");
                            evoCondition3 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(5).minWins(10).xpOver(2,50);
                            evoCondition4 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(10).minWins(10).xpOver(5,50);
                            evoCondition5 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(0,50);
                        }
                        @Override public String getSpecies() {return "Pulsemon";}
                    }, MobCategory.CREATURE)
                    .sized(0.75f,1.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "pulsemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BULKMON = DIGIMONS.register("bulkmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.DOKIMON}, DigiItems.MACHINE_DATA);
                            this.isMountDigimon = true;

                            this.sitAnim = "sit5";
                            this.attackAnim = "attack7";
                        }
                        @Override public String getSpecies() {return "Bulkmon";}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2.35f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bulkmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> THUNDERBALLMON = DIGIMONS.register("thunderballmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.DOKIMON}, DigiItems.MACHINE_DATA);

                            this.sitAnim = "sit4";
                        }
                        @Override public String getSpecies() {return "Thunderballmon";}
                    }, MobCategory.CREATURE)
                    .sized(1f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "thunderballmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> NAMAKEMON = DIGIMONS.register("namakemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.DOKIMON}, DigiItems.BEAST_DATA);

                            this.walkAnim = "walk2";
                        }
                        @Override public String getSpecies() {return "Namakemon";}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "namakemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> EXERMON = DIGIMONS.register("exermon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.DOKIMON}, DigiItems.PLANTINSECT_DATA);

                            this.setAnimations("idle4","sit2",null,null,"attack5","shoot6");
                        }
                        @Override public String getSpecies() {return "Exermon";}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "exermon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> RUNNERMON = DIGIMONS.register("runnermon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.PUNIMON}, DigiItems.BEAST_DATA);
                            this.isMountDigimon = true;

                            this.setAnimations("idle3","sit2","walk7",null,"attack3","shoot3");
                        }
                        @Override public String getSpecies() {return "Runnermon";}
                    }, MobCategory.CREATURE)
                    .sized(1.5f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "runnermon").toString()));



    public static final RegistryObject<EntityType<CustomDigimon>> PIYOMON = DIGIMONS.register("piyomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 1;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.NYOKIMON}, DigiItems.WIND_DATA);
                            this.canFlyDigimon = true;

                            this.idleAnim = "idle3";
                            this.shootAnim = "shoot5";
                            this.flyAnim = "fly4";

                            this.setEvos("akatorimon", "numemon", "birdramon","saberdramon",null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                            evoCondition2 = new EvolutionCondition(this).moodCheck("Sad");
                            evoCondition3 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(4,50);
                            evoCondition4 = new EvolutionCondition(this).moodCheck("Sad").maxMistakes(10).minWins(10).xpOver(7,50);
                        }
                        @Override public String getSpecies() {return "Piyomon";}
                    }, MobCategory.CREATURE)
                    .sized(1f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "piyomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BIRDRAMON = DIGIMONS.register("birdramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.NYOKIMON}, DigiItems.WIND_DATA);
                            this.isMountDigimon = true;
                            this.canFlyDigimon = true;

                            this.setAnimations("idle7","sit4","walk9",null,"attack6","shoot5");
                        }
                        @Override public String getSpecies() {return "Birdramon";}
                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(
                                    entity, this.getX(), this.getY() + this.getPassengersRidingOffset()/2, this.getZ());}}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "birdramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> SABERDRAMON = DIGIMONS.register("saberdramon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.NYOKIMON}, DigiItems.NIGHTMARE_DATA);
                            this.isMountDigimon = true;
                            this.canFlyDigimon = true;

                            this.setAnimations("idle7","sit4","walk9",null,"attack6","shoot5");
                        }
                        @Override public String getSpecies() {return "Saberdramon";}
                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(
                                    entity, this.getX(), this.getY() + this.getPassengersRidingOffset()/2, this.getZ());}}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "saberdramon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> AKATORIMON = DIGIMONS.register("akatorimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.NYOKIMON}, DigiItems.WIND_DATA);
                            this.isMountDigimon = true;

                            this.walkAnim = "walk2";
                            this.sitAnim = "sit6";
                        }
                        @Override public String getSpecies() {return "Akatorimon";}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "akatorimon").toString()));



    public static final RegistryObject<EntityType<CustomDigimon>> LOPMON = DIGIMONS.register("lopmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 1;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.DOKIMON}, DigiItems.MACHINE_DATA);

                            this.idleAnim = "idle3";
                            this.walkAnim = "walk7";
                            this.sitAnim = "sit8";

                            this.setEvos("blackgalgomon", "numemon", "wendimon","turuiemon",null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                            evoCondition2 = new EvolutionCondition(this).moodCheck("Sad");
                            evoCondition3 = new EvolutionCondition(this).moodCheck("Sad").maxMistakes(10).minWins(10).xpOver(7,50);
                            evoCondition4 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(8,25).xpOver(1,25);
                        }
                        @Override public String getSpecies() {return "Lopmon";}
                    }, MobCategory.CREATURE)
                    .sized(0.75f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "lopmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BLACKGALGOMON = DIGIMONS.register("blackgalgomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.CONOMON}, DigiItems.NIGHTMARE_DATA);

                            this.walkAnim = "walk6";
                            this.shootAnim = "shoot5";
                        }
                        @Override public String getSpecies() {return "BlackGalgomon";}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "blackgalgomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TURUIEMON = DIGIMONS.register("turuiemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.CONOMON}, DigiItems.HOLY_DATA);

                            this.idleAnim = "idle6";
                            this.walkAnim = "walk7";
                            this.sitAnim = "sit6";
                        }
                        @Override public String getSpecies() {return "Turuiemon";}
                    }, MobCategory.CREATURE)
                    .sized(1.0f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "turuiemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> WENDIMON = DIGIMONS.register("wendimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.CONOMON}, DigiItems.NIGHTMARE_DATA);

                            this.idleAnim = "idle5";
                            this.sitAnim = "sit5";
                            this.attackAnim = "attack6";
                        }
                        @Override public String getSpecies() {return "Wendimon";}
                    }, MobCategory.CREATURE)
                    .sized(1.25f,2.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "wendimon").toString()));



    public static final RegistryObject<EntityType<CustomDigimon>> IMPMON = DIGIMONS.register("impmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 1;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.KIIMON}, DigiItems.NIGHTMARE_DATA);

                            this.walkAnim ="walk5";
                            this.sitAnim ="sit9";
                            this.attackAnim ="attack7";

                            this.setEvos("boogiemon", "bakemon", "icedevimon","wizardmon",null,null);

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                            evoCondition2 = new EvolutionCondition(this).moodCheck("Sad");
                            evoCondition3 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(10).minWins(10).xpOver(3,25).xpOver(7,25);
                            evoCondition4 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(7,50);
                        }
                        @Override public String getSpecies() {return "Impmon";}
                    }, MobCategory.CREATURE)
                    .sized(0.75f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "impmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BAKEMON = DIGIMONS.register("bakemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.KIIMON}, DigiItems.NIGHTMARE_DATA);
                            this.canFlyDigimon = true;

                            this.setAnimations("idle2","sit7","fly4","fly4",null,null);
                        }
                        @Override public String getSpecies() {return "Bakemon";}
                    }, MobCategory.CREATURE)
                    .sized(1f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bakemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> ICEDEVIMON = DIGIMONS.register("icedevimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.KIIMON}, DigiItems.NIGHTMARE_DATA);
                            this.canFlyDigimon = true;

                            this.setAnimations(null,"sit6","walk7","fly5","attack5","shoot6");
                        }
                        @Override public String getSpecies() {return "Icedevimon";}
                    }, MobCategory.CREATURE)
                    .sized(1f,2.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "icedevimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BOOGIEMON = DIGIMONS.register("boogiemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.KIIMON}, DigiItems.NIGHTMARE_DATA);
                            this.canFlyDigimon = true;

                            this.setAnimations(null,"sit6","walk7","fly5","attack5","shoot6");
                        }
                        @Override public String getSpecies() {return "Boogiemon";}
                    }, MobCategory.CREATURE)
                    .sized(1f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "boogiemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> WIZARDMON = DIGIMONS.register("wizardmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.KIIMON}, DigiItems.NIGHTMARE_DATA);

                            this.setAnimations("idle4","sit6","walk6",null,"attack5","shoot2");
                        }
                        @Override public String getSpecies() {return "Wizardmon";}
                    }, MobCategory.CREATURE)
                    .sized(1f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "wizardmon").toString()));



    public static final RegistryObject<EntityType<CustomDigimon>> SUNARIZAMON = DIGIMONS.register("sunarizamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 1;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.SUNAMON}, DigiItems.EARTH_DATA);

                            this.setAnimations("idle3","sit7",null,null,"attack3","shoot6");

                            this.setEvos("tortamon", "numemon", "flarerizamon","cyclomon","baboongamon","golemon");

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                            evoCondition2 = new EvolutionCondition(this).moodCheck("Sad");
                            evoCondition3 = new EvolutionCondition(this).moodCheck("Meh").maxMistakes(10).minWins(10).xpOver(0,50);
                            evoCondition4 = new EvolutionCondition(this).moodCheck("Sad").maxMistakes(10).minWins(10).xpOver(0,50);
                            evoCondition5 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(6,25).xpOver(1,25);
                            evoCondition6 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(6,50);
                        }
                        @Override public String getSpecies() {return "Sunarizamon";}
                    }, MobCategory.CREATURE)
                    .sized(1.25f,0.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "sunarizamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> GOLEMON = DIGIMONS.register("golemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.SUNAMON}, DigiItems.EARTH_DATA);
                            this.isMountDigimon = true;

                            this.idleAnim = "idle5";
                            this.sitAnim = "sit3";
                            this.attackAnim = "attack6";
                        }
                        @Override public String getSpecies() {return "Golemon";}
                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(entity, this.getX(), this.getY() + this.getPassengersRidingOffset(), this.getZ());}
                        }
                    }, MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "golemon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> BABOONGAMON = DIGIMONS.register("baboongamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.SUNAMON}, DigiItems.EARTH_DATA);
                            this.isMountDigimon = true;

                            this.setAnimations("idle8","sit5","walk9",null,"attack7",null);
                        }
                        @Override public String getSpecies() {return "Baboongamon";}
                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(entity, this.getX(), this.getY() + this.getPassengersRidingOffset(), this.getZ());}
                        }
                    }, MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "baboongamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> CYCLOMON = DIGIMONS.register("cyclomon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.SUNAMON}, DigiItems.EARTH_DATA);
                            this.isMountDigimon = true;

                            this.idleAnim = "idle5";
                            this.walkAnim = "walk7";
                            this.sitAnim = "sit7";
                        }
                        @Override public String getSpecies() {return "Cyclomon";}
                    }, MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "cyclomon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> TORTAMON = DIGIMONS.register("tortamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.SUNAMON}, DigiItems.EARTH_DATA);

                            this.setAnimations("idle5","sit2","walk7",null,"attack3","shoot6");
                        }
                        @Override public String getSpecies() {return "Tortamon";}
                    }, MobCategory.CREATURE)
                    .sized(1.25f,2.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tortamon").toString()));



    public static final RegistryObject<EntityType<CustomDigimon>> PATAMON = DIGIMONS.register("patamon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 1;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.SUNAMON}, DigiItems.EARTH_DATA);
                            this.canFlyDigimon = true;

                            this.setAnimations("idle5","sit2",null,"fly5","attack10","attack10");

                            this.setEvos("unimon", "numemon", "centalmon","mimicmon","pegasmon","angemon");

                            evoCondition = new EvolutionCondition(this).alwaysTrue();
                            evoCondition2 = new EvolutionCondition(this).moodCheck("Sad");
                            evoCondition3 = new EvolutionCondition(this).moodCheck("Sad").maxMistakes(5).minWins(10).xpOver(5,50);
                            evoCondition4 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(5).minWins(10).xpOver(7,25).xpOver(8,25);
                            evoCondition5 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(5).minWins(10).xpOver(8,50);
                            evoCondition6 = new EvolutionCondition(this).moodCheck("Joyful").maxMistakes(0).minWins(15).xpOver(8,50);
                        }
                        @Override public String getSpecies() {return "Patamon";}
                    }, MobCategory.CREATURE)
                    .sized(1.25f,1.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "patamon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> UNIMON = DIGIMONS.register("unimon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.POYOMON}, DigiItems.BEAST_DATA);
                            this.isMountDigimon = true;
                            this.canFlyDigimon = true;

                            this.setAnimations("idle3","sit2","walk7","fly4","attack3","shoot6");
                        }
                        @Override public String getSpecies() {return "Unimon";}
                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(entity, this.getX(), this.getY() + this.getPassengersRidingOffset() + entity.getMyRidingOffset() - 0.1, this.getZ());}
                        }
                    }, MobCategory.CREATURE)
                    .sized(1.5f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "unimon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> PEGASMON = DIGIMONS.register("pegasmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.POYOMON}, DigiItems.HOLY_DATA);
                            this.isMountDigimon = true;
                            this.canFlyDigimon = true;

                            this.setAnimations("idle3","sit2","walk7","fly4","attack3","shoot6");
                        }
                        @Override public String getSpecies() {return "Pegasmon";}
                    }, MobCategory.CREATURE)
                    .sized(1.5f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "pegasmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> MIMICMON = DIGIMONS.register("mimicmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.POYOMON}, DigiItems.HOLY_DATA);

                            this.idleAnim = "idle8";
                            this.walkAnim = "walk9";
                            this.sitAnim = "sit5";
                        }
                        @Override public String getSpecies() {return "Mimicmon";}
                    }, MobCategory.CREATURE)
                    .sized(1.5f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mimicmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> CENTALMON = DIGIMONS.register("centalmon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.POYOMON}, DigiItems.MACHINE_DATA);
                            this.isMountDigimon = true;

                            this.setAnimations("idle3","sit2","walk7",null,"attack3","shoot6");
                        }
                        @Override public String getSpecies() {return "Centalmon";}
                        @Override
                        protected void positionRider(Entity entity, MoveFunction moveF) {
                            if (this.hasPassenger(entity)) {moveF.accept(entity, this.getX(), this.getY() + this.getPassengersRidingOffset()/2, this.getZ());}
                        }
                    }, MobCategory.CREATURE)
                    .sized(1.5f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "centalmon").toString()));

    public static final RegistryObject<EntityType<CustomDigimon>> ANGEMON = DIGIMONS.register("angemon",
            () -> EntityType.Builder.<CustomDigimon>of((type, world) -> new CustomDigimon(type, world){
                        @Override
                        public void setDigimon() {
                            this.entityData.define(SPMOVENAME, "bullet");
                            this.evoStage = 2;
                            this.setBabyAndXpDrop(new RegistryObject[]{DigiItems.POYOMON}, DigiItems.HOLY_DATA);
                            this.canFlyDigimon = true;

                            this.setAnimations("idle8","sit9","walk7","float","attack8",null);
                        }
                        @Override public String getSpecies() {return "Angemon";}
                    }, MobCategory.CREATURE)
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


}