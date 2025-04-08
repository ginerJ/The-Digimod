package net.minecraft.world.entity.ai.behavior;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class BehaviorUtils {
   private BehaviorUtils() {
   }

   public static void lockGazeAndWalkToEachOther(LivingEntity p_22603_, LivingEntity p_22604_, float p_22605_) {
      lookAtEachOther(p_22603_, p_22604_);
      setWalkAndLookTargetMemoriesToEachOther(p_22603_, p_22604_, p_22605_);
   }

   public static boolean entityIsVisible(Brain<?> p_22637_, LivingEntity p_22638_) {
      Optional<NearestVisibleLivingEntities> optional = p_22637_.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
      return optional.isPresent() && optional.get().contains(p_22638_);
   }

   public static boolean targetIsValid(Brain<?> p_22640_, MemoryModuleType<? extends LivingEntity> p_22641_, EntityType<?> p_22642_) {
      return targetIsValid(p_22640_, p_22641_, (p_289317_) -> {
         return p_289317_.getType() == p_22642_;
      });
   }

   private static boolean targetIsValid(Brain<?> p_22644_, MemoryModuleType<? extends LivingEntity> p_22645_, Predicate<LivingEntity> p_22646_) {
      return p_22644_.getMemory(p_22645_).filter(p_22646_).filter(LivingEntity::isAlive).filter((p_186037_) -> {
         return entityIsVisible(p_22644_, p_186037_);
      }).isPresent();
   }

   private static void lookAtEachOther(LivingEntity p_22671_, LivingEntity p_22672_) {
      lookAtEntity(p_22671_, p_22672_);
      lookAtEntity(p_22672_, p_22671_);
   }

   public static void lookAtEntity(LivingEntity p_22596_, LivingEntity p_22597_) {
      p_22596_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(p_22597_, true));
   }

   private static void setWalkAndLookTargetMemoriesToEachOther(LivingEntity p_22661_, LivingEntity p_22662_, float p_22663_) {
      int i = 2;
      setWalkAndLookTargetMemories(p_22661_, p_22662_, p_22663_, 2);
      setWalkAndLookTargetMemories(p_22662_, p_22661_, p_22663_, 2);
   }

   public static void setWalkAndLookTargetMemories(LivingEntity p_22591_, Entity p_22592_, float p_22593_, int p_22594_) {
      setWalkAndLookTargetMemories(p_22591_, new EntityTracker(p_22592_, true), p_22593_, p_22594_);
   }

   public static void setWalkAndLookTargetMemories(LivingEntity p_22618_, BlockPos p_22619_, float p_22620_, int p_22621_) {
      setWalkAndLookTargetMemories(p_22618_, new BlockPosTracker(p_22619_), p_22620_, p_22621_);
   }

   public static void setWalkAndLookTargetMemories(LivingEntity p_217129_, PositionTracker p_217130_, float p_217131_, int p_217132_) {
      WalkTarget walktarget = new WalkTarget(p_217130_, p_217131_, p_217132_);
      p_217129_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, p_217130_);
      p_217129_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, walktarget);
   }

   public static void throwItem(LivingEntity p_22614_, ItemStack p_22615_, Vec3 p_22616_) {
      Vec3 vec3 = new Vec3((double)0.3F, (double)0.3F, (double)0.3F);
      throwItem(p_22614_, p_22615_, p_22616_, vec3, 0.3F);
   }

   public static void throwItem(LivingEntity p_217134_, ItemStack p_217135_, Vec3 p_217136_, Vec3 p_217137_, float p_217138_) {
      double d0 = p_217134_.getEyeY() - (double)p_217138_;
      ItemEntity itementity = new ItemEntity(p_217134_.level(), p_217134_.getX(), d0, p_217134_.getZ(), p_217135_);
      itementity.setThrower(p_217134_.getUUID());
      Vec3 vec3 = p_217136_.subtract(p_217134_.position());
      vec3 = vec3.normalize().multiply(p_217137_.x, p_217137_.y, p_217137_.z);
      itementity.setDeltaMovement(vec3);
      itementity.setDefaultPickUpDelay();
      p_217134_.level().addFreshEntity(itementity);
   }

   public static SectionPos findSectionClosestToVillage(ServerLevel p_22582_, SectionPos p_22583_, int p_22584_) {
      int i = p_22582_.sectionsToVillage(p_22583_);
      return SectionPos.cube(p_22583_, p_22584_).filter((p_186017_) -> {
         return p_22582_.sectionsToVillage(p_186017_) < i;
      }).min(Comparator.comparingInt(p_22582_::sectionsToVillage)).orElse(p_22583_);
   }

   public static boolean isWithinAttackRange(Mob p_22633_, LivingEntity p_22634_, int p_22635_) {
      Item $$4 = p_22633_.getMainHandItem().getItem();
      if ($$4 instanceof ProjectileWeaponItem projectileweaponitem) {
         if (p_22633_.canFireProjectileWeapon(projectileweaponitem)) {
            int i = projectileweaponitem.getDefaultProjectileRange() - p_22635_;
            return p_22633_.closerThan(p_22634_, (double)i);
         }
      }

      return p_22633_.isWithinMeleeAttackRange(p_22634_);
   }

   public static boolean isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(LivingEntity p_22599_, LivingEntity p_22600_, double p_22601_) {
      Optional<LivingEntity> optional = p_22599_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
      if (optional.isEmpty()) {
         return false;
      } else {
         double d0 = p_22599_.distanceToSqr(optional.get().position());
         double d1 = p_22599_.distanceToSqr(p_22600_.position());
         return d1 > d0 + p_22601_ * p_22601_;
      }
   }

   public static boolean canSee(LivingEntity p_22668_, LivingEntity p_22669_) {
      Brain<?> brain = p_22668_.getBrain();
      return !brain.hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES) ? false : brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get().contains(p_22669_);
   }

   public static LivingEntity getNearestTarget(LivingEntity p_22626_, Optional<LivingEntity> p_22627_, LivingEntity p_22628_) {
      return p_22627_.isEmpty() ? p_22628_ : getTargetNearestMe(p_22626_, p_22627_.get(), p_22628_);
   }

   public static LivingEntity getTargetNearestMe(LivingEntity p_22607_, LivingEntity p_22608_, LivingEntity p_22609_) {
      Vec3 vec3 = p_22608_.position();
      Vec3 vec31 = p_22609_.position();
      return p_22607_.distanceToSqr(vec3) < p_22607_.distanceToSqr(vec31) ? p_22608_ : p_22609_;
   }

   public static Optional<LivingEntity> getLivingEntityFromUUIDMemory(LivingEntity p_22611_, MemoryModuleType<UUID> p_22612_) {
      Optional<UUID> optional = p_22611_.getBrain().getMemory(p_22612_);
      return optional.map((p_289315_) -> {
         return ((ServerLevel)p_22611_.level()).getEntity(p_289315_);
      }).map((p_186019_) -> {
         LivingEntity livingentity1;
         if (p_186019_ instanceof LivingEntity livingentity) {
            livingentity1 = livingentity;
         } else {
            livingentity1 = null;
         }

         return livingentity1;
      });
   }

   @Nullable
   public static Vec3 getRandomSwimmablePos(PathfinderMob p_147445_, int p_147446_, int p_147447_) {
      Vec3 vec3 = DefaultRandomPos.getPos(p_147445_, p_147446_, p_147447_);

      for(int i = 0; vec3 != null && !p_147445_.level().getBlockState(BlockPos.containing(vec3)).isPathfindable(p_147445_.level(), BlockPos.containing(vec3), PathComputationType.WATER) && i++ < 10; vec3 = DefaultRandomPos.getPos(p_147445_, p_147446_, p_147447_)) {
      }

      return vec3;
   }

   public static boolean isBreeding(LivingEntity p_217127_) {
      return p_217127_.getBrain().hasMemoryValue(MemoryModuleType.BREED_TARGET);
   }
}