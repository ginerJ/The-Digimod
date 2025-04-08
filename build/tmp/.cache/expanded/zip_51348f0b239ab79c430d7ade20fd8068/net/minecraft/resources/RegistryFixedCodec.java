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

public final class RegistryFixedCodec<E> implements Codec<Holder<E>> {
   private final ResourceKey<? extends Registry<E>> registryKey;

   public static <E> RegistryFixedCodec<E> create(ResourceKey<? extends Registry<E>> p_206741_) {
      return new RegistryFixedCodec<>(p_206741_);
   }

   private RegistryFixedCodec(ResourceKey<? extends Registry<E>> p_206723_) {
      this.registryKey = p_206723_;
   }

   public <T> DataResult<T> encode(Holder<E> p_206729_, DynamicOps<T> p_206730_, T p_206731_) {
      if (p_206730_ instanceof RegistryOps<?> registryops) {
         Optional<HolderOwner<E>> optional = registryops.owner(this.registryKey);
         if (optional.isPresent()) {
            if (!p_206729_.canSerializeIn(optional.get())) {
               return DataResult.error(() -> {
                  return "Element " + p_206729_ + " is not valid in current registry set";
               });
            }

            return p_206729_.unwrap().map((p_206727_) -> {
               return ResourceLocation.CODEC.encode(p_206727_.location(), p_206730_, p_206731_);
            }, (p_274804_) -> {
               return DataResult.error(() -> {
                  return "Elements from registry " + this.registryKey + " can't be serialized to a value";
               });
            });
         }
      }

      return DataResult.error(() -> {
         return "Can't access registry " + this.registryKey;
      });
   }

   public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> p_206743_, T p_206744_) {
      if (p_206743_ instanceof RegistryOps<?> registryops) {
         Optional<HolderGetter<E>> optional = registryops.getter(this.registryKey);
         if (optional.isPresent()) {
            return ResourceLocation.CODEC.decode(p_206743_, p_206744_).flatMap((p_255515_) -> {
               ResourceLocation resourcelocation = p_255515_.getFirst();
               return optional.get().get(ResourceKey.create(this.registryKey, resourcelocation)).map(DataResult::success).orElseGet(() -> {
                  return DataResult.error(() -> {
                     return "Failed to get element " + resourcelocation;
                  });
               }).map((p_256041_) -> {
                  return Pair.of((Holder<E>)p_256041_, (T)p_255515_.getSecond());
               }).setLifecycle(Lifecycle.stable());
            });
         }
      }

      return DataResult.error(() -> {
         return "Can't access registry " + this.registryKey;
      });
   }

   public String toString() {
      return "RegistryFixedCodec[" + this.registryKey + "]";
   }
}