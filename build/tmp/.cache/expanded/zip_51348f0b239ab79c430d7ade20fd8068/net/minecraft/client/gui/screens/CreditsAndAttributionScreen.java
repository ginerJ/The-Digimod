package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreditsAndAttributionScreen extends Screen {
   private static final int BUTTON_SPACING = 8;
   private static final int BUTTON_WIDTH = 210;
   private static final Component TITLE = Component.translatable("credits_and_attribution.screen.title");
   private static final Component CREDITS_BUTTON = Component.translatable("credits_and_attribution.button.credits");
   private static final Component ATTRIBUTION_BUTTON = Component.translatable("credits_and_attribution.button.attribution");
   private static final Component LICENSES_BUTTON = Component.translatable("credits_and_attribution.button.licenses");
   private final Screen lastScreen;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

   public CreditsAndAttributionScreen(Screen p_276298_) {
      super(TITLE);
      this.lastScreen = p_276298_;
   }

   protected void init() {
      this.layout.addToHeader(new StringWidget(this.getTitle(), this.font));
      GridLayout gridlayout = this.layout.addToContents(new GridLayout()).spacing(8);
      gridlayout.defaultCellSetting().alignHorizontallyCenter();
      GridLayout.RowHelper gridlayout$rowhelper = gridlayout.createRowHelper(1);
      gridlayout$rowhelper.addChild(Button.builder(CREDITS_BUTTON, (p_276287_) -> {
         this.openCreditsScreen();
      }).width(210).build());
      gridlayout$rowhelper.addChild(Button.builder(ATTRIBUTION_BUTTON, ConfirmLinkScreen.confirmLink("https://aka.ms/MinecraftJavaAttribution", this, true)).width(210).build());
      gridlayout$rowhelper.addChild(Button.builder(LICENSES_BUTTON, ConfirmLinkScreen.confirmLink("https://aka.ms/MinecraftJavaLicenses", this, true)).width(210).build());
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (p_276311_) -> {
         this.onClose();
      }).build());
      this.layout.arrangeElements();
      this.layout.visitWidgets(this::addRenderableWidget);
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   private void openCreditsScreen() {
      this.minecraft.setScreen(new WinScreen(false, () -> {
         this.minecraft.setScreen(this);
      }));
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public void render(GuiGraphics p_282447_, int p_282980_, int p_281933_, float p_282766_) {
      this.renderBackground(p_282447_);
      super.render(p_282447_, p_282980_, p_281933_, p_282766_);
   }
}