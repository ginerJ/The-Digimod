package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public record ClientboundSystemChatPacket(Component content, boolean overlay) implements Packet<ClientGamePacketListener> {
   public ClientboundSystemChatPacket(FriendlyByteBuf p_237852_) {
      this(p_237852_.readComponent(), p_237852_.readBoolean());
   }

   public void write(FriendlyByteBuf p_237860_) {
      p_237860_.writeComponent(this.content);
      p_237860_.writeBoolean(this.overlay);
   }

   public void handle(ClientGamePacketListener p_237864_) {
      p_237864_.handleSystemChat(this);
   }

   public boolean isSkippable() {
      return true;
   }
}