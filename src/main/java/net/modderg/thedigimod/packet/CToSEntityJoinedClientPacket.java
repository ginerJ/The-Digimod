package net.modderg.thedigimod.packet;

import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.events.ForgeBusEvents;

import java.util.function.Supplier;

public class CToSEntityJoinedClientPacket {

    private final int id;


    public CToSEntityJoinedClientPacket(int id) {
        this.id = id;
    }

    public CToSEntityJoinedClientPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
    }


    public void handle(Supplier<NetworkEvent.Context> context) {

        Entity entity = context.get().getSender().level().getEntity(id);
        if(entity instanceof CustomDigimon cd){
            JsonElement json = ForgeBusEvents.THE_DIGIMON_RELOAD_LISTENER.jsonMap.get(new ResourceLocation(TheDigiMod.MOD_ID, cd.getLowerCaseSpecies()));

            if(json != null){
                cd.jsonManager.applyJsonData(json, cd);
                PacketInit.sendToAll(new SToCLoadJsonDataPacket(cd.getId(), json.toString()));
            }
        }
    }
}
