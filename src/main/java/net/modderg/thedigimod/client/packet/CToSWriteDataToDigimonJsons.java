package net.modderg.thedigimod.client.packet;

import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import net.modderg.thedigimod.server.packet.PacketInit;
import net.modderg.thedigimod.server.packet.SToCLoadJsonDataPacket;
import net.modderg.thedigimod.server.events.EventsForgeBus;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class CToSWriteDataToDigimonJsons {

    private final int id;
    private final String babyName;

    public CToSWriteDataToDigimonJsons(int id, String babyName) {
        this.id = id;
        this.babyName = babyName;
    }

    public CToSWriteDataToDigimonJsons(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readUtf());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
        buffer.writeUtf(babyName);
    }


    public void handle(Supplier<NetworkEvent.Context> context) {

        Entity entity = context.get().getSender().level().getEntity(id);
        if(entity instanceof DigimonEntity cd){
            JsonObject json = EventsForgeBus.THE_DIGIMON_RELOAD_LISTENER.jsonMap.get(new ResourceLocation(TheDigiMod.MOD_ID, cd.getLowerCaseSpecies())).getAsJsonObject();

            if(json != null){

//                if(json.has("baby_digimon")){
//                    if (json.getAsJsonArray("baby_digimon").asList().stream().anyMatch(d -> d.equals(babyName)))
//                        return;
//                    JsonArray jsonArray = json.getAsJsonArray("baby_digimon");
//                    jsonArray.add(new JsonPrimitive(babyName));
//                    json.add("baby_digimon", jsonArray);
//                } else {
//                    JsonArray jsonArray = new JsonArray();
//                    jsonArray.add(new JsonPrimitive(babyName));
//                    json.add("baby_digimon", jsonArray);
//                }
//
//                saveJsonMapToFile(cd.getLowerCaseSpecies(), json);
            }
        }
    }

    public void saveJsonMapToFile(String id, JsonElement json) {
        Path path = Paths.get("C:\\Users\\juanc\\OneDrive\\Escritorio\\mods\\The-Digimod-private\\src\\main\\resources\\data\\thedigimod\\digimon", id + ".json");

        try (FileWriter fileWriter = new FileWriter(path.toFile())) {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(json, fileWriter);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
