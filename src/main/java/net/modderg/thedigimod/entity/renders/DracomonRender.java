package net.modderg.thedigimod.entity.renders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonAgumon;
import net.modderg.thedigimod.entity.digimons.DigimonDracomon;
import net.modderg.thedigimod.entity.models.AgumonModel;
import net.modderg.thedigimod.entity.models.DracomonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DracomonRender extends CustomDigimonRender<DigimonDracomon> {

    public DracomonRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new DracomonModel());
    }
}

