package net.minecraft.server.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.monster.warden.WardenSpawnTracker;
import net.minecraft.world.entity.player.Player;

public class WardenSpawnTrackerCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_214774_) {
      p_214774_.register(Commands.literal("warden_spawn_tracker").requires((p_214778_) -> {
         return p_214778_.hasPermission(2);
      }).then(Commands.literal("clear").executes((p_214787_) -> {
         return resetTracker(p_214787_.getSource(), ImmutableList.of(p_214787_.getSource().getPlayerOrException()));
      })).then(Commands.literal("set").then(Commands.argument("warning_level", IntegerArgumentType.integer(0, 4)).executes((p_214776_) -> {
         return setWarningLevel(p_214776_.getSource(), ImmutableList.of(p_214776_.getSource().getPlayerOrException()), IntegerArgumentType.getInteger(p_214776_, "warning_level"));
      }))));
   }

   private static int setWarningLevel(CommandSourceStack p_214783_, Collection<? extends Player> p_214784_, int p_214785_) {
      for(Player player : p_214784_) {
         player.getWardenSpawnTracker().ifPresent((p_248188_) -> {
            p_248188_.setWarningLevel(p_214785_);
         });
      }

      if (p_214784_.size() == 1) {
         p_214783_.sendSuccess(() -> {
            return Component.translatable("commands.warden_spawn_tracker.set.success.single", p_214784_.iterator().next().getDisplayName());
         }, true);
      } else {
         p_214783_.sendSuccess(() -> {
            return Component.translatable("commands.warden_spawn_tracker.set.success.multiple", p_214784_.size());
         }, true);
      }

      return p_214784_.size();
   }

   private static int resetTracker(CommandSourceStack p_214780_, Collection<? extends Player> p_214781_) {
      for(Player player : p_214781_) {
         player.getWardenSpawnTracker().ifPresent(WardenSpawnTracker::reset);
      }

      if (p_214781_.size() == 1) {
         p_214780_.sendSuccess(() -> {
            return Component.translatable("commands.warden_spawn_tracker.clear.success.single", p_214781_.iterator().next().getDisplayName());
         }, true);
      } else {
         p_214780_.sendSuccess(() -> {
            return Component.translatable("commands.warden_spawn_tracker.clear.success.multiple", p_214781_.size());
         }, true);
      }

      return p_214781_.size();
   }
}