package net.minecraft.server.packs.resources;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;

public class MultiPackResourceManager implements CloseableResourceManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<String, FallbackResourceManager> namespacedManagers;
   private final List<PackResources> packs;

   public MultiPackResourceManager(PackType p_203797_, List<PackResources> p_203798_) {
      this.packs = List.copyOf(p_203798_);
      Map<String, FallbackResourceManager> map = new HashMap<>();
      List<String> list = p_203798_.stream().flatMap((p_215471_) -> {
         return p_215471_.getNamespaces(p_203797_).stream();
      }).distinct().toList();

      for(PackResources packresources : p_203798_) {
         ResourceFilterSection resourcefiltersection = this.getPackFilterSection(packresources);
         Set<String> set = packresources.getNamespaces(p_203797_);
         Predicate<ResourceLocation> predicate = resourcefiltersection != null ? (p_215474_) -> {
            return resourcefiltersection.isPathFiltered(p_215474_.getPath());
         } : null;

         for(String s : list) {
            boolean flag = set.contains(s);
            boolean flag1 = resourcefiltersection != null && resourcefiltersection.isNamespaceFiltered(s);
            if (flag || flag1) {
               FallbackResourceManager fallbackresourcemanager = map.get(s);
               if (fallbackresourcemanager == null) {
                  fallbackresourcemanager = new FallbackResourceManager(p_203797_, s);
                  map.put(s, fallbackresourcemanager);
               }

               if (flag && flag1) {
                  fallbackresourcemanager.push(packresources, predicate);
               } else if (flag) {
                  fallbackresourcemanager.push(packresources);
               } else {
                  fallbackresourcemanager.pushFilterOnly(packresources.packId(), predicate);
               }
            }
         }
      }

      this.namespacedManagers = map;
   }

   @Nullable
   private ResourceFilterSection getPackFilterSection(PackResources p_215468_) {
      try {
         return p_215468_.getMetadataSection(ResourceFilterSection.TYPE);
      } catch (IOException ioexception) {
         if (!net.minecraftforge.data.loading.DatagenModLoader.isRunningDataGen())// Neo: Only warn about malformed pack filters outside of datagen in case modders are replacing variables with gradle
         LOGGER.error("Failed to get filter section from pack {}", (Object)p_215468_.packId());
         return null;
      }
   }

   public Set<String> getNamespaces() {
      return this.namespacedManagers.keySet();
   }

   public Optional<Resource> getResource(ResourceLocation p_215482_) {
      ResourceManager resourcemanager = this.namespacedManagers.get(p_215482_.getNamespace());
      return resourcemanager != null ? resourcemanager.getResource(p_215482_) : Optional.empty();
   }

   public List<Resource> getResourceStack(ResourceLocation p_215466_) {
      ResourceManager resourcemanager = this.namespacedManagers.get(p_215466_.getNamespace());
      return resourcemanager != null ? resourcemanager.getResourceStack(p_215466_) : List.of();
   }

   public Map<ResourceLocation, Resource> listResources(String p_215476_, Predicate<ResourceLocation> p_215477_) {
      checkTrailingDirectoryPath(p_215476_);
      Map<ResourceLocation, Resource> map = new TreeMap<>();

      for(FallbackResourceManager fallbackresourcemanager : this.namespacedManagers.values()) {
         map.putAll(fallbackresourcemanager.listResources(p_215476_, p_215477_));
      }

      return map;
   }

   public Map<ResourceLocation, List<Resource>> listResourceStacks(String p_215479_, Predicate<ResourceLocation> p_215480_) {
      checkTrailingDirectoryPath(p_215479_);
      Map<ResourceLocation, List<Resource>> map = new TreeMap<>();

      for(FallbackResourceManager fallbackresourcemanager : this.namespacedManagers.values()) {
         map.putAll(fallbackresourcemanager.listResourceStacks(p_215479_, p_215480_));
      }

      return map;
   }

   private static void checkTrailingDirectoryPath(String p_249608_) {
      if (p_249608_.endsWith("/")) {
         throw new IllegalArgumentException("Trailing slash in path " + p_249608_);
      }
   }

   public Stream<PackResources> listPacks() {
      return this.packs.stream();
   }

   public void close() {
      this.packs.forEach(PackResources::close);
   }
}
