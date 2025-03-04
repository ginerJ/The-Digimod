package net.modderg.thedigimod.server.entity.goals;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import net.modderg.thedigimod.server.item.TDItems;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class EatShitGoal extends Goal {
    ItemEntity itemTarget = null;
    DigimonEntity digimon;

    int hungryAgain = 10;

    public EatShitGoal(DigimonEntity cd) {
        this.digimon = cd;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return digimon.getMovementID() != 0 && hungryAgain++%40 == 0 && hasItemsToPick();
    }

    @Override
    public boolean canContinueToUse() {
        return digimon.getMovementID() != 0 && itemTarget != null;
    }

    @Override
    public void tick() {
        super.tick();

        if(itemTarget != null){

            moveLookinTo(itemTarget.position(), 1.1f);

            if (!itemTarget.isAddedToWorld()) {
                itemTarget = null;
                return;
            }

            if (digimon.distanceToSqr(this.itemTarget) < 2.5D) {

                digimon.isFood(itemTarget.getItem());
                itemTarget.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
                itemTarget = null;
            }
        }
    }

    public boolean hasItemsToPick(){

        AABB searchArea = new AABB(digimon.blockPosition()).inflate(15d);

        List<ItemEntity> items = digimon.level().getEntitiesOfClass(ItemEntity.class, searchArea);

        Collections.shuffle(items, new Random());

        itemTarget = items.stream().filter(i -> i.getItem().is(TDItems.POOP.get()) || i.getItem().is(TDItems.GOLD_POOP.get()))
                .findFirst().orElse(null);

        return itemTarget != null;
    }


    public void moveLookinTo(Vec3 pos, float speedMod){
        digimon.getLookControl().setLookAt(pos);
        digimon.getNavigation().moveTo(pos.x, pos.y, pos.z, speedMod);
    }

}
