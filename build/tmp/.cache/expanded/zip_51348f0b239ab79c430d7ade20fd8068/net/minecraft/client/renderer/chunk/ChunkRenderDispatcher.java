package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderDispatcher {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_WORKERS_32_BIT = 4;
   private static final VertexFormat VERTEX_FORMAT = DefaultVertexFormat.BLOCK;
   private static final int MAX_HIGH_PRIORITY_QUOTA = 2;
   private final PriorityBlockingQueue<ChunkRenderDispatcher.RenderChunk.ChunkCompileTask> toBatchHighPriority = Queues.newPriorityBlockingQueue();
   private final Queue<ChunkRenderDispatcher.RenderChunk.ChunkCompileTask> toBatchLowPriority = Queues.newLinkedBlockingDeque();
   private int highPriorityQuota = 2;
   private final Queue<ChunkBufferBuilderPack> freeBuffers;
   private final Queue<Runnable> toUpload = Queues.newConcurrentLinkedQueue();
   private volatile int toBatchCount;
   private volatile int freeBufferCount;
   final ChunkBufferBuilderPack fixedBuffers;
   private final ProcessorMailbox<Runnable> mailbox;
   private final Executor executor;
   ClientLevel level;
   final LevelRenderer renderer;
   private Vec3 camera = Vec3.ZERO;

   public ChunkRenderDispatcher(ClientLevel p_194405_, LevelRenderer p_194406_, Executor p_194407_, boolean p_194408_, ChunkBufferBuilderPack p_194409_) {
      this(p_194405_, p_194406_, p_194407_, p_194408_, p_194409_, -1);
   }
   public ChunkRenderDispatcher(ClientLevel p_194405_, LevelRenderer p_194406_, Executor p_194407_, boolean p_194408_, ChunkBufferBuilderPack p_194409_, int countRenderBuilders) {
      this.level = p_194405_;
      this.renderer = p_194406_;
      int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3D) / (RenderType.chunkBufferLayers().stream().mapToInt(RenderType::bufferSize).sum() * 4) - 1);
      int j = Runtime.getRuntime().availableProcessors();
      int k = p_194408_ ? j : Math.min(j, 4);
      int l = countRenderBuilders < 0 ? Math.max(1, Math.min(k, i)) : countRenderBuilders;
      this.fixedBuffers = p_194409_;
      List<ChunkBufferBuilderPack> list = Lists.newArrayListWithExpectedSize(l);

      try {
         for(int i1 = 0; i1 < l; ++i1) {
            list.add(new ChunkBufferBuilderPack());
         }
      } catch (OutOfMemoryError outofmemoryerror) {
         LOGGER.warn("Allocated only {}/{} buffers", list.size(), l);
         int j1 = Math.min(list.size() * 2 / 3, list.size() - 1);

         for(int k1 = 0; k1 < j1; ++k1) {
            list.remove(list.size() - 1);
         }

         System.gc();
      }

      this.freeBuffers = Queues.newArrayDeque(list);
      this.freeBufferCount = this.freeBuffers.size();
      this.executor = p_194407_;
      this.mailbox = ProcessorMailbox.create(p_194407_, "Chunk Renderer");
      this.mailbox.tell(this::runTask);
   }

   public void setLevel(ClientLevel p_194411_) {
      this.level = p_194411_;
   }

   private void runTask() {
      if (!this.freeBuffers.isEmpty()) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask chunkrenderdispatcher$renderchunk$chunkcompiletask = this.pollTask();
         if (chunkrenderdispatcher$renderchunk$chunkcompiletask != null) {
            ChunkBufferBuilderPack chunkbufferbuilderpack = this.freeBuffers.poll();
            this.toBatchCount = this.toBatchHighPriority.size() + this.toBatchLowPriority.size();
            this.freeBufferCount = this.freeBuffers.size();
            CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName(chunkrenderdispatcher$renderchunk$chunkcompiletask.name(), () -> {
               return chunkrenderdispatcher$renderchunk$chunkcompiletask.doTask(chunkbufferbuilderpack);
            }), this.executor).thenCompose((p_194416_) -> {
               return p_194416_;
            }).whenComplete((p_234458_, p_234459_) -> {
               if (p_234459_ != null) {
                  Minecraft.getInstance().delayCrash(CrashReport.forThrowable(p_234459_, "Batching chunks"));
               } else {
                  this.mailbox.tell(() -> {
                     if (p_234458_ == ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL) {
                        chunkbufferbuilderpack.clearAll();
                     } else {
                        chunkbufferbuilderpack.discardAll();
                     }

                     this.freeBuffers.add(chunkbufferbuilderpack);
                     this.freeBufferCount = this.freeBuffers.size();
                     this.runTask();
                  });
               }
            });
         }
      }
   }

   @Nullable
   private ChunkRenderDispatcher.RenderChunk.ChunkCompileTask pollTask() {
      if (this.highPriorityQuota <= 0) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask chunkrenderdispatcher$renderchunk$chunkcompiletask = this.toBatchLowPriority.poll();
         if (chunkrenderdispatcher$renderchunk$chunkcompiletask != null) {
            this.highPriorityQuota = 2;
            return chunkrenderdispatcher$renderchunk$chunkcompiletask;
         }
      }

      ChunkRenderDispatcher.RenderChunk.ChunkCompileTask chunkrenderdispatcher$renderchunk$chunkcompiletask1 = this.toBatchHighPriority.poll();
      if (chunkrenderdispatcher$renderchunk$chunkcompiletask1 != null) {
         --this.highPriorityQuota;
         return chunkrenderdispatcher$renderchunk$chunkcompiletask1;
      } else {
         this.highPriorityQuota = 2;
         return this.toBatchLowPriority.poll();
      }
   }

   public String getStats() {
      return String.format(Locale.ROOT, "pC: %03d, pU: %02d, aB: %02d", this.toBatchCount, this.toUpload.size(), this.freeBufferCount);
   }

   public int getToBatchCount() {
      return this.toBatchCount;
   }

   public int getToUpload() {
      return this.toUpload.size();
   }

   public int getFreeBufferCount() {
      return this.freeBufferCount;
   }

   public void setCamera(Vec3 p_112694_) {
      this.camera = p_112694_;
   }

   public Vec3 getCameraPosition() {
      return this.camera;
   }

   public void uploadAllPendingUploads() {
      Runnable runnable;
      while((runnable = this.toUpload.poll()) != null) {
         runnable.run();
      }

   }

   public void rebuildChunkSync(ChunkRenderDispatcher.RenderChunk p_200432_, RenderRegionCache p_200433_) {
      p_200432_.compileSync(p_200433_);
   }

   public void blockUntilClear() {
      this.clearBatchQueue();
   }

   public void schedule(ChunkRenderDispatcher.RenderChunk.ChunkCompileTask p_112710_) {
      this.mailbox.tell(() -> {
         if (p_112710_.isHighPriority) {
            this.toBatchHighPriority.offer(p_112710_);
         } else {
            this.toBatchLowPriority.offer(p_112710_);
         }

         this.toBatchCount = this.toBatchHighPriority.size() + this.toBatchLowPriority.size();
         this.runTask();
      });
   }

   public CompletableFuture<Void> uploadChunkLayer(BufferBuilder.RenderedBuffer p_234451_, VertexBuffer p_234452_) {
      return CompletableFuture.runAsync(() -> {
         if (!p_234452_.isInvalid()) {
            p_234452_.bind();
            p_234452_.upload(p_234451_);
            VertexBuffer.unbind();
         }
      }, this.toUpload::add);
   }

   private void clearBatchQueue() {
      while(!this.toBatchHighPriority.isEmpty()) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask chunkrenderdispatcher$renderchunk$chunkcompiletask = this.toBatchHighPriority.poll();
         if (chunkrenderdispatcher$renderchunk$chunkcompiletask != null) {
            chunkrenderdispatcher$renderchunk$chunkcompiletask.cancel();
         }
      }

      while(!this.toBatchLowPriority.isEmpty()) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask chunkrenderdispatcher$renderchunk$chunkcompiletask1 = this.toBatchLowPriority.poll();
         if (chunkrenderdispatcher$renderchunk$chunkcompiletask1 != null) {
            chunkrenderdispatcher$renderchunk$chunkcompiletask1.cancel();
         }
      }

      this.toBatchCount = 0;
   }

   public boolean isQueueEmpty() {
      return this.toBatchCount == 0 && this.toUpload.isEmpty();
   }

   public void dispose() {
      this.clearBatchQueue();
      this.mailbox.close();
      this.freeBuffers.clear();
   }

   @OnlyIn(Dist.CLIENT)
   static enum ChunkTaskResult {
      SUCCESSFUL,
      CANCELLED;
   }

   @OnlyIn(Dist.CLIENT)
   public static class CompiledChunk {
      public static final ChunkRenderDispatcher.CompiledChunk UNCOMPILED = new ChunkRenderDispatcher.CompiledChunk() {
         public boolean facesCanSeeEachother(Direction p_112782_, Direction p_112783_) {
            return false;
         }
      };
      final Set<RenderType> hasBlocks = new ObjectArraySet<>(RenderType.chunkBufferLayers().size());
      final List<BlockEntity> renderableBlockEntities = Lists.newArrayList();
      VisibilitySet visibilitySet = new VisibilitySet();
      @Nullable
      BufferBuilder.SortState transparencyState;

      public boolean hasNoRenderableLayers() {
         return this.hasBlocks.isEmpty();
      }

      public boolean isEmpty(RenderType p_112759_) {
         return !this.hasBlocks.contains(p_112759_);
      }

      public List<BlockEntity> getRenderableBlockEntities() {
         return this.renderableBlockEntities;
      }

      public boolean facesCanSeeEachother(Direction p_112771_, Direction p_112772_) {
         return this.visibilitySet.visibilityBetween(p_112771_, p_112772_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class RenderChunk {
      public static final int SIZE = 16;
      public final int index;
      public final AtomicReference<ChunkRenderDispatcher.CompiledChunk> compiled = new AtomicReference<>(ChunkRenderDispatcher.CompiledChunk.UNCOMPILED);
      final AtomicInteger initialCompilationCancelCount = new AtomicInteger(0);
      @Nullable
      private ChunkRenderDispatcher.RenderChunk.RebuildTask lastRebuildTask;
      @Nullable
      private ChunkRenderDispatcher.RenderChunk.ResortTransparencyTask lastResortTransparencyTask;
      private final Set<BlockEntity> globalBlockEntities = Sets.newHashSet();
      private final Map<RenderType, VertexBuffer> buffers = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((p_112837_) -> {
         return p_112837_;
      }, (p_286178_) -> {
         return new VertexBuffer(VertexBuffer.Usage.STATIC);
      }));
      private AABB bb;
      private boolean dirty = true;
      final BlockPos.MutableBlockPos origin = new BlockPos.MutableBlockPos(-1, -1, -1);
      private final BlockPos.MutableBlockPos[] relativeOrigins = Util.make(new BlockPos.MutableBlockPos[6], (p_112831_) -> {
         for(int i = 0; i < p_112831_.length; ++i) {
            p_112831_[i] = new BlockPos.MutableBlockPos();
         }

      });
      private boolean playerChanged;

      public RenderChunk(int p_202436_, int p_202437_, int p_202438_, int p_202439_) {
         this.index = p_202436_;
         this.setOrigin(p_202437_, p_202438_, p_202439_);
      }

      private boolean doesChunkExistAt(BlockPos p_112823_) {
         return ChunkRenderDispatcher.this.level.getChunk(SectionPos.blockToSectionCoord(p_112823_.getX()), SectionPos.blockToSectionCoord(p_112823_.getZ()), ChunkStatus.FULL, false) != null;
      }

      public boolean hasAllNeighbors() {
         int i = 24;
         if (!(this.getDistToPlayerSqr() > 576.0D)) {
            return true;
         } else {
            return this.doesChunkExistAt(this.relativeOrigins[Direction.WEST.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.NORTH.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.EAST.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.SOUTH.ordinal()]);
         }
      }

      public AABB getBoundingBox() {
         return this.bb;
      }

      public VertexBuffer getBuffer(RenderType p_112808_) {
         return this.buffers.get(p_112808_);
      }

      public void setOrigin(int p_112802_, int p_112803_, int p_112804_) {
         this.reset();
         this.origin.set(p_112802_, p_112803_, p_112804_);
         this.bb = new AABB((double)p_112802_, (double)p_112803_, (double)p_112804_, (double)(p_112802_ + 16), (double)(p_112803_ + 16), (double)(p_112804_ + 16));

         for(Direction direction : Direction.values()) {
            this.relativeOrigins[direction.ordinal()].set(this.origin).move(direction, 16);
         }

      }

      protected double getDistToPlayerSqr() {
         Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
         double d0 = this.bb.minX + 8.0D - camera.getPosition().x;
         double d1 = this.bb.minY + 8.0D - camera.getPosition().y;
         double d2 = this.bb.minZ + 8.0D - camera.getPosition().z;
         return d0 * d0 + d1 * d1 + d2 * d2;
      }

      void beginLayer(BufferBuilder p_112806_) {
         p_112806_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
      }

      public ChunkRenderDispatcher.CompiledChunk getCompiledChunk() {
         return this.compiled.get();
      }

      private void reset() {
         this.cancelTasks();
         this.compiled.set(ChunkRenderDispatcher.CompiledChunk.UNCOMPILED);
         this.dirty = true;
      }

      public void releaseBuffers() {
         this.reset();
         this.buffers.values().forEach(VertexBuffer::close);
      }

      public BlockPos getOrigin() {
         return this.origin;
      }

      public void setDirty(boolean p_112829_) {
         boolean flag = this.dirty;
         this.dirty = true;
         this.playerChanged = p_112829_ | (flag && this.playerChanged);
      }

      public void setNotDirty() {
         this.dirty = false;
         this.playerChanged = false;
      }

      public boolean isDirty() {
         return this.dirty;
      }

      public boolean isDirtyFromPlayer() {
         return this.dirty && this.playerChanged;
      }

      public BlockPos getRelativeOrigin(Direction p_112825_) {
         return this.relativeOrigins[p_112825_.ordinal()];
      }

      public boolean resortTransparency(RenderType p_112810_, ChunkRenderDispatcher p_112811_) {
         ChunkRenderDispatcher.CompiledChunk chunkrenderdispatcher$compiledchunk = this.getCompiledChunk();
         if (this.lastResortTransparencyTask != null) {
            this.lastResortTransparencyTask.cancel();
         }

         if (!chunkrenderdispatcher$compiledchunk.hasBlocks.contains(p_112810_)) {
            return false;
         } else {
            this.lastResortTransparencyTask = new ChunkRenderDispatcher.RenderChunk.ResortTransparencyTask(new net.minecraft.world.level.ChunkPos(getOrigin()), this.getDistToPlayerSqr(), chunkrenderdispatcher$compiledchunk);
            p_112811_.schedule(this.lastResortTransparencyTask);
            return true;
         }
      }

      protected boolean cancelTasks() {
         boolean flag = false;
         if (this.lastRebuildTask != null) {
            this.lastRebuildTask.cancel();
            this.lastRebuildTask = null;
            flag = true;
         }

         if (this.lastResortTransparencyTask != null) {
            this.lastResortTransparencyTask.cancel();
            this.lastResortTransparencyTask = null;
         }

         return flag;
      }

      public ChunkRenderDispatcher.RenderChunk.ChunkCompileTask createCompileTask(RenderRegionCache p_200438_) {
         boolean flag = this.cancelTasks();
         BlockPos blockpos = this.origin.immutable();
         int i = 1;
         RenderChunkRegion renderchunkregion = p_200438_.createRegion(ChunkRenderDispatcher.this.level, blockpos.offset(-1, -1, -1), blockpos.offset(16, 16, 16), 1);
         boolean flag1 = this.compiled.get() == ChunkRenderDispatcher.CompiledChunk.UNCOMPILED;
         if (flag1 && flag) {
            this.initialCompilationCancelCount.incrementAndGet();
         }

         this.lastRebuildTask = new ChunkRenderDispatcher.RenderChunk.RebuildTask(new net.minecraft.world.level.ChunkPos(getOrigin()), this.getDistToPlayerSqr(), renderchunkregion, flag || this.compiled.get() != ChunkRenderDispatcher.CompiledChunk.UNCOMPILED);
         return this.lastRebuildTask;
      }

      public void rebuildChunkAsync(ChunkRenderDispatcher p_200435_, RenderRegionCache p_200436_) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask chunkrenderdispatcher$renderchunk$chunkcompiletask = this.createCompileTask(p_200436_);
         p_200435_.schedule(chunkrenderdispatcher$renderchunk$chunkcompiletask);
      }

      void updateGlobalBlockEntities(Collection<BlockEntity> p_234466_) {
         Set<BlockEntity> set = Sets.newHashSet(p_234466_);
         Set<BlockEntity> set1;
         synchronized(this.globalBlockEntities) {
            set1 = Sets.newHashSet(this.globalBlockEntities);
            set.removeAll(this.globalBlockEntities);
            set1.removeAll(p_234466_);
            this.globalBlockEntities.clear();
            this.globalBlockEntities.addAll(p_234466_);
         }

         ChunkRenderDispatcher.this.renderer.updateGlobalBlockEntities(set1, set);
      }

      public void compileSync(RenderRegionCache p_200440_) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask chunkrenderdispatcher$renderchunk$chunkcompiletask = this.createCompileTask(p_200440_);
         chunkrenderdispatcher$renderchunk$chunkcompiletask.doTask(ChunkRenderDispatcher.this.fixedBuffers);
      }

      @OnlyIn(Dist.CLIENT)
      abstract class ChunkCompileTask implements Comparable<ChunkRenderDispatcher.RenderChunk.ChunkCompileTask> {
         protected final double distAtCreation;
         protected final AtomicBoolean isCancelled = new AtomicBoolean(false);
         protected final boolean isHighPriority;
         protected java.util.Map<net.minecraft.core.BlockPos, net.minecraftforge.client.model.data.ModelData> modelData;

         public ChunkCompileTask(double p_194423_, boolean p_194424_) {
            this(null, p_194423_, p_194424_);
         }

         public ChunkCompileTask(@Nullable net.minecraft.world.level.ChunkPos pos, double p_194423_, boolean p_194424_) {
            this.distAtCreation = p_194423_;
            this.isHighPriority = p_194424_;
            if (pos == null) {
               this.modelData = java.util.Collections.emptyMap();
            } else {
               this.modelData = net.minecraft.client.Minecraft.getInstance().level.getModelDataManager().getAt(pos);
            }
         }

         public abstract CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> doTask(ChunkBufferBuilderPack p_112853_);

         public abstract void cancel();

         protected abstract String name();

         public int compareTo(ChunkRenderDispatcher.RenderChunk.ChunkCompileTask p_112855_) {
            return Doubles.compare(this.distAtCreation, p_112855_.distAtCreation);
         }

         public net.minecraftforge.client.model.data.ModelData getModelData(net.minecraft.core.BlockPos pos) {
            return modelData.getOrDefault(pos, net.minecraftforge.client.model.data.ModelData.EMPTY);
         }
      }

      @OnlyIn(Dist.CLIENT)
      class RebuildTask extends ChunkRenderDispatcher.RenderChunk.ChunkCompileTask {
         @Nullable
         protected RenderChunkRegion region;

         @Deprecated
         public RebuildTask(double p_194427_, @Nullable RenderChunkRegion p_194428_, boolean p_194429_) {
            this(null, p_194427_, p_194428_, p_194429_);
         }

         public RebuildTask(@Nullable net.minecraft.world.level.ChunkPos pos, double p_194427_, @Nullable RenderChunkRegion p_194428_, boolean p_194429_) {
            super(pos, p_194427_, p_194429_);
            this.region = p_194428_;
         }

         protected String name() {
            return "rend_chk_rebuild";
         }

         public CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> doTask(ChunkBufferBuilderPack p_112872_) {
            if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (!RenderChunk.this.hasAllNeighbors()) {
               this.region = null;
               RenderChunk.this.setDirty(false);
               this.isCancelled.set(true);
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else {
               Vec3 vec3 = ChunkRenderDispatcher.this.getCameraPosition();
               float f = (float)vec3.x;
               float f1 = (float)vec3.y;
               float f2 = (float)vec3.z;
               ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults chunkrenderdispatcher$renderchunk$rebuildtask$compileresults = this.compile(f, f1, f2, p_112872_);
               RenderChunk.this.updateGlobalBlockEntities(chunkrenderdispatcher$renderchunk$rebuildtask$compileresults.globalBlockEntities);
               if (this.isCancelled.get()) {
                  chunkrenderdispatcher$renderchunk$rebuildtask$compileresults.renderedLayers.values().forEach(BufferBuilder.RenderedBuffer::release);
                  return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
               } else {
                  ChunkRenderDispatcher.CompiledChunk chunkrenderdispatcher$compiledchunk = new ChunkRenderDispatcher.CompiledChunk();
                  chunkrenderdispatcher$compiledchunk.visibilitySet = chunkrenderdispatcher$renderchunk$rebuildtask$compileresults.visibilitySet;
                  chunkrenderdispatcher$compiledchunk.renderableBlockEntities.addAll(chunkrenderdispatcher$renderchunk$rebuildtask$compileresults.blockEntities);
                  chunkrenderdispatcher$compiledchunk.transparencyState = chunkrenderdispatcher$renderchunk$rebuildtask$compileresults.transparencyState;
                  List<CompletableFuture<Void>> list = Lists.newArrayList();
                  chunkrenderdispatcher$renderchunk$rebuildtask$compileresults.renderedLayers.forEach((p_234482_, p_234483_) -> {
                     list.add(ChunkRenderDispatcher.this.uploadChunkLayer(p_234483_, RenderChunk.this.getBuffer(p_234482_)));
                     chunkrenderdispatcher$compiledchunk.hasBlocks.add(p_234482_);
                  });
                  return Util.sequenceFailFast(list).handle((p_234474_, p_234475_) -> {
                     if (p_234475_ != null && !(p_234475_ instanceof CancellationException) && !(p_234475_ instanceof InterruptedException)) {
                        Minecraft.getInstance().delayCrash(CrashReport.forThrowable(p_234475_, "Rendering chunk"));
                     }

                     if (this.isCancelled.get()) {
                        return ChunkRenderDispatcher.ChunkTaskResult.CANCELLED;
                     } else {
                        RenderChunk.this.compiled.set(chunkrenderdispatcher$compiledchunk);
                        RenderChunk.this.initialCompilationCancelCount.set(0);
                        ChunkRenderDispatcher.this.renderer.addRecentlyCompiledChunk(RenderChunk.this);
                        return ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL;
                     }
                  });
               }
            }
         }

         private ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults compile(float p_234468_, float p_234469_, float p_234470_, ChunkBufferBuilderPack p_234471_) {
            ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults chunkrenderdispatcher$renderchunk$rebuildtask$compileresults = new ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults();
            int i = 1;
            BlockPos blockpos = RenderChunk.this.origin.immutable();
            BlockPos blockpos1 = blockpos.offset(15, 15, 15);
            VisGraph visgraph = new VisGraph();
            RenderChunkRegion renderchunkregion = this.region;
            this.region = null;
            PoseStack posestack = new PoseStack();
            if (renderchunkregion != null) {
               ModelBlockRenderer.enableCaching();
               Set<RenderType> set = new ReferenceArraySet<>(RenderType.chunkBufferLayers().size());
               RandomSource randomsource = RandomSource.create();
               BlockRenderDispatcher blockrenderdispatcher = Minecraft.getInstance().getBlockRenderer();

               for(BlockPos blockpos2 : BlockPos.betweenClosed(blockpos, blockpos1)) {
                  BlockState blockstate = renderchunkregion.getBlockState(blockpos2);
                  if (blockstate.isSolidRender(renderchunkregion, blockpos2)) {
                     visgraph.setOpaque(blockpos2);
                  }

                  if (blockstate.hasBlockEntity()) {
                     BlockEntity blockentity = renderchunkregion.getBlockEntity(blockpos2);
                     if (blockentity != null) {
                        this.handleBlockEntity(chunkrenderdispatcher$renderchunk$rebuildtask$compileresults, blockentity);
                     }
                  }

                  BlockState blockstate1 = renderchunkregion.getBlockState(blockpos2);
                  FluidState fluidstate = blockstate1.getFluidState();
                  if (!fluidstate.isEmpty()) {
                     RenderType rendertype = ItemBlockRenderTypes.getRenderLayer(fluidstate);
                     BufferBuilder bufferbuilder = p_234471_.builder(rendertype);
                     if (set.add(rendertype)) {
                        RenderChunk.this.beginLayer(bufferbuilder);
                     }

                     blockrenderdispatcher.renderLiquid(blockpos2, renderchunkregion, bufferbuilder, blockstate1, fluidstate);
                  }

                  if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                     var model = blockrenderdispatcher.getBlockModel(blockstate);
                     var modelData = model.getModelData(renderchunkregion, blockpos2, blockstate, getModelData(blockpos2));
                     randomsource.setSeed(blockstate.getSeed(blockpos2));
                     for (RenderType rendertype2 : model.getRenderTypes(blockstate, randomsource, modelData)) {
                     BufferBuilder bufferbuilder2 = p_234471_.builder(rendertype2);
                     if (set.add(rendertype2)) {
                        RenderChunk.this.beginLayer(bufferbuilder2);
                     }

                     posestack.pushPose();
                     posestack.translate((float)(blockpos2.getX() & 15), (float)(blockpos2.getY() & 15), (float)(blockpos2.getZ() & 15));
                     blockrenderdispatcher.renderBatched(blockstate, blockpos2, renderchunkregion, posestack, bufferbuilder2, true, randomsource, modelData, rendertype2);
                     posestack.popPose();
                     }
                  }
               }

               if (set.contains(RenderType.translucent())) {
                  BufferBuilder bufferbuilder1 = p_234471_.builder(RenderType.translucent());
                  if (!bufferbuilder1.isCurrentBatchEmpty()) {
                     bufferbuilder1.setQuadSorting(VertexSorting.byDistance(p_234468_ - (float)blockpos.getX(), p_234469_ - (float)blockpos.getY(), p_234470_ - (float)blockpos.getZ()));
                     chunkrenderdispatcher$renderchunk$rebuildtask$compileresults.transparencyState = bufferbuilder1.getSortState();
                  }
               }

               for(RenderType rendertype1 : set) {
                  BufferBuilder.RenderedBuffer bufferbuilder$renderedbuffer = p_234471_.builder(rendertype1).endOrDiscardIfEmpty();
                  if (bufferbuilder$renderedbuffer != null) {
                     chunkrenderdispatcher$renderchunk$rebuildtask$compileresults.renderedLayers.put(rendertype1, bufferbuilder$renderedbuffer);
                  }
               }

               ModelBlockRenderer.clearCache();
            }

            chunkrenderdispatcher$renderchunk$rebuildtask$compileresults.visibilitySet = visgraph.resolve();
            return chunkrenderdispatcher$renderchunk$rebuildtask$compileresults;
         }

         private <E extends BlockEntity> void handleBlockEntity(ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults p_234477_, E p_234478_) {
            BlockEntityRenderer<E> blockentityrenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(p_234478_);
            if (blockentityrenderer != null) {
               if (blockentityrenderer.shouldRenderOffScreen(p_234478_)) {
                  p_234477_.globalBlockEntities.add(p_234478_);
               }
               else p_234477_.blockEntities.add(p_234478_); //FORGE: Fix MC-112730
            }

         }

         public void cancel() {
            this.region = null;
            if (this.isCancelled.compareAndSet(false, true)) {
               RenderChunk.this.setDirty(false);
            }

         }

         @OnlyIn(Dist.CLIENT)
         static final class CompileResults {
            public final List<BlockEntity> globalBlockEntities = new ArrayList<>();
            public final List<BlockEntity> blockEntities = new ArrayList<>();
            public final Map<RenderType, BufferBuilder.RenderedBuffer> renderedLayers = new Reference2ObjectArrayMap<>();
            public VisibilitySet visibilitySet = new VisibilitySet();
            @Nullable
            public BufferBuilder.SortState transparencyState;
         }
      }

      @OnlyIn(Dist.CLIENT)
      class ResortTransparencyTask extends ChunkRenderDispatcher.RenderChunk.ChunkCompileTask {
         private final ChunkRenderDispatcher.CompiledChunk compiledChunk;

         @Deprecated
         public ResortTransparencyTask(double p_112889_, ChunkRenderDispatcher.CompiledChunk p_112890_) {
            this(null, p_112889_, p_112890_);
         }

         public ResortTransparencyTask(@Nullable net.minecraft.world.level.ChunkPos pos, double p_112889_, ChunkRenderDispatcher.CompiledChunk p_112890_) {
            super(pos, p_112889_, true);
            this.compiledChunk = p_112890_;
         }

         protected String name() {
            return "rend_chk_sort";
         }

         public CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> doTask(ChunkBufferBuilderPack p_112893_) {
            if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (!RenderChunk.this.hasAllNeighbors()) {
               this.isCancelled.set(true);
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else {
               Vec3 vec3 = ChunkRenderDispatcher.this.getCameraPosition();
               float f = (float)vec3.x;
               float f1 = (float)vec3.y;
               float f2 = (float)vec3.z;
               BufferBuilder.SortState bufferbuilder$sortstate = this.compiledChunk.transparencyState;
               if (bufferbuilder$sortstate != null && !this.compiledChunk.isEmpty(RenderType.translucent())) {
                  BufferBuilder bufferbuilder = p_112893_.builder(RenderType.translucent());
                  RenderChunk.this.beginLayer(bufferbuilder);
                  bufferbuilder.restoreSortState(bufferbuilder$sortstate);
                  bufferbuilder.setQuadSorting(VertexSorting.byDistance(f - (float)RenderChunk.this.origin.getX(), f1 - (float)RenderChunk.this.origin.getY(), f2 - (float)RenderChunk.this.origin.getZ()));
                  this.compiledChunk.transparencyState = bufferbuilder.getSortState();
                  BufferBuilder.RenderedBuffer bufferbuilder$renderedbuffer = bufferbuilder.end();
                  if (this.isCancelled.get()) {
                     bufferbuilder$renderedbuffer.release();
                     return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
                  } else {
                     CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> completablefuture = ChunkRenderDispatcher.this.uploadChunkLayer(bufferbuilder$renderedbuffer, RenderChunk.this.getBuffer(RenderType.translucent())).thenApply((p_112898_) -> {
                        return ChunkRenderDispatcher.ChunkTaskResult.CANCELLED;
                     });
                     return completablefuture.handle((p_234491_, p_234492_) -> {
                        if (p_234492_ != null && !(p_234492_ instanceof CancellationException) && !(p_234492_ instanceof InterruptedException)) {
                           Minecraft.getInstance().delayCrash(CrashReport.forThrowable(p_234492_, "Rendering chunk"));
                        }

                        return this.isCancelled.get() ? ChunkRenderDispatcher.ChunkTaskResult.CANCELLED : ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL;
                     });
                  }
               } else {
                  return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
               }
            }
         }

         public void cancel() {
            this.isCancelled.set(true);
         }
      }
   }
}
