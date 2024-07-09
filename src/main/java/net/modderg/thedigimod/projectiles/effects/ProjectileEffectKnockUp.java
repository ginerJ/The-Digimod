package net.modderg.thedigimod.projectiles.effects;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.modderg.thedigimod.entity.CustomFlyingDigimon;

public class ProjectileEffectKnockUp extends ProjectileEffect {
    @Override
    public void applyEffects(Entity target) {

        Vec3 currentMotion = target.getDeltaMovement();

        Vec3 newMotion;

        if(target instanceof CustomFlyingDigimon cd && cd.getMovementID() == 2)
            newMotion = new Vec3(currentMotion.x, 0.15f, currentMotion.z);
        else newMotion = new Vec3(currentMotion.x, 1.0f, currentMotion.z);

        target.setDeltaMovement(newMotion);

        target.hurtMarked = true;
    }

}
