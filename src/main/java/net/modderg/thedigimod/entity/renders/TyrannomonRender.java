package net.modderg.thedigimod.entity.renders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonDarkTyrannomon;
import net.modderg.thedigimod.entity.digimons.DigimonTyrannomon;
import net.modderg.thedigimod.entity.models.DarkTyrannomonModel;
import net.modderg.thedigimod.entity.models.TyrannomonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class TyrannomonRender extends CustomDigimonRender<DigimonTyrannomon> {

    public TyrannomonRender(EntityRendererProvider.Context renderManager){
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new TyrannomonModel());
    }
}

