package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

public class BiomeFilter extends PlacementFilter {
   private static final BiomeFilter INSTANCE = new BiomeFilter();
   public static Codec<BiomeFilter> CODEC = Codec.unit(() -> {
      return INSTANCE;
   });

   private BiomeFilter() {
   }

   public static BiomeFilter biome() {
      return INSTANCE;
   }

   protected boolean shouldPlace(PlacementContext p_226317_, RandomSource p_226318_, BlockPos p_226319_) {
      PlacedFeature placedfeature = p_226317_.topFeature().orElseThrow(() -> {
         return new IllegalStateException("Tried to biome check an unregistered feature, or a feature that should not restrict the biome");
      });
      Holder<Biome> holder = p_226317_.getLevel().getBiome(p_226319_);
      return p_226317_.generator().getBiomeGenerationSettings(holder).hasFeature(placedfeature);
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.BIOME_FILTER;
   }
}