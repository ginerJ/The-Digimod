package net.minecraft.client.gui.screens.controls;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;

@OnlyIn(Dist.CLIENT)
public class KeyBindsList extends ContainerObjectSelectionList<KeyBindsList.Entry> {
   final KeyBindsScreen keyBindsScreen;
   int maxNameWidth;

   final static int NAME_SPLIT_LENGTH = 185;

   public KeyBindsList(KeyBindsScreen p_193861_, Minecraft p_193862_) {
      super(p_193862_, p_193861_.width + 45, p_193861_.height, 20, p_193861_.height - 32, 20);
      this.keyBindsScreen = p_193861_;
      KeyMapping[] akeymapping = ArrayUtils.clone((KeyMapping[])p_193862_.options.keyMappings);
      Arrays.sort((Object[])akeymapping);
      String s = null;

      for(KeyMapping keymapping : akeymapping) {
         String s1 = keymapping.getCategory();
         if (!s1.equals(s)) {
            s = s1;
            this.addEntry(new KeyBindsList.CategoryEntry(Component.translatable(s1)));
         }

         Component component = Component.translatable(keymapping.getName());
         int i = p_193862_.font.width(component);
         if (i > this.maxNameWidth) {
            // Neo: max width for the keybind descriptions to make all readable
            this.maxNameWidth = Math.min(i, NAME_SPLIT_LENGTH);
         }

         this.addEntry(new KeyBindsList.KeyEntry(keymapping, component));
      }

   }

   public void resetMappingAndUpdateButtons() {
      KeyMapping.resetMapping();
      this.refreshEntries();
   }

   public void refreshEntries() {
      this.children().forEach(KeyBindsList.Entry::refreshEntry);
   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 15 + 20;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 32;
   }

   @OnlyIn(Dist.CLIENT)
   public class CategoryEntry extends KeyBindsList.Entry {
      final Component name;
      private final int width;

      public CategoryEntry(Component p_193886_) {
         this.name = p_193886_;
         this.width = KeyBindsList.this.minecraft.font.width(this.name);
      }

      public void render(GuiGraphics p_281285_, int p_281396_, int p_283616_, int p_281333_, int p_282287_, int p_283549_, int p_283684_, int p_283258_, boolean p_281563_, float p_283186_) {
         p_281285_.drawString(KeyBindsList.this.minecraft.font, this.name, KeyBindsList.this.minecraft.screen.width / 2 - this.width / 2, p_283616_ + p_283549_ - 9 - 1, 16777215, false);
      }

      @Nullable
      public ComponentPath nextFocusPath(FocusNavigationEvent p_265391_) {
         return null;
      }

      public List<? extends GuiEventListener> children() {
         return Collections.emptyList();
      }

      public List<? extends NarratableEntry> narratables() {
         return ImmutableList.of(new NarratableEntry() {
            public NarratableEntry.NarrationPriority narrationPriority() {
               return NarratableEntry.NarrationPriority.HOVERED;
            }

            public void updateNarration(NarrationElementOutput p_193906_) {
               p_193906_.add(NarratedElementType.TITLE, CategoryEntry.this.name);
            }
         });
      }

      protected void refreshEntry() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry extends ContainerObjectSelectionList.Entry<KeyBindsList.Entry> {
      abstract void refreshEntry();
   }

   @OnlyIn(Dist.CLIENT)
   public class KeyEntry extends KeyBindsList.Entry {
      private final KeyMapping key;
      private final Component name;
      private final Button changeButton;
      private final Button resetButton;
      private boolean hasCollision = false;

      KeyEntry(KeyMapping p_193916_, Component p_193917_) {
         this.key = p_193916_;
         this.name = p_193917_;
         this.changeButton = Button.builder(p_193917_, (p_269618_) -> {
            KeyBindsList.this.keyBindsScreen.selectedKey = p_193916_;
            KeyBindsList.this.resetMappingAndUpdateButtons();
         }).bounds(0, 0, 75 + 20 /* Forge: Add space */, 20).createNarration((p_253311_) -> {
            return p_193916_.isUnbound() ? Component.translatable("narrator.controls.unbound", p_193917_) : Component.translatable("narrator.controls.bound", p_193917_, p_253311_.get());
         }).build();
         this.resetButton = Button.builder(Component.translatable("controls.reset"), (p_269616_) -> {
            this.key.setToDefault();
            KeyBindsList.this.minecraft.options.setKey(p_193916_, p_193916_.getDefaultKey());
            KeyBindsList.this.resetMappingAndUpdateButtons();
         }).bounds(0, 0, 50, 20).createNarration((p_253313_) -> {
            return Component.translatable("narrator.controls.reset", p_193917_);
         }).build();
         this.refreshEntry();
      }

