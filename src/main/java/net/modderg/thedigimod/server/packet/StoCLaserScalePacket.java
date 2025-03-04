package net.modderg.thedigimod.server.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.server.projectiles.ProjectileLaserDefault;
import software.bernie.geckolib.util.ClientUtils;

import java.util.function.Supplier;

public class StoCLaserScalePacket {

    private final int id;
    private final int target;


    public StoCLaserScalePacket(int id, int target) {
        this.id = id;
        this.target = target;

    }

    public StoCLaserScalePacket(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
        buffer.writeInt(target);
    }


    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(()->{
            Entity entity = ClientUtils.getLevel().getEntity(id);
            Entity targetEntity = ClientUtils.getLevel().getEntity(target);
            if(entity instanceof ProjectileLaserDefault laser && laser.getOwner() != null && targetEntity != null)
                laser.distanceToTarget = laser.getOwner().distanceTo(targetEntity);
        });
    }
}
