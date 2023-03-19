package net.modderg.thedigimod.entity.renders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonBabydmon;
import net.modderg.thedigimod.entity.models.BabydmonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BabydmonRender extends CustomDigimonRender<DigimonBabydmon> {

    public BabydmonRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new BabydmonModel());
    }
}

