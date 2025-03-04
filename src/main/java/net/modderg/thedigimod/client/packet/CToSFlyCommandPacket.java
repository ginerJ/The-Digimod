package net.modderg.thedigimod.client.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.server.entity.FlyingDigimonEntity;

import java.util.function.Supplier;

public class CToSFlyCommandPacket {

    int id;
    public CToSFlyCommandPacket(int id) {
        this.id = id;
    }
    public CToSFlyCommandPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();

            if(id != -1) {
                if(player.level().getEntity(id) instanceof FlyingDigimonEntity cd && cd.isOwnedBy(player)) {
                    while(cd.getMovementID() != 2)
                        cd.changeMovementID();
                }
            } else {
                AABB searchArea = new AABB(player.blockPosition()).inflate(15d);
                player.level().getEntitiesOfClass(FlyingDigimonEntity.class, searchArea).stream().filter(d -> d.isOwnedBy(player))
                        .forEach(cd->{while(cd.getMovementID() != 2) cd.changeMovementID();});
            }


        });
    }
}
