package net.minecraft.client.gui.screens.reporting;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.GenericWaitingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.client.multiplayer.chat.report.ChatReportBuilder;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ThrowingComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChatReportScreen extends Screen {
   private static final int BUTTON_WIDTH = 120;
   private static final int BUTTON_HEIGHT = 20;
   private static final int BUTTON_MARGIN = 20;
   private static final int BUTTON_MARGIN_HALF = 10;
   private static final int LABEL_HEIGHT = 25;
   private static final int SCREEN_WIDTH = 280;
   private static final int SCREEN_HEIGHT = 300;
   private static final Component OBSERVED_WHAT_LABEL = Component.translatable("gui.chatReport.observed_what");
   private static final Component SELECT_REASON = Component.translatable("gui.chatReport.select_reason");
   private static final Component MORE_COMMENTS_LABEL = Component.translatable("gui.chatReport.more_comments");
   private static final Component DESCRIBE_PLACEHOLDER = Component.translatable("gui.chatReport.describe");
   private static final Component REPORT_SENT_MESSAGE = Component.translatable("gui.chatReport.report_sent_msg");
   private static final Component SELECT_CHAT_MESSAGE = Component.translatable("gui.chatReport.select_chat");
   private static final Component REPORT_SENDING_TITLE = Component.translatable("gui.abuseReport.sending.title").withStyle(ChatFormatting.BOLD);
   private static final Component REPORT_SENT_TITLE = Component.translatable("gui.abuseReport.sent.title").withStyle(ChatFormatting.BOLD);
   private static final Component REPORT_ERROR_TITLE = Component.translatable("gui.abuseReport.error.title").withStyle(ChatFormatting.BOLD);
   private static final Component REPORT_SEND_GENERIC_ERROR = Component.translatable("gui.abuseReport.send.generic_error");
   private static final Logger LOGGER = LogUtils.getLogger();
   @Nullable
   final Screen lastScreen;
   private final ReportingContext reportingContext;
   @Nullable
   private MultiLineLabel reasonDescriptionLabel;
   @Nullable
   private MultiLineEditBox commentBox;
   private Button sendButton;
   private ChatReportBuilder reportBuilder;
   @Nullable
   private ChatReportBuilder.CannotBuildReason cannotBuildReason;

   private ChatReportScreen(@Nullable Screen p_253839_, ReportingContext p_254386_, ChatReportBuilder p_254309_) {
      super(Component.translatable("gui.chatReport.title"));
      this.lastScreen = p_253839_;
      this.reportingContext = p_254386_;
      this.reportBuilder = p_254309_;
   }

   public ChatReportScreen(@Nullable Screen p_239116_, ReportingContext p_239117_, UUID p_239118_) {
      this(p_239116_, p_239117_, new ChatReportBuilder(p_239118_, p_239117_.sender().reportLimits()));
   }

   public ChatReportScreen(@Nullable Screen p_254505_, ReportingContext p_254531_, ChatReportBuilder.ChatReport p_253775_) {
      this(p_254505_, p_254531_, new ChatReportBuilder(p_253775_, p_254531_.sender().reportLimits()));
   }

   protected void init() {
      AbuseReportLimits abusereportlimits = this.reportingContext.sender().reportLimits();
      int i = this.width / 2;
      ReportReason reportreason = this.reportBuilder.reason();
      if (reportreason != null) {
         this.reasonDescriptionLabel = MultiLineLabel.create(this.font, reportreason.description(), 280);
      } else {
         this.reasonDescriptionLabel = null;
      }

      IntSet intset = this.reportBuilder.reportedMessages();
      Component component;
      if (intset.isEmpty()) {
         component = SELECT_CHAT_MESSAGE;
      } else {
         component = Component.translatable("gui.chatReport.selected_chat", intset.size());
      }

      this.addRenderableWidget(Button.builder(component, (p_280882_) -> {
         this.minecraft.setScreen(new ChatSelectionScreen(this, this.reportingContext, this.reportBuilder, (p_239697_) -> {
            this.reportBuilder = p_239697_;
            this.onReportChanged();
         }));
      }).bounds(this.contentLeft(), this.selectChatTop(), 280, 20).build());
      Component component1 = Optionull.mapOrDefault(reportreason, ReportReason::title, SELECT_REASON);
      this.addRenderableWidget(Button.builder(component1, (p_280881_) -> {
         this.minecraft.setScreen(new ReportReasonSelectionScreen(this, this.reportBuilder.reason(), (p_239513_) -> {
            this.reportBuilder.setReason(p_239513_);
            this.onReportChanged();
         }));
      }).bounds(this.contentLeft(), this.selectInfoTop(), 280, 20).build());
      this.commentBox = this.addRenderableWidget(new MultiLineEditBox(this.minecraft.font, this.contentLeft(), this.commentBoxTop(), 280, this.commentBoxBottom() - this.commentBoxTop(), DESCRIBE_PLACEHOLDER, Component.translatable("gui.chatReport.comments")));
      this.commentBox.setValue(this.reportBuilder.comments());
      this.commentBox.setCharacterLimit(abusereportlimits.maxOpinionCommentsLength());
      this.commentBox.setValueListener((p_240036_) -> {
         this.reportBuilder.setComments(p_240036_);
         this.onReportChanged();
      });
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (p_239971_) -> {
         this.onClose();
      }).bounds(i - 120, this.completeButtonTop(), 120, 20).build());
      this.sendButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.chatReport.send"), (p_239742_) -> {
         this.sendReport();
      }).bounds(i + 10, this.completeButtonTop(), 120, 20).build());
      this.onReportChanged();
   }

   private void onReportChanged() {
      this.cannotBuildReason = this.reportBuilder.checkBuildable();
      this.sendButton.active = this.cannotBuildReason == null;
      this.sendButton.setTooltip(Optionull.map(this.cannotBuildReason, (p_258134_) -> {
         return Tooltip.create(p_258134_.message());
      }));
   }

   private void sendReport() {
      this.reportBuilder.build(this.reportingContext).ifLeft((p_280883_) -> {
         CompletableFuture<?> completablefuture = this.reportingContext.sender().send(p_280883_.id(), p_280883_.report());
         this.minecraft.setScreen(GenericWaitingScreen.createWaiting(REPORT_SENDING_TITLE, CommonComponents.GUI_CANCEL, () -> {
            this.minecraft.setScreen(this);
            completablefuture.cancel(true);
         }));
         completablefuture.handleAsync((p_240236_, p_240237_) -> {
            if (p_240237_ == null) {
               this.onReportSendSuccess();
            } else {
               if (p_240237_ instanceof CancellationException) {
                  return null;
               }

               this.onReportSendError(p_240237_);
            }

            return null;
         }, this.minecraft);
      }).ifRight((p_242967_) -> {
         this.displayReportSendError(p_242967_.message());
      });
   }

   private void onReportSendSuccess() {
      this.clearDraft();
      this.minecraft.setScreen(GenericWaitingScreen.createCompleted(REPORT_SENT_TITLE, REPORT_SENT_MESSAGE, CommonComponents.GUI_DONE, () -> {
         this.minecraft.setScreen((Screen)null);
      }));
   }

   private void onReportSendError(Throwable p_240314_) {
      LOGGER.error("Encountered error while sending abuse report", p_240314_);
      Throwable throwable = p_240314_.getCause();
      Component component;
      if (throwable instanceof ThrowingComponent throwingcomponent) {
         component = throwingcomponent.getComponent();
      } else {
         component = REPORT_SEND_GENERIC_ERROR;
      }

      this.displayReportSendError(component);
   }

   private void displayReportSendError(Component p_242978_) {
      Component component = p_242978_.copy().withStyle(ChatFormatting.RED);
      this.minecraft.setScreen(GenericWaitingScreen.createCompleted(REPORT_ERROR_TITLE, component, CommonComponents.GUI_BACK, () -> {
         this.minecraft.setScreen(this);
      }));
   }

   void saveDraft() {
      if (this.reportBuilder.hasContent()) {
         this.reportingContext.setChatReportDraft(this.reportBuilder.report().copy());
      }

   }

   void clearDraft() {
      this.reportingContext.setChatReportDraft((ChatReportBuilder.ChatReport)null);
   }

   public void render(GuiGraphics p_283069_, int p_239923_, int p_239924_, float p_239925_) {
      int i = this.width / 2;
      this.renderBackground(p_283069_);
      p_283069_.drawCenteredString(this.font, this.title, i, 10, 16777215);
      p_283069_.drawCenteredString(this.font, OBSERVED_WHAT_LABEL, i, this.selectChatTop() - 9 - 6, 16777215);
      if (this.reasonDescriptionLabel != null) {
         this.reasonDescriptionLabel.renderLeftAligned(p_283069_, this.contentLeft(), this.selectInfoTop() + 20 + 5, 9, 16777215);
      }

      p_283069_.drawString(this.font, MORE_COMMENTS_LABEL, this.contentLeft(), this.commentBoxTop() - 9 - 6, 16777215);
      super.render(p_283069_, p_239923_, p_239924_, p_239925_);
   }

   public void tick() {
      this.commentBox.tick();
      super.tick();
   }

   public void onClose() {
      if (this.reportBuilder.hasContent()) {
         this.minecraft.setScreen(new ChatReportScreen.DiscardReportWarningScreen());
      } else {
         this.minecraft.setScreen(this.lastScreen);
      }

   }

   public void removed() {
      this.saveDraft();
      super.removed();
   }

   public boolean mouseReleased(double p_239350_, double p_239351_, int p_239352_) {
      return super.mouseReleased(p_239350_, p_239351_, p_239352_) ? true : this.commentBox.mouseReleased(p_239350_, p_239351_, p_239352_);
   }

   private int contentLeft() {
      return this.width / 2 - 140;
   }

   private int contentRight() {
      return this.width / 2 + 140;
   }

   private int contentTop() {
      return Math.max((this.height - 300) / 2, 0);
   }

   private int contentBottom() {
      return Math.min((this.height + 300) / 2, this.height);
   }

   private int selectChatTop() {
      return this.contentTop() + 40;
   }

   private int selectInfoTop() {
      return this.selectChatTop() + 10 + 20;
   }

   private int commentBoxTop() {
      int i = this.selectInfoTop() + 20 + 25;
      if (this.reasonDescriptionLabel != null) {
         i += (this.reasonDescriptionLabel.getLineCount() + 1) * 9;
      }

      return i;
   }

   private int commentBoxBottom() {
      return this.completeButtonTop() - 20;
   }

   private int completeButtonTop() {
      return this.contentBottom() - 20 - 10;
   }

   @OnlyIn(Dist.CLIENT)
   class DiscardReportWarningScreen extends WarningScreen {
      private static final Component TITLE = Component.translatable("gui.chatReport.discard.title").withStyle(ChatFormatting.BOLD);
      private static final Component MESSAGE = Component.translatable("gui.chatReport.discard.content");
      private static final Component RETURN = Component.translatable("gui.chatReport.discard.return");
      private static final Component DRAFT = Component.translatable("gui.chatReport.discard.draft");
      private static final Component DISCARD = Component.translatable("gui.chatReport.discard.discard");

      protected DiscardReportWarningScreen() {
         super(TITLE, MESSAGE, MESSAGE);
      }

      protected void initButtons(int p_239753_) {
         int i = 150;
         this.addRenderableWidget(Button.builder(RETURN, (p_239525_) -> {
            this.onClose();
         }).bounds(this.width / 2 - 155, 100 + p_239753_, 150, 20).build());
         this.addRenderableWidget(Button.builder(DRAFT, (p_280885_) -> {
            ChatReportScreen.this.saveDraft();
            this.minecraft.setScreen(ChatReportScreen.this.lastScreen);
         }).bounds(this.width / 2 + 5, 100 + p_239753_, 150, 20).build());
         this.addRenderableWidget(Button.builder(DISCARD, (p_280886_) -> {
            ChatReportScreen.this.clearDraft();
            this.minecraft.setScreen(ChatReportScreen.this.lastScreen);
         }).bounds(this.width / 2 - 75, 130 + p_239753_, 150, 20).build());
      }

      public void onClose() {
         this.minecraft.setScreen(ChatReportScreen.this);
      }

      public boolean shouldCloseOnEsc() {
         return false;
      }

      protected void renderTitle(GuiGraphics p_282506_) {
         p_282506_.drawString(this.font, this.title, this.width / 2 - 155, 30, 16777215);
      }
   }
}