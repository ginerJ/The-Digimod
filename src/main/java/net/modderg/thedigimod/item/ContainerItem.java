package net.modderg.thedigimod.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.RegistryObject;

public class ContainerItem extends Item {
    private final RegistryObject<Item>[] itemlist;

    public ContainerItem(Properties p_41383_, RegistryObject<Item>[] items) {
        super(p_41383_);
        this.itemlist = items;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        for(RegistryObject<Item> item : this.itemlist){
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack((ItemLike) item.get()));
        }
        player.getItemInHand(hand).shrink(1);
        return super.use(level, player, hand);
    }
}
