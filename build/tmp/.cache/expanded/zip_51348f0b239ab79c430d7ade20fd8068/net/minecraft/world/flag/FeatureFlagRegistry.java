package net.minecraft.world.flag;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class FeatureFlagRegistry {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final FeatureFlagUniverse universe;
   private final Map<ResourceLocation, FeatureFlag> names;
   private final FeatureFlagSet allFlags;

   FeatureFlagRegistry(FeatureFlagUniverse p_249715_, FeatureFlagSet p_249277_, Map<ResourceLocation, FeatureFlag> p_249557_) {
      this.universe = p_249715_;
      this.names = p_249557_;
      this.allFlags = p_249277_;
   }

   public boolean isSubset(FeatureFlagSet p_251939_) {
      return p_251939_.isSubsetOf(this.allFlags);
   }

   public FeatureFlagSet allFlags() {
      return this.allFlags;
   }

   public FeatureFlagSet fromNames(Iterable<ResourceLocation> p_250759_) {
      return this.fromNames(p_250759_, (p_251224_) -> {
         LOGGER.warn("Unknown feature flag: {}", (Object)p_251224_);
      });
   }

   public FeatureFlagSet subset(FeatureFlag... p_252295_) {
      return FeatureFlagSet.create(this.universe, Arrays.asList(p_252295_));
   }

   public FeatureFlagSet fromNames(Iterable<ResourceLocation> p_251769_, Consumer<ResourceLocation> p_251521_) {
      Set<FeatureFlag> set = Sets.newIdentityHashSet();

      for(ResourceLocation resourcelocation : p_251769_) {
         FeatureFlag featureflag = this.names.get(resourcelocation);
         if (featureflag == null) {
            p_251521_.accept(resourcelocation);
         } else {
            set.add(featureflag);
         }
      }

      return FeatureFlagSet.create(this.universe, set);
   }

   public Set<ResourceLocation> toNames(FeatureFlagSet p_251153_) {
      Set<ResourceLocation> set = new HashSet<>();
      this.names.forEach((p_252018_, p_250772_) -> {
         if (p_251153_.contains(p_250772_)) {
            set.add(p_252018_);
         }

      });
      return set;
   }

   public Codec<FeatureFlagSet> codec() {
      return ResourceLocation.CODEC.listOf().comapFlatMap((p_275144_) -> {
         Set<ResourceLocation> set = new HashSet<>();
         FeatureFlagSet featureflagset = this.fromNames(p_275144_, set::add);
         return !set.isEmpty() ? DataResult.error(() -> {
            return "Unknown feature ids: " + set;
         }, featureflagset) : DataResult.success(featureflagset);
      }, (p_249796_) -> {
         return List.copyOf(this.toNames(p_249796_));
      });
   }

   public static class Builder {
      private final FeatureFlagUniverse universe;
      private int id;
      private final Map<ResourceLocation, FeatureFlag> flags = new LinkedHashMap<>();

      public Builder(String p_251576_) {
         this.universe = new FeatureFlagUniverse(p_251576_);
      }

      public FeatureFlag createVanilla(String p_251782_) {
         return this.create(new ResourceLocation("minecraft", p_251782_));
      }

      public FeatureFlag create(ResourceLocation p_250098_) {
         if (this.id >= 64) {
            throw new IllegalStateException("Too many feature flags");
         } else {
            FeatureFlag featureflag = new FeatureFlag(this.universe, this.id++);
            FeatureFlag featureflag1 = this.flags.put(p_250098_, featureflag);
            if (featureflag1 != null) {
               throw new IllegalStateException("Duplicate feature flag " + p_250098_);
            } else {
               return featureflag;
            }
         }
      }

      public FeatureFlagRegistry build() {
         FeatureFlagSet featureflagset = FeatureFlagSet.create(this.universe, this.flags.values());
         return new FeatureFlagRegistry(this.universe, featureflagset, Map.copyOf(this.flags));
      }
   }
}