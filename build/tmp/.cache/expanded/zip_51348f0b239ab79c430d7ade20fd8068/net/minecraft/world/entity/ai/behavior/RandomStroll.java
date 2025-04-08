package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class RandomStroll {
   private static final int MAX_XZ_DIST = 10;
   private static final int MAX_Y_DIST = 7;
   private static final int[][] SWIM_XY_DISTANCE_TIERS = new int[][]{{1, 1}, {3, 3}, {5, 5}, {6, 5}, {7, 7}, {10, 7}};

   public static OneShot<PathfinderMob> stroll(float p_260304_) {
      return stroll(p_260304_, true);
   }

   public static OneShot<PathfinderMob> stroll(float p_260303_, boolean p_259639_) {
      return strollFlyOrSwim(p_260303_, (p_258601_) -> {
         return LandRandomPos.getPos(p_258601_, 10, 7);
      }, p_259639_ ? (p_258615_) -> {
         return true;
      } : (p_289370_) -> {
         return !p_289370_.isInWaterOrBubble();
      });
   }

   public static BehaviorControl<PathfinderMob> stroll(float p_260204_, int p_259502_, int p_259891_) {
      return strollFlyOrSwim(p_260204_, (p_258605_) -> {
         return LandRandomPos.getPos(p_258605_, p_259502_, p_259891_);
      }, (p_258616_) -> {
         return true;
      });
   }

   public static BehaviorControl<PathfinderMob> fly(float p_259119_) {
      return strollFlyOrSwim(p_259119_, (p_258614_) -> {
         return getTargetFlyPos(p_258614_, 10, 7);
      }, (p_258602_) -> {
         return true;
      });
   }

   public static BehaviorControl<PathfinderMob> swim(float p_259469_) {
      return strollFlyOrSwim(p_259469_, RandomStroll::getTargetSwimPos, Entity::isInWaterOrBubble);
   }

   private static OneShot<PathfinderMob> strollFlyOrSwim(float p_260030_, Function<PathfinderMob, Vec3> p_259912_, Predicate<PathfinderMob> p_259088_) {
      return BehaviorBuilder.create((p_258620_) -> {
         return p_258620_.group(p_258620_.absent(MemoryModuleType.WALK_TARGET)).apply(p_258620_, (p_258600_) -> {
            return (p_258610_, p_258611_, p_258612_) -> {
               if (!p_259088_.test(p_258611_)) {
                  return false;
               } else {
                  Optional<Vec3> optional = Optional.ofNullable(p_259912_.apply(p_258611_));
                  p_258600_.setOrErase(optional.map((p_258622_) -> {
                     return new WalkTarget(p_258622_, p_260030_, 0);
                  }));
                  return true;
               }
            };
         });
      });
   }

   @Nullable
   private static Vec3 getTargetSwimPos(PathfinderMob p_259491_) {
      Vec3 vec3 = null;
      Vec3 vec31 = null;

      for(int[] aint : SWIM_XY_DISTANCE_TIERS) {
         if (vec3 == null) {
            vec31 = BehaviorUtils.getRandomSwimmablePos(p_259491_, aint[0], aint[1]);
         } else {
            vec31 = p_259491_.position().add(p_259491_.position().vectorTo(vec3).normalize().multiply((double)aint[0], (double)aint[1], (double)aint[0]));
         }

         if (vec31 == null || p_259491_.level().getFluidState(BlockPos.containing(vec31)).isEmpty()) {
            return vec3;
         }

         vec3 = vec31;
      }

      return vec31;
   }

   @Nullable
   private static Vec3 getTargetFlyPos(PathfinderMob p_260316_, int p_259038_, int p_259696_) {
      Vec3 vec3 = p_260316_.getViewVector(0.0F);
      return AirAndWaterRandomPos.getPos(p_260316_, p_259038_, p_259696_, -2, vec3.x, vec3.z, (double)((float)Math.PI / 2F));
   }
}