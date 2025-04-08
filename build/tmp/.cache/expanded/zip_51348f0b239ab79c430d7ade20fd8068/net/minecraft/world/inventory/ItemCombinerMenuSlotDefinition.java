package net.minecraft.world.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.world.item.ItemStack;

public class ItemCombinerMenuSlotDefinition {
   private final List<ItemCombinerMenuSlotDefinition.SlotDefinition> slots;
   private final ItemCombinerMenuSlotDefinition.SlotDefinition resultSlot;

   ItemCombinerMenuSlotDefinition(List<ItemCombinerMenuSlotDefinition.SlotDefinition> p_266947_, ItemCombinerMenuSlotDefinition.SlotDefinition p_266715_) {
      if (!p_266947_.isEmpty() && !p_266715_.equals(ItemCombinerMenuSlotDefinition.SlotDefinition.EMPTY)) {
         this.slots = p_266947_;
         this.resultSlot = p_266715_;
      } else {
         throw new IllegalArgumentException("Need to define both inputSlots and resultSlot");
      }
   }

   public static ItemCombinerMenuSlotDefinition.Builder create() {
      return new ItemCombinerMenuSlotDefinition.Builder();
   }

   public boolean hasSlot(int p_267185_) {
      return this.slots.size() >= p_267185_;
   }

   public ItemCombinerMenuSlotDefinition.SlotDefinition getSlot(int p_266907_) {
      return this.slots.get(p_266907_);
   }

   public ItemCombinerMenuSlotDefinition.SlotDefinition getResultSlot() {
      return this.resultSlot;
   }

   public List<ItemCombinerMenuSlotDefinition.SlotDefinition> getSlots() {
      return this.slots;
   }

   public int getNumOfInputSlots() {
      return this.slots.size();
   }

   public int getResultSlotIndex() {
      return this.getNumOfInputSlots();
   }

   public List<Integer> getInputSlotIndexes() {
      return this.slots.stream().map(ItemCombinerMenuSlotDefinition.SlotDefinition::slotIndex).collect(Collectors.toList());
   }

   public static class Builder {
      private final List<ItemCombinerMenuSlotDefinition.SlotDefinition> slots = new ArrayList<>();
      private ItemCombinerMenuSlotDefinition.SlotDefinition resultSlot = ItemCombinerMenuSlotDefinition.SlotDefinition.EMPTY;

      public ItemCombinerMenuSlotDefinition.Builder withSlot(int p_267315_, int p_267028_, int p_266815_, Predicate<ItemStack> p_267120_) {
         this.slots.add(new ItemCombinerMenuSlotDefinition.SlotDefinition(p_267315_, p_267028_, p_266815_, p_267120_));
         return this;
      }

      public ItemCombinerMenuSlotDefinition.Builder withResultSlot(int p_267180_, int p_267130_, int p_266910_) {
         this.resultSlot = new ItemCombinerMenuSlotDefinition.SlotDefinition(p_267180_, p_267130_, p_266910_, (p_266825_) -> {
            return false;
         });
         return this;
      }

      public ItemCombinerMenuSlotDefinition build() {
         return new ItemCombinerMenuSlotDefinition(this.slots, this.resultSlot);
      }
   }

   public static record SlotDefinition(int slotIndex, int x, int y, Predicate<ItemStack> mayPlace) {
      static final ItemCombinerMenuSlotDefinition.SlotDefinition EMPTY = new ItemCombinerMenuSlotDefinition.SlotDefinition(0, 0, 0, (p_267109_) -> {
         return true;
      });
   }
}