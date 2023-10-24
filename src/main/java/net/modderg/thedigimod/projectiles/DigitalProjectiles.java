package net.modderg.thedigimod.projectiles;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.digimons.DigimonKoromon;
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
        List<RegistryObject<EntityType<?>>> digimonList = PROJECTILES.getEntries().stream().toList();
        List<String> digimonNameList = PROJECTILES.getEntries().stream().map(e -> e.getId().getPath()).toList();

        projectileMap = IntStream.range(0, digimonNameList.size())
                .boxed()
                .collect(Collectors.toMap(digimonNameList::get, digimonList::get));
    }

    public static final RegistryObject<EntityType<CustomProjectile>> BULLET = PROJECTILES.register("bullet",
            () -> EntityType.Builder.<CustomProjectile>of(CustomProjectile::new, MobCategory.MISC)
                    .sized(1.0f,1.0f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "bullet").toString()));

    //dragon

    public static final RegistryObject<EntityType<CustomProjectile>> PEPPER_BREATH = PROJECTILES.register("pepper_breath",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "pepper_breath", DigitalParticles.PEPPER_BREATH), MobCategory.MISC)
                    .sized(0.6f,0.6f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "pepper_breath").toString()));

    public static final RegistryObject<EntityType<CustomProjectile>> MEGA_FLAME = PROJECTILES.register("mega_flame",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "mega_flame", DigitalParticles.PEPPER_BREATH), MobCategory.MISC)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mega_flame").toString()));

    //machine

    public static final RegistryObject<EntityType<CustomProjectile>> PETIT_THUNDER = PROJECTILES.register("petit_thunder",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "petit_thunder", DigitalParticles.THUNDER_ATTACK), MobCategory.MISC)
                    .sized(0.6f,0.6f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "petit_thunder").toString()));

    public static final RegistryObject<EntityType<CustomProjectile>> MEGA_BLASTER = PROJECTILES.register("mega_blaster",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "mega_blaster", DigitalParticles.THUNDER_ATTACK), MobCategory.MISC)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "mega_blaster").toString()));

    public static final RegistryObject<EntityType<CustomProjectile>> THUNDERBOLT = PROJECTILES.register("thunderbolt",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "thunderbolt", DigitalParticles.THUNDER_ATTACK), MobCategory.MISC)
                    .sized(0.9f,0.9f)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "thunderbolt").toString()));

    //insect
    public static final RegistryObject<EntityType<CustomProjectile>> DEADLY_STING = PROJECTILES.register("deadly_sting",
            () -> EntityType.Builder.<CustomProjectile>of((type, level) ->
                            new CustomProjectile(type,level, "deadly_sting", DigitalParticles.THUNDER_ATTACK), MobCategory.MISC)
                    .sized(0.7f,7)
                    .build(new ResourceLocation(TheDigiMod.MOD_ID, "deadly_sting").toString()));
}


