package net.modderg.thedigimod.client.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.server.entity.DigimonEntity;

import java.util.function.Supplier;

public class CToSSetRollFirstEvoPacket {

    int id;
    int idx;
    public CToSSetRollFirstEvoPacket(int id, int idx) {
        this.id = id;
        this.idx = idx;
    }

    public CToSSetRollFirstEvoPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
        buffer.writeInt(idx);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(
                () -> {
                    if(context.get().getSender().level().getEntity(id) instanceof DigimonEntity cd)
                        cd.setRollFirstEvo(idx);
                }
        );
    }
}
