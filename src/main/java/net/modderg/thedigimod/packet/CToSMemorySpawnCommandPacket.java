package net.modderg.thedigimod.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.item.InitItems;
import net.modderg.thedigimod.item.custom.DigiMemory;

import java.util.function.Supplier;

public class CToSMemorySpawnCommandPacket {

    private final int uuid;


    public CToSMemorySpawnCommandPacket(int uuid) {
        this.uuid = uuid;
    }

    public CToSMemorySpawnCommandPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(uuid);
    }


    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(()->{
            Entity entity = context.get().getSender().level().getEntity(uuid);

            if (entity instanceof CustomDigimon cd) {
                ItemStack itemStack = new ItemStack(InitItems.DIGI_MEMORY.get());
                ((DigiMemory)itemStack.getItem()).storeEntityInItem(cd,itemStack);

                ItemEntity itemEntity = cd.spawnAtLocation(itemStack);
                if(itemEntity != null && cd.getOwner() != null)
                    itemEntity.setPos(cd.getOwner().position());
                cd.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
            }
        });
    }

}
