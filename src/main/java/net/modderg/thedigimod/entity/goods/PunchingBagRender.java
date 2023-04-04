package net.modderg.thedigimod.entity.goods;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.projectiles.PepperBreath;
import net.modderg.thedigimod.projectiles.models.CustomProjectileModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class PunchingBagRender extends GeoProjectilesRenderer {

    public PunchingBagRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, (AnimatedGeoModel<PunchingBag>) new PunchingBagModel<>());
    }


}
