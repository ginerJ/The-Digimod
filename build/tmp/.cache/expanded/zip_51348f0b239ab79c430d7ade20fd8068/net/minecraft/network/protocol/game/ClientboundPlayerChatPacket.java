package net.minecraft.network.protocol.game;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.protocol.Packet;

public record ClientboundPlayerChatPacket(UUID sender, int index, @Nullable MessageSignature signature, SignedMessageBody.Packed body, @Nullable Component unsignedContent, FilterMask filterMask, ChatType.BoundNetwork chatType) implements Packet<ClientGamePacketListener> {
   public ClientboundPlayerChatPacket(FriendlyByteBuf p_237741_) {
      this(p_237741_.readUUID(), p_237741_.readVarInt(), p_237741_.readNullable(MessageSignature::read), new SignedMessageBody.Packed(p_237741_), p_237741_.readNullable(FriendlyByteBuf::readComponent), FilterMask.read(p_237741_), new ChatType.BoundNetwork(p_237741_));
   }

   public void write(FriendlyByteBuf p_237755_) {
      p_237755_.writeUUID(this.sender);
      p_237755_.writeVarInt(this.index);
      p_237755_.writeNullable(this.signature, MessageSignature::write);
      this.body.write(p_237755_);
      p_237755_.writeNullable(this.unsignedContent, FriendlyByteBuf::writeComponent);
      FilterMask.write(p_237755_, this.filterMask);
      this.chatType.write(p_237755_);
   }

   public void handle(ClientGamePacketListener p_237759_) {
      p_237759_.handlePlayerChat(this);
   }

   public boolean isSkippable() {
      return true;
   }
}