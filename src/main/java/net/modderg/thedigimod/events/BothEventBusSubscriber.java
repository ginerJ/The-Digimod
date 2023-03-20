package net.modderg.thedigimod.events;

import jdk.jfr.Registered;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.particles.ModParticle;
import net.modderg.thedigimod.particles.custom.DigitroneParticles;

import java.rmi.registry.Registry;

@Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.Mod)
public class BothEventBusSubscriber {

    @SubscribeEvent
    public static void registerParticleFactories(final ParticleFactoryRegisterEvent event){
        Minecraft.getInstance().particleEngine.register(ModParticles.DIGITRONE_PARTICLES.get(),
                DigitroneParticles.Provider::new);
    }

}
