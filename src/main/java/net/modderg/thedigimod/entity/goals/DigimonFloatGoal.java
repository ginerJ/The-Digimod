package net.modderg.thedigimod.entity.goals;


import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomSwimmerDigimon;

public class DigimonFloatGoal extends FloatGoal {

    CustomDigimon digimon;

    public DigimonFloatGoal(CustomDigimon mob ) {
        super(mob);
        digimon = mob;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !(digimon instanceof CustomSwimmerDigimon);
    }
}
