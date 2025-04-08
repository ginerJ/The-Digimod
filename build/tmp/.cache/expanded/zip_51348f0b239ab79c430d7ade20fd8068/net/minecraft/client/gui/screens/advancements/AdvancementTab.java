package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancementTab {
   private final Minecraft minecraft;
   private final AdvancementsScreen screen;
   private final AdvancementTabType type;
   private final int index;
   private final Advancement advancement;
   private final DisplayInfo display;
   private final ItemStack icon;
   private final Component title;
   private final AdvancementWidget root;
   private final Map<Advancement, AdvancementWidget> widgets = Maps.newLinkedHashMap();
   private double scrollX;
   private double scrollY;
   private int minX = Integer.MAX_VALUE;
   private int minY = Integer.MAX_VALUE;
   private int maxX = Integer.MIN_VALUE;
   private int maxY = Integer.MIN_VALUE;
   private float fade;
   private boolean centered;
   private int page;

   public AdvancementTab(Minecraft p_97145_, AdvancementsScreen p_97146_, AdvancementTabType p_97147_, int p_97148_, Advancement p_97149_, DisplayInfo p_97150_) {
      this.minecraft = p_97145_;
      this.screen = p_97146_;
      this.type = p_97147_;
      this.index = p_97148_;
      this.advancement = p_97149_;
      this.display = p_97150_;
      this.icon = p_97150_.getIcon();
      this.title = p_97150_.getTitle();
      this.root = new AdvancementWidget(this, p_97145_, p_97149_, p_97150_);
      this.addWidget(this.root, p_97149_);
   }

   public AdvancementTab(Minecraft mc, AdvancementsScreen screen, AdvancementTabType type, int index, int page, Advancement adv, DisplayInfo info) {
      this(mc, screen, type, index, adv, info);
      this.page = page;
   }

   public int getPage() {
      return page;
   }

   public AdvancementTabType getType() {
      return this.type;
   }

   public int getIndex() {
      return this.index;
   }

   public Advancement getAdvancement() {
      return this.advancement;
   }

   public Component getTitle() {
      return this.title;
   }

   public DisplayInfo getDisplay() {
      return this.display;
   }

   public void drawTab(GuiGraphics p_282671_, int p_282721_, int p_282964_, boolean p_283052_) {
      this.type.draw(p_282671_, p_282721_, p_282964_, p_283052_, this.index);
   }

   public void drawIcon(GuiGraphics p_282895_, int p_283419_, int p_283293_) {
      this.type.drawIcon(p_282895_, p_283419_, p_283293_, this.index, this.icon);
   }

   public void drawContents(GuiGraphics p_282728_, int p_282962_, int p_281511_) {
      if (!this.centered) {
         this.scrollX = (double)(117 - (this.maxX + this.minX) / 2);
         this.scrollY = (double)(56 - (this.maxY + this.minY) / 2);
         this.centered = true;
      }

      p_282728_.enableScissor(p_282962_, p_281511_, p_282962_ + 234, p_281511_ + 113);
      p_282728_.pose().pushPose();
      p_282728_.pose().translate((float)p_282962_, (float)p_281511_, 0.0F);
      ResourceLocation resourcelocation = Objects.requireNonNullElse(this.display.getBackground(), TextureManager.INTENTIONAL_MISSING_TEXTURE);
      int i = Mth.floor(this.scrollX);
      int j = Mth.floor(this.scrollY);
      int k = i % 16;
      int l = j % 16;

      for(int i1 = -1; i1 <= 15; ++i1) {
         for(int j1 = -1; j1 <= 8; ++j1) {
            p_282728_.blit(resourcelocation, k + 16 * i1, l + 16 * j1, 0.0F, 0.0F, 16, 16, 16, 16);
         }
      }

      this.root.drawConnectivity(p_282728_, i, j, true);
      this.root.drawConnectivity(p_282728_, i, j, false);
      this.root.draw(p_282728_, i, j);
      p_282728_.pose().popPose();
      p_282728_.disableScissor();
   }

   public void drawTooltips(GuiGraphics p_282892_, int p_283658_, int p_282602_, int p_282652_, int p_283595_) {
      p_282892_.pose().pushPose();
      p_282892_.pose().translate(0.0F, 0.0F, -200.0F);
      p_282892_.fill(0, 0, 234, 113, Mth.floor(this.fade * 255.0F) << 24);
      boolean flag = false;
      int i = Mth.floor(this.scrollX);
      int j = Mth.floor(this.scrollY);
      if (p_283658_ > 0 && p_283658_ < 234 && p_282602_ > 0 && p_282602_ < 113) {
         for(AdvancementWidget advancementwidget : this.widgets.values()) {
            if (advancementwidget.isMouseOver(i, j, p_283658_, p_282602_)) {
               flag = true;
               advancementwidget.drawHover(p_282892_, i, j, this.fade, p_282652_, p_283595_);
               break;
            }
         }
      }

      p_282892_.pose().popPose();
      if (flag) {
         this.fade = Mth.clamp(this.fade + 0.02F, 0.0F, 0.3F);
      } else {
         this.fade = Mth.clamp(this.fade - 0.04F, 0.0F, 1.0F);
      }

   }

   public boolean isMouseOver(int p_97155_, int p_97156_, double p_97157_, double p_97158_) {
      return this.type.isMouseOver(p_97155_, p_97156_, this.index, p_97157_, p_97158_);
   }

   @Nullable
   public static AdvancementTab create(Minecraft p_97171_, AdvancementsScreen p_97172_, int p_97173_, Advancement p_97174_) {
      if (p_97174_.getDisplay() == null) {
         return null;
      } else {
         for(AdvancementTabType advancementtabtype : AdvancementTabType.values()) {
            if ((p_97173_ % AdvancementTabType.MAX_TABS) < advancementtabtype.getMax()) {
               return new AdvancementTab(p_97171_, p_97172_, advancementtabtype, p_97173_ % AdvancementTabType.MAX_TABS, p_97173_ / AdvancementTabType.MAX_TABS, p_97174_, p_97174_.getDisplay());
            }

            p_97173_ -= advancementtabtype.getMax();
         }

         return null;
      }
   }

   public void scroll(double p_97152_, double p_97153_) {
      if (this.maxX - this.minX > 234) {
         this.scrollX = Mth.clamp(this.scrollX + p_97152_, (double)(-(this.maxX - 234)), 0.0D);
      }

      if (this.maxY - this.minY > 113) {
         this.scrollY = Mth.clamp(this.scrollY + p_97153_, (double)(-(this.maxY - 113)), 0.0D);
      }

   }

   public void addAdvancement(Advancement p_97179_) {
      if (p_97179_.getDisplay() != null) {
         AdvancementWidget advancementwidget = new AdvancementWidget(this, this.minecraft, p_97179_, p_97179_.getDisplay());
         this.addWidget(advancementwidget, p_97179_);
      }
   }

   private void addWidget(AdvancementWidget p_97176_, Advancement p_97177_) {
      this.widgets.put(p_97177_, p_97176_);
      int i = p_97176_.getX();
      int j = i + 28;
      int k = p_97176_.getY();
      int l = k + 27;
      this.minX = Math.min(this.minX, i);
      this.maxX = Math.max(this.maxX, j);
      this.minY = Math.min(this.minY, k);
      this.maxY = Math.max(this.maxY, l);

      for(AdvancementWidget advancementwidget : this.widgets.values()) {
         advancementwidget.attachToParent();
      }

   }

   @Nullable
   public AdvancementWidget getWidget(Advancement p_97181_) {
      return this.widgets.get(p_97181_);
   }

   public AdvancementsScreen getScreen() {
      return this.screen;
   }
}
