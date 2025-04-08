package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.grower.AzaleaTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AzaleaBlock extends BushBlock implements BonemealableBlock {
   private static final AzaleaTreeGrower TREE_GROWER = new AzaleaTreeGrower();
   private static final VoxelShape SHAPE = Shapes.or(Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D), Block.box(6.0D, 0.0D, 6.0D, 10.0D, 8.0D, 10.0D));

   public AzaleaBlock(BlockBehaviour.Properties p_152067_) {
      super(p_152067_);
   }

   public VoxelShape getShape(BlockState p_152084_, BlockGetter p_152085_, BlockPos p_152086_, CollisionContext p_152087_) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState p_152089_, BlockGetter p_152090_, BlockPos p_152091_) {
      return p_152089_.is(Blocks.CLAY) || super.mayPlaceOn(p_152089_, p_152090_, p_152091_);
   }

   public boolean isValidBonemealTarget(LevelReader p_256329_, BlockPos p_256107_, BlockState p_255771_, boolean p_255916_) {
      return p_256329_.getFluidState(p_256107_.above()).isEmpty();
   }

   public boolean isBonemealSuccess(Level p_220712_, RandomSource p_220713_, BlockPos p_220714_, BlockState p_220715_) {
      return (double)p_220712_.random.nextFloat() < 0.45D;
   }

   public void performBonemeal(ServerLevel p_220707_, RandomSource p_220708_, BlockPos p_220709_, BlockState p_220710_) {
      TREE_GROWER.growTree(p_220707_, p_220707_.getChunkSource().getGenerator(), p_220709_, p_220710_, p_220708_);
   }
}