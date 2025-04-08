package net.minecraft.world.item.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class CustomRecipe implements CraftingRecipe {
   private final ResourceLocation id;
   private final CraftingBookCategory category;

   public CustomRecipe(ResourceLocation p_252125_, CraftingBookCategory p_249010_) {
      this.id = p_252125_;
      this.category = p_249010_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public boolean isSpecial() {
      return true;
   }

   public ItemStack getResultItem(RegistryAccess p_267025_) {
      return ItemStack.EMPTY;
   }

   public CraftingBookCategory category() {
      return this.category;
   }
}