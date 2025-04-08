package net.minecraft.resources;

import java.util.List;
import java.util.Map;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class FileToIdConverter {
   private final String prefix;
   private final String extension;

   public FileToIdConverter(String p_248876_, String p_251478_) {
      this.prefix = p_248876_;
      this.extension = p_251478_;
   }

   public static FileToIdConverter json(String p_248754_) {
      return new FileToIdConverter(p_248754_, ".json");
   }

   public ResourceLocation idToFile(ResourceLocation p_251878_) {
      return p_251878_.withPath(this.prefix + "/" + p_251878_.getPath() + this.extension);
   }

   public ResourceLocation fileToId(ResourceLocation p_249595_) {
      String s = p_249595_.getPath();
      return p_249595_.withPath(s.substring(this.prefix.length() + 1, s.length() - this.extension.length()));
   }

   public Map<ResourceLocation, Resource> listMatchingResources(ResourceManager p_252045_) {
      return p_252045_.listResources(this.prefix, (p_251986_) -> {
         return p_251986_.getPath().endsWith(this.extension);
      });
   }

   public Map<ResourceLocation, List<Resource>> listMatchingResourceStacks(ResourceManager p_249881_) {
      return p_249881_.listResourceStacks(this.prefix, (p_248700_) -> {
         return p_248700_.getPath().endsWith(this.extension);
      });
   }
}