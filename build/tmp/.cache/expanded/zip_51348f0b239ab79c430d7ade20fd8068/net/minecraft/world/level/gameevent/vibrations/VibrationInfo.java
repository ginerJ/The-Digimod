package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public record VibrationInfo(GameEvent gameEvent, float distance, Vec3 pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid, @Nullable Entity entity) {
   public static final Codec<VibrationInfo> CODEC = RecordCodecBuilder.create((p_258994_) -> {
      return p_258994_.group(BuiltInRegistries.GAME_EVENT.byNameCodec().fieldOf("game_event").forGetter(VibrationInfo::gameEvent), Codec.floatRange(0.0F, Float.MAX_VALUE).fieldOf("distance").forGetter(VibrationInfo::distance), Vec3.CODEC.fieldOf("pos").forGetter(VibrationInfo::pos), UUIDUtil.CODEC.optionalFieldOf("source").forGetter((p_250608_) -> {
         return Optional.ofNullable(p_250608_.uuid());
      }), UUIDUtil.CODEC.optionalFieldOf("projectile_owner").forGetter((p_250607_) -> {
         return Optional.ofNullable(p_250607_.projectileOwnerUuid());
      })).apply(p_258994_, (p_249268_, p_252231_, p_250951_, p_250574_, p_249661_) -> {
         return new VibrationInfo(p_249268_, p_252231_, p_250951_, p_250574_.orElse((UUID)null), p_249661_.orElse((UUID)null));
      });
   });

   public VibrationInfo(GameEvent p_249055_, float p_250190_, Vec3 p_251692_, @Nullable UUID p_249849_, @Nullable UUID p_249731_) {
      this(p_249055_, p_250190_, p_251692_, p_249849_, p_249731_, (Entity)null);
   }

   public VibrationInfo(GameEvent p_252023_, float p_251086_, Vec3 p_250935_, @Nullable Entity p_249432_) {
      this(p_252023_, p_251086_, p_250935_, p_249432_ == null ? null : p_249432_.getUUID(), getProjectileOwner(p_249432_), p_249432_);
   }

   @Nullable
   private static UUID getProjectileOwner(@Nullable Entity p_251531_) {
      if (p_251531_ instanceof Projectile projectile) {
         if (projectile.getOwner() != null) {
            return projectile.getOwner().getUUID();
         }
      }

      return null;
   }

   public Optional<Entity> getEntity(ServerLevel p_249184_) {
      return Optional.ofNullable(this.entity).or(() -> {
         return Optional.ofNullable(this.uuid).map(p_249184_::getEntity);
      });
   }

   public Optional<Entity> getProjectileOwner(ServerLevel p_249217_) {
      return this.getEntity(p_249217_).filter((p_249829_) -> {
         return p_249829_ instanceof Projectile;
      }).map((p_249388_) -> {
         return (Projectile)p_249388_;
      }).map(Projectile::getOwner).or(() -> {
         return Optional.ofNullable(this.projectileOwnerUuid).map(p_249217_::getEntity);
      });
   }
}