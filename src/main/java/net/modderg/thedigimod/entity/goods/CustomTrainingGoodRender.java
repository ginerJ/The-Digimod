package net.modderg.thedigimod.entity.goods;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CustomTrainingGoodRender<D extends CustomTrainingGood> extends GeoEntityRenderer<CustomTrainingGood> {

    public CustomTrainingGoodRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel<CustomTrainingGood>) new CustomTrainingGoodModel<>());
    }
}
