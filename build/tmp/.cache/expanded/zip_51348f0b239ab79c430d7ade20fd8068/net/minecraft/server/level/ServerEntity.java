package net.minecraft.server.level;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ServerEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int TOLERANCE_LEVEL_ROTATION = 1;
   private final ServerLevel level;
   private final Entity entity;
   private final int updateInterval;
   private final boolean trackDelta;
   private final Consumer<Packet<?>> broadcast;
   private final VecDeltaCodec positionCodec = new VecDeltaCodec();
   private int yRotp;
   private int xRotp;
   private int yHeadRotp;
   private Vec3 ap = Vec3.ZERO;
   private int tickCount;
   private int teleportDelay;
   private List<Entity> lastPassengers = Collections.emptyList();
   private boolean wasRiding;
   private boolean wasOnGround;
   @Nullable
   private List<SynchedEntityData.DataValue<?>> trackedDataValues;

   public ServerEntity(ServerLevel p_8528_, Entity p_8529_, int p_8530_, boolean p_8531_, Consumer<Packet<?>> p_8532_) {
      this.level = p_8528_;
      this.broadcast = p_8532_;
      this.entity = p_8529_;
      this.updateInterval = p_8530_;
      this.trackDelta = p_8531_;
      this.positionCodec.setBase(p_8529_.trackingPosition());
      this.yRotp = Mth.floor(p_8529_.getYRot() * 256.0F / 360.0F);
      this.xRotp = Mth.floor(p_8529_.getXRot() * 256.0F / 360.0F);
      this.yHeadRotp = Mth.floor(p_8529_.getYHeadRot() * 256.0F / 360.0F);
      this.wasOnGround = p_8529_.onGround();
      this.trackedDataValues = p_8529_.getEntityData().getNonDefaultValues();
   }

   public void sendChanges() {
      List<Entity> list = this.entity.getPassengers();
      if (!list.equals(this.lastPassengers)) {
         this.broadcast.accept(new ClientboundSetPassengersPacket(this.entity));
         removedPassengers(list, this.lastPassengers).forEach((p_289307_) -> {
            if (p_289307_ instanceof ServerPlayer serverplayer1) {
               serverplayer1.connection.teleport(serverplayer1.getX(), serverplayer1.getY(), serverplayer1.getZ(), serverplayer1.getYRot(), serverplayer1.getXRot());
            }

         });
         this.lastPassengers = list;
      }

      Entity $$11 = this.entity;
      if ($$11 instanceof ItemFrame itemframe) {
         if (this.tickCount % 10 == 0) {
            ItemStack itemstack = itemframe.getItem();
         Integer integer = MapItem.getMapId(itemstack);
         MapItemSavedData mapitemsaveddata = MapItem.getSavedData(itemstack, this.level);
         if (mapitemsaveddata != null) {
                  for(ServerPlayer serverplayer : this.level.players()) {
                     mapitemsaveddata.tickCarriedBy(serverplayer, itemstack);
                     Packet<?> packet = mapitemsaveddata.getUpdatePacket(integer, serverplayer);
                     if (packet != null) {
                        serverplayer.connection.send(packet);
                     }
                  }
               }

            this.sendDirtyEntityData();
         }
      }



      if (this.tickCount % this.updateInterval == 0 || this.entity.hasImpulse || this.entity.getEntityData().isDirty()) {
         if (this.entity.isPassenger()) {
            int i1 = Mth.floor(this.entity.getYRot() * 256.0F / 360.0F);
            int l1 = Mth.floor(this.entity.getXRot() * 256.0F / 360.0F);
            boolean flag3 = Math.abs(i1 - this.yRotp) >= 1 || Math.abs(l1 - this.xRotp) >= 1;
            if (flag3) {
               this.broadcast.accept(new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)i1, (byte)l1, this.entity.onGround()));
               this.yRotp = i1;
               this.xRotp = l1;
            }

            this.positionCodec.setBase(this.entity.trackingPosition());
            this.sendDirtyEntityData();
            this.wasRiding = true;
         } else {
            ++this.teleportDelay;
            int l = Mth.floor(this.entity.getYRot() * 256.0F / 360.0F);
            int k1 = Mth.floor(this.entity.getXRot() * 256.0F / 360.0F);
            Vec3 vec3 = this.entity.trackingPosition();
            boolean flag4 = this.positionCodec.delta(vec3).lengthSqr() >= (double)7.6293945E-6F;
            Packet<?> packet1 = null;
            boolean flag5 = flag4 || this.tickCount % 60 == 0;
            boolean flag6 = Math.abs(l - this.yRotp) >= 1 || Math.abs(k1 - this.xRotp) >= 1;
            boolean flag = false;
            boolean flag1 = false;
            if (this.tickCount > 0 || this.entity instanceof AbstractArrow) {
               long i = this.positionCodec.encodeX(vec3);
               long j = this.positionCodec.encodeY(vec3);
               long k = this.positionCodec.encodeZ(vec3);
               boolean flag2 = i < -32768L || i > 32767L || j < -32768L || j > 32767L || k < -32768L || k > 32767L;
               if (!flag2 && this.teleportDelay <= 400 && !this.wasRiding && this.wasOnGround == this.entity.onGround()) {
                  if ((!flag5 || !flag6) && !(this.entity instanceof AbstractArrow)) {
                     if (flag5) {
                        packet1 = new ClientboundMoveEntityPacket.Pos(this.entity.getId(), (short)((int)i), (short)((int)j), (short)((int)k), this.entity.onGround());
                        flag = true;
                     } else if (flag6) {
                        packet1 = new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)l, (byte)k1, this.entity.onGround());
                        flag1 = true;
                     }
                  } else {
                     packet1 = new ClientboundMoveEntityPacket.PosRot(this.entity.getId(), (short)((int)i), (short)((int)j), (short)((int)k), (byte)l, (byte)k1, this.entity.onGround());
                     flag = true;
                     flag1 = true;
                  }
               } else {
                  this.wasOnGround = this.entity.onGround();
                  this.teleportDelay = 0;
                  packet1 = new ClientboundTeleportEntityPacket(this.entity);
                  flag = true;
                  flag1 = true;
               }
            }

            if ((this.trackDelta || this.entity.hasImpulse || this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying()) && this.tickCount > 0) {
               Vec3 vec31 = this.entity.getDeltaMovement();
               double d0 = vec31.distanceToSqr(this.ap);
               if (d0 > 1.0E-7D || d0 > 0.0D && vec31.lengthSqr() == 0.0D) {
                  this.ap = vec31;
                  this.broadcast.accept(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap));
               }
            }

            if (packet1 != null) {
               this.broadcast.accept(packet1);
            }

            this.sendDirtyEntityData();
            if (flag) {
               this.positionCodec.setBase(vec3);
            }

            if (flag1) {
               this.yRotp = l;
               this.xRotp = k1;
            }

            this.wasRiding = false;
         }

         int j1 = Mth.floor(this.entity.getYHeadRot() * 256.0F / 360.0F);
         if (Math.abs(j1 - this.yHeadRotp) >= 1) {
            this.broadcast.accept(new ClientboundRotateHeadPacket(this.entity, (byte)j1));
            this.yHeadRotp = j1;
         }

         this.entity.hasImpulse = false;
      }

      ++this.tickCount;
      if (this.entity.hurtMarked) {
         this.broadcastAndSend(new ClientboundSetEntityMotionPacket(this.entity));
         this.entity.hurtMarked = false;
      }

   }

   private static Stream<Entity> removedPassengers(List<Entity> p_277592_, List<Entity> p_277658_) {
      return p_277658_.stream().filter((p_275361_) -> {
         return !p_277592_.contains(p_275361_);
      });
   }

   public void removePairing(ServerPlayer p_8535_) {
      this.entity.stopSeenByPlayer(p_8535_);
      p_8535_.connection.send(new ClientboundRemoveEntitiesPacket(this.entity.getId()));
      net.minecraftforge.event.ForgeEventFactory.onStopEntityTracking(this.entity, p_8535_);
   }

   public void addPairing(ServerPlayer p_8542_) {
      List<Packet<ClientGamePacketListener>> list = new ArrayList<>();
      this.sendPairingData(p_8542_, list::add);
      p_8542_.connection.send(new ClientboundBundlePacket(list));
      this.entity.startSeenByPlayer(p_8542_);
      net.minecraftforge.event.ForgeEventFactory.onStartEntityTracking(this.entity, p_8542_);
   }

   public void sendPairingData(ServerPlayer p_289562_, Consumer<Packet<ClientGamePacketListener>> p_289563_) {
      if (this.entity.isRemoved()) {
         LOGGER.warn("Fetching packet for removed entity {}", (Object)this.entity);
      }

      Packet<ClientGamePacketListener> packet = this.entity.getAddEntityPacket();
      this.yHeadRotp = Mth.floor(this.entity.getYHeadRot() * 256.0F / 360.0F);
      p_289563_.accept(packet);
      if (this.trackedDataValues != null) {
         p_289563_.accept(new ClientboundSetEntityDataPacket(this.entity.getId(), this.trackedDataValues));
      }

      boolean flag = this.trackDelta;
      if (this.entity instanceof LivingEntity) {
         Collection<AttributeInstance> collection = ((LivingEntity)this.entity).getAttributes().getSyncableAttributes();
         if (!collection.isEmpty()) {
            p_289563_.accept(new ClientboundUpdateAttributesPacket(this.entity.getId(), collection));
         }

         if (((LivingEntity)this.entity).isFallFlying()) {
            flag = true;
         }
      }

      this.ap = this.entity.getDeltaMovement();
      if (flag && !(this.entity instanceof LivingEntity)) {
         p_289563_.accept(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap));
      }

      if (this.entity instanceof LivingEntity) {
         List<Pair<EquipmentSlot, ItemStack>> list = Lists.newArrayList();

         for(EquipmentSlot equipmentslot : EquipmentSlot.values()) {
            ItemStack itemstack = ((LivingEntity)this.entity).getItemBySlot(equipmentslot);
            if (!itemstack.isEmpty()) {
               list.add(Pair.of(equipmentslot, itemstack.copy()));
            }
         }

         if (!list.isEmpty()) {
            p_289563_.accept(new ClientboundSetEquipmentPacket(this.entity.getId(), list));
         }
      }

      if (!this.entity.getPassengers().isEmpty()) {
         p_289563_.accept(new ClientboundSetPassengersPacket(this.entity));
      }

      if (this.entity.isPassenger()) {
         p_289563_.accept(new ClientboundSetPassengersPacket(this.entity.getVehicle()));
      }

      if (this.entity instanceof Mob) {
         Mob mob = (Mob)this.entity;
         if (mob.isLeashed()) {
            p_289563_.accept(new ClientboundSetEntityLinkPacket(mob, mob.getLeashHolder()));
         }
      }

   }

   private void sendDirtyEntityData() {
      SynchedEntityData synchedentitydata = this.entity.getEntityData();
      List<SynchedEntityData.DataValue<?>> list = synchedentitydata.packDirty();
      if (list != null) {
         this.trackedDataValues = synchedentitydata.getNonDefaultValues();
         this.broadcastAndSend(new ClientboundSetEntityDataPacket(this.entity.getId(), list));
      }

      if (this.entity instanceof LivingEntity) {
         Set<AttributeInstance> set = ((LivingEntity)this.entity).getAttributes().getDirtyAttributes();
         if (!set.isEmpty()) {
            this.broadcastAndSend(new ClientboundUpdateAttributesPacket(this.entity.getId(), set));
         }

         set.clear();
      }

   }

   private void broadcastAndSend(Packet<?> p_8539_) {
      this.broadcast.accept(p_8539_);
      if (this.entity instanceof ServerPlayer) {
         ((ServerPlayer)this.entity).connection.send(p_8539_);
      }

   }
}
