package net.minecraft.world.entity.npc;

import net.minecraft.world.entity.VariantHolder;

public interface VillagerDataHolder extends VariantHolder<VillagerType> {
   VillagerData getVillagerData();

   void setVillagerData(VillagerData p_150027_);

   default VillagerType getVariant() {
      return this.getVillagerData().getType();
   }

   default void setVariant(VillagerType p_262647_) {
      this.setVillagerData(this.getVillagerData().setType(p_262647_));
   }
}