package net.minecraft.data.worldgen.placement;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ClampedInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.NoiseBasedCountPlacement;
import net.minecraft.world.level.levelgen.placement.NoiseThresholdCountPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;

public class VegetationPlacements {
   public static final ResourceKey<PlacedFeature> BAMBOO_LIGHT = PlacementUtils.createKey("bamboo_light");
   public static final ResourceKey<PlacedFeature> BAMBOO = PlacementUtils.createKey("bamboo");
   public static final ResourceKey<PlacedFeature> VINES = PlacementUtils.createKey("vines");
   public static final ResourceKey<PlacedFeature> PATCH_SUNFLOWER = PlacementUtils.createKey("patch_sunflower");
   public static final ResourceKey<PlacedFeature> PATCH_PUMPKIN = PlacementUtils.createKey("patch_pumpkin");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_PLAIN = PlacementUtils.createKey("patch_grass_plain");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_FOREST = PlacementUtils.createKey("patch_grass_forest");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_BADLANDS = PlacementUtils.createKey("patch_grass_badlands");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_SAVANNA = PlacementUtils.createKey("patch_grass_savanna");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_NORMAL = PlacementUtils.createKey("patch_grass_normal");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_TAIGA_2 = PlacementUtils.createKey("patch_grass_taiga_2");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_TAIGA = PlacementUtils.createKey("patch_grass_taiga");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_JUNGLE = PlacementUtils.createKey("patch_grass_jungle");
   public static final ResourceKey<PlacedFeature> GRASS_BONEMEAL = PlacementUtils.createKey("grass_bonemeal");
   public static final ResourceKey<PlacedFeature> PATCH_DEAD_BUSH_2 = PlacementUtils.createKey("patch_dead_bush_2");
   public static final ResourceKey<PlacedFeature> PATCH_DEAD_BUSH = PlacementUtils.createKey("patch_dead_bush");
   public static final ResourceKey<PlacedFeature> PATCH_DEAD_BUSH_BADLANDS = PlacementUtils.createKey("patch_dead_bush_badlands");
   public static final ResourceKey<PlacedFeature> PATCH_MELON = PlacementUtils.createKey("patch_melon");
   public static final ResourceKey<PlacedFeature> PATCH_MELON_SPARSE = PlacementUtils.createKey("patch_melon_sparse");
   public static final ResourceKey<PlacedFeature> PATCH_BERRY_COMMON = PlacementUtils.createKey("patch_berry_common");
   public static final ResourceKey<PlacedFeature> PATCH_BERRY_RARE = PlacementUtils.createKey("patch_berry_rare");
   public static final ResourceKey<PlacedFeature> PATCH_WATERLILY = PlacementUtils.createKey("patch_waterlily");
   public static final ResourceKey<PlacedFeature> PATCH_TALL_GRASS_2 = PlacementUtils.createKey("patch_tall_grass_2");
   public static final ResourceKey<PlacedFeature> PATCH_TALL_GRASS = PlacementUtils.createKey("patch_tall_grass");
   public static final ResourceKey<PlacedFeature> PATCH_LARGE_FERN = PlacementUtils.createKey("patch_large_fern");
   public static final ResourceKey<PlacedFeature> PATCH_CACTUS_DESERT = PlacementUtils.createKey("patch_cactus_desert");
   public static final ResourceKey<PlacedFeature> PATCH_CACTUS_DECORATED = PlacementUtils.createKey("patch_cactus_decorated");
   public static final ResourceKey<PlacedFeature> PATCH_SUGAR_CANE_SWAMP = PlacementUtils.createKey("patch_sugar_cane_swamp");
   public static final ResourceKey<PlacedFeature> PATCH_SUGAR_CANE_DESERT = PlacementUtils.createKey("patch_sugar_cane_desert");
   public static final ResourceKey<PlacedFeature> PATCH_SUGAR_CANE_BADLANDS = PlacementUtils.createKey("patch_sugar_cane_badlands");
   public static final ResourceKey<PlacedFeature> PATCH_SUGAR_CANE = PlacementUtils.createKey("patch_sugar_cane");
   public static final ResourceKey<PlacedFeature> BROWN_MUSHROOM_NETHER = PlacementUtils.createKey("brown_mushroom_nether");
   public static final ResourceKey<PlacedFeature> RED_MUSHROOM_NETHER = PlacementUtils.createKey("red_mushroom_nether");
   public static final ResourceKey<PlacedFeature> BROWN_MUSHROOM_NORMAL = PlacementUtils.createKey("brown_mushroom_normal");
   public static final ResourceKey<PlacedFeature> RED_MUSHROOM_NORMAL = PlacementUtils.createKey("red_mushroom_normal");
   public static final ResourceKey<PlacedFeature> BROWN_MUSHROOM_TAIGA = PlacementUtils.createKey("brown_mushroom_taiga");
   public static final ResourceKey<PlacedFeature> RED_MUSHROOM_TAIGA = PlacementUtils.createKey("red_mushroom_taiga");
   public static final ResourceKey<PlacedFeature> BROWN_MUSHROOM_OLD_GROWTH = PlacementUtils.createKey("brown_mushroom_old_growth");
   public static final ResourceKey<PlacedFeature> RED_MUSHROOM_OLD_GROWTH = PlacementUtils.createKey("red_mushroom_old_growth");
   public static final ResourceKey<PlacedFeature> BROWN_MUSHROOM_SWAMP = PlacementUtils.createKey("brown_mushroom_swamp");
   public static final ResourceKey<PlacedFeature> RED_MUSHROOM_SWAMP = PlacementUtils.createKey("red_mushroom_swamp");
   public static final ResourceKey<PlacedFeature> FLOWER_WARM = PlacementUtils.createKey("flower_warm");
   public static final ResourceKey<PlacedFeature> FLOWER_DEFAULT = PlacementUtils.createKey("flower_default");
   public static final ResourceKey<PlacedFeature> FLOWER_FLOWER_FOREST = PlacementUtils.createKey("flower_flower_forest");
   public static final ResourceKey<PlacedFeature> FLOWER_SWAMP = PlacementUtils.createKey("flower_swamp");
   public static final ResourceKey<PlacedFeature> FLOWER_PLAINS = PlacementUtils.createKey("flower_plains");
   public static final ResourceKey<PlacedFeature> FLOWER_MEADOW = PlacementUtils.createKey("flower_meadow");
   public static final ResourceKey<PlacedFeature> FLOWER_CHERRY = PlacementUtils.createKey("flower_cherry");
   public static final ResourceKey<PlacedFeature> TREES_PLAINS = PlacementUtils.createKey("trees_plains");
   public static final ResourceKey<PlacedFeature> DARK_FOREST_VEGETATION = PlacementUtils.createKey("dark_forest_vegetation");
   public static final ResourceKey<PlacedFeature> FLOWER_FOREST_FLOWERS = PlacementUtils.createKey("flower_forest_flowers");
   public static final ResourceKey<PlacedFeature> FOREST_FLOWERS = PlacementUtils.createKey("forest_flowers");
   public static final ResourceKey<PlacedFeature> TREES_FLOWER_FOREST = PlacementUtils.createKey("trees_flower_forest");
   public static final ResourceKey<PlacedFeature> TREES_MEADOW = PlacementUtils.createKey("trees_meadow");
   public static final ResourceKey<PlacedFeature> TREES_CHERRY = PlacementUtils.createKey("trees_cherry");
   public static final ResourceKey<PlacedFeature> TREES_TAIGA = PlacementUtils.createKey("trees_taiga");
   public static final ResourceKey<PlacedFeature> TREES_GROVE = PlacementUtils.createKey("trees_grove");
   public static final ResourceKey<PlacedFeature> TREES_BADLANDS = PlacementUtils.createKey("trees_badlands");
   public static final ResourceKey<PlacedFeature> TREES_SNOWY = PlacementUtils.createKey("trees_snowy");
   public static final ResourceKey<PlacedFeature> TREES_SWAMP = PlacementUtils.createKey("trees_swamp");
   public static final ResourceKey<PlacedFeature> TREES_WINDSWEPT_SAVANNA = PlacementUtils.createKey("trees_windswept_savanna");
   public static final ResourceKey<PlacedFeature> TREES_SAVANNA = PlacementUtils.createKey("trees_savanna");
   public static final ResourceKey<PlacedFeature> BIRCH_TALL = PlacementUtils.createKey("birch_tall");
   public static final ResourceKey<PlacedFeature> TREES_BIRCH = PlacementUtils.createKey("trees_birch");
   public static final ResourceKey<PlacedFeature> TREES_WINDSWEPT_FOREST = PlacementUtils.createKey("trees_windswept_forest");
   public static final ResourceKey<PlacedFeature> TREES_WINDSWEPT_HILLS = PlacementUtils.createKey("trees_windswept_hills");
   public static final ResourceKey<PlacedFeature> TREES_WATER = PlacementUtils.createKey("trees_water");
   public static final ResourceKey<PlacedFeature> TREES_BIRCH_AND_OAK = PlacementUtils.createKey("trees_birch_and_oak");
   public static final ResourceKey<PlacedFeature> TREES_SPARSE_JUNGLE = PlacementUtils.createKey("trees_sparse_jungle");
   public static final ResourceKey<PlacedFeature> TREES_OLD_GROWTH_SPRUCE_TAIGA = PlacementUtils.createKey("trees_old_growth_spruce_taiga");
   public static final ResourceKey<PlacedFeature> TREES_OLD_GROWTH_PINE_TAIGA = PlacementUtils.createKey("trees_old_growth_pine_taiga");
   public static final ResourceKey<PlacedFeature> TREES_JUNGLE = PlacementUtils.createKey("trees_jungle");
   public static final ResourceKey<PlacedFeature> BAMBOO_VEGETATION = PlacementUtils.createKey("bamboo_vegetation");
   public static final ResourceKey<PlacedFeature> MUSHROOM_ISLAND_VEGETATION = PlacementUtils.createKey("mushroom_island_vegetation");
   public static final ResourceKey<PlacedFeature> TREES_MANGROVE = PlacementUtils.createKey("trees_mangrove");
   private static final PlacementModifier TREE_THRESHOLD = SurfaceWaterDepthFilter.forMaxDepth(0);

