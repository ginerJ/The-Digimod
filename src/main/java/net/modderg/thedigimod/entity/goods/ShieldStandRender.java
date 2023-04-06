package net.modderg.thedigimod.entity.goods;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class ShieldStandRender extends GeoProjectilesRenderer {

    public ShieldStandRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (AnimatedGeoModel<ShieldStand>) new ShieldStandModel<>());
    }


}
