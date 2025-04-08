package net.minecraft.client.gui.navigation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ScreenAxis {
   HORIZONTAL,
   VERTICAL;

   public ScreenAxis orthogonal() {
      ScreenAxis screenaxis;
      switch (this) {
         case HORIZONTAL:
            screenaxis = VERTICAL;
            break;
         case VERTICAL:
            screenaxis = HORIZONTAL;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return screenaxis;
   }

   public ScreenDirection getPositive() {
      ScreenDirection screendirection;
      switch (this) {
         case HORIZONTAL:
            screendirection = ScreenDirection.RIGHT;
            break;
         case VERTICAL:
            screendirection = ScreenDirection.DOWN;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return screendirection;
   }

   public ScreenDirection getNegative() {
      ScreenDirection screendirection;
      switch (this) {
         case HORIZONTAL:
            screendirection = ScreenDirection.LEFT;
            break;
         case VERTICAL:
            screendirection = ScreenDirection.UP;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return screendirection;
   }

   public ScreenDirection getDirection(boolean p_265698_) {
      return p_265698_ ? this.getPositive() : this.getNegative();
   }
}