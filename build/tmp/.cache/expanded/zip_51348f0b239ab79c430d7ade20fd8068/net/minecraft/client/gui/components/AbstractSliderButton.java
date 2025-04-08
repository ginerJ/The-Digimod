package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSliderButton extends AbstractWidget {
   public static final ResourceLocation SLIDER_LOCATION = new ResourceLocation("textures/gui/slider.png");
   protected static final int TEXTURE_WIDTH = 200;
   protected static final int TEXTURE_HEIGHT = 20;
   protected static final int TEXTURE_BORDER_X = 20;
   protected static final int TEXTURE_BORDER_Y = 4;
   protected static final int TEXT_MARGIN = 2;
   private static final int HEIGHT = 20;
   private static final int HANDLE_HALF_WIDTH = 4;
   private static final int HANDLE_WIDTH = 8;
   private static final int BACKGROUND = 0;
   private static final int BACKGROUND_FOCUSED = 1;
   private static final int HANDLE = 2;
   private static final int HANDLE_FOCUSED = 3;
   protected double value;
   private boolean canChangeValue;

   public AbstractSliderButton(int p_93579_, int p_93580_, int p_93581_, int p_93582_, Component p_93583_, double p_93584_) {
      super(p_93579_, p_93580_, p_93581_, p_93582_, p_93583_);
      this.value = p_93584_;
   }

   protected int getTextureY() {
      int i = this.isFocused() && !this.canChangeValue ? 1 : 0;
      return i * 20;
   }

   protected int getHandleTextureY() {
      int i = !this.isHovered && !this.canChangeValue ? 2 : 3;
      return i * 20;
   }

   protected MutableComponent createNarrationMessage() {
      return Component.translatable("gui.narrate.slider", this.getMessage());
   }

   public void updateWidgetNarration(NarrationElementOutput p_168798_) {
      p_168798_.add(NarratedElementType.TITLE, this.createNarrationMessage());
      if (this.active) {
         if (this.isFocused()) {
            p_168798_.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.focused"));
         } else {
            p_168798_.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.hovered"));
         }
      }

   }

   public void renderWidget(GuiGraphics p_283427_, int p_281447_, int p_282852_, float p_282409_) {
      Minecraft minecraft = Minecraft.getInstance();
      p_283427_.setColor(1.0F, 1.0F, 1.0F, this.alpha);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.enableDepthTest();
      p_283427_.blitNineSliced(SLIDER_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
      p_283427_.blitNineSliced(SLIDER_LOCATION, this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 8, 20, 20, 4, 200, 20, 0, this.getHandleTextureY());
      p_283427_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      int i = this.active ? 16777215 : 10526880;
      this.renderScrollingString(p_283427_, minecraft.font, 2, i | Mth.ceil(this.alpha * 255.0F) << 24);
   }

   public void onClick(double p_93588_, double p_93589_) {
      this.setValueFromMouse(p_93588_);
   }

   public void setFocused(boolean p_265705_) {
      super.setFocused(p_265705_);
      if (!p_265705_) {
         this.canChangeValue = false;
      } else {
         InputType inputtype = Minecraft.getInstance().getLastInputType();
         if (inputtype == InputType.MOUSE || inputtype == InputType.KEYBOARD_TAB) {
            this.canChangeValue = true;
         }

      }
   }

   public boolean keyPressed(int p_93596_, int p_93597_, int p_93598_) {
      if (CommonInputs.selected(p_93596_)) {
         this.canChangeValue = !this.canChangeValue;
         return true;
      } else {
         if (this.canChangeValue) {
            boolean flag = p_93596_ == 263;
            if (flag || p_93596_ == 262) {
               float f = flag ? -1.0F : 1.0F;
               this.setValue(this.value + (double)(f / (float)(this.width - 8)));
               return true;
            }
         }

         return false;
      }
   }

   private void setValueFromMouse(double p_93586_) {
      this.setValue((p_93586_ - (double)(this.getX() + 4)) / (double)(this.width - 8));
   }

   private void setValue(double p_93612_) {
      double d0 = this.value;
      this.value = Mth.clamp(p_93612_, 0.0D, 1.0D);
      if (d0 != this.value) {
         this.applyValue();
      }

      this.updateMessage();
   }

   protected void onDrag(double p_93591_, double p_93592_, double p_93593_, double p_93594_) {
      this.setValueFromMouse(p_93591_);
      super.onDrag(p_93591_, p_93592_, p_93593_, p_93594_);
   }

   public void playDownSound(SoundManager p_93605_) {
   }

   public void onRelease(double p_93609_, double p_93610_) {
      super.playDownSound(Minecraft.getInstance().getSoundManager());
   }

   protected abstract void updateMessage();

   protected abstract void applyValue();
}