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

public class BeetrootBlock extends CropBlock {
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
   private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D)};

   public BeetrootBlock(BlockBehaviour.Properties p_49661_) {
      super(p_49661_);
   }

   protected IntegerProperty getAgeProperty() {
      return AGE;
   }

   public int getMaxAge() {
      return 3;
   }

   protected ItemLike getBaseSeedId() {
      return Items.BEETROOT_SEEDS;
   }

   public void randomTick(BlockState p_220778_, ServerLevel p_220779_, BlockPos p_220780_, RandomSource p_220781_) {
      if (p_220781_.nextInt(3) != 0) {
         super.randomTick(p_220778_, p_220779_, p_220780_, p_220781_);
      }

   }

   protected int getBonemealAgeIncrease(Level p_49663_) {
      return super.getBonemealAgeIncrease(p_49663_) / 3;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49665_) {
      p_49665_.add(AGE);
   }

   public VoxelShape getShape(BlockState p_49672_, BlockGetter p_49673_, BlockPos p_49674_, CollisionContext p_49675_) {
      return SHAPE_BY_AGE[this.getAge(p_49672_)];
   }
}