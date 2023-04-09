package net.modderg.thedigimod.entity.goods;


import net.minecraft.resources.ResourceLocation;
import net.modderg.thedigimod.TheDigiMod;
import software.bernie.geckolib.model.GeoModel;

public class CustomTrainingGoodModel<D extends CustomTrainingGood> extends GeoModel<CustomTrainingGood> {

    @Override
    public ResourceLocation getModelResource(CustomTrainingGood good) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "geo/training_goods/"+good.goodName()+".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CustomTrainingGood good) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "textures/training_goods/"+good.goodName()+".png");
    }

    @Override
    public ResourceLocation getAnimationResource(CustomTrainingGood good) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "animations/good_anims.json");
    }
}
