package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.players.PlayerList;

public class SayCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_138410_) {
      p_138410_.register(Commands.literal("say").requires((p_138414_) -> {
         return p_138414_.hasPermission(2);
      }).then(Commands.argument("message", MessageArgument.message()).executes((p_248171_) -> {
         MessageArgument.resolveChatMessage(p_248171_, "message", (p_248170_) -> {
            CommandSourceStack commandsourcestack = p_248171_.getSource();
            PlayerList playerlist = commandsourcestack.getServer().getPlayerList();
            playerlist.broadcastChatMessage(p_248170_, commandsourcestack, ChatType.bind(ChatType.SAY_COMMAND, commandsourcestack));
         });
         return 1;
      })));
   }
}