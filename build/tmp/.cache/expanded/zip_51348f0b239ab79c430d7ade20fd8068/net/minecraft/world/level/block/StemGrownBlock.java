package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class StemGrownBlock extends Block implements net.minecraftforge.common.IPlantable {
   public StemGrownBlock(BlockBehaviour.Properties p_57058_) {
      super(p_57058_);
   }

   public abstract StemBlock getStem();

   public abstract AttachedStemBlock getAttachedStem();

   //FORGE START
   @Override
   public net.minecraft.world.level.block.state.BlockState getPlant(net.minecraft.world.level.BlockGetter world, net.minecraft.core.BlockPos pos) {
      net.minecraft.world.level.block.state.BlockState state = world.getBlockState(pos);
      if (state.getBlock() != this) return defaultBlockState();
      return state;
   }
}
