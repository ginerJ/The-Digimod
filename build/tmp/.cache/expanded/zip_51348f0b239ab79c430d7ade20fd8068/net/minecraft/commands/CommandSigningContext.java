package net.minecraft.commands;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.network.chat.PlayerChatMessage;

public interface CommandSigningContext {
   CommandSigningContext ANONYMOUS = new CommandSigningContext() {
      @Nullable
      public PlayerChatMessage getArgument(String p_242898_) {
         return null;
      }
   };

   @Nullable
   PlayerChatMessage getArgument(String p_230580_);

   public static record SignedArguments(Map<String, PlayerChatMessage> arguments) implements CommandSigningContext {
      @Nullable
      public PlayerChatMessage getArgument(String p_242852_) {
         return this.arguments.get(p_242852_);
      }
   }
}