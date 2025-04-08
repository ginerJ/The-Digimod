package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.grower.MangroveTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MangrovePropaguleBlock extends SaplingBlock implements SimpleWaterloggedBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_4;
   public static final int MAX_AGE = 4;
   private static final VoxelShape[] SHAPE_PER_AGE = new VoxelShape[]{Block.box(7.0D, 13.0D, 7.0D, 9.0D, 16.0D, 9.0D), Block.box(7.0D, 10.0D, 7.0D, 9.0D, 16.0D, 9.0D), Block.box(7.0D, 7.0D, 7.0D, 9.0D, 16.0D, 9.0D), Block.box(7.0D, 3.0D, 7.0D, 9.0D, 16.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D)};
   private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
   private static final float GROW_TALL_MANGROVE_PROBABILITY = 0.85F;

   public MangrovePropaguleBlock(BlockBehaviour.Properties p_221449_) {
      super(new MangroveTreeGrower(0.85F), p_221449_);
      this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, Integer.valueOf(0)).setValue(AGE, Integer.valueOf(0)).setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(HANGING, Boolean.valueOf(false)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_221484_) {
      p_221484_.add(STAGE).add(AGE).add(WATERLOGGED).add(HANGING);
   }

   protected boolean mayPlaceOn(BlockState p_221496_, BlockGetter p_221497_, BlockPos p_221498_) {
      return super.mayPlaceOn(p_221496_, p_221497_, p_221498_) || p_221496_.is(Blocks.CLAY);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_221456_) {
      FluidState fluidstate = p_221456_.getLevel().getFluidState(p_221456_.getClickedPos());
      boolean flag = fluidstate.getType() == Fluids.WATER;
      return super.getStateForPlacement(p_221456_).setValue(WATERLOGGED, Boolean.valueOf(flag)).setValue(AGE, Integer.valueOf(4));
   }

   public VoxelShape getShape(BlockState p_221468_, BlockGetter p_221469_, BlockPos p_221470_, CollisionContext p_221471_) {
      Vec3 vec3 = p_221468_.getOffset(p_221469_, p_221470_);
      VoxelShape voxelshape;
      if (!p_221468_.getValue(HANGING)) {
         voxelshape = SHAPE_PER_AGE[4];
      } else {
         voxelshape = SHAPE_PER_AGE[p_221468_.getValue(AGE)];
      }

      return voxelshape.move(vec3.x, vec3.y, vec3.z);
   }

   public boolean canSurvive(BlockState p_221473_, LevelReader p_221474_, BlockPos p_221475_) {
      return isHanging(p_221473_) ? p_221474_.getBlockState(p_221475_.above()).is(Blocks.MANGROVE_LEAVES) : super.canSurvive(p_221473_, p_221474_, p_221475_);
   }

   public BlockState updateShape(BlockState p_221477_, Direction p_221478_, BlockState p_221479_, LevelAccessor p_221480_, BlockPos p_221481_, BlockPos p_221482_) {
      if (p_221477_.getValue(WATERLOGGED)) {
         p_221480_.scheduleTick(p_221481_, Fluids.WATER, Fluids.WATER.getTickDelay(p_221480_));
      }

      return p_221478_ == Direction.UP && !p_221477_.canSurvive(p_221480_, p_221481_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_221477_, p_221478_, p_221479_, p_221480_, p_221481_, p_221482_);
   }

   public FluidState getFluidState(BlockState p_221494_) {
      return p_221494_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_221494_);
   }

   public void randomTick(BlockState p_221488_, ServerLevel p_221489_, BlockPos p_221490_, RandomSource p_221491_) {
      if (!isHanging(p_221488_)) {
         if (p_221491_.nextInt(7) == 0) {
            this.advanceTree(p_221489_, p_221490_, p_221488_, p_221491_);
         }

      } else {
         if (!isFullyGrown(p_221488_)) {
            p_221489_.setBlock(p_221490_, p_221488_.cycle(AGE), 2);
         }

      }
   }

   public boolean isValidBonemealTarget(LevelReader p_256541_, BlockPos p_221459_, BlockState p_221460_, boolean p_221461_) {
      return !isHanging(p_221460_) || !isFullyGrown(p_221460_);
   }

   public boolean isBonemealSuccess(Level p_221463_, RandomSource p_221464_, BlockPos p_221465_, BlockState p_221466_) {
      return isHanging(p_221466_) ? !isFullyGrown(p_221466_) : super.isBonemealSuccess(p_221463_, p_221464_, p_221465_, p_221466_);
   }

   public void performBonemeal(ServerLevel p_221451_, RandomSource p_221452_, BlockPos p_221453_, BlockState p_221454_) {
      if (isHanging(p_221454_) && !isFullyGrown(p_221454_)) {
         p_221451_.setBlock(p_221453_, p_221454_.cycle(AGE), 2);
      } else {
         super.performBonemeal(p_221451_, p_221452_, p_221453_, p_221454_);
      }

   }

   private static boolean isHanging(BlockState p_221500_) {
      return p_221500_.getValue(HANGING);
   }

   private static boolean isFullyGrown(BlockState p_221502_) {
      return p_221502_.getValue(AGE) == 4;
   }

   public static BlockState createNewHangingPropagule() {
      return createNewHangingPropagule(0);
   }

   public static BlockState createNewHangingPropagule(int p_221486_) {
      return Blocks.MANGROVE_PROPAGULE.defaultBlockState().setValue(HANGING, Boolean.valueOf(true)).setValue(AGE, Integer.valueOf(p_221486_));
   }
}