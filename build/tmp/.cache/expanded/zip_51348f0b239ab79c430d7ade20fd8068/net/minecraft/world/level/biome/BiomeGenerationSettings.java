package net.minecraft.world.level.biome;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.slf4j.Logger;

public class BiomeGenerationSettings {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final BiomeGenerationSettings EMPTY = new BiomeGenerationSettings(ImmutableMap.of(), ImmutableList.of());
   public static final MapCodec<BiomeGenerationSettings> CODEC = RecordCodecBuilder.mapCodec((p_186655_) -> {
      return p_186655_.group(Codec.simpleMap(GenerationStep.Carving.CODEC, ConfiguredWorldCarver.LIST_CODEC.promotePartial(Util.prefix("Carver: ", LOGGER::error)), StringRepresentable.keys(GenerationStep.Carving.values())).fieldOf("carvers").forGetter((p_186661_) -> {
         return p_186661_.carvers;
      }), PlacedFeature.LIST_OF_LISTS_CODEC.promotePartial(Util.prefix("Features: ", LOGGER::error)).fieldOf("features").forGetter((p_186653_) -> {
         return p_186653_.features;
      })).apply(p_186655_, BiomeGenerationSettings::new);
   });
   private final Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> carvers;
   private final java.util.Set<GenerationStep.Carving> carversView;
   private final List<HolderSet<PlacedFeature>> features;
   private final Supplier<List<ConfiguredFeature<?, ?>>> flowerFeatures;
   private final Supplier<Set<PlacedFeature>> featureSet;

   BiomeGenerationSettings(Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> p_186650_, List<HolderSet<PlacedFeature>> p_186651_) {
      this.carvers = p_186650_;
      this.features = p_186651_;
      this.flowerFeatures = Suppliers.memoize(() -> {
         return p_186651_.stream().flatMap(HolderSet::stream).map(Holder::value).flatMap(PlacedFeature::getFeatures).filter((p_186657_) -> {
            return p_186657_.feature() == Feature.FLOWER;
         }).collect(ImmutableList.toImmutableList());
      });
      this.featureSet = Suppliers.memoize(() -> {
         return p_186651_.stream().flatMap(HolderSet::stream).map(Holder::value).collect(Collectors.toSet());
      });
      this.carversView = java.util.Collections.unmodifiableSet(carvers.keySet());
   }

   public Iterable<Holder<ConfiguredWorldCarver<?>>> getCarvers(GenerationStep.Carving p_204188_) {
      return Objects.requireNonNullElseGet(this.carvers.get(p_204188_), List::of);
   }

   public java.util.Set<GenerationStep.Carving> getCarvingStages() {
       return this.carversView;
   }

   public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
      return this.flowerFeatures.get();
   }

   public List<HolderSet<PlacedFeature>> features() {
      return this.features;
   }

   public boolean hasFeature(PlacedFeature p_186659_) {
      return this.featureSet.get().contains(p_186659_);
   }

   public static class Builder extends BiomeGenerationSettings.PlainBuilder {
      private final HolderGetter<PlacedFeature> placedFeatures;
      private final HolderGetter<ConfiguredWorldCarver<?>> worldCarvers;

      public Builder(HolderGetter<PlacedFeature> p_255774_, HolderGetter<ConfiguredWorldCarver<?>> p_256003_) {
         this.placedFeatures = p_255774_;
         this.worldCarvers = p_256003_;
      }

      public BiomeGenerationSettings.Builder addFeature(GenerationStep.Decoration p_256059_, ResourceKey<PlacedFeature> p_256259_) {
         this.addFeature(p_256059_.ordinal(), this.placedFeatures.getOrThrow(p_256259_));
         return this;
      }

      public BiomeGenerationSettings.Builder addCarver(GenerationStep.Carving p_256471_, ResourceKey<ConfiguredWorldCarver<?>> p_255733_) {
         this.addCarver(p_256471_, this.worldCarvers.getOrThrow(p_255733_));
         return this;
      }
   }

   public static class PlainBuilder {
      protected final Map<GenerationStep.Carving, List<Holder<ConfiguredWorldCarver<?>>>> carvers = Maps.newLinkedHashMap();
      protected final List<List<Holder<PlacedFeature>>> features = Lists.newArrayList();

      public BiomeGenerationSettings.PlainBuilder addFeature(GenerationStep.Decoration p_256360_, Holder<PlacedFeature> p_256577_) {
         return this.addFeature(p_256360_.ordinal(), p_256577_);
      }

      public BiomeGenerationSettings.PlainBuilder addFeature(int p_256305_, Holder<PlacedFeature> p_255636_) {
         this.addFeatureStepsUpTo(p_256305_);
         this.features.get(p_256305_).add(p_255636_);
         return this;
      }

      public BiomeGenerationSettings.PlainBuilder addCarver(GenerationStep.Carving p_256091_, Holder<ConfiguredWorldCarver<?>> p_256082_) {
         this.carvers.computeIfAbsent(p_256091_, (p_256199_) -> {
            return Lists.newArrayList();
         }).add(p_256082_);
         return this;
      }

      protected void addFeatureStepsUpTo(int p_256411_) {
         while(this.features.size() <= p_256411_) {
            this.features.add(Lists.newArrayList());
         }

      }

      public BiomeGenerationSettings build() {
         return new BiomeGenerationSettings(this.carvers.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (p_255831_) -> {
            return HolderSet.direct(p_255831_.getValue());
         })), this.features.stream().map(HolderSet::direct).collect(ImmutableList.toImmutableList()));
      }
   }
}
