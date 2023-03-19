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
    public CustomXpItem(Properties p_41383_, int id) {
        super(p_41383_);
        xpId = id;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack item, Player player, LivingEntity entity, InteractionHand hand) {
        if(entity instanceof CustomDigimon){
            CustomDigimon digimon = (CustomDigimon) entity;
            if(!digimon.isEvolving()){
                digimon.useXpItem(xpId);
                player.sendSystemMessage(Component.literal("Name: " + digimon.getNickName()));
                player.sendSystemMessage(Component.literal("Xp total: " + Integer.toString(digimon.getExperienceTotal())));
                player.sendSystemMessage(Component.literal("NextLevel Xp: " + Integer.toString(digimon.getLevelXp())));
                player.sendSystemMessage(Component.literal("Level: " + Integer.toString(digimon.getCurrentLevel())));
                player.sendSystemMessage(Component.literal("Xps: " + digimon.getSpecificXps()));
                player.sendSystemMessage(Component.literal("Mood: " + Integer.toString(digimon.getMoodPoints()) + digimon.getMood()));
                player.sendSystemMessage(Component.literal("a: " + Integer.toString(digimon.getSpecificXps(0))));
                player.getItemInHand(hand).shrink(1);
                return InteractionResult.CONSUME;
            }
        }
        return super.interactLivingEntity(item, player, entity, hand);
    }
}