   public static List<PlacementModifier> worldSurfaceSquaredWithCount(int p_195475_) {
      return List.of(CountPlacement.of(p_195475_), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
   }

   private static List<PlacementModifier> getMushroomPlacement(int p_195477_, @Nullable PlacementModifier p_195478_) {
      ImmutableList.Builder<PlacementModifier> builder = ImmutableList.builder();
      if (p_195478_ != null) {
         builder.add(p_195478_);
      }

      if (p_195477_ != 0) {
         builder.add(RarityFilter.onAverageOnceEvery(p_195477_));
      }

      builder.add(InSquarePlacement.spread());
      builder.add(PlacementUtils.HEIGHTMAP);
      builder.add(BiomeFilter.biome());
      return builder.build();
   }

   private static ImmutableList.Builder<PlacementModifier> treePlacementBase(PlacementModifier p_195485_) {
      return ImmutableList.<PlacementModifier>builder().add(p_195485_).add(InSquarePlacement.spread()).add(TREE_THRESHOLD).add(PlacementUtils.HEIGHTMAP_OCEAN_FLOOR).add(BiomeFilter.biome());
   }

   public static List<PlacementModifier> treePlacement(PlacementModifier p_195480_) {
      return treePlacementBase(p_195480_).build();
   }

   public static List<PlacementModifier> treePlacement(PlacementModifier p_195482_, Block p_195483_) {
      return treePlacementBase(p_195482_).add(BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(p_195483_.defaultBlockState(), BlockPos.ZERO))).build();
   }

