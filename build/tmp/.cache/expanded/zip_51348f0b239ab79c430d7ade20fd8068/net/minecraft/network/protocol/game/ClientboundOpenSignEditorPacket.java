package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundOpenSignEditorPacket implements Packet<ClientGamePacketListener> {
   private final BlockPos pos;
   private final boolean isFrontText;

   public ClientboundOpenSignEditorPacket(BlockPos p_277843_, boolean p_277748_) {
      this.pos = p_277843_;
      this.isFrontText = p_277748_;
   }

   public ClientboundOpenSignEditorPacket(FriendlyByteBuf p_179013_) {
      this.pos = p_179013_.readBlockPos();
      this.isFrontText = p_179013_.readBoolean();
   }

   public void write(FriendlyByteBuf p_132642_) {
      p_132642_.writeBlockPos(this.pos);
      p_132642_.writeBoolean(this.isFrontText);
   }

   public void handle(ClientGamePacketListener p_132639_) {
      p_132639_.handleOpenSignEditor(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public boolean isFrontText() {
      return this.isFrontText;
   }
}