package net.minecraft.network.protocol.game;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public record ClientboundUpdateEnabledFeaturesPacket(Set<ResourceLocation> features) implements Packet<ClientGamePacketListener> {
   public ClientboundUpdateEnabledFeaturesPacket(FriendlyByteBuf p_250545_) {
      this(p_250545_.<ResourceLocation, Set<ResourceLocation>>readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation));
   }

   public void write(FriendlyByteBuf p_251972_) {
      p_251972_.writeCollection(this.features, FriendlyByteBuf::writeResourceLocation);
   }

   public void handle(ClientGamePacketListener p_250317_) {
      p_250317_.handleEnabledFeatures(this);
   }
}