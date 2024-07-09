package net.modderg.thedigimod.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.entity.CustomDigimon;

import java.util.function.Supplier;

public class SToCSGainXpPacket {
    private final int id;
    private final int xp;

    public SToCSGainXpPacket(int id, int xp) {
        this.id = id;
        this.xp = xp;
    }

    public SToCSGainXpPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
        buffer.writeInt(xp);
    }


    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(()->{

            Entity entity = Minecraft.getInstance().player.level().getEntity(id);

            if(entity instanceof CustomDigimon cd)
                cd.gainSpecificXp(xp);
        });
    }
}
