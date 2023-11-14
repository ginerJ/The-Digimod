package net.modderg.thedigimod.projectiles;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.entity.CustomDigimon;
import org.jetbrains.annotations.NotNull;

public class CustomFallingProjectile extends CustomProjectile {

    public CustomFallingProjectile(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_, String attack, ParticleOptions particle) {
        super(p_36721_, p_36722_);
        this.attack = attack;
        this.particle = particle;
    }

    @Override
    public void performRangedAttack(@NotNull CustomDigimon mob, @NotNull LivingEntity target) {

        this.setPos(new Vec3(target.position().x, target.position().y + 10, target.position().z));

        this.setOwner(mob);
        mob.level().addFreshEntity(this);
    }

    @Override
    public boolean isNoGravity() {
        return false;
    }
}
