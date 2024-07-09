package net.modderg.thedigimod.packet;

import com.google.gson.JsonParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.entity.CustomDigimon;
import software.bernie.geckolib.util.ClientUtils;

import java.util.function.Supplier;

public class SToCLoadJsonDataPacket {
    private final int id;
    private final String json;

    public SToCLoadJsonDataPacket(int uuid, String json) {
        this.id = uuid;
        this.json = json;
    }

    public SToCLoadJsonDataPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readUtf());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
        buffer.writeUtf(json);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(()->{
            Entity entity = ClientUtils.getLevel().getEntity(id);
            if(entity instanceof CustomDigimon cd)
                cd.jsonManager.applyJsonData(JsonParser.parseString(json), cd);
        });
    }
}
