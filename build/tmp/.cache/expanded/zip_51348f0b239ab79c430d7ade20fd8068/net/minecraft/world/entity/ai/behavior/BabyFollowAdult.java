package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class BabyFollowAdult {
   public static OneShot<AgeableMob> create(UniformInt p_260109_, float p_259621_) {
      return create(p_260109_, (p_147421_) -> {
         return p_259621_;
      });
   }

   public static OneShot<AgeableMob> create(UniformInt p_259321_, Function<LivingEntity, Float> p_259190_) {
      return BehaviorBuilder.create((p_258331_) -> {
         return p_258331_.group(p_258331_.present(MemoryModuleType.NEAREST_VISIBLE_ADULT), p_258331_.registered(MemoryModuleType.LOOK_TARGET), p_258331_.absent(MemoryModuleType.WALK_TARGET)).apply(p_258331_, (p_258317_, p_258318_, p_258319_) -> {
            return (p_258326_, p_258327_, p_258328_) -> {
               if (!p_258327_.isBaby()) {
                  return false;
               } else {
                  AgeableMob ageablemob = p_258331_.get(p_258317_);
                  if (p_258327_.closerThan(ageablemob, (double)(p_259321_.getMaxValue() + 1)) && !p_258327_.closerThan(ageablemob, (double)p_259321_.getMinValue())) {
                     WalkTarget walktarget = new WalkTarget(new EntityTracker(ageablemob, false), p_259190_.apply(p_258327_), p_259321_.getMinValue() - 1);
                     p_258318_.set(new EntityTracker(ageablemob, true));
                     p_258319_.set(walktarget);
                     return true;
                  } else {
                     return false;
                  }
               }
            };
         });
      });
   }
}