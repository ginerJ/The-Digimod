package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.WorldDataConfiguration;
import org.slf4j.Logger;

public class WorldLoader {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static <D, R> CompletableFuture<R> load(WorldLoader.InitConfig p_214363_, WorldLoader.WorldDataSupplier<D> p_214364_, WorldLoader.ResultFactory<D, R> p_214365_, Executor p_214366_, Executor p_214367_) {
      try {
         Pair<WorldDataConfiguration, CloseableResourceManager> pair = p_214363_.packConfig.createResourceManager();
         CloseableResourceManager closeableresourcemanager = pair.getSecond();
         LayeredRegistryAccess<RegistryLayer> layeredregistryaccess = RegistryLayer.createRegistryAccess();
         LayeredRegistryAccess<RegistryLayer> layeredregistryaccess1 = loadAndReplaceLayer(closeableresourcemanager, layeredregistryaccess, RegistryLayer.WORLDGEN, net.minecraftforge.registries.DataPackRegistriesHooks.getDataPackRegistries());
         RegistryAccess.Frozen registryaccess$frozen = layeredregistryaccess1.getAccessForLoading(RegistryLayer.DIMENSIONS);
         RegistryAccess.Frozen registryaccess$frozen1 = RegistryDataLoader.load(closeableresourcemanager, registryaccess$frozen, RegistryDataLoader.DIMENSION_REGISTRIES);
         WorldDataConfiguration worlddataconfiguration = pair.getFirst();
         WorldLoader.DataLoadOutput<D> dataloadoutput = p_214364_.get(new WorldLoader.DataLoadContext(closeableresourcemanager, worlddataconfiguration, registryaccess$frozen, registryaccess$frozen1));
         LayeredRegistryAccess<RegistryLayer> layeredregistryaccess2 = layeredregistryaccess1.replaceFrom(RegistryLayer.DIMENSIONS, dataloadoutput.finalDimensions);
         RegistryAccess.Frozen registryaccess$frozen2 = layeredregistryaccess2.getAccessForLoading(RegistryLayer.RELOADABLE);
         return ReloadableServerResources.loadResources(closeableresourcemanager, registryaccess$frozen2, worlddataconfiguration.enabledFeatures(), p_214363_.commandSelection(), p_214363_.functionCompilationLevel(), p_214366_, p_214367_).whenComplete((p_214370_, p_214371_) -> {
            if (p_214371_ != null) {
               closeableresourcemanager.close();
            }

         }).thenApplyAsync((p_248101_) -> {
            p_248101_.updateRegistryTags(registryaccess$frozen2);
            return p_214365_.create(closeableresourcemanager, p_248101_, layeredregistryaccess2, dataloadoutput.cookie);
         }, p_214367_);
      } catch (Exception exception) {
         return CompletableFuture.failedFuture(exception);
      }
   }

   private static RegistryAccess.Frozen loadLayer(ResourceManager p_251529_, LayeredRegistryAccess<RegistryLayer> p_250737_, RegistryLayer p_250790_, List<RegistryDataLoader.RegistryData<?>> p_249516_) {
      RegistryAccess.Frozen registryaccess$frozen = p_250737_.getAccessForLoading(p_250790_);
      return RegistryDataLoader.load(p_251529_, registryaccess$frozen, p_249516_);
   }

   private static LayeredRegistryAccess<RegistryLayer> loadAndReplaceLayer(ResourceManager p_249913_, LayeredRegistryAccess<RegistryLayer> p_252077_, RegistryLayer p_250346_, List<RegistryDataLoader.RegistryData<?>> p_250589_) {
      RegistryAccess.Frozen registryaccess$frozen = loadLayer(p_249913_, p_252077_, p_250346_, p_250589_);
      return p_252077_.replaceFrom(p_250346_, registryaccess$frozen);
   }

   public static record DataLoadContext(ResourceManager resources, WorldDataConfiguration dataConfiguration, RegistryAccess.Frozen datapackWorldgen, RegistryAccess.Frozen datapackDimensions) {
   }

   public static record DataLoadOutput<D>(D cookie, RegistryAccess.Frozen finalDimensions) {
   }

   public static record InitConfig(WorldLoader.PackConfig packConfig, Commands.CommandSelection commandSelection, int functionCompilationLevel) {
   }

   public static record PackConfig(PackRepository packRepository, WorldDataConfiguration initialDataConfig, boolean safeMode, boolean initMode) {
      public Pair<WorldDataConfiguration, CloseableResourceManager> createResourceManager() {
         FeatureFlagSet featureflagset = this.initMode ? FeatureFlags.REGISTRY.allFlags() : this.initialDataConfig.enabledFeatures();
         WorldDataConfiguration worlddataconfiguration = MinecraftServer.configurePackRepository(this.packRepository, this.initialDataConfig.dataPacks(), this.safeMode, featureflagset);
         if (!this.initMode) {
            worlddataconfiguration = worlddataconfiguration.expandFeatures(this.initialDataConfig.enabledFeatures());
         }

         List<PackResources> list = this.packRepository.openAllSelected();
         CloseableResourceManager closeableresourcemanager = new MultiPackResourceManager(PackType.SERVER_DATA, list);
         return Pair.of(worlddataconfiguration, closeableresourcemanager);
      }
   }

   @FunctionalInterface
   public interface ResultFactory<D, R> {
      R create(CloseableResourceManager p_214408_, ReloadableServerResources p_214409_, LayeredRegistryAccess<RegistryLayer> p_248844_, D p_214411_);
   }

   @FunctionalInterface
   public interface WorldDataSupplier<D> {
      WorldLoader.DataLoadOutput<D> get(WorldLoader.DataLoadContext p_251042_);
   }
}
