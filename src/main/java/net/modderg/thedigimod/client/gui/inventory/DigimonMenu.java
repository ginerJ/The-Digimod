package net.modderg.thedigimod.client.gui.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DigimonMenu extends AbstractContainerMenu {
    private final DigimonEntity digimon;

    public DigimonMenu(int container_id, Inventory playerInventory, Entity digimon) {
        super(InitMenu.DIGIMON_CONTAINER.get(), container_id);
        this.digimon = (DigimonEntity) digimon;

        createPlayerHotbar(playerInventory);
        createPlayerInventory(playerInventory);
        createDigimonInventory(this.digimon);
    }

    public DigimonMenu(int container_id, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(container_id, playerInventory, Objects.requireNonNull(playerInventory.player.level().getEntity(extraData.readInt())));
    }

    public DigimonEntity getDigimon() {
        return digimon;
    }

    private void createPlayerHotbar(Inventory playerInventory){
        for(int column = 0; column < 9; column++)
            addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
    }

    private void createPlayerInventory(Inventory playerInventory) {
        for(int row = 0; row < 3; row++)
            for(int column = 0; column < 9; column++)
                addSlot(new Slot(playerInventory, 9 + column + row * 9, 8 + column * 18, 84 + row*18));
    }

    private void createDigimonInventory(DigimonEntity digimon){
        digimon.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inventory -> {

            int maxColumn = !digimon.isBaby() ? inventory.getSlots()/3 : 3;
            int columnOffset = (3 - maxColumn)/2;

            for(int row = 0; row < (!digimon.isBaby() ? 3 : 2); row++)
                for(int column = 0; column < maxColumn; column++)
                    addSlot(new SlotItemHandler(inventory, maxColumn * row + column, 62 + column * 18 + columnOffset * 18, 17 + row * 18));
        });
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return this.digimon.isAlive();
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int pIndex) {
        Slot fromSlot = getSlot(pIndex);
        if (!fromSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack fromStack = fromSlot.getItem();
        ItemStack copyFromStack = fromStack.copy();

        int playerInvSize = 36;
        int digimonInvSize = playerInvSize + digimon.getInventory().getStackHandler().getSlots();

        if (pIndex < playerInvSize) {
            if (!moveItemStackTo(fromStack, playerInvSize, digimonInvSize, false))
                return ItemStack.EMPTY;
        } else if (pIndex < digimonInvSize) {
            if (!moveItemStackTo(fromStack, 0, playerInvSize, false))
                return ItemStack.EMPTY;
        }

        if (fromStack.isEmpty())
            fromSlot.set(ItemStack.EMPTY);
        else
            fromSlot.setChanged();

        if (fromStack.getCount() == copyFromStack.getCount())
            return ItemStack.EMPTY;

        fromSlot.onTake(playerIn, fromStack);

        return copyFromStack;
    }

}
