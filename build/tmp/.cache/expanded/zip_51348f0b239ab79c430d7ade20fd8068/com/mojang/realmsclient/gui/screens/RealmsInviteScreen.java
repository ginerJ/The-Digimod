package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsInviteScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component NAME_LABEL = Component.translatable("mco.configure.world.invite.profile.name").withStyle((p_289621_) -> {
      return p_289621_.withColor(-6250336);
   });
   private static final Component INVITING_PLAYER_TEXT = Component.translatable("mco.configure.world.players.inviting").withStyle((p_289617_) -> {
      return p_289617_.withColor(-6250336);
   });
   private static final Component NO_SUCH_PLAYER_ERROR_TEXT = Component.translatable("mco.configure.world.players.error").withStyle((p_289622_) -> {
      return p_289622_.withColor(-65536);
   });
   private EditBox profileName;
   private Button inviteButton;
   private final RealmsServer serverData;
   private final RealmsConfigureWorldScreen configureScreen;
   private final Screen lastScreen;
   @Nullable
   private Component message;

   public RealmsInviteScreen(RealmsConfigureWorldScreen p_88703_, Screen p_88704_, RealmsServer p_88705_) {
      super(GameNarrator.NO_TITLE);
      this.configureScreen = p_88703_;
      this.lastScreen = p_88704_;
      this.serverData = p_88705_;
   }

   public void tick() {
      this.profileName.tick();
   }

   public void init() {
      this.profileName = new EditBox(this.minecraft.font, this.width / 2 - 100, row(2), 200, 20, (EditBox)null, Component.translatable("mco.configure.world.invite.profile.name"));
      this.addWidget(this.profileName);
      this.setInitialFocus(this.profileName);
      this.inviteButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.buttons.invite"), (p_88721_) -> {
         this.onInvite();
      }).bounds(this.width / 2 - 100, row(10), 200, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (p_280729_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }).bounds(this.width / 2 - 100, row(12), 200, 20).build());
   }

   private void onInvite() {
      if (Util.isBlank(this.profileName.getValue())) {
         this.showMessage(NO_SUCH_PLAYER_ERROR_TEXT);
      } else {
         long i = this.serverData.id;
         String s = this.profileName.getValue().trim();
         this.inviteButton.active = false;
         this.profileName.setEditable(false);
         this.showMessage(INVITING_PLAYER_TEXT);
         CompletableFuture.supplyAsync(() -> {
            try {
               return RealmsClient.create().invite(i, s);
            } catch (Exception exception) {
               LOGGER.error("Couldn't invite user");
               return null;
            }
         }, Util.ioPool()).thenAcceptAsync((p_289618_) -> {
            if (p_289618_ != null) {
               this.serverData.players = p_289618_.players;
               this.minecraft.setScreen(new RealmsPlayerScreen(this.configureScreen, this.serverData));
            } else {
               this.showMessage(NO_SUCH_PLAYER_ERROR_TEXT);
            }

            this.profileName.setEditable(true);
            this.inviteButton.active = true;
         }, this.screenExecutor);
      }
   }

   private void showMessage(Component p_289685_) {
      this.message = p_289685_;
      this.minecraft.getNarrator().sayNow(p_289685_);
   }

   public boolean keyPressed(int p_88707_, int p_88708_, int p_88709_) {
      if (p_88707_ == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(p_88707_, p_88708_, p_88709_);
      }
   }

   public void render(GuiGraphics p_282206_, int p_283415_, int p_282016_, float p_283011_) {
      this.renderBackground(p_282206_);
      p_282206_.drawString(this.font, NAME_LABEL, this.width / 2 - 100, row(1), -1, false);
      if (this.message != null) {
         p_282206_.drawCenteredString(this.font, this.message, this.width / 2, row(5), -1);
      }

      this.profileName.render(p_282206_, p_283415_, p_282016_, p_283011_);
      super.render(p_282206_, p_283415_, p_282016_, p_283011_);
   }
}