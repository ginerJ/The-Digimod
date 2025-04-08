package net.minecraft.client.gui.layouts;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface LayoutSettings {
   LayoutSettings padding(int p_265143_);

   LayoutSettings padding(int p_265473_, int p_265776_);

   LayoutSettings padding(int p_265186_, int p_265177_, int p_265631_, int p_265369_);

   LayoutSettings paddingLeft(int p_265709_);

   LayoutSettings paddingTop(int p_265644_);

   LayoutSettings paddingRight(int p_265608_);

   LayoutSettings paddingBottom(int p_265758_);

   LayoutSettings paddingHorizontal(int p_265366_);

   LayoutSettings paddingVertical(int p_265142_);

   LayoutSettings align(float p_265408_, float p_265269_);

   LayoutSettings alignHorizontally(float p_265729_);

   LayoutSettings alignVertically(float p_265557_);

   default LayoutSettings alignHorizontallyLeft() {
      return this.alignHorizontally(0.0F);
   }

   default LayoutSettings alignHorizontallyCenter() {
      return this.alignHorizontally(0.5F);
   }

   default LayoutSettings alignHorizontallyRight() {
      return this.alignHorizontally(1.0F);
   }

   default LayoutSettings alignVerticallyTop() {
      return this.alignVertically(0.0F);
   }

   default LayoutSettings alignVerticallyMiddle() {
      return this.alignVertically(0.5F);
   }

   default LayoutSettings alignVerticallyBottom() {
      return this.alignVertically(1.0F);
   }

   LayoutSettings copy();

   LayoutSettings.LayoutSettingsImpl getExposed();

   static LayoutSettings defaults() {
      return new LayoutSettings.LayoutSettingsImpl();
   }

   @OnlyIn(Dist.CLIENT)
   public static class LayoutSettingsImpl implements LayoutSettings {
      public int paddingLeft;
      public int paddingTop;
      public int paddingRight;
      public int paddingBottom;
      public float xAlignment;
      public float yAlignment;

      public LayoutSettingsImpl() {
      }

      public LayoutSettingsImpl(LayoutSettings.LayoutSettingsImpl p_265146_) {
         this.paddingLeft = p_265146_.paddingLeft;
         this.paddingTop = p_265146_.paddingTop;
         this.paddingRight = p_265146_.paddingRight;
         this.paddingBottom = p_265146_.paddingBottom;
         this.xAlignment = p_265146_.xAlignment;
         this.yAlignment = p_265146_.yAlignment;
      }

      public LayoutSettings.LayoutSettingsImpl padding(int p_265467_) {
         return this.padding(p_265467_, p_265467_);
      }

      public LayoutSettings.LayoutSettingsImpl padding(int p_265284_, int p_265730_) {
         return this.paddingHorizontal(p_265284_).paddingVertical(p_265730_);
      }

      public LayoutSettings.LayoutSettingsImpl padding(int p_265241_, int p_265325_, int p_265634_, int p_265174_) {
         return this.paddingLeft(p_265241_).paddingRight(p_265634_).paddingTop(p_265325_).paddingBottom(p_265174_);
      }

      public LayoutSettings.LayoutSettingsImpl paddingLeft(int p_265137_) {
         this.paddingLeft = p_265137_;
         return this;
      }

      public LayoutSettings.LayoutSettingsImpl paddingTop(int p_265512_) {
         this.paddingTop = p_265512_;
         return this;
      }

      public LayoutSettings.LayoutSettingsImpl paddingRight(int p_265595_) {
         this.paddingRight = p_265595_;
         return this;
      }

      public LayoutSettings.LayoutSettingsImpl paddingBottom(int p_265336_) {
         this.paddingBottom = p_265336_;
         return this;
      }

      public LayoutSettings.LayoutSettingsImpl paddingHorizontal(int p_265592_) {
         return this.paddingLeft(p_265592_).paddingRight(p_265592_);
      }

      public LayoutSettings.LayoutSettingsImpl paddingVertical(int p_265151_) {
         return this.paddingTop(p_265151_).paddingBottom(p_265151_);
      }

      public LayoutSettings.LayoutSettingsImpl align(float p_265459_, float p_265051_) {
         this.xAlignment = p_265459_;
         this.yAlignment = p_265051_;
         return this;
      }

      public LayoutSettings.LayoutSettingsImpl alignHorizontally(float p_265331_) {
         this.xAlignment = p_265331_;
         return this;
      }

      public LayoutSettings.LayoutSettingsImpl alignVertically(float p_265657_) {
         this.yAlignment = p_265657_;
         return this;
      }

      public LayoutSettings.LayoutSettingsImpl copy() {
         return new LayoutSettings.LayoutSettingsImpl(this);
      }

      public LayoutSettings.LayoutSettingsImpl getExposed() {
         return this;
      }
   }
}