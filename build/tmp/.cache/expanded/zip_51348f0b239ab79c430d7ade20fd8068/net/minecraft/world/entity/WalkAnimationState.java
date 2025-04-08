package net.minecraft.world.entity;

import net.minecraft.util.Mth;

public class WalkAnimationState {
   private float speedOld;
   private float speed;
   private float position;

   public void setSpeed(float p_268265_) {
      this.speed = p_268265_;
   }

   public void update(float p_267993_, float p_267967_) {
      this.speedOld = this.speed;
      this.speed += (p_267993_ - this.speed) * p_267967_;
      this.position += this.speed;
   }

   public float speed() {
      return this.speed;
   }

   public float speed(float p_268054_) {
      return Mth.lerp(p_268054_, this.speedOld, this.speed);
   }

   public float position() {
      return this.position;
   }

   public float position(float p_268007_) {
      return this.position - this.speed * (1.0F - p_268007_);
   }

   public boolean isMoving() {
      return this.speed > 1.0E-5F;
   }
}