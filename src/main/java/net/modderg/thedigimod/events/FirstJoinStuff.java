package net.modderg.thedigimod.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.item.BabyDigimonItems;
import net.modderg.thedigimod.item.DigivicesItems;
import net.modderg.thedigimod.item.InitItems;

import java.util.List;
import java.util.Random;

public class FirstJoinStuff {

    public static RegistryObject<Item> chooseItem(List<RegistryObject<Item>> items) {
        Random rand = new Random();
        return items.get(rand.nextInt(items.size()));
    }

    public static void giveFirstJoinItems(Entity entity) {
        if (entity == null)
            return;
        if (!(entity.getCapability(ModEventBusSubscriber.PLAYER_VARIABLES_CAPABILITY, null).orElse(new ModEventBusSubscriber.PlayerVariables())).firstJoin) {
            {
                boolean _setval = true;
                entity.getCapability(ModEventBusSubscriber.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                    capability.firstJoin = _setval;
                    capability.syncPlayerVariables(entity);
                });
            }
            if (entity instanceof Player _player) {

                ItemStack _setstack = new ItemStack(chooseItem(BabyDigimonItems.babiesMap.values().stream().toList()).get());

                ItemHandlerHelper.giveItemToPlayer(_player, _setstack);

                _setstack = new ItemStack(chooseItem(DigivicesItems.vicesMap.values().stream().toList()).get());

                ItemHandlerHelper.giveItemToPlayer(_player, _setstack);

                ItemHandlerHelper.giveItemToPlayer(_player, new ItemStack(InitItems.TRAINING_BAG.get()));

                ItemHandlerHelper.giveItemToPlayer(_player, new ItemStack(InitItems.DIGI_MEAT.get(), 20));
            }
        }
    }


}
