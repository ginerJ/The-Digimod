package net.modderg.thedigimod.client.goods;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.server.goods.AbstractTrainingGood;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AbstractGoodRender<D extends AbstractTrainingGood> extends GeoEntityRenderer<AbstractTrainingGood> {

    public AbstractGoodRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel<AbstractTrainingGood>) new AbstractGoodModel<>());
    }
}
