package net.minecraft.server.packs;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class FilePackResources extends AbstractPackResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Splitter SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
   private final File file;
   @Nullable
   private ZipFile zipFile;
   private boolean failedToLoad;

   public FilePackResources(String p_256076_, File p_255707_, boolean p_256556_) {
      super(p_256076_, p_256556_);
      this.file = p_255707_;
   }

   @Nullable
   private ZipFile getOrCreateZipFile() {
      if (this.failedToLoad) {
         return null;
      } else {
         if (this.zipFile == null) {
            try {
               this.zipFile = new ZipFile(this.file);
            } catch (IOException ioexception) {
               LOGGER.error("Failed to open pack {}", this.file, ioexception);
               this.failedToLoad = true;
               return null;
            }
         }

         return this.zipFile;
      }
   }

   private static String getPathFromLocation(PackType p_250585_, ResourceLocation p_251470_) {
      return String.format(Locale.ROOT, "%s/%s/%s", p_250585_.getDirectory(), p_251470_.getNamespace(), p_251470_.getPath());
   }

   @Nullable
   public IoSupplier<InputStream> getRootResource(String... p_248514_) {
      return this.getResource(String.join("/", p_248514_));
   }

   public IoSupplier<InputStream> getResource(PackType p_249605_, ResourceLocation p_252147_) {
      return this.getResource(getPathFromLocation(p_249605_, p_252147_));
   }

   @Nullable
   private IoSupplier<InputStream> getResource(String p_251795_) {
      ZipFile zipfile = this.getOrCreateZipFile();
      if (zipfile == null) {
         return null;
      } else {
         ZipEntry zipentry = zipfile.getEntry(p_251795_);
         return zipentry == null ? null : IoSupplier.create(zipfile, zipentry);
      }
   }

   public Set<String> getNamespaces(PackType p_10238_) {
      ZipFile zipfile = this.getOrCreateZipFile();
      if (zipfile == null) {
         return Set.of();
      } else {
         Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
         Set<String> set = Sets.newHashSet();

         while(enumeration.hasMoreElements()) {
            ZipEntry zipentry = enumeration.nextElement();
            String s = zipentry.getName();
            if (s.startsWith(p_10238_.getDirectory() + "/")) {
               List<String> list = Lists.newArrayList(SPLITTER.split(s));
               if (list.size() > 1) {
                  String s1 = list.get(1);
                  if (s1.equals(s1.toLowerCase(Locale.ROOT))) {
                     set.add(s1);
                  } else {
                     LOGGER.warn("Ignored non-lowercase namespace: {} in {}", s1, this.file);
                  }
               }
            }
         }

         return set;
      }
   }

   protected void finalize() throws Throwable {
      this.close();
      super.finalize();
   }

   public void close() {
      if (this.zipFile != null) {
         IOUtils.closeQuietly((Closeable)this.zipFile);
         this.zipFile = null;
      }

   }

   public void listResources(PackType p_250500_, String p_249598_, String p_251613_, PackResources.ResourceOutput p_250655_) {
      ZipFile zipfile = this.getOrCreateZipFile();
      if (zipfile != null) {
         Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
         String s = p_250500_.getDirectory() + "/" + p_249598_ + "/";
         String s1 = s + p_251613_ + "/";

         while(enumeration.hasMoreElements()) {
            ZipEntry zipentry = enumeration.nextElement();
            if (!zipentry.isDirectory()) {
               String s2 = zipentry.getName();
               if (s2.startsWith(s1)) {
                  String s3 = s2.substring(s.length());
                  ResourceLocation resourcelocation = ResourceLocation.tryBuild(p_249598_, s3);
                  if (resourcelocation != null) {
                     p_250655_.accept(resourcelocation, IoSupplier.create(zipfile, zipentry));
                  } else {
                     LOGGER.warn("Invalid path in datapack: {}:{}, ignoring", p_249598_, s3);
                  }
               }
            }
         }

      }
   }
}