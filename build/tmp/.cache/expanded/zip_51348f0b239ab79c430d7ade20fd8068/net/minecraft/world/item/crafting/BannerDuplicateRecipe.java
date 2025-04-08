package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerBlockEntity;

public class BannerDuplicateRecipe extends CustomRecipe {
   public BannerDuplicateRecipe(ResourceLocation p_249594_, CraftingBookCategory p_250373_) {
      super(p_249594_, p_250373_);
   }

   public boolean matches(CraftingContainer p_43785_, Level p_43786_) {
      DyeColor dyecolor = null;
      ItemStack itemstack = null;
      ItemStack itemstack1 = null;

      for(int i = 0; i < p_43785_.getContainerSize(); ++i) {
         ItemStack itemstack2 = p_43785_.getItem(i);
         if (!itemstack2.isEmpty()) {
            Item item = itemstack2.getItem();
            if (!(item instanceof BannerItem)) {
               return false;
            }

            BannerItem banneritem = (BannerItem)item;
            if (dyecolor == null) {
               dyecolor = banneritem.getColor();
            } else if (dyecolor != banneritem.getColor()) {
               return false;
            }

            int j = BannerBlockEntity.getPatternCount(itemstack2);
            if (j > 6) {
               return false;
            }

            if (j > 0) {
               if (itemstack != null) {
                  return false;
               }

               itemstack = itemstack2;
            } else {
               if (itemstack1 != null) {
                  return false;
               }

               itemstack1 = itemstack2;
            }
         }
      }

      return itemstack != null && itemstack1 != null;
   }

   public ItemStack assemble(CraftingContainer p_43783_, RegistryAccess p_267308_) {
      for(int i = 0; i < p_43783_.getContainerSize(); ++i) {
         ItemStack itemstack = p_43783_.getItem(i);
         if (!itemstack.isEmpty()) {
            int j = BannerBlockEntity.getPatternCount(itemstack);
            if (j > 0 && j <= 6) {
               return itemstack.copyWithCount(1);
            }
         }
      }

      return ItemStack.EMPTY;
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingContainer p_43791_) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_43791_.getContainerSize(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack itemstack = p_43791_.getItem(i);
         if (!itemstack.isEmpty()) {
            if (itemstack.hasCraftingRemainingItem()) {
               nonnulllist.set(i, itemstack.getCraftingRemainingItem());
            } else if (itemstack.hasTag() && BannerBlockEntity.getPatternCount(itemstack) > 0) {
               nonnulllist.set(i, itemstack.copyWithCount(1));
            }
         }
      }

      return nonnulllist;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.BANNER_DUPLICATE;
   }

   public boolean canCraftInDimensions(int p_43775_, int p_43776_) {
      return p_43775_ * p_43776_ >= 2;
   }
}
