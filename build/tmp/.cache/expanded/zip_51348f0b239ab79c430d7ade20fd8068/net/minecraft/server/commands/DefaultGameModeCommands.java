package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class DefaultGameModeCommands {
   public static void register(CommandDispatcher<CommandSourceStack> p_136927_) {
      p_136927_.register(Commands.literal("defaultgamemode").requires((p_136929_) -> {
         return p_136929_.hasPermission(2);
      }).then(Commands.argument("gamemode", GameModeArgument.gameMode()).executes((p_258227_) -> {
         return setMode(p_258227_.getSource(), GameModeArgument.getGameMode(p_258227_, "gamemode"));
      })));
   }

   private static int setMode(CommandSourceStack p_136931_, GameType p_136932_) {
      int i = 0;
      MinecraftServer minecraftserver = p_136931_.getServer();
      minecraftserver.setDefaultGameType(p_136932_);
      GameType gametype = minecraftserver.getForcedGameType();
      if (gametype != null) {
         for(ServerPlayer serverplayer : minecraftserver.getPlayerList().getPlayers()) {
            if (serverplayer.setGameMode(gametype)) {
               ++i;
            }
         }
      }

      p_136931_.sendSuccess(() -> {
         return Component.translatable("commands.defaultgamemode.success", p_136932_.getLongDisplayName());
      }, true);
      return i;
   }
}