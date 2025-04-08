package net.minecraft.network.protocol.status;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundStatusResponsePacket(ServerStatus status, @org.jetbrains.annotations.Nullable String cachedStatus) implements Packet<ClientStatusPacketListener> {
   public ClientboundStatusResponsePacket(ServerStatus status) {
      this(status, null);
   }

   public ClientboundStatusResponsePacket(FriendlyByteBuf p_179834_) {
      this(p_179834_.readJsonWithCodec(ServerStatus.CODEC));
   }

   public void write(FriendlyByteBuf p_134899_) {
      if (cachedStatus != null) p_134899_.writeUtf(cachedStatus);
      else
      p_134899_.writeJsonWithCodec(ServerStatus.CODEC, this.status);
   }

   public void handle(ClientStatusPacketListener p_134896_) {
      p_134896_.handleStatusResponse(this);
   }
}
