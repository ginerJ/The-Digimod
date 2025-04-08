package net.minecraft.network.protocol.game;

import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.RelativeMovement;

public class ClientboundPlayerPositionPacket implements Packet<ClientGamePacketListener> {
   private final double x;
   private final double y;
   private final double z;
   private final float yRot;
   private final float xRot;
   private final Set<RelativeMovement> relativeArguments;
   private final int id;

   public ClientboundPlayerPositionPacket(double p_275438_, double p_275354_, double p_275276_, float p_275280_, float p_275203_, Set<RelativeMovement> p_275228_, int p_275614_) {
      this.x = p_275438_;
      this.y = p_275354_;
      this.z = p_275276_;
      this.yRot = p_275280_;
      this.xRot = p_275203_;
      this.relativeArguments = p_275228_;
      this.id = p_275614_;
   }

   public ClientboundPlayerPositionPacket(FriendlyByteBuf p_179158_) {
      this.x = p_179158_.readDouble();
      this.y = p_179158_.readDouble();
      this.z = p_179158_.readDouble();
      this.yRot = p_179158_.readFloat();
      this.xRot = p_179158_.readFloat();
      this.relativeArguments = RelativeMovement.unpack(p_179158_.readUnsignedByte());
      this.id = p_179158_.readVarInt();
   }

   public void write(FriendlyByteBuf p_132820_) {
      p_132820_.writeDouble(this.x);
      p_132820_.writeDouble(this.y);
      p_132820_.writeDouble(this.z);
      p_132820_.writeFloat(this.yRot);
      p_132820_.writeFloat(this.xRot);
      p_132820_.writeByte(RelativeMovement.pack(this.relativeArguments));
      p_132820_.writeVarInt(this.id);
   }

   public void handle(ClientGamePacketListener p_132817_) {
      p_132817_.handleMovePlayer(this);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getYRot() {
      return this.yRot;
   }

   public float getXRot() {
      return this.xRot;
   }

   public int getId() {
      return this.id;
   }

   public Set<RelativeMovement> getRelativeArguments() {
      return this.relativeArguments;
   }
}