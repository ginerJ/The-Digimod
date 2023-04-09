package net.modderg.thedigimod.events;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
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
import net.modderg.thedigimod.block.DigiBlocks;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.goods.CustomTrainingGood;
import net.modderg.thedigimod.entity.goods.PunchingBag;
import net.modderg.thedigimod.entity.goods.PunchingBagRender;
import net.modderg.thedigimod.particles.DigitalParticles;
import net.modderg.thedigimod.particles.custom.DigitronParticles;
import net.modderg.thedigimod.particles.custom.StatUpParticles;

import java.util.function.Supplier;

public class ModEvents {
    @Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public class ModBusEvents {
        @SubscribeEvent
        public static void registerParticleFactories(final RegisterParticleProvidersEvent event){

            Minecraft.getInstance().particleEngine.register(DigitalParticles.DIGITRON_PARTICLES.get(), DigitronParticles.Provider::new);

            Minecraft.getInstance().particleEngine.register(DigitalParticles.ATTACK_UP.get(), StatUpParticles.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.DEFENCE_UP.get(), StatUpParticles.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.SPATTACK_UP.get(), StatUpParticles.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.SPDEFENCE_UP.get(), StatUpParticles.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.BATTLES_UP.get(), StatUpParticles.Provider::new);
            Minecraft.getInstance().particleEngine.register(DigitalParticles.HEALTH_UP.get(), StatUpParticles.Provider::new);

            Minecraft.getInstance().particleEngine.register(DigitalParticles.EVO_PARTICLES.get(), StatUpParticles.Provider::new);
        }
        @SubscribeEvent
        public static void onClientSetup(FMLCommonSetupEvent event) {
            ItemBlockRenderTypes.setRenderLayer(DigiBlocks.MEAT_CROP.get(), RenderType.cutout());
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public class ModEventBusSubscriber {
    @SubscribeEvent
    public static void onSetUp(FMLCommonSetupEvent event) {
        TheDigiMod.addNetworkMessage(PlayerVariablesSyncMessage.class, PlayerVariablesSyncMessage::buffer, PlayerVariablesSyncMessage::new, PlayerVariablesSyncMessage::handler);
    }

    @SubscribeEvent
    public static void init(RegisterCapabilitiesEvent event) {
        event.register(PlayerVariables.class);
    }

    @Mod.EventBusSubscriber
    public static class EventBusVariableHandlers {
        @SubscribeEvent
        public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
            if (!event.getEntity().level.isClientSide())
                ((PlayerVariables) event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables())).syncPlayerVariables(event.getEntity());
        }

        @SubscribeEvent
        public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
            if (!event.getEntity().level.isClientSide())
                ((PlayerVariables) event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables())).syncPlayerVariables(event.getEntity());
        }

        @SubscribeEvent
        public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (!event.getEntity().level.isClientSide())
                ((PlayerVariables) event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables())).syncPlayerVariables(event.getEntity());
        }

        @SubscribeEvent
        public static void clonePlayer(PlayerEvent.Clone event) {
            event.getOriginal().revive();
            PlayerVariables original = ((PlayerVariables) event.getOriginal().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables()));
            PlayerVariables clone = ((PlayerVariables) event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables()));
            clone.FirstJoin = original.FirstJoin;
            if (!event.isWasDeath()) {
            }
        }
    }

    public static final Capability<PlayerVariables> PLAYER_VARIABLES_CAPABILITY = CapabilityManager.get(new CapabilityToken<PlayerVariables>() {
    });

    @Mod.EventBusSubscriber
    private static class PlayerVariablesProvider implements ICapabilitySerializable<Tag> {
        @SubscribeEvent
        public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player && !(event.getObject() instanceof FakePlayer))
                event.addCapability(new ResourceLocation("digimod", "player_variables"), new PlayerVariablesProvider());
        }

        private final PlayerVariables playerVariables = new PlayerVariables();
        private final LazyOptional<PlayerVariables> instance = LazyOptional.of(() -> playerVariables);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            return cap == PLAYER_VARIABLES_CAPABILITY ? instance.cast() : LazyOptional.empty();
        }

        @Override
        public Tag serializeNBT() {
            return playerVariables.writeNBT();
        }

        @Override
        public void deserializeNBT(Tag nbt) {
            playerVariables.readNBT(nbt);
        }
    }

        public static class PlayerVariables {
            public boolean FirstJoin = false;

            public void syncPlayerVariables(Entity entity) {
                if (entity instanceof ServerPlayer serverPlayer)
                    TheDigiMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PlayerVariablesSyncMessage(this));
            }

            public Tag writeNBT() {
                CompoundTag nbt = new CompoundTag();
                nbt.putBoolean("FirstJoin", FirstJoin);
                return nbt;
            }

            public void readNBT(Tag Tag) {
                CompoundTag nbt = (CompoundTag) Tag;
                FirstJoin = nbt.getBoolean("FirstJoin");
            }
        }

        public static class PlayerVariablesSyncMessage {
            public PlayerVariables data;

            public PlayerVariablesSyncMessage(FriendlyByteBuf buffer) {
                this.data = new PlayerVariables();
                this.data.readNBT(buffer.readNbt());
            }

            public PlayerVariablesSyncMessage(PlayerVariables data) {
                this.data = data;
            }

            public static void buffer(PlayerVariablesSyncMessage message, FriendlyByteBuf buffer) {
                buffer.writeNbt((CompoundTag) message.data.writeNBT());
            }

            public static void handler(PlayerVariablesSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
                NetworkEvent.Context context = contextSupplier.get();
                context.enqueueWork(() -> {
                    if (!context.getDirection().getReceptionSide().isServer()) {
                        PlayerVariables variables = ((PlayerVariables) Minecraft.getInstance().player.getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables()));
                        variables.FirstJoin = message.data.FirstJoin;
                    }
                });
                context.setPacketHandled(true);
            }
        }
    }
}
