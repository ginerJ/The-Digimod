package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class ResourceOrTagKeyArgument<T> implements ArgumentType<ResourceOrTagKeyArgument.Result<T>> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
   final ResourceKey<? extends Registry<T>> registryKey;

   public ResourceOrTagKeyArgument(ResourceKey<? extends Registry<T>> p_248579_) {
      this.registryKey = p_248579_;
   }

   public static <T> ResourceOrTagKeyArgument<T> resourceOrTagKey(ResourceKey<? extends Registry<T>> p_249175_) {
      return new ResourceOrTagKeyArgument<>(p_249175_);
   }

   public static <T> ResourceOrTagKeyArgument.Result<T> getResourceOrTagKey(CommandContext<CommandSourceStack> p_252162_, String p_248628_, ResourceKey<Registry<T>> p_249008_, DynamicCommandExceptionType p_251387_) throws CommandSyntaxException {
      ResourceOrTagKeyArgument.Result<?> result = p_252162_.getArgument(p_248628_, ResourceOrTagKeyArgument.Result.class);
      Optional<ResourceOrTagKeyArgument.Result<T>> optional = result.cast(p_249008_);
      return optional.orElseThrow(() -> {
         return p_251387_.create(result);
      });
   }

   public ResourceOrTagKeyArgument.Result<T> parse(StringReader p_250307_) throws CommandSyntaxException {
      if (p_250307_.canRead() && p_250307_.peek() == '#') {
         int i = p_250307_.getCursor();

         try {
            p_250307_.skip();
            ResourceLocation resourcelocation1 = ResourceLocation.read(p_250307_);
            return new ResourceOrTagKeyArgument.TagResult<>(TagKey.create(this.registryKey, resourcelocation1));
         } catch (CommandSyntaxException commandsyntaxexception) {
            p_250307_.setCursor(i);
            throw commandsyntaxexception;
         }
      } else {
         ResourceLocation resourcelocation = ResourceLocation.read(p_250307_);
         return new ResourceOrTagKeyArgument.ResourceResult<>(ResourceKey.create(this.registryKey, resourcelocation));
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_251659_, SuggestionsBuilder p_251141_) {
      Object object = p_251659_.getSource();
      if (object instanceof SharedSuggestionProvider sharedsuggestionprovider) {
         return sharedsuggestionprovider.suggestRegistryElements(this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ALL, p_251141_, p_251659_);
      } else {
         return p_251141_.buildFuture();
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static class Info<T> implements ArgumentTypeInfo<ResourceOrTagKeyArgument<T>, ResourceOrTagKeyArgument.Info<T>.Template> {
      public void serializeToNetwork(ResourceOrTagKeyArgument.Info<T>.Template p_252211_, FriendlyByteBuf p_248784_) {
         p_248784_.writeResourceLocation(p_252211_.registryKey.location());
      }

      public ResourceOrTagKeyArgument.Info<T>.Template deserializeFromNetwork(FriendlyByteBuf p_250656_) {
         ResourceLocation resourcelocation = p_250656_.readResourceLocation();
         return new ResourceOrTagKeyArgument.Info.Template(ResourceKey.createRegistryKey(resourcelocation));
      }

      public void serializeToJson(ResourceOrTagKeyArgument.Info<T>.Template p_250715_, JsonObject p_249208_) {
         p_249208_.addProperty("registry", p_250715_.registryKey.location().toString());
      }

      public ResourceOrTagKeyArgument.Info<T>.Template unpack(ResourceOrTagKeyArgument<T> p_250422_) {
         return new ResourceOrTagKeyArgument.Info.Template(p_250422_.registryKey);
      }

      public final class Template implements ArgumentTypeInfo.Template<ResourceOrTagKeyArgument<T>> {
         final ResourceKey<? extends Registry<T>> registryKey;

         Template(ResourceKey<? extends Registry<T>> p_251992_) {
            this.registryKey = p_251992_;
         }

         public ResourceOrTagKeyArgument<T> instantiate(CommandBuildContext p_251559_) {
            return new ResourceOrTagKeyArgument<>(this.registryKey);
         }

         public ArgumentTypeInfo<ResourceOrTagKeyArgument<T>, ?> type() {
            return Info.this;
         }
      }
   }

   static record ResourceResult<T>(ResourceKey<T> key) implements ResourceOrTagKeyArgument.Result<T> {
      public Either<ResourceKey<T>, TagKey<T>> unwrap() {
         return Either.left(this.key);
      }

      public <E> Optional<ResourceOrTagKeyArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> p_251369_) {
         return this.key.cast(p_251369_).map(ResourceOrTagKeyArgument.ResourceResult::new);
      }

      public boolean test(Holder<T> p_250257_) {
         return p_250257_.is(this.key);
      }

      public String asPrintable() {
         return this.key.location().toString();
      }
   }

   public interface Result<T> extends Predicate<Holder<T>> {
      Either<ResourceKey<T>, TagKey<T>> unwrap();

      <E> Optional<ResourceOrTagKeyArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> p_251612_);

      String asPrintable();
   }

   static record TagResult<T>(TagKey<T> key) implements ResourceOrTagKeyArgument.Result<T> {
      public Either<ResourceKey<T>, TagKey<T>> unwrap() {
         return Either.right(this.key);
      }

      public <E> Optional<ResourceOrTagKeyArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> p_251833_) {
         return this.key.cast(p_251833_).map(ResourceOrTagKeyArgument.TagResult::new);
      }

      public boolean test(Holder<T> p_252238_) {
         return p_252238_.is(this.key);
      }

      public String asPrintable() {
         return "#" + this.key.location();
      }
   }
}