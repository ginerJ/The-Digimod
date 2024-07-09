package net.modderg.thedigimod.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.entity.CustomDigimon;

import java.util.function.Supplier;

public class CToSOpenDigiInventoryPacket {

    private final int id;


    public CToSOpenDigiInventoryPacket(int id) {
        this.id = id;
    }

    public CToSOpenDigiInventoryPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
    }


    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        Entity entity = player.level().getEntity(id);

        if (entity instanceof CustomDigimon digimon) {
            digimon.openMenu(player);
        }
    }
}
