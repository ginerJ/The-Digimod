package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.minecraft.UserApiService;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.reporting.ChatReportScreen;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ReportingContext {
   private static final int LOG_CAPACITY = 1024;
   private final AbuseReportSender sender;
   private final ReportEnvironment environment;
   private final ChatLog chatLog;
   @Nullable
   private ChatReportBuilder.ChatReport chatReportDraft;

   public ReportingContext(AbuseReportSender p_239187_, ReportEnvironment p_239188_, ChatLog p_239189_) {
      this.sender = p_239187_;
      this.environment = p_239188_;
      this.chatLog = p_239189_;
   }

   public static ReportingContext create(ReportEnvironment p_239686_, UserApiService p_239687_) {
      ChatLog chatlog = new ChatLog(1024);
      AbuseReportSender abusereportsender = AbuseReportSender.create(p_239686_, p_239687_);
      return new ReportingContext(abusereportsender, p_239686_, chatlog);
   }

   public void draftReportHandled(Minecraft p_261771_, @Nullable Screen p_261866_, Runnable p_262031_, boolean p_261540_) {
      if (this.chatReportDraft != null) {
         ChatReportBuilder.ChatReport chatreportbuilder$chatreport = this.chatReportDraft.copy();
         p_261771_.setScreen(new ConfirmScreen((p_261387_) -> {
            this.setChatReportDraft((ChatReportBuilder.ChatReport)null);
            if (p_261387_) {
               p_261771_.setScreen(new ChatReportScreen(p_261866_, this, chatreportbuilder$chatreport));
            } else {
               p_262031_.run();
            }

         }, Component.translatable(p_261540_ ? "gui.chatReport.draft.quittotitle.title" : "gui.chatReport.draft.title"), Component.translatable(p_261540_ ? "gui.chatReport.draft.quittotitle.content" : "gui.chatReport.draft.content"), Component.translatable("gui.chatReport.draft.edit"), Component.translatable("gui.chatReport.draft.discard")));
      } else {
         p_262031_.run();
      }

   }

   public AbuseReportSender sender() {
      return this.sender;
   }

   public ChatLog chatLog() {
      return this.chatLog;
   }

   public boolean matches(ReportEnvironment p_239734_) {
      return Objects.equals(this.environment, p_239734_);
   }

   public void setChatReportDraft(@Nullable ChatReportBuilder.ChatReport p_254293_) {
      this.chatReportDraft = p_254293_;
   }

   public boolean hasDraftReport() {
      return this.chatReportDraft != null;
   }

   public boolean hasDraftReportFor(UUID p_254340_) {
      return this.hasDraftReport() && this.chatReportDraft.isReportedPlayer(p_254340_);
   }
}