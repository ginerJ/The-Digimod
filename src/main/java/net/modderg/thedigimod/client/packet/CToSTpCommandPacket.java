package net.modderg.thedigimod.client.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.server.entity.DigimonEntity;

import java.util.function.Supplier;

public class CToSTpCommandPacket {

    int id;
    public CToSTpCommandPacket(int id) {
        this.id = id;
    }
    public CToSTpCommandPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();

        if(id != -1) {
            if(player.level().getEntity(id) instanceof DigimonEntity cd && cd.isOwnedBy(player)) {
                cd.setPos(player.position());
            }
        } else {
            AABB searchArea = new AABB(player.blockPosition()).inflate(15d);
            player.level().getEntitiesOfClass(DigimonEntity.class, searchArea).stream().filter(d -> d.isOwnedBy(player))
                    .forEach(d->d.setPos(player.position()));
        }
    }
}
