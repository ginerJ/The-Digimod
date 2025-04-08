package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ItemParser {
   private static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType(Component.translatable("argument.item.tag.disallowed"));
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM = new DynamicCommandExceptionType((p_121013_) -> {
      return Component.translatable("argument.item.id.invalid", p_121013_);
   });
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((p_235313_) -> {
      return Component.translatable("arguments.item.tag.unknown", p_235313_);
   });
   private static final char SYNTAX_START_NBT = '{';
   private static final char SYNTAX_TAG = '#';
   private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
   private final HolderLookup<Item> items;
   private final StringReader reader;
   private final boolean allowTags;
   private Either<Holder<Item>, HolderSet<Item>> result;
   @Nullable
   private CompoundTag nbt;
   private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

   private ItemParser(HolderLookup<Item> p_235291_, StringReader p_235292_, boolean p_235293_) {
      this.items = p_235291_;
      this.reader = p_235292_;
      this.allowTags = p_235293_;
   }

   public static ItemParser.ItemResult parseForItem(HolderLookup<Item> p_235306_, StringReader p_235307_) throws CommandSyntaxException {
      int i = p_235307_.getCursor();

      try {
         ItemParser itemparser = new ItemParser(p_235306_, p_235307_, false);
         itemparser.parse();
         Holder<Item> holder = itemparser.result.left().orElseThrow(() -> {
            return new IllegalStateException("Parser returned unexpected tag name");
         });
         return new ItemParser.ItemResult(holder, itemparser.nbt);
      } catch (CommandSyntaxException commandsyntaxexception) {
         p_235307_.setCursor(i);
         throw commandsyntaxexception;
      }
   }

   public static Either<ItemParser.ItemResult, ItemParser.TagResult> parseForTesting(HolderLookup<Item> p_235320_, StringReader p_235321_) throws CommandSyntaxException {
      int i = p_235321_.getCursor();

      try {
         ItemParser itemparser = new ItemParser(p_235320_, p_235321_, true);
         itemparser.parse();
         return itemparser.result.mapBoth((p_235301_) -> {
            return new ItemParser.ItemResult(p_235301_, itemparser.nbt);
         }, (p_235304_) -> {
            return new ItemParser.TagResult(p_235304_, itemparser.nbt);
         });
      } catch (CommandSyntaxException commandsyntaxexception) {
         p_235321_.setCursor(i);
         throw commandsyntaxexception;
      }
   }

   public static CompletableFuture<Suggestions> fillSuggestions(HolderLookup<Item> p_235309_, SuggestionsBuilder p_235310_, boolean p_235311_) {
      StringReader stringreader = new StringReader(p_235310_.getInput());
      stringreader.setCursor(p_235310_.getStart());
      ItemParser itemparser = new ItemParser(p_235309_, stringreader, p_235311_);

      try {
         itemparser.parse();
      } catch (CommandSyntaxException commandsyntaxexception) {
      }

      return itemparser.suggestions.apply(p_235310_.createOffset(stringreader.getCursor()));
   }

   private void readItem() throws CommandSyntaxException {
      int i = this.reader.getCursor();
      ResourceLocation resourcelocation = ResourceLocation.read(this.reader);
      Optional<? extends Holder<Item>> optional = this.items.get(ResourceKey.create(Registries.ITEM, resourcelocation));
      this.result = Either.left(optional.orElseThrow(() -> {
         this.reader.setCursor(i);
         return ERROR_UNKNOWN_ITEM.createWithContext(this.reader, resourcelocation);
      }));
   }

   private void readTag() throws CommandSyntaxException {
      if (!this.allowTags) {
         throw ERROR_NO_TAGS_ALLOWED.createWithContext(this.reader);
      } else {
         int i = this.reader.getCursor();
         this.reader.expect('#');
         this.suggestions = this::suggestTag;
         ResourceLocation resourcelocation = ResourceLocation.read(this.reader);
         Optional<? extends HolderSet<Item>> optional = this.items.get(TagKey.create(Registries.ITEM, resourcelocation));
         this.result = Either.right(optional.orElseThrow(() -> {
            this.reader.setCursor(i);
            return ERROR_UNKNOWN_TAG.createWithContext(this.reader, resourcelocation);
         }));
      }
   }

   private void readNbt() throws CommandSyntaxException {
      this.nbt = (new TagParser(this.reader)).readStruct();
   }

   private void parse() throws CommandSyntaxException {
      if (this.allowTags) {
         this.suggestions = this::suggestItemIdOrTag;
      } else {
         this.suggestions = this::suggestItem;
      }

      if (this.reader.canRead() && this.reader.peek() == '#') {
         this.readTag();
      } else {
         this.readItem();
      }

      this.suggestions = this::suggestOpenNbt;
      if (this.reader.canRead() && this.reader.peek() == '{') {
         this.suggestions = SUGGEST_NOTHING;
         this.readNbt();
      }

   }

   private CompletableFuture<Suggestions> suggestOpenNbt(SuggestionsBuilder p_235298_) {
      if (p_235298_.getRemaining().isEmpty()) {
         p_235298_.suggest(String.valueOf('{'));
      }

      return p_235298_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder p_235318_) {
      return SharedSuggestionProvider.suggestResource(this.items.listTagIds().map(TagKey::location), p_235318_, String.valueOf('#'));
   }

   private CompletableFuture<Suggestions> suggestItem(SuggestionsBuilder p_235323_) {
      return SharedSuggestionProvider.suggestResource(this.items.listElementIds().map(ResourceKey::location), p_235323_);
   }

   private CompletableFuture<Suggestions> suggestItemIdOrTag(SuggestionsBuilder p_235326_) {
      this.suggestTag(p_235326_);
      return this.suggestItem(p_235326_);
   }

   public static record ItemResult(Holder<Item> item, @Nullable CompoundTag nbt) {
   }

   public static record TagResult(HolderSet<Item> tag, @Nullable CompoundTag nbt) {
   }
}