package net.modderg.thedigimod.entity.renders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonGreymon;
import net.modderg.thedigimod.entity.digimons.DigimonGreymonVirus;
import net.modderg.thedigimod.entity.models.GreymonModel;
import net.modderg.thedigimod.entity.models.GreymonVirusModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GreymonVirusRender extends CustomDigimonRender<DigimonGreymonVirus> {

    public GreymonVirusRender(EntityRendererProvider.Context renderManager){
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new GreymonVirusModel());
    }
}

