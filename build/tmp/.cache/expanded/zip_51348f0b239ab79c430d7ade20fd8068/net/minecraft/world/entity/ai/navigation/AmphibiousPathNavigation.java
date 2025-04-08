package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class AmphibiousPathNavigation extends PathNavigation {
   public AmphibiousPathNavigation(Mob p_217788_, Level p_217789_) {
      super(p_217788_, p_217789_);
   }

   protected PathFinder createPathFinder(int p_217792_) {
      this.nodeEvaluator = new AmphibiousNodeEvaluator(false);
      this.nodeEvaluator.setCanPassDoors(true);
      return new PathFinder(this.nodeEvaluator, p_217792_);
   }

   protected boolean canUpdatePath() {
      return true;
   }

   protected Vec3 getTempMobPos() {
      return new Vec3(this.mob.getX(), this.mob.getY(0.5D), this.mob.getZ());
   }

   protected double getGroundY(Vec3 p_217794_) {
      return p_217794_.y;
   }

   protected boolean canMoveDirectly(Vec3 p_217796_, Vec3 p_217797_) {
      return this.isInLiquid() ? isClearForMovementBetween(this.mob, p_217796_, p_217797_, false) : false;
   }

   public boolean isStableDestination(BlockPos p_217799_) {
      return !this.level.getBlockState(p_217799_.below()).isAir();
   }

   public void setCanFloat(boolean p_217801_) {
   }
}