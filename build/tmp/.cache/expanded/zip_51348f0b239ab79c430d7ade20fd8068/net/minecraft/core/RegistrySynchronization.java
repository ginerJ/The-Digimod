package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

public class RegistrySynchronization {
   private static final Map<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> NETWORKABLE_REGISTRIES = Util.make(() -> {
      ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> builder = ImmutableMap.builder();
      put(builder, Registries.BIOME, Biome.NETWORK_CODEC);
      put(builder, Registries.CHAT_TYPE, ChatType.CODEC);
      put(builder, Registries.TRIM_PATTERN, TrimPattern.DIRECT_CODEC);
      put(builder, Registries.TRIM_MATERIAL, TrimMaterial.DIRECT_CODEC);
      put(builder, Registries.DIMENSION_TYPE, DimensionType.DIRECT_CODEC);
      put(builder, Registries.DAMAGE_TYPE, DamageType.CODEC);
      return net.minecraftforge.registries.DataPackRegistriesHooks.grabNetworkableRegistries(builder); // FORGE: Keep the map so custom registries can be added later
   });
   public static final Codec<RegistryAccess> NETWORK_CODEC = makeNetworkCodec();

   private static <E> void put(ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> p_251643_, ResourceKey<? extends Registry<E>> p_249195_, Codec<E> p_249212_) {
      p_251643_.put(p_249195_, new RegistrySynchronization.NetworkedRegistryData<>(p_249195_, p_249212_));
   }

   private static Stream<RegistryAccess.RegistryEntry<?>> ownedNetworkableRegistries(RegistryAccess p_251842_) {
      return p_251842_.registries().filter((p_250129_) -> {
         return NETWORKABLE_REGISTRIES.containsKey(p_250129_.key());
      });
   }

   private static <E> DataResult<? extends Codec<E>> getNetworkCodec(ResourceKey<? extends Registry<E>> p_252190_) {
      return Optional.ofNullable(NETWORKABLE_REGISTRIES.get(p_252190_)).map((p_250582_) -> {
         return (Codec<E>)p_250582_.networkCodec();
      }).map(DataResult::success).orElseGet(() -> {
         return DataResult.error(() -> {
            return "Unknown or not serializable registry: " + p_252190_;
         });
      });
   }

   private static <E> Codec<RegistryAccess> makeNetworkCodec() {
      Codec<ResourceKey<? extends Registry<E>>> codec = ResourceLocation.CODEC.xmap(ResourceKey::createRegistryKey, ResourceKey::location);
      Codec<Registry<E>> codec1 = codec.partialDispatch("type", (p_258198_) -> {
         return DataResult.success(p_258198_.key());
      }, (p_250682_) -> {
         return getNetworkCodec(p_250682_).map((p_252116_) -> {
            return RegistryCodecs.networkCodec(p_250682_, Lifecycle.experimental(), p_252116_);
         });
      });
      UnboundedMapCodec<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> unboundedmapcodec = Codec.unboundedMap(codec, codec1);
      return captureMap(unboundedmapcodec);
   }

   private static <K extends ResourceKey<? extends Registry<?>>, V extends Registry<?>> Codec<RegistryAccess> captureMap(UnboundedMapCodec<K, V> p_249934_) {
      return p_249934_.xmap(RegistryAccess.ImmutableRegistryAccess::new, (p_251578_) -> {
         return ownedNetworkableRegistries(p_251578_).collect(ImmutableMap.toImmutableMap((p_250395_) -> {
            return (K)p_250395_.key();
         }, (p_248951_) -> {
            return (V)p_248951_.value();
         }));
      });
   }

   public static Stream<RegistryAccess.RegistryEntry<?>> networkedRegistries(LayeredRegistryAccess<RegistryLayer> p_259290_) {
      return ownedNetworkableRegistries(p_259290_.getAccessFrom(RegistryLayer.WORLDGEN));
   }

   public static Stream<RegistryAccess.RegistryEntry<?>> networkSafeRegistries(LayeredRegistryAccess<RegistryLayer> p_249066_) {
      Stream<RegistryAccess.RegistryEntry<?>> stream = p_249066_.getLayer(RegistryLayer.STATIC).registries();
      Stream<RegistryAccess.RegistryEntry<?>> stream1 = networkedRegistries(p_249066_);
      return Stream.concat(stream1, stream);
   }

   public static record NetworkedRegistryData<E>(ResourceKey<? extends Registry<E>> key, Codec<E> networkCodec) {
   }
}
