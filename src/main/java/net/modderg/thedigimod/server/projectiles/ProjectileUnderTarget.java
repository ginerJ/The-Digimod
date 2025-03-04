package net.modderg.thedigimod.server.projectiles;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ProjectileUnderTarget extends ProjectileDefault {

    int hitNonDigimon = -1;
    public ProjectileUnderTarget(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {}

    @Override
    protected void onHitEntity(EntityHitResult hitted) {}

    @Override
    public void performRangedAttack(@NotNull DigimonEntity mob, @NotNull LivingEntity target){

        this.setBaseDamage(mob.getCurrentLevel());

        this.target = target;

        this.setPos(target.position());

        mob.getLookControl().setLookAt(Objects.requireNonNull(mob.getTarget()));

        this.setOwner(mob);

        mob.level().addFreshEntity(this);

        onHitEffects.forEach(pe -> pe.applyEffects(target));
        if(this.hurtNotDigimonTarget(target)) hitNonDigimon = 20;

        playShootSound();
    }

    @Override
    public void tick() {
        super.tick();
        if(--hitNonDigimon == 0)
            superOnHitEntity(new EntityHitResult(target));
    }

    @Override
    protected float hurtAndCalculateDigimonDamage(DigimonEntity cd, DigimonEntity hcd) {
        return super.hurtAndCalculateDigimonDamage(cd, hcd) * 0.8f;
    }

    public ProjectileUnderTarget setSpawnLife(int life){
        this.life = life;
        return this;
    }
}
