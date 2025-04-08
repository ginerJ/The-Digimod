package net.minecraft.world;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface Container extends Clearable {
   int LARGE_MAX_STACK_SIZE = 64;
   int DEFAULT_DISTANCE_LIMIT = 8;

   int getContainerSize();

   boolean isEmpty();

   ItemStack getItem(int p_18941_);

   ItemStack removeItem(int p_18942_, int p_18943_);

   ItemStack removeItemNoUpdate(int p_18951_);

   void setItem(int p_18944_, ItemStack p_18945_);

   default int getMaxStackSize() {
      return 64;
   }

   void setChanged();

   boolean stillValid(Player p_18946_);

   default void startOpen(Player p_18955_) {
   }

   default void stopOpen(Player p_18954_) {
   }

   default boolean canPlaceItem(int p_18952_, ItemStack p_18953_) {
      return true;
   }

   default boolean canTakeItem(Container p_273520_, int p_272681_, ItemStack p_273702_) {
      return true;
   }

   default int countItem(Item p_18948_) {
      int i = 0;

      for(int j = 0; j < this.getContainerSize(); ++j) {
         ItemStack itemstack = this.getItem(j);
         if (itemstack.getItem().equals(p_18948_)) {
            i += itemstack.getCount();
         }
      }

      return i;
   }

   default boolean hasAnyOf(Set<Item> p_18950_) {
      return this.hasAnyMatching((p_216873_) -> {
         return !p_216873_.isEmpty() && p_18950_.contains(p_216873_.getItem());
      });
   }

   default boolean hasAnyMatching(Predicate<ItemStack> p_216875_) {
      for(int i = 0; i < this.getContainerSize(); ++i) {
         ItemStack itemstack = this.getItem(i);
         if (p_216875_.test(itemstack)) {
            return true;
         }
      }

      return false;
   }

   static boolean stillValidBlockEntity(BlockEntity p_273154_, Player p_273222_) {
      return stillValidBlockEntity(p_273154_, p_273222_, 8);
   }

   static boolean stillValidBlockEntity(BlockEntity p_272877_, Player p_272670_, int p_273411_) {
      Level level = p_272877_.getLevel();
      BlockPos blockpos = p_272877_.getBlockPos();
      if (level == null) {
         return false;
      } else if (level.getBlockEntity(blockpos) != p_272877_) {
         return false;
      } else {
         return p_272670_.distanceToSqr((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D) <= (double)(p_273411_ * p_273411_);
      }
   }
}