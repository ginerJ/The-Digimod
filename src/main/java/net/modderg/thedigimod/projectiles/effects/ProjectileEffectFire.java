package net.modderg.thedigimod.projectiles.effects;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.modderg.thedigimod.config.ModCommonConfigs;

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
        if(ModCommonConfigs.FIRE_ATTACKS.get())
            flint.useOn(new UseOnContext(projectile.level(), null, InteractionHand.MAIN_HAND, flint, target));
    }
}
