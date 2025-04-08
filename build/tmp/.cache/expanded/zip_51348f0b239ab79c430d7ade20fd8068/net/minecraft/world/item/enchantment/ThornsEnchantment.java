package net.minecraft.world.item.enchantment;

import java.util.Map;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class ThornsEnchantment extends Enchantment {
   private static final float CHANCE_PER_LEVEL = 0.15F;

   public ThornsEnchantment(Enchantment.Rarity p_45196_, EquipmentSlot... p_45197_) {
      super(p_45196_, EnchantmentCategory.ARMOR_CHEST, p_45197_);
   }

   public int getMinCost(int p_45200_) {
      return 10 + 20 * (p_45200_ - 1);
   }

   public int getMaxCost(int p_45210_) {
      return super.getMinCost(p_45210_) + 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canEnchant(ItemStack p_45205_) {
      return p_45205_.getItem() instanceof ArmorItem ? true : super.canEnchant(p_45205_);
   }

   public void doPostHurt(LivingEntity p_45215_, Entity p_45216_, int p_45217_) {
      RandomSource randomsource = p_45215_.getRandom();
      Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.getRandomItemWith(Enchantments.THORNS, p_45215_);
      if (shouldHit(p_45217_, randomsource)) {
         if (p_45216_ != null) {
            p_45216_.hurt(p_45215_.damageSources().thorns(p_45215_), (float)getDamage(p_45217_, randomsource));
         }

         if (entry != null) {
            entry.getValue().hurtAndBreak(2, p_45215_, (p_45208_) -> {
               p_45208_.broadcastBreakEvent(entry.getKey());
            });
         }
      }

   }

   public static boolean shouldHit(int p_220317_, RandomSource p_220318_) {
      if (p_220317_ <= 0) {
         return false;
      } else {
         return p_220318_.nextFloat() < 0.15F * (float)p_220317_;
      }
   }

   public static int getDamage(int p_220320_, RandomSource p_220321_) {
      return p_220320_ > 10 ? p_220320_ - 10 : 1 + p_220321_.nextInt(4);
   }
}