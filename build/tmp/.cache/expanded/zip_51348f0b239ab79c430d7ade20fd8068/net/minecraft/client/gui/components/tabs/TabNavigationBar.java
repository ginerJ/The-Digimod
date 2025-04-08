package net.minecraft.client.gui.components.tabs;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class TabNavigationBar extends AbstractContainerEventHandler implements Renderable, GuiEventListener, NarratableEntry {
   private static final int NO_TAB = -1;
   private static final int MAX_WIDTH = 400;
   private static final int HEIGHT = 24;
   private static final int MARGIN = 14;
   private static final Component USAGE_NARRATION = Component.translatable("narration.tab_navigation.usage");
   private final GridLayout layout;
   private int width;
   private final TabManager tabManager;
   private final ImmutableList<Tab> tabs;
   private final ImmutableList<TabButton> tabButtons;

   TabNavigationBar(int p_275379_, TabManager p_275624_, Iterable<Tab> p_275279_) {
      this.width = p_275379_;
      this.tabManager = p_275624_;
      this.tabs = ImmutableList.copyOf(p_275279_);
      this.layout = new GridLayout(0, 0);
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      ImmutableList.Builder<TabButton> builder = ImmutableList.builder();
      int i = 0;

      for(Tab tab : p_275279_) {
         builder.add(this.layout.addChild(new TabButton(p_275624_, tab, 0, 24), 0, i++));
      }

      this.tabButtons = builder.build();
   }

   public static TabNavigationBar.Builder builder(TabManager p_268126_, int p_268070_) {
      return new TabNavigationBar.Builder(p_268126_, p_268070_);
   }

   public void setWidth(int p_268094_) {
      this.width = p_268094_;
   }

   public void setFocused(boolean p_275488_) {
      super.setFocused(p_275488_);
      if (this.getFocused() != null) {
         this.getFocused().setFocused(p_275488_);
      }

   }

   public void setFocused(@Nullable GuiEventListener p_275675_) {
      super.setFocused(p_275675_);
      if (p_275675_ instanceof TabButton tabbutton) {
         this.tabManager.setCurrentTab(tabbutton.tab(), true);
      }

   }

   public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent p_275418_) {
      if (!this.isFocused()) {
         TabButton tabbutton = this.currentTabButton();
         if (tabbutton != null) {
            return ComponentPath.path(this, ComponentPath.leaf(tabbutton));
         }
      }

      return p_275418_ instanceof FocusNavigationEvent.TabNavigation ? null : super.nextFocusPath(p_275418_);
   }

   public List<? extends GuiEventListener> children() {
      return this.tabButtons;
   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      return this.tabButtons.stream().map(AbstractWidget::narrationPriority).max(Comparator.naturalOrder()).orElse(NarratableEntry.NarrationPriority.NONE);
   }

   public void updateNarration(NarrationElementOutput p_275583_) {
      Optional<TabButton> optional = this.tabButtons.stream().filter(AbstractWidget::isHovered).findFirst().or(() -> {
         return Optional.ofNullable(this.currentTabButton());
      });
      optional.ifPresent((p_274663_) -> {
         this.narrateListElementPosition(p_275583_.nest(), p_274663_);
         p_274663_.updateNarration(p_275583_);
      });
      if (this.isFocused()) {
         p_275583_.add(NarratedElementType.USAGE, USAGE_NARRATION);
      }

   }

   protected void narrateListElementPosition(NarrationElementOutput p_275386_, TabButton p_275397_) {
      if (this.tabs.size() > 1) {
         int i = this.tabButtons.indexOf(p_275397_);
         if (i != -1) {
            p_275386_.add(NarratedElementType.POSITION, Component.translatable("narrator.position.tab", i + 1, this.tabs.size()));
         }
      }

   }

   public void render(GuiGraphics p_281720_, int p_282085_, int p_281687_, float p_283048_) {
      p_281720_.fill(0, 0, this.width, 24, -16777216);
      p_281720_.blit(CreateWorldScreen.HEADER_SEPERATOR, 0, this.layout.getY() + this.layout.getHeight() - 2, 0.0F, 0.0F, this.width, 2, 32, 2);

      for(TabButton tabbutton : this.tabButtons) {
         tabbutton.render(p_281720_, p_282085_, p_281687_, p_283048_);
      }

   }

   public ScreenRectangle getRectangle() {
      return this.layout.getRectangle();
   }

   public void arrangeElements() {
      int i = Math.min(400, this.width) - 28;
      int j = Mth.roundToward(i / this.tabs.size(), 2);

      for(TabButton tabbutton : this.tabButtons) {
         tabbutton.setWidth(j);
      }

      this.layout.arrangeElements();
      this.layout.setX(Mth.roundToward((this.width - i) / 2, 2));
      this.layout.setY(0);
   }

   public void selectTab(int p_276107_, boolean p_276125_) {
      if (this.isFocused()) {
         this.setFocused(this.tabButtons.get(p_276107_));
      } else {
         this.tabManager.setCurrentTab(this.tabs.get(p_276107_), p_276125_);
      }

   }

   public boolean keyPressed(int p_270495_) {
      if (Screen.hasControlDown()) {
         int i = this.getNextTabIndex(p_270495_);
         if (i != -1) {
            this.selectTab(Mth.clamp(i, 0, this.tabs.size() - 1), true);
            return true;
         }
      }

      return false;
   }

   private int getNextTabIndex(int p_270508_) {
      if (p_270508_ >= 49 && p_270508_ <= 57) {
         return p_270508_ - 49;
      } else {
         if (p_270508_ == 258) {
            int i = this.currentTabIndex();
            if (i != -1) {
               int j = Screen.hasShiftDown() ? i - 1 : i + 1;
               return Math.floorMod(j, this.tabs.size());
            }
         }

         return -1;
      }
   }

   private int currentTabIndex() {
      Tab tab = this.tabManager.getCurrentTab();
      int i = this.tabs.indexOf(tab);
      return i != -1 ? i : -1;
   }

   private @Nullable TabButton currentTabButton() {
      int i = this.currentTabIndex();
      return i != -1 ? this.tabButtons.get(i) : null;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final int width;
      private final TabManager tabManager;
      private final List<Tab> tabs = new ArrayList<>();

      Builder(TabManager p_268334_, int p_267986_) {
         this.tabManager = p_268334_;
         this.width = p_267986_;
      }

      public TabNavigationBar.Builder addTabs(Tab... p_268144_) {
         Collections.addAll(this.tabs, p_268144_);
         return this;
      }

      public TabNavigationBar build() {
         return new TabNavigationBar(this.width, this.tabManager, this.tabs);
      }
   }
}