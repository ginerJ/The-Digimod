package net.minecraft.data.worldgen.placement;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class PlacementUtils {
   public static final PlacementModifier HEIGHTMAP = HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING);
   public static final PlacementModifier HEIGHTMAP_TOP_SOLID = HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG);
   public static final PlacementModifier HEIGHTMAP_WORLD_SURFACE = HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG);
   public static final PlacementModifier HEIGHTMAP_OCEAN_FLOOR = HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR);
   public static final PlacementModifier FULL_RANGE = HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top());
   public static final PlacementModifier RANGE_10_10 = HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(10), VerticalAnchor.belowTop(10));
   public static final PlacementModifier RANGE_8_8 = HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(8), VerticalAnchor.belowTop(8));
   public static final PlacementModifier RANGE_4_4 = HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(4), VerticalAnchor.belowTop(4));
   public static final PlacementModifier RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT = HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(256));

   public static void bootstrap(BootstapContext<PlacedFeature> p_255779_) {
      AquaticPlacements.bootstrap(p_255779_);
      CavePlacements.bootstrap(p_255779_);
      EndPlacements.bootstrap(p_255779_);
      MiscOverworldPlacements.bootstrap(p_255779_);
      NetherPlacements.bootstrap(p_255779_);
      OrePlacements.bootstrap(p_255779_);
      TreePlacements.bootstrap(p_255779_);
      VegetationPlacements.bootstrap(p_255779_);
      VillagePlacements.bootstrap(p_255779_);
   }

   public static ResourceKey<PlacedFeature> createKey(String p_256293_) {
      return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(p_256293_));
   }

   public static void register(BootstapContext<PlacedFeature> p_255872_, ResourceKey<PlacedFeature> p_255820_, Holder<ConfiguredFeature<?, ?>> p_255813_, List<PlacementModifier> p_256042_) {
      p_255872_.register(p_255820_, new PlacedFeature(p_255813_, List.copyOf(p_256042_)));
   }

   public static void register(BootstapContext<PlacedFeature> p_256241_, ResourceKey<PlacedFeature> p_256614_, Holder<ConfiguredFeature<?, ?>> p_255855_, PlacementModifier... p_256413_) {
      register(p_256241_, p_256614_, p_255855_, List.of(p_256413_));
   }

   public static PlacementModifier countExtra(int p_195365_, float p_195366_, int p_195367_) {
      float f = 1.0F / p_195366_;
      if (Math.abs(f - (float)((int)f)) > 1.0E-5F) {
         throw new IllegalStateException("Chance data cannot be represented as list weight");
      } else {
         SimpleWeightedRandomList<IntProvider> simpleweightedrandomlist = SimpleWeightedRandomList.<IntProvider>builder().add(ConstantInt.of(p_195365_), (int)f - 1).add(ConstantInt.of(p_195365_ + p_195367_), 1).build();
         return CountPlacement.of(new WeightedListInt(simpleweightedrandomlist));
      }
   }

   public static PlacementFilter isEmpty() {
      return BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE);
   }

   public static BlockPredicateFilter filteredByBlockSurvival(Block p_206494_) {
      return BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(p_206494_.defaultBlockState(), BlockPos.ZERO));
   }

   public static Holder<PlacedFeature> inlinePlaced(Holder<ConfiguredFeature<?, ?>> p_206507_, PlacementModifier... p_206508_) {
      return Holder.direct(new PlacedFeature(p_206507_, List.of(p_206508_)));
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<PlacedFeature> inlinePlaced(F p_206503_, FC p_206504_, PlacementModifier... p_206505_) {
      return inlinePlaced(Holder.direct(new ConfiguredFeature<>(p_206503_, p_206504_)), p_206505_);
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<PlacedFeature> onlyWhenEmpty(F p_206496_, FC p_206497_) {
      return filtered(p_206496_, p_206497_, BlockPredicate.ONLY_IN_AIR_PREDICATE);
   }

   public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<PlacedFeature> filtered(F p_206499_, FC p_206500_, BlockPredicate p_206501_) {
      return inlinePlaced(p_206499_, p_206500_, BlockPredicateFilter.forPredicate(p_206501_));
   }
}