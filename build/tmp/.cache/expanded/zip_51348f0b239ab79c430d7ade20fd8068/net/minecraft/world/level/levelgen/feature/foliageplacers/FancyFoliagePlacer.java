package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class FancyFoliagePlacer extends BlobFoliagePlacer {
   public static final Codec<FancyFoliagePlacer> CODEC = RecordCodecBuilder.create((p_68518_) -> {
      return blobParts(p_68518_).apply(p_68518_, FancyFoliagePlacer::new);
   });

   public FancyFoliagePlacer(IntProvider p_161397_, IntProvider p_161398_, int p_161399_) {
      super(p_161397_, p_161398_, p_161399_);
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.FANCY_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader p_225582_, FoliagePlacer.FoliageSetter p_273184_, RandomSource p_225584_, TreeConfiguration p_225585_, int p_225586_, FoliagePlacer.FoliageAttachment p_225587_, int p_225588_, int p_225589_, int p_225590_) {
      for(int i = p_225590_; i >= p_225590_ - p_225588_; --i) {
         int j = p_225589_ + (i != p_225590_ && i != p_225590_ - p_225588_ ? 1 : 0);
         this.placeLeavesRow(p_225582_, p_273184_, p_225584_, p_225585_, p_225587_.pos(), j, i, p_225587_.doubleTrunk());
      }

   }

   protected boolean shouldSkipLocation(RandomSource p_225575_, int p_225576_, int p_225577_, int p_225578_, int p_225579_, boolean p_225580_) {
      return Mth.square((float)p_225576_ + 0.5F) + Mth.square((float)p_225578_ + 0.5F) > (float)(p_225579_ * p_225579_);
   }
}