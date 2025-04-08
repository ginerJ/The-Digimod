package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisconnectedScreen extends Screen {
   private static final Component TO_SERVER_LIST = Component.translatable("gui.toMenu");
   private static final Component TO_TITLE = Component.translatable("gui.toTitle");
   private final Screen parent;
   private final Component reason;
   private final Component buttonText;
   private final GridLayout layout = new GridLayout();

   public DisconnectedScreen(Screen p_95993_, Component p_95994_, Component p_95995_) {
      this(p_95993_, p_95994_, p_95995_, TO_SERVER_LIST);
   }

   public DisconnectedScreen(Screen p_279153_, Component p_279183_, Component p_279332_, Component p_279257_) {
      super(p_279183_);
      this.parent = p_279153_;
      this.reason = p_279332_;
      this.buttonText = p_279257_;
   }

   protected void init() {
      this.layout.defaultCellSetting().alignHorizontallyCenter().padding(10);
      GridLayout.RowHelper gridlayout$rowhelper = this.layout.createRowHelper(1);
      gridlayout$rowhelper.addChild(new StringWidget(this.title, this.font));
      gridlayout$rowhelper.addChild((new MultiLineTextWidget(this.reason, this.font)).setMaxWidth(this.width - 50).setCentered(true));
      Button button;
      if (this.minecraft.allowsMultiplayer()) {
         button = Button.builder(this.buttonText, (p_280799_) -> {
            this.minecraft.setScreen(this.parent);
         }).build();
      } else {
         button = Button.builder(TO_TITLE, (p_280800_) -> {
            this.minecraft.setScreen(new TitleScreen());
         }).build();
      }

      gridlayout$rowhelper.addChild(button);
      this.layout.arrangeElements();
      this.layout.visitWidgets(this::addRenderableWidget);
      this.repositionElements();
   }

   protected void repositionElements() {
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.title, this.reason);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void render(GuiGraphics p_281787_, int p_282952_, int p_283158_, float p_282166_) {
      this.renderBackground(p_281787_);
      super.render(p_281787_, p_282952_, p_283158_, p_282166_);
   }
}