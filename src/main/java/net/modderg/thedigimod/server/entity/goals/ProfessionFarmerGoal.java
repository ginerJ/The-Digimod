package net.modderg.thedigimod.server.entity.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import net.modderg.thedigimod.server.item.TDItems;

import java.util.stream.IntStream;

public class ProfessionFarmerGoal extends ProfessionAbstractGoal{

    ChestBlockEntity foundChest = null;
    BlockPos foundCrop = null;
    BlockPos farmland = null;

    public ProfessionFarmerGoal(DigimonEntity cd) {super(cd);}

    @Override
    public boolean canUse() {
        return super.canUse() && checkForWorkableBlocks(10);
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && (foundCrop != null || foundChest != null || farmland != null);
    }

    @Override
    public void stop() {
        super.stop();
        foundChest = null;
        foundCrop = null;
        farmland = null;
    }

    @Override
    public void tick() {
        super.tick();

        if(foundChest != null){
            BlockPos pos = foundChest.getBlockPos();

            moveLookinTo(pos.getCenter(), 1.1f);

            if(digimon.distanceToSqr(pos.getCenter()) < 3.5D)
                if(digimon.level().getBlockEntity(pos) instanceof ChestBlockEntity){
                    takeCropFromChest();
                    foundChest = null;
                    checkForWorkableBlocks(10);
                }
        }

        if(farmland != null && isCropItem(digimon.getPickedItem())) {

            BlockPos pos = farmland.above();

            moveLookinTo(pos.getCenter(), 0.9f);

            if (digimon.distanceToSqr(pos.getCenter()) < 3D) {

                if (!digimon.level().isClientSide() && digimon.getPickedItem().getItem() instanceof ItemNameBlockItem blockItem &&
                        (!digimon.level().getBlockState(pos).is(Blocks.AIR) || digimon.level().getBlockState(farmland).is(Blocks.FARMLAND))) {

                    digimon.level().setBlock(pos, blockItem.getBlock().defaultBlockState(), 3);

                    digimon.playSound(SoundEvents.GRASS_PLACE);

                    ItemStack restCrop = digimon.getPickedItem();

                    restCrop.setCount(restCrop.getCount() - 1);

                    digimon.setPickedItem(restCrop);

                    farmland = null;
                }
            }
        }

        if(foundCrop != null){

            moveLookinTo(foundCrop.getCenter(), 1.1f);

            if(digimon.distanceToSqr(foundCrop.getCenter()) < 3.5D){
                digimon.level().destroyBlock(foundCrop, true);
                foundCrop = null;
            }
        }
    }

    @Override
    protected boolean isBlockWorkable(BlockPos blockPos) {
        return checkCrops.check(blockPos) ||
                (this.hasItem() && checkFarmland.check(blockPos)) ||
                (!this.hasItem() && checkChestWithCrops.check(blockPos));
    }

    BlockConditions checkCrops = (currentPos) -> {

        BlockState currentState = digimon.level().getBlockState(currentPos);

        if(currentState.getBlock() instanceof CropBlock crop && crop.isMaxAge(currentState)) {
            foundCrop = currentPos;
            resetCount = 60;
            return true;
        }
        return false;
    };

    BlockConditions checkFarmland = (currentPos) -> {

        BlockState currentBlock = digimon.level().getBlockState(currentPos);

        if (currentBlock.is(Blocks.FARMLAND) && digimon.level().getBlockState(currentPos.above()).is(Blocks.AIR)) {
            farmland = currentPos;
            resetCount = 80;
            return true;
        }

        return false;
    };


    BlockConditions checkChestWithCrops = (currentPos) -> {

        BlockEntity currentBlock = digimon.level().getBlockEntity(currentPos);

        if(currentBlock instanceof ChestBlockEntity chest) {
            IItemHandler handler =  chest.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);

            if(handler == null)
                return false;

            if(IntStream.range(0,chest.getContainerSize())
                                .anyMatch(i -> isCropItem(handler.getStackInSlot(i)))) {
                foundChest = chest;
                resetCount = 100;
                return true;
            }
        }
        return false;
    };

    public void takeCropFromChest(){
        foundChest.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(
                handler -> {
                    int slot = IntStream.range(0, foundChest.getContainerSize())
                            .filter(i -> isCropItem(handler.getStackInSlot(i)))
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

    public boolean isCropItem(ItemStack item){
        return (item.is(ItemTags.VILLAGER_PLANTABLE_SEEDS)) || item.is(TDItems.DIGI_MEAT.get());
    }
}
