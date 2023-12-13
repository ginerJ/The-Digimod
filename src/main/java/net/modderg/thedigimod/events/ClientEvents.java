package net.modderg.thedigimod.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.CustomDigimon;
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

            for (String s:DigitalEntities.digimonMap.keySet()){
                EntityType<CustomDigimon> digimon = (EntityType<CustomDigimon>) DigitalEntities.digimonMap.get(s).get();
                event.registerEntityRenderer(digimon, CustomDigimonRender::new);
            }

            event.registerEntityRenderer(DigitalProjectiles.PUNCHING_BAG.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalProjectiles.SP_TARGET.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalProjectiles.SP_TABLE.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalProjectiles.SHIELD_STAND.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalProjectiles.UPDATE_GOOD.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalProjectiles.DRAGON_BONE.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalProjectiles.BALL_GOOD.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalProjectiles.CLOWN_BOX.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalProjectiles.FLYTRAP_GOOD.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalProjectiles.OLD_PC_GOOD.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalProjectiles.LIRA_GOOD.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalProjectiles.RED_FREEZER.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalProjectiles.WIND_VANE.get(), AbstractGoodRender::new);
            event.registerEntityRenderer(DigitalProjectiles.TRAINING_ROCK.get(), AbstractGoodRender::new);

            event.registerEntityRenderer(DigitalProjectiles.BULLET.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.PEPPER_BREATH.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.MEGA_FLAME.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.MEGA_BLASTER.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.THUNDERBOLT.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.PETIT_THUNDER.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.DEADLY_STING.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.V_ARROW.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.HYPER_HEAT.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.POOP_THROW.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.METEOR_WING.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.GATLING_GUN.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.DISC_ATTACK.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.HEAVENS_KNUCKLE.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.HOLY_SHOOT.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.GLIDING_ROCKS.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.BEAST_SLASH.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.INK_GUN.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.SNOW_BULLET.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.OCEAN_STORM.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.TRON_FLAME.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.DEATH_CLAW.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.POISON_BREATH.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.SAND_BLAST.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.NIGHT_OF_FIRE.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.BEAR_PUNCH.get(), CustomProjectileRender::new);
            event.registerEntityRenderer(DigitalProjectiles.PETIT_TWISTER.get(), CustomProjectileRender::new);
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
            Minecraft.getInstance().particleEngine.register(DigitalParticles.PEPPER_BREATH.get(), BrightParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.BULLET_PARTICLE.get(), BrightParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.THUNDER_ATTACK.get(), BrightParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.STINGER.get(), UpParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.ENERGY_STAR.get(), BrightParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.RED_ENERGY_STAR.get(), BrightParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.POOP_PARTICLE.get(), UpParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.HOLY_CROSS.get(), BrightParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.ROCK_PARTICLE.get(), UpParticle.Provider::new);
        }

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event){
            event.register(KeyBindings.NAVIGATING_KEY);
            event.register(KeyBindings.MOUSE_TRIGGER_KEY);
        }


        @Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, value = Dist.CLIENT)
        public static class ClientDistEvents {

            @SubscribeEvent
            public static void registerParticleFactories(final RenderNameTagEvent event){
                if(event.getEntity() instanceof CustomDigimon cd){
                    event.setContent(Component.literal(
                            event.getContent().getString().replace(cd.getSpecies(),I18n.get("entity.thedigimod." + cd.getLowerCaseSpecies()))
                    ));
                }
            }

            @SubscribeEvent
            public static void  onPlayerTick(TickEvent.PlayerTickEvent event){
                if(event.phase == TickEvent.Phase.END){
                    Player entity = event.player;
                    if (entity.isShiftKeyDown()) entity.ejectPassengers();
                }
            }

            @SubscribeEvent
            public static void onKeyPress(InputEvent.Key event){
                if(KeyBindings.NAVIGATING_KEY.consumeClick()){
                    StatsGui.switchGui();
                }

                if(KeyBindings.MOUSE_TRIGGER_KEY.consumeClick()){

                    if (StatsGui.isShowing)StatsGui.switchFreeMouse();
                }
            }

            @SubscribeEvent
            public static void  screenNameCheckEvent(ScreenEvent event){
                if(event.getScreen().isPauseScreen() || event.getScreen() instanceof InventoryScreen){
                    if (StatsGui.freedMouse)StatsGui.switchFreeMouse();
                }
            }
        }
    }

}
