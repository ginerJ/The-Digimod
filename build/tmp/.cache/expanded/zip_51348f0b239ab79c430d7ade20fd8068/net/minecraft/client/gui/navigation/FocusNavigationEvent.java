package net.minecraft.client.gui.navigation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface FocusNavigationEvent {
   ScreenDirection getVerticalDirectionForInitialFocus();

   @OnlyIn(Dist.CLIENT)
   public static record ArrowNavigation(ScreenDirection direction) implements FocusNavigationEvent {
      public ScreenDirection getVerticalDirectionForInitialFocus() {
         return this.direction.getAxis() == ScreenAxis.VERTICAL ? this.direction : ScreenDirection.DOWN;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class InitialFocus implements FocusNavigationEvent {
      public ScreenDirection getVerticalDirectionForInitialFocus() {
         return ScreenDirection.DOWN;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static record TabNavigation(boolean forward) implements FocusNavigationEvent {
      public ScreenDirection getVerticalDirectionForInitialFocus() {
         return this.forward ? ScreenDirection.DOWN : ScreenDirection.UP;
      }
   }
}