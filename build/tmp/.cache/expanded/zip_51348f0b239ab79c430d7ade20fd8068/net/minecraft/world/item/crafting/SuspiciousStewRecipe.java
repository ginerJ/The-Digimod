package net.minecraft.world.item.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SuspiciousEffectHolder;

public class SuspiciousStewRecipe extends CustomRecipe {
   public SuspiciousStewRecipe(ResourceLocation p_248870_, CraftingBookCategory p_250392_) {
      super(p_248870_, p_250392_);
   }

   public boolean matches(CraftingContainer p_44499_, Level p_44500_) {
      boolean flag = false;
      boolean flag1 = false;
      boolean flag2 = false;
      boolean flag3 = false;

      for(int i = 0; i < p_44499_.getContainerSize(); ++i) {
         ItemStack itemstack = p_44499_.getItem(i);
         if (!itemstack.isEmpty()) {
            if (itemstack.is(Blocks.BROWN_MUSHROOM.asItem()) && !flag2) {
               flag2 = true;
            } else if (itemstack.is(Blocks.RED_MUSHROOM.asItem()) && !flag1) {
               flag1 = true;
            } else if (itemstack.is(ItemTags.SMALL_FLOWERS) && !flag) {
               flag = true;
            } else {
               if (!itemstack.is(Items.BOWL) || flag3) {
                  return false;
               }

               flag3 = true;
            }
         }
      }

      return flag && flag2 && flag1 && flag3;
   }

   public ItemStack assemble(CraftingContainer p_44497_, RegistryAccess p_266871_) {
      ItemStack itemstack = new ItemStack(Items.SUSPICIOUS_STEW, 1);

      for(int i = 0; i < p_44497_.getContainerSize(); ++i) {
         ItemStack itemstack1 = p_44497_.getItem(i);
         if (!itemstack1.isEmpty()) {
            SuspiciousEffectHolder suspiciouseffectholder = SuspiciousEffectHolder.tryGet(itemstack1.getItem());
            if (suspiciouseffectholder != null) {
               SuspiciousStewItem.saveMobEffect(itemstack, suspiciouseffectholder.getSuspiciousEffect(), suspiciouseffectholder.getEffectDuration());
               break;
            }
         }
      }

      return itemstack;
   }

   public boolean canCraftInDimensions(int p_44489_, int p_44490_) {
      return p_44489_ >= 2 && p_44490_ >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SUSPICIOUS_STEW;
   }
}