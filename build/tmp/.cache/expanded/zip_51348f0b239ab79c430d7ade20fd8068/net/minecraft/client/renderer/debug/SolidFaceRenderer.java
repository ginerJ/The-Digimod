package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class SolidFaceRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public SolidFaceRenderer(Minecraft p_113668_) {
      this.minecraft = p_113668_;
   }

   public void render(PoseStack p_113670_, MultiBufferSource p_113671_, double p_113672_, double p_113673_, double p_113674_) {
      Matrix4f matrix4f = p_113670_.last().pose();
      BlockGetter blockgetter = this.minecraft.player.level();
      BlockPos blockpos = BlockPos.containing(p_113672_, p_113673_, p_113674_);

      for(BlockPos blockpos1 : BlockPos.betweenClosed(blockpos.offset(-6, -6, -6), blockpos.offset(6, 6, 6))) {
         BlockState blockstate = blockgetter.getBlockState(blockpos1);
         if (!blockstate.is(Blocks.AIR)) {
            VoxelShape voxelshape = blockstate.getShape(blockgetter, blockpos1);

            for(AABB aabb : voxelshape.toAabbs()) {
               AABB aabb1 = aabb.move(blockpos1).inflate(0.002D);
               float f = (float)(aabb1.minX - p_113672_);
               float f1 = (float)(aabb1.minY - p_113673_);
               float f2 = (float)(aabb1.minZ - p_113674_);
               float f3 = (float)(aabb1.maxX - p_113672_);
               float f4 = (float)(aabb1.maxY - p_113673_);
               float f5 = (float)(aabb1.maxZ - p_113674_);
               float f6 = 1.0F;
               float f7 = 0.0F;
               float f8 = 0.0F;
               float f9 = 0.5F;
               if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.WEST)) {
                  VertexConsumer vertexconsumer = p_113671_.getBuffer(RenderType.debugFilledBox());
                  vertexconsumer.vertex(matrix4f, f, f1, f2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer.vertex(matrix4f, f, f1, f5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer.vertex(matrix4f, f, f4, f2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer.vertex(matrix4f, f, f4, f5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               }

               if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.SOUTH)) {
                  VertexConsumer vertexconsumer1 = p_113671_.getBuffer(RenderType.debugFilledBox());
                  vertexconsumer1.vertex(matrix4f, f, f4, f5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer1.vertex(matrix4f, f, f1, f5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer1.vertex(matrix4f, f3, f4, f5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer1.vertex(matrix4f, f3, f1, f5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               }

               if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.EAST)) {
                  VertexConsumer vertexconsumer2 = p_113671_.getBuffer(RenderType.debugFilledBox());
                  vertexconsumer2.vertex(matrix4f, f3, f1, f5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer2.vertex(matrix4f, f3, f1, f2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer2.vertex(matrix4f, f3, f4, f5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer2.vertex(matrix4f, f3, f4, f2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               }

               if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.NORTH)) {
                  VertexConsumer vertexconsumer3 = p_113671_.getBuffer(RenderType.debugFilledBox());
                  vertexconsumer3.vertex(matrix4f, f3, f4, f2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer3.vertex(matrix4f, f3, f1, f2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer3.vertex(matrix4f, f, f4, f2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer3.vertex(matrix4f, f, f1, f2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               }

               if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.DOWN)) {
                  VertexConsumer vertexconsumer4 = p_113671_.getBuffer(RenderType.debugFilledBox());
                  vertexconsumer4.vertex(matrix4f, f, f1, f2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer4.vertex(matrix4f, f3, f1, f2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer4.vertex(matrix4f, f, f1, f5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer4.vertex(matrix4f, f3, f1, f5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               }

               if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.UP)) {
                  VertexConsumer vertexconsumer5 = p_113671_.getBuffer(RenderType.debugFilledBox());
                  vertexconsumer5.vertex(matrix4f, f, f4, f2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer5.vertex(matrix4f, f, f4, f5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer5.vertex(matrix4f, f3, f4, f2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  vertexconsumer5.vertex(matrix4f, f3, f4, f5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
               }
            }
         }
      }

   }
}