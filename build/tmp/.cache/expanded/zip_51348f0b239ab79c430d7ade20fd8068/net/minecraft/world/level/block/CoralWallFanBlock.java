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

public class CoralWallFanBlock extends BaseCoralWallFanBlock {
   private final Block deadBlock;

   public CoralWallFanBlock(Block p_52202_, BlockBehaviour.Properties p_52203_) {
      super(p_52203_);
      this.deadBlock = p_52202_;
   }

   public void onPlace(BlockState p_52217_, Level p_52218_, BlockPos p_52219_, BlockState p_52220_, boolean p_52221_) {
      this.tryScheduleDieTick(p_52217_, p_52218_, p_52219_);
   }

   public void tick(BlockState p_221035_, ServerLevel p_221036_, BlockPos p_221037_, RandomSource p_221038_) {
      if (!scanForWater(p_221035_, p_221036_, p_221037_)) {
         p_221036_.setBlock(p_221037_, this.deadBlock.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(FACING, p_221035_.getValue(FACING)), 2);
      }

   }

   public BlockState updateShape(BlockState p_52210_, Direction p_52211_, BlockState p_52212_, LevelAccessor p_52213_, BlockPos p_52214_, BlockPos p_52215_) {
      if (p_52211_.getOpposite() == p_52210_.getValue(FACING) && !p_52210_.canSurvive(p_52213_, p_52214_)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if (p_52210_.getValue(WATERLOGGED)) {
            p_52213_.scheduleTick(p_52214_, Fluids.WATER, Fluids.WATER.getTickDelay(p_52213_));
         }

         this.tryScheduleDieTick(p_52210_, p_52213_, p_52214_);
         return super.updateShape(p_52210_, p_52211_, p_52212_, p_52213_, p_52214_, p_52215_);
      }
   }
}