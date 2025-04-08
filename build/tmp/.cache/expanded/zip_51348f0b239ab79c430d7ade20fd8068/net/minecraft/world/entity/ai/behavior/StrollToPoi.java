package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import org.apache.commons.lang3.mutable.MutableLong;

public class StrollToPoi {
   public static BehaviorControl<PathfinderMob> create(MemoryModuleType<GlobalPos> p_259919_, float p_259285_, int p_259332_, int p_259904_) {
      MutableLong mutablelong = new MutableLong(0L);
      return BehaviorBuilder.create((p_258859_) -> {
         return p_258859_.group(p_258859_.registered(MemoryModuleType.WALK_TARGET), p_258859_.present(p_259919_)).apply(p_258859_, (p_258842_, p_258843_) -> {
            return (p_258851_, p_258852_, p_258853_) -> {
               GlobalPos globalpos = p_258859_.get(p_258843_);
               if (p_258851_.dimension() == globalpos.dimension() && globalpos.pos().closerToCenterThan(p_258852_.position(), (double)p_259904_)) {
                  if (p_258853_ <= mutablelong.getValue()) {
                     return true;
                  } else {
                     p_258842_.set(new WalkTarget(globalpos.pos(), p_259285_, p_259332_));
                     mutablelong.setValue(p_258853_ + 80L);
                     return true;
                  }
               } else {
                  return false;
               }
            };
         });
      });
   }
}