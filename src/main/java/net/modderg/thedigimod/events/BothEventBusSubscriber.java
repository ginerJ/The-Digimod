package net.modderg.thedigimod.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.particles.ModParticle;
import net.modderg.thedigimod.particles.custom.DigitronParticles;

@Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BothEventBusSubscriber {

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event){
        Minecraft.getInstance().particleEngine.register(ModParticle.DIGITRON_PARTICLES.get(),
                DigitronParticles.Provider::new);
    }

}
