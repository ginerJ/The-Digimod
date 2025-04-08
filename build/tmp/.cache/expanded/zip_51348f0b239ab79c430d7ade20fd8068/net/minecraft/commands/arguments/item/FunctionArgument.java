package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FunctionArgument implements ArgumentType<FunctionArgument.Result> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((p_120927_) -> {
      return Component.translatable("arguments.function.tag.unknown", p_120927_);
   });
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_FUNCTION = new DynamicCommandExceptionType((p_120917_) -> {
      return Component.translatable("arguments.function.unknown", p_120917_);
   });

   public static FunctionArgument functions() {
      return new FunctionArgument();
   }

   public FunctionArgument.Result parse(StringReader p_120909_) throws CommandSyntaxException {
      if (p_120909_.canRead() && p_120909_.peek() == '#') {
         p_120909_.skip();
         final ResourceLocation resourcelocation1 = ResourceLocation.read(p_120909_);
         return new FunctionArgument.Result() {
            public Collection<CommandFunction> create(CommandContext<CommandSourceStack> p_120943_) throws CommandSyntaxException {
               return FunctionArgument.getFunctionTag(p_120943_, resourcelocation1);
            }

            public Pair<ResourceLocation, Either<CommandFunction, Collection<CommandFunction>>> unwrap(CommandContext<CommandSourceStack> p_120945_) throws CommandSyntaxException {
               return Pair.of(resourcelocation1, Either.right(FunctionArgument.getFunctionTag(p_120945_, resourcelocation1)));
            }
         };
      } else {
         final ResourceLocation resourcelocation = ResourceLocation.read(p_120909_);
         return new FunctionArgument.Result() {
            public Collection<CommandFunction> create(CommandContext<CommandSourceStack> p_120952_) throws CommandSyntaxException {
               return Collections.singleton(FunctionArgument.getFunction(p_120952_, resourcelocation));
            }

            public Pair<ResourceLocation, Either<CommandFunction, Collection<CommandFunction>>> unwrap(CommandContext<CommandSourceStack> p_120954_) throws CommandSyntaxException {
               return Pair.of(resourcelocation, Either.left(FunctionArgument.getFunction(p_120954_, resourcelocation)));
            }
         };
      }
   }

   static CommandFunction getFunction(CommandContext<CommandSourceStack> p_120929_, ResourceLocation p_120930_) throws CommandSyntaxException {
      return p_120929_.getSource().getServer().getFunctions().get(p_120930_).orElseThrow(() -> {
         return ERROR_UNKNOWN_FUNCTION.create(p_120930_.toString());
      });
   }

   static Collection<CommandFunction> getFunctionTag(CommandContext<CommandSourceStack> p_235274_, ResourceLocation p_235275_) throws CommandSyntaxException {
      Collection<CommandFunction> collection = p_235274_.getSource().getServer().getFunctions().getTag(p_235275_);
      if (collection == null) {
         throw ERROR_UNKNOWN_TAG.create(p_235275_.toString());
      } else {
         return collection;
      }
   }

   public static Collection<CommandFunction> getFunctions(CommandContext<CommandSourceStack> p_120911_, String p_120912_) throws CommandSyntaxException {
      return p_120911_.getArgument(p_120912_, FunctionArgument.Result.class).create(p_120911_);
   }

   public static Pair<ResourceLocation, Either<CommandFunction, Collection<CommandFunction>>> getFunctionOrTag(CommandContext<CommandSourceStack> p_120921_, String p_120922_) throws CommandSyntaxException {
      return p_120921_.getArgument(p_120922_, FunctionArgument.Result.class).unwrap(p_120921_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public interface Result {
      Collection<CommandFunction> create(CommandContext<CommandSourceStack> p_120955_) throws CommandSyntaxException;

      Pair<ResourceLocation, Either<CommandFunction, Collection<CommandFunction>>> unwrap(CommandContext<CommandSourceStack> p_120956_) throws CommandSyntaxException;
   }
}