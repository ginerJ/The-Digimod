package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.definitions.CamelAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CamelModel<T extends Camel> extends HierarchicalModel<T> {
   private static final float MAX_WALK_ANIMATION_SPEED = 2.0F;
   private static final float WALK_ANIMATION_SCALE_FACTOR = 2.5F;
   private static final float BABY_SCALE = 0.45F;
   private static final float BABY_Y_OFFSET = 29.35F;
   private static final String SADDLE = "saddle";
   private static final String BRIDLE = "bridle";
   private static final String REINS = "reins";
   private final ModelPart root;
   private final ModelPart head;
   private final ModelPart[] saddleParts;
   private final ModelPart[] ridingParts;

   public CamelModel(ModelPart p_251834_) {
      this.root = p_251834_;
      ModelPart modelpart = p_251834_.getChild("body");
      this.head = modelpart.getChild("head");
      this.saddleParts = new ModelPart[]{modelpart.getChild("saddle"), this.head.getChild("bridle")};
      this.ridingParts = new ModelPart[]{this.head.getChild("reins")};
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      CubeDeformation cubedeformation = new CubeDeformation(0.1F);
      PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 25).addBox(-7.5F, -12.0F, -23.5F, 15.0F, 12.0F, 27.0F), PartPose.offset(0.0F, 4.0F, 9.5F));
      partdefinition1.addOrReplaceChild("hump", CubeListBuilder.create().texOffs(74, 0).addBox(-4.5F, -5.0F, -5.5F, 9.0F, 5.0F, 11.0F), PartPose.offset(0.0F, -12.0F, -10.0F));
      partdefinition1.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(122, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 0.0F), PartPose.offset(0.0F, -9.0F, 3.5F));
      PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(60, 24).addBox(-3.5F, -7.0F, -15.0F, 7.0F, 8.0F, 19.0F).texOffs(21, 0).addBox(-3.5F, -21.0F, -15.0F, 7.0F, 14.0F, 7.0F).texOffs(50, 0).addBox(-2.5F, -21.0F, -21.0F, 5.0F, 5.0F, 6.0F), PartPose.offset(0.0F, -3.0F, -19.5F));
      partdefinition2.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(45, 0).addBox(-0.5F, 0.5F, -1.0F, 3.0F, 1.0F, 2.0F), PartPose.offset(3.0F, -21.0F, -9.5F));
      partdefinition2.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(67, 0).addBox(-2.5F, 0.5F, -1.0F, 3.0F, 1.0F, 2.0F), PartPose.offset(-3.0F, -21.0F, -9.5F));
      partdefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(58, 16).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), PartPose.offset(4.9F, 1.0F, 9.5F));
      partdefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(94, 16).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), PartPose.offset(-4.9F, 1.0F, 9.5F));
      partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), PartPose.offset(4.9F, 1.0F, -10.5F));
      partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 26).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), PartPose.offset(-4.9F, 1.0F, -10.5F));
      partdefinition1.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(74, 64).addBox(-4.5F, -17.0F, -15.5F, 9.0F, 5.0F, 11.0F, cubedeformation).texOffs(92, 114).addBox(-3.5F, -20.0F, -15.5F, 7.0F, 3.0F, 11.0F, cubedeformation).texOffs(0, 89).addBox(-7.5F, -12.0F, -23.5F, 15.0F, 12.0F, 27.0F, cubedeformation), PartPose.offset(0.0F, 0.0F, 0.0F));
      partdefinition2.addOrReplaceChild("reins", CubeListBuilder.create().texOffs(98, 42).addBox(3.51F, -18.0F, -17.0F, 0.0F, 7.0F, 15.0F).texOffs(84, 57).addBox(-3.5F, -18.0F, -2.0F, 7.0F, 7.0F, 0.0F).texOffs(98, 42).addBox(-3.51F, -18.0F, -17.0F, 0.0F, 7.0F, 15.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
      partdefinition2.addOrReplaceChild("bridle", CubeListBuilder.create().texOffs(60, 87).addBox(-3.5F, -7.0F, -15.0F, 7.0F, 8.0F, 19.0F, cubedeformation).texOffs(21, 64).addBox(-3.5F, -21.0F, -15.0F, 7.0F, 14.0F, 7.0F, cubedeformation).texOffs(50, 64).addBox(-2.5F, -21.0F, -21.0F, 5.0F, 5.0F, 6.0F, cubedeformation).texOffs(74, 70).addBox(2.5F, -19.0F, -18.0F, 1.0F, 2.0F, 2.0F).texOffs(74, 70).mirror().addBox(-3.5F, -19.0F, -18.0F, 1.0F, 2.0F, 2.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void setupAnim(T p_250657_, float p_250501_, float p_249554_, float p_249527_, float p_248774_, float p_250710_) {
      this.root().getAllParts().forEach(ModelPart::resetPose);
      this.applyHeadRotation(p_250657_, p_248774_, p_250710_, p_249527_);
      this.toggleInvisibleParts(p_250657_);
      this.animateWalk(CamelAnimation.CAMEL_WALK, p_250501_, p_249554_, 2.0F, 2.5F);
      this.animate(p_250657_.sitAnimationState, CamelAnimation.CAMEL_SIT, p_249527_, 1.0F);
      this.animate(p_250657_.sitPoseAnimationState, CamelAnimation.CAMEL_SIT_POSE, p_249527_, 1.0F);
      this.animate(p_250657_.sitUpAnimationState, CamelAnimation.CAMEL_STANDUP, p_249527_, 1.0F);
      this.animate(p_250657_.idleAnimationState, CamelAnimation.CAMEL_IDLE, p_249527_, 1.0F);
      this.animate(p_250657_.dashAnimationState, CamelAnimation.CAMEL_DASH, p_249527_, 1.0F);
   }

   private void applyHeadRotation(T p_250436_, float p_249176_, float p_251814_, float p_248796_) {
      p_249176_ = Mth.clamp(p_249176_, -30.0F, 30.0F);
      p_251814_ = Mth.clamp(p_251814_, -25.0F, 45.0F);
      if (p_250436_.getJumpCooldown() > 0) {
         float f = p_248796_ - (float)p_250436_.tickCount;
         float f1 = 45.0F * ((float)p_250436_.getJumpCooldown() - f) / 55.0F;
         p_251814_ = Mth.clamp(p_251814_ + f1, -25.0F, 70.0F);
      }

      this.head.yRot = p_249176_ * ((float)Math.PI / 180F);
      this.head.xRot = p_251814_ * ((float)Math.PI / 180F);
   }

   private void toggleInvisibleParts(T p_251765_) {
      boolean flag = p_251765_.isSaddled();
      boolean flag1 = p_251765_.isVehicle();

      for(ModelPart modelpart : this.saddleParts) {
         modelpart.visible = flag;
      }

      for(ModelPart modelpart1 : this.ridingParts) {
         modelpart1.visible = flag1 && flag;
      }

   }

   public void renderToBuffer(PoseStack p_250278_, VertexConsumer p_251678_, int p_249298_, int p_251841_, float p_250541_, float p_248890_, float p_250527_, float p_250536_) {
      if (this.young) {
         p_250278_.pushPose();
         p_250278_.scale(0.45F, 0.45F, 0.45F);
         p_250278_.translate(0.0F, 1.834375F, 0.0F);
         this.root().render(p_250278_, p_251678_, p_249298_, p_251841_, p_250541_, p_248890_, p_250527_, p_250536_);
         p_250278_.popPose();
      } else {
         this.root().render(p_250278_, p_251678_, p_249298_, p_251841_, p_250541_, p_248890_, p_250527_, p_250536_);
      }

   }

   public ModelPart root() {
      return this.root;
   }
}