package net.minecraft.world.level.redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class InstantNeighborUpdater implements NeighborUpdater {
   private final Level level;

   public InstantNeighborUpdater(Level p_230743_) {
      this.level = p_230743_;
   }

   public void shapeUpdate(Direction p_230755_, BlockState p_230756_, BlockPos p_230757_, BlockPos p_230758_, int p_230759_, int p_230760_) {
      NeighborUpdater.executeShapeUpdate(this.level, p_230755_, p_230756_, p_230757_, p_230758_, p_230759_, p_230760_ - 1);
   }

   public void neighborChanged(BlockPos p_230751_, Block p_230752_, BlockPos p_230753_) {
      BlockState blockstate = this.level.getBlockState(p_230751_);
      this.neighborChanged(blockstate, p_230751_, p_230752_, p_230753_, false);
   }

   public void neighborChanged(BlockState p_230745_, BlockPos p_230746_, Block p_230747_, BlockPos p_230748_, boolean p_230749_) {
      NeighborUpdater.executeUpdate(this.level, p_230745_, p_230746_, p_230747_, p_230748_, p_230749_);
   }
}