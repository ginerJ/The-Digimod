package net.modderg.thedigimod.client.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.server.entity.DigimonEntity;

import java.util.function.Supplier;

public class CToSOpenDigiInventoryPacket {

    private final int id;

    public CToSOpenDigiInventoryPacket(int id) {
        this.id = id;
    }

    public CToSOpenDigiInventoryPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {buffer.writeInt(id);}


    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        Entity entity = player.level().getEntity(id);

        if (entity instanceof DigimonEntity digimon && digimon.isOwnedBy(player)) {
            digimon.openMenu(player);
        }
    }
}
