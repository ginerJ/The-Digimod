package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;

public final class RegistryFileCodec<E> implements Codec<Holder<E>> {
   private final ResourceKey<? extends Registry<E>> registryKey;
   private final Codec<E> elementCodec;
   private final boolean allowInline;

   public static <E> RegistryFileCodec<E> create(ResourceKey<? extends Registry<E>> p_135590_, Codec<E> p_135591_) {
      return create(p_135590_, p_135591_, true);
   }

   public static <E> RegistryFileCodec<E> create(ResourceKey<? extends Registry<E>> p_135593_, Codec<E> p_135594_, boolean p_135595_) {
      return new RegistryFileCodec<>(p_135593_, p_135594_, p_135595_);
   }

   private RegistryFileCodec(ResourceKey<? extends Registry<E>> p_135574_, Codec<E> p_135575_, boolean p_135576_) {
      this.registryKey = p_135574_;
      this.elementCodec = p_135575_;
      this.allowInline = p_135576_;
   }

   public <T> DataResult<T> encode(Holder<E> p_206716_, DynamicOps<T> p_206717_, T p_206718_) {
      if (p_206717_ instanceof RegistryOps<?> registryops) {
         Optional<HolderOwner<E>> optional = registryops.owner(this.registryKey);
         if (optional.isPresent()) {
            if (!p_206716_.canSerializeIn(optional.get())) {
               return DataResult.error(() -> {
                  return "Element " + p_206716_ + " is not valid in current registry set";
               });
            }

            return p_206716_.unwrap().map((p_206714_) -> {
               return ResourceLocation.CODEC.encode(p_206714_.location(), p_206717_, p_206718_);
            }, (p_206710_) -> {
               return this.elementCodec.encode(p_206710_, p_206717_, p_206718_);
            });
         }
      }

      return this.elementCodec.encode(p_206716_.value(), p_206717_, p_206718_);
   }

   public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> p_135608_, T p_135609_) {
      if (p_135608_ instanceof RegistryOps<?> registryops) {
         Optional<HolderGetter<E>> optional = registryops.getter(this.registryKey);
         if (optional.isEmpty()) {
            return DataResult.error(() -> {
               return "Registry does not exist: " + this.registryKey;
            });
         } else {
            HolderGetter<E> holdergetter = optional.get();
            DataResult<Pair<ResourceLocation, T>> dataresult = ResourceLocation.CODEC.decode(p_135608_, p_135609_);
            if (dataresult.result().isEmpty()) {
               return !this.allowInline ? DataResult.error(() -> {
                  return "Inline definitions not allowed here";
               }) : this.elementCodec.decode(p_135608_, p_135609_).map((p_206720_) -> {
                  return p_206720_.mapFirst(Holder::direct);
               });
            } else {
               Pair<ResourceLocation, T> pair = dataresult.result().get();
               ResourceKey<E> resourcekey = ResourceKey.create(this.registryKey, pair.getFirst());
               return holdergetter.get(resourcekey).map(DataResult::success).orElseGet(() -> {
                  return DataResult.error(() -> {
                     return "Failed to get element " + resourcekey;
                  });
               }).map((p_255658_) -> {
                  return Pair.of((Holder<E>)p_255658_, pair.getSecond());
               }).setLifecycle(Lifecycle.stable());
            }
         }
      } else {
         return this.elementCodec.decode(p_135608_, p_135609_).map((p_214212_) -> {
            return p_214212_.mapFirst(Holder::direct);
         });
      }
   }

   public String toString() {
      return "RegistryFileCodec[" + this.registryKey + " " + this.elementCodec + "]";
   }
}