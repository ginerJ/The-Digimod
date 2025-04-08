package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsNotification;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.task.DataFetcher;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsNotificationsScreen extends RealmsScreen {
   private static final ResourceLocation INVITE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invite_icon.png");
   private static final ResourceLocation TRIAL_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/trial_icon.png");
   private static final ResourceLocation NEWS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/news_notification_mainscreen.png");
   private static final ResourceLocation UNSEEN_NOTIFICATION_ICON_LOCATION = new ResourceLocation("minecraft", "textures/gui/unseen_notification.png");
   @Nullable
   private DataFetcher.Subscription realmsDataSubscription;
   @Nullable
   private RealmsNotificationsScreen.DataFetcherConfiguration currentConfiguration;
   private volatile int numberOfPendingInvites;
   static boolean checkedMcoAvailability;
   private static boolean trialAvailable;
   static boolean validClient;
   private static boolean hasUnreadNews;
   private static boolean hasUnseenNotifications;
   private final RealmsNotificationsScreen.DataFetcherConfiguration showAll = new RealmsNotificationsScreen.DataFetcherConfiguration() {
      public DataFetcher.Subscription initDataFetcher(RealmsDataFetcher p_275318_) {
         DataFetcher.Subscription datafetcher$subscription = p_275318_.dataFetcher.createSubscription();
         RealmsNotificationsScreen.this.addNewsAndInvitesSubscriptions(p_275318_, datafetcher$subscription);
         RealmsNotificationsScreen.this.addNotificationsSubscriptions(p_275318_, datafetcher$subscription);
         return datafetcher$subscription;
      }

      public boolean showOldNotifications() {
         return true;
      }
   };
   private final RealmsNotificationsScreen.DataFetcherConfiguration onlyNotifications = new RealmsNotificationsScreen.DataFetcherConfiguration() {
      public DataFetcher.Subscription initDataFetcher(RealmsDataFetcher p_275318_) {
         DataFetcher.Subscription datafetcher$subscription = p_275318_.dataFetcher.createSubscription();
         RealmsNotificationsScreen.this.addNotificationsSubscriptions(p_275318_, datafetcher$subscription);
         return datafetcher$subscription;
      }

      public boolean showOldNotifications() {
         return false;
      }
   };

   public RealmsNotificationsScreen() {
      super(GameNarrator.NO_TITLE);
   }

   public void init() {
      this.checkIfMcoEnabled();
      if (this.realmsDataSubscription != null) {
         this.realmsDataSubscription.forceUpdate();
      }

   }

   public void added() {
      super.added();
      this.minecraft.realmsDataFetcher().notificationsTask.reset();
   }

   @Nullable
   private RealmsNotificationsScreen.DataFetcherConfiguration getConfiguration() {
      boolean flag = this.inTitleScreen() && validClient;
      if (!flag) {
         return null;
      } else {
         return this.getRealmsNotificationsEnabled() ? this.showAll : this.onlyNotifications;
      }
   }

   public void tick() {
      RealmsNotificationsScreen.DataFetcherConfiguration realmsnotificationsscreen$datafetcherconfiguration = this.getConfiguration();
      if (!Objects.equals(this.currentConfiguration, realmsnotificationsscreen$datafetcherconfiguration)) {
         this.currentConfiguration = realmsnotificationsscreen$datafetcherconfiguration;
         if (this.currentConfiguration != null) {
            this.realmsDataSubscription = this.currentConfiguration.initDataFetcher(this.minecraft.realmsDataFetcher());
         } else {
            this.realmsDataSubscription = null;
         }
      }

      if (this.realmsDataSubscription != null) {
         this.realmsDataSubscription.tick();
      }

   }

   private boolean getRealmsNotificationsEnabled() {
      return this.minecraft.options.realmsNotifications().get();
   }

   private boolean inTitleScreen() {
      return this.minecraft.screen instanceof TitleScreen;
   }

   private void checkIfMcoEnabled() {
      if (!checkedMcoAvailability) {
         checkedMcoAvailability = true;
         (new Thread("Realms Notification Availability checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.create();

               try {
                  RealmsClient.CompatibleVersionResponse realmsclient$compatibleversionresponse = realmsclient.clientCompatible();
                  if (realmsclient$compatibleversionresponse != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                     return;
                  }
               } catch (RealmsServiceException realmsserviceexception) {
                  if (realmsserviceexception.httpResultCode != 401) {
                     RealmsNotificationsScreen.checkedMcoAvailability = false;
                  }

                  return;
               }

               RealmsNotificationsScreen.validClient = true;
            }
         }).start();
      }

   }

   public void render(GuiGraphics p_282587_, int p_282992_, int p_283028_, float p_281605_) {
      if (validClient) {
         this.drawIcons(p_282587_);
      }

      super.render(p_282587_, p_282992_, p_283028_, p_281605_);
   }

   private void drawIcons(GuiGraphics p_282966_) {
      int i = this.numberOfPendingInvites;
      int j = 24;
      int k = this.height / 4 + 48;
      int l = this.width / 2 + 80;
      int i1 = k + 48 + 2;
      int j1 = 0;
      if (hasUnseenNotifications) {
         p_282966_.blit(UNSEEN_NOTIFICATION_ICON_LOCATION, l - j1 + 5, i1 + 3, 0.0F, 0.0F, 10, 10, 10, 10);
         j1 += 14;
      }

      if (this.currentConfiguration != null && this.currentConfiguration.showOldNotifications()) {
         if (hasUnreadNews) {
            p_282966_.pose().pushPose();
            p_282966_.pose().scale(0.4F, 0.4F, 0.4F);
            p_282966_.blit(NEWS_ICON_LOCATION, (int)((double)(l + 2 - j1) * 2.5D), (int)((double)i1 * 2.5D), 0.0F, 0.0F, 40, 40, 40, 40);
            p_282966_.pose().popPose();
            j1 += 14;
         }

         if (i != 0) {
            p_282966_.blit(INVITE_ICON_LOCATION, l - j1, i1, 0.0F, 0.0F, 18, 15, 18, 30);
            j1 += 16;
         }

         if (trialAvailable) {
            int k1 = 0;
            if ((Util.getMillis() / 800L & 1L) == 1L) {
               k1 = 8;
            }

            p_282966_.blit(TRIAL_ICON_LOCATION, l + 4 - j1, i1 + 4, 0.0F, (float)k1, 8, 8, 8, 16);
         }
      }

   }

   void addNewsAndInvitesSubscriptions(RealmsDataFetcher p_275490_, DataFetcher.Subscription p_275623_) {
      p_275623_.subscribe(p_275490_.pendingInvitesTask, (p_239521_) -> {
         this.numberOfPendingInvites = p_239521_;
      });
      p_275623_.subscribe(p_275490_.trialAvailabilityTask, (p_239494_) -> {
         trialAvailable = p_239494_;
      });
      p_275623_.subscribe(p_275490_.newsTask, (p_238946_) -> {
         p_275490_.newsManager.updateUnreadNews(p_238946_);
         hasUnreadNews = p_275490_.newsManager.hasUnreadNews();
      });
   }

   void addNotificationsSubscriptions(RealmsDataFetcher p_275619_, DataFetcher.Subscription p_275628_) {
      p_275628_.subscribe(p_275619_.notificationsTask, (p_274637_) -> {
         hasUnseenNotifications = false;

         for(RealmsNotification realmsnotification : p_274637_) {
            if (!realmsnotification.seen()) {
               hasUnseenNotifications = true;
               break;
            }
         }

      });
   }

   @OnlyIn(Dist.CLIENT)
   interface DataFetcherConfiguration {
      DataFetcher.Subscription initDataFetcher(RealmsDataFetcher p_275608_);

      boolean showOldNotifications();
   }
}
