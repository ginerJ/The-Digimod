package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class WeightedListHeight extends HeightProvider {
   public static final Codec<WeightedListHeight> CODEC = RecordCodecBuilder.create((p_191539_) -> {
      return p_191539_.group(SimpleWeightedRandomList.wrappedCodec(HeightProvider.CODEC).fieldOf("distribution").forGetter((p_191541_) -> {
         return p_191541_.distribution;
      })).apply(p_191539_, WeightedListHeight::new);
   });
   private final SimpleWeightedRandomList<HeightProvider> distribution;

   public WeightedListHeight(SimpleWeightedRandomList<HeightProvider> p_191536_) {
      this.distribution = p_191536_;
   }

   public int sample(RandomSource p_226314_, WorldGenerationContext p_226315_) {
      return this.distribution.getRandomValue(p_226314_).orElseThrow(IllegalStateException::new).sample(p_226314_, p_226315_);
   }

   public HeightProviderType<?> getType() {
      return HeightProviderType.WEIGHTED_LIST;
   }
}