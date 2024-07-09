package net.modderg.thedigimod.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SToCDigiviceScreenPacket {
    private final int id;

    public SToCDigiviceScreenPacket(int uuid) {
        this.id = uuid;
    }

    public SToCDigiviceScreenPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
    }


    public void handle(Supplier<NetworkEvent.Context> context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> SToCDigiviceScreenHandle.handle(context, id));
    }
}
