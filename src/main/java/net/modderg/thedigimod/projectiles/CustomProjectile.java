package net.modderg.thedigimod.projectiles;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.DigitalEntities;
import org.jetbrains.annotations.Nullable;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import static software.bernie.geckolib3.util.GeckoLibUtil.createFactory;

public class CustomProjectile extends AbstractArrow implements IAnimatable {

    protected String attack = "pepper_breath";
    public String getAttackName(){
        return attack;
    }

    public int life = 80;

    public CustomProjectile(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
    }

    public void setOwner(@Nullable CustomDigimon p_36770_) {
        super.setOwner(p_36770_);
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
    protected void onHitEntity(EntityHitResult p_36757_) {
        if(!p_36757_.getEntity().equals(getOwner())){
            this.remove(RemovalReason.UNLOADED_TO_CHUNK);
        }
        super.onHitEntity(p_36757_);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected ItemStack getPickupItem() {
        return null;
    }

    protected AnimationFactory factory = createFactory(this);

    private <E extends IAnimatable> PlayState animController(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<CustomProjectile>(this, "controller", 0, this::animController));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
