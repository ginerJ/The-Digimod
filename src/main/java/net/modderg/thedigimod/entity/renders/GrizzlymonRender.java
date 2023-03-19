package net.modderg.thedigimod.entity.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonAgumon;
import net.modderg.thedigimod.entity.digimons.DigimonGrizzlymon;
import net.modderg.thedigimod.entity.models.AgumonModel;
import net.modderg.thedigimod.entity.models.GrizzlymonModel;
import net.modderg.thedigimod.entity.models.KabuterimonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GrizzlymonRender extends CustomDigimonRender<DigimonGrizzlymon> {

    public GrizzlymonRender(EntityRendererProvider.Context renderManager){
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new GrizzlymonModel());
    }
}

