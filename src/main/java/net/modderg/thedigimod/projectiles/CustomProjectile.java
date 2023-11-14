package net.modderg.thedigimod.projectiles;

import com.google.common.collect.Sets;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.particles.DigitalParticles;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Collection;
import java.util.Set;

public class CustomProjectile extends AbstractArrow implements GeoEntity {

    protected int timesRepeat = 1;
    protected int healOnHit = 0;

    protected CustomDigimon mob = null;
    protected LivingEntity target = null;

    protected String attack = "small_bullet";
    protected ParticleOptions particle = DigitalParticles.BUBBLE_ATTACK.get();
    protected boolean isPhysical = false;

    protected boolean onFire = false ;
    protected boolean bright = false ;

    public int life = 100;

    private final Set<MobEffectInstance> effects = Sets.newHashSet();
    public final Set<MobEffectInstance> oEffects = Sets.newHashSet();

    public CustomProjectile(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
    }

    public CustomProjectile(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_, String attack, ParticleOptions particle) {
        super(p_36721_, p_36722_);
        this.attack = attack;
        this.particle = particle;
    }

    public CustomProjectile addEffect(MobEffect effect) {
        this.effects.add(new MobEffectInstance(effect,200,0));
        return this;
    }

    public CustomProjectile addEffect(MobEffect effect, int seconds) {
        this.effects.add(new MobEffectInstance(effect,40*seconds,0));
        return this;
    }

    public CustomProjectile addOwnerEffect(MobEffect effect) {
        this.oEffects.add(new MobEffectInstance(effect,600,0));
        return this;
    }

    public CustomProjectile addOwnerEffect(MobEffect effect, int seconds) {
        this.oEffects.add(new MobEffectInstance(effect,40*seconds,0));
        return this;
    }

    public CustomProjectile setPhysical(){
        isPhysical = true;
        return this;
    }

    public CustomProjectile setFire(){
        onFire = true;
        return this;
    }

    public CustomProjectile healOnHit(int heal){
        this.healOnHit = heal;
        return this;
    }

    public CustomProjectile setRepeat(int times){
        timesRepeat = times;
        return this;
    }

    public int getRepeatTimes(){
        return timesRepeat;
    }

    public CustomProjectile setBright(){
        bright = true;
        return this;
    }

    public void performRangedAttack(@NotNull CustomDigimon mob, @NotNull LivingEntity target){
        this.setPos(new Vec3(mob.position().x, mob.position().y + 1, mob.position().z));
        mob.getLookControl().setLookAt(mob.getTarget());

        double d0 = target.getX() - mob.getX();
        double d1 = target.getY(0.3333333333333333D) - mob.getEyeY();
        double d2 = target.getZ() - mob.getZ();
        double distance = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        double motionX = d0 / distance * 1.5;
        double motionY = d1 / distance * 1.5;
        double motionZ = d2 / distance * 1.5;
        motionY += 0.1;

        shoot(motionX, motionY, motionZ, 1.5F, 10.0F);

        this.setOwner(mob);

        this.mob = mob;
        this.target = target;

        mob.level().addFreshEntity(this);
    }

    @Override
    public void tick() {
        if(--life <0){
            this.remove(RemovalReason.UNLOADED_TO_CHUNK);
        }
        super.tick();
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        this.remove(RemovalReason.UNLOADED_TO_CHUNK);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitted) {
        if(this.getOwner() instanceof CustomDigimon cd){
            if(hitted.getEntity() instanceof CustomDigimon hcd){
                if(!(hcd.getOwner() != null && cd.getOwner() != null && cd.getOwner().is(hcd.getOwner()))){
                    hcd.hurt(this.damageSources().mobAttack(cd), cd.calculateDamage(
                            (isPhysical ? cd.getAttackStat()/2 : cd.getSpAttackStat())/ timesRepeat + cd.getCurrentLevel(),
                            hcd.getSpDefenceStat() + hcd.getCurrentLevel()
                    ));}
            }else {
                super.onHitEntity(hitted);
            }
            cd.heal(healOnHit);
        }
        if(onFire) hitted.getEntity().setSecondsOnFire(3);
        this.remove(RemovalReason.UNLOADED_TO_CHUNK);
    }

    @Override
    public void onRemovedFromWorld() {
        for(int i = 0; i < 360; i++) {
            if(random.nextInt(0,20) == 5) {
                this.level().addParticle(particle,
                        this.blockPosition().getX() + 1d, this.blockPosition().getY(), this.blockPosition().getZ() + 1d,
                        Math.cos(i) * 0.3d, 0.15d + random.nextDouble()*0.1d, Math.sin(i) * 0.3d);

                this.level().addParticle(particle,
                        this.blockPosition().getX() + 1d, this.blockPosition().getY(), this.blockPosition().getZ() + 1d,
                        Math.sin(i) * 0.3d, 0.15d + random.nextDouble()*0.1d, Math.sin(i) * 0.3d);
            }
        }
        super.onRemovedFromWorld();
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected ItemStack getPickupItem() {
        return null;
    }

    protected AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    private PlayState animController(AnimationState event) {
        event.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::animController));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }

    @Override
    protected void doPostHurtEffects(LivingEntity p_36873_) {
        for(MobEffectInstance effect : this.effects) {
            p_36873_.addEffect(effect, this);
        }
    }
}
