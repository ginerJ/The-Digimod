package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.Packet;

public record ClientboundDeleteChatPacket(MessageSignature.Packed messageSignature) implements Packet<ClientGamePacketListener> {
   public ClientboundDeleteChatPacket(FriendlyByteBuf p_241415_) {
      this(MessageSignature.Packed.read(p_241415_));
   }

   public void write(FriendlyByteBuf p_241358_) {
      MessageSignature.Packed.write(p_241358_, this.messageSignature);
   }

   public void handle(ClientGamePacketListener p_241426_) {
      p_241426_.handleDeleteChat(this);
   }
}