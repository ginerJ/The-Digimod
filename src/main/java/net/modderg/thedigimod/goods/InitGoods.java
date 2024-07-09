package net.modderg.thedigimod.goods;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.item.InitItems;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InitGoods {

    public static void register(IEventBus bus) {
        GOODS.register(bus);
        init();
    }

    public static DeferredRegister<EntityType<?>> GOODS = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TheDigiMod.MOD_ID);

    public static Map<String, RegistryObject<EntityType<?>>> goodMap;
    public static void init() {
        List<RegistryObject<EntityType<?>>> digimonList = GOODS.getEntries().stream().toList();
        List<String> digimonNameList = GOODS.getEntries().stream().map(e -> e.getId().getPath()).toList();

        goodMap = IntStream.range(0, digimonNameList.size())
                .boxed()
                .collect(Collectors.toMap(digimonNameList::get, digimonList::get));
    }

    //training goods
    public static final RegistryObject<EntityType<AbstractTrainingGood>> PUNCHING_BAG = GOODS.register("punching_bag",
            () -> EntityType.Builder.<AbstractTrainingGood>of((type, world) -> new AbstractTrainingGood(type, world)
                                    .setName("punching_bag").setStatTrain("attack").setItem(InitItems.BAG_ITEM.get())

                            , MobCategory.MISC).sized(1.0f,2.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "punching_bag").toString()));

    public static final RegistryObject<EntityType<AbstractTrainingGood>> SP_TARGET = GOODS.register("target",
            () -> EntityType.Builder.<AbstractTrainingGood>of((type, world) -> new AbstractTrainingGood(type, world)
                                    .setName("target").setStatTrain("spattack").setItem(InitItems.TARGET_ITEM.get())

                            ,MobCategory.MISC).sized(1.0f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "target").toString()));

    public static final RegistryObject<EntityType<AbstractTrainingGood>> SP_TABLE = GOODS.register("defence_table",
            () -> EntityType.Builder.<AbstractTrainingGood>of((type, world) -> new AbstractTrainingGood(type, world)
                                    .setName("defence_table").setStatTrain("spdefence").setItem(InitItems.TABLE_ITEM.get())

                            , MobCategory.MISC).sized(1.0f,1.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "defence_table").toString()));

    public static final RegistryObject<EntityType<AbstractTrainingGood>> SHIELD_STAND = GOODS.register("shield",
            () -> EntityType.Builder.<AbstractTrainingGood>of((type, world) -> new AbstractTrainingGood(type, world)
                                    .setName("shield").setStatTrain("defence").setItem(InitItems.SHIELD_ITEM.get())

                            , MobCategory.MISC).sized(1.0f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "shield").toString()));

    public static final RegistryObject<EntityType<AbstractTrainingGood>> UPDATE_GOOD = GOODS.register("update",
            () -> EntityType.Builder.<AbstractTrainingGood>of((type, world) -> new AbstractTrainingGood(type, world)
                                    .setName("update").setStatTrain("health").setItem(InitItems.UPDATE_ITEM.get())

                            , MobCategory.MISC).sized(1.0f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "update").toString()));

    public static final RegistryObject<EntityType<AbstractTrainingGood>> DRAGON_BONE = GOODS.register("dragon_bone",
            () -> EntityType.Builder.<AbstractTrainingGood>of((type, world) -> new AbstractTrainingGood(type, world)
                                    .setName("dragon_bone").setStatTrain("attack").setItem(InitItems.DRAGON_BONE_ITEM.get())
                                    .setStatMultiplier(1.5f).setXpId(0)

                            , MobCategory.MISC).sized(1.25f,0.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "dragon_bone").toString()));

    public static final RegistryObject<EntityType<AbstractTrainingGood>> BALL_GOOD = GOODS.register("ball_good",
            () -> EntityType.Builder.<AbstractTrainingGood>of((type, world) -> new AbstractTrainingGood(type, world)
                                    .setName("ball_good").setStatTrain("attack").setItem(InitItems.BALL_GOOD_ITEM.get())
                                    .setStatMultiplier(1.5f).setXpId(1)

                            , MobCategory.MISC).sized(1.25f,1.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "ball_good").toString()));

    public static final RegistryObject<EntityType<AbstractTrainingGood>> FLYTRAP_GOOD = GOODS.register("flytrap_good",
            () -> EntityType.Builder.<AbstractTrainingGood>of((type, world) -> new AbstractTrainingGood(type, world)
                                    .setName("flytrap_good").setStatTrain("spdefence").setItem(InitItems.FLYTRAP_GOOD.get())
                                    .setStatMultiplier(1.5f).setXpId(2)

                            , MobCategory.MISC).sized(0.75f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "flytrap_good").toString()));

    public static final RegistryObject<EntityType<AbstractTrainingGood>> RED_FREEZER = GOODS.register("red_freezer",
            () -> EntityType.Builder.<AbstractTrainingGood>of((type, world) -> new AbstractTrainingGood(type, world)
                                    .setName("red_freezer").setStatTrain("spattack").setItem(InitItems.RED_FREEZER.get())
                                    .setStatMultiplier(1.5f).setXpId(3)

                            , MobCategory.MISC).sized(1f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "red_freezer").toString()));

    public static final RegistryObject<EntityType<AbstractTrainingGood>> WIND_VANE = GOODS.register("wind_vane",
            () -> EntityType.Builder.<AbstractTrainingGood>of((type, world) -> new AbstractTrainingGood(type, world)
                                    .setName("wind_vane").setStatTrain("spattack").setItem(InitItems.WIND_VANE.get())
                                    .setStatMultiplier(1.5f).setXpId(4)

                            ,MobCategory.MISC).sized(1f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "wind_vane").toString()));

    public static final RegistryObject<EntityType<AbstractTrainingGood>> OLD_PC_GOOD = GOODS.register("old_pc",
            () -> EntityType.Builder.<AbstractTrainingGood>of((type, world) -> new AbstractTrainingGood(type, world)
                                    .setName("old_pc").setStatTrain("defence").setItem(InitItems.OLD_PC_GOOD.get())
                                    .setStatMultiplier(1.5f).setXpId(5)

                            , MobCategory.MISC).sized(1.5f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "old_pc").toString()));

    public static final RegistryObject<EntityType<AbstractTrainingGood>> TRAINING_ROCK = GOODS.register("training_rock",
            () -> EntityType.Builder.<AbstractTrainingGood>of((type, world) -> new AbstractTrainingGood(type, world)
                                    .setName("training_rock").setStatTrain("defence").setItem(InitItems.TRAINING_ROCK.get())
                                    .setStatMultiplier(1.5f).setXpId(6)

                            , MobCategory.MISC).sized(1.75f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "training_rock").toString()));

    public static final RegistryObject<EntityType<AbstractTrainingGood>> CLOWN_BOX = GOODS.register("clown_box",
            () -> EntityType.Builder.<AbstractTrainingGood>of((type, world) -> new AbstractTrainingGood(type, world)
                                    .setName("clown_box").setStatTrain("spattack").setItem(InitItems.CLOWN_BOX.get())
                                    .setStatMultiplier(1.5f).setXpId(7)

                            , MobCategory.MISC).sized(1f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "clown_box").toString()));

    public static final RegistryObject<EntityType<AbstractTrainingGood>> LIRA_GOOD = GOODS.register("lira_good",
            () -> EntityType.Builder.<AbstractTrainingGood>of((type, world) -> new AbstractTrainingGood(type, world)
                                    .setName("lira_good").setStatTrain("spdefence").setItem(InitItems.LIRA_GOOD.get())
                                    .setStatMultiplier(1.5f).setXpId(8)

                            , MobCategory.MISC).sized(1f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "lira_good").toString()));

    public static final RegistryObject<EntityType<AbstractTrainingGood>> M2_DISK_GOOD = GOODS.register("m2_disk",
            () -> EntityType.Builder.<AbstractTrainingGood>of((type, world) -> new AbstractTrainingGood(type, world)
                                    .setName("m2_disk").setStatTrain("health").setItem(InitItems.M2_HEALTH_DISK.get())
                                    .setStatMultiplier(1.5f)

                            , MobCategory.MISC).sized(0.8f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "m2_disk").toString()));
}
