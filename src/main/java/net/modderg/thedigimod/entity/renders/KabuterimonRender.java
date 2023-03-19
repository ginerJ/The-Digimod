package net.modderg.thedigimod.entity.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.CustomDigimonRender;
import net.modderg.thedigimod.entity.digimons.DigimonKabuterimon;
import net.modderg.thedigimod.entity.models.KabuterimonModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class KabuterimonRender extends CustomDigimonRender<DigimonKabuterimon> {

    public KabuterimonRender(EntityRendererProvider.Context renderManager){
        super(renderManager, (AnimatedGeoModel<CustomDigimon>) new KabuterimonModel());
    }
}

