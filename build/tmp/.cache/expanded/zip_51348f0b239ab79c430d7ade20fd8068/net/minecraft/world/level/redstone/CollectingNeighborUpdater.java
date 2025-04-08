package net.minecraft.world.level.redstone;

import com.mojang.logging.LogUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class CollectingNeighborUpdater implements NeighborUpdater {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Level level;
   private final int maxChainedNeighborUpdates;
   private final ArrayDeque<CollectingNeighborUpdater.NeighborUpdates> stack = new ArrayDeque<>();
   private final List<CollectingNeighborUpdater.NeighborUpdates> addedThisLayer = new ArrayList<>();
   private int count = 0;

   public CollectingNeighborUpdater(Level p_230643_, int p_230644_) {
      this.level = p_230643_;
      this.maxChainedNeighborUpdates = p_230644_;
   }

   public void shapeUpdate(Direction p_230664_, BlockState p_230665_, BlockPos p_230666_, BlockPos p_230667_, int p_230668_, int p_230669_) {
      this.addAndRun(p_230666_, new CollectingNeighborUpdater.ShapeUpdate(p_230664_, p_230665_, p_230666_.immutable(), p_230667_.immutable(), p_230668_, p_230669_));
   }

   public void neighborChanged(BlockPos p_230653_, Block p_230654_, BlockPos p_230655_) {
      this.addAndRun(p_230653_, new CollectingNeighborUpdater.SimpleNeighborUpdate(p_230653_, p_230654_, p_230655_.immutable()));
   }

   public void neighborChanged(BlockState p_230647_, BlockPos p_230648_, Block p_230649_, BlockPos p_230650_, boolean p_230651_) {
      this.addAndRun(p_230648_, new CollectingNeighborUpdater.FullNeighborUpdate(p_230647_, p_230648_.immutable(), p_230649_, p_230650_.immutable(), p_230651_));
   }

   public void updateNeighborsAtExceptFromFacing(BlockPos p_230657_, Block p_230658_, @Nullable Direction p_230659_) {
      this.addAndRun(p_230657_, new CollectingNeighborUpdater.MultiNeighborUpdate(p_230657_.immutable(), p_230658_, p_230659_));
   }

   private void addAndRun(BlockPos p_230661_, CollectingNeighborUpdater.NeighborUpdates p_230662_) {
      boolean flag = this.count > 0;
      boolean flag1 = this.maxChainedNeighborUpdates >= 0 && this.count >= this.maxChainedNeighborUpdates;
      ++this.count;
      if (!flag1) {
         if (flag) {
            this.addedThisLayer.add(p_230662_);
         } else {
            this.stack.push(p_230662_);
         }
      } else if (this.count - 1 == this.maxChainedNeighborUpdates) {
         LOGGER.error("Too many chained neighbor updates. Skipping the rest. First skipped position: " + p_230661_.toShortString());
      }

      if (!flag) {
         this.runUpdates();
      }

   }

   private void runUpdates() {
      try {
         while(!this.stack.isEmpty() || !this.addedThisLayer.isEmpty()) {
            for(int i = this.addedThisLayer.size() - 1; i >= 0; --i) {
               this.stack.push(this.addedThisLayer.get(i));
            }

            this.addedThisLayer.clear();
            CollectingNeighborUpdater.NeighborUpdates collectingneighborupdater$neighborupdates = this.stack.peek();

            while(this.addedThisLayer.isEmpty()) {
               if (!collectingneighborupdater$neighborupdates.runNext(this.level)) {
                  this.stack.pop();
                  break;
               }
            }
         }
      } finally {
         this.stack.clear();
         this.addedThisLayer.clear();
         this.count = 0;
      }

   }

   static record FullNeighborUpdate(BlockState state, BlockPos pos, Block block, BlockPos neighborPos, boolean movedByPiston) implements CollectingNeighborUpdater.NeighborUpdates {
      public boolean runNext(Level p_230683_) {
         NeighborUpdater.executeUpdate(p_230683_, this.state, this.pos, this.block, this.neighborPos, this.movedByPiston);
         return false;
      }
   }

   static final class MultiNeighborUpdate implements CollectingNeighborUpdater.NeighborUpdates {
      private final BlockPos sourcePos;
      private final Block sourceBlock;
      @Nullable
      private final Direction skipDirection;
      private int idx = 0;

      MultiNeighborUpdate(BlockPos p_230697_, Block p_230698_, @Nullable Direction p_230699_) {
         this.sourcePos = p_230697_;
         this.sourceBlock = p_230698_;
         this.skipDirection = p_230699_;
         if (NeighborUpdater.UPDATE_ORDER[this.idx] == p_230699_) {
            ++this.idx;
         }

      }

      public boolean runNext(Level p_230701_) {
         BlockPos blockpos = this.sourcePos.relative(NeighborUpdater.UPDATE_ORDER[this.idx++]);
         BlockState blockstate = p_230701_.getBlockState(blockpos);
         blockstate.neighborChanged(p_230701_, blockpos, this.sourceBlock, this.sourcePos, false);
         if (this.idx < NeighborUpdater.UPDATE_ORDER.length && NeighborUpdater.UPDATE_ORDER[this.idx] == this.skipDirection) {
            ++this.idx;
         }

         return this.idx < NeighborUpdater.UPDATE_ORDER.length;
      }
   }

   interface NeighborUpdates {
      boolean runNext(Level p_230702_);
   }

   static record ShapeUpdate(Direction direction, BlockState state, BlockPos pos, BlockPos neighborPos, int updateFlags, int updateLimit) implements CollectingNeighborUpdater.NeighborUpdates {
      public boolean runNext(Level p_230716_) {
         NeighborUpdater.executeShapeUpdate(p_230716_, this.direction, this.state, this.pos, this.neighborPos, this.updateFlags, this.updateLimit);
         return false;
      }
   }

   static record SimpleNeighborUpdate(BlockPos pos, Block block, BlockPos neighborPos) implements CollectingNeighborUpdater.NeighborUpdates {
      public boolean runNext(Level p_230734_) {
         BlockState blockstate = p_230734_.getBlockState(this.pos);
         NeighborUpdater.executeUpdate(p_230734_, blockstate, this.pos, this.block, this.neighborPos, false);
         return false;
      }
   }
}