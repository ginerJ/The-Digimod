package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class FlyNodeEvaluator extends WalkNodeEvaluator {
   private final Long2ObjectMap<BlockPathTypes> pathTypeByPosCache = new Long2ObjectOpenHashMap<>();
   private static final float SMALL_MOB_INFLATED_START_NODE_BOUNDING_BOX = 1.5F;
   private static final int MAX_START_NODE_CANDIDATES = 10;

   public void prepare(PathNavigationRegion p_77261_, Mob p_77262_) {
      super.prepare(p_77261_, p_77262_);
      this.pathTypeByPosCache.clear();
      p_77262_.onPathfindingStart();
   }

   public void done() {
      this.mob.onPathfindingDone();
      this.pathTypeByPosCache.clear();
      super.done();
   }

   public Node getStart() {
      int i;
      if (this.canFloat() && this.mob.isInWater()) {
         i = this.mob.getBlockY();
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(this.mob.getX(), (double)i, this.mob.getZ());

         for(BlockState blockstate = this.level.getBlockState(blockpos$mutableblockpos); blockstate.is(Blocks.WATER); blockstate = this.level.getBlockState(blockpos$mutableblockpos)) {
            ++i;
            blockpos$mutableblockpos.set(this.mob.getX(), (double)i, this.mob.getZ());
         }
      } else {
         i = Mth.floor(this.mob.getY() + 0.5D);
      }

      BlockPos blockpos1 = BlockPos.containing(this.mob.getX(), (double)i, this.mob.getZ());
      if (!this.canStartAt(blockpos1)) {
         for(BlockPos blockpos : this.iteratePathfindingStartNodeCandidatePositions(this.mob)) {
            if (this.canStartAt(blockpos)) {
               return super.getStartNode(blockpos);
            }
         }
      }

      return super.getStartNode(blockpos1);
   }

   protected boolean canStartAt(BlockPos p_262645_) {
      BlockPathTypes blockpathtypes = this.getBlockPathType(this.mob, p_262645_);
      return this.mob.getPathfindingMalus(blockpathtypes) >= 0.0F;
   }

   public Target getGoal(double p_77229_, double p_77230_, double p_77231_) {
      return this.getTargetFromNode(this.getNode(Mth.floor(p_77229_), Mth.floor(p_77230_), Mth.floor(p_77231_)));
   }

   public int getNeighbors(Node[] p_77266_, Node p_77267_) {
      int i = 0;
      Node node = this.findAcceptedNode(p_77267_.x, p_77267_.y, p_77267_.z + 1);
      if (this.isOpen(node)) {
         p_77266_[i++] = node;
      }

      Node node1 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y, p_77267_.z);
      if (this.isOpen(node1)) {
         p_77266_[i++] = node1;
      }

      Node node2 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y, p_77267_.z);
      if (this.isOpen(node2)) {
         p_77266_[i++] = node2;
      }

      Node node3 = this.findAcceptedNode(p_77267_.x, p_77267_.y, p_77267_.z - 1);
      if (this.isOpen(node3)) {
         p_77266_[i++] = node3;
      }

      Node node4 = this.findAcceptedNode(p_77267_.x, p_77267_.y + 1, p_77267_.z);
      if (this.isOpen(node4)) {
         p_77266_[i++] = node4;
      }

      Node node5 = this.findAcceptedNode(p_77267_.x, p_77267_.y - 1, p_77267_.z);
      if (this.isOpen(node5)) {
         p_77266_[i++] = node5;
      }

      Node node6 = this.findAcceptedNode(p_77267_.x, p_77267_.y + 1, p_77267_.z + 1);
      if (this.isOpen(node6) && this.hasMalus(node) && this.hasMalus(node4)) {
         p_77266_[i++] = node6;
      }

      Node node7 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y + 1, p_77267_.z);
      if (this.isOpen(node7) && this.hasMalus(node1) && this.hasMalus(node4)) {
         p_77266_[i++] = node7;
      }

      Node node8 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y + 1, p_77267_.z);
      if (this.isOpen(node8) && this.hasMalus(node2) && this.hasMalus(node4)) {
         p_77266_[i++] = node8;
      }

      Node node9 = this.findAcceptedNode(p_77267_.x, p_77267_.y + 1, p_77267_.z - 1);
      if (this.isOpen(node9) && this.hasMalus(node3) && this.hasMalus(node4)) {
         p_77266_[i++] = node9;
      }

      Node node10 = this.findAcceptedNode(p_77267_.x, p_77267_.y - 1, p_77267_.z + 1);
      if (this.isOpen(node10) && this.hasMalus(node) && this.hasMalus(node5)) {
         p_77266_[i++] = node10;
      }

      Node node11 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y - 1, p_77267_.z);
      if (this.isOpen(node11) && this.hasMalus(node1) && this.hasMalus(node5)) {
         p_77266_[i++] = node11;
      }

      Node node12 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y - 1, p_77267_.z);
      if (this.isOpen(node12) && this.hasMalus(node2) && this.hasMalus(node5)) {
         p_77266_[i++] = node12;
      }

      Node node13 = this.findAcceptedNode(p_77267_.x, p_77267_.y - 1, p_77267_.z - 1);
      if (this.isOpen(node13) && this.hasMalus(node3) && this.hasMalus(node5)) {
         p_77266_[i++] = node13;
      }

      Node node14 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y, p_77267_.z - 1);
      if (this.isOpen(node14) && this.hasMalus(node3) && this.hasMalus(node2)) {
         p_77266_[i++] = node14;
      }

      Node node15 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y, p_77267_.z + 1);
      if (this.isOpen(node15) && this.hasMalus(node) && this.hasMalus(node2)) {
         p_77266_[i++] = node15;
      }

      Node node16 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y, p_77267_.z - 1);
      if (this.isOpen(node16) && this.hasMalus(node3) && this.hasMalus(node1)) {
         p_77266_[i++] = node16;
      }

      Node node17 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y, p_77267_.z + 1);
      if (this.isOpen(node17) && this.hasMalus(node) && this.hasMalus(node1)) {
         p_77266_[i++] = node17;
      }

      Node node18 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y + 1, p_77267_.z - 1);
      if (this.isOpen(node18) && this.hasMalus(node14) && this.hasMalus(node3) && this.hasMalus(node2) && this.hasMalus(node4) && this.hasMalus(node9) && this.hasMalus(node8)) {
         p_77266_[i++] = node18;
      }

      Node node19 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y + 1, p_77267_.z + 1);
      if (this.isOpen(node19) && this.hasMalus(node15) && this.hasMalus(node) && this.hasMalus(node2) && this.hasMalus(node4) && this.hasMalus(node6) && this.hasMalus(node8)) {
         p_77266_[i++] = node19;
      }

      Node node20 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y + 1, p_77267_.z - 1);
      if (this.isOpen(node20) && this.hasMalus(node16) && this.hasMalus(node3) && this.hasMalus(node1) && this.hasMalus(node4) && this.hasMalus(node9) && this.hasMalus(node7)) {
         p_77266_[i++] = node20;
      }

      Node node21 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y + 1, p_77267_.z + 1);
      if (this.isOpen(node21) && this.hasMalus(node17) && this.hasMalus(node) && this.hasMalus(node1) && this.hasMalus(node4) && this.hasMalus(node6) && this.hasMalus(node7)) {
         p_77266_[i++] = node21;
      }

      Node node22 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y - 1, p_77267_.z - 1);
      if (this.isOpen(node22) && this.hasMalus(node14) && this.hasMalus(node3) && this.hasMalus(node2) && this.hasMalus(node5) && this.hasMalus(node13) && this.hasMalus(node12)) {
         p_77266_[i++] = node22;
      }

      Node node23 = this.findAcceptedNode(p_77267_.x + 1, p_77267_.y - 1, p_77267_.z + 1);
      if (this.isOpen(node23) && this.hasMalus(node15) && this.hasMalus(node) && this.hasMalus(node2) && this.hasMalus(node5) && this.hasMalus(node10) && this.hasMalus(node12)) {
         p_77266_[i++] = node23;
      }

      Node node24 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y - 1, p_77267_.z - 1);
      if (this.isOpen(node24) && this.hasMalus(node16) && this.hasMalus(node3) && this.hasMalus(node1) && this.hasMalus(node5) && this.hasMalus(node13) && this.hasMalus(node11)) {
         p_77266_[i++] = node24;
      }

      Node node25 = this.findAcceptedNode(p_77267_.x - 1, p_77267_.y - 1, p_77267_.z + 1);
      if (this.isOpen(node25) && this.hasMalus(node17) && this.hasMalus(node) && this.hasMalus(node1) && this.hasMalus(node5) && this.hasMalus(node10) && this.hasMalus(node11)) {
         p_77266_[i++] = node25;
      }

      return i;
   }

   private boolean hasMalus(@Nullable Node p_77264_) {
      return p_77264_ != null && p_77264_.costMalus >= 0.0F;
   }

   private boolean isOpen(@Nullable Node p_77270_) {
      return p_77270_ != null && !p_77270_.closed;
   }

   @Nullable
   protected Node findAcceptedNode(int p_262970_, int p_263018_, int p_262947_) {
      Node node = null;
      BlockPathTypes blockpathtypes = this.getCachedBlockPathType(p_262970_, p_263018_, p_262947_);
      float f = this.mob.getPathfindingMalus(blockpathtypes);
      if (f >= 0.0F) {
         node = this.getNode(p_262970_, p_263018_, p_262947_);
         node.type = blockpathtypes;
         node.costMalus = Math.max(node.costMalus, f);
         if (blockpathtypes == BlockPathTypes.WALKABLE) {
            ++node.costMalus;
         }
      }

      return node;
   }

   private BlockPathTypes getCachedBlockPathType(int p_164694_, int p_164695_, int p_164696_) {
      return this.pathTypeByPosCache.computeIfAbsent(BlockPos.asLong(p_164694_, p_164695_, p_164696_), (p_265010_) -> {
         return this.getBlockPathType(this.level, p_164694_, p_164695_, p_164696_, this.mob);
      });
   }

   public BlockPathTypes getBlockPathType(BlockGetter p_265753_, int p_265243_, int p_265376_, int p_265253_, Mob p_265367_) {
      EnumSet<BlockPathTypes> enumset = EnumSet.noneOf(BlockPathTypes.class);
      BlockPathTypes blockpathtypes = BlockPathTypes.BLOCKED;
      BlockPos blockpos = p_265367_.blockPosition();
      blockpathtypes = super.getBlockPathTypes(p_265753_, p_265243_, p_265376_, p_265253_, enumset, blockpathtypes, blockpos);
      if (enumset.contains(BlockPathTypes.FENCE)) {
         return BlockPathTypes.FENCE;
      } else {
         BlockPathTypes blockpathtypes1 = BlockPathTypes.BLOCKED;

         for(BlockPathTypes blockpathtypes2 : enumset) {
            if (p_265367_.getPathfindingMalus(blockpathtypes2) < 0.0F) {
               return blockpathtypes2;
            }

            if (p_265367_.getPathfindingMalus(blockpathtypes2) >= p_265367_.getPathfindingMalus(blockpathtypes1)) {
               blockpathtypes1 = blockpathtypes2;
            }
         }

         return blockpathtypes == BlockPathTypes.OPEN && p_265367_.getPathfindingMalus(blockpathtypes1) == 0.0F ? BlockPathTypes.OPEN : blockpathtypes1;
      }
   }

   public BlockPathTypes getBlockPathType(BlockGetter p_77245_, int p_77246_, int p_77247_, int p_77248_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      BlockPathTypes blockpathtypes = getBlockPathTypeRaw(p_77245_, blockpos$mutableblockpos.set(p_77246_, p_77247_, p_77248_));
      if (blockpathtypes == BlockPathTypes.OPEN && p_77247_ >= p_77245_.getMinBuildHeight() + 1) {
         BlockPathTypes blockpathtypes1 = getBlockPathTypeRaw(p_77245_, blockpos$mutableblockpos.set(p_77246_, p_77247_ - 1, p_77248_));
         if (blockpathtypes1 != BlockPathTypes.DAMAGE_FIRE && blockpathtypes1 != BlockPathTypes.LAVA) {
            if (blockpathtypes1 == BlockPathTypes.DAMAGE_OTHER) {
               blockpathtypes = BlockPathTypes.DAMAGE_OTHER;
            } else if (blockpathtypes1 == BlockPathTypes.COCOA) {
               blockpathtypes = BlockPathTypes.COCOA;
            } else if (blockpathtypes1 == BlockPathTypes.FENCE) {
               if (!blockpos$mutableblockpos.equals(this.mob.blockPosition())) {
                  blockpathtypes = BlockPathTypes.FENCE;
               }
            } else {
               blockpathtypes = blockpathtypes1 != BlockPathTypes.WALKABLE && blockpathtypes1 != BlockPathTypes.OPEN && blockpathtypes1 != BlockPathTypes.WATER ? BlockPathTypes.WALKABLE : BlockPathTypes.OPEN;
            }
         } else {
            blockpathtypes = BlockPathTypes.DAMAGE_FIRE;
         }
      }

      if (blockpathtypes == BlockPathTypes.WALKABLE || blockpathtypes == BlockPathTypes.OPEN) {
         blockpathtypes = checkNeighbourBlocks(p_77245_, blockpos$mutableblockpos.set(p_77246_, p_77247_, p_77248_), blockpathtypes);
      }

      return blockpathtypes;
   }

   private Iterable<BlockPos> iteratePathfindingStartNodeCandidatePositions(Mob p_263108_) {
      float f = 1.0F;
      AABB aabb = p_263108_.getBoundingBox();
      boolean flag = aabb.getSize() < 1.0D;
      if (!flag) {
         return List.of(BlockPos.containing(aabb.minX, (double)p_263108_.getBlockY(), aabb.minZ), BlockPos.containing(aabb.minX, (double)p_263108_.getBlockY(), aabb.maxZ), BlockPos.containing(aabb.maxX, (double)p_263108_.getBlockY(), aabb.minZ), BlockPos.containing(aabb.maxX, (double)p_263108_.getBlockY(), aabb.maxZ));
      } else {
         double d0 = Math.max(0.0D, (1.5D - aabb.getZsize()) / 2.0D);
         double d1 = Math.max(0.0D, (1.5D - aabb.getXsize()) / 2.0D);
         double d2 = Math.max(0.0D, (1.5D - aabb.getYsize()) / 2.0D);
         AABB aabb1 = aabb.inflate(d1, d2, d0);
         return BlockPos.randomBetweenClosed(p_263108_.getRandom(), 10, Mth.floor(aabb1.minX), Mth.floor(aabb1.minY), Mth.floor(aabb1.minZ), Mth.floor(aabb1.maxX), Mth.floor(aabb1.maxY), Mth.floor(aabb1.maxZ));
      }
   }
}