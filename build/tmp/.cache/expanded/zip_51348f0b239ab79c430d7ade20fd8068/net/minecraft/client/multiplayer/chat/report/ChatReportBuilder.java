package net.minecraft.client.multiplayer.chat.report;

import com.google.common.collect.Lists;
import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportChatMessage;
import com.mojang.authlib.minecraft.report.ReportEvidence;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.SignedMessageLink;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class ChatReportBuilder {
   private final ChatReportBuilder.ChatReport report;
   private final AbuseReportLimits limits;

   public ChatReportBuilder(ChatReportBuilder.ChatReport p_254092_, AbuseReportLimits p_254265_) {
      this.report = p_254092_;
      this.limits = p_254265_;
   }

   public ChatReportBuilder(UUID p_239528_, AbuseReportLimits p_239529_) {
      this.report = new ChatReportBuilder.ChatReport(UUID.randomUUID(), Instant.now(), p_239528_);
      this.limits = p_239529_;
   }

   public ChatReportBuilder.ChatReport report() {
      return this.report;
   }

   public UUID reportedProfileId() {
      return this.report.reportedProfileId;
   }

   public IntSet reportedMessages() {
      return this.report.reportedMessages;
   }

   public String comments() {
      return this.report.comments;
   }

   public void setComments(String p_239080_) {
      this.report.comments = p_239080_;
   }

   @Nullable
   public ReportReason reason() {
      return this.report.reason;
   }

   public void setReason(ReportReason p_239098_) {
      this.report.reason = p_239098_;
   }

   public void toggleReported(int p_239052_) {
      this.report.toggleReported(p_239052_, this.limits);
   }

   public boolean isReported(int p_243333_) {
      return this.report.reportedMessages.contains(p_243333_);
   }

   public boolean hasContent() {
      return StringUtils.isNotEmpty(this.comments()) || !this.reportedMessages().isEmpty() || this.reason() != null;
   }

   @Nullable
   public ChatReportBuilder.CannotBuildReason checkBuildable() {
      if (this.report.reportedMessages.isEmpty()) {
         return ChatReportBuilder.CannotBuildReason.NO_REPORTED_MESSAGES;
      } else if (this.report.reportedMessages.size() > this.limits.maxReportedMessageCount()) {
         return ChatReportBuilder.CannotBuildReason.TOO_MANY_MESSAGES;
      } else if (this.report.reason == null) {
         return ChatReportBuilder.CannotBuildReason.NO_REASON;
      } else {
         return this.report.comments.length() > this.limits.maxOpinionCommentsLength() ? ChatReportBuilder.CannotBuildReason.COMMENTS_TOO_LONG : null;
      }
   }

   public Either<ChatReportBuilder.Result, ChatReportBuilder.CannotBuildReason> build(ReportingContext p_240129_) {
      ChatReportBuilder.CannotBuildReason chatreportbuilder$cannotbuildreason = this.checkBuildable();
      if (chatreportbuilder$cannotbuildreason != null) {
         return Either.right(chatreportbuilder$cannotbuildreason);
      } else {
         String s = Objects.requireNonNull(this.report.reason).backendName();
         ReportEvidence reportevidence = this.buildEvidence(p_240129_.chatLog());
         ReportedEntity reportedentity = new ReportedEntity(this.report.reportedProfileId);
         AbuseReport abusereport = new AbuseReport(this.report.comments, s, reportevidence, reportedentity, this.report.createdAt);
         return Either.left(new ChatReportBuilder.Result(this.report.reportId, abusereport));
      }
   }

   private ReportEvidence buildEvidence(ChatLog p_239183_) {
      List<ReportChatMessage> list = new ArrayList<>();
      ChatReportContextBuilder chatreportcontextbuilder = new ChatReportContextBuilder(this.limits.leadingContextMessageCount());
      chatreportcontextbuilder.collectAllContext(p_239183_, this.report.reportedMessages, (p_247891_, p_247892_) -> {
         list.add(this.buildReportedChatMessage(p_247892_, this.isReported(p_247891_)));
      });
      return new ReportEvidence(Lists.reverse(list));
   }

   private ReportChatMessage buildReportedChatMessage(LoggedChatMessage.Player p_251321_, boolean p_252182_) {
      SignedMessageLink signedmessagelink = p_251321_.message().link();
      SignedMessageBody signedmessagebody = p_251321_.message().signedBody();
      List<ByteBuffer> list = signedmessagebody.lastSeen().entries().stream().map(MessageSignature::asByteBuffer).toList();
      ByteBuffer bytebuffer = Optionull.map(p_251321_.message().signature(), MessageSignature::asByteBuffer);
      return new ReportChatMessage(signedmessagelink.index(), signedmessagelink.sender(), signedmessagelink.sessionId(), signedmessagebody.timeStamp(), signedmessagebody.salt(), list, signedmessagebody.content(), bytebuffer, p_252182_);
   }

   public ChatReportBuilder copy() {
      return new ChatReportBuilder(this.report.copy(), this.limits);
   }

   @OnlyIn(Dist.CLIENT)
   public static record CannotBuildReason(Component message) {
      public static final ChatReportBuilder.CannotBuildReason NO_REASON = new ChatReportBuilder.CannotBuildReason(Component.translatable("gui.chatReport.send.no_reason"));
      public static final ChatReportBuilder.CannotBuildReason NO_REPORTED_MESSAGES = new ChatReportBuilder.CannotBuildReason(Component.translatable("gui.chatReport.send.no_reported_messages"));
      public static final ChatReportBuilder.CannotBuildReason TOO_MANY_MESSAGES = new ChatReportBuilder.CannotBuildReason(Component.translatable("gui.chatReport.send.too_many_messages"));
      public static final ChatReportBuilder.CannotBuildReason COMMENTS_TOO_LONG = new ChatReportBuilder.CannotBuildReason(Component.translatable("gui.chatReport.send.comments_too_long"));
   }

   @OnlyIn(Dist.CLIENT)
   public class ChatReport {
      final UUID reportId;
      final Instant createdAt;
      final UUID reportedProfileId;
      final IntSet reportedMessages = new IntOpenHashSet();
      String comments = "";
      @Nullable
      ReportReason reason;

      ChatReport(UUID p_254298_, Instant p_253854_, UUID p_253630_) {
         this.reportId = p_254298_;
         this.createdAt = p_253854_;
         this.reportedProfileId = p_253630_;
      }

      public void toggleReported(int p_254375_, AbuseReportLimits p_254456_) {
         if (this.reportedMessages.contains(p_254375_)) {
            this.reportedMessages.remove(p_254375_);
         } else if (this.reportedMessages.size() < p_254456_.maxReportedMessageCount()) {
            this.reportedMessages.add(p_254375_);
         }

      }

      public ChatReportBuilder.ChatReport copy() {
         ChatReportBuilder.ChatReport chatreportbuilder$chatreport = ChatReportBuilder.this.new ChatReport(this.reportId, this.createdAt, this.reportedProfileId);
         chatreportbuilder$chatreport.reportedMessages.addAll(this.reportedMessages);
         chatreportbuilder$chatreport.comments = this.comments;
         chatreportbuilder$chatreport.reason = this.reason;
         return chatreportbuilder$chatreport;
      }

      public boolean isReportedPlayer(UUID p_253762_) {
         return p_253762_.equals(this.reportedProfileId);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static record Result(UUID id, AbuseReport report) {
   }
}