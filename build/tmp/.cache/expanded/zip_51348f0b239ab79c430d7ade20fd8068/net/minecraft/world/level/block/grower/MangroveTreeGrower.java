package net.minecraft.world.level.block.grower;

import javax.annotation.Nullable;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class MangroveTreeGrower extends AbstractTreeGrower {
   private final float tallProbability;

   public MangroveTreeGrower(float p_222933_) {
      this.tallProbability = p_222933_;
   }

   @Nullable
   protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource p_255870_, boolean p_256508_) {
      return p_255870_.nextFloat() < this.tallProbability ? TreeFeatures.TALL_MANGROVE : TreeFeatures.MANGROVE;
   }
}