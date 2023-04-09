package net.modderg.thedigimod.entity.goods;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShieldStandRender<D extends ShieldStand> extends GeoEntityRenderer<ShieldStand> {

    public ShieldStandRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel<ShieldStand>) new ShieldStandModel<>());
    }


}
