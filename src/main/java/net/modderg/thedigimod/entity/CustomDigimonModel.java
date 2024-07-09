package net.modderg.thedigimod.entity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.ScreenEvent;
import net.modderg.thedigimod.TheDigiMod;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CustomDigimonModel<D extends CustomDigimon> extends GeoModel<CustomDigimon> {

    @Override
    public ResourceLocation getModelResource(CustomDigimon digimon) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "geo/digimon/" +
                digimon.getLowerCaseSpecies()
                + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CustomDigimon digimon) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "textures/entities/" +
                digimon.getLowerCaseSpecies()
                + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(CustomDigimon digimon) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "animations/digimons_anims.json");
    }


    @Override
    public void setCustomAnimations(CustomDigimon animatable, long instanceId, AnimationState<CustomDigimon> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && !animatable.isControlledByLocalInstance()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            float headPitch = entityData.headPitch();
            float headYaw = entityData.netHeadYaw();

            float pitchRadians = headPitch * Mth.DEG_TO_RAD;
            float yawRadians = headYaw * Mth.DEG_TO_RAD;

            head.setRotX(pitchRadians);
            head.setRotY(yawRadians);
        }

        super.setCustomAnimations(animatable, instanceId, animationState);
    }




    @Override
    public RenderType getRenderType(CustomDigimon animatable, ResourceLocation texture) {
        if(animatable.isEvolving())
            return CustomRenderType.getEvolvingBlend(texture);
        return super.getRenderType(animatable, texture);
    }
}