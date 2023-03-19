package net.modderg.thedigimod.entity.renders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonExermon;
import net.modderg.thedigimod.entity.models.ExermonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ExermonRender extends CustomDigimonRender<DigimonExermon> {

    public ExermonRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new ExermonModel());
    }
}

