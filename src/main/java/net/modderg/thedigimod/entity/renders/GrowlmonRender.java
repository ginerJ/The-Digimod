package net.modderg.thedigimod.entity.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonGrizzlymon;
import net.modderg.thedigimod.entity.digimons.DigimonGrowlmon;
import net.modderg.thedigimod.entity.models.GrizzlymonModel;
import net.modderg.thedigimod.entity.models.GrowlmonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GrowlmonRender extends CustomDigimonRender<DigimonGrowlmon> {

    public GrowlmonRender(EntityRendererProvider.Context renderManager){
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new GrizzlymonModel());
    }
}

