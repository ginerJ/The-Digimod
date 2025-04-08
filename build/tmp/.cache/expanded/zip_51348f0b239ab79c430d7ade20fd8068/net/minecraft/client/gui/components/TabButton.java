package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TabButton extends AbstractWidget {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/gui/tab_button.png");
   private static final int TEXTURE_WIDTH = 130;
   private static final int TEXTURE_HEIGHT = 24;
   private static final int TEXTURE_BORDER = 2;
   private static final int TEXTURE_BORDER_BOTTOM = 0;
   private static final int SELECTED_OFFSET = 3;
   private static final int TEXT_MARGIN = 1;
   private static final int UNDERLINE_HEIGHT = 1;
   private static final int UNDERLINE_MARGIN_X = 4;
   private static final int UNDERLINE_MARGIN_BOTTOM = 2;
   private final TabManager tabManager;
   private final Tab tab;

   public TabButton(TabManager p_275399_, Tab p_275391_, int p_275340_, int p_275364_) {
      super(0, 0, p_275340_, p_275364_, p_275391_.getTabTitle());
      this.tabManager = p_275399_;
      this.tab = p_275391_;
   }

   public void renderWidget(GuiGraphics p_283350_, int p_283437_, int p_281595_, float p_282117_) {
      p_283350_.blitNineSliced(TEXTURE_LOCATION, this.getX(), this.getY(), this.width, this.height, 2, 2, 2, 0, 130, 24, 0, this.getTextureY());
      Font font = Minecraft.getInstance().font;
      int i = this.active ? -1 : -6250336;
      this.renderString(p_283350_, font, i);
      if (this.isSelected()) {
         this.renderFocusUnderline(p_283350_, font, i);
      }

   }

   public void renderString(GuiGraphics p_282917_, Font p_275208_, int p_275293_) {
      int i = this.getX() + 1;
      int j = this.getY() + (this.isSelected() ? 0 : 3);
      int k = this.getX() + this.getWidth() - 1;
      int l = this.getY() + this.getHeight();
      renderScrollingString(p_282917_, p_275208_, this.getMessage(), i, j, k, l, p_275293_);
   }

   private void renderFocusUnderline(GuiGraphics p_282383_, Font p_275475_, int p_275367_) {
      int i = Math.min(p_275475_.width(this.getMessage()), this.getWidth() - 4);
      int j = this.getX() + (this.getWidth() - i) / 2;
      int k = this.getY() + this.getHeight() - 2;
      p_282383_.fill(j, k, j + i, k + 1, p_275367_);
   }

   protected int getTextureY() {
      int i = 2;
      if (this.isSelected() && this.isHoveredOrFocused()) {
         i = 1;
      } else if (this.isSelected()) {
         i = 0;
      } else if (this.isHoveredOrFocused()) {
         i = 3;
      }

      return i * 24;
   }

   protected void updateWidgetNarration(NarrationElementOutput p_275465_) {
      p_275465_.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.tab", this.tab.getTabTitle()));
   }

   public void playDownSound(SoundManager p_276302_) {
   }

   public Tab tab() {
      return this.tab;
   }

   public boolean isSelected() {
      return this.tabManager.getCurrentTab() == this.tab;
   }
}