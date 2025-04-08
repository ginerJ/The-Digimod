package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class VillagerCalmDown {
   private static final int SAFE_DISTANCE_FROM_DANGER = 36;

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((p_258884_) -> {
         return p_258884_.group(p_258884_.registered(MemoryModuleType.HURT_BY), p_258884_.registered(MemoryModuleType.HURT_BY_ENTITY), p_258884_.registered(MemoryModuleType.NEAREST_HOSTILE)).apply(p_258884_, (p_258886_, p_258887_, p_258888_) -> {
            return (p_288856_, p_288857_, p_288858_) -> {
               boolean flag = p_258884_.tryGet(p_258886_).isPresent() || p_258884_.tryGet(p_258888_).isPresent() || p_258884_.<LivingEntity>tryGet(p_258887_).filter((p_259890_) -> {
                  return p_259890_.distanceToSqr(p_288857_) <= 36.0D;
               }).isPresent();
               if (!flag) {
                  p_258886_.erase();
                  p_258887_.erase();
                  p_288857_.getBrain().updateActivityFromSchedule(p_288856_.getDayTime(), p_288856_.getGameTime());
               }

               return true;
            };
         });
      });
   }
}