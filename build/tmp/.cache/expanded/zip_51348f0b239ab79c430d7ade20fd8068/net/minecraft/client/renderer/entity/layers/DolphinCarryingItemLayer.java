package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.DolphinModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DolphinCarryingItemLayer extends RenderLayer<Dolphin, DolphinModel<Dolphin>> {
   private final ItemInHandRenderer itemInHandRenderer;

   public DolphinCarryingItemLayer(RenderLayerParent<Dolphin, DolphinModel<Dolphin>> p_234834_, ItemInHandRenderer p_234835_) {
      super(p_234834_);
      this.itemInHandRenderer = p_234835_;
   }

   public void render(PoseStack p_116897_, MultiBufferSource p_116898_, int p_116899_, Dolphin p_116900_, float p_116901_, float p_116902_, float p_116903_, float p_116904_, float p_116905_, float p_116906_) {
      boolean flag = p_116900_.getMainArm() == HumanoidArm.RIGHT;
      p_116897_.pushPose();
      float f = 1.0F;
      float f1 = -1.0F;
      float f2 = Mth.abs(p_116900_.getXRot()) / 60.0F;
      if (p_116900_.getXRot() < 0.0F) {
         p_116897_.translate(0.0F, 1.0F - f2 * 0.5F, -1.0F + f2 * 0.5F);
      } else {
         p_116897_.translate(0.0F, 1.0F + f2 * 0.8F, -1.0F + f2 * 0.2F);
      }

      ItemStack itemstack = flag ? p_116900_.getMainHandItem() : p_116900_.getOffhandItem();
      this.itemInHandRenderer.renderItem(p_116900_, itemstack, ItemDisplayContext.GROUND, false, p_116897_, p_116898_, p_116899_);
      p_116897_.popPose();
   }
}