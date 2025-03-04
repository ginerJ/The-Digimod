package net.modderg.thedigimod.server.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StatUpItem extends DigimonItem {

    private String stat;

    private int addition;

    public StatUpItem(Properties p_41383_, String st, int addition) {
        super(p_41383_);
        this.stat = st;
        this.addition = addition;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack item, Player player, LivingEntity entity, InteractionHand hand) {
        if(entity instanceof DigimonEntity cd && (player.isCreative() || cd.isTame())){
            if(stat == "attack"){
                cd.setAttackStat(cd.getAttackStat() + addition);
            } else if(stat == "spattack"){
                cd.setSpAttackStat(cd.getSpAttackStat() + addition);
            } else if(stat == "defence"){
                cd.setDefenceStat(cd.getDefenceStat() + addition);
            } else if(stat == "spdefence"){
                cd.setSpDefenceStat(cd.getSpDefenceStat() + addition);
            } else if(stat == "health"){
                cd.setHealthStat(cd.getHealthStat() + addition);
            } else if(stat == "battle"){
                cd.setBattlesStat(cd.getBattlesStat() + 5);
            } else if(stat == "mistakes"){
                cd.setCareMistakesStat(cd.getCareMistakesStat() + 1);
            } else if(stat == "lifes"){
                cd.addLife();
            }
            cd.eatItemAnim(item);
        }
        return super.interactLivingEntity(item, player, entity, hand);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> components, TooltipFlag p_41424_) {
        components.add(Component.empty());
        components.add(Component.literal(" Provides " + stat + " to a digimon"));

        super.appendHoverText(p_41421_, p_41422_, components, p_41424_);
    }
}
