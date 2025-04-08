package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Display;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public abstract class DisplayRenderer<T extends Display, S> extends EntityRenderer<T> {
   private final EntityRenderDispatcher entityRenderDispatcher;

   protected DisplayRenderer(EntityRendererProvider.Context p_270168_) {
      super(p_270168_);
      this.entityRenderDispatcher = p_270168_.getEntityRenderDispatcher();
   }

   public ResourceLocation getTextureLocation(T p_270675_) {
      return TextureAtlas.LOCATION_BLOCKS;
   }

   public void render(T p_270405_, float p_270225_, float p_270279_, PoseStack p_270728_, MultiBufferSource p_270209_, int p_270298_) {
      Display.RenderState display$renderstate = p_270405_.renderState();
      if (display$renderstate != null) {
         S s = this.getSubState(p_270405_);
         if (s != null) {
            float f = p_270405_.calculateInterpolationProgress(p_270279_);
            this.shadowRadius = display$renderstate.shadowRadius().get(f);
            this.shadowStrength = display$renderstate.shadowStrength().get(f);
            int i = display$renderstate.brightnessOverride();
            int j = i != -1 ? i : p_270298_;
            super.render(p_270405_, p_270225_, p_270279_, p_270728_, p_270209_, j);
            p_270728_.pushPose();
            p_270728_.mulPose(this.calculateOrientation(display$renderstate, p_270405_));
            Transformation transformation = display$renderstate.transformation().get(f);
            p_270728_.mulPoseMatrix(transformation.getMatrix());
            p_270728_.last().normal().rotate(transformation.getLeftRotation()).rotate(transformation.getRightRotation());
            this.renderInner(p_270405_, s, p_270728_, p_270209_, j, f);
            p_270728_.popPose();
         }
      }
   }

   private Quaternionf calculateOrientation(Display.RenderState p_277846_, T p_271013_) {
      Camera camera = this.entityRenderDispatcher.camera;
      Quaternionf quaternionf;
      switch (p_277846_.billboardConstraints()) {
         case FIXED:
            quaternionf = p_271013_.orientation();
            break;
         case HORIZONTAL:
            quaternionf = (new Quaternionf()).rotationYXZ(-0.017453292F * p_271013_.getYRot(), -0.017453292F * camera.getXRot(), 0.0F);
            break;
         case VERTICAL:
            quaternionf = (new Quaternionf()).rotationYXZ((float)Math.PI - ((float)Math.PI / 180F) * camera.getYRot(), ((float)Math.PI / 180F) * p_271013_.getXRot(), 0.0F);
            break;
         case CENTER:
            quaternionf = (new Quaternionf()).rotationYXZ((float)Math.PI - ((float)Math.PI / 180F) * camera.getYRot(), -0.017453292F * camera.getXRot(), 0.0F);
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return quaternionf;
   }

   @Nullable
   protected abstract S getSubState(T p_270246_);

   protected abstract void renderInner(T p_277862_, S p_277363_, PoseStack p_277686_, MultiBufferSource p_277429_, int p_278023_, float p_277453_);

   @OnlyIn(Dist.CLIENT)
   public static class BlockDisplayRenderer extends DisplayRenderer<Display.BlockDisplay, Display.BlockDisplay.BlockRenderState> {
      private final BlockRenderDispatcher blockRenderer;

      protected BlockDisplayRenderer(EntityRendererProvider.Context p_270283_) {
         super(p_270283_);
         this.blockRenderer = p_270283_.getBlockRenderDispatcher();
      }

      @Nullable
      protected Display.BlockDisplay.BlockRenderState getSubState(Display.BlockDisplay p_277721_) {
         return p_277721_.blockRenderState();
      }

      public void renderInner(Display.BlockDisplay p_277939_, Display.BlockDisplay.BlockRenderState p_277885_, PoseStack p_277831_, MultiBufferSource p_277554_, int p_278071_, float p_277847_) {
         this.blockRenderer.renderSingleBlock(p_277885_.blockState(), p_277831_, p_277554_, p_278071_, OverlayTexture.NO_OVERLAY);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ItemDisplayRenderer extends DisplayRenderer<Display.ItemDisplay, Display.ItemDisplay.ItemRenderState> {
      private final ItemRenderer itemRenderer;

      protected ItemDisplayRenderer(EntityRendererProvider.Context p_270110_) {
         super(p_270110_);
         this.itemRenderer = p_270110_.getItemRenderer();
      }

      @Nullable
      protected Display.ItemDisplay.ItemRenderState getSubState(Display.ItemDisplay p_277464_) {
         return p_277464_.itemRenderState();
      }

      public void renderInner(Display.ItemDisplay p_277863_, Display.ItemDisplay.ItemRenderState p_277481_, PoseStack p_277889_, MultiBufferSource p_277509_, int p_277861_, float p_277670_) {
         p_277889_.mulPose(Axis.YP.rotation((float)Math.PI));
         this.itemRenderer.renderStatic(p_277481_.itemStack(), p_277481_.itemTransform(), p_277861_, OverlayTexture.NO_OVERLAY, p_277889_, p_277509_, p_277863_.level(), p_277863_.getId());
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class TextDisplayRenderer extends DisplayRenderer<Display.TextDisplay, Display.TextDisplay.TextRenderState> {
      private final Font font;

      protected TextDisplayRenderer(EntityRendererProvider.Context p_271012_) {
         super(p_271012_);
         this.font = p_271012_.getFont();
      }

      private Display.TextDisplay.CachedInfo splitLines(Component p_270823_, int p_270893_) {
         List<FormattedCharSequence> list = this.font.split(p_270823_, p_270893_);
         List<Display.TextDisplay.CachedLine> list1 = new ArrayList<>(list.size());
         int i = 0;

         for(FormattedCharSequence formattedcharsequence : list) {
            int j = this.font.width(formattedcharsequence);
            i = Math.max(i, j);
            list1.add(new Display.TextDisplay.CachedLine(formattedcharsequence, j));
         }

         return new Display.TextDisplay.CachedInfo(list1, i);
      }

      @Nullable
      protected Display.TextDisplay.TextRenderState getSubState(Display.TextDisplay p_277947_) {
         return p_277947_.textRenderState();
      }

      public void renderInner(Display.TextDisplay p_277522_, Display.TextDisplay.TextRenderState p_277620_, PoseStack p_277536_, MultiBufferSource p_277845_, int p_278046_, float p_277769_) {
         byte b0 = p_277620_.flags();
         boolean flag = (b0 & 2) != 0;
         boolean flag1 = (b0 & 4) != 0;
         boolean flag2 = (b0 & 1) != 0;
         Display.TextDisplay.Align display$textdisplay$align = Display.TextDisplay.getAlign(b0);
         byte b1 = (byte)p_277620_.textOpacity().get(p_277769_);
         int i;
         if (flag1) {
            float f = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            i = (int)(f * 255.0F) << 24;
         } else {
            i = p_277620_.backgroundColor().get(p_277769_);
         }

         float f2 = 0.0F;
         Matrix4f matrix4f = p_277536_.last().pose();
         matrix4f.rotate((float)Math.PI, 0.0F, 1.0F, 0.0F);
         matrix4f.scale(-0.025F, -0.025F, -0.025F);
         Display.TextDisplay.CachedInfo display$textdisplay$cachedinfo = p_277522_.cacheDisplay(this::splitLines);
         int j = 9 + 1;
         int k = display$textdisplay$cachedinfo.width();
         int l = display$textdisplay$cachedinfo.lines().size() * j;
         matrix4f.translate(1.0F - (float)k / 2.0F, (float)(-l), 0.0F);
         if (i != 0) {
            VertexConsumer vertexconsumer = p_277845_.getBuffer(flag ? RenderType.textBackgroundSeeThrough() : RenderType.textBackground());
            vertexconsumer.vertex(matrix4f, -1.0F, -1.0F, 0.0F).color(i).uv2(p_278046_).endVertex();
            vertexconsumer.vertex(matrix4f, -1.0F, (float)l, 0.0F).color(i).uv2(p_278046_).endVertex();
            vertexconsumer.vertex(matrix4f, (float)k, (float)l, 0.0F).color(i).uv2(p_278046_).endVertex();
            vertexconsumer.vertex(matrix4f, (float)k, -1.0F, 0.0F).color(i).uv2(p_278046_).endVertex();
         }

         for(Display.TextDisplay.CachedLine display$textdisplay$cachedline : display$textdisplay$cachedinfo.lines()) {
            float f3;
            switch (display$textdisplay$align) {
               case LEFT:
                  f3 = 0.0F;
                  break;
               case RIGHT:
                  f3 = (float)(k - display$textdisplay$cachedline.width());
                  break;
               case CENTER:
                  f3 = (float)k / 2.0F - (float)display$textdisplay$cachedline.width() / 2.0F;
                  break;
               default:
                  throw new IncompatibleClassChangeError();
            }

            float f1 = f3;
            this.font.drawInBatch(display$textdisplay$cachedline.contents(), f1, f2, b1 << 24 | 16777215, flag2, matrix4f, p_277845_, flag ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.POLYGON_OFFSET, 0, p_278046_);
            f2 += (float)j;
         }

      }
   }
}