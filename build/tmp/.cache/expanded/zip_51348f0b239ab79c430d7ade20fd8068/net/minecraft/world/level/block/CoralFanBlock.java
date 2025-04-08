package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class CoralFanBlock extends BaseCoralFanBlock {
   private final Block deadBlock;

   public CoralFanBlock(Block p_52151_, BlockBehaviour.Properties p_52152_) {
      super(p_52152_);
      this.deadBlock = p_52151_;
   }

   public void onPlace(BlockState p_52166_, Level p_52167_, BlockPos p_52168_, BlockState p_52169_, boolean p_52170_) {
      this.tryScheduleDieTick(p_52166_, p_52167_, p_52168_);
   }

   public void tick(BlockState p_221025_, ServerLevel p_221026_, BlockPos p_221027_, RandomSource p_221028_) {
      if (!scanForWater(p_221025_, p_221026_, p_221027_)) {
         p_221026_.setBlock(p_221027_, this.deadBlock.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)), 2);
      }

   }

   public BlockState updateShape(BlockState p_52159_, Direction p_52160_, BlockState p_52161_, LevelAccessor p_52162_, BlockPos p_52163_, BlockPos p_52164_) {
      if (p_52160_ == Direction.DOWN && !p_52159_.canSurvive(p_52162_, p_52163_)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         this.tryScheduleDieTick(p_52159_, p_52162_, p_52163_);
         if (p_52159_.getValue(WATERLOGGED)) {
            p_52162_.scheduleTick(p_52163_, Fluids.WATER, Fluids.WATER.getTickDelay(p_52162_));
         }

         return super.updateShape(p_52159_, p_52160_, p_52161_, p_52162_, p_52163_, p_52164_);
      }
   }
}