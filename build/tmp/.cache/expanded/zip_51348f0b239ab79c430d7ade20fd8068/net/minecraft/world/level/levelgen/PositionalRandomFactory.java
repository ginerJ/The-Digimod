package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public interface PositionalRandomFactory {
   default RandomSource at(BlockPos p_224543_) {
      return this.at(p_224543_.getX(), p_224543_.getY(), p_224543_.getZ());
   }

   default RandomSource fromHashOf(ResourceLocation p_224541_) {
      return this.fromHashOf(p_224541_.toString());
   }

   RandomSource fromHashOf(String p_224544_);

   RandomSource at(int p_224537_, int p_224538_, int p_224539_);

   @VisibleForTesting
   void parityConfigString(StringBuilder p_189317_);
}