   public static void bootstrap(BootstapContext<PlacedFeature> p_255657_) {
      HolderGetter<ConfiguredFeature<?, ?>> holdergetter = p_255657_.lookup(Registries.CONFIGURED_FEATURE);
      Holder<ConfiguredFeature<?, ?>> holder = holdergetter.getOrThrow(VegetationFeatures.BAMBOO_NO_PODZOL);
      Holder<ConfiguredFeature<?, ?>> holder1 = holdergetter.getOrThrow(VegetationFeatures.BAMBOO_SOME_PODZOL);
      Holder<ConfiguredFeature<?, ?>> holder2 = holdergetter.getOrThrow(VegetationFeatures.VINES);
      Holder<ConfiguredFeature<?, ?>> holder3 = holdergetter.getOrThrow(VegetationFeatures.PATCH_SUNFLOWER);
      Holder<ConfiguredFeature<?, ?>> holder4 = holdergetter.getOrThrow(VegetationFeatures.PATCH_PUMPKIN);
      Holder<ConfiguredFeature<?, ?>> holder5 = holdergetter.getOrThrow(VegetationFeatures.PATCH_GRASS);
      Holder<ConfiguredFeature<?, ?>> holder6 = holdergetter.getOrThrow(VegetationFeatures.PATCH_TAIGA_GRASS);
      Holder<ConfiguredFeature<?, ?>> holder7 = holdergetter.getOrThrow(VegetationFeatures.PATCH_GRASS_JUNGLE);
      Holder<ConfiguredFeature<?, ?>> holder8 = holdergetter.getOrThrow(VegetationFeatures.SINGLE_PIECE_OF_GRASS);
      Holder<ConfiguredFeature<?, ?>> holder9 = holdergetter.getOrThrow(VegetationFeatures.PATCH_DEAD_BUSH);
      Holder<ConfiguredFeature<?, ?>> holder10 = holdergetter.getOrThrow(VegetationFeatures.PATCH_MELON);
      Holder<ConfiguredFeature<?, ?>> holder11 = holdergetter.getOrThrow(VegetationFeatures.PATCH_BERRY_BUSH);
      Holder<ConfiguredFeature<?, ?>> holder12 = holdergetter.getOrThrow(VegetationFeatures.PATCH_WATERLILY);
      Holder<ConfiguredFeature<?, ?>> holder13 = holdergetter.getOrThrow(VegetationFeatures.PATCH_TALL_GRASS);
      Holder<ConfiguredFeature<?, ?>> holder14 = holdergetter.getOrThrow(VegetationFeatures.PATCH_LARGE_FERN);
      Holder<ConfiguredFeature<?, ?>> holder15 = holdergetter.getOrThrow(VegetationFeatures.PATCH_CACTUS);
      Holder<ConfiguredFeature<?, ?>> holder16 = holdergetter.getOrThrow(VegetationFeatures.PATCH_SUGAR_CANE);
      Holder<ConfiguredFeature<?, ?>> holder17 = holdergetter.getOrThrow(VegetationFeatures.PATCH_BROWN_MUSHROOM);
      Holder<ConfiguredFeature<?, ?>> holder18 = holdergetter.getOrThrow(VegetationFeatures.PATCH_RED_MUSHROOM);
      Holder<ConfiguredFeature<?, ?>> holder19 = holdergetter.getOrThrow(VegetationFeatures.FLOWER_DEFAULT);
      Holder<ConfiguredFeature<?, ?>> holder20 = holdergetter.getOrThrow(VegetationFeatures.FLOWER_FLOWER_FOREST);
      Holder<ConfiguredFeature<?, ?>> holder21 = holdergetter.getOrThrow(VegetationFeatures.FLOWER_SWAMP);
      Holder<ConfiguredFeature<?, ?>> holder22 = holdergetter.getOrThrow(VegetationFeatures.FLOWER_PLAIN);
      Holder<ConfiguredFeature<?, ?>> holder23 = holdergetter.getOrThrow(VegetationFeatures.FLOWER_MEADOW);
      Holder<ConfiguredFeature<?, ?>> holder24 = holdergetter.getOrThrow(VegetationFeatures.FLOWER_CHERRY);
      Holder<ConfiguredFeature<?, ?>> holder25 = holdergetter.getOrThrow(VegetationFeatures.TREES_PLAINS);
      Holder<ConfiguredFeature<?, ?>> holder26 = holdergetter.getOrThrow(VegetationFeatures.DARK_FOREST_VEGETATION);
      Holder<ConfiguredFeature<?, ?>> holder27 = holdergetter.getOrThrow(VegetationFeatures.FOREST_FLOWERS);
      Holder<ConfiguredFeature<?, ?>> holder28 = holdergetter.getOrThrow(VegetationFeatures.TREES_FLOWER_FOREST);
      Holder<ConfiguredFeature<?, ?>> holder29 = holdergetter.getOrThrow(VegetationFeatures.MEADOW_TREES);
      Holder<ConfiguredFeature<?, ?>> holder30 = holdergetter.getOrThrow(VegetationFeatures.TREES_TAIGA);
      Holder<ConfiguredFeature<?, ?>> holder31 = holdergetter.getOrThrow(VegetationFeatures.TREES_GROVE);
      Holder<ConfiguredFeature<?, ?>> holder32 = holdergetter.getOrThrow(TreeFeatures.OAK);
      Holder<ConfiguredFeature<?, ?>> holder33 = holdergetter.getOrThrow(TreeFeatures.SPRUCE);
      Holder<ConfiguredFeature<?, ?>> holder34 = holdergetter.getOrThrow(TreeFeatures.CHERRY_BEES_005);
      Holder<ConfiguredFeature<?, ?>> holder35 = holdergetter.getOrThrow(TreeFeatures.SWAMP_OAK);
      Holder<ConfiguredFeature<?, ?>> holder36 = holdergetter.getOrThrow(VegetationFeatures.TREES_SAVANNA);
      Holder<ConfiguredFeature<?, ?>> holder37 = holdergetter.getOrThrow(VegetationFeatures.BIRCH_TALL);
      Holder<ConfiguredFeature<?, ?>> holder38 = holdergetter.getOrThrow(TreeFeatures.BIRCH_BEES_0002);
      Holder<ConfiguredFeature<?, ?>> holder39 = holdergetter.getOrThrow(VegetationFeatures.TREES_WINDSWEPT_HILLS);
      Holder<ConfiguredFeature<?, ?>> holder40 = holdergetter.getOrThrow(VegetationFeatures.TREES_WATER);
      Holder<ConfiguredFeature<?, ?>> holder41 = holdergetter.getOrThrow(VegetationFeatures.TREES_BIRCH_AND_OAK);
      Holder<ConfiguredFeature<?, ?>> holder42 = holdergetter.getOrThrow(VegetationFeatures.TREES_SPARSE_JUNGLE);
      Holder<ConfiguredFeature<?, ?>> holder43 = holdergetter.getOrThrow(VegetationFeatures.TREES_OLD_GROWTH_SPRUCE_TAIGA);
      Holder<ConfiguredFeature<?, ?>> holder44 = holdergetter.getOrThrow(VegetationFeatures.TREES_OLD_GROWTH_PINE_TAIGA);
      Holder<ConfiguredFeature<?, ?>> holder45 = holdergetter.getOrThrow(VegetationFeatures.TREES_JUNGLE);
      Holder<ConfiguredFeature<?, ?>> holder46 = holdergetter.getOrThrow(VegetationFeatures.BAMBOO_VEGETATION);
      Holder<ConfiguredFeature<?, ?>> holder47 = holdergetter.getOrThrow(VegetationFeatures.MUSHROOM_ISLAND_VEGETATION);
      Holder<ConfiguredFeature<?, ?>> holder48 = holdergetter.getOrThrow(VegetationFeatures.MANGROVE_VEGETATION);
      PlacementUtils.register(p_255657_, BAMBOO_LIGHT, holder, RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, BAMBOO, holder1, NoiseBasedCountPlacement.of(160, 80.0D, 0.3D), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, VINES, holder2, CountPlacement.of(127), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(100)), BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_SUNFLOWER, holder3, RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_PUMPKIN, holder4, RarityFilter.onAverageOnceEvery(300), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_GRASS_PLAIN, holder5, NoiseThresholdCountPlacement.of(-0.8D, 5, 10), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_GRASS_FOREST, holder5, worldSurfaceSquaredWithCount(2));
      PlacementUtils.register(p_255657_, PATCH_GRASS_BADLANDS, holder5, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_GRASS_SAVANNA, holder5, worldSurfaceSquaredWithCount(20));
      PlacementUtils.register(p_255657_, PATCH_GRASS_NORMAL, holder5, worldSurfaceSquaredWithCount(5));
      PlacementUtils.register(p_255657_, PATCH_GRASS_TAIGA_2, holder6, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_GRASS_TAIGA, holder6, worldSurfaceSquaredWithCount(7));
      PlacementUtils.register(p_255657_, PATCH_GRASS_JUNGLE, holder7, worldSurfaceSquaredWithCount(25));
      PlacementUtils.register(p_255657_, GRASS_BONEMEAL, holder8, PlacementUtils.isEmpty());
      PlacementUtils.register(p_255657_, PATCH_DEAD_BUSH_2, holder9, worldSurfaceSquaredWithCount(2));
      PlacementUtils.register(p_255657_, PATCH_DEAD_BUSH, holder9, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_DEAD_BUSH_BADLANDS, holder9, worldSurfaceSquaredWithCount(20));
      PlacementUtils.register(p_255657_, PATCH_MELON, holder10, RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_MELON_SPARSE, holder10, RarityFilter.onAverageOnceEvery(64), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_BERRY_COMMON, holder11, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_BERRY_RARE, holder11, RarityFilter.onAverageOnceEvery(384), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_WATERLILY, holder12, worldSurfaceSquaredWithCount(4));
      PlacementUtils.register(p_255657_, PATCH_TALL_GRASS_2, holder13, NoiseThresholdCountPlacement.of(-0.8D, 0, 7), RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_TALL_GRASS, holder13, RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_LARGE_FERN, holder14, RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_CACTUS_DESERT, holder15, RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_CACTUS_DECORATED, holder15, RarityFilter.onAverageOnceEvery(13), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_SUGAR_CANE_SWAMP, holder16, RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_SUGAR_CANE_DESERT, holder16, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_SUGAR_CANE_BADLANDS, holder16, RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, PATCH_SUGAR_CANE, holder16, RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, BROWN_MUSHROOM_NETHER, holder17, RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, RED_MUSHROOM_NETHER, holder18, RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, BROWN_MUSHROOM_NORMAL, holder17, getMushroomPlacement(256, (PlacementModifier)null));
      PlacementUtils.register(p_255657_, RED_MUSHROOM_NORMAL, holder18, getMushroomPlacement(512, (PlacementModifier)null));
      PlacementUtils.register(p_255657_, BROWN_MUSHROOM_TAIGA, holder17, getMushroomPlacement(4, (PlacementModifier)null));
      PlacementUtils.register(p_255657_, RED_MUSHROOM_TAIGA, holder18, getMushroomPlacement(256, (PlacementModifier)null));
      PlacementUtils.register(p_255657_, BROWN_MUSHROOM_OLD_GROWTH, holder17, getMushroomPlacement(4, CountPlacement.of(3)));
      PlacementUtils.register(p_255657_, RED_MUSHROOM_OLD_GROWTH, holder18, getMushroomPlacement(171, (PlacementModifier)null));
      PlacementUtils.register(p_255657_, BROWN_MUSHROOM_SWAMP, holder17, getMushroomPlacement(0, CountPlacement.of(2)));
      PlacementUtils.register(p_255657_, RED_MUSHROOM_SWAMP, holder18, getMushroomPlacement(64, (PlacementModifier)null));
      PlacementUtils.register(p_255657_, FLOWER_WARM, holder19, RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, FLOWER_DEFAULT, holder19, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, FLOWER_FLOWER_FOREST, holder20, CountPlacement.of(3), RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, FLOWER_SWAMP, holder21, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, FLOWER_PLAINS, holder22, NoiseThresholdCountPlacement.of(-0.8D, 15, 4), RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, FLOWER_CHERRY, holder24, NoiseThresholdCountPlacement.of(-0.8D, 5, 10), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, FLOWER_MEADOW, holder23, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementModifier placementmodifier = SurfaceWaterDepthFilter.forMaxDepth(0);
      PlacementUtils.register(p_255657_, TREES_PLAINS, holder25, PlacementUtils.countExtra(0, 0.05F, 1), InSquarePlacement.spread(), placementmodifier, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), BlockPos.ZERO)), BiomeFilter.biome());
      PlacementUtils.register(p_255657_, DARK_FOREST_VEGETATION, holder26, CountPlacement.of(16), InSquarePlacement.spread(), placementmodifier, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, FLOWER_FOREST_FLOWERS, holder27, RarityFilter.onAverageOnceEvery(7), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, CountPlacement.of(ClampedInt.of(UniformInt.of(-1, 3), 0, 3)), BiomeFilter.biome());
      PlacementUtils.register(p_255657_, FOREST_FLOWERS, holder27, RarityFilter.onAverageOnceEvery(7), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, CountPlacement.of(ClampedInt.of(UniformInt.of(-3, 1), 0, 1)), BiomeFilter.biome());
      PlacementUtils.register(p_255657_, TREES_FLOWER_FOREST, holder28, treePlacement(PlacementUtils.countExtra(6, 0.1F, 1)));
      PlacementUtils.register(p_255657_, TREES_MEADOW, holder29, treePlacement(RarityFilter.onAverageOnceEvery(100)));
      PlacementUtils.register(p_255657_, TREES_CHERRY, holder34, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1), Blocks.CHERRY_SAPLING));
      PlacementUtils.register(p_255657_, TREES_TAIGA, holder30, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(p_255657_, TREES_GROVE, holder31, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(p_255657_, TREES_BADLANDS, holder32, treePlacement(PlacementUtils.countExtra(5, 0.1F, 1), Blocks.OAK_SAPLING));
      PlacementUtils.register(p_255657_, TREES_SNOWY, holder33, treePlacement(PlacementUtils.countExtra(0, 0.1F, 1), Blocks.SPRUCE_SAPLING));
      PlacementUtils.register(p_255657_, TREES_SWAMP, holder35, PlacementUtils.countExtra(2, 0.1F, 1), InSquarePlacement.spread(), SurfaceWaterDepthFilter.forMaxDepth(2), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome(), BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), BlockPos.ZERO)));
      PlacementUtils.register(p_255657_, TREES_WINDSWEPT_SAVANNA, holder36, treePlacement(PlacementUtils.countExtra(2, 0.1F, 1)));
      PlacementUtils.register(p_255657_, TREES_SAVANNA, holder36, treePlacement(PlacementUtils.countExtra(1, 0.1F, 1)));
      PlacementUtils.register(p_255657_, BIRCH_TALL, holder37, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(p_255657_, TREES_BIRCH, holder38, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1), Blocks.BIRCH_SAPLING));
      PlacementUtils.register(p_255657_, TREES_WINDSWEPT_FOREST, holder39, treePlacement(PlacementUtils.countExtra(3, 0.1F, 1)));
      PlacementUtils.register(p_255657_, TREES_WINDSWEPT_HILLS, holder39, treePlacement(PlacementUtils.countExtra(0, 0.1F, 1)));
      PlacementUtils.register(p_255657_, TREES_WATER, holder40, treePlacement(PlacementUtils.countExtra(0, 0.1F, 1)));
      PlacementUtils.register(p_255657_, TREES_BIRCH_AND_OAK, holder41, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(p_255657_, TREES_SPARSE_JUNGLE, holder42, treePlacement(PlacementUtils.countExtra(2, 0.1F, 1)));
      PlacementUtils.register(p_255657_, TREES_OLD_GROWTH_SPRUCE_TAIGA, holder43, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(p_255657_, TREES_OLD_GROWTH_PINE_TAIGA, holder44, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(p_255657_, TREES_JUNGLE, holder45, treePlacement(PlacementUtils.countExtra(50, 0.1F, 1)));
      PlacementUtils.register(p_255657_, BAMBOO_VEGETATION, holder46, treePlacement(PlacementUtils.countExtra(30, 0.1F, 1)));
      PlacementUtils.register(p_255657_, MUSHROOM_ISLAND_VEGETATION, holder47, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(p_255657_, TREES_MANGROVE, holder48, CountPlacement.of(25), InSquarePlacement.spread(), SurfaceWaterDepthFilter.forMaxDepth(5), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome(), BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.MANGROVE_PROPAGULE.defaultBlockState(), BlockPos.ZERO)));
   }
}