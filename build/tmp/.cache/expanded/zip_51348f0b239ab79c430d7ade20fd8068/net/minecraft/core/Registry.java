package net.minecraft.core;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public interface Registry<T> extends Keyable, IdMap<T> {
   ResourceKey<? extends Registry<T>> key();

   default Codec<T> byNameCodec() {
      Codec<T> codec = ResourceLocation.CODEC.flatXmap((p_258170_) -> {
         return Optional.ofNullable(this.get(p_258170_)).map(DataResult::success).orElseGet(() -> {
            return DataResult.error(() -> {
               return "Unknown registry key in " + this.key() + ": " + p_258170_;
            });
         });
      }, (p_258177_) -> {
         return this.getResourceKey(p_258177_).map(ResourceKey::location).map(DataResult::success).orElseGet(() -> {
            return DataResult.error(() -> {
               return "Unknown registry element in " + this.key() + ":" + p_258177_;
            });
         });
      });
      Codec<T> codec1 = ExtraCodecs.idResolverCodec((p_258179_) -> {
         return this.getResourceKey(p_258179_).isPresent() ? this.getId(p_258179_) : -1;
      }, this::byId, -1);
      return ExtraCodecs.overrideLifecycle(ExtraCodecs.orCompressed(codec, codec1), this::lifecycle, this::lifecycle);
   }

   default Codec<Holder<T>> holderByNameCodec() {
      Codec<Holder<T>> codec = ResourceLocation.CODEC.flatXmap((p_258174_) -> {
         return this.getHolder(ResourceKey.create(this.key(), p_258174_)).map(DataResult::success).orElseGet(() -> {
            return DataResult.error(() -> {
               return "Unknown registry key in " + this.key() + ": " + p_258174_;
            });
         });
      }, (p_206061_) -> {
         return p_206061_.unwrapKey().map(ResourceKey::location).map(DataResult::success).orElseGet(() -> {
            return DataResult.error(() -> {
               return "Unknown registry element in " + this.key() + ":" + p_206061_;
            });
         });
      });
      return ExtraCodecs.overrideLifecycle(codec, (p_258178_) -> {
         return this.lifecycle(p_258178_.value());
      }, (p_258171_) -> {
         return this.lifecycle(p_258171_.value());
      });
   }

   default <U> Stream<U> keys(DynamicOps<U> p_123030_) {
      return this.keySet().stream().map((p_235784_) -> {
         return p_123030_.createString(p_235784_.toString());
      });
   }

   @Nullable
   ResourceLocation getKey(T p_123006_);

   Optional<ResourceKey<T>> getResourceKey(T p_123008_);

   int getId(@Nullable T p_122977_);

   @Nullable
   T get(@Nullable ResourceKey<T> p_122980_);

   @Nullable
   T get(@Nullable ResourceLocation p_123002_);

   Lifecycle lifecycle(T p_123012_);

   Lifecycle registryLifecycle();

   default Optional<T> getOptional(@Nullable ResourceLocation p_123007_) {
      return Optional.ofNullable(this.get(p_123007_));
   }

   default Optional<T> getOptional(@Nullable ResourceKey<T> p_123010_) {
      return Optional.ofNullable(this.get(p_123010_));
   }

   default T getOrThrow(ResourceKey<T> p_123014_) {
      T t = this.get(p_123014_);
      if (t == null) {
         throw new IllegalStateException("Missing key in " + this.key() + ": " + p_123014_);
      } else {
         return t;
      }
   }

   Set<ResourceLocation> keySet();

   Set<Map.Entry<ResourceKey<T>, T>> entrySet();

   Set<ResourceKey<T>> registryKeySet();

   Optional<Holder.Reference<T>> getRandom(RandomSource p_235781_);

   default Stream<T> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   boolean containsKey(ResourceLocation p_123011_);

   boolean containsKey(ResourceKey<T> p_175475_);

   static <T> T register(Registry<? super T> p_122962_, String p_122963_, T p_122964_) {
      return register(p_122962_, new ResourceLocation(p_122963_), p_122964_);
   }

   static <V, T extends V> T register(Registry<V> p_122966_, ResourceLocation p_122967_, T p_122968_) {
      return register(p_122966_, ResourceKey.create(p_122966_.key(), p_122967_), p_122968_);
   }

   static <V, T extends V> T register(Registry<V> p_194580_, ResourceKey<V> p_194581_, T p_194582_) {
      ((WritableRegistry)p_194580_).register(p_194581_, (V)p_194582_, Lifecycle.stable());
      return p_194582_;
   }

   static <T> Holder.Reference<T> registerForHolder(Registry<T> p_263347_, ResourceKey<T> p_263355_, T p_263428_) {
      return ((WritableRegistry)p_263347_).register(p_263355_, p_263428_, Lifecycle.stable());
   }

   static <T> Holder.Reference<T> registerForHolder(Registry<T> p_263351_, ResourceLocation p_263363_, T p_263423_) {
      return registerForHolder(p_263351_, ResourceKey.create(p_263351_.key(), p_263363_), p_263423_);
   }

   static <V, T extends V> T registerMapping(Registry<V> p_122957_, int p_122958_, String p_122959_, T p_122960_) {
      ((WritableRegistry)p_122957_).registerMapping(p_122958_, ResourceKey.create(p_122957_.key(), new ResourceLocation(p_122959_)), (V)p_122960_, Lifecycle.stable());
      return p_122960_;
   }

   Registry<T> freeze();

   Holder.Reference<T> createIntrusiveHolder(T p_206068_);

   Optional<Holder.Reference<T>> getHolder(int p_206051_);

   Optional<Holder.Reference<T>> getHolder(ResourceKey<T> p_206050_);

   Holder<T> wrapAsHolder(T p_263382_);

   default Holder.Reference<T> getHolderOrThrow(ResourceKey<T> p_249087_) {
      return this.getHolder(p_249087_).orElseThrow(() -> {
         return new IllegalStateException("Missing key in " + this.key() + ": " + p_249087_);
      });
   }

   Stream<Holder.Reference<T>> holders();

   Optional<HolderSet.Named<T>> getTag(TagKey<T> p_206052_);

   default Iterable<Holder<T>> getTagOrEmpty(TagKey<T> p_206059_) {
      return DataFixUtils.orElse(this.getTag(p_206059_), List.of());
   }

   HolderSet.Named<T> getOrCreateTag(TagKey<T> p_206045_);

   Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags();

   Stream<TagKey<T>> getTagNames();

   void resetTags();

   void bindTags(Map<TagKey<T>, List<Holder<T>>> p_205997_);

   default IdMap<Holder<T>> asHolderIdMap() {
      return new IdMap<Holder<T>>() {
         public int getId(Holder<T> p_259992_) {
            return Registry.this.getId(p_259992_.value());
         }

         @Nullable
         public Holder<T> byId(int p_259972_) {
            return (Holder)Registry.this.getHolder(p_259972_).orElse(null);
         }

         public int size() {
            return Registry.this.size();
         }

         public Iterator<Holder<T>> iterator() {
            return Registry.this.holders().map((p_260061_) -> {
               return (Holder<T>)p_260061_;
            }).iterator();
         }
      };
   }

   HolderOwner<T> holderOwner();

   HolderLookup.RegistryLookup<T> asLookup();

   default HolderLookup.RegistryLookup<T> asTagAddingLookup() {
      return new HolderLookup.RegistryLookup.Delegate<T>() {
         protected HolderLookup.RegistryLookup<T> parent() {
            return Registry.this.asLookup();
         }

         public Optional<HolderSet.Named<T>> get(TagKey<T> p_259111_) {
            return Optional.of(this.getOrThrow(p_259111_));
         }

         public HolderSet.Named<T> getOrThrow(TagKey<T> p_259653_) {
            return Registry.this.getOrCreateTag(p_259653_);
         }
      };
   }
}