package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MangroveLeavesBlock extends LeavesBlock implements BonemealableBlock {
   public MangroveLeavesBlock(BlockBehaviour.Properties p_221425_) {
      super(p_221425_);
   }

   public boolean isValidBonemealTarget(LevelReader p_256534_, BlockPos p_256299_, BlockState p_255926_, boolean p_255711_) {
      return p_256534_.getBlockState(p_256299_.below()).isAir();
   }

   public boolean isBonemealSuccess(Level p_221437_, RandomSource p_221438_, BlockPos p_221439_, BlockState p_221440_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_221427_, RandomSource p_221428_, BlockPos p_221429_, BlockState p_221430_) {
      p_221427_.setBlock(p_221429_.below(), MangrovePropaguleBlock.createNewHangingPropagule(), 2);
   }
}