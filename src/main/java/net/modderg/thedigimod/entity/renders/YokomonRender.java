package net.modderg.thedigimod.entity.renders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonBulkmon;
import net.modderg.thedigimod.entity.digimons.DigimonYokomon;
import net.modderg.thedigimod.entity.models.BulkmonModel;
import net.modderg.thedigimod.entity.models.YokomonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class YokomonRender extends CustomDigimonRender<DigimonYokomon> {

    public YokomonRender(EntityRendererProvider.Context renderManager){
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new YokomonModel());
    }
}

