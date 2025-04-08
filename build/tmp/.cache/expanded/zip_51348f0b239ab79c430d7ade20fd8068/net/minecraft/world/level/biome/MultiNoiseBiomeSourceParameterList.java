package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class MultiNoiseBiomeSourceParameterList {
   public static final Codec<MultiNoiseBiomeSourceParameterList> DIRECT_CODEC = RecordCodecBuilder.create((p_275469_) -> {
      return p_275469_.group(MultiNoiseBiomeSourceParameterList.Preset.CODEC.fieldOf("preset").forGetter((p_275196_) -> {
         return p_275196_.preset;
      }), RegistryOps.retrieveGetter(Registries.BIOME)).apply(p_275469_, MultiNoiseBiomeSourceParameterList::new);
   });
   public static final Codec<Holder<MultiNoiseBiomeSourceParameterList>> CODEC = RegistryFileCodec.create(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, DIRECT_CODEC);
   private final MultiNoiseBiomeSourceParameterList.Preset preset;
   private final Climate.ParameterList<Holder<Biome>> parameters;

   public MultiNoiseBiomeSourceParameterList(MultiNoiseBiomeSourceParameterList.Preset p_275275_, HolderGetter<Biome> p_275192_) {
      this.preset = p_275275_;
      this.parameters = p_275275_.provider.apply(p_275192_::getOrThrow);
   }

   public Climate.ParameterList<Holder<Biome>> parameters() {
      return this.parameters;
   }

   public static Map<MultiNoiseBiomeSourceParameterList.Preset, Climate.ParameterList<ResourceKey<Biome>>> knownPresets() {
      return MultiNoiseBiomeSourceParameterList.Preset.BY_NAME.values().stream().collect(Collectors.toMap((p_275210_) -> {
         return p_275210_;
      }, (p_275480_) -> {
         return p_275480_.provider().apply((p_275406_) -> {
            return p_275406_;
         });
      }));
   }

   public static record Preset(ResourceLocation id, MultiNoiseBiomeSourceParameterList.Preset.SourceProvider provider) {
      public static final MultiNoiseBiomeSourceParameterList.Preset NETHER = new MultiNoiseBiomeSourceParameterList.Preset(new ResourceLocation("nether"), new MultiNoiseBiomeSourceParameterList.Preset.SourceProvider() {
         public <T> Climate.ParameterList<T> apply(Function<ResourceKey<Biome>, T> p_275356_) {
            return new Climate.ParameterList<>(List.of(Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), p_275356_.apply(Biomes.NETHER_WASTES)), Pair.of(Climate.parameters(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), p_275356_.apply(Biomes.SOUL_SAND_VALLEY)), Pair.of(Climate.parameters(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), p_275356_.apply(Biomes.CRIMSON_FOREST)), Pair.of(Climate.parameters(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), p_275356_.apply(Biomes.WARPED_FOREST)), Pair.of(Climate.parameters(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), p_275356_.apply(Biomes.BASALT_DELTAS))));
         }
      });
      public static final MultiNoiseBiomeSourceParameterList.Preset OVERWORLD = new MultiNoiseBiomeSourceParameterList.Preset(new ResourceLocation("overworld"), new MultiNoiseBiomeSourceParameterList.Preset.SourceProvider() {
         public <T> Climate.ParameterList<T> apply(Function<ResourceKey<Biome>, T> p_275530_) {
            return MultiNoiseBiomeSourceParameterList.Preset.generateOverworldBiomes(p_275530_);
         }
      });
      static final Map<ResourceLocation, MultiNoiseBiomeSourceParameterList.Preset> BY_NAME = Stream.of(NETHER, OVERWORLD).collect(Collectors.toMap(MultiNoiseBiomeSourceParameterList.Preset::id, (p_275365_) -> {
         return p_275365_;
      }));
      public static final Codec<MultiNoiseBiomeSourceParameterList.Preset> CODEC = ResourceLocation.CODEC.flatXmap((p_275567_) -> {
         return Optional.ofNullable(BY_NAME.get(p_275567_)).map(DataResult::success).orElseGet(() -> {
            return DataResult.error(() -> {
               return "Unknown preset: " + p_275567_;
            });
         });
      }, (p_275325_) -> {
         return DataResult.success(p_275325_.id);
      });

      static <T> Climate.ParameterList<T> generateOverworldBiomes(Function<ResourceKey<Biome>, T> p_277826_) {
         ImmutableList.Builder<Pair<Climate.ParameterPoint, T>> builder = ImmutableList.builder();
         (new OverworldBiomeBuilder()).addBiomes((p_275579_) -> {
            builder.add(p_275579_.mapSecond(p_277826_));
         });
         return new Climate.ParameterList<>(builder.build());
      }

      public Stream<ResourceKey<Biome>> usedBiomes() {
         return this.provider.apply((p_275429_) -> {
            return p_275429_;
         }).values().stream().map(Pair::getSecond).distinct();
      }

      @FunctionalInterface
      interface SourceProvider {
         <T> Climate.ParameterList<T> apply(Function<ResourceKey<Biome>, T> p_275485_);
      }
   }
}