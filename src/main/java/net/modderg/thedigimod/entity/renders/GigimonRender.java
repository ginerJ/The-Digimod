package net.modderg.thedigimod.entity.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonGigimon;
import net.modderg.thedigimod.entity.digimons.DigimonKoromon;
import net.modderg.thedigimod.entity.models.BearmonModel;
import net.modderg.thedigimod.entity.models.GigimonModel;
import net.modderg.thedigimod.entity.models.KoromonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GigimonRender extends CustomDigimonRender<DigimonGigimon> {

    public GigimonRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new GigimonModel());
    }
}

