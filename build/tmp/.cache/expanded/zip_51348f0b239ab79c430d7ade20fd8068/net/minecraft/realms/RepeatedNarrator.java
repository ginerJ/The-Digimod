package net.minecraft.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.GameNarrator;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RepeatedNarrator {
   private final float permitsPerSecond;
   private final AtomicReference<RepeatedNarrator.Params> params = new AtomicReference<>();

   public RepeatedNarrator(Duration p_120788_) {
      this.permitsPerSecond = 1000.0F / (float)p_120788_.toMillis();
   }

   public void narrate(GameNarrator p_240528_, Component p_240604_) {
      RepeatedNarrator.Params repeatednarrator$params = this.params.updateAndGet((p_175080_) -> {
         return p_175080_ != null && p_240604_.equals(p_175080_.narration) ? p_175080_ : new RepeatedNarrator.Params(p_240604_, RateLimiter.create((double)this.permitsPerSecond));
      });
      if (repeatednarrator$params.rateLimiter.tryAcquire(1)) {
         p_240528_.sayNow(p_240604_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   static class Params {
      final Component narration;
      final RateLimiter rateLimiter;

      Params(Component p_175082_, RateLimiter p_175083_) {
         this.narration = p_175082_;
         this.rateLimiter = p_175083_;
      }
   }
}