package net.modderg.thedigimod.entity.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.modderg.thedigimod.entity.CustomDigimon;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class ProfessionTransporterGoal extends ProfessionAbstractGoal {

    ItemEntity itemTarget = null;
    ChestBlockEntity foundChest = null;

    public ProfessionTransporterGoal(CustomDigimon cd) {
        super(cd);
    }

    @Override
    public boolean canUse() {
        return super.canUse() &&
                ((hasItem() && checkForWorkableBlocks(10)) || (!hasItem() && hasItemsToPick()));
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && (foundChest != null || itemTarget != null);
    }

    @Override
    public void stop() {
        super.stop();
        this.itemTarget = null;
        this.foundChest = null;
    }

    @Override
    public void tick() {
        super.tick();

        if (foundChest != null) {

            BlockPos pos = foundChest.getBlockPos();

            moveLookinTo(foundChest.getBlockPos().getCenter(), 0.9f);

            if (digimon.distanceToSqr(pos.getCenter()) < 3.5D) {
                if(digimon.level().getBlockEntity(pos) instanceof ChestBlockEntity) putStuffInChest();
                foundChest = null;
            }
        }

        if(itemTarget != null){

            moveLookinTo(itemTarget.position(), 1.1f);

            if (!itemTarget.isAddedToWorld()) {
                itemTarget = null;
                return;
            }

            if (digimon.distanceToSqr(this.itemTarget) < 2.5D) {

                digimon.setPickedItem(this.itemTarget.getItem());
                digimon.playSound(SoundEvents.ITEM_PICKUP, 0.15F, 1.0F);
                itemTarget.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
                checkForWorkableBlocks(10);
                itemTarget = null;
            }
        }
    }

    @Override
    protected boolean isBlockWorkable(BlockPos blockPos) {
        return checkFreeSpaceChest.check(blockPos);
    }

    public boolean hasItemsToPick(){

        AABB searchArea = new AABB(digimon.blockPosition()).inflate(15d);

        List<ItemEntity> items = digimon.level().getEntitiesOfClass(ItemEntity.class, searchArea);

        Collections.shuffle(items, new Random());

        itemTarget = items.stream().findFirst().orElse(null);
        this.resetCount = 100;
        return itemTarget != null;
    }

    BlockConditions checkFreeSpaceChest = (currentPos) -> {

        BlockEntity currentBlock = digimon.level().getBlockEntity(currentPos);

        if(currentBlock instanceof ChestBlockEntity chest) {
            IItemHandler handler =  chest.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);

            if(IntStream.range(0,chest.getContainerSize())
                                .anyMatch(i -> handler.insertItem(i, digimon.getPickedItem(), true).getCount() < digimon.getPickedItem().getCount())) {
                foundChest = chest;
                this.resetCount = 100;
                return true;
            }
        }
        return false;
    };

    public void putStuffInChest(){
        IItemHandler handler = foundChest.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);

        int slot = IntStream.range(0, foundChest.getContainerSize())
                .filter(i -> handler.insertItem(i, digimon.getPickedItem(), true).getCount() < digimon.getPickedItem().getCount())
                .findFirst()
                .orElse(-1);
        if (slot == -1) foundChest = null;
        else {
            ItemStack newItem = handler.insertItem(slot, digimon.getPickedItem(), false);
            digimon.setPickedItem(newItem);
            hasItemsToPick();
            foundChest = null;
        }
    }
}
