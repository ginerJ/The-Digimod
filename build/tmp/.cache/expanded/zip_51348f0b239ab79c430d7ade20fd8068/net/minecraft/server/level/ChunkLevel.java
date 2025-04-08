package net.minecraft.server.level;

import net.minecraft.world.level.chunk.ChunkStatus;

public class ChunkLevel {
   private static final int FULL_CHUNK_LEVEL = 33;
   private static final int BLOCK_TICKING_LEVEL = 32;
   private static final int ENTITY_TICKING_LEVEL = 31;
   public static final int MAX_LEVEL = 33 + ChunkStatus.maxDistance();

   public static ChunkStatus generationStatus(int p_287738_) {
      return p_287738_ < 33 ? ChunkStatus.FULL : ChunkStatus.getStatusAroundFullChunk(p_287738_ - 33);
   }

   public static int byStatus(ChunkStatus p_287771_) {
      return 33 + ChunkStatus.getDistance(p_287771_);
   }

   public static FullChunkStatus fullStatus(int p_287750_) {
      if (p_287750_ <= 31) {
         return FullChunkStatus.ENTITY_TICKING;
      } else if (p_287750_ <= 32) {
         return FullChunkStatus.BLOCK_TICKING;
      } else {
         return p_287750_ <= 33 ? FullChunkStatus.FULL : FullChunkStatus.INACCESSIBLE;
      }
   }

   public static int byStatus(FullChunkStatus p_287601_) {
      int i;
      switch (p_287601_) {
         case INACCESSIBLE:
            i = MAX_LEVEL;
            break;
         case FULL:
            i = 33;
            break;
         case BLOCK_TICKING:
            i = 32;
            break;
         case ENTITY_TICKING:
            i = 31;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return i;
   }

   public static boolean isEntityTicking(int p_287767_) {
      return p_287767_ <= 31;
   }

   public static boolean isBlockTicking(int p_287696_) {
      return p_287696_ <= 32;
   }

   public static boolean isLoaded(int p_287635_) {
      return p_287635_ <= MAX_LEVEL;
   }
}