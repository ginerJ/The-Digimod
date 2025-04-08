package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.Mth;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.portal.PortalForcer;
import net.minecraft.world.level.saveddata.maps.MapIndex;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.LevelTicks;
import org.slf4j.Logger;

public class ServerLevel extends Level implements WorldGenLevel {
   public static final BlockPos END_SPAWN_POINT = new BlockPos(100, 50, 0);
   public static final IntProvider RAIN_DELAY = UniformInt.of(12000, 180000);
   public static final IntProvider RAIN_DURATION = UniformInt.of(12000, 24000);
   private static final IntProvider THUNDER_DELAY = UniformInt.of(12000, 180000);
   public static final IntProvider THUNDER_DURATION = UniformInt.of(3600, 15600);
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int EMPTY_TIME_NO_TICK = 300;
   private static final int MAX_SCHEDULED_TICKS_PER_TICK = 65536;
   final List<ServerPlayer> players = Lists.newArrayList();
   private final ServerChunkCache chunkSource;
   private final MinecraftServer server;
   private final ServerLevelData serverLevelData;
   final EntityTickList entityTickList = new EntityTickList();
   private final PersistentEntitySectionManager<Entity> entityManager;
   private final GameEventDispatcher gameEventDispatcher;
   public boolean noSave;
   private final SleepStatus sleepStatus;
   private int emptyTime;
   private final PortalForcer portalForcer;
   private final LevelTicks<Block> blockTicks = new LevelTicks<>(this::isPositionTickingWithEntitiesLoaded, this.getProfilerSupplier());
   private final LevelTicks<Fluid> fluidTicks = new LevelTicks<>(this::isPositionTickingWithEntitiesLoaded, this.getProfilerSupplier());
   final Set<Mob> navigatingMobs = new ObjectOpenHashSet<>();
   volatile boolean isUpdatingNavigations;
   protected final Raids raids;
   private final ObjectLinkedOpenHashSet<BlockEventData> blockEvents = new ObjectLinkedOpenHashSet<>();
   private final List<BlockEventData> blockEventsToReschedule = new ArrayList<>(64);
   private boolean handlingTick;
   private final List<CustomSpawner> customSpawners;
   @Nullable
   private EndDragonFight dragonFight;
   final Int2ObjectMap<net.minecraftforge.entity.PartEntity<?>> dragonParts = new Int2ObjectOpenHashMap<>();
   private final StructureManager structureManager;
   private final StructureCheck structureCheck;
   private final boolean tickTime;
   private final RandomSequences randomSequences;
   private net.minecraftforge.common.util.LevelCapabilityData capabilityData;

   public ServerLevel(MinecraftServer p_214999_, Executor p_215000_, LevelStorageSource.LevelStorageAccess p_215001_, ServerLevelData p_215002_, ResourceKey<Level> p_215003_, LevelStem p_215004_, ChunkProgressListener p_215005_, boolean p_215006_, long p_215007_, List<CustomSpawner> p_215008_, boolean p_215009_, @Nullable RandomSequences p_288977_) {
      super(p_215002_, p_215003_, p_214999_.registryAccess(), p_215004_.type(), p_214999_::getProfiler, false, p_215006_, p_215007_, p_214999_.getMaxChainedNeighborUpdates());
      this.tickTime = p_215009_;
      this.server = p_214999_;
      this.customSpawners = p_215008_;
      this.serverLevelData = p_215002_;
      ChunkGenerator chunkgenerator = p_215004_.generator();
      boolean flag = p_214999_.forceSynchronousWrites();
      DataFixer datafixer = p_214999_.getFixerUpper();
      EntityPersistentStorage<Entity> entitypersistentstorage = new EntityStorage(this, p_215001_.getDimensionPath(p_215003_).resolve("entities"), datafixer, flag, p_214999_);
      this.entityManager = new PersistentEntitySectionManager<>(Entity.class, new ServerLevel.EntityCallbacks(), entitypersistentstorage);
      this.chunkSource = new ServerChunkCache(this, p_215001_, datafixer, p_214999_.getStructureManager(), p_215000_, chunkgenerator, p_214999_.getPlayerList().getViewDistance(), p_214999_.getPlayerList().getSimulationDistance(), flag, p_215005_, this.entityManager::updateChunkStatus, () -> {
         return p_214999_.overworld().getDataStorage();
      });
      this.chunkSource.getGeneratorState().ensureStructuresGenerated();
      this.portalForcer = new PortalForcer(this);
      this.updateSkyBrightness();
      this.prepareWeather();
      this.getWorldBorder().setAbsoluteMaxSize(p_214999_.getAbsoluteMaxWorldSize());
      this.raids = this.getDataStorage().computeIfAbsent((p_184095_) -> {
         return Raids.load(this, p_184095_);
      }, () -> {
         return new Raids(this);
      }, Raids.getFileId(this.dimensionTypeRegistration()));
      if (!p_214999_.isSingleplayer()) {
         p_215002_.setGameType(p_214999_.getDefaultGameType());
      }

      long i = p_214999_.getWorldData().worldGenOptions().seed();
      this.structureCheck = new StructureCheck(this.chunkSource.chunkScanner(), this.registryAccess(), p_214999_.getStructureManager(), p_215003_, chunkgenerator, this.chunkSource.randomState(), this, chunkgenerator.getBiomeSource(), i, datafixer);
      this.structureManager = new StructureManager(this, p_214999_.getWorldData().worldGenOptions(), this.structureCheck);
      if (this.dimension() == Level.END && this.dimensionTypeRegistration().is(BuiltinDimensionTypes.END)) {
         this.dragonFight = new EndDragonFight(this, i, p_214999_.getWorldData().endDragonFightData());
      } else {
         this.dragonFight = null;
      }

      this.sleepStatus = new SleepStatus();
      this.gameEventDispatcher = new GameEventDispatcher(this);
      this.randomSequences = Objects.requireNonNullElseGet(p_288977_, () -> {
         return this.getDataStorage().computeIfAbsent((p_287374_) -> {
            return RandomSequences.load(i, p_287374_);
         }, () -> {
            return new RandomSequences(i);
         }, "random_sequences");
      });
      this.initCapabilities();
   }

   /** @deprecated */
   @Deprecated
   @VisibleForTesting
   public void setDragonFight(@Nullable EndDragonFight p_287779_) {
      this.dragonFight = p_287779_;
   }

   public void setWeatherParameters(int p_8607_, int p_8608_, boolean p_8609_, boolean p_8610_) {
      this.serverLevelData.setClearWeatherTime(p_8607_);
      this.serverLevelData.setRainTime(p_8608_);
      this.serverLevelData.setThunderTime(p_8608_);
      this.serverLevelData.setRaining(p_8609_);
      this.serverLevelData.setThundering(p_8610_);
   }

   public Holder<Biome> getUncachedNoiseBiome(int p_203775_, int p_203776_, int p_203777_) {
      return this.getChunkSource().getGenerator().getBiomeSource().getNoiseBiome(p_203775_, p_203776_, p_203777_, this.getChunkSource().randomState().sampler());
   }

   public StructureManager structureManager() {
      return this.structureManager;
   }

