package net.modderg.thedigimod.entity.renders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonCoredramon;
import net.modderg.thedigimod.entity.digimons.DigimonGreymon;
import net.modderg.thedigimod.entity.models.CoredramonModel;
import net.modderg.thedigimod.entity.models.GreymonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CoredramonRender extends CustomDigimonRender<DigimonCoredramon> {

    public CoredramonRender(EntityRendererProvider.Context renderManager){
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new CoredramonModel());
    }
}

