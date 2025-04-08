package net.minecraft.client.gui.screens.advancements;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
enum AdvancementTabType {
   ABOVE(0, 0, 28, 32, 8),
   BELOW(84, 0, 28, 32, 8),
   LEFT(0, 64, 32, 28, 5),
   RIGHT(96, 64, 32, 28, 5);

   public static final int MAX_TABS = java.util.Arrays.stream(values()).mapToInt(e -> e.max).sum();
   private final int textureX;
   private final int textureY;
   private final int width;
   private final int height;
   private final int max;

   private AdvancementTabType(int p_97205_, int p_97206_, int p_97207_, int p_97208_, int p_97209_) {
      this.textureX = p_97205_;
      this.textureY = p_97206_;
      this.width = p_97207_;
      this.height = p_97208_;
      this.max = p_97209_;
   }

   public int getMax() {
      return this.max;
   }

   public void draw(GuiGraphics p_283216_, int p_282432_, int p_283617_, boolean p_282320_, int p_281898_) {
      int i = this.textureX;
      if (p_281898_ > 0) {
         i += this.width;
      }

      if (p_281898_ == this.max - 1) {
         i += this.width;
      }

      int j = p_282320_ ? this.textureY + this.height : this.textureY;
      p_283216_.blit(AdvancementsScreen.TABS_LOCATION, p_282432_ + this.getX(p_281898_), p_283617_ + this.getY(p_281898_), i, j, this.width, this.height);
   }

   public void drawIcon(GuiGraphics p_281370_, int p_283209_, int p_282807_, int p_282968_, ItemStack p_283383_) {
      int i = p_283209_ + this.getX(p_282968_);
      int j = p_282807_ + this.getY(p_282968_);
      switch (this) {
         case ABOVE:
            i += 6;
            j += 9;
            break;
         case BELOW:
            i += 6;
            j += 6;
            break;
         case LEFT:
            i += 10;
            j += 5;
            break;
         case RIGHT:
            i += 6;
            j += 5;
      }

      p_281370_.renderFakeItem(p_283383_, i, j);
   }

   public int getX(int p_97212_) {
      switch (this) {
         case ABOVE:
            return (this.width + 4) * p_97212_;
         case BELOW:
            return (this.width + 4) * p_97212_;
         case LEFT:
            return -this.width + 4;
         case RIGHT:
            return 248;
         default:
            throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
      }
   }

   public int getY(int p_97233_) {
      switch (this) {
         case ABOVE:
            return -this.height + 4;
         case BELOW:
            return 136;
         case LEFT:
            return this.height * p_97233_;
         case RIGHT:
            return this.height * p_97233_;
         default:
            throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
      }
   }

   public boolean isMouseOver(int p_97214_, int p_97215_, int p_97216_, double p_97217_, double p_97218_) {
      int i = p_97214_ + this.getX(p_97216_);
      int j = p_97215_ + this.getY(p_97216_);
      return p_97217_ > (double)i && p_97217_ < (double)(i + this.width) && p_97218_ > (double)j && p_97218_ < (double)(j + this.height);
   }
}
