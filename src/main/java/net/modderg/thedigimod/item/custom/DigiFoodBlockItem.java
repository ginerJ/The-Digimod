package net.modderg.thedigimod.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class DigiFoodBlockItem extends ItemNameBlockItem {

    int calories;
    int heal;

    public DigiFoodBlockItem(Block p_41579_, Properties p_41580_, int calories, int heal) {
        super(p_41579_, p_41580_);
        this.calories = calories;
        this.heal = heal;
    }

    public int getCalories() {
        return calories;
    }

    public int getHeal() {
        return heal;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @org.jetbrains.annotations.Nullable Level level, List<Component> p_41423_, TooltipFlag p_41424_) {
        if(level != null && level.isClientSide()){
            p_41423_.add(Component.literal(""));
            p_41423_.add(Component.literal(" " + calories + " Calories").withStyle(ChatFormatting.GREEN));
            p_41423_.add(Component.literal(" " + heal + " Hp Regen").withStyle(ChatFormatting.GREEN));
        }
        super.appendHoverText(itemStack, level, p_41423_, p_41424_);
    }
}
