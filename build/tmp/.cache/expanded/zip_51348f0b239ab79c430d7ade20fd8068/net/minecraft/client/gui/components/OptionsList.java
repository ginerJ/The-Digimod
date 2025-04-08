package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionsList extends ContainerObjectSelectionList<OptionsList.Entry> {
   public OptionsList(Minecraft p_94465_, int p_94466_, int p_94467_, int p_94468_, int p_94469_, int p_94470_) {
      super(p_94465_, p_94466_, p_94467_, p_94468_, p_94469_, p_94470_);
      this.centerListVertically = false;
   }

   public int addBig(OptionInstance<?> p_232529_) {
      return this.addEntry(OptionsList.Entry.big(this.minecraft.options, this.width, p_232529_));
   }

   public void addSmall(OptionInstance<?> p_232531_, @Nullable OptionInstance<?> p_232532_) {
      this.addEntry(OptionsList.Entry.small(this.minecraft.options, this.width, p_232531_, p_232532_));
   }

   public void addSmall(OptionInstance<?>[] p_232534_) {
      for(int i = 0; i < p_232534_.length; i += 2) {
         this.addSmall(p_232534_[i], i < p_232534_.length - 1 ? p_232534_[i + 1] : null);
      }

   }

   public int getRowWidth() {
      return 400;
   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 32;
   }

   @Nullable
   public AbstractWidget findOption(OptionInstance<?> p_232536_) {
      for(OptionsList.Entry optionslist$entry : this.children()) {
         AbstractWidget abstractwidget = optionslist$entry.options.get(p_232536_);
         if (abstractwidget != null) {
            return abstractwidget;
         }
      }

      return null;
   }

   public Optional<AbstractWidget> getMouseOver(double p_94481_, double p_94482_) {
      for(OptionsList.Entry optionslist$entry : this.children()) {
         for(AbstractWidget abstractwidget : optionslist$entry.children) {
            if (abstractwidget.isMouseOver(p_94481_, p_94482_)) {
               return Optional.of(abstractwidget);
            }
         }
      }

      return Optional.empty();
   }

   @OnlyIn(Dist.CLIENT)
   protected static class Entry extends ContainerObjectSelectionList.Entry<OptionsList.Entry> {
      final Map<OptionInstance<?>, AbstractWidget> options;
      final List<AbstractWidget> children;

      private Entry(Map<OptionInstance<?>, AbstractWidget> p_169047_) {
         this.options = p_169047_;
         this.children = ImmutableList.copyOf(p_169047_.values());
      }

      public static OptionsList.Entry big(Options p_232538_, int p_232539_, OptionInstance<?> p_232540_) {
         return new OptionsList.Entry(ImmutableMap.of(p_232540_, p_232540_.createButton(p_232538_, p_232539_ / 2 - 155, 0, 310)));
      }

      public static OptionsList.Entry small(Options p_232542_, int p_232543_, OptionInstance<?> p_232544_, @Nullable OptionInstance<?> p_232545_) {
         AbstractWidget abstractwidget = p_232544_.createButton(p_232542_, p_232543_ / 2 - 155, 0, 150);
         return p_232545_ == null ? new OptionsList.Entry(ImmutableMap.of(p_232544_, abstractwidget)) : new OptionsList.Entry(ImmutableMap.of(p_232544_, abstractwidget, p_232545_, p_232545_.createButton(p_232542_, p_232543_ / 2 - 155 + 160, 0, 150)));
      }

      public void render(GuiGraphics p_281311_, int p_94497_, int p_94498_, int p_94499_, int p_94500_, int p_94501_, int p_94502_, int p_94503_, boolean p_94504_, float p_94505_) {
         this.children.forEach((p_280776_) -> {
            p_280776_.setY(p_94498_);
            p_280776_.render(p_281311_, p_94502_, p_94503_, p_94505_);
         });
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }
   }
}