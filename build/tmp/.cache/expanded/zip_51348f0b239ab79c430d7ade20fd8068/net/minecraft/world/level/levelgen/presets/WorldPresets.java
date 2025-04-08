package net.minecraft.world.level.levelgen.presets;

import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class WorldPresets {
   public static final ResourceKey<WorldPreset> NORMAL = register("normal");
   public static final ResourceKey<WorldPreset> FLAT = register("flat");
   public static final ResourceKey<WorldPreset> LARGE_BIOMES = register("large_biomes");
   public static final ResourceKey<WorldPreset> AMPLIFIED = register("amplified");
   public static final ResourceKey<WorldPreset> SINGLE_BIOME_SURFACE = register("single_biome_surface");
   public static final ResourceKey<WorldPreset> DEBUG = register("debug_all_block_states");

   public static void bootstrap(BootstapContext<WorldPreset> p_256172_) {
      (new WorldPresets.Bootstrap(p_256172_)).bootstrap();
   }

   private static ResourceKey<WorldPreset> register(String p_226460_) {
      return ResourceKey.create(Registries.WORLD_PRESET, new ResourceLocation(p_226460_));
   }

   public static Optional<ResourceKey<WorldPreset>> fromSettings(Registry<LevelStem> p_249784_) {
      return p_249784_.getOptional(LevelStem.OVERWORLD).flatMap((p_251294_) -> {
         ChunkGenerator chunkgenerator = p_251294_.generator();
         if (chunkgenerator instanceof FlatLevelSource) {
            return Optional.of(FLAT);
         } else {
            return chunkgenerator instanceof DebugLevelSource ? Optional.of(DEBUG) : Optional.empty();
         }
      });
   }

   public static WorldDimensions createNormalWorldDimensions(RegistryAccess p_251732_) {
      return p_251732_.registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(NORMAL).value().createWorldDimensions();
   }

   public static LevelStem getNormalOverworld(RegistryAccess p_226464_) {
      return p_226464_.registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(NORMAL).value().overworld().orElseThrow();
   }

   static class Bootstrap {
      private final BootstapContext<WorldPreset> context;
      private final HolderGetter<NoiseGeneratorSettings> noiseSettings;
      private final HolderGetter<Biome> biomes;
      private final HolderGetter<PlacedFeature> placedFeatures;
      private final HolderGetter<StructureSet> structureSets;
      private final HolderGetter<MultiNoiseBiomeSourceParameterList> multiNoiseBiomeSourceParameterLists;
      private final Holder<DimensionType> overworldDimensionType;
      private final LevelStem netherStem;
      private final LevelStem endStem;

      Bootstrap(BootstapContext<WorldPreset> p_256588_) {
         this.context = p_256588_;
         HolderGetter<DimensionType> holdergetter = p_256588_.lookup(Registries.DIMENSION_TYPE);
         this.noiseSettings = p_256588_.lookup(Registries.NOISE_SETTINGS);
         this.biomes = p_256588_.lookup(Registries.BIOME);
         this.placedFeatures = p_256588_.lookup(Registries.PLACED_FEATURE);
         this.structureSets = p_256588_.lookup(Registries.STRUCTURE_SET);
         this.multiNoiseBiomeSourceParameterLists = p_256588_.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);
         this.overworldDimensionType = holdergetter.getOrThrow(BuiltinDimensionTypes.OVERWORLD);
         Holder<DimensionType> holder = holdergetter.getOrThrow(BuiltinDimensionTypes.NETHER);
         Holder<NoiseGeneratorSettings> holder1 = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.NETHER);
         Holder.Reference<MultiNoiseBiomeSourceParameterList> reference = this.multiNoiseBiomeSourceParameterLists.getOrThrow(MultiNoiseBiomeSourceParameterLists.NETHER);
         this.netherStem = new LevelStem(holder, new NoiseBasedChunkGenerator(MultiNoiseBiomeSource.createFromPreset(reference), holder1));
         Holder<DimensionType> holder2 = holdergetter.getOrThrow(BuiltinDimensionTypes.END);
         Holder<NoiseGeneratorSettings> holder3 = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.END);
         this.endStem = new LevelStem(holder2, new NoiseBasedChunkGenerator(TheEndBiomeSource.create(this.biomes), holder3));
      }

      private LevelStem makeOverworld(ChunkGenerator p_226488_) {
         return new LevelStem(this.overworldDimensionType, p_226488_);
      }

      private LevelStem makeNoiseBasedOverworld(BiomeSource p_226485_, Holder<NoiseGeneratorSettings> p_226486_) {
         return this.makeOverworld(new NoiseBasedChunkGenerator(p_226485_, p_226486_));
      }

      private WorldPreset createPresetWithCustomOverworld(LevelStem p_226490_) {
         return new WorldPreset(Map.of(LevelStem.OVERWORLD, p_226490_, LevelStem.NETHER, this.netherStem, LevelStem.END, this.endStem));
      }

      private void registerCustomOverworldPreset(ResourceKey<WorldPreset> p_256570_, LevelStem p_256269_) {
         this.context.register(p_256570_, this.createPresetWithCustomOverworld(p_256269_));
      }

      private void registerOverworlds(BiomeSource p_273133_) {
         Holder<NoiseGeneratorSettings> holder = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
         this.registerCustomOverworldPreset(WorldPresets.NORMAL, this.makeNoiseBasedOverworld(p_273133_, holder));
         Holder<NoiseGeneratorSettings> holder1 = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.LARGE_BIOMES);
         this.registerCustomOverworldPreset(WorldPresets.LARGE_BIOMES, this.makeNoiseBasedOverworld(p_273133_, holder1));
         Holder<NoiseGeneratorSettings> holder2 = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.AMPLIFIED);
         this.registerCustomOverworldPreset(WorldPresets.AMPLIFIED, this.makeNoiseBasedOverworld(p_273133_, holder2));
      }

      public void bootstrap() {
         Holder.Reference<MultiNoiseBiomeSourceParameterList> reference = this.multiNoiseBiomeSourceParameterLists.getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD);
         this.registerOverworlds(MultiNoiseBiomeSource.createFromPreset(reference));
         Holder<NoiseGeneratorSettings> holder = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
         Holder.Reference<Biome> reference1 = this.biomes.getOrThrow(Biomes.PLAINS);
         this.registerCustomOverworldPreset(WorldPresets.SINGLE_BIOME_SURFACE, this.makeNoiseBasedOverworld(new FixedBiomeSource(reference1), holder));
         this.registerCustomOverworldPreset(WorldPresets.FLAT, this.makeOverworld(new FlatLevelSource(FlatLevelGeneratorSettings.getDefault(this.biomes, this.structureSets, this.placedFeatures))));
         this.registerCustomOverworldPreset(WorldPresets.DEBUG, this.makeOverworld(new DebugLevelSource(reference1)));
      }
   }
}