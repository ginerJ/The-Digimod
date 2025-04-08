package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.EnumSet;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DecoratedPotRenderer implements BlockEntityRenderer<DecoratedPotBlockEntity> {
   private static final String NECK = "neck";
   private static final String FRONT = "front";
   private static final String BACK = "back";
   private static final String LEFT = "left";
   private static final String RIGHT = "right";
   private static final String TOP = "top";
   private static final String BOTTOM = "bottom";
   private final ModelPart neck;
   private final ModelPart frontSide;
   private final ModelPart backSide;
   private final ModelPart leftSide;
   private final ModelPart rightSide;
   private final ModelPart top;
   private final ModelPart bottom;
   private final Material baseMaterial = Objects.requireNonNull(Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.BASE));

   public DecoratedPotRenderer(BlockEntityRendererProvider.Context p_272872_) {
      ModelPart modelpart = p_272872_.bakeLayer(ModelLayers.DECORATED_POT_BASE);
      this.neck = modelpart.getChild("neck");
      this.top = modelpart.getChild("top");
      this.bottom = modelpart.getChild("bottom");
      ModelPart modelpart1 = p_272872_.bakeLayer(ModelLayers.DECORATED_POT_SIDES);
      this.frontSide = modelpart1.getChild("front");
      this.backSide = modelpart1.getChild("back");
      this.leftSide = modelpart1.getChild("left");
      this.rightSide = modelpart1.getChild("right");
   }

   public static LayerDefinition createBaseLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      CubeDeformation cubedeformation = new CubeDeformation(0.2F);
      CubeDeformation cubedeformation1 = new CubeDeformation(-0.1F);
      partdefinition.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 0).addBox(4.0F, 17.0F, 4.0F, 8.0F, 3.0F, 8.0F, cubedeformation1).texOffs(0, 5).addBox(5.0F, 20.0F, 5.0F, 6.0F, 1.0F, 6.0F, cubedeformation), PartPose.offsetAndRotation(0.0F, 37.0F, 16.0F, (float)Math.PI, 0.0F, 0.0F));
      CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(-14, 13).addBox(0.0F, 0.0F, 0.0F, 14.0F, 0.0F, 14.0F);
      partdefinition.addOrReplaceChild("top", cubelistbuilder, PartPose.offsetAndRotation(1.0F, 16.0F, 1.0F, 0.0F, 0.0F, 0.0F));
      partdefinition.addOrReplaceChild("bottom", cubelistbuilder, PartPose.offsetAndRotation(1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F));
      return LayerDefinition.create(meshdefinition, 32, 32);
   }

   public static LayerDefinition createSidesLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(1, 0).addBox(0.0F, 0.0F, 0.0F, 14.0F, 16.0F, 0.0F, EnumSet.of(Direction.NORTH));
      partdefinition.addOrReplaceChild("back", cubelistbuilder, PartPose.offsetAndRotation(15.0F, 16.0F, 1.0F, 0.0F, 0.0F, (float)Math.PI));
      partdefinition.addOrReplaceChild("left", cubelistbuilder, PartPose.offsetAndRotation(1.0F, 16.0F, 1.0F, 0.0F, (-(float)Math.PI / 2F), (float)Math.PI));
      partdefinition.addOrReplaceChild("right", cubelistbuilder, PartPose.offsetAndRotation(15.0F, 16.0F, 15.0F, 0.0F, ((float)Math.PI / 2F), (float)Math.PI));
      partdefinition.addOrReplaceChild("front", cubelistbuilder, PartPose.offsetAndRotation(1.0F, 16.0F, 15.0F, (float)Math.PI, 0.0F, 0.0F));
      return LayerDefinition.create(meshdefinition, 16, 16);
   }

   @Nullable
   private static Material getMaterial(Item p_272698_) {
      Material material = Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.getResourceKey(p_272698_));
      if (material == null) {
         material = Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.getResourceKey(Items.BRICK));
      }

      return material;
   }

   public void render(DecoratedPotBlockEntity p_273776_, float p_273103_, PoseStack p_273455_, MultiBufferSource p_273010_, int p_273407_, int p_273059_) {
      p_273455_.pushPose();
      Direction direction = p_273776_.getDirection();
      p_273455_.translate(0.5D, 0.0D, 0.5D);
      p_273455_.mulPose(Axis.YP.rotationDegrees(180.0F - direction.toYRot()));
      p_273455_.translate(-0.5D, 0.0D, -0.5D);
      VertexConsumer vertexconsumer = this.baseMaterial.buffer(p_273010_, RenderType::entitySolid);
      this.neck.render(p_273455_, vertexconsumer, p_273407_, p_273059_);
      this.top.render(p_273455_, vertexconsumer, p_273407_, p_273059_);
      this.bottom.render(p_273455_, vertexconsumer, p_273407_, p_273059_);
      DecoratedPotBlockEntity.Decorations decoratedpotblockentity$decorations = p_273776_.getDecorations();
      this.renderSide(this.frontSide, p_273455_, p_273010_, p_273407_, p_273059_, getMaterial(decoratedpotblockentity$decorations.front()));
      this.renderSide(this.backSide, p_273455_, p_273010_, p_273407_, p_273059_, getMaterial(decoratedpotblockentity$decorations.back()));
      this.renderSide(this.leftSide, p_273455_, p_273010_, p_273407_, p_273059_, getMaterial(decoratedpotblockentity$decorations.left()));
      this.renderSide(this.rightSide, p_273455_, p_273010_, p_273407_, p_273059_, getMaterial(decoratedpotblockentity$decorations.right()));
      p_273455_.popPose();
   }

   private void renderSide(ModelPart p_273495_, PoseStack p_272899_, MultiBufferSource p_273582_, int p_273242_, int p_273108_, @Nullable Material p_273173_) {
      if (p_273173_ == null) {
         p_273173_ = getMaterial(Items.BRICK);
      }

      if (p_273173_ != null) {
         p_273495_.render(p_272899_, p_273173_.buffer(p_273582_, RenderType::entitySolid), p_273242_, p_273108_);
      }

   }
}