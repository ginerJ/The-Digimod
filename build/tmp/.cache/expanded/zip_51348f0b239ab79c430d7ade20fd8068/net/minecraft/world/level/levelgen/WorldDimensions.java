package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.PrimaryLevelData;

public record WorldDimensions(Registry<LevelStem> dimensions) {
   public static final MapCodec<WorldDimensions> CODEC = RecordCodecBuilder.mapCodec((p_258996_) -> {
      return p_258996_.group(RegistryCodecs.fullCodec(Registries.LEVEL_STEM, Lifecycle.stable(), LevelStem.CODEC).fieldOf("dimensions").forGetter(WorldDimensions::dimensions)).apply(p_258996_, p_258996_.stable(WorldDimensions::new));
   });
   private static final Set<ResourceKey<LevelStem>> BUILTIN_ORDER = ImmutableSet.of(LevelStem.OVERWORLD, LevelStem.NETHER, LevelStem.END);
   private static final int VANILLA_DIMENSION_COUNT = BUILTIN_ORDER.size();

   public WorldDimensions {
      LevelStem levelstem = dimensions.get(LevelStem.OVERWORLD);
      if (levelstem == null) {
         throw new IllegalStateException("Overworld settings missing");
      }
   }

   public static Stream<ResourceKey<LevelStem>> keysInOrder(Stream<ResourceKey<LevelStem>> p_251309_) {
      return Stream.concat(BUILTIN_ORDER.stream(), p_251309_.filter((p_251885_) -> {
         return !BUILTIN_ORDER.contains(p_251885_);
      }));
   }

   public WorldDimensions replaceOverworldGenerator(RegistryAccess p_251390_, ChunkGenerator p_248755_) {
      Registry<DimensionType> registry = p_251390_.registryOrThrow(Registries.DIMENSION_TYPE);
      Registry<LevelStem> registry1 = withOverworld(registry, this.dimensions, p_248755_);
      return new WorldDimensions(registry1);
   }

   public static Registry<LevelStem> withOverworld(Registry<DimensionType> p_248853_, Registry<LevelStem> p_251908_, ChunkGenerator p_251737_) {
      LevelStem levelstem = p_251908_.get(LevelStem.OVERWORLD);
      Holder<DimensionType> holder = (Holder<DimensionType>)(levelstem == null ? p_248853_.getHolderOrThrow(BuiltinDimensionTypes.OVERWORLD) : levelstem.type());
      return withOverworld(p_251908_, holder, p_251737_);
   }

   public static Registry<LevelStem> withOverworld(Registry<LevelStem> p_248907_, Holder<DimensionType> p_251895_, ChunkGenerator p_250220_) {
      WritableRegistry<LevelStem> writableregistry = new MappedRegistry<>(Registries.LEVEL_STEM, Lifecycle.experimental());
      writableregistry.register(LevelStem.OVERWORLD, new LevelStem(p_251895_, p_250220_), Lifecycle.stable());

      for(Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : p_248907_.entrySet()) {
         ResourceKey<LevelStem> resourcekey = entry.getKey();
         if (resourcekey != LevelStem.OVERWORLD) {
            writableregistry.register(resourcekey, entry.getValue(), p_248907_.lifecycle(entry.getValue()));
         }
      }

      return writableregistry.freeze();
   }

   public ChunkGenerator overworld() {
      LevelStem levelstem = this.dimensions.get(LevelStem.OVERWORLD);
      if (levelstem == null) {
         throw new IllegalStateException("Overworld settings missing");
      } else {
         return levelstem.generator();
      }
   }

   public Optional<LevelStem> get(ResourceKey<LevelStem> p_250824_) {
      return this.dimensions.getOptional(p_250824_);
   }

   public ImmutableSet<ResourceKey<Level>> levels() {
      return this.dimensions().entrySet().stream().map(Map.Entry::getKey).map(Registries::levelStemToLevel).collect(ImmutableSet.toImmutableSet());
   }

   public boolean isDebug() {
      return this.overworld() instanceof DebugLevelSource;
   }

   private static PrimaryLevelData.SpecialWorldProperty specialWorldProperty(Registry<LevelStem> p_251549_) {
      return p_251549_.getOptional(LevelStem.OVERWORLD).map((p_251481_) -> {
         ChunkGenerator chunkgenerator = p_251481_.generator();
         if (chunkgenerator instanceof DebugLevelSource) {
            return PrimaryLevelData.SpecialWorldProperty.DEBUG;
         } else {
            return chunkgenerator instanceof FlatLevelSource ? PrimaryLevelData.SpecialWorldProperty.FLAT : PrimaryLevelData.SpecialWorldProperty.NONE;
         }
      }).orElse(PrimaryLevelData.SpecialWorldProperty.NONE);
   }

