package net.modderg.thedigimod.client.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import oshi.util.tuples.Pair;

import java.util.function.Supplier;

public class CToSEatCommandPacket {

    int id;
    public CToSEatCommandPacket(int id) {
        this.id = id;
    }
    public CToSEatCommandPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(()-> {
            ServerPlayer player = context.get().getSender();

            if(id != -1) {
                if(player.level().getEntity(id) instanceof DigimonEntity cd)
                    giveFoodToDigimon(cd, player);
            } else {
                AABB searchArea = new AABB(player.blockPosition()).inflate(15d);

                player.level().getEntitiesOfClass(DigimonEntity.class, searchArea).stream().filter(d -> d.isOwnedBy(player))
                        .forEach(d-> giveFoodToDigimon(d, player));
            }
        });
    }

    public void giveFoodToDigimon(DigimonEntity cd, Player player){
        ItemStack foodItem = player.getInventory().items.stream()
                .filter(item -> cd.getDiet().isPartOfDiet(item))
                .findFirst().orElse(ItemStack.EMPTY);

        if (foodItem.isEmpty())
            return;

        Pair<Integer, Integer> props = cd.getDiet().getCaloriesAndHeal(foodItem);
        cd.eatItem(foodItem, props.getA(), props.getB());
        cd.playSound(foodItem.getItem().getEatingSound(), 0.15F, 1.0F);
    }
}
