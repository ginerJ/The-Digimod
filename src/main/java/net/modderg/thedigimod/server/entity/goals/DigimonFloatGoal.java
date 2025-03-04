package net.modderg.thedigimod.server.entity.goals;


import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import net.modderg.thedigimod.server.entity.SwimmerDigimonEntity;

public class DigimonFloatGoal extends FloatGoal {

    DigimonEntity digimon;

    public DigimonFloatGoal(DigimonEntity mob ) {
        super(mob);
        digimon = mob;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !(digimon instanceof SwimmerDigimonEntity);
    }
}
