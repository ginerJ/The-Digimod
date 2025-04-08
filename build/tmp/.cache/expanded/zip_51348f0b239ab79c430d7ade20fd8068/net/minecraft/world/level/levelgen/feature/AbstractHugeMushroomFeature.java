package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;

public abstract class AbstractHugeMushroomFeature extends Feature<HugeMushroomFeatureConfiguration> {
   public AbstractHugeMushroomFeature(Codec<HugeMushroomFeatureConfiguration> p_65093_) {
      super(p_65093_);
   }

   protected void placeTrunk(LevelAccessor p_224930_, RandomSource p_224931_, BlockPos p_224932_, HugeMushroomFeatureConfiguration p_224933_, int p_224934_, BlockPos.MutableBlockPos p_224935_) {
      for(int i = 0; i < p_224934_; ++i) {
         p_224935_.set(p_224932_).move(Direction.UP, i);
         if (!p_224930_.getBlockState(p_224935_).isSolidRender(p_224930_, p_224935_)) {
            this.setBlock(p_224930_, p_224935_, p_224933_.stemProvider.getState(p_224931_, p_224932_));
         }
      }

   }

   protected int getTreeHeight(RandomSource p_224922_) {
      int i = p_224922_.nextInt(3) + 4;
      if (p_224922_.nextInt(12) == 0) {
         i *= 2;
      }

      return i;
   }

   protected boolean isValidPosition(LevelAccessor p_65099_, BlockPos p_65100_, int p_65101_, BlockPos.MutableBlockPos p_65102_, HugeMushroomFeatureConfiguration p_65103_) {
      int i = p_65100_.getY();
      if (i >= p_65099_.getMinBuildHeight() + 1 && i + p_65101_ + 1 < p_65099_.getMaxBuildHeight()) {
         BlockState blockstate = p_65099_.getBlockState(p_65100_.below());
         if (!isDirt(blockstate) && !blockstate.is(BlockTags.MUSHROOM_GROW_BLOCK)) {
            return false;
         } else {
            for(int j = 0; j <= p_65101_; ++j) {
               int k = this.getTreeRadiusForHeight(-1, -1, p_65103_.foliageRadius, j);

               for(int l = -k; l <= k; ++l) {
                  for(int i1 = -k; i1 <= k; ++i1) {
                     BlockState blockstate1 = p_65099_.getBlockState(p_65102_.setWithOffset(p_65100_, l, j, i1));
                     if (!blockstate1.isAir() && !blockstate1.is(BlockTags.LEAVES)) {
                        return false;
                     }
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean place(FeaturePlaceContext<HugeMushroomFeatureConfiguration> p_159436_) {
      WorldGenLevel worldgenlevel = p_159436_.level();
      BlockPos blockpos = p_159436_.origin();
      RandomSource randomsource = p_159436_.random();
      HugeMushroomFeatureConfiguration hugemushroomfeatureconfiguration = p_159436_.config();
      int i = this.getTreeHeight(randomsource);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      if (!this.isValidPosition(worldgenlevel, blockpos, i, blockpos$mutableblockpos, hugemushroomfeatureconfiguration)) {
         return false;
      } else {
         this.makeCap(worldgenlevel, randomsource, blockpos, i, blockpos$mutableblockpos, hugemushroomfeatureconfiguration);
         this.placeTrunk(worldgenlevel, randomsource, blockpos, hugemushroomfeatureconfiguration, i, blockpos$mutableblockpos);
         return true;
      }
   }

   protected abstract int getTreeRadiusForHeight(int p_65094_, int p_65095_, int p_65096_, int p_65097_);

   protected abstract void makeCap(LevelAccessor p_224923_, RandomSource p_224924_, BlockPos p_224925_, int p_224926_, BlockPos.MutableBlockPos p_224927_, HugeMushroomFeatureConfiguration p_224928_);
}