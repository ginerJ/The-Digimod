package net.minecraft.network.protocol.game;

import java.time.Instant;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.protocol.Packet;

public record ServerboundChatCommandPacket(String command, Instant timeStamp, long salt, ArgumentSignatures argumentSignatures, LastSeenMessages.Update lastSeenMessages) implements Packet<ServerGamePacketListener> {
   public ServerboundChatCommandPacket(FriendlyByteBuf p_237932_) {
      this(p_237932_.readUtf(256), p_237932_.readInstant(), p_237932_.readLong(), new ArgumentSignatures(p_237932_), new LastSeenMessages.Update(p_237932_));
   }

   public void write(FriendlyByteBuf p_237936_) {
      p_237936_.writeUtf(this.command, 256);
      p_237936_.writeInstant(this.timeStamp);
      p_237936_.writeLong(this.salt);
      this.argumentSignatures.write(p_237936_);
      this.lastSeenMessages.write(p_237936_);
   }

   public void handle(ServerGamePacketListener p_237940_) {
      p_237940_.handleChatCommand(this);
   }
}