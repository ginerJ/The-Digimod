package net.modderg.thedigimod.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.modderg.thedigimod.TheDigiMod;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CustomDigimonModel<D extends CustomDigimon> extends GeoModel<CustomDigimon> {

    @Override
    public ResourceLocation getModelResource(CustomDigimon digimon) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "geo/" +
                digimon.getSpecies().toLowerCase().replace("(", "").replace(")","")
                + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CustomDigimon digimon) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "textures/entities/" +
                digimon.getSpecies().toLowerCase().replace("(", "").replace(")","")
                + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(CustomDigimon digimon) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "animations/digimons_anims.json");
    }

    @Override
    public void setCustomAnimations(CustomDigimon animatable, long instanceId, AnimationState<CustomDigimon> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
        super.setCustomAnimations(animatable, instanceId, animationState);
    }
}