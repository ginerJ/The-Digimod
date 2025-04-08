package net.minecraft.client.gui.navigation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record ScreenPosition(int x, int y) {
   public static ScreenPosition of(ScreenAxis p_265175_, int p_265751_, int p_265120_) {
      ScreenPosition screenposition;
      switch (p_265175_) {
         case HORIZONTAL:
            screenposition = new ScreenPosition(p_265751_, p_265120_);
            break;
         case VERTICAL:
            screenposition = new ScreenPosition(p_265120_, p_265751_);
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return screenposition;
   }

   public ScreenPosition step(ScreenDirection p_265084_) {
      ScreenPosition screenposition;
      switch (p_265084_) {
         case DOWN:
            screenposition = new ScreenPosition(this.x, this.y + 1);
            break;
         case UP:
            screenposition = new ScreenPosition(this.x, this.y - 1);
            break;
         case LEFT:
            screenposition = new ScreenPosition(this.x - 1, this.y);
            break;
         case RIGHT:
            screenposition = new ScreenPosition(this.x + 1, this.y);
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return screenposition;
   }

   public int getCoordinate(ScreenAxis p_265656_) {
      int i;
      switch (p_265656_) {
         case HORIZONTAL:
            i = this.x;
            break;
         case VERTICAL:
            i = this.y;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return i;
   }
}