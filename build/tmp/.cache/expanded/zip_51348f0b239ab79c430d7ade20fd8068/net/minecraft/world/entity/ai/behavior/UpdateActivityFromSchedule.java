package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;

public class UpdateActivityFromSchedule {
   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((p_259429_) -> {
         return p_259429_.point((p_288849_, p_288850_, p_288851_) -> {
            p_288850_.getBrain().updateActivityFromSchedule(p_288849_.getDayTime(), p_288849_.getGameTime());
            return true;
         });
      });
   }
}