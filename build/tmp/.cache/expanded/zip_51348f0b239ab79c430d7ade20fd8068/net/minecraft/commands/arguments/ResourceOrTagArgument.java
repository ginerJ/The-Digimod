package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
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
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class ResourceOrTagArgument<T> implements ArgumentType<ResourceOrTagArgument.Result<T>> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
   private static final Dynamic2CommandExceptionType ERROR_UNKNOWN_TAG = new Dynamic2CommandExceptionType((p_250953_, p_249704_) -> {
      return Component.translatable("argument.resource_tag.not_found", p_250953_, p_249704_);
   });
   private static final Dynamic3CommandExceptionType ERROR_INVALID_TAG_TYPE = new Dynamic3CommandExceptionType((p_250188_, p_252173_, p_251453_) -> {
      return Component.translatable("argument.resource_tag.invalid_type", p_250188_, p_252173_, p_251453_);
   });
   private final HolderLookup<T> registryLookup;
   final ResourceKey<? extends Registry<T>> registryKey;

   public ResourceOrTagArgument(CommandBuildContext p_249382_, ResourceKey<? extends Registry<T>> p_251209_) {
      this.registryKey = p_251209_;
      this.registryLookup = p_249382_.holderLookup(p_251209_);
   }

   public static <T> ResourceOrTagArgument<T> resourceOrTag(CommandBuildContext p_251101_, ResourceKey<? extends Registry<T>> p_248888_) {
      return new ResourceOrTagArgument<>(p_251101_, p_248888_);
   }

   public static <T> ResourceOrTagArgument.Result<T> getResourceOrTag(CommandContext<CommandSourceStack> p_249001_, String p_251520_, ResourceKey<Registry<T>> p_250370_) throws CommandSyntaxException {
      ResourceOrTagArgument.Result<?> result = p_249001_.getArgument(p_251520_, ResourceOrTagArgument.Result.class);
      Optional<ResourceOrTagArgument.Result<T>> optional = result.cast(p_250370_);
      return optional.orElseThrow(() -> {
         return result.unwrap().map((p_252340_) -> {
            ResourceKey<?> resourcekey = p_252340_.key();
            return ResourceArgument.ERROR_INVALID_RESOURCE_TYPE.create(resourcekey.location(), resourcekey.registry(), p_250370_.location());
         }, (p_250301_) -> {
            TagKey<?> tagkey = p_250301_.key();
            return ERROR_INVALID_TAG_TYPE.create(tagkey.location(), tagkey.registry(), p_250370_.location());
         });
      });
   }

   public ResourceOrTagArgument.Result<T> parse(StringReader p_250860_) throws CommandSyntaxException {
      if (p_250860_.canRead() && p_250860_.peek() == '#') {
         int i = p_250860_.getCursor();

         try {
            p_250860_.skip();
            ResourceLocation resourcelocation1 = ResourceLocation.read(p_250860_);
            TagKey<T> tagkey = TagKey.create(this.registryKey, resourcelocation1);
            HolderSet.Named<T> named = this.registryLookup.get(tagkey).orElseThrow(() -> {
               return ERROR_UNKNOWN_TAG.create(resourcelocation1, this.registryKey.location());
            });
            return new ResourceOrTagArgument.TagResult<>(named);
         } catch (CommandSyntaxException commandsyntaxexception) {
            p_250860_.setCursor(i);
            throw commandsyntaxexception;
         }
      } else {
         ResourceLocation resourcelocation = ResourceLocation.read(p_250860_);
         ResourceKey<T> resourcekey = ResourceKey.create(this.registryKey, resourcelocation);
         Holder.Reference<T> reference = this.registryLookup.get(resourcekey).orElseThrow(() -> {
            return ResourceArgument.ERROR_UNKNOWN_RESOURCE.create(resourcelocation, this.registryKey.location());
         });
         return new ResourceOrTagArgument.ResourceResult<>(reference);
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_250223_, SuggestionsBuilder p_252354_) {
      SharedSuggestionProvider.suggestResource(this.registryLookup.listTagIds().map(TagKey::location), p_252354_, "#");
      return SharedSuggestionProvider.suggestResource(this.registryLookup.listElementIds().map(ResourceKey::location), p_252354_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static class Info<T> implements ArgumentTypeInfo<ResourceOrTagArgument<T>, ResourceOrTagArgument.Info<T>.Template> {
      public void serializeToNetwork(ResourceOrTagArgument.Info<T>.Template p_250419_, FriendlyByteBuf p_249726_) {
         p_249726_.writeResourceLocation(p_250419_.registryKey.location());
      }

      public ResourceOrTagArgument.Info<T>.Template deserializeFromNetwork(FriendlyByteBuf p_250205_) {
         ResourceLocation resourcelocation = p_250205_.readResourceLocation();
         return new ResourceOrTagArgument.Info.Template(ResourceKey.createRegistryKey(resourcelocation));
      }

      public void serializeToJson(ResourceOrTagArgument.Info<T>.Template p_251957_, JsonObject p_249067_) {
         p_249067_.addProperty("registry", p_251957_.registryKey.location().toString());
      }

      public ResourceOrTagArgument.Info<T>.Template unpack(ResourceOrTagArgument<T> p_252206_) {
         return new ResourceOrTagArgument.Info.Template(p_252206_.registryKey);
      }

      public final class Template implements ArgumentTypeInfo.Template<ResourceOrTagArgument<T>> {
         final ResourceKey<? extends Registry<T>> registryKey;

         Template(ResourceKey<? extends Registry<T>> p_250107_) {
            this.registryKey = p_250107_;
         }

         public ResourceOrTagArgument<T> instantiate(CommandBuildContext p_251386_) {
            return new ResourceOrTagArgument<>(p_251386_, this.registryKey);
         }

         public ArgumentTypeInfo<ResourceOrTagArgument<T>, ?> type() {
            return Info.this;
         }
      }
   }

   static record ResourceResult<T>(Holder.Reference<T> value) implements ResourceOrTagArgument.Result<T> {
      public Either<Holder.Reference<T>, HolderSet.Named<T>> unwrap() {
         return Either.left(this.value);
      }

      public <E> Optional<ResourceOrTagArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> p_250007_) {
         return this.value.key().isFor(p_250007_) ? Optional.of((ResourceOrTagArgument.Result) this) : Optional.empty();
      }

      public boolean test(Holder<T> p_249230_) {
         return p_249230_.equals(this.value);
      }

      public String asPrintable() {
         return this.value.key().location().toString();
      }
   }

   public interface Result<T> extends Predicate<Holder<T>> {
      Either<Holder.Reference<T>, HolderSet.Named<T>> unwrap();

      <E> Optional<ResourceOrTagArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> p_249572_);

      String asPrintable();
   }

   static record TagResult<T>(HolderSet.Named<T> tag) implements ResourceOrTagArgument.Result<T> {
      public Either<Holder.Reference<T>, HolderSet.Named<T>> unwrap() {
         return Either.right(this.tag);
      }

      public <E> Optional<ResourceOrTagArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> p_250945_) {
         return this.tag.key().isFor(p_250945_) ? Optional.of((ResourceOrTagArgument.Result) this) : Optional.empty();
      }

      public boolean test(Holder<T> p_252187_) {
         return this.tag.contains(p_252187_);
      }

      public String asPrintable() {
         return "#" + this.tag.key().location();
      }
   }
}