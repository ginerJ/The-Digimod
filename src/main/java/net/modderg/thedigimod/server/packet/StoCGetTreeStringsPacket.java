package net.modderg.thedigimod.server.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.client.gui.DigiviceEvoTreeScreen;

import java.util.function.Supplier;

public class StoCGetTreeStringsPacket {

    private final String tree;

    public StoCGetTreeStringsPacket(String tree) {
        this.tree = tree;
    }

    public StoCGetTreeStringsPacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(tree);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(()->{
            if(Minecraft.getInstance().screen instanceof DigiviceEvoTreeScreen screen)
                screen.treeString = tree;
        });
    }
}
