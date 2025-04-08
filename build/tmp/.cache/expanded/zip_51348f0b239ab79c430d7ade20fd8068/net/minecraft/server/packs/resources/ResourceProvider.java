package net.minecraft.server.packs.resources;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface ResourceProvider {
   Optional<Resource> getResource(ResourceLocation p_215592_);

   default Resource getResourceOrThrow(ResourceLocation p_215594_) throws FileNotFoundException {
      return this.getResource(p_215594_).orElseThrow(() -> {
         return new FileNotFoundException(p_215594_.toString());
      });
   }

   default InputStream open(ResourceLocation p_215596_) throws IOException {
      return this.getResourceOrThrow(p_215596_).open();
   }

   default BufferedReader openAsReader(ResourceLocation p_215598_) throws IOException {
      return this.getResourceOrThrow(p_215598_).openAsReader();
   }

   static ResourceProvider fromMap(Map<ResourceLocation, Resource> p_251819_) {
      return (p_248274_) -> {
         return Optional.ofNullable(p_251819_.get(p_248274_));
      };
   }
}