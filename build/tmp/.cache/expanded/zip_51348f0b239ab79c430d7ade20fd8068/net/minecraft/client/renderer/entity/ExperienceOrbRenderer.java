package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class ExperienceOrbRenderer extends EntityRenderer<ExperienceOrb> {
   private static final ResourceLocation EXPERIENCE_ORB_LOCATION = new ResourceLocation("textures/entity/experience_orb.png");
   private static final RenderType RENDER_TYPE = RenderType.itemEntityTranslucentCull(EXPERIENCE_ORB_LOCATION);

   public ExperienceOrbRenderer(EntityRendererProvider.Context p_174110_) {
      super(p_174110_);
      this.shadowRadius = 0.15F;
      this.shadowStrength = 0.75F;
   }

   protected int getBlockLightLevel(ExperienceOrb p_114606_, BlockPos p_114607_) {
      return Mth.clamp(super.getBlockLightLevel(p_114606_, p_114607_) + 7, 0, 15);
   }

   public void render(ExperienceOrb p_114599_, float p_114600_, float p_114601_, PoseStack p_114602_, MultiBufferSource p_114603_, int p_114604_) {
      p_114602_.pushPose();
      int i = p_114599_.getIcon();
      float f = (float)(i % 4 * 16 + 0) / 64.0F;
      float f1 = (float)(i % 4 * 16 + 16) / 64.0F;
      float f2 = (float)(i / 4 * 16 + 0) / 64.0F;
      float f3 = (float)(i / 4 * 16 + 16) / 64.0F;
      float f4 = 1.0F;
      float f5 = 0.5F;
      float f6 = 0.25F;
      float f7 = 255.0F;
      float f8 = ((float)p_114599_.tickCount + p_114601_) / 2.0F;
      int j = (int)((Mth.sin(f8 + 0.0F) + 1.0F) * 0.5F * 255.0F);
      int k = 255;
      int l = (int)((Mth.sin(f8 + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
      p_114602_.translate(0.0F, 0.1F, 0.0F);
      p_114602_.mulPose(this.entityRenderDispatcher.cameraOrientation());
      p_114602_.mulPose(Axis.YP.rotationDegrees(180.0F));
      float f9 = 0.3F;
      p_114602_.scale(0.3F, 0.3F, 0.3F);
      VertexConsumer vertexconsumer = p_114603_.getBuffer(RENDER_TYPE);
      PoseStack.Pose posestack$pose = p_114602_.last();
      Matrix4f matrix4f = posestack$pose.pose();
      Matrix3f matrix3f = posestack$pose.normal();
      vertex(vertexconsumer, matrix4f, matrix3f, -0.5F, -0.25F, j, 255, l, f, f3, p_114604_);
      vertex(vertexconsumer, matrix4f, matrix3f, 0.5F, -0.25F, j, 255, l, f1, f3, p_114604_);
      vertex(vertexconsumer, matrix4f, matrix3f, 0.5F, 0.75F, j, 255, l, f1, f2, p_114604_);
      vertex(vertexconsumer, matrix4f, matrix3f, -0.5F, 0.75F, j, 255, l, f, f2, p_114604_);
      p_114602_.popPose();
      super.render(p_114599_, p_114600_, p_114601_, p_114602_, p_114603_, p_114604_);
   }

   private static void vertex(VertexConsumer p_254515_, Matrix4f p_253946_, Matrix3f p_253754_, float p_253952_, float p_254066_, int p_254283_, int p_254566_, int p_253882_, float p_254434_, float p_254223_, int p_254372_) {
      p_254515_.vertex(p_253946_, p_253952_, p_254066_, 0.0F).color(p_254283_, p_254566_, p_253882_, 128).uv(p_254434_, p_254223_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_254372_).normal(p_253754_, 0.0F, 1.0F, 0.0F).endVertex();
   }

   public ResourceLocation getTextureLocation(ExperienceOrb p_114597_) {
      return EXPERIENCE_ORB_LOCATION;
   }
}