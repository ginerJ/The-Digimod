package net.minecraft.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;

public class RegistryCodecs {
   private static <T> MapCodec<RegistryCodecs.RegistryEntry<T>> withNameAndId(ResourceKey<? extends Registry<T>> p_206304_, MapCodec<T> p_206305_) {
      return RecordCodecBuilder.mapCodec((p_206309_) -> {
         return p_206309_.group(ResourceKey.codec(p_206304_).fieldOf("name").forGetter(RegistryCodecs.RegistryEntry::key), Codec.INT.fieldOf("id").forGetter(RegistryCodecs.RegistryEntry::id), p_206305_.forGetter(RegistryCodecs.RegistryEntry::value)).apply(p_206309_, RegistryCodecs.RegistryEntry::new);
      });
   }

   public static <T> Codec<Registry<T>> networkCodec(ResourceKey<? extends Registry<T>> p_206292_, Lifecycle p_206293_, Codec<T> p_206294_) {
      return withNameAndId(p_206292_, p_206294_.fieldOf("element")).codec().listOf().xmap((p_258188_) -> {
         WritableRegistry<T> writableregistry = new MappedRegistry<>(p_206292_, p_206293_);

         for(RegistryCodecs.RegistryEntry<T> registryentry : p_258188_) {
            writableregistry.registerMapping(registryentry.id(), registryentry.key(), registryentry.value(), p_206293_);
         }

         return writableregistry;
      }, (p_258185_) -> {
         ImmutableList.Builder<RegistryCodecs.RegistryEntry<T>> builder = ImmutableList.builder();

         for(T t : p_258185_) {
            builder.add(new RegistryCodecs.RegistryEntry<>(p_258185_.getResourceKey(t).get(), p_258185_.getId(t), t));
         }

         return builder.build();
      });
   }

   public static <E> Codec<Registry<E>> fullCodec(ResourceKey<? extends Registry<E>> p_248884_, Lifecycle p_251810_, Codec<E> p_250169_) {
      // FORGE: Fix MC-197860
      Codec<Map<ResourceKey<E>, E>> codec = new net.minecraftforge.common.LenientUnboundedMapCodec<>(ResourceKey.codec(p_248884_), p_250169_);
      return codec.xmap((p_258184_) -> {
         WritableRegistry<E> writableregistry = new MappedRegistry<>(p_248884_, p_251810_);
         p_258184_.forEach((p_258191_, p_258192_) -> {
            writableregistry.register(p_258191_, p_258192_, p_251810_);
         });
         return writableregistry.freeze();
      }, (p_258193_) -> {
         return ImmutableMap.copyOf(p_258193_.entrySet());
      });
   }

   public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> p_206280_, Codec<E> p_206281_) {
      return homogeneousList(p_206280_, p_206281_, false);
   }

   public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> p_206288_, Codec<E> p_206289_, boolean p_206290_) {
      return HolderSetCodec.create(p_206288_, RegistryFileCodec.create(p_206288_, p_206289_), p_206290_);
   }

   public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> p_206278_) {
      return homogeneousList(p_206278_, false);
   }

   public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> p_206311_, boolean p_206312_) {
      return HolderSetCodec.create(p_206311_, RegistryFixedCodec.create(p_206311_), p_206312_);
   }

   static record RegistryEntry<T>(ResourceKey<T> key, int id, T value) {
   }
}
