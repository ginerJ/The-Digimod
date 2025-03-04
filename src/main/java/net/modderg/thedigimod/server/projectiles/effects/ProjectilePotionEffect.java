package net.modderg.thedigimod.server.projectiles.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ProjectilePotionEffect extends ProjectileEffect{

    MobEffectInstance effect;

    public ProjectilePotionEffect(MobEffect effect, int ticks) {
        this.effect = new MobEffectInstance(effect, ticks);
    }

    @Override
    public void applyEffects(Entity target) {
        if(target instanceof LivingEntity livingEntity){
            livingEntity.addEffect(effect, projectile);
        }
    }
}
