package net.minecraft.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.gametest.framework.TestCommand;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.AdvancementCommands;
import net.minecraft.server.commands.AttributeCommand;
import net.minecraft.server.commands.BanIpCommands;
import net.minecraft.server.commands.BanListCommands;
import net.minecraft.server.commands.BanPlayerCommands;
import net.minecraft.server.commands.BossBarCommands;
import net.minecraft.server.commands.ClearInventoryCommands;
import net.minecraft.server.commands.CloneCommands;
import net.minecraft.server.commands.DamageCommand;
import net.minecraft.server.commands.DataPackCommand;
import net.minecraft.server.commands.DeOpCommands;
import net.minecraft.server.commands.DebugCommand;
import net.minecraft.server.commands.DefaultGameModeCommands;
import net.minecraft.server.commands.DifficultyCommand;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.server.commands.EmoteCommands;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.server.commands.ExperienceCommand;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.commands.FillCommand;
import net.minecraft.server.commands.ForceLoadCommand;
import net.minecraft.server.commands.FunctionCommand;
import net.minecraft.server.commands.GameModeCommand;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.commands.HelpCommand;
import net.minecraft.server.commands.ItemCommands;
import net.minecraft.server.commands.JfrCommand;
import net.minecraft.server.commands.KickCommand;
import net.minecraft.server.commands.KillCommand;
import net.minecraft.server.commands.ListPlayersCommand;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.commands.LootCommand;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.commands.OpCommand;
import net.minecraft.server.commands.PardonCommand;
import net.minecraft.server.commands.PardonIpCommand;
import net.minecraft.server.commands.ParticleCommand;
import net.minecraft.server.commands.PerfCommand;
import net.minecraft.server.commands.PlaceCommand;
import net.minecraft.server.commands.PlaySoundCommand;
import net.minecraft.server.commands.PublishCommand;
import net.minecraft.server.commands.RecipeCommand;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.commands.ReturnCommand;
import net.minecraft.server.commands.RideCommand;
import net.minecraft.server.commands.SaveAllCommand;
import net.minecraft.server.commands.SaveOffCommand;
import net.minecraft.server.commands.SaveOnCommand;
import net.minecraft.server.commands.SayCommand;
import net.minecraft.server.commands.ScheduleCommand;
import net.minecraft.server.commands.ScoreboardCommand;
import net.minecraft.server.commands.SeedCommand;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.commands.SetPlayerIdleTimeoutCommand;
import net.minecraft.server.commands.SetSpawnCommand;
import net.minecraft.server.commands.SetWorldSpawnCommand;
import net.minecraft.server.commands.SpawnArmorTrimsCommand;
import net.minecraft.server.commands.SpectateCommand;
import net.minecraft.server.commands.SpreadPlayersCommand;
import net.minecraft.server.commands.StopCommand;
import net.minecraft.server.commands.StopSoundCommand;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.commands.TagCommand;
import net.minecraft.server.commands.TeamCommand;
import net.minecraft.server.commands.TeamMsgCommand;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.commands.TellRawCommand;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.server.commands.TitleCommand;
import net.minecraft.server.commands.TriggerCommand;
import net.minecraft.server.commands.WeatherCommand;
import net.minecraft.server.commands.WhitelistCommand;
import net.minecraft.server.commands.WorldBorderCommand;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class Commands {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int LEVEL_ALL = 0;
   public static final int LEVEL_MODERATORS = 1;
   public static final int LEVEL_GAMEMASTERS = 2;
   public static final int LEVEL_ADMINS = 3;
   public static final int LEVEL_OWNERS = 4;
   private final CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();

   public Commands(Commands.CommandSelection p_230943_, CommandBuildContext p_230944_) {
      AdvancementCommands.register(this.dispatcher);
      AttributeCommand.register(this.dispatcher, p_230944_);
      ExecuteCommand.register(this.dispatcher, p_230944_);
      BossBarCommands.register(this.dispatcher);
      ClearInventoryCommands.register(this.dispatcher, p_230944_);
      CloneCommands.register(this.dispatcher, p_230944_);
      DamageCommand.register(this.dispatcher, p_230944_);
      DataCommands.register(this.dispatcher);
      DataPackCommand.register(this.dispatcher);
      DebugCommand.register(this.dispatcher);
      DefaultGameModeCommands.register(this.dispatcher);
      DifficultyCommand.register(this.dispatcher);
      EffectCommands.register(this.dispatcher, p_230944_);
      EmoteCommands.register(this.dispatcher);
      EnchantCommand.register(this.dispatcher, p_230944_);
      ExperienceCommand.register(this.dispatcher);
      FillCommand.register(this.dispatcher, p_230944_);
      FillBiomeCommand.register(this.dispatcher, p_230944_);
      ForceLoadCommand.register(this.dispatcher);
      FunctionCommand.register(this.dispatcher);
      GameModeCommand.register(this.dispatcher);
      GameRuleCommand.register(this.dispatcher);
      GiveCommand.register(this.dispatcher, p_230944_);
      HelpCommand.register(this.dispatcher);
      ItemCommands.register(this.dispatcher, p_230944_);
      KickCommand.register(this.dispatcher);
      KillCommand.register(this.dispatcher);
      ListPlayersCommand.register(this.dispatcher);
      LocateCommand.register(this.dispatcher, p_230944_);
      LootCommand.register(this.dispatcher, p_230944_);
      MsgCommand.register(this.dispatcher);
      ParticleCommand.register(this.dispatcher, p_230944_);
      PlaceCommand.register(this.dispatcher);
      PlaySoundCommand.register(this.dispatcher);
      ReloadCommand.register(this.dispatcher);
      RecipeCommand.register(this.dispatcher);
      ReturnCommand.register(this.dispatcher);
      RideCommand.register(this.dispatcher);
      SayCommand.register(this.dispatcher);
      ScheduleCommand.register(this.dispatcher);
      ScoreboardCommand.register(this.dispatcher);
      SeedCommand.register(this.dispatcher, p_230943_ != Commands.CommandSelection.INTEGRATED);
      SetBlockCommand.register(this.dispatcher, p_230944_);
      SetSpawnCommand.register(this.dispatcher);
      SetWorldSpawnCommand.register(this.dispatcher);
      SpectateCommand.register(this.dispatcher);
      SpreadPlayersCommand.register(this.dispatcher);
      StopSoundCommand.register(this.dispatcher);
      SummonCommand.register(this.dispatcher, p_230944_);
      TagCommand.register(this.dispatcher);
      TeamCommand.register(this.dispatcher);
      TeamMsgCommand.register(this.dispatcher);
      TeleportCommand.register(this.dispatcher);
      TellRawCommand.register(this.dispatcher);
      TimeCommand.register(this.dispatcher);
      TitleCommand.register(this.dispatcher);
      TriggerCommand.register(this.dispatcher);
      WeatherCommand.register(this.dispatcher);
      WorldBorderCommand.register(this.dispatcher);
      if (JvmProfiler.INSTANCE.isAvailable()) {
         JfrCommand.register(this.dispatcher);
      }

      if (net.minecraftforge.gametest.ForgeGameTestHooks.isGametestEnabled()) {
         TestCommand.register(this.dispatcher);
         SpawnArmorTrimsCommand.register(this.dispatcher);
      }

      if (p_230943_.includeDedicated) {
         BanIpCommands.register(this.dispatcher);
         BanListCommands.register(this.dispatcher);
         BanPlayerCommands.register(this.dispatcher);
         DeOpCommands.register(this.dispatcher);
         OpCommand.register(this.dispatcher);
         PardonCommand.register(this.dispatcher);
         PardonIpCommand.register(this.dispatcher);
         PerfCommand.register(this.dispatcher);
         SaveAllCommand.register(this.dispatcher);
         SaveOffCommand.register(this.dispatcher);
         SaveOnCommand.register(this.dispatcher);
         SetPlayerIdleTimeoutCommand.register(this.dispatcher);
         StopCommand.register(this.dispatcher);
         WhitelistCommand.register(this.dispatcher);
      }

      if (p_230943_.includeIntegrated) {
         PublishCommand.register(this.dispatcher);
      }
      net.minecraftforge.event.ForgeEventFactory.onCommandRegister(this.dispatcher, p_230943_, p_230944_);

      this.dispatcher.setConsumer((p_230954_, p_230955_, p_230956_) -> {
         p_230954_.getSource().onCommandComplete(p_230954_, p_230955_, p_230956_);
         p_230954_.getSource().onCommandComplete(p_230954_, p_230955_, p_230956_);
      });
   }

   public static <S> ParseResults<S> mapSource(ParseResults<S> p_242928_, UnaryOperator<S> p_242890_) {
      CommandContextBuilder<S> commandcontextbuilder = p_242928_.getContext();
      CommandContextBuilder<S> commandcontextbuilder1 = commandcontextbuilder.withSource(p_242890_.apply(commandcontextbuilder.getSource()));
      return new ParseResults<>(commandcontextbuilder1, p_242928_.getReader(), p_242928_.getExceptions());
   }

   public int performPrefixedCommand(CommandSourceStack p_230958_, String p_230959_) {
      p_230959_ = p_230959_.startsWith("/") ? p_230959_.substring(1) : p_230959_;
      return this.performCommand(this.dispatcher.parse(p_230959_, p_230958_), p_230959_);
   }

   public int performCommand(ParseResults<CommandSourceStack> p_242844_, String p_242841_) {
      CommandSourceStack commandsourcestack = p_242844_.getContext().getSource();
      commandsourcestack.getServer().getProfiler().push(() -> {
         return "/" + p_242841_;
      });

      try {
         try {
            net.minecraftforge.event.CommandEvent event = new net.minecraftforge.event.CommandEvent(p_242844_);
            if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
               if (event.getException() instanceof Exception exception) {
                  throw exception;
               } else if (event.getException() != null) {
                  com.google.common.base.Throwables.throwIfUnchecked(event.getException());
               }
               return 1;
            }
            return this.dispatcher.execute(event.getParseResults());
         } catch (CommandRuntimeException commandruntimeexception) {
            commandsourcestack.sendFailure(commandruntimeexception.getComponent());
            return 0;
         } catch (CommandSyntaxException commandsyntaxexception) {
            commandsourcestack.sendFailure(ComponentUtils.fromMessage(commandsyntaxexception.getRawMessage()));
            if (commandsyntaxexception.getInput() != null && commandsyntaxexception.getCursor() >= 0) {
               int j = Math.min(commandsyntaxexception.getInput().length(), commandsyntaxexception.getCursor());
               MutableComponent mutablecomponent1 = Component.empty().withStyle(ChatFormatting.GRAY).withStyle((p_82134_) -> {
                  return p_82134_.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + p_242841_));
               });
               if (j > 10) {
                  mutablecomponent1.append(CommonComponents.ELLIPSIS);
               }

               mutablecomponent1.append(commandsyntaxexception.getInput().substring(Math.max(0, j - 10), j));
               if (j < commandsyntaxexception.getInput().length()) {
                  Component component = Component.literal(commandsyntaxexception.getInput().substring(j)).withStyle(ChatFormatting.RED, ChatFormatting.UNDERLINE);
                  mutablecomponent1.append(component);
               }

               mutablecomponent1.append(Component.translatable("command.context.here").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
               commandsourcestack.sendFailure(mutablecomponent1);
            }
         } catch (Exception exception) {
            MutableComponent mutablecomponent = Component.literal(exception.getMessage() == null ? exception.getClass().getName() : exception.getMessage());
            if (LOGGER.isDebugEnabled()) {
               LOGGER.error("Command exception: /{}", p_242841_, exception);
               StackTraceElement[] astacktraceelement = exception.getStackTrace();

               for(int i = 0; i < Math.min(astacktraceelement.length, 3); ++i) {
                  mutablecomponent.append("\n\n").append(astacktraceelement[i].getMethodName()).append("\n ").append(astacktraceelement[i].getFileName()).append(":").append(String.valueOf(astacktraceelement[i].getLineNumber()));
               }
            }

            commandsourcestack.sendFailure(Component.translatable("command.failed").withStyle((p_82137_) -> {
               return p_82137_.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, mutablecomponent));
            }));
            if (SharedConstants.IS_RUNNING_IN_IDE) {
               commandsourcestack.sendFailure(Component.literal(Util.describeError(exception)));
               LOGGER.error("'/{}' threw an exception", p_242841_, exception);
            }

            return 0;
         }

         return 0;
      } finally {
         commandsourcestack.getServer().getProfiler().pop();
      }
   }

   public void sendCommands(ServerPlayer p_82096_) {
      Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> map = Maps.newHashMap();
      RootCommandNode<SharedSuggestionProvider> rootcommandnode = new RootCommandNode<>();
      map.put(this.dispatcher.getRoot(), rootcommandnode);
      // FORGE: Use our own command node merging method to handle redirect nodes properly, see issue #7551
      net.minecraftforge.server.command.CommandHelper.mergeCommandNode(this.dispatcher.getRoot(), rootcommandnode, map, p_82096_.createCommandSourceStack(), ctx -> 0, suggest -> SuggestionProviders.safelySwap((com.mojang.brigadier.suggestion.SuggestionProvider<SharedSuggestionProvider>) (com.mojang.brigadier.suggestion.SuggestionProvider<?>) suggest));
      p_82096_.connection.send(new ClientboundCommandsPacket(rootcommandnode));
   }

   private void fillUsableCommands(CommandNode<CommandSourceStack> p_82113_, CommandNode<SharedSuggestionProvider> p_82114_, CommandSourceStack p_82115_, Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> p_82116_) {
      for(CommandNode<CommandSourceStack> commandnode : p_82113_.getChildren()) {
         if (commandnode.canUse(p_82115_)) {
            ArgumentBuilder<SharedSuggestionProvider, ?> argumentbuilder = (ArgumentBuilder)commandnode.createBuilder();
            argumentbuilder.requires((p_82126_) -> {
               return true;
            });
            if (argumentbuilder.getCommand() != null) {
               argumentbuilder.executes((p_82102_) -> {
                  return 0;
               });
            }

            if (argumentbuilder instanceof RequiredArgumentBuilder) {
               RequiredArgumentBuilder<SharedSuggestionProvider, ?> requiredargumentbuilder = (RequiredArgumentBuilder)argumentbuilder;
               if (requiredargumentbuilder.getSuggestionsProvider() != null) {
                  requiredargumentbuilder.suggests(SuggestionProviders.safelySwap(requiredargumentbuilder.getSuggestionsProvider()));
               }
            }

            if (argumentbuilder.getRedirect() != null) {
               argumentbuilder.redirect(p_82116_.get(argumentbuilder.getRedirect()));
            }

            CommandNode<SharedSuggestionProvider> commandnode1 = argumentbuilder.build();
            p_82116_.put(commandnode, commandnode1);
            p_82114_.addChild(commandnode1);
            if (!commandnode.getChildren().isEmpty()) {
               this.fillUsableCommands(commandnode, commandnode1, p_82115_, p_82116_);
            }
         }
      }

   }

   public static LiteralArgumentBuilder<CommandSourceStack> literal(String p_82128_) {
      return LiteralArgumentBuilder.literal(p_82128_);
   }

   public static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String p_82130_, ArgumentType<T> p_82131_) {
      return RequiredArgumentBuilder.argument(p_82130_, p_82131_);
   }

   public static Predicate<String> createValidator(Commands.ParseFunction p_82121_) {
      return (p_82124_) -> {
         try {
            p_82121_.parse(new StringReader(p_82124_));
            return true;
         } catch (CommandSyntaxException commandsyntaxexception) {
            return false;
         }
      };
   }

   public CommandDispatcher<CommandSourceStack> getDispatcher() {
      return this.dispatcher;
   }

   @Nullable
   public static <S> CommandSyntaxException getParseException(ParseResults<S> p_82098_) {
      if (!p_82098_.getReader().canRead()) {
         return null;
      } else if (p_82098_.getExceptions().size() == 1) {
         return p_82098_.getExceptions().values().iterator().next();
      } else {
         return p_82098_.getContext().getRange().isEmpty() ? CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(p_82098_.getReader()) : CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(p_82098_.getReader());
      }
   }

   public static CommandBuildContext createValidationContext(final HolderLookup.Provider p_256243_) {
      return new CommandBuildContext() {
         public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> p_256244_) {
            final HolderLookup.RegistryLookup<T> registrylookup = p_256243_.lookupOrThrow(p_256244_);
            return new HolderLookup.Delegate<T>(registrylookup) {
               public Optional<HolderSet.Named<T>> get(TagKey<T> p_255936_) {
                  return Optional.of(this.getOrThrow(p_255936_));
               }

               public HolderSet.Named<T> getOrThrow(TagKey<T> p_255953_) {
                  Optional<HolderSet.Named<T>> optional = registrylookup.get(p_255953_);
                  return optional.orElseGet(() -> {
                     return HolderSet.emptyNamed(registrylookup, p_255953_);
                  });
               }
            };
         }
      };
   }

   public static void validate() {
      CommandBuildContext commandbuildcontext = createValidationContext(VanillaRegistries.createLookup());
      CommandDispatcher<CommandSourceStack> commanddispatcher = (new Commands(Commands.CommandSelection.ALL, commandbuildcontext)).getDispatcher();
      RootCommandNode<CommandSourceStack> rootcommandnode = commanddispatcher.getRoot();
      commanddispatcher.findAmbiguities((p_230947_, p_230948_, p_230949_, p_230950_) -> {
         LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", commanddispatcher.getPath(p_230948_), commanddispatcher.getPath(p_230949_), p_230950_);
      });
      Set<ArgumentType<?>> set = ArgumentUtils.findUsedArgumentTypes(rootcommandnode);
      Set<ArgumentType<?>> set1 = set.stream().filter((p_230961_) -> {
         return !ArgumentTypeInfos.isClassRecognized(p_230961_.getClass());
      }).collect(Collectors.toSet());
      if (!set1.isEmpty()) {
         LOGGER.warn("Missing type registration for following arguments:\n {}", set1.stream().map((p_230952_) -> {
            return "\t" + p_230952_;
         }).collect(Collectors.joining(",\n")));
         throw new IllegalStateException("Unregistered argument types");
      }
   }

   public static enum CommandSelection {
      ALL(true, true),
      DEDICATED(false, true),
      INTEGRATED(true, false);

      final boolean includeIntegrated;
      final boolean includeDedicated;

      private CommandSelection(boolean p_82151_, boolean p_82152_) {
         this.includeIntegrated = p_82151_;
         this.includeDedicated = p_82152_;
      }
   }

   @FunctionalInterface
   public interface ParseFunction {
      void parse(StringReader p_82161_) throws CommandSyntaxException;
   }
}
