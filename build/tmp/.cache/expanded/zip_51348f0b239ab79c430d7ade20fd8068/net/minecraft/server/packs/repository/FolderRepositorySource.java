package net.minecraft.server.packs.repository;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.linkfs.LinkFileSystem;
import org.slf4j.Logger;

public class FolderRepositorySource implements RepositorySource {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path folder;
   private final PackType packType;
   private final PackSource packSource;

   public FolderRepositorySource(Path p_251796_, PackType p_251664_, PackSource p_250854_) {
      this.folder = p_251796_;
      this.packType = p_251664_;
      this.packSource = p_250854_;
   }

   private static String nameFromPath(Path p_248745_) {
      return p_248745_.getFileName().toString();
   }

   public void loadPacks(Consumer<Pack> p_250965_) {
      try {
         FileUtil.createDirectoriesSafe(this.folder);
         discoverPacks(this.folder, false, (p_248243_, p_248244_) -> {
            String s = nameFromPath(p_248243_);
            Pack pack = Pack.readMetaAndCreate("file/" + s, Component.literal(s), false, p_248244_, this.packType, Pack.Position.TOP, this.packSource);
            if (pack != null) {
               p_250965_.accept(pack);
            }

         });
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to list packs in {}", this.folder, ioexception);
      }

   }

   public static void discoverPacks(Path p_248794_, boolean p_255987_, BiConsumer<Path, Pack.ResourcesSupplier> p_248580_) throws IOException {
      try (DirectoryStream<Path> directorystream = Files.newDirectoryStream(p_248794_)) {
         for(Path path : directorystream) {
            Pack.ResourcesSupplier pack$resourcessupplier = detectPackResources(path, p_255987_);
            if (pack$resourcessupplier != null) {
               p_248580_.accept(path, pack$resourcessupplier);
            }
         }
      }

   }

   @Nullable
   public static Pack.ResourcesSupplier detectPackResources(Path p_255665_, boolean p_255971_) {
      BasicFileAttributes basicfileattributes;
      try {
         basicfileattributes = Files.readAttributes(p_255665_, BasicFileAttributes.class);
      } catch (NoSuchFileException nosuchfileexception) {
         return null;
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to read properties of '{}', ignoring", p_255665_, ioexception);
         return null;
      }

      if (basicfileattributes.isDirectory() && Files.isRegularFile(p_255665_.resolve("pack.mcmeta"))) {
         return (p_255538_) -> {
            return new PathPackResources(p_255538_, p_255665_, p_255971_);
         };
      } else {
         if (basicfileattributes.isRegularFile() && p_255665_.getFileName().toString().endsWith(".zip")) {
            FileSystem filesystem = p_255665_.getFileSystem();
            if (filesystem == FileSystems.getDefault() || filesystem instanceof LinkFileSystem) {
               File file1 = p_255665_.toFile();
               return (p_255541_) -> {
                  return new FilePackResources(p_255541_, file1, p_255971_);
               };
            }
         }

         LOGGER.info("Found non-pack entry '{}', ignoring", (Object)p_255665_);
         return null;
      }
   }
}