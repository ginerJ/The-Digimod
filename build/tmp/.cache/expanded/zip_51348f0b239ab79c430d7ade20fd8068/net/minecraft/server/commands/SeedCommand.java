package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;

public class SeedCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_138590_, boolean p_138591_) {
      p_138590_.register(Commands.literal("seed").requires((p_138596_) -> {
         return !p_138591_ || p_138596_.hasPermission(2);
      }).executes((p_288608_) -> {
         long i = p_288608_.getSource().getLevel().getSeed();
         Component component = ComponentUtils.copyOnClickText(String.valueOf(i));
         p_288608_.getSource().sendSuccess(() -> {
            return Component.translatable("commands.seed.success", component);
         }, false);
         return (int)i;
      }));
   }
}