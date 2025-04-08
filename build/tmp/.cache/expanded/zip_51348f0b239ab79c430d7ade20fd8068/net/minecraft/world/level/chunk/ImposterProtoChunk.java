package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.TickContainerAccess;

public class ImposterProtoChunk extends ProtoChunk {
   private final LevelChunk wrapped;
   private final boolean allowWrites;

   public ImposterProtoChunk(LevelChunk p_187920_, boolean p_187921_) {
      super(p_187920_.getPos(), UpgradeData.EMPTY, p_187920_.levelHeightAccessor, p_187920_.getLevel().registryAccess().registryOrThrow(Registries.BIOME), p_187920_.getBlendingData());
      this.wrapped = p_187920_;
      this.allowWrites = p_187921_;
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos p_62744_) {
      return this.wrapped.getBlockEntity(p_62744_);
   }

   public BlockState getBlockState(BlockPos p_62749_) {
      return this.wrapped.getBlockState(p_62749_);
   }

   public FluidState getFluidState(BlockPos p_62736_) {
      return this.wrapped.getFluidState(p_62736_);
   }

   public int getMaxLightLevel() {
      return this.wrapped.getMaxLightLevel();
   }

   public LevelChunkSection getSection(int p_187932_) {
      return this.allowWrites ? this.wrapped.getSection(p_187932_) : super.getSection(p_187932_);
   }

   @Nullable
   public BlockState setBlockState(BlockPos p_62722_, BlockState p_62723_, boolean p_62724_) {
      return this.allowWrites ? this.wrapped.setBlockState(p_62722_, p_62723_, p_62724_) : null;
   }

   public void setBlockEntity(BlockEntity p_156358_) {
      if (this.allowWrites) {
         this.wrapped.setBlockEntity(p_156358_);
      }

   }

   public void addEntity(Entity p_62692_) {
      if (this.allowWrites) {
         this.wrapped.addEntity(p_62692_);
      }

   }

   public void setStatus(ChunkStatus p_62698_) {
      if (this.allowWrites) {
         super.setStatus(p_62698_);
      }

   }

   public LevelChunkSection[] getSections() {
      return this.wrapped.getSections();
   }

   public void setHeightmap(Heightmap.Types p_62706_, long[] p_62707_) {
   }

   private Heightmap.Types fixType(Heightmap.Types p_62742_) {
      if (p_62742_ == Heightmap.Types.WORLD_SURFACE_WG) {
         return Heightmap.Types.WORLD_SURFACE;
      } else {
         return p_62742_ == Heightmap.Types.OCEAN_FLOOR_WG ? Heightmap.Types.OCEAN_FLOOR : p_62742_;
      }
   }

   public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types p_187928_) {
      return this.wrapped.getOrCreateHeightmapUnprimed(p_187928_);
   }

   public int getHeight(Heightmap.Types p_62702_, int p_62703_, int p_62704_) {
      return this.wrapped.getHeight(this.fixType(p_62702_), p_62703_, p_62704_);
   }

   public Holder<Biome> getNoiseBiome(int p_204430_, int p_204431_, int p_204432_) {
      return this.wrapped.getNoiseBiome(p_204430_, p_204431_, p_204432_);
   }

   public ChunkPos getPos() {
      return this.wrapped.getPos();
   }

   @Nullable
   public StructureStart getStartForStructure(Structure p_223400_) {
      return this.wrapped.getStartForStructure(p_223400_);
   }

   public void setStartForStructure(Structure p_223405_, StructureStart p_223406_) {
   }

   public Map<Structure, StructureStart> getAllStarts() {
      return this.wrapped.getAllStarts();
   }

   public void setAllStarts(Map<Structure, StructureStart> p_62726_) {
   }

   public LongSet getReferencesForStructure(Structure p_223408_) {
      return this.wrapped.getReferencesForStructure(p_223408_);
   }

   public void addReferenceForStructure(Structure p_223402_, long p_223403_) {
   }

   public Map<Structure, LongSet> getAllReferences() {
      return this.wrapped.getAllReferences();
   }

   public void setAllReferences(Map<Structure, LongSet> p_62738_) {
   }

   public void setUnsaved(boolean p_62730_) {
      this.wrapped.setUnsaved(p_62730_);
   }

   public boolean isUnsaved() {
      return false;
   }

   public ChunkStatus getStatus() {
      return this.wrapped.getStatus();
   }

   public void removeBlockEntity(BlockPos p_62747_) {
   }

   public void markPosForPostprocessing(BlockPos p_62752_) {
   }

   public void setBlockEntityNbt(CompoundTag p_62728_) {
   }

   @Nullable
   public CompoundTag getBlockEntityNbt(BlockPos p_62757_) {
      return this.wrapped.getBlockEntityNbt(p_62757_);
   }

   @Nullable
   public CompoundTag getBlockEntityNbtForSaving(BlockPos p_62760_) {
      return this.wrapped.getBlockEntityNbtForSaving(p_62760_);
   }

   public void findBlocks(Predicate<BlockState> p_285465_, BiConsumer<BlockPos, BlockState> p_285061_) {
      this.wrapped.findBlocks(p_285465_, p_285061_);
   }

   @Override
   public void findBlocks(java.util.function.BiPredicate<BlockState, BlockPos> p_285343_, BiConsumer<BlockPos, BlockState> p_285030_) {
      this.wrapped.findBlocks(p_285343_, p_285030_);
   }

   public TickContainerAccess<Block> getBlockTicks() {
      return this.allowWrites ? this.wrapped.getBlockTicks() : BlackholeTickAccess.emptyContainer();
   }

   public TickContainerAccess<Fluid> getFluidTicks() {
      return this.allowWrites ? this.wrapped.getFluidTicks() : BlackholeTickAccess.emptyContainer();
   }

   public ChunkAccess.TicksToSave getTicksForSerialization() {
      return this.wrapped.getTicksForSerialization();
   }

   @Nullable
   public BlendingData getBlendingData() {
      return this.wrapped.getBlendingData();
   }

   public void setBlendingData(BlendingData p_187930_) {
      this.wrapped.setBlendingData(p_187930_);
   }

   public CarvingMask getCarvingMask(GenerationStep.Carving p_187926_) {
      if (this.allowWrites) {
         return super.getCarvingMask(p_187926_);
      } else {
         throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("Meaningless in this context"));
      }
   }

   public CarvingMask getOrCreateCarvingMask(GenerationStep.Carving p_187934_) {
      if (this.allowWrites) {
         return super.getOrCreateCarvingMask(p_187934_);
      } else {
         throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("Meaningless in this context"));
      }
   }

   public LevelChunk getWrapped() {
      return this.wrapped;
   }

   public boolean isLightCorrect() {
      return this.wrapped.isLightCorrect();
   }

   public void setLightCorrect(boolean p_62740_) {
      this.wrapped.setLightCorrect(p_62740_);
   }

   public void fillBiomesFromNoise(BiomeResolver p_187923_, Climate.Sampler p_187924_) {
      if (this.allowWrites) {
         this.wrapped.fillBiomesFromNoise(p_187923_, p_187924_);
      }

   }

   public void initializeLightSources() {
      this.wrapped.initializeLightSources();
   }

   public ChunkSkyLightSources getSkyLightSources() {
      return this.wrapped.getSkyLightSources();
   }
}
