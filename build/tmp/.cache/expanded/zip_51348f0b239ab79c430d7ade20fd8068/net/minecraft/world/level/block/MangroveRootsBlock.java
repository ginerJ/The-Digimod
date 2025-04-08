package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class MangroveRootsBlock extends Block implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   public MangroveRootsBlock(BlockBehaviour.Properties p_221506_) {
      super(p_221506_);
      this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public boolean skipRendering(BlockState p_221510_, BlockState p_221511_, Direction p_221512_) {
      return p_221511_.is(Blocks.MANGROVE_ROOTS) && p_221512_.getAxis() == Direction.Axis.Y;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_221508_) {
      FluidState fluidstate = p_221508_.getLevel().getFluidState(p_221508_.getClickedPos());
      boolean flag = fluidstate.getType() == Fluids.WATER;
      return super.getStateForPlacement(p_221508_).setValue(WATERLOGGED, Boolean.valueOf(flag));
   }

   public BlockState updateShape(BlockState p_221514_, Direction p_221515_, BlockState p_221516_, LevelAccessor p_221517_, BlockPos p_221518_, BlockPos p_221519_) {
      if (p_221514_.getValue(WATERLOGGED)) {
         p_221517_.scheduleTick(p_221518_, Fluids.WATER, Fluids.WATER.getTickDelay(p_221517_));
      }

      return super.updateShape(p_221514_, p_221515_, p_221516_, p_221517_, p_221518_, p_221519_);
   }

   public FluidState getFluidState(BlockState p_221523_) {
      return p_221523_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_221523_);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_221521_) {
      p_221521_.add(WATERLOGGED);
   }
}