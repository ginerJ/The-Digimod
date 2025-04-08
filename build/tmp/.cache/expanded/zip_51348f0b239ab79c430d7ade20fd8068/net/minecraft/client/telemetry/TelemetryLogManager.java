package net.minecraft.client.telemetry;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.eventlog.EventLogDirectory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TelemetryLogManager implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String RAW_EXTENSION = ".json";
   private static final int EXPIRY_DAYS = 7;
   private final EventLogDirectory directory;
   @Nullable
   private CompletableFuture<Optional<TelemetryEventLog>> sessionLog;

   private TelemetryLogManager(EventLogDirectory p_261728_) {
      this.directory = p_261728_;
   }

   public static CompletableFuture<Optional<TelemetryLogManager>> open(Path p_262078_) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            EventLogDirectory eventlogdirectory = EventLogDirectory.open(p_262078_, ".json");
            eventlogdirectory.listFiles().prune(LocalDate.now(), 7).compressAll();
            return Optional.of(new TelemetryLogManager(eventlogdirectory));
         } catch (Exception exception) {
            LOGGER.error("Failed to create telemetry log manager", (Throwable)exception);
            return Optional.empty();
         }
      }, Util.backgroundExecutor());
   }

   public CompletableFuture<Optional<TelemetryEventLogger>> openLogger() {
      if (this.sessionLog == null) {
         this.sessionLog = CompletableFuture.supplyAsync(() -> {
            try {
               EventLogDirectory.RawFile eventlogdirectory$rawfile = this.directory.createNewFile(LocalDate.now());
               FileChannel filechannel = eventlogdirectory$rawfile.openChannel();
               return Optional.of(new TelemetryEventLog(filechannel, Util.backgroundExecutor()));
            } catch (IOException ioexception) {
               LOGGER.error("Failed to open channel for telemetry event log", (Throwable)ioexception);
               return Optional.empty();
            }
         }, Util.backgroundExecutor());
      }

      return this.sessionLog.thenApply((p_262106_) -> {
         return p_262106_.map(TelemetryEventLog::logger);
      });
   }

   public void close() {
      if (this.sessionLog != null) {
         this.sessionLog.thenAccept((p_261871_) -> {
            p_261871_.ifPresent(TelemetryEventLog::close);
         });
      }

   }
}