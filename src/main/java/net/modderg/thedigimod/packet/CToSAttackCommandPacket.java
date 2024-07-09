package net.modderg.thedigimod.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.entity.CustomDigimon;

import java.util.function.Supplier;

public class CToSAttackCommandPacket {

    private final int id;


    public CToSAttackCommandPacket(int id) {
        this.id = id;
    }

    public CToSAttackCommandPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
    }


    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        Entity entity = player.level().getEntity(id);

        if (entity instanceof LivingEntity lvEntity) {
            AABB searchArea = new AABB(player.blockPosition()).inflate(15d);
            player.level().getEntitiesOfClass(CustomDigimon.class, searchArea).stream().filter(d -> d.isOwnedBy(player))
                    .forEach(d -> d.setTarget(lvEntity));
        }
    }
}
