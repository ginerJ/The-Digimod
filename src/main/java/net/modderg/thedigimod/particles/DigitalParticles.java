package net.modderg.thedigimod.particles;

import net.minecraft.client.particle.HugeExplosionParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;

public class DigitalParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES,TheDigiMod.MOD_ID);

    public static final RegistryObject<SimpleParticleType> DIGITRON_PARTICLES =
            PARTICLE_TYPES.register( "digitron_particles", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> MEAT_BUBBLE =
            PARTICLE_TYPES.register( "meat_bubble", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> MISTAKE_BUBBLE =
            PARTICLE_TYPES.register( "mistake_bubble", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SAD_BUBBLE =
            PARTICLE_TYPES.register( "sad_bubble", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> MEH_BUBBLE =
            PARTICLE_TYPES.register( "meh_bubble", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> HAPPY_BUBBLE =
            PARTICLE_TYPES.register( "happy_bubble", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> JOYFUL_BUBBLE =
            PARTICLE_TYPES.register( "joyful_bubble", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> ATTACK_UP =
            PARTICLE_TYPES.register( "attack_up", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> DEFENCE_UP =
            PARTICLE_TYPES.register( "defence_up", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SPATTACK_UP =
            PARTICLE_TYPES.register( "spattack_up", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SPDEFENCE_UP =
            PARTICLE_TYPES.register( "spdefence_up", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BATTLES_UP =
            PARTICLE_TYPES.register( "battles_up", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> HEALTH_UP =
            PARTICLE_TYPES.register( "health_up", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> LIFE_PARTICLE =
            PARTICLE_TYPES.register( "life_particle", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> EVO_PARTICLES =
            PARTICLE_TYPES.register( "evolution_particles", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> EVO_PARTICLES_CHAMPION =
            PARTICLE_TYPES.register( "evolution_particles_chmp", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> EVO_PARTICLES_ULTIMATE =
            PARTICLE_TYPES.register( "evolution_particles_ult", () -> new SimpleParticleType(true));

    //moves
    public static final RegistryObject<SimpleParticleType> BUBBLE_ATTACK =
            PARTICLE_TYPES.register( "small_bullet", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> NOTE_PARTICLE =
            PARTICLE_TYPES.register( "note_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> PEPPER_BREATH =
            PARTICLE_TYPES.register( "pepper_breath", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BULLET_PARTICLE =
            PARTICLE_TYPES.register( "bullet_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> THUNDER_ATTACK =
            PARTICLE_TYPES.register( "thunder", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> STINGER =
            PARTICLE_TYPES.register( "stinger", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> ENERGY_STAR =
            PARTICLE_TYPES.register( "energy_star", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> RED_ENERGY_STAR =
            PARTICLE_TYPES.register( "red_energy_star", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> POOP_PARTICLE =
            PARTICLE_TYPES.register( "poop_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> HOLY_CROSS =
            PARTICLE_TYPES.register( "holy_cross", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> ROCK_PARTICLE =
            PARTICLE_TYPES.register( "rock_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> RED_EXPLOSION =
            PARTICLE_TYPES.register( "red_explosion", () -> new SimpleParticleType(true));
}
