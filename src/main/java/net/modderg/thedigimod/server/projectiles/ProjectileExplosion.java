package net.modderg.thedigimod.server.projectiles;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import org.jetbrains.annotations.NotNull;

public class ProjectileExplosion extends ProjectileDefault {
    private int radius;

    public ProjectileExplosion(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_, int radius) {
        super(p_36721_, p_36722_);
        this.radius = radius;
    }

    @Override
    public void remove(RemovalReason p_146834_) {
        findEntitiesCaughtInExplosion();
        this.playSound(this.getDefaultHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        super.remove(p_146834_);
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {this.life = waitTime;}

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.GENERIC_EXPLODE;
    }

    int waitTime = 60;

    public ProjectileExplosion setWaitTime(int waitTime){
        this.waitTime = waitTime;
        return this;
    }

    private void findEntitiesCaughtInExplosion(){
        AABB searchArea = new AABB(this.blockPosition()).inflate(radius);
        this.level().getEntitiesOfClass(DigimonEntity.class, searchArea)
                .forEach(this::hurtEntityByExplosion);
    }

    private void hurtEntityByExplosion(Entity hitted){

        if(this.getOwner() instanceof DigimonEntity cd){
            if(this.getOwner().is(hitted)) return;

            if(hitted instanceof DigimonEntity hcd){
                if(!(hcd.getOwner() != null && cd.getOwner() != null)){
                    hcd.hurt(this.damageSources().mobAttack(cd), cd.calculateDamage(
                            (isPhysical ? cd.getAttackStat()/2 : cd.getSpAttackStat())/ timesRepeat + cd.getCurrentLevel(),
                            hcd.getSpDefenceStat() + hcd.getCurrentLevel()
                    ));}
            }
        }
    }
}
