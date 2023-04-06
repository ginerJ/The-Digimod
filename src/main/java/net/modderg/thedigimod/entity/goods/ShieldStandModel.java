package net.modderg.thedigimod.entity.goods;


import net.minecraft.resources.ResourceLocation;
import net.modderg.thedigimod.TheDigiMod;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ShieldStandModel<D extends ShieldStand> extends AnimatedGeoModel<ShieldStand> {

    @Override
    public ResourceLocation getModelResource(ShieldStand p) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "geo/training_goods/shield.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ShieldStand p) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "textures/training_goods/shield.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ShieldStand p) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "animations/good_anims.json");
    }
}
