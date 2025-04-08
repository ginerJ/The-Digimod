package net.minecraft.server.packs.repository;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public abstract class BuiltInPackSource implements RepositorySource {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String VANILLA_ID = "vanilla";
   private final PackType packType;
   private final VanillaPackResources vanillaPack;
   private final ResourceLocation packDir;

   public BuiltInPackSource(PackType p_249137_, VanillaPackResources p_250453_, ResourceLocation p_251151_) {
      this.packType = p_249137_;
      this.vanillaPack = p_250453_;
      this.packDir = p_251151_;
   }

   public void loadPacks(Consumer<Pack> p_250708_) {
      Pack pack = this.createVanillaPack(this.vanillaPack);
      if (pack != null) {
         p_250708_.accept(pack);
      }

      this.listBundledPacks(p_250708_);
   }

   @Nullable
   protected abstract Pack createVanillaPack(PackResources p_251690_);

   protected abstract Component getPackTitle(String p_251850_);

   public VanillaPackResources getVanillaPack() {
      return this.vanillaPack;
   }

   private void listBundledPacks(Consumer<Pack> p_249128_) {
      Map<String, Function<String, Pack>> map = new HashMap<>();
      this.populatePackList(map::put);
      map.forEach((p_250371_, p_250946_) -> {
         Pack pack = p_250946_.apply(p_250371_);
         if (pack != null) {
            p_249128_.accept(pack);
         }

      });
   }

   protected void populatePackList(BiConsumer<String, Function<String, Pack>> p_250341_) {
      this.vanillaPack.listRawPaths(this.packType, this.packDir, (p_250248_) -> {
         this.discoverPacksInPath(p_250248_, p_250341_);
      });
   }

   protected void discoverPacksInPath(@Nullable Path p_250013_, BiConsumer<String, Function<String, Pack>> p_249898_) {
      if (p_250013_ != null && Files.isDirectory(p_250013_)) {
         try {
            FolderRepositorySource.discoverPacks(p_250013_, true, (p_252012_, p_249772_) -> {
               p_249898_.accept(pathToId(p_252012_), (p_250601_) -> {
                  return this.createBuiltinPack(p_250601_, p_249772_, this.getPackTitle(p_250601_));
               });
            });
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to discover packs in {}", p_250013_, ioexception);
         }
      }

   }

   private static String pathToId(Path p_252048_) {
      return StringUtils.removeEnd(p_252048_.getFileName().toString(), ".zip");
   }

   @Nullable
   protected abstract Pack createBuiltinPack(String p_249992_, Pack.ResourcesSupplier p_248670_, Component p_252197_);
}