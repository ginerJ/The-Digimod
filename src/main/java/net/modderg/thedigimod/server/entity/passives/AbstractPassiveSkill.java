package net.modderg.thedigimod.server.entity.passives;

import net.modderg.thedigimod.server.entity.DigimonEntity;

public abstract class AbstractPassiveSkill {

    private DigimonEntity digimon;

    public AbstractPassiveSkill(DigimonEntity digimon) {
        this.digimon = digimon;
    }

    public void tick() {
    }
}
