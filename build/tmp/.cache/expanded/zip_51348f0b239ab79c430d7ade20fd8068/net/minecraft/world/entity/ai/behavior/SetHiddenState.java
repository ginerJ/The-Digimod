package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.apache.commons.lang3.mutable.MutableInt;

public class SetHiddenState {
   private static final int HIDE_TIMEOUT = 300;

   public static BehaviorControl<LivingEntity> create(int p_259244_, int p_260263_) {
      int i = p_259244_ * 20;
      MutableInt mutableint = new MutableInt(0);
      return BehaviorBuilder.create((p_259055_) -> {
         return p_259055_.group(p_259055_.present(MemoryModuleType.HIDING_PLACE), p_259055_.present(MemoryModuleType.HEARD_BELL_TIME)).apply(p_259055_, (p_260296_, p_260145_) -> {
            return (p_288844_, p_288845_, p_288846_) -> {
               long j = p_259055_.<Long>get(p_260145_);
               boolean flag = j + 300L <= p_288846_;
               if (mutableint.getValue() <= i && !flag) {
                  BlockPos blockpos = p_259055_.get(p_260296_).pos();
                  if (blockpos.closerThan(p_288845_.blockPosition(), (double)p_260263_)) {
                     mutableint.increment();
                  }

                  return true;
               } else {
                  p_260145_.erase();
                  p_260296_.erase();
                  p_288845_.getBrain().updateActivityFromSchedule(p_288844_.getDayTime(), p_288844_.getGameTime());
                  mutableint.setValue(0);
                  return true;
               }
            };
         });
      });
   }
}