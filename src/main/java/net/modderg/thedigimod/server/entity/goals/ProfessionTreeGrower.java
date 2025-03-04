package net.modderg.thedigimod.server.entity.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.modderg.thedigimod.server.entity.DigimonEntity;

import java.util.stream.IntStream;

public class ProfessionTreeGrower extends ProfessionAbstractGoal{

    ItemEntity itemTarget = null;
    ChestBlockEntity foundChest = null;
    BlockPos dirt = null;

    public ProfessionTreeGrower(DigimonEntity cd) {
        super(cd);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && (
                ( !hasItem() && (hasItemsToPick()) || checkForWorkableBlocks(10)));
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && itemTarget != null && foundChest != null && dirt != null;
    }

    @Override
    public void stop() {
        super.stop();
        itemTarget = null;
        foundChest = null;
        dirt = null;
    }

    @Override
    public void tick() {
        if (itemTarget != null) {

            if (!itemTarget.isAddedToWorld()) {
                itemTarget = null;
                return;
            }

            moveLookinTo(itemTarget.position(), 1.1f);


            if (digimon.distanceToSqr(this.itemTarget) < 2.2D) {

                digimon.setPickedItem(this.itemTarget.getItem());
                digimon.playSound(SoundEvents.ITEM_PICKUP, 0.15F, 1.0F);
                itemTarget.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
                checkForWorkableBlocks(10);
                itemTarget = null;
            }
        }

        if (foundChest != null) {
            BlockPos pos = foundChest.getBlockPos();

            moveLookinTo(foundChest.getBlockPos().getCenter(), 1.1f);

            if (foundChest != null) {
                if (digimon.distanceToSqr(pos.getCenter()) < 3.5D) {
                    takeSaplingFromChest();
                    checkForWorkableBlocks(10);
                }
            }
        }

        if (dirt != null) {

            BlockPos pos = dirt.above();

            moveLookinTo(pos.getCenter(), 0.9f);

            if (!digimon.level().getBlockState(pos).is(Blocks.AIR) || !digimon.level().getBlockState(dirt).is(Blocks.DIRT))
                dirt = null;

            if (digimon.distanceToSqr(pos.getCenter()) < 3D) {
                if (!digimon.level().isClientSide() && digimon.getPickedItem().getItem() instanceof BlockItem blockItem) {
                    digimon.level().setBlock(pos, blockItem.getBlock().defaultBlockState(), 3);
                    digimon.playSound(SoundEvents.GRASS_PLACE);
                    ItemStack restSapling = digimon.getPickedItem();
                    restSapling.setCount(restSapling.getCount() - 1);
                    digimon.setPickedItem(restSapling);
                }
            }
        }
    }

    @Override
    protected boolean isBlockWorkable(BlockPos blockPos) {
        return (!hasItem() && checkChestSapling.check(blockPos)) || (hasItem() && checkDirt.check(blockPos));
    }

    BlockConditions checkChestSapling  = (currentPos) -> {

        BlockEntity currentBlock = digimon.level().getBlockEntity(currentPos);

        if(currentBlock instanceof ChestBlockEntity chest) {
            IItemHandler handler =  chest.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);

            if(handler == null)
                return false;

            if(IntStream.range(0,chest.getContainerSize())
                    .anyMatch(i -> handler.getStackInSlot(i).is(ItemTags.SAPLINGS))) {
                foundChest = chest;
                this.resetCount = 100;
                return true;
            }
        }
        return false;
    };

    BlockConditions checkDirt  = (currentPos) -> {

        BlockState currentBlock = digimon.level().getBlockState(currentPos);

        if(currentBlock.is(Blocks.DIRT) && digimon.level().getBlockState(currentPos.above()).is(Blocks.AIR)) {
            dirt = currentPos;
            this.resetCount = 100;
            return true;
        }
        return false;
    };

    public boolean hasItemsToPick(){

        AABB searchArea = new AABB(digimon.blockPosition()).inflate(15d);

        itemTarget = digimon.level().getEntitiesOfClass(ItemEntity.class, searchArea)
                .stream()
                .filter(i-> i.getItem().is(ItemTags.SAPLINGS))
                .findFirst()
                .orElse(null);
        this.resetCount = 100;
        return itemTarget != null;
    }

    public void takeSaplingFromChest(){
        foundChest.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(
                handler -> {
                    int slot = IntStream.range(0, foundChest.getContainerSize())
                            .filter(i -> handler.getStackInSlot(i).is(ItemTags.SAPLINGS))
                            .findFirst()
                            .orElse(-1);
                    if (slot == -1) stop();
                    else {
                        ItemStack newItem = handler.extractItem(slot,1,false);
                        digimon.setPickedItem(newItem);
                        foundChest = null;
                    }
                }
        );
    }
}
