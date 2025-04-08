package net.minecraft.world.item.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;

public class DecoratedPotRecipe extends CustomRecipe {
   public DecoratedPotRecipe(ResourceLocation p_273671_, CraftingBookCategory p_273056_) {
      super(p_273671_, p_273056_);
   }

   public boolean matches(CraftingContainer p_272882_, Level p_272812_) {
      if (!this.canCraftInDimensions(p_272882_.getWidth(), p_272882_.getHeight())) {
         return false;
      } else {
         for(int i = 0; i < p_272882_.getContainerSize(); ++i) {
            ItemStack itemstack = p_272882_.getItem(i);
            switch (i) {
               case 1:
               case 3:
               case 5:
               case 7:
                  if (!itemstack.is(ItemTags.DECORATED_POT_INGREDIENTS)) {
                     return false;
                  }
                  break;
               case 2:
               case 4:
               case 6:
               default:
                  if (!itemstack.is(Items.AIR)) {
                     return false;
                  }
            }
         }

         return true;
      }
   }

   public ItemStack assemble(CraftingContainer p_272861_, RegistryAccess p_273288_) {
      DecoratedPotBlockEntity.Decorations decoratedpotblockentity$decorations = new DecoratedPotBlockEntity.Decorations(p_272861_.getItem(1).getItem(), p_272861_.getItem(3).getItem(), p_272861_.getItem(5).getItem(), p_272861_.getItem(7).getItem());
      return createDecoratedPotItem(decoratedpotblockentity$decorations);
   }

   public static ItemStack createDecoratedPotItem(DecoratedPotBlockEntity.Decorations p_285413_) {
      ItemStack itemstack = Items.DECORATED_POT.getDefaultInstance();
      CompoundTag compoundtag = p_285413_.save(new CompoundTag());
      BlockItem.setBlockEntityData(itemstack, BlockEntityType.DECORATED_POT, compoundtag);
      return itemstack;
   }

   public boolean canCraftInDimensions(int p_273734_, int p_273516_) {
      return p_273734_ == 3 && p_273516_ == 3;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.DECORATED_POT_RECIPE;
   }
}