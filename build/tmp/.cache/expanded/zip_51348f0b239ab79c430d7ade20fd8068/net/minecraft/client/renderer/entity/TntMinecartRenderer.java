package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TntMinecartRenderer extends MinecartRenderer<MinecartTNT> {
   private final BlockRenderDispatcher blockRenderer;

   public TntMinecartRenderer(EntityRendererProvider.Context p_174424_) {
      super(p_174424_, ModelLayers.TNT_MINECART);
      this.blockRenderer = p_174424_.getBlockRenderDispatcher();
   }

   protected void renderMinecartContents(MinecartTNT p_116151_, float p_116152_, BlockState p_116153_, PoseStack p_116154_, MultiBufferSource p_116155_, int p_116156_) {
      int i = p_116151_.getFuse();
      if (i > -1 && (float)i - p_116152_ + 1.0F < 10.0F) {
         float f = 1.0F - ((float)i - p_116152_ + 1.0F) / 10.0F;
         f = Mth.clamp(f, 0.0F, 1.0F);
         f *= f;
         f *= f;
         float f1 = 1.0F + f * 0.3F;
         p_116154_.scale(f1, f1, f1);
      }

      renderWhiteSolidBlock(this.blockRenderer, p_116153_, p_116154_, p_116155_, p_116156_, i > -1 && i / 5 % 2 == 0);
   }

   public static void renderWhiteSolidBlock(BlockRenderDispatcher p_234662_, BlockState p_234663_, PoseStack p_234664_, MultiBufferSource p_234665_, int p_234666_, boolean p_234667_) {
      int i;
      if (p_234667_) {
         i = OverlayTexture.pack(OverlayTexture.u(1.0F), 10);
      } else {
         i = OverlayTexture.NO_OVERLAY;
      }

      p_234662_.renderSingleBlock(p_234663_, p_234664_, p_234665_, p_234666_, i);
   }
}