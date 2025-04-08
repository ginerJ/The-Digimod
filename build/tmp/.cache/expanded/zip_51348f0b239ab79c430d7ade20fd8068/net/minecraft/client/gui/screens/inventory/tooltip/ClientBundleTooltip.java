package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientBundleTooltip implements ClientTooltipComponent {
   public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/gui/container/bundle.png");
   private static final int MARGIN_Y = 4;
   private static final int BORDER_WIDTH = 1;
   private static final int TEX_SIZE = 128;
   private static final int SLOT_SIZE_X = 18;
   private static final int SLOT_SIZE_Y = 20;
   private final NonNullList<ItemStack> items;
   private final int weight;

   public ClientBundleTooltip(BundleTooltip p_169873_) {
      this.items = p_169873_.getItems();
      this.weight = p_169873_.getWeight();
   }

   public int getHeight() {
      return this.gridSizeY() * 20 + 2 + 4;
   }

   public int getWidth(Font p_169901_) {
      return this.gridSizeX() * 18 + 2;
   }

   public void renderImage(Font p_194042_, int p_194043_, int p_194044_, GuiGraphics p_282522_) {
      int i = this.gridSizeX();
      int j = this.gridSizeY();
      boolean flag = this.weight >= 64;
      int k = 0;

      for(int l = 0; l < j; ++l) {
         for(int i1 = 0; i1 < i; ++i1) {
            int j1 = p_194043_ + i1 * 18 + 1;
            int k1 = p_194044_ + l * 20 + 1;
            this.renderSlot(j1, k1, k++, flag, p_282522_, p_194042_);
         }
      }

      this.drawBorder(p_194043_, p_194044_, i, j, p_282522_);
   }

   private void renderSlot(int p_283180_, int p_282972_, int p_282547_, boolean p_283053_, GuiGraphics p_283625_, Font p_281863_) {
      if (p_282547_ >= this.items.size()) {
         this.blit(p_283625_, p_283180_, p_282972_, p_283053_ ? ClientBundleTooltip.Texture.BLOCKED_SLOT : ClientBundleTooltip.Texture.SLOT);
      } else {
         ItemStack itemstack = this.items.get(p_282547_);
         this.blit(p_283625_, p_283180_, p_282972_, ClientBundleTooltip.Texture.SLOT);
         p_283625_.renderItem(itemstack, p_283180_ + 1, p_282972_ + 1, p_282547_);
         p_283625_.renderItemDecorations(p_281863_, itemstack, p_283180_ + 1, p_282972_ + 1);
         if (p_282547_ == 0) {
            AbstractContainerScreen.renderSlotHighlight(p_283625_, p_283180_ + 1, p_282972_ + 1, 0);
         }

      }
   }

   private void drawBorder(int p_276018_, int p_276015_, int p_276048_, int p_276056_, GuiGraphics p_283218_) {
      this.blit(p_283218_, p_276018_, p_276015_, ClientBundleTooltip.Texture.BORDER_CORNER_TOP);
      this.blit(p_283218_, p_276018_ + p_276048_ * 18 + 1, p_276015_, ClientBundleTooltip.Texture.BORDER_CORNER_TOP);

      for(int i = 0; i < p_276048_; ++i) {
         this.blit(p_283218_, p_276018_ + 1 + i * 18, p_276015_, ClientBundleTooltip.Texture.BORDER_HORIZONTAL_TOP);
         this.blit(p_283218_, p_276018_ + 1 + i * 18, p_276015_ + p_276056_ * 20, ClientBundleTooltip.Texture.BORDER_HORIZONTAL_BOTTOM);
      }

      for(int j = 0; j < p_276056_; ++j) {
         this.blit(p_283218_, p_276018_, p_276015_ + j * 20 + 1, ClientBundleTooltip.Texture.BORDER_VERTICAL);
         this.blit(p_283218_, p_276018_ + p_276048_ * 18 + 1, p_276015_ + j * 20 + 1, ClientBundleTooltip.Texture.BORDER_VERTICAL);
      }

      this.blit(p_283218_, p_276018_, p_276015_ + p_276056_ * 20, ClientBundleTooltip.Texture.BORDER_CORNER_BOTTOM);
      this.blit(p_283218_, p_276018_ + p_276048_ * 18 + 1, p_276015_ + p_276056_ * 20, ClientBundleTooltip.Texture.BORDER_CORNER_BOTTOM);
   }

   private void blit(GuiGraphics p_281273_, int p_282428_, int p_281897_, ClientBundleTooltip.Texture p_281917_) {
      p_281273_.blit(TEXTURE_LOCATION, p_282428_, p_281897_, 0, (float)p_281917_.x, (float)p_281917_.y, p_281917_.w, p_281917_.h, 128, 128);
   }

   private int gridSizeX() {
      return Math.max(2, (int)Math.ceil(Math.sqrt((double)this.items.size() + 1.0D)));
   }

   private int gridSizeY() {
      return (int)Math.ceil(((double)this.items.size() + 1.0D) / (double)this.gridSizeX());
   }

   @OnlyIn(Dist.CLIENT)
   static enum Texture {
      SLOT(0, 0, 18, 20),
      BLOCKED_SLOT(0, 40, 18, 20),
      BORDER_VERTICAL(0, 18, 1, 20),
      BORDER_HORIZONTAL_TOP(0, 20, 18, 1),
      BORDER_HORIZONTAL_BOTTOM(0, 60, 18, 1),
      BORDER_CORNER_TOP(0, 20, 1, 1),
      BORDER_CORNER_BOTTOM(0, 60, 1, 1);

      public final int x;
      public final int y;
      public final int w;
      public final int h;

      private Texture(int p_169928_, int p_169929_, int p_169930_, int p_169931_) {
         this.x = p_169928_;
         this.y = p_169929_;
         this.w = p_169930_;
         this.h = p_169931_;
      }
   }
}