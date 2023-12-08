package net.modderg.thedigimod.entity.goals;

import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.modderg.thedigimod.entity.CustomDigimon;
import org.jetbrains.annotations.NotNull;

public class DigitalWaterPathNavigation extends WaterBoundPathNavigation {

    CustomDigimon digimon;

    public DigitalWaterPathNavigation(CustomDigimon p_26594_, Level p_26595_) {
        super(p_26594_, p_26595_);
        this.digimon = p_26594_;
    }

    @Override
    protected @NotNull PathFinder createPathFinder(int p_26598_) {
        this.nodeEvaluator = new SwimNodeEvaluator(true);
        return new PathFinder(this.nodeEvaluator, p_26598_);
    }

    @Override
    protected boolean canMoveDirectly(@NotNull Vec3 p_186138_, @NotNull Vec3 p_186139_) {
        return isClearForMovementBetween(this.mob, p_186138_, p_186139_, true);
    }

    @Override
    protected boolean canUpdatePath() {
        return this.isInLiquid() && !this.digimon.isOrderedToSit();
    }

}
