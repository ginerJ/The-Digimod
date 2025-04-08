package net.minecraft.client.gui.navigation;

import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record ScreenRectangle(ScreenPosition position, int width, int height) {
   private static final ScreenRectangle EMPTY = new ScreenRectangle(0, 0, 0, 0);

   public ScreenRectangle(int p_265721_, int p_265116_, int p_265225_, int p_265493_) {
      this(new ScreenPosition(p_265721_, p_265116_), p_265225_, p_265493_);
   }

   public static ScreenRectangle empty() {
      return EMPTY;
   }

   public static ScreenRectangle of(ScreenAxis p_265648_, int p_265317_, int p_265685_, int p_265218_, int p_265226_) {
      ScreenRectangle screenrectangle;
      switch (p_265648_) {
         case HORIZONTAL:
            screenrectangle = new ScreenRectangle(p_265317_, p_265685_, p_265218_, p_265226_);
            break;
         case VERTICAL:
            screenrectangle = new ScreenRectangle(p_265685_, p_265317_, p_265226_, p_265218_);
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return screenrectangle;
   }

   public ScreenRectangle step(ScreenDirection p_265714_) {
      return new ScreenRectangle(this.position.step(p_265714_), this.width, this.height);
   }

   public int getLength(ScreenAxis p_265463_) {
      int i;
      switch (p_265463_) {
         case HORIZONTAL:
            i = this.width;
            break;
         case VERTICAL:
            i = this.height;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return i;
   }

   public int getBoundInDirection(ScreenDirection p_265778_) {
      ScreenAxis screenaxis = p_265778_.getAxis();
      return p_265778_.isPositive() ? this.position.getCoordinate(screenaxis) + this.getLength(screenaxis) - 1 : this.position.getCoordinate(screenaxis);
   }

   public ScreenRectangle getBorder(ScreenDirection p_265704_) {
      int i = this.getBoundInDirection(p_265704_);
      ScreenAxis screenaxis = p_265704_.getAxis().orthogonal();
      int j = this.getBoundInDirection(screenaxis.getNegative());
      int k = this.getLength(screenaxis);
      return of(p_265704_.getAxis(), i, j, 1, k).step(p_265704_);
   }

   public boolean overlaps(ScreenRectangle p_265652_) {
      return this.overlapsInAxis(p_265652_, ScreenAxis.HORIZONTAL) && this.overlapsInAxis(p_265652_, ScreenAxis.VERTICAL);
   }

   public boolean overlapsInAxis(ScreenRectangle p_265306_, ScreenAxis p_265340_) {
      int i = this.getBoundInDirection(p_265340_.getNegative());
      int j = p_265306_.getBoundInDirection(p_265340_.getNegative());
      int k = this.getBoundInDirection(p_265340_.getPositive());
      int l = p_265306_.getBoundInDirection(p_265340_.getPositive());
      return Math.max(i, j) <= Math.min(k, l);
   }

   public int getCenterInAxis(ScreenAxis p_265694_) {
      return (this.getBoundInDirection(p_265694_.getPositive()) + this.getBoundInDirection(p_265694_.getNegative())) / 2;
   }

   @Nullable
   public ScreenRectangle intersection(ScreenRectangle p_276058_) {
      int i = Math.max(this.left(), p_276058_.left());
      int j = Math.max(this.top(), p_276058_.top());
      int k = Math.min(this.right(), p_276058_.right());
      int l = Math.min(this.bottom(), p_276058_.bottom());
      return i < k && j < l ? new ScreenRectangle(i, j, k - i, l - j) : null;
   }

   public int top() {
      return this.position.y();
   }

   public int bottom() {
      return this.position.y() + this.height;
   }

   public int left() {
      return this.position.x();
   }

   public int right() {
      return this.position.x() + this.width;
   }
}