package net.minecraft.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public interface DataProvider {
   ToIntFunction<String> FIXED_ORDER_FIELDS = Util.make(new Object2IntOpenHashMap<>(), (p_236070_) -> {
      p_236070_.put("type", 0);
      p_236070_.put("parent", 1);
      p_236070_.defaultReturnValue(2);
   });
   Comparator<String> KEY_COMPARATOR = Comparator.comparingInt(FIXED_ORDER_FIELDS).thenComparing((p_236077_) -> {
      return p_236077_;
   });
   Logger LOGGER = LogUtils.getLogger();

   CompletableFuture<?> run(CachedOutput p_236071_);

   String getName();

   static CompletableFuture<?> saveStable(CachedOutput p_253653_, JsonElement p_254542_, Path p_254467_) {
      return CompletableFuture.runAsync(() -> {
         try {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);

            try (JsonWriter jsonwriter = new JsonWriter(new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8))) {
               jsonwriter.setSerializeNulls(false);
               jsonwriter.setIndent("  ");
               GsonHelper.writeValue(jsonwriter, p_254542_, KEY_COMPARATOR);
            }

            p_253653_.writeIfNeeded(p_254467_, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
         } catch (IOException ioexception) {
            LOGGER.error("Failed to save file to {}", p_254467_, ioexception);
         }

      }, Util.backgroundExecutor());
   }

   @FunctionalInterface
   public interface Factory<T extends DataProvider> {
      T create(PackOutput p_253851_);
   }
}