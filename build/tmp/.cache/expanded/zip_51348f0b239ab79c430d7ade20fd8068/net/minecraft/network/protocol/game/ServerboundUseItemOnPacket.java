package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;

public class ServerboundUseItemOnPacket implements Packet<ServerGamePacketListener> {
   private final BlockHitResult blockHit;
   private final InteractionHand hand;
   private final int sequence;

   public ServerboundUseItemOnPacket(InteractionHand p_238005_, BlockHitResult p_238006_, int p_238007_) {
      this.hand = p_238005_;
      this.blockHit = p_238006_;
      this.sequence = p_238007_;
   }

   public ServerboundUseItemOnPacket(FriendlyByteBuf p_179796_) {
      this.hand = p_179796_.readEnum(InteractionHand.class);
      this.blockHit = p_179796_.readBlockHitResult();
      this.sequence = p_179796_.readVarInt();
   }

   public void write(FriendlyByteBuf p_134705_) {
      p_134705_.writeEnum(this.hand);
      p_134705_.writeBlockHitResult(this.blockHit);
      p_134705_.writeVarInt(this.sequence);
   }

   public void handle(ServerGamePacketListener p_134702_) {
      p_134702_.handleUseItemOn(this);
   }

   public InteractionHand getHand() {
      return this.hand;
   }

   public BlockHitResult getHitResult() {
      return this.blockHit;
   }

   public int getSequence() {
      return this.sequence;
   }
}