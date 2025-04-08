package com.mojang.realmsclient.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsLongConfirmationScreen extends RealmsScreen {
   static final Component WARNING = Component.translatable("mco.warning");
   static final Component INFO = Component.translatable("mco.info");
   private final RealmsLongConfirmationScreen.Type type;
   private final Component line2;
   private final Component line3;
   protected final BooleanConsumer callback;
   private final boolean yesNoQuestion;

   public RealmsLongConfirmationScreen(BooleanConsumer p_88731_, RealmsLongConfirmationScreen.Type p_88732_, Component p_88733_, Component p_88734_, boolean p_88735_) {
      super(GameNarrator.NO_TITLE);
      this.callback = p_88731_;
      this.type = p_88732_;
      this.line2 = p_88733_;
      this.line3 = p_88734_;
      this.yesNoQuestion = p_88735_;
   }

   public void init() {
      if (this.yesNoQuestion) {
         this.addRenderableWidget(Button.builder(CommonComponents.GUI_YES, (p_88751_) -> {
            this.callback.accept(true);
         }).bounds(this.width / 2 - 105, row(8), 100, 20).build());
         this.addRenderableWidget(Button.builder(CommonComponents.GUI_NO, (p_88749_) -> {
            this.callback.accept(false);
         }).bounds(this.width / 2 + 5, row(8), 100, 20).build());
      } else {
         this.addRenderableWidget(Button.builder(CommonComponents.GUI_OK, (p_88746_) -> {
            this.callback.accept(true);
         }).bounds(this.width / 2 - 50, row(8), 100, 20).build());
      }

   }

   public Component getNarrationMessage() {
      return CommonComponents.joinLines(this.type.text, this.line2, this.line3);
   }

   public boolean keyPressed(int p_88737_, int p_88738_, int p_88739_) {
      if (p_88737_ == 256) {
         this.callback.accept(false);
         return true;
      } else {
         return super.keyPressed(p_88737_, p_88738_, p_88739_);
      }
   }

   public void render(GuiGraphics p_282797_, int p_88742_, int p_88743_, float p_88744_) {
      this.renderBackground(p_282797_);
      p_282797_.drawCenteredString(this.font, this.type.text, this.width / 2, row(2), this.type.colorCode);
      p_282797_.drawCenteredString(this.font, this.line2, this.width / 2, row(4), 16777215);
      p_282797_.drawCenteredString(this.font, this.line3, this.width / 2, row(6), 16777215);
      super.render(p_282797_, p_88742_, p_88743_, p_88744_);
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      WARNING(RealmsLongConfirmationScreen.WARNING, 16711680),
      INFO(RealmsLongConfirmationScreen.INFO, 8226750);

      public final int colorCode;
      public final Component text;

      private Type(Component p_287726_, int p_287605_) {
         this.text = p_287726_;
         this.colorCode = p_287605_;
      }
   }
}