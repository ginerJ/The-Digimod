package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Map;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HangingSignRenderer extends SignRenderer {
   private static final String PLANK = "plank";
   private static final String V_CHAINS = "vChains";
   private static final String NORMAL_CHAINS = "normalChains";
   private static final String CHAIN_L_1 = "chainL1";
   private static final String CHAIN_L_2 = "chainL2";
   private static final String CHAIN_R_1 = "chainR1";
   private static final String CHAIN_R_2 = "chainR2";
   private static final String BOARD = "board";
   private static final float MODEL_RENDER_SCALE = 1.0F;
   private static final float TEXT_RENDER_SCALE = 0.9F;
   private static final Vec3 TEXT_OFFSET = new Vec3(0.0D, (double)-0.32F, (double)0.073F);
   private final Map<WoodType, HangingSignRenderer.HangingSignModel> hangingSignModels;

   public HangingSignRenderer(BlockEntityRendererProvider.Context p_248772_) {
      super(p_248772_);
      this.hangingSignModels = WoodType.values().collect(ImmutableMap.toImmutableMap((p_249901_) -> {
         return p_249901_;
      }, (p_251956_) -> {
         return new HangingSignRenderer.HangingSignModel(p_248772_.bakeLayer(ModelLayers.createHangingSignModelName(p_251956_)));
      }));
   }

   public float getSignModelRenderScale() {
      return 1.0F;
   }

   public float getSignTextRenderScale() {
      return 0.9F;
   }

   public void render(SignBlockEntity p_249482_, float p_249273_, PoseStack p_250062_, MultiBufferSource p_250878_, int p_249035_, int p_252030_) {
      BlockState blockstate = p_249482_.getBlockState();
      SignBlock signblock = (SignBlock)blockstate.getBlock();
      WoodType woodtype = SignBlock.getWoodType(signblock);
      HangingSignRenderer.HangingSignModel hangingsignrenderer$hangingsignmodel = this.hangingSignModels.get(woodtype);
      hangingsignrenderer$hangingsignmodel.evaluateVisibleParts(blockstate);
      this.renderSignWithText(p_249482_, p_250062_, p_250878_, p_249035_, p_252030_, blockstate, signblock, woodtype, hangingsignrenderer$hangingsignmodel);
   }

   void translateSign(PoseStack p_277807_, float p_277917_, BlockState p_277638_) {
      p_277807_.translate(0.5D, 0.9375D, 0.5D);
      p_277807_.mulPose(Axis.YP.rotationDegrees(p_277917_));
      p_277807_.translate(0.0F, -0.3125F, 0.0F);
   }

   void renderSignModel(PoseStack p_251159_, int p_249874_, int p_249794_, Model p_248746_, VertexConsumer p_249165_) {
      HangingSignRenderer.HangingSignModel hangingsignrenderer$hangingsignmodel = (HangingSignRenderer.HangingSignModel)p_248746_;
      hangingsignrenderer$hangingsignmodel.root.render(p_251159_, p_249165_, p_249874_, p_249794_);
   }

   Material getSignMaterial(WoodType p_251791_) {
      return Sheets.getHangingSignMaterial(p_251791_);
   }

   Vec3 getTextOffset() {
      return TEXT_OFFSET;
   }

   public static LayerDefinition createHangingSignLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("board", CubeListBuilder.create().texOffs(0, 12).addBox(-7.0F, 0.0F, -1.0F, 14.0F, 10.0F, 2.0F), PartPose.ZERO);
      partdefinition.addOrReplaceChild("plank", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -6.0F, -2.0F, 16.0F, 2.0F, 4.0F), PartPose.ZERO);
      PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("normalChains", CubeListBuilder.create(), PartPose.ZERO);
      partdefinition1.addOrReplaceChild("chainL1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, (-(float)Math.PI / 4F), 0.0F));
      partdefinition1.addOrReplaceChild("chainL2", CubeListBuilder.create().texOffs(6, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, ((float)Math.PI / 4F), 0.0F));
      partdefinition1.addOrReplaceChild("chainR1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, (-(float)Math.PI / 4F), 0.0F));
      partdefinition1.addOrReplaceChild("chainR2", CubeListBuilder.create().texOffs(6, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, ((float)Math.PI / 4F), 0.0F));
      partdefinition.addOrReplaceChild("vChains", CubeListBuilder.create().texOffs(14, 6).addBox(-6.0F, -6.0F, 0.0F, 12.0F, 6.0F, 0.0F), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   @OnlyIn(Dist.CLIENT)
   public static final class HangingSignModel extends Model {
      public final ModelPart root;
      public final ModelPart plank;
      public final ModelPart vChains;
      public final ModelPart normalChains;

      public HangingSignModel(ModelPart p_249124_) {
         super(RenderType::entityCutoutNoCull);
         this.root = p_249124_;
         this.plank = p_249124_.getChild("plank");
         this.normalChains = p_249124_.getChild("normalChains");
         this.vChains = p_249124_.getChild("vChains");
      }

      public void evaluateVisibleParts(BlockState p_250268_) {
         boolean flag = !(p_250268_.getBlock() instanceof CeilingHangingSignBlock);
         this.plank.visible = flag;
         this.vChains.visible = false;
         this.normalChains.visible = true;
         if (!flag) {
            boolean flag1 = p_250268_.getValue(BlockStateProperties.ATTACHED);
            this.normalChains.visible = !flag1;
            this.vChains.visible = flag1;
         }

      }

      public void renderToBuffer(PoseStack p_251590_, VertexConsumer p_249470_, int p_250912_, int p_251773_, float p_248839_, float p_249626_, float p_251131_, float p_249679_) {
         this.root.render(p_251590_, p_249470_, p_250912_, p_251773_, p_248839_, p_249626_, p_251131_, p_249679_);
      }
   }
}