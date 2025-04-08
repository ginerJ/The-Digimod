package net.minecraft.world.level.levelgen.synth;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;

public class PerlinSimplexNoise {
   private final SimplexNoise[] noiseLevels;
   private final double highestFreqValueFactor;
   private final double highestFreqInputFactor;

   public PerlinSimplexNoise(RandomSource p_230546_, List<Integer> p_230547_) {
      this(p_230546_, new IntRBTreeSet(p_230547_));
   }

   private PerlinSimplexNoise(RandomSource p_230543_, IntSortedSet p_230544_) {
      if (p_230544_.isEmpty()) {
         throw new IllegalArgumentException("Need some octaves!");
      } else {
         int i = -p_230544_.firstInt();
         int j = p_230544_.lastInt();
         int k = i + j + 1;
         if (k < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
         } else {
            SimplexNoise simplexnoise = new SimplexNoise(p_230543_);
            int l = j;
            this.noiseLevels = new SimplexNoise[k];
            if (j >= 0 && j < k && p_230544_.contains(0)) {
               this.noiseLevels[j] = simplexnoise;
            }

            for(int i1 = j + 1; i1 < k; ++i1) {
               if (i1 >= 0 && p_230544_.contains(l - i1)) {
                  this.noiseLevels[i1] = new SimplexNoise(p_230543_);
               } else {
                  p_230543_.consumeCount(262);
               }
            }

            if (j > 0) {
               long k1 = (long)(simplexnoise.getValue(simplexnoise.xo, simplexnoise.yo, simplexnoise.zo) * (double)Long.MAX_VALUE);
               RandomSource randomsource = new WorldgenRandom(new LegacyRandomSource(k1));

               for(int j1 = l - 1; j1 >= 0; --j1) {
                  if (j1 < k && p_230544_.contains(l - j1)) {
                     this.noiseLevels[j1] = new SimplexNoise(randomsource);
                  } else {
                     randomsource.consumeCount(262);
                  }
               }
            }

            this.highestFreqInputFactor = Math.pow(2.0D, (double)j);
            this.highestFreqValueFactor = 1.0D / (Math.pow(2.0D, (double)k) - 1.0D);
         }
      }
   }

   public double getValue(double p_75450_, double p_75451_, boolean p_75452_) {
      double d0 = 0.0D;
      double d1 = this.highestFreqInputFactor;
      double d2 = this.highestFreqValueFactor;

      for(SimplexNoise simplexnoise : this.noiseLevels) {
         if (simplexnoise != null) {
            d0 += simplexnoise.getValue(p_75450_ * d1 + (p_75452_ ? simplexnoise.xo : 0.0D), p_75451_ * d1 + (p_75452_ ? simplexnoise.yo : 0.0D)) * d2;
         }

         d1 /= 2.0D;
         d2 *= 2.0D;
      }

      return d0;
   }
}