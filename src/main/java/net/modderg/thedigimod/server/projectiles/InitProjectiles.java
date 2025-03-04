package net.modderg.thedigimod.server.projectiles;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.server.item.TDItems;
import net.modderg.thedigimod.client.particles.DigitalParticles;
import net.modderg.thedigimod.server.projectiles.effects.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InitProjectiles {

    public static void register(IEventBus bus) {
        PROJECTILES.register(bus);
        init();
    }

    public static DeferredRegister<EntityType<?>> PROJECTILES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TheDigiMod.MOD_ID);

    public static Map<String, RegistryObject<EntityType<?>>> projectileMap;

    public static void init() {

        List<RegistryObject<EntityType<?>>> projectileList = PROJECTILES.getEntries().stream().toList();
        List<String> projectileNameList = PROJECTILES.getEntries().stream().map(e -> e.getId().getPath()).toList();

        projectileMap = IntStream.range(0, projectileNameList.size())
                .boxed()
                .collect(Collectors.toMap(projectileNameList::get, projectileList::get));
    }

    public static final RegistryObject<EntityType<ProjectileDefault>> BULLET = PROJECTILES.register("bullet",
            () -> EntityType.Builder.<ProjectileDefault>of(ProjectileDefault::new, MobCategory.MISC)
                    .sized(0.7f,0.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bullet").toString()));

    //dragon
    public static final RegistryObject<EntityType<ProjectileDefault>> PEPPER_BREATH =
            PROJECTILES.register("pepper_breath", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type,level)
                                    .setParticle(DigitalParticles.PEPPER_BREATH.get())
                                    .setBright()
                                    .addOnHitEffects(new ProjectileEffectFire(3))
                            ,MobCategory.MISC)
                    .sized(0.6f,0.6f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "pepper_breath").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> MEGA_FLAME =
            PROJECTILES.register("mega_flame", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type,level)
                                    .setParticle(DigitalParticles.PEPPER_BREATH.get())
                                    .setBright()
                                    .addOnHitEffects(new ProjectileEffectFire(3))
                            ,MobCategory.MISC)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mega_flame").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> V_ARROW =
            PROJECTILES.register("v_arrow", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type,level)
                                    .setParticle(DigitalParticles.ENERGY_STAR.get())
                                    .setBright()
                            ,MobCategory.MISC)
                    .sized(1.2f,0.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "v_arrow").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> X_ATTACK =
            PROJECTILES.register("x_attack", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                                    (type, level) -> new ProjectileDefault(type,level)
                                            .setParticle(DigitalParticles.ENERGY_STAR.get())
                                            .setBright()
                                            .addOnHitEffects(new ProjectileEffectFire(3))
                                    ,MobCategory.MISC)
                            .sized(1.2f,0.5f)
                            .build(new ResourceLocation(TheDigiMod.MOD_ID, "x_attack").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> GOLD_ARROW =
            PROJECTILES.register("gold_arrow", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                                    (type, level) -> new ProjectileDefault(type,level)
                                            .setParticle(DigitalParticles.THUNDER_ATTACK.get())
                                            .setBright()
                                            .addOnHitEffects(new ProjectilePotionEffect(MobEffects.GLOWING, 100))
                                    ,MobCategory.MISC)
                            .sized(1.2f,0.5f)
                            .build(new ResourceLocation(TheDigiMod.MOD_ID, "gold_arrow").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> HYPER_HEAT =
            PROJECTILES.register("hyper_heat", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileLaserDefault(type,level)
                                    .setParticle(DigitalParticles.RED_ENERGY_STAR.get())
                                    .setBright()
                                    .addOnHitEffects(new ProjectileEffectFire(3))
                            ,MobCategory.MISC)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "hyper_heat").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> METEOR_WING =
            PROJECTILES.register("meteor_wing", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type,level)
                                    .setParticle(DigitalParticles.PEPPER_BREATH.get())
                                    .setBright()
                                    .addOnHitEffects(new ProjectileEffectFire(3))
                            ,MobCategory.MISC)
                    .sized(1.2f,1.2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "meteor_wing").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> MAGMA_SPIT =
            PROJECTILES.register("magma_spit", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                                    (type, level) -> new ProjectileParticleStreamDefault(type,level)
                                            .setRange(7)
                                            .setStreams(4)
                                            .setParticle(Items.MAGMA_BLOCK)
                                            .setBright()
                                            .addOnHitEffects(new ProjectileEffectFire(5))
                                    ,MobCategory.MISC)
                            .sized(0.1f,0.1f)
                            .build(new ResourceLocation(TheDigiMod.MOD_ID, "magma_spit").toString()));

    //beast
    public static final RegistryObject<EntityType<ProjectileDefault>> BEAST_SLASH =
            PROJECTILES.register("beast_slash", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type,level)
                                    .setParticle(DigitalParticles.STINGER.get())
                                    .setPhysical()
                            , MobCategory.MISC)
                    .sized(1f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "beast_slash").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> WORLD_SLASH =
            PROJECTILES.register("world_slash", () ->
                EntityType.Builder.<ProjectileDefault>of(
                    (type, level) -> new ProjectileDefault(type, level)
                                    .setParticle(TDItems.CHIP_WORLD_SLASH.get())
                                    .setPhysical()
                            , MobCategory.MISC)
                    .sized(1f,0.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "world_slash").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> SECRET_IDENTITY =
            PROJECTILES.register("secret_identity", () ->
                EntityType.Builder.<ProjectileDefault>of(
                    (type, level) -> new ProjectileDefault(type, level)
                                    .setParticle(TDItems.CHIP_SECRET_IDENTITY.get())
                            , MobCategory.MISC)
                    .sized(1f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "secret_identity").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> BEAR_PUNCH =
            PROJECTILES.register("bear_punch", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                                    (type, level) -> new ProjectileDefault(type, level)
                                            .setParticle(TDItems.CHIP_BEAST_SLASH.get())
                                            .setPhysical()
                                    , MobCategory.MISC)
                            .sized(0.7f,0.7f)
                            .build(new ResourceLocation(TheDigiMod.MOD_ID, "bear_punch").toString()));

    //aquan
    public static final RegistryObject<EntityType<ProjectileDefault>> INK_GUN =
            PROJECTILES.register("ink_gun", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type, level)
                                    .setParticle(Items.INK_SAC)
                                    .addOnHitEffects(new ProjectilePotionEffect(MobEffects.POISON, 100))
                                    .addOnHitEffects(new ProjectilePotionEffect(MobEffects.BLINDNESS, 100))
                            , MobCategory.MISC)
                    .sized(0.7f,0.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "ink_gun").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> SNOW_BULLET =
            PROJECTILES.register("snow_bullet", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type,level)
                                    .setParticle(Items.SNOWBALL)
                                    .setRepeat(4)
                            , MobCategory.MISC)
                    .sized(0.6f,0.6f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "snow_bullet").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> OCEAN_STORM =
            PROJECTILES.register("ocean_storm", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type, level)
                                    .setParticle(TDItems.CHIP_OCEAN_STORM.get())
                                    .setRepeat(2)
                            ,MobCategory.MISC)
                    .sized(1f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "ocean_storm").toString()));

    //machine
    public static final RegistryObject<EntityType<ProjectileDefault>> PETIT_THUNDER =
            PROJECTILES.register("petit_thunder", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type, level)
                                    .setParticle(DigitalParticles.THUNDER_ATTACK.get())
                                    .setBright()
                                    .addOnHitEffects(new ProjectilePotionOwnerEffect(MobEffects.MOVEMENT_SPEED, 100))
                            ,MobCategory.MISC)
                    .sized(0.6f,0.6f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "petit_thunder").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> MEGA_BLASTER =
            PROJECTILES.register("mega_blaster", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileLaserDefault(type, level)
                                    .setParticle(TDItems.CHIP_MEGA_BLASTER.get())
                                    .setBright()
                                    .addOnHitEffects(new ProjectilePotionOwnerEffect(MobEffects.MOVEMENT_SPEED, 100))
                            , MobCategory.MISC)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mega_blaster").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> THUNDERBOLT =
            PROJECTILES.register("thunderbolt", () ->
                EntityType.Builder.<ProjectileDefault>of(
                    (type, level) -> new ProjectileDefault(type,level)
                                    .setParticle(DigitalParticles.THUNDER_ATTACK.get())
                                    .setBright()
                                    .addOnHitEffects(new ProjectilePotionOwnerEffect(MobEffects.MOVEMENT_SPEED, 100))
                            , MobCategory.MISC)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "thunderbolt").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> GATLING_GUN =
            PROJECTILES.register("gatling_arm", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type,level)
                                    .setParticle(DigitalParticles.BULLET_PARTICLE.get())
                                    .setBright()
                                    .setRepeat(5)
                            , MobCategory.MISC)
                    .sized(1.2f,1.2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gatling_arm").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> DISC_ATTACK =
            PROJECTILES.register("disc_attack", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type, level)
                                    .setParticle(DigitalParticles.ENERGY_STAR.get())
                                    .addOnHitEffects(new ProjectilePotionOwnerEffect(MobEffects.MOVEMENT_SPEED, 100))
                                    .setBright()
                                    .setRepeat(3)
                            , MobCategory.MISC)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "disc_attack").toString()));

    public static final RegistryObject<EntityType<ProjectileExplosion>> SMILEY_BOMB =
            PROJECTILES.register("smiley_bomb", () ->
                    EntityType.Builder.<ProjectileExplosion>of(
                            (type, level) -> (ProjectileExplosion) new ProjectileExplosion(type,level, 6)
                                    .setWaitTime(60)
                                    .setParticle(DigitalParticles.RED_EXPLOSION.get())
                                    .setParticleRadius(4d)
                            , MobCategory.MISC)
                    .sized(0.8f,0.8f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "smiley_bomb").toString()));

    public static final RegistryObject<EntityType<ProjectileExplosion>> GIGA_DESTROYER =
            PROJECTILES.register("giga_destroyer", () ->
                    EntityType.Builder.<ProjectileExplosion>of(
                        (type, level) -> (ProjectileExplosion) new ProjectileExplosion(type,level, 6)
                                .setWaitTime(0)
                                .setParticle(DigitalParticles.RED_EXPLOSION.get())
                                .setRepeat(2)
                                .setParticleRadius(5d)
                        , MobCategory.MISC)
                    .sized(0.8f,0.8f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "giga_destroyer").toString()));

    //insect
    public static final RegistryObject<EntityType<ProjectileDefault>> DEADLY_STING =
            PROJECTILES.register("deadly_sting", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type,level)
                                    .setParticle(TDItems.CHIP_DEADLY_STING.get())
                                    .addOnHitEffects(new ProjectilePotionEffect(MobEffects.POISON, 100))
                            ,MobCategory.MISC)
                    .sized(0.7f,0.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "deadly_sting").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> DOCTASE =
            PROJECTILES.register("doctase", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                                    (type, level) -> new ProjectileParticleStreamDefault(type,level)
                                            .setRange(6)
                                            .setStreams(3)
                                            .setParticle(Items.GLOW_INK_SAC)
                                            .addOnHitEffects(new ProjectileEffectHeal(10))
                                            .addOnHitEffects(new ProjectilePotionEffect(MobEffects.POISON, 100))
                                    , MobCategory.MISC)
                            .sized(0.1f,0.1f)
                            .build(new ResourceLocation(TheDigiMod.MOD_ID, "doctase").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> CRUEL_SISSORS =
            PROJECTILES.register("cruel_sissors", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                                    (type, level) -> new ProjectileUnderTarget(type,level)
                                            .setSpawnLife(15)
                                            .setParticle(Items.REDSTONE)
                                            .addOnHitEffects(new ProjectileEffectKnockUp())
                                            .addOnHitEffects(new ProjectilePotionEffect(MobEffects.WITHER, 100))
                                    ,MobCategory.MISC)
                            .sized(0.1f,0.1f)
                            .build(new ResourceLocation(TheDigiMod.MOD_ID, "cruel_sissors").toString()));

    //wind
    public static final RegistryObject<EntityType<ProjectileDefault>> PETIT_TWISTER =
            PROJECTILES.register("petit_twister", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type,level)
                                    .setParticle(TDItems.CHIP_PETIT_TWISTER.get())
                                    .addOnHitEffects(new ProjectileEffectKnockUp())
                            ,MobCategory.MISC)
                    .sized(0.7f,0.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "petit_twister").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> MACH_TORNADO =
            PROJECTILES.register("mach_tornado", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                                    (type, level) -> new ProjectileUnderTarget(type,level)
                                            .setParticle(TDItems.CHIP_MACH_TORNADO.get())
                                            .addOnHitEffects(new ProjectileEffectKnockUp())
                                            .addOnHitEffects(new ProjectileEffectFire(5))
                                    ,MobCategory.MISC)
                            .sized(0.7f,0.7f)
                            .build(new ResourceLocation(TheDigiMod.MOD_ID, "mach_tornado").toString()));

    //nightmare
    public static final RegistryObject<EntityType<ProjectileDefault>> TRON_FLAME =
            PROJECTILES.register("tron_flame", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type,level)
                                    .setBright()
                                    .setParticle(TDItems.BLACK_DIGITRON.get())
                                    .addOnHitEffects(new ProjectileEffectFire(3))
                                    .setDoppelgangerAttack()
                            ,MobCategory.MISC)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "tron_flame").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> DEATH_CLAW =
            PROJECTILES.register("death_claw", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type,level)
                                    .setParticle(TDItems.BLACK_DIGITRON.get())
                                    .setBright()
                            ,MobCategory.MISC)
                    .sized(1f,1f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "death_claw").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> POISON_BREATH =
            PROJECTILES.register("poison_breath", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileParticleStreamDefault(type,level)
                                    .setStreams(5)
                                    .setRange(6.5f)
                                    .setParticle(TDItems.CHIP_POISON_BREATH.get())
                                    .setDoppelgangerAttack()
                                    .addOnHitEffects(new ProjectilePotionEffect(MobEffects.POISON, 100))
                            ,MobCategory.MISC)
                    .sized(0.7f,0.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "poison_breath").toString()));


    public static final RegistryObject<EntityType<ProjectileDefault>> NIGHT_OF_FIRE =
            PROJECTILES.register("night_of_fire", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type,level)
                                    .setParticle(DigitalParticles.PEPPER_BREATH.get())
                                    .setBright()
                                    .addOnHitEffects(new ProjectileEffectFire(3))
                            ,MobCategory.MISC)
                    .sized(0.6f,0.6f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "night_of_fire").toString()));

    //holy
    public static final RegistryObject<EntityType<ProjectileDefault>> HEAVENS_KNUCKLE =
            PROJECTILES.register("heavens_knuckle", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type,level)
                                    .setPhysical()
                                    .setParticle(DigitalParticles.HOLY_CROSS.get())
                                    .setBright()
                            , MobCategory.MISC)
                    .sized(0.9f,1.2f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "heavens_knuckle").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> HOLY_SHOOT =
            PROJECTILES.register("holy_shoot", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type,level)
                                    .setParticle(DigitalParticles.HOLY_CROSS.get())
                                    .addOnHitEffects(new ProjectileEffectHeal(10))
                            , MobCategory.MISC)
                    .sized(0.7f,0.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "holy_shoot").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> LOVE_SONG =
            PROJECTILES.register("love_song", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                                    (type, level) -> new ProjectileParticleStreamDefault(type,level)
                                            .setRange(6)
                                            .setParticle(DigitalParticles.NOTE_PARTICLE.get())
                                            .addOnHitEffects(new ProjectileEffectHeal(10))
                                            .addOnHitEffects(new ProjectilePotionEffect(MobEffects.MOVEMENT_SLOWDOWN, 100))
                                            .setShootSound(SoundEvents.NOTE_BLOCK_BELL.get())
                                    , MobCategory.MISC)
                            .sized(0.7f,0.7f)
                            .build(new ResourceLocation(TheDigiMod.MOD_ID, "holy_shoot").toString()));

    public static final RegistryObject<EntityType<ProjectileDefault>> DIVINE_AXE =
            PROJECTILES.register("divine_axe", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                                    (type, level) -> new ProjectileSkyFallingDefault(type,level)
                                            .setParticle(Items.REDSTONE)
                                            .setBright()
                                            .setParticleRadius(2d)
                                            .addOnHitEffects(new ProjectileEffectHeal(15))
                                    , MobCategory.MISC)
                            .sized(1.5f,1f)
                            .build(new ResourceLocation(TheDigiMod.MOD_ID, "divine_axe").toString()));

    //earth
    public static final RegistryObject<EntityType<ProjectileDefault>> SAND_BLAST =
            PROJECTILES.register("sand_blast", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileParticleStreamDefault(type,level)
                                    .setStreams(3)
                                    .setRange(6)
                                    .setParticle(Items.SAND)
                                    .setRepeat(4)
                            , MobCategory.MISC)
                    .sized(0.3f,0.3f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "sand_blast").toString()));

    public static final RegistryObject<EntityType<ProjectileSkyFallingDefault>> GLIDING_ROCKS =
            PROJECTILES.register("gliding_rocks", () ->
                    EntityType.Builder.<ProjectileSkyFallingDefault>of(
                            (type, level) -> (ProjectileSkyFallingDefault) new ProjectileSkyFallingDefault(type,level)
                                    .setParticle(DigitalParticles.ROCK_PARTICLE.get())
                                    .setRepeat(3)
                            , MobCategory.MISC)
                    .sized(0.7f,0.7f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "gliding_rocks").toString()));

    public static final RegistryObject<EntityType<ProjectileSkyFallingDefault>> CRYSTAL_RAIN =
            PROJECTILES.register("crystal_rain", () ->
                    EntityType.Builder.<ProjectileSkyFallingDefault>of(
                                    (type, level) -> (ProjectileSkyFallingDefault) new ProjectileSkyFallingDefault(type,level)
                                            .setParticle(TDItems.CHIP_CRYSTAL_RAIN.get())
                                            .setBright()
                                            .setParticleRadius(2d)
                                    , MobCategory.MISC)
                            .sized(1.5f,5f)
                            .build(new ResourceLocation(TheDigiMod.MOD_ID, "crystal_rain").toString()));

    //shit
    public static final RegistryObject<EntityType<ProjectileDefault>> POOP_THROW =
            PROJECTILES.register("poop_throw", () ->
                    EntityType.Builder.<ProjectileDefault>of(
                            (type, level) -> new ProjectileDefault(type,level)
                                    .setParticle(DigitalParticles.POOP_PARTICLE.get())
                                    .addOnHitEffects(new ProjectileEffectDirty())
                            , MobCategory.MISC)
                    .sized(0.5f,0.5f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "poop_throw").toString()));
}


