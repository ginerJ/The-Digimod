package net.modderg.thedigimod.entity.goods;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.projectiles.models.CustomProjectileModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PunchingBagRender<D extends PunchingBag> extends GeoEntityRenderer<PunchingBag> {

    public PunchingBagRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel<PunchingBag>) new PunchingBagModel<>());
    }
}
