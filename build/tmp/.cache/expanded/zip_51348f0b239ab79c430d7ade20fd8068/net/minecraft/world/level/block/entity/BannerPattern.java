package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class BannerPattern {
   final String hashname;

   public BannerPattern(String p_222696_) {
      this.hashname = p_222696_;
   }

   public static ResourceLocation location(ResourceKey<BannerPattern> p_222698_, boolean p_222699_) {
      String s = p_222699_ ? "banner" : "shield";
      return p_222698_.location().withPrefix("entity/" + s + "/");
   }

   public String getHashname() {
      return this.hashname;
   }

   @Nullable
   public static Holder<BannerPattern> byHash(String p_222701_) {
      return BuiltInRegistries.BANNER_PATTERN.holders().filter((p_222704_) -> {
         return (p_222704_.value()).hashname.equals(p_222701_);
      }).findAny().orElse((Holder.Reference<BannerPattern>)null);
   }

   public static class Builder {
      private final List<Pair<Holder<BannerPattern>, DyeColor>> patterns = Lists.newArrayList();

      public BannerPattern.Builder addPattern(ResourceKey<BannerPattern> p_222706_, DyeColor p_222707_) {
         return this.addPattern(BuiltInRegistries.BANNER_PATTERN.getHolderOrThrow(p_222706_), p_222707_);
      }

      public BannerPattern.Builder addPattern(Holder<BannerPattern> p_222709_, DyeColor p_222710_) {
         return this.addPattern(Pair.of(p_222709_, p_222710_));
      }

      public BannerPattern.Builder addPattern(Pair<Holder<BannerPattern>, DyeColor> p_155049_) {
         this.patterns.add(p_155049_);
         return this;
      }

      public ListTag toListTag() {
         ListTag listtag = new ListTag();

         for(Pair<Holder<BannerPattern>, DyeColor> pair : this.patterns) {
            CompoundTag compoundtag = new CompoundTag();
            compoundtag.putString("Pattern", (pair.getFirst().value()).hashname);
            compoundtag.putInt("Color", pair.getSecond().getId());
            listtag.add(compoundtag);
         }

         return listtag;
      }
   }
}