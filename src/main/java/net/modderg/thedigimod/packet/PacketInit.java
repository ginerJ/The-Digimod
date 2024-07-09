package net.modderg.thedigimod.packet;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.modderg.thedigimod.TheDigiMod;

public class PacketInit {

    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(
            new ResourceLocation(TheDigiMod.MOD_ID, "main"))
            .serverAcceptedVersions((s)->true)
            .clientAcceptedVersions((s)->true)
            .networkProtocolVersion(() ->NetworkConstants.NETVERSION)
            .simpleChannel();

    public static void register(){

        int id = 0;

        INSTANCE.messageBuilder(CToSAttackCommandPacket.class, id++)
                .encoder(CToSAttackCommandPacket::encode)
                .decoder(CToSAttackCommandPacket::new)
                .consumerMainThread(CToSAttackCommandPacket::handle)
                .add();

        INSTANCE.messageBuilder(CToSSitCommandPacket.class, id++)
                .encoder(CToSSitCommandPacket::encode)
                .decoder(CToSSitCommandPacket::new)
                .consumerMainThread(CToSSitCommandPacket::handle)
                .add();

        INSTANCE.messageBuilder(CToSStandCommandPacket.class, id++)
                .encoder(CToSStandCommandPacket::encode)
                .decoder(CToSStandCommandPacket::new)
                .consumerMainThread(CToSStandCommandPacket::handle)
                .add();

        INSTANCE.messageBuilder(CToSMemorySpawnCommandPacket.class, id++)
                .encoder(CToSMemorySpawnCommandPacket::encode)
                .decoder(CToSMemorySpawnCommandPacket::new)
                .consumerMainThread(CToSMemorySpawnCommandPacket::handle)
                .add();

        INSTANCE.messageBuilder(CToSFlyCommandPacket.class, id++)
                .encoder(CToSFlyCommandPacket::encode)
                .decoder(CToSFlyCommandPacket::new)
                .consumerMainThread(CToSFlyCommandPacket::handle)
                .add();

        INSTANCE.messageBuilder(CToSEatCommandPacket.class, id++)
                .encoder(CToSEatCommandPacket::encode)
                .decoder(CToSEatCommandPacket::new)
                .consumerMainThread(CToSEatCommandPacket::handle)
                .add();

        INSTANCE.messageBuilder(CToSWanderCommandPacket.class, id++)
                .encoder(CToSWanderCommandPacket::encode)
                .decoder(CToSWanderCommandPacket::new)
                .consumerMainThread(CToSWanderCommandPacket::handle)
                .add();

        INSTANCE.messageBuilder(CToSTpCommandPacket.class, id++)
                .encoder(CToSTpCommandPacket::encode)
                .decoder(CToSTpCommandPacket::new)
                .consumerMainThread(CToSTpCommandPacket::handle)
                .add();

        INSTANCE.messageBuilder(CToSWorkCommandPacket.class, id++)
                .encoder(CToSWorkCommandPacket::encode)
                .decoder(CToSWorkCommandPacket::new)
                .consumerMainThread(CToSWorkCommandPacket::handle)
                .add();

        INSTANCE.messageBuilder(CToSOpenDigiInventoryPacket.class, id++)
                .encoder(CToSOpenDigiInventoryPacket::encode)
                .decoder(CToSOpenDigiInventoryPacket::new)
                .consumerMainThread(CToSOpenDigiInventoryPacket::handle)
                .add();

        INSTANCE.messageBuilder(SToCSGainXpPacket.class, id++)
                .encoder(SToCSGainXpPacket::encode)
                .decoder(SToCSGainXpPacket::new)
                .consumerMainThread(SToCSGainXpPacket::handle)
                .add();

        INSTANCE.messageBuilder(SToCDigiviceScreenPacket.class, id++)
                .encoder(SToCDigiviceScreenPacket::encode)
                .decoder(SToCDigiviceScreenPacket::new)
                .consumerMainThread(SToCDigiviceScreenPacket::handle)
                .add();

        INSTANCE.messageBuilder(StoCLaserScalePacket.class, id++)
                .encoder(StoCLaserScalePacket::encode)
                .decoder(StoCLaserScalePacket::new)
                .consumerMainThread(StoCLaserScalePacket::handle)
                .add();

        INSTANCE.messageBuilder(StoCShootParticlesPacket.class, id++)
                .encoder(StoCShootParticlesPacket::encode)
                .decoder(StoCShootParticlesPacket::new)
                .consumerMainThread(StoCShootParticlesPacket::handle)
                .add();

        INSTANCE.messageBuilder(SToCLoadJsonDataPacket.class, id++)
                .encoder(SToCLoadJsonDataPacket::encode)
                .decoder(SToCLoadJsonDataPacket::new)
                .consumerMainThread(SToCLoadJsonDataPacket::handle)
                .add();

        INSTANCE.messageBuilder(CToSEntityJoinedClientPacket.class, id++)
                .encoder(CToSEntityJoinedClientPacket::encode)
                .decoder(CToSEntityJoinedClientPacket::new)
                .consumerMainThread(CToSEntityJoinedClientPacket::handle)
                .add();
    }

    public static void sendToServer(Object msg){
        INSTANCE.send(PacketDistributor.SERVER.noArg(), msg);
    }

    public static void sendToClient(Object msg, ServerPlayer player)
    {
        if (INSTANCE.isRemotePresent(player.connection.connection))
        {
            INSTANCE.send(PacketDistributor.PLAYER.with(()-> player), msg);        }
    }

    public static void sendToAll(Object msg)
    {
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }
}
