package net.modderg.thedigimod.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.item.DigiItems;

import javax.annotation.Nullable;
import java.util.Random;

public class AddRandomDim {

    public static RegistryObject<?> chooseItem(RegistryObject<?>[] babies) {
        Random rand = new Random();
        return babies[rand.nextInt(babies.length)];
    }

    static RegistryObject<?>[] babies = {DigiItems.BOTAMON,  DigiItems.PUNIMON, DigiItems.BUBBMON};
    static RegistryObject<?>[] vices = {DigiItems.VPET,  DigiItems.DIGIVICE, DigiItems.VITALBRACELET};

    @Mod.EventBusSubscriber
    public class IniciarProcedure {

        private static void execute(@Nullable Event event, Entity entity) {
            if (entity == null)
                return;
            if (!(entity.getCapability(ModEvents.ModEventBusSubscriber.PLAYER_VARIABLES_CAPABILITY, null).orElse(new ModEvents.ModEventBusSubscriber.PlayerVariables())).FirstJoin) {
                {
                    boolean _setval = true;
                    entity.getCapability(ModEvents.ModEventBusSubscriber.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                        capability.FirstJoin = _setval;
                        capability.syncPlayerVariables(entity);
                    });
                }
                if (entity instanceof Player _player) {
                    ItemStack _setstack = new ItemStack((ItemLike) chooseItem(babies).get());
                    ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    _setstack = new ItemStack((ItemLike) chooseItem(vices).get());
                    ItemHandlerHelper.giveItemToPlayer(_player, _setstack);

                    ItemHandlerHelper.giveItemToPlayer(_player, new ItemStack(DigiItems.BAG_ITEM.get()));
                    ItemHandlerHelper.giveItemToPlayer(_player, new ItemStack(DigiItems.TABLE_ITEM.get()));
                    ItemHandlerHelper.giveItemToPlayer(_player, new ItemStack(DigiItems.TARGET_ITEM.get()));
                    ItemHandlerHelper.giveItemToPlayer(_player, new ItemStack(DigiItems.SHIELD_ITEM.get()));

                }
            }
        }
    }
}
