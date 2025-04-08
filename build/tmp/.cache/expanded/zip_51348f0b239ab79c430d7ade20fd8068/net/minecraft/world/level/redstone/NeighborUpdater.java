package net.minecraft.world.level.redstone;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface NeighborUpdater {
   Direction[] UPDATE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH};

   void shapeUpdate(Direction p_230791_, BlockState p_230792_, BlockPos p_230793_, BlockPos p_230794_, int p_230795_, int p_230796_);

   void neighborChanged(BlockPos p_230785_, Block p_230786_, BlockPos p_230787_);

   void neighborChanged(BlockState p_230780_, BlockPos p_230781_, Block p_230782_, BlockPos p_230783_, boolean p_230784_);

   default void updateNeighborsAtExceptFromFacing(BlockPos p_230788_, Block p_230789_, @Nullable Direction p_230790_) {
      for(Direction direction : UPDATE_ORDER) {
         if (direction != p_230790_) {
            this.neighborChanged(p_230788_.relative(direction), p_230789_, p_230788_);
         }
      }

   }

   static void executeShapeUpdate(LevelAccessor p_230771_, Direction p_230772_, BlockState p_230773_, BlockPos p_230774_, BlockPos p_230775_, int p_230776_, int p_230777_) {
      BlockState blockstate = p_230771_.getBlockState(p_230774_);
      BlockState blockstate1 = blockstate.updateShape(p_230772_, p_230773_, p_230771_, p_230774_, p_230775_);
      Block.updateOrDestroy(blockstate, blockstate1, p_230771_, p_230774_, p_230776_, p_230777_);
   }

   static void executeUpdate(Level p_230764_, BlockState p_230765_, BlockPos p_230766_, Block p_230767_, BlockPos p_230768_, boolean p_230769_) {
      try {
         p_230765_.neighborChanged(p_230764_, p_230766_, p_230767_, p_230768_, p_230769_);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception while updating neighbours");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Block being updated");
         crashreportcategory.setDetail("Source block type", () -> {
            try {
               return String.format(Locale.ROOT, "ID #%s (%s // %s)", BuiltInRegistries.BLOCK.getKey(p_230767_), p_230767_.getDescriptionId(), p_230767_.getClass().getCanonicalName());
            } catch (Throwable throwable1) {
               return "ID #" + BuiltInRegistries.BLOCK.getKey(p_230767_);
            }
         });
         CrashReportCategory.populateBlockDetails(crashreportcategory, p_230764_, p_230766_, p_230765_);
         throw new ReportedException(crashreport);
      }
   }
}