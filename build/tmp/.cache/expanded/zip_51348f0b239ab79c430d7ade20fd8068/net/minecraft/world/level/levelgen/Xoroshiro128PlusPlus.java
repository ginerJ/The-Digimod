package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.stream.LongStream;
import net.minecraft.Util;

public class Xoroshiro128PlusPlus {
   private long seedLo;
   private long seedHi;
   public static final Codec<Xoroshiro128PlusPlus> CODEC = Codec.LONG_STREAM.comapFlatMap((p_287733_) -> {
      return Util.fixedSize(p_287733_, 2).map((p_287742_) -> {
         return new Xoroshiro128PlusPlus(p_287742_[0], p_287742_[1]);
      });
   }, (p_287687_) -> {
      return LongStream.of(p_287687_.seedLo, p_287687_.seedHi);
   });

   public Xoroshiro128PlusPlus(RandomSupport.Seed128bit p_190095_) {
      this(p_190095_.seedLo(), p_190095_.seedHi());
   }

   public Xoroshiro128PlusPlus(long p_190092_, long p_190093_) {
      this.seedLo = p_190092_;
      this.seedHi = p_190093_;
      if ((this.seedLo | this.seedHi) == 0L) {
         this.seedLo = -7046029254386353131L;
         this.seedHi = 7640891576956012809L;
      }

   }

   public long nextLong() {
      long i = this.seedLo;
      long j = this.seedHi;
      long k = Long.rotateLeft(i + j, 17) + i;
      j ^= i;
      this.seedLo = Long.rotateLeft(i, 49) ^ j ^ j << 21;
      this.seedHi = Long.rotateLeft(j, 28);
      return k;
   }
}