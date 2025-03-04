package net.modderg.thedigimod.server.item.custom;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.modderg.thedigimod.server.entity.DigimonEntity;

public class CustomXpItem extends DigimonItem {
    public int xpId;
    public int charges;

    public CustomXpItem(Properties p_41383_, int id, int charges) {
        super(p_41383_);
        xpId = id;
        this.charges = charges;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack item, Player player, LivingEntity entity, InteractionHand hand) {
        if(entity instanceof DigimonEntity){
            DigimonEntity digimon = (DigimonEntity) entity;
            if(!digimon.isEvolving()){
                for(int i = 0; i < charges; i++)
                    if(!digimon.level().isClientSide())
                        digimon.gainSpecificXp(xpId);

                item.shrink(1);
                return InteractionResult.CONSUME;
            }
        }
        return super.interactLivingEntity(item, player, entity, hand);
    }
}
