package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancementWidget {
   private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");
   private static final int HEIGHT = 26;
   private static final int BOX_X = 0;
   private static final int BOX_WIDTH = 200;
   private static final int FRAME_WIDTH = 26;
   private static final int ICON_X = 8;
   private static final int ICON_Y = 5;
   private static final int ICON_WIDTH = 26;
   private static final int TITLE_PADDING_LEFT = 3;
   private static final int TITLE_PADDING_RIGHT = 5;
   private static final int TITLE_X = 32;
   private static final int TITLE_Y = 9;
   private static final int TITLE_MAX_WIDTH = 163;
   private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
   private final AdvancementTab tab;
   private final Advancement advancement;
   private final DisplayInfo display;
   private final FormattedCharSequence title;
   private final int width;
   private final List<FormattedCharSequence> description;
   private final Minecraft minecraft;
   @Nullable
   private AdvancementWidget parent;
   private final List<AdvancementWidget> children = Lists.newArrayList();
   @Nullable
   private AdvancementProgress progress;
   private final int x;
   private final int y;

   public AdvancementWidget(AdvancementTab p_97255_, Minecraft p_97256_, Advancement p_97257_, DisplayInfo p_97258_) {
      this.tab = p_97255_;
      this.advancement = p_97257_;
      this.display = p_97258_;
      this.minecraft = p_97256_;
      this.title = Language.getInstance().getVisualOrder(p_97256_.font.substrByWidth(p_97258_.getTitle(), 163));
      this.x = Mth.floor(p_97258_.getX() * 28.0F);
      this.y = Mth.floor(p_97258_.getY() * 27.0F);
      int i = p_97257_.getMaxCriteraRequired();
      int j = String.valueOf(i).length();
      int k = i > 1 ? p_97256_.font.width("  ") + p_97256_.font.width("0") * j * 2 + p_97256_.font.width("/") : 0;
      int l = 29 + p_97256_.font.width(this.title) + k;
      this.description = Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles(p_97258_.getDescription().copy(), Style.EMPTY.withColor(p_97258_.getFrame().getChatColor())), l));

      for(FormattedCharSequence formattedcharsequence : this.description) {
         l = Math.max(l, p_97256_.font.width(formattedcharsequence));
      }

      this.width = l + 3 + 5;
   }

   private static float getMaxWidth(StringSplitter p_97304_, List<FormattedText> p_97305_) {
      return (float)p_97305_.stream().mapToDouble(p_97304_::stringWidth).max().orElse(0.0D);
   }

   private List<FormattedText> findOptimalLines(Component p_97309_, int p_97310_) {
      StringSplitter stringsplitter = this.minecraft.font.getSplitter();
      List<FormattedText> list = null;
      float f = Float.MAX_VALUE;

      for(int i : TEST_SPLIT_OFFSETS) {
         List<FormattedText> list1 = stringsplitter.splitLines(p_97309_, p_97310_ - i, Style.EMPTY);
         float f1 = Math.abs(getMaxWidth(stringsplitter, list1) - (float)p_97310_);
         if (f1 <= 10.0F) {
            return list1;
         }

         if (f1 < f) {
            f = f1;
            list = list1;
         }
      }

      return list;
   }

   @Nullable
   private AdvancementWidget getFirstVisibleParent(Advancement p_97312_) {
      do {
         p_97312_ = p_97312_.getParent();
      } while(p_97312_ != null && p_97312_.getDisplay() == null);

      return p_97312_ != null && p_97312_.getDisplay() != null ? this.tab.getWidget(p_97312_) : null;
   }

   public void drawConnectivity(GuiGraphics p_281947_, int p_97300_, int p_97301_, boolean p_97302_) {
      if (this.parent != null) {
         int i = p_97300_ + this.parent.x + 13;
         int j = p_97300_ + this.parent.x + 26 + 4;
         int k = p_97301_ + this.parent.y + 13;
         int l = p_97300_ + this.x + 13;
         int i1 = p_97301_ + this.y + 13;
         int j1 = p_97302_ ? -16777216 : -1;
         if (p_97302_) {
            p_281947_.hLine(j, i, k - 1, j1);
            p_281947_.hLine(j + 1, i, k, j1);
            p_281947_.hLine(j, i, k + 1, j1);
            p_281947_.hLine(l, j - 1, i1 - 1, j1);
            p_281947_.hLine(l, j - 1, i1, j1);
            p_281947_.hLine(l, j - 1, i1 + 1, j1);
            p_281947_.vLine(j - 1, i1, k, j1);
            p_281947_.vLine(j + 1, i1, k, j1);
         } else {
            p_281947_.hLine(j, i, k, j1);
            p_281947_.hLine(l, j, i1, j1);
            p_281947_.vLine(j, i1, k, j1);
         }
      }

      for(AdvancementWidget advancementwidget : this.children) {
         advancementwidget.drawConnectivity(p_281947_, p_97300_, p_97301_, p_97302_);
      }

   }

   public void draw(GuiGraphics p_281958_, int p_281323_, int p_283679_) {
      if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
         float f = this.progress == null ? 0.0F : this.progress.getPercent();
         AdvancementWidgetType advancementwidgettype;
         if (f >= 1.0F) {
            advancementwidgettype = AdvancementWidgetType.OBTAINED;
         } else {
            advancementwidgettype = AdvancementWidgetType.UNOBTAINED;
         }

         p_281958_.blit(WIDGETS_LOCATION, p_281323_ + this.x + 3, p_283679_ + this.y, this.display.getFrame().getTexture(), 128 + advancementwidgettype.getIndex() * 26, 26, 26);
         p_281958_.renderFakeItem(this.display.getIcon(), p_281323_ + this.x + 8, p_283679_ + this.y + 5);
      }

      for(AdvancementWidget advancementwidget : this.children) {
         advancementwidget.draw(p_281958_, p_281323_, p_283679_);
      }

   }

   public int getWidth() {
      return this.width;
   }

   public void setProgress(AdvancementProgress p_97265_) {
      this.progress = p_97265_;
   }

   public void addChild(AdvancementWidget p_97307_) {
      this.children.add(p_97307_);
   }

   public void drawHover(GuiGraphics p_283068_, int p_281304_, int p_281253_, float p_281848_, int p_282097_, int p_281537_) {
      boolean flag = p_282097_ + p_281304_ + this.x + this.width + 26 >= this.tab.getScreen().width;
      String s = this.progress == null ? null : this.progress.getProgressText();
      int i = s == null ? 0 : this.minecraft.font.width(s);
      boolean flag1 = 113 - p_281253_ - this.y - 26 <= 6 + this.description.size() * 9;
      float f = this.progress == null ? 0.0F : this.progress.getPercent();
      int j = Mth.floor(f * (float)this.width);
      AdvancementWidgetType advancementwidgettype;
      AdvancementWidgetType advancementwidgettype1;
      AdvancementWidgetType advancementwidgettype2;
      if (f >= 1.0F) {
         j = this.width / 2;
         advancementwidgettype = AdvancementWidgetType.OBTAINED;
         advancementwidgettype1 = AdvancementWidgetType.OBTAINED;
         advancementwidgettype2 = AdvancementWidgetType.OBTAINED;
      } else if (j < 2) {
         j = this.width / 2;
         advancementwidgettype = AdvancementWidgetType.UNOBTAINED;
         advancementwidgettype1 = AdvancementWidgetType.UNOBTAINED;
         advancementwidgettype2 = AdvancementWidgetType.UNOBTAINED;
      } else if (j > this.width - 2) {
         j = this.width / 2;
         advancementwidgettype = AdvancementWidgetType.OBTAINED;
         advancementwidgettype1 = AdvancementWidgetType.OBTAINED;
         advancementwidgettype2 = AdvancementWidgetType.UNOBTAINED;
      } else {
         advancementwidgettype = AdvancementWidgetType.OBTAINED;
         advancementwidgettype1 = AdvancementWidgetType.UNOBTAINED;
         advancementwidgettype2 = AdvancementWidgetType.UNOBTAINED;
      }

      int k = this.width - j;
      RenderSystem.enableBlend();
      int l = p_281253_ + this.y;
      int i1;
      if (flag) {
         i1 = p_281304_ + this.x - this.width + 26 + 6;
      } else {
         i1 = p_281304_ + this.x;
      }

      int j1 = 32 + this.description.size() * 9;
      if (!this.description.isEmpty()) {
         if (flag1) {
            p_283068_.blitNineSliced(WIDGETS_LOCATION, i1, l + 26 - j1, this.width, j1, 10, 200, 26, 0, 52);
         } else {
            p_283068_.blitNineSliced(WIDGETS_LOCATION, i1, l, this.width, j1, 10, 200, 26, 0, 52);
         }
      }

      p_283068_.blit(WIDGETS_LOCATION, i1, l, 0, advancementwidgettype.getIndex() * 26, j, 26);
      p_283068_.blit(WIDGETS_LOCATION, i1 + j, l, 200 - k, advancementwidgettype1.getIndex() * 26, k, 26);
      p_283068_.blit(WIDGETS_LOCATION, p_281304_ + this.x + 3, p_281253_ + this.y, this.display.getFrame().getTexture(), 128 + advancementwidgettype2.getIndex() * 26, 26, 26);
      if (flag) {
         p_283068_.drawString(this.minecraft.font, this.title, i1 + 5, p_281253_ + this.y + 9, -1);
         if (s != null) {
            p_283068_.drawString(this.minecraft.font, s, p_281304_ + this.x - i, p_281253_ + this.y + 9, -1);
         }
      } else {
         p_283068_.drawString(this.minecraft.font, this.title, p_281304_ + this.x + 32, p_281253_ + this.y + 9, -1);
         if (s != null) {
            p_283068_.drawString(this.minecraft.font, s, p_281304_ + this.x + this.width - i - 5, p_281253_ + this.y + 9, -1);
         }
      }

      if (flag1) {
         for(int k1 = 0; k1 < this.description.size(); ++k1) {
            p_283068_.drawString(this.minecraft.font, this.description.get(k1), i1 + 5, l + 26 - j1 + 7 + k1 * 9, -5592406, false);
         }
      } else {
         for(int l1 = 0; l1 < this.description.size(); ++l1) {
            p_283068_.drawString(this.minecraft.font, this.description.get(l1), i1 + 5, p_281253_ + this.y + 9 + 17 + l1 * 9, -5592406, false);
         }
      }

      p_283068_.renderFakeItem(this.display.getIcon(), p_281304_ + this.x + 8, p_281253_ + this.y + 5);
   }

   public boolean isMouseOver(int p_97260_, int p_97261_, int p_97262_, int p_97263_) {
      if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
         int i = p_97260_ + this.x;
         int j = i + 26;
         int k = p_97261_ + this.y;
         int l = k + 26;
         return p_97262_ >= i && p_97262_ <= j && p_97263_ >= k && p_97263_ <= l;
      } else {
         return false;
      }
   }

   public void attachToParent() {
      if (this.parent == null && this.advancement.getParent() != null) {
         this.parent = this.getFirstVisibleParent(this.advancement);
         if (this.parent != null) {
            this.parent.addChild(this);
         }
      }

   }

   public int getY() {
      return this.y;
   }

   public int getX() {
      return this.x;
   }
}