package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsError;
import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsGenericErrorScreen extends RealmsScreen {
   private final Screen nextScreen;
   private final RealmsGenericErrorScreen.ErrorMessage lines;
   private MultiLineLabel line2Split = MultiLineLabel.EMPTY;

   public RealmsGenericErrorScreen(RealmsServiceException p_88669_, Screen p_88670_) {
      super(GameNarrator.NO_TITLE);
      this.nextScreen = p_88670_;
      this.lines = errorMessage(p_88669_);
   }

   public RealmsGenericErrorScreen(Component p_88672_, Screen p_88673_) {
      super(GameNarrator.NO_TITLE);
      this.nextScreen = p_88673_;
      this.lines = errorMessage(p_88672_);
   }

   public RealmsGenericErrorScreen(Component p_88675_, Component p_88676_, Screen p_88677_) {
      super(GameNarrator.NO_TITLE);
      this.nextScreen = p_88677_;
      this.lines = errorMessage(p_88675_, p_88676_);
   }

   private static RealmsGenericErrorScreen.ErrorMessage errorMessage(RealmsServiceException p_288965_) {
      RealmsError realmserror = p_288965_.realmsError;
      if (realmserror == null) {
         return errorMessage(Component.translatable("mco.errorMessage.realmsService", p_288965_.httpResultCode), Component.literal(p_288965_.rawResponse));
      } else {
         int i = realmserror.getErrorCode();
         String s = "mco.errorMessage." + i;
         return errorMessage(Component.translatable("mco.errorMessage.realmsService.realmsError", i), (Component)(I18n.exists(s) ? Component.translatable(s) : Component.nullToEmpty(realmserror.getErrorMessage())));
      }
   }

   private static RealmsGenericErrorScreen.ErrorMessage errorMessage(Component p_289003_) {
      return errorMessage(Component.translatable("mco.errorMessage.generic"), p_289003_);
   }

   private static RealmsGenericErrorScreen.ErrorMessage errorMessage(Component p_289010_, Component p_289015_) {
      return new RealmsGenericErrorScreen.ErrorMessage(p_289010_, p_289015_);
   }

   public void init() {
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_OK, (p_280728_) -> {
         this.minecraft.setScreen(this.nextScreen);
      }).bounds(this.width / 2 - 100, this.height - 52, 200, 20).build());
      this.line2Split = MultiLineLabel.create(this.font, this.lines.detail, this.width * 3 / 4);
   }

   public Component getNarrationMessage() {
      return Component.empty().append(this.lines.title).append(": ").append(this.lines.detail);
   }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
       if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
          minecraft.setScreen(this.nextScreen);
          return true;
       }
       return super.keyPressed(key, scanCode, modifiers);
    }

   public void render(GuiGraphics p_283497_, int p_88680_, int p_88681_, float p_88682_) {
      this.renderBackground(p_283497_);
      p_283497_.drawCenteredString(this.font, this.lines.title, this.width / 2, 80, 16777215);
      this.line2Split.renderCentered(p_283497_, this.width / 2, 100, 9, 16711680);
      super.render(p_283497_, p_88680_, p_88681_, p_88682_);
   }

   @OnlyIn(Dist.CLIENT)
   static record ErrorMessage(Component title, Component detail) {
   }
}
