package net.modderg.thedigimod.particles.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import oshi.software.os.mac.MacInternetProtocolStats;

public class ShootParticles extends TextureSheetParticle {
    private final int Lifetime;
    private String name;

    protected ShootParticles(ClientLevel level, double xCoord, double yCoord, double zCoord,
                             SpriteSet spriteSet, double xd, double yd, double zd, String name) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        this.name = name;
        this.setSprite(getSpriteFromTextureName(name));

        this.friction = 0.8F;
        this.xd = xd;
        this.yd = 1.5d * yd;
        this.zd = zd;
        this.quadSize *= 1F;
        this.Lifetime = 30;
        this.setSpriteFromAge(spriteSet);

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
    }

    private TextureAtlasSprite getSpriteFromTextureName(String textureName) {
        ResourceLocation textureLocation = new ResourceLocation("thedigimod", "particles/" + textureName);
        return Minecraft.getInstance().getTextureAtlas(textureLocation).apply(textureLocation);
    }

    @Override
    public void tick() {
        super.tick();
        fadeOut();
    }

    private void fadeOut(){
        this.alpha = (-(1/(float)lifetime) * age +1);
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
            return  new ShootParticles(level, x, y, z, this.sprites, dx, dy, dz, "digi_meat");
        }
    }
}
