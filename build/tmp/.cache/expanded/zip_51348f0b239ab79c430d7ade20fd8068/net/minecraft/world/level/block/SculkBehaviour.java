package net.minecraft.world.level.block;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public interface SculkBehaviour {
   SculkBehaviour DEFAULT = new SculkBehaviour() {
      public boolean attemptSpreadVein(LevelAccessor p_222048_, BlockPos p_222049_, BlockState p_222050_, @Nullable Collection<Direction> p_222051_, boolean p_222052_) {
         if (p_222051_ == null) {
            return ((SculkVeinBlock)Blocks.SCULK_VEIN).getSameSpaceSpreader().spreadAll(p_222048_.getBlockState(p_222049_), p_222048_, p_222049_, p_222052_) > 0L;
         } else if (!p_222051_.isEmpty()) {
            return !p_222050_.isAir() && !p_222050_.getFluidState().is(Fluids.WATER) ? false : SculkVeinBlock.regrow(p_222048_, p_222049_, p_222050_, p_222051_);
         } else {
            return SculkBehaviour.super.attemptSpreadVein(p_222048_, p_222049_, p_222050_, p_222051_, p_222052_);
         }
      }

      public int attemptUseCharge(SculkSpreader.ChargeCursor p_222054_, LevelAccessor p_222055_, BlockPos p_222056_, RandomSource p_222057_, SculkSpreader p_222058_, boolean p_222059_) {
         return p_222054_.getDecayDelay() > 0 ? p_222054_.getCharge() : 0;
      }

      public int updateDecayDelay(int p_222061_) {
         return Math.max(p_222061_ - 1, 0);
      }
   };

   default byte getSculkSpreadDelay() {
      return 1;
   }

   default void onDischarged(LevelAccessor p_222026_, BlockState p_222027_, BlockPos p_222028_, RandomSource p_222029_) {
   }

   default boolean depositCharge(LevelAccessor p_222031_, BlockPos p_222032_, RandomSource p_222033_) {
      return false;
   }

   default boolean attemptSpreadVein(LevelAccessor p_222034_, BlockPos p_222035_, BlockState p_222036_, @Nullable Collection<Direction> p_222037_, boolean p_222038_) {
      return ((MultifaceBlock)Blocks.SCULK_VEIN).getSpreader().spreadAll(p_222036_, p_222034_, p_222035_, p_222038_) > 0L;
   }

   default boolean canChangeBlockStateOnSpread() {
      return true;
   }

   default int updateDecayDelay(int p_222045_) {
      return 1;
   }

   int attemptUseCharge(SculkSpreader.ChargeCursor p_222039_, LevelAccessor p_222040_, BlockPos p_222041_, RandomSource p_222042_, SculkSpreader p_222043_, boolean p_222044_);
}