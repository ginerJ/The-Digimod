package net.modderg.thedigimod.events;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.client.Minecraft;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.particles.DigitalParticles;
import net.modderg.thedigimod.particles.custom.BubbleParticle;
import net.modderg.thedigimod.particles.custom.DigitronParticles;
import net.modderg.thedigimod.particles.custom.StatUpParticles;

import java.util.function.Supplier;

public class ModEvents {
    @Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public class ModBusEvents {
        @SubscribeEvent
        public static void registerParticleFactories(final RegisterParticleProvidersEvent event){

            Minecraft.getInstance().particleEngine.register(DigitalParticles.DIGITRON_PARTICLES.get(), DigitronParticles.Provider::new);

            Minecraft.getInstance().particleEngine.register(DigitalParticles.MEAT_BUBBLE.get(), BubbleParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.MISTAKE_BUBBLE.get(), BubbleParticle.Provider::new);

            Minecraft.getInstance().particleEngine.register(DigitalParticles.ATTACK_UP.get(), StatUpParticles.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.DEFENCE_UP.get(), StatUpParticles.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.SPATTACK_UP.get(), StatUpParticles.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.SPDEFENCE_UP.get(), StatUpParticles.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.BATTLES_UP.get(), StatUpParticles.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.HEALTH_UP.get(), StatUpParticles.Provider::new);

            Minecraft.getInstance().particleEngine.register(DigitalParticles.EVO_PARTICLES.get(), StatUpParticles.Provider::new);
        }
    }
}
