package net.modderg.thedigimod.client.particles.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RegularDigitalParticle extends TextureSheetParticle {
    private final int lifetime;

    protected RegularDigitalParticle(ClientLevel level, double xCoord, double yCoord, double zCoord,
                                     SpriteSet spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);
        this.friction = 0.8F;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 1F;
        this.lifetime = 100;
        this.setSpriteFromAge(spriteSet);
        this.gravity = 0.4f;
        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
    }

    @Override
    public void tick() {
        super.tick();
        fadeOut();
    }

    private void fadeOut(){
        this.alpha = (-(1/(float)lifetime * 2) * age + 1);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType>{
        private final SpriteSet sprites;

        public  Provider (SpriteSet spriteSet){
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz){
            return  new RegularDigitalParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
