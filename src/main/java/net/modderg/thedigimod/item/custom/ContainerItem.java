package net.modderg.thedigimod.item.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.sound.DigiSounds;

public class ContainerItem extends DigimonItem {
    private final RegistryObject<Item>[] itemlist;

    public ContainerItem(Properties p_41383_, RegistryObject<Item>[] items) {
        super(p_41383_);
        this.itemlist = items;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        for(RegistryObject<Item> item : this.itemlist){
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(item.get()));
        }
        player.playNotifySound(DigiSounds.BAG_OPEN_SOUND.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
        player.getItemInHand(hand).shrink(1);
        return super.use(level, player, hand);
    }
}
