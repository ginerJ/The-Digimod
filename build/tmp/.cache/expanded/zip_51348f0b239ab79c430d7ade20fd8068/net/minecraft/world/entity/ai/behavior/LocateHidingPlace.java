package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;

public class LocateHidingPlace {
   public static OneShot<LivingEntity> create(int p_259202_, float p_259881_, int p_259982_) {
      return BehaviorBuilder.create((p_258505_) -> {
         return p_258505_.group(p_258505_.absent(MemoryModuleType.WALK_TARGET), p_258505_.registered(MemoryModuleType.HOME), p_258505_.registered(MemoryModuleType.HIDING_PLACE), p_258505_.registered(MemoryModuleType.PATH), p_258505_.registered(MemoryModuleType.LOOK_TARGET), p_258505_.registered(MemoryModuleType.BREED_TARGET), p_258505_.registered(MemoryModuleType.INTERACTION_TARGET)).apply(p_258505_, (p_258484_, p_258485_, p_258486_, p_258487_, p_258488_, p_258489_, p_258490_) -> {
            return (p_289346_, p_289347_, p_289348_) -> {
               p_289346_.getPoiManager().find((p_217258_) -> {
                  return p_217258_.is(PoiTypes.HOME);
               }, (p_23425_) -> {
                  return true;
               }, p_289347_.blockPosition(), p_259982_ + 1, PoiManager.Occupancy.ANY).filter((p_289334_) -> {
                  return p_289334_.closerToCenterThan(p_289347_.position(), (double)p_259982_);
               }).or(() -> {
                  return p_289346_.getPoiManager().getRandom((p_217256_) -> {
                     return p_217256_.is(PoiTypes.HOME);
                  }, (p_23421_) -> {
                     return true;
                  }, PoiManager.Occupancy.ANY, p_289347_.blockPosition(), p_259202_, p_289347_.getRandom());
               }).or(() -> {
                  return p_258505_.<GlobalPos>tryGet(p_258485_).map(GlobalPos::pos);
               }).ifPresent((p_289359_) -> {
                  p_258487_.erase();
                  p_258488_.erase();
                  p_258489_.erase();
                  p_258490_.erase();
                  p_258486_.set(GlobalPos.of(p_289346_.dimension(), p_289359_));
                  if (!p_289359_.closerToCenterThan(p_289347_.position(), (double)p_259982_)) {
                     p_258484_.set(new WalkTarget(p_289359_, p_259881_, p_259982_));
                  }

               });
               return true;
            };
         });
      });
   }
}