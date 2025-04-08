package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.DataVersion;
import org.slf4j.Logger;

public class DetectedVersion implements WorldVersion {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final WorldVersion BUILT_IN = new DetectedVersion();
   private final String id;
   private final String name;
   private final boolean stable;
   private final DataVersion worldVersion;
   private final int protocolVersion;
   private final int resourcePackVersion;
   private final int dataPackVersion;
   private final Date buildTime;

   private DetectedVersion() {
      this.id = UUID.randomUUID().toString().replaceAll("-", "");
      this.name = "1.20.1";
      this.stable = true;
      this.worldVersion = new DataVersion(3465, "main");
      this.protocolVersion = SharedConstants.getProtocolVersion();
      this.resourcePackVersion = 15;
      this.dataPackVersion = 15;
      this.buildTime = new Date();
   }

   private DetectedVersion(JsonObject p_132489_) {
      this.id = GsonHelper.getAsString(p_132489_, "id");
      this.name = GsonHelper.getAsString(p_132489_, "name");
      this.stable = GsonHelper.getAsBoolean(p_132489_, "stable");
      this.worldVersion = new DataVersion(GsonHelper.getAsInt(p_132489_, "world_version"), GsonHelper.getAsString(p_132489_, "series_id", DataVersion.MAIN_SERIES));
      this.protocolVersion = GsonHelper.getAsInt(p_132489_, "protocol_version");
      JsonObject jsonobject = GsonHelper.getAsJsonObject(p_132489_, "pack_version");
      this.resourcePackVersion = GsonHelper.getAsInt(jsonobject, "resource");
      this.dataPackVersion = GsonHelper.getAsInt(jsonobject, "data");
      this.buildTime = Date.from(ZonedDateTime.parse(GsonHelper.getAsString(p_132489_, "build_time")).toInstant());
   }

   public static WorldVersion tryDetectVersion() {
      try (InputStream inputstream = DetectedVersion.class.getResourceAsStream("/version.json")) {
         if (inputstream == null) {
            LOGGER.warn("Missing version information!");
            return BUILT_IN;
         } else {
            try (InputStreamReader inputstreamreader = new InputStreamReader(inputstream)) {
               return new DetectedVersion(GsonHelper.parse(inputstreamreader));
            }
         }
      } catch (JsonParseException | IOException ioexception) {
         throw new IllegalStateException("Game version information is corrupt", ioexception);
      }
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public DataVersion getDataVersion() {
      return this.worldVersion;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   public int getPackVersion(PackType p_265454_) {
      return p_265454_ == PackType.SERVER_DATA ? this.dataPackVersion : this.resourcePackVersion;
   }

   public Date getBuildTime() {
      return this.buildTime;
   }

   public boolean isStable() {
      return this.stable;
   }
}