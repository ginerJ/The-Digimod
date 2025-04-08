package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ClientboundSelectAdvancementsTabPacket implements Packet<ClientGamePacketListener> {
   @Nullable
   private final ResourceLocation tab;

   public ClientboundSelectAdvancementsTabPacket(@Nullable ResourceLocation p_133006_) {
      this.tab = p_133006_;
   }

   public void handle(ClientGamePacketListener p_133012_) {
      p_133012_.handleSelectAdvancementsTab(this);
   }

   public ClientboundSelectAdvancementsTabPacket(FriendlyByteBuf p_179198_) {
      this.tab = p_179198_.readNullable(FriendlyByteBuf::readResourceLocation);
   }

   public void write(FriendlyByteBuf p_133015_) {
      p_133015_.writeNullable(this.tab, FriendlyByteBuf::writeResourceLocation);
   }

   @Nullable
   public ResourceLocation getTab() {
      return this.tab;
   }
}