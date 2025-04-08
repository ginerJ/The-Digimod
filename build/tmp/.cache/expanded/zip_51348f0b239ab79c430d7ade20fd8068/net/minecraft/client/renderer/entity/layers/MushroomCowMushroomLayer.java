package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MushroomCowMushroomLayer<T extends MushroomCow> extends RenderLayer<T, CowModel<T>> {
   private final BlockRenderDispatcher blockRenderer;

   public MushroomCowMushroomLayer(RenderLayerParent<T, CowModel<T>> p_234850_, BlockRenderDispatcher p_234851_) {
      super(p_234850_);
      this.blockRenderer = p_234851_;
   }

   public void render(PoseStack p_117256_, MultiBufferSource p_117257_, int p_117258_, T p_117259_, float p_117260_, float p_117261_, float p_117262_, float p_117263_, float p_117264_, float p_117265_) {
      if (!p_117259_.isBaby()) {
         Minecraft minecraft = Minecraft.getInstance();
         boolean flag = minecraft.shouldEntityAppearGlowing(p_117259_) && p_117259_.isInvisible();
         if (!p_117259_.isInvisible() || flag) {
            BlockState blockstate = p_117259_.getVariant().getBlockState();
            int i = LivingEntityRenderer.getOverlayCoords(p_117259_, 0.0F);
            BakedModel bakedmodel = this.blockRenderer.getBlockModel(blockstate);
            p_117256_.pushPose();
            p_117256_.translate(0.2F, -0.35F, 0.5F);
            p_117256_.mulPose(Axis.YP.rotationDegrees(-48.0F));
            p_117256_.scale(-1.0F, -1.0F, 1.0F);
            p_117256_.translate(-0.5F, -0.5F, -0.5F);
            this.renderMushroomBlock(p_117256_, p_117257_, p_117258_, flag, blockstate, i, bakedmodel);
            p_117256_.popPose();
            p_117256_.pushPose();
            p_117256_.translate(0.2F, -0.35F, 0.5F);
            p_117256_.mulPose(Axis.YP.rotationDegrees(42.0F));
            p_117256_.translate(0.1F, 0.0F, -0.6F);
            p_117256_.mulPose(Axis.YP.rotationDegrees(-48.0F));
            p_117256_.scale(-1.0F, -1.0F, 1.0F);
            p_117256_.translate(-0.5F, -0.5F, -0.5F);
            this.renderMushroomBlock(p_117256_, p_117257_, p_117258_, flag, blockstate, i, bakedmodel);
            p_117256_.popPose();
            p_117256_.pushPose();
            this.getParentModel().getHead().translateAndRotate(p_117256_);
            p_117256_.translate(0.0F, -0.7F, -0.2F);
            p_117256_.mulPose(Axis.YP.rotationDegrees(-78.0F));
            p_117256_.scale(-1.0F, -1.0F, 1.0F);
            p_117256_.translate(-0.5F, -0.5F, -0.5F);
            this.renderMushroomBlock(p_117256_, p_117257_, p_117258_, flag, blockstate, i, bakedmodel);
            p_117256_.popPose();
         }
      }
   }

   private void renderMushroomBlock(PoseStack p_234853_, MultiBufferSource p_234854_, int p_234855_, boolean p_234856_, BlockState p_234857_, int p_234858_, BakedModel p_234859_) {
      if (p_234856_) {
         this.blockRenderer.getModelRenderer().renderModel(p_234853_.last(), p_234854_.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)), p_234857_, p_234859_, 0.0F, 0.0F, 0.0F, p_234855_, p_234858_);
      } else {
         this.blockRenderer.renderSingleBlock(p_234857_, p_234853_, p_234854_, p_234855_, p_234858_);
      }

   }
}