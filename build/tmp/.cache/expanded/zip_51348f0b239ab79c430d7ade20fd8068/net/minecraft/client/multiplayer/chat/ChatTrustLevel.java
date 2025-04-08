package net.minecraft.client.multiplayer.chat;

import com.mojang.serialization.Codec;
import java.time.Instant;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ChatTrustLevel implements StringRepresentable {
   SECURE("secure"),
   MODIFIED("modified"),
   NOT_SECURE("not_secure");

   public static final Codec<ChatTrustLevel> CODEC = StringRepresentable.fromEnum(ChatTrustLevel::values);
   private final String serializedName;

   private ChatTrustLevel(String p_254190_) {
      this.serializedName = p_254190_;
   }

   public static ChatTrustLevel evaluate(PlayerChatMessage p_248663_, Component p_248544_, Instant p_252024_) {
      if (p_248663_.hasSignature() && !p_248663_.hasExpiredClient(p_252024_)) {
         return isModified(p_248663_, p_248544_) ? MODIFIED : SECURE;
      } else {
         return NOT_SECURE;
      }
   }

   private static boolean isModified(PlayerChatMessage p_252093_, Component p_250811_) {
      if (!p_250811_.getString().contains(p_252093_.signedContent())) {
         return true;
      } else {
         Component component = p_252093_.unsignedContent();
         return component == null ? false : containsModifiedStyle(component);
      }
   }

   private static boolean containsModifiedStyle(Component p_251011_) {
      return p_251011_.visit((p_251711_, p_250844_) -> {
         return isModifiedStyle(p_251711_) ? Optional.of(true) : Optional.empty();
      }, Style.EMPTY).orElse(false);
   }

   private static boolean isModifiedStyle(Style p_251347_) {
      return !p_251347_.getFont().equals(Style.DEFAULT_FONT);
   }

   public boolean isNotSecure() {
      return this == NOT_SECURE;
   }

   @Nullable
   public GuiMessageTag createTag(PlayerChatMessage p_240632_) {
      GuiMessageTag guimessagetag;
      switch (this) {
         case MODIFIED:
            guimessagetag = GuiMessageTag.chatModified(p_240632_.signedContent());
            break;
         case NOT_SECURE:
            guimessagetag = GuiMessageTag.chatNotSecure();
            break;
         default:
            guimessagetag = null;
      }

      return guimessagetag;
   }

   public String getSerializedName() {
      return this.serializedName;
   }
}