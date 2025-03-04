package net.modderg.thedigimod.server.projectiles;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import org.jetbrains.annotations.NotNull;

public class ProjectileSkyFallingDefault extends ProjectileDefault {

    public ProjectileSkyFallingDefault(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
    }

    @Override
    public void performRangedAttack(@NotNull DigimonEntity mob, @NotNull LivingEntity target) {

        this.setBaseDamage(mob.getCurrentLevel());

        this.setPos(new Vec3(target.position().x, target.position().y + 10, target.position().z));

        this.setOwner(mob);

        mob.level().addFreshEntity(this);
    }

    @Override
    public boolean isNoGravity() {
        return false;
    }
}
