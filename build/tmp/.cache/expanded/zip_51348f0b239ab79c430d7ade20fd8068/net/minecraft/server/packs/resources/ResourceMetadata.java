package net.minecraft.server.packs.resources;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;

public interface ResourceMetadata {
   ResourceMetadata EMPTY = new ResourceMetadata() {
      public <T> Optional<T> getSection(MetadataSectionSerializer<T> p_215584_) {
         return Optional.empty();
      }
   };
   IoSupplier<ResourceMetadata> EMPTY_SUPPLIER = () -> {
      return EMPTY;
   };

   static ResourceMetadata fromJsonStream(InputStream p_215581_) throws IOException {
      try (BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(p_215581_, StandardCharsets.UTF_8))) {
         final JsonObject jsonobject = GsonHelper.parse(bufferedreader);
         return new ResourceMetadata() {
            public <T> Optional<T> getSection(MetadataSectionSerializer<T> p_215589_) {
               String s = p_215589_.getMetadataSectionName();
               return jsonobject.has(s) ? Optional.of(p_215589_.fromJson(GsonHelper.getAsJsonObject(jsonobject, s))) : Optional.empty();
            }
         };
      }
   }

   <T> Optional<T> getSection(MetadataSectionSerializer<T> p_215579_);
}