package net.modderg.thedigimod.projectiles.models;

import net.minecraft.resources.ResourceLocation;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.projectiles.CustomProjectile;
import net.modderg.thedigimod.projectiles.PepperBreath;
import software.bernie.geckolib.model.GeoModel;

public class CustomProjectileModel<D extends CustomProjectile> extends GeoModel<CustomProjectile> {

    @Override
    public ResourceLocation getModelResource(CustomProjectile projectile) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "geo/" + projectile.getAttackName() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CustomProjectile projectile) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "textures/projectiles/" + projectile.getAttackName() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(CustomProjectile projectile) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "animations/projectiles_anims.json");
    }
}