package net.minecraft.util;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.ThreadSafeLegacyRandomSource;

public interface RandomSource {
   /** @deprecated */
   @Deprecated
   double GAUSSIAN_SPREAD_FACTOR = 2.297D;

   static RandomSource create() {
      return create(RandomSupport.generateUniqueSeed());
   }

   /** @deprecated */
   @Deprecated
   static RandomSource createThreadSafe() {
      return new ThreadSafeLegacyRandomSource(RandomSupport.generateUniqueSeed());
   }

   static RandomSource create(long p_216336_) {
      return new LegacyRandomSource(p_216336_);
   }

   static RandomSource createNewThreadLocalInstance() {
      return new SingleThreadedRandomSource(ThreadLocalRandom.current().nextLong());
   }

   RandomSource fork();

   PositionalRandomFactory forkPositional();

   void setSeed(long p_216342_);

   int nextInt();

   int nextInt(int p_216331_);

   default int nextIntBetweenInclusive(int p_216333_, int p_216334_) {
      return this.nextInt(p_216334_ - p_216333_ + 1) + p_216333_;
   }

   long nextLong();

   boolean nextBoolean();

   float nextFloat();

   double nextDouble();

   double nextGaussian();

   default double triangle(double p_216329_, double p_216330_) {
      return p_216329_ + p_216330_ * (this.nextDouble() - this.nextDouble());
   }

   default void consumeCount(int p_216338_) {
      for(int i = 0; i < p_216338_; ++i) {
         this.nextInt();
      }

   }

   default int nextInt(int p_216340_, int p_216341_) {
      if (p_216340_ >= p_216341_) {
         throw new IllegalArgumentException("bound - origin is non positive");
      } else {
         return p_216340_ + this.nextInt(p_216341_ - p_216340_);
      }
   }
}