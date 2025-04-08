package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class ItemArgument implements ArgumentType<ItemInput> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");
   private final HolderLookup<Item> items;

   public ItemArgument(CommandBuildContext p_235278_) {
      this.items = p_235278_.holderLookup(Registries.ITEM);
   }

   public static ItemArgument item(CommandBuildContext p_235280_) {
      return new ItemArgument(p_235280_);
   }

   public ItemInput parse(StringReader p_120962_) throws CommandSyntaxException {
      ItemParser.ItemResult itemparser$itemresult = ItemParser.parseForItem(this.items, p_120962_);
      return new ItemInput(itemparser$itemresult.item(), itemparser$itemresult.nbt());
   }

   public static <S> ItemInput getItem(CommandContext<S> p_120964_, String p_120965_) {
      return p_120964_.getArgument(p_120965_, ItemInput.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_120968_, SuggestionsBuilder p_120969_) {
      return ItemParser.fillSuggestions(this.items, p_120969_, false);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}