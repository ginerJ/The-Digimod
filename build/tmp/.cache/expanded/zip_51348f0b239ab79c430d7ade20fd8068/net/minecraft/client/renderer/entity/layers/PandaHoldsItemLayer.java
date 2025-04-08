package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PandaHoldsItemLayer extends RenderLayer<Panda, PandaModel<Panda>> {
   private final ItemInHandRenderer itemInHandRenderer;

   public PandaHoldsItemLayer(RenderLayerParent<Panda, PandaModel<Panda>> p_234862_, ItemInHandRenderer p_234863_) {
      super(p_234862_);
      this.itemInHandRenderer = p_234863_;
   }

   public void render(PoseStack p_117280_, MultiBufferSource p_117281_, int p_117282_, Panda p_117283_, float p_117284_, float p_117285_, float p_117286_, float p_117287_, float p_117288_, float p_117289_) {
      ItemStack itemstack = p_117283_.getItemBySlot(EquipmentSlot.MAINHAND);
      if (p_117283_.isSitting() && !p_117283_.isScared()) {
         float f = -0.6F;
         float f1 = 1.4F;
         if (p_117283_.isEating()) {
            f -= 0.2F * Mth.sin(p_117287_ * 0.6F) + 0.2F;
            f1 -= 0.09F * Mth.sin(p_117287_ * 0.6F);
         }

         p_117280_.pushPose();
         p_117280_.translate(0.1F, f1, f);
         this.itemInHandRenderer.renderItem(p_117283_, itemstack, ItemDisplayContext.GROUND, false, p_117280_, p_117281_, p_117282_);
         p_117280_.popPose();
      }
   }
}