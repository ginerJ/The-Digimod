package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class RingBell {
   private static final float BELL_RING_CHANCE = 0.95F;
   public static final int RING_BELL_FROM_DISTANCE = 3;

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((p_259094_) -> {
         return p_259094_.group(p_259094_.present(MemoryModuleType.MEETING_POINT)).apply(p_259094_, (p_259028_) -> {
            return (p_259026_, p_260317_, p_260205_) -> {
               if (p_259026_.random.nextFloat() <= 0.95F) {
                  return false;
               } else {
                  BlockPos blockpos = p_259094_.get(p_259028_).pos();
                  if (blockpos.closerThan(p_260317_.blockPosition(), 3.0D)) {
                     BlockState blockstate = p_259026_.getBlockState(blockpos);
                     if (blockstate.is(Blocks.BELL)) {
                        BellBlock bellblock = (BellBlock)blockstate.getBlock();
                        bellblock.attemptToRing(p_260317_, p_259026_, blockpos, (Direction)null);
                     }
                  }

                  return true;
               }
            };
         });
      });
   }
}