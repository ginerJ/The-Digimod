package net.modderg.thedigimod.server.entity.goals;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.modderg.thedigimod.server.entity.DigimonEntity;

public class WaterMoveControl extends MoveControl {
    private final DigimonEntity digimon;

    public WaterMoveControl(DigimonEntity p_27501_) {
        super(p_27501_);
        this.digimon = p_27501_;
    }

    public void tick() {

        if (this.operation == MoveControl.Operation.MOVE_TO && !this.digimon.getNavigation().isDone()) {
            float f = (float) (this.speedModifier * this.digimon.getAttributeValue(Attributes.MOVEMENT_SPEED));
            this.digimon.setSpeed(Mth.lerp(0.125F, this.digimon.getSpeed(), f));
            double d0 = this.wantedX - this.digimon.getX();
            double d1 = this.wantedY - this.digimon.getY();
            double d2 = this.wantedZ - this.digimon.getZ();

            if (d0 != 0.0D || d2 != 0.0D) {
                float horizontalAngle = (float) (Mth.atan2(d2, d0) * (180F / Math.PI)) - 90.0F;
                double horizontalDistance = Math.sqrt(d0 * d0 + d2 * d2);
                double horizontalSpeed = this.digimon.getSpeed() * 0.15D;

                double verticalSpeed = horizontalSpeed * (d1 / horizontalDistance) * 0.01D;

                this.digimon.setDeltaMovement(this.digimon.getDeltaMovement().add(
                        horizontalSpeed * (d0 / horizontalDistance),
                        verticalSpeed,
                        horizontalSpeed * (d2 / horizontalDistance)
                ));

                this.digimon.setYRot(this.rotlerp(this.digimon.getYRot(), horizontalAngle, 90.0F));
                this.digimon.yBodyRot = this.digimon.getYRot();
            }
        } else {
            this.digimon.setSpeed(0.0F);
        }
    }
}
