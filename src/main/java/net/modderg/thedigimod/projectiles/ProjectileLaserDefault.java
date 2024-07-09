package net.modderg.thedigimod.projectiles;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.packet.PacketInit;
import net.modderg.thedigimod.packet.StoCLaserScalePacket;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ProjectileLaserDefault extends ProjectileDefault {

    int hitNonDigimon = -1;
    public float distanceToTarget = 0f;

    public ProjectileLaserDefault(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
        life = 5;
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {}

    @Override
    protected void onHitEntity(EntityHitResult hitted) {}

    @Override
    public void performRangedAttack(@NotNull CustomDigimon mob, @NotNull LivingEntity target){

        this.setBaseDamage(mob.getCurrentLevel());

        this.target = target;

        this.setPos(new Vec3(mob.position().x, mob.position().y + 1, mob.position().z));

        mob.getLookControl().setLookAt(Objects.requireNonNull(mob.getTarget()));

        shootAtTargetWithSpeed(mob, 0.001f);

        this.setRot((float) mob.getLookControl().getWantedX(), (float) mob.getLookControl().getWantedY());

        this.setOwner(mob);

        mob.level().addFreshEntity(this);

        if(!this.level().isClientSide())
            PacketInit.sendToAll(new StoCLaserScalePacket(this.getId(), target.getId()));

        onHitEffects.forEach(pe -> pe.applyEffects(target));
        if(this.hurtNotDigimonTarget(target)) hitNonDigimon = 4;

        playShootSound();
    }

    @Override
    public void tick() {
        super.tick();
        if(--hitNonDigimon == 0)
            superOnHitEntity(new EntityHitResult(target));
    }

    @Override
    public void onRemovedFromWorld() {
        superOnRemovedFromWorld();
    }

    @Override
    protected float hurtAndCalculateDigimonDamage(CustomDigimon cd, CustomDigimon hcd) {
        return super.hurtAndCalculateDigimonDamage(cd, hcd) * 0.8f;
    }
}
