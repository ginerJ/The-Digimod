package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PinkPetalsBlock extends BushBlock implements BonemealableBlock {
   public static final int MIN_FLOWERS = 1;
   public static final int MAX_FLOWERS = 4;
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final IntegerProperty AMOUNT = BlockStateProperties.FLOWER_AMOUNT;

   public PinkPetalsBlock(BlockBehaviour.Properties p_273335_) {
      super(p_273335_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(AMOUNT, Integer.valueOf(1)));
   }

   public BlockState rotate(BlockState p_273485_, Rotation p_273021_) {
      return p_273485_.setValue(FACING, p_273021_.rotate(p_273485_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_272961_, Mirror p_273278_) {
      return p_272961_.rotate(p_273278_.getRotation(p_272961_.getValue(FACING)));
   }

   public boolean canBeReplaced(BlockState p_272922_, BlockPlaceContext p_273534_) {
      return !p_273534_.isSecondaryUseActive() && p_273534_.getItemInHand().is(this.asItem()) && p_272922_.getValue(AMOUNT) < 4 ? true : super.canBeReplaced(p_272922_, p_273534_);
   }

   public VoxelShape getShape(BlockState p_273399_, BlockGetter p_273568_, BlockPos p_273314_, CollisionContext p_273274_) {
      return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_273158_) {
      BlockState blockstate = p_273158_.getLevel().getBlockState(p_273158_.getClickedPos());
      return blockstate.is(this) ? blockstate.setValue(AMOUNT, Integer.valueOf(Math.min(4, blockstate.getValue(AMOUNT) + 1))) : this.defaultBlockState().setValue(FACING, p_273158_.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_272634_) {
      p_272634_.add(FACING, AMOUNT);
   }

   public boolean isValidBonemealTarget(LevelReader p_272968_, BlockPos p_273762_, BlockState p_273662_, boolean p_273778_) {
      return true;
   }

   public boolean isBonemealSuccess(Level p_272604_, RandomSource p_273609_, BlockPos p_272988_, BlockState p_273231_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_273476_, RandomSource p_273093_, BlockPos p_272601_, BlockState p_272683_) {
      int i = p_272683_.getValue(AMOUNT);
      if (i < 4) {
         p_273476_.setBlock(p_272601_, p_272683_.setValue(AMOUNT, Integer.valueOf(i + 1)), 2);
      } else {
         popResource(p_273476_, p_272601_, new ItemStack(this));
      }

   }
}