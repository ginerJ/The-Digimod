package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;

public class CocoaDecorator extends TreeDecorator {
   public static final Codec<CocoaDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(CocoaDecorator::new, (p_69989_) -> {
      return p_69989_.probability;
   }).codec();
   private final float probability;

   public CocoaDecorator(float p_69976_) {
      this.probability = p_69976_;
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.COCOA;
   }

   public void place(TreeDecorator.Context p_226028_) {
      RandomSource randomsource = p_226028_.random();
      if (!(randomsource.nextFloat() >= this.probability)) {
         List<BlockPos> list = p_226028_.logs();
         int i = list.get(0).getY();
         list.stream().filter((p_69980_) -> {
            return p_69980_.getY() - i <= 2;
         }).forEach((p_226026_) -> {
            for(Direction direction : Direction.Plane.HORIZONTAL) {
               if (randomsource.nextFloat() <= 0.25F) {
                  Direction direction1 = direction.getOpposite();
                  BlockPos blockpos = p_226026_.offset(direction1.getStepX(), 0, direction1.getStepZ());
                  if (p_226028_.isAir(blockpos)) {
                     p_226028_.setBlock(blockpos, Blocks.COCOA.defaultBlockState().setValue(CocoaBlock.AGE, Integer.valueOf(randomsource.nextInt(3))).setValue(CocoaBlock.FACING, direction));
                  }
               }
            }

         });
      }
   }
}