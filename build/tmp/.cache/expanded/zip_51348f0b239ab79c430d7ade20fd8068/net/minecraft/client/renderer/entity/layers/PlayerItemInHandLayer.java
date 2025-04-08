package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerItemInHandLayer<T extends Player, M extends EntityModel<T> & ArmedModel & HeadedModel> extends ItemInHandLayer<T, M> {
   private final ItemInHandRenderer itemInHandRenderer;
   private static final float X_ROT_MIN = (-(float)Math.PI / 6F);
   private static final float X_ROT_MAX = ((float)Math.PI / 2F);

   public PlayerItemInHandLayer(RenderLayerParent<T, M> p_234866_, ItemInHandRenderer p_234867_) {
      super(p_234866_, p_234867_);
      this.itemInHandRenderer = p_234867_;
   }

   protected void renderArmWithItem(LivingEntity p_270884_, ItemStack p_270379_, ItemDisplayContext p_270607_, HumanoidArm p_270324_, PoseStack p_270124_, MultiBufferSource p_270414_, int p_270295_) {
      if (p_270379_.is(Items.SPYGLASS) && p_270884_.getUseItem() == p_270379_ && p_270884_.swingTime == 0) {
         this.renderArmWithSpyglass(p_270884_, p_270379_, p_270324_, p_270124_, p_270414_, p_270295_);
      } else {
         super.renderArmWithItem(p_270884_, p_270379_, p_270607_, p_270324_, p_270124_, p_270414_, p_270295_);
      }

   }

   private void renderArmWithSpyglass(LivingEntity p_174518_, ItemStack p_174519_, HumanoidArm p_174520_, PoseStack p_174521_, MultiBufferSource p_174522_, int p_174523_) {
      p_174521_.pushPose();
      ModelPart modelpart = this.getParentModel().getHead();
      float f = modelpart.xRot;
      modelpart.xRot = Mth.clamp(modelpart.xRot, (-(float)Math.PI / 6F), ((float)Math.PI / 2F));
      modelpart.translateAndRotate(p_174521_);
      modelpart.xRot = f;
      CustomHeadLayer.translateToHead(p_174521_, false);
      boolean flag = p_174520_ == HumanoidArm.LEFT;
      p_174521_.translate((flag ? -2.5F : 2.5F) / 16.0F, -0.0625F, 0.0F);
      this.itemInHandRenderer.renderItem(p_174518_, p_174519_, ItemDisplayContext.HEAD, false, p_174521_, p_174522_, p_174523_);
      p_174521_.popPose();
   }
}