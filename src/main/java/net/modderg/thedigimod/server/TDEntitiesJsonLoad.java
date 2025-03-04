package net.modderg.thedigimod.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class TDEntitiesJsonLoad {
    private static final Map<String, JsonObject> DIGIMON_DATA = new HashMap<>();

    public static Map<String, JsonObject> getDigimonData() {
        return DIGIMON_DATA;
    }

    public static void loadDigimonData() {

        for (String file : getDigimonFiles()) {
            String path = "/data/thedigimod/digimon/" + file + ".json";
            try (InputStream stream = TDEntitiesJsonLoad.class.getResourceAsStream(path);
                 InputStreamReader reader = new InputStreamReader(stream)) {
                JsonElement element = JsonParser.parseReader(reader);
                if (element.isJsonObject())
                    DIGIMON_DATA.put(file, element.getAsJsonObject());
            } catch (Exception e) {
                System.err.println("Failed to load Digimon JSON: " + file + ".json");
                e.printStackTrace();
            }
        }
    }

    public static List<String> getDigimonFiles() {

        try (InputStream stream = TDEntitiesJsonLoad.class.getResourceAsStream("/data/thedigimod/digimon/digimon.json");
             InputStreamReader reader = new InputStreamReader(stream)) {
            JsonObject element = JsonParser.parseReader(reader).getAsJsonObject();

            return element.get("digimon").getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static JsonObject getDigimonData(String name) {
        return DIGIMON_DATA.get(name);
    }

}
