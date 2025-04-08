package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public interface ChangeOverTimeBlock<T extends Enum<T>> {
   int SCAN_DISTANCE = 4;

   Optional<BlockState> getNext(BlockState p_153040_);

   float getChanceModifier();

   default void onRandomTick(BlockState p_220948_, ServerLevel p_220949_, BlockPos p_220950_, RandomSource p_220951_) {
      float f = 0.05688889F;
      if (p_220951_.nextFloat() < 0.05688889F) {
         this.applyChangeOverTime(p_220948_, p_220949_, p_220950_, p_220951_);
      }

   }

   T getAge();

   default void applyChangeOverTime(BlockState p_220953_, ServerLevel p_220954_, BlockPos p_220955_, RandomSource p_220956_) {
      int i = this.getAge().ordinal();
      int j = 0;
      int k = 0;

      for(BlockPos blockpos : BlockPos.withinManhattan(p_220955_, 4, 4, 4)) {
         int l = blockpos.distManhattan(p_220955_);
         if (l > 4) {
            break;
         }

         if (!blockpos.equals(p_220955_)) {
            BlockState blockstate = p_220954_.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (block instanceof ChangeOverTimeBlock) {
               Enum<?> oenum = ((ChangeOverTimeBlock)block).getAge();
               if (this.getAge().getClass() == oenum.getClass()) {
                  int i1 = oenum.ordinal();
                  if (i1 < i) {
                     return;
                  }

                  if (i1 > i) {
                     ++k;
                  } else {
                     ++j;
                  }
               }
            }
         }
      }

      float f = (float)(k + 1) / (float)(k + j + 1);
      float f1 = f * f * this.getChanceModifier();
      if (p_220956_.nextFloat() < f1) {
         this.getNext(p_220953_).ifPresent((p_153039_) -> {
            p_220954_.setBlockAndUpdate(p_220955_, p_153039_);
         });
      }

   }
}