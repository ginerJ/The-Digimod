package net.minecraft.world.inventory;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ItemCombinerMenu extends AbstractContainerMenu {
   private static final int INVENTORY_SLOTS_PER_ROW = 9;
   private static final int INVENTORY_SLOTS_PER_COLUMN = 3;
   protected final ContainerLevelAccess access;
   protected final Player player;
   protected final Container inputSlots;
   private final List<Integer> inputSlotIndexes;
   protected final ResultContainer resultSlots = new ResultContainer();
   private final int resultSlotIndex;

   protected abstract boolean mayPickup(Player p_39798_, boolean p_39799_);

   protected abstract void onTake(Player p_150601_, ItemStack p_150602_);

   protected abstract boolean isValidBlock(BlockState p_39788_);

   public ItemCombinerMenu(@Nullable MenuType<?> p_39773_, int p_39774_, Inventory p_39775_, ContainerLevelAccess p_39776_) {
      super(p_39773_, p_39774_);
      this.access = p_39776_;
      this.player = p_39775_.player;
      ItemCombinerMenuSlotDefinition itemcombinermenuslotdefinition = this.createInputSlotDefinitions();
      this.inputSlots = this.createContainer(itemcombinermenuslotdefinition.getNumOfInputSlots());
      this.inputSlotIndexes = itemcombinermenuslotdefinition.getInputSlotIndexes();
      this.resultSlotIndex = itemcombinermenuslotdefinition.getResultSlotIndex();
      this.createInputSlots(itemcombinermenuslotdefinition);
      this.createResultSlot(itemcombinermenuslotdefinition);
      this.createInventorySlots(p_39775_);
   }

   private void createInputSlots(ItemCombinerMenuSlotDefinition p_267172_) {
      for(final ItemCombinerMenuSlotDefinition.SlotDefinition itemcombinermenuslotdefinition$slotdefinition : p_267172_.getSlots()) {
         this.addSlot(new Slot(this.inputSlots, itemcombinermenuslotdefinition$slotdefinition.slotIndex(), itemcombinermenuslotdefinition$slotdefinition.x(), itemcombinermenuslotdefinition$slotdefinition.y()) {
            public boolean mayPlace(ItemStack p_267156_) {
               return itemcombinermenuslotdefinition$slotdefinition.mayPlace().test(p_267156_);
            }
         });
      }

   }

   private void createResultSlot(ItemCombinerMenuSlotDefinition p_267000_) {
      this.addSlot(new Slot(this.resultSlots, p_267000_.getResultSlot().slotIndex(), p_267000_.getResultSlot().x(), p_267000_.getResultSlot().y()) {
         public boolean mayPlace(ItemStack p_39818_) {
            return false;
         }

         public boolean mayPickup(Player p_39813_) {
            return ItemCombinerMenu.this.mayPickup(p_39813_, this.hasItem());
         }

         public void onTake(Player p_150604_, ItemStack p_150605_) {
            ItemCombinerMenu.this.onTake(p_150604_, p_150605_);
         }
      });
   }

   private void createInventorySlots(Inventory p_267325_) {
      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_267325_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_267325_, k, 8 + k * 18, 142));
      }

   }

   public abstract void createResult();

   protected abstract ItemCombinerMenuSlotDefinition createInputSlotDefinitions();

   private SimpleContainer createContainer(int p_267204_) {
      return new SimpleContainer(p_267204_) {
         public void setChanged() {
            super.setChanged();
            ItemCombinerMenu.this.slotsChanged(this);
         }
      };
   }

   public void slotsChanged(Container p_39778_) {
      super.slotsChanged(p_39778_);
      if (p_39778_ == this.inputSlots) {
         this.createResult();
      }

   }

   public void removed(Player p_39790_) {
      super.removed(p_39790_);
      this.access.execute((p_39796_, p_39797_) -> {
         this.clearContainer(p_39790_, this.inputSlots);
      });
   }

   public boolean stillValid(Player p_39780_) {
      return this.access.evaluate((p_39785_, p_39786_) -> {
         return !this.isValidBlock(p_39785_.getBlockState(p_39786_)) ? false : p_39780_.distanceToSqr((double)p_39786_.getX() + 0.5D, (double)p_39786_.getY() + 0.5D, (double)p_39786_.getZ() + 0.5D) <= 64.0D;
      }, true);
   }

   public ItemStack quickMoveStack(Player p_39792_, int p_39793_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_39793_);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         int i = this.getInventorySlotStart();
         int j = this.getUseRowEnd();
         if (p_39793_ == this.getResultSlot()) {
            if (!this.moveItemStackTo(itemstack1, i, j, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemstack1, itemstack);
         } else if (this.inputSlotIndexes.contains(p_39793_)) {
            if (!this.moveItemStackTo(itemstack1, i, j, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.canMoveIntoInputSlots(itemstack1) && p_39793_ >= this.getInventorySlotStart() && p_39793_ < this.getUseRowEnd()) {
            int k = this.getSlotToQuickMoveTo(itemstack);
            if (!this.moveItemStackTo(itemstack1, k, this.getResultSlot(), false)) {
               return ItemStack.EMPTY;
            }
         } else if (p_39793_ >= this.getInventorySlotStart() && p_39793_ < this.getInventorySlotEnd()) {
            if (!this.moveItemStackTo(itemstack1, this.getUseRowStart(), this.getUseRowEnd(), false)) {
               return ItemStack.EMPTY;
            }
         } else if (p_39793_ >= this.getUseRowStart() && p_39793_ < this.getUseRowEnd() && !this.moveItemStackTo(itemstack1, this.getInventorySlotStart(), this.getInventorySlotEnd(), false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(p_39792_, itemstack1);
      }

      return itemstack;
   }

   protected boolean canMoveIntoInputSlots(ItemStack p_39787_) {
      return true;
   }

   public int getSlotToQuickMoveTo(ItemStack p_267159_) {
      return this.inputSlots.isEmpty() ? 0 : this.inputSlotIndexes.get(0);
   }

   public int getResultSlot() {
      return this.resultSlotIndex;
   }

   private int getInventorySlotStart() {
      return this.getResultSlot() + 1;
   }

   private int getInventorySlotEnd() {
      return this.getInventorySlotStart() + 27;
   }

   private int getUseRowStart() {
      return this.getInventorySlotEnd();
   }

   private int getUseRowEnd() {
      return this.getUseRowStart() + 9;
   }
}