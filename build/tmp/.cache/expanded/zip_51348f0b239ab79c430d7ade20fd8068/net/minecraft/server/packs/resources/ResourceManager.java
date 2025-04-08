package net.minecraft.server.packs.resources;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;

public interface ResourceManager extends ResourceProvider {
   Set<String> getNamespaces();

   List<Resource> getResourceStack(ResourceLocation p_215562_);

   Map<ResourceLocation, Resource> listResources(String p_215563_, Predicate<ResourceLocation> p_215564_);

   Map<ResourceLocation, List<Resource>> listResourceStacks(String p_215565_, Predicate<ResourceLocation> p_215566_);

   Stream<PackResources> listPacks();

   public static enum Empty implements ResourceManager {
      INSTANCE;

      public Set<String> getNamespaces() {
         return Set.of();
      }

      public Optional<Resource> getResource(ResourceLocation p_215576_) {
         return Optional.empty();
      }

      public List<Resource> getResourceStack(ResourceLocation p_215568_) {
         return List.of();
      }

      public Map<ResourceLocation, Resource> listResources(String p_215570_, Predicate<ResourceLocation> p_215571_) {
         return Map.of();
      }

      public Map<ResourceLocation, List<Resource>> listResourceStacks(String p_215573_, Predicate<ResourceLocation> p_215574_) {
         return Map.of();
      }

      public Stream<PackResources> listPacks() {
         return Stream.of();
      }
   }
}