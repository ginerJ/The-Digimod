package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RaidDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final int MAX_RENDER_DIST = 160;
   private static final float TEXT_SCALE = 0.04F;
   private final Minecraft minecraft;
   private Collection<BlockPos> raidCenters = Lists.newArrayList();

   public RaidDebugRenderer(Minecraft p_113650_) {
      this.minecraft = p_113650_;
   }

   public void setRaidCenters(Collection<BlockPos> p_113664_) {
      this.raidCenters = p_113664_;
   }

   public void render(PoseStack p_113652_, MultiBufferSource p_113653_, double p_113654_, double p_113655_, double p_113656_) {
      BlockPos blockpos = this.getCamera().getBlockPosition();

      for(BlockPos blockpos1 : this.raidCenters) {
         if (blockpos.closerThan(blockpos1, 160.0D)) {
            highlightRaidCenter(p_113652_, p_113653_, blockpos1);
         }
      }

   }

   private static void highlightRaidCenter(PoseStack p_270914_, MultiBufferSource p_270517_, BlockPos p_270208_) {
      DebugRenderer.renderFilledBox(p_270914_, p_270517_, p_270208_.offset(-1, -1, -1), p_270208_.offset(1, 1, 1), 1.0F, 0.0F, 0.0F, 0.15F);
      int i = -65536;
      renderTextOverBlock(p_270914_, p_270517_, "Raid center", p_270208_, -65536);
   }

   private static void renderTextOverBlock(PoseStack p_270092_, MultiBufferSource p_270518_, String p_270237_, BlockPos p_270941_, int p_270307_) {
      double d0 = (double)p_270941_.getX() + 0.5D;
      double d1 = (double)p_270941_.getY() + 1.3D;
      double d2 = (double)p_270941_.getZ() + 0.5D;
      DebugRenderer.renderFloatingText(p_270092_, p_270518_, p_270237_, d0, d1, d2, p_270307_, 0.04F, true, 0.0F, true);
   }

   private Camera getCamera() {
      return this.minecraft.gameRenderer.getMainCamera();
   }
}