package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsTermsScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component TITLE = Component.translatable("mco.terms.title");
   private static final Component TERMS_STATIC_TEXT = Component.translatable("mco.terms.sentence.1");
   private static final Component TERMS_LINK_TEXT = CommonComponents.space().append(Component.translatable("mco.terms.sentence.2").withStyle(Style.EMPTY.withUnderlined(true)));
   private final Screen lastScreen;
   private final RealmsMainScreen mainScreen;
   private final RealmsServer realmsServer;
   private boolean onLink;

   public RealmsTermsScreen(Screen p_90033_, RealmsMainScreen p_90034_, RealmsServer p_90035_) {
      super(TITLE);
      this.lastScreen = p_90033_;
      this.mainScreen = p_90034_;
      this.realmsServer = p_90035_;
   }

   public void init() {
      int i = this.width / 4 - 2;
      this.addRenderableWidget(Button.builder(Component.translatable("mco.terms.buttons.agree"), (p_90054_) -> {
         this.agreedToTos();
      }).bounds(this.width / 4, row(12), i, 20).build());
      this.addRenderableWidget(Button.builder(Component.translatable("mco.terms.buttons.disagree"), (p_280762_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }).bounds(this.width / 2 + 4, row(12), i, 20).build());
   }

   public boolean keyPressed(int p_90041_, int p_90042_, int p_90043_) {
      if (p_90041_ == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(p_90041_, p_90042_, p_90043_);
      }
   }

   private void agreedToTos() {
      RealmsClient realmsclient = RealmsClient.create();

      try {
         realmsclient.agreeToTos();
         this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new GetServerDetailsTask(this.mainScreen, this.lastScreen, this.realmsServer, new ReentrantLock())));
      } catch (RealmsServiceException realmsserviceexception) {
         LOGGER.error("Couldn't agree to TOS");
      }

   }

   public boolean mouseClicked(double p_90037_, double p_90038_, int p_90039_) {
      if (this.onLink) {
         this.minecraft.keyboardHandler.setClipboard("https://aka.ms/MinecraftRealmsTerms");
         Util.getPlatform().openUri("https://aka.ms/MinecraftRealmsTerms");
         return true;
      } else {
         return super.mouseClicked(p_90037_, p_90038_, p_90039_);
      }
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), TERMS_STATIC_TEXT).append(CommonComponents.SPACE).append(TERMS_LINK_TEXT);
   }

   public void render(GuiGraphics p_281619_, int p_283526_, int p_282002_, float p_282536_) {
      this.renderBackground(p_281619_);
      p_281619_.drawCenteredString(this.font, this.title, this.width / 2, 17, 16777215);
      p_281619_.drawString(this.font, TERMS_STATIC_TEXT, this.width / 2 - 120, row(5), 16777215, false);
      int i = this.font.width(TERMS_STATIC_TEXT);
      int j = this.width / 2 - 121 + i;
      int k = row(5);
      int l = j + this.font.width(TERMS_LINK_TEXT) + 1;
      int i1 = k + 1 + 9;
      this.onLink = j <= p_283526_ && p_283526_ <= l && k <= p_282002_ && p_282002_ <= i1;
      p_281619_.drawString(this.font, TERMS_LINK_TEXT, this.width / 2 - 120 + i, row(5), this.onLink ? 7107012 : 3368635, false);
      super.render(p_281619_, p_283526_, p_282002_, p_282536_);
   }
}