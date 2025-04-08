package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;

public class AmbientParticleSettings {
   public static final Codec<AmbientParticleSettings> CODEC = RecordCodecBuilder.create((p_47423_) -> {
      return p_47423_.group(ParticleTypes.CODEC.fieldOf("options").forGetter((p_151654_) -> {
         return p_151654_.options;
      }), Codec.FLOAT.fieldOf("probability").forGetter((p_151652_) -> {
         return p_151652_.probability;
      })).apply(p_47423_, AmbientParticleSettings::new);
   });
   private final ParticleOptions options;
   private final float probability;

   public AmbientParticleSettings(ParticleOptions p_47417_, float p_47418_) {
      this.options = p_47417_;
      this.probability = p_47418_;
   }

   public ParticleOptions getOptions() {
      return this.options;
   }

   public boolean canSpawn(RandomSource p_220528_) {
      return p_220528_.nextFloat() <= this.probability;
   }
}