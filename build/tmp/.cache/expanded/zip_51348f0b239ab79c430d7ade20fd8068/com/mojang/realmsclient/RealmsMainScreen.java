package com.mojang.realmsclient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsNotification;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.RealmsNewsManager;
import com.mojang.realmsclient.gui.RealmsServerList;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.gui.task.DataFetcher;
import com.mojang.realmsclient.util.RealmsPersistence;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.CommonLinks;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsMainScreen extends RealmsScreen {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation ON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/on_icon.png");
   private static final ResourceLocation OFF_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/off_icon.png");
   private static final ResourceLocation EXPIRED_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expired_icon.png");
   private static final ResourceLocation EXPIRES_SOON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expires_soon_icon.png");
   static final ResourceLocation INVITATION_ICONS_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invitation_icons.png");
   static final ResourceLocation INVITE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invite_icon.png");
   static final ResourceLocation WORLDICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/world_icon.png");
   private static final ResourceLocation LOGO_LOCATION = new ResourceLocation("realms", "textures/gui/title/realms.png");
   private static final ResourceLocation NEWS_LOCATION = new ResourceLocation("realms", "textures/gui/realms/news_icon.png");
   private static final ResourceLocation POPUP_LOCATION = new ResourceLocation("realms", "textures/gui/realms/popup.png");
   private static final ResourceLocation DARKEN_LOCATION = new ResourceLocation("realms", "textures/gui/realms/darken.png");
   static final ResourceLocation CROSS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/cross_icon.png");
   private static final ResourceLocation TRIAL_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/trial_icon.png");
   static final ResourceLocation INFO_ICON_LOCATION = new ResourceLocation("minecraft", "textures/gui/info_icon.png");
   static final List<Component> TRIAL_MESSAGE_LINES = ImmutableList.of(Component.translatable("mco.trial.message.line1"), Component.translatable("mco.trial.message.line2"));
   static final Component SERVER_UNITIALIZED_TEXT = Component.translatable("mco.selectServer.uninitialized");
   static final Component SUBSCRIPTION_EXPIRED_TEXT = Component.translatable("mco.selectServer.expiredList");
   private static final Component SUBSCRIPTION_RENEW_TEXT = Component.translatable("mco.selectServer.expiredRenew");
   static final Component TRIAL_EXPIRED_TEXT = Component.translatable("mco.selectServer.expiredTrial");
   static final Component SELECT_MINIGAME_PREFIX = Component.translatable("mco.selectServer.minigame").append(CommonComponents.SPACE);
   private static final Component POPUP_TEXT = Component.translatable("mco.selectServer.popup");
   private static final Component PLAY_TEXT = Component.translatable("mco.selectServer.play");
   private static final Component LEAVE_SERVER_TEXT = Component.translatable("mco.selectServer.leave");
   private static final Component CONFIGURE_SERVER_TEXT = Component.translatable("mco.selectServer.configure");
   private static final Component SERVER_EXPIRED_TOOLTIP = Component.translatable("mco.selectServer.expired");
   private static final Component SERVER_EXPIRES_SOON_TOOLTIP = Component.translatable("mco.selectServer.expires.soon");
   private static final Component SERVER_EXPIRES_IN_DAY_TOOLTIP = Component.translatable("mco.selectServer.expires.day");
   private static final Component SERVER_OPEN_TOOLTIP = Component.translatable("mco.selectServer.open");
   private static final Component SERVER_CLOSED_TOOLTIP = Component.translatable("mco.selectServer.closed");
   private static final Component NEWS_TOOLTIP = Component.translatable("mco.news");
   static final Component UNITIALIZED_WORLD_NARRATION = Component.translatable("gui.narrate.button", SERVER_UNITIALIZED_TEXT);
   static final Component TRIAL_TEXT = CommonComponents.joinLines(TRIAL_MESSAGE_LINES);
   private static final int BUTTON_WIDTH = 100;
   private static final int BUTTON_TOP_ROW_WIDTH = 308;
   private static final int BUTTON_BOTTOM_ROW_WIDTH = 204;
   private static final int FOOTER_HEIGHT = 64;
   private static final int LOGO_WIDTH = 128;
   private static final int LOGO_HEIGHT = 34;
   private static final int LOGO_TEXTURE_WIDTH = 128;
   private static final int LOGO_TEXTURE_HEIGHT = 64;
   private static final int LOGO_PADDING = 5;
   private static final int HEADER_HEIGHT = 44;
   private static List<ResourceLocation> teaserImages = ImmutableList.of();
   @Nullable
   private DataFetcher.Subscription dataSubscription;
   private RealmsServerList serverList;
   private final Set<UUID> handledSeenNotifications = new HashSet<>();
   private static boolean overrideConfigure;
   private static int lastScrollYPosition = -1;
   static volatile boolean hasParentalConsent;
   static volatile boolean checkedParentalConsent;
   static volatile boolean checkedClientCompatability;
   @Nullable
   static Screen realmsGenericErrorScreen;
   private static boolean regionsPinged;
   private final RateLimiter inviteNarrationLimiter;
   private boolean dontSetConnectedToRealms;
   final Screen lastScreen;
   RealmsMainScreen.RealmSelectionList realmSelectionList;
   private boolean realmsSelectionListAdded;
   private Button playButton;
   private Button backButton;
   private Button renewButton;
   private Button configureButton;
   private Button leaveButton;
   private List<RealmsServer> realmsServers = ImmutableList.of();
   volatile int numberOfPendingInvites;
   int animTick;
   private boolean hasFetchedServers;
   boolean popupOpenedByUser;
   private boolean justClosedPopup;
   private volatile boolean trialsAvailable;
   private volatile boolean createdTrial;
   private volatile boolean showingPopup;
   volatile boolean hasUnreadNews;
   @Nullable
   volatile String newsLink;
   private int carouselIndex;
   private int carouselTick;
   private boolean hasSwitchedCarouselImage;
   private List<KeyCombo> keyCombos;
   long lastClickTime;
   private ReentrantLock connectLock = new ReentrantLock();
   private MultiLineLabel formattedPopup = MultiLineLabel.EMPTY;
   private final List<RealmsNotification> notifications = new ArrayList<>();
   private Button showPopupButton;
   private RealmsMainScreen.PendingInvitesButton pendingInvitesButton;
   private Button newsButton;
   private Button createTrialButton;
   private Button buyARealmButton;
   private Button closeButton;

   public RealmsMainScreen(Screen p_86315_) {
      super(GameNarrator.NO_TITLE);
      this.lastScreen = p_86315_;
      this.inviteNarrationLimiter = RateLimiter.create((double)0.016666668F);
   }

   private boolean shouldShowMessageInList() {
      if (hasParentalConsent() && this.hasFetchedServers) {
         if (this.trialsAvailable && !this.createdTrial) {
            return true;
         } else {
            for(RealmsServer realmsserver : this.realmsServers) {
               if (realmsserver.ownerUUID.equals(this.minecraft.getUser().getUuid())) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean shouldShowPopup() {
      if (hasParentalConsent() && this.hasFetchedServers) {
         return this.popupOpenedByUser ? true : this.realmsServers.isEmpty();
      } else {
         return false;
      }
   }

   public void init() {
      this.keyCombos = Lists.newArrayList(new KeyCombo(new char[]{'3', '2', '1', '4', '5', '6'}, () -> {
         overrideConfigure = !overrideConfigure;
      }), new KeyCombo(new char[]{'9', '8', '7', '1', '2', '3'}, () -> {
         if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
            this.switchToProd();
         } else {
            this.switchToStage();
         }

      }), new KeyCombo(new char[]{'9', '8', '7', '4', '5', '6'}, () -> {
         if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
            this.switchToProd();
         } else {
            this.switchToLocal();
         }

      }));
      if (realmsGenericErrorScreen != null) {
         this.minecraft.setScreen(realmsGenericErrorScreen);
      } else {
         this.connectLock = new ReentrantLock();
         if (checkedClientCompatability && !hasParentalConsent()) {
            this.checkParentalConsent();
         }

         this.checkClientCompatability();
         if (!this.dontSetConnectedToRealms) {
            this.minecraft.setConnectedToRealms(false);
         }

         this.showingPopup = false;
         this.realmSelectionList = new RealmsMainScreen.RealmSelectionList();
         if (lastScrollYPosition != -1) {
            this.realmSelectionList.setScrollAmount((double)lastScrollYPosition);
         }

         this.addWidget(this.realmSelectionList);
         this.realmsSelectionListAdded = true;
         this.setInitialFocus(this.realmSelectionList);
         this.addMiddleButtons();
         this.addFooterButtons();
         this.addTopButtons();
         this.updateButtonStates((RealmsServer)null);
         this.formattedPopup = MultiLineLabel.create(this.font, POPUP_TEXT, 100);
         RealmsNewsManager realmsnewsmanager = this.minecraft.realmsDataFetcher().newsManager;
         this.hasUnreadNews = realmsnewsmanager.hasUnreadNews();
         this.newsLink = realmsnewsmanager.newsLink();
         if (this.serverList == null) {
            this.serverList = new RealmsServerList(this.minecraft);
         }

         if (this.dataSubscription != null) {
            this.dataSubscription.forceUpdate();
         }

      }
   }

   private static boolean hasParentalConsent() {
      return checkedParentalConsent && hasParentalConsent;
   }

   public void addTopButtons() {
      this.pendingInvitesButton = this.addRenderableWidget(new RealmsMainScreen.PendingInvitesButton());
      this.newsButton = this.addRenderableWidget(new RealmsMainScreen.NewsButton());
      this.showPopupButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.selectServer.purchase"), (p_86597_) -> {
         this.popupOpenedByUser = !this.popupOpenedByUser;
      }).bounds(this.width - 90, 12, 80, 20).build());
   }

   public void addMiddleButtons() {
      this.createTrialButton = this.addWidget(Button.builder(Component.translatable("mco.selectServer.trial"), (p_280681_) -> {
         if (this.trialsAvailable && !this.createdTrial) {
            Util.getPlatform().openUri("https://aka.ms/startjavarealmstrial");
            this.minecraft.setScreen(this.lastScreen);
         }
      }).bounds(this.width / 2 + 52, this.popupY0() + 137 - 20, 98, 20).build());
      this.buyARealmButton = this.addWidget(Button.builder(Component.translatable("mco.selectServer.buy"), (p_231255_) -> {
         Util.getPlatform().openUri("https://aka.ms/BuyJavaRealms");
      }).bounds(this.width / 2 + 52, this.popupY0() + 160 - 20, 98, 20).build());
      this.closeButton = this.addWidget(new RealmsMainScreen.CloseButton());
   }

   public void addFooterButtons() {
      this.playButton = Button.builder(PLAY_TEXT, (p_86659_) -> {
         this.play(this.getSelectedServer(), this);
      }).width(100).build();
      this.configureButton = Button.builder(CONFIGURE_SERVER_TEXT, (p_86672_) -> {
         this.configureClicked(this.getSelectedServer());
      }).width(100).build();
      this.renewButton = Button.builder(SUBSCRIPTION_RENEW_TEXT, (p_86622_) -> {
         this.onRenew(this.getSelectedServer());
      }).width(100).build();
      this.leaveButton = Button.builder(LEAVE_SERVER_TEXT, (p_86679_) -> {
         this.leaveClicked(this.getSelectedServer());
      }).width(100).build();
      this.backButton = Button.builder(CommonComponents.GUI_BACK, (p_280683_) -> {
         if (!this.justClosedPopup) {
            this.minecraft.setScreen(this.lastScreen);
         }

      }).width(100).build();
      GridLayout gridlayout = new GridLayout();
      GridLayout.RowHelper gridlayout$rowhelper = gridlayout.createRowHelper(1);
      LinearLayout linearlayout = gridlayout$rowhelper.addChild(new LinearLayout(308, 20, LinearLayout.Orientation.HORIZONTAL), gridlayout$rowhelper.newCellSettings().paddingBottom(4));
      linearlayout.addChild(this.playButton);
      linearlayout.addChild(this.configureButton);
      linearlayout.addChild(this.renewButton);
      LinearLayout linearlayout1 = gridlayout$rowhelper.addChild(new LinearLayout(204, 20, LinearLayout.Orientation.HORIZONTAL), gridlayout$rowhelper.newCellSettings().alignHorizontallyCenter());
      linearlayout1.addChild(this.leaveButton);
      linearlayout1.addChild(this.backButton);
      gridlayout.visitWidgets((p_272289_) -> {
         AbstractWidget abstractwidget = this.addRenderableWidget(p_272289_);
      });
      gridlayout.arrangeElements();
      FrameLayout.centerInRectangle(gridlayout, 0, this.height - 64, this.width, 64);
   }

   void updateButtonStates(@Nullable RealmsServer p_86514_) {
      this.backButton.active = true;
      if (hasParentalConsent() && this.hasFetchedServers) {
         boolean flag = this.shouldShowPopup() && this.trialsAvailable && !this.createdTrial;
         this.createTrialButton.visible = flag;
         this.createTrialButton.active = flag;
         this.buyARealmButton.visible = this.shouldShowPopup();
         this.closeButton.visible = this.shouldShowPopup();
         this.newsButton.active = true;
         this.newsButton.visible = this.newsLink != null;
         this.pendingInvitesButton.active = true;
         this.pendingInvitesButton.visible = true;
         this.showPopupButton.active = !this.shouldShowPopup();
         this.playButton.visible = !this.shouldShowPopup();
         this.renewButton.visible = !this.shouldShowPopup();
         this.leaveButton.visible = !this.shouldShowPopup();
         this.configureButton.visible = !this.shouldShowPopup();
         this.backButton.visible = !this.shouldShowPopup();
         this.playButton.active = this.shouldPlayButtonBeActive(p_86514_);
         this.renewButton.active = this.shouldRenewButtonBeActive(p_86514_);
         this.leaveButton.active = this.shouldLeaveButtonBeActive(p_86514_);
         this.configureButton.active = this.shouldConfigureButtonBeActive(p_86514_);
      } else {
         hideWidgets(new AbstractWidget[]{this.playButton, this.renewButton, this.configureButton, this.createTrialButton, this.buyARealmButton, this.closeButton, this.newsButton, this.pendingInvitesButton, this.showPopupButton, this.leaveButton});
      }
   }

   private boolean shouldShowPopupButton() {
      return (!this.shouldShowPopup() || this.popupOpenedByUser) && hasParentalConsent() && this.hasFetchedServers;
   }

   boolean shouldPlayButtonBeActive(@Nullable RealmsServer p_86563_) {
      return p_86563_ != null && !p_86563_.expired && p_86563_.state == RealmsServer.State.OPEN;
   }

   private boolean shouldRenewButtonBeActive(@Nullable RealmsServer p_86595_) {
      return p_86595_ != null && p_86595_.expired && this.isSelfOwnedServer(p_86595_);
   }

   private boolean shouldConfigureButtonBeActive(@Nullable RealmsServer p_86620_) {
      return p_86620_ != null && this.isSelfOwnedServer(p_86620_);
   }

   private boolean shouldLeaveButtonBeActive(@Nullable RealmsServer p_86645_) {
      return p_86645_ != null && !this.isSelfOwnedServer(p_86645_);
   }

   public void tick() {
      super.tick();
      if (this.pendingInvitesButton != null) {
         this.pendingInvitesButton.tick();
      }

      this.justClosedPopup = false;
      ++this.animTick;
      boolean flag = hasParentalConsent();
      if (this.dataSubscription == null && flag) {
         this.dataSubscription = this.initDataFetcher(this.minecraft.realmsDataFetcher());
      } else if (this.dataSubscription != null && !flag) {
         this.dataSubscription = null;
      }

      if (this.dataSubscription != null) {
         this.dataSubscription.tick();
      }

      if (this.shouldShowPopup()) {
         ++this.carouselTick;
      }

      if (this.showPopupButton != null) {
         this.showPopupButton.visible = this.shouldShowPopupButton();
         this.showPopupButton.active = this.showPopupButton.visible;
      }

   }

   private DataFetcher.Subscription initDataFetcher(RealmsDataFetcher p_238836_) {
      DataFetcher.Subscription datafetcher$subscription = p_238836_.dataFetcher.createSubscription();
      datafetcher$subscription.subscribe(p_238836_.serverListUpdateTask, (p_275856_) -> {
         List<RealmsServer> list = this.serverList.updateServersList(p_275856_);
         boolean flag = false;

         for(RealmsServer realmsserver : list) {
            if (this.isSelfOwnedNonExpiredServer(realmsserver)) {
               flag = true;
            }
         }

         this.realmsServers = list;
         this.hasFetchedServers = true;
         this.refreshRealmsSelectionList();
         if (!regionsPinged && flag) {
            regionsPinged = true;
            this.pingRegions();
         }

      });
      callRealmsClient(RealmsClient::getNotifications, (p_274622_) -> {
         this.notifications.clear();
         this.notifications.addAll(p_274622_);
         this.refreshRealmsSelectionList();
      });
      datafetcher$subscription.subscribe(p_238836_.pendingInvitesTask, (p_280682_) -> {
         this.numberOfPendingInvites = p_280682_;
         if (this.numberOfPendingInvites > 0 && this.inviteNarrationLimiter.tryAcquire(1)) {
            this.minecraft.getNarrator().sayNow(Component.translatable("mco.configure.world.invite.narration", this.numberOfPendingInvites));
         }

      });
      datafetcher$subscription.subscribe(p_238836_.trialAvailabilityTask, (p_238839_) -> {
         if (!this.createdTrial) {
            if (p_238839_ != this.trialsAvailable && this.shouldShowPopup()) {
               this.trialsAvailable = p_238839_;
               this.showingPopup = false;
            } else {
               this.trialsAvailable = p_238839_;
            }

         }
      });
      datafetcher$subscription.subscribe(p_238836_.liveStatsTask, (p_238847_) -> {
         for(RealmsServerPlayerList realmsserverplayerlist : p_238847_.servers) {
            for(RealmsServer realmsserver : this.realmsServers) {
               if (realmsserver.id == realmsserverplayerlist.serverId) {
                  realmsserver.updateServerPing(realmsserverplayerlist);
                  break;
               }
            }
         }

      });
      datafetcher$subscription.subscribe(p_238836_.newsTask, (p_231355_) -> {
         p_238836_.newsManager.updateUnreadNews(p_231355_);
         this.hasUnreadNews = p_238836_.newsManager.hasUnreadNews();
         this.newsLink = p_238836_.newsManager.newsLink();
         this.updateButtonStates((RealmsServer)null);
      });
      return datafetcher$subscription;
   }

   private static <T> void callRealmsClient(RealmsMainScreen.RealmsCall<T> p_275561_, Consumer<T> p_275686_) {
      Minecraft minecraft = Minecraft.getInstance();
      CompletableFuture.supplyAsync(() -> {
         try {
            return p_275561_.request(RealmsClient.create(minecraft));
         } catch (RealmsServiceException realmsserviceexception) {
            throw new RuntimeException(realmsserviceexception);
         }
      }).thenAcceptAsync(p_275686_, minecraft).exceptionally((p_274626_) -> {
         LOGGER.error("Failed to execute call to Realms Service", p_274626_);
         return null;
      });
   }

   private void refreshRealmsSelectionList() {
      boolean flag = !this.hasFetchedServers;
      this.realmSelectionList.clear();
      List<UUID> list = new ArrayList<>();

      for(RealmsNotification realmsnotification : this.notifications) {
         this.addEntriesForNotification(this.realmSelectionList, realmsnotification);
         if (!realmsnotification.seen() && !this.handledSeenNotifications.contains(realmsnotification.uuid())) {
            list.add(realmsnotification.uuid());
         }
      }

      if (!list.isEmpty()) {
         callRealmsClient((p_274625_) -> {
            p_274625_.notificationsSeen(list);
            return null;
         }, (p_274630_) -> {
            this.handledSeenNotifications.addAll(list);
         });
      }

      if (this.shouldShowMessageInList()) {
         this.realmSelectionList.addEntry(new RealmsMainScreen.TrialEntry());
      }

      RealmsMainScreen.Entry realmsmainscreen$entry = null;
      RealmsServer realmsserver1 = this.getSelectedServer();

      for(RealmsServer realmsserver : this.realmsServers) {
         RealmsMainScreen.ServerEntry realmsmainscreen$serverentry = new RealmsMainScreen.ServerEntry(realmsserver);
         this.realmSelectionList.addEntry(realmsmainscreen$serverentry);
         if (realmsserver1 != null && realmsserver1.id == realmsserver.id) {
            realmsmainscreen$entry = realmsmainscreen$serverentry;
         }
      }

      if (flag) {
         this.updateButtonStates((RealmsServer)null);
      } else {
         this.realmSelectionList.setSelected(realmsmainscreen$entry);
      }

   }

   private void addEntriesForNotification(RealmsMainScreen.RealmSelectionList p_275392_, RealmsNotification p_275492_) {
      if (p_275492_ instanceof RealmsNotification.VisitUrl realmsnotification$visiturl) {
         p_275392_.addEntry(new RealmsMainScreen.NotificationMessageEntry(realmsnotification$visiturl.getMessage(), realmsnotification$visiturl));
         p_275392_.addEntry(new RealmsMainScreen.ButtonEntry(realmsnotification$visiturl.buildOpenLinkButton(this)));
      }

   }

   void refreshFetcher() {
      if (this.dataSubscription != null) {
         this.dataSubscription.reset();
      }

   }

   private void pingRegions() {
      (new Thread(() -> {
         List<RegionPingResult> list = Ping.pingAllRegions();
         RealmsClient realmsclient = RealmsClient.create();
         PingResult pingresult = new PingResult();
         pingresult.pingResults = list;
         pingresult.worldIds = this.getOwnedNonExpiredWorldIds();

         try {
            realmsclient.sendPingResults(pingresult);
         } catch (Throwable throwable) {
            LOGGER.warn("Could not send ping result to Realms: ", throwable);
         }

      })).start();
   }

   private List<Long> getOwnedNonExpiredWorldIds() {
      List<Long> list = Lists.newArrayList();

      for(RealmsServer realmsserver : this.realmsServers) {
         if (this.isSelfOwnedNonExpiredServer(realmsserver)) {
            list.add(realmsserver.id);
         }
      }

      return list;
   }

   public void setCreatedTrial(boolean p_167191_) {
      this.createdTrial = p_167191_;
   }

   private void onRenew(@Nullable RealmsServer p_193500_) {
      if (p_193500_ != null) {
         String s = CommonLinks.extendRealms(p_193500_.remoteSubscriptionId, this.minecraft.getUser().getUuid(), p_193500_.expiredTrial);
         this.minecraft.keyboardHandler.setClipboard(s);
         Util.getPlatform().openUri(s);
      }

   }

   private void checkClientCompatability() {
      if (!checkedClientCompatability) {
         checkedClientCompatability = true;
         (new Thread("MCO Compatability Checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.create();

               try {
                  RealmsClient.CompatibleVersionResponse realmsclient$compatibleversionresponse = realmsclient.clientCompatible();
                  if (realmsclient$compatibleversionresponse != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                     RealmsMainScreen.realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen);
                     RealmsMainScreen.this.minecraft.execute(() -> {
                        RealmsMainScreen.this.minecraft.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
                     });
                     return;
                  }

                  RealmsMainScreen.this.checkParentalConsent();
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.checkedClientCompatability = false;
                  RealmsMainScreen.LOGGER.error("Couldn't connect to realms", (Throwable)realmsserviceexception);
                  if (realmsserviceexception.httpResultCode == 401) {
                     RealmsMainScreen.realmsGenericErrorScreen = new RealmsGenericErrorScreen(Component.translatable("mco.error.invalid.session.title"), Component.translatable("mco.error.invalid.session.message"), RealmsMainScreen.this.lastScreen);
                     RealmsMainScreen.this.minecraft.execute(() -> {
                        RealmsMainScreen.this.minecraft.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
                     });
                  } else {
                     RealmsMainScreen.this.minecraft.execute(() -> {
                        RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, RealmsMainScreen.this.lastScreen));
                     });
                  }
               }

            }
         }).start();
      }

   }

   void checkParentalConsent() {
      (new Thread("MCO Compatability Checker #1") {
         public void run() {
            RealmsClient realmsclient = RealmsClient.create();

            try {
               Boolean obool = realmsclient.mcoEnabled();
               if (obool) {
                  RealmsMainScreen.LOGGER.info("Realms is available for this user");
                  RealmsMainScreen.hasParentalConsent = true;
               } else {
                  RealmsMainScreen.LOGGER.info("Realms is not available for this user");
                  RealmsMainScreen.hasParentalConsent = false;
                  RealmsMainScreen.this.minecraft.execute(() -> {
                     RealmsMainScreen.this.minecraft.setScreen(new RealmsParentalConsentScreen(RealmsMainScreen.this.lastScreen));
                  });
               }

               RealmsMainScreen.checkedParentalConsent = true;
            } catch (RealmsServiceException realmsserviceexception) {
               RealmsMainScreen.LOGGER.error("Couldn't connect to realms", (Throwable)realmsserviceexception);
               RealmsMainScreen.this.minecraft.execute(() -> {
                  RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, RealmsMainScreen.this.lastScreen));
               });
            }

         }
      }).start();
   }

   private void switchToStage() {
      if (RealmsClient.currentEnvironment != RealmsClient.Environment.STAGE) {
         (new Thread("MCO Stage Availability Checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.create();

               try {
                  Boolean obool = realmsclient.stageAvailable();
                  if (obool) {
                     RealmsClient.switchToStage();
                     RealmsMainScreen.LOGGER.info("Switched to stage");
                     RealmsMainScreen.this.refreshFetcher();
                  }
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.LOGGER.error("Couldn't connect to Realms: {}", (Object)realmsserviceexception.toString());
               }

            }
         }).start();
      }

   }

   private void switchToLocal() {
      if (RealmsClient.currentEnvironment != RealmsClient.Environment.LOCAL) {
         (new Thread("MCO Local Availability Checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.create();

               try {
                  Boolean obool = realmsclient.stageAvailable();
                  if (obool) {
                     RealmsClient.switchToLocal();
                     RealmsMainScreen.LOGGER.info("Switched to local");
                     RealmsMainScreen.this.refreshFetcher();
                  }
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.LOGGER.error("Couldn't connect to Realms: {}", (Object)realmsserviceexception.toString());
               }

            }
         }).start();
      }

   }

   private void switchToProd() {
      RealmsClient.switchToProd();
      this.refreshFetcher();
   }

   private void configureClicked(@Nullable RealmsServer p_86657_) {
      if (p_86657_ != null && (this.minecraft.getUser().getUuid().equals(p_86657_.ownerUUID) || overrideConfigure)) {
         this.saveListScrollPosition();
         this.minecraft.setScreen(new RealmsConfigureWorldScreen(this, p_86657_.id));
      }

   }

   private void leaveClicked(@Nullable RealmsServer p_86670_) {
      if (p_86670_ != null && !this.minecraft.getUser().getUuid().equals(p_86670_.ownerUUID)) {
         this.saveListScrollPosition();
         Component component = Component.translatable("mco.configure.world.leave.question.line1");
         Component component1 = Component.translatable("mco.configure.world.leave.question.line2");
         this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_231253_) -> {
            this.leaveServer(p_231253_, p_86670_);
         }, RealmsLongConfirmationScreen.Type.INFO, component, component1, true));
      }

   }

   private void saveListScrollPosition() {
      lastScrollYPosition = (int)this.realmSelectionList.getScrollAmount();
   }

   @Nullable
   private RealmsServer getSelectedServer() {
      if (this.realmSelectionList == null) {
         return null;
      } else {
         RealmsMainScreen.Entry realmsmainscreen$entry = this.realmSelectionList.getSelected();
         return realmsmainscreen$entry != null ? realmsmainscreen$entry.getServer() : null;
      }
   }

   private void leaveServer(boolean p_193494_, final RealmsServer p_193495_) {
      if (p_193494_) {
         (new Thread("Realms-leave-server") {
            public void run() {
               try {
                  RealmsClient realmsclient = RealmsClient.create();
                  realmsclient.uninviteMyselfFrom(p_193495_.id);
                  RealmsMainScreen.this.minecraft.execute(() -> {
                     RealmsMainScreen.this.removeServer(p_193495_);
                  });
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.LOGGER.error("Couldn't configure world");
                  RealmsMainScreen.this.minecraft.execute(() -> {
                     RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, RealmsMainScreen.this));
                  });
               }

            }
         }).start();
      }

      this.minecraft.setScreen(this);
   }

   void removeServer(RealmsServer p_86677_) {
      this.realmsServers = this.serverList.removeItem(p_86677_);
      this.realmSelectionList.children().removeIf((p_231250_) -> {
         RealmsServer realmsserver = p_231250_.getServer();
         return realmsserver != null && realmsserver.id == p_86677_.id;
      });
      this.realmSelectionList.setSelected((RealmsMainScreen.Entry)null);
      this.updateButtonStates((RealmsServer)null);
      this.playButton.active = false;
   }

   void dismissNotification(UUID p_275349_) {
      callRealmsClient((p_274628_) -> {
         p_274628_.notificationsDismiss(List.of(p_275349_));
         return null;
      }, (p_274632_) -> {
         this.notifications.removeIf((p_274621_) -> {
            return p_274621_.dismissable() && p_275349_.equals(p_274621_.uuid());
         });
         this.refreshRealmsSelectionList();
      });
   }

   public void resetScreen() {
      if (this.realmSelectionList != null) {
         this.realmSelectionList.setSelected((RealmsMainScreen.Entry)null);
      }

   }

   public boolean keyPressed(int p_86401_, int p_86402_, int p_86403_) {
      if (p_86401_ == 256) {
         this.keyCombos.forEach(KeyCombo::reset);
         this.onClosePopup();
         return true;
      } else {
         return super.keyPressed(p_86401_, p_86402_, p_86403_);
      }
   }

   void onClosePopup() {
      if (this.shouldShowPopup() && this.popupOpenedByUser) {
         this.popupOpenedByUser = false;
      } else {
         this.minecraft.setScreen(this.lastScreen);
      }

   }

   public boolean charTyped(char p_86388_, int p_86389_) {
      this.keyCombos.forEach((p_231245_) -> {
         p_231245_.keyPressed(p_86388_);
      });
      return true;
   }

   public void render(GuiGraphics p_282736_, int p_283347_, int p_282480_, float p_283485_) {
      this.renderBackground(p_282736_);
      this.realmSelectionList.render(p_282736_, p_283347_, p_282480_, p_283485_);
      p_282736_.blit(LOGO_LOCATION, this.width / 2 - 64, 5, 0.0F, 0.0F, 128, 34, 128, 64);
      if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
         this.renderStage(p_282736_);
      }

      if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
         this.renderLocal(p_282736_);
      }

      if (this.shouldShowPopup()) {
         p_282736_.pose().pushPose();
         p_282736_.pose().translate(0.0F, 0.0F, 100.0F);
         this.drawPopup(p_282736_, p_283347_, p_282480_, p_283485_);
         p_282736_.pose().popPose();
      } else {
         if (this.showingPopup) {
            this.updateButtonStates((RealmsServer)null);
            if (!this.realmsSelectionListAdded) {
               this.addWidget(this.realmSelectionList);
               this.realmsSelectionListAdded = true;
            }

            this.playButton.active = this.shouldPlayButtonBeActive(this.getSelectedServer());
         }

         this.showingPopup = false;
      }

      super.render(p_282736_, p_283347_, p_282480_, p_283485_);
      if (this.trialsAvailable && !this.createdTrial && this.shouldShowPopup()) {
         int i = 8;
         int j = 8;
         int k = 0;
         if ((Util.getMillis() / 800L & 1L) == 1L) {
            k = 8;
         }

         p_282736_.pose().pushPose();
         p_282736_.pose().translate(0.0F, 0.0F, 110.0F);
         p_282736_.blit(TRIAL_ICON_LOCATION, this.createTrialButton.getX() + this.createTrialButton.getWidth() - 8 - 4, this.createTrialButton.getY() + this.createTrialButton.getHeight() / 2 - 4, 0.0F, (float)k, 8, 8, 8, 16);
         p_282736_.pose().popPose();
      }

   }

   public boolean mouseClicked(double p_86397_, double p_86398_, int p_86399_) {
      if (this.isOutsidePopup(p_86397_, p_86398_) && this.popupOpenedByUser) {
         this.popupOpenedByUser = false;
         this.justClosedPopup = true;
         return true;
      } else {
         return super.mouseClicked(p_86397_, p_86398_, p_86399_);
      }
   }

   private boolean isOutsidePopup(double p_86394_, double p_86395_) {
      int i = this.popupX0();
      int j = this.popupY0();
      return p_86394_ < (double)(i - 5) || p_86394_ > (double)(i + 315) || p_86395_ < (double)(j - 5) || p_86395_ > (double)(j + 171);
   }

   private void drawPopup(GuiGraphics p_283329_, int p_290033_, int p_290032_, float p_290030_) {
      int i = this.popupX0();
      int j = this.popupY0();
      if (!this.showingPopup) {
         this.carouselIndex = 0;
         this.carouselTick = 0;
         this.hasSwitchedCarouselImage = true;
         this.updateButtonStates((RealmsServer)null);
         if (this.realmsSelectionListAdded) {
            this.removeWidget(this.realmSelectionList);
            this.realmsSelectionListAdded = false;
         }

         this.minecraft.getNarrator().sayNow(POPUP_TEXT);
      }

      if (this.hasFetchedServers) {
         this.showingPopup = true;
      }

      p_283329_.setColor(1.0F, 1.0F, 1.0F, 0.7F);
      RenderSystem.enableBlend();
      p_283329_.blit(DARKEN_LOCATION, 0, 44, 0.0F, 0.0F, this.width, this.height - 44, 310, 166);
      RenderSystem.disableBlend();
      p_283329_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      p_283329_.blit(POPUP_LOCATION, i, j, 0.0F, 0.0F, 310, 166, 310, 166);
      if (!teaserImages.isEmpty()) {
         p_283329_.blit(teaserImages.get(this.carouselIndex), i + 7, j + 7, 0.0F, 0.0F, 195, 152, 195, 152);
         if (this.carouselTick % 95 < 5) {
            if (!this.hasSwitchedCarouselImage) {
               this.carouselIndex = (this.carouselIndex + 1) % teaserImages.size();
               this.hasSwitchedCarouselImage = true;
            }
         } else {
            this.hasSwitchedCarouselImage = false;
         }
      }

      this.formattedPopup.renderLeftAlignedNoShadow(p_283329_, this.width / 2 + 52, j + 7, 10, 16777215);
      this.createTrialButton.render(p_283329_, p_290033_, p_290032_, p_290030_);
      this.buyARealmButton.render(p_283329_, p_290033_, p_290032_, p_290030_);
      this.closeButton.render(p_283329_, p_290033_, p_290032_, p_290030_);
   }

   int popupX0() {
      return (this.width - 310) / 2;
   }

   int popupY0() {
      return this.height / 2 - 80;
   }

   public void play(@Nullable RealmsServer p_86516_, Screen p_86517_) {
      if (p_86516_ != null) {
         try {
            if (!this.connectLock.tryLock(1L, TimeUnit.SECONDS)) {
               return;
            }

            if (this.connectLock.getHoldCount() > 1) {
               return;
            }
         } catch (InterruptedException interruptedexception) {
            return;
         }

         this.dontSetConnectedToRealms = true;
         this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(p_86517_, new GetServerDetailsTask(this, p_86517_, p_86516_, this.connectLock)));
      }

   }

   boolean isSelfOwnedServer(RealmsServer p_86684_) {
      return p_86684_.ownerUUID != null && p_86684_.ownerUUID.equals(this.minecraft.getUser().getUuid());
   }

   private boolean isSelfOwnedNonExpiredServer(RealmsServer p_86689_) {
      return this.isSelfOwnedServer(p_86689_) && !p_86689_.expired;
   }

   void drawExpired(GuiGraphics p_282859_, int p_283367_, int p_283231_, int p_281593_, int p_281773_) {
      p_282859_.blit(EXPIRED_ICON_LOCATION, p_283367_, p_283231_, 0.0F, 0.0F, 10, 28, 10, 28);
      if (p_281593_ >= p_283367_ && p_281593_ <= p_283367_ + 9 && p_281773_ >= p_283231_ && p_281773_ <= p_283231_ + 27 && p_281773_ < this.height - 40 && p_281773_ > 32 && !this.shouldShowPopup()) {
         this.setTooltipForNextRenderPass(SERVER_EXPIRED_TOOLTIP);
      }

   }

   void drawExpiring(GuiGraphics p_283382_, int p_282134_, int p_283200_, int p_281673_, int p_282920_, int p_282554_) {
      if (this.animTick % 20 < 10) {
         p_283382_.blit(EXPIRES_SOON_ICON_LOCATION, p_282134_, p_283200_, 0.0F, 0.0F, 10, 28, 20, 28);
      } else {
         p_283382_.blit(EXPIRES_SOON_ICON_LOCATION, p_282134_, p_283200_, 10.0F, 0.0F, 10, 28, 20, 28);
      }

      if (p_281673_ >= p_282134_ && p_281673_ <= p_282134_ + 9 && p_282920_ >= p_283200_ && p_282920_ <= p_283200_ + 27 && p_282920_ < this.height - 40 && p_282920_ > 32 && !this.shouldShowPopup()) {
         if (p_282554_ <= 0) {
            this.setTooltipForNextRenderPass(SERVER_EXPIRES_SOON_TOOLTIP);
         } else if (p_282554_ == 1) {
            this.setTooltipForNextRenderPass(SERVER_EXPIRES_IN_DAY_TOOLTIP);
         } else {
            this.setTooltipForNextRenderPass(Component.translatable("mco.selectServer.expires.days", p_282554_));
         }
      }

   }

   void drawOpen(GuiGraphics p_283235_, int p_281895_, int p_283564_, int p_281543_, int p_282977_) {
      p_283235_.blit(ON_ICON_LOCATION, p_281895_, p_283564_, 0.0F, 0.0F, 10, 28, 10, 28);
      if (p_281543_ >= p_281895_ && p_281543_ <= p_281895_ + 9 && p_282977_ >= p_283564_ && p_282977_ <= p_283564_ + 27 && p_282977_ < this.height - 40 && p_282977_ > 32 && !this.shouldShowPopup()) {
         this.setTooltipForNextRenderPass(SERVER_OPEN_TOOLTIP);
      }

   }

   void drawClose(GuiGraphics p_281685_, int p_282388_, int p_282489_, int p_281732_, int p_283445_) {
      p_281685_.blit(OFF_ICON_LOCATION, p_282388_, p_282489_, 0.0F, 0.0F, 10, 28, 10, 28);
      if (p_281732_ >= p_282388_ && p_281732_ <= p_282388_ + 9 && p_283445_ >= p_282489_ && p_283445_ <= p_282489_ + 27 && p_283445_ < this.height - 40 && p_283445_ > 32 && !this.shouldShowPopup()) {
         this.setTooltipForNextRenderPass(SERVER_CLOSED_TOOLTIP);
      }

   }

   void renderNews(GuiGraphics p_282435_, int p_283627_, int p_282268_, boolean p_282717_, int p_282793_, int p_283443_, boolean p_282143_, boolean p_282764_) {
      boolean flag = false;
      if (p_283627_ >= p_282793_ && p_283627_ <= p_282793_ + 20 && p_282268_ >= p_283443_ && p_282268_ <= p_283443_ + 20) {
         flag = true;
      }

      if (!p_282764_) {
         p_282435_.setColor(0.5F, 0.5F, 0.5F, 1.0F);
      }

      boolean flag1 = p_282764_ && p_282143_;
      float f = flag1 ? 20.0F : 0.0F;
      p_282435_.blit(NEWS_LOCATION, p_282793_, p_283443_, f, 0.0F, 20, 20, 40, 20);
      if (flag && p_282764_) {
         this.setTooltipForNextRenderPass(NEWS_TOOLTIP);
      }

      p_282435_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      if (p_282717_ && p_282764_) {
         int i = flag ? 0 : (int)(Math.max(0.0F, Math.max(Mth.sin((float)(10 + this.animTick) * 0.57F), Mth.cos((float)this.animTick * 0.35F))) * -6.0F);
         p_282435_.blit(INVITATION_ICONS_LOCATION, p_282793_ + 10, p_283443_ + 2 + i, 40.0F, 0.0F, 8, 8, 48, 16);
      }

   }

   private void renderLocal(GuiGraphics p_282133_) {
      String s = "LOCAL!";
      p_282133_.pose().pushPose();
      p_282133_.pose().translate((float)(this.width / 2 - 25), 20.0F, 0.0F);
      p_282133_.pose().mulPose(Axis.ZP.rotationDegrees(-20.0F));
      p_282133_.pose().scale(1.5F, 1.5F, 1.5F);
      p_282133_.drawString(this.font, "LOCAL!", 0, 0, 8388479, false);
      p_282133_.pose().popPose();
   }

   private void renderStage(GuiGraphics p_282858_) {
      String s = "STAGE!";
      p_282858_.pose().pushPose();
      p_282858_.pose().translate((float)(this.width / 2 - 25), 20.0F, 0.0F);
      p_282858_.pose().mulPose(Axis.ZP.rotationDegrees(-20.0F));
      p_282858_.pose().scale(1.5F, 1.5F, 1.5F);
      p_282858_.drawString(this.font, "STAGE!", 0, 0, -256, false);
      p_282858_.pose().popPose();
   }

   public RealmsMainScreen newScreen() {
      RealmsMainScreen realmsmainscreen = new RealmsMainScreen(this.lastScreen);
      realmsmainscreen.init(this.minecraft, this.width, this.height);
      return realmsmainscreen;
   }

   public static void updateTeaserImages(ResourceManager p_86407_) {
      Collection<ResourceLocation> collection = p_86407_.listResources("textures/gui/images", (p_193492_) -> {
         return p_193492_.getPath().endsWith(".png");
      }).keySet();
      teaserImages = collection.stream().filter((p_231247_) -> {
         return p_231247_.getNamespace().equals("realms");
      }).toList();
   }

   @OnlyIn(Dist.CLIENT)
   class ButtonEntry extends RealmsMainScreen.Entry {
      private final Button button;
      private final int xPos = RealmsMainScreen.this.width / 2 - 75;

      public ButtonEntry(Button p_275726_) {
         this.button = p_275726_;
      }

      public boolean mouseClicked(double p_275240_, double p_275616_, int p_275528_) {
         this.button.mouseClicked(p_275240_, p_275616_, p_275528_);
         return true;
      }

      public boolean keyPressed(int p_275630_, int p_275328_, int p_275519_) {
         return this.button.keyPressed(p_275630_, p_275328_, p_275519_) ? true : super.keyPressed(p_275630_, p_275328_, p_275519_);
      }

      public void render(GuiGraphics p_283542_, int p_282029_, int p_281480_, int p_281377_, int p_283160_, int p_281920_, int p_283267_, int p_281282_, boolean p_281269_, float p_282372_) {
         this.button.setPosition(this.xPos, p_281480_ + 4);
         this.button.render(p_283542_, p_283267_, p_281282_, p_282372_);
      }

      public Component getNarration() {
         return this.button.getMessage();
      }
   }

   @OnlyIn(Dist.CLIENT)
   class CloseButton extends RealmsMainScreen.CrossButton {
      public CloseButton() {
         super(RealmsMainScreen.this.popupX0() + 4, RealmsMainScreen.this.popupY0() + 4, (p_86775_) -> {
            RealmsMainScreen.this.onClosePopup();
         }, Component.translatable("mco.selectServer.close"));
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class CrossButton extends Button {
      protected CrossButton(Button.OnPress p_275420_, Component p_275193_) {
         this(0, 0, p_275420_, p_275193_);
      }

      protected CrossButton(int p_275644_, int p_275716_, Button.OnPress p_275547_, Component p_275717_) {
         super(p_275644_, p_275716_, 14, 14, p_275717_, p_275547_, DEFAULT_NARRATION);
         this.setTooltip(Tooltip.create(p_275717_));
      }

      public void renderWidget(GuiGraphics p_281814_, int p_281517_, int p_282662_, float p_283217_) {
         float f = this.isHoveredOrFocused() ? 14.0F : 0.0F;
         p_281814_.blit(RealmsMainScreen.CROSS_ICON_LOCATION, this.getX(), this.getY(), 0.0F, f, 14, 14, 14, 28);
      }
   }

   @OnlyIn(Dist.CLIENT)
   abstract class Entry extends ObjectSelectionList.Entry<RealmsMainScreen.Entry> {
      @Nullable
      public RealmsServer getServer() {
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class NewsButton extends Button {
      private static final int SIDE = 20;

      public NewsButton() {
         super(RealmsMainScreen.this.width - 115, 12, 20, 20, Component.translatable("mco.news"), (p_274636_) -> {
            if (RealmsMainScreen.this.newsLink != null) {
               ConfirmLinkScreen.confirmLinkNow(RealmsMainScreen.this.newsLink, RealmsMainScreen.this, true);
               if (RealmsMainScreen.this.hasUnreadNews) {
                  RealmsPersistence.RealmsPersistenceData realmspersistence$realmspersistencedata = RealmsPersistence.readFile();
                  realmspersistence$realmspersistencedata.hasUnreadNews = false;
                  RealmsMainScreen.this.hasUnreadNews = false;
                  RealmsPersistence.writeFile(realmspersistence$realmspersistencedata);
               }

            }
         }, DEFAULT_NARRATION);
      }

      public void renderWidget(GuiGraphics p_281287_, int p_282698_, int p_282096_, float p_283518_) {
         RealmsMainScreen.this.renderNews(p_281287_, p_282698_, p_282096_, RealmsMainScreen.this.hasUnreadNews, this.getX(), this.getY(), this.isHoveredOrFocused(), this.active);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class NotificationMessageEntry extends RealmsMainScreen.Entry {
      private static final int SIDE_MARGINS = 40;
      private static final int ITEM_HEIGHT = 36;
      private static final int OUTLINE_COLOR = -12303292;
      private final Component text;
      private final List<AbstractWidget> children = new ArrayList<>();
      @Nullable
      private final RealmsMainScreen.CrossButton dismissButton;
      private final MultiLineTextWidget textWidget;
      private final GridLayout gridLayout;
      private final FrameLayout textFrame;
      private int lastEntryWidth = -1;

      public NotificationMessageEntry(Component p_275215_, RealmsNotification p_275494_) {
         this.text = p_275215_;
         this.gridLayout = new GridLayout();
         int i = 7;
         this.gridLayout.addChild(new ImageWidget(20, 20, RealmsMainScreen.INFO_ICON_LOCATION), 0, 0, this.gridLayout.newCellSettings().padding(7, 7, 0, 0));
         this.gridLayout.addChild(SpacerElement.width(40), 0, 0);
         this.textFrame = this.gridLayout.addChild(new FrameLayout(0, 9 * 3), 0, 1, this.gridLayout.newCellSettings().paddingTop(7));
         this.textWidget = this.textFrame.addChild((new MultiLineTextWidget(p_275215_, RealmsMainScreen.this.font)).setCentered(true).setMaxRows(3), this.textFrame.newChildLayoutSettings().alignHorizontallyCenter().alignVerticallyTop());
         this.gridLayout.addChild(SpacerElement.width(40), 0, 2);
         if (p_275494_.dismissable()) {
            this.dismissButton = this.gridLayout.addChild(new RealmsMainScreen.CrossButton((p_275478_) -> {
               RealmsMainScreen.this.dismissNotification(p_275494_.uuid());
            }, Component.translatable("mco.notification.dismiss")), 0, 2, this.gridLayout.newCellSettings().alignHorizontallyRight().padding(0, 7, 7, 0));
         } else {
            this.dismissButton = null;
         }

         this.gridLayout.visitWidgets(this.children::add);
      }

      public boolean keyPressed(int p_275646_, int p_275453_, int p_275621_) {
         return this.dismissButton != null && this.dismissButton.keyPressed(p_275646_, p_275453_, p_275621_) ? true : super.keyPressed(p_275646_, p_275453_, p_275621_);
      }

      private void updateEntryWidth(int p_275670_) {
         if (this.lastEntryWidth != p_275670_) {
            this.refreshLayout(p_275670_);
            this.lastEntryWidth = p_275670_;
         }

      }

      private void refreshLayout(int p_275267_) {
         int i = p_275267_ - 80;
         this.textFrame.setMinWidth(i);
         this.textWidget.setMaxWidth(i);
         this.gridLayout.arrangeElements();
      }

      public void renderBack(GuiGraphics p_281374_, int p_282622_, int p_283656_, int p_281830_, int p_281651_, int p_283685_, int p_281784_, int p_282510_, boolean p_283146_, float p_283324_) {
         super.renderBack(p_281374_, p_282622_, p_283656_, p_281830_, p_281651_, p_283685_, p_281784_, p_282510_, p_283146_, p_283324_);
         p_281374_.renderOutline(p_281830_ - 2, p_283656_ - 2, p_281651_, 70, -12303292);
      }

      public void render(GuiGraphics p_281768_, int p_275375_, int p_275358_, int p_275447_, int p_275694_, int p_275477_, int p_275710_, int p_275677_, boolean p_275542_, float p_275323_) {
         this.gridLayout.setPosition(p_275447_, p_275358_);
         this.updateEntryWidth(p_275694_ - 4);
         this.children.forEach((p_280688_) -> {
            p_280688_.render(p_281768_, p_275710_, p_275677_, p_275323_);
         });
      }

      public boolean mouseClicked(double p_275209_, double p_275338_, int p_275560_) {
         if (this.dismissButton != null) {
            this.dismissButton.mouseClicked(p_275209_, p_275338_, p_275560_);
         }

         return true;
      }

      public Component getNarration() {
         return this.text;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class PendingInvitesButton extends ImageButton {
      private static final Component TITLE = Component.translatable("mco.invites.title");
      private static final Tooltip NO_PENDING_INVITES = Tooltip.create(Component.translatable("mco.invites.nopending"));
      private static final Tooltip PENDING_INVITES = Tooltip.create(Component.translatable("mco.invites.pending"));
      private static final int WIDTH = 18;
      private static final int HEIGHT = 15;
      private static final int X_OFFSET = 10;
      private static final int INVITES_WIDTH = 8;
      private static final int INVITES_HEIGHT = 8;
      private static final int INVITES_OFFSET = 11;

      public PendingInvitesButton() {
         super(RealmsMainScreen.this.width / 2 + 64 + 10, 15, 18, 15, 0, 0, 15, RealmsMainScreen.INVITE_ICON_LOCATION, 18, 30, (p_279110_) -> {
            RealmsMainScreen.this.minecraft.setScreen(new RealmsPendingInvitesScreen(RealmsMainScreen.this.lastScreen, TITLE));
         }, TITLE);
         this.setTooltip(NO_PENDING_INVITES);
      }

      public void tick() {
         this.setTooltip(RealmsMainScreen.this.numberOfPendingInvites == 0 ? NO_PENDING_INVITES : PENDING_INVITES);
      }

      public void renderWidget(GuiGraphics p_281409_, int p_282719_, int p_282753_, float p_281312_) {
         super.renderWidget(p_281409_, p_282719_, p_282753_, p_281312_);
         this.drawInvitations(p_281409_);
      }

      private void drawInvitations(GuiGraphics p_282293_) {
         boolean flag = this.active && RealmsMainScreen.this.numberOfPendingInvites != 0;
         if (flag) {
            int i = (Math.min(RealmsMainScreen.this.numberOfPendingInvites, 6) - 1) * 8;
            int j = (int)(Math.max(0.0F, Math.max(Mth.sin((float)(10 + RealmsMainScreen.this.animTick) * 0.57F), Mth.cos((float)RealmsMainScreen.this.animTick * 0.35F))) * -6.0F);
            float f = this.isHoveredOrFocused() ? 8.0F : 0.0F;
            p_282293_.blit(RealmsMainScreen.INVITATION_ICONS_LOCATION, this.getX() + 11, this.getY() + j, (float)i, f, 8, 8, 48, 16);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class RealmSelectionList extends RealmsObjectSelectionList<RealmsMainScreen.Entry> {
      public RealmSelectionList() {
         super(RealmsMainScreen.this.width, RealmsMainScreen.this.height, 44, RealmsMainScreen.this.height - 64, 36);
      }

      public void setSelected(@Nullable RealmsMainScreen.Entry p_86849_) {
         super.setSelected(p_86849_);
         if (p_86849_ != null) {
            RealmsMainScreen.this.updateButtonStates(p_86849_.getServer());
         } else {
            RealmsMainScreen.this.updateButtonStates((RealmsServer)null);
         }

      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public int getRowWidth() {
         return 300;
      }
   }

   @OnlyIn(Dist.CLIENT)
   interface RealmsCall<T> {
      T request(RealmsClient p_275639_) throws RealmsServiceException;
   }

   @OnlyIn(Dist.CLIENT)
   class ServerEntry extends RealmsMainScreen.Entry {
      private static final int SKIN_HEAD_LARGE_WIDTH = 36;
      private final RealmsServer serverData;

      public ServerEntry(RealmsServer p_86856_) {
         this.serverData = p_86856_;
      }

      public void render(GuiGraphics p_283093_, int p_281645_, int p_283047_, int p_283525_, int p_282321_, int p_282391_, int p_281913_, int p_282475_, boolean p_282378_, float p_282843_) {
         this.renderMcoServerItem(this.serverData, p_283093_, p_283525_, p_283047_, p_281913_, p_282475_);
      }

      public boolean mouseClicked(double p_86858_, double p_86859_, int p_86860_) {
         if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
            RealmsMainScreen.this.minecraft.setScreen(new RealmsCreateRealmScreen(this.serverData, RealmsMainScreen.this));
         } else if (RealmsMainScreen.this.shouldPlayButtonBeActive(this.serverData)) {
            if (Util.getMillis() - RealmsMainScreen.this.lastClickTime < 250L && this.isFocused()) {
               RealmsMainScreen.this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
               RealmsMainScreen.this.play(this.serverData, RealmsMainScreen.this);
            }

            RealmsMainScreen.this.lastClickTime = Util.getMillis();
         }

         return true;
      }

      public boolean keyPressed(int p_279120_, int p_279121_, int p_279296_) {
         if (CommonInputs.selected(p_279120_) && RealmsMainScreen.this.shouldPlayButtonBeActive(this.serverData)) {
            RealmsMainScreen.this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            RealmsMainScreen.this.play(this.serverData, RealmsMainScreen.this);
            return true;
         } else {
            return super.keyPressed(p_279120_, p_279121_, p_279296_);
         }
      }

      private void renderMcoServerItem(RealmsServer p_281434_, GuiGraphics p_283436_, int p_282392_, int p_283179_, int p_282272_, int p_281903_) {
         this.renderLegacy(p_281434_, p_283436_, p_282392_ + 36, p_283179_, p_282272_, p_281903_);
      }

      private void renderLegacy(RealmsServer p_282180_, GuiGraphics p_281405_, int p_281795_, int p_282842_, int p_283593_, int p_281798_) {
         if (p_282180_.state == RealmsServer.State.UNINITIALIZED) {
            p_281405_.blit(RealmsMainScreen.WORLDICON_LOCATION, p_281795_ + 10, p_282842_ + 6, 0.0F, 0.0F, 40, 20, 40, 20);
            float f = 0.5F + (1.0F + Mth.sin((float)RealmsMainScreen.this.animTick * 0.25F)) * 0.25F;
            int l = -16777216 | (int)(127.0F * f) << 16 | (int)(255.0F * f) << 8 | (int)(127.0F * f);
            p_281405_.drawCenteredString(RealmsMainScreen.this.font, RealmsMainScreen.SERVER_UNITIALIZED_TEXT, p_281795_ + 10 + 40 + 75, p_282842_ + 12, l);
         } else {
            int i = 225;
            int j = 2;
            this.renderStatusLights(p_282180_, p_281405_, p_281795_, p_282842_, p_283593_, p_281798_, 225, 2);
            if (!"0".equals(p_282180_.serverPing.nrOfPlayers)) {
               String s = ChatFormatting.GRAY + p_282180_.serverPing.nrOfPlayers;
               p_281405_.drawString(RealmsMainScreen.this.font, s, p_281795_ + 207 - RealmsMainScreen.this.font.width(s), p_282842_ + 3, 8421504, false);
               if (p_283593_ >= p_281795_ + 207 - RealmsMainScreen.this.font.width(s) && p_283593_ <= p_281795_ + 207 && p_281798_ >= p_282842_ + 1 && p_281798_ <= p_282842_ + 10 && p_281798_ < RealmsMainScreen.this.height - 40 && p_281798_ > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                  RealmsMainScreen.this.setTooltipForNextRenderPass(Component.literal(p_282180_.serverPing.playerList));
               }
            }

            if (RealmsMainScreen.this.isSelfOwnedServer(p_282180_) && p_282180_.expired) {
               Component component = p_282180_.expiredTrial ? RealmsMainScreen.TRIAL_EXPIRED_TEXT : RealmsMainScreen.SUBSCRIPTION_EXPIRED_TEXT;
               int j1 = p_282842_ + 11 + 5;
               p_281405_.drawString(RealmsMainScreen.this.font, component, p_281795_ + 2, j1 + 1, 15553363, false);
            } else {
               if (p_282180_.worldType == RealmsServer.WorldType.MINIGAME) {
                  int i1 = 13413468;
                  int k = RealmsMainScreen.this.font.width(RealmsMainScreen.SELECT_MINIGAME_PREFIX);
                  p_281405_.drawString(RealmsMainScreen.this.font, RealmsMainScreen.SELECT_MINIGAME_PREFIX, p_281795_ + 2, p_282842_ + 12, 13413468, false);
                  p_281405_.drawString(RealmsMainScreen.this.font, p_282180_.getMinigameName(), p_281795_ + 2 + k, p_282842_ + 12, 7105644, false);
               } else {
                  p_281405_.drawString(RealmsMainScreen.this.font, p_282180_.getDescription(), p_281795_ + 2, p_282842_ + 12, 7105644, false);
               }

               if (!RealmsMainScreen.this.isSelfOwnedServer(p_282180_)) {
                  p_281405_.drawString(RealmsMainScreen.this.font, p_282180_.owner, p_281795_ + 2, p_282842_ + 12 + 11, 5000268, false);
               }
            }

            p_281405_.drawString(RealmsMainScreen.this.font, p_282180_.getName(), p_281795_ + 2, p_282842_ + 1, 16777215, false);
            RealmsUtil.renderPlayerFace(p_281405_, p_281795_ - 36, p_282842_, 32, p_282180_.ownerUUID);
         }
      }

      private void renderStatusLights(RealmsServer p_272798_, GuiGraphics p_283451_, int p_273706_, int p_272591_, int p_273561_, int p_273468_, int p_273073_, int p_273187_) {
         int i = p_273706_ + p_273073_ + 22;
         if (p_272798_.expired) {
            RealmsMainScreen.this.drawExpired(p_283451_, i, p_272591_ + p_273187_, p_273561_, p_273468_);
         } else if (p_272798_.state == RealmsServer.State.CLOSED) {
            RealmsMainScreen.this.drawClose(p_283451_, i, p_272591_ + p_273187_, p_273561_, p_273468_);
         } else if (RealmsMainScreen.this.isSelfOwnedServer(p_272798_) && p_272798_.daysLeft < 7) {
            RealmsMainScreen.this.drawExpiring(p_283451_, i, p_272591_ + p_273187_, p_273561_, p_273468_, p_272798_.daysLeft);
         } else if (p_272798_.state == RealmsServer.State.OPEN) {
            RealmsMainScreen.this.drawOpen(p_283451_, i, p_272591_ + p_273187_, p_273561_, p_273468_);
         }

      }

      public Component getNarration() {
         return (Component)(this.serverData.state == RealmsServer.State.UNINITIALIZED ? RealmsMainScreen.UNITIALIZED_WORLD_NARRATION : Component.translatable("narrator.select", this.serverData.name));
      }

      @Nullable
      public RealmsServer getServer() {
         return this.serverData;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class TrialEntry extends RealmsMainScreen.Entry {
      public void render(GuiGraphics p_282936_, int p_282868_, int p_282346_, int p_281297_, int p_282360_, int p_283241_, int p_282253_, int p_282299_, boolean p_282018_, float p_281364_) {
         this.renderTrialItem(p_282936_, p_282868_, p_281297_, p_282346_, p_282253_, p_282299_);
      }

      public boolean mouseClicked(double p_86910_, double p_86911_, int p_86912_) {
         RealmsMainScreen.this.popupOpenedByUser = true;
         return true;
      }

      private void renderTrialItem(GuiGraphics p_283578_, int p_86915_, int p_86916_, int p_86917_, int p_86918_, int p_86919_) {
         int i = p_86917_ + 8;
         int j = 0;
         boolean flag = false;
         if (p_86916_ <= p_86918_ && p_86918_ <= (int)RealmsMainScreen.this.realmSelectionList.getScrollAmount() && p_86917_ <= p_86919_ && p_86919_ <= p_86917_ + 32) {
            flag = true;
         }

         int k = 8388479;
         if (flag && !RealmsMainScreen.this.shouldShowPopup()) {
            k = 6077788;
         }

         for(Component component : RealmsMainScreen.TRIAL_MESSAGE_LINES) {
            p_283578_.drawCenteredString(RealmsMainScreen.this.font, component, RealmsMainScreen.this.width / 2, i + j, k);
            j += 10;
         }

      }

      public Component getNarration() {
         return RealmsMainScreen.TRIAL_TEXT;
      }
   }
}