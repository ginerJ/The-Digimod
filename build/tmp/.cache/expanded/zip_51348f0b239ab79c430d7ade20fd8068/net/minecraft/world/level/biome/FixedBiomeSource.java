package net.minecraft.world.level.biome;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;

public class FixedBiomeSource extends BiomeSource implements BiomeManager.NoiseBiomeSource {
   public static final Codec<FixedBiomeSource> CODEC = Biome.CODEC.fieldOf("biome").xmap(FixedBiomeSource::new, (p_204259_) -> {
      return p_204259_.biome;
   }).stable().codec();
   private final Holder<Biome> biome;

   public FixedBiomeSource(Holder<Biome> p_204257_) {
      this.biome = p_204257_;
   }

   protected Stream<Holder<Biome>> collectPossibleBiomes() {
      return Stream.of(this.biome);
   }

   protected Codec<? extends BiomeSource> codec() {
      return CODEC;
   }

   public Holder<Biome> getNoiseBiome(int p_204265_, int p_204266_, int p_204267_, Climate.Sampler p_204268_) {
      return this.biome;
   }

   public Holder<Biome> getNoiseBiome(int p_204261_, int p_204262_, int p_204263_) {
      return this.biome;
   }

   @Nullable
   public Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(int p_220640_, int p_220641_, int p_220642_, int p_220643_, int p_220644_, Predicate<Holder<Biome>> p_220645_, RandomSource p_220646_, boolean p_220647_, Climate.Sampler p_220648_) {
      if (p_220645_.test(this.biome)) {
         return p_220647_ ? Pair.of(new BlockPos(p_220640_, p_220641_, p_220642_), this.biome) : Pair.of(new BlockPos(p_220640_ - p_220643_ + p_220646_.nextInt(p_220643_ * 2 + 1), p_220641_, p_220642_ - p_220643_ + p_220646_.nextInt(p_220643_ * 2 + 1)), this.biome);
      } else {
         return null;
      }
   }

   @Nullable
   public Pair<BlockPos, Holder<Biome>> findClosestBiome3d(BlockPos p_220650_, int p_220651_, int p_220652_, int p_220653_, Predicate<Holder<Biome>> p_220654_, Climate.Sampler p_220655_, LevelReader p_220656_) {
      return p_220654_.test(this.biome) ? Pair.of(p_220650_, this.biome) : null;
   }

   public Set<Holder<Biome>> getBiomesWithin(int p_187038_, int p_187039_, int p_187040_, int p_187041_, Climate.Sampler p_187042_) {
      return Sets.newHashSet(Set.of(this.biome));
   }
}