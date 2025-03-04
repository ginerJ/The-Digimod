package net.modderg.thedigimod.server.item.custom;

import com.google.common.collect.Sets;
import net.minecraft.ChatFormatting;
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

import java.util.List;
import java.util.Set;


public class SpMoveItem extends DigimonItem {

    private String itemName;

    private final Set<String> effects = Sets.newHashSet();

    protected boolean isPhysical = false;

    protected int timesRepeat = 1;

    public SpMoveItem setRepeat(int times){
        timesRepeat = times;
        return this;
    }

    public SpMoveItem setPhysical(){
        isPhysical = true;
        return this;
    }

    public SpMoveItem addDescriptionEffect(String effect) {
        this.effects.add(effect);
        return this;
    }

    public SpMoveItem(Properties p_41383_, String name) {
        super(p_41383_);
        itemName = name;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack itemStack, @NotNull Player player, @NotNull LivingEntity entity, @NotNull InteractionHand p_41401_) {
        if(entity instanceof DigimonEntity cd && cd.isOwnedBy(player)){
            cd.setSpMoveName(itemName);
            cd.eatItemAnim(itemStack);
        }
        return super.interactLivingEntity(itemStack, player, entity, p_41401_);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @org.jetbrains.annotations.Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        p_41423_.add(Component.literal(""));
        p_41423_.add(Component.literal((isPhysical?"Half Physical Attack":"Sp. Attack") + " Move").withStyle(ChatFormatting.BLUE));
        if(timesRepeat>1)p_41423_.add(Component.translatable(" Attacks "+ String.valueOf(timesRepeat)+ " Times").withStyle(ChatFormatting.GREEN));
        for(String s:effects)p_41423_.add(Component.translatable(s).withStyle(ChatFormatting.GREEN));
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
    }
}
