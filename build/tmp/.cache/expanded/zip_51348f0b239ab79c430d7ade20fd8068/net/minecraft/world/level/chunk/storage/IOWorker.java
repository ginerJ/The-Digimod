package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.IOException;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.util.Unit;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.util.thread.StrictQueue;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;

public class IOWorker implements ChunkScanAccess, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final AtomicBoolean shutdownRequested = new AtomicBoolean();
   private final ProcessorMailbox<StrictQueue.IntRunnable> mailbox;
   private final RegionFileStorage storage;
   private final Map<ChunkPos, IOWorker.PendingStore> pendingWrites = Maps.newLinkedHashMap();
   private final Long2ObjectLinkedOpenHashMap<CompletableFuture<BitSet>> regionCacheForBlender = new Long2ObjectLinkedOpenHashMap<>();
   private static final int REGION_CACHE_SIZE = 1024;

   protected IOWorker(Path p_196930_, boolean p_196931_, String p_196932_) {
      this.storage = new RegionFileStorage(p_196930_, p_196931_);
      this.mailbox = new ProcessorMailbox<>(new StrictQueue.FixedPriorityQueue(IOWorker.Priority.values().length), Util.ioPool(), "IOWorker-" + p_196932_);
   }

   public boolean isOldChunkAround(ChunkPos p_223472_, int p_223473_) {
      ChunkPos chunkpos = new ChunkPos(p_223472_.x - p_223473_, p_223472_.z - p_223473_);
      ChunkPos chunkpos1 = new ChunkPos(p_223472_.x + p_223473_, p_223472_.z + p_223473_);

      for(int i = chunkpos.getRegionX(); i <= chunkpos1.getRegionX(); ++i) {
         for(int j = chunkpos.getRegionZ(); j <= chunkpos1.getRegionZ(); ++j) {
            BitSet bitset = this.getOrCreateOldDataForRegion(i, j).join();
            if (!bitset.isEmpty()) {
               ChunkPos chunkpos2 = ChunkPos.minFromRegion(i, j);
               int k = Math.max(chunkpos.x - chunkpos2.x, 0);
               int l = Math.max(chunkpos.z - chunkpos2.z, 0);
               int i1 = Math.min(chunkpos1.x - chunkpos2.x, 31);
               int j1 = Math.min(chunkpos1.z - chunkpos2.z, 31);

               for(int k1 = k; k1 <= i1; ++k1) {
                  for(int l1 = l; l1 <= j1; ++l1) {
                     int i2 = l1 * 32 + k1;
                     if (bitset.get(i2)) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   private CompletableFuture<BitSet> getOrCreateOldDataForRegion(int p_223464_, int p_223465_) {
      long i = ChunkPos.asLong(p_223464_, p_223465_);
      synchronized(this.regionCacheForBlender) {
         CompletableFuture<BitSet> completablefuture = this.regionCacheForBlender.getAndMoveToFirst(i);
         if (completablefuture == null) {
            completablefuture = this.createOldDataForRegion(p_223464_, p_223465_);
            this.regionCacheForBlender.putAndMoveToFirst(i, completablefuture);
            if (this.regionCacheForBlender.size() > 1024) {
               this.regionCacheForBlender.removeLast();
            }
         }

         return completablefuture;
      }
   }

   private CompletableFuture<BitSet> createOldDataForRegion(int p_223490_, int p_223491_) {
      return CompletableFuture.supplyAsync(() -> {
         ChunkPos chunkpos = ChunkPos.minFromRegion(p_223490_, p_223491_);
         ChunkPos chunkpos1 = ChunkPos.maxFromRegion(p_223490_, p_223491_);
         BitSet bitset = new BitSet();
         ChunkPos.rangeClosed(chunkpos, chunkpos1).forEach((p_223480_) -> {
            CollectFields collectfields = new CollectFields(new FieldSelector(IntTag.TYPE, "DataVersion"), new FieldSelector(CompoundTag.TYPE, "blending_data"));

            try {
               this.scanChunk(p_223480_, collectfields).join();
            } catch (Exception exception) {
               LOGGER.warn("Failed to scan chunk {}", p_223480_, exception);
               return;
            }

            Tag tag = collectfields.getResult();
            if (tag instanceof CompoundTag compoundtag) {
               if (this.isOldChunk(compoundtag)) {
                  int i = p_223480_.getRegionLocalZ() * 32 + p_223480_.getRegionLocalX();
                  bitset.set(i);
               }
            }

         });
         return bitset;
      }, Util.backgroundExecutor());
   }

   private boolean isOldChunk(CompoundTag p_223485_) {
      return p_223485_.contains("DataVersion", 99) && p_223485_.getInt("DataVersion") >= 3441 ? p_223485_.contains("blending_data", 10) : true;
   }

   public CompletableFuture<Void> store(ChunkPos p_63539_, @Nullable CompoundTag p_63540_) {
      return this.submitTask(() -> {
         IOWorker.PendingStore ioworker$pendingstore = this.pendingWrites.computeIfAbsent(p_63539_, (p_223488_) -> {
            return new IOWorker.PendingStore(p_63540_);
         });
         ioworker$pendingstore.data = p_63540_;
         return Either.left(ioworker$pendingstore.result);
      }).thenCompose(Function.identity());
   }

   public CompletableFuture<Optional<CompoundTag>> loadAsync(ChunkPos p_156588_) {
      return this.submitTask(() -> {
         IOWorker.PendingStore ioworker$pendingstore = this.pendingWrites.get(p_156588_);
         if (ioworker$pendingstore != null) {
            return Either.left(Optional.ofNullable(ioworker$pendingstore.data));
         } else {
            try {
               CompoundTag compoundtag = this.storage.read(p_156588_);
               return Either.left(Optional.ofNullable(compoundtag));
            } catch (Exception exception) {
               LOGGER.warn("Failed to read chunk {}", p_156588_, exception);
               return Either.right(exception);
            }
         }
      });
   }

   public CompletableFuture<Void> synchronize(boolean p_182499_) {
      CompletableFuture<Void> completablefuture = this.submitTask(() -> {
         return Either.left(CompletableFuture.allOf(this.pendingWrites.values().stream().map((p_223475_) -> {
            return p_223475_.result;
         }).toArray((p_223462_) -> {
            return new CompletableFuture[p_223462_];
         })));
      }).thenCompose(Function.identity());
      return p_182499_ ? completablefuture.thenCompose((p_182494_) -> {
         return this.submitTask(() -> {
            try {
               this.storage.flush();
               return Either.left((Void)null);
            } catch (Exception exception) {
               LOGGER.warn("Failed to synchronize chunks", (Throwable)exception);
               return Either.right(exception);
            }
         });
      }) : completablefuture.thenCompose((p_223477_) -> {
         return this.submitTask(() -> {
            return Either.left((Void)null);
         });
      });
   }

   public CompletableFuture<Void> scanChunk(ChunkPos p_196939_, StreamTagVisitor p_196940_) {
      return this.submitTask(() -> {
         try {
            IOWorker.PendingStore ioworker$pendingstore = this.pendingWrites.get(p_196939_);
            if (ioworker$pendingstore != null) {
               if (ioworker$pendingstore.data != null) {
                  ioworker$pendingstore.data.acceptAsRoot(p_196940_);
               }
            } else {
               this.storage.scanChunk(p_196939_, p_196940_);
            }

            return Either.left((Void)null);
         } catch (Exception exception) {
            LOGGER.warn("Failed to bulk scan chunk {}", p_196939_, exception);
            return Either.right(exception);
         }
      });
   }

   private <T> CompletableFuture<T> submitTask(Supplier<Either<T, Exception>> p_63546_) {
      return this.mailbox.askEither((p_223483_) -> {
         return new StrictQueue.IntRunnable(IOWorker.Priority.FOREGROUND.ordinal(), () -> {
            if (!this.shutdownRequested.get()) {
               p_223483_.tell(p_63546_.get());
            }

            this.tellStorePending();
         });
      });
   }

   private void storePendingChunk() {
      if (!this.pendingWrites.isEmpty()) {
         Iterator<Map.Entry<ChunkPos, IOWorker.PendingStore>> iterator = this.pendingWrites.entrySet().iterator();
         Map.Entry<ChunkPos, IOWorker.PendingStore> entry = iterator.next();
         iterator.remove();
         this.runStore(entry.getKey(), entry.getValue());
         this.tellStorePending();
      }
   }

   private void tellStorePending() {
      this.mailbox.tell(new StrictQueue.IntRunnable(IOWorker.Priority.BACKGROUND.ordinal(), this::storePendingChunk));
   }

   private void runStore(ChunkPos p_63536_, IOWorker.PendingStore p_63537_) {
      try {
         this.storage.write(p_63536_, p_63537_.data);
         p_63537_.result.complete((Void)null);
      } catch (Exception exception) {
         LOGGER.error("Failed to store chunk {}", p_63536_, exception);
         p_63537_.result.completeExceptionally(exception);
      }

   }

   public void close() throws IOException {
      if (this.shutdownRequested.compareAndSet(false, true)) {
         this.mailbox.ask((p_223467_) -> {
            return new StrictQueue.IntRunnable(IOWorker.Priority.SHUTDOWN.ordinal(), () -> {
               p_223467_.tell(Unit.INSTANCE);
            });
         }).join();
         this.mailbox.close();

         try {
            this.storage.close();
         } catch (Exception exception) {
            LOGGER.error("Failed to close storage", (Throwable)exception);
         }

      }
   }

   static class PendingStore {
      @Nullable
      CompoundTag data;
      final CompletableFuture<Void> result = new CompletableFuture<>();

      public PendingStore(@Nullable CompoundTag p_63568_) {
         this.data = p_63568_;
      }
   }

   static enum Priority {
      FOREGROUND,
      BACKGROUND,
      SHUTDOWN;
   }
}