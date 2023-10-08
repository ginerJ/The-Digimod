package net.modderg.thedigimod.item.custom;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.modderg.thedigimod.entity.CustomDigimon;

public class TameItem extends Item {
    public TameItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack p_41398_, Player player, LivingEntity entity, InteractionHand p_41401_) {
        if(entity instanceof CustomDigimon cd && cd.getOwner() == null){
            cd.tame(player);
        }
        return super.interactLivingEntity(p_41398_, player, entity, p_41401_);
    }
}
