package net.modderg.thedigimod.entity.goods;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SpTargetRender<D extends SpTarget> extends GeoEntityRenderer<SpTarget> {

    public SpTargetRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel<SpTarget>) new SpTargetModel<>());
    }


}
