package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class RandomSpreadFoliagePlacer extends FoliagePlacer {
   public static final Codec<RandomSpreadFoliagePlacer> CODEC = RecordCodecBuilder.create((p_161522_) -> {
      return foliagePlacerParts(p_161522_).and(p_161522_.group(IntProvider.codec(1, 512).fieldOf("foliage_height").forGetter((p_161537_) -> {
         return p_161537_.foliageHeight;
      }), Codec.intRange(0, 256).fieldOf("leaf_placement_attempts").forGetter((p_161524_) -> {
         return p_161524_.leafPlacementAttempts;
      }))).apply(p_161522_, RandomSpreadFoliagePlacer::new);
   });
   private final IntProvider foliageHeight;
   private final int leafPlacementAttempts;

   public RandomSpreadFoliagePlacer(IntProvider p_161506_, IntProvider p_161507_, IntProvider p_161508_, int p_161509_) {
      super(p_161506_, p_161507_);
      this.foliageHeight = p_161508_;
      this.leafPlacementAttempts = p_161509_;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.RANDOM_SPREAD_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader p_225723_, FoliagePlacer.FoliageSetter p_272842_, RandomSource p_225725_, TreeConfiguration p_225726_, int p_225727_, FoliagePlacer.FoliageAttachment p_225728_, int p_225729_, int p_225730_, int p_225731_) {
      BlockPos blockpos = p_225728_.pos();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = blockpos.mutable();

      for(int i = 0; i < this.leafPlacementAttempts; ++i) {
         blockpos$mutableblockpos.setWithOffset(blockpos, p_225725_.nextInt(p_225730_) - p_225725_.nextInt(p_225730_), p_225725_.nextInt(p_225729_) - p_225725_.nextInt(p_225729_), p_225725_.nextInt(p_225730_) - p_225725_.nextInt(p_225730_));
         tryPlaceLeaf(p_225723_, p_272842_, p_225725_, p_225726_, blockpos$mutableblockpos);
      }

   }

   public int foliageHeight(RandomSource p_225719_, int p_225720_, TreeConfiguration p_225721_) {
      return this.foliageHeight.sample(p_225719_);
   }

   protected boolean shouldSkipLocation(RandomSource p_225712_, int p_225713_, int p_225714_, int p_225715_, int p_225716_, boolean p_225717_) {
      return false;
   }
}