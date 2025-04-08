package net.minecraft.resources;

import com.google.common.collect.MapMaker;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class ResourceKey<T> implements Comparable<ResourceKey<?>> {
   private static final ConcurrentMap<ResourceKey.InternKey, ResourceKey<?>> VALUES = (new MapMaker()).weakValues().makeMap();
   private final ResourceLocation registryName;
   private final ResourceLocation location;

   public static <T> Codec<ResourceKey<T>> codec(ResourceKey<? extends Registry<T>> p_195967_) {
      return ResourceLocation.CODEC.xmap((p_195979_) -> {
         return create(p_195967_, p_195979_);
      }, ResourceKey::location);
   }

   public static <T> ResourceKey<T> create(ResourceKey<? extends Registry<T>> p_135786_, ResourceLocation p_135787_) {
      return create(p_135786_.location, p_135787_);
   }

   public static <T> ResourceKey<Registry<T>> createRegistryKey(ResourceLocation p_135789_) {
      return create(BuiltInRegistries.ROOT_REGISTRY_NAME, p_135789_);
   }

   private static <T> ResourceKey<T> create(ResourceLocation p_135791_, ResourceLocation p_135792_) {
      return (ResourceKey<T>)VALUES.computeIfAbsent(new ResourceKey.InternKey(p_135791_, p_135792_), (p_258225_) -> {
         return new ResourceKey(p_258225_.registry, p_258225_.location);
      });
   }

   private ResourceKey(ResourceLocation p_135780_, ResourceLocation p_135781_) {
      this.registryName = p_135780_;
      this.location = p_135781_;
   }

   public String toString() {
      return "ResourceKey[" + this.registryName + " / " + this.location + "]";
   }

   public boolean isFor(ResourceKey<? extends Registry<?>> p_135784_) {
      return this.registryName.equals(p_135784_.location());
   }

   public <E> Optional<ResourceKey<E>> cast(ResourceKey<? extends Registry<E>> p_195976_) {
      return this.isFor(p_195976_) ? Optional.of((ResourceKey<E>)this) : Optional.empty();
   }

   public ResourceLocation location() {
      return this.location;
   }

   public ResourceLocation registry() {
      return this.registryName;
   }

   @Override
   public int compareTo(ResourceKey<?> o) {
      int ret = this.registry().compareTo(o.registry());
      if (ret == 0) ret = this.location().compareTo(o.location());
      return ret;
   }

   static record InternKey(ResourceLocation registry, ResourceLocation location) {
   }
}
