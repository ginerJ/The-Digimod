package net.minecraft.util.valueproviders;

import java.util.Arrays;
import net.minecraft.util.RandomSource;

public class MultipliedFloats implements SampledFloat {
   private final SampledFloat[] values;

   public MultipliedFloats(SampledFloat... p_216858_) {
      this.values = p_216858_;
   }

   public float sample(RandomSource p_216860_) {
      float f = 1.0F;

      for(int i = 0; i < this.values.length; ++i) {
         f *= this.values[i].sample(p_216860_);
      }

      return f;
   }

   public String toString() {
      return "MultipliedFloats" + Arrays.toString((Object[])this.values);
   }
}