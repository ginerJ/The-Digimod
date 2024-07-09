package net.modderg.thedigimod.projectiles.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ProjectilePotionOwnerEffect extends ProjectileEffect{

    MobEffectInstance effect;

    public ProjectilePotionOwnerEffect(MobEffect effect, int ticks) {
        this.effect = new MobEffectInstance(effect, ticks);
    }

    @Override
    public void applyEffects(Entity entity) {
        if(projectile.getOwner() instanceof LivingEntity livingEntity){
            livingEntity.addEffect(effect, projectile);
        }
    }
}
