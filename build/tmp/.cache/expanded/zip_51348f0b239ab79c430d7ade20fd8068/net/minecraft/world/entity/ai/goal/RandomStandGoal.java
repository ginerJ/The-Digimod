package net.minecraft.world.entity.ai.goal;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class RandomStandGoal extends Goal {
   private final AbstractHorse horse;
   private int nextStand;

   public RandomStandGoal(AbstractHorse p_251685_) {
      this.horse = p_251685_;
      this.resetStandInterval(p_251685_);
   }

   public void start() {
      this.horse.standIfPossible();
      this.playStandSound();
   }

   private void playStandSound() {
      SoundEvent soundevent = this.horse.getAmbientStandSound();
      if (soundevent != null) {
         this.horse.playSound(soundevent);
      }

   }

   public boolean canContinueToUse() {
      return false;
   }

   public boolean canUse() {
      ++this.nextStand;
      if (this.nextStand > 0 && this.horse.getRandom().nextInt(1000) < this.nextStand) {
         this.resetStandInterval(this.horse);
         return !this.horse.isImmobile() && this.horse.getRandom().nextInt(10) == 0;
      } else {
         return false;
      }
   }

   private void resetStandInterval(AbstractHorse p_250439_) {
      this.nextStand = -p_250439_.getAmbientStandInterval();
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }
}