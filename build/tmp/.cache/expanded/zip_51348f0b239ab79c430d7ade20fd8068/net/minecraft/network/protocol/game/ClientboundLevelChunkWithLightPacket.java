package net.minecraft.network.protocol.game;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLevelChunkWithLightPacket implements Packet<ClientGamePacketListener> {
   private final int x;
   private final int z;
   private final ClientboundLevelChunkPacketData chunkData;
   private final ClientboundLightUpdatePacketData lightData;

   public ClientboundLevelChunkWithLightPacket(LevelChunk p_285290_, LevelLightEngine p_285254_, @Nullable BitSet p_285350_, @Nullable BitSet p_285304_) {
      ChunkPos chunkpos = p_285290_.getPos();
      this.x = chunkpos.x;
      this.z = chunkpos.z;
      this.chunkData = new ClientboundLevelChunkPacketData(p_285290_);
      this.lightData = new ClientboundLightUpdatePacketData(chunkpos, p_285254_, p_285350_, p_285304_);
   }

   public ClientboundLevelChunkWithLightPacket(FriendlyByteBuf p_195710_) {
      this.x = p_195710_.readInt();
      this.z = p_195710_.readInt();
      this.chunkData = new ClientboundLevelChunkPacketData(p_195710_, this.x, this.z);
      this.lightData = new ClientboundLightUpdatePacketData(p_195710_, this.x, this.z);
   }

   public void write(FriendlyByteBuf p_195712_) {
      p_195712_.writeInt(this.x);
      p_195712_.writeInt(this.z);
      this.chunkData.write(p_195712_);
      this.lightData.write(p_195712_);
   }

   public void handle(ClientGamePacketListener p_195716_) {
      p_195716_.handleLevelChunkWithLight(this);
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public ClientboundLevelChunkPacketData getChunkData() {
      return this.chunkData;
   }

   public ClientboundLightUpdatePacketData getLightData() {
      return this.lightData;
   }
}