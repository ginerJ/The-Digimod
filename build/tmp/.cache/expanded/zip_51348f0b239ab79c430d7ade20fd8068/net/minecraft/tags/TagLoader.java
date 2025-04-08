package net.minecraft.tags;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.DependencySorter;
import org.slf4j.Logger;

public class TagLoader<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   final Function<ResourceLocation, Optional<? extends T>> idToValue;
   private final String directory;

   public TagLoader(Function<ResourceLocation, Optional<? extends T>> p_144493_, String p_144494_) {
      this.idToValue = p_144493_;
      this.directory = p_144494_;
   }

   public Map<ResourceLocation, List<TagLoader.EntryWithSource>> load(ResourceManager p_144496_) {
      Map<ResourceLocation, List<TagLoader.EntryWithSource>> map = Maps.newHashMap();
      FileToIdConverter filetoidconverter = FileToIdConverter.json(this.directory);

      for(Map.Entry<ResourceLocation, List<Resource>> entry : filetoidconverter.listMatchingResourceStacks(p_144496_).entrySet()) {
         ResourceLocation resourcelocation = entry.getKey();
         ResourceLocation resourcelocation1 = filetoidconverter.fileToId(resourcelocation);

         for(Resource resource : entry.getValue()) {
            try (Reader reader = resource.openAsReader()) {
               JsonElement jsonelement = JsonParser.parseReader(reader);
               List<TagLoader.EntryWithSource> list = map.computeIfAbsent(resourcelocation1, (p_215974_) -> {
                  return new ArrayList();
               });
               TagFile tagfile = TagFile.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, jsonelement)).getOrThrow(false, LOGGER::error);
               if (tagfile.replace()) {
                  list.clear();
               }

               String s = resource.sourcePackId();
               tagfile.entries().forEach((p_215997_) -> {
                  list.add(new TagLoader.EntryWithSource(p_215997_, s));
               });
               tagfile.remove().forEach(e -> list.add(new TagLoader.EntryWithSource(e, s, true)));
            } catch (Exception exception) {
               LOGGER.error("Couldn't read tag list {} from {} in data pack {}", resourcelocation1, resourcelocation, resource.sourcePackId(), exception);
            }
         }
      }

      return map;
   }

   private Either<Collection<TagLoader.EntryWithSource>, Collection<T>> build(TagEntry.Lookup<T> p_215979_, List<TagLoader.EntryWithSource> p_215980_) {
      var builder = new java.util.HashSet<T>();
      List<TagLoader.EntryWithSource> list = new ArrayList<>();

      for(TagLoader.EntryWithSource tagloader$entrywithsource : p_215980_) {
         if (!tagloader$entrywithsource.entry().build(p_215979_, tagloader$entrywithsource.remove() ? builder::remove : builder::add)) {
            if (!tagloader$entrywithsource.remove()) // Treat all removals as optional at runtime. If it was missing, then it could of never been added.
            list.add(tagloader$entrywithsource);
         }
      }

      return list.isEmpty() ? Either.right(List.copyOf(builder)) : Either.left(list);
   }

   public Map<ResourceLocation, Collection<T>> build(Map<ResourceLocation, List<TagLoader.EntryWithSource>> p_203899_) {
      final Map<ResourceLocation, Collection<T>> map = Maps.newHashMap();
      TagEntry.Lookup<T> lookup = new TagEntry.Lookup<T>() {
         @Nullable
         public T element(ResourceLocation p_216039_) {
            return TagLoader.this.idToValue.apply(p_216039_).orElse(null);
         }

         @Nullable
         public Collection<T> tag(ResourceLocation p_216041_) {
            return map.get(p_216041_);
         }
      };
      DependencySorter<ResourceLocation, TagLoader.SortingEntry> dependencysorter = new DependencySorter<>();
      p_203899_.forEach((p_284685_, p_284686_) -> {
         dependencysorter.addEntry(p_284685_, new TagLoader.SortingEntry(p_284686_));
      });
      dependencysorter.orderByDependencies((p_284682_, p_284683_) -> {
         this.build(lookup, p_284683_.entries).ifLeft((p_215977_) -> {
            LOGGER.error("Couldn't load tag {} as it is missing following references: {}", p_284682_, p_215977_.stream().map(Objects::toString).collect(Collectors.joining("\n\t", "\n\t", "")));
         }).ifRight((p_216001_) -> {
            map.put(p_284682_, p_216001_);
         });
      });
      return map;
   }

   public Map<ResourceLocation, Collection<T>> loadAndBuild(ResourceManager p_203901_) {
      return this.build(this.load(p_203901_));
   }

   public static record EntryWithSource(TagEntry entry, String source, boolean remove) {
      public EntryWithSource(TagEntry entry, String source) { this(entry, source, false); }
      public String toString() {
         return this.entry + " (from " + this.source + ")";
      }
   }

   static record SortingEntry(List<TagLoader.EntryWithSource> entries) implements DependencySorter.Entry<ResourceLocation> {
      public void visitRequiredDependencies(Consumer<ResourceLocation> p_285529_) {
         this.entries.forEach((p_285236_) -> {
            p_285236_.entry.visitRequiredDependencies(p_285529_);
         });
      }

      public void visitOptionalDependencies(Consumer<ResourceLocation> p_285469_) {
         this.entries.forEach((p_284943_) -> {
            p_284943_.entry.visitOptionalDependencies(p_285469_);
         });
      }
   }
}
