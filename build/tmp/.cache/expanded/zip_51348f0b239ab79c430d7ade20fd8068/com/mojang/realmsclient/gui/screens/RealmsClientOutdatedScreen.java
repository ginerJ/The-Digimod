package com.mojang.realmsclient.gui.screens;

import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsClientOutdatedScreen extends RealmsScreen {
   private static final Component INCOMPATIBLE_TITLE = Component.translatable("mco.client.incompatible.title");
   private static final Component[] INCOMPATIBLE_MESSAGES_SNAPSHOT = new Component[]{Component.translatable("mco.client.incompatible.msg.line1"), Component.translatable("mco.client.incompatible.msg.line2"), Component.translatable("mco.client.incompatible.msg.line3")};
   private static final Component[] INCOMPATIBLE_MESSAGES = new Component[]{Component.translatable("mco.client.incompatible.msg.line1"), Component.translatable("mco.client.incompatible.msg.line2")};
   private final Screen lastScreen;

   public RealmsClientOutdatedScreen(Screen p_231304_) {
      super(INCOMPATIBLE_TITLE);
      this.lastScreen = p_231304_;
   }

   public void init() {
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (p_280710_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }).bounds(this.width / 2 - 100, row(12), 200, 20).build());
   }

   public void render(GuiGraphics p_283142_, int p_88374_, int p_88375_, float p_88376_) {
      this.renderBackground(p_283142_);
      p_283142_.drawCenteredString(this.font, this.title, this.width / 2, row(3), 16711680);
      Component[] acomponent = this.getMessages();

      for(int i = 0; i < acomponent.length; ++i) {
         p_283142_.drawCenteredString(this.font, acomponent[i], this.width / 2, row(5) + i * 12, 16777215);
      }

      super.render(p_283142_, p_88374_, p_88375_, p_88376_);
   }

   private Component[] getMessages() {
      return SharedConstants.getCurrentVersion().isStable() ? INCOMPATIBLE_MESSAGES : INCOMPATIBLE_MESSAGES_SNAPSHOT;
   }

   public boolean keyPressed(int p_88369_, int p_88370_, int p_88371_) {
      if (p_88369_ != 257 && p_88369_ != 335 && p_88369_ != 256) {
         return super.keyPressed(p_88369_, p_88370_, p_88371_);
      } else {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      }
   }
}