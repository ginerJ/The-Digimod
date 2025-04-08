package net.minecraft.server.packs.resources;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public abstract class SimpleJsonResourceReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Gson gson;
   private final String directory;

   public SimpleJsonResourceReloadListener(Gson p_10768_, String p_10769_) {
      this.gson = p_10768_;
      this.directory = p_10769_;
   }

   protected Map<ResourceLocation, JsonElement> prepare(ResourceManager p_10771_, ProfilerFiller p_10772_) {
      Map<ResourceLocation, JsonElement> map = new HashMap<>();
      scanDirectory(p_10771_, this.directory, this.gson, map);
      return map;
   }

   public static void scanDirectory(ResourceManager p_279308_, String p_279131_, Gson p_279261_, Map<ResourceLocation, JsonElement> p_279404_) {
      FileToIdConverter filetoidconverter = FileToIdConverter.json(p_279131_);

      for(Map.Entry<ResourceLocation, Resource> entry : filetoidconverter.listMatchingResources(p_279308_).entrySet()) {
         ResourceLocation resourcelocation = entry.getKey();
         ResourceLocation resourcelocation1 = filetoidconverter.fileToId(resourcelocation);

         try (Reader reader = entry.getValue().openAsReader()) {
            JsonElement jsonelement = GsonHelper.fromJson(p_279261_, reader, JsonElement.class);
            JsonElement jsonelement1 = p_279404_.put(resourcelocation1, jsonelement);
            if (jsonelement1 != null) {
               throw new IllegalStateException("Duplicate data file ignored with ID " + resourcelocation1);
            }
         } catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
            LOGGER.error("Couldn't parse data file {} from {}", resourcelocation1, resourcelocation, jsonparseexception);
         }
      }

   }

   protected ResourceLocation getPreparedPath(ResourceLocation rl) {
      return rl.withPath(this.directory + "/" + rl.getPath() + ".json");
   }
}
