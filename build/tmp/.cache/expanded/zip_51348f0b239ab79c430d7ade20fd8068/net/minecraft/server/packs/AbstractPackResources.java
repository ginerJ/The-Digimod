package net.minecraft.server.packs;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public abstract class AbstractPackResources implements PackResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String name;
   private final boolean isBuiltin;

   protected AbstractPackResources(String p_255888_, boolean p_256392_) {
      this.name = p_255888_;
      this.isBuiltin = p_256392_;
   }

   @Nullable
   public <T> T getMetadataSection(MetadataSectionSerializer<T> p_10213_) throws IOException {
      IoSupplier<InputStream> iosupplier = this.getRootResource(new String[]{"pack.mcmeta"});
      if (iosupplier == null) {
         return (T)null;
      } else {
         try (InputStream inputstream = iosupplier.get()) {
            return getMetadataFromStream(p_10213_, inputstream);
         }
      }
   }

   @Nullable
   public static <T> T getMetadataFromStream(MetadataSectionSerializer<T> p_10215_, InputStream p_10216_) {
      JsonObject jsonobject;
      try (BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(p_10216_, StandardCharsets.UTF_8))) {
         jsonobject = GsonHelper.parse(bufferedreader);
      } catch (Exception exception1) {
         LOGGER.error("Couldn't load {} metadata", p_10215_.getMetadataSectionName(), exception1);
         return (T)null;
      }

      if (!jsonobject.has(p_10215_.getMetadataSectionName())) {
         return (T)null;
      } else {
         try {
            return p_10215_.fromJson(GsonHelper.getAsJsonObject(jsonobject, p_10215_.getMetadataSectionName()));
         } catch (Exception exception) {
            LOGGER.error("Couldn't load {} metadata", p_10215_.getMetadataSectionName(), exception);
            return (T)null;
         }
      }
   }

   public String packId() {
      return this.name;
   }

   public boolean isBuiltin() {
      return this.isBuiltin;
   }

   @Override
   public String toString()
   {
      return String.format(java.util.Locale.ROOT, "%s: %s", getClass().getName(), this.name);
   }
}
