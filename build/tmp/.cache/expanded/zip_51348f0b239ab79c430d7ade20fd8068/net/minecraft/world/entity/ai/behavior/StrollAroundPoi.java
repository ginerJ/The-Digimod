package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableLong;

public class StrollAroundPoi {
   private static final int MIN_TIME_BETWEEN_STROLLS = 180;
   private static final int STROLL_MAX_XZ_DIST = 8;
   private static final int STROLL_MAX_Y_DIST = 6;

   public static OneShot<PathfinderMob> create(MemoryModuleType<GlobalPos> p_259159_, float p_260182_, int p_260102_) {
      MutableLong mutablelong = new MutableLong(0L);
      return BehaviorBuilder.create((p_258827_) -> {
         return p_258827_.group(p_258827_.registered(MemoryModuleType.WALK_TARGET), p_258827_.present(p_259159_)).apply(p_258827_, (p_258821_, p_258822_) -> {
            return (p_258834_, p_258835_, p_258836_) -> {
               GlobalPos globalpos = p_258827_.get(p_258822_);
               if (p_258834_.dimension() == globalpos.dimension() && globalpos.pos().closerToCenterThan(p_258835_.position(), (double)p_260102_)) {
                  if (p_258836_ <= mutablelong.getValue()) {
                     return true;
                  } else {
                     Optional<Vec3> optional = Optional.ofNullable(LandRandomPos.getPos(p_258835_, 8, 6));
                     p_258821_.setOrErase(optional.map((p_258816_) -> {
                        return new WalkTarget(p_258816_, p_260182_, 1);
                     }));
                     mutablelong.setValue(p_258836_ + 180L);
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