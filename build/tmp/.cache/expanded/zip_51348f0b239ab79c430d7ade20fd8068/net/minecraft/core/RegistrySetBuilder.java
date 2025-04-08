package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class RegistrySetBuilder {
   private final List<RegistrySetBuilder.RegistryStub<?>> entries = new ArrayList<>();

   static <T> HolderGetter<T> wrapContextLookup(final HolderLookup.RegistryLookup<T> p_255625_) {
      return new RegistrySetBuilder.EmptyTagLookup<T>(p_255625_) {
         public Optional<Holder.Reference<T>> get(ResourceKey<T> p_255765_) {
            return p_255625_.get(p_255765_);
         }
      };
   }

   public <T> RegistrySetBuilder add(ResourceKey<? extends Registry<T>> p_256446_, Lifecycle p_256394_, RegistrySetBuilder.RegistryBootstrap<T> p_256638_) {
      this.entries.add(new RegistrySetBuilder.RegistryStub<>(p_256446_, p_256394_, p_256638_));
      return this;
   }

   public <T> RegistrySetBuilder add(ResourceKey<? extends Registry<T>> p_256261_, RegistrySetBuilder.RegistryBootstrap<T> p_256010_) {
      return this.add(p_256261_, Lifecycle.stable(), p_256010_);
   }

   public List<? extends ResourceKey<? extends Registry<?>>> getEntryKeys() {
      return this.entries.stream().map(RegistrySetBuilder.RegistryStub::key).toList();
   }

   private RegistrySetBuilder.BuildState createState(RegistryAccess p_256400_) {
      RegistrySetBuilder.BuildState registrysetbuilder$buildstate = RegistrySetBuilder.BuildState.create(p_256400_, this.entries.stream().map(RegistrySetBuilder.RegistryStub::key));
      this.entries.forEach((p_255629_) -> {
         p_255629_.apply(registrysetbuilder$buildstate);
      });
      return registrysetbuilder$buildstate;
   }

   public HolderLookup.Provider build(RegistryAccess p_256112_) {
      RegistrySetBuilder.BuildState registrysetbuilder$buildstate = this.createState(p_256112_);
      Stream<HolderLookup.RegistryLookup<?>> stream = p_256112_.registries().map((p_258195_) -> {
         return p_258195_.value().asLookup();
      });
      Stream<HolderLookup.RegistryLookup<?>> stream1 = this.entries.stream().map((p_255700_) -> {
         return p_255700_.collectChanges(registrysetbuilder$buildstate).buildAsLookup();
      });
      HolderLookup.Provider holderlookup$provider = HolderLookup.Provider.create(Stream.concat(stream, stream1.peek(registrysetbuilder$buildstate::addOwner)));
      registrysetbuilder$buildstate.reportRemainingUnreferencedValues();
      registrysetbuilder$buildstate.throwOnError();
      return holderlookup$provider;
   }

   public HolderLookup.Provider buildPatch(RegistryAccess p_255676_, HolderLookup.Provider p_255900_) {
      RegistrySetBuilder.BuildState registrysetbuilder$buildstate = this.createState(p_255676_);
      Map<ResourceKey<? extends Registry<?>>, RegistrySetBuilder.RegistryContents<?>> map = new HashMap<>();
      registrysetbuilder$buildstate.collectReferencedRegistries().forEach((p_272339_) -> {
         map.put(p_272339_.key, p_272339_);
      });
      this.entries.stream().map((p_272337_) -> {
         return p_272337_.collectChanges(registrysetbuilder$buildstate);
      }).forEach((p_272341_) -> {
         map.put(p_272341_.key, p_272341_);
      });
      Stream<HolderLookup.RegistryLookup<?>> stream = p_255676_.registries().map((p_258194_) -> {
         return p_258194_.value().asLookup();
      });
      HolderLookup.Provider holderlookup$provider = HolderLookup.Provider.create(Stream.concat(stream, map.values().stream().map(RegistrySetBuilder.RegistryContents::buildAsLookup).peek(registrysetbuilder$buildstate::addOwner)));
      registrysetbuilder$buildstate.fillMissingHolders(p_255900_);
      registrysetbuilder$buildstate.reportRemainingUnreferencedValues();
      registrysetbuilder$buildstate.throwOnError();
      return holderlookup$provider;
   }

   static record BuildState(RegistrySetBuilder.CompositeOwner owner, RegistrySetBuilder.UniversalLookup lookup, Map<ResourceLocation, HolderGetter<?>> registries, Map<ResourceKey<?>, RegistrySetBuilder.RegisteredValue<?>> registeredValues, List<RuntimeException> errors) {
      public static RegistrySetBuilder.BuildState create(RegistryAccess p_255995_, Stream<ResourceKey<? extends Registry<?>>> p_256495_) {
         RegistrySetBuilder.CompositeOwner registrysetbuilder$compositeowner = new RegistrySetBuilder.CompositeOwner();
         List<RuntimeException> list = new ArrayList<>();
         RegistrySetBuilder.UniversalLookup registrysetbuilder$universallookup = new RegistrySetBuilder.UniversalLookup(registrysetbuilder$compositeowner);
         ImmutableMap.Builder<ResourceLocation, HolderGetter<?>> builder = ImmutableMap.builder();
         p_255995_.registries().forEach((p_258197_) -> {
            builder.put(p_258197_.key().location(), net.minecraftforge.common.ForgeHooks.wrapRegistryLookup(p_258197_.value().asLookup()));
         });
         p_256495_.forEach((p_256603_) -> {
            builder.put(p_256603_.location(), registrysetbuilder$universallookup);
         });
         return new RegistrySetBuilder.BuildState(registrysetbuilder$compositeowner, registrysetbuilder$universallookup, builder.build(), new HashMap<>(), list);
      }

      public <T> BootstapContext<T> bootstapContext() {
         return new BootstapContext<T>() {
            public Holder.Reference<T> register(ResourceKey<T> p_256176_, T p_256422_, Lifecycle p_255924_) {
               RegistrySetBuilder.RegisteredValue<?> registeredvalue = BuildState.this.registeredValues.put(p_256176_, new RegistrySetBuilder.RegisteredValue(p_256422_, p_255924_));
               if (registeredvalue != null) {
                  BuildState.this.errors.add(new IllegalStateException("Duplicate registration for " + p_256176_ + ", new=" + p_256422_ + ", old=" + registeredvalue.value));
               }

               return BuildState.this.lookup.getOrCreate(p_256176_);
            }

            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> p_255961_) {
               return (HolderGetter<S>) BuildState.this.registries.getOrDefault(p_255961_.location(), BuildState.this.lookup);
            }

            @Override
            public <S> Optional<HolderLookup.RegistryLookup<S>> registryLookup(ResourceKey<? extends Registry<? extends S>> registry) {
               return Optional.ofNullable((HolderLookup.RegistryLookup<S>) BuildState.this.registries.get(registry.location()));
            }
         };
      }

      public void reportRemainingUnreferencedValues() {
         for(ResourceKey<Object> resourcekey : this.lookup.holders.keySet()) {
            this.errors.add(new IllegalStateException("Unreferenced key: " + resourcekey));
         }

         this.registeredValues.forEach((p_256143_, p_256662_) -> {
            this.errors.add(new IllegalStateException("Orpaned value " + p_256662_.value + " for key " + p_256143_));
         });
      }

      public void throwOnError() {
         if (!this.errors.isEmpty()) {
            IllegalStateException illegalstateexception = new IllegalStateException("Errors during registry creation");

            for(RuntimeException runtimeexception : this.errors) {
               illegalstateexception.addSuppressed(runtimeexception);
            }

            throw illegalstateexception;
         }
      }

      public void addOwner(HolderOwner<?> p_256407_) {
         this.owner.add(p_256407_);
      }

      public void fillMissingHolders(HolderLookup.Provider p_255679_) {
         Map<ResourceLocation, Optional<? extends HolderLookup<Object>>> map = new HashMap<>();
         Iterator<Map.Entry<ResourceKey<Object>, Holder.Reference<Object>>> iterator = this.lookup.holders.entrySet().iterator();

         while(iterator.hasNext()) {
            Map.Entry<ResourceKey<Object>, Holder.Reference<Object>> entry = iterator.next();
            ResourceKey<Object> resourcekey = entry.getKey();
            Holder.Reference<Object> reference = entry.getValue();
            map.computeIfAbsent(resourcekey.registry(), (p_255896_) -> {
               return p_255679_.lookup(ResourceKey.createRegistryKey(p_255896_));
            }).flatMap((p_256068_) -> {
               return p_256068_.get(resourcekey);
            }).ifPresent((p_256030_) -> {
               reference.bindValue(p_256030_.value());
               iterator.remove();
            });
         }

      }

      public Stream<RegistrySetBuilder.RegistryContents<?>> collectReferencedRegistries() {
         return this.lookup.holders.keySet().stream().map(ResourceKey::registry).distinct().map((p_272342_) -> {
            return new RegistrySetBuilder.RegistryContents(ResourceKey.createRegistryKey(p_272342_), Lifecycle.stable(), Map.of());
         });
      }
   }

   static class CompositeOwner implements HolderOwner<Object> {
      private final Set<HolderOwner<?>> owners = Sets.newIdentityHashSet();

      public boolean canSerializeIn(HolderOwner<Object> p_256333_) {
         return this.owners.contains(p_256333_);
      }

      public void add(HolderOwner<?> p_256361_) {
         this.owners.add(p_256361_);
      }
   }

   abstract static class EmptyTagLookup<T> implements HolderGetter<T> {
      protected final HolderOwner<T> owner;

      protected EmptyTagLookup(HolderOwner<T> p_256166_) {
         this.owner = p_256166_;
      }

      public Optional<HolderSet.Named<T>> get(TagKey<T> p_256664_) {
         return Optional.of(HolderSet.emptyNamed(this.owner, p_256664_));
      }
   }

   static record RegisteredValue<T>(T value, Lifecycle lifecycle) {
   }

   @FunctionalInterface
   public interface RegistryBootstrap<T> {
      void run(BootstapContext<T> p_255783_);
   }

   static record RegistryContents<T>(ResourceKey<? extends Registry<? extends T>> key, Lifecycle lifecycle, Map<ResourceKey<T>, RegistrySetBuilder.ValueAndHolder<T>> values) {
      public HolderLookup.RegistryLookup<T> buildAsLookup() {
         return new HolderLookup.RegistryLookup<T>() {
            private final Map<ResourceKey<T>, Holder.Reference<T>> entries = RegistryContents.this.values.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, (p_256193_) -> {
               RegistrySetBuilder.ValueAndHolder<T> valueandholder = p_256193_.getValue();
               Holder.Reference<T> reference = valueandholder.holder().orElseGet(() -> {
                  return Holder.Reference.createStandAlone(this, p_256193_.getKey());
               });
               reference.bindValue(valueandholder.value().value());
               return reference;
            }));

            public ResourceKey<? extends Registry<? extends T>> key() {
               return RegistryContents.this.key;
            }

            public Lifecycle registryLifecycle() {
               return RegistryContents.this.lifecycle;
            }

            public Optional<Holder.Reference<T>> get(ResourceKey<T> p_255760_) {
               return Optional.ofNullable(this.entries.get(p_255760_));
            }

            public Stream<Holder.Reference<T>> listElements() {
               return this.entries.values().stream();
            }

            public Optional<HolderSet.Named<T>> get(TagKey<T> p_255810_) {
               return Optional.empty();
            }

            public Stream<HolderSet.Named<T>> listTags() {
               return Stream.empty();
            }
         };
      }
   }

   static record RegistryStub<T>(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, RegistrySetBuilder.RegistryBootstrap<T> bootstrap) {
      void apply(RegistrySetBuilder.BuildState p_256272_) {
         this.bootstrap.run(p_256272_.bootstapContext());
      }

      public RegistrySetBuilder.RegistryContents<T> collectChanges(RegistrySetBuilder.BuildState p_256416_) {
         Map<ResourceKey<T>, RegistrySetBuilder.ValueAndHolder<T>> map = new HashMap<>();
         Iterator<Map.Entry<ResourceKey<?>, RegistrySetBuilder.RegisteredValue<?>>> iterator = p_256416_.registeredValues.entrySet().iterator();

         while(iterator.hasNext()) {
            Map.Entry<ResourceKey<?>, RegistrySetBuilder.RegisteredValue<?>> entry = iterator.next();
            ResourceKey<?> resourcekey = entry.getKey();
            if (resourcekey.isFor(this.key)) {
               RegistrySetBuilder.RegisteredValue<T> registeredvalue = (RegistrySetBuilder.RegisteredValue<T>) entry.getValue();
               Holder.Reference<T> reference = (Holder.Reference<T>) p_256416_.lookup.holders.remove(resourcekey);
               map.put((ResourceKey<T>) resourcekey, new RegistrySetBuilder.ValueAndHolder<>(registeredvalue, Optional.ofNullable(reference)));
               iterator.remove();
            }
         }

         return new RegistrySetBuilder.RegistryContents<>(this.key, this.lifecycle, map);
      }
   }

   static class UniversalLookup extends RegistrySetBuilder.EmptyTagLookup<Object> {
      final Map<ResourceKey<Object>, Holder.Reference<Object>> holders = new HashMap<>();

      public UniversalLookup(HolderOwner<Object> p_256629_) {
         super(p_256629_);
      }

      public Optional<Holder.Reference<Object>> get(ResourceKey<Object> p_256303_) {
         return Optional.of(this.getOrCreate(p_256303_));
      }

      <T> Holder.Reference<T> getOrCreate(ResourceKey<T> p_256298_) {
         return (Holder.Reference<T>) this.holders.computeIfAbsent((ResourceKey<Object> )p_256298_, (p_256154_) -> {
            return Holder.Reference.createStandAlone(this.owner, p_256154_);
         });
      }
   }

   static record ValueAndHolder<T>(RegistrySetBuilder.RegisteredValue<T> value, Optional<Holder.Reference<T>> holder) {
   }
}
