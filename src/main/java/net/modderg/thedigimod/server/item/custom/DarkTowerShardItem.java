package net.modderg.thedigimod.server.item.custom;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.modderg.thedigimod.server.advancements.TDAdvancements;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import net.modderg.thedigimod.server.sound.DigiSounds;
import org.jetbrains.annotations.NotNull;

public class DarkTowerShardItem extends DigimonItem {
    public DarkTowerShardItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack item, @NotNull Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        if(entity instanceof DigimonEntity cd && cd.getOwner() != null && cd.isOwnedBy(player)){
            cd.deEvolveDigimon();
            entity.playSound(DigiSounds.DIGITRON_SOUND.get(), 0.25F, 1.0F);
            return InteractionResult.CONSUME;
        }
        return super.interactLivingEntity(item, player, entity, hand);
    }
}
