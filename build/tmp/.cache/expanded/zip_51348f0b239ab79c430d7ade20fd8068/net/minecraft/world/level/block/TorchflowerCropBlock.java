package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TorchflowerCropBlock extends CropBlock {
   public static final int MAX_AGE = 2;
   public static final IntegerProperty AGE = BlockStateProperties.AGE_1;
   private static final float AABB_OFFSET = 3.0F;
   private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D), Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D)};
   private static final int BONEMEAL_INCREASE = 1;

   public TorchflowerCropBlock(BlockBehaviour.Properties p_272642_) {
      super(p_272642_);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_272679_) {
      p_272679_.add(AGE);
   }

   public VoxelShape getShape(BlockState p_272748_, BlockGetter p_273408_, BlockPos p_272762_, CollisionContext p_272649_) {
      return SHAPE_BY_AGE[this.getAge(p_272748_)];
   }

   protected IntegerProperty getAgeProperty() {
      return AGE;
   }

   public int getMaxAge() {
      return 2;
   }

   protected ItemLike getBaseSeedId() {
      return Items.TORCHFLOWER_SEEDS;
   }

   public BlockState getStateForAge(int p_275698_) {
      return p_275698_ == 2 ? Blocks.TORCHFLOWER.defaultBlockState() : super.getStateForAge(p_275698_);
   }

   public void randomTick(BlockState p_273361_, ServerLevel p_273515_, BlockPos p_273546_, RandomSource p_273261_) {
      if (p_273261_.nextInt(3) != 0) {
         super.randomTick(p_273361_, p_273515_, p_273546_, p_273261_);
      }

   }

   protected int getBonemealAgeIncrease(Level p_273475_) {
      return 1;
   }
}