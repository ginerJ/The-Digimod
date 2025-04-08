package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BrushableBlockRenderer implements BlockEntityRenderer<BrushableBlockEntity> {
   private final ItemRenderer itemRenderer;

   public BrushableBlockRenderer(BlockEntityRendererProvider.Context p_277899_) {
      this.itemRenderer = p_277899_.getItemRenderer();
   }

   public void render(BrushableBlockEntity p_277712_, float p_277981_, PoseStack p_277490_, MultiBufferSource p_278015_, int p_277463_, int p_277346_) {
      if (p_277712_.getLevel() != null) {
         int i = p_277712_.getBlockState().getValue(BlockStateProperties.DUSTED);
         if (i > 0) {
            Direction direction = p_277712_.getHitDirection();
            if (direction != null) {
               ItemStack itemstack = p_277712_.getItem();
               if (!itemstack.isEmpty()) {
                  p_277490_.pushPose();
                  p_277490_.translate(0.0F, 0.5F, 0.0F);
                  float[] afloat = this.translations(direction, i);
                  p_277490_.translate(afloat[0], afloat[1], afloat[2]);
                  p_277490_.mulPose(Axis.YP.rotationDegrees(75.0F));
                  boolean flag = direction == Direction.EAST || direction == Direction.WEST;
                  p_277490_.mulPose(Axis.YP.rotationDegrees((float)((flag ? 90 : 0) + 11)));
                  p_277490_.scale(0.5F, 0.5F, 0.5F);
                  int j = LevelRenderer.getLightColor(p_277712_.getLevel(), p_277712_.getBlockState(), p_277712_.getBlockPos().relative(direction));
                  this.itemRenderer.renderStatic(itemstack, ItemDisplayContext.FIXED, j, OverlayTexture.NO_OVERLAY, p_277490_, p_278015_, p_277712_.getLevel(), 0);
                  p_277490_.popPose();
               }
            }
         }
      }
   }

   private float[] translations(Direction p_278030_, int p_277997_) {
      float[] afloat = new float[]{0.5F, 0.0F, 0.5F};
      float f = (float)p_277997_ / 10.0F * 0.75F;
      switch (p_278030_) {
         case EAST:
            afloat[0] = 0.73F + f;
            break;
         case WEST:
            afloat[0] = 0.25F - f;
            break;
         case UP:
            afloat[1] = 0.25F + f;
            break;
         case DOWN:
            afloat[1] = -0.23F - f;
            break;
         case NORTH:
            afloat[2] = 0.25F - f;
            break;
         case SOUTH:
            afloat[2] = 0.73F + f;
      }

      return afloat;
   }
}