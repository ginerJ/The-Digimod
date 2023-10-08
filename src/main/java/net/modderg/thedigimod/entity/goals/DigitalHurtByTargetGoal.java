package net.modderg.thedigimod.entity.goals;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

public class DigitalHurtByTargetGoal extends HurtByTargetGoal {
    public DigitalHurtByTargetGoal(PathfinderMob p_26039_, Class<?>... p_26040_) {
        super(p_26039_, p_26040_);
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.mob.getTarget() != null;
    }

    @Override
    public boolean canUse() {
        this.targetMob = this.mob.getTarget();
        return super.canUse();
    }
}
