package net.minecraft.client.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.stats.Stats;
import net.minecraft.util.ModCheck;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class IntegratedServer extends MinecraftServer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MIN_SIM_DISTANCE = 2;
   private final Minecraft minecraft;
   private boolean paused = true;
   private int publishedPort = -1;
   @Nullable
   private GameType publishedGameType;
   @Nullable
   private LanServerPinger lanPinger;
   @Nullable
   private UUID uuid;
   private int previousSimulationDistance = 0;

   public IntegratedServer(Thread p_235248_, Minecraft p_235249_, LevelStorageSource.LevelStorageAccess p_235250_, PackRepository p_235251_, WorldStem p_235252_, Services p_235253_, ChunkProgressListenerFactory p_235254_) {
      super(p_235248_, p_235250_, p_235251_, p_235252_, p_235249_.getProxy(), p_235249_.getFixerUpper(), p_235253_, p_235254_);
      this.setSingleplayerProfile(p_235249_.getUser().getGameProfile());
      this.setDemo(p_235249_.isDemo());
      this.setPlayerList(new IntegratedPlayerList(this, this.registries(), this.playerDataStorage));
      this.minecraft = p_235249_;
   }

   public boolean initServer() {
      LOGGER.info("Starting integrated minecraft server version {}", (Object)SharedConstants.getCurrentVersion().getName());
      this.setUsesAuthentication(true);
      this.setPvpAllowed(true);
      this.setFlightAllowed(true);
      this.initializeKeyPair();
      if (!net.minecraftforge.server.ServerLifecycleHooks.handleServerAboutToStart(this)) return false;
      this.loadLevel();
      GameProfile gameprofile = this.getSingleplayerProfile();
      String s = this.getWorldData().getLevelName();
      this.setMotd(gameprofile != null ? gameprofile.getName() + " - " + s : s);
      return net.minecraftforge.server.ServerLifecycleHooks.handleServerStarting(this);
   }

   public void tickServer(BooleanSupplier p_120049_) {
      boolean flag = this.paused;
      this.paused = Minecraft.getInstance().isPaused();
      ProfilerFiller profilerfiller = this.getProfiler();
      if (!flag && this.paused) {
         profilerfiller.push("autoSave");
         LOGGER.info("Saving and pausing game...");
         this.saveEverything(false, false, false);
         profilerfiller.pop();
      }

      boolean flag1 = Minecraft.getInstance().getConnection() != null;
      if (flag1 && this.paused) {
         this.tickPaused();
      } else {
         if (flag && !this.paused) {
            this.forceTimeSynchronization();
         }

         super.tickServer(p_120049_);
         int i = Math.max(2, this.minecraft.options.renderDistance().get());
         if (i != this.getPlayerList().getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", i, this.getPlayerList().getViewDistance());
            this.getPlayerList().setViewDistance(i);
         }

         int j = Math.max(2, this.minecraft.options.simulationDistance().get());
         if (j != this.previousSimulationDistance) {
            LOGGER.info("Changing simulation distance to {}, from {}", j, this.previousSimulationDistance);
            this.getPlayerList().setSimulationDistance(j);
            this.previousSimulationDistance = j;
         }

      }
   }

   private void tickPaused() {
      for(ServerPlayer serverplayer : this.getPlayerList().getPlayers()) {
         serverplayer.awardStat(Stats.TOTAL_WORLD_TIME);
      }

   }

   public boolean shouldRconBroadcast() {
      return true;
   }

   public boolean shouldInformAdmins() {
      return true;
   }

   public File getServerDirectory() {
      return this.minecraft.gameDirectory;
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

   public void onServerCrash(CrashReport p_120051_) {
      this.minecraft.delayCrashRaw(p_120051_);
   }

   public SystemReport fillServerSystemReport(SystemReport p_174970_) {
      p_174970_.setDetail("Type", "Integrated Server (map_client.txt)");
      p_174970_.setDetail("Is Modded", () -> {
         return this.getModdedStatus().fullDescription();
      });
      p_174970_.setDetail("Launched Version", this.minecraft::getLaunchedVersion);
      return p_174970_;
   }

   public ModCheck getModdedStatus() {
      return Minecraft.checkModStatus().merge(super.getModdedStatus());
   }

   public boolean publishServer(@Nullable GameType p_120041_, boolean p_120042_, int p_120043_) {
      try {
         this.minecraft.prepareForMultiplayer();
         this.minecraft.getProfileKeyPairManager().prepareKeyPair().thenAcceptAsync((p_263550_) -> {
            p_263550_.ifPresent((p_263549_) -> {
               ClientPacketListener clientpacketlistener = this.minecraft.getConnection();
               if (clientpacketlistener != null) {
                  clientpacketlistener.setKeyPair(p_263549_);
               }

            });
         }, this.minecraft);
         this.getConnection().startTcpServerListener((InetAddress)null, p_120043_);
         LOGGER.info("Started serving on {}", (int)p_120043_);
         this.publishedPort = p_120043_;
         this.lanPinger = new LanServerPinger(this.getMotd(), "" + p_120043_);
         this.lanPinger.start();
         this.publishedGameType = p_120041_;
         this.getPlayerList().setAllowCheatsForAllPlayers(p_120042_);
         int i = this.getProfilePermissions(this.minecraft.player.getGameProfile());
         this.minecraft.player.setPermissionLevel(i);

         for(ServerPlayer serverplayer : this.getPlayerList().getPlayers()) {
            this.getCommands().sendCommands(serverplayer);
         }

         return true;
      } catch (IOException ioexception) {
         return false;
      }
   }

   public void stopServer() {
      super.stopServer();
      if (this.lanPinger != null) {
         this.lanPinger.interrupt();
         this.lanPinger = null;
      }

   }

   public void halt(boolean p_120053_) {
      if (isRunning())
      this.executeBlocking(() -> {
         for(ServerPlayer serverplayer : Lists.newArrayList(this.getPlayerList().getPlayers())) {
            if (!serverplayer.getUUID().equals(this.uuid)) {
               this.getPlayerList().remove(serverplayer);
            }
         }

      });
      super.halt(p_120053_);
      if (this.lanPinger != null) {
         this.lanPinger.interrupt();
         this.lanPinger = null;
      }

   }

   public boolean isPublished() {
      return this.publishedPort > -1;
   }

   public int getPort() {
      return this.publishedPort;
   }

   public void setDefaultGameType(GameType p_120039_) {
      super.setDefaultGameType(p_120039_);
      this.publishedGameType = null;
   }

   public boolean isCommandBlockEnabled() {
      return true;
   }

   public int getOperatorUserPermissionLevel() {
      return 2;
   }

   public int getFunctionCompilationLevel() {
      return 2;
   }

   public void setUUID(UUID p_120047_) {
      this.uuid = p_120047_;
   }

   public boolean isSingleplayerOwner(GameProfile p_120045_) {
      return this.getSingleplayerProfile() != null && p_120045_.getName().equalsIgnoreCase(this.getSingleplayerProfile().getName());
   }

   public int getScaledTrackingDistance(int p_120056_) {
      return (int)(this.minecraft.options.entityDistanceScaling().get() * (double)p_120056_);
   }

   public boolean forceSynchronousWrites() {
      return this.minecraft.options.syncWrites;
   }

   @Nullable
   public GameType getForcedGameType() {
      return this.isPublished() ? MoreObjects.firstNonNull(this.publishedGameType, this.worldData.getGameType()) : null;
   }
}
