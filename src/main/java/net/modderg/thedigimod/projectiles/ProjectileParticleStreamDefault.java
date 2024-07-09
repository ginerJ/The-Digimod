package net.modderg.thedigimod.projectiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.packet.PacketInit;
import net.modderg.thedigimod.packet.StoCShootParticlesPacket;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ProjectileParticleStreamDefault extends ProjectileDefault {

    public ProjectileParticleStreamDefault(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
        this.life = 5;
        this.hurtMultiplier = 0.2f;
    }

    @Override
    public void performRangedAttack(@NotNull CustomDigimon mob, @NotNull LivingEntity target) {
        this.setBaseDamage(mob.getCurrentLevel());

        this.target = target;

        this.setPos(mob.position());

        mob.getLookControl().setLookAt(Objects.requireNonNull(mob.getTarget()));

        this.setRot((float) mob.getLookControl().getWantedX(), (float) mob.getLookControl().getWantedY());

        this.setOwner(mob);

        playShootSound();

        mob.level().addFreshEntity(this);


        if(mob.distanceTo(target) < range){
            onHitEffects.forEach(pe -> pe.applyEffects(target));
            if(this.hurtNotDigimonTarget(target)) superOnHitEntity(new EntityHitResult(target));
        }
    }

    int hit = 0;
    int times = 0;

    @Override
    public void tick() {
        super.tick();
        if(--hit == 0 && this.getTarget() != null)
            this.hurtNotDigimonTarget(this.getTarget());
    }

    @Override
    protected boolean hurtNotDigimonTarget(Entity hitted) {
        if(times < 5) {
            hit = 5;
            life = 10;
        }
        times++;

        if(!this.level().isClientSide() && this.getTarget() != null){
            PacketInit.sendToAll(new StoCShootParticlesPacket(this.getId(), target.blockPosition(), this.blockPosition()));
        }
        return super.hurtNotDigimonTarget(hitted);
    }

    float range = 0f;

    public ProjectileParticleStreamDefault setRange(float ran){
        this.range = ran;
        return this;
    }

    int streams = 1;

    public ProjectileParticleStreamDefault setStreams(int streams){
        this.streams = streams;
        return this;
    }

    public void spawnParticleBeam(BlockPos start, BlockPos end) {
        Vec3 direction = new Vec3(
                end.getX() - start.getX(),
                end.getY() - start.getY(),
                end.getZ() - start.getZ()
        ).normalize();

        double speedMultiplier = 1;

        for (int i = 0; i < speedMultiplier * 10; i++){
            for (int j = 0; j < streams; j++){
                for (int k = 0; k < 10; k++) {
                    double modifiedSpeedMultiplier = i * 0.1 + (random.nextDouble() * 0.05 - 0.025);

                    double velocityX = direction.x * modifiedSpeedMultiplier;
                    double velocityY = (direction.y + j * 0.1) * modifiedSpeedMultiplier;
                    double velocityZ = direction.z * modifiedSpeedMultiplier;

                    double startX = start.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.1;
                    double startY = start.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.1;
                    double startZ = start.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.1;

                    this.level().addAlwaysVisibleParticle(particle, startX, startY, startZ, velocityX, velocityY, velocityZ);
                }
            }
        }
    }
}
