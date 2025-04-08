package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class MappedRegistry<T> implements WritableRegistry<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   final ResourceKey<? extends Registry<T>> key;
   private final ObjectList<Holder.Reference<T>> byId = new ObjectArrayList<>(256);
   private final Object2IntMap<T> toId = Util.make(new Object2IntOpenCustomHashMap<>(Util.identityStrategy()), (p_194539_) -> {
      p_194539_.defaultReturnValue(-1);
   });
   private final Map<ResourceLocation, Holder.Reference<T>> byLocation = new HashMap<>();
   private final Map<ResourceKey<T>, Holder.Reference<T>> byKey = new HashMap<>();
   private final Map<T, Holder.Reference<T>> byValue = new IdentityHashMap<>();
   private final Map<T, Lifecycle> lifecycles = new IdentityHashMap<>();
   private Lifecycle registryLifecycle;
   private volatile Map<TagKey<T>, HolderSet.Named<T>> tags = new IdentityHashMap<>();
   private boolean frozen;
   @Nullable
   protected Map<T, Holder.Reference<T>> unregisteredIntrusiveHolders;
   @Nullable
   private List<Holder.Reference<T>> holdersInOrder;
   private int nextId;
   private final HolderLookup.RegistryLookup<T> lookup = new HolderLookup.RegistryLookup<T>() {
      public ResourceKey<? extends Registry<? extends T>> key() {
         return MappedRegistry.this.key;
      }

      public Lifecycle registryLifecycle() {
         return MappedRegistry.this.registryLifecycle();
      }

      public Optional<Holder.Reference<T>> get(ResourceKey<T> p_255624_) {
         return MappedRegistry.this.getHolder(p_255624_);
      }

      public Stream<Holder.Reference<T>> listElements() {
         return MappedRegistry.this.holders();
      }

      public Optional<HolderSet.Named<T>> get(TagKey<T> p_256277_) {
         return MappedRegistry.this.getTag(p_256277_);
      }

      public Stream<HolderSet.Named<T>> listTags() {
         return MappedRegistry.this.getTags().map(Pair::getSecond);
      }
   };

   public MappedRegistry(ResourceKey<? extends Registry<T>> p_249899_, Lifecycle p_252249_) {
      this(p_249899_, p_252249_, false);
   }

   public MappedRegistry(ResourceKey<? extends Registry<T>> p_252132_, Lifecycle p_249215_, boolean p_251014_) {
      Bootstrap.checkBootstrapCalled(() -> {
         return "registry " + p_252132_;
      });
      this.key = p_252132_;
      this.registryLifecycle = p_249215_;
      if (p_251014_) {
         this.unregisteredIntrusiveHolders = new IdentityHashMap<>();
      }

   }

   public ResourceKey<? extends Registry<T>> key() {
      return this.key;
   }

   public String toString() {
      return "Registry[" + this.key + " (" + this.registryLifecycle + ")]";
   }

   private List<Holder.Reference<T>> holdersInOrder() {
      if (this.holdersInOrder == null) {
         this.holdersInOrder = this.byId.stream().filter(Objects::nonNull).toList();
      }

      return this.holdersInOrder;
   }

   private void validateWrite() {
      if (this.frozen) {
         throw new IllegalStateException("Registry is already frozen");
      }
   }

   private void validateWrite(ResourceKey<T> p_205922_) {
      if (this.frozen) {
         throw new IllegalStateException("Registry is already frozen (trying to add key " + p_205922_ + ")");
      }
   }

   private static final Set<ResourceLocation> KNOWN = new java.util.LinkedHashSet<>();
   public static Set<ResourceLocation> getKnownRegistries() {
      return java.util.Collections.unmodifiableSet(KNOWN);
   }
   protected final void markKnown() {
      KNOWN.add(key().location());
   }

   public Holder.Reference<T> registerMapping(int p_256563_, ResourceKey<T> p_256594_, T p_256374_, Lifecycle p_256469_) {
      markKnown();
      this.validateWrite(p_256594_);
      Validate.notNull(p_256594_);
      Validate.notNull(p_256374_);
      if (this.byLocation.containsKey(p_256594_.location())) {
         Util.pauseInIde(new IllegalStateException("Adding duplicate key '" + p_256594_ + "' to registry"));
      }

      if (this.byValue.containsKey(p_256374_)) {
         Util.pauseInIde(new IllegalStateException("Adding duplicate value '" + p_256374_ + "' to registry"));
      }

      Holder.Reference<T> reference;
      if (this.unregisteredIntrusiveHolders != null) {
         reference = this.unregisteredIntrusiveHolders.remove(p_256374_);
         if (reference == null) {
            throw new AssertionError("Missing intrusive holder for " + p_256594_ + ":" + p_256374_);
         }

         reference.bindKey(p_256594_);
      } else {
         reference = this.byKey.computeIfAbsent(p_256594_, (p_258168_) -> {
            return Holder.Reference.createStandAlone(this.holderOwner(), p_258168_);
         });
         // Forge: Bind the value immediately so it can be queried while the registry is not frozen
         reference.bindValue(p_256374_);
      }

      this.byKey.put(p_256594_, reference);
      this.byLocation.put(p_256594_.location(), reference);
      this.byValue.put(p_256374_, reference);
      this.byId.size(Math.max(this.byId.size(), p_256563_ + 1));
      this.byId.set(p_256563_, reference);
      this.toId.put(p_256374_, p_256563_);
      if (this.nextId <= p_256563_) {
         this.nextId = p_256563_ + 1;
      }

      this.lifecycles.put(p_256374_, p_256469_);
      this.registryLifecycle = this.registryLifecycle.add(p_256469_);
      this.holdersInOrder = null;
      return reference;
   }

   public Holder.Reference<T> register(ResourceKey<T> p_256252_, T p_256591_, Lifecycle p_256255_) {
      return this.registerMapping(this.nextId, p_256252_, p_256591_, p_256255_);
   }

   @Nullable
   public ResourceLocation getKey(T p_122746_) {
      Holder.Reference<T> reference = this.byValue.get(p_122746_);
      return reference != null ? reference.key().location() : null;
   }

   public Optional<ResourceKey<T>> getResourceKey(T p_122755_) {
      return Optional.ofNullable(this.byValue.get(p_122755_)).map(Holder.Reference::key);
   }

   public int getId(@Nullable T p_122706_) {
      return this.toId.getInt(p_122706_);
   }

   @Nullable
   public T get(@Nullable ResourceKey<T> p_122714_) {
      return getValueFromNullable(this.byKey.get(p_122714_));
   }

   @Nullable
   public T byId(int p_122684_) {
      return (T)(p_122684_ >= 0 && p_122684_ < this.byId.size() ? getValueFromNullable(this.byId.get(p_122684_)) : null);
   }

   public Optional<Holder.Reference<T>> getHolder(int p_205907_) {
      return p_205907_ >= 0 && p_205907_ < this.byId.size() ? Optional.ofNullable(this.byId.get(p_205907_)) : Optional.empty();
   }

   public Optional<Holder.Reference<T>> getHolder(ResourceKey<T> p_205905_) {
      return Optional.ofNullable(this.byKey.get(p_205905_));
   }

   public Holder<T> wrapAsHolder(T p_263356_) {
      Holder.Reference<T> reference = this.byValue.get(p_263356_);
      return (Holder<T>)(reference != null ? reference : Holder.direct(p_263356_));
   }

   Holder.Reference<T> getOrCreateHolderOrThrow(ResourceKey<T> p_248831_) {
      return this.byKey.computeIfAbsent(p_248831_, (p_258169_) -> {
         if (this.unregisteredIntrusiveHolders != null) {
            throw new IllegalStateException("This registry can't create new holders without value");
         } else {
            this.validateWrite(p_258169_);
            return Holder.Reference.createStandAlone(this.holderOwner(), p_258169_);
         }
      });
   }

   public int size() {
      return this.byKey.size();
   }

   public Lifecycle lifecycle(T p_122764_) {
      return this.lifecycles.get(p_122764_);
   }

   public Lifecycle registryLifecycle() {
      return this.registryLifecycle;
   }

   public Iterator<T> iterator() {
      return Iterators.transform(this.holdersInOrder().iterator(), Holder::value);
   }

   @Nullable
   public T get(@Nullable ResourceLocation p_122739_) {
      Holder.Reference<T> reference = this.byLocation.get(p_122739_);
      return getValueFromNullable(reference);
   }

   @Nullable
   private static <T> T getValueFromNullable(@Nullable Holder.Reference<T> p_205866_) {
      return (T)(p_205866_ != null ? p_205866_.value() : null);
   }

   public Set<ResourceLocation> keySet() {
      return Collections.unmodifiableSet(this.byLocation.keySet());
   }

   public Set<ResourceKey<T>> registryKeySet() {
      return Collections.unmodifiableSet(this.byKey.keySet());
   }

   public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
      return Collections.unmodifiableSet(Maps.transformValues(this.byKey, Holder::value).entrySet());
   }

   public Stream<Holder.Reference<T>> holders() {
      return this.holdersInOrder().stream();
   }

   public Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags() {
      return this.tags.entrySet().stream().map((p_211060_) -> {
         return Pair.of(p_211060_.getKey(), p_211060_.getValue());
      });
   }

   public HolderSet.Named<T> getOrCreateTag(TagKey<T> p_205895_) {
      HolderSet.Named<T> named = this.tags.get(p_205895_);
      if (named == null) {
         named = this.createTag(p_205895_);
         Map<TagKey<T>, HolderSet.Named<T>> map = new IdentityHashMap<>(this.tags);
         map.put(p_205895_, named);
         this.tags = map;
      }

      return named;
   }

   private HolderSet.Named<T> createTag(TagKey<T> p_211068_) {
      return new HolderSet.Named<>(this.holderOwner(), p_211068_);
   }

   public Stream<TagKey<T>> getTagNames() {
      return this.tags.keySet().stream();
   }

   public boolean isEmpty() {
      return this.byKey.isEmpty();
   }

   public Optional<Holder.Reference<T>> getRandom(RandomSource p_235716_) {
      return Util.getRandomSafe(this.holdersInOrder(), p_235716_);
   }

   public boolean containsKey(ResourceLocation p_122761_) {
      return this.byLocation.containsKey(p_122761_);
   }

   public boolean containsKey(ResourceKey<T> p_175392_) {
      return this.byKey.containsKey(p_175392_);
   }

   /** @deprecated Forge: For internal use only. Use the Register events when registering values. */
   @Deprecated
   public void unfreeze() {
      this.frozen = false;
   }

   public Registry<T> freeze() {
      if (this.frozen) {
         return this;
      } else {
         this.frozen = true;
         List<ResourceLocation> list = this.byKey.entrySet().stream().filter((p_211055_) -> {
            return !p_211055_.getValue().isBound();
         }).map((p_211794_) -> {
            return p_211794_.getKey().location();
         }).sorted().toList();
         if (!list.isEmpty()) {
            throw new IllegalStateException("Unbound values in registry " + this.key() + ": " + list);
         } else {
            if (this.unregisteredIntrusiveHolders != null) {
               if (!this.unregisteredIntrusiveHolders.isEmpty()) {
                  throw new IllegalStateException("Some intrusive holders were not registered: " + this.unregisteredIntrusiveHolders.values());
               }

               // Forge: We freeze/unfreeze vanilla registries more than once, so we need to keep the unregistered intrusive holders map around.
               // this.unregisteredIntrusiveHolders = null;
            }

            return this;
         }
      }
   }

   public Holder.Reference<T> createIntrusiveHolder(T p_205915_) {
      if (this.unregisteredIntrusiveHolders == null) {
         throw new IllegalStateException("This registry can't create intrusive holders");
      } else {
         this.validateWrite();
         return this.unregisteredIntrusiveHolders.computeIfAbsent(p_205915_, (p_258166_) -> {
            return Holder.Reference.createIntrusive(this.asLookup(), p_258166_);
         });
      }
   }

   public Optional<HolderSet.Named<T>> getTag(TagKey<T> p_205909_) {
      return Optional.ofNullable(this.tags.get(p_205909_));
   }

   public void bindTags(Map<TagKey<T>, List<Holder<T>>> p_205875_) {
      Map<Holder.Reference<T>, List<TagKey<T>>> map = new IdentityHashMap<>();
      this.byKey.values().forEach((p_211801_) -> {
         map.put(p_211801_, new ArrayList<>());
      });
      p_205875_.forEach((p_211806_, p_211807_) -> {
         for(Holder<T> holder : p_211807_) {
            if (!holder.canSerializeIn(this.asLookup())) {
               throw new IllegalStateException("Can't create named set " + p_211806_ + " containing value " + holder + " from outside registry " + this);
            }

            if (!(holder instanceof Holder.Reference)) {
               throw new IllegalStateException("Found direct holder " + holder + " value in tag " + p_211806_);
            }

            Holder.Reference<T> reference = (Holder.Reference)holder;
            map.get(reference).add(p_211806_);
         }

      });
      Set<TagKey<T>> set = Sets.difference(this.tags.keySet(), p_205875_.keySet());
      if (!set.isEmpty()) {
         LOGGER.warn("Not all defined tags for registry {} are present in data pack: {}", this.key(), set.stream().map((p_211811_) -> {
            return p_211811_.location().toString();
         }).sorted().collect(Collectors.joining(", ")));
      }

      Map<TagKey<T>, HolderSet.Named<T>> map1 = new IdentityHashMap<>(this.tags);
      p_205875_.forEach((p_211797_, p_211798_) -> {
         map1.computeIfAbsent(p_211797_, this::createTag).bind(p_211798_);
      });
      map.forEach(Holder.Reference::bindTags);
      this.tags = map1;
   }

   public void resetTags() {
      this.tags.values().forEach((p_211792_) -> {
         p_211792_.bind(List.of());
      });
      this.byKey.values().forEach((p_211803_) -> {
         p_211803_.bindTags(Set.of());
      });
   }

   public HolderGetter<T> createRegistrationLookup() {
      this.validateWrite();
      return new HolderGetter<T>() {
         public Optional<Holder.Reference<T>> get(ResourceKey<T> p_259097_) {
            return Optional.of(this.getOrThrow(p_259097_));
         }

         public Holder.Reference<T> getOrThrow(ResourceKey<T> p_259750_) {
            return MappedRegistry.this.getOrCreateHolderOrThrow(p_259750_);
         }

         public Optional<HolderSet.Named<T>> get(TagKey<T> p_259486_) {
            return Optional.of(this.getOrThrow(p_259486_));
         }

         public HolderSet.Named<T> getOrThrow(TagKey<T> p_260298_) {
            return MappedRegistry.this.getOrCreateTag(p_260298_);
         }
      };
   }

   public HolderOwner<T> holderOwner() {
      return this.lookup;
   }

   public HolderLookup.RegistryLookup<T> asLookup() {
      return this.lookup;
   }
}
