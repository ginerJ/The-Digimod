package net.modderg.thedigimod.projectiles.renders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.projectiles.PepperBreath;
import net.modderg.thedigimod.projectiles.models.CustomProjectileModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class CustomProjectileRender extends GeoProjectilesRenderer {

    public CustomProjectileRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (AnimatedGeoModel<PepperBreath>) new CustomProjectileModel());
    }
}
