package net.modderg.thedigimod.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.modderg.thedigimod.entity.CustomDigimon;

import java.util.Objects;

public class CustomDimItem extends Item {

    private EntityType DimDigi;

    public CustomDimItem(Properties p_41383_, EntityType digimon) {
        super(p_41383_);
        DimDigi = digimon;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack itemstack = context.getItemInHand();
        itemstack.shrink(1);
        CustomDigimon digi = (CustomDigimon) DimDigi.create(context.getLevel());
        digi.tame(Objects.requireNonNull(context.getPlayer()));
        digi.setPos(context.getPlayer().position());
        context.getLevel().addFreshEntity(digi);
        return InteractionResult.CONSUME;
    }
}
