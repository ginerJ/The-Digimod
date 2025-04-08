package net.minecraft.world.entity.monster.piglin;

import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;

public class StopAdmiringIfItemTooFarAway<E extends Piglin> {
   public static BehaviorControl<LivingEntity> create(int p_259415_) {
      return BehaviorBuilder.create((p_259152_) -> {
         return p_259152_.group(p_259152_.present(MemoryModuleType.ADMIRING_ITEM), p_259152_.registered(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM)).apply(p_259152_, (p_260178_, p_259241_) -> {
            return (p_259613_, p_259304_, p_259748_) -> {
               if (!p_259304_.getOffhandItem().isEmpty()) {
                  return false;
               } else {
                  Optional<ItemEntity> optional = p_259152_.tryGet(p_259241_);
                  if (optional.isPresent() && optional.get().closerThan(p_259304_, (double)p_259415_)) {
                     return false;
                  } else {
                     p_260178_.erase();
                     return true;
                  }
               }
            };
         });
      });
   }
}