package net.minecraft.network.protocol.game;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.SynchedEntityData;

public record ClientboundSetEntityDataPacket(int id, List<SynchedEntityData.DataValue<?>> packedItems) implements Packet<ClientGamePacketListener> {
   public static final int EOF_MARKER = 255;

   public ClientboundSetEntityDataPacket(FriendlyByteBuf p_179290_) {
      this(p_179290_.readVarInt(), unpack(p_179290_));
   }

   private static void pack(List<SynchedEntityData.DataValue<?>> p_253940_, FriendlyByteBuf p_253901_) {
      for(SynchedEntityData.DataValue<?> datavalue : p_253940_) {
         datavalue.write(p_253901_);
      }

      p_253901_.writeByte(255);
   }

   private static List<SynchedEntityData.DataValue<?>> unpack(FriendlyByteBuf p_253726_) {
      List<SynchedEntityData.DataValue<?>> list = new ArrayList<>();

      int i;
      while((i = p_253726_.readUnsignedByte()) != 255) {
         list.add(SynchedEntityData.DataValue.read(p_253726_, i));
      }

      return list;
   }

   public void write(FriendlyByteBuf p_133158_) {
      p_133158_.writeVarInt(this.id);
      pack(this.packedItems, p_133158_);
   }

   public void handle(ClientGamePacketListener p_133155_) {
      p_133155_.handleSetEntityData(this);
   }
}