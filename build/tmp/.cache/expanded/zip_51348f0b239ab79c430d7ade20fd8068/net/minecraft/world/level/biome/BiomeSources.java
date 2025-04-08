package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public class BiomeSources {
   public static Codec<? extends BiomeSource> bootstrap(Registry<Codec<? extends BiomeSource>> p_220587_) {
      Registry.register(p_220587_, "fixed", FixedBiomeSource.CODEC);
      Registry.register(p_220587_, "multi_noise", MultiNoiseBiomeSource.CODEC);
      Registry.register(p_220587_, "checkerboard", CheckerboardColumnBiomeSource.CODEC);
      return Registry.register(p_220587_, "the_end", TheEndBiomeSource.CODEC);
   }
}