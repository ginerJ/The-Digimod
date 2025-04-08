package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

public abstract class IntrinsicHolderTagsProvider<T> extends TagsProvider<T> {
   private final Function<T, ResourceKey<T>> keyExtractor;

   /**
    * @deprecated Forge: Use the {@linkplain #IntrinsicHolderTagsProvider(PackOutput, ResourceKey, CompletableFuture, Function, String, net.minecraftforge.common.data.ExistingFileHelper) mod id variant}
    */
   @Deprecated
   public IntrinsicHolderTagsProvider(PackOutput p_256164_, ResourceKey<? extends Registry<T>> p_256155_, CompletableFuture<HolderLookup.Provider> p_256488_, Function<T, ResourceKey<T>> p_256168_) {
      this(p_256164_, p_256155_, p_256488_, p_256168_, "vanilla", null);
   }
   public IntrinsicHolderTagsProvider(PackOutput p_256164_, ResourceKey<? extends Registry<T>> p_256155_, CompletableFuture<HolderLookup.Provider> p_256488_, Function<T, ResourceKey<T>> p_256168_, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
      super(p_256164_, p_256155_, p_256488_, modId, existingFileHelper);
      this.keyExtractor = p_256168_;
   }

   /**
    * @deprecated Forge: Use the {@linkplain #IntrinsicHolderTagsProvider(PackOutput, ResourceKey, CompletableFuture, CompletableFuture, Function, String, net.minecraftforge.common.data.ExistingFileHelper) mod id variant}
    */
   @Deprecated
   public IntrinsicHolderTagsProvider(PackOutput p_275304_, ResourceKey<? extends Registry<T>> p_275709_, CompletableFuture<HolderLookup.Provider> p_275227_, CompletableFuture<TagsProvider.TagLookup<T>> p_275311_, Function<T, ResourceKey<T>> p_275566_) {
      this(p_275304_, p_275709_, p_275227_, p_275311_, p_275566_, "vanilla", null);
   }
   public IntrinsicHolderTagsProvider(PackOutput p_275304_, ResourceKey<? extends Registry<T>> p_275709_, CompletableFuture<HolderLookup.Provider> p_275227_, CompletableFuture<TagsProvider.TagLookup<T>> p_275311_, Function<T, ResourceKey<T>> p_275566_, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
      super(p_275304_, p_275709_, p_275227_, p_275311_, modId, existingFileHelper);
      this.keyExtractor = p_275566_;
   }

   protected IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> tag(TagKey<T> p_255730_) {
      TagBuilder tagbuilder = this.getOrCreateRawBuilder(p_255730_);
      return new IntrinsicHolderTagsProvider.IntrinsicTagAppender<>(tagbuilder, this.keyExtractor, this.modId);
   }

   public static class IntrinsicTagAppender<T> extends TagsProvider.TagAppender<T> implements net.minecraftforge.common.extensions.IForgeIntrinsicHolderTagAppender<T> {
      private final Function<T, ResourceKey<T>> keyExtractor;

      IntrinsicTagAppender(TagBuilder p_256108_, Function<T, ResourceKey<T>> p_256433_, String modId) {
         super(p_256108_, modId);
         this.keyExtractor = p_256433_;
      }

      public IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> addTag(TagKey<T> p_256311_) {
         super.addTag(p_256311_);
         return this;
      }

      public final IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> add(T p_256557_) {
         this.add(this.keyExtractor.apply(p_256557_));
         return this;
      }

      @SafeVarargs
      public final IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> add(T... p_255868_) {
         Stream.<T>of(p_255868_).map(this.keyExtractor).forEach(this::add);
         return this;
      }

      @Override
      public final ResourceKey<T> getKey(T value) {
         return this.keyExtractor.apply(value);
      }
   }
}
