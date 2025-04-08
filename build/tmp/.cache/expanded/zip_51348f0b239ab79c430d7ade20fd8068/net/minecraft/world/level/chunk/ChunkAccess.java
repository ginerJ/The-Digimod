package net.minecraft.world.level.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListenerRegistry;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.SerializableTickContainer;
import net.minecraft.world.ticks.TickContainerAccess;
import org.slf4j.Logger;

public abstract class ChunkAccess implements BlockGetter, BiomeManager.NoiseBiomeSource, LightChunk, StructureAccess {
   public static final int NO_FILLED_SECTION = -1;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final LongSet EMPTY_REFERENCE_SET = new LongOpenHashSet();
   protected final ShortList[] postProcessing;
   protected volatile boolean unsaved;
   private volatile boolean isLightCorrect;
   protected final ChunkPos chunkPos;
   private long inhabitedTime;
   /** @deprecated */
   @Nullable
   @Deprecated
   private BiomeGenerationSettings carverBiomeSettings;
   @Nullable
   protected NoiseChunk noiseChunk;
   protected final UpgradeData upgradeData;
   @Nullable
   protected BlendingData blendingData;
   protected final Map<Heightmap.Types, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Types.class);
   protected ChunkSkyLightSources skyLightSources;
   private final Map<Structure, StructureStart> structureStarts = Maps.newHashMap();
   private final Map<Structure, LongSet> structuresRefences = Maps.newHashMap();
   protected final Map<BlockPos, CompoundTag> pendingBlockEntities = Maps.newHashMap();
   protected final Map<BlockPos, BlockEntity> blockEntities = Maps.newHashMap();
   protected final LevelHeightAccessor levelHeightAccessor;
   protected final LevelChunkSection[] sections;

   public ChunkAccess(ChunkPos p_187621_, UpgradeData p_187622_, LevelHeightAccessor p_187623_, Registry<Biome> p_187624_, long p_187625_, @Nullable LevelChunkSection[] p_187626_, @Nullable BlendingData p_187627_) {
      this.chunkPos = p_187621_;
      this.upgradeData = p_187622_;
      this.levelHeightAccessor = p_187623_;
      this.sections = new LevelChunkSection[p_187623_.getSectionsCount()];
      this.inhabitedTime = p_187625_;
      this.postProcessing = new ShortList[p_187623_.getSectionsCount()];
      this.blendingData = p_187627_;
      this.skyLightSources = new ChunkSkyLightSources(p_187623_);
      if (p_187626_ != null) {
         if (this.sections.length == p_187626_.length) {
            System.arraycopy(p_187626_, 0, this.sections, 0, this.sections.length);
         } else {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", p_187626_.length, this.sections.length);
         }
      }

      replaceMissingSections(p_187624_, this.sections);
   }

   private static void replaceMissingSections(Registry<Biome> p_281389_, LevelChunkSection[] p_282796_) {
      for(int i = 0; i < p_282796_.length; ++i) {
         if (p_282796_[i] == null) {
            p_282796_[i] = new LevelChunkSection(p_281389_);
         }
      }

   }

   public GameEventListenerRegistry getListenerRegistry(int p_251437_) {
      return GameEventListenerRegistry.NOOP;
   }

   @Nullable
   public abstract BlockState setBlockState(BlockPos p_62087_, BlockState p_62088_, boolean p_62089_);

   public abstract void setBlockEntity(BlockEntity p_156114_);

   public abstract void addEntity(Entity p_62078_);

   public int getHighestFilledSectionIndex() {
      LevelChunkSection[] alevelchunksection = this.getSections();

      for(int i = alevelchunksection.length - 1; i >= 0; --i) {
         LevelChunkSection levelchunksection = alevelchunksection[i];
         if (!levelchunksection.hasOnlyAir()) {
            return i;
         }
      }

      return -1;
   }

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public int getHighestSectionPosition() {
      int i = this.getHighestFilledSectionIndex();
      return i == -1 ? this.getMinBuildHeight() : SectionPos.sectionToBlockCoord(this.getSectionYFromSectionIndex(i));
   }

   public Set<BlockPos> getBlockEntitiesPos() {
      Set<BlockPos> set = Sets.newHashSet(this.pendingBlockEntities.keySet());
      set.addAll(this.blockEntities.keySet());
      return set;
   }

   public LevelChunkSection[] getSections() {
      return this.sections;
   }

   public LevelChunkSection getSection(int p_187657_) {
      return this.getSections()[p_187657_];
   }

   public Collection<Map.Entry<Heightmap.Types, Heightmap>> getHeightmaps() {
      return Collections.unmodifiableSet(this.heightmaps.entrySet());
   }

   public void setHeightmap(Heightmap.Types p_62083_, long[] p_62084_) {
      this.getOrCreateHeightmapUnprimed(p_62083_).setRawData(this, p_62083_, p_62084_);
   }

   public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types p_62079_) {
      return this.heightmaps.computeIfAbsent(p_62079_, (p_187665_) -> {
         return new Heightmap(this, p_187665_);
      });
   }

   public boolean hasPrimedHeightmap(Heightmap.Types p_187659_) {
      return this.heightmaps.get(p_187659_) != null;
   }

   public int getHeight(Heightmap.Types p_62080_, int p_62081_, int p_62082_) {
      Heightmap heightmap = this.heightmaps.get(p_62080_);
      if (heightmap == null) {
         if (SharedConstants.IS_RUNNING_IN_IDE && this instanceof LevelChunk) {
            LOGGER.error("Unprimed heightmap: " + p_62080_ + " " + p_62081_ + " " + p_62082_);
         }

         Heightmap.primeHeightmaps(this, EnumSet.of(p_62080_));
         heightmap = this.heightmaps.get(p_62080_);
      }

      return heightmap.getFirstAvailable(p_62081_ & 15, p_62082_ & 15) - 1;
   }

   public ChunkPos getPos() {
      return this.chunkPos;
   }

   @Nullable
   public StructureStart getStartForStructure(Structure p_223005_) {
      return this.structureStarts.get(p_223005_);
   }

   public void setStartForStructure(Structure p_223010_, StructureStart p_223011_) {
      this.structureStarts.put(p_223010_, p_223011_);
      this.unsaved = true;
   }

   public Map<Structure, StructureStart> getAllStarts() {
      return Collections.unmodifiableMap(this.structureStarts);
   }

   public void setAllStarts(Map<Structure, StructureStart> p_62090_) {
      this.structureStarts.clear();
      this.structureStarts.putAll(p_62090_);
      this.unsaved = true;
   }

   public LongSet getReferencesForStructure(Structure p_223017_) {
      return this.structuresRefences.getOrDefault(p_223017_, EMPTY_REFERENCE_SET);
   }

   public void addReferenceForStructure(Structure p_223007_, long p_223008_) {
      this.structuresRefences.computeIfAbsent(p_223007_, (p_223019_) -> {
         return new LongOpenHashSet();
      }).add(p_223008_);
      this.unsaved = true;
   }

   public Map<Structure, LongSet> getAllReferences() {
      return Collections.unmodifiableMap(this.structuresRefences);
   }

   public void setAllReferences(Map<Structure, LongSet> p_187663_) {
      this.structuresRefences.clear();
      this.structuresRefences.putAll(p_187663_);
      this.unsaved = true;
   }

   public boolean isYSpaceEmpty(int p_62075_, int p_62076_) {
      if (p_62075_ < this.getMinBuildHeight()) {
         p_62075_ = this.getMinBuildHeight();
      }

      if (p_62076_ >= this.getMaxBuildHeight()) {
         p_62076_ = this.getMaxBuildHeight() - 1;
      }

      for(int i = p_62075_; i <= p_62076_; i += 16) {
         if (!this.getSection(this.getSectionIndex(i)).hasOnlyAir()) {
            return false;
         }
      }

      return true;
   }

   public void setUnsaved(boolean p_62094_) {
      this.unsaved = p_62094_;
   }

   public boolean isUnsaved() {
      return this.unsaved;
   }

   public abstract ChunkStatus getStatus();

   public ChunkStatus getHighestGeneratedStatus() {
      ChunkStatus chunkstatus = this.getStatus();
      BelowZeroRetrogen belowzeroretrogen = this.getBelowZeroRetrogen();
      if (belowzeroretrogen != null) {
         ChunkStatus chunkstatus1 = belowzeroretrogen.targetStatus();
         return chunkstatus1.isOrAfter(chunkstatus) ? chunkstatus1 : chunkstatus;
      } else {
         return chunkstatus;
      }
   }

   public abstract void removeBlockEntity(BlockPos p_62101_);

   public void markPosForPostprocessing(BlockPos p_62102_) {
      LOGGER.warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", (Object)p_62102_);
   }

   public ShortList[] getPostProcessing() {
      return this.postProcessing;
   }

   public void addPackedPostProcess(short p_62092_, int p_62093_) {
      getOrCreateOffsetList(this.getPostProcessing(), p_62093_).add(p_62092_);
   }

   public void setBlockEntityNbt(CompoundTag p_62091_) {
      this.pendingBlockEntities.put(BlockEntity.getPosFromTag(p_62091_), p_62091_);
   }

   @Nullable
   public CompoundTag getBlockEntityNbt(BlockPos p_62103_) {
      return this.pendingBlockEntities.get(p_62103_);
   }

   @Nullable
   public abstract CompoundTag getBlockEntityNbtForSaving(BlockPos p_62104_);

   public final void findBlockLightSources(BiConsumer<BlockPos, BlockState> p_285269_) {
      this.findBlocks((p_284897_, pos) -> {
         return p_284897_.getLightEmission(this, pos) != 0;
      }, p_285269_);
   }

   public void findBlocks(Predicate<BlockState> p_285343_, BiConsumer<BlockPos, BlockState> p_285030_) {
      findBlocks((state, pos) -> p_285343_.test(state), p_285030_);
   }

   public void findBlocks(java.util.function.BiPredicate<BlockState, BlockPos> p_285343_, BiConsumer<BlockPos, BlockState> p_285030_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i = this.getMinSection(); i < this.getMaxSection(); ++i) {
         LevelChunkSection levelchunksection = this.getSection(this.getSectionIndexFromSectionY(i));
         if (levelchunksection.maybeHas((state) -> p_285343_.test(state, BlockPos.ZERO))) {
            BlockPos blockpos = SectionPos.of(this.chunkPos, i).origin();

            for(int j = 0; j < 16; ++j) {
               for(int k = 0; k < 16; ++k) {
                  for(int l = 0; l < 16; ++l) {
                     BlockState blockstate = levelchunksection.getBlockState(l, j, k);
                     blockpos$mutableblockpos.setWithOffset(blockpos, l, j, k);
                     if (p_285343_.test(blockstate, blockpos$mutableblockpos.immutable())) {
                        p_285030_.accept(blockpos$mutableblockpos, blockstate);
                     }
                  }
               }
            }
         }
      }

   }

   public abstract TickContainerAccess<Block> getBlockTicks();

   public abstract TickContainerAccess<Fluid> getFluidTicks();

   public abstract ChunkAccess.TicksToSave getTicksForSerialization();

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public boolean isOldNoiseGeneration() {
      return this.blendingData != null;
   }

   @Nullable
   public BlendingData getBlendingData() {
      return this.blendingData;
   }

   public void setBlendingData(BlendingData p_187646_) {
      this.blendingData = p_187646_;
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void incrementInhabitedTime(long p_187633_) {
      this.inhabitedTime += p_187633_;
   }

   public void setInhabitedTime(long p_62099_) {
      this.inhabitedTime = p_62099_;
   }

   public static ShortList getOrCreateOffsetList(ShortList[] p_62096_, int p_62097_) {
      if (p_62096_[p_62097_] == null) {
         p_62096_[p_62097_] = new ShortArrayList();
      }

      return p_62096_[p_62097_];
   }

   public boolean isLightCorrect() {
      return this.isLightCorrect;
   }

   public void setLightCorrect(boolean p_62100_) {
      this.isLightCorrect = p_62100_;
      this.setUnsaved(true);
   }

   public int getMinBuildHeight() {
      return this.levelHeightAccessor.getMinBuildHeight();
   }

   public int getHeight() {
      return this.levelHeightAccessor.getHeight();
   }

   public NoiseChunk getOrCreateNoiseChunk(Function<ChunkAccess, NoiseChunk> p_223013_) {
      if (this.noiseChunk == null) {
         this.noiseChunk = p_223013_.apply(this);
      }

      return this.noiseChunk;
   }

   /** @deprecated */
   @Deprecated
   public BiomeGenerationSettings carverBiome(Supplier<BiomeGenerationSettings> p_223015_) {
      if (this.carverBiomeSettings == null) {
         this.carverBiomeSettings = p_223015_.get();
      }

      return this.carverBiomeSettings;
   }

   public Holder<Biome> getNoiseBiome(int p_204347_, int p_204348_, int p_204349_) {
      try {
         int i = QuartPos.fromBlock(this.getMinBuildHeight());
         int k = i + QuartPos.fromBlock(this.getHeight()) - 1;
         int l = Mth.clamp(p_204348_, i, k);
         int j = this.getSectionIndex(QuartPos.toBlock(l));
         return this.sections[j].getNoiseBiome(p_204347_ & 3, l & 3, p_204349_ & 3);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting biome");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Biome being got");
         crashreportcategory.setDetail("Location", () -> {
            return CrashReportCategory.formatLocation(this, p_204347_, p_204348_, p_204349_);
         });
         throw new ReportedException(crashreport);
      }
   }

   public void fillBiomesFromNoise(BiomeResolver p_187638_, Climate.Sampler p_187639_) {
      ChunkPos chunkpos = this.getPos();
      int i = QuartPos.fromBlock(chunkpos.getMinBlockX());
      int j = QuartPos.fromBlock(chunkpos.getMinBlockZ());
      LevelHeightAccessor levelheightaccessor = this.getHeightAccessorForGeneration();

      for(int k = levelheightaccessor.getMinSection(); k < levelheightaccessor.getMaxSection(); ++k) {
         LevelChunkSection levelchunksection = this.getSection(this.getSectionIndexFromSectionY(k));
         int l = QuartPos.fromSection(k);
         levelchunksection.fillBiomesFromNoise(p_187638_, p_187639_, i, l, j);
      }

   }

   public boolean hasAnyStructureReferences() {
      return !this.getAllReferences().isEmpty();
   }

   @Nullable
   public BelowZeroRetrogen getBelowZeroRetrogen() {
      return null;
   }

   public boolean isUpgrading() {
      return this.getBelowZeroRetrogen() != null;
   }

   public LevelHeightAccessor getHeightAccessorForGeneration() {
      return this;
   }

   public void initializeLightSources() {
      this.skyLightSources.fillFrom(this);
   }

   public ChunkSkyLightSources getSkyLightSources() {
      return this.skyLightSources;
   }

   public static record TicksToSave(SerializableTickContainer<Block> blocks, SerializableTickContainer<Fluid> fluids) {
   }

   @Nullable
   public net.minecraft.world.level.LevelAccessor getWorldForge() { return null; }
}
