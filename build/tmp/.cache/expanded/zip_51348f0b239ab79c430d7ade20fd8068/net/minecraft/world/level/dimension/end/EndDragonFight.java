package net.minecraft.world.level.dimension.end;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class EndDragonFight {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_TICKS_BEFORE_DRAGON_RESPAWN = 1200;
   private static final int TIME_BETWEEN_CRYSTAL_SCANS = 100;
   public static final int TIME_BETWEEN_PLAYER_SCANS = 20;
   private static final int ARENA_SIZE_CHUNKS = 8;
   public static final int ARENA_TICKET_LEVEL = 9;
   private static final int GATEWAY_COUNT = 20;
   private static final int GATEWAY_DISTANCE = 96;
   public static final int DRAGON_SPAWN_Y = 128;
   private final Predicate<Entity> validPlayer;
   private final ServerBossEvent dragonEvent = (ServerBossEvent)(new ServerBossEvent(Component.translatable("entity.minecraft.ender_dragon"), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS)).setPlayBossMusic(true).setCreateWorldFog(true);
   private final ServerLevel level;
   private final BlockPos origin;
   private final ObjectArrayList<Integer> gateways = new ObjectArrayList<>();
   private final BlockPattern exitPortalPattern;
   private int ticksSinceDragonSeen;
   private int crystalsAlive;
   private int ticksSinceCrystalsScanned;
   private int ticksSinceLastPlayerScan = 21;
   private boolean dragonKilled;
   private boolean previouslyKilled;
   private boolean skipArenaLoadedCheck = false;
   @Nullable
   private UUID dragonUUID;
   private boolean needsStateScanning = true;
   @Nullable
   private BlockPos portalLocation;
   @Nullable
   private DragonRespawnAnimation respawnStage;
   private int respawnTime;
   @Nullable
   private List<EndCrystal> respawnCrystals;

   public EndDragonFight(ServerLevel p_289759_, long p_289805_, EndDragonFight.Data p_289800_) {
      this(p_289759_, p_289805_, p_289800_, BlockPos.ZERO);
   }

   public EndDragonFight(ServerLevel p_289771_, long p_289793_, EndDragonFight.Data p_289768_, BlockPos p_289794_) {
      this.level = p_289771_;
      this.origin = p_289794_;
      this.validPlayer = EntitySelector.ENTITY_STILL_ALIVE.and(EntitySelector.withinDistance((double)p_289794_.getX(), (double)(128 + p_289794_.getY()), (double)p_289794_.getZ(), 192.0D));
      this.needsStateScanning = p_289768_.needsStateScanning;
      this.dragonUUID = p_289768_.dragonUUID.orElse((UUID)null);
      this.dragonKilled = p_289768_.dragonKilled;
      this.previouslyKilled = p_289768_.previouslyKilled;
      if (p_289768_.isRespawning) {
         this.respawnStage = DragonRespawnAnimation.START;
      }

      this.portalLocation = p_289768_.exitPortalLocation.orElse((BlockPos)null);
      this.gateways.addAll(p_289768_.gateways.orElseGet(() -> {
         ObjectArrayList<Integer> objectarraylist = new ObjectArrayList<>(ContiguousSet.create(Range.closedOpen(0, 20), DiscreteDomain.integers()));
         Util.shuffle(objectarraylist, RandomSource.create(p_289793_));
         return objectarraylist;
      }));
      this.exitPortalPattern = BlockPatternBuilder.start().aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ").aisle("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ").where('#', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.BEDROCK))).build();
   }

   /** @deprecated */
   @Deprecated
   @VisibleForTesting
   public void skipArenaLoadedCheck() {
      this.skipArenaLoadedCheck = true;
   }

   public EndDragonFight.Data saveData() {
      return new EndDragonFight.Data(this.needsStateScanning, this.dragonKilled, this.previouslyKilled, false, Optional.ofNullable(this.dragonUUID), Optional.ofNullable(this.portalLocation), Optional.of(this.gateways));
   }

   public void tick() {
      this.dragonEvent.setVisible(!this.dragonKilled);
      if (++this.ticksSinceLastPlayerScan >= 20) {
         this.updatePlayers();
         this.ticksSinceLastPlayerScan = 0;
      }

      if (!this.dragonEvent.getPlayers().isEmpty()) {
         this.level.getChunkSource().addRegionTicket(TicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
         boolean flag = this.isArenaLoaded();
         if (this.needsStateScanning && flag) {
            this.scanState();
            this.needsStateScanning = false;
         }

         if (this.respawnStage != null) {
            if (this.respawnCrystals == null && flag) {
               this.respawnStage = null;
               this.tryRespawn();
            }

            this.respawnStage.tick(this.level, this, this.respawnCrystals, this.respawnTime++, this.portalLocation);
         }

         if (!this.dragonKilled) {
            if ((this.dragonUUID == null || ++this.ticksSinceDragonSeen >= 1200) && flag) {
               this.findOrCreateDragon();
               this.ticksSinceDragonSeen = 0;
            }

            if (++this.ticksSinceCrystalsScanned >= 100 && flag) {
               this.updateCrystalCount();
               this.ticksSinceCrystalsScanned = 0;
            }
         }
      } else {
         this.level.getChunkSource().removeRegionTicket(TicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
      }

   }

   private void scanState() {
      LOGGER.info("Scanning for legacy world dragon fight...");
      boolean flag = this.hasActiveExitPortal();
      if (flag) {
         LOGGER.info("Found that the dragon has been killed in this world already.");
         this.previouslyKilled = true;
      } else {
         LOGGER.info("Found that the dragon has not yet been killed in this world.");
         this.previouslyKilled = false;
         if (this.findExitPortal() == null) {
            this.spawnExitPortal(false);
         }
      }

      List<? extends EnderDragon> list = this.level.getDragons();
      if (list.isEmpty()) {
         this.dragonKilled = true;
      } else {
         EnderDragon enderdragon = list.get(0);
         this.dragonUUID = enderdragon.getUUID();
         LOGGER.info("Found that there's a dragon still alive ({})", (Object)enderdragon);
         this.dragonKilled = false;
         if (!flag) {
            LOGGER.info("But we didn't have a portal, let's remove it.");
            enderdragon.discard();
            this.dragonUUID = null;
         }
      }

      if (!this.previouslyKilled && this.dragonKilled) {
         this.dragonKilled = false;
      }

   }

   private void findOrCreateDragon() {
      List<? extends EnderDragon> list = this.level.getDragons();
      if (list.isEmpty()) {
         LOGGER.debug("Haven't seen the dragon, respawning it");
         this.createNewDragon();
      } else {
         LOGGER.debug("Haven't seen our dragon, but found another one to use.");
         this.dragonUUID = list.get(0).getUUID();
      }

   }

   protected void setRespawnStage(DragonRespawnAnimation p_64088_) {
      if (this.respawnStage == null) {
         throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
      } else {
         this.respawnTime = 0;
         if (p_64088_ == DragonRespawnAnimation.END) {
            this.respawnStage = null;
            this.dragonKilled = false;
            EnderDragon enderdragon = this.createNewDragon();
            if (enderdragon != null) {
               for(ServerPlayer serverplayer : this.dragonEvent.getPlayers()) {
                  CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayer, enderdragon);
               }
            }
         } else {
            this.respawnStage = p_64088_;
         }

      }
   }

   private boolean hasActiveExitPortal() {
      for(int i = -8; i <= 8; ++i) {
         for(int j = -8; j <= 8; ++j) {
            LevelChunk levelchunk = this.level.getChunk(i, j);

            for(BlockEntity blockentity : levelchunk.getBlockEntities().values()) {
               if (blockentity instanceof TheEndPortalBlockEntity) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Nullable
   private BlockPattern.BlockPatternMatch findExitPortal() {
      ChunkPos chunkpos = new ChunkPos(this.origin);

      for(int i = -8 + chunkpos.x; i <= 8 + chunkpos.x; ++i) {
         for(int j = -8 + chunkpos.z; j <= 8 + chunkpos.z; ++j) {
            LevelChunk levelchunk = this.level.getChunk(i, j);

            for(BlockEntity blockentity : levelchunk.getBlockEntities().values()) {
               if (blockentity instanceof TheEndPortalBlockEntity) {
                  BlockPattern.BlockPatternMatch blockpattern$blockpatternmatch = this.exitPortalPattern.find(this.level, blockentity.getBlockPos());
                  if (blockpattern$blockpatternmatch != null) {
                     BlockPos blockpos = blockpattern$blockpatternmatch.getBlock(3, 3, 3).getPos();
                     if (this.portalLocation == null) {
                        this.portalLocation = blockpos;
                     }

                     return blockpattern$blockpatternmatch;
                  }
               }
            }
         }
      }

      BlockPos blockpos1 = EndPodiumFeature.getLocation(this.origin);
      int k = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockpos1).getY();

      for(int l = k; l >= this.level.getMinBuildHeight(); --l) {
         BlockPattern.BlockPatternMatch blockpattern$blockpatternmatch1 = this.exitPortalPattern.find(this.level, new BlockPos(blockpos1.getX(), l, blockpos1.getZ()));
         if (blockpattern$blockpatternmatch1 != null) {
            if (this.portalLocation == null) {
               this.portalLocation = blockpattern$blockpatternmatch1.getBlock(3, 3, 3).getPos();
            }

            return blockpattern$blockpatternmatch1;
         }
      }

      return null;
   }

   private boolean isArenaLoaded() {
      if (this.skipArenaLoadedCheck) {
         return true;
      } else {
         ChunkPos chunkpos = new ChunkPos(this.origin);

         for(int i = -8 + chunkpos.x; i <= 8 + chunkpos.x; ++i) {
            for(int j = 8 + chunkpos.z; j <= 8 + chunkpos.z; ++j) {
               ChunkAccess chunkaccess = this.level.getChunk(i, j, ChunkStatus.FULL, false);
               if (!(chunkaccess instanceof LevelChunk)) {
                  return false;
               }

               FullChunkStatus fullchunkstatus = ((LevelChunk)chunkaccess).getFullStatus();
               if (!fullchunkstatus.isOrAfter(FullChunkStatus.BLOCK_TICKING)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private void updatePlayers() {
      Set<ServerPlayer> set = Sets.newHashSet();

      for(ServerPlayer serverplayer : this.level.getPlayers(this.validPlayer)) {
         this.dragonEvent.addPlayer(serverplayer);
         set.add(serverplayer);
      }

      Set<ServerPlayer> set1 = Sets.newHashSet(this.dragonEvent.getPlayers());
      set1.removeAll(set);

      for(ServerPlayer serverplayer1 : set1) {
         this.dragonEvent.removePlayer(serverplayer1);
      }

   }

   private void updateCrystalCount() {
      this.ticksSinceCrystalsScanned = 0;
      this.crystalsAlive = 0;

      for(SpikeFeature.EndSpike spikefeature$endspike : SpikeFeature.getSpikesForLevel(this.level)) {
         this.crystalsAlive += this.level.getEntitiesOfClass(EndCrystal.class, spikefeature$endspike.getTopBoundingBox()).size();
      }

      LOGGER.debug("Found {} end crystals still alive", (int)this.crystalsAlive);
   }

   public void setDragonKilled(EnderDragon p_64086_) {
      if (p_64086_.getUUID().equals(this.dragonUUID)) {
         this.dragonEvent.setProgress(0.0F);
         this.dragonEvent.setVisible(false);
         this.spawnExitPortal(true);
         this.spawnNewGateway();
         if (!this.previouslyKilled) {
            this.level.setBlockAndUpdate(this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.getLocation(this.origin)), Blocks.DRAGON_EGG.defaultBlockState());
         }

         this.previouslyKilled = true;
         this.dragonKilled = true;
      }

   }

   /** @deprecated */
   @Deprecated
   @VisibleForTesting
   public void removeAllGateways() {
      this.gateways.clear();
   }

   private void spawnNewGateway() {
      if (!this.gateways.isEmpty()) {
         int i = this.gateways.remove(this.gateways.size() - 1);
         int j = Mth.floor(96.0D * Math.cos(2.0D * (-Math.PI + 0.15707963267948966D * (double)i)));
         int k = Mth.floor(96.0D * Math.sin(2.0D * (-Math.PI + 0.15707963267948966D * (double)i)));
         this.spawnNewGateway(new BlockPos(j, 75, k));
      }
   }

   private void spawnNewGateway(BlockPos p_64090_) {
      this.level.levelEvent(3000, p_64090_, 0);
      this.level.registryAccess().registry(Registries.CONFIGURED_FEATURE).flatMap((p_258993_) -> {
         return p_258993_.getHolder(EndFeatures.END_GATEWAY_DELAYED);
      }).ifPresent((p_256486_) -> {
         p_256486_.value().place(this.level, this.level.getChunkSource().getGenerator(), RandomSource.create(), p_64090_);
      });
   }

   private void spawnExitPortal(boolean p_64094_) {
      EndPodiumFeature endpodiumfeature = new EndPodiumFeature(p_64094_);
      if (this.portalLocation == null) {
         for(this.portalLocation = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.getLocation(this.origin)).below(); this.level.getBlockState(this.portalLocation).is(Blocks.BEDROCK) && this.portalLocation.getY() > this.level.getSeaLevel(); this.portalLocation = this.portalLocation.below()) {
         }
      }

      endpodiumfeature.place(FeatureConfiguration.NONE, this.level, this.level.getChunkSource().getGenerator(), RandomSource.create(), this.portalLocation);
   }

   @Nullable
   private EnderDragon createNewDragon() {
      this.level.getChunkAt(new BlockPos(this.origin.getX(), 128 + this.origin.getY(), this.origin.getZ()));
      EnderDragon enderdragon = EntityType.ENDER_DRAGON.create(this.level);
      if (enderdragon != null) {
         enderdragon.setDragonFight(this);
         enderdragon.setFightOrigin(this.origin);
         enderdragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
         enderdragon.moveTo((double)this.origin.getX(), (double)(128 + this.origin.getY()), (double)this.origin.getZ(), this.level.random.nextFloat() * 360.0F, 0.0F);
         this.level.addFreshEntity(enderdragon);
         this.dragonUUID = enderdragon.getUUID();
      }

      return enderdragon;
   }

   public void updateDragon(EnderDragon p_64097_) {
      if (p_64097_.getUUID().equals(this.dragonUUID)) {
         this.dragonEvent.setProgress(p_64097_.getHealth() / p_64097_.getMaxHealth());
         this.ticksSinceDragonSeen = 0;
         if (p_64097_.hasCustomName()) {
            this.dragonEvent.setName(p_64097_.getDisplayName());
         }
      }

   }

   public int getCrystalsAlive() {
      return this.crystalsAlive;
   }

   public void onCrystalDestroyed(EndCrystal p_64083_, DamageSource p_64084_) {
      if (this.respawnStage != null && this.respawnCrystals.contains(p_64083_)) {
         LOGGER.debug("Aborting respawn sequence");
         this.respawnStage = null;
         this.respawnTime = 0;
         this.resetSpikeCrystals();
         this.spawnExitPortal(true);
      } else {
         this.updateCrystalCount();
         Entity entity = this.level.getEntity(this.dragonUUID);
         if (entity instanceof EnderDragon) {
            ((EnderDragon)entity).onCrystalDestroyed(p_64083_, p_64083_.blockPosition(), p_64084_);
         }
      }

   }

   public boolean hasPreviouslyKilledDragon() {
      return this.previouslyKilled;
   }

   public void tryRespawn() {
      if (this.dragonKilled && this.respawnStage == null) {
         BlockPos blockpos = this.portalLocation;
         if (blockpos == null) {
            LOGGER.debug("Tried to respawn, but need to find the portal first.");
            BlockPattern.BlockPatternMatch blockpattern$blockpatternmatch = this.findExitPortal();
            if (blockpattern$blockpatternmatch == null) {
               LOGGER.debug("Couldn't find a portal, so we made one.");
               this.spawnExitPortal(true);
            } else {
               LOGGER.debug("Found the exit portal & saved its location for next time.");
            }

            blockpos = this.portalLocation;
         }

         List<EndCrystal> list1 = Lists.newArrayList();
         BlockPos blockpos1 = blockpos.above(1);

         for(Direction direction : Direction.Plane.HORIZONTAL) {
            List<EndCrystal> list = this.level.getEntitiesOfClass(EndCrystal.class, new AABB(blockpos1.relative(direction, 2)));
            if (list.isEmpty()) {
               return;
            }

            list1.addAll(list);
         }

         LOGGER.debug("Found all crystals, respawning dragon.");
         this.respawnDragon(list1);
      }

   }

   private void respawnDragon(List<EndCrystal> p_64092_) {
      if (this.dragonKilled && this.respawnStage == null) {
         for(BlockPattern.BlockPatternMatch blockpattern$blockpatternmatch = this.findExitPortal(); blockpattern$blockpatternmatch != null; blockpattern$blockpatternmatch = this.findExitPortal()) {
            for(int i = 0; i < this.exitPortalPattern.getWidth(); ++i) {
               for(int j = 0; j < this.exitPortalPattern.getHeight(); ++j) {
                  for(int k = 0; k < this.exitPortalPattern.getDepth(); ++k) {
                     BlockInWorld blockinworld = blockpattern$blockpatternmatch.getBlock(i, j, k);
                     if (blockinworld.getState().is(Blocks.BEDROCK) || blockinworld.getState().is(Blocks.END_PORTAL)) {
                        this.level.setBlockAndUpdate(blockinworld.getPos(), Blocks.END_STONE.defaultBlockState());
                     }
                  }
               }
            }
         }

         this.respawnStage = DragonRespawnAnimation.START;
         this.respawnTime = 0;
         this.spawnExitPortal(false);
         this.respawnCrystals = p_64092_;
      }

   }

   public void resetSpikeCrystals() {
      for(SpikeFeature.EndSpike spikefeature$endspike : SpikeFeature.getSpikesForLevel(this.level)) {
         for(EndCrystal endcrystal : this.level.getEntitiesOfClass(EndCrystal.class, spikefeature$endspike.getTopBoundingBox())) {
            endcrystal.setInvulnerable(false);
            endcrystal.setBeamTarget((BlockPos)null);
         }
      }
   }

   public void addPlayer(ServerPlayer player) {
      this.dragonEvent.addPlayer(player);
   }

   public void removePlayer(ServerPlayer player) {
      this.dragonEvent.removePlayer(player);
   }

   @Nullable
   public UUID getDragonUUID() {
      return this.dragonUUID;
   }

   public static record Data(boolean needsStateScanning, boolean dragonKilled, boolean previouslyKilled, boolean isRespawning, Optional<UUID> dragonUUID, Optional<BlockPos> exitPortalLocation, Optional<List<Integer>> gateways) {
      public static final Codec<EndDragonFight.Data> CODEC = RecordCodecBuilder.create((p_289803_) -> {
         return p_289803_.group(Codec.BOOL.fieldOf("NeedsStateScanning").orElse(true).forGetter(EndDragonFight.Data::needsStateScanning), Codec.BOOL.fieldOf("DragonKilled").orElse(false).forGetter(EndDragonFight.Data::dragonKilled), Codec.BOOL.fieldOf("PreviouslyKilled").orElse(false).forGetter(EndDragonFight.Data::previouslyKilled), Codec.BOOL.optionalFieldOf("IsRespawning", Boolean.valueOf(false)).forGetter(EndDragonFight.Data::isRespawning), UUIDUtil.CODEC.optionalFieldOf("Dragon").forGetter(EndDragonFight.Data::dragonUUID), BlockPos.CODEC.optionalFieldOf("ExitPortalLocation").forGetter(EndDragonFight.Data::exitPortalLocation), Codec.list(Codec.INT).optionalFieldOf("Gateways").forGetter(EndDragonFight.Data::gateways)).apply(p_289803_, EndDragonFight.Data::new);
      });
      public static final EndDragonFight.Data DEFAULT = new EndDragonFight.Data(true, false, false, false, Optional.empty(), Optional.empty(), Optional.empty());
   }
}
