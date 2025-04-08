package net.minecraft.client.gui.screens.worldselection;

import java.util.Map;
import java.util.Optional;
import net.minecraft.client.gui.screens.CreateBuffetWorldScreen;
import net.minecraft.client.gui.screens.CreateFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface PresetEditor {
   /**
    * @deprecated Forge: Use {@link net.minecraftforge.client.PresetEditorManager#get(ResourceKey)} instead.
    */
   @Deprecated
   Map<Optional<ResourceKey<WorldPreset>>, PresetEditor> EDITORS = Map.of(Optional.of(WorldPresets.FLAT), (p_232974_, p_232975_) -> {
      ChunkGenerator chunkgenerator = p_232975_.selectedDimensions().overworld();
      RegistryAccess registryaccess = p_232975_.worldgenLoadContext();
      HolderGetter<Biome> holdergetter = registryaccess.lookupOrThrow(Registries.BIOME);
      HolderGetter<StructureSet> holdergetter1 = registryaccess.lookupOrThrow(Registries.STRUCTURE_SET);
      HolderGetter<PlacedFeature> holdergetter2 = registryaccess.lookupOrThrow(Registries.PLACED_FEATURE);
      return new CreateFlatWorldScreen(p_232974_, (p_267859_) -> {
         p_232974_.getUiState().updateDimensions(flatWorldConfigurator(p_267859_));
      }, chunkgenerator instanceof FlatLevelSource ? ((FlatLevelSource)chunkgenerator).settings() : FlatLevelGeneratorSettings.getDefault(holdergetter, holdergetter1, holdergetter2));
   }, Optional.of(WorldPresets.SINGLE_BIOME_SURFACE), (p_232962_, p_232963_) -> {
      return new CreateBuffetWorldScreen(p_232962_, p_232963_, (p_267861_) -> {
         p_232962_.getUiState().updateDimensions(fixedBiomeConfigurator(p_267861_));
      });
   });

   Screen createEditScreen(CreateWorldScreen p_232977_, WorldCreationContext p_232978_);

   private static WorldCreationContext.DimensionsUpdater flatWorldConfigurator(FlatLevelGeneratorSettings p_250871_) {
      return (p_255454_, p_255455_) -> {
         ChunkGenerator chunkgenerator = new FlatLevelSource(p_250871_);
         return p_255455_.replaceOverworldGenerator(p_255454_, chunkgenerator);
      };
   }

   private static WorldCreationContext.DimensionsUpdater fixedBiomeConfigurator(Holder<Biome> p_248835_) {
      return (p_258137_, p_258138_) -> {
         Registry<NoiseGeneratorSettings> registry = p_258137_.registryOrThrow(Registries.NOISE_SETTINGS);
         Holder<NoiseGeneratorSettings> holder = registry.getHolderOrThrow(NoiseGeneratorSettings.OVERWORLD);
         BiomeSource biomesource = new FixedBiomeSource(p_248835_);
         ChunkGenerator chunkgenerator = new NoiseBasedChunkGenerator(biomesource, holder);
         return p_258138_.replaceOverworldGenerator(p_258137_, chunkgenerator);
      };
   }
}
