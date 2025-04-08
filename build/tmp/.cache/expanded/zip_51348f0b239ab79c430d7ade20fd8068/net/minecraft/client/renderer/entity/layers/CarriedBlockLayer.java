package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CarriedBlockLayer extends RenderLayer<EnderMan, EndermanModel<EnderMan>> {
   private final BlockRenderDispatcher blockRenderer;

   public CarriedBlockLayer(RenderLayerParent<EnderMan, EndermanModel<EnderMan>> p_234814_, BlockRenderDispatcher p_234815_) {
      super(p_234814_);
      this.blockRenderer = p_234815_;
   }

   public void render(PoseStack p_116639_, MultiBufferSource p_116640_, int p_116641_, EnderMan p_116642_, float p_116643_, float p_116644_, float p_116645_, float p_116646_, float p_116647_, float p_116648_) {
      BlockState blockstate = p_116642_.getCarriedBlock();
      if (blockstate != null) {
         p_116639_.pushPose();
         p_116639_.translate(0.0F, 0.6875F, -0.75F);
         p_116639_.mulPose(Axis.XP.rotationDegrees(20.0F));
         p_116639_.mulPose(Axis.YP.rotationDegrees(45.0F));
         p_116639_.translate(0.25F, 0.1875F, 0.25F);
         float f = 0.5F;
         p_116639_.scale(-0.5F, -0.5F, 0.5F);
         p_116639_.mulPose(Axis.YP.rotationDegrees(90.0F));
         this.blockRenderer.renderSingleBlock(blockstate, p_116639_, p_116640_, p_116641_, OverlayTexture.NO_OVERLAY);
         p_116639_.popPose();
      }
   }
}