      public void render(GuiGraphics p_281805_, int p_281298_, int p_282357_, int p_281373_, int p_283433_, int p_281932_, int p_282224_, int p_282053_, boolean p_282605_, float p_281432_) {
         int k = p_281373_ + 90 - KeyBindsList.this.maxNameWidth;
         // Neo: Trim strings that are too long, and show a tooltip if the mouse is over the trimmed string
         List<net.minecraft.network.chat.FormattedText> lines = KeyBindsList.this.minecraft.font.getSplitter().splitLines(this.name, NAME_SPLIT_LENGTH, net.minecraft.network.chat.Style.EMPTY);
         Component nameComponent = lines.size() > 1 ? Component.literal(lines.get(0).getString() + "...") : this.name;
         if(lines.size() > 1 && this.isMouseOver(p_282224_ + 95, p_282053_) && p_282224_ < p_281373_ - 90 + KeyBindsList.this.maxNameWidth) {
            KeyBindsList.this.keyBindsScreen.setTooltipForNextRenderPass(net.minecraft.locale.Language.getInstance().getVisualOrder(lines));
         }
         p_281805_.drawString(KeyBindsList.this.minecraft.font, nameComponent, k, p_282357_ + p_281932_ / 2 - 9 / 2, 16777215, false);
         this.resetButton.setX(p_281373_ + 190 + 20);
         this.resetButton.setY(p_282357_);
         this.resetButton.render(p_281805_, p_282224_, p_282053_, p_281432_);
         this.changeButton.setX(p_281373_ + 105);
         this.changeButton.setY(p_282357_);
         if (this.hasCollision) {
            int i = 3;
            int j = this.changeButton.getX() - 6;
            p_281805_.fill(j, p_282357_ + 2, j + 3, p_282357_ + p_281932_ + 2, ChatFormatting.RED.getColor() | -16777216);
         }

         this.changeButton.render(p_281805_, p_282224_, p_282053_, p_281432_);
      }

      public List<? extends GuiEventListener> children() {
         return ImmutableList.of(this.changeButton, this.resetButton);
      }

      public List<? extends NarratableEntry> narratables() {
         return ImmutableList.of(this.changeButton, this.resetButton);
      }

      protected void refreshEntry() {
         this.changeButton.setMessage(this.key.getTranslatedKeyMessage());
         this.resetButton.active = !this.key.isDefault();
         this.hasCollision = false;
         MutableComponent mutablecomponent = Component.empty();
         if (!this.key.isUnbound()) {
            for(KeyMapping keymapping : KeyBindsList.this.minecraft.options.keyMappings) {
               if ((keymapping != this.key && this.key.same(keymapping)) || keymapping.hasKeyModifierConflict(this.key)) { // FORGE: gracefully handle conflicts like SHIFT vs SHIFT+G
                  if (this.hasCollision) {
                     mutablecomponent.append(", ");
                  }

                  this.hasCollision = true;
                  mutablecomponent.append(Component.translatable(keymapping.getName()));
               }
            }
         }

         if (this.hasCollision) {
            this.changeButton.setMessage(Component.literal("[ ").append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.WHITE)).append(" ]").withStyle(ChatFormatting.RED));
            this.changeButton.setTooltip(Tooltip.create(Component.translatable("controls.keybinds.duplicateKeybinds", mutablecomponent)));
         } else {
            this.changeButton.setTooltip((Tooltip)null);
         }

         if (KeyBindsList.this.keyBindsScreen.selectedKey == this.key) {
            this.changeButton.setMessage(Component.literal("> ").append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE)).append(" <").withStyle(ChatFormatting.YELLOW));
         }

      }
   }
}
