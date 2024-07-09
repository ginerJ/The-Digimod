package net.modderg.thedigimod.projectiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraftforge.fluids.FluidType;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.particles.DigitalParticles;
import net.modderg.thedigimod.projectiles.effects.ProjectileEffect;
import net.modderg.thedigimod.sound.DigiSounds;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.LinkedList;
import java.util.Objects;

public class ProjectileDefault extends AbstractArrow implements GeoEntity {

    protected int timesRepeat = 1;

    protected LivingEntity target = null;
    public LivingEntity getTarget(){return target;}

    protected ParticleOptions particle = DigitalParticles.BUBBLE_ATTACK.get();
    protected boolean isPhysical = false;

    protected boolean bright = false ;

    protected int life = 100;

    protected float hurtMultiplier = 1f;

    public LinkedList<ProjectileEffect> onHitEffects = new LinkedList<>();

    public ProjectileDefault addOnHitEffects(ProjectileEffect effect){
        onHitEffects.add(effect.setProjectile(this));
        return this;
    }

    public ProjectileDefault(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
    }

    public String getAttackName(){
        return this.getType().getDescriptionId().replace("entity.thedigimod.", "");
    }

    public ProjectileDefault setParticle(ParticleOptions particle){
        this.particle = particle;
        return this;
    }

    public ProjectileDefault setParticle(ItemLike item){
        this.particle =  new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(item));
        return this;
    }

    public void performRangedAttack(@NotNull CustomDigimon mob, @NotNull LivingEntity target){

        this.setBaseDamage(mob.getCurrentLevel());

        this.target = target;

        this.setPos(new Vec3(mob.position().x, mob.getEyeY(), mob.position().z));

        mob.getLookControl().setLookAt(Objects.requireNonNull(mob.getTarget()));

        shootAtTargetWithSpeed(mob, 0.7f);

        this.setRot((float) mob.getLookControl().getWantedX(), (float) mob.getLookControl().getWantedY());

        this.setOwner(mob);

        mob.level().addFreshEntity(this);

        playShootSound();
    }

    protected void shootAtTargetWithSpeed(CustomDigimon mob, float speed) {
        double d0 = target.getX() - mob.getX();
        double d1 = target.getY(0.3333333333333333D) - mob.getEyeY();
        double d2 = target.getZ() - mob.getZ();
        double distance = Math.sqrt(d0 * d0 + d2 * d2);

        double motionX = d0 / distance * speed;
        double motionY = d1 / distance * speed;
        double motionZ = d2 / distance * speed;

        shoot(motionX, motionY, motionZ, speed, 1.0F);
    }

    @Override
    public void tick() {
        if(--life <0)
            this.remove(RemovalReason.UNLOADED_TO_CHUNK);
        super.tick();
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        this.remove(RemovalReason.UNLOADED_TO_CHUNK);
        onHitEffects.forEach(pe -> pe.applyBlockEffects(hit));
    }

    @Override
    protected void onHitEntity(EntityHitResult hitted) {

        if(hurtNotDigimonTarget(hitted.getEntity())){
            if(this.getOwner() instanceof CustomDigimon cd && hitted.getEntity() instanceof LivingEntity lv && !cd.isOwnedBy(lv))
                onHitEffects.forEach(pe -> pe.applyEffects(hitted.getEntity()));
            super.onHitEntity(hitted);
        }

        this.remove(RemovalReason.UNLOADED_TO_CHUNK);
    }

    protected void superOnHitEntity(EntityHitResult hitted){
        super.onHitEntity(hitted);
    }

    protected boolean hurtNotDigimonTarget(Entity hitted){
        if(this.getOwner() instanceof CustomDigimon cd){
            if(hitted instanceof CustomDigimon hcd){
                if(!(hcd.getOwner() != null && cd.getOwner() != null && cd.getOwner().is(hcd.getOwner()))){

                    onHitEffects.forEach(pe -> pe.applyEffects(hitted));

                    hcd.hurt(this.damageSources().mobAttack(cd), this.hurtAndCalculateDigimonDamage(cd, hcd) * hurtMultiplier);
                }
            } else {
                return true;
            }
        }
        return false;
    }

    protected float hurtAndCalculateDigimonDamage(CustomDigimon cd, CustomDigimon hcd) {
        int dmgPlusEffects = getDmgPlusEffects(cd, hcd);

        return cd.calculateDamage(
                dmgPlusEffects / timesRepeat,
                hcd.getSpDefenceStat() + hcd.getCurrentLevel());
    }

    protected int getDmgPlusEffects(CustomDigimon cd, CustomDigimon hcd) {
        int actualStatDmg = (isPhysical ? cd.getAttackStat()/2 : cd.getSpAttackStat());

        int dmgPlusLevel = actualStatDmg + cd.getCurrentLevel();

        boolean isHittingDoppelganger = this.doppelganger &&
                ((cd.digitronEvo != null && cd.digitronEvo.equals(hcd.getLowerCaseSpecies())) ||
                (hcd.digitronEvo != null && hcd.digitronEvo.equals(cd.getLowerCaseSpecies())));

        int dmgPlusEffects = dmgPlusLevel + (isHittingDoppelganger?10:0);
        return dmgPlusEffects;
    }

    private double particleRadius = 0.7d;

    @Override
    public void onRemovedFromWorld() {
        spawnParticles(this.blockPosition());
        super.onRemovedFromWorld();
    }

    protected void superOnRemovedFromWorld() {
        super.onRemovedFromWorld();
    }

    public void spawnParticles(BlockPos blockPos){
        for (int theta = 0; theta < 360; theta += 5) {
            for (int phi = 0; phi < 180; phi += 5) {
                if (random.nextInt(0, 40) == 5) {
                    double thetaRad = Math.toRadians(theta);
                    double phiRad = Math.toRadians(phi);
                    double x = Math.sin(phiRad) * Math.cos(thetaRad);
                    double y = Math.sin(phiRad) * Math.sin(thetaRad);
                    double z = Math.cos(phiRad);

                    double radius = particleRadius;
                    double speed = 0.15d + random.nextDouble() * 0.1d;

                    this.level().addParticle(particle,
                            blockPos.getX() + 1d + radius * x,
                            blockPos.getY() + radius * z,
                            blockPos.getZ() + 1d + radius * y,
                            speed * x, speed * z, speed * y);
                }
            }
        }
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return null;
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    protected float getWaterInertia() {
        return 1f;
    }

    @Override
    public boolean isNoGravity() {return true;}

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() { return DigiSounds.MOVE_HIT.get();}

    protected void playShootSound(){
        this.playSound(this.shootSound, 0.1F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
    }

    public ProjectileDefault setPhysical(){
        isPhysical = true;
        return this;
    }

    public ProjectileDefault setRepeat(int times){
        timesRepeat = times;
        return this;
    }

    public ProjectileDefault setParticleRadius(double radius){
        particleRadius = radius;
        return this;
    }


    public int getRepeatTimes(){
        return timesRepeat;
    }

    public ProjectileDefault setBright(){
        bright = true;
        return this;
    }

    private boolean doppelganger = false;
    public ProjectileDefault setDoppelgangerAttack(){
        doppelganger = true;
        return this;
    }

    SoundEvent shootSound = DigiSounds.DEFAULT_SHOOT.get();

    public ProjectileDefault setShootSound(SoundEvent sound){
        this.shootSound = sound;
        return this;
    }

    //animation stuff

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
}
