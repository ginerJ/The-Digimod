package net.modderg.thedigimod.entity.goods;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class SpTargetRender extends GeoProjectilesRenderer {

    public SpTargetRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (AnimatedGeoModel<SpTarget>) new SpTargetModel<>());
    }


}
