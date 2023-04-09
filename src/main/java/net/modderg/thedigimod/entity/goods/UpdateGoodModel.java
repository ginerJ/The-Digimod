package net.modderg.thedigimod.entity.goods;


import net.minecraft.resources.ResourceLocation;
import net.modderg.thedigimod.TheDigiMod;
import software.bernie.geckolib.model.GeoModel;

public class UpdateGoodModel<D extends UpdateGood> extends GeoModel<UpdateGood> {

    @Override
    public ResourceLocation getModelResource(UpdateGood p) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "geo/training_goods/update.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(UpdateGood p) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "textures/training_goods/update.png");
    }

    @Override
    public ResourceLocation getAnimationResource(UpdateGood p) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "animations/good_anims.json");
    }
}
