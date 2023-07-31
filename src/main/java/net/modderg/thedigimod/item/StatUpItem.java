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
                cd.setAttackStat(cd.MAXMEGASTAT);
            } else if(stat == "spattack"){
                cd.setSpAttackStat(cd.MAXMEGASTAT);
            } else if(stat == "defence"){
                cd.setDefenceStat(cd.MAXMEGASTAT);
            } else if(stat == "spdefence"){
                cd.setSpDefenceStat(cd.MAXMEGASTAT);
            } else if(stat == "health"){
                cd.setHealthStat(cd.MAXMEGASTAT);
                cd.setHealth(cd.MAXMEGASTAT);
            } else if(stat == "battle"){
                cd.setBattlesStat(cd.getBattlesStat() + 5);
            } else if(stat == "mistakes"){
                cd.setCareMistakesStat(cd.getCareMistakesStat() + 1);
            } else if(stat == "lifes"){
                cd.addLife();
                item.shrink(1);
            }
        }
        return super.interactLivingEntity(item, player, entity, hand);
    }
}
