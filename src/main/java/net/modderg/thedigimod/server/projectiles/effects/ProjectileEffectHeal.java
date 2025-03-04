package net.modderg.thedigimod.server.projectiles.effects;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ProjectileEffectHeal extends ProjectileEffect{

    int amount;

    public ProjectileEffectHeal(int amount){
        this.amount = amount;
    }

    @Override
    public void applyEffects(Entity target) {
        if(projectile.getOwner() instanceof LivingEntity owner)
            owner.heal(amount);
    }
}
