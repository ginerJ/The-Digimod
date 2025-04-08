package net.minecraft.network.chat;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureUpdater;
import net.minecraft.util.SignatureValidator;

public record MessageSignature(byte[] bytes) {
   public static final Codec<MessageSignature> CODEC = ExtraCodecs.BASE64_STRING.xmap(MessageSignature::new, MessageSignature::bytes);
   public static final int BYTES = 256;

   public MessageSignature {
      Preconditions.checkState(bytes.length == 256, "Invalid message signature size");
   }

   public static MessageSignature read(FriendlyByteBuf p_249837_) {
      byte[] abyte = new byte[256];
      p_249837_.readBytes(abyte);
      return new MessageSignature(abyte);
   }

   public static void write(FriendlyByteBuf p_250642_, MessageSignature p_249714_) {
      p_250642_.writeBytes(p_249714_.bytes);
   }

   public boolean verify(SignatureValidator p_250998_, SignatureUpdater p_249843_) {
      return p_250998_.validate(p_249843_, this.bytes);
   }

   public ByteBuffer asByteBuffer() {
      return ByteBuffer.wrap(this.bytes);
   }

   public boolean equals(Object p_237166_) {
      if (this != p_237166_) {
         if (p_237166_ instanceof MessageSignature) {
            MessageSignature messagesignature = (MessageSignature)p_237166_;
            if (Arrays.equals(this.bytes, messagesignature.bytes)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.bytes);
   }

   public String toString() {
      return Base64.getEncoder().encodeToString(this.bytes);
   }

   public MessageSignature.Packed pack(MessageSignatureCache p_253845_) {
      int i = p_253845_.pack(this);
      return i != -1 ? new MessageSignature.Packed(i) : new MessageSignature.Packed(this);
   }

   public static record Packed(int id, @Nullable MessageSignature fullSignature) {
      public static final int FULL_SIGNATURE = -1;

      public Packed(MessageSignature p_249705_) {
         this(-1, p_249705_);
      }

      public Packed(int p_250015_) {
         this(p_250015_, (MessageSignature)null);
      }

      public static MessageSignature.Packed read(FriendlyByteBuf p_250810_) {
         int i = p_250810_.readVarInt() - 1;
         return i == -1 ? new MessageSignature.Packed(MessageSignature.read(p_250810_)) : new MessageSignature.Packed(i);
      }

      public static void write(FriendlyByteBuf p_251691_, MessageSignature.Packed p_252193_) {
         p_251691_.writeVarInt(p_252193_.id() + 1);
         if (p_252193_.fullSignature() != null) {
            MessageSignature.write(p_251691_, p_252193_.fullSignature());
         }

      }

      public Optional<MessageSignature> unpack(MessageSignatureCache p_254423_) {
         return this.fullSignature != null ? Optional.of(this.fullSignature) : Optional.ofNullable(p_254423_.unpack(this.id));
      }
   }
}