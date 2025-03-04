package net.modderg.thedigimod.client.packet;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.server.events.EventsForgeBus;
import net.modderg.thedigimod.server.packet.PacketInit;
import net.modderg.thedigimod.server.packet.StoCGetTreeStringsPacket;

import java.util.function.Supplier;

public class CToSGetTreeStringsPacket {

    private final String head;
    private final String uuid;

    public CToSGetTreeStringsPacket(String head, String uuid) {
        this.head = head; this.uuid = uuid;
    }

    public CToSGetTreeStringsPacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readUtf());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(head); buffer.writeUtf(uuid);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(()->{
            JsonObject json = new JsonObject();

            buildLineString(json, head);

            Player p = context.get().getSender();

            if (p instanceof ServerPlayer sp)
                PacketInit.sendToClient(new StoCGetTreeStringsPacket(json.toString()), sp);
        });
    }

    void buildLineString(JsonObject parentJson, String name){
        JsonObject json = EventsForgeBus.THE_DIGIMON_RELOAD_LISTENER.jsonMap.get(new ResourceLocation(TheDigiMod.MOD_ID, name)).getAsJsonObject();

        JsonObject currentJson = new JsonObject();
        parentJson.add(name, currentJson);

        if (!json.has("evolutions"))
            return;

        for (String key : json.getAsJsonObject("evolutions").keySet()){
            buildLineString(currentJson, key);
        }
    }
}
