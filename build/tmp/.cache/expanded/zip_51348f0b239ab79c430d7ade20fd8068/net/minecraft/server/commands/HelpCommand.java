package net.minecraft.server.commands;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class HelpCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.help.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> p_137788_) {
      p_137788_.register(Commands.literal("help").executes((p_288460_) -> {
         Map<CommandNode<CommandSourceStack>, String> map = p_137788_.getSmartUsage(p_137788_.getRoot(), p_288460_.getSource());

         for(String s : map.values()) {
            p_288460_.getSource().sendSuccess(() -> {
               return Component.literal("/" + s);
            }, false);
         }

         return map.size();
      }).then(Commands.argument("command", StringArgumentType.greedyString()).executes((p_288458_) -> {
         ParseResults<CommandSourceStack> parseresults = p_137788_.parse(StringArgumentType.getString(p_288458_, "command"), p_288458_.getSource());
         if (parseresults.getContext().getNodes().isEmpty()) {
            throw ERROR_FAILED.create();
         } else {
            Map<CommandNode<CommandSourceStack>, String> map = p_137788_.getSmartUsage(Iterables.getLast(parseresults.getContext().getNodes()).getNode(), p_288458_.getSource());

            for(String s : map.values()) {
               p_288458_.getSource().sendSuccess(() -> {
                  return Component.literal("/" + parseresults.getReader().getString() + " " + s);
               }, false);
            }

            return map.size();
         }
      })));
   }
}