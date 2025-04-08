package net.minecraft.world.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;

public class Marker extends Entity {
   private static final String DATA_TAG = "data";
   private CompoundTag data = new CompoundTag();

   public Marker(EntityType<?> p_147250_, Level p_147251_) {
      super(p_147250_, p_147251_);
      this.noPhysics = true;
   }

   public void tick() {
   }

   protected void defineSynchedData() {
   }

   protected void readAdditionalSaveData(CompoundTag p_147254_) {
      this.data = p_147254_.getCompound("data");
   }

   protected void addAdditionalSaveData(CompoundTag p_147257_) {
      p_147257_.put("data", this.data.copy());
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      throw new IllegalStateException("Markers should never be sent");
   }

   protected boolean canAddPassenger(Entity p_265289_) {
      return false;
   }

   protected boolean couldAcceptPassenger() {
      return false;
   }

   protected void addPassenger(Entity p_270306_) {
      throw new IllegalStateException("Should never addPassenger without checking couldAcceptPassenger()");
   }

   public PushReaction getPistonPushReaction() {
      return PushReaction.IGNORE;
   }

   public boolean isIgnoringBlockTriggers() {
      return true;
   }
}