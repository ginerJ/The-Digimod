package net.minecraft.world.level.levelgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

public class PatrolSpawner implements CustomSpawner {
   private int nextTick;

   public int tick(ServerLevel p_64570_, boolean p_64571_, boolean p_64572_) {
      if (!p_64571_) {
         return 0;
      } else if (!p_64570_.getGameRules().getBoolean(GameRules.RULE_DO_PATROL_SPAWNING)) {
         return 0;
      } else {
         RandomSource randomsource = p_64570_.random;
         --this.nextTick;
         if (this.nextTick > 0) {
            return 0;
         } else {
            this.nextTick += 12000 + randomsource.nextInt(1200);
            long i = p_64570_.getDayTime() / 24000L;
            if (i >= 5L && p_64570_.isDay()) {
               if (randomsource.nextInt(5) != 0) {
                  return 0;
               } else {
                  int j = p_64570_.players().size();
                  if (j < 1) {
                     return 0;
                  } else {
                     Player player = p_64570_.players().get(randomsource.nextInt(j));
                     if (player.isSpectator()) {
                        return 0;
                     } else if (p_64570_.isCloseToVillage(player.blockPosition(), 2)) {
                        return 0;
                     } else {
                        int k = (24 + randomsource.nextInt(24)) * (randomsource.nextBoolean() ? -1 : 1);
                        int l = (24 + randomsource.nextInt(24)) * (randomsource.nextBoolean() ? -1 : 1);
                        BlockPos.MutableBlockPos blockpos$mutableblockpos = player.blockPosition().mutable().move(k, 0, l);
                        int i1 = 10;
                        if (!p_64570_.hasChunksAt(blockpos$mutableblockpos.getX() - 10, blockpos$mutableblockpos.getZ() - 10, blockpos$mutableblockpos.getX() + 10, blockpos$mutableblockpos.getZ() + 10)) {
                           return 0;
                        } else {
                           Holder<Biome> holder = p_64570_.getBiome(blockpos$mutableblockpos);
                           if (holder.is(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
                              return 0;
                           } else {
                              int j1 = 0;
                              int k1 = (int)Math.ceil((double)p_64570_.getCurrentDifficultyAt(blockpos$mutableblockpos).getEffectiveDifficulty()) + 1;

                              for(int l1 = 0; l1 < k1; ++l1) {
                                 ++j1;
                                 blockpos$mutableblockpos.setY(p_64570_.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutableblockpos).getY());
                                 if (l1 == 0) {
                                    if (!this.spawnPatrolMember(p_64570_, blockpos$mutableblockpos, randomsource, true)) {
                                       break;
                                    }
                                 } else {
                                    this.spawnPatrolMember(p_64570_, blockpos$mutableblockpos, randomsource, false);
                                 }

                                 blockpos$mutableblockpos.setX(blockpos$mutableblockpos.getX() + randomsource.nextInt(5) - randomsource.nextInt(5));
                                 blockpos$mutableblockpos.setZ(blockpos$mutableblockpos.getZ() + randomsource.nextInt(5) - randomsource.nextInt(5));
                              }

                              return j1;
                           }
                        }
                     }
                  }
               }
            } else {
               return 0;
            }
         }
      }
   }

   private boolean spawnPatrolMember(ServerLevel p_224533_, BlockPos p_224534_, RandomSource p_224535_, boolean p_224536_) {
      BlockState blockstate = p_224533_.getBlockState(p_224534_);
      if (!NaturalSpawner.isValidEmptySpawnBlock(p_224533_, p_224534_, blockstate, blockstate.getFluidState(), EntityType.PILLAGER)) {
         return false;
      } else if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(EntityType.PILLAGER, p_224533_, MobSpawnType.PATROL, p_224534_, p_224535_)) {
         return false;
      } else {
         PatrollingMonster patrollingmonster = EntityType.PILLAGER.create(p_224533_);
         if (patrollingmonster != null) {
            if (p_224536_) {
               patrollingmonster.setPatrolLeader(true);
               patrollingmonster.findPatrolTarget();
            }

            patrollingmonster.setPos((double)p_224534_.getX(), (double)p_224534_.getY(), (double)p_224534_.getZ());
            patrollingmonster.finalizeSpawn(p_224533_, p_224533_.getCurrentDifficultyAt(p_224534_), MobSpawnType.PATROL, (SpawnGroupData)null, (CompoundTag)null);
            p_224533_.addFreshEntityWithPassengers(patrollingmonster);
            return true;
         } else {
            return false;
         }
      }
   }
}