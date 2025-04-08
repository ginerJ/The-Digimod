package net.minecraft.client.gui.screens.worldselection;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DatapackLoadFailureScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SymlinkWarningScreen;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.validation.ContentValidationException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldOpenFlows {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Minecraft minecraft;
   private final LevelStorageSource levelSource;

   public WorldOpenFlows(Minecraft p_233093_, LevelStorageSource p_233094_) {
      this.minecraft = p_233093_;
      this.levelSource = p_233094_;
   }

   public void loadLevel(Screen p_233134_, String p_233135_) {
      this.doLoadLevel(p_233134_, p_233135_, false, true);
   }

   public void createFreshLevel(String p_233158_, LevelSettings p_233159_, WorldOptions p_249243_, Function<RegistryAccess, WorldDimensions> p_249252_) {
      LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = this.createWorldAccess(p_233158_);
      if (levelstoragesource$levelstorageaccess != null) {
         PackRepository packrepository = ServerPacksSource.createPackRepository(levelstoragesource$levelstorageaccess);
         WorldDataConfiguration worlddataconfiguration = p_233159_.getDataConfiguration();

         try {
            WorldLoader.PackConfig worldloader$packconfig = new WorldLoader.PackConfig(packrepository, worlddataconfiguration, false, false);
            WorldStem worldstem = this.loadWorldDataBlocking(worldloader$packconfig, (p_258145_) -> {
               WorldDimensions.Complete worlddimensions$complete = p_249252_.apply(p_258145_.datapackWorldgen()).bake(p_258145_.datapackDimensions().registryOrThrow(Registries.LEVEL_STEM));
               return new WorldLoader.DataLoadOutput<>(new PrimaryLevelData(p_233159_, p_249243_, worlddimensions$complete.specialWorldProperty(), worlddimensions$complete.lifecycle()), worlddimensions$complete.dimensionsRegistryAccess());
            }, WorldStem::new);
            this.minecraft.doWorldLoad(p_233158_, levelstoragesource$levelstorageaccess, packrepository, worldstem, true);
         } catch (Exception exception) {
            LOGGER.warn("Failed to load datapacks, can't proceed with server load", (Throwable)exception);
            safeCloseAccess(levelstoragesource$levelstorageaccess, p_233158_);
         }

      }
   }

   @Nullable
   private LevelStorageSource.LevelStorageAccess createWorldAccess(String p_233156_) {
      try {
         return this.levelSource.validateAndCreateAccess(p_233156_);
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to read level {} data", p_233156_, ioexception);
         SystemToast.onWorldAccessFailure(this.minecraft, p_233156_);
         this.minecraft.setScreen((Screen)null);
         return null;
      } catch (ContentValidationException contentvalidationexception) {
         LOGGER.warn("{}", (Object)contentvalidationexception.getMessage());
         this.minecraft.setScreen(new SymlinkWarningScreen((Screen)null));
         return null;
      }
   }

   public void createLevelFromExistingSettings(LevelStorageSource.LevelStorageAccess p_250919_, ReloadableServerResources p_248897_, LayeredRegistryAccess<RegistryLayer> p_250801_, WorldData p_251654_) {
      PackRepository packrepository = ServerPacksSource.createPackRepository(p_250919_);
      CloseableResourceManager closeableresourcemanager = (new WorldLoader.PackConfig(packrepository, p_251654_.getDataConfiguration(), false, false)).createResourceManager().getSecond();
      this.minecraft.doWorldLoad(p_250919_.getLevelId(), p_250919_, packrepository, new WorldStem(closeableresourcemanager, p_248897_, p_250801_, p_251654_), true);
   }

   private WorldStem loadWorldStem(LevelStorageSource.LevelStorageAccess p_233123_, boolean p_233124_, PackRepository p_233125_) throws Exception {
      WorldLoader.PackConfig worldloader$packconfig = this.getPackConfigFromLevelData(p_233123_, p_233124_, p_233125_);
      return this.loadWorldDataBlocking(worldloader$packconfig, (p_247851_) -> {
         DynamicOps<Tag> dynamicops = RegistryOps.create(NbtOps.INSTANCE, p_247851_.datapackWorldgen());
         Registry<LevelStem> registry = p_247851_.datapackDimensions().registryOrThrow(Registries.LEVEL_STEM);
         Pair<WorldData, WorldDimensions.Complete> pair = p_233123_.getDataTag(dynamicops, p_247851_.dataConfiguration(), registry, p_247851_.datapackWorldgen().allRegistriesLifecycle());
         if (pair == null) {
            throw new IllegalStateException("Failed to load world");
         } else {
            return new WorldLoader.DataLoadOutput<>(pair.getFirst(), pair.getSecond().dimensionsRegistryAccess());
         }
      }, WorldStem::new);
   }

   public Pair<LevelSettings, WorldCreationContext> recreateWorldData(LevelStorageSource.LevelStorageAccess p_249540_) throws Exception {
      PackRepository packrepository = ServerPacksSource.createPackRepository(p_249540_);
      WorldLoader.PackConfig worldloader$packconfig = this.getPackConfigFromLevelData(p_249540_, false, packrepository);
      @OnlyIn(Dist.CLIENT)
      record Data(LevelSettings levelSettings, WorldOptions options, Registry<LevelStem> existingDimensions) {
      }
      return this.<Data, Pair<LevelSettings, WorldCreationContext>>loadWorldDataBlocking(worldloader$packconfig, (p_247857_) -> {
         DynamicOps<Tag> dynamicops = RegistryOps.create(NbtOps.INSTANCE, p_247857_.datapackWorldgen());
         Registry<LevelStem> registry = (new MappedRegistry<>(Registries.LEVEL_STEM, Lifecycle.stable())).freeze();
         Pair<WorldData, WorldDimensions.Complete> pair = p_249540_.getDataTag(dynamicops, p_247857_.dataConfiguration(), registry, p_247857_.datapackWorldgen().allRegistriesLifecycle());
         if (pair == null) {
            throw new IllegalStateException("Failed to load world");
         } else {
            return new WorldLoader.DataLoadOutput<>(new Data(pair.getFirst().getLevelSettings(), pair.getFirst().worldGenOptions(), pair.getSecond().dimensions()), p_247857_.datapackDimensions());
         }
      }, (p_247840_, p_247841_, p_247842_, p_247843_) -> {
         p_247840_.close();
         return Pair.of(p_247843_.levelSettings, new WorldCreationContext(p_247843_.options, new WorldDimensions(p_247843_.existingDimensions), p_247842_, p_247841_, p_247843_.levelSettings.getDataConfiguration()));
      });
   }

   private WorldLoader.PackConfig getPackConfigFromLevelData(LevelStorageSource.LevelStorageAccess p_249986_, boolean p_248615_, PackRepository p_249167_) {
      WorldDataConfiguration worlddataconfiguration = p_249986_.getDataConfiguration();
      if (worlddataconfiguration == null) {
         throw new IllegalStateException("Failed to load data pack config");
      } else {
         return new WorldLoader.PackConfig(p_249167_, worlddataconfiguration, p_248615_, false);
      }
   }

   public WorldStem loadWorldStem(LevelStorageSource.LevelStorageAccess p_233120_, boolean p_233121_) throws Exception {
      PackRepository packrepository = ServerPacksSource.createPackRepository(p_233120_);
      return this.loadWorldStem(p_233120_, p_233121_, packrepository);
   }

   private <D, R> R loadWorldDataBlocking(WorldLoader.PackConfig p_250997_, WorldLoader.WorldDataSupplier<D> p_251759_, WorldLoader.ResultFactory<D, R> p_249635_) throws Exception {
      WorldLoader.InitConfig worldloader$initconfig = new WorldLoader.InitConfig(p_250997_, Commands.CommandSelection.INTEGRATED, 2);
      CompletableFuture<R> completablefuture = WorldLoader.load(worldloader$initconfig, p_251759_, p_249635_, Util.backgroundExecutor(), this.minecraft);
      this.minecraft.managedBlock(completablefuture::isDone);
      return completablefuture.get();
   }

   private void doLoadLevel(Screen p_233146_, String p_233147_, boolean p_233148_, boolean p_233149_) {
      // FORGE: Patch in overload to reduce further patching
      this.doLoadLevel(p_233146_, p_233147_, p_233148_, p_233149_, false);
   }

   // FORGE: Patch in confirmExperimentalWarning which confirms the experimental warning when true
   private void doLoadLevel(Screen p_233146_, String p_233147_, boolean p_233148_, boolean p_233149_, boolean confirmExperimentalWarning) {
      LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = this.createWorldAccess(p_233147_);
      if (levelstoragesource$levelstorageaccess != null) {
         PackRepository packrepository = ServerPacksSource.createPackRepository(levelstoragesource$levelstorageaccess);

         WorldStem worldstem;
         try {
            levelstoragesource$levelstorageaccess.readAdditionalLevelSaveData(); // Read extra (e.g. modded) data from the world before creating it
            worldstem = this.loadWorldStem(levelstoragesource$levelstorageaccess, p_233148_, packrepository);
            if (confirmExperimentalWarning && worldstem.worldData() instanceof PrimaryLevelData pld) {
               pld.withConfirmedWarning(true);
            }
         } catch (Exception exception) {
            LOGGER.warn("Failed to load level data or datapacks, can't proceed with server load", (Throwable)exception);
            if (!p_233148_) {
               this.minecraft.setScreen(new DatapackLoadFailureScreen(() -> {
                  this.doLoadLevel(p_233146_, p_233147_, true, p_233149_);
               }));
            } else {
               this.minecraft.setScreen(new AlertScreen(() -> {
                  this.minecraft.setScreen((Screen)null);
               }, Component.translatable("datapackFailure.safeMode.failed.title"), Component.translatable("datapackFailure.safeMode.failed.description"), CommonComponents.GUI_TO_TITLE, true));
            }

            safeCloseAccess(levelstoragesource$levelstorageaccess, p_233147_);
            return;
         }

         WorldData worlddata = worldstem.worldData();
         boolean flag = worlddata.worldGenOptions().isOldCustomizedWorld();
         boolean flag1 = worlddata.worldGenSettingsLifecycle() != Lifecycle.stable();
         // Forge: Skip confirmation if it has been done already for this world
         boolean skipConfirmation = worlddata instanceof PrimaryLevelData pld && pld.hasConfirmedExperimentalWarning();
         if (skipConfirmation || !p_233149_ || !flag && !flag1) {
            this.minecraft.getDownloadedPackSource().loadBundledResourcePack(levelstoragesource$levelstorageaccess).thenApply((p_233177_) -> {
               return true;
            }).exceptionallyComposeAsync((p_233183_) -> {
               LOGGER.warn("Failed to load pack: ", p_233183_);
               return this.promptBundledPackLoadFailure();
            }, this.minecraft).thenAcceptAsync((p_233168_) -> {
               if (p_233168_) {
                  this.minecraft.doWorldLoad(p_233147_, levelstoragesource$levelstorageaccess, packrepository, worldstem, false);
               } else {
                  worldstem.close();
                  safeCloseAccess(levelstoragesource$levelstorageaccess, p_233147_);
                  this.minecraft.getDownloadedPackSource().clearServerPack().thenRunAsync(() -> {
                     this.minecraft.setScreen(p_233146_);
                  }, this.minecraft);
               }

            }, this.minecraft).exceptionally((p_233175_) -> {
               this.minecraft.delayCrash(CrashReport.forThrowable(p_233175_, "Load world"));
               return null;
            });
         } else {
            if (flag) // Forge: For legacy world options, let vanilla handle it.
            this.askForBackup(p_233146_, p_233147_, flag, () -> {
               this.doLoadLevel(p_233146_, p_233147_, p_233148_, false);
            });
            else net.minecraftforge.client.ForgeHooksClient.createWorldConfirmationScreen(() -> this.doLoadLevel(p_233146_, p_233147_, p_233148_, false, true));
            worldstem.close();
            safeCloseAccess(levelstoragesource$levelstorageaccess, p_233147_);
         }
      }
   }

   private CompletableFuture<Boolean> promptBundledPackLoadFailure() {
      CompletableFuture<Boolean> completablefuture = new CompletableFuture<>();
      this.minecraft.setScreen(new ConfirmScreen(completablefuture::complete, Component.translatable("multiplayer.texturePrompt.failure.line1"), Component.translatable("multiplayer.texturePrompt.failure.line2"), CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
      return completablefuture;
   }

   private static void safeCloseAccess(LevelStorageSource.LevelStorageAccess p_233117_, String p_233118_) {
      try {
         p_233117_.close();
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to unlock access to level {}", p_233118_, ioexception);
      }

   }

   private void askForBackup(Screen p_233141_, String p_233142_, boolean p_233143_, Runnable p_233144_) {
      Component component;
      Component component1;
      if (p_233143_) {
         component = Component.translatable("selectWorld.backupQuestion.customized");
         component1 = Component.translatable("selectWorld.backupWarning.customized");
      } else {
         component = Component.translatable("selectWorld.backupQuestion.experimental");
         component1 = Component.translatable("selectWorld.backupWarning.experimental");
      }

      this.minecraft.setScreen(new BackupConfirmScreen(p_233141_, (p_233172_, p_233173_) -> {
         if (p_233172_) {
            EditWorldScreen.makeBackupAndShowToast(this.levelSource, p_233142_);
         }

         p_233144_.run();
      }, component, component1, false));
   }

   public static void confirmWorldCreation(Minecraft p_270593_, CreateWorldScreen p_270733_, Lifecycle p_270539_, Runnable p_270158_, boolean p_270709_) {
      BooleanConsumer booleanconsumer = (p_233154_) -> {
         if (p_233154_) {
            p_270158_.run();
         } else {
            p_270593_.setScreen(p_270733_);
         }

      };
      if (!p_270709_ && p_270539_ != Lifecycle.stable()) {
         if (p_270539_ == Lifecycle.experimental()) {
            p_270593_.setScreen(new ConfirmScreen(booleanconsumer, Component.translatable("selectWorld.warning.experimental.title"), Component.translatable("selectWorld.warning.experimental.question")));
         } else {
            p_270593_.setScreen(new ConfirmScreen(booleanconsumer, Component.translatable("selectWorld.warning.deprecated.title"), Component.translatable("selectWorld.warning.deprecated.question")));
         }
      } else {
         p_270158_.run();
      }

   }
}
