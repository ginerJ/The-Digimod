package net.minecraft.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public interface DefaultedRegistry<T> extends Registry<T> {
   @Nonnull
   ResourceLocation getKey(T p_122330_);

   @Nonnull
   T get(@Nullable ResourceLocation p_122328_);

   @Nonnull
   T byId(int p_122317_);

   ResourceLocation getDefaultKey();
}