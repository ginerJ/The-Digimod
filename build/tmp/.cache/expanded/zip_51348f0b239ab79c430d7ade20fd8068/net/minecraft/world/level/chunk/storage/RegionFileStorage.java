package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.util.ExceptionCollector;
import net.minecraft.world.level.ChunkPos;

public final class RegionFileStorage implements AutoCloseable {
   public static final String ANVIL_EXTENSION = ".mca";
   private static final int MAX_CACHE_SIZE = 256;
   private final Long2ObjectLinkedOpenHashMap<RegionFile> regionCache = new Long2ObjectLinkedOpenHashMap<>();
   private final Path folder;
   private final boolean sync;

   RegionFileStorage(Path p_196954_, boolean p_196955_) {
      this.folder = p_196954_;
      this.sync = p_196955_;
   }

   private RegionFile getRegionFile(ChunkPos p_63712_) throws IOException {
      long i = ChunkPos.asLong(p_63712_.getRegionX(), p_63712_.getRegionZ());
      RegionFile regionfile = this.regionCache.getAndMoveToFirst(i);
      if (regionfile != null) {
         return regionfile;
      } else {
         if (this.regionCache.size() >= 256) {
            this.regionCache.removeLast().close();
         }

         FileUtil.createDirectoriesSafe(this.folder);
         Path path = this.folder.resolve("r." + p_63712_.getRegionX() + "." + p_63712_.getRegionZ() + ".mca");
         RegionFile regionfile1 = new RegionFile(path, this.folder, this.sync);
         this.regionCache.putAndMoveToFirst(i, regionfile1);
         return regionfile1;
      }
   }

   @Nullable
   public CompoundTag read(ChunkPos p_63707_) throws IOException {
      RegionFile regionfile = this.getRegionFile(p_63707_);

      try (DataInputStream datainputstream = regionfile.getChunkDataInputStream(p_63707_)) {
         return datainputstream == null ? null : NbtIo.read(datainputstream);
      }
   }

   public void scanChunk(ChunkPos p_196957_, StreamTagVisitor p_196958_) throws IOException {
      RegionFile regionfile = this.getRegionFile(p_196957_);

      try (DataInputStream datainputstream = regionfile.getChunkDataInputStream(p_196957_)) {
         if (datainputstream != null) {
            NbtIo.parse(datainputstream, p_196958_);
         }
      }

   }

   protected void write(ChunkPos p_63709_, @Nullable CompoundTag p_63710_) throws IOException {
      RegionFile regionfile = this.getRegionFile(p_63709_);
      if (p_63710_ == null) {
         regionfile.clear(p_63709_);
      } else {
         try (DataOutputStream dataoutputstream = regionfile.getChunkDataOutputStream(p_63709_)) {
            NbtIo.write(p_63710_, dataoutputstream);
         }
      }

   }

   public void close() throws IOException {
      ExceptionCollector<IOException> exceptioncollector = new ExceptionCollector<>();

      for(RegionFile regionfile : this.regionCache.values()) {
         try {
            regionfile.close();
         } catch (IOException ioexception) {
            exceptioncollector.add(ioexception);
         }
      }

      exceptioncollector.throwIfPresent();
   }

   public void flush() throws IOException {
      for(RegionFile regionfile : this.regionCache.values()) {
         regionfile.flush();
      }

   }
}