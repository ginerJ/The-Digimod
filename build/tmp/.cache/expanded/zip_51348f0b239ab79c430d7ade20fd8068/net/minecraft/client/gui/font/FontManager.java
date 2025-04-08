package net.minecraft.client.gui.font;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.DependencySorter;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FontManager implements PreparableReloadListener, AutoCloseable {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final String FONTS_PATH = "fonts.json";
   public static final ResourceLocation MISSING_FONT = new ResourceLocation("minecraft", "missing");
   private static final FileToIdConverter FONT_DEFINITIONS = FileToIdConverter.json("font");
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final FontSet missingFontSet;
   private final List<GlyphProvider> providersToClose = new ArrayList<>();
   private final Map<ResourceLocation, FontSet> fontSets = new HashMap<>();
   private final TextureManager textureManager;
   private Map<ResourceLocation, ResourceLocation> renames = ImmutableMap.of();

   public FontManager(TextureManager p_95005_) {
      this.textureManager = p_95005_;
      this.missingFontSet = Util.make(new FontSet(p_95005_, MISSING_FONT), (p_95010_) -> {
         p_95010_.reload(Lists.newArrayList(new AllMissingGlyphProvider()));
      });
   }

   public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier p_285160_, ResourceManager p_285231_, ProfilerFiller p_285232_, ProfilerFiller p_285262_, Executor p_284975_, Executor p_285218_) {
      p_285232_.startTick();
      p_285232_.endTick();
      return this.prepare(p_285231_, p_284975_).thenCompose(p_285160_::wait).thenAcceptAsync((p_284609_) -> {
         this.apply(p_284609_, p_285262_);
      }, p_285218_);
   }

   private CompletableFuture<FontManager.Preparation> prepare(ResourceManager p_285252_, Executor p_284969_) {
      List<CompletableFuture<FontManager.UnresolvedBuilderBundle>> list = new ArrayList<>();

      for(Map.Entry<ResourceLocation, List<Resource>> entry : FONT_DEFINITIONS.listMatchingResourceStacks(p_285252_).entrySet()) {
         ResourceLocation resourcelocation = FONT_DEFINITIONS.fileToId(entry.getKey());
         list.add(CompletableFuture.supplyAsync(() -> {
            List<Pair<FontManager.BuilderId, GlyphProviderDefinition>> list1 = loadResourceStack(entry.getValue(), resourcelocation);
            FontManager.UnresolvedBuilderBundle fontmanager$unresolvedbuilderbundle = new FontManager.UnresolvedBuilderBundle(resourcelocation);

            for(Pair<FontManager.BuilderId, GlyphProviderDefinition> pair : list1) {
               FontManager.BuilderId fontmanager$builderid = pair.getFirst();
               pair.getSecond().unpack().ifLeft((p_286126_) -> {
                  CompletableFuture<Optional<GlyphProvider>> completablefuture = this.safeLoad(fontmanager$builderid, p_286126_, p_285252_, p_284969_);
                  fontmanager$unresolvedbuilderbundle.add(fontmanager$builderid, completablefuture);
               }).ifRight((p_286129_) -> {
                  fontmanager$unresolvedbuilderbundle.add(fontmanager$builderid, p_286129_);
               });
            }

            return fontmanager$unresolvedbuilderbundle;
         }, p_284969_));
      }

      return Util.sequence(list).thenCompose((p_284592_) -> {
         List<CompletableFuture<Optional<GlyphProvider>>> list1 = p_284592_.stream().flatMap(FontManager.UnresolvedBuilderBundle::listBuilders).collect(Collectors.toCollection(ArrayList::new));
         GlyphProvider glyphprovider = new AllMissingGlyphProvider();
         list1.add(CompletableFuture.completedFuture(Optional.of(glyphprovider)));
         return Util.sequence(list1).thenCompose((p_284618_) -> {
            Map<ResourceLocation, List<GlyphProvider>> map = this.resolveProviders(p_284592_);
            CompletableFuture<?>[] completablefuture = map.values().stream().map((p_284585_) -> {
               return CompletableFuture.runAsync(() -> {
                  this.finalizeProviderLoading(p_284585_, glyphprovider);
               }, p_284969_);
            }).toArray((p_284587_) -> {
               return new CompletableFuture[p_284587_];
            });
            return CompletableFuture.allOf(completablefuture).thenApply((p_284595_) -> {
               List<GlyphProvider> list2 = p_284618_.stream().flatMap(Optional::stream).toList();
               return new FontManager.Preparation(map, list2);
            });
         });
      });
   }

   private CompletableFuture<Optional<GlyphProvider>> safeLoad(FontManager.BuilderId p_285113_, GlyphProviderDefinition.Loader p_286561_, ResourceManager p_285424_, Executor p_285371_) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            return Optional.of(p_286561_.load(p_285424_));
         } catch (Exception exception) {
            LOGGER.warn("Failed to load builder {}, rejecting", p_285113_, exception);
            return Optional.empty();
         }
      }, p_285371_);
   }

   private Map<ResourceLocation, List<GlyphProvider>> resolveProviders(List<FontManager.UnresolvedBuilderBundle> p_285282_) {
      Map<ResourceLocation, List<GlyphProvider>> map = new HashMap<>();
      DependencySorter<ResourceLocation, FontManager.UnresolvedBuilderBundle> dependencysorter = new DependencySorter<>();
      p_285282_.forEach((p_284626_) -> {
         dependencysorter.addEntry(p_284626_.fontId, p_284626_);
      });
      dependencysorter.orderByDependencies((p_284620_, p_284621_) -> {
         p_284621_.resolve(map::get).ifPresent((p_284590_) -> {
            map.put(p_284620_, p_284590_);
         });
      });
      return map;
   }

   private void finalizeProviderLoading(List<GlyphProvider> p_285520_, GlyphProvider p_285397_) {
      p_285520_.add(0, p_285397_);
      IntSet intset = new IntOpenHashSet();

      for(GlyphProvider glyphprovider : p_285520_) {
         intset.addAll(glyphprovider.getSupportedGlyphs());
      }

      intset.forEach((p_284614_) -> {
         if (p_284614_ != 32) {
            for(GlyphProvider glyphprovider1 : Lists.reverse(p_285520_)) {
               if (glyphprovider1.getGlyph(p_284614_) != null) {
                  break;
               }
            }

         }
      });
   }

   private void apply(FontManager.Preparation p_284939_, ProfilerFiller p_285407_) {
      p_285407_.startTick();
      p_285407_.push("closing");
      this.fontSets.values().forEach(FontSet::close);
      this.fontSets.clear();
      this.providersToClose.forEach(GlyphProvider::close);
      this.providersToClose.clear();
      p_285407_.popPush("reloading");
      p_284939_.providers().forEach((p_284627_, p_284628_) -> {
         FontSet fontset = new FontSet(this.textureManager, p_284627_);
         fontset.reload(Lists.reverse(p_284628_));
         this.fontSets.put(p_284627_, fontset);
      });
      this.providersToClose.addAll(p_284939_.allProviders);
      p_285407_.pop();
      p_285407_.endTick();
      if (!this.fontSets.containsKey(this.getActualId(Minecraft.DEFAULT_FONT))) {
         throw new IllegalStateException("Default font failed to load");
      }
   }

   private static List<Pair<FontManager.BuilderId, GlyphProviderDefinition>> loadResourceStack(List<Resource> p_284976_, ResourceLocation p_285272_) {
      List<Pair<FontManager.BuilderId, GlyphProviderDefinition>> list = new ArrayList<>();

      for(Resource resource : p_284976_) {
         try (Reader reader = resource.openAsReader()) {
            JsonElement jsonelement = GSON.fromJson(reader, JsonElement.class);
            FontManager.FontDefinitionFile fontmanager$fontdefinitionfile = Util.getOrThrow(FontManager.FontDefinitionFile.CODEC.parse(JsonOps.INSTANCE, jsonelement), JsonParseException::new);
            List<GlyphProviderDefinition> list1 = fontmanager$fontdefinitionfile.providers;

            for(int i = list1.size() - 1; i >= 0; --i) {
               FontManager.BuilderId fontmanager$builderid = new FontManager.BuilderId(p_285272_, resource.sourcePackId(), i);
               list.add(Pair.of(fontmanager$builderid, list1.get(i)));
            }
         } catch (Exception exception) {
            LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}'", p_285272_, "fonts.json", resource.sourcePackId(), exception);
         }
      }

      return list;
   }

   public void setRenames(Map<ResourceLocation, ResourceLocation> p_95012_) {
      this.renames = p_95012_;
   }

   private ResourceLocation getActualId(ResourceLocation p_285141_) {
      return this.renames.getOrDefault(p_285141_, p_285141_);
   }

   public Font createFont() {
      return new Font((p_284586_) -> {
         return this.fontSets.getOrDefault(this.getActualId(p_284586_), this.missingFontSet);
      }, false);
   }

   public Font createFontFilterFishy() {
      return new Font((p_284596_) -> {
         return this.fontSets.getOrDefault(this.getActualId(p_284596_), this.missingFontSet);
      }, true);
   }

   public void close() {
      this.fontSets.values().forEach(FontSet::close);
      this.providersToClose.forEach(GlyphProvider::close);
      this.missingFontSet.close();
   }

   @OnlyIn(Dist.CLIENT)
   static record BuilderId(ResourceLocation fontId, String pack, int index) {
      public String toString() {
         return "(" + this.fontId + ": builder #" + this.index + " from pack " + this.pack + ")";
      }
   }

   @OnlyIn(Dist.CLIENT)
   static record BuilderResult(FontManager.BuilderId id, Either<CompletableFuture<Optional<GlyphProvider>>, ResourceLocation> result) {
      public Optional<List<GlyphProvider>> resolve(Function<ResourceLocation, List<GlyphProvider>> p_284942_) {
         return this.result.map((p_285332_) -> {
            return p_285332_.join().map(List::of);
         }, (p_285367_) -> {
            List<GlyphProvider> list = p_284942_.apply(p_285367_);
            if (list == null) {
               FontManager.LOGGER.warn("Can't find font {} referenced by builder {}, either because it's missing, failed to load or is part of loading cycle", p_285367_, this.id);
               return Optional.empty();
            } else {
               return Optional.of(list);
            }
         });
      }
   }

   @OnlyIn(Dist.CLIENT)
   static record FontDefinitionFile(List<GlyphProviderDefinition> providers) {
      public static final Codec<FontManager.FontDefinitionFile> CODEC = RecordCodecBuilder.create((p_286425_) -> {
         return p_286425_.group(GlyphProviderDefinition.CODEC.listOf().fieldOf("providers").forGetter(FontManager.FontDefinitionFile::providers)).apply(p_286425_, FontManager.FontDefinitionFile::new);
      });
   }

   @OnlyIn(Dist.CLIENT)
   static record Preparation(Map<ResourceLocation, List<GlyphProvider>> providers, List<GlyphProvider> allProviders) {
   }

   @OnlyIn(Dist.CLIENT)
   static record UnresolvedBuilderBundle(ResourceLocation fontId, List<FontManager.BuilderResult> builders, Set<ResourceLocation> dependencies) implements DependencySorter.Entry<ResourceLocation> {
      public UnresolvedBuilderBundle(ResourceLocation p_284984_) {
         this(p_284984_, new ArrayList<>(), new HashSet<>());
      }

      public void add(FontManager.BuilderId p_286837_, GlyphProviderDefinition.Reference p_286500_) {
         this.builders.add(new FontManager.BuilderResult(p_286837_, Either.right(p_286500_.id())));
         this.dependencies.add(p_286500_.id());
      }

      public void add(FontManager.BuilderId p_284935_, CompletableFuture<Optional<GlyphProvider>> p_284966_) {
         this.builders.add(new FontManager.BuilderResult(p_284935_, Either.left(p_284966_)));
      }

      private Stream<CompletableFuture<Optional<GlyphProvider>>> listBuilders() {
         return this.builders.stream().flatMap((p_285041_) -> {
            return p_285041_.result.left().stream();
         });
      }

      public Optional<List<GlyphProvider>> resolve(Function<ResourceLocation, List<GlyphProvider>> p_285118_) {
         List<GlyphProvider> list = new ArrayList<>();

         for(FontManager.BuilderResult fontmanager$builderresult : this.builders) {
            Optional<List<GlyphProvider>> optional = fontmanager$builderresult.resolve(p_285118_);
            if (!optional.isPresent()) {
               return Optional.empty();
            }

            list.addAll(optional.get());
         }

         return Optional.of(list);
      }

      public void visitRequiredDependencies(Consumer<ResourceLocation> p_285391_) {
         this.dependencies.forEach(p_285391_);
      }

      public void visitOptionalDependencies(Consumer<ResourceLocation> p_285405_) {
      }
   }
}