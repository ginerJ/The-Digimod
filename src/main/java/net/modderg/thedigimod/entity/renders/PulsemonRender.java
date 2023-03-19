package net.modderg.thedigimod.entity.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonAgumon;
import net.modderg.thedigimod.entity.digimons.DigimonPulsemon;
import net.modderg.thedigimod.entity.models.AgumonModel;
import net.modderg.thedigimod.entity.models.PulsemonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PulsemonRender extends CustomDigimonRender<DigimonPulsemon> {

    public PulsemonRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new PulsemonModel());
    }

    @Override
    public void render(CustomDigimon entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        stack.scale(1.1f,1.1f,1.1f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }
}

