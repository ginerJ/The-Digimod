package net.modderg.thedigimod.server.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.server.TDConfig;
import net.modderg.thedigimod.server.entity.TDEntities;
import net.modderg.thedigimod.server.item.TDItemsDigivices;
import net.modderg.thedigimod.server.item.TDItems;
import net.modderg.thedigimod.server.item.custom.DigiviceItem;

import java.util.List;
import java.util.Random;

public class FirstJoinStuff {

    public static RegistryObject<Item> chooseItem(List<RegistryObject<Item>> items) {
        Random rand = new Random();
        return items.get(rand.nextInt(items.size()));
    }

    public static RegistryObject<DigiviceItem> chooseExtendsItem(List<RegistryObject<DigiviceItem>> items) {
        Random rand = new Random();
        return items.get(rand.nextInt(items.size()));
    }

    public static void giveFirstJoinItems(Entity entity) {
        if (entity == null)
            return;
        if (!(entity.getCapability(EventsBus.PLAYER_VARIABLES_CAPABILITY, null).orElse(new EventsBus.PlayerVariables())).firstJoin) {
            {
                boolean _setval = true;
                entity.getCapability(EventsBus.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                    capability.firstJoin = _setval;
                    capability.syncPlayerVariables(entity);
                });
            }
            if (entity instanceof Player _player) {

                ItemStack _setstack = new ItemStack(chooseItem(TDEntities.BABIES.getEntries().stream().toList()).get());

                ItemHandlerHelper.giveItemToPlayer(_player, _setstack);

                _setstack = new ItemStack(chooseExtendsItem(TDItemsDigivices.vicesMap).get());

                ItemHandlerHelper.giveItemToPlayer(_player, _setstack);

                if(TDConfig.GIVE_TRAINING_BAG.get())
                    ItemHandlerHelper.giveItemToPlayer(_player, new ItemStack(TDItems.TRAINING_BAG.get()));

                ItemHandlerHelper.giveItemToPlayer(_player, new ItemStack(TDItems.DIGI_MEAT.get(), 20));
            }
        }
    }


}
