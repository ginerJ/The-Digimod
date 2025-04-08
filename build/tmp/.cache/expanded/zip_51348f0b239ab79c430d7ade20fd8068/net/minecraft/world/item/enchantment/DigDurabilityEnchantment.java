package net.minecraft.world.item.enchantment;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class DigDurabilityEnchantment extends Enchantment {
   protected DigDurabilityEnchantment(Enchantment.Rarity p_44648_, EquipmentSlot... p_44649_) {
      super(p_44648_, EnchantmentCategory.BREAKABLE, p_44649_);
   }

   public int getMinCost(int p_44652_) {
      return 5 + (p_44652_ - 1) * 8;
   }

   public int getMaxCost(int p_44660_) {
      return super.getMinCost(p_44660_) + 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canEnchant(ItemStack p_44654_) {
      return p_44654_.isDamageableItem() ? true : super.canEnchant(p_44654_);
   }

   public static boolean shouldIgnoreDurabilityDrop(ItemStack p_220283_, int p_220284_, RandomSource p_220285_) {
      if (p_220283_.getItem() instanceof ArmorItem && p_220285_.nextFloat() < 0.6F) {
         return false;
      } else {
         return p_220285_.nextInt(p_220284_ + 1) > 0;
      }
   }
}