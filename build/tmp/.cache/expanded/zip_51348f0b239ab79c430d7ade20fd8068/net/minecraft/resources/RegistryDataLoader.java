package net.minecraft.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.slf4j.Logger;

public class RegistryDataLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final List<RegistryDataLoader.RegistryData<?>> WORLDGEN_REGISTRIES = List.of(new RegistryDataLoader.RegistryData<>(Registries.DIMENSION_TYPE, DimensionType.DIRECT_CODEC), new RegistryDataLoader.RegistryData<>(Registries.BIOME, Biome.DIRECT_CODEC), new RegistryDataLoader.RegistryData<>(Registries.CHAT_TYPE, ChatType.CODEC), new RegistryDataLoader.RegistryData<>(Registries.CONFIGURED_CARVER, ConfiguredWorldCarver.DIRECT_CODEC), new RegistryDataLoader.RegistryData<>(Registries.CONFIGURED_FEATURE, ConfiguredFeature.DIRECT_CODEC), new RegistryDataLoader.RegistryData<>(Registries.PLACED_FEATURE, PlacedFeature.DIRECT_CODEC), new RegistryDataLoader.RegistryData<>(Registries.STRUCTURE, Structure.DIRECT_CODEC), new RegistryDataLoader.RegistryData<>(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC), new RegistryDataLoader.RegistryData<>(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC), new RegistryDataLoader.RegistryData<>(Registries.TEMPLATE_POOL, StructureTemplatePool.DIRECT_CODEC), new RegistryDataLoader.RegistryData<>(Registries.NOISE_SETTINGS, NoiseGeneratorSettings.DIRECT_CODEC), new RegistryDataLoader.RegistryData<>(Registries.NOISE, NormalNoise.NoiseParameters.DIRECT_CODEC), new RegistryDataLoader.RegistryData<>(Registries.DENSITY_FUNCTION, DensityFunction.DIRECT_CODEC), new RegistryDataLoader.RegistryData<>(Registries.WORLD_PRESET, WorldPreset.DIRECT_CODEC), new RegistryDataLoader.RegistryData<>(Registries.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPreset.DIRECT_CODEC), new RegistryDataLoader.RegistryData<>(Registries.TRIM_PATTERN, TrimPattern.DIRECT_CODEC), new RegistryDataLoader.RegistryData<>(Registries.TRIM_MATERIAL, TrimMaterial.DIRECT_CODEC), new RegistryDataLoader.RegistryData<>(Registries.DAMAGE_TYPE, DamageType.CODEC), new RegistryDataLoader.RegistryData<>(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterList.DIRECT_CODEC));
   public static final List<RegistryDataLoader.RegistryData<?>> DIMENSION_REGISTRIES = List.of(new RegistryDataLoader.RegistryData<>(Registries.LEVEL_STEM, LevelStem.CODEC));

   public static RegistryAccess.Frozen load(ResourceManager p_252046_, RegistryAccess p_249916_, List<RegistryDataLoader.RegistryData<?>> p_250344_) {
      Map<ResourceKey<?>, Exception> map = new HashMap<>();
      List<Pair<WritableRegistry<?>, RegistryDataLoader.Loader>> list = p_250344_.stream().map((p_250249_) -> {
         return p_250249_.create(Lifecycle.stable(), map);
      }).toList();
      RegistryOps.RegistryInfoLookup registryops$registryinfolookup = createContext(p_249916_, list);
      list.forEach((p_255508_) -> {
         p_255508_.getSecond().load(p_252046_, registryops$registryinfolookup);
      });
      list.forEach((p_258223_) -> {
         Registry<?> registry = p_258223_.getFirst();

         try {
            registry.freeze();
         } catch (Exception exception) {
            map.put(registry.key(), exception);
         }

      });
      if (!map.isEmpty()) {
         logErrors(map);
         throw new IllegalStateException("Failed to load registries due to above errors");
      } else {
         return (new RegistryAccess.ImmutableRegistryAccess(list.stream().map(Pair::getFirst).toList())).freeze();
      }
   }

   private static RegistryOps.RegistryInfoLookup createContext(RegistryAccess p_256568_, List<Pair<WritableRegistry<?>, RegistryDataLoader.Loader>> p_255821_) {
      final Map<ResourceKey<? extends Registry<?>>, RegistryOps.RegistryInfo<?>> map = new HashMap<>();
      p_256568_.registries().forEach((p_255505_) -> {
         map.put(p_255505_.key(), createInfoForContextRegistry(p_255505_.value()));
      });
      p_255821_.forEach((p_258221_) -> {
         map.put(p_258221_.getFirst().key(), createInfoForNewRegistry(p_258221_.getFirst()));
      });
      return new RegistryOps.RegistryInfoLookup() {
         public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> p_256014_) {
            return Optional.ofNullable((RegistryOps.RegistryInfo<T>) map.get(p_256014_));
         }
      };
   }

   private static <T> RegistryOps.RegistryInfo<T> createInfoForNewRegistry(WritableRegistry<T> p_256020_) {
      return new RegistryOps.RegistryInfo<>(p_256020_.asLookup(), p_256020_.createRegistrationLookup(), p_256020_.registryLifecycle());
   }

   private static <T> RegistryOps.RegistryInfo<T> createInfoForContextRegistry(Registry<T> p_256230_) {
      return new RegistryOps.RegistryInfo<>(p_256230_.asLookup(), p_256230_.asTagAddingLookup(), p_256230_.registryLifecycle());
   }

   private static void logErrors(Map<ResourceKey<?>, Exception> p_252325_) {
      StringWriter stringwriter = new StringWriter();
      PrintWriter printwriter = new PrintWriter(stringwriter);
      Map<ResourceLocation, Map<ResourceLocation, Exception>> map = p_252325_.entrySet().stream().collect(Collectors.groupingBy((p_249353_) -> {
         return p_249353_.getKey().registry();
      }, Collectors.toMap((p_251444_) -> {
         return p_251444_.getKey().location();
      }, Map.Entry::getValue)));
      map.entrySet().stream().sorted(Entry.comparingByKey()).forEach((p_249838_) -> {
         printwriter.printf("> Errors in registry %s:%n", p_249838_.getKey());
         p_249838_.getValue().entrySet().stream().sorted(Entry.comparingByKey()).forEach((p_250688_) -> {
            printwriter.printf(">> Errors in element %s:%n", p_250688_.getKey());
            p_250688_.getValue().printStackTrace(printwriter);
         });
      });
      printwriter.flush();
      LOGGER.error("Registry loading errors:\n{}", (Object)stringwriter);
   }

   private static String registryDirPath(ResourceLocation p_252033_) {
      return net.minecraftforge.common.ForgeHooks.prefixNamespace(p_252033_); // FORGE: add non-vanilla registry namespace to loader directory, same format as tag directory (see net.minecraft.tags.TagManager#getTagDir(ResourceKey))
   }

   static <E> void loadRegistryContents(RegistryOps.RegistryInfoLookup p_256369_, ResourceManager p_256349_, ResourceKey<? extends Registry<E>> p_255792_, WritableRegistry<E> p_256211_, Decoder<E> p_256232_, Map<ResourceKey<?>, Exception> p_255884_) {
      String s = registryDirPath(p_255792_.location());
      FileToIdConverter filetoidconverter = FileToIdConverter.json(s);
      RegistryOps<JsonElement> registryops = RegistryOps.create(JsonOps.INSTANCE, p_256369_);

      for(Map.Entry<ResourceLocation, Resource> entry : filetoidconverter.listMatchingResources(p_256349_).entrySet()) {
         ResourceLocation resourcelocation = entry.getKey();
         ResourceKey<E> resourcekey = ResourceKey.create(p_255792_, filetoidconverter.fileToId(resourcelocation));
         Resource resource = entry.getValue();

         try (Reader reader = resource.openAsReader()) {
            JsonElement jsonelement = JsonParser.parseReader(reader);
            if (!net.minecraftforge.common.crafting.conditions.ICondition.shouldRegisterEntry(jsonelement)) continue;
            DataResult<E> dataresult = p_256232_.parse(registryops, jsonelement);
            E e = dataresult.getOrThrow(false, (p_248715_) -> {
            });
            p_256211_.register(resourcekey, e, resource.isBuiltin() ? Lifecycle.stable() : dataresult.lifecycle());
         } catch (Exception exception) {
            p_255884_.put(resourcekey, new IllegalStateException(String.format(Locale.ROOT, "Failed to parse %s from pack %s", resourcelocation, resource.sourcePackId()), exception));
         }
      }

   }

   interface Loader {
      void load(ResourceManager p_249926_, RegistryOps.RegistryInfoLookup p_256258_);
   }

   public static record RegistryData<T>(ResourceKey<? extends Registry<T>> key, Codec<T> elementCodec) {
      Pair<WritableRegistry<?>, RegistryDataLoader.Loader> create(Lifecycle p_251662_, Map<ResourceKey<?>, Exception> p_251565_) {
         WritableRegistry<T> writableregistry = new MappedRegistry<>(this.key, p_251662_);
         RegistryDataLoader.Loader registrydataloader$loader = (p_255511_, p_255512_) -> {
            RegistryDataLoader.loadRegistryContents(p_255512_, p_255511_, this.key, writableregistry, this.elementCodec, p_251565_);
         };
         return Pair.of(writableregistry, registrydataloader$loader);
      }
   }
}
