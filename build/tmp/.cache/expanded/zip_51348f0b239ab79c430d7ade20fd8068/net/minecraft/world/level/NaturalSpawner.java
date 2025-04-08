package net.minecraft.world.level;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

// TODO: ForgeHooks.canEntitySpawn
public final class NaturalSpawner {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MIN_SPAWN_DISTANCE = 24;
   public static final int SPAWN_DISTANCE_CHUNK = 8;
   public static final int SPAWN_DISTANCE_BLOCK = 128;
   static final int MAGIC_NUMBER = (int)Math.pow(17.0D, 2.0D);
   private static final MobCategory[] SPAWNING_CATEGORIES = Stream.of(MobCategory.values()).filter((p_47037_) -> {
      return p_47037_ != MobCategory.MISC;
   }).toArray((p_46983_) -> {
      return new MobCategory[p_46983_];
   });

   private NaturalSpawner() {
   }

   public static NaturalSpawner.SpawnState createState(int p_186525_, Iterable<Entity> p_186526_, NaturalSpawner.ChunkGetter p_186527_, LocalMobCapCalculator p_186528_) {
      PotentialCalculator potentialcalculator = new PotentialCalculator();
      Object2IntOpenHashMap<MobCategory> object2intopenhashmap = new Object2IntOpenHashMap<>();

      for(Entity entity : p_186526_) {
         if (entity instanceof Mob mob) {
            if (mob.isPersistenceRequired() || mob.requiresCustomPersistence()) {
               continue;
            }
         }

         MobCategory mobcategory = entity.getClassification(true);
         if (mobcategory != MobCategory.MISC) {
            BlockPos blockpos = entity.blockPosition();
            p_186527_.query(ChunkPos.asLong(blockpos), (p_275163_) -> {
               MobSpawnSettings.MobSpawnCost mobspawnsettings$mobspawncost = getRoughBiome(blockpos, p_275163_).getMobSettings().getMobSpawnCost(entity.getType());
               if (mobspawnsettings$mobspawncost != null) {
                  potentialcalculator.addCharge(entity.blockPosition(), mobspawnsettings$mobspawncost.charge());
               }

               if (entity instanceof Mob) {
                  p_186528_.addMob(p_275163_.getPos(), mobcategory);
               }

               object2intopenhashmap.addTo(mobcategory, 1);
            });
         }
      }

      return new NaturalSpawner.SpawnState(p_186525_, object2intopenhashmap, potentialcalculator, p_186528_);
   }

   static Biome getRoughBiome(BlockPos p_47096_, ChunkAccess p_47097_) {
      return p_47097_.getNoiseBiome(QuartPos.fromBlock(p_47096_.getX()), QuartPos.fromBlock(p_47096_.getY()), QuartPos.fromBlock(p_47096_.getZ())).value();
   }

   public static void spawnForChunk(ServerLevel p_47030_, LevelChunk p_47031_, NaturalSpawner.SpawnState p_47032_, boolean p_47033_, boolean p_47034_, boolean p_47035_) {
      p_47030_.getProfiler().push("spawner");

      for(MobCategory mobcategory : SPAWNING_CATEGORIES) {
         if ((p_47033_ || !mobcategory.isFriendly()) && (p_47034_ || mobcategory.isFriendly()) && (p_47035_ || !mobcategory.isPersistent()) && p_47032_.canSpawnForCategory(mobcategory, p_47031_.getPos())) {
            spawnCategoryForChunk(mobcategory, p_47030_, p_47031_, p_47032_::canSpawn, p_47032_::afterSpawn);
         }
      }

      p_47030_.getProfiler().pop();
   }

   public static void spawnCategoryForChunk(MobCategory p_47046_, ServerLevel p_47047_, LevelChunk p_47048_, NaturalSpawner.SpawnPredicate p_47049_, NaturalSpawner.AfterSpawnCallback p_47050_) {
      BlockPos blockpos = getRandomPosWithin(p_47047_, p_47048_);
      if (blockpos.getY() >= p_47047_.getMinBuildHeight() + 1) {
         spawnCategoryForPosition(p_47046_, p_47047_, p_47048_, blockpos, p_47049_, p_47050_);
      }
   }

   @VisibleForDebug
   public static void spawnCategoryForPosition(MobCategory p_151613_, ServerLevel p_151614_, BlockPos p_151615_) {
      spawnCategoryForPosition(p_151613_, p_151614_, p_151614_.getChunk(p_151615_), p_151615_, (p_151606_, p_151607_, p_151608_) -> {
         return true;
      }, (p_151610_, p_151611_) -> {
      });
   }

