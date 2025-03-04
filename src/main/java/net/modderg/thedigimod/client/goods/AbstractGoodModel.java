package net.modderg.thedigimod.client.goods;


import net.minecraft.resources.ResourceLocation;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.server.goods.AbstractTrainingGood;
import software.bernie.geckolib.model.GeoModel;

public class AbstractGoodModel<D extends AbstractTrainingGood> extends GeoModel<AbstractTrainingGood> {

    @Override
    public ResourceLocation getModelResource(AbstractTrainingGood good) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "geo/training_goods/"+good.getGoodName()+".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AbstractTrainingGood good) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "textures/training_goods/"+good.getGoodName()+".png");
    }

    @Override
    public ResourceLocation getAnimationResource(AbstractTrainingGood projectile) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "animations/good_anims.json");
    }
}
