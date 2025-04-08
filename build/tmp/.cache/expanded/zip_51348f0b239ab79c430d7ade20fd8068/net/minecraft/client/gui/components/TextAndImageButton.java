package net.minecraft.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextAndImageButton extends Button {
   protected final ResourceLocation resourceLocation;
   protected final int xTexStart;
   protected final int yTexStart;
   protected final int yDiffTex;
   protected final int textureWidth;
   protected final int textureHeight;
   private final int xOffset;
   private final int yOffset;
   private final int usedTextureWidth;
   private final int usedTextureHeight;

   TextAndImageButton(Component p_268357_, int p_268106_, int p_268141_, int p_268331_, int p_268045_, int p_268300_, int p_268151_, int p_267955_, int p_268114_, int p_268103_, ResourceLocation p_268067_, Button.OnPress p_268052_) {
      super(0, 0, 150, 20, p_268357_, p_268052_, DEFAULT_NARRATION);
      this.textureWidth = p_268114_;
      this.textureHeight = p_268103_;
      this.xTexStart = p_268106_;
      this.yTexStart = p_268141_;
      this.yDiffTex = p_268300_;
      this.resourceLocation = p_268067_;
      this.xOffset = p_268331_;
      this.yOffset = p_268045_;
      this.usedTextureWidth = p_268151_;
      this.usedTextureHeight = p_267955_;
   }

   public void renderWidget(GuiGraphics p_282062_, int p_283189_, int p_283584_, float p_283402_) {
      super.renderWidget(p_282062_, p_283189_, p_283584_, p_283402_);
      this.renderTexture(p_282062_, this.resourceLocation, this.getXOffset(), this.getYOffset(), this.xTexStart, this.yTexStart, this.yDiffTex, this.usedTextureWidth, this.usedTextureHeight, this.textureWidth, this.textureHeight);
   }

   public void renderString(GuiGraphics p_281792_, Font p_283239_, int p_283135_) {
      int i = this.getX() + 2;
      int j = this.getX() + this.getWidth() - this.usedTextureWidth - 6;
      renderScrollingString(p_281792_, p_283239_, this.getMessage(), i, this.getY(), j, this.getY() + this.getHeight(), p_283135_);
   }

   private int getXOffset() {
      return this.getX() + (this.width / 2 - this.usedTextureWidth / 2) + this.xOffset;
   }

   private int getYOffset() {
      return this.getY() + this.yOffset;
   }

   public static TextAndImageButton.Builder builder(Component p_268304_, ResourceLocation p_268277_, Button.OnPress p_268297_) {
      return new TextAndImageButton.Builder(p_268304_, p_268277_, p_268297_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final Component message;
      private final ResourceLocation resourceLocation;
      private final Button.OnPress onPress;
      private int xTexStart;
      private int yTexStart;
      private int yDiffTex;
      private int usedTextureWidth;
      private int usedTextureHeight;
      private int textureWidth;
      private int textureHeight;
      private int xOffset;
      private int yOffset;

      public Builder(Component p_267988_, ResourceLocation p_268260_, Button.OnPress p_268075_) {
         this.message = p_267988_;
         this.resourceLocation = p_268260_;
         this.onPress = p_268075_;
      }

      public TextAndImageButton.Builder texStart(int p_267995_, int p_268187_) {
         this.xTexStart = p_267995_;
         this.yTexStart = p_268187_;
         return this;
      }

      public TextAndImageButton.Builder offset(int p_268306_, int p_268207_) {
         this.xOffset = p_268306_;
         this.yOffset = p_268207_;
         return this;
      }

      public TextAndImageButton.Builder yDiffTex(int p_268008_) {
         this.yDiffTex = p_268008_;
         return this;
      }

      public TextAndImageButton.Builder usedTextureSize(int p_268087_, int p_268011_) {
         this.usedTextureWidth = p_268087_;
         this.usedTextureHeight = p_268011_;
         return this;
      }

      public TextAndImageButton.Builder textureSize(int p_268166_, int p_268310_) {
         this.textureWidth = p_268166_;
         this.textureHeight = p_268310_;
         return this;
      }

      public TextAndImageButton build() {
         return new TextAndImageButton(this.message, this.xTexStart, this.yTexStart, this.xOffset, this.yOffset, this.yDiffTex, this.usedTextureWidth, this.usedTextureHeight, this.textureWidth, this.textureHeight, this.resourceLocation, this.onPress);
      }
   }
}