   public static void spawnCategoryForPosition(MobCategory p_47039_, ServerLevel p_47040_, ChunkAccess p_47041_, BlockPos p_47042_, NaturalSpawner.SpawnPredicate p_47043_, NaturalSpawner.AfterSpawnCallback p_47044_) {
      StructureManager structuremanager = p_47040_.structureManager();
      ChunkGenerator chunkgenerator = p_47040_.getChunkSource().getGenerator();
      int i = p_47042_.getY();
      BlockState blockstate = p_47041_.getBlockState(p_47042_);
      if (!blockstate.isRedstoneConductor(p_47041_, p_47042_)) {
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
         int j = 0;

         for(int k = 0; k < 3; ++k) {
            int l = p_47042_.getX();
            int i1 = p_47042_.getZ();
            int j1 = 6;
            MobSpawnSettings.SpawnerData mobspawnsettings$spawnerdata = null;
            SpawnGroupData spawngroupdata = null;
            int k1 = Mth.ceil(p_47040_.random.nextFloat() * 4.0F);
            int l1 = 0;

            for(int i2 = 0; i2 < k1; ++i2) {
               l += p_47040_.random.nextInt(6) - p_47040_.random.nextInt(6);
               i1 += p_47040_.random.nextInt(6) - p_47040_.random.nextInt(6);
               blockpos$mutableblockpos.set(l, i, i1);
               double d0 = (double)l + 0.5D;
               double d1 = (double)i1 + 0.5D;
               Player player = p_47040_.getNearestPlayer(d0, (double)i, d1, -1.0D, false);
               if (player != null) {
                  double d2 = player.distanceToSqr(d0, (double)i, d1);
                  if (isRightDistanceToPlayerAndSpawnPoint(p_47040_, p_47041_, blockpos$mutableblockpos, d2)) {
                     if (mobspawnsettings$spawnerdata == null) {
                        Optional<MobSpawnSettings.SpawnerData> optional = getRandomSpawnMobAt(p_47040_, structuremanager, chunkgenerator, p_47039_, p_47040_.random, blockpos$mutableblockpos);
                        if (optional.isEmpty()) {
                           break;
                        }

                        mobspawnsettings$spawnerdata = optional.get();
                        k1 = mobspawnsettings$spawnerdata.minCount + p_47040_.random.nextInt(1 + mobspawnsettings$spawnerdata.maxCount - mobspawnsettings$spawnerdata.minCount);
                     }

                     if (isValidSpawnPostitionForType(p_47040_, p_47039_, structuremanager, chunkgenerator, mobspawnsettings$spawnerdata, blockpos$mutableblockpos, d2) && p_47043_.test(mobspawnsettings$spawnerdata.type, blockpos$mutableblockpos, p_47041_)) {
                        Mob mob = getMobForSpawn(p_47040_, mobspawnsettings$spawnerdata.type);
                        if (mob == null) {
                           return;
                        }

                        mob.moveTo(d0, (double)i, d1, p_47040_.random.nextFloat() * 360.0F, 0.0F);
                        if (isValidPositionForMob(p_47040_, mob, d2)) {
                           spawngroupdata = mob.finalizeSpawn(p_47040_, p_47040_.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.NATURAL, spawngroupdata, (CompoundTag)null);
                           ++j;
                           ++l1;
                           p_47040_.addFreshEntityWithPassengers(mob);
                           p_47044_.run(mob, p_47041_);
                           if (j >= net.minecraftforge.event.ForgeEventFactory.getMaxSpawnPackSize(mob)) {
                              return;
                           }

                           if (mob.isMaxGroupSizeReached(l1)) {
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private static boolean isRightDistanceToPlayerAndSpawnPoint(ServerLevel p_47025_, ChunkAccess p_47026_, BlockPos.MutableBlockPos p_47027_, double p_47028_) {
      if (p_47028_ <= 576.0D) {
         return false;
      } else if (p_47025_.getSharedSpawnPos().closerToCenterThan(new Vec3((double)p_47027_.getX() + 0.5D, (double)p_47027_.getY(), (double)p_47027_.getZ() + 0.5D), 24.0D)) {
         return false;
      } else {
         return Objects.equals(new ChunkPos(p_47027_), p_47026_.getPos()) || p_47025_.isNaturalSpawningAllowed(p_47027_);
      }
   }

   private static boolean isValidSpawnPostitionForType(ServerLevel p_220422_, MobCategory p_220423_, StructureManager p_220424_, ChunkGenerator p_220425_, MobSpawnSettings.SpawnerData p_220426_, BlockPos.MutableBlockPos p_220427_, double p_220428_) {
      EntityType<?> entitytype = p_220426_.type;
      if (entitytype.getCategory() == MobCategory.MISC) {
         return false;
      } else if (!entitytype.canSpawnFarFromPlayer() && p_220428_ > (double)(entitytype.getCategory().getDespawnDistance() * entitytype.getCategory().getDespawnDistance())) {
         return false;
      } else if (entitytype.canSummon() && canSpawnMobAt(p_220422_, p_220424_, p_220425_, p_220423_, p_220426_, p_220427_)) {
         SpawnPlacements.Type spawnplacements$type = SpawnPlacements.getPlacementType(entitytype);
         if (!isSpawnPositionOk(spawnplacements$type, p_220422_, p_220427_, entitytype)) {
            return false;
         } else if (!SpawnPlacements.checkSpawnRules(entitytype, p_220422_, MobSpawnType.NATURAL, p_220427_, p_220422_.random)) {
            return false;
         } else {
            return p_220422_.noCollision(entitytype.getAABB((double)p_220427_.getX() + 0.5D, (double)p_220427_.getY(), (double)p_220427_.getZ() + 0.5D));
         }
      } else {
         return false;
      }
   }

   @Nullable
   private static Mob getMobForSpawn(ServerLevel p_46989_, EntityType<?> p_46990_) {
      try {
         Entity entity = p_46990_.create(p_46989_);
         if (entity instanceof Mob) {
            return (Mob)entity;
         }

         LOGGER.warn("Can't spawn entity of type: {}", (Object)BuiltInRegistries.ENTITY_TYPE.getKey(p_46990_));
      } catch (Exception exception) {
         LOGGER.warn("Failed to create mob", (Throwable)exception);
      }

      return null;
   }

   private static boolean isValidPositionForMob(ServerLevel p_46992_, Mob p_46993_, double p_46994_) {
      if (p_46994_ > (double)(p_46993_.getType().getCategory().getDespawnDistance() * p_46993_.getType().getCategory().getDespawnDistance()) && p_46993_.removeWhenFarAway(p_46994_)) {
         return false;
      } else {
         return net.minecraftforge.event.ForgeEventFactory.checkSpawnPosition(p_46993_, p_46992_, MobSpawnType.NATURAL);
      }
   }

   private static Optional<MobSpawnSettings.SpawnerData> getRandomSpawnMobAt(ServerLevel p_220430_, StructureManager p_220431_, ChunkGenerator p_220432_, MobCategory p_220433_, RandomSource p_220434_, BlockPos p_220435_) {
      Holder<Biome> holder = p_220430_.getBiome(p_220435_);
      return p_220433_ == MobCategory.WATER_AMBIENT && holder.is(BiomeTags.REDUCED_WATER_AMBIENT_SPAWNS) && p_220434_.nextFloat() < 0.98F ? Optional.empty() : mobsAt(p_220430_, p_220431_, p_220432_, p_220433_, p_220435_, holder).getRandom(p_220434_);
   }

   private static boolean canSpawnMobAt(ServerLevel p_220437_, StructureManager p_220438_, ChunkGenerator p_220439_, MobCategory p_220440_, MobSpawnSettings.SpawnerData p_220441_, BlockPos p_220442_) {
      return mobsAt(p_220437_, p_220438_, p_220439_, p_220440_, p_220442_, (Holder<Biome>)null).unwrap().contains(p_220441_);
   }

   private static WeightedRandomList<MobSpawnSettings.SpawnerData> mobsAt(ServerLevel p_220444_, StructureManager p_220445_, ChunkGenerator p_220446_, MobCategory p_220447_, BlockPos p_220448_, @Nullable Holder<Biome> p_220449_) {
      // Forge: Add in potential spawns, and replace hardcoded nether fortress mob list
      return net.minecraftforge.event.ForgeEventFactory.getPotentialSpawns(p_220444_, p_220447_, p_220448_, isInNetherFortressBounds(p_220448_, p_220444_, p_220447_, p_220445_) ? p_220445_.registryAccess().registryOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.FORTRESS).spawnOverrides().get(MobCategory.MONSTER).spawns() : p_220446_.getMobsAt(p_220449_ != null ? p_220449_ : p_220444_.getBiome(p_220448_), p_220445_, p_220447_, p_220448_));
   }

   public static boolean isInNetherFortressBounds(BlockPos p_220456_, ServerLevel p_220457_, MobCategory p_220458_, StructureManager p_220459_) {
      if (p_220458_ == MobCategory.MONSTER && p_220457_.getBlockState(p_220456_.below()).is(Blocks.NETHER_BRICKS)) {
         Structure structure = p_220459_.registryAccess().registryOrThrow(Registries.STRUCTURE).get(BuiltinStructures.FORTRESS);
         return structure == null ? false : p_220459_.getStructureAt(p_220456_, structure).isValid();
      } else {
         return false;
      }
   }

   private static BlockPos getRandomPosWithin(Level p_47063_, LevelChunk p_47064_) {
      ChunkPos chunkpos = p_47064_.getPos();
      int i = chunkpos.getMinBlockX() + p_47063_.random.nextInt(16);
      int j = chunkpos.getMinBlockZ() + p_47063_.random.nextInt(16);
      int k = p_47064_.getHeight(Heightmap.Types.WORLD_SURFACE, i, j) + 1;
      int l = Mth.randomBetweenInclusive(p_47063_.random, p_47063_.getMinBuildHeight(), k);
      return new BlockPos(i, l, j);
   }

   public static boolean isValidEmptySpawnBlock(BlockGetter p_47057_, BlockPos p_47058_, BlockState p_47059_, FluidState p_47060_, EntityType<?> p_47061_) {
      if (p_47059_.isCollisionShapeFullBlock(p_47057_, p_47058_)) {
         return false;
      } else if (p_47059_.isSignalSource()) {
         return false;
      } else if (!p_47060_.isEmpty()) {
         return false;
      } else if (p_47059_.is(BlockTags.PREVENT_MOB_SPAWNING_INSIDE)) {
         return false;
      } else {
         return !p_47061_.isBlockDangerous(p_47059_);
      }
   }

   public static boolean isSpawnPositionOk(SpawnPlacements.Type p_47052_, LevelReader p_47053_, BlockPos p_47054_, @Nullable EntityType<?> p_47055_) {
      if (p_47052_ == SpawnPlacements.Type.NO_RESTRICTIONS) {
         return true;
      } else if (p_47055_ != null && p_47053_.getWorldBorder().isWithinBounds(p_47054_)) {
         return p_47052_.canSpawnAt(p_47053_, p_47054_, p_47055_);
      }
      return false;
   }

   public static boolean canSpawnAtBody(SpawnPlacements.Type p_47052_, LevelReader p_47053_, BlockPos p_47054_, @Nullable EntityType<?> p_47055_) {
      {
         BlockState blockstate = p_47053_.getBlockState(p_47054_);
         FluidState fluidstate = p_47053_.getFluidState(p_47054_);
         BlockPos blockpos = p_47054_.above();
         BlockPos blockpos1 = p_47054_.below();
         switch (p_47052_) {
            case IN_WATER:
               return fluidstate.is(FluidTags.WATER) && !p_47053_.getBlockState(blockpos).isRedstoneConductor(p_47053_, blockpos);
            case IN_LAVA:
               return fluidstate.is(FluidTags.LAVA);
            case ON_GROUND:
            default:
               BlockState blockstate1 = p_47053_.getBlockState(blockpos1);
            if (!blockstate1.isValidSpawn(p_47053_, blockpos1, p_47052_, p_47055_)) {
                  return false;
               } else {
                  return isValidEmptySpawnBlock(p_47053_, p_47054_, blockstate, fluidstate, p_47055_) && isValidEmptySpawnBlock(p_47053_, blockpos, p_47053_.getBlockState(blockpos), p_47053_.getFluidState(blockpos), p_47055_);
               }
         }
      }
   }

   public static void spawnMobsForChunkGeneration(ServerLevelAccessor p_220451_, Holder<Biome> p_220452_, ChunkPos p_220453_, RandomSource p_220454_) {
      MobSpawnSettings mobspawnsettings = p_220452_.value().getMobSettings();
      WeightedRandomList<MobSpawnSettings.SpawnerData> weightedrandomlist = mobspawnsettings.getMobs(MobCategory.CREATURE);
      if (!weightedrandomlist.isEmpty()) {
         int i = p_220453_.getMinBlockX();
         int j = p_220453_.getMinBlockZ();

         while(p_220454_.nextFloat() < mobspawnsettings.getCreatureProbability()) {
            Optional<MobSpawnSettings.SpawnerData> optional = weightedrandomlist.getRandom(p_220454_);
            if (optional.isPresent()) {
               MobSpawnSettings.SpawnerData mobspawnsettings$spawnerdata = optional.get();
               int k = mobspawnsettings$spawnerdata.minCount + p_220454_.nextInt(1 + mobspawnsettings$spawnerdata.maxCount - mobspawnsettings$spawnerdata.minCount);
               SpawnGroupData spawngroupdata = null;
               int l = i + p_220454_.nextInt(16);
               int i1 = j + p_220454_.nextInt(16);
               int j1 = l;
               int k1 = i1;

               for(int l1 = 0; l1 < k; ++l1) {
                  boolean flag = false;

                  for(int i2 = 0; !flag && i2 < 4; ++i2) {
                     BlockPos blockpos = getTopNonCollidingPos(p_220451_, mobspawnsettings$spawnerdata.type, l, i1);
                     if (mobspawnsettings$spawnerdata.type.canSummon() && isSpawnPositionOk(SpawnPlacements.getPlacementType(mobspawnsettings$spawnerdata.type), p_220451_, blockpos, mobspawnsettings$spawnerdata.type)) {
                        float f = mobspawnsettings$spawnerdata.type.getWidth();
                        double d0 = Mth.clamp((double)l, (double)i + (double)f, (double)i + 16.0D - (double)f);
                        double d1 = Mth.clamp((double)i1, (double)j + (double)f, (double)j + 16.0D - (double)f);
                        if (!p_220451_.noCollision(mobspawnsettings$spawnerdata.type.getAABB(d0, (double)blockpos.getY(), d1)) || !SpawnPlacements.checkSpawnRules(mobspawnsettings$spawnerdata.type, p_220451_, MobSpawnType.CHUNK_GENERATION, BlockPos.containing(d0, (double)blockpos.getY(), d1), p_220451_.getRandom())) {
                           continue;
                        }

                        Entity entity;
                        try {
                           entity = mobspawnsettings$spawnerdata.type.create(p_220451_.getLevel());
                        } catch (Exception exception) {
                           LOGGER.warn("Failed to create mob", (Throwable)exception);
                           continue;
                        }

                        if (entity == null) {
                           continue;
                        }

                        entity.moveTo(d0, (double)blockpos.getY(), d1, p_220454_.nextFloat() * 360.0F, 0.0F);
                        if (entity instanceof Mob) {
                           Mob mob = (Mob)entity;
                           if (net.minecraftforge.event.ForgeEventFactory.checkSpawnPosition(mob, p_220451_, MobSpawnType.CHUNK_GENERATION)) {
                              spawngroupdata = mob.finalizeSpawn(p_220451_, p_220451_.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.CHUNK_GENERATION, spawngroupdata, (CompoundTag)null);
                              p_220451_.addFreshEntityWithPassengers(mob);
                              flag = true;
                           }
                        }
                     }

                     l += p_220454_.nextInt(5) - p_220454_.nextInt(5);

                     for(i1 += p_220454_.nextInt(5) - p_220454_.nextInt(5); l < i || l >= i + 16 || i1 < j || i1 >= j + 16; i1 = k1 + p_220454_.nextInt(5) - p_220454_.nextInt(5)) {
                        l = j1 + p_220454_.nextInt(5) - p_220454_.nextInt(5);
                     }
                  }
               }
            }
         }

      }
   }

   private static BlockPos getTopNonCollidingPos(LevelReader p_47066_, EntityType<?> p_47067_, int p_47068_, int p_47069_) {
      int i = p_47066_.getHeight(SpawnPlacements.getHeightmapType(p_47067_), p_47068_, p_47069_);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_47068_, i, p_47069_);
      if (p_47066_.dimensionType().hasCeiling()) {
         do {
            blockpos$mutableblockpos.move(Direction.DOWN);
         } while(!p_47066_.getBlockState(blockpos$mutableblockpos).isAir());

         do {
            blockpos$mutableblockpos.move(Direction.DOWN);
         } while(p_47066_.getBlockState(blockpos$mutableblockpos).isAir() && blockpos$mutableblockpos.getY() > p_47066_.getMinBuildHeight());
      }

      if (SpawnPlacements.getPlacementType(p_47067_) == SpawnPlacements.Type.ON_GROUND) {
         BlockPos blockpos = blockpos$mutableblockpos.below();
         if (p_47066_.getBlockState(blockpos).isPathfindable(p_47066_, blockpos, PathComputationType.LAND)) {
            return blockpos;
         }
      }

      return blockpos$mutableblockpos.immutable();
   }

   @FunctionalInterface
   public interface AfterSpawnCallback {
      void run(Mob p_47101_, ChunkAccess p_47102_);
   }

   @FunctionalInterface
   public interface ChunkGetter {
      void query(long p_47104_, Consumer<LevelChunk> p_47105_);
   }

   @FunctionalInterface
   public interface SpawnPredicate {
      boolean test(EntityType<?> p_47107_, BlockPos p_47108_, ChunkAccess p_47109_);
   }

   public static class SpawnState {
      private final int spawnableChunkCount;
      private final Object2IntOpenHashMap<MobCategory> mobCategoryCounts;
      private final PotentialCalculator spawnPotential;
      private final Object2IntMap<MobCategory> unmodifiableMobCategoryCounts;
      private final LocalMobCapCalculator localMobCapCalculator;
      @Nullable
      private BlockPos lastCheckedPos;
      @Nullable
      private EntityType<?> lastCheckedType;
      private double lastCharge;

      SpawnState(int p_186544_, Object2IntOpenHashMap<MobCategory> p_186545_, PotentialCalculator p_186546_, LocalMobCapCalculator p_186547_) {
         this.spawnableChunkCount = p_186544_;
         this.mobCategoryCounts = p_186545_;
         this.spawnPotential = p_186546_;
         this.localMobCapCalculator = p_186547_;
         this.unmodifiableMobCategoryCounts = Object2IntMaps.unmodifiable(p_186545_);
      }

      private boolean canSpawn(EntityType<?> p_47128_, BlockPos p_47129_, ChunkAccess p_47130_) {
         this.lastCheckedPos = p_47129_;
         this.lastCheckedType = p_47128_;
         MobSpawnSettings.MobSpawnCost mobspawnsettings$mobspawncost = NaturalSpawner.getRoughBiome(p_47129_, p_47130_).getMobSettings().getMobSpawnCost(p_47128_);
         if (mobspawnsettings$mobspawncost == null) {
            this.lastCharge = 0.0D;
            return true;
         } else {
            double d0 = mobspawnsettings$mobspawncost.charge();
            this.lastCharge = d0;
            double d1 = this.spawnPotential.getPotentialEnergyChange(p_47129_, d0);
            return d1 <= mobspawnsettings$mobspawncost.energyBudget();
         }
      }

      private void afterSpawn(Mob p_47132_, ChunkAccess p_47133_) {
         EntityType<?> entitytype = p_47132_.getType();
         BlockPos blockpos = p_47132_.blockPosition();
         double d0;
         if (blockpos.equals(this.lastCheckedPos) && entitytype == this.lastCheckedType) {
            d0 = this.lastCharge;
         } else {
            MobSpawnSettings.MobSpawnCost mobspawnsettings$mobspawncost = NaturalSpawner.getRoughBiome(blockpos, p_47133_).getMobSettings().getMobSpawnCost(entitytype);
            if (mobspawnsettings$mobspawncost != null) {
               d0 = mobspawnsettings$mobspawncost.charge();
            } else {
               d0 = 0.0D;
            }
         }

         this.spawnPotential.addCharge(blockpos, d0);
         MobCategory mobcategory = entitytype.getCategory();
         this.mobCategoryCounts.addTo(mobcategory, 1);
         this.localMobCapCalculator.addMob(new ChunkPos(blockpos), mobcategory);
      }

      public int getSpawnableChunkCount() {
         return this.spawnableChunkCount;
      }

      public Object2IntMap<MobCategory> getMobCategoryCounts() {
         return this.unmodifiableMobCategoryCounts;
      }

      boolean canSpawnForCategory(MobCategory p_186549_, ChunkPos p_186550_) {
         int i = p_186549_.getMaxInstancesPerChunk() * this.spawnableChunkCount / NaturalSpawner.MAGIC_NUMBER;
         if (this.mobCategoryCounts.getInt(p_186549_) >= i) {
            return false;
         } else {
            return this.localMobCapCalculator.canSpawn(p_186549_, p_186550_);
         }
      }
   }
}
