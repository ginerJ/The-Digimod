package net.minecraft.core;

import java.util.Optional;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public interface HolderGetter<T> {
   Optional<Holder.Reference<T>> get(ResourceKey<T> p_255645_);

   default Holder.Reference<T> getOrThrow(ResourceKey<T> p_255990_) {
      return this.get(p_255990_).orElseThrow(() -> {
         return new IllegalStateException("Missing element " + p_255990_);
      });
   }

   Optional<HolderSet.Named<T>> get(TagKey<T> p_256283_);

   default HolderSet.Named<T> getOrThrow(TagKey<T> p_256125_) {
      return this.get(p_256125_).orElseThrow(() -> {
         return new IllegalStateException("Missing tag " + p_256125_);
      });
   }

   public interface Provider {
      <T> Optional<HolderGetter<T>> lookup(ResourceKey<? extends Registry<? extends T>> p_256648_);

      default <T> HolderGetter<T> lookupOrThrow(ResourceKey<? extends Registry<? extends T>> p_255881_) {
         return this.lookup(p_255881_).orElseThrow(() -> {
            return new IllegalStateException("Registry " + p_255881_.location() + " not found");
         });
      }
   }
}