package net.modderg.thedigimod.entity.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonDarkLizzardmon;
import net.modderg.thedigimod.entity.digimons.DigimonDarkTyrannomon;
import net.modderg.thedigimod.entity.models.DarkLizzardmonModel;
import net.modderg.thedigimod.entity.models.DarkTyrannomonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DarkLizzardmonRender extends CustomDigimonRender<DigimonDarkLizzardmon> {

    public DarkLizzardmonRender(EntityRendererProvider.Context renderManager){
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new DarkLizzardmonModel());
    }

    @Override
    public void render(CustomDigimon entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        stack.scale(0.85f,0.85f,0.85f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
}

