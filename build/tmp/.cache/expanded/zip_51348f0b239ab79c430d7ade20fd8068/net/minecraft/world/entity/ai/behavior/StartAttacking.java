package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class StartAttacking {
   public static <E extends Mob> BehaviorControl<E> create(Function<E, Optional<? extends LivingEntity>> p_259868_) {
      return create((p_24212_) -> {
         return true;
      }, p_259868_);
   }

   public static <E extends Mob> BehaviorControl<E> create(Predicate<E> p_259618_, Function<E, Optional<? extends LivingEntity>> p_259435_) {
      return BehaviorBuilder.create((p_258782_) -> {
         return p_258782_.group(p_258782_.absent(MemoryModuleType.ATTACK_TARGET), p_258782_.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(p_258782_, (p_258778_, p_258779_) -> {
            return (p_258773_, p_258774_, p_258775_) -> {
               if (!p_259618_.test(p_258774_)) {
                  return false;
               } else {
                  Optional<? extends LivingEntity> optional = p_259435_.apply(p_258774_);
                  if (optional.isEmpty()) {
                     return false;
                  } else {
                     LivingEntity livingentity = optional.get();
                     if (!p_258774_.canAttack(livingentity)) {
                        return false;
                     } else {
                        net.minecraftforge.event.entity.living.LivingChangeTargetEvent changeTargetEvent = net.minecraftforge.common.ForgeHooks.onLivingChangeTarget(p_258774_, livingentity, net.minecraftforge.event.entity.living.LivingChangeTargetEvent.LivingTargetType.BEHAVIOR_TARGET);
                        if (changeTargetEvent.isCanceled())
                           return false;

                        p_258778_.set(changeTargetEvent.getNewTarget());
                        p_258779_.erase();
                        return true;
                     }
                  }
               }
            };
         });
      });
   }
}
