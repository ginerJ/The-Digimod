package net.modderg.thedigimod.entity.goods;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class UpdateGoodRender<D extends UpdateGood> extends GeoEntityRenderer<UpdateGood> {

    public UpdateGoodRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel<UpdateGood>) new UpdateGoodModel<>());
    }


}
