package net.modderg.thedigimod.server.projectiles.effects;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.BlockHitResult;
import net.modderg.thedigimod.server.TDConfig;

public class ProjectileEffectFire extends ProjectileEffect{

    int time;

    ItemStack flint = new ItemStack(Items.FLINT_AND_STEEL);

    public ProjectileEffectFire(int time){
        this.time = time;
    }

    @Override
    public void applyEffects(Entity target) {
        target.setSecondsOnFire(time);
    }

    @Override
    public void applyBlockEffects(BlockHitResult target) {
        if((TDConfig.FIRE_ATTACKS.get() && projectile.level().getGameRules().getRule(GameRules.RULE_MOBGRIEFING).get()) && !projectile.level().players().isEmpty())
            flint.useOn(new UseOnContext(projectile.level(), projectile.level().players().get(0), InteractionHand.MAIN_HAND, flint, target));
    }
}
