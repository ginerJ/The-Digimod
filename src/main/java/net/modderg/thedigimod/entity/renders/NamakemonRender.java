package net.modderg.thedigimod.entity.renders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonNamakemon;
import net.modderg.thedigimod.entity.models.NamakemonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class NamakemonRender extends CustomDigimonRender<DigimonNamakemon> {

    public NamakemonRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new NamakemonModel());
    }
}

