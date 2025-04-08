package net.minecraft.network.protocol;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;

public abstract class BundlePacket<T extends PacketListener> implements Packet<T> {
   private final Iterable<Packet<T>> packets;

   protected BundlePacket(Iterable<Packet<T>> p_265290_) {
      this.packets = p_265290_;
   }

   public final Iterable<Packet<T>> subPackets() {
      return this.packets;
   }

   public final void write(FriendlyByteBuf p_265519_) {
   }
}