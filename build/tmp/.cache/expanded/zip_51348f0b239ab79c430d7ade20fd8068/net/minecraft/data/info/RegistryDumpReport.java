package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class RegistryDumpReport implements DataProvider {
   private final PackOutput output;

   public RegistryDumpReport(PackOutput p_249862_) {
      this.output = p_249862_;
   }

   public CompletableFuture<?> run(CachedOutput p_253743_) {
      JsonObject jsonobject = new JsonObject();
      BuiltInRegistries.REGISTRY.holders().forEach((p_211088_) -> {
         jsonobject.add(p_211088_.key().location().toString(), dumpRegistry(p_211088_.value()));
      });
      Path path = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("registries.json");
      return DataProvider.saveStable(p_253743_, jsonobject, path);
   }

   private static <T> JsonElement dumpRegistry(Registry<T> p_124059_) {
      JsonObject jsonobject = new JsonObject();
      if (p_124059_ instanceof DefaultedRegistry) {
         ResourceLocation resourcelocation = ((DefaultedRegistry)p_124059_).getDefaultKey();
         jsonobject.addProperty("default", resourcelocation.toString());
      }

      int i = ((Registry)BuiltInRegistries.REGISTRY).getId(p_124059_);
      jsonobject.addProperty("protocol_id", i);
      JsonObject jsonobject1 = new JsonObject();
      p_124059_.holders().forEach((p_211092_) -> {
         T t = p_211092_.value();
         int j = p_124059_.getId(t);
         JsonObject jsonobject2 = new JsonObject();
         jsonobject2.addProperty("protocol_id", j);
         jsonobject1.add(p_211092_.key().location().toString(), jsonobject2);
      });
      jsonobject.add("entries", jsonobject1);
      return jsonobject;
   }

   public final String getName() {
      return "Registry Dump";
   }
}