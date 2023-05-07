package net.modderg.thedigimod.projectiles;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec2;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.DigitalEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class CustomProjectile extends AbstractArrow implements GeoEntity {

    protected String attack = "pepper_breath";
    public String getAttackName(){
        return attack;
    }

    public int life = 80;

    public CustomProjectile(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
    }

    @Override
    public void tick() {
        life--;
        if(life <0){
            this.remove(RemovalReason.UNLOADED_TO_CHUNK);
        }
        super.tick();
    }

    @Override
    protected void onHitBlock(BlockHitResult p_36755_) {
        this.remove(RemovalReason.UNLOADED_TO_CHUNK);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitted) {
        if(hitted.getEntity() instanceof CustomDigimon hcd && this.getOwner() instanceof CustomDigimon cd){
            if(!(hcd.getOwner() != null && cd.getOwner() != null && cd.getOwner().is(hcd.getOwner()))){
                hcd.hurt(DamageSource.mobAttack(cd), cd.calculateDamage(cd.getSpAttackStat(), hcd.getSpDefenceStat()));
            }
        }else {super.onHitEntity(hitted);}
        this.remove(RemovalReason.UNLOADED_TO_CHUNK);
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
        controllers.add(new AnimationController<CustomProjectile>(this, "controller", 0, this::animController));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }
}
