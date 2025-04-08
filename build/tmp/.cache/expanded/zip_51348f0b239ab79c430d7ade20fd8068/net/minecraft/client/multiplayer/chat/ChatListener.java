package net.minecraft.client.multiplayer.chat;

import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import java.time.Instant;
import java.util.Deque;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.util.StringDecomposer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class ChatListener {
   private final Minecraft minecraft;
   private final Deque<ChatListener.Message> delayedMessageQueue = Queues.newArrayDeque();
   private long messageDelay;
   private long previousMessageTime;

   public ChatListener(Minecraft p_240569_) {
      this.minecraft = p_240569_;
   }

   public void tick() {
      if (this.messageDelay != 0L) {
         if (Util.getMillis() >= this.previousMessageTime + this.messageDelay) {
            for(ChatListener.Message chatlistener$message = this.delayedMessageQueue.poll(); chatlistener$message != null && !chatlistener$message.accept(); chatlistener$message = this.delayedMessageQueue.poll()) {
            }
         }

      }
   }

   public void setMessageDelay(double p_240785_) {
      long i = (long)(p_240785_ * 1000.0D);
      if (i == 0L && this.messageDelay > 0L) {
         this.delayedMessageQueue.forEach(ChatListener.Message::accept);
         this.delayedMessageQueue.clear();
      }

      this.messageDelay = i;
   }

   public void acceptNextDelayedMessage() {
      this.delayedMessageQueue.remove().accept();
   }

   public long queueSize() {
      return (long)this.delayedMessageQueue.size();
   }

   public void clearQueue() {
      this.delayedMessageQueue.forEach(ChatListener.Message::accept);
      this.delayedMessageQueue.clear();
   }

   public boolean removeFromDelayedMessageQueue(MessageSignature p_241445_) {
      return this.delayedMessageQueue.removeIf((p_247887_) -> {
         return p_241445_.equals(p_247887_.signature());
      });
   }

   private boolean willDelayMessages() {
      return this.messageDelay > 0L && Util.getMillis() < this.previousMessageTime + this.messageDelay;
   }

   private void handleMessage(@Nullable MessageSignature p_249408_, BooleanSupplier p_250870_) {
      if (this.willDelayMessages()) {
         this.delayedMessageQueue.add(new ChatListener.Message(p_249408_, p_250870_));
      } else {
         p_250870_.getAsBoolean();
      }

   }

   public void handlePlayerChatMessage(PlayerChatMessage p_251553_, GameProfile p_250022_, ChatType.Bound p_252158_) {
      boolean flag = this.minecraft.options.onlyShowSecureChat().get();
      PlayerChatMessage playerchatmessage = flag ? p_251553_.removeUnsignedContent() : p_251553_;
      Component component = p_252158_.decorate(playerchatmessage.decoratedContent());
      Instant instant = Instant.now();
      this.handleMessage(p_251553_.signature(), () -> {
         boolean flag1 = this.showMessageToPlayer(p_252158_, p_251553_, component, p_250022_, flag, instant);
         ClientPacketListener clientpacketlistener = this.minecraft.getConnection();
         if (clientpacketlistener != null) {
            clientpacketlistener.markMessageAsProcessed(p_251553_, flag1);
         }

         return flag1;
      });
   }

   public void handleDisguisedChatMessage(Component p_250375_, ChatType.Bound p_251256_) {
      Instant instant = Instant.now();
      this.handleMessage((MessageSignature)null, () -> {
         Component component = p_251256_.decorate(p_250375_);
         Component forgeComponent = net.minecraftforge.client.ForgeHooksClient.onClientChat(p_251256_, component, Util.NIL_UUID);
         if (forgeComponent == null) return false;
         this.minecraft.gui.getChat().addMessage(forgeComponent);
         this.narrateChatMessage(p_251256_, p_250375_);
         this.logSystemMessage(component, instant);
         this.previousMessageTime = Util.getMillis();
         return true;
      });
   }

   private boolean showMessageToPlayer(ChatType.Bound p_251766_, PlayerChatMessage p_249430_, Component p_249231_, GameProfile p_249177_, boolean p_251638_, Instant p_249665_) {
      ChatTrustLevel chattrustlevel = this.evaluateTrustLevel(p_249430_, p_249231_, p_249665_);
      if (p_251638_ && chattrustlevel.isNotSecure()) {
         return false;
      } else if (!this.minecraft.isBlocked(p_249430_.sender()) && !p_249430_.isFullyFiltered()) {
         GuiMessageTag guimessagetag = chattrustlevel.createTag(p_249430_);
         MessageSignature messagesignature = p_249430_.signature();
         FilterMask filtermask = p_249430_.filterMask();
         if (filtermask.isEmpty()) {
            Component forgeComponent = net.minecraftforge.client.ForgeHooksClient.onClientPlayerChat(p_251766_, p_249231_, p_249430_, p_249430_.sender());
            if (forgeComponent == null) return false;
            this.minecraft.gui.getChat().addMessage(forgeComponent, messagesignature, guimessagetag);
            this.narrateChatMessage(p_251766_, p_249430_.decoratedContent());
         } else {
            Component component = filtermask.applyWithFormatting(p_249430_.signedContent());
            if (component != null) {
               Component forgeComponent = net.minecraftforge.client.ForgeHooksClient.onClientPlayerChat(p_251766_, p_251766_.decorate(component), p_249430_, p_249430_.sender());
               if (forgeComponent == null) return false;
               this.minecraft.gui.getChat().addMessage(forgeComponent, messagesignature, guimessagetag);
               this.narrateChatMessage(p_251766_, component);
            }
         }

         this.logPlayerMessage(p_249430_, p_251766_, p_249177_, chattrustlevel);
         this.previousMessageTime = Util.getMillis();
         return true;
      } else {
         return false;
      }
   }

   private void narrateChatMessage(ChatType.Bound p_241352_, Component p_243262_) {
      this.minecraft.getNarrator().sayChat(p_241352_.decorateNarration(p_243262_));
   }

   private ChatTrustLevel evaluateTrustLevel(PlayerChatMessage p_251246_, Component p_250576_, Instant p_249995_) {
      return this.isSenderLocalPlayer(p_251246_.sender()) ? ChatTrustLevel.SECURE : ChatTrustLevel.evaluate(p_251246_, p_250576_, p_249995_);
   }

   private void logPlayerMessage(PlayerChatMessage p_252155_, ChatType.Bound p_249730_, GameProfile p_248589_, ChatTrustLevel p_248881_) {
      ChatLog chatlog = this.minecraft.getReportingContext().chatLog();
      chatlog.push(LoggedChatMessage.player(p_248589_, p_252155_, p_248881_));
   }

   private void logSystemMessage(Component p_240609_, Instant p_240541_) {
      ChatLog chatlog = this.minecraft.getReportingContext().chatLog();
      chatlog.push(LoggedChatMessage.system(p_240609_, p_240541_));
   }

   public void handleSystemMessage(Component p_240522_, boolean p_240642_) {
      if (!this.minecraft.options.hideMatchedNames().get() || !this.minecraft.isBlocked(this.guessChatUUID(p_240522_))) {
         p_240522_ = net.minecraftforge.client.ForgeHooksClient.onClientSystemChat(p_240522_, p_240642_);
         if (p_240522_ == null) return;
         if (p_240642_) {
            this.minecraft.gui.setOverlayMessage(p_240522_, false);
         } else {
            this.minecraft.gui.getChat().addMessage(p_240522_);
            this.logSystemMessage(p_240522_, Instant.now());
         }

         this.minecraft.getNarrator().say(p_240522_);
      }
   }

   private UUID guessChatUUID(Component p_240595_) {
      String s = StringDecomposer.getPlainText(p_240595_);
      String s1 = StringUtils.substringBetween(s, "<", ">");
      return s1 == null ? Util.NIL_UUID : this.minecraft.getPlayerSocialManager().getDiscoveredUUID(s1);
   }

   private boolean isSenderLocalPlayer(UUID p_241343_) {
      if (this.minecraft.isLocalServer() && this.minecraft.player != null) {
         UUID uuid = this.minecraft.player.getGameProfile().getId();
         return uuid.equals(p_241343_);
      } else {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static record Message(@Nullable MessageSignature signature, BooleanSupplier handler) {
      public boolean accept() {
         return this.handler.getAsBoolean();
      }
   }
}
