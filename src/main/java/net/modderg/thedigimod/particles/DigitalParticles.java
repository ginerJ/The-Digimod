package net.modderg.thedigimod.particles;

import net.minecraft.core.particles.ParticleType;
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

    public static final RegistryObject<SimpleParticleType> ATTACK_UP =
            PARTICLE_TYPES.register( "attack_up", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> DEFENCE_UP =
            PARTICLE_TYPES.register( "defence_up", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SPATTACK_UP =
            PARTICLE_TYPES.register( "spattack_up", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SPDEFENCE_UP =
            PARTICLE_TYPES.register( "spdefence_up", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> EVO_PARTICLES =
            PARTICLE_TYPES.register( "evolution_particles", () -> new SimpleParticleType(true));
}
