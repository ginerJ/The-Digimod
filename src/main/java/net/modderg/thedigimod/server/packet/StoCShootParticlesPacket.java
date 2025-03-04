package net.modderg.thedigimod.server.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.server.projectiles.ProjectileParticleStreamDefault;
import software.bernie.geckolib.util.ClientUtils;

import java.util.function.Supplier;

public class StoCShootParticlesPacket {

    private final int id;
    private final BlockPos target;
    private final BlockPos pos;


    public StoCShootParticlesPacket(int id, BlockPos target, BlockPos pos) {
        this.id = id;
        this.target = target;
        this.pos = pos;

    }

    public StoCShootParticlesPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readBlockPos(), buffer.readBlockPos());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
        buffer.writeBlockPos(target);
        buffer.writeBlockPos(pos);
    }


    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(()->{
            Entity entity = ClientUtils.getLevel().getEntity(id);
            if(entity instanceof ProjectileParticleStreamDefault projectile) {
                projectile.spawnParticleBeam(pos.above(), target.above());
            }
        });
    }
}
