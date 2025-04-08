package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleSupplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SupportBlockRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private double lastUpdateTime = Double.MIN_VALUE;
   private List<Entity> surroundEntities = Collections.emptyList();

   public SupportBlockRenderer(Minecraft p_286424_) {
      this.minecraft = p_286424_;
   }

   public void render(PoseStack p_286297_, MultiBufferSource p_286436_, double p_286291_, double p_286388_, double p_286330_) {
      double d0 = (double)Util.getNanos();
      if (d0 - this.lastUpdateTime > 1.0E8D) {
         this.lastUpdateTime = d0;
         Entity entity = this.minecraft.gameRenderer.getMainCamera().getEntity();
         this.surroundEntities = ImmutableList.copyOf(entity.level().getEntities(entity, entity.getBoundingBox().inflate(16.0D)));
      }

      Player player = this.minecraft.player;
      if (player != null && player.mainSupportingBlockPos.isPresent()) {
         this.drawHighlights(p_286297_, p_286436_, p_286291_, p_286388_, p_286330_, player, () -> {
            return 0.0D;
         }, 1.0F, 0.0F, 0.0F);
      }

      for(Entity entity1 : this.surroundEntities) {
         if (entity1 != player) {
            this.drawHighlights(p_286297_, p_286436_, p_286291_, p_286388_, p_286330_, entity1, () -> {
               return this.getBias(entity1);
            }, 0.0F, 1.0F, 0.0F);
         }
      }

   }

   private void drawHighlights(PoseStack p_286525_, MultiBufferSource p_286495_, double p_286696_, double p_286417_, double p_286386_, Entity p_286273_, DoubleSupplier p_286458_, float p_286487_, float p_286710_, float p_286793_) {
      p_286273_.mainSupportingBlockPos.ifPresent((p_286428_) -> {
         double d0 = p_286458_.getAsDouble();
         BlockPos blockpos = p_286273_.getOnPos();
         this.highlightPosition(blockpos, p_286525_, p_286696_, p_286417_, p_286386_, p_286495_, 0.02D + d0, p_286487_, p_286710_, p_286793_);
         BlockPos blockpos1 = p_286273_.getOnPosLegacy();
         if (!blockpos1.equals(blockpos)) {
            this.highlightPosition(blockpos1, p_286525_, p_286696_, p_286417_, p_286386_, p_286495_, 0.04D + d0, 0.0F, 1.0F, 1.0F);
         }

      });
   }

   private double getBias(Entity p_286713_) {
      return 0.02D * (double)(String.valueOf((double)p_286713_.getId() + 0.132453657D).hashCode() % 1000) / 1000.0D;
   }

   private void highlightPosition(BlockPos p_286268_, PoseStack p_286592_, double p_286463_, double p_286552_, double p_286660_, MultiBufferSource p_286314_, double p_286880_, float p_286918_, float p_286304_, float p_286672_) {
      double d0 = (double)p_286268_.getX() - p_286463_ - 2.0D * p_286880_;
      double d1 = (double)p_286268_.getY() - p_286552_ - 2.0D * p_286880_;
      double d2 = (double)p_286268_.getZ() - p_286660_ - 2.0D * p_286880_;
      double d3 = d0 + 1.0D + 4.0D * p_286880_;
      double d4 = d1 + 1.0D + 4.0D * p_286880_;
      double d5 = d2 + 1.0D + 4.0D * p_286880_;
      LevelRenderer.renderLineBox(p_286592_, p_286314_.getBuffer(RenderType.lines()), d0, d1, d2, d3, d4, d5, p_286918_, p_286304_, p_286672_, 0.4F);
      LevelRenderer.renderVoxelShape(p_286592_, p_286314_.getBuffer(RenderType.lines()), this.minecraft.level.getBlockState(p_286268_).getCollisionShape(this.minecraft.level, p_286268_, CollisionContext.empty()).move((double)p_286268_.getX(), (double)p_286268_.getY(), (double)p_286268_.getZ()), -p_286463_, -p_286552_, -p_286660_, p_286918_, p_286304_, p_286672_, 1.0F, false);
   }
}