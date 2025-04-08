package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfirmLinkScreen extends ConfirmScreen {
   private static final Component COPY_BUTTON_TEXT = Component.translatable("chat.copy");
   private static final Component WARNING_TEXT = Component.translatable("chat.link.warning");
   private final String url;
   private final boolean showWarning;

   public ConfirmLinkScreen(BooleanConsumer p_95631_, String p_95632_, boolean p_95633_) {
      this(p_95631_, confirmMessage(p_95633_), Component.literal(p_95632_), p_95632_, p_95633_ ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_NO, p_95633_);
   }

   public ConfirmLinkScreen(BooleanConsumer p_238329_, Component p_238330_, String p_238331_, boolean p_238332_) {
      this(p_238329_, p_238330_, p_238331_, p_238332_ ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_NO, p_238332_);
   }

   public ConfirmLinkScreen(BooleanConsumer p_239991_, Component p_239992_, String p_239993_, Component p_239994_, boolean p_239995_) {
      this(p_239991_, p_239992_, confirmMessage(p_239995_, p_239993_), p_239993_, p_239994_, p_239995_);
   }

   public ConfirmLinkScreen(BooleanConsumer p_240191_, Component p_240192_, Component p_240193_, String p_240194_, Component p_240195_, boolean p_240196_) {
      super(p_240191_, p_240192_, p_240193_);
      this.yesButton = (Component)(p_240196_ ? Component.translatable("chat.link.open") : CommonComponents.GUI_YES);
      this.noButton = p_240195_;
      this.showWarning = !p_240196_;
      this.url = p_240194_;
   }

   protected static MutableComponent confirmMessage(boolean p_239180_, String p_239181_) {
      return confirmMessage(p_239180_).append(CommonComponents.SPACE).append(Component.literal(p_239181_));
   }

   protected static MutableComponent confirmMessage(boolean p_240014_) {
      return Component.translatable(p_240014_ ? "chat.link.confirmTrusted" : "chat.link.confirm");
   }

   protected void addButtons(int p_169243_) {
      this.addRenderableWidget(Button.builder(this.yesButton, (p_169249_) -> {
         this.callback.accept(true);
      }).bounds(this.width / 2 - 50 - 105, p_169243_, 100, 20).build());
      this.addRenderableWidget(Button.builder(COPY_BUTTON_TEXT, (p_169247_) -> {
         this.copyToClipboard();
         this.callback.accept(false);
      }).bounds(this.width / 2 - 50, p_169243_, 100, 20).build());
      this.addRenderableWidget(Button.builder(this.noButton, (p_169245_) -> {
         this.callback.accept(false);
      }).bounds(this.width / 2 - 50 + 105, p_169243_, 100, 20).build());
   }

   public void copyToClipboard() {
      this.minecraft.keyboardHandler.setClipboard(this.url);
   }

   public void render(GuiGraphics p_281548_, int p_281671_, int p_283205_, float p_283628_) {
      super.render(p_281548_, p_281671_, p_283205_, p_283628_);
      if (this.showWarning) {
         p_281548_.drawCenteredString(this.font, WARNING_TEXT, this.width / 2, 110, 16764108);
      }

   }

   public static void confirmLinkNow(String p_275417_, Screen p_275593_, boolean p_275446_) {
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.setScreen(new ConfirmLinkScreen((p_274671_) -> {
         if (p_274671_) {
            Util.getPlatform().openUri(p_275417_);
         }

         minecraft.setScreen(p_275593_);
      }, p_275417_, p_275446_));
   }

   public static Button.OnPress confirmLink(String p_275241_, Screen p_275326_, boolean p_275642_) {
      return (p_274667_) -> {
         confirmLinkNow(p_275241_, p_275326_, p_275642_);
      };
   }
}