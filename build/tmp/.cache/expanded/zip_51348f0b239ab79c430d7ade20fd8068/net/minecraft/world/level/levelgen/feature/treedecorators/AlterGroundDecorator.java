package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class AlterGroundDecorator extends TreeDecorator {
   public static final Codec<AlterGroundDecorator> CODEC = BlockStateProvider.CODEC.fieldOf("provider").xmap(AlterGroundDecorator::new, (p_69327_) -> {
      return p_69327_.provider;
   }).codec();
   private final BlockStateProvider provider;

   public AlterGroundDecorator(BlockStateProvider p_69306_) {
      this.provider = p_69306_;
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.ALTER_GROUND;
   }

   public void place(TreeDecorator.Context p_225969_) {
      List<BlockPos> list = Lists.newArrayList();
      List<BlockPos> list1 = p_225969_.roots();
      List<BlockPos> list2 = p_225969_.logs();
      if (list1.isEmpty()) {
         list.addAll(list2);
      } else if (!list2.isEmpty() && list1.get(0).getY() == list2.get(0).getY()) {
         list.addAll(list2);
         list.addAll(list1);
      } else {
         list.addAll(list1);
      }

      if (!list.isEmpty()) {
         var eventProvider = net.minecraftforge.event.ForgeEventFactory.alterGround(p_225969_, list, this.provider::getState);
         int i = list.get(0).getY();
         list.stream().filter((p_69310_) -> {
            return p_69310_.getY() == i;
         }).forEach((p_225978_) -> {
            this.placeCircle(p_225969_, p_225978_.west().north(), eventProvider);
            this.placeCircle(p_225969_, p_225978_.east(2).north(), eventProvider);
            this.placeCircle(p_225969_, p_225978_.west().south(2), eventProvider);
            this.placeCircle(p_225969_, p_225978_.east(2).south(2), eventProvider);

            for(int j = 0; j < 5; ++j) {
               int k = p_225969_.random().nextInt(64);
               int l = k % 8;
               int i1 = k / 8;
               if (l == 0 || l == 7 || i1 == 0 || i1 == 7) {
                  this.placeCircle(p_225969_, p_225978_.offset(-3 + l, 0, -3 + i1), eventProvider);
               }
            }

         });
      }
   }

   private void placeCircle(TreeDecorator.Context p_225971_, BlockPos p_225972_) {
      placeCircle(p_225971_, p_225972_, this.provider::getState);
   }

   private void placeCircle(TreeDecorator.Context p_225971_, BlockPos p_225972_, net.minecraftforge.event.level.AlterGroundEvent.StateProvider eventProvider) {
      for(int i = -2; i <= 2; ++i) {
         for(int j = -2; j <= 2; ++j) {
            if (Math.abs(i) != 2 || Math.abs(j) != 2) {
               this.placeBlockAt(p_225971_, p_225972_.offset(i, 0, j), eventProvider);
            }
         }
      }

   }

   private void placeBlockAt(TreeDecorator.Context p_225974_, BlockPos p_225975_) {
      placeCircle(p_225974_, p_225975_, this.provider::getState);
   }

   private void placeBlockAt(TreeDecorator.Context p_225974_, BlockPos p_225975_, net.minecraftforge.event.level.AlterGroundEvent.StateProvider eventProvider) {
      for(int i = 2; i >= -3; --i) {
         BlockPos blockpos = p_225975_.above(i);
         if (Feature.isGrassOrDirt(p_225974_.level(), blockpos)) {
            p_225974_.setBlock(blockpos, eventProvider.getState(p_225974_.random(), p_225975_));
            break;
         }

         if (!p_225974_.isAir(blockpos) && i < 0) {
            break;
         }
      }

   }
}
