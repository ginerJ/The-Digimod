package net.modderg.thedigimod.entity.goods;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SpTableBookRender<D extends SpTableBook> extends GeoEntityRenderer<SpTableBook> {

    public SpTableBookRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel<SpTableBook>) new SpTableBookModel<>());
    }


}
