package net.modderg.thedigimod.particles;

import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.modderg.thedigimod.TheDigiMod;
import org.apache.http.config.Registry;

public class ModParticle {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DEFERRED_REGISTER.create(ForgeRegistries.PARTICLE_TYPES,TheDigiMod.MOD_ID);

    public static final RegisterObject<SimpleParticleType> DIGITRON_PARTICLES =
            PARTICLE_TYPES.register(bus "digitron_particles", () -> new SimpleParticleType(true));
    public static void register(IEventBus eventBus){
        PARTICLE_TYPES.register(eventBus);
    }
}
