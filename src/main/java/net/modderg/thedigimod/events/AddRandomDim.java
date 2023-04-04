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

    public static RegistryObject<?> chooseBaby(RegistryObject<?>[] babies) {
        Random rand = new Random();
        int randomIndex = rand.nextInt(babies.length);
        return babies[randomIndex];
    }

    static RegistryObject<?>[] babies = { DigiItems.BOTAMON,  DigiItems.PUNIMON, DigiItems.BUBBMON };

    @Mod.EventBusSubscriber
    public class IniciarProcedure {
        @SubscribeEvent
        public static void onEntityJoin(EntityJoinLevelEvent event) {
            execute(event, event.getEntity());
        }

        public static void execute(Entity entity) {
            execute(null, entity);
        }

        private static void execute(@Nullable Event event, Entity entity) {
            if (entity == null)
                return;
            if ((entity.getCapability(ModEvents.PLAYER_VARIABLES_CAPABILITY, null).orElse(new ModEvents.PlayerVariables())).FirstJoin == false) {
                {
                    boolean _setval = true;
                    entity.getCapability(ModEvents.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                        capability.FirstJoin = _setval;
                        capability.syncPlayerVariables(entity);
                    });
                }
                if (entity instanceof Player _player) {
                    RegistryObject<?> randomBaby = chooseBaby(babies);
                    ItemStack _setstack = new ItemStack((ItemLike) randomBaby.get());
                    _setstack.setCount(1);
                    ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                }
                if (entity instanceof Player _player) {
                    ItemStack _setstack = new ItemStack((DigiItems.DIGIVICE) .get());
                    _setstack.setCount(1);
                    ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                }
            }
        }
    }
}
