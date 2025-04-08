package net.minecraft.network.chat;

import javax.annotation.Nullable;
import net.minecraft.util.SignatureValidator;

@FunctionalInterface
public interface SignedMessageValidator {
   SignedMessageValidator ACCEPT_UNSIGNED = (p_252109_) -> {
      return !p_252109_.hasSignature();
   };
   SignedMessageValidator REJECT_ALL = (p_251793_) -> {
      return false;
   };

   boolean updateAndValidate(PlayerChatMessage p_251036_);

   public static class KeyBased implements SignedMessageValidator {
      private final SignatureValidator validator;
      @Nullable
      private PlayerChatMessage lastMessage;
      private boolean isChainValid = true;

      public KeyBased(SignatureValidator p_241517_) {
         this.validator = p_241517_;
      }

      private boolean validateChain(PlayerChatMessage p_250412_) {
         if (p_250412_.equals(this.lastMessage)) {
            return true;
         } else {
            return this.lastMessage == null || p_250412_.link().isDescendantOf(this.lastMessage.link());
         }
      }

      public boolean updateAndValidate(PlayerChatMessage p_251182_) {
         this.isChainValid = this.isChainValid && p_251182_.verify(this.validator) && this.validateChain(p_251182_);
         if (!this.isChainValid) {
            return false;
         } else {
            this.lastMessage = p_251182_;
            return true;
         }
      }
   }
}