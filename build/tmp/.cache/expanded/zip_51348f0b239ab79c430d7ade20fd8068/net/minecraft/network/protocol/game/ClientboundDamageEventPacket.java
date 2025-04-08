package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record ClientboundDamageEventPacket(int entityId, int sourceTypeId, int sourceCauseId, int sourceDirectId, Optional<Vec3> sourcePosition) implements Packet<ClientGamePacketListener> {
   public ClientboundDamageEventPacket(Entity p_270474_, DamageSource p_270781_) {
      this(p_270474_.getId(), p_270474_.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getId(p_270781_.type()), p_270781_.getEntity() != null ? p_270781_.getEntity().getId() : -1, p_270781_.getDirectEntity() != null ? p_270781_.getDirectEntity().getId() : -1, Optional.ofNullable(p_270781_.sourcePositionRaw()));
   }

   public ClientboundDamageEventPacket(FriendlyByteBuf p_270722_) {
      this(p_270722_.readVarInt(), p_270722_.readVarInt(), readOptionalEntityId(p_270722_), readOptionalEntityId(p_270722_), p_270722_.readOptional((p_270813_) -> {
         return new Vec3(p_270813_.readDouble(), p_270813_.readDouble(), p_270813_.readDouble());
      }));
   }

   private static void writeOptionalEntityId(FriendlyByteBuf p_270812_, int p_270852_) {
      p_270812_.writeVarInt(p_270852_ + 1);
   }

   private static int readOptionalEntityId(FriendlyByteBuf p_270462_) {
      return p_270462_.readVarInt() - 1;
   }

   public void write(FriendlyByteBuf p_270971_) {
      p_270971_.writeVarInt(this.entityId);
      p_270971_.writeVarInt(this.sourceTypeId);
      writeOptionalEntityId(p_270971_, this.sourceCauseId);
      writeOptionalEntityId(p_270971_, this.sourceDirectId);
      p_270971_.writeOptional(this.sourcePosition, (p_270788_, p_270196_) -> {
         p_270788_.writeDouble(p_270196_.x());
         p_270788_.writeDouble(p_270196_.y());
         p_270788_.writeDouble(p_270196_.z());
      });
   }

   public void handle(ClientGamePacketListener p_270510_) {
      p_270510_.handleDamageEvent(this);
   }

   public DamageSource getSource(Level p_270943_) {
      Holder<DamageType> holder = p_270943_.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolder(this.sourceTypeId).get();
      if (this.sourcePosition.isPresent()) {
         return new DamageSource(holder, this.sourcePosition.get());
      } else {
         Entity entity = p_270943_.getEntity(this.sourceCauseId);
         Entity entity1 = p_270943_.getEntity(this.sourceDirectId);
         return new DamageSource(holder, entity1, entity);
      }
   }
}