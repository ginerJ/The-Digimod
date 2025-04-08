package net.minecraft.network.protocol.game;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public record ClientboundLoginPacket(int playerId, boolean hardcore, GameType gameType, @Nullable GameType previousGameType, Set<ResourceKey<Level>> levels, RegistryAccess.Frozen registryHolder, ResourceKey<DimensionType> dimensionType, ResourceKey<Level> dimension, long seed, int maxPlayers, int chunkRadius, int simulationDistance, boolean reducedDebugInfo, boolean showDeathScreen, boolean isDebug, boolean isFlat, Optional<GlobalPos> lastDeathLocation, int portalCooldown) implements Packet<ClientGamePacketListener> {
   private static final RegistryOps<Tag> BUILTIN_CONTEXT_OPS = RegistryOps.create(NbtOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));

   public ClientboundLoginPacket(FriendlyByteBuf p_178960_) {
      this(p_178960_.readInt(), p_178960_.readBoolean(), GameType.byId(p_178960_.readByte()), GameType.byNullableId(p_178960_.readByte()), p_178960_.readCollection(Sets::newHashSetWithExpectedSize, (p_258210_) -> {
         return p_258210_.readResourceKey(Registries.DIMENSION);
      }), p_178960_.readWithCodec(BUILTIN_CONTEXT_OPS, RegistrySynchronization.NETWORK_CODEC).freeze(), p_178960_.readResourceKey(Registries.DIMENSION_TYPE), p_178960_.readResourceKey(Registries.DIMENSION), p_178960_.readLong(), p_178960_.readVarInt(), p_178960_.readVarInt(), p_178960_.readVarInt(), p_178960_.readBoolean(), p_178960_.readBoolean(), p_178960_.readBoolean(), p_178960_.readBoolean(), p_178960_.readOptional(FriendlyByteBuf::readGlobalPos), p_178960_.readVarInt());
   }

   public void write(FriendlyByteBuf p_132400_) {
      p_132400_.writeInt(this.playerId);
      p_132400_.writeBoolean(this.hardcore);
      p_132400_.writeByte(this.gameType.getId());
      p_132400_.writeByte(GameType.getNullableId(this.previousGameType));
      p_132400_.writeCollection(this.levels, FriendlyByteBuf::writeResourceKey);
      p_132400_.writeWithCodec(BUILTIN_CONTEXT_OPS, RegistrySynchronization.NETWORK_CODEC, this.registryHolder);
      p_132400_.writeResourceKey(this.dimensionType);
      p_132400_.writeResourceKey(this.dimension);
      p_132400_.writeLong(this.seed);
      p_132400_.writeVarInt(this.maxPlayers);
      p_132400_.writeVarInt(this.chunkRadius);
      p_132400_.writeVarInt(this.simulationDistance);
      p_132400_.writeBoolean(this.reducedDebugInfo);
      p_132400_.writeBoolean(this.showDeathScreen);
      p_132400_.writeBoolean(this.isDebug);
      p_132400_.writeBoolean(this.isFlat);
      p_132400_.writeOptional(this.lastDeathLocation, FriendlyByteBuf::writeGlobalPos);
      p_132400_.writeVarInt(this.portalCooldown);
   }

   public void handle(ClientGamePacketListener p_132397_) {
      p_132397_.handleLogin(this);
   }
}