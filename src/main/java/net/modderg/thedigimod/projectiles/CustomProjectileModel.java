package net.modderg.thedigimod.projectiles;

import net.minecraft.resources.ResourceLocation;
import net.modderg.thedigimod.TheDigiMod;
import software.bernie.geckolib.model.GeoModel;

public class CustomProjectileModel<D extends CustomProjectile> extends GeoModel<CustomProjectile> {

    @Override
    public ResourceLocation getModelResource(CustomProjectile projectile) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "geo/moves/" + projectile.attack + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CustomProjectile projectile) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "textures/moves/" + projectile.attack + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(CustomProjectile projectile) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "animations/projectiles_anims.json");
    }
}