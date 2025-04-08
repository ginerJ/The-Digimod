package net.minecraft.gametest.framework;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.Difficulty;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.slf4j.Logger;

public class GameTestServer extends MinecraftServer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int PROGRESS_REPORT_INTERVAL = 20;
   private static final Services NO_SERVICES = new Services((MinecraftSessionService)null, ServicesKeySet.EMPTY, (GameProfileRepository)null, (GameProfileCache)null);
   private final List<GameTestBatch> testBatches;
   private final BlockPos spawnPos;
   private static final GameRules TEST_GAME_RULES = Util.make(new GameRules(), (p_177615_) -> {
      p_177615_.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, (MinecraftServer)null);
      p_177615_.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, (MinecraftServer)null);
   });
   private static final WorldOptions WORLD_OPTIONS = new WorldOptions(0L, false, false);
   @Nullable
   private MultipleTestTracker testTracker;

   public static GameTestServer create(Thread p_206607_, LevelStorageSource.LevelStorageAccess p_206608_, PackRepository p_206609_, Collection<GameTestBatch> p_206610_, BlockPos p_206611_) {
      if (p_206610_.isEmpty()) {
         throw new IllegalArgumentException("No test batches were given!");
      } else {
         p_206609_.reload();
         WorldDataConfiguration worlddataconfiguration = new WorldDataConfiguration(new DataPackConfig(new ArrayList<>(p_206609_.getAvailableIds()), List.of()), FeatureFlags.REGISTRY.allFlags());
         LevelSettings levelsettings = new LevelSettings("Test Level", GameType.CREATIVE, false, Difficulty.NORMAL, true, TEST_GAME_RULES, worlddataconfiguration);
         WorldLoader.PackConfig worldloader$packconfig = new WorldLoader.PackConfig(p_206609_, worlddataconfiguration, false, true);
         WorldLoader.InitConfig worldloader$initconfig = new WorldLoader.InitConfig(worldloader$packconfig, Commands.CommandSelection.DEDICATED, 4);

         try {
            LOGGER.debug("Starting resource loading");
            Stopwatch stopwatch = Stopwatch.createStarted();
            WorldStem worldstem = Util.blockUntilDone((p_248045_) -> {
               return WorldLoader.load(worldloader$initconfig, (p_258205_) -> {
                  Registry<LevelStem> registry = (new MappedRegistry<>(Registries.LEVEL_STEM, Lifecycle.stable())).freeze();
                  WorldDimensions.Complete worlddimensions$complete = p_258205_.datapackWorldgen().registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(WorldPresets.FLAT).value().createWorldDimensions().bake(registry);
                  return new WorldLoader.DataLoadOutput<>(new PrimaryLevelData(levelsettings, WORLD_OPTIONS, worlddimensions$complete.specialWorldProperty(), worlddimensions$complete.lifecycle()), worlddimensions$complete.dimensionsRegistryAccess());
               }, WorldStem::new, Util.backgroundExecutor(), p_248045_);
            }).get();
            stopwatch.stop();
            LOGGER.debug("Finished resource loading after {} ms", (long)stopwatch.elapsed(TimeUnit.MILLISECONDS));
            return new GameTestServer(p_206607_, p_206608_, p_206609_, worldstem, p_206610_, p_206611_);
         } catch (Exception exception) {
            LOGGER.warn("Failed to load vanilla datapack, bit oops", (Throwable)exception);
            System.exit(-1);
            throw new IllegalStateException();
         }
      }
   }

   public GameTestServer(Thread p_206597_, LevelStorageSource.LevelStorageAccess p_206598_, PackRepository p_206599_, WorldStem p_206600_, Collection<GameTestBatch> p_206601_, BlockPos p_206602_) {
      super(p_206597_, p_206598_, p_206599_, p_206600_, Proxy.NO_PROXY, DataFixers.getDataFixer(), NO_SERVICES, LoggerChunkProgressListener::new);
      this.testBatches = Lists.newArrayList(p_206601_);
      this.spawnPos = p_206602_;
   }

   public boolean initServer() {
      this.setPlayerList(new PlayerList(this, this.registries(), this.playerDataStorage, 1) {
      });
      if (!net.minecraftforge.server.ServerLifecycleHooks.handleServerAboutToStart(this)) return false;
      this.loadLevel();
      ServerLevel serverlevel = this.overworld();
      serverlevel.setDefaultSpawnPos(this.spawnPos, 0.0F);
      int i = 20000000;
      serverlevel.setWeatherParameters(20000000, 20000000, false, false);
      LOGGER.info("Started game test server");
      return net.minecraftforge.server.ServerLifecycleHooks.handleServerStarting(this);
   }

   public void tickServer(BooleanSupplier p_177619_) {
      super.tickServer(p_177619_);
      ServerLevel serverlevel = this.overworld();
      if (!this.haveTestsStarted()) {
         this.startTests(serverlevel);
      }

      if (serverlevel.getGameTime() % 20L == 0L) {
         LOGGER.info(this.testTracker.getProgressBar());
      }

      if (this.testTracker.isDone()) {
         this.halt(false);
         LOGGER.info(this.testTracker.getProgressBar());
         GlobalTestReporter.finish();
         LOGGER.info("========= {} GAME TESTS COMPLETE ======================", (int)this.testTracker.getTotalCount());
         if (this.testTracker.hasFailedRequired()) {
            LOGGER.info("{} required tests failed :(", (int)this.testTracker.getFailedRequiredCount());
            this.testTracker.getFailedRequired().forEach((p_206615_) -> {
               LOGGER.info("   - {}", (Object)p_206615_.getTestName());
            });
         } else {
            LOGGER.info("All {} required tests passed :)", (int)this.testTracker.getTotalCount());
         }

         if (this.testTracker.hasFailedOptional()) {
            LOGGER.info("{} optional tests failed", (int)this.testTracker.getFailedOptionalCount());
            this.testTracker.getFailedOptional().forEach((p_206613_) -> {
               LOGGER.info("   - {}", (Object)p_206613_.getTestName());
            });
         }

         LOGGER.info("====================================================");
      }

   }

   public void waitUntilNextTick() {
      this.runAllTasks();
   }

   public SystemReport fillServerSystemReport(SystemReport p_177613_) {
      p_177613_.setDetail("Type", "Game test server");
      return p_177613_;
   }

   public void onServerExit() {
      super.onServerExit();
      LOGGER.info("Game test server shutting down");
      System.exit(this.testTracker.getFailedRequiredCount());
   }

   public void onServerCrash(CrashReport p_177623_) {
      super.onServerCrash(p_177623_);
      LOGGER.error("Game test server crashed\n{}", (Object)p_177623_.getFriendlyReport());
      System.exit(1);
   }

   private void startTests(ServerLevel p_177625_) {
      Collection<GameTestInfo> collection = GameTestRunner.runTestBatches(this.testBatches, new BlockPos(0, -60, 0), Rotation.NONE, p_177625_, GameTestTicker.SINGLETON, 8);
      this.testTracker = new MultipleTestTracker(collection);
      LOGGER.info("{} tests are now running!", (int)this.testTracker.getTotalCount());
   }

   private boolean haveTestsStarted() {
      return this.testTracker != null;
   }

   public boolean isHardcore() {
      return false;
   }

   public int getOperatorUserPermissionLevel() {
      return 0;
   }

   public int getFunctionCompilationLevel() {
      return 4;
   }

   public boolean shouldRconBroadcast() {
      return false;
   }

   public boolean isDedicatedServer() {
      return false;
   }

   public int getRateLimitPacketsPerSecond() {
      return 0;
   }

   public boolean isEpollEnabled() {
      return false;
   }

   public boolean isCommandBlockEnabled() {
      return true;
   }

   public boolean isPublished() {
      return false;
   }

   public boolean shouldInformAdmins() {
      return false;
   }

   public boolean isSingleplayerOwner(GameProfile p_177617_) {
      return false;
   }
}
