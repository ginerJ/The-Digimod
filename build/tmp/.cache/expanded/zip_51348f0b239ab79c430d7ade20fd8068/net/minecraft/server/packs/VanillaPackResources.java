package net.minecraft.server.packs;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.slf4j.Logger;

public class VanillaPackResources implements PackResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final BuiltInMetadata metadata;
   private final Set<String> namespaces;
   private final List<Path> rootPaths;
   private final Map<PackType, List<Path>> pathsForType;

   VanillaPackResources(BuiltInMetadata p_249743_, Set<String> p_250468_, List<Path> p_248798_, Map<PackType, List<Path>> p_251106_) {
      this.metadata = p_249743_;
      this.namespaces = p_250468_;
      this.rootPaths = p_248798_;
      this.pathsForType = p_251106_;
   }

   @Nullable
   public IoSupplier<InputStream> getRootResource(String... p_250530_) {
      FileUtil.validatePath(p_250530_);
      List<String> list = List.of(p_250530_);

      for(Path path : this.rootPaths) {
         Path path1 = FileUtil.resolvePath(path, list);
         if (Files.exists(path1) && PathPackResources.validatePath(path1)) {
            return IoSupplier.create(path1);
         }
      }

      return null;
   }

   public void listRawPaths(PackType p_252103_, ResourceLocation p_250441_, Consumer<Path> p_251968_) {
      FileUtil.decomposePath(p_250441_.getPath()).get().ifLeft((p_248238_) -> {
         String s = p_250441_.getNamespace();

         for(Path path : this.pathsForType.get(p_252103_)) {
            Path path1 = path.resolve(s);
            p_251968_.accept(FileUtil.resolvePath(path1, p_248238_));
         }

      }).ifRight((p_248232_) -> {
         LOGGER.error("Invalid path {}: {}", p_250441_, p_248232_.message());
      });
   }

   public void listResources(PackType p_248974_, String p_248703_, String p_250848_, PackResources.ResourceOutput p_249668_) {
      FileUtil.decomposePath(p_250848_).get().ifLeft((p_248228_) -> {
         List<Path> list = this.pathsForType.get(p_248974_);
         int i = list.size();
         if (i == 1) {
            getResources(p_249668_, p_248703_, list.get(0), p_248228_);
         } else if (i > 1) {
            Map<ResourceLocation, IoSupplier<InputStream>> map = new HashMap<>();

            for(int j = 0; j < i - 1; ++j) {
               getResources(map::putIfAbsent, p_248703_, list.get(j), p_248228_);
            }

            Path path = list.get(i - 1);
            if (map.isEmpty()) {
               getResources(p_249668_, p_248703_, path, p_248228_);
            } else {
               getResources(map::putIfAbsent, p_248703_, path, p_248228_);
               map.forEach(p_249668_);
            }
         }

      }).ifRight((p_248234_) -> {
         LOGGER.error("Invalid path {}: {}", p_250848_, p_248234_.message());
      });
   }

   private static void getResources(PackResources.ResourceOutput p_249662_, String p_251249_, Path p_251290_, List<String> p_250451_) {
      Path path = p_251290_.resolve(p_251249_);
      PathPackResources.listPath(p_251249_, path, p_250451_, p_249662_);
   }

   @Nullable
   public IoSupplier<InputStream> getResource(PackType p_250512_, ResourceLocation p_251554_) {
      return FileUtil.decomposePath(p_251554_.getPath()).get().map((p_248224_) -> {
         String s = p_251554_.getNamespace();

         for(Path path : this.pathsForType.get(p_250512_)) {
            Path path1 = FileUtil.resolvePath(path.resolve(s), p_248224_);
            if (Files.exists(path1) && PathPackResources.validatePath(path1)) {
               return IoSupplier.create(path1);
            }
         }

         return null;
      }, (p_248230_) -> {
         LOGGER.error("Invalid path {}: {}", p_251554_, p_248230_.message());
         return null;
      });
   }

   public Set<String> getNamespaces(PackType p_10322_) {
      return this.namespaces;
   }

   @Nullable
   public <T> T getMetadataSection(MetadataSectionSerializer<T> p_10333_) {
      IoSupplier<InputStream> iosupplier = this.getRootResource("pack.mcmeta");
      if (iosupplier != null) {
         try (InputStream inputstream = iosupplier.get()) {
            T t = AbstractPackResources.getMetadataFromStream(p_10333_, inputstream);
            if (t != null) {
               return t;
            }
         } catch (IOException ioexception) {
         }
      }

      return this.metadata.get(p_10333_);
   }

   public String packId() {
      return "vanilla";
   }

   public boolean isBuiltin() {
      return true;
   }

   public void close() {
   }

   public ResourceProvider asProvider() {
      return (p_248239_) -> {
         return Optional.ofNullable(this.getResource(PackType.CLIENT_RESOURCES, p_248239_)).map((p_248221_) -> {
            return new Resource(this, p_248221_);
         });
      };
   }
}