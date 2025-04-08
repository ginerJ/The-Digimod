package net.minecraft.network.protocol.game;

import java.util.List;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundPlayerInfoRemovePacket(List<UUID> profileIds) implements Packet<ClientGamePacketListener> {
   public ClientboundPlayerInfoRemovePacket(FriendlyByteBuf p_248744_) {
      this(p_248744_.readList(FriendlyByteBuf::readUUID));
   }

   public void write(FriendlyByteBuf p_249263_) {
      p_249263_.writeCollection(this.profileIds, FriendlyByteBuf::writeUUID);
   }

   public void handle(ClientGamePacketListener p_250111_) {
      p_250111_.handlePlayerInfoRemove(this);
   }
}