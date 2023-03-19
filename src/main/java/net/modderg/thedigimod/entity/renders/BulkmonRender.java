package net.modderg.thedigimod.entity.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonBulkmon;
import net.modderg.thedigimod.entity.digimons.DigimonGreymon;
import net.modderg.thedigimod.entity.models.BulkmonModel;
import net.modderg.thedigimod.entity.models.GreymonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BulkmonRender extends CustomDigimonRender<DigimonBulkmon> {

    public BulkmonRender(EntityRendererProvider.Context renderManager){
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new BulkmonModel());
    }
}

