package net.modderg.thedigimod.entity.goals;

import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.modderg.thedigimod.entity.CustomDigimon;

public class ScapeDigimonGoal extends AvoidEntityGoal {

    public ScapeDigimonGoal(CustomDigimon digimon, Class p_25028_, float p_25029_, double p_25030_, double p_25031_) {
        super(digimon, p_25028_, p_25029_, p_25030_, p_25031_);
        mob = digimon;
    }

    CustomDigimon mob;

    @Override
    public boolean canUse() {
        return super.canUse() && mob.scape < 0 && mob.getTarget() instanceof CustomDigimon && mob.isInSittingPose();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && mob.scape < 0 && mob.getTarget() instanceof CustomDigimon && mob.isInSittingPose();
    }


}
