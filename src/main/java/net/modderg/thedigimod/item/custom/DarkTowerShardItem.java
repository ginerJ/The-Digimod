package net.modderg.thedigimod.item.custom;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.sound.DigiSounds;

public class DarkTowerShardItem extends DigimonItem {
    public DarkTowerShardItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack item, Player player, LivingEntity entity, InteractionHand hand) {
        if(entity instanceof CustomDigimon cd && cd.getOwner() != null && cd.isOwnedBy(player)){
            cd.deEvolveDigimon();
            entity.playSound(DigiSounds.DIGITRON_SOUND.get(), 0.25F, 1.0F);
            return InteractionResult.CONSUME;
        }
        return super.interactLivingEntity(item, player, entity, hand);
    }
}
