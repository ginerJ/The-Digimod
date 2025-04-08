package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.task.OpenServerTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsBrokenWorldScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int DEFAULT_BUTTON_WIDTH = 80;
   private final Screen lastScreen;
   private final RealmsMainScreen mainScreen;
   @Nullable
   private RealmsServer serverData;
   private final long serverId;
   private final Component[] message = new Component[]{Component.translatable("mco.brokenworld.message.line1"), Component.translatable("mco.brokenworld.message.line2")};
   private int leftX;
   private int rightX;
   private final List<Integer> slotsThatHasBeenDownloaded = Lists.newArrayList();
   private int animTick;

   public RealmsBrokenWorldScreen(Screen p_88296_, RealmsMainScreen p_88297_, long p_88298_, boolean p_88299_) {
      super(p_88299_ ? Component.translatable("mco.brokenworld.minigame.title") : Component.translatable("mco.brokenworld.title"));
      this.lastScreen = p_88296_;
      this.mainScreen = p_88297_;
      this.serverId = p_88298_;
   }

   public void init() {
      this.leftX = this.width / 2 - 150;
      this.rightX = this.width / 2 + 190;
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (p_88333_) -> {
         this.backButtonClicked();
      }).bounds(this.rightX - 80 + 8, row(13) - 5, 70, 20).build());
      if (this.serverData == null) {
         this.fetchServerData(this.serverId);
      } else {
         this.addButtons();
      }

   }

   public Component getNarrationMessage() {
      return ComponentUtils.formatList(Stream.concat(Stream.of(this.title), Stream.of(this.message)).collect(Collectors.toList()), CommonComponents.SPACE);
   }

   private void addButtons() {
      for(Map.Entry<Integer, RealmsWorldOptions> entry : this.serverData.slots.entrySet()) {
         int i = entry.getKey();
         boolean flag = i != this.serverData.activeSlot || this.serverData.worldType == RealmsServer.WorldType.MINIGAME;
         Button button;
         if (flag) {
            button = Button.builder(Component.translatable("mco.brokenworld.play"), (p_88347_) -> {
               if ((this.serverData.slots.get(i)).empty) {
                  RealmsResetWorldScreen realmsresetworldscreen = new RealmsResetWorldScreen(this, this.serverData, Component.translatable("mco.configure.world.switch.slot"), Component.translatable("mco.configure.world.switch.slot.subtitle"), 10526880, CommonComponents.GUI_CANCEL, this::doSwitchOrReset, () -> {
                     this.minecraft.setScreen(this);
                     this.doSwitchOrReset();
                  });
                  realmsresetworldscreen.setSlot(i);
                  realmsresetworldscreen.setResetTitle(Component.translatable("mco.create.world.reset.title"));
                  this.minecraft.setScreen(realmsresetworldscreen);
               } else {
                  this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchSlotTask(this.serverData.id, i, this::doSwitchOrReset)));
               }

            }).bounds(this.getFramePositionX(i), row(8), 80, 20).build();
         } else {
            button = Button.builder(Component.translatable("mco.brokenworld.download"), (p_287302_) -> {
               Component component = Component.translatable("mco.configure.world.restore.download.question.line1");
               Component component1 = Component.translatable("mco.configure.world.restore.download.question.line2");
               this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_280705_) -> {
                  if (p_280705_) {
                     this.downloadWorld(i);
                  } else {
                     this.minecraft.setScreen(this);
                  }

               }, RealmsLongConfirmationScreen.Type.INFO, component, component1, true));
            }).bounds(this.getFramePositionX(i), row(8), 80, 20).build();
         }

         if (this.slotsThatHasBeenDownloaded.contains(i)) {
            button.active = false;
            button.setMessage(Component.translatable("mco.brokenworld.downloaded"));
         }

         this.addRenderableWidget(button);
         this.addRenderableWidget(Button.builder(Component.translatable("mco.brokenworld.reset"), (p_280707_) -> {
            RealmsResetWorldScreen realmsresetworldscreen = new RealmsResetWorldScreen(this, this.serverData, this::doSwitchOrReset, () -> {
               this.minecraft.setScreen(this);
               this.doSwitchOrReset();
            });
            if (i != this.serverData.activeSlot || this.serverData.worldType == RealmsServer.WorldType.MINIGAME) {
               realmsresetworldscreen.setSlot(i);
            }

            this.minecraft.setScreen(realmsresetworldscreen);
         }).bounds(this.getFramePositionX(i), row(10), 80, 20).build());
      }

   }

   public void tick() {
      ++this.animTick;
   }

   public void render(GuiGraphics p_282934_, int p_88317_, int p_88318_, float p_88319_) {
      this.renderBackground(p_282934_);
      super.render(p_282934_, p_88317_, p_88318_, p_88319_);
      p_282934_.drawCenteredString(this.font, this.title, this.width / 2, 17, 16777215);

      for(int i = 0; i < this.message.length; ++i) {
         p_282934_.drawCenteredString(this.font, this.message[i], this.width / 2, row(-1) + 3 + i * 12, 10526880);
      }

      if (this.serverData != null) {
         for(Map.Entry<Integer, RealmsWorldOptions> entry : this.serverData.slots.entrySet()) {
            if ((entry.getValue()).templateImage != null && (entry.getValue()).templateId != -1L) {
               this.drawSlotFrame(p_282934_, this.getFramePositionX(entry.getKey()), row(1) + 5, p_88317_, p_88318_, this.serverData.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().getSlotName(entry.getKey()), entry.getKey(), (entry.getValue()).templateId, (entry.getValue()).templateImage, (entry.getValue()).empty);
            } else {
               this.drawSlotFrame(p_282934_, this.getFramePositionX(entry.getKey()), row(1) + 5, p_88317_, p_88318_, this.serverData.activeSlot == entry.getKey() && !this.isMinigame(), entry.getValue().getSlotName(entry.getKey()), entry.getKey(), -1L, (String)null, (entry.getValue()).empty);
            }
         }

      }
   }

   private int getFramePositionX(int p_88302_) {
      return this.leftX + (p_88302_ - 1) * 110;
   }

   public boolean keyPressed(int p_88304_, int p_88305_, int p_88306_) {
      if (p_88304_ == 256) {
         this.backButtonClicked();
         return true;
      } else {
         return super.keyPressed(p_88304_, p_88305_, p_88306_);
      }
   }

   private void backButtonClicked() {
      this.minecraft.setScreen(this.lastScreen);
   }

   private void fetchServerData(long p_88314_) {
      (new Thread(() -> {
         RealmsClient realmsclient = RealmsClient.create();

         try {
            this.serverData = realmsclient.getOwnWorld(p_88314_);
            this.addButtons();
         } catch (RealmsServiceException realmsserviceexception) {
            LOGGER.error("Couldn't get own world");
            this.minecraft.setScreen(new RealmsGenericErrorScreen(Component.nullToEmpty(realmsserviceexception.getMessage()), this.lastScreen));
         }

      })).start();
   }

   public void doSwitchOrReset() {
      (new Thread(() -> {
         RealmsClient realmsclient = RealmsClient.create();
         if (this.serverData.state == RealmsServer.State.CLOSED) {
            this.minecraft.execute(() -> {
               this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this, new OpenServerTask(this.serverData, this, this.mainScreen, true, this.minecraft)));
            });
         } else {
            try {
               RealmsServer realmsserver = realmsclient.getOwnWorld(this.serverId);
               this.minecraft.execute(() -> {
                  this.mainScreen.newScreen().play(realmsserver, this);
               });
            } catch (RealmsServiceException realmsserviceexception) {
               LOGGER.error("Couldn't get own world");
               this.minecraft.execute(() -> {
                  this.minecraft.setScreen(this.lastScreen);
               });
            }
         }

      })).start();
   }

   private void downloadWorld(int p_88336_) {
      RealmsClient realmsclient = RealmsClient.create();

      try {
         WorldDownload worlddownload = realmsclient.requestDownloadInfo(this.serverData.id, p_88336_);
         RealmsDownloadLatestWorldScreen realmsdownloadlatestworldscreen = new RealmsDownloadLatestWorldScreen(this, worlddownload, this.serverData.getWorldName(p_88336_), (p_280702_) -> {
            if (p_280702_) {
               this.slotsThatHasBeenDownloaded.add(p_88336_);
               this.clearWidgets();
               this.addButtons();
            } else {
               this.minecraft.setScreen(this);
            }

         });
         this.minecraft.setScreen(realmsdownloadlatestworldscreen);
      } catch (RealmsServiceException realmsserviceexception) {
         LOGGER.error("Couldn't download world data");
         this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, this));
      }

   }

   private boolean isMinigame() {
      return this.serverData != null && this.serverData.worldType == RealmsServer.WorldType.MINIGAME;
   }

   private void drawSlotFrame(GuiGraphics p_281929_, int p_283393_, int p_281553_, int p_283523_, int p_282823_, boolean p_283032_, String p_283498_, int p_283330_, long p_283588_, @Nullable String p_282484_, boolean p_282283_) {
      ResourceLocation resourcelocation;
      if (p_282283_) {
         resourcelocation = RealmsWorldSlotButton.EMPTY_SLOT_LOCATION;
      } else if (p_282484_ != null && p_283588_ != -1L) {
         resourcelocation = RealmsTextureManager.worldTemplate(String.valueOf(p_283588_), p_282484_);
      } else if (p_283330_ == 1) {
         resourcelocation = RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_1;
      } else if (p_283330_ == 2) {
         resourcelocation = RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_2;
      } else if (p_283330_ == 3) {
         resourcelocation = RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_3;
      } else {
         resourcelocation = RealmsTextureManager.worldTemplate(String.valueOf(this.serverData.minigameId), this.serverData.minigameImage);
      }

      if (!p_283032_) {
         p_281929_.setColor(0.56F, 0.56F, 0.56F, 1.0F);
      } else if (p_283032_) {
         float f = 0.9F + 0.1F * Mth.cos((float)this.animTick * 0.2F);
         p_281929_.setColor(f, f, f, 1.0F);
      }

      p_281929_.blit(resourcelocation, p_283393_ + 3, p_281553_ + 3, 0.0F, 0.0F, 74, 74, 74, 74);
      if (p_283032_) {
         p_281929_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         p_281929_.setColor(0.56F, 0.56F, 0.56F, 1.0F);
      }

      p_281929_.blit(RealmsWorldSlotButton.SLOT_FRAME_LOCATION, p_283393_, p_281553_, 0.0F, 0.0F, 80, 80, 80, 80);
      p_281929_.drawCenteredString(this.font, p_283498_, p_283393_ + 40, p_281553_ + 66, 16777215);
      p_281929_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }
}