   public void tick(BooleanSupplier p_8794_) {
      ProfilerFiller profilerfiller = this.getProfiler();
      this.handlingTick = true;
      profilerfiller.push("world border");
      this.getWorldBorder().tick();
      profilerfiller.popPush("weather");
      this.advanceWeatherCycle();
      int i = this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
      if (this.sleepStatus.areEnoughSleeping(i) && this.sleepStatus.areEnoughDeepSleeping(i, this.players)) {
         if (this.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            long j = this.getDayTime() + 24000L;
            this.setDayTime(net.minecraftforge.event.ForgeEventFactory.onSleepFinished(this, j - j % 24000L, this.getDayTime()));
         }

         this.wakeUpAllPlayers();
         if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE) && this.isRaining()) {
            this.resetWeatherCycle();
         }
      }

      this.updateSkyBrightness();
      this.tickTime();
      profilerfiller.popPush("tickPending");
      if (!this.isDebug()) {
         long k = this.getGameTime();
         profilerfiller.push("blockTicks");
         this.blockTicks.tick(k, 65536, this::tickBlock);
         profilerfiller.popPush("fluidTicks");
         this.fluidTicks.tick(k, 65536, this::tickFluid);
         profilerfiller.pop();
      }

      profilerfiller.popPush("raid");
      this.raids.tick();
      profilerfiller.popPush("chunkSource");
      this.getChunkSource().tick(p_8794_, true);
      profilerfiller.popPush("blockEvents");
      this.runBlockEvents();
      this.handlingTick = false;
      profilerfiller.pop();
      boolean flag = !this.players.isEmpty() || net.minecraftforge.common.world.ForgeChunkManager.hasForcedChunks(this); //Forge: Replace vanilla's has forced chunk check with forge's that checks both the vanilla and forge added ones
      if (flag) {
         this.resetEmptyTime();
      }

      if (flag || this.emptyTime++ < 300) {
         profilerfiller.push("entities");
         if (this.dragonFight != null) {
            profilerfiller.push("dragonFight");
            this.dragonFight.tick();
            profilerfiller.pop();
         }

         this.entityTickList.forEach((p_184065_) -> {
            if (!p_184065_.isRemoved()) {
               if (this.shouldDiscardEntity(p_184065_)) {
                  p_184065_.discard();
               } else {
                  profilerfiller.push("checkDespawn");
                  p_184065_.checkDespawn();
                  profilerfiller.pop();
                  if (this.chunkSource.chunkMap.getDistanceManager().inEntityTickingRange(p_184065_.chunkPosition().toLong())) {
                     Entity entity = p_184065_.getVehicle();
                     if (entity != null) {
                        if (!entity.isRemoved() && entity.hasPassenger(p_184065_)) {
                           return;
                        }

                        p_184065_.stopRiding();
                     }

                     profilerfiller.push("tick");
                     if (!p_184065_.isRemoved() && !(p_184065_ instanceof net.minecraftforge.entity.PartEntity)) {
                        this.guardEntityTick(this::tickNonPassenger, p_184065_);
                     }
                     profilerfiller.pop();
                  }
               }
            }
         });
         profilerfiller.pop();
         this.tickBlockEntities();
      }

      profilerfiller.push("entityManagement");
      this.entityManager.tick();
      profilerfiller.pop();
   }

   public boolean shouldTickBlocksAt(long p_184059_) {
      return this.chunkSource.chunkMap.getDistanceManager().inBlockTickingRange(p_184059_);
   }

   protected void tickTime() {
      if (this.tickTime) {
         long i = this.levelData.getGameTime() + 1L;
         this.serverLevelData.setGameTime(i);
         this.serverLevelData.getScheduledEvents().tick(this.server, i);
         if (this.levelData.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            this.setDayTime(this.levelData.getDayTime() + 1L);
         }

      }
   }

   public void setDayTime(long p_8616_) {
      this.serverLevelData.setDayTime(p_8616_);
   }

   public void tickCustomSpawners(boolean p_8800_, boolean p_8801_) {
      for(CustomSpawner customspawner : this.customSpawners) {
         customspawner.tick(this, p_8800_, p_8801_);
      }

   }

   private boolean shouldDiscardEntity(Entity p_143343_) {
      if (this.server.isSpawningAnimals() || !(p_143343_ instanceof Animal) && !(p_143343_ instanceof WaterAnimal)) {
         return !this.server.areNpcsEnabled() && p_143343_ instanceof Npc;
      } else {
         return true;
      }
   }

   private void wakeUpAllPlayers() {
      this.sleepStatus.removeAllSleepers();
      this.players.stream().filter(LivingEntity::isSleeping).collect(Collectors.toList()).forEach((p_184116_) -> {
         p_184116_.stopSleepInBed(false, false);
      });
   }

   public void tickChunk(LevelChunk p_8715_, int p_8716_) {
      ChunkPos chunkpos = p_8715_.getPos();
      boolean flag = this.isRaining();
      int i = chunkpos.getMinBlockX();
      int j = chunkpos.getMinBlockZ();
      ProfilerFiller profilerfiller = this.getProfiler();
      profilerfiller.push("thunder");
      if (flag && this.isThundering() && this.random.nextInt(100000) == 0) {
         BlockPos blockpos = this.findLightningTargetAround(this.getBlockRandomPos(i, 0, j, 15));
         if (this.isRainingAt(blockpos)) {
            DifficultyInstance difficultyinstance = this.getCurrentDifficultyAt(blockpos);
            boolean flag1 = this.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && this.random.nextDouble() < (double)difficultyinstance.getEffectiveDifficulty() * 0.01D && !this.getBlockState(blockpos.below()).is(Blocks.LIGHTNING_ROD);
            if (flag1) {
               SkeletonHorse skeletonhorse = EntityType.SKELETON_HORSE.create(this);
               if (skeletonhorse != null) {
                  skeletonhorse.setTrap(true);
                  skeletonhorse.setAge(0);
                  skeletonhorse.setPos((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
                  this.addFreshEntity(skeletonhorse);
               }
            }

            LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(this);
            if (lightningbolt != null) {
               lightningbolt.moveTo(Vec3.atBottomCenterOf(blockpos));
               lightningbolt.setVisualOnly(flag1);
               this.addFreshEntity(lightningbolt);
            }
         }
      }

      profilerfiller.popPush("iceandsnow");
      if (this.random.nextInt(16) == 0) {
         BlockPos blockpos1 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, this.getBlockRandomPos(i, 0, j, 15));
         BlockPos blockpos2 = blockpos1.below();
         Biome biome = this.getBiome(blockpos1).value();
         if (this.isAreaLoaded(blockpos2, 1)) // Forge: check area to avoid loading neighbors in unloaded chunks
         if (biome.shouldFreeze(this, blockpos2)) {
            this.setBlockAndUpdate(blockpos2, Blocks.ICE.defaultBlockState());
         }

         if (flag) {
            int i1 = this.getGameRules().getInt(GameRules.RULE_SNOW_ACCUMULATION_HEIGHT);
            if (i1 > 0 && biome.shouldSnow(this, blockpos1)) {
               BlockState blockstate = this.getBlockState(blockpos1);
               if (blockstate.is(Blocks.SNOW)) {
                  int k = blockstate.getValue(SnowLayerBlock.LAYERS);
                  if (k < Math.min(i1, 8)) {
                     BlockState blockstate1 = blockstate.setValue(SnowLayerBlock.LAYERS, Integer.valueOf(k + 1));
                     Block.pushEntitiesUp(blockstate, blockstate1, this, blockpos1);
                     this.setBlockAndUpdate(blockpos1, blockstate1);
                  }
               } else {
                  this.setBlockAndUpdate(blockpos1, Blocks.SNOW.defaultBlockState());
               }
            }

            Biome.Precipitation biome$precipitation = biome.getPrecipitationAt(blockpos2);
            if (biome$precipitation != Biome.Precipitation.NONE) {
               BlockState blockstate3 = this.getBlockState(blockpos2);
               blockstate3.getBlock().handlePrecipitation(blockstate3, this, blockpos2, biome$precipitation);
            }
         }
      }

      profilerfiller.popPush("tickBlocks");
      if (p_8716_ > 0) {
         LevelChunkSection[] alevelchunksection = p_8715_.getSections();

         for(int l = 0; l < alevelchunksection.length; ++l) {
            LevelChunkSection levelchunksection = alevelchunksection[l];
            if (levelchunksection.isRandomlyTicking()) {
               int j1 = p_8715_.getSectionYFromSectionIndex(l);
               int k1 = SectionPos.sectionToBlockCoord(j1);

               for(int l1 = 0; l1 < p_8716_; ++l1) {
                  BlockPos blockpos3 = this.getBlockRandomPos(i, k1, j, 15);
                  profilerfiller.push("randomTick");
                  BlockState blockstate2 = levelchunksection.getBlockState(blockpos3.getX() - i, blockpos3.getY() - k1, blockpos3.getZ() - j);
                  if (blockstate2.isRandomlyTicking()) {
                     blockstate2.randomTick(this, blockpos3, this.random);
                  }

                  FluidState fluidstate = blockstate2.getFluidState();
                  if (fluidstate.isRandomlyTicking()) {
                     fluidstate.randomTick(this, blockpos3, this.random);
                  }

                  profilerfiller.pop();
               }
            }
         }
      }

      profilerfiller.pop();
   }

   private Optional<BlockPos> findLightningRod(BlockPos p_143249_) {
      Optional<BlockPos> optional = this.getPoiManager().findClosest((p_215059_) -> {
         return p_215059_.is(PoiTypes.LIGHTNING_ROD);
      }, (p_184055_) -> {
         return p_184055_.getY() == this.getHeight(Heightmap.Types.WORLD_SURFACE, p_184055_.getX(), p_184055_.getZ()) - 1;
      }, p_143249_, 128, PoiManager.Occupancy.ANY);
      return optional.map((p_184053_) -> {
         return p_184053_.above(1);
      });
   }

   protected BlockPos findLightningTargetAround(BlockPos p_143289_) {
      BlockPos blockpos = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, p_143289_);
      Optional<BlockPos> optional = this.findLightningRod(blockpos);
      if (optional.isPresent()) {
         return optional.get();
      } else {
         AABB aabb = (new AABB(blockpos, new BlockPos(blockpos.getX(), this.getMaxBuildHeight(), blockpos.getZ()))).inflate(3.0D);
         List<LivingEntity> list = this.getEntitiesOfClass(LivingEntity.class, aabb, (p_289308_) -> {
            return p_289308_ != null && p_289308_.isAlive() && this.canSeeSky(p_289308_.blockPosition());
         });
         if (!list.isEmpty()) {
            return list.get(this.random.nextInt(list.size())).blockPosition();
         } else {
            if (blockpos.getY() == this.getMinBuildHeight() - 1) {
               blockpos = blockpos.above(2);
            }

            return blockpos;
         }
      }
   }

   public boolean isHandlingTick() {
      return this.handlingTick;
   }

   public boolean canSleepThroughNights() {
      return this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE) <= 100;
   }

   private void announceSleepStatus() {
      if (this.canSleepThroughNights()) {
         if (!this.getServer().isSingleplayer() || this.getServer().isPublished()) {
            int i = this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
            Component component;
            if (this.sleepStatus.areEnoughSleeping(i)) {
               component = Component.translatable("sleep.skipping_night");
            } else {
               component = Component.translatable("sleep.players_sleeping", this.sleepStatus.amountSleeping(), this.sleepStatus.sleepersNeeded(i));
            }

            for(ServerPlayer serverplayer : this.players) {
               serverplayer.displayClientMessage(component, true);
            }

         }
      }
   }

   public void updateSleepingPlayerList() {
      if (!this.players.isEmpty() && this.sleepStatus.update(this.players)) {
         this.announceSleepStatus();
      }

   }

   public ServerScoreboard getScoreboard() {
      return this.server.getScoreboard();
   }

   private void advanceWeatherCycle() {
      boolean flag = this.isRaining();
      if (this.dimensionType().hasSkyLight()) {
         if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
            int i = this.serverLevelData.getClearWeatherTime();
            int j = this.serverLevelData.getThunderTime();
            int k = this.serverLevelData.getRainTime();
            boolean flag1 = this.levelData.isThundering();
            boolean flag2 = this.levelData.isRaining();
            if (i > 0) {
               --i;
               j = flag1 ? 0 : 1;
               k = flag2 ? 0 : 1;
               flag1 = false;
               flag2 = false;
            } else {
               if (j > 0) {
                  --j;
                  if (j == 0) {
                     flag1 = !flag1;
                  }
               } else if (flag1) {
                  j = THUNDER_DURATION.sample(this.random);
               } else {
                  j = THUNDER_DELAY.sample(this.random);
               }

               if (k > 0) {
                  --k;
                  if (k == 0) {
                     flag2 = !flag2;
                  }
               } else if (flag2) {
                  k = RAIN_DURATION.sample(this.random);
               } else {
                  k = RAIN_DELAY.sample(this.random);
               }
            }

            this.serverLevelData.setThunderTime(j);
            this.serverLevelData.setRainTime(k);
            this.serverLevelData.setClearWeatherTime(i);
            this.serverLevelData.setThundering(flag1);
            this.serverLevelData.setRaining(flag2);
         }

         this.oThunderLevel = this.thunderLevel;
         if (this.levelData.isThundering()) {
            this.thunderLevel += 0.01F;
         } else {
            this.thunderLevel -= 0.01F;
         }

         this.thunderLevel = Mth.clamp(this.thunderLevel, 0.0F, 1.0F);
         this.oRainLevel = this.rainLevel;
         if (this.levelData.isRaining()) {
            this.rainLevel += 0.01F;
         } else {
            this.rainLevel -= 0.01F;
         }

         this.rainLevel = Mth.clamp(this.rainLevel, 0.0F, 1.0F);
      }

      if (this.oRainLevel != this.rainLevel) {
         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel), this.dimension());
      }

      if (this.oThunderLevel != this.thunderLevel) {
         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel), this.dimension());
      }

      /* The function in use here has been replaced in order to only send the weather info to players in the correct dimension,
       * rather than to all players on the server. This is what causes the client-side rain, as the
       * client believes that it has started raining locally, rather than in another dimension.
       */
      if (flag != this.isRaining()) {
         if (flag) {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.STOP_RAINING, 0.0F), this.dimension());
         } else {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0F), this.dimension());
         }

         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel), this.dimension());
         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel), this.dimension());
      }

   }

   private void resetWeatherCycle() {
      this.serverLevelData.setRainTime(0);
      this.serverLevelData.setRaining(false);
      this.serverLevelData.setThunderTime(0);
      this.serverLevelData.setThundering(false);
   }

   public void resetEmptyTime() {
      this.emptyTime = 0;
   }

   private void tickFluid(BlockPos p_184077_, Fluid p_184078_) {
      FluidState fluidstate = this.getFluidState(p_184077_);
      if (fluidstate.is(p_184078_)) {
         fluidstate.tick(this, p_184077_);
      }

   }

   private void tickBlock(BlockPos p_184113_, Block p_184114_) {
      BlockState blockstate = this.getBlockState(p_184113_);
      if (blockstate.is(p_184114_)) {
         blockstate.tick(this, p_184113_, this.random);
      }

   }

   public void tickNonPassenger(Entity p_8648_) {
      p_8648_.setOldPosAndRot();
      ProfilerFiller profilerfiller = this.getProfiler();
      ++p_8648_.tickCount;
      this.getProfiler().push(() -> {
         return BuiltInRegistries.ENTITY_TYPE.getKey(p_8648_.getType()).toString();
      });
      profilerfiller.incrementCounter("tickNonPassenger");
      p_8648_.tick();
      this.getProfiler().pop();

      for(Entity entity : p_8648_.getPassengers()) {
         this.tickPassenger(p_8648_, entity);
      }

   }

   private void tickPassenger(Entity p_8663_, Entity p_8664_) {
      if (!p_8664_.isRemoved() && p_8664_.getVehicle() == p_8663_) {
         if (p_8664_ instanceof Player || this.entityTickList.contains(p_8664_)) {
            p_8664_.setOldPosAndRot();
            ++p_8664_.tickCount;
            ProfilerFiller profilerfiller = this.getProfiler();
            profilerfiller.push(() -> {
               return BuiltInRegistries.ENTITY_TYPE.getKey(p_8664_.getType()).toString();
            });
            profilerfiller.incrementCounter("tickPassenger");
            if (p_8664_.canUpdate())
            p_8664_.rideTick();
            profilerfiller.pop();

            for(Entity entity : p_8664_.getPassengers()) {
               this.tickPassenger(p_8664_, entity);
            }

         }
      } else {
         p_8664_.stopRiding();
      }
   }

   public boolean mayInteract(Player p_8696_, BlockPos p_8697_) {
      return !this.server.isUnderSpawnProtection(this, p_8697_, p_8696_) && this.getWorldBorder().isWithinBounds(p_8697_);
   }

   public void save(@Nullable ProgressListener p_8644_, boolean p_8645_, boolean p_8646_) {
      ServerChunkCache serverchunkcache = this.getChunkSource();
      if (!p_8646_) {
         if (p_8644_ != null) {
            p_8644_.progressStartNoAbort(Component.translatable("menu.savingLevel"));
         }

         this.saveLevelData();
         if (p_8644_ != null) {
            p_8644_.progressStage(Component.translatable("menu.savingChunks"));
         }

         serverchunkcache.save(p_8645_);
         if (p_8645_) {
            this.entityManager.saveAll();
         } else {
            this.entityManager.autoSave();
         }

         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.level.LevelEvent.Save(this));
      }
   }

   private void saveLevelData() {
      if (this.dragonFight != null) {
         this.server.getWorldData().setEndDragonFightData(this.dragonFight.saveData());
      }

      this.getChunkSource().getDataStorage().save();
   }

   public <T extends Entity> List<? extends T> getEntities(EntityTypeTest<Entity, T> p_143281_, Predicate<? super T> p_143282_) {
      List<T> list = Lists.newArrayList();
      this.getEntities(p_143281_, p_143282_, list);
      return list;
   }

   public <T extends Entity> void getEntities(EntityTypeTest<Entity, T> p_262152_, Predicate<? super T> p_261808_, List<? super T> p_261583_) {
      this.getEntities(p_262152_, p_261808_, p_261583_, Integer.MAX_VALUE);
   }

   public <T extends Entity> void getEntities(EntityTypeTest<Entity, T> p_261842_, Predicate<? super T> p_262091_, List<? super T> p_261703_, int p_261907_) {
      this.getEntities().get(p_261842_, (p_261428_) -> {
         if (p_262091_.test(p_261428_)) {
            p_261703_.add(p_261428_);
            if (p_261703_.size() >= p_261907_) {
               return AbortableIterationConsumer.Continuation.ABORT;
            }
         }

         return AbortableIterationConsumer.Continuation.CONTINUE;
      });
   }

   public List<? extends EnderDragon> getDragons() {
      return this.getEntities(EntityType.ENDER_DRAGON, LivingEntity::isAlive);
   }

   public List<ServerPlayer> getPlayers(Predicate<? super ServerPlayer> p_8796_) {
      return this.getPlayers(p_8796_, Integer.MAX_VALUE);
   }

   public List<ServerPlayer> getPlayers(Predicate<? super ServerPlayer> p_261698_, int p_262035_) {
      List<ServerPlayer> list = Lists.newArrayList();

      for(ServerPlayer serverplayer : this.players) {
         if (p_261698_.test(serverplayer)) {
            list.add(serverplayer);
            if (list.size() >= p_262035_) {
               return list;
            }
         }
      }

      return list;
   }

   @Nullable
   public ServerPlayer getRandomPlayer() {
      List<ServerPlayer> list = this.getPlayers(LivingEntity::isAlive);
      return list.isEmpty() ? null : list.get(this.random.nextInt(list.size()));
   }

   public boolean addFreshEntity(Entity p_8837_) {
      return this.addEntity(p_8837_);
   }

   public boolean addWithUUID(Entity p_8848_) {
      return this.addEntity(p_8848_);
   }

   public void addDuringTeleport(Entity p_143335_) {
      this.addEntity(p_143335_);
   }

   public void addDuringCommandTeleport(ServerPlayer p_8623_) {
      this.addPlayer(p_8623_);
   }

   public void addDuringPortalTeleport(ServerPlayer p_8818_) {
      this.addPlayer(p_8818_);
   }

   public void addNewPlayer(ServerPlayer p_8835_) {
      this.addPlayer(p_8835_);
   }

   public void addRespawnedPlayer(ServerPlayer p_8846_) {
      this.addPlayer(p_8846_);
   }

   private void addPlayer(ServerPlayer p_8854_) {
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityJoinLevelEvent(p_8854_, this))) return;
      Entity entity = this.getEntities().get(p_8854_.getUUID());
      if (entity != null) {
         LOGGER.warn("Force-added player with duplicate UUID {}", (Object)p_8854_.getUUID().toString());
         entity.unRide();
         this.removePlayerImmediately((ServerPlayer)entity, Entity.RemovalReason.DISCARDED);
      }

      this.entityManager.addNewEntityWithoutEvent(p_8854_);
      p_8854_.onAddedToWorld();
   }

   private boolean addEntity(Entity p_8873_) {
      if (p_8873_.isRemoved()) {
         LOGGER.warn("Tried to add entity {} but it was marked as removed already", (Object)EntityType.getKey(p_8873_.getType()));
         return false;
      } else {
         if (this.entityManager.addNewEntity(p_8873_)) {
            p_8873_.onAddedToWorld();
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean tryAddFreshEntityWithPassengers(Entity p_8861_) {
      if (p_8861_.getSelfAndPassengers().map(Entity::getUUID).anyMatch(this.entityManager::isLoaded)) {
         return false;
      } else {
         this.addFreshEntityWithPassengers(p_8861_);
         return true;
      }
   }

   public void unload(LevelChunk p_8713_) {
      p_8713_.clearAllBlockEntities();
      p_8713_.unregisterTickContainerFromLevel(this);
   }

   public void removePlayerImmediately(ServerPlayer p_143262_, Entity.RemovalReason p_143263_) {
      p_143262_.remove(p_143263_);
   }

   public void destroyBlockProgress(int p_8612_, BlockPos p_8613_, int p_8614_) {
      for(ServerPlayer serverplayer : this.server.getPlayerList().getPlayers()) {
         if (serverplayer != null && serverplayer.level() == this && serverplayer.getId() != p_8612_) {
            double d0 = (double)p_8613_.getX() - serverplayer.getX();
            double d1 = (double)p_8613_.getY() - serverplayer.getY();
            double d2 = (double)p_8613_.getZ() - serverplayer.getZ();
            if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D) {
               serverplayer.connection.send(new ClientboundBlockDestructionPacket(p_8612_, p_8613_, p_8614_));
            }
         }
      }

   }

   public void playSeededSound(@Nullable Player p_263330_, double p_263393_, double p_263369_, double p_263354_, Holder<SoundEvent> p_263412_, SoundSource p_263338_, float p_263352_, float p_263390_, long p_263403_) {
      net.minecraftforge.event.PlayLevelSoundEvent.AtPosition event = net.minecraftforge.event.ForgeEventFactory.onPlaySoundAtPosition(this, p_263393_, p_263369_, p_263354_, p_263412_, p_263338_, p_263352_, p_263390_);
      if (event.isCanceled() || event.getSound() == null) return;
      p_263412_ = event.getSound();
      p_263338_ = event.getSource();
      p_263352_ = event.getNewVolume();
      p_263390_ = event.getNewPitch();
      this.server.getPlayerList().broadcast(p_263330_, p_263393_, p_263369_, p_263354_, (double)p_263412_.value().getRange(p_263352_), this.dimension(), new ClientboundSoundPacket(p_263412_, p_263338_, p_263393_, p_263369_, p_263354_, p_263352_, p_263390_, p_263403_));
   }

   public void playSeededSound(@Nullable Player p_263545_, Entity p_263544_, Holder<SoundEvent> p_263491_, SoundSource p_263542_, float p_263530_, float p_263520_, long p_263490_) {
      net.minecraftforge.event.PlayLevelSoundEvent.AtEntity event = net.minecraftforge.event.ForgeEventFactory.onPlaySoundAtEntity(p_263544_, p_263491_, p_263542_, p_263530_, p_263520_);
      if (event.isCanceled() || event.getSound() == null) return;
      p_263491_ = event.getSound();
      p_263542_ = event.getSource();
      p_263530_ = event.getNewVolume();
      p_263520_ = event.getNewPitch();
      this.server.getPlayerList().broadcast(p_263545_, p_263544_.getX(), p_263544_.getY(), p_263544_.getZ(), (double)p_263491_.value().getRange(p_263530_), this.dimension(), new ClientboundSoundEntityPacket(p_263491_, p_263542_, p_263544_, p_263530_, p_263520_, p_263490_));
   }

   public void globalLevelEvent(int p_8811_, BlockPos p_8812_, int p_8813_) {
      if (this.getGameRules().getBoolean(GameRules.RULE_GLOBAL_SOUND_EVENTS)) {
         this.server.getPlayerList().broadcastAll(new ClientboundLevelEventPacket(p_8811_, p_8812_, p_8813_, true));
      } else {
         this.levelEvent((Player)null, p_8811_, p_8812_, p_8813_);
      }

   }

   public void levelEvent(@Nullable Player p_8684_, int p_8685_, BlockPos p_8686_, int p_8687_) {
      this.server.getPlayerList().broadcast(p_8684_, (double)p_8686_.getX(), (double)p_8686_.getY(), (double)p_8686_.getZ(), 64.0D, this.dimension(), new ClientboundLevelEventPacket(p_8685_, p_8686_, p_8687_, false));
   }

   public int getLogicalHeight() {
      return this.dimensionType().logicalHeight();
   }

   public void gameEvent(GameEvent p_215041_, Vec3 p_215042_, GameEvent.Context p_215043_) {
      if (!net.minecraftforge.common.ForgeHooks.onVanillaGameEvent(this, p_215041_, p_215042_, p_215043_)) return;
      this.gameEventDispatcher.post(p_215041_, p_215042_, p_215043_);
   }

   public void sendBlockUpdated(BlockPos p_8755_, BlockState p_8756_, BlockState p_8757_, int p_8758_) {
      if (this.isUpdatingNavigations) {
         String s = "recursive call to sendBlockUpdated";
         Util.logAndPauseIfInIde("recursive call to sendBlockUpdated", new IllegalStateException("recursive call to sendBlockUpdated"));
      }

      this.getChunkSource().blockChanged(p_8755_);
      VoxelShape voxelshape1 = p_8756_.getCollisionShape(this, p_8755_);
      VoxelShape voxelshape = p_8757_.getCollisionShape(this, p_8755_);
      if (Shapes.joinIsNotEmpty(voxelshape1, voxelshape, BooleanOp.NOT_SAME)) {
         List<PathNavigation> list = new ObjectArrayList<>();

         for(Mob mob : this.navigatingMobs) {
            PathNavigation pathnavigation = mob.getNavigation();
            if (pathnavigation.shouldRecomputePath(p_8755_)) {
               list.add(pathnavigation);
            }
         }

         try {
            this.isUpdatingNavigations = true;

            for(PathNavigation pathnavigation1 : list) {
               pathnavigation1.recomputePath();
            }
         } finally {
            this.isUpdatingNavigations = false;
         }

      }
   }

   public void updateNeighborsAt(BlockPos p_215045_, Block p_215046_) {
      net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(this, p_215045_, this.getBlockState(p_215045_), java.util.EnumSet.allOf(Direction.class), false).isCanceled();
      this.neighborUpdater.updateNeighborsAtExceptFromFacing(p_215045_, p_215046_, (Direction)null);
   }

   public void updateNeighborsAtExceptFromFacing(BlockPos p_215052_, Block p_215053_, Direction p_215054_) {
      java.util.EnumSet<Direction> directions = java.util.EnumSet.allOf(Direction.class);
      directions.remove(p_215054_);
      if (net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(this, p_215052_, this.getBlockState(p_215052_), directions, false).isCanceled())
         return;
      this.neighborUpdater.updateNeighborsAtExceptFromFacing(p_215052_, p_215053_, p_215054_);
   }

   public void neighborChanged(BlockPos p_215048_, Block p_215049_, BlockPos p_215050_) {
      this.neighborUpdater.neighborChanged(p_215048_, p_215049_, p_215050_);
   }

   public void neighborChanged(BlockState p_215035_, BlockPos p_215036_, Block p_215037_, BlockPos p_215038_, boolean p_215039_) {
      this.neighborUpdater.neighborChanged(p_215035_, p_215036_, p_215037_, p_215038_, p_215039_);
   }

   public void broadcastEntityEvent(Entity p_8650_, byte p_8651_) {
      this.getChunkSource().broadcastAndSend(p_8650_, new ClientboundEntityEventPacket(p_8650_, p_8651_));
   }

   public void broadcastDamageEvent(Entity p_270420_, DamageSource p_270311_) {
      this.getChunkSource().broadcastAndSend(p_270420_, new ClientboundDamageEventPacket(p_270420_, p_270311_));
   }

   public ServerChunkCache getChunkSource() {
      return this.chunkSource;
   }

   public Explosion explode(@Nullable Entity p_256039_, @Nullable DamageSource p_255778_, @Nullable ExplosionDamageCalculator p_256002_, double p_256067_, double p_256370_, double p_256153_, float p_256045_, boolean p_255686_, Level.ExplosionInteraction p_255827_) {
      Explosion explosion = this.explode(p_256039_, p_255778_, p_256002_, p_256067_, p_256370_, p_256153_, p_256045_, p_255686_, p_255827_, false);
      if (!explosion.interactsWithBlocks()) {
         explosion.clearToBlow();
      }

      for(ServerPlayer serverplayer : this.players) {
         if (serverplayer.distanceToSqr(p_256067_, p_256370_, p_256153_) < 4096.0D) {
            serverplayer.connection.send(new ClientboundExplodePacket(p_256067_, p_256370_, p_256153_, p_256045_, explosion.getToBlow(), explosion.getHitPlayers().get(serverplayer)));
         }
      }

      return explosion;
   }

   public void blockEvent(BlockPos p_8746_, Block p_8747_, int p_8748_, int p_8749_) {
      this.blockEvents.add(new BlockEventData(p_8746_, p_8747_, p_8748_, p_8749_));
   }

   private void runBlockEvents() {
      this.blockEventsToReschedule.clear();

      while(!this.blockEvents.isEmpty()) {
         BlockEventData blockeventdata = this.blockEvents.removeFirst();
         if (this.shouldTickBlocksAt(blockeventdata.pos())) {
            if (this.doBlockEvent(blockeventdata)) {
               this.server.getPlayerList().broadcast((Player)null, (double)blockeventdata.pos().getX(), (double)blockeventdata.pos().getY(), (double)blockeventdata.pos().getZ(), 64.0D, this.dimension(), new ClientboundBlockEventPacket(blockeventdata.pos(), blockeventdata.block(), blockeventdata.paramA(), blockeventdata.paramB()));
            }
         } else {
            this.blockEventsToReschedule.add(blockeventdata);
         }
      }

      this.blockEvents.addAll(this.blockEventsToReschedule);
   }

   private boolean doBlockEvent(BlockEventData p_8699_) {
      BlockState blockstate = this.getBlockState(p_8699_.pos());
      return blockstate.is(p_8699_.block()) ? blockstate.triggerEvent(this, p_8699_.pos(), p_8699_.paramA(), p_8699_.paramB()) : false;
   }

   public LevelTicks<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public LevelTicks<Fluid> getFluidTicks() {
      return this.fluidTicks;
   }

   @Nonnull
   public MinecraftServer getServer() {
      return this.server;
   }

   public PortalForcer getPortalForcer() {
      return this.portalForcer;
   }

   public StructureTemplateManager getStructureManager() {
      return this.server.getStructureManager();
   }

   public <T extends ParticleOptions> int sendParticles(T p_8768_, double p_8769_, double p_8770_, double p_8771_, int p_8772_, double p_8773_, double p_8774_, double p_8775_, double p_8776_) {
      ClientboundLevelParticlesPacket clientboundlevelparticlespacket = new ClientboundLevelParticlesPacket(p_8768_, false, p_8769_, p_8770_, p_8771_, (float)p_8773_, (float)p_8774_, (float)p_8775_, (float)p_8776_, p_8772_);
      int i = 0;

      for(int j = 0; j < this.players.size(); ++j) {
         ServerPlayer serverplayer = this.players.get(j);
         if (this.sendParticles(serverplayer, false, p_8769_, p_8770_, p_8771_, clientboundlevelparticlespacket)) {
            ++i;
         }
      }

      return i;
   }

   public <T extends ParticleOptions> boolean sendParticles(ServerPlayer p_8625_, T p_8626_, boolean p_8627_, double p_8628_, double p_8629_, double p_8630_, int p_8631_, double p_8632_, double p_8633_, double p_8634_, double p_8635_) {
      Packet<?> packet = new ClientboundLevelParticlesPacket(p_8626_, p_8627_, p_8628_, p_8629_, p_8630_, (float)p_8632_, (float)p_8633_, (float)p_8634_, (float)p_8635_, p_8631_);
      return this.sendParticles(p_8625_, p_8627_, p_8628_, p_8629_, p_8630_, packet);
   }

   private boolean sendParticles(ServerPlayer p_8637_, boolean p_8638_, double p_8639_, double p_8640_, double p_8641_, Packet<?> p_8642_) {
      if (p_8637_.level() != this) {
         return false;
      } else {
         BlockPos blockpos = p_8637_.blockPosition();
         if (blockpos.closerToCenterThan(new Vec3(p_8639_, p_8640_, p_8641_), p_8638_ ? 512.0D : 32.0D)) {
            p_8637_.connection.send(p_8642_);
            return true;
         } else {
            return false;
         }
      }
   }

   @Nullable
   public Entity getEntity(int p_8597_) {
      return this.getEntities().get(p_8597_);
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   public Entity getEntityOrPart(int p_143318_) {
      Entity entity = this.getEntities().get(p_143318_);
      return entity != null ? entity : this.dragonParts.get(p_143318_);
   }

   @Nullable
   public Entity getEntity(UUID p_8792_) {
      return this.getEntities().get(p_8792_);
   }

   @Nullable
   public BlockPos findNearestMapStructure(TagKey<Structure> p_215012_, BlockPos p_215013_, int p_215014_, boolean p_215015_) {
      if (!this.server.getWorldData().worldGenOptions().generateStructures()) {
         return null;
      } else {
         Optional<HolderSet.Named<Structure>> optional = this.registryAccess().registryOrThrow(Registries.STRUCTURE).getTag(p_215012_);
         if (optional.isEmpty()) {
            return null;
         } else {
            Pair<BlockPos, Holder<Structure>> pair = this.getChunkSource().getGenerator().findNearestMapStructure(this, optional.get(), p_215013_, p_215014_, p_215015_);
            return pair != null ? pair.getFirst() : null;
         }
      }
   }

   @Nullable
   public Pair<BlockPos, Holder<Biome>> findClosestBiome3d(Predicate<Holder<Biome>> p_215070_, BlockPos p_215071_, int p_215072_, int p_215073_, int p_215074_) {
      return this.getChunkSource().getGenerator().getBiomeSource().findClosestBiome3d(p_215071_, p_215072_, p_215073_, p_215074_, p_215070_, this.getChunkSource().randomState().sampler(), this);
   }

   public RecipeManager getRecipeManager() {
      return this.server.getRecipeManager();
   }

   public boolean noSave() {
      return this.noSave;
   }

   public DimensionDataStorage getDataStorage() {
      return this.getChunkSource().getDataStorage();
   }

   @Nullable
   public MapItemSavedData getMapData(String p_8785_) {
      return this.getServer().overworld().getDataStorage().get(MapItemSavedData::load, p_8785_);
   }

   public void setMapData(String p_143305_, MapItemSavedData p_143306_) {
      this.getServer().overworld().getDataStorage().set(p_143305_, p_143306_);
   }

   public int getFreeMapId() {
      return this.getServer().overworld().getDataStorage().computeIfAbsent(MapIndex::load, MapIndex::new, "idcounts").getFreeAuxValueForMap();
   }

   public void setDefaultSpawnPos(BlockPos p_8734_, float p_8735_) {
      ChunkPos chunkpos = new ChunkPos(new BlockPos(this.levelData.getXSpawn(), 0, this.levelData.getZSpawn()));
      this.levelData.setSpawn(p_8734_, p_8735_);
      this.getChunkSource().removeRegionTicket(TicketType.START, chunkpos, 11, Unit.INSTANCE);
      this.getChunkSource().addRegionTicket(TicketType.START, new ChunkPos(p_8734_), 11, Unit.INSTANCE);
      this.getServer().getPlayerList().broadcastAll(new ClientboundSetDefaultSpawnPositionPacket(p_8734_, p_8735_));
   }

   public LongSet getForcedChunks() {
      ForcedChunksSavedData forcedchunkssaveddata = this.getDataStorage().get(ForcedChunksSavedData::load, "chunks");
      return (LongSet)(forcedchunkssaveddata != null ? LongSets.unmodifiable(forcedchunkssaveddata.getChunks()) : LongSets.EMPTY_SET);
   }

   public boolean setChunkForced(int p_8603_, int p_8604_, boolean p_8605_) {
      ForcedChunksSavedData forcedchunkssaveddata = this.getDataStorage().computeIfAbsent(ForcedChunksSavedData::load, ForcedChunksSavedData::new, "chunks");
      ChunkPos chunkpos = new ChunkPos(p_8603_, p_8604_);
      long i = chunkpos.toLong();
      boolean flag;
      if (p_8605_) {
         flag = forcedchunkssaveddata.getChunks().add(i);
         if (flag) {
            this.getChunk(p_8603_, p_8604_);
         }
      } else {
         flag = forcedchunkssaveddata.getChunks().remove(i);
      }

      forcedchunkssaveddata.setDirty(flag);
      if (flag) {
         this.getChunkSource().updateChunkForced(chunkpos, p_8605_);
      }

      return flag;
   }

   public List<ServerPlayer> players() {
      return this.players;
   }

   public void onBlockStateChange(BlockPos p_8751_, BlockState p_8752_, BlockState p_8753_) {
      Optional<Holder<PoiType>> optional = PoiTypes.forState(p_8752_);
      Optional<Holder<PoiType>> optional1 = PoiTypes.forState(p_8753_);
      if (!Objects.equals(optional, optional1)) {
         BlockPos blockpos = p_8751_.immutable();
         optional.ifPresent((p_215081_) -> {
            this.getServer().execute(() -> {
               this.getPoiManager().remove(blockpos);
               DebugPackets.sendPoiRemovedPacket(this, blockpos);
            });
         });
         optional1.ifPresent((p_215057_) -> {
            this.getServer().execute(() -> {
               this.getPoiManager().add(blockpos, p_215057_);
               DebugPackets.sendPoiAddedPacket(this, blockpos);
            });
         });
      }
   }

   public PoiManager getPoiManager() {
      return this.getChunkSource().getPoiManager();
   }

   public boolean isVillage(BlockPos p_8803_) {
      return this.isCloseToVillage(p_8803_, 1);
   }

   public boolean isVillage(SectionPos p_8763_) {
      return this.isVillage(p_8763_.center());
   }

   public boolean isCloseToVillage(BlockPos p_8737_, int p_8738_) {
      if (p_8738_ > 6) {
         return false;
      } else {
         return this.sectionsToVillage(SectionPos.of(p_8737_)) <= p_8738_;
      }
   }

   public int sectionsToVillage(SectionPos p_8829_) {
      return this.getPoiManager().sectionsToVillage(p_8829_);
   }

   public Raids getRaids() {
      return this.raids;
   }

   @Nullable
   public Raid getRaidAt(BlockPos p_8833_) {
      return this.raids.getNearbyRaid(p_8833_, 9216);
   }

   public boolean isRaided(BlockPos p_8844_) {
      return this.getRaidAt(p_8844_) != null;
   }

   public void onReputationEvent(ReputationEventType p_8671_, Entity p_8672_, ReputationEventHandler p_8673_) {
      p_8673_.onReputationEventFrom(p_8671_, p_8672_);
   }

   public void saveDebugReport(Path p_8787_) throws IOException {
      ChunkMap chunkmap = this.getChunkSource().chunkMap;

      try (Writer writer = Files.newBufferedWriter(p_8787_.resolve("stats.txt"))) {
         writer.write(String.format(Locale.ROOT, "spawning_chunks: %d\n", chunkmap.getDistanceManager().getNaturalSpawnChunkCount()));
         NaturalSpawner.SpawnState naturalspawner$spawnstate = this.getChunkSource().getLastSpawnState();
         if (naturalspawner$spawnstate != null) {
            for(Object2IntMap.Entry<MobCategory> entry : naturalspawner$spawnstate.getMobCategoryCounts().object2IntEntrySet()) {
               writer.write(String.format(Locale.ROOT, "spawn_count.%s: %d\n", entry.getKey().getName(), entry.getIntValue()));
            }
         }

         writer.write(String.format(Locale.ROOT, "entities: %s\n", this.entityManager.gatherStats()));
         writer.write(String.format(Locale.ROOT, "block_entity_tickers: %d\n", this.blockEntityTickers.size()));
         writer.write(String.format(Locale.ROOT, "block_ticks: %d\n", this.getBlockTicks().count()));
         writer.write(String.format(Locale.ROOT, "fluid_ticks: %d\n", this.getFluidTicks().count()));
         writer.write("distance_manager: " + chunkmap.getDistanceManager().getDebugStatus() + "\n");
         writer.write(String.format(Locale.ROOT, "pending_tasks: %d\n", this.getChunkSource().getPendingTasksCount()));
      }

      CrashReport crashreport = new CrashReport("Level dump", new Exception("dummy"));
      this.fillReportDetails(crashreport);

      try (Writer writer3 = Files.newBufferedWriter(p_8787_.resolve("example_crash.txt"))) {
         writer3.write(crashreport.getFriendlyReport());
      }

      Path path = p_8787_.resolve("chunks.csv");

      try (Writer writer4 = Files.newBufferedWriter(path)) {
         chunkmap.dumpChunks(writer4);
      }

      Path path1 = p_8787_.resolve("entity_chunks.csv");

      try (Writer writer5 = Files.newBufferedWriter(path1)) {
         this.entityManager.dumpSections(writer5);
      }

      Path path2 = p_8787_.resolve("entities.csv");

      try (Writer writer1 = Files.newBufferedWriter(path2)) {
         dumpEntities(writer1, this.getEntities().getAll());
      }

      Path path3 = p_8787_.resolve("block_entities.csv");

      try (Writer writer2 = Files.newBufferedWriter(path3)) {
         this.dumpBlockEntityTickers(writer2);
      }

   }

   private static void dumpEntities(Writer p_8782_, Iterable<Entity> p_8783_) throws IOException {
      CsvOutput csvoutput = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("uuid").addColumn("type").addColumn("alive").addColumn("display_name").addColumn("custom_name").build(p_8782_);

      for(Entity entity : p_8783_) {
         Component component = entity.getCustomName();
         Component component1 = entity.getDisplayName();
         csvoutput.writeRow(entity.getX(), entity.getY(), entity.getZ(), entity.getUUID(), BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()), entity.isAlive(), component1.getString(), component != null ? component.getString() : null);
      }

   }

   private void dumpBlockEntityTickers(Writer p_143300_) throws IOException {
      CsvOutput csvoutput = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("type").build(p_143300_);

      for(TickingBlockEntity tickingblockentity : this.blockEntityTickers) {
         BlockPos blockpos = tickingblockentity.getPos();
         csvoutput.writeRow(blockpos.getX(), blockpos.getY(), blockpos.getZ(), tickingblockentity.getType());
      }

   }

   @VisibleForTesting
   public void clearBlockEvents(BoundingBox p_8723_) {
      this.blockEvents.removeIf((p_207568_) -> {
         return p_8723_.isInside(p_207568_.pos());
      });
   }

   public void blockUpdated(BlockPos p_8743_, Block p_8744_) {
      if (!this.isDebug()) {
         this.updateNeighborsAt(p_8743_, p_8744_);
      }

   }

   public float getShade(Direction p_8760_, boolean p_8761_) {
      return 1.0F;
   }

   public Iterable<Entity> getAllEntities() {
      return this.getEntities().getAll();
   }

   public String toString() {
      return "ServerLevel[" + this.serverLevelData.getLevelName() + "]";
   }

   public boolean isFlat() {
      return this.server.getWorldData().isFlatWorld();
   }

   public long getSeed() {
      return this.server.getWorldData().worldGenOptions().seed();
   }

   @Nullable
   public EndDragonFight getDragonFight() {
      return this.dragonFight;
   }

   public ServerLevel getLevel() {
      return this;
   }

   @VisibleForTesting
   public String getWatchdogStats() {
      return String.format(Locale.ROOT, "players: %s, entities: %s [%s], block_entities: %d [%s], block_ticks: %d, fluid_ticks: %d, chunk_source: %s", this.players.size(), this.entityManager.gatherStats(), getTypeCount(this.entityManager.getEntityGetter().getAll(), (p_258244_) -> {
         return BuiltInRegistries.ENTITY_TYPE.getKey(p_258244_.getType()).toString();
      }), this.blockEntityTickers.size(), getTypeCount(this.blockEntityTickers, TickingBlockEntity::getType), this.getBlockTicks().count(), this.getFluidTicks().count(), this.gatherChunkSourceStats());
   }

   private static <T> String getTypeCount(Iterable<T> p_143302_, Function<T, String> p_143303_) {
      try {
         Object2IntOpenHashMap<String> object2intopenhashmap = new Object2IntOpenHashMap<>();

         for(T t : p_143302_) {
            String s = p_143303_.apply(t);
            object2intopenhashmap.addTo(s, 1);
         }

         return object2intopenhashmap.object2IntEntrySet().stream().sorted(Comparator.<Object2IntMap.Entry<String>,Integer>comparing(Object2IntMap.Entry::getIntValue).reversed()).limit(5L).map((p_207570_) -> {
            return (String)p_207570_.getKey() + ":" + p_207570_.getIntValue();
         }).collect(Collectors.joining(","));
      } catch (Exception exception) {
         return "";
      }
   }

   public static void makeObsidianPlatform(ServerLevel p_8618_) {
      BlockPos blockpos = END_SPAWN_POINT;
      int i = blockpos.getX();
      int j = blockpos.getY() - 2;
      int k = blockpos.getZ();
      BlockPos.betweenClosed(i - 2, j + 1, k - 2, i + 2, j + 3, k + 2).forEach((p_207578_) -> {
         p_8618_.setBlockAndUpdate(p_207578_, Blocks.AIR.defaultBlockState());
      });
      BlockPos.betweenClosed(i - 2, j, k - 2, i + 2, j, k + 2).forEach((p_184101_) -> {
         p_8618_.setBlockAndUpdate(p_184101_, Blocks.OBSIDIAN.defaultBlockState());
      });
   }

   protected void initCapabilities() {
      this.gatherCapabilities();
      capabilityData = this.getDataStorage().computeIfAbsent(e -> net.minecraftforge.common.util.LevelCapabilityData.load(e, getCapabilities()), () -> new net.minecraftforge.common.util.LevelCapabilityData(getCapabilities()), net.minecraftforge.common.util.LevelCapabilityData.ID);
      capabilityData.setCapabilities(getCapabilities());
   }

   public LevelEntityGetter<Entity> getEntities() {
      return this.entityManager.getEntityGetter();
   }

   public void addLegacyChunkEntities(Stream<Entity> p_143312_) {
      this.entityManager.addLegacyChunkEntities(p_143312_);
   }

   public void addWorldGenChunkEntities(Stream<Entity> p_143328_) {
      this.entityManager.addWorldGenChunkEntities(p_143328_);
   }

   public void startTickingChunk(LevelChunk p_184103_) {
      p_184103_.unpackTicks(this.getLevelData().getGameTime());
   }

   public void onStructureStartsAvailable(ChunkAccess p_196558_) {
      this.server.execute(() -> {
         this.structureCheck.onStructureLoad(p_196558_.getPos(), p_196558_.getAllStarts());
      });
   }

   public void close() throws IOException {
      super.close();
      this.entityManager.close();
   }

   public String gatherChunkSourceStats() {
      return "Chunks[S] W: " + this.chunkSource.gatherStats() + " E: " + this.entityManager.gatherStats();
   }

   public boolean areEntitiesLoaded(long p_143320_) {
      return this.entityManager.areEntitiesLoaded(p_143320_);
   }

   private boolean isPositionTickingWithEntitiesLoaded(long p_184111_) {
      return this.areEntitiesLoaded(p_184111_) && this.chunkSource.isPositionTicking(p_184111_);
   }

   public boolean isPositionEntityTicking(BlockPos p_143341_) {
      return this.entityManager.canPositionTick(p_143341_) && this.chunkSource.chunkMap.getDistanceManager().inEntityTickingRange(ChunkPos.asLong(p_143341_));
   }

   public boolean isNaturalSpawningAllowed(BlockPos p_201919_) {
      return this.entityManager.canPositionTick(p_201919_);
   }

   public boolean isNaturalSpawningAllowed(ChunkPos p_201917_) {
      return this.entityManager.canPositionTick(p_201917_);
   }

   public FeatureFlagSet enabledFeatures() {
      return this.server.getWorldData().enabledFeatures();
   }

   public RandomSource getRandomSequence(ResourceLocation p_287689_) {
      return this.randomSequences.get(p_287689_);
   }

   public RandomSequences getRandomSequences() {
      return this.randomSequences;
   }

   final class EntityCallbacks implements LevelCallback<Entity> {
      public void onCreated(Entity p_143355_) {
      }

      public void onDestroyed(Entity p_143359_) {
         ServerLevel.this.getScoreboard().entityRemoved(p_143359_);
      }

      public void onTickingStart(Entity p_143363_) {
         ServerLevel.this.entityTickList.add(p_143363_);
      }

      public void onTickingEnd(Entity p_143367_) {
         ServerLevel.this.entityTickList.remove(p_143367_);
      }

      public void onTrackingStart(Entity p_143371_) {
         ServerLevel.this.getChunkSource().addEntity(p_143371_);
         if (p_143371_ instanceof ServerPlayer serverplayer) {
            ServerLevel.this.players.add(serverplayer);
            ServerLevel.this.updateSleepingPlayerList();
         }

         if (p_143371_ instanceof Mob mob) {
            if (ServerLevel.this.isUpdatingNavigations) {
               String s = "onTrackingStart called during navigation iteration";
               Util.logAndPauseIfInIde("onTrackingStart called during navigation iteration", new IllegalStateException("onTrackingStart called during navigation iteration"));
            }

            ServerLevel.this.navigatingMobs.add(mob);
         }

         if (p_143371_.isMultipartEntity()) {
            for(net.minecraftforge.entity.PartEntity<?> enderdragonpart : p_143371_.getParts()) {
               ServerLevel.this.dragonParts.put(enderdragonpart.getId(), enderdragonpart);
            }
         }

         p_143371_.updateDynamicGameEventListener(DynamicGameEventListener::add);
      }

      public void onTrackingEnd(Entity p_143375_) {
         ServerLevel.this.getChunkSource().removeEntity(p_143375_);
         if (p_143375_ instanceof ServerPlayer serverplayer) {
            ServerLevel.this.players.remove(serverplayer);
            ServerLevel.this.updateSleepingPlayerList();
         }

         if (p_143375_ instanceof Mob mob) {
            if (ServerLevel.this.isUpdatingNavigations) {
               String s = "onTrackingStart called during navigation iteration";
               Util.logAndPauseIfInIde("onTrackingStart called during navigation iteration", new IllegalStateException("onTrackingStart called during navigation iteration"));
            }

            ServerLevel.this.navigatingMobs.remove(mob);
         }

         if (p_143375_.isMultipartEntity()) {
            for(net.minecraftforge.entity.PartEntity<?> enderdragonpart : p_143375_.getParts()) {
               ServerLevel.this.dragonParts.remove(enderdragonpart.getId());
            }
         }

         p_143375_.updateDynamicGameEventListener(DynamicGameEventListener::remove);

         p_143375_.onRemovedFromWorld();
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityLeaveLevelEvent(p_143375_, ServerLevel.this));
      }

      public void onSectionChange(Entity p_215086_) {
         p_215086_.updateDynamicGameEventListener(DynamicGameEventListener::move);
      }
   }

   @Override
   public java.util.Collection<net.minecraftforge.entity.PartEntity<?>> getPartEntities() {
      return this.dragonParts.values();
   }
}
