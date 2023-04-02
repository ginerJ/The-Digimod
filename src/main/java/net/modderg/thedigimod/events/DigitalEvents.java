package net.modderg.thedigimod.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.goods.CustomTrainingGood;
import net.modderg.thedigimod.particles.DigitalParticles;
import net.modderg.thedigimod.particles.custom.DigitronParticles;
import net.modderg.thedigimod.particles.custom.StatUpParticles;

@Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DigitalEvents {

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event){

        Minecraft.getInstance().particleEngine.register(DigitalParticles.DIGITRON_PARTICLES.get(),
                DigitronParticles.Provider::new);

        Minecraft.getInstance().particleEngine.register(DigitalParticles.ATTACK_UP.get(),
                StatUpParticles.Provider::new);
        Minecraft.getInstance().particleEngine.register(DigitalParticles.DEFENCE_UP.get(),
                StatUpParticles.Provider::new);
        Minecraft.getInstance().particleEngine.register(DigitalParticles.SPATTACK_UP.get(),
                StatUpParticles.Provider::new);
        Minecraft.getInstance().particleEngine.register(DigitalParticles.SPDEFENCE_UP.get(),
                StatUpParticles.Provider::new);

    }
}
