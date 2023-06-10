package net.modderg.thedigimod.entity.goals;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.modderg.thedigimod.entity.CustomDigimon;

public class DigitalMeleeAttackGoal extends MeleeAttackGoal {

    CustomDigimon digimon;

    public DigitalMeleeAttackGoal(CustomDigimon digimon, double p_25553_, boolean p_25554_) {
        super(digimon, p_25553_, p_25554_);
        this.digimon = digimon;
    }

    @Override
    public boolean canUse() {
        return (super.canUse() && !digimon.isOrderedToSit());
    }

    @Override
    public boolean canContinueToUse() {
        return (super.canContinueToUse() && !digimon.isOrderedToSit());
    }
}
