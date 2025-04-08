package net.minecraft.client.gui.components.tabs;

import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TabManager {
   private final Consumer<AbstractWidget> addWidget;
   private final Consumer<AbstractWidget> removeWidget;
   @Nullable
   private Tab currentTab;
   @Nullable
   private ScreenRectangle tabArea;

   public TabManager(Consumer<AbstractWidget> p_268279_, Consumer<AbstractWidget> p_268196_) {
      this.addWidget = p_268279_;
      this.removeWidget = p_268196_;
   }

   public void setTabArea(ScreenRectangle p_268042_) {
      this.tabArea = p_268042_;
      Tab tab = this.getCurrentTab();
      if (tab != null) {
         tab.doLayout(p_268042_);
      }

   }

   public void setCurrentTab(Tab p_276109_, boolean p_276120_) {
      if (!Objects.equals(this.currentTab, p_276109_)) {
         if (this.currentTab != null) {
            this.currentTab.visitChildren(this.removeWidget);
         }

         this.currentTab = p_276109_;
         p_276109_.visitChildren(this.addWidget);
         if (this.tabArea != null) {
            p_276109_.doLayout(this.tabArea);
         }

         if (p_276120_) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }
      }

   }

   @Nullable
   public Tab getCurrentTab() {
      return this.currentTab;
   }

   public void tickCurrent() {
      Tab tab = this.getCurrentTab();
      if (tab != null) {
         tab.tick();
      }

   }
}