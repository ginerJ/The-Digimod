package net.modderg.thedigimod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.modderg.thedigimod.entity.CustomDigimon;

public class CustomXpItem extends Item {
    private int xpId;
    private int charges;

    public int getXpId(){
        return xpId;
    }

    public CustomXpItem(Properties p_41383_, int id, int charges) {
        super(p_41383_);
        xpId = id;
        this.charges = charges;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack item, Player player, LivingEntity entity, InteractionHand hand) {
        if(entity instanceof CustomDigimon){
            CustomDigimon digimon = (CustomDigimon) entity;
            if(!digimon.isEvolving()){
                for(int i = 0; i < charges; i++){
                    digimon.useXpItem(xpId);
                }
                player.getItemInHand(hand).shrink(1);
                return InteractionResult.CONSUME;
            }
        }
        return super.interactLivingEntity(item, player, entity, hand);
    }
}
