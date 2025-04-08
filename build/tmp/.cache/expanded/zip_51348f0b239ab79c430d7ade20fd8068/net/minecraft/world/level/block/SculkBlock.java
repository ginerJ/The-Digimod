package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;

public class SculkBlock extends DropExperienceBlock implements SculkBehaviour {
   public SculkBlock(BlockBehaviour.Properties p_222063_) {
      super(p_222063_, ConstantInt.of(1));
   }

   public int attemptUseCharge(SculkSpreader.ChargeCursor p_222073_, LevelAccessor p_222074_, BlockPos p_222075_, RandomSource p_222076_, SculkSpreader p_222077_, boolean p_222078_) {
      int i = p_222073_.getCharge();
      if (i != 0 && p_222076_.nextInt(p_222077_.chargeDecayRate()) == 0) {
         BlockPos blockpos = p_222073_.getPos();
         boolean flag = blockpos.closerThan(p_222075_, (double)p_222077_.noGrowthRadius());
         if (!flag && canPlaceGrowth(p_222074_, blockpos)) {
            int j = p_222077_.growthSpawnCost();
            if (p_222076_.nextInt(j) < i) {
               BlockPos blockpos1 = blockpos.above();
               BlockState blockstate = this.getRandomGrowthState(p_222074_, blockpos1, p_222076_, p_222077_.isWorldGeneration());
               p_222074_.setBlock(blockpos1, blockstate, 3);
               p_222074_.playSound((Player)null, blockpos, blockstate.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            return Math.max(0, i - j);
         } else {
            return p_222076_.nextInt(p_222077_.additionalDecayRate()) != 0 ? i : i - (flag ? 1 : getDecayPenalty(p_222077_, blockpos, p_222075_, i));
         }
      } else {
         return i;
      }
   }

   private static int getDecayPenalty(SculkSpreader p_222080_, BlockPos p_222081_, BlockPos p_222082_, int p_222083_) {
      int i = p_222080_.noGrowthRadius();
      float f = Mth.square((float)Math.sqrt(p_222081_.distSqr(p_222082_)) - (float)i);
      int j = Mth.square(24 - i);
      float f1 = Math.min(1.0F, f / (float)j);
      return Math.max(1, (int)((float)p_222083_ * f1 * 0.5F));
   }

   private BlockState getRandomGrowthState(LevelAccessor p_222068_, BlockPos p_222069_, RandomSource p_222070_, boolean p_222071_) {
      BlockState blockstate;
      if (p_222070_.nextInt(11) == 0) {
         blockstate = Blocks.SCULK_SHRIEKER.defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, Boolean.valueOf(p_222071_));
      } else {
         blockstate = Blocks.SCULK_SENSOR.defaultBlockState();
      }

      return blockstate.hasProperty(BlockStateProperties.WATERLOGGED) && !p_222068_.getFluidState(p_222069_).isEmpty() ? blockstate.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true)) : blockstate;
   }

   private static boolean canPlaceGrowth(LevelAccessor p_222065_, BlockPos p_222066_) {
      BlockState blockstate = p_222065_.getBlockState(p_222066_.above());
      if (blockstate.isAir() || blockstate.is(Blocks.WATER) && blockstate.getFluidState().is(Fluids.WATER)) {
         int i = 0;

         for(BlockPos blockpos : BlockPos.betweenClosed(p_222066_.offset(-4, 0, -4), p_222066_.offset(4, 2, 4))) {
            BlockState blockstate1 = p_222065_.getBlockState(blockpos);
            if (blockstate1.is(Blocks.SCULK_SENSOR) || blockstate1.is(Blocks.SCULK_SHRIEKER)) {
               ++i;
            }

            if (i > 2) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean canChangeBlockStateOnSpread() {
      return false;
   }
}