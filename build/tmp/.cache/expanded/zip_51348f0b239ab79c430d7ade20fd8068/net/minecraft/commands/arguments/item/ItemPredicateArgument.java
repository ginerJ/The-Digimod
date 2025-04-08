package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemPredicateArgument implements ArgumentType<ItemPredicateArgument.Result> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
   private final HolderLookup<Item> items;

   public ItemPredicateArgument(CommandBuildContext p_235352_) {
      this.items = p_235352_.holderLookup(Registries.ITEM);
   }

   public static ItemPredicateArgument itemPredicate(CommandBuildContext p_235354_) {
      return new ItemPredicateArgument(p_235354_);
   }

   public ItemPredicateArgument.Result parse(StringReader p_121039_) throws CommandSyntaxException {
      Either<ItemParser.ItemResult, ItemParser.TagResult> either = ItemParser.parseForTesting(this.items, p_121039_);
      return either.map((p_235356_) -> {
         return createResult((p_235359_) -> {
            return p_235359_ == p_235356_.item();
         }, p_235356_.nbt());
      }, (p_235361_) -> {
         return createResult(p_235361_.tag()::contains, p_235361_.nbt());
      });
   }

   public static Predicate<ItemStack> getItemPredicate(CommandContext<CommandSourceStack> p_121041_, String p_121042_) {
      return p_121041_.getArgument(p_121042_, ItemPredicateArgument.Result.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_121054_, SuggestionsBuilder p_121055_) {
      return ItemParser.fillSuggestions(this.items, p_121055_, true);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   private static ItemPredicateArgument.Result createResult(Predicate<Holder<Item>> p_235366_, @Nullable CompoundTag p_235367_) {
      return p_235367_ != null ? (p_235371_) -> {
         return p_235371_.is(p_235366_) && NbtUtils.compareNbt(p_235367_, p_235371_.getTag(), true);
      } : (p_235364_) -> {
         return p_235364_.is(p_235366_);
      };
   }

   public interface Result extends Predicate<ItemStack> {
   }
}