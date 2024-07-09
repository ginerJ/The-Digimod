package net.modderg.thedigimod.entity.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.modderg.thedigimod.entity.CustomDigimon;

import java.util.ArrayList;
import java.util.Collections;

public class ProfessionMinerGoal extends ProfessionAbstractGoal{

    BlockPos targetBlock = null;

    public ProfessionMinerGoal(CustomDigimon cd) {
        super(cd);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && checkForWorkableBlocks(10);
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && targetBlock != null;
    }

    @Override
    public void stop() {
        super.stop();
        this.targetBlock = null;
    }

    int breakCount = 0;

    @Override
    public void tick() {
        super.tick();

        if(targetBlock != null){

            double distanceTo = digimon.distanceToSqr(targetBlock.getCenter());


            if(distanceTo < 10){
                if(breakCount-- == 0) {
                    if(isMineMaterial(digimon.level().getBlockState(targetBlock))) digimon.level().destroyBlock(targetBlock, true);
                    targetBlock = null;
                    checkForWorkableBlocks(10);
                } else if(breakCount % 20 == 0) {
                    digimon.playSound(SoundEvents.STONE_HIT);
                    if(digimon.level() instanceof ServerLevel)
                        digimon.triggerAnim("movement", "attack");
                }
            } else
                moveLookinTo(targetBlock.getCenter(), 1.1f);
        }
    }

    @Override
    protected boolean isBlockWorkable(BlockPos blockPos) {
        return checkMineableBlocks.check(blockPos);
    }

    BlockConditions checkMineableBlocks = (currentPos) -> {

        BlockState currentBlock = digimon.level().getBlockState(currentPos);

        if (isMineMaterial(currentBlock)) {
            this.targetBlock = currentPos;
            this.breakCount = (digimon.isRookie() ? 100 : digimon.isChampion() ? 80 : digimon.isUltimate() ? 60 : 40);
            this.resetCount = 200;
            return true;
        }

        return false;
    };

    public boolean isMineMaterial(BlockState block){
        return block.is(Blocks.STONE)|| block.is(Blocks.COBBLESTONE);
    }
}
