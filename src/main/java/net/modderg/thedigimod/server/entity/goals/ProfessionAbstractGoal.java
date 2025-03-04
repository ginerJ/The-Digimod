package net.modderg.thedigimod.server.entity.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.modderg.thedigimod.server.entity.DigimonEntity;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProfessionAbstractGoal extends Goal {

    protected DigimonEntity digimon;

    int workCount = 80;

    int resetValue = -1;

    int resetCount = resetValue;

    public ProfessionAbstractGoal(DigimonEntity cd){
        digimon = cd;
    }

    @Override
    public boolean canUse() {

        if(workCount-- == 0)
            workCount= digimon.getRandom().nextInt(10,50);

        return digimon.isTame() && workCount == 1 && digimon.getMovementID() == -2;
    }

    @Override
    public boolean canContinueToUse() {
        return digimon.getMovementID() == -2;
    }

    @Override
    public void tick() {
        super.tick();

        if(--resetCount == 0){
            resetCount = resetValue;
            stop();
        }

    }

    public void moveLookinTo(Vec3 pos, float speedMod){
        digimon.getLookControl().setLookAt(pos);
        digimon.getNavigation().moveTo(pos.x, pos.y, pos.z, speedMod);
    }

    public boolean hasItem(){
        return !digimon.getPickedItem().isEmpty();
    }

    protected interface BlockConditions {Boolean check(BlockPos blockPos);}

    protected boolean isBlockWorkable(BlockPos blockPos){return false;}

    protected boolean checkForWorkableBlocks(int radius) {

        BlockPos entityPos = digimon.blockPosition();
        Random rand = new Random();

        List<Integer> xs = IntStream.rangeClosed(-radius, radius).boxed().collect(Collectors.toList());
        List<Integer> ys = IntStream.rangeClosed(-radius - 4, radius + 4).boxed().collect(Collectors.toList());
        List<Integer> zs = IntStream.rangeClosed(-radius, radius).boxed().collect(Collectors.toList());

        Collections.shuffle(xs, rand);
        Collections.shuffle(ys, rand);
        Collections.shuffle(zs, rand);

        for (int y : ys) {
            for (int x : xs) {
                for (int z : zs) {
                    BlockPos currentPos = entityPos.offset(x, y, z);
                    if (isBlockWorkable(currentPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
