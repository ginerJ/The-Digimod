package net.minecraft.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

public class RandomSequence {
   public static final Codec<RandomSequence> CODEC = RecordCodecBuilder.create((p_287586_) -> {
      return p_287586_.group(XoroshiroRandomSource.CODEC.fieldOf("source").forGetter((p_287757_) -> {
         return p_287757_.source;
      })).apply(p_287586_, RandomSequence::new);
   });
   private final XoroshiroRandomSource source;

   public RandomSequence(XoroshiroRandomSource p_287597_) {
      this.source = p_287597_;
   }

   public RandomSequence(long p_287592_, ResourceLocation p_287762_) {
      this(createSequence(p_287592_, p_287762_));
   }

   private static XoroshiroRandomSource createSequence(long p_289567_, ResourceLocation p_289545_) {
      return new XoroshiroRandomSource(RandomSupport.upgradeSeedTo128bitUnmixed(p_289567_).xor(seedForKey(p_289545_)).mixed());
   }

   public static RandomSupport.Seed128bit seedForKey(ResourceLocation p_288989_) {
      return RandomSupport.seedFromHashOf(p_288989_.toString());
   }

   public RandomSource random() {
      return this.source;
   }
}