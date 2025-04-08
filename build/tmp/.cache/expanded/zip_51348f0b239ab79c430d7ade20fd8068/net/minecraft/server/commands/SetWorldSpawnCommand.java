package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class SetWorldSpawnCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_138661_) {
      p_138661_.register(Commands.literal("setworldspawn").requires((p_138665_) -> {
         return p_138665_.hasPermission(2);
      }).executes((p_274830_) -> {
         return setSpawn(p_274830_.getSource(), BlockPos.containing(p_274830_.getSource().getPosition()), 0.0F);
      }).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_138671_) -> {
         return setSpawn(p_138671_.getSource(), BlockPosArgument.getSpawnablePos(p_138671_, "pos"), 0.0F);
      }).then(Commands.argument("angle", AngleArgument.angle()).executes((p_138663_) -> {
         return setSpawn(p_138663_.getSource(), BlockPosArgument.getSpawnablePos(p_138663_, "pos"), AngleArgument.getAngle(p_138663_, "angle"));
      }))));
   }

   private static int setSpawn(CommandSourceStack p_138667_, BlockPos p_138668_, float p_138669_) {
      p_138667_.getLevel().setDefaultSpawnPos(p_138668_, p_138669_);
      p_138667_.sendSuccess(() -> {
         return Component.translatable("commands.setworldspawn.success", p_138668_.getX(), p_138668_.getY(), p_138668_.getZ(), p_138669_);
      }, true);
      return 1;
   }
}