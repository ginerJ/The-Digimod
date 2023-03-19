package net.modderg.thedigimod.entity.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonTentomon;
import net.modderg.thedigimod.entity.models.MochimonModel;
import net.modderg.thedigimod.entity.models.PuyoyomonModel;
import net.modderg.thedigimod.entity.models.TentomonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class TentomonRender extends CustomDigimonRender<DigimonTentomon> {

    public TentomonRender(EntityRendererProvider.Context renderManager){
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new TentomonModel());
    }
}

