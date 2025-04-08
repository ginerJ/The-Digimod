package net.minecraft.client.player;

import net.minecraft.client.Options;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyboardInput extends Input {
   private final Options options;

   public KeyboardInput(Options p_108580_) {
      this.options = p_108580_;
   }

   private static float calculateImpulse(boolean p_205578_, boolean p_205579_) {
      if (p_205578_ == p_205579_) {
         return 0.0F;
      } else {
         return p_205578_ ? 1.0F : -1.0F;
      }
   }

   public void tick(boolean p_234118_, float p_234119_) {
      this.up = this.options.keyUp.isDown();
      this.down = this.options.keyDown.isDown();
      this.left = this.options.keyLeft.isDown();
      this.right = this.options.keyRight.isDown();
      this.forwardImpulse = calculateImpulse(this.up, this.down);
      this.leftImpulse = calculateImpulse(this.left, this.right);
      this.jumping = this.options.keyJump.isDown();
      this.shiftKeyDown = this.options.keyShift.isDown();
      if (p_234118_) {
         this.leftImpulse *= p_234119_;
         this.forwardImpulse *= p_234119_;
      }

   }
}