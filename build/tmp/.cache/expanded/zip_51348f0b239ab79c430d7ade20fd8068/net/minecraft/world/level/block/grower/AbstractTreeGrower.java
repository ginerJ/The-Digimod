package net.minecraft.world.level.block.grower;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public abstract class AbstractTreeGrower {
   @Nullable
   protected abstract ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource p_222910_, boolean p_222911_);

   public boolean growTree(ServerLevel p_222905_, ChunkGenerator p_222906_, BlockPos p_222907_, BlockState p_222908_, RandomSource p_222909_) {
      ResourceKey<ConfiguredFeature<?, ?>> resourcekey = this.getConfiguredFeature(p_222909_, this.hasFlowers(p_222905_, p_222907_));
      if (resourcekey == null) {
         return false;
      } else {
         Holder<ConfiguredFeature<?, ?>> holder = p_222905_.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(resourcekey).orElse((Holder.Reference<ConfiguredFeature<?, ?>>)null);
         var event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(p_222905_, p_222909_, p_222907_, holder);
         holder = event.getFeature();
         if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.DENY) return false;
         if (holder == null) {
            return false;
         } else {
            ConfiguredFeature<?, ?> configuredfeature = holder.value();
            BlockState blockstate = p_222905_.getFluidState(p_222907_).createLegacyBlock();
            p_222905_.setBlock(p_222907_, blockstate, 4);
            if (configuredfeature.place(p_222905_, p_222906_, p_222909_, p_222907_)) {
               if (p_222905_.getBlockState(p_222907_) == blockstate) {
                  p_222905_.sendBlockUpdated(p_222907_, p_222908_, blockstate, 2);
               }

               return true;
            } else {
               p_222905_.setBlock(p_222907_, p_222908_, 4);
               return false;
            }
         }
      }
   }

   private boolean hasFlowers(LevelAccessor p_60012_, BlockPos p_60013_) {
      for(BlockPos blockpos : BlockPos.MutableBlockPos.betweenClosed(p_60013_.below().north(2).west(2), p_60013_.above().south(2).east(2))) {
         if (p_60012_.getBlockState(blockpos).is(BlockTags.FLOWERS)) {
            return true;
         }
      }

      return false;
   }
}
