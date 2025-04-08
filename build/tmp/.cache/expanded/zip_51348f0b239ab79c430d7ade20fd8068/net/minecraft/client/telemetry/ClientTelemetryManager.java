package net.minecraft.client.telemetry;

import com.google.common.base.Suppliers;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientTelemetryManager implements AutoCloseable {
   private static final AtomicInteger THREAD_COUNT = new AtomicInteger(1);
   private static final Executor EXECUTOR = Executors.newSingleThreadExecutor((p_261485_) -> {
      Thread thread = new Thread(p_261485_);
      thread.setName("Telemetry-Sender-#" + THREAD_COUNT.getAndIncrement());
      return thread;
   });
   private final UserApiService userApiService;
   private final TelemetryPropertyMap deviceSessionProperties;
   private final Path logDirectory;
   private final CompletableFuture<Optional<TelemetryLogManager>> logManager;
   private final Supplier<TelemetryEventSender> outsideSessionSender = Suppliers.memoize(this::createEventSender);

   public ClientTelemetryManager(Minecraft p_261610_, UserApiService p_261552_, User p_262159_) {
      this.userApiService = p_261552_;
      TelemetryPropertyMap.Builder telemetrypropertymap$builder = TelemetryPropertyMap.builder();
      p_262159_.getXuid().ifPresent((p_261810_) -> {
         telemetrypropertymap$builder.put(TelemetryProperty.USER_ID, p_261810_);
      });
      p_262159_.getClientId().ifPresent((p_261690_) -> {
         telemetrypropertymap$builder.put(TelemetryProperty.CLIENT_ID, p_261690_);
      });
      telemetrypropertymap$builder.put(TelemetryProperty.MINECRAFT_SESSION_ID, UUID.randomUUID());
      telemetrypropertymap$builder.put(TelemetryProperty.GAME_VERSION, SharedConstants.getCurrentVersion().getId());
      telemetrypropertymap$builder.put(TelemetryProperty.OPERATING_SYSTEM, Util.getPlatform().telemetryName());
      telemetrypropertymap$builder.put(TelemetryProperty.PLATFORM, System.getProperty("os.name"));
      telemetrypropertymap$builder.put(TelemetryProperty.CLIENT_MODDED, Minecraft.checkModStatus().shouldReportAsModified());
      telemetrypropertymap$builder.putIfNotNull(TelemetryProperty.LAUNCHER_NAME, System.getProperty("minecraft.launcher.brand"));
      this.deviceSessionProperties = telemetrypropertymap$builder.build();
      this.logDirectory = p_261610_.gameDirectory.toPath().resolve("logs/telemetry");
      this.logManager = TelemetryLogManager.open(this.logDirectory);
   }

   public WorldSessionTelemetryManager createWorldSessionManager(boolean p_286373_, @Nullable Duration p_286752_, @Nullable String p_286568_) {
      return new WorldSessionTelemetryManager(this.createEventSender(), p_286373_, p_286752_, p_286568_);
   }

   public TelemetryEventSender getOutsideSessionSender() {
      return this.outsideSessionSender.get();
   }

   private TelemetryEventSender createEventSender() {
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         return TelemetryEventSender.DISABLED;
      } else {
         TelemetrySession telemetrysession = this.userApiService.newTelemetrySession(EXECUTOR);
         if (!telemetrysession.isEnabled()) {
            return TelemetryEventSender.DISABLED;
         } else {
            CompletableFuture<Optional<TelemetryEventLogger>> completablefuture = this.logManager.thenCompose((p_261737_) -> {
               return p_261737_.map(TelemetryLogManager::openLogger).orElseGet(() -> {
                  return CompletableFuture.completedFuture(Optional.empty());
               });
            });
            return (p_261827_, p_261818_) -> {
               if (!p_261827_.isOptIn() || Minecraft.getInstance().telemetryOptInExtra()) {
                  TelemetryPropertyMap.Builder telemetrypropertymap$builder = TelemetryPropertyMap.builder();
                  telemetrypropertymap$builder.putAll(this.deviceSessionProperties);
                  telemetrypropertymap$builder.put(TelemetryProperty.EVENT_TIMESTAMP_UTC, Instant.now());
                  telemetrypropertymap$builder.put(TelemetryProperty.OPT_IN, p_261827_.isOptIn());
                  p_261818_.accept(telemetrypropertymap$builder);
                  TelemetryEventInstance telemetryeventinstance = new TelemetryEventInstance(p_261827_, telemetrypropertymap$builder.build());
                  completablefuture.thenAccept((p_262038_) -> {
                     if (!p_262038_.isEmpty()) {
                        p_262038_.get().log(telemetryeventinstance);
                        telemetryeventinstance.export(telemetrysession).send();
                     }
                  });
               }
            };
         }
      }
   }

   public Path getLogDirectory() {
      return this.logDirectory;
   }

   public void close() {
      this.logManager.thenAccept((p_261643_) -> {
         p_261643_.ifPresent(TelemetryLogManager::close);
      });
   }
}