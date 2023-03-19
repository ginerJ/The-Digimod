package net.modderg.thedigimod.entity.renders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonKoromon;
import net.modderg.thedigimod.entity.digimons.DigimonKoromonB;
import net.modderg.thedigimod.entity.models.KoromonBModel;
import net.modderg.thedigimod.entity.models.KoromonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class KoromonBRender extends CustomDigimonRender<DigimonKoromonB> {

    public KoromonBRender(EntityRendererProvider.Context renderManager){
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new KoromonBModel());
    }
}

