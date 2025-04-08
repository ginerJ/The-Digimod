package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class ResetRaidStatus {
   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((p_259870_) -> {
         return p_259870_.point((p_288835_, p_288836_, p_288837_) -> {
            if (p_288835_.random.nextInt(20) != 0) {
               return false;
            } else {
               Brain<?> brain = p_288836_.getBrain();
               Raid raid = p_288835_.getRaidAt(p_288836_.blockPosition());
               if (raid == null || raid.isStopped() || raid.isLoss()) {
                  brain.setDefaultActivity(Activity.IDLE);
                  brain.updateActivityFromSchedule(p_288835_.getDayTime(), p_288835_.getGameTime());
               }

               return true;
            }
         });
      });
   }
}