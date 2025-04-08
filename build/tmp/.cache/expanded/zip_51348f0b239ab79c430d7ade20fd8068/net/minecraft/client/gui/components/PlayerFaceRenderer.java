package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerFaceRenderer {
   public static final int SKIN_HEAD_U = 8;
   public static final int SKIN_HEAD_V = 8;
   public static final int SKIN_HEAD_WIDTH = 8;
   public static final int SKIN_HEAD_HEIGHT = 8;
   public static final int SKIN_HAT_U = 40;
   public static final int SKIN_HAT_V = 8;
   public static final int SKIN_HAT_WIDTH = 8;
   public static final int SKIN_HAT_HEIGHT = 8;
   public static final int SKIN_TEX_WIDTH = 64;
   public static final int SKIN_TEX_HEIGHT = 64;

   public static void draw(GuiGraphics p_281827_, ResourceLocation p_281637_, int p_282126_, int p_281693_, int p_281565_) {
      draw(p_281827_, p_281637_, p_282126_, p_281693_, p_281565_, true, false);
   }

   public static void draw(GuiGraphics p_283244_, ResourceLocation p_281495_, int p_282035_, int p_282441_, int p_281801_, boolean p_283149_, boolean p_283555_) {
      int i = 8 + (p_283555_ ? 8 : 0);
      int j = 8 * (p_283555_ ? -1 : 1);
      p_283244_.blit(p_281495_, p_282035_, p_282441_, p_281801_, p_281801_, 8.0F, (float)i, 8, j, 64, 64);
      if (p_283149_) {
         drawHat(p_283244_, p_281495_, p_282035_, p_282441_, p_281801_, p_283555_);
      }

   }

   private static void drawHat(GuiGraphics p_282228_, ResourceLocation p_282835_, int p_282585_, int p_282234_, int p_282576_, boolean p_281523_) {
      int i = 8 + (p_281523_ ? 8 : 0);
      int j = 8 * (p_281523_ ? -1 : 1);
      RenderSystem.enableBlend();
      p_282228_.blit(p_282835_, p_282585_, p_282234_, p_282576_, p_282576_, 40.0F, (float)i, 8, j, 64, 64);
      RenderSystem.disableBlend();
   }
}