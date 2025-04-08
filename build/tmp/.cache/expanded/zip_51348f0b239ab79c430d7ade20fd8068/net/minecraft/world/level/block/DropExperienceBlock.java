package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class DropExperienceBlock extends Block {
   private final IntProvider xpRange;

   public DropExperienceBlock(BlockBehaviour.Properties p_221081_) {
      this(p_221081_, ConstantInt.of(0));
   }

   public DropExperienceBlock(BlockBehaviour.Properties p_221083_, IntProvider p_221084_) {
      super(p_221083_);
      this.xpRange = p_221084_;
   }

   public void spawnAfterBreak(BlockState p_221086_, ServerLevel p_221087_, BlockPos p_221088_, ItemStack p_221089_, boolean p_221090_) {
      super.spawnAfterBreak(p_221086_, p_221087_, p_221088_, p_221089_, p_221090_);

   }

   @Override
   public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.util.RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
      return silkTouchLevel == 0 ? this.xpRange.sample(randomSource) : 0;
   }
}
