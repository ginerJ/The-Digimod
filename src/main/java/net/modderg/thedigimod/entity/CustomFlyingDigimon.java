package net.modderg.thedigimod.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED;

public class CustomFlyingDigimon extends CustomDigimon implements PlayerRideableJumping {
    protected CustomFlyingDigimon(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
    }

    @Override
    public void setMovementID(int i) {
        switchNavigation(i);
        super.setMovementID(i);
    }

    @Override
    public void changeMovementID(){
        int i = this.getMovementID();

        if(i == 1){
            messageState("flying");
            setMovementID(2);
            return;

        } if(i == 2)
            setMovementID(1);

        super.changeMovementID();
    }


    protected void switchNavigation(int b) {
        if (b == 2 && !(moveControl instanceof FlyingMoveControl)) {
            this.moveControl = new FlyingMoveControl(this, 20, true);
            this.navigation = new FlyingPathNavigation(this, this.level());
        } else if (b != 2 && moveControl instanceof FlyingMoveControl) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, this.level());
            this.setNoGravity(false);
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor p_146746_, @NotNull DifficultyInstance p_146747_, @NotNull MobSpawnType p_146748_, @Nullable SpawnGroupData p_146749_, @Nullable CompoundTag p_146750_) {
        if(!this.isTame())
            setMovementID(2);

        return super.finalizeSpawn(p_146746_, p_146747_, p_146748_, p_146749_, p_146750_);
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return this.getMovementID() == 2;
    }

    @Override
    public void travel(@NotNull Vec3 pos) {
        if(!this.isEvolving()) {
            if (this.isAlive()) {
                if (getMovementID() == 2) {
                    if (this.canBeControlledByRider() && this.getFirstPassenger() instanceof LivingEntity passenger) {

                        float movY = (float) (-passenger.getXRot() * (Math.PI / 180)) / 3;

                        Vec3 newPos = new Vec3(pos.x, movY, pos.z);

                        if(!this.onGround())
                            moveRelative(flyingTravelRideSpeed(), newPos);

                    } else if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
                        if (this.isInWater()) {
                            this.moveRelative(0.02F, pos);
                            this.move(MoverType.SELF, this.getDeltaMovement());
                            this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
                        } else if (this.isInLava()) {
                            this.moveRelative(0.02F, pos);
                            this.move(MoverType.SELF, this.getDeltaMovement());
                            this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
                        } else {
                            this.moveRelative(this.getSpeed(), pos);
                            this.move(MoverType.SELF, this.getDeltaMovement());
                            this.setDeltaMovement(this.getDeltaMovement().scale(0.91F));
                        }
                    }
                }
                super.travel(pos);
            }
        }
    }

    protected float flyingTravelRideSpeed(){
        float baseValue = (float) this.getAttribute(Attributes.FLYING_SPEED).getBaseValue() * (this.isChampion() ? 1f: this.isUltimate() ? 1.2f : 1.5f);

        if(!this.isInWater())
            return baseValue;
        else
            return baseValue / 3;
    }

    @Override
    protected float travelRideSpeed(){
        return (float) this.getAttribute(MOVEMENT_SPEED).getValue();
    }

    @Override
    public void onPlayerJump(int p_21696_) {
        if (this.getMovementID() != 2) this.setMovementID(2);
        else this.setMovementID(1);
    }

    @Override
    public boolean canJump() {return true;}

    @Override
    public void handleStartJump(int p_21695_) {}

    @Override
    public void handleStopJump() {}

    //Animations

    protected AnimationController<CustomDigimon> getMovementController(){
        return flyingAnimController(this);
    }

    public static <T extends CustomDigimon & GeoEntity> AnimationController<T> flyingAnimController(T digimon) {
        return new AnimationController<>(digimon, "movement", 10, event -> {

            if (digimon.isEvolving()) {
                event.getController().setAnimation(RawAnimation.begin().then("show", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }
            if (digimon.isInSittingPose()) {
                event.getController().setAnimation(RawAnimation.begin().then(digimon.sitAnim, Animation.LoopType.HOLD_ON_LAST_FRAME));
                return PlayState.CONTINUE;
            }

            if(digimon.getMovementID() == 2){
                event.getController().setAnimation(RawAnimation.begin().then(digimon.flyAnim, Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            } else if (event.isMoving()) {
                
                event.getController().setAnimation(RawAnimation.begin().then(digimon.walkAnim, Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            } else {
                event.getController().setAnimation(RawAnimation.begin().then(digimon.idleAnim, Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }
        });
    }
}
