package net.modderg.thedigimod.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class DigiviceItem extends Item {
    public DigiviceItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @org.jetbrains.annotations.Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        p_41423_.add(Component.literal(""));
        p_41423_.add(Component.literal("Point to a digimon to open menu").withStyle(ChatFormatting.BLUE));
        p_41423_.add(Component.literal("Z free cursor").withStyle(ChatFormatting.BLUE));
        p_41423_.add(Component.literal("R navigate").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
    }
}
