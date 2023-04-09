package net.modderg.thedigimod.entity.goods;


import net.minecraft.resources.ResourceLocation;
import net.modderg.thedigimod.TheDigiMod;
import software.bernie.geckolib.model.GeoModel;

public class PunchingBagModel<D extends PunchingBag> extends GeoModel<PunchingBag> {

    @Override
    public ResourceLocation getModelResource(PunchingBag p) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "geo/training_goods/punching_bag.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PunchingBag p) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "textures/training_goods/punching_bag.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PunchingBag p) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "animations/good_anims.json");
    }
}
