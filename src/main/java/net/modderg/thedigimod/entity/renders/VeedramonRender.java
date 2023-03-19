package net.modderg.thedigimod.entity.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonGreymon;
import net.modderg.thedigimod.entity.digimons.DigimonVeedramon;
import net.modderg.thedigimod.entity.models.GreymonModel;
import net.modderg.thedigimod.entity.models.VeedramonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class VeedramonRender extends CustomDigimonRender<DigimonVeedramon> {

    public VeedramonRender(EntityRendererProvider.Context renderManager){
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new VeedramonModel());
    }

    @Override
    public void render(CustomDigimon entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        stack.scale(0.95f,0.95f,0.95f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
}

