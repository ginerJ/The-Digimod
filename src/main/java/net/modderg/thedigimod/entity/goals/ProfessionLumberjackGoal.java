package net.modderg.thedigimod.entity.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.modderg.thedigimod.entity.CustomDigimon;

public class ProfessionLumberjackGoal extends ProfessionAbstractGoal{

    BlockPos targetLog = null;

    public ProfessionLumberjackGoal(CustomDigimon cd) {
        super(cd);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && checkForWorkableBlocks(10);
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && targetLog != null;
    }

    @Override
    public void stop() {
        super.stop();
        this.targetLog = null;
    }

    int breakCount = 0;

    @Override
    public void tick() {
        super.tick();
        if(targetLog != null){

            double distanceTo = digimon.distanceToSqr(targetLog.getCenter());

            if(distanceTo < 10){
                if(breakCount-- == 0) {
                    if(isLog(digimon.level().getBlockState(targetLog))) digimon.level().destroyBlock(targetLog, true);
                    targetLog = null;
                    checkForWorkableBlocks(10);
                } else if(breakCount % 20 == 0) {
                    digimon.playSound(SoundEvents.STONE_HIT);
                    if(digimon.level() instanceof ServerLevel)
                        digimon.triggerAnim("movement", "attack");
                }
            } else
                moveLookinTo(targetLog.getCenter(), 1.1f);
        }
    }

    @Override
    protected boolean isBlockWorkable(BlockPos blockPos) {
        return checkMineableBlocks.check(blockPos);
    }

    BlockConditions checkMineableBlocks = (currentPos) -> {

        BlockState currentBlock = digimon.level().getBlockState(currentPos);

        if(isLog(currentBlock)){
            this.targetLog = currentPos;
            this.breakCount = (digimon.isRookie() ? 80 : digimon.isChampion() ? 60 : digimon.isUltimate() ? 40 : 20);
            this.resetCount = 180;
            return true;
        }

        return false;
    };

    public boolean isLog(BlockState block){
        return  block.is(Blocks.ACACIA_LOG)||
                block.is(Blocks.BIRCH_LOG)||
                block.is(Blocks.CHERRY_LOG)||
                block.is(Blocks.JUNGLE_LOG)||
                block.is(Blocks.MANGROVE_LOG)||
                block.is(Blocks.DARK_OAK_LOG)||
                block.is(Blocks.OAK_LOG)||
                block.is(Blocks.SPRUCE_LOG);
    }
}
