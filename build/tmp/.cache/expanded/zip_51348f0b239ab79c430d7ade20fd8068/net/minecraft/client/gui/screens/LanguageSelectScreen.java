package net.minecraft.client.gui.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LanguageSelectScreen extends OptionsSubScreen {
   private static final Component WARNING_LABEL = Component.literal("(").append(Component.translatable("options.languageWarning")).append(")").withStyle(ChatFormatting.GRAY);
   private LanguageSelectScreen.LanguageSelectionList packSelectionList;
   final LanguageManager languageManager;

   public LanguageSelectScreen(Screen p_96085_, Options p_96086_, LanguageManager p_96087_) {
      super(p_96085_, p_96086_, Component.translatable("options.language"));
      this.languageManager = p_96087_;
   }

   protected void init() {
      this.packSelectionList = new LanguageSelectScreen.LanguageSelectionList(this.minecraft);
      this.addWidget(this.packSelectionList);
      this.addRenderableWidget(this.options.forceUnicodeFont().createButton(this.options, this.width / 2 - 155, this.height - 38, 150));
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_288243_) -> {
         this.onDone();
      }).bounds(this.width / 2 - 155 + 160, this.height - 38, 150, 20).build());
      super.init();
   }

   void onDone() {
      LanguageSelectScreen.LanguageSelectionList.Entry languageselectscreen$languageselectionlist$entry = this.packSelectionList.getSelected();
      if (languageselectscreen$languageselectionlist$entry != null && !languageselectscreen$languageselectionlist$entry.code.equals(this.languageManager.getSelected())) {
         this.languageManager.setSelected(languageselectscreen$languageselectionlist$entry.code);
         this.options.languageCode = languageselectscreen$languageselectionlist$entry.code;
         this.minecraft.reloadResourcePacks();
         this.options.save();
      }

      this.minecraft.setScreen(this.lastScreen);
   }

   public boolean keyPressed(int p_289001_, int p_288978_, int p_289021_) {
      if (CommonInputs.selected(p_289001_)) {
         LanguageSelectScreen.LanguageSelectionList.Entry languageselectscreen$languageselectionlist$entry = this.packSelectionList.getSelected();
         if (languageselectscreen$languageselectionlist$entry != null) {
            languageselectscreen$languageselectionlist$entry.select();
            this.onDone();
            return true;
         }
      }

      return super.keyPressed(p_289001_, p_288978_, p_289021_);
   }

   public void render(GuiGraphics p_283397_, int p_96090_, int p_96091_, float p_96092_) {
      this.packSelectionList.render(p_283397_, p_96090_, p_96091_, p_96092_);
      p_283397_.drawCenteredString(this.font, this.title, this.width / 2, 16, 16777215);
      p_283397_.drawCenteredString(this.font, WARNING_LABEL, this.width / 2, this.height - 56, 8421504);
      super.render(p_283397_, p_96090_, p_96091_, p_96092_);
   }

   @OnlyIn(Dist.CLIENT)
   class LanguageSelectionList extends ObjectSelectionList<LanguageSelectScreen.LanguageSelectionList.Entry> {
      public LanguageSelectionList(Minecraft p_96103_) {
         super(p_96103_, LanguageSelectScreen.this.width, LanguageSelectScreen.this.height, 32, LanguageSelectScreen.this.height - 65 + 4, 18);
         String s = LanguageSelectScreen.this.languageManager.getSelected();
         LanguageSelectScreen.this.languageManager.getLanguages().forEach((p_265492_, p_265377_) -> {
            LanguageSelectScreen.LanguageSelectionList.Entry languageselectscreen$languageselectionlist$entry = new LanguageSelectScreen.LanguageSelectionList.Entry(p_265492_, p_265377_);
            this.addEntry(languageselectscreen$languageselectionlist$entry);
            if (s.equals(p_265492_)) {
               this.setSelected(languageselectscreen$languageselectionlist$entry);
            }

         });
         if (this.getSelected() != null) {
            this.centerScrollOn(this.getSelected());
         }

      }

      protected int getScrollbarPosition() {
         return super.getScrollbarPosition() + 20;
      }

      public int getRowWidth() {
         return super.getRowWidth() + 50;
      }

      protected void renderBackground(GuiGraphics p_282399_) {
         LanguageSelectScreen.this.renderBackground(p_282399_);
      }

      @OnlyIn(Dist.CLIENT)
      public class Entry extends ObjectSelectionList.Entry<LanguageSelectScreen.LanguageSelectionList.Entry> {
         final String code;
         private final Component language;
         private long lastClickTime;

         public Entry(String p_265319_, LanguageInfo p_265357_) {
            this.code = p_265319_;
            this.language = p_265357_.toComponent();
         }

         public void render(GuiGraphics p_282025_, int p_283548_, int p_282485_, int p_282109_, int p_283314_, int p_283303_, int p_281337_, int p_283527_, boolean p_283295_, float p_282169_) {
            p_282025_.drawCenteredString(LanguageSelectScreen.this.font, this.language, LanguageSelectionList.this.width / 2, p_282485_ + 1, 16777215);
         }

         public boolean mouseClicked(double p_96122_, double p_96123_, int p_96124_) {
            if (p_96124_ == 0) {
               this.select();
               if (Util.getMillis() - this.lastClickTime < 250L) {
                  LanguageSelectScreen.this.onDone();
               }

               this.lastClickTime = Util.getMillis();
               return true;
            } else {
               this.lastClickTime = Util.getMillis();
               return false;
            }
         }

         void select() {
            LanguageSelectionList.this.setSelected(this);
         }

         public Component getNarration() {
            return Component.translatable("narrator.select", this.language);
         }
      }
   }
}