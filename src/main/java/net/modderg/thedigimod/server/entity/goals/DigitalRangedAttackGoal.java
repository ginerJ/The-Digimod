package net.modderg.thedigimod.server.entity.goals;

import java.util.EnumSet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.modderg.thedigimod.server.entity.DigimonEntity;

public class DigitalRangedAttackGoal<T extends net.minecraft.world.entity.Mob & RangedAttackMob> extends Goal {
    private final DigimonEntity mob;
    private int attackIntervalMin;
    private int attackTime = 1;
    private final double speedTowardsTarget;
    private final float maxAttackDistance;
    private final float minAttackDistance;

    private int changeDirectionTicks = 0;
    private boolean strafeLeft = false;

    public DigitalRangedAttackGoal(DigimonEntity mob, double speedTowardsTarget, int attackIntervalMin, float maxAttackDistance) {
        this.mob = mob;
        this.speedTowardsTarget = speedTowardsTarget;
        this.attackIntervalMin = attackIntervalMin;
        this.maxAttackDistance = maxAttackDistance * maxAttackDistance; // Store as squared for performance
        this.minAttackDistance = (maxAttackDistance-1.5f) * (maxAttackDistance-1.5f); // Define a minimum distance to keep from the target
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        return this.mob.getMovementID() != 0  && this.mob.getTarget() != null && this.mob.getTarget().isAddedToWorld() && mob.getControllingPassenger() == null;
    }

    public boolean canContinueToUse() {
        return this.canUse();
    }

    public void start() {
        super.start();
        this.mob.setAggressive(true);
    }

    public void stop() {
        super.stop();
        this.mob.getMoveControl().strafe(0f,0f);
        this.mob.setAggressive(false);
        this.attackTime = -1;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void setAttackTime(int attackTime) {
        this.attackTime = attackTime;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            double distanceSq = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (distanceSq > this.maxAttackDistance) {
                this.mob.getNavigation().moveTo(target, this.speedTowardsTarget);
            } else {
                if (--changeDirectionTicks <= 0) {
                    strafeLeft = !strafeLeft;
                    changeDirectionTicks = 20;
                }

                float strafe = strafeLeft ? 0.5F : -0.5F;
                float backward = 0.5F;

                if (distanceSq < this.minAttackDistance) {
                    this.mob.getMoveControl().strafe(-backward, strafe);
                } else {
                    this.mob.getMoveControl().strafe(0.0F, strafe);
                }
            }

            if (--this.attackTime == 60) {
                this.mob.performRangedAttack(target, 1f);
            } else if (this.attackTime <= 0) {
                this.attackTime = this.attackIntervalMin;
            }
        }
    }
}