package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public interface Equipable extends Vanishable {
   EquipmentSlot getEquipmentSlot();

   default SoundEvent getEquipSound() {
      return SoundEvents.ARMOR_EQUIP_GENERIC;
   }

   default InteractionResultHolder<ItemStack> swapWithEquipmentSlot(Item p_270453_, Level p_270395_, Player p_270300_, InteractionHand p_270262_) {
      ItemStack itemstack = p_270300_.getItemInHand(p_270262_);
      EquipmentSlot equipmentslot = Mob.getEquipmentSlotForItem(itemstack);
      ItemStack itemstack1 = p_270300_.getItemBySlot(equipmentslot);
      if (!EnchantmentHelper.hasBindingCurse(itemstack1) && !ItemStack.matches(itemstack, itemstack1)) {
         if (!p_270395_.isClientSide()) {
            p_270300_.awardStat(Stats.ITEM_USED.get(p_270453_));
         }

         ItemStack itemstack2 = itemstack1.isEmpty() ? itemstack : itemstack1.copyAndClear();
         ItemStack itemstack3 = itemstack.copyAndClear();
         p_270300_.setItemSlot(equipmentslot, itemstack3);
         return InteractionResultHolder.sidedSuccess(itemstack2, p_270395_.isClientSide());
      } else {
         return InteractionResultHolder.fail(itemstack);
      }
   }

   @Nullable
   static Equipable get(ItemStack p_270317_) {
      Item $$3 = p_270317_.getItem();
      if ($$3 instanceof Equipable equipable) {
         return equipable;
      } else {
         Item item1 = p_270317_.getItem();
         if (item1 instanceof BlockItem blockitem) {
            Block block = blockitem.getBlock();
            if (block instanceof Equipable equipable1) {
               return equipable1;
            }
         }

         return null;
      }
   }
}