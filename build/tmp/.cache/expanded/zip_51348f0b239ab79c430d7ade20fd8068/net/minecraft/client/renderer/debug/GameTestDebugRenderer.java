package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GameTestDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final float PADDING = 0.02F;
   private final Map<BlockPos, GameTestDebugRenderer.Marker> markers = Maps.newHashMap();

   public void addMarker(BlockPos p_113525_, int p_113526_, String p_113527_, int p_113528_) {
      this.markers.put(p_113525_, new GameTestDebugRenderer.Marker(p_113526_, p_113527_, Util.getMillis() + (long)p_113528_));
   }

   public void clear() {
      this.markers.clear();
   }

   public void render(PoseStack p_113519_, MultiBufferSource p_113520_, double p_113521_, double p_113522_, double p_113523_) {
      long i = Util.getMillis();
      this.markers.entrySet().removeIf((p_113517_) -> {
         return i > (p_113517_.getValue()).removeAtTime;
      });
      this.markers.forEach((p_269737_, p_269738_) -> {
         this.renderMarker(p_113519_, p_113520_, p_269737_, p_269738_);
      });
   }

   private void renderMarker(PoseStack p_270274_, MultiBufferSource p_271018_, BlockPos p_270918_, GameTestDebugRenderer.Marker p_270827_) {
      DebugRenderer.renderFilledBox(p_270274_, p_271018_, p_270918_, 0.02F, p_270827_.getR(), p_270827_.getG(), p_270827_.getB(), p_270827_.getA() * 0.75F);
      if (!p_270827_.text.isEmpty()) {
         double d0 = (double)p_270918_.getX() + 0.5D;
         double d1 = (double)p_270918_.getY() + 1.2D;
         double d2 = (double)p_270918_.getZ() + 0.5D;
         DebugRenderer.renderFloatingText(p_270274_, p_271018_, p_270827_.text, d0, d1, d2, -1, 0.01F, true, 0.0F, true);
      }

   }

   @OnlyIn(Dist.CLIENT)
   static class Marker {
      public int color;
      public String text;
      public long removeAtTime;

      public Marker(int p_113536_, String p_113537_, long p_113538_) {
         this.color = p_113536_;
         this.text = p_113537_;
         this.removeAtTime = p_113538_;
      }

      public float getR() {
         return (float)(this.color >> 16 & 255) / 255.0F;
      }

      public float getG() {
         return (float)(this.color >> 8 & 255) / 255.0F;
      }

      public float getB() {
         return (float)(this.color & 255) / 255.0F;
      }

      public float getA() {
         return (float)(this.color >> 24 & 255) / 255.0F;
      }
   }
}