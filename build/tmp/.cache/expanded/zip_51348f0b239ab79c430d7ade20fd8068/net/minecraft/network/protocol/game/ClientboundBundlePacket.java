package net.minecraft.network.protocol.game;

import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;

public class ClientboundBundlePacket extends BundlePacket<ClientGamePacketListener> {
   public ClientboundBundlePacket(Iterable<Packet<ClientGamePacketListener>> p_265231_) {
      super(p_265231_);
   }

   public void handle(ClientGamePacketListener p_265490_) {
      p_265490_.handleBundlePacket(this);
   }
}