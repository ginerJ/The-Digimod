package net.modderg.thedigimod.server.item.custom;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import net.modderg.thedigimod.server.sound.DigiSounds;

public class BossCubeItem extends DigimonItem{
    public BossCubeItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player p_41399_, LivingEntity entity, InteractionHand p_41401_) {

        if(entity instanceof DigimonEntity cd){
            cd.setBoss(true);
            cd.playSound(DigiSounds.XP_GAIN_SOUND.get(), 1.0F, 1.0F);
            cd.playSound(DigiSounds.LEVEL_UP_SOUND.get(), 1.0F, 1.0F);
            cd.eatItemAnim(itemStack);
        }

        return super.interactLivingEntity(itemStack, p_41399_, entity, p_41401_);
    }
}
