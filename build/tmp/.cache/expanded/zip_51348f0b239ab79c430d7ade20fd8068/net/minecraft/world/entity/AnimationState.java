package net.minecraft.world.entity;

import java.util.function.Consumer;
import net.minecraft.util.Mth;

public class AnimationState {
   private static final long STOPPED = Long.MAX_VALUE;
   private long lastTime = Long.MAX_VALUE;
   private long accumulatedTime;

   public void start(int p_216978_) {
      this.lastTime = (long)p_216978_ * 1000L / 20L;
      this.accumulatedTime = 0L;
   }

   public void startIfStopped(int p_216983_) {
      if (!this.isStarted()) {
         this.start(p_216983_);
      }

   }

   public void animateWhen(boolean p_252220_, int p_249486_) {
      if (p_252220_) {
         this.startIfStopped(p_249486_);
      } else {
         this.stop();
      }

   }

   public void stop() {
      this.lastTime = Long.MAX_VALUE;
   }

   public void ifStarted(Consumer<AnimationState> p_216980_) {
      if (this.isStarted()) {
         p_216980_.accept(this);
      }

   }

   public void updateTime(float p_216975_, float p_216976_) {
      if (this.isStarted()) {
         long i = Mth.lfloor((double)(p_216975_ * 1000.0F / 20.0F));
         this.accumulatedTime += (long)((float)(i - this.lastTime) * p_216976_);
         this.lastTime = i;
      }
   }

   public long getAccumulatedTime() {
      return this.accumulatedTime;
   }

   public boolean isStarted() {
      return this.lastTime != Long.MAX_VALUE;
   }
}