package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SignRenderer implements BlockEntityRenderer<SignBlockEntity> {
   private static final String STICK = "stick";
   private static final int BLACK_TEXT_OUTLINE_COLOR = -988212;
   private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
   private static final float RENDER_SCALE = 0.6666667F;
   private static final Vec3 TEXT_OFFSET = new Vec3(0.0D, (double)0.33333334F, (double)0.046666667F);
   private final Map<WoodType, SignRenderer.SignModel> signModels;
   private final Font font;

   public SignRenderer(BlockEntityRendererProvider.Context p_173636_) {
      this.signModels = WoodType.values().collect(ImmutableMap.toImmutableMap((p_173645_) -> {
         return p_173645_;
      }, (p_173651_) -> {
         return new SignRenderer.SignModel(p_173636_.bakeLayer(ModelLayers.createSignModelName(p_173651_)));
      }));
      this.font = p_173636_.getFont();
   }

   public void render(SignBlockEntity p_112497_, float p_112498_, PoseStack p_112499_, MultiBufferSource p_112500_, int p_112501_, int p_112502_) {
      BlockState blockstate = p_112497_.getBlockState();
      SignBlock signblock = (SignBlock)blockstate.getBlock();
      WoodType woodtype = SignBlock.getWoodType(signblock);
      SignRenderer.SignModel signrenderer$signmodel = this.signModels.get(woodtype);
      signrenderer$signmodel.stick.visible = blockstate.getBlock() instanceof StandingSignBlock;
      this.renderSignWithText(p_112497_, p_112499_, p_112500_, p_112501_, p_112502_, blockstate, signblock, woodtype, signrenderer$signmodel);
   }

   public float getSignModelRenderScale() {
      return 0.6666667F;
   }

   public float getSignTextRenderScale() {
      return 0.6666667F;
   }

   void renderSignWithText(SignBlockEntity p_279389_, PoseStack p_279331_, MultiBufferSource p_279303_, int p_279396_, int p_279203_, BlockState p_279391_, SignBlock p_279224_, WoodType p_279162_, Model p_279444_) {
      p_279331_.pushPose();
      this.translateSign(p_279331_, -p_279224_.getYRotationDegrees(p_279391_), p_279391_);
      this.renderSign(p_279331_, p_279303_, p_279396_, p_279203_, p_279162_, p_279444_);
      this.renderSignText(p_279389_.getBlockPos(), p_279389_.getFrontText(), p_279331_, p_279303_, p_279396_, p_279389_.getTextLineHeight(), p_279389_.getMaxTextLineWidth(), true);
      this.renderSignText(p_279389_.getBlockPos(), p_279389_.getBackText(), p_279331_, p_279303_, p_279396_, p_279389_.getTextLineHeight(), p_279389_.getMaxTextLineWidth(), false);
      p_279331_.popPose();
   }

   void translateSign(PoseStack p_278074_, float p_277875_, BlockState p_277559_) {
      p_278074_.translate(0.5F, 0.75F * this.getSignModelRenderScale(), 0.5F);
      p_278074_.mulPose(Axis.YP.rotationDegrees(p_277875_));
      if (!(p_277559_.getBlock() instanceof StandingSignBlock)) {
         p_278074_.translate(0.0F, -0.3125F, -0.4375F);
      }

   }

   void renderSign(PoseStack p_279104_, MultiBufferSource p_279408_, int p_279494_, int p_279344_, WoodType p_279170_, Model p_279159_) {
      p_279104_.pushPose();
      float f = this.getSignModelRenderScale();
      p_279104_.scale(f, -f, -f);
      Material material = this.getSignMaterial(p_279170_);
      VertexConsumer vertexconsumer = material.buffer(p_279408_, p_279159_::renderType);
      this.renderSignModel(p_279104_, p_279494_, p_279344_, p_279159_, vertexconsumer);
      p_279104_.popPose();
   }

   void renderSignModel(PoseStack p_250252_, int p_249399_, int p_249042_, Model p_250082_, VertexConsumer p_251093_) {
      SignRenderer.SignModel signrenderer$signmodel = (SignRenderer.SignModel)p_250082_;
      signrenderer$signmodel.root.render(p_250252_, p_251093_, p_249399_, p_249042_);
   }

   Material getSignMaterial(WoodType p_251961_) {
      return Sheets.getSignMaterial(p_251961_);
   }

   void renderSignText(BlockPos p_279403_, SignText p_279361_, PoseStack p_279234_, MultiBufferSource p_279338_, int p_279300_, int p_279179_, int p_279357_, boolean p_279325_) {
      p_279234_.pushPose();
      this.translateSignText(p_279234_, p_279325_, this.getTextOffset());
      int i = getDarkColor(p_279361_);
      int j = 4 * p_279179_ / 2;
      FormattedCharSequence[] aformattedcharsequence = p_279361_.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (p_277227_) -> {
         List<FormattedCharSequence> list = this.font.split(p_277227_, p_279357_);
         return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
      });
      int k;
      boolean flag;
      int l;
      if (p_279361_.hasGlowingText()) {
         k = p_279361_.getColor().getTextColor();
         flag = isOutlineVisible(p_279403_, k);
         l = 15728880;
      } else {
         k = i;
         flag = false;
         l = p_279300_;
      }

      for(int i1 = 0; i1 < 4; ++i1) {
         FormattedCharSequence formattedcharsequence = aformattedcharsequence[i1];
         float f = (float)(-this.font.width(formattedcharsequence) / 2);
         if (flag) {
            this.font.drawInBatch8xOutline(formattedcharsequence, f, (float)(i1 * p_279179_ - j), k, i, p_279234_.last().pose(), p_279338_, l);
         } else {
            this.font.drawInBatch(formattedcharsequence, f, (float)(i1 * p_279179_ - j), k, false, p_279234_.last().pose(), p_279338_, Font.DisplayMode.POLYGON_OFFSET, 0, l);
         }
      }

      p_279234_.popPose();
   }

   private void translateSignText(PoseStack p_279133_, boolean p_279134_, Vec3 p_279280_) {
      if (!p_279134_) {
         p_279133_.mulPose(Axis.YP.rotationDegrees(180.0F));
      }

      float f = 0.015625F * this.getSignTextRenderScale();
      p_279133_.translate(p_279280_.x, p_279280_.y, p_279280_.z);
      p_279133_.scale(f, -f, f);
   }

   Vec3 getTextOffset() {
      return TEXT_OFFSET;
   }

   static boolean isOutlineVisible(BlockPos p_277741_, int p_278022_) {
      if (p_278022_ == DyeColor.BLACK.getTextColor()) {
         return true;
      } else {
         Minecraft minecraft = Minecraft.getInstance();
         LocalPlayer localplayer = minecraft.player;
         if (localplayer != null && minecraft.options.getCameraType().isFirstPerson() && localplayer.isScoping()) {
            return true;
         } else {
            Entity entity = minecraft.getCameraEntity();
            return entity != null && entity.distanceToSqr(Vec3.atCenterOf(p_277741_)) < (double)OUTLINE_RENDER_DISTANCE;
         }
      }
   }

   static int getDarkColor(SignText p_277914_) {
      int i = p_277914_.getColor().getTextColor();
      if (i == DyeColor.BLACK.getTextColor() && p_277914_.hasGlowingText()) {
         return -988212;
      } else {
         double d0 = 0.4D;
         int j = (int)((double)FastColor.ARGB32.red(i) * 0.4D);
         int k = (int)((double)FastColor.ARGB32.green(i) * 0.4D);
         int l = (int)((double)FastColor.ARGB32.blue(i) * 0.4D);
         return FastColor.ARGB32.color(0, j, k, l);
      }
   }

   public static SignRenderer.SignModel createSignModel(EntityModelSet p_173647_, WoodType p_173648_) {
      return new SignRenderer.SignModel(p_173647_.bakeLayer(ModelLayers.createSignModelName(p_173648_)));
   }

   public static LayerDefinition createSignLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("sign", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F), PartPose.ZERO);
      partdefinition.addOrReplaceChild("stick", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   @OnlyIn(Dist.CLIENT)
   public static final class SignModel extends Model {
      public final ModelPart root;
      public final ModelPart stick;

      public SignModel(ModelPart p_173657_) {
         super(RenderType::entityCutoutNoCull);
         this.root = p_173657_;
         this.stick = p_173657_.getChild("stick");
      }

      public void renderToBuffer(PoseStack p_112510_, VertexConsumer p_112511_, int p_112512_, int p_112513_, float p_112514_, float p_112515_, float p_112516_, float p_112517_) {
         this.root.render(p_112510_, p_112511_, p_112512_, p_112513_, p_112514_, p_112515_, p_112516_, p_112517_);
      }
   }
}