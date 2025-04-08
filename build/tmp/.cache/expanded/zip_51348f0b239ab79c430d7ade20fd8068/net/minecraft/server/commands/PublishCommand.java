package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.level.GameType;

public class PublishCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.publish.failed"));
   private static final DynamicCommandExceptionType ERROR_ALREADY_PUBLISHED = new DynamicCommandExceptionType((p_138194_) -> {
      return Component.translatable("commands.publish.alreadyPublished", p_138194_);
   });

   public static void register(CommandDispatcher<CommandSourceStack> p_138185_) {
      p_138185_.register(Commands.literal("publish").requires((p_138189_) -> {
         return p_138189_.hasPermission(4);
      }).executes((p_258235_) -> {
         return publish(p_258235_.getSource(), HttpUtil.getAvailablePort(), false, (GameType)null);
      }).then(Commands.argument("allowCommands", BoolArgumentType.bool()).executes((p_258236_) -> {
         return publish(p_258236_.getSource(), HttpUtil.getAvailablePort(), BoolArgumentType.getBool(p_258236_, "allowCommands"), (GameType)null);
      }).then(Commands.argument("gamemode", GameModeArgument.gameMode()).executes((p_258237_) -> {
         return publish(p_258237_.getSource(), HttpUtil.getAvailablePort(), BoolArgumentType.getBool(p_258237_, "allowCommands"), GameModeArgument.getGameMode(p_258237_, "gamemode"));
      }).then(Commands.argument("port", IntegerArgumentType.integer(0, 65535)).executes((p_258238_) -> {
         return publish(p_258238_.getSource(), IntegerArgumentType.getInteger(p_258238_, "port"), BoolArgumentType.getBool(p_258238_, "allowCommands"), GameModeArgument.getGameMode(p_258238_, "gamemode"));
      })))));
   }

   private static int publish(CommandSourceStack p_260117_, int p_259411_, boolean p_260137_, @Nullable GameType p_259145_) throws CommandSyntaxException {
      if (p_260117_.getServer().isPublished()) {
         throw ERROR_ALREADY_PUBLISHED.create(p_260117_.getServer().getPort());
      } else if (!p_260117_.getServer().publishServer(p_259145_, p_260137_, p_259411_)) {
         throw ERROR_FAILED.create();
      } else {
         p_260117_.sendSuccess(() -> {
            return getSuccessMessage(p_259411_);
         }, true);
         return p_259411_;
      }
   }

   public static MutableComponent getSuccessMessage(int p_259532_) {
      Component component = ComponentUtils.copyOnClickText(String.valueOf(p_259532_));
      return Component.translatable("commands.publish.started", component);
   }
}