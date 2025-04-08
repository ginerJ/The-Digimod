package net.minecraft.network.protocol.game;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class ClientboundRespawnPacket implements Packet<ClientGamePacketListener> {
   public static final byte KEEP_ATTRIBUTES = 1;
   public static final byte KEEP_ENTITY_DATA = 2;
   public static final byte KEEP_ALL_DATA = 3;
   private final ResourceKey<DimensionType> dimensionType;
   private final ResourceKey<Level> dimension;
   private final long seed;
   private final GameType playerGameType;
   @Nullable
   private final GameType previousPlayerGameType;
   private final boolean isDebug;
   private final boolean isFlat;
   private final byte dataToKeep;
   private final Optional<GlobalPos> lastDeathLocation;
   private final int portalCooldown;

   public ClientboundRespawnPacket(ResourceKey<DimensionType> p_287723_, ResourceKey<Level> p_287745_, long p_287746_, GameType p_287624_, @Nullable GameType p_287780_, boolean p_287655_, boolean p_287735_, byte p_287694_, Optional<GlobalPos> p_287615_, int p_287636_) {
      this.dimensionType = p_287723_;
      this.dimension = p_287745_;
      this.seed = p_287746_;
      this.playerGameType = p_287624_;
      this.previousPlayerGameType = p_287780_;
      this.isDebug = p_287655_;
      this.isFlat = p_287735_;
      this.dataToKeep = p_287694_;
      this.lastDeathLocation = p_287615_;
      this.portalCooldown = p_287636_;
   }

   public ClientboundRespawnPacket(FriendlyByteBuf p_179191_) {
      this.dimensionType = p_179191_.readResourceKey(Registries.DIMENSION_TYPE);
      this.dimension = p_179191_.readResourceKey(Registries.DIMENSION);
      this.seed = p_179191_.readLong();
      this.playerGameType = GameType.byId(p_179191_.readUnsignedByte());
      this.previousPlayerGameType = GameType.byNullableId(p_179191_.readByte());
      this.isDebug = p_179191_.readBoolean();
      this.isFlat = p_179191_.readBoolean();
      this.dataToKeep = p_179191_.readByte();
      this.lastDeathLocation = p_179191_.readOptional(FriendlyByteBuf::readGlobalPos);
      this.portalCooldown = p_179191_.readVarInt();
   }

   public void write(FriendlyByteBuf p_132954_) {
      p_132954_.writeResourceKey(this.dimensionType);
      p_132954_.writeResourceKey(this.dimension);
      p_132954_.writeLong(this.seed);
      p_132954_.writeByte(this.playerGameType.getId());
      p_132954_.writeByte(GameType.getNullableId(this.previousPlayerGameType));
      p_132954_.writeBoolean(this.isDebug);
      p_132954_.writeBoolean(this.isFlat);
      p_132954_.writeByte(this.dataToKeep);
      p_132954_.writeOptional(this.lastDeathLocation, FriendlyByteBuf::writeGlobalPos);
      p_132954_.writeVarInt(this.portalCooldown);
   }

   public void handle(ClientGamePacketListener p_132951_) {
      p_132951_.handleRespawn(this);
   }

   public ResourceKey<DimensionType> getDimensionType() {
      return this.dimensionType;
   }

   public ResourceKey<Level> getDimension() {
      return this.dimension;
   }

   public long getSeed() {
      return this.seed;
   }

   public GameType getPlayerGameType() {
      return this.playerGameType;
   }

   @Nullable
   public GameType getPreviousPlayerGameType() {
      return this.previousPlayerGameType;
   }

   public boolean isDebug() {
      return this.isDebug;
   }

   public boolean isFlat() {
      return this.isFlat;
   }

   public boolean shouldKeep(byte p_263573_) {
      return (this.dataToKeep & p_263573_) != 0;
   }

   public Optional<GlobalPos> getLastDeathLocation() {
      return this.lastDeathLocation;
   }

   public int getPortalCooldown() {
      return this.portalCooldown;
   }
}