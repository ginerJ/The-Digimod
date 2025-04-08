package net.modderg.thedigimod.client.gui.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.modderg.thedigimod.server.entity.DigimonEntity;

public class DigimonInventory implements INBTSerializable<CompoundTag> {

    private ItemStackHandler stackHandler;

    private final LazyOptional<ItemStackHandler> inventoryCapability;

    private final DigimonEntity digimon;

    public DigimonInventory(DigimonEntity digimon, int sum) {

        this.digimon = digimon;

        genSlots(sum);

        inventoryCapability =  LazyOptional.of(() -> stackHandler);
    }

    public void genSlots(int sum) {
        boolean transporter = digimon.profession != null && digimon.profession.equals("transporter");
        int stage = digimon.getEvoStage() + (transporter ? 1:0) + sum;
        int size = 27;

        if(stage == 0)
            size = 6;
        else if(stage == 1)
            size = 9;
        else if(stage == 2)
            size = 15;
        else if(stage == 3)
            size = 21;

        if(this.stackHandler != null){
            ItemStackHandler newHandler = new ItemStackHandler(size);
            inventoryReplace(this.stackHandler, newHandler);
            this.stackHandler = newHandler;
        }
        else
            stackHandler = new ItemStackHandler(size);
    }

    public LazyOptional<ItemStackHandler> getInventoryCapability() {
        return inventoryCapability;
    }

    @Override
    public CompoundTag serializeNBT() {
        return stackHandler.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        stackHandler.deserializeNBT(nbt.getCompound("Inventory"));
    }

    public ItemStackHandler getStackHandler() {
        return stackHandler;
    }


    public void inventoryReplace(ItemStackHandler source, ItemStackHandler target) {
        for (int i = 0; i < source.getSlots(); i++) {
            ItemStack stack = source.getStackInSlot(i);
            if (!stack.isEmpty()) {
                for (int j = 0; j < target.getSlots(); j++) {
                    if(target.getStackInSlot(j).isEmpty()){
                        target.insertItem(j, stack, false);
                        stack = ItemStack.EMPTY;
                        break;
                    }
                }
                if (!stack.isEmpty())
                    dropItemStackInWorld(stack, digimon.level());
            }
        }
    }

    private void dropItemStackInWorld(ItemStack stack, Level world) {
        ItemEntity itemEntity = new ItemEntity(world, digimon.getX(), digimon.getY(), digimon.getZ(), stack);
        world.addFreshEntity(itemEntity);
    }
}
