package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ParticleProvider<T extends ParticleOptions> {
   @Nullable
   Particle createParticle(T p_107421_, ClientLevel p_107422_, double p_107423_, double p_107424_, double p_107425_, double p_107426_, double p_107427_, double p_107428_);

   @OnlyIn(Dist.CLIENT)
   public interface Sprite<T extends ParticleOptions> {
      @Nullable
      TextureSheetParticle createParticle(T p_273550_, ClientLevel p_273071_, double p_273160_, double p_273576_, double p_272710_, double p_273652_, double p_273457_, double p_272840_);
   }
}