package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TooltipRenderUtil {
   public static final int MOUSE_OFFSET = 12;
   private static final int PADDING = 3;
   public static final int PADDING_LEFT = 3;
   public static final int PADDING_RIGHT = 3;
   public static final int PADDING_TOP = 3;
   public static final int PADDING_BOTTOM = 3;
   private static final int BACKGROUND_COLOR = -267386864;
   private static final int BORDER_COLOR_TOP = 1347420415;
   private static final int BORDER_COLOR_BOTTOM = 1344798847;

   public static void renderTooltipBackground(GuiGraphics p_282666_, int p_281901_, int p_281846_, int p_281559_, int p_283336_, int p_283422_) {
      renderTooltipBackground(p_282666_, p_281901_, p_281846_, p_281559_, p_283336_, p_283422_, BACKGROUND_COLOR, BACKGROUND_COLOR, BORDER_COLOR_TOP, BORDER_COLOR_BOTTOM);
   }

   // Forge: Allow specifying colors for the inner border gradient and a gradient instead of a single color for the background and outer border
   public static void renderTooltipBackground(GuiGraphics p_282666_, int p_281901_, int p_281846_, int p_281559_, int p_283336_, int p_283422_, int backgroundTop, int backgroundBottom, int borderTop, int borderBottom)
   {
      int i = p_281901_ - 3;
      int j = p_281846_ - 3;
      int k = p_281559_ + 3 + 3;
      int l = p_283336_ + 3 + 3;
      renderHorizontalLine(p_282666_, i, j - 1, k, p_283422_, backgroundTop);
      renderHorizontalLine(p_282666_, i, j + l, k, p_283422_, backgroundBottom);
      renderRectangle(p_282666_, i, j, k, l, p_283422_, backgroundTop, backgroundBottom);
      renderVerticalLineGradient(p_282666_, i - 1, j, l, p_283422_, backgroundTop, backgroundBottom);
      renderVerticalLineGradient(p_282666_, i + k, j, l, p_283422_, backgroundTop, backgroundBottom);
      renderFrameGradient(p_282666_, i, j + 1, k, l, p_283422_, borderTop, borderBottom);
   }

   private static void renderFrameGradient(GuiGraphics p_282000_, int p_282055_, int p_281580_, int p_283284_, int p_282599_, int p_283432_, int p_282907_, int p_283153_) {
      renderVerticalLineGradient(p_282000_, p_282055_, p_281580_, p_282599_ - 2, p_283432_, p_282907_, p_283153_);
      renderVerticalLineGradient(p_282000_, p_282055_ + p_283284_ - 1, p_281580_, p_282599_ - 2, p_283432_, p_282907_, p_283153_);
      renderHorizontalLine(p_282000_, p_282055_, p_281580_ - 1, p_283284_, p_283432_, p_282907_);
      renderHorizontalLine(p_282000_, p_282055_, p_281580_ - 1 + p_282599_ - 1, p_283284_, p_283432_, p_283153_);
   }

   private static void renderVerticalLine(GuiGraphics p_281270_, int p_281928_, int p_281561_, int p_283155_, int p_282552_, int p_282221_) {
      p_281270_.fill(p_281928_, p_281561_, p_281928_ + 1, p_281561_ + p_283155_, p_282552_, p_282221_);
   }

   private static void renderVerticalLineGradient(GuiGraphics p_282478_, int p_282583_, int p_283262_, int p_283161_, int p_283322_, int p_282624_, int p_282756_) {
      p_282478_.fillGradient(p_282583_, p_283262_, p_282583_ + 1, p_283262_ + p_283161_, p_283322_, p_282624_, p_282756_);
   }

   private static void renderHorizontalLine(GuiGraphics p_282981_, int p_282028_, int p_282141_, int p_281771_, int p_282734_, int p_281979_) {
      p_282981_.fill(p_282028_, p_282141_, p_282028_ + p_281771_, p_282141_ + 1, p_282734_, p_281979_);
   }

   /**
   * @deprecated Forge: Use gradient overload instead
   */
   @Deprecated
   private static void renderRectangle(GuiGraphics p_281392_, int p_282294_, int p_283353_, int p_282640_, int p_281964_, int p_283211_, int p_282349_) {
      renderRectangle(p_281392_, p_282294_, p_283353_, p_282640_, p_281964_, p_283211_, p_282349_, p_282349_);
   }

   // Forge: Allow specifying a gradient instead of a single color for the background
   private static void renderRectangle(GuiGraphics p_281392_, int p_282294_, int p_283353_, int p_282640_, int p_281964_, int p_283211_, int p_282349_, int colorTo) {
      p_281392_.fillGradient(p_282294_, p_283353_, p_282294_ + p_282640_, p_283353_ + p_281964_, p_283211_, p_282349_, colorTo);
   }
}
