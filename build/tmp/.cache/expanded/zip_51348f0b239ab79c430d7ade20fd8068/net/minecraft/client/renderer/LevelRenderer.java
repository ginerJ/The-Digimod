package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector4f;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class LevelRenderer implements ResourceManagerReloadListener, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int CHUNK_SIZE = 16;
   private static final int HALF_CHUNK_SIZE = 8;
   private static final float SKY_DISC_RADIUS = 512.0F;
   private static final int MINIMUM_ADVANCED_CULLING_DISTANCE = 60;
   private static final double CEILED_SECTION_DIAGONAL = Math.ceil(Math.sqrt(3.0D) * 16.0D);
   private static final int MIN_FOG_DISTANCE = 32;
   private static final int RAIN_RADIUS = 10;
   private static final int RAIN_DIAMETER = 21;
   private static final int TRANSPARENT_SORT_COUNT = 15;
   private static final int HALF_A_SECOND_IN_MILLIS = 500;
   private static final ResourceLocation MOON_LOCATION = new ResourceLocation("textures/environment/moon_phases.png");
   private static final ResourceLocation SUN_LOCATION = new ResourceLocation("textures/environment/sun.png");
   private static final ResourceLocation CLOUDS_LOCATION = new ResourceLocation("textures/environment/clouds.png");
   private static final ResourceLocation END_SKY_LOCATION = new ResourceLocation("textures/environment/end_sky.png");
   private static final ResourceLocation FORCEFIELD_LOCATION = new ResourceLocation("textures/misc/forcefield.png");
   private static final ResourceLocation RAIN_LOCATION = new ResourceLocation("textures/environment/rain.png");
   private static final ResourceLocation SNOW_LOCATION = new ResourceLocation("textures/environment/snow.png");
   public static final Direction[] DIRECTIONS = Direction.values();
   private final Minecraft minecraft;
   private final EntityRenderDispatcher entityRenderDispatcher;
   private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
   private final RenderBuffers renderBuffers;
   @Nullable
   private ClientLevel level;
   private final BlockingQueue<ChunkRenderDispatcher.RenderChunk> recentlyCompiledChunks = new LinkedBlockingQueue<>();
   private final AtomicReference<LevelRenderer.RenderChunkStorage> renderChunkStorage = new AtomicReference<>();
   private final ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum = new ObjectArrayList<>(10000);
   private final Set<BlockEntity> globalBlockEntities = Sets.newHashSet();
   @Nullable
   private Future<?> lastFullRenderChunkUpdate;
   @Nullable
   private ViewArea viewArea;
   @Nullable
   private VertexBuffer starBuffer;
   @Nullable
   private VertexBuffer skyBuffer;
   @Nullable
   private VertexBuffer darkBuffer;
   private boolean generateClouds = true;
   @Nullable
   private VertexBuffer cloudBuffer;
   private final RunningTrimmedMean frameTimes = new RunningTrimmedMean(100);
   private int ticks;
   private final Int2ObjectMap<BlockDestructionProgress> destroyingBlocks = new Int2ObjectOpenHashMap<>();
   private final Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress = new Long2ObjectOpenHashMap<>();
   private final Map<BlockPos, SoundInstance> playingRecords = Maps.newHashMap();
   @Nullable
   private RenderTarget entityTarget;
   @Nullable
   private PostChain entityEffect;
   @Nullable
   private RenderTarget translucentTarget;
   @Nullable
   private RenderTarget itemEntityTarget;
   @Nullable
   private RenderTarget particlesTarget;
   @Nullable
   private RenderTarget weatherTarget;
   @Nullable
   private RenderTarget cloudsTarget;
   @Nullable
   private PostChain transparencyChain;
   private double lastCameraX = Double.MIN_VALUE;
   private double lastCameraY = Double.MIN_VALUE;
   private double lastCameraZ = Double.MIN_VALUE;
   private int lastCameraChunkX = Integer.MIN_VALUE;
   private int lastCameraChunkY = Integer.MIN_VALUE;
   private int lastCameraChunkZ = Integer.MIN_VALUE;
   private double prevCamX = Double.MIN_VALUE;
   private double prevCamY = Double.MIN_VALUE;
   private double prevCamZ = Double.MIN_VALUE;
   private double prevCamRotX = Double.MIN_VALUE;
   private double prevCamRotY = Double.MIN_VALUE;
   private int prevCloudX = Integer.MIN_VALUE;
   private int prevCloudY = Integer.MIN_VALUE;
   private int prevCloudZ = Integer.MIN_VALUE;
   private Vec3 prevCloudColor = Vec3.ZERO;
   @Nullable
   private CloudStatus prevCloudsType;
   @Nullable
   private ChunkRenderDispatcher chunkRenderDispatcher;
   private int lastViewDistance = -1;
   private int renderedEntities;
   private int culledEntities;
   private Frustum cullingFrustum;
   private boolean captureFrustum;
   @Nullable
   private Frustum capturedFrustum;
   private final Vector4f[] frustumPoints = new Vector4f[8];
   private final Vector3d frustumPos = new Vector3d(0.0D, 0.0D, 0.0D);
   private double xTransparentOld;
   private double yTransparentOld;
   private double zTransparentOld;
   private boolean needsFullRenderChunkUpdate = true;
   private final AtomicLong nextFullUpdateMillis = new AtomicLong(0L);
   private final AtomicBoolean needsFrustumUpdate = new AtomicBoolean(false);
   private int rainSoundTime;
   private final float[] rainSizeX = new float[1024];
   private final float[] rainSizeZ = new float[1024];

   public LevelRenderer(Minecraft p_234245_, EntityRenderDispatcher p_234246_, BlockEntityRenderDispatcher p_234247_, RenderBuffers p_234248_) {
      this.minecraft = p_234245_;
      this.entityRenderDispatcher = p_234246_;
      this.blockEntityRenderDispatcher = p_234247_;
      this.renderBuffers = p_234248_;

      for(int i = 0; i < 32; ++i) {
         for(int j = 0; j < 32; ++j) {
            float f = (float)(j - 16);
            float f1 = (float)(i - 16);
            float f2 = Mth.sqrt(f * f + f1 * f1);
            this.rainSizeX[i << 5 | j] = -f1 / f2;
            this.rainSizeZ[i << 5 | j] = f / f2;
         }
      }

      this.createStars();
      this.createLightSky();
      this.createDarkSky();
   }

   private void renderSnowAndRain(LightTexture p_109704_, float p_109705_, double p_109706_, double p_109707_, double p_109708_) {
      if (level.effects().renderSnowAndRain(level, ticks, p_109705_, p_109704_, p_109706_, p_109707_, p_109708_))
         return;
      float f = this.minecraft.level.getRainLevel(p_109705_);
      if (!(f <= 0.0F)) {
         p_109704_.turnOnLightLayer();
         Level level = this.minecraft.level;
         int i = Mth.floor(p_109706_);
         int j = Mth.floor(p_109707_);
         int k = Mth.floor(p_109708_);
         Tesselator tesselator = Tesselator.getInstance();
         BufferBuilder bufferbuilder = tesselator.getBuilder();
         RenderSystem.disableCull();
         RenderSystem.enableBlend();
         RenderSystem.enableDepthTest();
         int l = 5;
         if (Minecraft.useFancyGraphics()) {
            l = 10;
         }

         RenderSystem.depthMask(Minecraft.useShaderTransparency());
         int i1 = -1;
         float f1 = (float)this.ticks + p_109705_;
         RenderSystem.setShader(GameRenderer::getParticleShader);
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int j1 = k - l; j1 <= k + l; ++j1) {
            for(int k1 = i - l; k1 <= i + l; ++k1) {
               int l1 = (j1 - k + 16) * 32 + k1 - i + 16;
               double d0 = (double)this.rainSizeX[l1] * 0.5D;
               double d1 = (double)this.rainSizeZ[l1] * 0.5D;
               blockpos$mutableblockpos.set((double)k1, p_109707_, (double)j1);
               Biome biome = level.getBiome(blockpos$mutableblockpos).value();
               if (biome.hasPrecipitation()) {
                  int i2 = level.getHeight(Heightmap.Types.MOTION_BLOCKING, k1, j1);
                  int j2 = j - l;
                  int k2 = j + l;
                  if (j2 < i2) {
                     j2 = i2;
                  }

                  if (k2 < i2) {
                     k2 = i2;
                  }

                  int l2 = i2;
                  if (i2 < j) {
                     l2 = j;
                  }

                  if (j2 != k2) {
                     RandomSource randomsource = RandomSource.create((long)(k1 * k1 * 3121 + k1 * 45238971 ^ j1 * j1 * 418711 + j1 * 13761));
                     blockpos$mutableblockpos.set(k1, j2, j1);
                     Biome.Precipitation biome$precipitation = biome.getPrecipitationAt(blockpos$mutableblockpos);
                     if (biome$precipitation == Biome.Precipitation.RAIN) {
                        if (i1 != 0) {
                           if (i1 >= 0) {
                              tesselator.end();
                           }

                           i1 = 0;
                           RenderSystem.setShaderTexture(0, RAIN_LOCATION);
                           bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                        }

                        int i3 = this.ticks + k1 * k1 * 3121 + k1 * 45238971 + j1 * j1 * 418711 + j1 * 13761 & 31;
                        float f2 = -((float)i3 + p_109705_) / 32.0F * (3.0F + randomsource.nextFloat());
                        double d2 = (double)k1 + 0.5D - p_109706_;
                        double d4 = (double)j1 + 0.5D - p_109708_;
                        float f3 = (float)Math.sqrt(d2 * d2 + d4 * d4) / (float)l;
                        float f4 = ((1.0F - f3 * f3) * 0.5F + 0.5F) * f;
                        blockpos$mutableblockpos.set(k1, l2, j1);
                        int j3 = getLightColor(level, blockpos$mutableblockpos);
                        bufferbuilder.vertex((double)k1 - p_109706_ - d0 + 0.5D, (double)k2 - p_109707_, (double)j1 - p_109708_ - d1 + 0.5D).uv(0.0F, (float)j2 * 0.25F + f2).color(1.0F, 1.0F, 1.0F, f4).uv2(j3).endVertex();
                        bufferbuilder.vertex((double)k1 - p_109706_ + d0 + 0.5D, (double)k2 - p_109707_, (double)j1 - p_109708_ + d1 + 0.5D).uv(1.0F, (float)j2 * 0.25F + f2).color(1.0F, 1.0F, 1.0F, f4).uv2(j3).endVertex();
                        bufferbuilder.vertex((double)k1 - p_109706_ + d0 + 0.5D, (double)j2 - p_109707_, (double)j1 - p_109708_ + d1 + 0.5D).uv(1.0F, (float)k2 * 0.25F + f2).color(1.0F, 1.0F, 1.0F, f4).uv2(j3).endVertex();
                        bufferbuilder.vertex((double)k1 - p_109706_ - d0 + 0.5D, (double)j2 - p_109707_, (double)j1 - p_109708_ - d1 + 0.5D).uv(0.0F, (float)k2 * 0.25F + f2).color(1.0F, 1.0F, 1.0F, f4).uv2(j3).endVertex();
                     } else if (biome$precipitation == Biome.Precipitation.SNOW) {
                        if (i1 != 1) {
                           if (i1 >= 0) {
                              tesselator.end();
                           }

                           i1 = 1;
                           RenderSystem.setShaderTexture(0, SNOW_LOCATION);
                           bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                        }

                        float f5 = -((float)(this.ticks & 511) + p_109705_) / 512.0F;
                        float f6 = (float)(randomsource.nextDouble() + (double)f1 * 0.01D * (double)((float)randomsource.nextGaussian()));
                        float f7 = (float)(randomsource.nextDouble() + (double)(f1 * (float)randomsource.nextGaussian()) * 0.001D);
                        double d3 = (double)k1 + 0.5D - p_109706_;
                        double d5 = (double)j1 + 0.5D - p_109708_;
                        float f8 = (float)Math.sqrt(d3 * d3 + d5 * d5) / (float)l;
                        float f9 = ((1.0F - f8 * f8) * 0.3F + 0.5F) * f;
                        blockpos$mutableblockpos.set(k1, l2, j1);
                        int k3 = getLightColor(level, blockpos$mutableblockpos);
                        int l3 = k3 >> 16 & '\uffff';
                        int i4 = k3 & '\uffff';
                        int j4 = (l3 * 3 + 240) / 4;
                        int k4 = (i4 * 3 + 240) / 4;
                        bufferbuilder.vertex((double)k1 - p_109706_ - d0 + 0.5D, (double)k2 - p_109707_, (double)j1 - p_109708_ - d1 + 0.5D).uv(0.0F + f6, (float)j2 * 0.25F + f5 + f7).color(1.0F, 1.0F, 1.0F, f9).uv2(k4, j4).endVertex();
                        bufferbuilder.vertex((double)k1 - p_109706_ + d0 + 0.5D, (double)k2 - p_109707_, (double)j1 - p_109708_ + d1 + 0.5D).uv(1.0F + f6, (float)j2 * 0.25F + f5 + f7).color(1.0F, 1.0F, 1.0F, f9).uv2(k4, j4).endVertex();
                        bufferbuilder.vertex((double)k1 - p_109706_ + d0 + 0.5D, (double)j2 - p_109707_, (double)j1 - p_109708_ + d1 + 0.5D).uv(1.0F + f6, (float)k2 * 0.25F + f5 + f7).color(1.0F, 1.0F, 1.0F, f9).uv2(k4, j4).endVertex();
                        bufferbuilder.vertex((double)k1 - p_109706_ - d0 + 0.5D, (double)j2 - p_109707_, (double)j1 - p_109708_ - d1 + 0.5D).uv(0.0F + f6, (float)k2 * 0.25F + f5 + f7).color(1.0F, 1.0F, 1.0F, f9).uv2(k4, j4).endVertex();
                     }
                  }
               }
            }
         }

         if (i1 >= 0) {
            tesselator.end();
         }

         RenderSystem.enableCull();
         RenderSystem.disableBlend();
         p_109704_.turnOffLightLayer();
      }
   }

   public void tickRain(Camera p_109694_) {
      if (level.effects().tickRain(level, ticks, p_109694_))
         return;
      float f = this.minecraft.level.getRainLevel(1.0F) / (Minecraft.useFancyGraphics() ? 1.0F : 2.0F);
      if (!(f <= 0.0F)) {
         RandomSource randomsource = RandomSource.create((long)this.ticks * 312987231L);
         LevelReader levelreader = this.minecraft.level;
         BlockPos blockpos = BlockPos.containing(p_109694_.getPosition());
         BlockPos blockpos1 = null;
         int i = (int)(100.0F * f * f) / (this.minecraft.options.particles().get() == ParticleStatus.DECREASED ? 2 : 1);

         for(int j = 0; j < i; ++j) {
            int k = randomsource.nextInt(21) - 10;
            int l = randomsource.nextInt(21) - 10;
            BlockPos blockpos2 = levelreader.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockpos.offset(k, 0, l));
            if (blockpos2.getY() > levelreader.getMinBuildHeight() && blockpos2.getY() <= blockpos.getY() + 10 && blockpos2.getY() >= blockpos.getY() - 10) {
               Biome biome = levelreader.getBiome(blockpos2).value();
               if (biome.getPrecipitationAt(blockpos2) == Biome.Precipitation.RAIN) {
                  blockpos1 = blockpos2.below();
                  if (this.minecraft.options.particles().get() == ParticleStatus.MINIMAL) {
                     break;
                  }

                  double d0 = randomsource.nextDouble();
                  double d1 = randomsource.nextDouble();
                  BlockState blockstate = levelreader.getBlockState(blockpos1);
                  FluidState fluidstate = levelreader.getFluidState(blockpos1);
                  VoxelShape voxelshape = blockstate.getCollisionShape(levelreader, blockpos1);
                  double d2 = voxelshape.max(Direction.Axis.Y, d0, d1);
                  double d3 = (double)fluidstate.getHeight(levelreader, blockpos1);
                  double d4 = Math.max(d2, d3);
                  ParticleOptions particleoptions = !fluidstate.is(FluidTags.LAVA) && !blockstate.is(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(blockstate) ? ParticleTypes.RAIN : ParticleTypes.SMOKE;
                  this.minecraft.level.addParticle(particleoptions, (double)blockpos1.getX() + d0, (double)blockpos1.getY() + d4, (double)blockpos1.getZ() + d1, 0.0D, 0.0D, 0.0D);
               }
            }
         }

         if (blockpos1 != null && randomsource.nextInt(3) < this.rainSoundTime++) {
            this.rainSoundTime = 0;
            if (blockpos1.getY() > blockpos.getY() + 1 && levelreader.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockpos).getY() > Mth.floor((float)blockpos.getY())) {
               this.minecraft.level.playLocalSound(blockpos1, SoundEvents.WEATHER_RAIN_ABOVE, SoundSource.WEATHER, 0.1F, 0.5F, false);
            } else {
               this.minecraft.level.playLocalSound(blockpos1, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2F, 1.0F, false);
            }
         }

      }
   }

   public void close() {
      if (this.entityEffect != null) {
         this.entityEffect.close();
      }

      if (this.transparencyChain != null) {
         this.transparencyChain.close();
      }

   }

   public void onResourceManagerReload(ResourceManager p_109513_) {
      this.initOutline();
      if (Minecraft.useShaderTransparency()) {
         this.initTransparency();
      }

   }

   public void initOutline() {
      if (this.entityEffect != null) {
         this.entityEffect.close();
      }

      ResourceLocation resourcelocation = new ResourceLocation("shaders/post/entity_outline.json");

      try {
         this.entityEffect = new PostChain(this.minecraft.getTextureManager(), this.minecraft.getResourceManager(), this.minecraft.getMainRenderTarget(), resourcelocation);
         this.entityEffect.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
         this.entityTarget = this.entityEffect.getTempTarget("final");
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to load shader: {}", resourcelocation, ioexception);
         this.entityEffect = null;
         this.entityTarget = null;
      } catch (JsonSyntaxException jsonsyntaxexception) {
         LOGGER.warn("Failed to parse shader: {}", resourcelocation, jsonsyntaxexception);
         this.entityEffect = null;
         this.entityTarget = null;
      }

   }

   private void initTransparency() {
      this.deinitTransparency();
      ResourceLocation resourcelocation = new ResourceLocation("shaders/post/transparency.json");

      try {
         PostChain postchain = new PostChain(this.minecraft.getTextureManager(), this.minecraft.getResourceManager(), this.minecraft.getMainRenderTarget(), resourcelocation);
         postchain.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
         RenderTarget rendertarget1 = postchain.getTempTarget("translucent");
         RenderTarget rendertarget2 = postchain.getTempTarget("itemEntity");
         RenderTarget rendertarget3 = postchain.getTempTarget("particles");
         RenderTarget rendertarget4 = postchain.getTempTarget("weather");
         RenderTarget rendertarget = postchain.getTempTarget("clouds");
         this.transparencyChain = postchain;
         this.translucentTarget = rendertarget1;
         this.itemEntityTarget = rendertarget2;
         this.particlesTarget = rendertarget3;
         this.weatherTarget = rendertarget4;
         this.cloudsTarget = rendertarget;
      } catch (Exception exception) {
         String s = exception instanceof JsonSyntaxException ? "parse" : "load";
         String s1 = "Failed to " + s + " shader: " + resourcelocation;
         LevelRenderer.TransparencyShaderException levelrenderer$transparencyshaderexception = new LevelRenderer.TransparencyShaderException(s1, exception);
         if (this.minecraft.getResourcePackRepository().getSelectedIds().size() > 1) {
            Component component = this.minecraft.getResourceManager().listPacks().findFirst().map((p_234256_) -> {
               return Component.literal(p_234256_.packId());
            }).orElse((MutableComponent)null);
            this.minecraft.options.graphicsMode().set(GraphicsStatus.FANCY);
            this.minecraft.clearResourcePacksOnError(levelrenderer$transparencyshaderexception, component);
         } else {
            CrashReport crashreport = this.minecraft.fillReport(new CrashReport(s1, levelrenderer$transparencyshaderexception));
            this.minecraft.options.graphicsMode().set(GraphicsStatus.FANCY);
            this.minecraft.options.save();
            LOGGER.error(LogUtils.FATAL_MARKER, s1, (Throwable)levelrenderer$transparencyshaderexception);
            this.minecraft.emergencySave();
            Minecraft.crash(crashreport);
         }
      }

   }

   private void deinitTransparency() {
      if (this.transparencyChain != null) {
         this.transparencyChain.close();
         this.translucentTarget.destroyBuffers();
         this.itemEntityTarget.destroyBuffers();
         this.particlesTarget.destroyBuffers();
         this.weatherTarget.destroyBuffers();
         this.cloudsTarget.destroyBuffers();
         this.transparencyChain = null;
         this.translucentTarget = null;
         this.itemEntityTarget = null;
         this.particlesTarget = null;
         this.weatherTarget = null;
         this.cloudsTarget = null;
      }

   }

   public void doEntityOutline() {
      if (this.shouldShowEntityOutlines()) {
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         this.entityTarget.blitToScreen(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), false);
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
      }

   }

   public boolean shouldShowEntityOutlines() {
      return !this.minecraft.gameRenderer.isPanoramicMode() && this.entityTarget != null && this.entityEffect != null && this.minecraft.player != null;
   }

   private void createDarkSky() {
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      if (this.darkBuffer != null) {
         this.darkBuffer.close();
      }

      this.darkBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
      BufferBuilder.RenderedBuffer bufferbuilder$renderedbuffer = buildSkyDisc(bufferbuilder, -16.0F);
      this.darkBuffer.bind();
      this.darkBuffer.upload(bufferbuilder$renderedbuffer);
      VertexBuffer.unbind();
   }

   private void createLightSky() {
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      if (this.skyBuffer != null) {
         this.skyBuffer.close();
      }

      this.skyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
      BufferBuilder.RenderedBuffer bufferbuilder$renderedbuffer = buildSkyDisc(bufferbuilder, 16.0F);
      this.skyBuffer.bind();
      this.skyBuffer.upload(bufferbuilder$renderedbuffer);
      VertexBuffer.unbind();
   }

   private static BufferBuilder.RenderedBuffer buildSkyDisc(BufferBuilder p_234268_, float p_234269_) {
      float f = Math.signum(p_234269_) * 512.0F;
      float f1 = 512.0F;
      RenderSystem.setShader(GameRenderer::getPositionShader);
      p_234268_.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
      p_234268_.vertex(0.0D, (double)p_234269_, 0.0D).endVertex();

      for(int i = -180; i <= 180; i += 45) {
         p_234268_.vertex((double)(f * Mth.cos((float)i * ((float)Math.PI / 180F))), (double)p_234269_, (double)(512.0F * Mth.sin((float)i * ((float)Math.PI / 180F)))).endVertex();
      }

      return p_234268_.end();
   }

   private void createStars() {
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionShader);
      if (this.starBuffer != null) {
         this.starBuffer.close();
      }

      this.starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
      BufferBuilder.RenderedBuffer bufferbuilder$renderedbuffer = this.drawStars(bufferbuilder);
      this.starBuffer.bind();
      this.starBuffer.upload(bufferbuilder$renderedbuffer);
      VertexBuffer.unbind();
   }

   private BufferBuilder.RenderedBuffer drawStars(BufferBuilder p_234260_) {
      RandomSource randomsource = RandomSource.create(10842L);
      p_234260_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

      for(int i = 0; i < 1500; ++i) {
         double d0 = (double)(randomsource.nextFloat() * 2.0F - 1.0F);
         double d1 = (double)(randomsource.nextFloat() * 2.0F - 1.0F);
         double d2 = (double)(randomsource.nextFloat() * 2.0F - 1.0F);
         double d3 = (double)(0.15F + randomsource.nextFloat() * 0.1F);
         double d4 = d0 * d0 + d1 * d1 + d2 * d2;
         if (d4 < 1.0D && d4 > 0.01D) {
            d4 = 1.0D / Math.sqrt(d4);
            d0 *= d4;
            d1 *= d4;
            d2 *= d4;
            double d5 = d0 * 100.0D;
            double d6 = d1 * 100.0D;
            double d7 = d2 * 100.0D;
            double d8 = Math.atan2(d0, d2);
            double d9 = Math.sin(d8);
            double d10 = Math.cos(d8);
            double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
            double d12 = Math.sin(d11);
            double d13 = Math.cos(d11);
            double d14 = randomsource.nextDouble() * Math.PI * 2.0D;
            double d15 = Math.sin(d14);
            double d16 = Math.cos(d14);

            for(int j = 0; j < 4; ++j) {
               double d17 = 0.0D;
               double d18 = (double)((j & 2) - 1) * d3;
               double d19 = (double)((j + 1 & 2) - 1) * d3;
               double d20 = 0.0D;
               double d21 = d18 * d16 - d19 * d15;
               double d22 = d19 * d16 + d18 * d15;
               double d23 = d21 * d12 + 0.0D * d13;
               double d24 = 0.0D * d12 - d21 * d13;
               double d25 = d24 * d9 - d22 * d10;
               double d26 = d22 * d9 + d24 * d10;
               p_234260_.vertex(d5 + d25, d6 + d23, d7 + d26).endVertex();
            }
         }
      }

      return p_234260_.end();
   }

   public void setLevel(@Nullable ClientLevel p_109702_) {
      this.lastCameraX = Double.MIN_VALUE;
      this.lastCameraY = Double.MIN_VALUE;
      this.lastCameraZ = Double.MIN_VALUE;
      this.lastCameraChunkX = Integer.MIN_VALUE;
      this.lastCameraChunkY = Integer.MIN_VALUE;
      this.lastCameraChunkZ = Integer.MIN_VALUE;
      this.entityRenderDispatcher.setLevel(p_109702_);
      this.level = p_109702_;
      if (p_109702_ != null) {
         this.allChanged();
      } else {
         if (this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
            this.viewArea = null;
         }

         if (this.chunkRenderDispatcher != null) {
            this.chunkRenderDispatcher.dispose();
         }

         this.chunkRenderDispatcher = null;
         this.globalBlockEntities.clear();
         this.renderChunkStorage.set((LevelRenderer.RenderChunkStorage)null);
         this.renderChunksInFrustum.clear();
      }

   }

   public void graphicsChanged() {
      if (Minecraft.useShaderTransparency()) {
         this.initTransparency();
      } else {
         this.deinitTransparency();
      }

   }

   public void allChanged() {
      if (this.level != null) {
         this.graphicsChanged();
         this.level.clearTintCaches();
         if (this.chunkRenderDispatcher == null) {
            this.chunkRenderDispatcher = new ChunkRenderDispatcher(this.level, this, Util.backgroundExecutor(), this.minecraft.is64Bit(), this.renderBuffers.fixedBufferPack());
         } else {
            this.chunkRenderDispatcher.setLevel(this.level);
         }

         this.needsFullRenderChunkUpdate = true;
         this.generateClouds = true;
         this.recentlyCompiledChunks.clear();
         ItemBlockRenderTypes.setFancy(Minecraft.useFancyGraphics());
         this.lastViewDistance = this.minecraft.options.getEffectiveRenderDistance();
         if (this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
         }

         this.chunkRenderDispatcher.blockUntilClear();
         synchronized(this.globalBlockEntities) {
            this.globalBlockEntities.clear();
         }

         this.viewArea = new ViewArea(this.chunkRenderDispatcher, this.level, this.minecraft.options.getEffectiveRenderDistance(), this);
         if (this.lastFullRenderChunkUpdate != null) {
            try {
               this.lastFullRenderChunkUpdate.get();
               this.lastFullRenderChunkUpdate = null;
            } catch (Exception exception) {
               LOGGER.warn("Full update failed", (Throwable)exception);
            }
         }

         this.renderChunkStorage.set(new LevelRenderer.RenderChunkStorage(this.viewArea.chunks.length));
         this.renderChunksInFrustum.clear();
         Entity entity = this.minecraft.getCameraEntity();
         if (entity != null) {
            this.viewArea.repositionCamera(entity.getX(), entity.getZ());
         }

      }
   }

   public void resize(int p_109488_, int p_109489_) {
      this.needsUpdate();
      if (this.entityEffect != null) {
         this.entityEffect.resize(p_109488_, p_109489_);
      }

      if (this.transparencyChain != null) {
         this.transparencyChain.resize(p_109488_, p_109489_);
      }

   }

   public String getChunkStatistics() {
      int i = this.viewArea.chunks.length;
      int j = this.countRenderedChunks();
      return String.format(Locale.ROOT, "C: %d/%d %sD: %d, %s", j, i, this.minecraft.smartCull ? "(s) " : "", this.lastViewDistance, this.chunkRenderDispatcher == null ? "null" : this.chunkRenderDispatcher.getStats());
   }

   public ChunkRenderDispatcher getChunkRenderDispatcher() {
      return this.chunkRenderDispatcher;
   }

   public double getTotalChunks() {
      return (double)this.viewArea.chunks.length;
   }

   public double getLastViewDistance() {
      return (double)this.lastViewDistance;
   }

   public int countRenderedChunks() {
      int i = 0;

      for(LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo : this.renderChunksInFrustum) {
         if (!levelrenderer$renderchunkinfo.chunk.getCompiledChunk().hasNoRenderableLayers()) {
            ++i;
         }
      }

      return i;
   }

   public String getEntityStatistics() {
      return "E: " + this.renderedEntities + "/" + this.level.getEntityCount() + ", B: " + this.culledEntities + ", SD: " + this.level.getServerSimulationDistance();
   }

   private void setupRender(Camera p_194339_, Frustum p_194340_, boolean p_194341_, boolean p_194342_) {
      Vec3 vec3 = p_194339_.getPosition();
      if (this.minecraft.options.getEffectiveRenderDistance() != this.lastViewDistance) {
         this.allChanged();
      }

      this.level.getProfiler().push("camera");
      double d0 = this.minecraft.player.getX();
      double d1 = this.minecraft.player.getY();
      double d2 = this.minecraft.player.getZ();
      int i = SectionPos.posToSectionCoord(d0);
      int j = SectionPos.posToSectionCoord(d1);
      int k = SectionPos.posToSectionCoord(d2);
      if (this.lastCameraChunkX != i || this.lastCameraChunkY != j || this.lastCameraChunkZ != k) {
         this.lastCameraX = d0;
         this.lastCameraY = d1;
         this.lastCameraZ = d2;
         this.lastCameraChunkX = i;
         this.lastCameraChunkY = j;
         this.lastCameraChunkZ = k;
         this.viewArea.repositionCamera(d0, d2);
      }

      this.chunkRenderDispatcher.setCamera(vec3);
      this.level.getProfiler().popPush("cull");
      this.minecraft.getProfiler().popPush("culling");
      BlockPos blockpos = p_194339_.getBlockPosition();
      double d3 = Math.floor(vec3.x / 8.0D);
      double d4 = Math.floor(vec3.y / 8.0D);
      double d5 = Math.floor(vec3.z / 8.0D);
      this.needsFullRenderChunkUpdate = this.needsFullRenderChunkUpdate || d3 != this.prevCamX || d4 != this.prevCamY || d5 != this.prevCamZ;
      this.nextFullUpdateMillis.updateAndGet((p_234309_) -> {
         if (p_234309_ > 0L && System.currentTimeMillis() > p_234309_) {
            this.needsFullRenderChunkUpdate = true;
            return 0L;
         } else {
            return p_234309_;
         }
      });
      this.prevCamX = d3;
      this.prevCamY = d4;
      this.prevCamZ = d5;
      this.minecraft.getProfiler().popPush("update");
      boolean flag = this.minecraft.smartCull;
      if (p_194342_ && this.level.getBlockState(blockpos).isSolidRender(this.level, blockpos)) {
         flag = false;
      }

      if (!p_194341_) {
         if (this.needsFullRenderChunkUpdate && (this.lastFullRenderChunkUpdate == null || this.lastFullRenderChunkUpdate.isDone())) {
            this.minecraft.getProfiler().push("full_update_schedule");
            this.needsFullRenderChunkUpdate = false;
            boolean flag1 = flag;
            this.lastFullRenderChunkUpdate = Util.backgroundExecutor().submit(() -> {
               Queue<LevelRenderer.RenderChunkInfo> queue1 = Queues.newArrayDeque();
               this.initializeQueueForFullUpdate(p_194339_, queue1);
               LevelRenderer.RenderChunkStorage levelrenderer$renderchunkstorage1 = new LevelRenderer.RenderChunkStorage(this.viewArea.chunks.length);
               this.updateRenderChunks(levelrenderer$renderchunkstorage1.renderChunks, levelrenderer$renderchunkstorage1.renderInfoMap, vec3, queue1, flag1);
               this.renderChunkStorage.set(levelrenderer$renderchunkstorage1);
               this.needsFrustumUpdate.set(true);
            });
            this.minecraft.getProfiler().pop();
         }

         LevelRenderer.RenderChunkStorage levelrenderer$renderchunkstorage = this.renderChunkStorage.get();
         if (!this.recentlyCompiledChunks.isEmpty()) {
            this.minecraft.getProfiler().push("partial_update");
            Queue<LevelRenderer.RenderChunkInfo> queue = Queues.newArrayDeque();

            while(!this.recentlyCompiledChunks.isEmpty()) {
               ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk = this.recentlyCompiledChunks.poll();
               LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo = levelrenderer$renderchunkstorage.renderInfoMap.get(chunkrenderdispatcher$renderchunk);
               if (levelrenderer$renderchunkinfo != null && levelrenderer$renderchunkinfo.chunk == chunkrenderdispatcher$renderchunk) {
                  queue.add(levelrenderer$renderchunkinfo);
               }
            }

            this.updateRenderChunks(levelrenderer$renderchunkstorage.renderChunks, levelrenderer$renderchunkstorage.renderInfoMap, vec3, queue, flag);
            this.needsFrustumUpdate.set(true);
            this.minecraft.getProfiler().pop();
         }

         double d6 = Math.floor((double)(p_194339_.getXRot() / 2.0F));
         double d7 = Math.floor((double)(p_194339_.getYRot() / 2.0F));
         if (this.needsFrustumUpdate.compareAndSet(true, false) || d6 != this.prevCamRotX || d7 != this.prevCamRotY) {
            this.applyFrustum((new Frustum(p_194340_)).offsetToFullyIncludeCameraCube(8));
            this.prevCamRotX = d6;
            this.prevCamRotY = d7;
         }
      }

      this.minecraft.getProfiler().pop();
   }

   private void applyFrustum(Frustum p_194355_) {
      if (!Minecraft.getInstance().isSameThread()) {
         throw new IllegalStateException("applyFrustum called from wrong thread: " + Thread.currentThread().getName());
      } else {
         this.minecraft.getProfiler().push("apply_frustum");
         this.renderChunksInFrustum.clear();

         for(LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo : (this.renderChunkStorage.get()).renderChunks) {
            if (p_194355_.isVisible(levelrenderer$renderchunkinfo.chunk.getBoundingBox())) {
               this.renderChunksInFrustum.add(levelrenderer$renderchunkinfo);
            }
         }

         this.minecraft.getProfiler().pop();
      }
   }

   private void initializeQueueForFullUpdate(Camera p_194344_, Queue<LevelRenderer.RenderChunkInfo> p_194345_) {
      int i = 16;
      Vec3 vec3 = p_194344_.getPosition();
      BlockPos blockpos = p_194344_.getBlockPosition();
      ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk = this.viewArea.getRenderChunkAt(blockpos);
      if (chunkrenderdispatcher$renderchunk == null) {
         boolean flag = blockpos.getY() > this.level.getMinBuildHeight();
         int j = flag ? this.level.getMaxBuildHeight() - 8 : this.level.getMinBuildHeight() + 8;
         int k = Mth.floor(vec3.x / 16.0D) * 16;
         int l = Mth.floor(vec3.z / 16.0D) * 16;
         List<LevelRenderer.RenderChunkInfo> list = Lists.newArrayList();

         for(int i1 = -this.lastViewDistance; i1 <= this.lastViewDistance; ++i1) {
            for(int j1 = -this.lastViewDistance; j1 <= this.lastViewDistance; ++j1) {
               ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk1 = this.viewArea.getRenderChunkAt(new BlockPos(k + SectionPos.sectionToBlockCoord(i1, 8), j, l + SectionPos.sectionToBlockCoord(j1, 8)));
               if (chunkrenderdispatcher$renderchunk1 != null) {
                  list.add(new LevelRenderer.RenderChunkInfo(chunkrenderdispatcher$renderchunk1, (Direction)null, 0));
               }
            }
         }

         list.sort(Comparator.comparingDouble((p_234303_) -> {
            return blockpos.distSqr(p_234303_.chunk.getOrigin().offset(8, 8, 8));
         }));
         p_194345_.addAll(list);
      } else {
         p_194345_.add(new LevelRenderer.RenderChunkInfo(chunkrenderdispatcher$renderchunk, (Direction)null, 0));
      }

   }

   public void addRecentlyCompiledChunk(ChunkRenderDispatcher.RenderChunk p_194353_) {
      this.recentlyCompiledChunks.add(p_194353_);
   }

   private void updateRenderChunks(LinkedHashSet<LevelRenderer.RenderChunkInfo> p_194363_, LevelRenderer.RenderInfoMap p_194364_, Vec3 p_194365_, Queue<LevelRenderer.RenderChunkInfo> p_194366_, boolean p_194367_) {
      int i = 16;
      BlockPos blockpos = new BlockPos(Mth.floor(p_194365_.x / 16.0D) * 16, Mth.floor(p_194365_.y / 16.0D) * 16, Mth.floor(p_194365_.z / 16.0D) * 16);
      BlockPos blockpos1 = blockpos.offset(8, 8, 8);
      Entity.setViewScale(Mth.clamp((double)this.minecraft.options.getEffectiveRenderDistance() / 8.0D, 1.0D, 2.5D) * this.minecraft.options.entityDistanceScaling().get());

      while(!p_194366_.isEmpty()) {
         LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo = p_194366_.poll();
         ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk = levelrenderer$renderchunkinfo.chunk;
         p_194363_.add(levelrenderer$renderchunkinfo);
         boolean flag = Math.abs(chunkrenderdispatcher$renderchunk.getOrigin().getX() - blockpos.getX()) > 60 || Math.abs(chunkrenderdispatcher$renderchunk.getOrigin().getY() - blockpos.getY()) > 60 || Math.abs(chunkrenderdispatcher$renderchunk.getOrigin().getZ() - blockpos.getZ()) > 60;

         for(Direction direction : DIRECTIONS) {
            ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk1 = this.getRelativeFrom(blockpos, chunkrenderdispatcher$renderchunk, direction);
            if (chunkrenderdispatcher$renderchunk1 != null && (!p_194367_ || !levelrenderer$renderchunkinfo.hasDirection(direction.getOpposite()))) {
               if (p_194367_ && levelrenderer$renderchunkinfo.hasSourceDirections()) {
                  ChunkRenderDispatcher.CompiledChunk chunkrenderdispatcher$compiledchunk = chunkrenderdispatcher$renderchunk.getCompiledChunk();
                  boolean flag1 = false;

                  for(int j = 0; j < DIRECTIONS.length; ++j) {
                     if (levelrenderer$renderchunkinfo.hasSourceDirection(j) && chunkrenderdispatcher$compiledchunk.facesCanSeeEachother(DIRECTIONS[j].getOpposite(), direction)) {
                        flag1 = true;
                        break;
                     }
                  }

                  if (!flag1) {
                     continue;
                  }
               }

               if (p_194367_ && flag) {
                  BlockPos blockpos2;
                  byte b0;
                  label126: {
                     label125: {
                        blockpos2 = chunkrenderdispatcher$renderchunk1.getOrigin();
                        if (direction.getAxis() == Direction.Axis.X) {
                           if (blockpos1.getX() > blockpos2.getX()) {
                              break label125;
                           }
                        } else if (blockpos1.getX() < blockpos2.getX()) {
                           break label125;
                        }

                        b0 = 0;
                        break label126;
                     }

                     b0 = 16;
                  }

                  byte b1;
                  label118: {
                     label117: {
                        if (direction.getAxis() == Direction.Axis.Y) {
                           if (blockpos1.getY() > blockpos2.getY()) {
                              break label117;
                           }
                        } else if (blockpos1.getY() < blockpos2.getY()) {
                           break label117;
                        }

                        b1 = 0;
                        break label118;
                     }

                     b1 = 16;
                  }

                  byte b2;
                  label110: {
                     label109: {
                        if (direction.getAxis() == Direction.Axis.Z) {
                           if (blockpos1.getZ() > blockpos2.getZ()) {
                              break label109;
                           }
                        } else if (blockpos1.getZ() < blockpos2.getZ()) {
                           break label109;
                        }

                        b2 = 0;
                        break label110;
                     }

                     b2 = 16;
                  }

                  BlockPos blockpos3 = blockpos2.offset(b0, b1, b2);
                  Vec3 vec31 = new Vec3((double)blockpos3.getX(), (double)blockpos3.getY(), (double)blockpos3.getZ());
                  Vec3 vec3 = p_194365_.subtract(vec31).normalize().scale(CEILED_SECTION_DIAGONAL);
                  boolean flag2 = true;

                  while(p_194365_.subtract(vec31).lengthSqr() > 3600.0D) {
                     vec31 = vec31.add(vec3);
                     if (vec31.y > (double)this.level.getMaxBuildHeight() || vec31.y < (double)this.level.getMinBuildHeight()) {
                        break;
                     }

                     ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk2 = this.viewArea.getRenderChunkAt(BlockPos.containing(vec31.x, vec31.y, vec31.z));
                     if (chunkrenderdispatcher$renderchunk2 == null || p_194364_.get(chunkrenderdispatcher$renderchunk2) == null) {
                        flag2 = false;
                        break;
                     }
                  }

                  if (!flag2) {
                     continue;
                  }
               }

               LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo1 = p_194364_.get(chunkrenderdispatcher$renderchunk1);
               if (levelrenderer$renderchunkinfo1 != null) {
                  levelrenderer$renderchunkinfo1.addSourceDirection(direction);
               } else if (!chunkrenderdispatcher$renderchunk1.hasAllNeighbors()) {
                  if (!this.closeToBorder(blockpos, chunkrenderdispatcher$renderchunk)) {
                     this.nextFullUpdateMillis.set(System.currentTimeMillis() + 500L);
                  }
               } else {
                  LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo2 = new LevelRenderer.RenderChunkInfo(chunkrenderdispatcher$renderchunk1, direction, levelrenderer$renderchunkinfo.step + 1);
                  levelrenderer$renderchunkinfo2.setDirections(levelrenderer$renderchunkinfo.directions, direction);
                  p_194366_.add(levelrenderer$renderchunkinfo2);
                  p_194364_.put(chunkrenderdispatcher$renderchunk1, levelrenderer$renderchunkinfo2);
               }
            }
         }
      }

   }

   @Nullable
   private ChunkRenderDispatcher.RenderChunk getRelativeFrom(BlockPos p_109729_, ChunkRenderDispatcher.RenderChunk p_109730_, Direction p_109731_) {
      BlockPos blockpos = p_109730_.getRelativeOrigin(p_109731_);
      if (Mth.abs(p_109729_.getX() - blockpos.getX()) > this.lastViewDistance * 16) {
         return null;
      } else if (Mth.abs(p_109729_.getY() - blockpos.getY()) <= this.lastViewDistance * 16 && blockpos.getY() >= this.level.getMinBuildHeight() && blockpos.getY() < this.level.getMaxBuildHeight()) {
         return Mth.abs(p_109729_.getZ() - blockpos.getZ()) > this.lastViewDistance * 16 ? null : this.viewArea.getRenderChunkAt(blockpos);
      } else {
         return null;
      }
   }

   private boolean closeToBorder(BlockPos p_194360_, ChunkRenderDispatcher.RenderChunk p_194361_) {
      int i = SectionPos.blockToSectionCoord(p_194360_.getX());
      int j = SectionPos.blockToSectionCoord(p_194360_.getZ());
      BlockPos blockpos = p_194361_.getOrigin();
      int k = SectionPos.blockToSectionCoord(blockpos.getX());
      int l = SectionPos.blockToSectionCoord(blockpos.getZ());
      return !ChunkMap.isChunkInRange(k, l, i, j, this.lastViewDistance - 3);
   }

   private void captureFrustum(Matrix4f p_253756_, Matrix4f p_253787_, double p_254187_, double p_253833_, double p_254547_, Frustum p_253954_) {
      this.capturedFrustum = p_253954_;
      Matrix4f matrix4f = new Matrix4f(p_253787_);
      matrix4f.mul(p_253756_);
      matrix4f.invert();
      this.frustumPos.x = p_254187_;
      this.frustumPos.y = p_253833_;
      this.frustumPos.z = p_254547_;
      this.frustumPoints[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
      this.frustumPoints[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
      this.frustumPoints[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
      this.frustumPoints[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
      this.frustumPoints[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
      this.frustumPoints[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
      this.frustumPoints[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.frustumPoints[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

      for(int i = 0; i < 8; ++i) {
         matrix4f.transform(this.frustumPoints[i]);
         this.frustumPoints[i].div(this.frustumPoints[i].w());
      }

   }

   public void prepareCullFrustum(PoseStack p_253986_, Vec3 p_253766_, Matrix4f p_254341_) {
      Matrix4f matrix4f = p_253986_.last().pose();
      double d0 = p_253766_.x();
      double d1 = p_253766_.y();
      double d2 = p_253766_.z();
      this.cullingFrustum = new Frustum(matrix4f, p_254341_);
      this.cullingFrustum.prepare(d0, d1, d2);
   }

   public void renderLevel(PoseStack p_109600_, float p_109601_, long p_109602_, boolean p_109603_, Camera p_109604_, GameRenderer p_109605_, LightTexture p_109606_, Matrix4f p_254120_) {
      RenderSystem.setShaderGameTime(this.level.getGameTime(), p_109601_);
      this.blockEntityRenderDispatcher.prepare(this.level, p_109604_, this.minecraft.hitResult);
      this.entityRenderDispatcher.prepare(this.level, p_109604_, this.minecraft.crosshairPickEntity);
      ProfilerFiller profilerfiller = this.level.getProfiler();
      profilerfiller.popPush("light_update_queue");
      this.level.pollLightUpdates();
      profilerfiller.popPush("light_updates");
      this.level.getChunkSource().getLightEngine().runLightUpdates();
      Vec3 vec3 = p_109604_.getPosition();
      double d0 = vec3.x();
      double d1 = vec3.y();
      double d2 = vec3.z();
      Matrix4f matrix4f = p_109600_.last().pose();
      profilerfiller.popPush("culling");
      boolean flag = this.capturedFrustum != null;
      Frustum frustum;
      if (flag) {
         frustum = this.capturedFrustum;
         frustum.prepare(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z);
      } else {
         frustum = this.cullingFrustum;
      }

      this.minecraft.getProfiler().popPush("captureFrustum");
      if (this.captureFrustum) {
         this.captureFrustum(matrix4f, p_254120_, vec3.x, vec3.y, vec3.z, flag ? new Frustum(matrix4f, p_254120_) : frustum);
         this.captureFrustum = false;
      }

      profilerfiller.popPush("clear");
      FogRenderer.setupColor(p_109604_, p_109601_, this.minecraft.level, this.minecraft.options.getEffectiveRenderDistance(), p_109605_.getDarkenWorldAmount(p_109601_));
      FogRenderer.levelFogColor();
      RenderSystem.clear(16640, Minecraft.ON_OSX);
      float f = p_109605_.getRenderDistance();
      boolean flag1 = this.minecraft.level.effects().isFoggyAt(Mth.floor(d0), Mth.floor(d1)) || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog();
      FogRenderer.setupFog(p_109604_, FogRenderer.FogMode.FOG_SKY, f, flag1, p_109601_);
      profilerfiller.popPush("sky");
      RenderSystem.setShader(GameRenderer::getPositionShader);
      this.renderSky(p_109600_, p_254120_, p_109601_, p_109604_, flag1, () -> {
         FogRenderer.setupFog(p_109604_, FogRenderer.FogMode.FOG_SKY, f, flag1, p_109601_);
      });
      net.minecraftforge.client.ForgeHooksClient.dispatchRenderStage(net.minecraftforge.client.event.RenderLevelStageEvent.Stage.AFTER_SKY, this, p_109600_, p_254120_, this.ticks, p_109604_, frustum);
      profilerfiller.popPush("fog");
      FogRenderer.setupFog(p_109604_, FogRenderer.FogMode.FOG_TERRAIN, Math.max(f, 32.0F), flag1, p_109601_);
      profilerfiller.popPush("terrain_setup");
      this.setupRender(p_109604_, frustum, flag, this.minecraft.player.isSpectator());
      profilerfiller.popPush("compilechunks");
      this.compileChunks(p_109604_);
      profilerfiller.popPush("terrain");
      this.renderChunkLayer(RenderType.solid(), p_109600_, d0, d1, d2, p_254120_);
      this.minecraft.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).setBlurMipmap(false, this.minecraft.options.mipmapLevels().get() > 0); // FORGE: fix flickering leaves when mods mess up the blurMipmap settings
      this.renderChunkLayer(RenderType.cutoutMipped(), p_109600_, d0, d1, d2, p_254120_);
      this.minecraft.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).restoreLastBlurMipmap();
      this.renderChunkLayer(RenderType.cutout(), p_109600_, d0, d1, d2, p_254120_);
      if (this.level.effects().constantAmbientLight()) {
         Lighting.setupNetherLevel(p_109600_.last().pose());
      } else {
         Lighting.setupLevel(p_109600_.last().pose());
      }

      profilerfiller.popPush("entities");
      this.renderedEntities = 0;
      this.culledEntities = 0;
      if (this.itemEntityTarget != null) {
         this.itemEntityTarget.clear(Minecraft.ON_OSX);
         this.itemEntityTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
         this.minecraft.getMainRenderTarget().bindWrite(false);
      }

      if (this.weatherTarget != null) {
         this.weatherTarget.clear(Minecraft.ON_OSX);
      }

      if (this.shouldShowEntityOutlines()) {
         this.entityTarget.clear(Minecraft.ON_OSX);
         this.minecraft.getMainRenderTarget().bindWrite(false);
      }

      boolean flag2 = false;
      MultiBufferSource.BufferSource multibuffersource$buffersource = this.renderBuffers.bufferSource();

      for(Entity entity : this.level.entitiesForRendering()) {
         if (this.entityRenderDispatcher.shouldRender(entity, frustum, d0, d1, d2) || entity.hasIndirectPassenger(this.minecraft.player)) {
            BlockPos blockpos = entity.blockPosition();
            if ((this.level.isOutsideBuildHeight(blockpos.getY()) || this.isChunkCompiled(blockpos)) && (entity != p_109604_.getEntity() || p_109604_.isDetached() || p_109604_.getEntity() instanceof LivingEntity && ((LivingEntity)p_109604_.getEntity()).isSleeping()) && (!(entity instanceof LocalPlayer) || p_109604_.getEntity() == entity || (entity == minecraft.player && !minecraft.player.isSpectator()))) { //FORGE: render local player entity when it is not the renderViewEntity
               ++this.renderedEntities;
               if (entity.tickCount == 0) {
                  entity.xOld = entity.getX();
                  entity.yOld = entity.getY();
                  entity.zOld = entity.getZ();
               }

               MultiBufferSource multibuffersource;
               if (this.shouldShowEntityOutlines() && this.minecraft.shouldEntityAppearGlowing(entity)) {
                  flag2 = true;
                  OutlineBufferSource outlinebuffersource = this.renderBuffers.outlineBufferSource();
                  multibuffersource = outlinebuffersource;
                  int i = entity.getTeamColor();
                  outlinebuffersource.setColor(FastColor.ARGB32.red(i), FastColor.ARGB32.green(i), FastColor.ARGB32.blue(i), 255);
               } else {
                  if (this.shouldShowEntityOutlines() && entity.hasCustomOutlineRendering(this.minecraft.player)) { // FORGE: allow custom outline rendering
                     flag2 = true;
                  }
                  multibuffersource = multibuffersource$buffersource;
               }

               this.renderEntity(entity, d0, d1, d2, p_109601_, p_109600_, multibuffersource);
            }
         }
      }

      multibuffersource$buffersource.endLastBatch();
      this.checkPoseStack(p_109600_);
      multibuffersource$buffersource.endBatch(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS));
      multibuffersource$buffersource.endBatch(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
      multibuffersource$buffersource.endBatch(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));
      multibuffersource$buffersource.endBatch(RenderType.entitySmoothCutout(TextureAtlas.LOCATION_BLOCKS));
      net.minecraftforge.client.ForgeHooksClient.dispatchRenderStage(net.minecraftforge.client.event.RenderLevelStageEvent.Stage.AFTER_ENTITIES, this, p_109600_, p_254120_, this.ticks, p_109604_, frustum);
      profilerfiller.popPush("blockentities");

      for(LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo : this.renderChunksInFrustum) {
         List<BlockEntity> list = levelrenderer$renderchunkinfo.chunk.getCompiledChunk().getRenderableBlockEntities();
         if (!list.isEmpty()) {
            for(BlockEntity blockentity1 : list) {
               if(!frustum.isVisible(blockentity1.getRenderBoundingBox())) continue;
               BlockPos blockpos4 = blockentity1.getBlockPos();
               MultiBufferSource multibuffersource1 = multibuffersource$buffersource;
               p_109600_.pushPose();
               p_109600_.translate((double)blockpos4.getX() - d0, (double)blockpos4.getY() - d1, (double)blockpos4.getZ() - d2);
               SortedSet<BlockDestructionProgress> sortedset = this.destructionProgress.get(blockpos4.asLong());
               if (sortedset != null && !sortedset.isEmpty()) {
                  int j = sortedset.last().getProgress();
                  if (j >= 0) {
                     PoseStack.Pose posestack$pose = p_109600_.last();
                     VertexConsumer vertexconsumer = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(j)), posestack$pose.pose(), posestack$pose.normal(), 1.0F);
                     multibuffersource1 = (p_234298_) -> {
                        VertexConsumer vertexconsumer3 = multibuffersource$buffersource.getBuffer(p_234298_);
                        return p_234298_.affectsCrumbling() ? VertexMultiConsumer.create(vertexconsumer, vertexconsumer3) : vertexconsumer3;
                     };
                  }
               }
               if (this.shouldShowEntityOutlines() && blockentity1.hasCustomOutlineRendering(this.minecraft.player)) { // FORGE: allow custom outline rendering
                  flag2 = true;
               }

               this.blockEntityRenderDispatcher.render(blockentity1, p_109601_, p_109600_, multibuffersource1);
               p_109600_.popPose();
            }
         }
      }

      synchronized(this.globalBlockEntities) {
         for(BlockEntity blockentity : this.globalBlockEntities) {
            if(!frustum.isVisible(blockentity.getRenderBoundingBox())) continue;
            BlockPos blockpos3 = blockentity.getBlockPos();
            p_109600_.pushPose();
            p_109600_.translate((double)blockpos3.getX() - d0, (double)blockpos3.getY() - d1, (double)blockpos3.getZ() - d2);
            if (this.shouldShowEntityOutlines() && blockentity.hasCustomOutlineRendering(this.minecraft.player)) { // FORGE: allow custom outline rendering
               flag2 = true;
            }
            this.blockEntityRenderDispatcher.render(blockentity, p_109601_, p_109600_, multibuffersource$buffersource);
            p_109600_.popPose();
         }
      }

      this.checkPoseStack(p_109600_);
      multibuffersource$buffersource.endBatch(RenderType.solid());
      multibuffersource$buffersource.endBatch(RenderType.endPortal());
      multibuffersource$buffersource.endBatch(RenderType.endGateway());
      multibuffersource$buffersource.endBatch(Sheets.solidBlockSheet());
      multibuffersource$buffersource.endBatch(Sheets.cutoutBlockSheet());
      multibuffersource$buffersource.endBatch(Sheets.bedSheet());
      multibuffersource$buffersource.endBatch(Sheets.shulkerBoxSheet());
      multibuffersource$buffersource.endBatch(Sheets.signSheet());
      multibuffersource$buffersource.endBatch(Sheets.hangingSignSheet());
      multibuffersource$buffersource.endBatch(Sheets.chestSheet());
      this.renderBuffers.outlineBufferSource().endOutlineBatch();
      if (flag2) {
         this.entityEffect.process(p_109601_);
         this.minecraft.getMainRenderTarget().bindWrite(false);
      }

      net.minecraftforge.client.ForgeHooksClient.dispatchRenderStage(net.minecraftforge.client.event.RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES, this, p_109600_, p_254120_, this.ticks, p_109604_, frustum);
      profilerfiller.popPush("destroyProgress");

      for(Long2ObjectMap.Entry<SortedSet<BlockDestructionProgress>> entry : this.destructionProgress.long2ObjectEntrySet()) {
         BlockPos blockpos2 = BlockPos.of(entry.getLongKey());
         double d3 = (double)blockpos2.getX() - d0;
         double d4 = (double)blockpos2.getY() - d1;
         double d5 = (double)blockpos2.getZ() - d2;
         if (!(d3 * d3 + d4 * d4 + d5 * d5 > 1024.0D)) {
            SortedSet<BlockDestructionProgress> sortedset1 = entry.getValue();
            if (sortedset1 != null && !sortedset1.isEmpty()) {
               int k = sortedset1.last().getProgress();
               p_109600_.pushPose();
               p_109600_.translate((double)blockpos2.getX() - d0, (double)blockpos2.getY() - d1, (double)blockpos2.getZ() - d2);
               PoseStack.Pose posestack$pose1 = p_109600_.last();
               VertexConsumer vertexconsumer1 = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(k)), posestack$pose1.pose(), posestack$pose1.normal(), 1.0F);
               net.minecraftforge.client.model.data.ModelData modelData = level.getModelDataManager().getAt(blockpos2);
               this.minecraft.getBlockRenderer().renderBreakingTexture(this.level.getBlockState(blockpos2), blockpos2, this.level, p_109600_, vertexconsumer1, modelData == null ? net.minecraftforge.client.model.data.ModelData.EMPTY : modelData);
               p_109600_.popPose();
            }
         }
      }

      this.checkPoseStack(p_109600_);
      HitResult hitresult = this.minecraft.hitResult;
      if (p_109603_ && hitresult != null && hitresult.getType() == HitResult.Type.BLOCK) {
         profilerfiller.popPush("outline");
         BlockPos blockpos1 = ((BlockHitResult)hitresult).getBlockPos();
         BlockState blockstate = this.level.getBlockState(blockpos1);
         if (!net.minecraftforge.client.ForgeHooksClient.onDrawHighlight(this, p_109604_, hitresult, p_109601_, p_109600_, multibuffersource$buffersource))
         if (!blockstate.isAir() && this.level.getWorldBorder().isWithinBounds(blockpos1)) {
            VertexConsumer vertexconsumer2 = multibuffersource$buffersource.getBuffer(RenderType.lines());
            this.renderHitOutline(p_109600_, vertexconsumer2, p_109604_.getEntity(), d0, d1, d2, blockpos1, blockstate);
         }
      } else if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
         net.minecraftforge.client.ForgeHooksClient.onDrawHighlight(this, p_109604_, hitresult, p_109601_, p_109600_, multibuffersource$buffersource);
      }

      this.minecraft.debugRenderer.render(p_109600_, multibuffersource$buffersource, d0, d1, d2);
      multibuffersource$buffersource.endLastBatch();
      PoseStack posestack = RenderSystem.getModelViewStack();
      RenderSystem.applyModelViewMatrix();
      multibuffersource$buffersource.endBatch(Sheets.translucentCullBlockSheet());
      multibuffersource$buffersource.endBatch(Sheets.bannerSheet());
      multibuffersource$buffersource.endBatch(Sheets.shieldSheet());
      multibuffersource$buffersource.endBatch(RenderType.armorGlint());
      multibuffersource$buffersource.endBatch(RenderType.armorEntityGlint());
      multibuffersource$buffersource.endBatch(RenderType.glint());
      multibuffersource$buffersource.endBatch(RenderType.glintDirect());
      multibuffersource$buffersource.endBatch(RenderType.glintTranslucent());
      multibuffersource$buffersource.endBatch(RenderType.entityGlint());
      multibuffersource$buffersource.endBatch(RenderType.entityGlintDirect());
      multibuffersource$buffersource.endBatch(RenderType.waterMask());
      this.renderBuffers.crumblingBufferSource().endBatch();
      if (this.transparencyChain != null) {
         multibuffersource$buffersource.endBatch(RenderType.lines());
         multibuffersource$buffersource.endBatch();
         this.translucentTarget.clear(Minecraft.ON_OSX);
         this.translucentTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
         profilerfiller.popPush("translucent");
         this.renderChunkLayer(RenderType.translucent(), p_109600_, d0, d1, d2, p_254120_);
         profilerfiller.popPush("string");
         this.renderChunkLayer(RenderType.tripwire(), p_109600_, d0, d1, d2, p_254120_);
         this.particlesTarget.clear(Minecraft.ON_OSX);
         this.particlesTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
         RenderStateShard.PARTICLES_TARGET.setupRenderState();
         profilerfiller.popPush("particles");
         this.minecraft.particleEngine.render(p_109600_, multibuffersource$buffersource, p_109606_, p_109604_, p_109601_, frustum);
         net.minecraftforge.client.ForgeHooksClient.dispatchRenderStage(net.minecraftforge.client.event.RenderLevelStageEvent.Stage.AFTER_PARTICLES, this, p_109600_, p_254120_, this.ticks, p_109604_, frustum);
         RenderStateShard.PARTICLES_TARGET.clearRenderState();
      } else {
         profilerfiller.popPush("translucent");
         if (this.translucentTarget != null) {
            this.translucentTarget.clear(Minecraft.ON_OSX);
         }

         this.renderChunkLayer(RenderType.translucent(), p_109600_, d0, d1, d2, p_254120_);
         multibuffersource$buffersource.endBatch(RenderType.lines());
         multibuffersource$buffersource.endBatch();
         profilerfiller.popPush("string");
         this.renderChunkLayer(RenderType.tripwire(), p_109600_, d0, d1, d2, p_254120_);
         profilerfiller.popPush("particles");
         this.minecraft.particleEngine.render(p_109600_, multibuffersource$buffersource, p_109606_, p_109604_, p_109601_, frustum);
         net.minecraftforge.client.ForgeHooksClient.dispatchRenderStage(net.minecraftforge.client.event.RenderLevelStageEvent.Stage.AFTER_PARTICLES, this, p_109600_, p_254120_, this.ticks, p_109604_, frustum);
      }

      posestack.pushPose();
      posestack.mulPoseMatrix(p_109600_.last().pose());
      RenderSystem.applyModelViewMatrix();
      if (this.minecraft.options.getCloudsType() != CloudStatus.OFF) {
         if (this.transparencyChain != null) {
            this.cloudsTarget.clear(Minecraft.ON_OSX);
            RenderStateShard.CLOUDS_TARGET.setupRenderState();
            profilerfiller.popPush("clouds");
            this.renderClouds(p_109600_, p_254120_, p_109601_, d0, d1, d2);
            RenderStateShard.CLOUDS_TARGET.clearRenderState();
         } else {
            profilerfiller.popPush("clouds");
            RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
            this.renderClouds(p_109600_, p_254120_, p_109601_, d0, d1, d2);
         }
      }

      if (this.transparencyChain != null) {
         RenderStateShard.WEATHER_TARGET.setupRenderState();
         profilerfiller.popPush("weather");
         this.renderSnowAndRain(p_109606_, p_109601_, d0, d1, d2);
         net.minecraftforge.client.ForgeHooksClient.dispatchRenderStage(net.minecraftforge.client.event.RenderLevelStageEvent.Stage.AFTER_WEATHER, this, p_109600_, p_254120_, this.ticks, p_109604_, frustum);
         this.renderWorldBorder(p_109604_);
         RenderStateShard.WEATHER_TARGET.clearRenderState();
         this.transparencyChain.process(p_109601_);
         this.minecraft.getMainRenderTarget().bindWrite(false);
      } else {
         RenderSystem.depthMask(false);
         profilerfiller.popPush("weather");
         this.renderSnowAndRain(p_109606_, p_109601_, d0, d1, d2);
         net.minecraftforge.client.ForgeHooksClient.dispatchRenderStage(net.minecraftforge.client.event.RenderLevelStageEvent.Stage.AFTER_WEATHER, this, p_109600_, p_254120_, this.ticks, p_109604_, frustum);
         this.renderWorldBorder(p_109604_);
         RenderSystem.depthMask(true);
      }

      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
      this.renderDebug(p_109600_, multibuffersource$buffersource, p_109604_);
      multibuffersource$buffersource.endLastBatch();
      RenderSystem.depthMask(true);
      RenderSystem.disableBlend();
      FogRenderer.setupNoFog();
   }

   private void checkPoseStack(PoseStack p_109589_) {
      if (!p_109589_.clear()) {
         throw new IllegalStateException("Pose stack not empty");
      }
   }

   private void renderEntity(Entity p_109518_, double p_109519_, double p_109520_, double p_109521_, float p_109522_, PoseStack p_109523_, MultiBufferSource p_109524_) {
      double d0 = Mth.lerp((double)p_109522_, p_109518_.xOld, p_109518_.getX());
      double d1 = Mth.lerp((double)p_109522_, p_109518_.yOld, p_109518_.getY());
      double d2 = Mth.lerp((double)p_109522_, p_109518_.zOld, p_109518_.getZ());
      float f = Mth.lerp(p_109522_, p_109518_.yRotO, p_109518_.getYRot());
      this.entityRenderDispatcher.render(p_109518_, d0 - p_109519_, d1 - p_109520_, d2 - p_109521_, f, p_109522_, p_109523_, p_109524_, this.entityRenderDispatcher.getPackedLightCoords(p_109518_, p_109522_));
   }

   private void renderChunkLayer(RenderType p_172994_, PoseStack p_172995_, double p_172996_, double p_172997_, double p_172998_, Matrix4f p_254039_) {
      RenderSystem.assertOnRenderThread();
      p_172994_.setupRenderState();
      if (p_172994_ == RenderType.translucent()) {
         this.minecraft.getProfiler().push("translucent_sort");
         double d0 = p_172996_ - this.xTransparentOld;
         double d1 = p_172997_ - this.yTransparentOld;
         double d2 = p_172998_ - this.zTransparentOld;
         if (d0 * d0 + d1 * d1 + d2 * d2 > 1.0D) {
            int j = SectionPos.posToSectionCoord(p_172996_);
            int k = SectionPos.posToSectionCoord(p_172997_);
            int l = SectionPos.posToSectionCoord(p_172998_);
            boolean flag = j != SectionPos.posToSectionCoord(this.xTransparentOld) || l != SectionPos.posToSectionCoord(this.zTransparentOld) || k != SectionPos.posToSectionCoord(this.yTransparentOld);
            this.xTransparentOld = p_172996_;
            this.yTransparentOld = p_172997_;
            this.zTransparentOld = p_172998_;
            int i1 = 0;

            for(LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo : this.renderChunksInFrustum) {
               if (i1 < 15 && (flag || levelrenderer$renderchunkinfo.isAxisAlignedWith(j, k, l)) && levelrenderer$renderchunkinfo.chunk.resortTransparency(p_172994_, this.chunkRenderDispatcher)) {
                  ++i1;
               }
            }
         }

         this.minecraft.getProfiler().pop();
      }

      this.minecraft.getProfiler().push("filterempty");
      this.minecraft.getProfiler().popPush(() -> {
         return "render_" + p_172994_;
      });
      boolean flag1 = p_172994_ != RenderType.translucent();
      ObjectListIterator<LevelRenderer.RenderChunkInfo> objectlistiterator = this.renderChunksInFrustum.listIterator(flag1 ? 0 : this.renderChunksInFrustum.size());
      ShaderInstance shaderinstance = RenderSystem.getShader();

      for(int i = 0; i < 12; ++i) {
         int j1 = RenderSystem.getShaderTexture(i);
         shaderinstance.setSampler("Sampler" + i, j1);
      }

      if (shaderinstance.MODEL_VIEW_MATRIX != null) {
         shaderinstance.MODEL_VIEW_MATRIX.set(p_172995_.last().pose());
      }

      if (shaderinstance.PROJECTION_MATRIX != null) {
         shaderinstance.PROJECTION_MATRIX.set(p_254039_);
      }

      if (shaderinstance.COLOR_MODULATOR != null) {
         shaderinstance.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
      }

      if (shaderinstance.GLINT_ALPHA != null) {
         shaderinstance.GLINT_ALPHA.set(RenderSystem.getShaderGlintAlpha());
      }

      if (shaderinstance.FOG_START != null) {
         shaderinstance.FOG_START.set(RenderSystem.getShaderFogStart());
      }

      if (shaderinstance.FOG_END != null) {
         shaderinstance.FOG_END.set(RenderSystem.getShaderFogEnd());
      }

      if (shaderinstance.FOG_COLOR != null) {
         shaderinstance.FOG_COLOR.set(RenderSystem.getShaderFogColor());
      }

      if (shaderinstance.FOG_SHAPE != null) {
         shaderinstance.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
      }

      if (shaderinstance.TEXTURE_MATRIX != null) {
         shaderinstance.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
      }

      if (shaderinstance.GAME_TIME != null) {
         shaderinstance.GAME_TIME.set(RenderSystem.getShaderGameTime());
      }

      RenderSystem.setupShaderLights(shaderinstance);
      shaderinstance.apply();
      Uniform uniform = shaderinstance.CHUNK_OFFSET;

      while(true) {
         if (flag1) {
            if (!objectlistiterator.hasNext()) {
               break;
            }
         } else if (!objectlistiterator.hasPrevious()) {
            break;
         }

         LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo1 = flag1 ? objectlistiterator.next() : objectlistiterator.previous();
         ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk = levelrenderer$renderchunkinfo1.chunk;
         if (!chunkrenderdispatcher$renderchunk.getCompiledChunk().isEmpty(p_172994_)) {
            VertexBuffer vertexbuffer = chunkrenderdispatcher$renderchunk.getBuffer(p_172994_);
            BlockPos blockpos = chunkrenderdispatcher$renderchunk.getOrigin();
            if (uniform != null) {
               uniform.set((float)((double)blockpos.getX() - p_172996_), (float)((double)blockpos.getY() - p_172997_), (float)((double)blockpos.getZ() - p_172998_));
               uniform.upload();
            }

            vertexbuffer.bind();
            vertexbuffer.draw();
         }
      }

      if (uniform != null) {
         uniform.set(0.0F, 0.0F, 0.0F);
      }

      shaderinstance.clear();
      VertexBuffer.unbind();
      this.minecraft.getProfiler().pop();
      net.minecraftforge.client.ForgeHooksClient.dispatchRenderStage(p_172994_, this, p_172995_, p_254039_, this.ticks, this.minecraft.gameRenderer.getMainCamera(), this.getFrustum());
      p_172994_.clearRenderState();
   }

   private void renderDebug(PoseStack p_271014_, MultiBufferSource p_270107_, Camera p_270483_) {
      if (this.minecraft.chunkPath || this.minecraft.chunkVisibility) {
         double d0 = p_270483_.getPosition().x();
         double d1 = p_270483_.getPosition().y();
         double d2 = p_270483_.getPosition().z();

         for(LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo : this.renderChunksInFrustum) {
            ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk = levelrenderer$renderchunkinfo.chunk;
            BlockPos blockpos = chunkrenderdispatcher$renderchunk.getOrigin();
            p_271014_.pushPose();
            p_271014_.translate((double)blockpos.getX() - d0, (double)blockpos.getY() - d1, (double)blockpos.getZ() - d2);
            Matrix4f matrix4f = p_271014_.last().pose();
            if (this.minecraft.chunkPath) {
               VertexConsumer vertexconsumer1 = p_270107_.getBuffer(RenderType.lines());
               int i = levelrenderer$renderchunkinfo.step == 0 ? 0 : Mth.hsvToRgb((float)levelrenderer$renderchunkinfo.step / 50.0F, 0.9F, 0.9F);
               int j = i >> 16 & 255;
               int k = i >> 8 & 255;
               int l = i & 255;

               for(int i1 = 0; i1 < DIRECTIONS.length; ++i1) {
                  if (levelrenderer$renderchunkinfo.hasSourceDirection(i1)) {
                     Direction direction = DIRECTIONS[i1];
                     vertexconsumer1.vertex(matrix4f, 8.0F, 8.0F, 8.0F).color(j, k, l, 255).normal((float)direction.getStepX(), (float)direction.getStepY(), (float)direction.getStepZ()).endVertex();
                     vertexconsumer1.vertex(matrix4f, (float)(8 - 16 * direction.getStepX()), (float)(8 - 16 * direction.getStepY()), (float)(8 - 16 * direction.getStepZ())).color(j, k, l, 255).normal((float)direction.getStepX(), (float)direction.getStepY(), (float)direction.getStepZ()).endVertex();
                  }
               }
            }

            if (this.minecraft.chunkVisibility && !chunkrenderdispatcher$renderchunk.getCompiledChunk().hasNoRenderableLayers()) {
               VertexConsumer vertexconsumer3 = p_270107_.getBuffer(RenderType.lines());
               int j1 = 0;

               for(Direction direction2 : DIRECTIONS) {
                  for(Direction direction1 : DIRECTIONS) {
                     boolean flag = chunkrenderdispatcher$renderchunk.getCompiledChunk().facesCanSeeEachother(direction2, direction1);
                     if (!flag) {
                        ++j1;
                        vertexconsumer3.vertex(matrix4f, (float)(8 + 8 * direction2.getStepX()), (float)(8 + 8 * direction2.getStepY()), (float)(8 + 8 * direction2.getStepZ())).color(255, 0, 0, 255).normal((float)direction2.getStepX(), (float)direction2.getStepY(), (float)direction2.getStepZ()).endVertex();
                        vertexconsumer3.vertex(matrix4f, (float)(8 + 8 * direction1.getStepX()), (float)(8 + 8 * direction1.getStepY()), (float)(8 + 8 * direction1.getStepZ())).color(255, 0, 0, 255).normal((float)direction1.getStepX(), (float)direction1.getStepY(), (float)direction1.getStepZ()).endVertex();
                     }
                  }
               }

               if (j1 > 0) {
                  VertexConsumer vertexconsumer4 = p_270107_.getBuffer(RenderType.debugQuads());
                  float f = 0.5F;
                  float f1 = 0.2F;
                  vertexconsumer4.vertex(matrix4f, 0.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 15.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 15.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 0.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 0.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 15.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 15.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 0.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 0.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 0.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 0.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 0.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 15.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 15.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 15.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 15.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 0.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 15.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 15.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 0.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 0.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 15.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 15.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  vertexconsumer4.vertex(matrix4f, 0.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
               }
            }

            p_271014_.popPose();
         }
      }

      if (this.capturedFrustum != null) {
         p_271014_.pushPose();
         p_271014_.translate((float)(this.frustumPos.x - p_270483_.getPosition().x), (float)(this.frustumPos.y - p_270483_.getPosition().y), (float)(this.frustumPos.z - p_270483_.getPosition().z));
         Matrix4f matrix4f1 = p_271014_.last().pose();
         VertexConsumer vertexconsumer = p_270107_.getBuffer(RenderType.debugQuads());
         this.addFrustumQuad(vertexconsumer, matrix4f1, 0, 1, 2, 3, 0, 1, 1);
         this.addFrustumQuad(vertexconsumer, matrix4f1, 4, 5, 6, 7, 1, 0, 0);
         this.addFrustumQuad(vertexconsumer, matrix4f1, 0, 1, 5, 4, 1, 1, 0);
         this.addFrustumQuad(vertexconsumer, matrix4f1, 2, 3, 7, 6, 0, 0, 1);
         this.addFrustumQuad(vertexconsumer, matrix4f1, 0, 4, 7, 3, 0, 1, 0);
         this.addFrustumQuad(vertexconsumer, matrix4f1, 1, 5, 6, 2, 1, 0, 1);
         VertexConsumer vertexconsumer2 = p_270107_.getBuffer(RenderType.lines());
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 0);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 1);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 1);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 2);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 2);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 3);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 3);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 0);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 4);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 5);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 5);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 6);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 6);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 7);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 7);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 4);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 0);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 4);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 1);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 5);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 2);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 6);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 3);
         this.addFrustumVertex(vertexconsumer2, matrix4f1, 7);
         p_271014_.popPose();
      }

   }

   private void addFrustumVertex(VertexConsumer p_270950_, Matrix4f p_270118_, int p_270865_) {
      p_270950_.vertex(p_270118_, this.frustumPoints[p_270865_].x(), this.frustumPoints[p_270865_].y(), this.frustumPoints[p_270865_].z()).color(0, 0, 0, 255).normal(0.0F, 0.0F, -1.0F).endVertex();
   }

   private void addFrustumQuad(VertexConsumer p_270858_, Matrix4f p_270341_, int p_270702_, int p_270959_, int p_270732_, int p_270363_, int p_270273_, int p_270934_, int p_270916_) {
      float f = 0.25F;
      p_270858_.vertex(p_270341_, this.frustumPoints[p_270702_].x(), this.frustumPoints[p_270702_].y(), this.frustumPoints[p_270702_].z()).color((float)p_270273_, (float)p_270934_, (float)p_270916_, 0.25F).endVertex();
      p_270858_.vertex(p_270341_, this.frustumPoints[p_270959_].x(), this.frustumPoints[p_270959_].y(), this.frustumPoints[p_270959_].z()).color((float)p_270273_, (float)p_270934_, (float)p_270916_, 0.25F).endVertex();
      p_270858_.vertex(p_270341_, this.frustumPoints[p_270732_].x(), this.frustumPoints[p_270732_].y(), this.frustumPoints[p_270732_].z()).color((float)p_270273_, (float)p_270934_, (float)p_270916_, 0.25F).endVertex();
      p_270858_.vertex(p_270341_, this.frustumPoints[p_270363_].x(), this.frustumPoints[p_270363_].y(), this.frustumPoints[p_270363_].z()).color((float)p_270273_, (float)p_270934_, (float)p_270916_, 0.25F).endVertex();
   }

   public void captureFrustum() {
      this.captureFrustum = true;
   }

   public void killFrustum() {
      this.capturedFrustum = null;
   }

   public void tick() {
      ++this.ticks;
      if (this.ticks % 20 == 0) {
         Iterator<BlockDestructionProgress> iterator = this.destroyingBlocks.values().iterator();

         while(iterator.hasNext()) {
            BlockDestructionProgress blockdestructionprogress = iterator.next();
            int i = blockdestructionprogress.getUpdatedRenderTick();
            if (this.ticks - i > 400) {
               iterator.remove();
               this.removeProgress(blockdestructionprogress);
            }
         }

      }
   }

   private void removeProgress(BlockDestructionProgress p_109766_) {
      long i = p_109766_.getPos().asLong();
      Set<BlockDestructionProgress> set = this.destructionProgress.get(i);
      set.remove(p_109766_);
      if (set.isEmpty()) {
         this.destructionProgress.remove(i);
      }

   }

   private void renderEndSky(PoseStack p_109781_) {
      RenderSystem.enableBlend();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      RenderSystem.setShaderTexture(0, END_SKY_LOCATION);
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();

      for(int i = 0; i < 6; ++i) {
         p_109781_.pushPose();
         if (i == 1) {
            p_109781_.mulPose(Axis.XP.rotationDegrees(90.0F));
         }

         if (i == 2) {
            p_109781_.mulPose(Axis.XP.rotationDegrees(-90.0F));
         }

         if (i == 3) {
            p_109781_.mulPose(Axis.XP.rotationDegrees(180.0F));
         }

         if (i == 4) {
            p_109781_.mulPose(Axis.ZP.rotationDegrees(90.0F));
         }

         if (i == 5) {
            p_109781_.mulPose(Axis.ZP.rotationDegrees(-90.0F));
         }

         Matrix4f matrix4f = p_109781_.last().pose();
         bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).uv(0.0F, 0.0F).color(40, 40, 40, 255).endVertex();
         bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).uv(0.0F, 16.0F).color(40, 40, 40, 255).endVertex();
         bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).uv(16.0F, 16.0F).color(40, 40, 40, 255).endVertex();
         bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).uv(16.0F, 0.0F).color(40, 40, 40, 255).endVertex();
         tesselator.end();
         p_109781_.popPose();
      }

      RenderSystem.depthMask(true);
      RenderSystem.disableBlend();
   }

   public void renderSky(PoseStack p_202424_, Matrix4f p_254034_, float p_202426_, Camera p_202427_, boolean p_202428_, Runnable p_202429_) {
      if (level.effects().renderSky(level, ticks, p_202426_, p_202424_, p_202427_, p_254034_, p_202428_, p_202429_))
         return;
      p_202429_.run();
      if (!p_202428_) {
         FogType fogtype = p_202427_.getFluidInCamera();
         if (fogtype != FogType.POWDER_SNOW && fogtype != FogType.LAVA && !this.doesMobEffectBlockSky(p_202427_)) {
            if (this.minecraft.level.effects().skyType() == DimensionSpecialEffects.SkyType.END) {
               this.renderEndSky(p_202424_);
            } else if (this.minecraft.level.effects().skyType() == DimensionSpecialEffects.SkyType.NORMAL) {
               Vec3 vec3 = this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), p_202426_);
               float f = (float)vec3.x;
               float f1 = (float)vec3.y;
               float f2 = (float)vec3.z;
               FogRenderer.levelFogColor();
               BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
               RenderSystem.depthMask(false);
               RenderSystem.setShaderColor(f, f1, f2, 1.0F);
               ShaderInstance shaderinstance = RenderSystem.getShader();
               this.skyBuffer.bind();
               this.skyBuffer.drawWithShader(p_202424_.last().pose(), p_254034_, shaderinstance);
               VertexBuffer.unbind();
               RenderSystem.enableBlend();
               float[] afloat = this.level.effects().getSunriseColor(this.level.getTimeOfDay(p_202426_), p_202426_);
               if (afloat != null) {
                  RenderSystem.setShader(GameRenderer::getPositionColorShader);
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                  p_202424_.pushPose();
                  p_202424_.mulPose(Axis.XP.rotationDegrees(90.0F));
                  float f3 = Mth.sin(this.level.getSunAngle(p_202426_)) < 0.0F ? 180.0F : 0.0F;
                  p_202424_.mulPose(Axis.ZP.rotationDegrees(f3));
                  p_202424_.mulPose(Axis.ZP.rotationDegrees(90.0F));
                  float f4 = afloat[0];
                  float f5 = afloat[1];
                  float f6 = afloat[2];
                  Matrix4f matrix4f = p_202424_.last().pose();
                  bufferbuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
                  bufferbuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(f4, f5, f6, afloat[3]).endVertex();
                  int i = 16;

                  for(int j = 0; j <= 16; ++j) {
                     float f7 = (float)j * ((float)Math.PI * 2F) / 16.0F;
                     float f8 = Mth.sin(f7);
                     float f9 = Mth.cos(f7);
                     bufferbuilder.vertex(matrix4f, f8 * 120.0F, f9 * 120.0F, -f9 * 40.0F * afloat[3]).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
                  }

                  BufferUploader.drawWithShader(bufferbuilder.end());
                  p_202424_.popPose();
               }

               RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
               p_202424_.pushPose();
               float f11 = 1.0F - this.level.getRainLevel(p_202426_);
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f11);
               p_202424_.mulPose(Axis.YP.rotationDegrees(-90.0F));
               p_202424_.mulPose(Axis.XP.rotationDegrees(this.level.getTimeOfDay(p_202426_) * 360.0F));
               Matrix4f matrix4f1 = p_202424_.last().pose();
               float f12 = 30.0F;
               RenderSystem.setShader(GameRenderer::getPositionTexShader);
               RenderSystem.setShaderTexture(0, SUN_LOCATION);
               bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
               bufferbuilder.vertex(matrix4f1, -f12, 100.0F, -f12).uv(0.0F, 0.0F).endVertex();
               bufferbuilder.vertex(matrix4f1, f12, 100.0F, -f12).uv(1.0F, 0.0F).endVertex();
               bufferbuilder.vertex(matrix4f1, f12, 100.0F, f12).uv(1.0F, 1.0F).endVertex();
               bufferbuilder.vertex(matrix4f1, -f12, 100.0F, f12).uv(0.0F, 1.0F).endVertex();
               BufferUploader.drawWithShader(bufferbuilder.end());
               f12 = 20.0F;
               RenderSystem.setShaderTexture(0, MOON_LOCATION);
               int k = this.level.getMoonPhase();
               int l = k % 4;
               int i1 = k / 4 % 2;
               float f13 = (float)(l + 0) / 4.0F;
               float f14 = (float)(i1 + 0) / 2.0F;
               float f15 = (float)(l + 1) / 4.0F;
               float f16 = (float)(i1 + 1) / 2.0F;
               bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
               bufferbuilder.vertex(matrix4f1, -f12, -100.0F, f12).uv(f15, f16).endVertex();
               bufferbuilder.vertex(matrix4f1, f12, -100.0F, f12).uv(f13, f16).endVertex();
               bufferbuilder.vertex(matrix4f1, f12, -100.0F, -f12).uv(f13, f14).endVertex();
               bufferbuilder.vertex(matrix4f1, -f12, -100.0F, -f12).uv(f15, f14).endVertex();
               BufferUploader.drawWithShader(bufferbuilder.end());
               float f10 = this.level.getStarBrightness(p_202426_) * f11;
               if (f10 > 0.0F) {
                  RenderSystem.setShaderColor(f10, f10, f10, f10);
                  FogRenderer.setupNoFog();
                  this.starBuffer.bind();
                  this.starBuffer.drawWithShader(p_202424_.last().pose(), p_254034_, GameRenderer.getPositionShader());
                  VertexBuffer.unbind();
                  p_202429_.run();
               }

               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.disableBlend();
               RenderSystem.defaultBlendFunc();
               p_202424_.popPose();
               RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
               double d0 = this.minecraft.player.getEyePosition(p_202426_).y - this.level.getLevelData().getHorizonHeight(this.level);
               if (d0 < 0.0D) {
                  p_202424_.pushPose();
                  p_202424_.translate(0.0F, 12.0F, 0.0F);
                  this.darkBuffer.bind();
                  this.darkBuffer.drawWithShader(p_202424_.last().pose(), p_254034_, shaderinstance);
                  VertexBuffer.unbind();
                  p_202424_.popPose();
               }

               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.depthMask(true);
            }
         }
      }
   }

   private boolean doesMobEffectBlockSky(Camera p_234311_) {
      Entity entity = p_234311_.getEntity();
      if (!(entity instanceof LivingEntity livingentity)) {
         return false;
      } else {
         return livingentity.hasEffect(MobEffects.BLINDNESS) || livingentity.hasEffect(MobEffects.DARKNESS);
      }
   }

   public void renderClouds(PoseStack p_254145_, Matrix4f p_254537_, float p_254364_, double p_253843_, double p_253663_, double p_253795_) {
      if (level.effects().renderClouds(level, ticks, p_254364_, p_254145_, p_253843_, p_253663_, p_253795_, p_254537_))
         return;
      float f = this.level.effects().getCloudHeight();
      if (!Float.isNaN(f)) {
         RenderSystem.disableCull();
         RenderSystem.enableBlend();
         RenderSystem.enableDepthTest();
         RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         RenderSystem.depthMask(true);
         float f1 = 12.0F;
         float f2 = 4.0F;
         double d0 = 2.0E-4D;
         double d1 = (double)(((float)this.ticks + p_254364_) * 0.03F);
         double d2 = (p_253843_ + d1) / 12.0D;
         double d3 = (double)(f - (float)p_253663_ + 0.33F);
         double d4 = p_253795_ / 12.0D + (double)0.33F;
         d2 -= (double)(Mth.floor(d2 / 2048.0D) * 2048);
         d4 -= (double)(Mth.floor(d4 / 2048.0D) * 2048);
         float f3 = (float)(d2 - (double)Mth.floor(d2));
         float f4 = (float)(d3 / 4.0D - (double)Mth.floor(d3 / 4.0D)) * 4.0F;
         float f5 = (float)(d4 - (double)Mth.floor(d4));
         Vec3 vec3 = this.level.getCloudColor(p_254364_);
         int i = (int)Math.floor(d2);
         int j = (int)Math.floor(d3 / 4.0D);
         int k = (int)Math.floor(d4);
         if (i != this.prevCloudX || j != this.prevCloudY || k != this.prevCloudZ || this.minecraft.options.getCloudsType() != this.prevCloudsType || this.prevCloudColor.distanceToSqr(vec3) > 2.0E-4D) {
            this.prevCloudX = i;
            this.prevCloudY = j;
            this.prevCloudZ = k;
            this.prevCloudColor = vec3;
            this.prevCloudsType = this.minecraft.options.getCloudsType();
            this.generateClouds = true;
         }

         if (this.generateClouds) {
            this.generateClouds = false;
            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
            if (this.cloudBuffer != null) {
               this.cloudBuffer.close();
            }

            this.cloudBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            BufferBuilder.RenderedBuffer bufferbuilder$renderedbuffer = this.buildClouds(bufferbuilder, d2, d3, d4, vec3);
            this.cloudBuffer.bind();
            this.cloudBuffer.upload(bufferbuilder$renderedbuffer);
            VertexBuffer.unbind();
         }

         RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
         RenderSystem.setShaderTexture(0, CLOUDS_LOCATION);
         FogRenderer.levelFogColor();
         p_254145_.pushPose();
         p_254145_.scale(12.0F, 1.0F, 12.0F);
         p_254145_.translate(-f3, f4, -f5);
         if (this.cloudBuffer != null) {
            this.cloudBuffer.bind();
            int l = this.prevCloudsType == CloudStatus.FANCY ? 0 : 1;

            for(int i1 = l; i1 < 2; ++i1) {
               if (i1 == 0) {
                  RenderSystem.colorMask(false, false, false, false);
               } else {
                  RenderSystem.colorMask(true, true, true, true);
               }

               ShaderInstance shaderinstance = RenderSystem.getShader();
               this.cloudBuffer.drawWithShader(p_254145_.last().pose(), p_254537_, shaderinstance);
            }

            VertexBuffer.unbind();
         }

         p_254145_.popPose();
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
      }
   }

   private BufferBuilder.RenderedBuffer buildClouds(BufferBuilder p_234262_, double p_234263_, double p_234264_, double p_234265_, Vec3 p_234266_) {
      float f = 4.0F;
      float f1 = 0.00390625F;
      int i = 8;
      int j = 4;
      float f2 = 9.765625E-4F;
      float f3 = (float)Mth.floor(p_234263_) * 0.00390625F;
      float f4 = (float)Mth.floor(p_234265_) * 0.00390625F;
      float f5 = (float)p_234266_.x;
      float f6 = (float)p_234266_.y;
      float f7 = (float)p_234266_.z;
      float f8 = f5 * 0.9F;
      float f9 = f6 * 0.9F;
      float f10 = f7 * 0.9F;
      float f11 = f5 * 0.7F;
      float f12 = f6 * 0.7F;
      float f13 = f7 * 0.7F;
      float f14 = f5 * 0.8F;
      float f15 = f6 * 0.8F;
      float f16 = f7 * 0.8F;
      RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
      p_234262_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
      float f17 = (float)Math.floor(p_234264_ / 4.0D) * 4.0F;
      if (this.prevCloudsType == CloudStatus.FANCY) {
         for(int k = -3; k <= 4; ++k) {
            for(int l = -3; l <= 4; ++l) {
               float f18 = (float)(k * 8);
               float f19 = (float)(l * 8);
               if (f17 > -5.0F) {
                  p_234262_.vertex((double)(f18 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + 8.0F)).uv((f18 + 0.0F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                  p_234262_.vertex((double)(f18 + 8.0F), (double)(f17 + 0.0F), (double)(f19 + 8.0F)).uv((f18 + 8.0F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                  p_234262_.vertex((double)(f18 + 8.0F), (double)(f17 + 0.0F), (double)(f19 + 0.0F)).uv((f18 + 8.0F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                  p_234262_.vertex((double)(f18 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + 0.0F)).uv((f18 + 0.0F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               }

               if (f17 <= 5.0F) {
                  p_234262_.vertex((double)(f18 + 0.0F), (double)(f17 + 4.0F - 9.765625E-4F), (double)(f19 + 8.0F)).uv((f18 + 0.0F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                  p_234262_.vertex((double)(f18 + 8.0F), (double)(f17 + 4.0F - 9.765625E-4F), (double)(f19 + 8.0F)).uv((f18 + 8.0F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                  p_234262_.vertex((double)(f18 + 8.0F), (double)(f17 + 4.0F - 9.765625E-4F), (double)(f19 + 0.0F)).uv((f18 + 8.0F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                  p_234262_.vertex((double)(f18 + 0.0F), (double)(f17 + 4.0F - 9.765625E-4F), (double)(f19 + 0.0F)).uv((f18 + 0.0F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
               }

               if (k > -1) {
                  for(int i1 = 0; i1 < 8; ++i1) {
                     p_234262_.vertex((double)(f18 + (float)i1 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + 8.0F)).uv((f18 + (float)i1 + 0.5F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                     p_234262_.vertex((double)(f18 + (float)i1 + 0.0F), (double)(f17 + 4.0F), (double)(f19 + 8.0F)).uv((f18 + (float)i1 + 0.5F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                     p_234262_.vertex((double)(f18 + (float)i1 + 0.0F), (double)(f17 + 4.0F), (double)(f19 + 0.0F)).uv((f18 + (float)i1 + 0.5F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                     p_234262_.vertex((double)(f18 + (float)i1 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + 0.0F)).uv((f18 + (float)i1 + 0.5F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                  }
               }

               if (k <= 1) {
                  for(int j2 = 0; j2 < 8; ++j2) {
                     p_234262_.vertex((double)(f18 + (float)j2 + 1.0F - 9.765625E-4F), (double)(f17 + 0.0F), (double)(f19 + 8.0F)).uv((f18 + (float)j2 + 0.5F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                     p_234262_.vertex((double)(f18 + (float)j2 + 1.0F - 9.765625E-4F), (double)(f17 + 4.0F), (double)(f19 + 8.0F)).uv((f18 + (float)j2 + 0.5F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                     p_234262_.vertex((double)(f18 + (float)j2 + 1.0F - 9.765625E-4F), (double)(f17 + 4.0F), (double)(f19 + 0.0F)).uv((f18 + (float)j2 + 0.5F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                     p_234262_.vertex((double)(f18 + (float)j2 + 1.0F - 9.765625E-4F), (double)(f17 + 0.0F), (double)(f19 + 0.0F)).uv((f18 + (float)j2 + 0.5F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                  }
               }

               if (l > -1) {
                  for(int k2 = 0; k2 < 8; ++k2) {
                     p_234262_.vertex((double)(f18 + 0.0F), (double)(f17 + 4.0F), (double)(f19 + (float)k2 + 0.0F)).uv((f18 + 0.0F) * 0.00390625F + f3, (f19 + (float)k2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                     p_234262_.vertex((double)(f18 + 8.0F), (double)(f17 + 4.0F), (double)(f19 + (float)k2 + 0.0F)).uv((f18 + 8.0F) * 0.00390625F + f3, (f19 + (float)k2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                     p_234262_.vertex((double)(f18 + 8.0F), (double)(f17 + 0.0F), (double)(f19 + (float)k2 + 0.0F)).uv((f18 + 8.0F) * 0.00390625F + f3, (f19 + (float)k2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                     p_234262_.vertex((double)(f18 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + (float)k2 + 0.0F)).uv((f18 + 0.0F) * 0.00390625F + f3, (f19 + (float)k2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                  }
               }

               if (l <= 1) {
                  for(int l2 = 0; l2 < 8; ++l2) {
                     p_234262_.vertex((double)(f18 + 0.0F), (double)(f17 + 4.0F), (double)(f19 + (float)l2 + 1.0F - 9.765625E-4F)).uv((f18 + 0.0F) * 0.00390625F + f3, (f19 + (float)l2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                     p_234262_.vertex((double)(f18 + 8.0F), (double)(f17 + 4.0F), (double)(f19 + (float)l2 + 1.0F - 9.765625E-4F)).uv((f18 + 8.0F) * 0.00390625F + f3, (f19 + (float)l2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                     p_234262_.vertex((double)(f18 + 8.0F), (double)(f17 + 0.0F), (double)(f19 + (float)l2 + 1.0F - 9.765625E-4F)).uv((f18 + 8.0F) * 0.00390625F + f3, (f19 + (float)l2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                     p_234262_.vertex((double)(f18 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + (float)l2 + 1.0F - 9.765625E-4F)).uv((f18 + 0.0F) * 0.00390625F + f3, (f19 + (float)l2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                  }
               }
            }
         }
      } else {
         int j1 = 1;
         int k1 = 32;

         for(int l1 = -32; l1 < 32; l1 += 32) {
            for(int i2 = -32; i2 < 32; i2 += 32) {
               p_234262_.vertex((double)(l1 + 0), (double)f17, (double)(i2 + 32)).uv((float)(l1 + 0) * 0.00390625F + f3, (float)(i2 + 32) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               p_234262_.vertex((double)(l1 + 32), (double)f17, (double)(i2 + 32)).uv((float)(l1 + 32) * 0.00390625F + f3, (float)(i2 + 32) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               p_234262_.vertex((double)(l1 + 32), (double)f17, (double)(i2 + 0)).uv((float)(l1 + 32) * 0.00390625F + f3, (float)(i2 + 0) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               p_234262_.vertex((double)(l1 + 0), (double)f17, (double)(i2 + 0)).uv((float)(l1 + 0) * 0.00390625F + f3, (float)(i2 + 0) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
            }
         }
      }

      return p_234262_.end();
   }

   private void compileChunks(Camera p_194371_) {
      this.minecraft.getProfiler().push("populate_chunks_to_compile");
      LevelLightEngine levellightengine = this.level.getLightEngine();
      RenderRegionCache renderregioncache = new RenderRegionCache();
      BlockPos blockpos = p_194371_.getBlockPosition();
      List<ChunkRenderDispatcher.RenderChunk> list = Lists.newArrayList();

      for(LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo : this.renderChunksInFrustum) {
         ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk = levelrenderer$renderchunkinfo.chunk;
         SectionPos sectionpos = SectionPos.of(chunkrenderdispatcher$renderchunk.getOrigin());
         if (chunkrenderdispatcher$renderchunk.isDirty() && levellightengine.lightOnInSection(sectionpos)) {
            boolean flag = false;
            if (this.minecraft.options.prioritizeChunkUpdates().get() == PrioritizeChunkUpdates.NEARBY) {
               BlockPos blockpos1 = chunkrenderdispatcher$renderchunk.getOrigin().offset(8, 8, 8);
               flag = !net.minecraftforge.common.ForgeConfig.CLIENT.alwaysSetupTerrainOffThread.get() && (blockpos1.distSqr(blockpos) < 768.0D || chunkrenderdispatcher$renderchunk.isDirtyFromPlayer()); // the target is the else block below, so invert the forge addition to get there early
            } else if (this.minecraft.options.prioritizeChunkUpdates().get() == PrioritizeChunkUpdates.PLAYER_AFFECTED) {
               flag = chunkrenderdispatcher$renderchunk.isDirtyFromPlayer();
            }

            if (flag) {
               this.minecraft.getProfiler().push("build_near_sync");
               this.chunkRenderDispatcher.rebuildChunkSync(chunkrenderdispatcher$renderchunk, renderregioncache);
               chunkrenderdispatcher$renderchunk.setNotDirty();
               this.minecraft.getProfiler().pop();
            } else {
               list.add(chunkrenderdispatcher$renderchunk);
            }
         }
      }

      this.minecraft.getProfiler().popPush("upload");
      this.chunkRenderDispatcher.uploadAllPendingUploads();
      this.minecraft.getProfiler().popPush("schedule_async_compile");

      for(ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk1 : list) {
         chunkrenderdispatcher$renderchunk1.rebuildChunkAsync(this.chunkRenderDispatcher, renderregioncache);
         chunkrenderdispatcher$renderchunk1.setNotDirty();
      }

      this.minecraft.getProfiler().pop();
   }

   private void renderWorldBorder(Camera p_173013_) {
      BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
      WorldBorder worldborder = this.level.getWorldBorder();
      double d0 = (double)(this.minecraft.options.getEffectiveRenderDistance() * 16);
      if (!(p_173013_.getPosition().x < worldborder.getMaxX() - d0) || !(p_173013_.getPosition().x > worldborder.getMinX() + d0) || !(p_173013_.getPosition().z < worldborder.getMaxZ() - d0) || !(p_173013_.getPosition().z > worldborder.getMinZ() + d0)) {
         double d1 = 1.0D - worldborder.getDistanceToBorder(p_173013_.getPosition().x, p_173013_.getPosition().z) / d0;
         d1 = Math.pow(d1, 4.0D);
         d1 = Mth.clamp(d1, 0.0D, 1.0D);
         double d2 = p_173013_.getPosition().x;
         double d3 = p_173013_.getPosition().z;
         double d4 = (double)this.minecraft.gameRenderer.getDepthFar();
         RenderSystem.enableBlend();
         RenderSystem.enableDepthTest();
         RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         RenderSystem.setShaderTexture(0, FORCEFIELD_LOCATION);
         RenderSystem.depthMask(Minecraft.useShaderTransparency());
         PoseStack posestack = RenderSystem.getModelViewStack();
         posestack.pushPose();
         RenderSystem.applyModelViewMatrix();
         int i = worldborder.getStatus().getColor();
         float f = (float)(i >> 16 & 255) / 255.0F;
         float f1 = (float)(i >> 8 & 255) / 255.0F;
         float f2 = (float)(i & 255) / 255.0F;
         RenderSystem.setShaderColor(f, f1, f2, (float)d1);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.polygonOffset(-3.0F, -3.0F);
         RenderSystem.enablePolygonOffset();
         RenderSystem.disableCull();
         float f3 = (float)(Util.getMillis() % 3000L) / 3000.0F;
         float f4 = (float)(-Mth.frac(p_173013_.getPosition().y * 0.5D));
         float f5 = f4 + (float)d4;
         bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         double d5 = Math.max((double)Mth.floor(d3 - d0), worldborder.getMinZ());
         double d6 = Math.min((double)Mth.ceil(d3 + d0), worldborder.getMaxZ());
         float f6 = (float)(Mth.floor(d5) & 1) * 0.5F;
         if (d2 > worldborder.getMaxX() - d0) {
            float f7 = f6;

            for(double d7 = d5; d7 < d6; f7 += 0.5F) {
               double d8 = Math.min(1.0D, d6 - d7);
               float f8 = (float)d8 * 0.5F;
               bufferbuilder.vertex(worldborder.getMaxX() - d2, -d4, d7 - d3).uv(f3 - f7, f3 + f5).endVertex();
               bufferbuilder.vertex(worldborder.getMaxX() - d2, -d4, d7 + d8 - d3).uv(f3 - (f8 + f7), f3 + f5).endVertex();
               bufferbuilder.vertex(worldborder.getMaxX() - d2, d4, d7 + d8 - d3).uv(f3 - (f8 + f7), f3 + f4).endVertex();
               bufferbuilder.vertex(worldborder.getMaxX() - d2, d4, d7 - d3).uv(f3 - f7, f3 + f4).endVertex();
               ++d7;
            }
         }

         if (d2 < worldborder.getMinX() + d0) {
            float f9 = f6;

            for(double d9 = d5; d9 < d6; f9 += 0.5F) {
               double d12 = Math.min(1.0D, d6 - d9);
               float f12 = (float)d12 * 0.5F;
               bufferbuilder.vertex(worldborder.getMinX() - d2, -d4, d9 - d3).uv(f3 + f9, f3 + f5).endVertex();
               bufferbuilder.vertex(worldborder.getMinX() - d2, -d4, d9 + d12 - d3).uv(f3 + f12 + f9, f3 + f5).endVertex();
               bufferbuilder.vertex(worldborder.getMinX() - d2, d4, d9 + d12 - d3).uv(f3 + f12 + f9, f3 + f4).endVertex();
               bufferbuilder.vertex(worldborder.getMinX() - d2, d4, d9 - d3).uv(f3 + f9, f3 + f4).endVertex();
               ++d9;
            }
         }

         d5 = Math.max((double)Mth.floor(d2 - d0), worldborder.getMinX());
         d6 = Math.min((double)Mth.ceil(d2 + d0), worldborder.getMaxX());
         f6 = (float)(Mth.floor(d5) & 1) * 0.5F;
         if (d3 > worldborder.getMaxZ() - d0) {
            float f10 = f6;

            for(double d10 = d5; d10 < d6; f10 += 0.5F) {
               double d13 = Math.min(1.0D, d6 - d10);
               float f13 = (float)d13 * 0.5F;
               bufferbuilder.vertex(d10 - d2, -d4, worldborder.getMaxZ() - d3).uv(f3 + f10, f3 + f5).endVertex();
               bufferbuilder.vertex(d10 + d13 - d2, -d4, worldborder.getMaxZ() - d3).uv(f3 + f13 + f10, f3 + f5).endVertex();
               bufferbuilder.vertex(d10 + d13 - d2, d4, worldborder.getMaxZ() - d3).uv(f3 + f13 + f10, f3 + f4).endVertex();
               bufferbuilder.vertex(d10 - d2, d4, worldborder.getMaxZ() - d3).uv(f3 + f10, f3 + f4).endVertex();
               ++d10;
            }
         }

         if (d3 < worldborder.getMinZ() + d0) {
            float f11 = f6;

            for(double d11 = d5; d11 < d6; f11 += 0.5F) {
               double d14 = Math.min(1.0D, d6 - d11);
               float f14 = (float)d14 * 0.5F;
               bufferbuilder.vertex(d11 - d2, -d4, worldborder.getMinZ() - d3).uv(f3 - f11, f3 + f5).endVertex();
               bufferbuilder.vertex(d11 + d14 - d2, -d4, worldborder.getMinZ() - d3).uv(f3 - (f14 + f11), f3 + f5).endVertex();
               bufferbuilder.vertex(d11 + d14 - d2, d4, worldborder.getMinZ() - d3).uv(f3 - (f14 + f11), f3 + f4).endVertex();
               bufferbuilder.vertex(d11 - d2, d4, worldborder.getMinZ() - d3).uv(f3 - f11, f3 + f4).endVertex();
               ++d11;
            }
         }

         BufferUploader.drawWithShader(bufferbuilder.end());
         RenderSystem.enableCull();
         RenderSystem.polygonOffset(0.0F, 0.0F);
         RenderSystem.disablePolygonOffset();
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
         posestack.popPose();
         RenderSystem.applyModelViewMatrix();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.depthMask(true);
      }
   }

   private void renderHitOutline(PoseStack p_109638_, VertexConsumer p_109639_, Entity p_109640_, double p_109641_, double p_109642_, double p_109643_, BlockPos p_109644_, BlockState p_109645_) {
      renderShape(p_109638_, p_109639_, p_109645_.getShape(this.level, p_109644_, CollisionContext.of(p_109640_)), (double)p_109644_.getX() - p_109641_, (double)p_109644_.getY() - p_109642_, (double)p_109644_.getZ() - p_109643_, 0.0F, 0.0F, 0.0F, 0.4F);
   }

   private static Vec3 mixColor(float p_286899_) {
      float f = 5.99999F;
      int i = (int)(Mth.clamp(p_286899_, 0.0F, 1.0F) * 5.99999F);
      float f1 = p_286899_ * 5.99999F - (float)i;
      Vec3 vec3;
      switch (i) {
         case 0:
            vec3 = new Vec3(1.0D, (double)f1, 0.0D);
            break;
         case 1:
            vec3 = new Vec3((double)(1.0F - f1), 1.0D, 0.0D);
            break;
         case 2:
            vec3 = new Vec3(0.0D, 1.0D, (double)f1);
            break;
         case 3:
            vec3 = new Vec3(0.0D, 1.0D - (double)f1, 1.0D);
            break;
         case 4:
            vec3 = new Vec3((double)f1, 0.0D, 1.0D);
            break;
         case 5:
            vec3 = new Vec3(1.0D, 0.0D, 1.0D - (double)f1);
            break;
         default:
            throw new IllegalStateException("Unexpected value: " + i);
      }

      return vec3;
   }

   private static Vec3 shiftHue(float p_286907_, float p_286536_, float p_286318_, float p_286590_) {
      Vec3 vec3 = mixColor(p_286590_).scale((double)p_286907_);
      Vec3 vec31 = mixColor((p_286590_ + 0.33333334F) % 1.0F).scale((double)p_286536_);
      Vec3 vec32 = mixColor((p_286590_ + 0.6666667F) % 1.0F).scale((double)p_286318_);
      Vec3 vec33 = vec3.add(vec31).add(vec32);
      double d0 = Math.max(Math.max(1.0D, vec33.x), Math.max(vec33.y, vec33.z));
      return new Vec3(vec33.x / d0, vec33.y / d0, vec33.z / d0);
   }

   public static void renderVoxelShape(PoseStack p_286791_, VertexConsumer p_286416_, VoxelShape p_286863_, double p_286432_, double p_286836_, double p_286774_, float p_286612_, float p_286516_, float p_286787_, float p_286300_, boolean p_286443_) {
      List<AABB> list = p_286863_.toAabbs();
      if (!list.isEmpty()) {
         int i = p_286443_ ? list.size() : list.size() * 8;
         renderShape(p_286791_, p_286416_, Shapes.create(list.get(0)), p_286432_, p_286836_, p_286774_, p_286612_, p_286516_, p_286787_, p_286300_);

         for(int j = 1; j < list.size(); ++j) {
            AABB aabb = list.get(j);
            float f = (float)j / (float)i;
            Vec3 vec3 = shiftHue(p_286612_, p_286516_, p_286787_, f);
            renderShape(p_286791_, p_286416_, Shapes.create(aabb), p_286432_, p_286836_, p_286774_, (float)vec3.x, (float)vec3.y, (float)vec3.z, p_286300_);
         }

      }
   }

   private static void renderShape(PoseStack p_109783_, VertexConsumer p_109784_, VoxelShape p_109785_, double p_109786_, double p_109787_, double p_109788_, float p_109789_, float p_109790_, float p_109791_, float p_109792_) {
      PoseStack.Pose posestack$pose = p_109783_.last();
      p_109785_.forAllEdges((p_234280_, p_234281_, p_234282_, p_234283_, p_234284_, p_234285_) -> {
         float f = (float)(p_234283_ - p_234280_);
         float f1 = (float)(p_234284_ - p_234281_);
         float f2 = (float)(p_234285_ - p_234282_);
         float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
         f /= f3;
         f1 /= f3;
         f2 /= f3;
         p_109784_.vertex(posestack$pose.pose(), (float)(p_234280_ + p_109786_), (float)(p_234281_ + p_109787_), (float)(p_234282_ + p_109788_)).color(p_109789_, p_109790_, p_109791_, p_109792_).normal(posestack$pose.normal(), f, f1, f2).endVertex();
         p_109784_.vertex(posestack$pose.pose(), (float)(p_234283_ + p_109786_), (float)(p_234284_ + p_109787_), (float)(p_234285_ + p_109788_)).color(p_109789_, p_109790_, p_109791_, p_109792_).normal(posestack$pose.normal(), f, f1, f2).endVertex();
      });
   }

   public static void renderLineBox(VertexConsumer p_172966_, double p_172967_, double p_172968_, double p_172969_, double p_172970_, double p_172971_, double p_172972_, float p_172973_, float p_172974_, float p_172975_, float p_172976_) {
      renderLineBox(new PoseStack(), p_172966_, p_172967_, p_172968_, p_172969_, p_172970_, p_172971_, p_172972_, p_172973_, p_172974_, p_172975_, p_172976_, p_172973_, p_172974_, p_172975_);
   }

   public static void renderLineBox(PoseStack p_109647_, VertexConsumer p_109648_, AABB p_109649_, float p_109650_, float p_109651_, float p_109652_, float p_109653_) {
      renderLineBox(p_109647_, p_109648_, p_109649_.minX, p_109649_.minY, p_109649_.minZ, p_109649_.maxX, p_109649_.maxY, p_109649_.maxZ, p_109650_, p_109651_, p_109652_, p_109653_, p_109650_, p_109651_, p_109652_);
   }

   public static void renderLineBox(PoseStack p_109609_, VertexConsumer p_109610_, double p_109611_, double p_109612_, double p_109613_, double p_109614_, double p_109615_, double p_109616_, float p_109617_, float p_109618_, float p_109619_, float p_109620_) {
      renderLineBox(p_109609_, p_109610_, p_109611_, p_109612_, p_109613_, p_109614_, p_109615_, p_109616_, p_109617_, p_109618_, p_109619_, p_109620_, p_109617_, p_109618_, p_109619_);
   }

   public static void renderLineBox(PoseStack p_109622_, VertexConsumer p_109623_, double p_109624_, double p_109625_, double p_109626_, double p_109627_, double p_109628_, double p_109629_, float p_109630_, float p_109631_, float p_109632_, float p_109633_, float p_109634_, float p_109635_, float p_109636_) {
      Matrix4f matrix4f = p_109622_.last().pose();
      Matrix3f matrix3f = p_109622_.last().normal();
      float f = (float)p_109624_;
      float f1 = (float)p_109625_;
      float f2 = (float)p_109626_;
      float f3 = (float)p_109627_;
      float f4 = (float)p_109628_;
      float f5 = (float)p_109629_;
      p_109623_.vertex(matrix4f, f, f1, f2).color(p_109630_, p_109635_, p_109636_, p_109633_).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
      p_109623_.vertex(matrix4f, f3, f1, f2).color(p_109630_, p_109635_, p_109636_, p_109633_).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
      p_109623_.vertex(matrix4f, f, f1, f2).color(p_109634_, p_109631_, p_109636_, p_109633_).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
      p_109623_.vertex(matrix4f, f, f4, f2).color(p_109634_, p_109631_, p_109636_, p_109633_).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
      p_109623_.vertex(matrix4f, f, f1, f2).color(p_109634_, p_109635_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
      p_109623_.vertex(matrix4f, f, f1, f5).color(p_109634_, p_109635_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
      p_109623_.vertex(matrix4f, f3, f1, f2).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
      p_109623_.vertex(matrix4f, f3, f4, f2).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
      p_109623_.vertex(matrix4f, f3, f4, f2).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
      p_109623_.vertex(matrix4f, f, f4, f2).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
      p_109623_.vertex(matrix4f, f, f4, f2).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
      p_109623_.vertex(matrix4f, f, f4, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
      p_109623_.vertex(matrix4f, f, f4, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
      p_109623_.vertex(matrix4f, f, f1, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
      p_109623_.vertex(matrix4f, f, f1, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
      p_109623_.vertex(matrix4f, f3, f1, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
      p_109623_.vertex(matrix4f, f3, f1, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
      p_109623_.vertex(matrix4f, f3, f1, f2).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
      p_109623_.vertex(matrix4f, f, f4, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
      p_109623_.vertex(matrix4f, f3, f4, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
      p_109623_.vertex(matrix4f, f3, f1, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
      p_109623_.vertex(matrix4f, f3, f4, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
      p_109623_.vertex(matrix4f, f3, f4, f2).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
      p_109623_.vertex(matrix4f, f3, f4, f5).color(p_109630_, p_109631_, p_109632_, p_109633_).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
   }

   public static void addChainedFilledBoxVertices(PoseStack p_270343_, VertexConsumer p_270926_, double p_270503_, double p_270353_, double p_270642_, double p_270676_, double p_270711_, double p_270601_, float p_270523_, float p_270382_, float p_270089_, float p_270335_) {
      addChainedFilledBoxVertices(p_270343_, p_270926_, (float)p_270503_, (float)p_270353_, (float)p_270642_, (float)p_270676_, (float)p_270711_, (float)p_270601_, p_270523_, p_270382_, p_270089_, p_270335_);
   }

   public static void addChainedFilledBoxVertices(PoseStack p_270352_, VertexConsumer p_271015_, float p_270144_, float p_270901_, float p_270546_, float p_270102_, float p_270605_, float p_271006_, float p_270864_, float p_270181_, float p_270220_, float p_270293_) {
      Matrix4f matrix4f = p_270352_.last().pose();
      p_271015_.vertex(matrix4f, p_270144_, p_270901_, p_270546_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270144_, p_270901_, p_270546_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270144_, p_270901_, p_270546_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270144_, p_270901_, p_271006_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270144_, p_270605_, p_270546_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270144_, p_270605_, p_271006_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270144_, p_270605_, p_271006_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270144_, p_270901_, p_271006_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270102_, p_270605_, p_271006_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270102_, p_270901_, p_271006_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270102_, p_270901_, p_271006_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270102_, p_270901_, p_270546_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270102_, p_270605_, p_271006_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270102_, p_270605_, p_270546_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270102_, p_270605_, p_270546_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270102_, p_270901_, p_270546_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270144_, p_270605_, p_270546_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270144_, p_270901_, p_270546_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270144_, p_270901_, p_270546_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270102_, p_270901_, p_270546_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270144_, p_270901_, p_271006_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270102_, p_270901_, p_271006_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270102_, p_270901_, p_271006_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270144_, p_270605_, p_270546_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270144_, p_270605_, p_270546_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270144_, p_270605_, p_271006_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270102_, p_270605_, p_270546_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270102_, p_270605_, p_271006_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270102_, p_270605_, p_271006_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
      p_271015_.vertex(matrix4f, p_270102_, p_270605_, p_271006_).color(p_270864_, p_270181_, p_270220_, p_270293_).endVertex();
   }

   public void blockChanged(BlockGetter p_109545_, BlockPos p_109546_, BlockState p_109547_, BlockState p_109548_, int p_109549_) {
      this.setBlockDirty(p_109546_, (p_109549_ & 8) != 0);
   }

   private void setBlockDirty(BlockPos p_109733_, boolean p_109734_) {
      for(int i = p_109733_.getZ() - 1; i <= p_109733_.getZ() + 1; ++i) {
         for(int j = p_109733_.getX() - 1; j <= p_109733_.getX() + 1; ++j) {
            for(int k = p_109733_.getY() - 1; k <= p_109733_.getY() + 1; ++k) {
               this.setSectionDirty(SectionPos.blockToSectionCoord(j), SectionPos.blockToSectionCoord(k), SectionPos.blockToSectionCoord(i), p_109734_);
            }
         }
      }

   }

   public void setBlocksDirty(int p_109495_, int p_109496_, int p_109497_, int p_109498_, int p_109499_, int p_109500_) {
      for(int i = p_109497_ - 1; i <= p_109500_ + 1; ++i) {
         for(int j = p_109495_ - 1; j <= p_109498_ + 1; ++j) {
            for(int k = p_109496_ - 1; k <= p_109499_ + 1; ++k) {
               this.setSectionDirty(SectionPos.blockToSectionCoord(j), SectionPos.blockToSectionCoord(k), SectionPos.blockToSectionCoord(i));
            }
         }
      }

   }

   public void setBlockDirty(BlockPos p_109722_, BlockState p_109723_, BlockState p_109724_) {
      if (this.minecraft.getModelManager().requiresRender(p_109723_, p_109724_)) {
         this.setBlocksDirty(p_109722_.getX(), p_109722_.getY(), p_109722_.getZ(), p_109722_.getX(), p_109722_.getY(), p_109722_.getZ());
      }

   }

   public void setSectionDirtyWithNeighbors(int p_109491_, int p_109492_, int p_109493_) {
      for(int i = p_109493_ - 1; i <= p_109493_ + 1; ++i) {
         for(int j = p_109491_ - 1; j <= p_109491_ + 1; ++j) {
            for(int k = p_109492_ - 1; k <= p_109492_ + 1; ++k) {
               this.setSectionDirty(j, k, i);
            }
         }
      }

   }

   public void setSectionDirty(int p_109771_, int p_109772_, int p_109773_) {
      this.setSectionDirty(p_109771_, p_109772_, p_109773_, false);
   }

   private void setSectionDirty(int p_109502_, int p_109503_, int p_109504_, boolean p_109505_) {
      this.viewArea.setDirty(p_109502_, p_109503_, p_109504_, p_109505_);
   }

   public Frustum getFrustum() {
      return this.capturedFrustum != null ? this.capturedFrustum : this.cullingFrustum;
   }

   public int getTicks() {
      return this.ticks;
   }

   /**
    * @deprecated Forge: Use item aware function below
    */
   @Deprecated
   public void playStreamingMusic(@Nullable SoundEvent p_109515_, BlockPos p_109516_) {
      this.playStreamingMusic(p_109515_, p_109516_, p_109515_ == null? null : RecordItem.getBySound(p_109515_));
   }

   public void playStreamingMusic(@Nullable SoundEvent p_109515_, BlockPos p_109516_, @Nullable RecordItem musicDiscItem) {
      SoundInstance soundinstance = this.playingRecords.get(p_109516_);
      if (soundinstance != null) {
         this.minecraft.getSoundManager().stop(soundinstance);
         this.playingRecords.remove(p_109516_);
      }

      if (p_109515_ != null) {
         RecordItem recorditem = musicDiscItem;
         if (recorditem != null) {
            this.minecraft.gui.setNowPlaying(recorditem.getDisplayName());
         }

         SoundInstance simplesoundinstance = SimpleSoundInstance.forRecord(p_109515_, Vec3.atCenterOf(p_109516_));
         this.playingRecords.put(p_109516_, simplesoundinstance);
         this.minecraft.getSoundManager().play(simplesoundinstance);
      }

      this.notifyNearbyEntities(this.level, p_109516_, p_109515_ != null);
   }

   private void notifyNearbyEntities(Level p_109551_, BlockPos p_109552_, boolean p_109553_) {
      for(LivingEntity livingentity : p_109551_.getEntitiesOfClass(LivingEntity.class, (new AABB(p_109552_)).inflate(3.0D))) {
         livingentity.setRecordPlayingNearby(p_109552_, p_109553_);
      }

   }

   public void addParticle(ParticleOptions p_109744_, boolean p_109745_, double p_109746_, double p_109747_, double p_109748_, double p_109749_, double p_109750_, double p_109751_) {
      this.addParticle(p_109744_, p_109745_, false, p_109746_, p_109747_, p_109748_, p_109749_, p_109750_, p_109751_);
   }

   public void addParticle(ParticleOptions p_109753_, boolean p_109754_, boolean p_109755_, double p_109756_, double p_109757_, double p_109758_, double p_109759_, double p_109760_, double p_109761_) {
      try {
         this.addParticleInternal(p_109753_, p_109754_, p_109755_, p_109756_, p_109757_, p_109758_, p_109759_, p_109760_, p_109761_);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception while adding particle");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Particle being added");
         crashreportcategory.setDetail("ID", BuiltInRegistries.PARTICLE_TYPE.getKey(p_109753_.getType()));
         crashreportcategory.setDetail("Parameters", p_109753_.writeToString());
         crashreportcategory.setDetail("Position", () -> {
            return CrashReportCategory.formatLocation(this.level, p_109756_, p_109757_, p_109758_);
         });
         throw new ReportedException(crashreport);
      }
   }

   private <T extends ParticleOptions> void addParticle(T p_109736_, double p_109737_, double p_109738_, double p_109739_, double p_109740_, double p_109741_, double p_109742_) {
      this.addParticle(p_109736_, p_109736_.getType().getOverrideLimiter(), p_109737_, p_109738_, p_109739_, p_109740_, p_109741_, p_109742_);
   }

   @Nullable
   private Particle addParticleInternal(ParticleOptions p_109796_, boolean p_109797_, double p_109798_, double p_109799_, double p_109800_, double p_109801_, double p_109802_, double p_109803_) {
      return this.addParticleInternal(p_109796_, p_109797_, false, p_109798_, p_109799_, p_109800_, p_109801_, p_109802_, p_109803_);
   }

   @Nullable
   private Particle addParticleInternal(ParticleOptions p_109805_, boolean p_109806_, boolean p_109807_, double p_109808_, double p_109809_, double p_109810_, double p_109811_, double p_109812_, double p_109813_) {
      Camera camera = this.minecraft.gameRenderer.getMainCamera();
      ParticleStatus particlestatus = this.calculateParticleLevel(p_109807_);
      if (p_109806_) {
         return this.minecraft.particleEngine.createParticle(p_109805_, p_109808_, p_109809_, p_109810_, p_109811_, p_109812_, p_109813_);
      } else if (camera.getPosition().distanceToSqr(p_109808_, p_109809_, p_109810_) > 1024.0D) {
         return null;
      } else {
         return particlestatus == ParticleStatus.MINIMAL ? null : this.minecraft.particleEngine.createParticle(p_109805_, p_109808_, p_109809_, p_109810_, p_109811_, p_109812_, p_109813_);
      }
   }

   private ParticleStatus calculateParticleLevel(boolean p_109768_) {
      ParticleStatus particlestatus = this.minecraft.options.particles().get();
      if (p_109768_ && particlestatus == ParticleStatus.MINIMAL && this.level.random.nextInt(10) == 0) {
         particlestatus = ParticleStatus.DECREASED;
      }

      if (particlestatus == ParticleStatus.DECREASED && this.level.random.nextInt(3) == 0) {
         particlestatus = ParticleStatus.MINIMAL;
      }

      return particlestatus;
   }

   public void clear() {
   }

   public void globalLevelEvent(int p_109507_, BlockPos p_109508_, int p_109509_) {
      switch (p_109507_) {
         case 1023:
         case 1028:
         case 1038:
            Camera camera = this.minecraft.gameRenderer.getMainCamera();
            if (camera.isInitialized()) {
               double d0 = (double)p_109508_.getX() - camera.getPosition().x;
               double d1 = (double)p_109508_.getY() - camera.getPosition().y;
               double d2 = (double)p_109508_.getZ() - camera.getPosition().z;
               double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
               double d4 = camera.getPosition().x;
               double d5 = camera.getPosition().y;
               double d6 = camera.getPosition().z;
               if (d3 > 0.0D) {
                  d4 += d0 / d3 * 2.0D;
                  d5 += d1 / d3 * 2.0D;
                  d6 += d2 / d3 * 2.0D;
               }

               if (p_109507_ == 1023) {
                  this.level.playLocalSound(d4, d5, d6, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0F, 1.0F, false);
               } else if (p_109507_ == 1038) {
                  this.level.playLocalSound(d4, d5, d6, SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, 1.0F, 1.0F, false);
               } else {
                  this.level.playLocalSound(d4, d5, d6, SoundEvents.ENDER_DRAGON_DEATH, SoundSource.HOSTILE, 5.0F, 1.0F, false);
               }
            }
         default:
      }
   }

   public void levelEvent(int p_234305_, BlockPos p_234306_, int p_234307_) {
      RandomSource randomsource = this.level.random;
      switch (p_234305_) {
         case 1000:
            this.level.playLocalSound(p_234306_, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1001:
            this.level.playLocalSound(p_234306_, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0F, 1.2F, false);
            break;
         case 1002:
            this.level.playLocalSound(p_234306_, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 1.0F, 1.2F, false);
            break;
         case 1003:
            this.level.playLocalSound(p_234306_, SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 1.0F, 1.2F, false);
            break;
         case 1004:
            this.level.playLocalSound(p_234306_, SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.2F, false);
            break;
         case 1009:
            if (p_234307_ == 0) {
               this.level.playLocalSound(p_234306_, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (randomsource.nextFloat() - randomsource.nextFloat()) * 0.8F, false);
            } else if (p_234307_ == 1) {
               this.level.playLocalSound(p_234306_, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.7F, 1.6F + (randomsource.nextFloat() - randomsource.nextFloat()) * 0.4F, false);
            }
            break;
         case 1010:
            Item item = Item.byId(p_234307_);
            if (item instanceof RecordItem recorditem) {
               this.playStreamingMusic(recorditem.getSound(), p_234306_, recorditem);
            }
            break;
         case 1011:
            this.playStreamingMusic((SoundEvent)null, p_234306_);
            break;
         case 1015:
            this.level.playLocalSound(p_234306_, SoundEvents.GHAST_WARN, SoundSource.HOSTILE, 10.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1016:
            this.level.playLocalSound(p_234306_, SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 10.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1017:
            this.level.playLocalSound(p_234306_, SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 10.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1018:
            this.level.playLocalSound(p_234306_, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 2.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1019:
            this.level.playLocalSound(p_234306_, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1020:
            this.level.playLocalSound(p_234306_, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.HOSTILE, 2.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1021:
            this.level.playLocalSound(p_234306_, SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1022:
            this.level.playLocalSound(p_234306_, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 2.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1024:
            this.level.playLocalSound(p_234306_, SoundEvents.WITHER_SHOOT, SoundSource.HOSTILE, 2.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1025:
            this.level.playLocalSound(p_234306_, SoundEvents.BAT_TAKEOFF, SoundSource.NEUTRAL, 0.05F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1026:
            this.level.playLocalSound(p_234306_, SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 2.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1027:
            this.level.playLocalSound(p_234306_, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.HOSTILE, 2.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1029:
            this.level.playLocalSound(p_234306_, SoundEvents.ANVIL_DESTROY, SoundSource.BLOCKS, 1.0F, randomsource.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1030:
            this.level.playLocalSound(p_234306_, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, randomsource.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1031:
            this.level.playLocalSound(p_234306_, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.3F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1032:
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.PORTAL_TRAVEL, randomsource.nextFloat() * 0.4F + 0.8F, 0.25F));
            break;
         case 1033:
            this.level.playLocalSound(p_234306_, SoundEvents.CHORUS_FLOWER_GROW, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1034:
            this.level.playLocalSound(p_234306_, SoundEvents.CHORUS_FLOWER_DEATH, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1035:
            this.level.playLocalSound(p_234306_, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1039:
            this.level.playLocalSound(p_234306_, SoundEvents.PHANTOM_BITE, SoundSource.HOSTILE, 0.3F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1040:
            this.level.playLocalSound(p_234306_, SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, SoundSource.HOSTILE, 2.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1041:
            this.level.playLocalSound(p_234306_, SoundEvents.HUSK_CONVERTED_TO_ZOMBIE, SoundSource.HOSTILE, 2.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1042:
            this.level.playLocalSound(p_234306_, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1043:
            this.level.playLocalSound(p_234306_, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1044:
            this.level.playLocalSound(p_234306_, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1045:
            this.level.playLocalSound(p_234306_, SoundEvents.POINTED_DRIPSTONE_LAND, SoundSource.BLOCKS, 2.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1046:
            this.level.playLocalSound(p_234306_, SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA_INTO_CAULDRON, SoundSource.BLOCKS, 2.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1047:
            this.level.playLocalSound(p_234306_, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, SoundSource.BLOCKS, 2.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1048:
            this.level.playLocalSound(p_234306_, SoundEvents.SKELETON_CONVERTED_TO_STRAY, SoundSource.HOSTILE, 2.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1500:
            ComposterBlock.handleFill(this.level, p_234306_, p_234307_ > 0);
            break;
         case 1501:
            this.level.playLocalSound(p_234306_, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (randomsource.nextFloat() - randomsource.nextFloat()) * 0.8F, false);

            for(int j3 = 0; j3 < 8; ++j3) {
               this.level.addParticle(ParticleTypes.LARGE_SMOKE, (double)p_234306_.getX() + randomsource.nextDouble(), (double)p_234306_.getY() + 1.2D, (double)p_234306_.getZ() + randomsource.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
            break;
         case 1502:
            this.level.playLocalSound(p_234306_, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5F, 2.6F + (randomsource.nextFloat() - randomsource.nextFloat()) * 0.8F, false);

            for(int i3 = 0; i3 < 5; ++i3) {
               double d16 = (double)p_234306_.getX() + randomsource.nextDouble() * 0.6D + 0.2D;
               double d22 = (double)p_234306_.getY() + randomsource.nextDouble() * 0.6D + 0.2D;
               double d27 = (double)p_234306_.getZ() + randomsource.nextDouble() * 0.6D + 0.2D;
               this.level.addParticle(ParticleTypes.SMOKE, d16, d22, d27, 0.0D, 0.0D, 0.0D);
            }
            break;
         case 1503:
            this.level.playLocalSound(p_234306_, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);

            for(int l2 = 0; l2 < 16; ++l2) {
               double d15 = (double)p_234306_.getX() + (5.0D + randomsource.nextDouble() * 6.0D) / 16.0D;
               double d21 = (double)p_234306_.getY() + 0.8125D;
               double d26 = (double)p_234306_.getZ() + (5.0D + randomsource.nextDouble() * 6.0D) / 16.0D;
               this.level.addParticle(ParticleTypes.SMOKE, d15, d21, d26, 0.0D, 0.0D, 0.0D);
            }
            break;
         case 1504:
            PointedDripstoneBlock.spawnDripParticle(this.level, p_234306_, this.level.getBlockState(p_234306_));
            break;
         case 1505:
            BoneMealItem.addGrowthParticles(this.level, p_234306_, p_234307_);
            this.level.playLocalSound(p_234306_, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 2000:
            Direction direction1 = Direction.from3DDataValue(p_234307_);
            int j = direction1.getStepX();
            int i1 = direction1.getStepY();
            int k1 = direction1.getStepZ();
            double d10 = (double)p_234306_.getX() + (double)j * 0.6D + 0.5D;
            double d14 = (double)p_234306_.getY() + (double)i1 * 0.6D + 0.5D;
            double d20 = (double)p_234306_.getZ() + (double)k1 * 0.6D + 0.5D;

            for(int i4 = 0; i4 < 10; ++i4) {
               double d28 = randomsource.nextDouble() * 0.2D + 0.01D;
               double d2 = d10 + (double)j * 0.01D + (randomsource.nextDouble() - 0.5D) * (double)k1 * 0.5D;
               double d3 = d14 + (double)i1 * 0.01D + (randomsource.nextDouble() - 0.5D) * (double)i1 * 0.5D;
               double d32 = d20 + (double)k1 * 0.01D + (randomsource.nextDouble() - 0.5D) * (double)j * 0.5D;
               double d4 = (double)j * d28 + randomsource.nextGaussian() * 0.01D;
               double d5 = (double)i1 * d28 + randomsource.nextGaussian() * 0.01D;
               double d6 = (double)k1 * d28 + randomsource.nextGaussian() * 0.01D;
               this.addParticle(ParticleTypes.SMOKE, d2, d3, d32, d4, d5, d6);
            }
            break;
         case 2001:
            BlockState blockstate1 = Block.stateById(p_234307_);
            if (!blockstate1.isAir()) {
               SoundType soundtype = blockstate1.getSoundType(this.level, p_234306_, null);
               this.level.playLocalSound(p_234306_, soundtype.getBreakSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F, false);
            }

            this.level.addDestroyBlockEffect(p_234306_, blockstate1);
            break;
         case 2002:
         case 2007:
            Vec3 vec3 = Vec3.atBottomCenterOf(p_234306_);

            for(int i = 0; i < 8; ++i) {
               this.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), vec3.x, vec3.y, vec3.z, randomsource.nextGaussian() * 0.15D, randomsource.nextDouble() * 0.2D, randomsource.nextGaussian() * 0.15D);
            }

            float f3 = (float)(p_234307_ >> 16 & 255) / 255.0F;
            float f4 = (float)(p_234307_ >> 8 & 255) / 255.0F;
            float f6 = (float)(p_234307_ >> 0 & 255) / 255.0F;
            ParticleOptions particleoptions = p_234305_ == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;

            for(int k2 = 0; k2 < 100; ++k2) {
               double d13 = randomsource.nextDouble() * 4.0D;
               double d19 = randomsource.nextDouble() * Math.PI * 2.0D;
               double d25 = Math.cos(d19) * d13;
               double d30 = 0.01D + randomsource.nextDouble() * 0.5D;
               double d31 = Math.sin(d19) * d13;
               Particle particle1 = this.addParticleInternal(particleoptions, particleoptions.getType().getOverrideLimiter(), vec3.x + d25 * 0.1D, vec3.y + 0.3D, vec3.z + d31 * 0.1D, d25, d30, d31);
               if (particle1 != null) {
                  float f2 = 0.75F + randomsource.nextFloat() * 0.25F;
                  particle1.setColor(f3 * f2, f4 * f2, f6 * f2);
                  particle1.setPower((float)d13);
               }
            }

            this.level.playLocalSound(p_234306_, SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0F, randomsource.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 2003:
            double d0 = (double)p_234306_.getX() + 0.5D;
            double d7 = (double)p_234306_.getY();
            double d9 = (double)p_234306_.getZ() + 0.5D;

            for(int k3 = 0; k3 < 8; ++k3) {
               this.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)), d0, d7, d9, randomsource.nextGaussian() * 0.15D, randomsource.nextDouble() * 0.2D, randomsource.nextGaussian() * 0.15D);
            }

            for(double d12 = 0.0D; d12 < (Math.PI * 2D); d12 += 0.15707963267948966D) {
               this.addParticle(ParticleTypes.PORTAL, d0 + Math.cos(d12) * 5.0D, d7 - 0.4D, d9 + Math.sin(d12) * 5.0D, Math.cos(d12) * -5.0D, 0.0D, Math.sin(d12) * -5.0D);
               this.addParticle(ParticleTypes.PORTAL, d0 + Math.cos(d12) * 5.0D, d7 - 0.4D, d9 + Math.sin(d12) * 5.0D, Math.cos(d12) * -7.0D, 0.0D, Math.sin(d12) * -7.0D);
            }
            break;
         case 2004:
            for(int l = 0; l < 20; ++l) {
               double d8 = (double)p_234306_.getX() + 0.5D + (randomsource.nextDouble() - 0.5D) * 2.0D;
               double d11 = (double)p_234306_.getY() + 0.5D + (randomsource.nextDouble() - 0.5D) * 2.0D;
               double d17 = (double)p_234306_.getZ() + 0.5D + (randomsource.nextDouble() - 0.5D) * 2.0D;
               this.level.addParticle(ParticleTypes.SMOKE, d8, d11, d17, 0.0D, 0.0D, 0.0D);
               this.level.addParticle(ParticleTypes.FLAME, d8, d11, d17, 0.0D, 0.0D, 0.0D);
            }
            break;
         case 2005:
            BoneMealItem.addGrowthParticles(this.level, p_234306_, p_234307_);
            break;
         case 2006:
            for(int j2 = 0; j2 < 200; ++j2) {
               float f10 = randomsource.nextFloat() * 4.0F;
               float f11 = randomsource.nextFloat() * ((float)Math.PI * 2F);
               double d18 = (double)(Mth.cos(f11) * f10);
               double d24 = 0.01D + randomsource.nextDouble() * 0.5D;
               double d29 = (double)(Mth.sin(f11) * f10);
               Particle particle = this.addParticleInternal(ParticleTypes.DRAGON_BREATH, false, (double)p_234306_.getX() + d18 * 0.1D, (double)p_234306_.getY() + 0.3D, (double)p_234306_.getZ() + d29 * 0.1D, d18, d24, d29);
               if (particle != null) {
                  particle.setPower(f10);
               }
            }

            if (p_234307_ == 1) {
               this.level.playLocalSound(p_234306_, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 1.0F, randomsource.nextFloat() * 0.1F + 0.9F, false);
            }
            break;
         case 2008:
            this.level.addParticle(ParticleTypes.EXPLOSION, (double)p_234306_.getX() + 0.5D, (double)p_234306_.getY() + 0.5D, (double)p_234306_.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
            break;
         case 2009:
            for(int i2 = 0; i2 < 8; ++i2) {
               this.level.addParticle(ParticleTypes.CLOUD, (double)p_234306_.getX() + randomsource.nextDouble(), (double)p_234306_.getY() + 1.2D, (double)p_234306_.getZ() + randomsource.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
            break;
         case 3000:
            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, (double)p_234306_.getX() + 0.5D, (double)p_234306_.getY() + 0.5D, (double)p_234306_.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
            this.level.playLocalSound(p_234306_, SoundEvents.END_GATEWAY_SPAWN, SoundSource.BLOCKS, 10.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
            break;
         case 3001:
            this.level.playLocalSound(p_234306_, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 64.0F, 0.8F + this.level.random.nextFloat() * 0.3F, false);
            break;
         case 3002:
            if (p_234307_ >= 0 && p_234307_ < Direction.Axis.VALUES.length) {
               ParticleUtils.spawnParticlesAlongAxis(Direction.Axis.VALUES[p_234307_], this.level, p_234306_, 0.125D, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(10, 19));
            } else {
               ParticleUtils.spawnParticlesOnBlockFaces(this.level, p_234306_, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(3, 5));
            }
            break;
         case 3003:
            ParticleUtils.spawnParticlesOnBlockFaces(this.level, p_234306_, ParticleTypes.WAX_ON, UniformInt.of(3, 5));
            this.level.playLocalSound(p_234306_, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 3004:
            ParticleUtils.spawnParticlesOnBlockFaces(this.level, p_234306_, ParticleTypes.WAX_OFF, UniformInt.of(3, 5));
            break;
         case 3005:
            ParticleUtils.spawnParticlesOnBlockFaces(this.level, p_234306_, ParticleTypes.SCRAPE, UniformInt.of(3, 5));
            break;
         case 3006:
            int k = p_234307_ >> 6;
            if (k > 0) {
               if (randomsource.nextFloat() < 0.3F + (float)k * 0.1F) {
                  float f5 = 0.15F + 0.02F * (float)k * (float)k * randomsource.nextFloat();
                  float f7 = 0.4F + 0.3F * (float)k * randomsource.nextFloat();
                  this.level.playLocalSound(p_234306_, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, f5, f7, false);
               }

               byte b0 = (byte)(p_234307_ & 63);
               IntProvider intprovider = UniformInt.of(0, k);
               float f = 0.005F;
               Supplier<Vec3> supplier = () -> {
                  return new Vec3(Mth.nextDouble(randomsource, (double)-0.005F, (double)0.005F), Mth.nextDouble(randomsource, (double)-0.005F, (double)0.005F), Mth.nextDouble(randomsource, (double)-0.005F, (double)0.005F));
               };
               if (b0 == 0) {
                  for(Direction direction : Direction.values()) {
                     float f1 = direction == Direction.DOWN ? (float)Math.PI : 0.0F;
                     double d1 = direction.getAxis() == Direction.Axis.Y ? 0.65D : 0.57D;
                     ParticleUtils.spawnParticlesOnBlockFace(this.level, p_234306_, new SculkChargeParticleOptions(f1), intprovider, direction, supplier, d1);
                  }
               } else {
                  for(Direction direction2 : MultifaceBlock.unpack(b0)) {
                     float f13 = direction2 == Direction.UP ? (float)Math.PI : 0.0F;
                     double d23 = 0.35D;
                     ParticleUtils.spawnParticlesOnBlockFace(this.level, p_234306_, new SculkChargeParticleOptions(f13), intprovider, direction2, supplier, 0.35D);
                  }
               }
            } else {
               this.level.playLocalSound(p_234306_, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
               boolean flag1 = this.level.getBlockState(p_234306_).isCollisionShapeFullBlock(this.level, p_234306_);
               int l1 = flag1 ? 40 : 20;
               float f8 = flag1 ? 0.45F : 0.25F;
               float f9 = 0.07F;

               for(int l3 = 0; l3 < l1; ++l3) {
                  float f12 = 2.0F * randomsource.nextFloat() - 1.0F;
                  float f14 = 2.0F * randomsource.nextFloat() - 1.0F;
                  float f15 = 2.0F * randomsource.nextFloat() - 1.0F;
                  this.level.addParticle(ParticleTypes.SCULK_CHARGE_POP, (double)p_234306_.getX() + 0.5D + (double)(f12 * f8), (double)p_234306_.getY() + 0.5D + (double)(f14 * f8), (double)p_234306_.getZ() + 0.5D + (double)(f15 * f8), (double)(f12 * 0.07F), (double)(f14 * 0.07F), (double)(f15 * 0.07F));
               }
            }
            break;
         case 3007:
            for(int j1 = 0; j1 < 10; ++j1) {
               this.level.addParticle(new ShriekParticleOption(j1 * 5), false, (double)p_234306_.getX() + 0.5D, (double)p_234306_.getY() + SculkShriekerBlock.TOP_Y, (double)p_234306_.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
            }

            BlockState blockstate2 = this.level.getBlockState(p_234306_);
            boolean flag = blockstate2.hasProperty(BlockStateProperties.WATERLOGGED) && blockstate2.getValue(BlockStateProperties.WATERLOGGED);
            if (!flag) {
               this.level.playLocalSound((double)p_234306_.getX() + 0.5D, (double)p_234306_.getY() + SculkShriekerBlock.TOP_Y, (double)p_234306_.getZ() + 0.5D, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.BLOCKS, 2.0F, 0.6F + this.level.random.nextFloat() * 0.4F, false);
            }
            break;
         case 3008:
            BlockState blockstate = Block.stateById(p_234307_);
            Block $$68 = blockstate.getBlock();
            if ($$68 instanceof BrushableBlock brushableblock) {
               this.level.playLocalSound(p_234306_, brushableblock.getBrushCompletedSound(), SoundSource.PLAYERS, 1.0F, 1.0F, false);
            }

            this.level.addDestroyBlockEffect(p_234306_, blockstate);
            break;
         case 3009:
            ParticleUtils.spawnParticlesOnBlockFaces(this.level, p_234306_, ParticleTypes.EGG_CRACK, UniformInt.of(3, 6));
      }

   }

   public void destroyBlockProgress(int p_109775_, BlockPos p_109776_, int p_109777_) {
      if (p_109777_ >= 0 && p_109777_ < 10) {
         BlockDestructionProgress blockdestructionprogress1 = this.destroyingBlocks.get(p_109775_);
         if (blockdestructionprogress1 != null) {
            this.removeProgress(blockdestructionprogress1);
         }

         if (blockdestructionprogress1 == null || blockdestructionprogress1.getPos().getX() != p_109776_.getX() || blockdestructionprogress1.getPos().getY() != p_109776_.getY() || blockdestructionprogress1.getPos().getZ() != p_109776_.getZ()) {
            blockdestructionprogress1 = new BlockDestructionProgress(p_109775_, p_109776_);
            this.destroyingBlocks.put(p_109775_, blockdestructionprogress1);
         }

         blockdestructionprogress1.setProgress(p_109777_);
         blockdestructionprogress1.updateTick(this.ticks);
         this.destructionProgress.computeIfAbsent(blockdestructionprogress1.getPos().asLong(), (p_234254_) -> {
            return Sets.newTreeSet();
         }).add(blockdestructionprogress1);
      } else {
         BlockDestructionProgress blockdestructionprogress = this.destroyingBlocks.remove(p_109775_);
         if (blockdestructionprogress != null) {
            this.removeProgress(blockdestructionprogress);
         }
      }

   }

   public boolean hasRenderedAllChunks() {
      return this.chunkRenderDispatcher.isQueueEmpty();
   }

   public void needsUpdate() {
      this.needsFullRenderChunkUpdate = true;
      this.generateClouds = true;
   }

   public void updateGlobalBlockEntities(Collection<BlockEntity> p_109763_, Collection<BlockEntity> p_109764_) {
      synchronized(this.globalBlockEntities) {
         this.globalBlockEntities.removeAll(p_109763_);
         this.globalBlockEntities.addAll(p_109764_);
      }
   }

   public static int getLightColor(BlockAndTintGetter p_109542_, BlockPos p_109543_) {
      return getLightColor(p_109542_, p_109542_.getBlockState(p_109543_), p_109543_);
   }

   public static int getLightColor(BlockAndTintGetter p_109538_, BlockState p_109539_, BlockPos p_109540_) {
      if (p_109539_.emissiveRendering(p_109538_, p_109540_)) {
         return 15728880;
      } else {
         int i = p_109538_.getBrightness(LightLayer.SKY, p_109540_);
         int j = p_109538_.getBrightness(LightLayer.BLOCK, p_109540_);
         int k = p_109539_.getLightEmission(p_109538_, p_109540_);
         if (j < k) {
            j = k;
         }

         return i << 20 | j << 4;
      }
   }

   public boolean isChunkCompiled(BlockPos p_202431_) {
      ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk = this.viewArea.getRenderChunkAt(p_202431_);
      return chunkrenderdispatcher$renderchunk != null && chunkrenderdispatcher$renderchunk.compiled.get() != ChunkRenderDispatcher.CompiledChunk.UNCOMPILED;
   }

   @Nullable
   public RenderTarget entityTarget() {
      return this.entityTarget;
   }

   @Nullable
   public RenderTarget getTranslucentTarget() {
      return this.translucentTarget;
   }

   @Nullable
   public RenderTarget getItemEntityTarget() {
      return this.itemEntityTarget;
   }

   @Nullable
   public RenderTarget getParticlesTarget() {
      return this.particlesTarget;
   }

   @Nullable
   public RenderTarget getWeatherTarget() {
      return this.weatherTarget;
   }

   @Nullable
   public RenderTarget getCloudsTarget() {
      return this.cloudsTarget;
   }

   @OnlyIn(Dist.CLIENT)
   static class RenderChunkInfo {
      final ChunkRenderDispatcher.RenderChunk chunk;
      private byte sourceDirections;
      byte directions;
      final int step;

      RenderChunkInfo(ChunkRenderDispatcher.RenderChunk p_173022_, @Nullable Direction p_173023_, int p_173024_) {
         this.chunk = p_173022_;
         if (p_173023_ != null) {
            this.addSourceDirection(p_173023_);
         }

         this.step = p_173024_;
      }

      public void setDirections(byte p_109855_, Direction p_109856_) {
         this.directions = (byte)(this.directions | p_109855_ | 1 << p_109856_.ordinal());
      }

      public boolean hasDirection(Direction p_109860_) {
         return (this.directions & 1 << p_109860_.ordinal()) > 0;
      }

      public void addSourceDirection(Direction p_173029_) {
         this.sourceDirections = (byte)(this.sourceDirections | this.sourceDirections | 1 << p_173029_.ordinal());
      }

      public boolean hasSourceDirection(int p_173027_) {
         return (this.sourceDirections & 1 << p_173027_) > 0;
      }

      public boolean hasSourceDirections() {
         return this.sourceDirections != 0;
      }

      public boolean isAxisAlignedWith(int p_275489_, int p_275424_, int p_275596_) {
         BlockPos blockpos = this.chunk.getOrigin();
         return p_275489_ == blockpos.getX() / 16 || p_275596_ == blockpos.getZ() / 16 || p_275424_ == blockpos.getY() / 16;
      }

      public int hashCode() {
         return this.chunk.getOrigin().hashCode();
      }

      public boolean equals(Object p_194373_) {
         if (!(p_194373_ instanceof LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo)) {
            return false;
         } else {
            return this.chunk.getOrigin().equals(levelrenderer$renderchunkinfo.chunk.getOrigin());
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class RenderChunkStorage {
      public final LevelRenderer.RenderInfoMap renderInfoMap;
      public final LinkedHashSet<LevelRenderer.RenderChunkInfo> renderChunks;

      public RenderChunkStorage(int p_194378_) {
         this.renderInfoMap = new LevelRenderer.RenderInfoMap(p_194378_);
         this.renderChunks = new LinkedHashSet<>(p_194378_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class RenderInfoMap {
      private final LevelRenderer.RenderChunkInfo[] infos;

      RenderInfoMap(int p_173033_) {
         this.infos = new LevelRenderer.RenderChunkInfo[p_173033_];
      }

      public void put(ChunkRenderDispatcher.RenderChunk p_173038_, LevelRenderer.RenderChunkInfo p_173039_) {
         this.infos[p_173038_.index] = p_173039_;
      }

      @Nullable
      public LevelRenderer.RenderChunkInfo get(ChunkRenderDispatcher.RenderChunk p_173036_) {
         int i = p_173036_.index;
         return i >= 0 && i < this.infos.length ? this.infos[i] : null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class TransparencyShaderException extends RuntimeException {
      public TransparencyShaderException(String p_109868_, Throwable p_109869_) {
         super(p_109868_, p_109869_);
      }
   }
}
