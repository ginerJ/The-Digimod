package net.modderg.thedigimod.entity.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomSwimmerDigimon;

import java.util.EnumSet;


public class DigitalFollowOwnerGoal extends Goal {

    private final CustomDigimon tamable;
    private LivingEntity owner;
    private final LevelReader level;
    private final double speedModifier;
    private PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private float oldWaterCost;
    private final boolean canFly;

    public DigitalFollowOwnerGoal(CustomDigimon p_25294_, double p_25295_, float p_25296_, float p_25297_, boolean p_25298_) {
        this.tamable = p_25294_;
        this.level = p_25294_.level();
        this.speedModifier = p_25295_;
        this.navigation = p_25294_.getNavigation();
        this.startDistance = p_25296_;
        this.stopDistance = p_25297_;
        this.canFly = p_25298_;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));

    }

    public boolean canUse() {
        LivingEntity livingentity = this.tamable.getOwner();
        if (livingentity == null || this.tamable.isAggressive()) {
            return false;
        } else if (livingentity.isSpectator()) {
            return false;
        } else if (this.tamable.isOrderedToSit() || this.tamable.getMovementID() == -1 || this.tamable.getMovementID() == -2) {
            return false;
        } else if (this.tamable.distanceToSqr(livingentity) < (double)(this.startDistance * this.startDistance)) {
            return false;
        } else {
            this.owner = livingentity;
            return true;
        }
    }

    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        } else if (this.tamable.isOrderedToSit() || this.tamable.getMovementID() == -1) {
            return false;
        } else {
            return !(this.tamable.distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
        }
    }

    public void start() {
        this.timeToRecalcPath = 0;
        if(!(tamable instanceof CustomSwimmerDigimon)){
            this.oldWaterCost = this.tamable.getPathfindingMalus(BlockPathTypes.WATER);
            this.tamable.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }
    }

    public void stop() {
        this.owner = null;
        this.navigation.stop();
        if(!(tamable instanceof CustomSwimmerDigimon)){
            this.tamable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        }
    }

    public void tick() {
        this.navigation = this.tamable.getNavigation();
        this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tamable.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (!this.tamable.isLeashed() && !this.tamable.isPassenger()) {
                if (this.tamable.distanceToSqr(this.owner) >= 200.0D && this.canTeleportTo(this.owner.blockPosition())) {
                    this.teleportToOwner();
                } else {
                    this.navigation.moveTo(this.owner, this.speedModifier);
                }

            }
        }
    }

    private void teleportToOwner() {
        BlockPos blockpos = this.owner.blockPosition();

        for(int i = 0; i < 10; ++i) {
            int j = this.randomIntInclusive(-3, 3);
            int k = this.randomIntInclusive(-1, 1);
            int l = this.randomIntInclusive(-3, 3);
            boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (flag) {
                return;
            }
        }

    }

    private boolean maybeTeleportTo(int p_25304_, int p_25305_, int p_25306_) {
        if (Math.abs((double)p_25304_ - this.owner.getX()) < 2.0D && Math.abs((double)p_25306_ - this.owner.getZ()) < 2.0D) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(p_25304_, p_25305_, p_25306_))) {
            return false;
        } else {
            this.tamable.moveTo((double)p_25304_ + 0.5D, p_25305_, (double)p_25306_ + 0.5D, this.tamable.getYRot(), this.tamable.getXRot());
            this.navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos p_25308_) {
        BlockPathTypes blockpathtypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, p_25308_.mutable());
        if ((!(tamable instanceof CustomSwimmerDigimon) || blockpathtypes != BlockPathTypes.WATER) && blockpathtypes != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            BlockState blockstate = this.level.getBlockState(p_25308_.below());
            if (!this.canFly && blockstate.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockpos = p_25308_.subtract(this.tamable.blockPosition());
                return this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(blockpos));
            }
        }
    }

    private int randomIntInclusive(int p_25301_, int p_25302_) {
        return this.tamable.getRandom().nextInt(p_25302_ - p_25301_ + 1) + p_25301_;
    }
}
