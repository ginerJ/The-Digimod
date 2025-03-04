package net.modderg.thedigimod.server.projectiles.effects;

import net.minecraft.world.entity.Entity;
import net.modderg.thedigimod.server.entity.DigimonEntity;

public class ProjectileEffectDirty extends ProjectileEffect{
    @Override
    public void applyEffects(Entity target) {
        if(target instanceof DigimonEntity digimon)
            digimon.setDirtyCounter(Math.max(1, digimon.getDirtyCounter()));
    }
}
