package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ReturnCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_282091_) {
      p_282091_.register(Commands.literal("return").requires((p_281281_) -> {
         return p_281281_.hasPermission(2);
      }).then(Commands.argument("value", IntegerArgumentType.integer()).executes((p_281464_) -> {
         return setReturn(p_281464_.getSource(), IntegerArgumentType.getInteger(p_281464_, "value"));
      })));
   }

   private static int setReturn(CommandSourceStack p_281858_, int p_281623_) {
      p_281858_.getReturnValueConsumer().accept(p_281623_);
      return p_281623_;
   }
}