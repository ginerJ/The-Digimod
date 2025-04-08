package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemInput implements Predicate<ItemStack> {
   private static final Dynamic2CommandExceptionType ERROR_STACK_TOO_BIG = new Dynamic2CommandExceptionType((p_120986_, p_120987_) -> {
      return Component.translatable("arguments.item.overstacked", p_120986_, p_120987_);
   });
   private final Holder<Item> item;
   @Nullable
   private final CompoundTag tag;

   public ItemInput(Holder<Item> p_235282_, @Nullable CompoundTag p_235283_) {
      this.item = p_235282_;
      this.tag = p_235283_;
   }

   public Item getItem() {
      return this.item.value();
   }

   public boolean test(ItemStack p_120984_) {
      return p_120984_.is(this.item) && NbtUtils.compareNbt(this.tag, p_120984_.getTag(), true);
   }

   public ItemStack createItemStack(int p_120981_, boolean p_120982_) throws CommandSyntaxException {
      ItemStack itemstack = new ItemStack(this.item, p_120981_);
      if (this.tag != null) {
         itemstack.setTag(this.tag);
      }

      if (p_120982_ && p_120981_ > itemstack.getMaxStackSize()) {
         throw ERROR_STACK_TOO_BIG.create(this.getItemName(), itemstack.getMaxStackSize());
      } else {
         return itemstack;
      }
   }

   public String serialize() {
      StringBuilder stringbuilder = new StringBuilder(this.getItemName());
      if (this.tag != null) {
         stringbuilder.append((Object)this.tag);
      }

      return stringbuilder.toString();
   }

   private String getItemName() {
      return this.item.unwrapKey().<Object>map(ResourceKey::location).orElseGet(() -> {
         return "unknown[" + this.item + "]";
      }).toString();
   }
}