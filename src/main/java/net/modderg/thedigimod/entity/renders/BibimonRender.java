package net.modderg.thedigimod.entity.renders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonBibimon;
import net.modderg.thedigimod.entity.digimons.DigimonGigimon;
import net.modderg.thedigimod.entity.models.BibimonModel;
import net.modderg.thedigimod.entity.models.GigimonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BibimonRender extends CustomDigimonRender<DigimonBibimon> {

    public BibimonRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new BibimonModel());
    }
}

