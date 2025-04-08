package net.minecraft.data.worldgen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseData {
   /** @deprecated */
   @Deprecated
   public static final NormalNoise.NoiseParameters DEFAULT_SHIFT = new NormalNoise.NoiseParameters(-3, 1.0D, 1.0D, 1.0D, 0.0D);

   public static void bootstrap(BootstapContext<NormalNoise.NoiseParameters> p_256579_) {
      registerBiomeNoises(p_256579_, 0, Noises.TEMPERATURE, Noises.VEGETATION, Noises.CONTINENTALNESS, Noises.EROSION);
      registerBiomeNoises(p_256579_, -2, Noises.TEMPERATURE_LARGE, Noises.VEGETATION_LARGE, Noises.CONTINENTALNESS_LARGE, Noises.EROSION_LARGE);
      register(p_256579_, Noises.RIDGE, -7, 1.0D, 2.0D, 1.0D, 0.0D, 0.0D, 0.0D);
      p_256579_.register(Noises.SHIFT, DEFAULT_SHIFT);
      register(p_256579_, Noises.AQUIFER_BARRIER, -3, 1.0D);
      register(p_256579_, Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS, -7, 1.0D);
      register(p_256579_, Noises.AQUIFER_LAVA, -1, 1.0D);
      register(p_256579_, Noises.AQUIFER_FLUID_LEVEL_SPREAD, -5, 1.0D);
      register(p_256579_, Noises.PILLAR, -7, 1.0D, 1.0D);
      register(p_256579_, Noises.PILLAR_RARENESS, -8, 1.0D);
      register(p_256579_, Noises.PILLAR_THICKNESS, -8, 1.0D);
      register(p_256579_, Noises.SPAGHETTI_2D, -7, 1.0D);
      register(p_256579_, Noises.SPAGHETTI_2D_ELEVATION, -8, 1.0D);
      register(p_256579_, Noises.SPAGHETTI_2D_MODULATOR, -11, 1.0D);
      register(p_256579_, Noises.SPAGHETTI_2D_THICKNESS, -11, 1.0D);
      register(p_256579_, Noises.SPAGHETTI_3D_1, -7, 1.0D);
      register(p_256579_, Noises.SPAGHETTI_3D_2, -7, 1.0D);
      register(p_256579_, Noises.SPAGHETTI_3D_RARITY, -11, 1.0D);
      register(p_256579_, Noises.SPAGHETTI_3D_THICKNESS, -8, 1.0D);
      register(p_256579_, Noises.SPAGHETTI_ROUGHNESS, -5, 1.0D);
      register(p_256579_, Noises.SPAGHETTI_ROUGHNESS_MODULATOR, -8, 1.0D);
      register(p_256579_, Noises.CAVE_ENTRANCE, -7, 0.4D, 0.5D, 1.0D);
      register(p_256579_, Noises.CAVE_LAYER, -8, 1.0D);
      register(p_256579_, Noises.CAVE_CHEESE, -8, 0.5D, 1.0D, 2.0D, 1.0D, 2.0D, 1.0D, 0.0D, 2.0D, 0.0D);
      register(p_256579_, Noises.ORE_VEININESS, -8, 1.0D);
      register(p_256579_, Noises.ORE_VEIN_A, -7, 1.0D);
      register(p_256579_, Noises.ORE_VEIN_B, -7, 1.0D);
      register(p_256579_, Noises.ORE_GAP, -5, 1.0D);
      register(p_256579_, Noises.NOODLE, -8, 1.0D);
      register(p_256579_, Noises.NOODLE_THICKNESS, -8, 1.0D);
      register(p_256579_, Noises.NOODLE_RIDGE_A, -7, 1.0D);
      register(p_256579_, Noises.NOODLE_RIDGE_B, -7, 1.0D);
      register(p_256579_, Noises.JAGGED, -16, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D);
      register(p_256579_, Noises.SURFACE, -6, 1.0D, 1.0D, 1.0D);
      register(p_256579_, Noises.SURFACE_SECONDARY, -6, 1.0D, 1.0D, 0.0D, 1.0D);
      register(p_256579_, Noises.CLAY_BANDS_OFFSET, -8, 1.0D);
      register(p_256579_, Noises.BADLANDS_PILLAR, -2, 1.0D, 1.0D, 1.0D, 1.0D);
      register(p_256579_, Noises.BADLANDS_PILLAR_ROOF, -8, 1.0D);
      register(p_256579_, Noises.BADLANDS_SURFACE, -6, 1.0D, 1.0D, 1.0D);
      register(p_256579_, Noises.ICEBERG_PILLAR, -6, 1.0D, 1.0D, 1.0D, 1.0D);
      register(p_256579_, Noises.ICEBERG_PILLAR_ROOF, -3, 1.0D);
      register(p_256579_, Noises.ICEBERG_SURFACE, -6, 1.0D, 1.0D, 1.0D);
      register(p_256579_, Noises.SWAMP, -2, 1.0D);
      register(p_256579_, Noises.CALCITE, -9, 1.0D, 1.0D, 1.0D, 1.0D);
      register(p_256579_, Noises.GRAVEL, -8, 1.0D, 1.0D, 1.0D, 1.0D);
      register(p_256579_, Noises.POWDER_SNOW, -6, 1.0D, 1.0D, 1.0D, 1.0D);
      register(p_256579_, Noises.PACKED_ICE, -7, 1.0D, 1.0D, 1.0D, 1.0D);
      register(p_256579_, Noises.ICE, -4, 1.0D, 1.0D, 1.0D, 1.0D);
      register(p_256579_, Noises.SOUL_SAND_LAYER, -8, 1.0D, 1.0D, 1.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.013333333333333334D);
      register(p_256579_, Noises.GRAVEL_LAYER, -8, 1.0D, 1.0D, 1.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.013333333333333334D);
      register(p_256579_, Noises.PATCH, -5, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.013333333333333334D);
      register(p_256579_, Noises.NETHERRACK, -3, 1.0D, 0.0D, 0.0D, 0.35D);
      register(p_256579_, Noises.NETHER_WART, -3, 1.0D, 0.0D, 0.0D, 0.9D);
      register(p_256579_, Noises.NETHER_STATE_SELECTOR, -4, 1.0D);
   }

   private static void registerBiomeNoises(BootstapContext<NormalNoise.NoiseParameters> p_256503_, int p_236479_, ResourceKey<NormalNoise.NoiseParameters> p_236480_, ResourceKey<NormalNoise.NoiseParameters> p_236481_, ResourceKey<NormalNoise.NoiseParameters> p_236482_, ResourceKey<NormalNoise.NoiseParameters> p_236483_) {
      register(p_256503_, p_236480_, -10 + p_236479_, 1.5D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D);
      register(p_256503_, p_236481_, -8 + p_236479_, 1.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D);
      register(p_256503_, p_236482_, -9 + p_236479_, 1.0D, 1.0D, 2.0D, 2.0D, 2.0D, 1.0D, 1.0D, 1.0D, 1.0D);
      register(p_256503_, p_236483_, -9 + p_236479_, 1.0D, 1.0D, 0.0D, 1.0D, 1.0D);
   }

   private static void register(BootstapContext<NormalNoise.NoiseParameters> p_256150_, ResourceKey<NormalNoise.NoiseParameters> p_255970_, int p_256539_, double p_256566_, double... p_255998_) {
      p_256150_.register(p_255970_, new NormalNoise.NoiseParameters(p_256539_, p_256566_, p_255998_));
   }
}