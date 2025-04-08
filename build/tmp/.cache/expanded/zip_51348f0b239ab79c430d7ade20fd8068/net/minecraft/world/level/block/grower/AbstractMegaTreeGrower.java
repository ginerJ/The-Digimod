package net.minecraft.world.level.block.grower;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public abstract class AbstractMegaTreeGrower extends AbstractTreeGrower {
   public boolean growTree(ServerLevel p_222891_, ChunkGenerator p_222892_, BlockPos p_222893_, BlockState p_222894_, RandomSource p_222895_) {
      for(int i = 0; i >= -1; --i) {
         for(int j = 0; j >= -1; --j) {
            if (isTwoByTwoSapling(p_222894_, p_222891_, p_222893_, i, j)) {
               return this.placeMega(p_222891_, p_222892_, p_222893_, p_222894_, p_222895_, i, j);
            }
         }
      }

      return super.growTree(p_222891_, p_222892_, p_222893_, p_222894_, p_222895_);
   }

   @Nullable
   protected abstract ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource p_222904_);

   public boolean placeMega(ServerLevel p_222897_, ChunkGenerator p_222898_, BlockPos p_222899_, BlockState p_222900_, RandomSource p_222901_, int p_222902_, int p_222903_) {
      ResourceKey<ConfiguredFeature<?, ?>> resourcekey = this.getConfiguredMegaFeature(p_222901_);
      if (resourcekey == null) {
         return false;
      } else {
         Holder<ConfiguredFeature<?, ?>> holder = p_222897_.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(resourcekey).orElse((Holder.Reference<ConfiguredFeature<?, ?>>)null);
         var event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(p_222897_, p_222901_, p_222899_, holder);
         holder = event.getFeature();
         if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.DENY) return false;
         if (holder == null) {
            return false;
         } else {
            ConfiguredFeature<?, ?> configuredfeature = holder.value();
            BlockState blockstate = Blocks.AIR.defaultBlockState();
            p_222897_.setBlock(p_222899_.offset(p_222902_, 0, p_222903_), blockstate, 4);
            p_222897_.setBlock(p_222899_.offset(p_222902_ + 1, 0, p_222903_), blockstate, 4);
            p_222897_.setBlock(p_222899_.offset(p_222902_, 0, p_222903_ + 1), blockstate, 4);
            p_222897_.setBlock(p_222899_.offset(p_222902_ + 1, 0, p_222903_ + 1), blockstate, 4);
            if (configuredfeature.place(p_222897_, p_222898_, p_222901_, p_222899_.offset(p_222902_, 0, p_222903_))) {
               return true;
            } else {
               p_222897_.setBlock(p_222899_.offset(p_222902_, 0, p_222903_), p_222900_, 4);
               p_222897_.setBlock(p_222899_.offset(p_222902_ + 1, 0, p_222903_), p_222900_, 4);
               p_222897_.setBlock(p_222899_.offset(p_222902_, 0, p_222903_ + 1), p_222900_, 4);
               p_222897_.setBlock(p_222899_.offset(p_222902_ + 1, 0, p_222903_ + 1), p_222900_, 4);
               return false;
            }
         }
      }
   }

   public static boolean isTwoByTwoSapling(BlockState p_59999_, BlockGetter p_60000_, BlockPos p_60001_, int p_60002_, int p_60003_) {
      Block block = p_59999_.getBlock();
      return p_60000_.getBlockState(p_60001_.offset(p_60002_, 0, p_60003_)).is(block) && p_60000_.getBlockState(p_60001_.offset(p_60002_ + 1, 0, p_60003_)).is(block) && p_60000_.getBlockState(p_60001_.offset(p_60002_, 0, p_60003_ + 1)).is(block) && p_60000_.getBlockState(p_60001_.offset(p_60002_ + 1, 0, p_60003_ + 1)).is(block);
   }
}
