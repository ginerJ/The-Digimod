package net.minecraft.core.particles;

import com.mojang.serialization.Codec;

public abstract class ParticleType<T extends ParticleOptions> {
   private final boolean overrideLimiter;
   private final ParticleOptions.Deserializer<T> deserializer;

   public ParticleType(boolean p_123740_, ParticleOptions.Deserializer<T> p_123741_) {
      this.overrideLimiter = p_123740_;
      this.deserializer = p_123741_;
   }

   public boolean getOverrideLimiter() {
      return this.overrideLimiter;
   }

   public ParticleOptions.Deserializer<T> getDeserializer() {
      return this.deserializer;
   }

   public abstract Codec<T> codec();
}