package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;

public interface RegistryAccess extends HolderLookup.Provider {
   Logger LOGGER = LogUtils.getLogger();
   RegistryAccess.Frozen EMPTY = (new RegistryAccess.ImmutableRegistryAccess(Map.of())).freeze();

   <E> Optional<Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> p_123085_);

   default <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> p_256275_) {
      return this.registry(p_256275_).map(Registry::asLookup);
   }

   default <E> Registry<E> registryOrThrow(ResourceKey<? extends Registry<? extends E>> p_175516_) {
      return this.registry(p_175516_).orElseThrow(() -> {
         return new IllegalStateException("Missing registry: " + p_175516_);
      });
   }

   Stream<RegistryAccess.RegistryEntry<?>> registries();

   static RegistryAccess.Frozen fromRegistryOfRegistries(final Registry<? extends Registry<?>> p_206166_) {
      return new RegistryAccess.Frozen() {
         public <T> Optional<Registry<T>> registry(ResourceKey<? extends Registry<? extends T>> p_206220_) {
            Registry<Registry<T>> registry = (Registry<Registry<T>>) p_206166_;
            return registry.getOptional((ResourceKey<Registry<T>>) p_206220_);
         }

         public Stream<RegistryAccess.RegistryEntry<?>> registries() {
            return p_206166_.entrySet().stream().map(RegistryAccess.RegistryEntry::fromMapEntry);
         }

         public RegistryAccess.Frozen freeze() {
            return this;
         }
      };
   }

   default RegistryAccess.Frozen freeze() {
      class FrozenAccess extends RegistryAccess.ImmutableRegistryAccess implements RegistryAccess.Frozen {
         protected FrozenAccess(Stream<RegistryAccess.RegistryEntry<?>> p_252031_) {
            super(p_252031_);
         }
      }

      return new FrozenAccess(this.registries().map(RegistryAccess.RegistryEntry::freeze));
   }

   default Lifecycle allRegistriesLifecycle() {
      return this.registries().map((p_258181_) -> {
         return p_258181_.value.registryLifecycle();
      }).reduce(Lifecycle.stable(), Lifecycle::add);
   }

   public interface Frozen extends RegistryAccess {
   }

   public static class ImmutableRegistryAccess implements RegistryAccess {
      private final Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries;

      public ImmutableRegistryAccess(List<? extends Registry<?>> p_248540_) {
         this.registries = p_248540_.stream().collect(Collectors.toUnmodifiableMap(Registry::key, (p_206232_) -> {
            return p_206232_;
         }));
      }

      public ImmutableRegistryAccess(Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> p_206225_) {
         this.registries = Map.copyOf(p_206225_);
      }

      public ImmutableRegistryAccess(Stream<RegistryAccess.RegistryEntry<?>> p_206227_) {
         this.registries = p_206227_.collect(ImmutableMap.toImmutableMap(RegistryAccess.RegistryEntry::key, RegistryAccess.RegistryEntry::value));
      }

      public <E> Optional<Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> p_206229_) {
         return Optional.ofNullable(this.registries.get(p_206229_)).map((p_247993_) -> {
            return (Registry<E>) p_247993_;
         });
      }

      public Stream<RegistryAccess.RegistryEntry<?>> registries() {
         return this.registries.entrySet().stream().map(RegistryAccess.RegistryEntry::fromMapEntry);
      }
   }

   public static record RegistryEntry<T>(ResourceKey<? extends Registry<T>> key, Registry<T> value) {
      private static <T, R extends Registry<? extends T>> RegistryAccess.RegistryEntry<T> fromMapEntry(Map.Entry<? extends ResourceKey<? extends Registry<?>>, R> p_206242_) {
         return fromUntyped(p_206242_.getKey(), p_206242_.getValue());
      }

      private static <T> RegistryAccess.RegistryEntry<T> fromUntyped(ResourceKey<? extends Registry<?>> p_206244_, Registry<?> p_206245_) {
         return new RegistryAccess.RegistryEntry<T>((ResourceKey<? extends Registry<T>>) p_206244_, (Registry<T>) p_206245_);
      }

      private RegistryAccess.RegistryEntry<T> freeze() {
         return new RegistryAccess.RegistryEntry<>(this.key, this.value.freeze());
      }
   }
}