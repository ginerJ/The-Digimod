package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Locale;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PathfindingRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Map<Integer, Path> pathMap = Maps.newHashMap();
   private final Map<Integer, Float> pathMaxDist = Maps.newHashMap();
   private final Map<Integer, Long> creationMap = Maps.newHashMap();
   private static final long TIMEOUT = 5000L;
   private static final float MAX_RENDER_DIST = 80.0F;
   private static final boolean SHOW_OPEN_CLOSED = true;
   private static final boolean SHOW_OPEN_CLOSED_COST_MALUS = false;
   private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_TEXT = false;
   private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_BOX = true;
   private static final boolean SHOW_GROUND_LABELS = true;
   private static final float TEXT_SCALE = 0.02F;

   public void addPath(int p_113612_, Path p_113613_, float p_113614_) {
      this.pathMap.put(p_113612_, p_113613_);
      this.creationMap.put(p_113612_, Util.getMillis());
      this.pathMaxDist.put(p_113612_, p_113614_);
   }

   public void render(PoseStack p_113629_, MultiBufferSource p_113630_, double p_113631_, double p_113632_, double p_113633_) {
      if (!this.pathMap.isEmpty()) {
         long i = Util.getMillis();

         for(Integer integer : this.pathMap.keySet()) {
            Path path = this.pathMap.get(integer);
            float f = this.pathMaxDist.get(integer);
            renderPath(p_113629_, p_113630_, path, f, true, true, p_113631_, p_113632_, p_113633_);
         }

         for(Integer integer1 : this.creationMap.keySet().toArray(new Integer[0])) {
            if (i - this.creationMap.get(integer1) > 5000L) {
               this.pathMap.remove(integer1);
               this.creationMap.remove(integer1);
            }
         }

      }
   }

   public static void renderPath(PoseStack p_270399_, MultiBufferSource p_270359_, Path p_270189_, float p_270841_, boolean p_270481_, boolean p_270748_, double p_270187_, double p_270252_, double p_270371_) {
      renderPathLine(p_270399_, p_270359_.getBuffer(RenderType.debugLineStrip(6.0D)), p_270189_, p_270187_, p_270252_, p_270371_);
      BlockPos blockpos = p_270189_.getTarget();
      if (distanceToCamera(blockpos, p_270187_, p_270252_, p_270371_) <= 80.0F) {
         DebugRenderer.renderFilledBox(p_270399_, p_270359_, (new AABB((double)((float)blockpos.getX() + 0.25F), (double)((float)blockpos.getY() + 0.25F), (double)blockpos.getZ() + 0.25D, (double)((float)blockpos.getX() + 0.75F), (double)((float)blockpos.getY() + 0.75F), (double)((float)blockpos.getZ() + 0.75F))).move(-p_270187_, -p_270252_, -p_270371_), 0.0F, 1.0F, 0.0F, 0.5F);

         for(int i = 0; i < p_270189_.getNodeCount(); ++i) {
            Node node = p_270189_.getNode(i);
            if (distanceToCamera(node.asBlockPos(), p_270187_, p_270252_, p_270371_) <= 80.0F) {
               float f = i == p_270189_.getNextNodeIndex() ? 1.0F : 0.0F;
               float f1 = i == p_270189_.getNextNodeIndex() ? 0.0F : 1.0F;
               DebugRenderer.renderFilledBox(p_270399_, p_270359_, (new AABB((double)((float)node.x + 0.5F - p_270841_), (double)((float)node.y + 0.01F * (float)i), (double)((float)node.z + 0.5F - p_270841_), (double)((float)node.x + 0.5F + p_270841_), (double)((float)node.y + 0.25F + 0.01F * (float)i), (double)((float)node.z + 0.5F + p_270841_))).move(-p_270187_, -p_270252_, -p_270371_), f, 0.0F, f1, 0.5F);
            }
         }
      }

      if (p_270481_) {
         for(Node node2 : p_270189_.getClosedSet()) {
            if (distanceToCamera(node2.asBlockPos(), p_270187_, p_270252_, p_270371_) <= 80.0F) {
               DebugRenderer.renderFilledBox(p_270399_, p_270359_, (new AABB((double)((float)node2.x + 0.5F - p_270841_ / 2.0F), (double)((float)node2.y + 0.01F), (double)((float)node2.z + 0.5F - p_270841_ / 2.0F), (double)((float)node2.x + 0.5F + p_270841_ / 2.0F), (double)node2.y + 0.1D, (double)((float)node2.z + 0.5F + p_270841_ / 2.0F))).move(-p_270187_, -p_270252_, -p_270371_), 1.0F, 0.8F, 0.8F, 0.5F);
            }
         }

         for(Node node3 : p_270189_.getOpenSet()) {
            if (distanceToCamera(node3.asBlockPos(), p_270187_, p_270252_, p_270371_) <= 80.0F) {
               DebugRenderer.renderFilledBox(p_270399_, p_270359_, (new AABB((double)((float)node3.x + 0.5F - p_270841_ / 2.0F), (double)((float)node3.y + 0.01F), (double)((float)node3.z + 0.5F - p_270841_ / 2.0F), (double)((float)node3.x + 0.5F + p_270841_ / 2.0F), (double)node3.y + 0.1D, (double)((float)node3.z + 0.5F + p_270841_ / 2.0F))).move(-p_270187_, -p_270252_, -p_270371_), 0.8F, 1.0F, 1.0F, 0.5F);
            }
         }
      }

      if (p_270748_) {
         for(int j = 0; j < p_270189_.getNodeCount(); ++j) {
            Node node1 = p_270189_.getNode(j);
            if (distanceToCamera(node1.asBlockPos(), p_270187_, p_270252_, p_270371_) <= 80.0F) {
               DebugRenderer.renderFloatingText(p_270399_, p_270359_, String.valueOf((Object)node1.type), (double)node1.x + 0.5D, (double)node1.y + 0.75D, (double)node1.z + 0.5D, -1, 0.02F, true, 0.0F, true);
               DebugRenderer.renderFloatingText(p_270399_, p_270359_, String.format(Locale.ROOT, "%.2f", node1.costMalus), (double)node1.x + 0.5D, (double)node1.y + 0.25D, (double)node1.z + 0.5D, -1, 0.02F, true, 0.0F, true);
            }
         }
      }

   }

   public static void renderPathLine(PoseStack p_270666_, VertexConsumer p_270602_, Path p_270511_, double p_270524_, double p_270163_, double p_270176_) {
      for(int i = 0; i < p_270511_.getNodeCount(); ++i) {
         Node node = p_270511_.getNode(i);
         if (!(distanceToCamera(node.asBlockPos(), p_270524_, p_270163_, p_270176_) > 80.0F)) {
            float f = (float)i / (float)p_270511_.getNodeCount() * 0.33F;
            int j = i == 0 ? 0 : Mth.hsvToRgb(f, 0.9F, 0.9F);
            int k = j >> 16 & 255;
            int l = j >> 8 & 255;
            int i1 = j & 255;
            p_270602_.vertex(p_270666_.last().pose(), (float)((double)node.x - p_270524_ + 0.5D), (float)((double)node.y - p_270163_ + 0.5D), (float)((double)node.z - p_270176_ + 0.5D)).color(k, l, i1, 255).endVertex();
         }
      }

   }

   private static float distanceToCamera(BlockPos p_113635_, double p_113636_, double p_113637_, double p_113638_) {
      return (float)(Math.abs((double)p_113635_.getX() - p_113636_) + Math.abs((double)p_113635_.getY() - p_113637_) + Math.abs((double)p_113635_.getZ() - p_113638_));
   }
}