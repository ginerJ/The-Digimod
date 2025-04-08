package net.minecraft.world.item;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;

public class ItemStackLinkedSet {
   public static final Hash.Strategy<? super ItemStack> TYPE_AND_TAG = new Hash.Strategy<ItemStack>() {
      public int hashCode(@Nullable ItemStack p_251266_) {
         return ItemStackLinkedSet.hashStackAndTag(p_251266_);
      }

      public boolean equals(@Nullable ItemStack p_250623_, @Nullable ItemStack p_251135_) {
         return p_250623_ == p_251135_ || p_250623_ != null && p_251135_ != null && p_250623_.isEmpty() == p_251135_.isEmpty() && ItemStack.isSameItemSameTags(p_250623_, p_251135_);
      }
   };

   static int hashStackAndTag(@Nullable ItemStack p_262160_) {
      if (p_262160_ != null) {
         CompoundTag compoundtag = p_262160_.getTag();
         int i = 31 + p_262160_.getItem().hashCode();
         return 31 * i + (compoundtag == null ? 0 : compoundtag.hashCode());
      } else {
         return 0;
      }
   }

   public static Set<ItemStack> createTypeAndTagSet() {
      return new ObjectLinkedOpenCustomHashSet<>(TYPE_AND_TAG);
   }
}