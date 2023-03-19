package net.modderg.thedigimod.entity.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonAkatorimon;
import net.modderg.thedigimod.entity.models.AkatorimonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class AkatorimonRender extends CustomDigimonRender<DigimonAkatorimon> {

    public AkatorimonRender(EntityRendererProvider.Context renderManager){
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new AkatorimonModel());
    }

    @Override
    public void render(CustomDigimon entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        stack.scale(0.9f,0.9f,0.9f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
}

