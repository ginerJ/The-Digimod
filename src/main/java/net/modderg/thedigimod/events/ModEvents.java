package net.modderg.thedigimod.events;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.client.Minecraft;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.particles.DigitalParticles;
import net.modderg.thedigimod.particles.custom.*;

public class ModEvents {
    @Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public class ModBusEvents {
        @SubscribeEvent
        public static void registerParticleFactories(final RegisterParticleProvidersEvent event){

            Minecraft.getInstance().particleEngine.register(DigitalParticles.DIGITRON_PARTICLES.get(), DigitronParticles.Provider::new);

            Minecraft.getInstance().particleEngine.register(DigitalParticles.MEAT_BUBBLE.get(), BubbleParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.MISTAKE_BUBBLE.get(), BubbleParticle.Provider::new);

            Minecraft.getInstance().particleEngine.register(DigitalParticles.ATTACK_UP.get(), UpParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.DEFENCE_UP.get(), UpParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.SPATTACK_UP.get(), UpParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.SPDEFENCE_UP.get(), UpParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.BATTLES_UP.get(), UpParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.HEALTH_UP.get(), UpParticle.Provider::new);

            Minecraft.getInstance().particleEngine.register(DigitalParticles.XP_PARTICLE.get(), DownParticle.Provider::new);

            Minecraft.getInstance().particleEngine.register(DigitalParticles.LIFE_PARTICLE.get(), LifeParticle.Provider::new);

            Minecraft.getInstance().particleEngine.register(DigitalParticles.EVO_PARTICLES.get(), UpParticle.Provider::new);
        }
    }
}
