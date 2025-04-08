package net.minecraft.network.protocol.login;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundCustomQueryPacket implements Packet<ServerLoginPacketListener>, net.minecraftforge.network.ICustomPacket<ServerboundCustomQueryPacket>
{
   private static final int MAX_PAYLOAD_SIZE = 1048576;
   private final int transactionId;
   @Nullable
   private final FriendlyByteBuf data;

   public ServerboundCustomQueryPacket(int p_134829_, @Nullable FriendlyByteBuf p_134830_) {
      this.transactionId = p_134829_;
      this.data = p_134830_;
   }

   public ServerboundCustomQueryPacket(FriendlyByteBuf p_179823_) {
      this.transactionId = p_179823_.readVarInt();
      this.data = p_179823_.readNullable((p_238039_) -> {
         int i = p_238039_.readableBytes();
         if (i >= 0 && i <= 1048576) {
            return new FriendlyByteBuf(p_238039_.readBytes(i));
         } else {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
         }
      });
   }

   public void write(FriendlyByteBuf p_134838_) {
      p_134838_.writeVarInt(this.transactionId);
      p_134838_.writeNullable(this.data, (p_238036_, p_238037_) -> {
         p_238036_.writeBytes(p_238037_.slice());
      });
   }

   public void handle(ServerLoginPacketListener p_134836_) {
      p_134836_.handleCustomQueryPacket(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   @Nullable
   public FriendlyByteBuf getData() {
      return this.data;
   }

   @Override public int getIndex() { return getTransactionId(); }
   @Override public net.minecraft.resources.ResourceLocation getName() { return net.minecraftforge.network.LoginWrapper.WRAPPER; }
   @org.jetbrains.annotations.Nullable @Override public FriendlyByteBuf getInternalData() { return getData(); }
}
