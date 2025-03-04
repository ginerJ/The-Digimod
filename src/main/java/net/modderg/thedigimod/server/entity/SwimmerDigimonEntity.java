package net.modderg.thedigimod.server.entity;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import net.modderg.thedigimod.server.entity.goals.DigitalWaterPathNavigation;
import net.modderg.thedigimod.server.entity.goals.WaterMoveControl;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import static net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED;

public class SwimmerDigimonEntity extends DigimonEntity {
    protected SwimmerDigimonEntity(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
    }

    @Override
    public void tick() {
        super.tick();
        switchNavigation(this.isInWater()? 2 : 1);
    }

    protected void switchNavigation(int b) {
        if (b == 2 && !(moveControl instanceof WaterMoveControl)) {
            this.moveControl = new WaterMoveControl(this);
            this.navigation = new DigitalWaterPathNavigation(this, this.level());
        } else if (b != 2 && moveControl instanceof WaterMoveControl) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, this.level());
            this.setMaxUpStep(1.1f);
        }
    }

    @Override
    public boolean isNoGravity() {
        return this.isInWater();
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        if (type == ForgeMod.WATER_TYPE.get()) return false;
        return super.canDrownInFluidType(type);
    }

    @Override
    public void travel(@NotNull Vec3 pos) {
        if(!this.isEvolving()) {
            if (this.isAlive()) {
                if (this.isInWater() && this.canBeControlledByRider() && this.getFirstPassenger() instanceof LivingEntity passenger) {

                    float movY = (float) (-passenger.getXRot() * (Math.PI / 180));

                    pos = new Vec3(pos.x, movY, pos.z);
                }
                super.travel(pos);
            }
        }
    }

    @Override
    protected float travelRideSpeed(){
        float baseVal = (float) this.getAttribute(MOVEMENT_SPEED).getValue() * (this.isChampion() ? 1f: this.isUltimate() ? 1.2f : 1.5f);
        if(!this.isInWater())
            return baseVal / 4;
        else return baseVal * (this.onGround() ? 1 : 4);
    }

    protected boolean isAffectedByFluids() {
        return false;
    }

    //Animations

    protected AnimationController<DigimonEntity> getMovementController(){
        return waterAnimController(this);
    }

    public static <T extends DigimonEntity & GeoEntity> AnimationController<T> waterAnimController(T digimon) {
        return new AnimationController<>(digimon, "movement", 10, event -> {

            long time = digimon.level().getDayTime() % 24000;
            boolean night = digimon.sleeps && time > 13000 && time < 23000;

            if (digimon.isEvolving()) {
                event.getController().setAnimation(RawAnimation.begin().then("show", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }
            if (digimon.isInSittingPose()) {
                event.getController().setAnimation(RawAnimation.begin().then(night ? "sleep" : digimon.sitAnim, Animation.LoopType.HOLD_ON_LAST_FRAME));
                return PlayState.CONTINUE;
            }

            if (event.isMoving()) {
                if(digimon.isInWater())
                    event.getController().setAnimation(RawAnimation.begin().then(digimon.flyAnim, Animation.LoopType.LOOP));
                else{
                    
                    event.getController().setAnimation(RawAnimation.begin().then(digimon.walkAnim, Animation.LoopType.LOOP));
                }
                return PlayState.CONTINUE;
            } else {
                event.getController().setAnimation(RawAnimation.begin().then(digimon.idleAnim, Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }
        });
    }
}
