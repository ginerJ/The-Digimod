package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import org.slf4j.Logger;

public class SectionStorage<R> implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String SECTIONS_TAG = "Sections";
   private final IOWorker worker;
   private final Long2ObjectMap<Optional<R>> storage = new Long2ObjectOpenHashMap<>();
   private final LongLinkedOpenHashSet dirty = new LongLinkedOpenHashSet();
   private final Function<Runnable, Codec<R>> codec;
   private final Function<Runnable, R> factory;
   private final DataFixer fixerUpper;
   private final DataFixTypes type;
   private final RegistryAccess registryAccess;
   protected final LevelHeightAccessor levelHeightAccessor;

   public SectionStorage(Path p_223509_, Function<Runnable, Codec<R>> p_223510_, Function<Runnable, R> p_223511_, DataFixer p_223512_, DataFixTypes p_223513_, boolean p_223514_, RegistryAccess p_223515_, LevelHeightAccessor p_223516_) {
      this.codec = p_223510_;
      this.factory = p_223511_;
      this.fixerUpper = p_223512_;
      this.type = p_223513_;
      this.registryAccess = p_223515_;
      this.levelHeightAccessor = p_223516_;
      this.worker = new IOWorker(p_223509_, p_223514_, p_223509_.getFileName().toString());
   }

   protected void tick(BooleanSupplier p_63812_) {
      while(this.hasWork() && p_63812_.getAsBoolean()) {
         ChunkPos chunkpos = SectionPos.of(this.dirty.firstLong()).chunk();
         this.writeColumn(chunkpos);
      }

   }

   public boolean hasWork() {
      return !this.dirty.isEmpty();
   }

   @Nullable
   protected Optional<R> get(long p_63819_) {
      return this.storage.get(p_63819_);
   }

   protected Optional<R> getOrLoad(long p_63824_) {
      if (this.outsideStoredRange(p_63824_)) {
         return Optional.empty();
      } else {
         Optional<R> optional = this.get(p_63824_);
         if (optional != null) {
            return optional;
         } else {
            this.readColumn(SectionPos.of(p_63824_).chunk());
            optional = this.get(p_63824_);
            if (optional == null) {
               throw (IllegalStateException)Util.pauseInIde(new IllegalStateException());
            } else {
               return optional;
            }
         }
      }
   }

   protected boolean outsideStoredRange(long p_156631_) {
      int i = SectionPos.sectionToBlockCoord(SectionPos.y(p_156631_));
      return this.levelHeightAccessor.isOutsideBuildHeight(i);
   }

   protected R getOrCreate(long p_63828_) {
      if (this.outsideStoredRange(p_63828_)) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("sectionPos out of bounds"));
      } else {
         Optional<R> optional = this.getOrLoad(p_63828_);
         if (optional.isPresent()) {
            return optional.get();
         } else {
            R r = this.factory.apply(() -> {
               this.setDirty(p_63828_);
            });
            this.storage.put(p_63828_, Optional.of(r));
            return r;
         }
      }
   }

   private void readColumn(ChunkPos p_63815_) {
      Optional<CompoundTag> optional = this.tryRead(p_63815_).join();
      RegistryOps<Tag> registryops = RegistryOps.create(NbtOps.INSTANCE, this.registryAccess);
      this.readColumn(p_63815_, registryops, optional.orElse((CompoundTag)null));
   }

   private CompletableFuture<Optional<CompoundTag>> tryRead(ChunkPos p_223533_) {
      return this.worker.loadAsync(p_223533_).exceptionally((p_223526_) -> {
         if (p_223526_ instanceof IOException ioexception) {
            LOGGER.error("Error reading chunk {} data from disk", p_223533_, ioexception);
            return Optional.empty();
         } else {
            throw new CompletionException(p_223526_);
         }
      });
   }

   private <T> void readColumn(ChunkPos p_63802_, DynamicOps<T> p_63803_, @Nullable T p_63804_) {
      if (p_63804_ == null) {
         for(int i = this.levelHeightAccessor.getMinSection(); i < this.levelHeightAccessor.getMaxSection(); ++i) {
            this.storage.put(getKey(p_63802_, i), Optional.empty());
         }
      } else {
         Dynamic<T> dynamic1 = new Dynamic<>(p_63803_, p_63804_);
         int j = getVersion(dynamic1);
         int k = SharedConstants.getCurrentVersion().getDataVersion().getVersion();
         boolean flag = j != k;
         Dynamic<T> dynamic = this.type.update(this.fixerUpper, dynamic1, j, k);
         OptionalDynamic<T> optionaldynamic = dynamic.get("Sections");

         for(int l = this.levelHeightAccessor.getMinSection(); l < this.levelHeightAccessor.getMaxSection(); ++l) {
            long i1 = getKey(p_63802_, l);
            Optional<R> optional = optionaldynamic.get(Integer.toString(l)).result().flatMap((p_223519_) -> {
               return this.codec.apply(() -> {
                  this.setDirty(i1);
               }).parse(p_223519_).resultOrPartial(LOGGER::error);
            });
            this.storage.put(i1, optional);
            optional.ifPresent((p_223523_) -> {
               this.onSectionLoad(i1);
               if (flag) {
                  this.setDirty(i1);
               }

            });
         }
      }

   }

   private void writeColumn(ChunkPos p_63826_) {
      RegistryOps<Tag> registryops = RegistryOps.create(NbtOps.INSTANCE, this.registryAccess);
      Dynamic<Tag> dynamic = this.writeColumn(p_63826_, registryops);
      Tag tag = dynamic.getValue();
      if (tag instanceof CompoundTag) {
         this.worker.store(p_63826_, (CompoundTag)tag);
      } else {
         LOGGER.error("Expected compound tag, got {}", (Object)tag);
      }

   }

   private <T> Dynamic<T> writeColumn(ChunkPos p_63799_, DynamicOps<T> p_63800_) {
      Map<T, T> map = Maps.newHashMap();

      for(int i = this.levelHeightAccessor.getMinSection(); i < this.levelHeightAccessor.getMaxSection(); ++i) {
         long j = getKey(p_63799_, i);
         this.dirty.remove(j);
         Optional<R> optional = this.storage.get(j);
         if (optional != null && optional.isPresent()) {
            DataResult<T> dataresult = this.codec.apply(() -> {
               this.setDirty(j);
            }).encodeStart(p_63800_, optional.get());
            String s = Integer.toString(i);
            dataresult.resultOrPartial(LOGGER::error).ifPresent((p_223531_) -> {
               map.put(p_63800_.createString(s), p_223531_);
            });
         }
      }

      return new Dynamic<>(p_63800_, p_63800_.createMap(ImmutableMap.of(p_63800_.createString("Sections"), p_63800_.createMap(map), p_63800_.createString("DataVersion"), p_63800_.createInt(SharedConstants.getCurrentVersion().getDataVersion().getVersion()))));
   }

   private static long getKey(ChunkPos p_156628_, int p_156629_) {
      return SectionPos.asLong(p_156628_.x, p_156629_, p_156628_.z);
   }

   protected void onSectionLoad(long p_63813_) {
   }

   protected void setDirty(long p_63788_) {
      Optional<R> optional = this.storage.get(p_63788_);
      if (optional != null && optional.isPresent()) {
         this.dirty.add(p_63788_);
      } else {
         LOGGER.warn("No data for position: {}", (Object)SectionPos.of(p_63788_));
      }
   }

   private static int getVersion(Dynamic<?> p_63806_) {
      return p_63806_.get("DataVersion").asInt(1945);
   }

   public void flush(ChunkPos p_63797_) {
      if (this.hasWork()) {
         for(int i = this.levelHeightAccessor.getMinSection(); i < this.levelHeightAccessor.getMaxSection(); ++i) {
            long j = getKey(p_63797_, i);
            if (this.dirty.contains(j)) {
               this.writeColumn(p_63797_);
               return;
            }
         }
      }

   }

   public void close() throws IOException {
      this.worker.close();
   }
}