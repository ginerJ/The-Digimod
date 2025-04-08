package net.modderg.thedigimod.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import static net.modderg.thedigimod.client.TDClientConfig.SHOULD_RENDER_DIRTY_EFFECT;
import static net.modderg.thedigimod.client.TDClientConfig.SHOULD_RENDER_GLOWMASKS;


public class CustomDigimonRender extends GeoEntityRenderer<DigimonEntity> {
    protected Style XpStyle = Style.EMPTY;

    public CustomDigimonRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CustomDigimonModel<>());
    }

    private final AutoGlowingGeoLayer<DigimonEntity> glowingLayer = new AutoGlowingGeoLayer<>(this);

    @Override
    public void render(@NotNull DigimonEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack stack, @NotNull MultiBufferSource bufferIn, int packedLightIn) {

        if (SHOULD_RENDER_GLOWMASKS.get() && entity.isEmissive && !this.renderLayers.getRenderLayers().contains(glowingLayer))
            this.addRenderLayer(glowingLayer);

        if(this.shadowRadius == 0)
            this.shadowRadius = (float) entity.getBoundingBox().getXsize()/2;

        float scale = entity.getScale();

        stack.scale(scale,scale,scale);

        if(!entity.isControlledByLocalInstance())
            renderDigimonInfo(entity, partialTicks, stack, bufferIn, packedLightIn);

        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, DigimonEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (SHOULD_RENDER_DIRTY_EFFECT.get() && animatable.getDirtyCounter() > 0)
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, 0.949f, 0.675f, 0.812f, 0.05f);
        else
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    protected void renderDigimonInfo(DigimonEntity entity, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn){
        var renderNameTagEvent = new net.minecraftforge.client.event.RenderNameTagEvent(entity, entity.getDisplayName(), this, stack, bufferIn, packedLightIn, partialTicks);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameTagEvent);

        MutableComponent mp = MutableComponent.create(Component.literal(xpBarMath( entity.getNeededXp(), entity.getLevelXp())).getContents());
        mp.setStyle(XpStyle.withColor(TextColor.fromRgb(11534335)));

        MutableComponent mp2 = entity.moodManager.getMoodComponent();

        MutableComponent mp3 = MutableComponent.create(Component.literal(Integer.toString(Math.round(entity.getHealth())) + "/" + (int)entity.getAttribute(Attributes.MAX_HEALTH).getValue() + "Hp").getContents());

        int lifes = entity.getLives();
        mp3.setStyle(XpStyle.withColor(TextColor.fromRgb(lifes == 3 ? 8704641 : (lifes == 2 ? 0xFFFF00 : 0xFF0000))));

        if (renderNameTagEvent.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (renderNameTagEvent.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || this.shouldShowName(entity))) {
            if(entity.isTame()){
                this.renderHover(entity, mp, stack, bufferIn, packedLightIn,0.2F);
                this.renderHover(entity, mp2, stack, bufferIn, packedLightIn,0.8F);
            }
            this.renderHover(entity, mp3, stack, bufferIn, packedLightIn,1.07F);
        }
    }

    protected String xpBarMath(int max, int current){
        StringBuilder sb = new StringBuilder();
        int percentage = (30 *(100*current/max))/100;
        int i = 0;
        while(i < percentage){
            sb.append("|");
            i++;
        }
        while(i < 30){
            sb.append("·");
            i++;
        }
        return sb.toString();
    }

    protected void renderHover(DigimonEntity p_114498_, Component p_114499_, PoseStack stack, MultiBufferSource p_114501_, int p_114502_ , float heightAdd) {
        double d0 = this.entityRenderDispatcher.distanceToSqr(p_114498_);
        if (net.minecraftforge.client.ForgeHooksClient.isNameplateInRenderDistance(p_114498_, d0)) {
            boolean flag = !p_114498_.isDiscrete();
            float f = p_114498_.getBbHeight() + heightAdd;
            int i = "deadmau5".equals(p_114499_.getString()) ? -10 : 0;
            stack.pushPose();
            stack.translate(0.0D, (double)f, 0.0D);
            stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            stack.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = stack.last().pose();
            float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            int j = (int)(f1 * 255.0F) << 24;
            Font font = this.getFont();
            float f2 = (float)(-font.width(p_114499_) / 2);
            font.drawInBatch(p_114499_, f2, (float)i, 553648127, false, matrix4f, p_114501_, flag ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, j, p_114502_);
            if (flag) {
                font.drawInBatch(p_114499_, f2, (float)i, -1, false, matrix4f, p_114501_, Font.DisplayMode.NORMAL, 0, p_114502_);
            }
            stack.popPose();
        }
    }
}
