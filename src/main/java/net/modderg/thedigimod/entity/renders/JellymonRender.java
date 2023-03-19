package net.modderg.thedigimod.entity.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonJellymon;
import net.modderg.thedigimod.entity.models.GuilmonModel;
import net.modderg.thedigimod.entity.models.JellymonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class JellymonRender extends CustomDigimonRender<DigimonJellymon> {

    public JellymonRender(EntityRendererProvider.Context renderManager){
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new JellymonModel());
    }

    @Override
    public void render(CustomDigimon entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        stack.scale(0.8f,0.8f,0.8f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
}

