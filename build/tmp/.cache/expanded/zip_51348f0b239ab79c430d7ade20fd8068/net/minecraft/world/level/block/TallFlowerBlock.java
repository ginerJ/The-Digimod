package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TallFlowerBlock extends DoublePlantBlock implements BonemealableBlock {
   public TallFlowerBlock(BlockBehaviour.Properties p_57296_) {
      super(p_57296_);
   }

   public boolean isValidBonemealTarget(LevelReader p_256234_, BlockPos p_57304_, BlockState p_57305_, boolean p_57306_) {
      return true;
   }

   public boolean isBonemealSuccess(Level p_222573_, RandomSource p_222574_, BlockPos p_222575_, BlockState p_222576_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_222568_, RandomSource p_222569_, BlockPos p_222570_, BlockState p_222571_) {
      popResource(p_222568_, p_222570_, new ItemStack(this));
   }
}