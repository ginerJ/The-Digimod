package net.minecraft.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.WorldVersion;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class HashCache {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final String HEADER_MARKER = "// ";
   private final Path rootDir;
   private final Path cacheDir;
   private final String versionId;
   private final Map<String, HashCache.ProviderCache> caches;
   private final Map<String, HashCache.ProviderCache> originalCaches;
   private final Set<String> cachesToWrite = new HashSet<>();
   private final Set<Path> cachePaths = new HashSet<>();
   private final int initialCount;
   private int writes;

   private Path getProviderCachePath(String p_254395_) {
      return this.cacheDir.resolve(Hashing.sha1().hashString(p_254395_, StandardCharsets.UTF_8).toString());
   }

   public HashCache(Path p_236087_, Collection<String> p_253748_, WorldVersion p_236089_) throws IOException {
      this.versionId = p_236089_.getName();
      this.rootDir = p_236087_;
      this.cacheDir = p_236087_.resolve(".cache");
      Files.createDirectories(this.cacheDir);
      Map<String, HashCache.ProviderCache> map = new HashMap<>();
      int i = 0;

      for(String s : p_253748_) {
         Path path = this.getProviderCachePath(s);
         this.cachePaths.add(path);
         HashCache.ProviderCache hashcache$providercache = readCache(p_236087_, path);
         map.put(s, hashcache$providercache);
         i += hashcache$providercache.count();
      }

      this.caches = map;
      this.originalCaches = Map.copyOf(this.caches);
      this.initialCount = i;
   }

   private static HashCache.ProviderCache readCache(Path p_236093_, Path p_236094_) {
      if (Files.isReadable(p_236094_)) {
         try {
            return HashCache.ProviderCache.load(p_236093_, p_236094_);
         } catch (Exception exception) {
            LOGGER.warn("Failed to parse cache {}, discarding", p_236094_, exception);
         }
      }

      return new HashCache.ProviderCache("unknown", ImmutableMap.of());
   }

   public boolean shouldRunInThisVersion(String p_254319_) {
      HashCache.ProviderCache hashcache$providercache = this.caches.get(p_254319_);
      return hashcache$providercache == null || !hashcache$providercache.version.equals(this.versionId);
   }

   public CompletableFuture<HashCache.UpdateResult> generateUpdate(String p_253944_, HashCache.UpdateFunction p_254321_) {
      HashCache.ProviderCache hashcache$providercache = this.caches.get(p_253944_);
      if (hashcache$providercache == null) {
         throw new IllegalStateException("Provider not registered: " + p_253944_);
      } else {
         HashCache.CacheUpdater hashcache$cacheupdater = new HashCache.CacheUpdater(p_253944_, this.versionId, hashcache$providercache);
         return p_254321_.update(hashcache$cacheupdater).thenApply((p_253376_) -> {
            return hashcache$cacheupdater.close();
         });
      }
   }

   public void applyUpdate(HashCache.UpdateResult p_253725_) {
      this.caches.put(p_253725_.providerId(), p_253725_.cache());
      this.cachesToWrite.add(p_253725_.providerId());
      this.writes += p_253725_.writes();
   }

   public void purgeStaleAndWrite() throws IOException {
      Set<Path> set = new HashSet<>();
      this.caches.forEach((p_253378_, p_253379_) -> {
         if (this.cachesToWrite.contains(p_253378_)) {
            Path path = this.getProviderCachePath(p_253378_);
            // Forge: Only rewrite the cache file if it changed or is missing
            if (!p_253379_.equals(this.originalCaches.get(p_253378_)) || !Files.exists(path))
            p_253379_.save(this.rootDir, path, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + "\t" + p_253378_);
         }

         set.addAll(p_253379_.data().keySet());
      });
      set.add(this.rootDir.resolve("version.json"));
      MutableInt mutableint = new MutableInt();
      MutableInt mutableint1 = new MutableInt();

      try (Stream<Path> stream = Files.walk(this.rootDir)) {
         stream.forEach((p_236106_) -> {
            if (!Files.isDirectory(p_236106_)) {
               if (!this.cachePaths.contains(p_236106_)) {
                  mutableint.increment();
                  if (!set.contains(p_236106_)) {
                     try {
                        Files.delete(p_236106_);
                     } catch (IOException ioexception) {
                        LOGGER.warn("Failed to delete file {}", p_236106_, ioexception);
                     }

                     mutableint1.increment();
                  }
               }
            }
         });
      }

      LOGGER.info("Caching: total files: {}, old count: {}, new count: {}, removed stale: {}, written: {}", mutableint, this.initialCount, set.size(), mutableint1, this.writes);
   }

   class CacheUpdater implements CachedOutput {
      private final String provider;
      private final HashCache.ProviderCache oldCache;
      private final HashCache.ProviderCacheBuilder newCache;
      private final AtomicInteger writes = new AtomicInteger();
      private volatile boolean closed;

      CacheUpdater(String p_253971_, String p_254002_, HashCache.ProviderCache p_254244_) {
         this.provider = p_253971_;
         this.oldCache = p_254244_;
         this.newCache = new HashCache.ProviderCacheBuilder(p_254002_);
      }

      private boolean shouldWrite(Path p_236120_, HashCode p_236121_) {
         return !Objects.equals(this.oldCache.get(p_236120_), p_236121_) || !Files.exists(p_236120_);
      }

      public void writeIfNeeded(Path p_236123_, byte[] p_236124_, HashCode p_236125_) throws IOException {
         if (this.closed) {
            throw new IllegalStateException("Cannot write to cache as it has already been closed");
         } else {
            if (this.shouldWrite(p_236123_, p_236125_)) {
               this.writes.incrementAndGet();
               Files.createDirectories(p_236123_.getParent());
               Files.write(p_236123_, p_236124_);
            }

            this.newCache.put(p_236123_, p_236125_);
         }
      }

      public HashCache.UpdateResult close() {
         this.closed = true;
         return new HashCache.UpdateResult(this.provider, this.newCache.build(), this.writes.get());
      }
   }

   static record ProviderCache(String version, ImmutableMap<Path, HashCode> data) {
      @Nullable
      public HashCode get(Path p_236135_) {
         return this.data.get(p_236135_);
      }

      public int count() {
         return this.data.size();
      }

      public static HashCache.ProviderCache load(Path p_236140_, Path p_236141_) throws IOException {
         try (BufferedReader bufferedreader = Files.newBufferedReader(p_236141_, StandardCharsets.UTF_8)) {
            String s = bufferedreader.readLine();
            if (!s.startsWith("// ")) {
               throw new IllegalStateException("Missing cache file header");
            } else {
               String[] astring = s.substring("// ".length()).split("\t", 2);
               String s1 = astring[0];
               ImmutableMap.Builder<Path, HashCode> builder = ImmutableMap.builder();
               bufferedreader.lines().forEach((p_253382_) -> {
                  int i = p_253382_.indexOf(32);
                  builder.put(p_236140_.resolve(p_253382_.substring(i + 1)), HashCode.fromString(p_253382_.substring(0, i)));
               });
               return new HashCache.ProviderCache(s1, builder.build());
            }
         }
      }

      public void save(Path p_236143_, Path p_236144_, String p_236145_) {
         try (BufferedWriter bufferedwriter = Files.newBufferedWriter(p_236144_, StandardCharsets.UTF_8)) {
            bufferedwriter.write("// ");
            bufferedwriter.write(this.version);
            bufferedwriter.write(9);
            bufferedwriter.write(p_236145_);
            bufferedwriter.newLine();

            // Forge: Standardize order of entries
            for(Map.Entry<Path, HashCode> entry : this.data.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
               bufferedwriter.write(entry.getValue().toString());
               bufferedwriter.write(32);
               bufferedwriter.write(p_236143_.relativize(entry.getKey()).toString().replace("\\", "/")); // Forge: Standardize file paths.
               bufferedwriter.newLine();
            }
         } catch (IOException ioexception) {
            HashCache.LOGGER.warn("Unable write cachefile {}: {}", p_236144_, ioexception);
         }

      }
   }

   static record ProviderCacheBuilder(String version, ConcurrentMap<Path, HashCode> data) {
      ProviderCacheBuilder(String p_254186_) {
         this(p_254186_, new ConcurrentHashMap<>());
      }

      public void put(Path p_254121_, HashCode p_254288_) {
         this.data.put(p_254121_, p_254288_);
      }

      public HashCache.ProviderCache build() {
         return new HashCache.ProviderCache(this.version, ImmutableMap.copyOf(this.data));
      }
   }

   @FunctionalInterface
   public interface UpdateFunction {
      CompletableFuture<?> update(CachedOutput p_253936_);
   }

   public static record UpdateResult(String providerId, HashCache.ProviderCache cache, int writes) {
   }
}
