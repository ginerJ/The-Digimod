package net.modderg.thedigimod.entity.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonBirdramon;
import net.modderg.thedigimod.entity.digimons.DigimonGreymon;
import net.modderg.thedigimod.entity.models.BirdramonModel;
import net.modderg.thedigimod.entity.models.GreymonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BirdramonRender extends CustomDigimonRender<DigimonBirdramon> {

    public BirdramonRender(EntityRendererProvider.Context renderManager){
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new BirdramonModel());
    }

    @Override
    public void render(CustomDigimon entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        stack.scale(0.9f,0.9f,0.9f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
}

