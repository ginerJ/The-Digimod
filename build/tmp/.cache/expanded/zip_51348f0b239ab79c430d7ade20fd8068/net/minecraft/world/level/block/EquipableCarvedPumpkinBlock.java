package net.minecraft.world.level.block;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class EquipableCarvedPumpkinBlock extends CarvedPumpkinBlock implements Equipable {
   public EquipableCarvedPumpkinBlock(BlockBehaviour.Properties p_289677_) {
      super(p_289677_);
   }

   public EquipmentSlot getEquipmentSlot() {
      return EquipmentSlot.HEAD;
   }
}