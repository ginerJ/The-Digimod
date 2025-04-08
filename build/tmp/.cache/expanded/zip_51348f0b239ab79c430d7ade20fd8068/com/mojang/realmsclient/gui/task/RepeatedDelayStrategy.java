package com.mojang.realmsclient.gui.task;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public interface RepeatedDelayStrategy {
   RepeatedDelayStrategy CONSTANT = new RepeatedDelayStrategy() {
      public long delayCyclesAfterSuccess() {
         return 1L;
      }

      public long delayCyclesAfterFailure() {
         return 1L;
      }
   };

   long delayCyclesAfterSuccess();

   long delayCyclesAfterFailure();

   static RepeatedDelayStrategy exponentialBackoff(final int p_239256_) {
      return new RepeatedDelayStrategy() {
         private static final Logger LOGGER = LogUtils.getLogger();
         private int failureCount;

         public long delayCyclesAfterSuccess() {
            this.failureCount = 0;
            return 1L;
         }

         public long delayCyclesAfterFailure() {
            ++this.failureCount;
            long i = Math.min(1L << this.failureCount, (long)p_239256_);
            LOGGER.debug("Skipping for {} extra cycles", (long)i);
            return i;
         }
      };
   }
}