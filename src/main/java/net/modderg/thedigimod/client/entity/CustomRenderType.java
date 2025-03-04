package net.modderg.thedigimod.client.entity;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class CustomRenderType extends RenderType {


    public CustomRenderType(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }

    public static RenderType getEvolvingBlend(ResourceLocation texture) {
        RenderType.CompositeState state = RenderType.CompositeState.builder().setShaderState(RenderStateShard.RENDERTYPE_END_PORTAL_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(NO_TRANSPARENCY) //CRUMBLING_TRANSPARENCY
                .setCullState(NO_CULL)
                .setLightmapState(NO_LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
        return create("evolving_blend", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.DEBUG_LINE_STRIP, 256, true, false, state);
    }
}