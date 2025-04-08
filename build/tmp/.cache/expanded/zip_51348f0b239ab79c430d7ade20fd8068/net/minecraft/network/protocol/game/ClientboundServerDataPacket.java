package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundServerDataPacket implements Packet<ClientGamePacketListener> {
   private final Component motd;
   private final Optional<byte[]> iconBytes;
   private final boolean enforcesSecureChat;

   public ClientboundServerDataPacket(Component p_272598_, Optional<byte[]> p_273716_, boolean p_273165_) {
      this.motd = p_272598_;
      this.iconBytes = p_273716_;
      this.enforcesSecureChat = p_273165_;
   }

   public ClientboundServerDataPacket(FriendlyByteBuf p_237799_) {
      this.motd = p_237799_.readComponent();
      this.iconBytes = p_237799_.readOptional(FriendlyByteBuf::readByteArray);
      this.enforcesSecureChat = p_237799_.readBoolean();
   }

   public void write(FriendlyByteBuf p_237805_) {
      p_237805_.writeComponent(this.motd);
      p_237805_.writeOptional(this.iconBytes, FriendlyByteBuf::writeByteArray);
      p_237805_.writeBoolean(this.enforcesSecureChat);
   }

   public void handle(ClientGamePacketListener p_237809_) {
      p_237809_.handleServerData(this);
   }

   public Component getMotd() {
      return this.motd;
   }

   public Optional<byte[]> getIconBytes() {
      return this.iconBytes;
   }

   public boolean enforcesSecureChat() {
      return this.enforcesSecureChat;
   }
}