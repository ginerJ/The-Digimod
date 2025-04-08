package net.minecraft.world.inventory;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;

public class TransientCraftingContainer implements CraftingContainer {
   private final NonNullList<ItemStack> items;
   private final int width;
   private final int height;
   private final AbstractContainerMenu menu;

   public TransientCraftingContainer(AbstractContainerMenu p_287684_, int p_287629_, int p_287593_) {
      this(p_287684_, p_287629_, p_287593_, NonNullList.withSize(p_287629_ * p_287593_, ItemStack.EMPTY));
   }

   public TransientCraftingContainer(AbstractContainerMenu p_287708_, int p_287591_, int p_287609_, NonNullList<ItemStack> p_287695_) {
      this.items = p_287695_;
      this.menu = p_287708_;
      this.width = p_287591_;
      this.height = p_287609_;
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.items) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(int p_287712_) {
      return p_287712_ >= this.getContainerSize() ? ItemStack.EMPTY : this.items.get(p_287712_);
   }

   public ItemStack removeItemNoUpdate(int p_287637_) {
      return ContainerHelper.takeItem(this.items, p_287637_);
   }

   public ItemStack removeItem(int p_287682_, int p_287576_) {
      ItemStack itemstack = ContainerHelper.removeItem(this.items, p_287682_, p_287576_);
      if (!itemstack.isEmpty()) {
         this.menu.slotsChanged(this);
      }

      return itemstack;
   }

   public void setItem(int p_287681_, ItemStack p_287620_) {
      this.items.set(p_287681_, p_287620_);
      this.menu.slotsChanged(this);
   }

   public void setChanged() {
   }

   public boolean stillValid(Player p_287774_) {
      return true;
   }

   public void clearContent() {
      this.items.clear();
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public List<ItemStack> getItems() {
      return List.copyOf(this.items);
   }

   public void fillStackedContents(StackedContents p_287653_) {
      for(ItemStack itemstack : this.items) {
         p_287653_.accountSimpleStack(itemstack);
      }

   }
}