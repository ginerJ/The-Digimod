package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpacerElement implements LayoutElement {
   private int x;
   private int y;
   private final int width;
   private final int height;

   public SpacerElement(int p_265229_, int p_265527_) {
      this(0, 0, p_265229_, p_265527_);
   }

   public SpacerElement(int p_265199_, int p_265495_, int p_265101_, int p_265469_) {
      this.x = p_265199_;
      this.y = p_265495_;
      this.width = p_265101_;
      this.height = p_265469_;
   }

   public static SpacerElement width(int p_265056_) {
      return new SpacerElement(p_265056_, 0);
   }

   public static SpacerElement height(int p_265087_) {
      return new SpacerElement(0, p_265087_);
   }

   public void setX(int p_265605_) {
      this.x = p_265605_;
   }

   public void setY(int p_265406_) {
      this.y = p_265406_;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void visitWidgets(Consumer<AbstractWidget> p_265477_) {
   }
}