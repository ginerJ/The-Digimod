package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;

public interface LightEventListener {
   void checkBlock(BlockPos p_164454_);

   boolean hasLightWork();

   int runLightUpdates();

   default void updateSectionStatus(BlockPos p_75835_, boolean p_75836_) {
      this.updateSectionStatus(SectionPos.of(p_75835_), p_75836_);
   }

   void updateSectionStatus(SectionPos p_75837_, boolean p_75838_);

   void setLightEnabled(ChunkPos p_164452_, boolean p_164453_);

   void propagateLightSources(ChunkPos p_285263_);
}