package net.modderg.thedigimod.projectiles;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.goods.*;
import net.modderg.thedigimod.item.DigiItems;
import net.modderg.thedigimod.particles.DigitalParticles;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DigitalProjectiles {

    public static DeferredRegister<EntityType<?>> PROJECTILES = DeferredRegister.create(
            ForgeRegistries.ENTITY_TYPES, TheDigiMod.MOD_ID);

    public static Map<String, RegistryObject<EntityType<?>>> projectileMap;

    public static void init() {

        List<RegistryObject<EntityType<?>>> projectileList = PROJECTILES.getEntries().stream().toList();
        List<String> projectileNameList = PROJECTILES.getEntries().stream().map(e -> e.getId().getPath()).toList();

        projectileMap = IntStream.range(0, projectileNameList.size())
                .boxed()
                .collect(Collectors.toMap(projectileNameList::get, projectileList::get));
    }

    public static final RegistryObject<EntityType<CustomProjectile>> BULLET = PROJECTILES.register("bullet",
            () -> EntityType.Builder.<CustomProjectile>of(CustomProjectile::new, MobCategory.MISC)
                    .sized(1.0f,1.0f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bullet").toString()));

    //dragon
    public static final RegistryObject<EntityType<CustomProjectile>> PEPPER_BREATH = PROJECTILES.register("pepper_breath",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "pepper_breath", DigitalParticles.PEPPER_BREATH.get())
                                    .setBright()
                                    .setFire()
                            ,MobCategory.MISC)
                    .sized(0.6f,0.6f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "pepper_breath").toString()));

    public static final RegistryObject<EntityType<CustomProjectile>> MEGA_FLAME = PROJECTILES.register("mega_flame",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "mega_flame", DigitalParticles.PEPPER_BREATH.get())
                                    .setBright()
                                    .setFire()
                            ,MobCategory.MISC)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mega_flame").toString()));

    public static final RegistryObject<EntityType<CustomProjectile>> V_ARROW = PROJECTILES.register("v_arrow",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "v_arrow", DigitalParticles.ENERGY_STAR.get())
                                    .setBright()
                            ,MobCategory.MISC)
                    .sized(1.2f,0.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "v_arrow").toString()));

    public static final RegistryObject<EntityType<CustomProjectile>> HYPER_HEAT = PROJECTILES.register("hyper_heat",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "hyper_heat", DigitalParticles.RED_ENERGY_STAR.get())
                                    .setBright()
                                    .setFire()
                            ,MobCategory.MISC)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "hyper_heat").toString()));

    public static final RegistryObject<EntityType<CustomProjectile>> METEOR_WING = PROJECTILES.register("meteor_wing",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "meteor_wing", DigitalParticles.PEPPER_BREATH.get())
                                    .setBright()
                                    .setFire()
                            ,MobCategory.MISC)
                    .sized(1.2f,1.2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "meteor_wing").toString()));

    //beast
    public static final RegistryObject<EntityType<CustomProjectile>> BEAST_SLASH = PROJECTILES.register("beast_slash",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "beast_slash", DigitalParticles.STINGER.get())
                                    .setPhysical()
                            , MobCategory.MISC)
                    .sized(1f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "beast_slash").toString()));

    public static final RegistryObject<EntityType<CustomProjectile>> BEAR_PUNCH = PROJECTILES.register("bear_punch",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                                    new CustomProjectile(type,level, "bear_punch", new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(DigiItems.CHIP_BEAST_SLASH.get())))
                                            .setPhysical()
                            , MobCategory.MISC)
                    .sized(0.7f,0.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bear_punch").toString()));

    //aquan
    public static final RegistryObject<EntityType<CustomProjectile>> INK_GUN = PROJECTILES.register("ink_gun",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "ink_gun", new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.INK_SAC)))
                                    .addEffect(MobEffects.POISON)
                            , MobCategory.MISC)
                    .sized(0.7f,0.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "ink_gun").toString()));

    public static final RegistryObject<EntityType<CustomProjectile>> SNOW_BULLET = PROJECTILES.register("snow_bullet",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                                    new CustomProjectile(type,level, "snow_bullet", new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SNOWBALL)))
                                            .setRepeat(4)
                            , MobCategory.MISC)
                    .sized(0.6f,0.6f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "snow_bullet").toString()));

    //machine
    public static final RegistryObject<EntityType<CustomProjectile>> PETIT_THUNDER = PROJECTILES.register("petit_thunder",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "petit_thunder", DigitalParticles.THUNDER_ATTACK.get())
                                    .setBright()
                                    .addOwnerEffect(MobEffects.MOVEMENT_SPEED)
                            ,MobCategory.MISC)
                    .sized(0.6f,0.6f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "petit_thunder").toString()));

    public static final RegistryObject<EntityType<CustomProjectile>> MEGA_BLASTER = PROJECTILES.register("mega_blaster",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "mega_blaster", DigitalParticles.THUNDER_ATTACK.get())
                                    .setBright()
                                    .addOwnerEffect(MobEffects.MOVEMENT_SPEED)
                            , MobCategory.MISC)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mega_blaster").toString()));

    public static final RegistryObject<EntityType<CustomProjectile>> THUNDERBOLT = PROJECTILES.register("thunderbolt",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "thunderbolt", DigitalParticles.THUNDER_ATTACK.get())
                                    .setBright()
                                    .addOwnerEffect(MobEffects.MOVEMENT_SPEED)
                            , MobCategory.MISC)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "thunderbolt").toString()));

    public static final RegistryObject<EntityType<CustomProjectile>> GATLING_GUN = PROJECTILES.register("gatling_arm",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                                    new CustomProjectile(type,level, "gatling_arm", DigitalParticles.BULLET_PARTICLE.get())
                                            .setBright()
                                            .setRepeat(5)
                            , MobCategory.MISC)
                    .sized(1.2f,1.2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gatling_arm").toString()));

    public static final RegistryObject<EntityType<CustomProjectile>> DISC_ATTACK = PROJECTILES.register("disc_attack",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                                    new CustomProjectile(type,level, "disc_attack", DigitalParticles.ENERGY_STAR.get())
                                            .addOwnerEffect(MobEffects.MOVEMENT_SPEED)
                                            .setBright()
                                            .setRepeat(3)
                            , MobCategory.MISC)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "disc_attack").toString()));

    //insect
    public static final RegistryObject<EntityType<CustomProjectile>> DEADLY_STING = PROJECTILES.register("deadly_sting",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "deadly_sting", DigitalParticles.STINGER.get())
                                    .addEffect(MobEffects.POISON)
                            ,MobCategory.MISC)
                    .sized(0.7f,0.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "deadly_sting").toString()));

    //wind
    public static final RegistryObject<EntityType<CustomProjectile>> PETIT_TWISTER = PROJECTILES.register("petit_twister",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                                    new CustomProjectile(type,level, "petit_twister", new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(DigiItems.CHIP_PETIT_TWISTER.get())))
                                            .addEffect(MobEffects.POISON)
                            ,MobCategory.MISC)
                    .sized(0.7f,0.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "petit_twister").toString()));

    //nightmare
    public static final RegistryObject<EntityType<CustomProjectile>> TRON_FLAME = PROJECTILES.register("tron_flame",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                                    new CustomProjectile(type,level, "tron_flame", new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(DigiItems.BLACK_DIGITRON.get())))
                                            .setBright()
                                            .setFire()
                            ,MobCategory.MISC)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tron_flame").toString()));

    public static final RegistryObject<EntityType<CustomProjectile>> DEATH_CLAW = PROJECTILES.register("death_claw",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                                    new CustomProjectile(type,level, "death_claw", new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(DigiItems.BLACK_DIGITRON.get())))
                                            .setBright()
                            ,MobCategory.MISC)
                    .sized(1f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "death_claw").toString()));

    public static final RegistryObject<EntityType<CustomProjectile>> POISON_BREATH = PROJECTILES.register("poison_breath",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                                    new CustomProjectile(type,level, "poison_breath", new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.PINK_DYE)))
                                            .setBright()
                                            .addEffect(MobEffects.POISON)
                            ,MobCategory.MISC)
                    .sized(0.7f,0.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "poison_breath").toString()));


    public static final RegistryObject<EntityType<CustomProjectile>> NIGHT_OF_FIRE = PROJECTILES.register("night_of_fire",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                                    new CustomProjectile(type,level, "night_of_fire", DigitalParticles.PEPPER_BREATH.get())
                                            .setBright()
                                            .setFire()
                            ,MobCategory.MISC)
                    .sized(0.6f,0.6f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "night_of_fire").toString()));

    //holy
    public static final RegistryObject<EntityType<CustomProjectile>> HEAVENS_KNUCKLE = PROJECTILES.register("heavens_knuckle",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "heavens_knuckle", DigitalParticles.HOLY_CROSS.get()).setPhysical()
                                    .setBright()
                            , MobCategory.MISC)
                    .sized(0.9f,1.2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "heavens_knuckle").toString()));

    public static final RegistryObject<EntityType<CustomProjectile>> HOLY_SHOOT = PROJECTILES.register("holy_shoot",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "holy_shoot", DigitalParticles.HOLY_CROSS.get())
                                    .healOnHit(10)
                            , MobCategory.MISC)
                    .sized(0.7f,0.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "holy_shoot").toString()));

    //earth
    public static final RegistryObject<EntityType<CustomProjectile>> SAND_BLAST = PROJECTILES.register("sand_blast",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                                    new CustomProjectile(type,level, "sand_blast", new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SAND)))
                                            .setRepeat(8)
                            , MobCategory.MISC)
                    .sized(0.3f,0.3f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "sand_blast").toString()));

    public static final RegistryObject<EntityType<CustomFallingProjectile>> GLIDING_ROCKS = PROJECTILES.register("gliding_rocks",
            () -> EntityType.Builder.<CustomFallingProjectile>of((type, level) -> (CustomFallingProjectile)
                                    new CustomFallingProjectile(type,level, "gliding_rocks", DigitalParticles.ROCK_PARTICLE.get())
                                            .setRepeat(3)
                            , MobCategory.MISC)
                    .sized(0.7f,0.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gliding_rocks").toString()));

    //shit
    public static final RegistryObject<EntityType<CustomProjectile>> POOP_THROW = PROJECTILES.register("poop_throw",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "poop_throw", DigitalParticles.POOP_PARTICLE.get()), MobCategory.MISC)
                    .sized(0.5f,0.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "poop_throw").toString()));

    //training goods
    public static final RegistryObject<EntityType<PunchingBag>> PUNCHING_BAG = PROJECTILES.register("punching_bag",
            () -> EntityType.Builder.of(PunchingBag:: new, MobCategory.MISC)
                    .sized(1.0f,2.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "punching_bag").toString()));

    public static final RegistryObject<EntityType<SpTarget>> SP_TARGET = PROJECTILES.register("target",
            () -> EntityType.Builder.of(SpTarget:: new, MobCategory.MISC)
                    .sized(1.0f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "target").toString()));

    public static final RegistryObject<EntityType<SpTableBook>> SP_TABLE = PROJECTILES.register("defence_table",
            () -> EntityType.Builder.of(SpTableBook:: new, MobCategory.MISC)
                    .sized(1.0f,1.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "defence_table").toString()));

    public static final RegistryObject<EntityType<ShieldStand>> SHIELD_STAND = PROJECTILES.register("shield",
            () -> EntityType.Builder.of(ShieldStand:: new, MobCategory.MISC)
                    .sized(1.0f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "shield").toString()));

    public static final RegistryObject<EntityType<UpdateGood>> UPDATE_GOOD = PROJECTILES.register("update",
            () -> EntityType.Builder.of(UpdateGood:: new, MobCategory.MISC)
                    .sized(1.0f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "update").toString()));

    public static final RegistryObject<EntityType<DragonBone>> DRAGON_BONE = PROJECTILES.register("dragon_bone",
            () -> EntityType.Builder.<DragonBone>of((type, world) -> (DragonBone) new DragonBone(type, world).setStatMultiplier(1.25f), MobCategory.MISC)
                    .sized(1.25f,0.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "dragon_bone").toString()));

    public static final RegistryObject<EntityType<BallGood>> BALL_GOOD = PROJECTILES.register("ball_good",
            () -> EntityType.Builder.<BallGood>of((type, world) -> (BallGood) new BallGood(type, world).setStatMultiplier(1.25f), MobCategory.MISC)
                    .sized(1.25f,1.25f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "ball_good").toString()));

    public static final RegistryObject<EntityType<ClownBox>> CLOWN_BOX = PROJECTILES.register("clown_box",
            () -> EntityType.Builder.<ClownBox>of((type, world) -> (ClownBox) new ClownBox(type, world).setStatMultiplier(1.25f), MobCategory.MISC)
                    .sized(1f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "clown_box").toString()));

    public static final RegistryObject<EntityType<FlytrapGood>> FLYTRAP_GOOD = PROJECTILES.register("flytrap_good",
            () -> EntityType.Builder.<FlytrapGood>of((type, world) -> (FlytrapGood) new FlytrapGood(type, world).setStatMultiplier(1.25f), MobCategory.MISC)
                    .sized(0.75f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "flytrap_good").toString()));

    public static final RegistryObject<EntityType<OldPc>> OLD_PC_GOOD = PROJECTILES.register("old_pc",
            () -> EntityType.Builder.<OldPc>of((type, world) -> (OldPc) new OldPc(type, world).setStatMultiplier(1.25f), MobCategory.MISC)
                    .sized(1.5f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "old_pc").toString()));

    public static final RegistryObject<EntityType<LiraGood>> LIRA_GOOD = PROJECTILES.register("lira_good",
            () -> EntityType.Builder.<LiraGood>of((type, world) -> (LiraGood) new LiraGood(type, world).setStatMultiplier(1.25f), MobCategory.MISC)
                    .sized(1f,1.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "lira_good").toString()));

    public static final RegistryObject<EntityType<RedFreezer>> RED_FREEZER = PROJECTILES.register("red_freezer",
            () -> EntityType.Builder.<RedFreezer>of((type, world) -> (RedFreezer) new RedFreezer(type, world).setStatMultiplier(1.25f), MobCategory.MISC)
                    .sized(1f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "red_freezer").toString()));

    public static final RegistryObject<EntityType<WindVane>> WIND_VANE = PROJECTILES.register("wind_vane",
            () -> EntityType.Builder.<WindVane>of((type, world) -> (WindVane) new WindVane(type, world).setStatMultiplier(1.25f), MobCategory.MISC)
                    .sized(1f,2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "wind_vane").toString()));

    public static final RegistryObject<EntityType<TrainingRock>> TRAINING_ROCK = PROJECTILES.register("training_rock",
            () -> EntityType.Builder.<TrainingRock>of((type, world) -> (TrainingRock) new TrainingRock(type, world).setStatMultiplier(1.25f), MobCategory.MISC)
                    .sized(1.75f,1.75f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "training_rock").toString()));
}


