package net.minecraft.network.chat;

import com.google.common.primitives.Ints;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.security.SignatureException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureUpdater;
import net.minecraft.util.SignatureValidator;

public record PlayerChatMessage(SignedMessageLink link, @Nullable MessageSignature signature, SignedMessageBody signedBody, @Nullable Component unsignedContent, FilterMask filterMask) {
   public static final MapCodec<PlayerChatMessage> MAP_CODEC = RecordCodecBuilder.mapCodec((p_253460_) -> {
      return p_253460_.group(SignedMessageLink.CODEC.fieldOf("link").forGetter(PlayerChatMessage::link), MessageSignature.CODEC.optionalFieldOf("signature").forGetter((p_253459_) -> {
         return Optional.ofNullable(p_253459_.signature);
      }), SignedMessageBody.MAP_CODEC.forGetter(PlayerChatMessage::signedBody), ExtraCodecs.COMPONENT.optionalFieldOf("unsigned_content").forGetter((p_253458_) -> {
         return Optional.ofNullable(p_253458_.unsignedContent);
      }), FilterMask.CODEC.optionalFieldOf("filter_mask", FilterMask.PASS_THROUGH).forGetter(PlayerChatMessage::filterMask)).apply(p_253460_, (p_253461_, p_253462_, p_253463_, p_253464_, p_253465_) -> {
         return new PlayerChatMessage(p_253461_, p_253462_.orElse((MessageSignature)null), p_253463_, p_253464_.orElse((Component)null), p_253465_);
      });
   });
   private static final UUID SYSTEM_SENDER = Util.NIL_UUID;
   public static final Duration MESSAGE_EXPIRES_AFTER_SERVER = Duration.ofMinutes(5L);
   public static final Duration MESSAGE_EXPIRES_AFTER_CLIENT = MESSAGE_EXPIRES_AFTER_SERVER.plus(Duration.ofMinutes(2L));

   public static PlayerChatMessage system(String p_249209_) {
      return unsigned(SYSTEM_SENDER, p_249209_);
   }

   public static PlayerChatMessage unsigned(UUID p_251783_, String p_251615_) {
      SignedMessageBody signedmessagebody = SignedMessageBody.unsigned(p_251615_);
      SignedMessageLink signedmessagelink = SignedMessageLink.unsigned(p_251783_);
      return new PlayerChatMessage(signedmessagelink, (MessageSignature)null, signedmessagebody, (Component)null, FilterMask.PASS_THROUGH);
   }

   public PlayerChatMessage withUnsignedContent(Component p_242164_) {
      Component component = !p_242164_.equals(Component.literal(this.signedContent())) ? p_242164_ : null;
      return new PlayerChatMessage(this.link, this.signature, this.signedBody, component, this.filterMask);
   }

   public PlayerChatMessage removeUnsignedContent() {
      return this.unsignedContent != null ? new PlayerChatMessage(this.link, this.signature, this.signedBody, (Component)null, this.filterMask) : this;
   }

   public PlayerChatMessage filter(FilterMask p_243320_) {
      return this.filterMask.equals(p_243320_) ? this : new PlayerChatMessage(this.link, this.signature, this.signedBody, this.unsignedContent, p_243320_);
   }

   public PlayerChatMessage filter(boolean p_243223_) {
      return this.filter(p_243223_ ? this.filterMask : FilterMask.PASS_THROUGH);
   }

   public static void updateSignature(SignatureUpdater.Output p_250661_, SignedMessageLink p_248621_, SignedMessageBody p_248823_) throws SignatureException {
      p_250661_.update(Ints.toByteArray(1));
      p_248621_.updateSignature(p_250661_);
      p_248823_.updateSignature(p_250661_);
   }

   public boolean verify(SignatureValidator p_241442_) {
      return this.signature != null && this.signature.verify(p_241442_, (p_249861_) -> {
         updateSignature(p_249861_, this.link, this.signedBody);
      });
   }

   public String signedContent() {
      return this.signedBody.content();
   }

   public Component decoratedContent() {
      return Objects.requireNonNullElseGet(this.unsignedContent, () -> {
         return Component.literal(this.signedContent());
      });
   }

   public Instant timeStamp() {
      return this.signedBody.timeStamp();
   }

   public long salt() {
      return this.signedBody.salt();
   }

   public boolean hasExpiredServer(Instant p_240573_) {
      return p_240573_.isAfter(this.timeStamp().plus(MESSAGE_EXPIRES_AFTER_SERVER));
   }

   public boolean hasExpiredClient(Instant p_240629_) {
      return p_240629_.isAfter(this.timeStamp().plus(MESSAGE_EXPIRES_AFTER_CLIENT));
   }

   public UUID sender() {
      return this.link.sender();
   }

   public boolean isSystem() {
      return this.sender().equals(SYSTEM_SENDER);
   }

   public boolean hasSignature() {
      return this.signature != null;
   }

   public boolean hasSignatureFrom(UUID p_243236_) {
      return this.hasSignature() && this.link.sender().equals(p_243236_);
   }

   public boolean isFullyFiltered() {
      return this.filterMask.isFullyFiltered();
   }
}