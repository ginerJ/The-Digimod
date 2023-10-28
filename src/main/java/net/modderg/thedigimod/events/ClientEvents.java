package net.modderg.thedigimod.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.DigitalEntities;
import net.modderg.thedigimod.goods.AbstractGoodRender;
import net.modderg.thedigimod.gui.KeyBindings;
import net.modderg.thedigimod.gui.StatsGui;
import net.modderg.thedigimod.particles.DigitalParticles;
import net.modderg.thedigimod.particles.custom.*;
import net.modderg.thedigimod.projectiles.CustomProjectileRender;
import net.modderg.thedigimod.projectiles.DigitalProjectiles;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientDistEventBusSubscriber {
        @SubscribeEvent
        public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(DigitalEntities.KOROMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.MOCHIMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.TSUNOMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.GIGIMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.BABYDMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.PUYOYOMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.BIBIMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.YOKOMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.KEEMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.TOKOMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.GOROMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.CHOCOMON.get(), CustomDigimonRender::new);
            
            event.registerEntityRenderer(DigitalEntities.AGUMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.TENTOMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.KABUTERIMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.GREYMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.GRIZZLYMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.BEARMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.KUNEMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.GUILMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.JELLYMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.TESLAJELLYMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.GROWLMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.GROWLMONDATA.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.BLACKGROWLMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.KUWAGAMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.DRACOMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.COREDRAMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.COREDRAMONGREEN.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.PULSEMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.BULKMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.AGUMONBLACK.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.DARKTYRANNOMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.TYRANNOMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.VEEDRAMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.CHAKMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.BLACKGAOGAMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.PIYOMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.BIRDRAMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.AKATORIMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.SABERDRAMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.NAMAKEMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.EXERMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.GREYMONVIRUS.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.DARKLIZARDMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.FLARERIZAMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.RUNNERMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.THUNDERBALLMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.OCTOMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.GESOMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.AIRDRAMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.ROACHMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.FLYMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.LOPMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.BLACKGALGOMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.TURUIEMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.WENDIMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.IMPMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.NUMEMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.BAKEMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.ICEDEVIMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.WIZARDMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.BOOGIEMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.SUNARIZAMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.GOLEMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.BABOONGAMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.CYCLOMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.TORTAMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.PATAMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.UNIMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.PEGASMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.MIMICMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.CENTALMON.get(), CustomDigimonRender::new);
            event.registerEntityRenderer(DigitalEntities.ANGEMON.get(), CustomDigimonRender::new);

            event.registerEntityRenderer(DigitalEntities.PUNCHING_BAG.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalEntities.SP_TARGET.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalEntities.SP_TABLE.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalEntities.SHIELD_STAND.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalEntities.UPDATE_GOOD.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalEntities.DRAGON_BONE.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalEntities.BALL_GOOD.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalEntities.CLOWN_BOX.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalEntities.FLYTRAP_GOOD.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalEntities.OLD_PC_GOOD.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalEntities.LIRA_GOOD.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalEntities.RED_FREEZER.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalEntities.WIND_VANE.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalEntities.TRAINING_ROCK.get(), AbstractGoodRender::new);

            event.registerEntityRenderer(DigitalProjectiles.BULLET.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.PEPPER_BREATH.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.MEGA_FLAME.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.MEGA_BLASTER.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.THUNDERBOLT.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.PETIT_THUNDER.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.DEADLY_STING.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.V_ARROW.get(), CustomProjectileRender::new);
        }

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

            Minecraft.getInstance().particleEngine.register(DigitalParticles.LIFE_PARTICLE.get(), LifeParticle.Provider::new);

            Minecraft.getInstance().particleEngine.register(DigitalParticles.EVO_PARTICLES.get(), UpParticle.Provider::new);

            Minecraft.getInstance().particleEngine.register(DigitalParticles.BUBBLE_ATTACK.get(), UpParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.PEPPER_BREATH.get(), UpParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.THUNDER_ATTACK.get(), UpParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.STINGER.get(), UpParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.ENERGY_STAR.get(), UpParticle.Provider::new);
        }

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event){
            event.register(KeyBindings.NAVIGATING_KEY);
            event.register(KeyBindings.MOUSE_TRIGGER_KEY);
        }


        @Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, value = Dist.CLIENT)
        public static class ClientDistEvents {

            @SubscribeEvent
            public static void onKeyPress(InputEvent.Key event){
                if(KeyBindings.NAVIGATING_KEY.consumeClick()){
                    StatsGui.switchGui();
                }

                if(KeyBindings.MOUSE_TRIGGER_KEY.consumeClick()){

                    if (StatsGui.isShowing)StatsGui.switchFreeMouse();
                }

                GuiOverlayManager.getOverlays();
            }
        }
    }

}
