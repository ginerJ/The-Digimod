package net.modderg.thedigimod.server.entity.goals;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.modderg.thedigimod.server.entity.DigimonEntity;

public class DigitalMeleeAttackGoal extends MeleeAttackGoal {

    DigimonEntity digimon;

    public DigitalMeleeAttackGoal(DigimonEntity digimon, double p_25553_, boolean p_25554_) {
        super(digimon, p_25553_, p_25554_);
        this.digimon = digimon;
    }

    @Override
    public boolean canUse() {
        return (super.canUse() && !digimon.isOrderedToSit() && !digimon.canBeControlledByRider());
    }

    @Override
    public boolean canContinueToUse() {
        return (super.canContinueToUse() && !digimon.isOrderedToSit() && !digimon.canBeControlledByRider());
    }

    @Override
    public void stop() {;
        super.stop();
    }
}
