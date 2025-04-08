package net.modderg.thedigimod.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import oshi.util.tuples.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class TDEntitiesJsonLoad {

    public static List<Pair<String, JsonObject>> getAllDigimonJsons() {
        List<Pair<String, JsonObject>> results = new ArrayList<>();

        List<IModInfo> modInfos = ModList.get().getMods();

        int theDigimodIdx = 0;
        for(IModInfo modInfo : modInfos) {
            if (modInfo.getModId().equals("thedigimod"))
                break;
            theDigimodIdx++;
        }

        modInfos.add(modInfos.remove(theDigimodIdx));

        for (IModInfo modInfo : modInfos) {
            String modId = modInfo.getModId();
            IModFile modFile = modInfo.getOwningFile().getFile();

            Path digimonDir = modFile.findResource("data/thedigimod/digimon");

            if (Files.exists(digimonDir))
                try (Stream<Path> paths = Files.walk(digimonDir)) {
                    paths.filter(Files::isRegularFile)
                            .filter(path -> path.toString().endsWith(".json"))
                            .forEach(path -> {
                                try (InputStream is = Files.newInputStream(path);
                                     InputStreamReader reader = new InputStreamReader(is)) {
                                    JsonElement json = JsonParser.parseReader(reader);
                                    if (json.isJsonObject()) {
                                        String name = path.getFileName().toString().replace(".json", "");
                                        if(!containsKey(results, name))
                                            results.add(new Pair(name, json.getAsJsonObject()));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return results;
    }

    public static boolean containsKey(List<Pair<String, JsonObject>> list, String key) {
        return list.stream().anyMatch(pair -> pair.getA().equals(key));
    }

}
