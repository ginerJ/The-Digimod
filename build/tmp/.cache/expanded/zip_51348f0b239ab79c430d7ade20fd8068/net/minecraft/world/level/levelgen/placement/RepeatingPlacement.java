package net.minecraft.world.level.levelgen.placement;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public abstract class RepeatingPlacement extends PlacementModifier {
   protected abstract int count(RandomSource p_226400_, BlockPos p_226401_);

   public Stream<BlockPos> getPositions(PlacementContext p_226403_, RandomSource p_226404_, BlockPos p_226405_) {
      return IntStream.range(0, this.count(p_226404_, p_226405_)).mapToObj((p_191912_) -> {
         return p_226405_;
      });
   }
}