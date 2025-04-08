package net.minecraft.client.gui.components.events;

import javax.annotation.Nullable;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.TabOrderedElement;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface GuiEventListener extends TabOrderedElement {
   long DOUBLE_CLICK_THRESHOLD_MS = 250L;

   default void mouseMoved(double p_94758_, double p_94759_) {
   }

   default boolean mouseClicked(double p_94737_, double p_94738_, int p_94739_) {
      return false;
   }

   default boolean mouseReleased(double p_94753_, double p_94754_, int p_94755_) {
      return false;
   }

   default boolean mouseDragged(double p_94740_, double p_94741_, int p_94742_, double p_94743_, double p_94744_) {
      return false;
   }

   default boolean mouseScrolled(double p_94734_, double p_94735_, double p_94736_) {
      return false;
   }

   default boolean keyPressed(int p_94745_, int p_94746_, int p_94747_) {
      return false;
   }

   default boolean keyReleased(int p_94750_, int p_94751_, int p_94752_) {
      return false;
   }

   default boolean charTyped(char p_94732_, int p_94733_) {
      return false;
   }

   @Nullable
   default ComponentPath nextFocusPath(FocusNavigationEvent p_265234_) {
      return null;
   }

   default boolean isMouseOver(double p_94748_, double p_94749_) {
      return false;
   }

   void setFocused(boolean p_265728_);

   boolean isFocused();

   @Nullable
   default ComponentPath getCurrentFocusPath() {
      return this.isFocused() ? ComponentPath.leaf(this) : null;
   }

   default ScreenRectangle getRectangle() {
      return ScreenRectangle.empty();
   }
}