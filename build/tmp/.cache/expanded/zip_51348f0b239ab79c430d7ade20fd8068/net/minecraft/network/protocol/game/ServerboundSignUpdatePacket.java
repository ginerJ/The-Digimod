package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundSignUpdatePacket implements Packet<ServerGamePacketListener> {
   private static final int MAX_STRING_LENGTH = 384;
   private final BlockPos pos;
   private final String[] lines;
   private final boolean isFrontText;

   public ServerboundSignUpdatePacket(BlockPos p_277902_, boolean p_277750_, String p_278086_, String p_277504_, String p_277814_, String p_277726_) {
      this.pos = p_277902_;
      this.isFrontText = p_277750_;
      this.lines = new String[]{p_278086_, p_277504_, p_277814_, p_277726_};
   }

   public ServerboundSignUpdatePacket(FriendlyByteBuf p_179790_) {
      this.pos = p_179790_.readBlockPos();
      this.isFrontText = p_179790_.readBoolean();
      this.lines = new String[4];

      for(int i = 0; i < 4; ++i) {
         this.lines[i] = p_179790_.readUtf(384);
      }

   }

   public void write(FriendlyByteBuf p_134662_) {
      p_134662_.writeBlockPos(this.pos);
      p_134662_.writeBoolean(this.isFrontText);

      for(int i = 0; i < 4; ++i) {
         p_134662_.writeUtf(this.lines[i]);
      }

   }

   public void handle(ServerGamePacketListener p_134659_) {
      p_134659_.handleSignUpdate(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public boolean isFrontText() {
      return this.isFrontText;
   }

   public String[] getLines() {
      return this.lines;
   }
}