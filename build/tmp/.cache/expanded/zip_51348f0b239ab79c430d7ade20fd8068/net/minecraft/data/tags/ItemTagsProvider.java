package net.minecraft.data.tags;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public abstract class ItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {
   private final CompletableFuture<TagsProvider.TagLookup<Block>> blockTags;
   private final Map<TagKey<Block>, TagKey<Item>> tagsToCopy = new HashMap<>();

   /**
    * @deprecated Forge: Use the {@linkplain #ItemTagsProvider(PackOutput, CompletableFuture, CompletableFuture, String, net.minecraftforge.common.data.ExistingFileHelper) mod id variant}
    */
   @Deprecated
   public ItemTagsProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagsProvider.TagLookup<Block>> p_275322_) {
      this(p_275343_, p_275729_, p_275322_, "vanilla", null);
   }
   public ItemTagsProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagsProvider.TagLookup<Block>> p_275322_, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
      super(p_275343_, Registries.ITEM, p_275729_, (p_255790_) -> {
         return p_255790_.builtInRegistryHolder().key();
      }, modId, existingFileHelper);
      this.blockTags = p_275322_;
   }

   /**
    * @deprecated Forge: Use the {@linkplain #ItemTagsProvider(PackOutput, CompletableFuture, CompletableFuture, CompletableFuture, String, net.minecraftforge.common.data.ExistingFileHelper) mod id variant}
    */
   @Deprecated
   public ItemTagsProvider(PackOutput p_275204_, CompletableFuture<HolderLookup.Provider> p_275194_, CompletableFuture<TagsProvider.TagLookup<Item>> p_275207_, CompletableFuture<TagsProvider.TagLookup<Block>> p_275634_) {
      this(p_275204_, p_275194_, p_275207_, p_275634_, "vanilla", null);
   }
   public ItemTagsProvider(PackOutput p_275204_, CompletableFuture<HolderLookup.Provider> p_275194_, CompletableFuture<TagsProvider.TagLookup<Item>> p_275207_, CompletableFuture<TagsProvider.TagLookup<Block>> p_275634_, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
      super(p_275204_, Registries.ITEM, p_275194_, p_275207_, (p_274765_) -> {
         return p_274765_.builtInRegistryHolder().key();
      });
      this.blockTags = p_275634_;
   }

   protected void copy(TagKey<Block> p_206422_, TagKey<Item> p_206423_) {
      this.tagsToCopy.put(p_206422_, p_206423_);
   }

   protected CompletableFuture<HolderLookup.Provider> createContentsProvider() {
      return super.createContentsProvider().thenCombineAsync(this.blockTags, (p_274766_, p_274767_) -> {
         this.tagsToCopy.forEach((p_274763_, p_274764_) -> {
            TagBuilder tagbuilder = this.getOrCreateRawBuilder(p_274764_);
            Optional<TagBuilder> optional = p_274767_.apply(p_274763_);
            optional.orElseThrow(() -> {
               return new IllegalStateException("Missing block tag " + p_274764_.location());
            }).build().forEach(tagbuilder::add);
         });
         return p_274766_;
      });
   }
}
