package net.modderg.thedigimod.server.entity.goals;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

public class DigitalHurtByTargetGoal extends HurtByTargetGoal {
    public DigitalHurtByTargetGoal(PathfinderMob p_26039_, Class<?>... p_26040_) {
        super(p_26039_, p_26040_);
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.getTarget() != null && super.canContinueToUse();
    }
}
