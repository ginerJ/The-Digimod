package net.modderg.thedigimod.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.HugeExplosionParticle;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.InitDigimons;
import net.modderg.thedigimod.goods.AbstractGoodRender;
import net.modderg.thedigimod.goods.AbstractTrainingGood;
import net.modderg.thedigimod.goods.InitGoods;
import net.modderg.thedigimod.gui.KeyBindings;
import net.modderg.thedigimod.gui.inventory.DigimonInventoryScreen;
import net.modderg.thedigimod.gui.inventory.InitMenu;
import net.modderg.thedigimod.particles.DigitalParticles;
import net.modderg.thedigimod.particles.custom.*;
import net.modderg.thedigimod.projectiles.ProjectileDefault;
import net.modderg.thedigimod.projectiles.CustomProjectileRender;
import net.modderg.thedigimod.projectiles.InitProjectiles;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientDistEventBusSubscriber {

        @SubscribeEvent
        public static void registerEntityRenders(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                MenuScreens.register(InitMenu.DIGIMON_CONTAINER.get(), DigimonInventoryScreen::new);

            });
        }

        @SubscribeEvent
        public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {

            for (String s: InitDigimons.digimonMap.keySet()){
                EntityType<CustomDigimon> digimon = (EntityType<CustomDigimon>) InitDigimons.digimonMap.get(s).get();
                event.registerEntityRenderer(digimon, CustomDigimonRender::new);
            }

            for (String s: InitGoods.goodMap.keySet()){
                EntityType<AbstractTrainingGood> digimon = (EntityType<AbstractTrainingGood>) InitGoods.goodMap.get(s).get();
                event.registerEntityRenderer(digimon, AbstractGoodRender::new);
            }

            for (String s: InitProjectiles.projectileMap.keySet()){
                EntityType<ProjectileDefault> projectile = (EntityType<ProjectileDefault>) InitProjectiles.projectileMap.get(s).get();
                event.registerEntityRenderer(projectile, CustomProjectileRender::new);
            }
        }

        @SubscribeEvent
        public static void registerParticleFactories(final RegisterParticleProvidersEvent event){

            Minecraft.getInstance().particleEngine.register(DigitalParticles.DIGITRON_PARTICLES.get(), RegularDigitalParticle.Provider::new);

            Minecraft.getInstance().particleEngine.register(DigitalParticles.MEAT_BUBBLE.get(), BubbleParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.MISTAKE_BUBBLE.get(), BubbleParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.SAD_BUBBLE.get(), BubbleParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.MEH_BUBBLE.get(), BubbleParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.HAPPY_BUBBLE.get(), BubbleParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.JOYFUL_BUBBLE.get(), BubbleParticle.Provider::new);

            Minecraft.getInstance().particleEngine.register(DigitalParticles.ATTACK_UP.get(), RegularDigitalParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.DEFENCE_UP.get(), RegularDigitalParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.SPATTACK_UP.get(), RegularDigitalParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.SPDEFENCE_UP.get(), RegularDigitalParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.BATTLES_UP.get(), RegularDigitalParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.HEALTH_UP.get(), RegularDigitalParticle.Provider::new);

            Minecraft.getInstance().particleEngine.register(DigitalParticles.LIFE_PARTICLE.get(), LifeParticle.Provider::new);

            Minecraft.getInstance().particleEngine.register(DigitalParticles.EVO_PARTICLES.get(), NegativeDigitalParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.EVO_PARTICLES_CHAMPION.get(), NegativeDigitalParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.EVO_PARTICLES_ULTIMATE.get(), NegativeDigitalParticle.Provider::new);

            Minecraft.getInstance().particleEngine.register(DigitalParticles.BUBBLE_ATTACK.get(), RegularDigitalParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.NOTE_PARTICLE.get(), RegularDigitalParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.PEPPER_BREATH.get(), BrightParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.BULLET_PARTICLE.get(), BrightParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.THUNDER_ATTACK.get(), BrightParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.STINGER.get(), RegularDigitalParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.ENERGY_STAR.get(), BrightParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.RED_ENERGY_STAR.get(), BrightParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.POOP_PARTICLE.get(), RegularDigitalParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.HOLY_CROSS.get(), BrightParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.ROCK_PARTICLE.get(), RegularDigitalParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.RED_EXPLOSION.get(), HugeExplosionParticle.Provider::new);
        }

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event){
            event.register(KeyBindings.NAVIGATING_KEY);
        }

        @Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, value = Dist.CLIENT)
        public static class ClientDistEvents {

            @SubscribeEvent
            public static void registerParticleFactories(final RenderNameTagEvent event){
                if(event.getEntity() instanceof CustomDigimon cd){
                    event.setContent(Component.literal(
                            event.getContent().getString().replace("Digimon",I18n.get("entity.thedigimod." + cd.getLowerCaseSpecies()))
                    ));
                }
            }
        }
    }

}
