package net.modderg.thedigimod.client.particles.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LifeParticle extends SimpleAnimatedParticle {
    LifeParticle(ClientLevel p_108346_, double p_108347_, double p_108348_, double p_108349_, double p_108350_, double p_108351_, double p_108352_, SpriteSet p_108353_) {
        super(p_108346_, p_108347_, p_108348_, p_108349_, p_108353_, 1.25F);
        this.friction = 0.6F;
        this.xd = p_108350_;
        this.yd = p_108351_;
        this.zd = p_108352_;
        this.quadSize *= 0.75F;
        this.lifetime = 60 + this.random.nextInt(12);
        this.setSpriteFromAge(p_108353_);
        this.setFadeColor(0x00BFFF);
        this.setColor(this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.3F, 0.4F + this.random.nextFloat() * 0.6F);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet p_108366_) {
            this.sprites = p_108366_;
        }

        public Particle createParticle(SimpleParticleType p_108377_, ClientLevel p_108378_, double p_108379_, double p_108380_, double p_108381_, double p_108382_, double p_108383_, double p_108384_) {
            return new LifeParticle(p_108378_, p_108379_, p_108380_, p_108381_, p_108382_, p_108383_, p_108384_, this.sprites);
        }
    }

}
