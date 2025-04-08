package net.minecraft.client.gui.components.spectator;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenuListener;
import net.minecraft.client.gui.spectator.categories.SpectatorPage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpectatorGui implements SpectatorMenuListener {
   private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   public static final ResourceLocation SPECTATOR_LOCATION = new ResourceLocation("textures/gui/spectator_widgets.png");
   private static final long FADE_OUT_DELAY = 5000L;
   private static final long FADE_OUT_TIME = 2000L;
   private final Minecraft minecraft;
   private long lastSelectionTime;
   @Nullable
   private SpectatorMenu menu;

   public SpectatorGui(Minecraft p_94767_) {
      this.minecraft = p_94767_;
   }

   public void onHotbarSelected(int p_94772_) {
      this.lastSelectionTime = Util.getMillis();
      if (this.menu != null) {
         this.menu.selectSlot(p_94772_);
      } else {
         this.menu = new SpectatorMenu(this);
      }

   }

   private float getHotbarAlpha() {
      long i = this.lastSelectionTime - Util.getMillis() + 5000L;
      return Mth.clamp((float)i / 2000.0F, 0.0F, 1.0F);
   }

   public void renderHotbar(GuiGraphics p_281458_) {
      if (this.menu != null) {
         float f = this.getHotbarAlpha();
         if (f <= 0.0F) {
            this.menu.exit();
         } else {
            int i = p_281458_.guiWidth() / 2;
            p_281458_.pose().pushPose();
            p_281458_.pose().translate(0.0F, 0.0F, -90.0F);
            int j = Mth.floor((float)p_281458_.guiHeight() - 22.0F * f);
            SpectatorPage spectatorpage = this.menu.getCurrentPage();
            this.renderPage(p_281458_, f, i, j, spectatorpage);
            p_281458_.pose().popPose();
         }
      }
   }

   protected void renderPage(GuiGraphics p_282945_, float p_281688_, int p_281726_, int p_281730_, SpectatorPage p_282361_) {
      RenderSystem.enableBlend();
      p_282945_.setColor(1.0F, 1.0F, 1.0F, p_281688_);
      p_282945_.blit(WIDGETS_LOCATION, p_281726_ - 91, p_281730_, 0, 0, 182, 22);
      if (p_282361_.getSelectedSlot() >= 0) {
         p_282945_.blit(WIDGETS_LOCATION, p_281726_ - 91 - 1 + p_282361_.getSelectedSlot() * 20, p_281730_ - 1, 0, 22, 24, 22);
      }

      p_282945_.setColor(1.0F, 1.0F, 1.0F, 1.0F);

      for(int i = 0; i < 9; ++i) {
         this.renderSlot(p_282945_, i, p_282945_.guiWidth() / 2 - 90 + i * 20 + 2, (float)(p_281730_ + 3), p_281688_, p_282361_.getItem(i));
      }

      RenderSystem.disableBlend();
   }

   private void renderSlot(GuiGraphics p_281411_, int p_283536_, int p_281853_, float p_282693_, float p_281955_, SpectatorMenuItem p_283370_) {
      if (p_283370_ != SpectatorMenu.EMPTY_SLOT) {
         int i = (int)(p_281955_ * 255.0F);
         p_281411_.pose().pushPose();
         p_281411_.pose().translate((float)p_281853_, p_282693_, 0.0F);
         float f = p_283370_.isEnabled() ? 1.0F : 0.25F;
         p_281411_.setColor(f, f, f, p_281955_);
         p_283370_.renderIcon(p_281411_, f, i);
         p_281411_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         p_281411_.pose().popPose();
         if (i > 3 && p_283370_.isEnabled()) {
            Component component = this.minecraft.options.keyHotbarSlots[p_283536_].getTranslatedKeyMessage();
            p_281411_.drawString(this.minecraft.font, component, p_281853_ + 19 - 2 - this.minecraft.font.width(component), (int)p_282693_ + 6 + 3, 16777215 + (i << 24));
         }
      }

   }

   public void renderTooltip(GuiGraphics p_283107_) {
      int i = (int)(this.getHotbarAlpha() * 255.0F);
      if (i > 3 && this.menu != null) {
         SpectatorMenuItem spectatormenuitem = this.menu.getSelectedItem();
         Component component = spectatormenuitem == SpectatorMenu.EMPTY_SLOT ? this.menu.getSelectedCategory().getPrompt() : spectatormenuitem.getName();
         if (component != null) {
            int j = (p_283107_.guiWidth() - this.minecraft.font.width(component)) / 2;
            int k = p_283107_.guiHeight() - 35;
            p_283107_.drawString(this.minecraft.font, component, j, k, 16777215 + (i << 24));
         }
      }

   }

   public void onSpectatorMenuClosed(SpectatorMenu p_94792_) {
      this.menu = null;
      this.lastSelectionTime = 0L;
   }

   public boolean isMenuActive() {
      return this.menu != null;
   }

   public void onMouseScrolled(int p_205381_) {
      int i;
      for(i = this.menu.getSelectedSlot() + p_205381_; i >= 0 && i <= 8 && (this.menu.getItem(i) == SpectatorMenu.EMPTY_SLOT || !this.menu.getItem(i).isEnabled()); i += p_205381_) {
      }

      if (i >= 0 && i <= 8) {
         this.menu.selectSlot(i);
         this.lastSelectionTime = Util.getMillis();
      }

   }

   public void onMouseMiddleClick() {
      this.lastSelectionTime = Util.getMillis();
      if (this.isMenuActive()) {
         int i = this.menu.getSelectedSlot();
         if (i != -1) {
            this.menu.selectSlot(i);
         }
      } else {
         this.menu = new SpectatorMenu(this);
      }

   }
}