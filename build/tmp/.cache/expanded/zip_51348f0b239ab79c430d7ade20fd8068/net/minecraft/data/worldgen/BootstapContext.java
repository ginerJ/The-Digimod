package net.minecraft.data.worldgen;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface BootstapContext<T> {
   Holder.Reference<T> register(ResourceKey<T> p_256008_, T p_256454_, Lifecycle p_255725_);

   default Holder.Reference<T> register(ResourceKey<T> p_255743_, T p_256121_) {
      return this.register(p_255743_, p_256121_, Lifecycle.stable());
   }

   <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> p_256410_);

   default <S> java.util.Optional<net.minecraft.core.HolderLookup.RegistryLookup<S>> registryLookup(ResourceKey<? extends Registry<? extends S>> registry) { return java.util.Optional.empty(); }
}
