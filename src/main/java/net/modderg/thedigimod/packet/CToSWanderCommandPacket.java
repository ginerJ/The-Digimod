package net.modderg.thedigimod.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.entity.CustomDigimon;

import java.util.function.Supplier;

public class CToSWanderCommandPacket {

    int id;
    public CToSWanderCommandPacket(int id) {
        this.id = id;
    }
    public CToSWanderCommandPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();

        if(id != -1) {
            if(player.level().getEntity(id) instanceof CustomDigimon cd && cd.isOwnedBy(player)) {
                while(cd.getMovementID() != -1)
                    cd.changeMovementID();
            }
        } else {
            AABB searchArea = new AABB(player.blockPosition()).inflate(15d);
            player.level().getEntitiesOfClass(CustomDigimon.class, searchArea).stream().filter(d -> d.isOwnedBy(player))
                    .forEach(cd->{while(cd.getMovementID() != -1) cd.changeMovementID();});
        }
    }
}
