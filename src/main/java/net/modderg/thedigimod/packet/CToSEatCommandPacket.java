package net.modderg.thedigimod.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.item.InitItems;
import net.modderg.thedigimod.item.custom.DigiFoodBlockItem;
import net.modderg.thedigimod.item.custom.DigiFoodItem;

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
                if(player.level().getEntity(id) instanceof CustomDigimon cd) {
                    giveFoodToDigimon(cd, player);
                }
            } else {
                AABB searchArea = new AABB(player.blockPosition()).inflate(15d);

                player.level().getEntitiesOfClass(CustomDigimon.class, searchArea).stream().filter(d -> d.isOwnedBy(player))
                        .forEach(d->{
                            giveFoodToDigimon(d, player);
                        });
            }
        });
    }

    public void giveFoodToDigimon(CustomDigimon cd, Player player){
        if(playerHasFood(player)){
            ItemStack foodItem = player.getInventory().items.stream()
                    .filter(item -> InitItems.digiFood().stream().anyMatch(item::is))
                    .findFirst().get();

            if(foodItem.getItem() instanceof DigiFoodItem food){
                cd.eatItem(foodItem, food.getCalories(), food.getHeal());
            } else if(foodItem.getItem() instanceof DigiFoodBlockItem food){
                cd.eatItem(foodItem, food.getCalories(), food.getHeal());
            }

            cd.playSound(foodItem.getItem().getEatingSound(), 0.15F, 1.0F);
        }
    }

    public boolean playerHasFood(Player player){
        return player.getInventory().hasAnyOf(InitItems.digiFood());
    }
}
