package net.modderg.thedigimod.server.projectiles.effects;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.modderg.thedigimod.server.projectiles.ProjectileDefault;

public abstract class ProjectileEffect {

    protected ProjectileDefault projectile;

    public ProjectileEffect setProjectile(ProjectileDefault projectile){
        this.projectile = projectile;
        return this;
    }

    public abstract void applyEffects(Entity target);
    public void applyBlockEffects(BlockHitResult target){}
}