   static Lifecycle checkStability(ResourceKey<LevelStem> p_250764_, LevelStem p_248865_) {
      return isVanillaLike(p_250764_, p_248865_) ? Lifecycle.stable() : Lifecycle.experimental();
   }

   private static boolean isVanillaLike(ResourceKey<LevelStem> p_250556_, LevelStem p_250034_) {
      if (p_250556_ == LevelStem.OVERWORLD) {
         return isStableOverworld(p_250034_);
      } else if (p_250556_ == LevelStem.NETHER) {
         return isStableNether(p_250034_);
      } else {
         return p_250556_ == LevelStem.END ? isStableEnd(p_250034_) : false;
      }
   }

   private static boolean isStableOverworld(LevelStem p_250762_) {
      Holder<DimensionType> holder = p_250762_.type();
      if (!holder.is(BuiltinDimensionTypes.OVERWORLD) && !holder.is(BuiltinDimensionTypes.OVERWORLD_CAVES)) {
         return false;
      } else {
         BiomeSource biomesource = p_250762_.generator().getBiomeSource();
         if (biomesource instanceof MultiNoiseBiomeSource) {
            MultiNoiseBiomeSource multinoisebiomesource = (MultiNoiseBiomeSource)biomesource;
            if (!multinoisebiomesource.stable(MultiNoiseBiomeSourceParameterLists.OVERWORLD)) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean isStableNether(LevelStem p_250497_) {
      if (p_250497_.type().is(BuiltinDimensionTypes.NETHER)) {
         ChunkGenerator chunkgenerator = p_250497_.generator();
         if (chunkgenerator instanceof NoiseBasedChunkGenerator) {
            NoiseBasedChunkGenerator noisebasedchunkgenerator = (NoiseBasedChunkGenerator)chunkgenerator;
            if (noisebasedchunkgenerator.stable(NoiseGeneratorSettings.NETHER)) {
               BiomeSource biomesource = noisebasedchunkgenerator.getBiomeSource();
               if (biomesource instanceof MultiNoiseBiomeSource) {
                  MultiNoiseBiomeSource multinoisebiomesource = (MultiNoiseBiomeSource)biomesource;
                  if (multinoisebiomesource.stable(MultiNoiseBiomeSourceParameterLists.NETHER)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private static boolean isStableEnd(LevelStem p_250720_) {
      if (p_250720_.type().is(BuiltinDimensionTypes.END)) {
         ChunkGenerator chunkgenerator = p_250720_.generator();
         if (chunkgenerator instanceof NoiseBasedChunkGenerator) {
            NoiseBasedChunkGenerator noisebasedchunkgenerator = (NoiseBasedChunkGenerator)chunkgenerator;
            if (noisebasedchunkgenerator.stable(NoiseGeneratorSettings.END) && noisebasedchunkgenerator.getBiomeSource() instanceof TheEndBiomeSource) {
               return true;
            }
         }
      }

      return false;
   }

   public WorldDimensions.Complete bake(Registry<LevelStem> p_248787_) {
      record Entry(ResourceKey<LevelStem> key, LevelStem value) {
         Lifecycle lifecycle() {
            return WorldDimensions.checkStability(this.key, this.value);
         }
      }
      Stream<ResourceKey<LevelStem>> stream = Stream.concat(p_248787_.registryKeySet().stream(), this.dimensions.registryKeySet().stream()).distinct();
      List<Entry> list = new ArrayList<>();
      keysInOrder(stream).forEach((p_248571_) -> {
         p_248787_.getOptional(p_248571_).or(() -> {
            return this.dimensions.getOptional(p_248571_);
         }).ifPresent((p_250263_) -> {
            list.add(new Entry(p_248571_, p_250263_));
         });
      });
      Lifecycle lifecycle = list.size() == VANILLA_DIMENSION_COUNT ? Lifecycle.stable() : Lifecycle.experimental();
      WritableRegistry<LevelStem> writableregistry = new MappedRegistry<>(Registries.LEVEL_STEM, lifecycle);
      list.forEach((p_259001_) -> {
         writableregistry.register(p_259001_.key, p_259001_.value, p_259001_.lifecycle());
      });
      Registry<LevelStem> registry = writableregistry.freeze();
      PrimaryLevelData.SpecialWorldProperty primaryleveldata$specialworldproperty = specialWorldProperty(registry);
      return new WorldDimensions.Complete(registry.freeze(), primaryleveldata$specialworldproperty);
   }

   public static record Complete(Registry<LevelStem> dimensions, PrimaryLevelData.SpecialWorldProperty specialWorldProperty) {
      public Lifecycle lifecycle() {
         return this.dimensions.registryLifecycle();
      }

      public RegistryAccess.Frozen dimensionsRegistryAccess() {
         return (new RegistryAccess.ImmutableRegistryAccess(List.of(this.dimensions))).freeze();
      }
   }
}