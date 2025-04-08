package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SnowLayerBlock extends Block {
   public static final int MAX_HEIGHT = 8;
   public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
   protected static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[]{Shapes.empty(), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};
   public static final int HEIGHT_IMPASSABLE = 5;

   public SnowLayerBlock(BlockBehaviour.Properties p_56585_) {
      super(p_56585_);
      this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, Integer.valueOf(1)));
   }

   public boolean isPathfindable(BlockState p_56592_, BlockGetter p_56593_, BlockPos p_56594_, PathComputationType p_56595_) {
      switch (p_56595_) {
         case LAND:
            return p_56592_.getValue(LAYERS) < 5;
         case WATER:
            return false;
         case AIR:
            return false;
         default:
            return false;
      }
   }

   public VoxelShape getShape(BlockState p_56620_, BlockGetter p_56621_, BlockPos p_56622_, CollisionContext p_56623_) {
      return SHAPE_BY_LAYER[p_56620_.getValue(LAYERS)];
   }

   public VoxelShape getCollisionShape(BlockState p_56625_, BlockGetter p_56626_, BlockPos p_56627_, CollisionContext p_56628_) {
      return SHAPE_BY_LAYER[p_56625_.getValue(LAYERS) - 1];
   }

   public VoxelShape getBlockSupportShape(BlockState p_56632_, BlockGetter p_56633_, BlockPos p_56634_) {
      return SHAPE_BY_LAYER[p_56632_.getValue(LAYERS)];
   }

   public VoxelShape getVisualShape(BlockState p_56597_, BlockGetter p_56598_, BlockPos p_56599_, CollisionContext p_56600_) {
      return SHAPE_BY_LAYER[p_56597_.getValue(LAYERS)];
   }

   public boolean useShapeForLightOcclusion(BlockState p_56630_) {
      return true;
   }

   public float getShadeBrightness(BlockState p_222453_, BlockGetter p_222454_, BlockPos p_222455_) {
      return p_222453_.getValue(LAYERS) == 8 ? 0.2F : 1.0F;
   }

   public boolean canSurvive(BlockState p_56602_, LevelReader p_56603_, BlockPos p_56604_) {
      BlockState blockstate = p_56603_.getBlockState(p_56604_.below());
      if (blockstate.is(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)) {
         return false;
      } else if (blockstate.is(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON)) {
         return true;
      } else {
         return Block.isFaceFull(blockstate.getCollisionShape(p_56603_, p_56604_.below()), Direction.UP) || blockstate.is(this) && blockstate.getValue(LAYERS) == 8;
      }
   }

   public BlockState updateShape(BlockState p_56606_, Direction p_56607_, BlockState p_56608_, LevelAccessor p_56609_, BlockPos p_56610_, BlockPos p_56611_) {
      return !p_56606_.canSurvive(p_56609_, p_56610_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_56606_, p_56607_, p_56608_, p_56609_, p_56610_, p_56611_);
   }

   public void randomTick(BlockState p_222448_, ServerLevel p_222449_, BlockPos p_222450_, RandomSource p_222451_) {
      if (p_222449_.getBrightness(LightLayer.BLOCK, p_222450_) > 11) {
         dropResources(p_222448_, p_222449_, p_222450_);
         p_222449_.removeBlock(p_222450_, false);
      }

   }

   public boolean canBeReplaced(BlockState p_56589_, BlockPlaceContext p_56590_) {
      int i = p_56589_.getValue(LAYERS);
      if (p_56590_.getItemInHand().is(this.asItem()) && i < 8) {
         if (p_56590_.replacingClickedOnBlock()) {
            return p_56590_.getClickedFace() == Direction.UP;
         } else {
            return true;
         }
      } else {
         return i == 1;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_56587_) {
      BlockState blockstate = p_56587_.getLevel().getBlockState(p_56587_.getClickedPos());
      if (blockstate.is(this)) {
         int i = blockstate.getValue(LAYERS);
         return blockstate.setValue(LAYERS, Integer.valueOf(Math.min(8, i + 1)));
      } else {
         return super.getStateForPlacement(p_56587_);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_56613_) {
      p_56613_.add(LAYERS);
   }
}