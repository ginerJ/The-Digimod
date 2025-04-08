package net.modderg.thedigimod.server.item.custom;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.modderg.thedigimod.server.advancements.TDAdvancements;
import org.jetbrains.annotations.NotNull;

public class DigitalItemNameBlockItem extends ItemNameBlockItem {
    public DigitalItemNameBlockItem(Block p_41579_, Properties p_41580_) {
        super(p_41579_, p_41580_);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        InteractionResult result = super.useOn(context);

        if(result.equals(InteractionResult.CONSUME))
            if(context.getPlayer() instanceof ServerPlayer sp)
                TDAdvancements.grantAdvancement(sp, TDAdvancements.DELIGHTED_FARMER);

        return result;
    }
}
