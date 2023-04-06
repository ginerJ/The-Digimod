package net.modderg.thedigimod.entity.goods;


import net.minecraft.resources.ResourceLocation;
import net.modderg.thedigimod.TheDigiMod;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SpTargetModel<D extends SpTarget> extends AnimatedGeoModel<SpTarget> {

    @Override
    public ResourceLocation getModelResource(SpTarget p) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "geo/training_goods/target.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SpTarget p) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "textures/training_goods/target.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SpTarget p) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "animations/good_anims.json");
    }
}
