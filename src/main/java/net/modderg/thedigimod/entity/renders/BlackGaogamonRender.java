package net.modderg.thedigimod.entity.renders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonBlackGaogamon;
import net.modderg.thedigimod.entity.digimons.DigimonGrizzlymon;
import net.modderg.thedigimod.entity.models.BlackGaogamonModel;
import net.modderg.thedigimod.entity.models.GrizzlymonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BlackGaogamonRender extends CustomDigimonRender<DigimonBlackGaogamon> {

    public BlackGaogamonRender(EntityRendererProvider.Context renderManager){
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new BlackGaogamonModel());
    }
}

