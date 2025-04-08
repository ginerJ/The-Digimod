package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector2i;
import org.joml.Vector2ic;

@OnlyIn(Dist.CLIENT)
public class MenuTooltipPositioner implements ClientTooltipPositioner {
   private static final int MARGIN = 5;
   private static final int MOUSE_OFFSET_X = 12;
   public static final int MAX_OVERLAP_WITH_WIDGET = 3;
   public static final int MAX_DISTANCE_TO_WIDGET = 5;
   private final AbstractWidget widget;

   public MenuTooltipPositioner(AbstractWidget p_268223_) {
      this.widget = p_268223_;
   }

   public Vector2ic positionTooltip(int p_283490_, int p_282509_, int p_282684_, int p_281703_, int p_281348_, int p_283657_) {
      Vector2i vector2i = new Vector2i(p_282684_ + 12, p_281703_);
      if (vector2i.x + p_281348_ > p_283490_ - 5) {
         vector2i.x = Math.max(p_282684_ - 12 - p_281348_, 9);
      }

      vector2i.y += 3;
      int i = p_283657_ + 3 + 3;
      int j = this.widget.getY() + this.widget.getHeight() + 3 + getOffset(0, 0, this.widget.getHeight());
      int k = p_282509_ - 5;
      if (j + i <= k) {
         vector2i.y += getOffset(vector2i.y, this.widget.getY(), this.widget.getHeight());
      } else {
         vector2i.y -= i + getOffset(vector2i.y, this.widget.getY() + this.widget.getHeight(), this.widget.getHeight());
      }

      return vector2i;
   }

   private static int getOffset(int p_268188_, int p_268026_, int p_268015_) {
      int i = Math.min(Math.abs(p_268188_ - p_268026_), p_268015_);
      return Math.round(Mth.lerp((float)i / (float)p_268015_, (float)(p_268015_ - 3), 5.0F));
   }
}