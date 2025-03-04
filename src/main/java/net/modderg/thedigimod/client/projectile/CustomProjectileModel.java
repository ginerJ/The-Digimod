package net.modderg.thedigimod.client.projectile;

import net.minecraft.resources.ResourceLocation;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.server.projectiles.ProjectileDefault;
import net.modderg.thedigimod.server.projectiles.ProjectileLaserDefault;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class CustomProjectileModel<D extends ProjectileDefault> extends GeoModel<ProjectileDefault> {

    @Override
    public ResourceLocation getModelResource(ProjectileDefault projectile) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "geo/moves/" + projectile.getAttackName() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ProjectileDefault projectile) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "textures/moves/" + projectile.getAttackName() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(ProjectileDefault projectile) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "animations/projectiles_anims.json");
    }

    @Override
    public void setCustomAnimations(ProjectileDefault animatable, long instanceId, AnimationState<ProjectileDefault> animationState) {

        if(animatable instanceof ProjectileLaserDefault laserProj){
            CoreGeoBone laser = getAnimationProcessor().getBone("laser");

            if (laser != null) {
                laser.setScaleZ(laserProj.distanceToTarget);
            }
        }

        super.setCustomAnimations(animatable, instanceId, animationState);
    }
}