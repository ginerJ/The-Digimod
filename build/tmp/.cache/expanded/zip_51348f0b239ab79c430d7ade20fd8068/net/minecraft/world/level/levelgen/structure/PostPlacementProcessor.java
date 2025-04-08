package net.minecraft.world.level.levelgen.structure;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;

@FunctionalInterface
public interface PostPlacementProcessor {
   PostPlacementProcessor NONE = (p_226518_, p_226519_, p_226520_, p_226521_, p_226522_, p_226523_, p_226524_) -> {
   };

   void afterPlace(WorldGenLevel p_226526_, StructureManager p_226527_, ChunkGenerator p_226528_, RandomSource p_226529_, BoundingBox p_226530_, ChunkPos p_226531_, PiecesContainer p_226532_);
}