package net.modderg.thedigimod.entity.renders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonAgumon;
import net.modderg.thedigimod.entity.digimons.DigimonAgumonBlack;
import net.modderg.thedigimod.entity.models.AgumonBlackModel;
import net.modderg.thedigimod.entity.models.AgumonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class AgumonBlackRender extends CustomDigimonRender<DigimonAgumonBlack> {

    public AgumonBlackRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new AgumonBlackModel());
    }
}

