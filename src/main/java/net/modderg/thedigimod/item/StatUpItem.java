package net.modderg.thedigimod.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.modderg.thedigimod.entity.CustomDigimon;

public class StatUpItem extends Item {

    private String stat;

    public StatUpItem(Properties p_41383_, String st) {
        super(p_41383_);
        stat = st;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack item, Player player, LivingEntity entity, InteractionHand hand) {
        if(entity instanceof CustomDigimon cd){
            if(stat == "attack"){
                cd.setAttackStat(cd.MAXULTSTAT);
            } else if(stat == "spattack"){
                cd.setSpAttackStat(cd.MAXULTSTAT);
            } else if(stat == "defence"){
                cd.setDefenceStat(cd.MAXULTSTAT);
            } else if(stat == "spdefence"){
                cd.setSpDefenceStat(cd.MAXULTSTAT);
            } else if(stat == "health"){
                cd.setHealthStat(cd.MAXULTSTAT);
                cd.setHealth(cd.MAXMEGASTAT);
            } else if(stat == "battle"){
                cd.setBattlesStat(cd.getBattlesStat() + 5);
            }
        }
        return super.interactLivingEntity(item, player, entity, hand);
    }
}
