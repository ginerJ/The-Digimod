package net.minecraft.client.gui.layouts;

import com.mojang.math.Divisor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LinearLayout extends AbstractLayout {
   private final LinearLayout.Orientation orientation;
   private final List<LinearLayout.ChildContainer> children = new ArrayList<>();
   private final LayoutSettings defaultChildLayoutSettings = LayoutSettings.defaults();

   public LinearLayout(int p_265093_, int p_265502_, LinearLayout.Orientation p_265112_) {
      this(0, 0, p_265093_, p_265502_, p_265112_);
   }

   public LinearLayout(int p_265489_, int p_265500_, int p_265233_, int p_265301_, LinearLayout.Orientation p_265341_) {
      super(p_265489_, p_265500_, p_265233_, p_265301_);
      this.orientation = p_265341_;
   }

   public void arrangeElements() {
      super.arrangeElements();
      if (!this.children.isEmpty()) {
         int i = 0;
         int j = this.orientation.getSecondaryLength(this);

         for(LinearLayout.ChildContainer linearlayout$childcontainer : this.children) {
            i += this.orientation.getPrimaryLength(linearlayout$childcontainer);
            j = Math.max(j, this.orientation.getSecondaryLength(linearlayout$childcontainer));
         }

         int k = this.orientation.getPrimaryLength(this) - i;
         int l = this.orientation.getPrimaryPosition(this);
         Iterator<LinearLayout.ChildContainer> iterator = this.children.iterator();
         LinearLayout.ChildContainer linearlayout$childcontainer1 = iterator.next();
         this.orientation.setPrimaryPosition(linearlayout$childcontainer1, l);
         l += this.orientation.getPrimaryLength(linearlayout$childcontainer1);
         LinearLayout.ChildContainer linearlayout$childcontainer2;
         if (this.children.size() >= 2) {
            for(Divisor divisor = new Divisor(k, this.children.size() - 1); divisor.hasNext(); l += this.orientation.getPrimaryLength(linearlayout$childcontainer2)) {
               l += divisor.nextInt();
               linearlayout$childcontainer2 = iterator.next();
               this.orientation.setPrimaryPosition(linearlayout$childcontainer2, l);
            }
         }

         int i1 = this.orientation.getSecondaryPosition(this);

         for(LinearLayout.ChildContainer linearlayout$childcontainer3 : this.children) {
            this.orientation.setSecondaryPosition(linearlayout$childcontainer3, i1, j);
         }

         switch (this.orientation) {
            case HORIZONTAL:
               this.height = j;
               break;
            case VERTICAL:
               this.width = j;
         }

      }
   }

   public void visitChildren(Consumer<LayoutElement> p_265508_) {
      this.children.forEach((p_265178_) -> {
         p_265508_.accept(p_265178_.child);
      });
   }

   public LayoutSettings newChildLayoutSettings() {
      return this.defaultChildLayoutSettings.copy();
   }

   public LayoutSettings defaultChildLayoutSetting() {
      return this.defaultChildLayoutSettings;
   }

   public <T extends LayoutElement> T addChild(T p_265140_) {
      return this.addChild(p_265140_, this.newChildLayoutSettings());
   }

   public <T extends LayoutElement> T addChild(T p_265475_, LayoutSettings p_265684_) {
      this.children.add(new LinearLayout.ChildContainer(p_265475_, p_265684_));
      return p_265475_;
   }

   @OnlyIn(Dist.CLIENT)
   static class ChildContainer extends AbstractLayout.AbstractChildWrapper {
      protected ChildContainer(LayoutElement p_265706_, LayoutSettings p_265131_) {
         super(p_265706_, p_265131_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Orientation {
      HORIZONTAL,
      VERTICAL;

      int getPrimaryLength(LayoutElement p_265322_) {
         int i;
         switch (this) {
            case HORIZONTAL:
               i = p_265322_.getWidth();
               break;
            case VERTICAL:
               i = p_265322_.getHeight();
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return i;
      }

      int getPrimaryLength(LinearLayout.ChildContainer p_265173_) {
         int i;
         switch (this) {
            case HORIZONTAL:
               i = p_265173_.getWidth();
               break;
            case VERTICAL:
               i = p_265173_.getHeight();
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return i;
      }

      int getSecondaryLength(LayoutElement p_265570_) {
         int i;
         switch (this) {
            case HORIZONTAL:
               i = p_265570_.getHeight();
               break;
            case VERTICAL:
               i = p_265570_.getWidth();
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return i;
      }

      int getSecondaryLength(LinearLayout.ChildContainer p_265345_) {
         int i;
         switch (this) {
            case HORIZONTAL:
               i = p_265345_.getHeight();
               break;
            case VERTICAL:
               i = p_265345_.getWidth();
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return i;
      }

      void setPrimaryPosition(LinearLayout.ChildContainer p_265660_, int p_265194_) {
         switch (this) {
            case HORIZONTAL:
               p_265660_.setX(p_265194_, p_265660_.getWidth());
               break;
            case VERTICAL:
               p_265660_.setY(p_265194_, p_265660_.getHeight());
         }

      }

      void setSecondaryPosition(LinearLayout.ChildContainer p_265536_, int p_265313_, int p_265295_) {
         switch (this) {
            case HORIZONTAL:
               p_265536_.setY(p_265313_, p_265295_);
               break;
            case VERTICAL:
               p_265536_.setX(p_265313_, p_265295_);
         }

      }

      int getPrimaryPosition(LayoutElement p_265209_) {
         int i;
         switch (this) {
            case HORIZONTAL:
               i = p_265209_.getX();
               break;
            case VERTICAL:
               i = p_265209_.getY();
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return i;
      }

      int getSecondaryPosition(LayoutElement p_265676_) {
         int i;
         switch (this) {
            case HORIZONTAL:
               i = p_265676_.getY();
               break;
            case VERTICAL:
               i = p_265676_.getX();
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return i;
      }
   }
}