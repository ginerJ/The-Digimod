package net.modderg.thedigimod.entity.goods;


import net.minecraft.resources.ResourceLocation;
import net.modderg.thedigimod.TheDigiMod;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SpTableBookModel<D extends SpTableBook> extends AnimatedGeoModel<SpTableBook> {

    @Override
    public ResourceLocation getModelResource(SpTableBook p) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "geo/training_goods/defence_table.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SpTableBook p) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "textures/training_goods/defence_table.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SpTableBook p) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "animations/good_anims.json");
    }
}
