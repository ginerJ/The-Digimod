package net.minecraft.tags;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public class TagEntry {
   private static final Codec<TagEntry> FULL_CODEC = RecordCodecBuilder.create((p_215937_) -> {
      return p_215937_.group(ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("id").forGetter(TagEntry::elementOrTag), Codec.BOOL.optionalFieldOf("required", Boolean.valueOf(true)).forGetter((p_215952_) -> {
         return p_215952_.required;
      })).apply(p_215937_, TagEntry::new);
   });
   public static final Codec<TagEntry> CODEC = Codec.either(ExtraCodecs.TAG_OR_ELEMENT_ID, FULL_CODEC).xmap((p_215935_) -> {
      return p_215935_.map((p_215933_) -> {
         return new TagEntry(p_215933_, true);
      }, (p_215946_) -> {
         return p_215946_;
      });
   }, (p_215931_) -> {
      return p_215931_.required ? Either.left(p_215931_.elementOrTag()) : Either.right(p_215931_);
   });
   private final ResourceLocation id;
   private final boolean tag;
   private final boolean required;

   private TagEntry(ResourceLocation p_215918_, boolean p_215919_, boolean p_215920_) {
      this.id = p_215918_;
      this.tag = p_215919_;
      this.required = p_215920_;
   }

   private TagEntry(ExtraCodecs.TagOrElementLocation p_215922_, boolean p_215923_) {
      this.id = p_215922_.id();
      this.tag = p_215922_.tag();
      this.required = p_215923_;
   }

   private ExtraCodecs.TagOrElementLocation elementOrTag() {
      return new ExtraCodecs.TagOrElementLocation(this.id, this.tag);
   }

   public static TagEntry element(ResourceLocation p_215926_) {
      return new TagEntry(p_215926_, false, true);
   }

   public static TagEntry optionalElement(ResourceLocation p_215944_) {
      return new TagEntry(p_215944_, false, false);
   }

   public static TagEntry tag(ResourceLocation p_215950_) {
      return new TagEntry(p_215950_, true, true);
   }

   public static TagEntry optionalTag(ResourceLocation p_215954_) {
      return new TagEntry(p_215954_, true, false);
   }

   public <T> boolean build(TagEntry.Lookup<T> p_215928_, Consumer<T> p_215929_) {
      if (this.tag) {
         Collection<T> collection = p_215928_.tag(this.id);
         if (collection == null) {
            return !this.required;
         }

         collection.forEach(p_215929_);
      } else {
         T t = p_215928_.element(this.id);
         if (t == null) {
            return !this.required;
         }

         p_215929_.accept(t);
      }

      return true;
   }

   public void visitRequiredDependencies(Consumer<ResourceLocation> p_215939_) {
      if (this.tag && this.required) {
         p_215939_.accept(this.id);
      }

   }

   public void visitOptionalDependencies(Consumer<ResourceLocation> p_215948_) {
      if (this.tag && !this.required) {
         p_215948_.accept(this.id);
      }

   }

   public boolean verifyIfPresent(Predicate<ResourceLocation> p_215941_, Predicate<ResourceLocation> p_215942_) {
      return !this.required || (this.tag ? p_215942_ : p_215941_).test(this.id);
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      if (this.tag) {
         stringbuilder.append('#');
      }

      stringbuilder.append((Object)this.id);
      if (!this.required) {
         stringbuilder.append('?');
      }

      return stringbuilder.toString();
   }

   public ResourceLocation getId() {
      return id;
   }

   public boolean isRequired() {
      return required;
   }

   public boolean isTag() {
      return tag;
   }

   public interface Lookup<T> {
      @Nullable
      T element(ResourceLocation p_215956_);

      @Nullable
      Collection<T> tag(ResourceLocation p_215957_);
   }
}
