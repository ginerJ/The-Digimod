package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class StopAttackingIfTargetInvalid {
   private static final int TIMEOUT_TO_GET_WITHIN_ATTACK_RANGE = 200;

   public static <E extends Mob> BehaviorControl<E> create(BiConsumer<E, LivingEntity> p_260165_) {
      return create((p_147988_) -> {
         return false;
      }, p_260165_, true);
   }

   public static <E extends Mob> BehaviorControl<E> create(Predicate<LivingEntity> p_259762_) {
      return create(p_259762_, (p_217411_, p_217412_) -> {
      }, true);
   }

   public static <E extends Mob> BehaviorControl<E> create() {
      return create((p_147986_) -> {
         return false;
      }, (p_217408_, p_217409_) -> {
      }, true);
   }

   public static <E extends Mob> BehaviorControl<E> create(Predicate<LivingEntity> p_260357_, BiConsumer<E, LivingEntity> p_259568_, boolean p_260319_) {
      return BehaviorBuilder.create((p_258801_) -> {
         return p_258801_.group(p_258801_.present(MemoryModuleType.ATTACK_TARGET), p_258801_.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(p_258801_, (p_258787_, p_258788_) -> {
            return (p_258795_, p_258796_, p_258797_) -> {
               LivingEntity livingentity = p_258801_.get(p_258787_);
               if (p_258796_.canAttack(livingentity) && (!p_260319_ || !isTiredOfTryingToReachTarget(p_258796_, p_258801_.tryGet(p_258788_))) && livingentity.isAlive() && livingentity.level() == p_258796_.level() && !p_260357_.test(livingentity)) {
                  return true;
               } else {
                  p_259568_.accept(p_258796_, livingentity);
                  p_258787_.erase();
                  return true;
               }
            };
         });
      });
   }

   private static boolean isTiredOfTryingToReachTarget(LivingEntity p_259416_, Optional<Long> p_259377_) {
      return p_259377_.isPresent() && p_259416_.level().getGameTime() - p_259377_.get() > 200L;
   }
}