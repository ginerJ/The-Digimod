package net.modderg.thedigimod.client.entity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CustomDigimonModel<D extends DigimonEntity> extends GeoModel<DigimonEntity> {

    @Override
    public ResourceLocation getModelResource(DigimonEntity digimon) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "geo/digimon/" +
                digimon.getLowerCaseSpecies()
                + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DigimonEntity digimon) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "textures/entities/" +
                digimon.getLowerCaseSpecies()
                + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(DigimonEntity digimon) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "animations/" + digimon.animFileName + ".json");
    }

    @Override
    public void setCustomAnimations(DigimonEntity animatable, long instanceId, AnimationState<DigimonEntity> animationState) {
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
    public RenderType getRenderType(DigimonEntity animatable, ResourceLocation texture) {
        if(animatable.isEvolving())
            return CustomRenderType.getEvolvingBlend(texture);
        return super.getRenderType(animatable, texture);
    }
}