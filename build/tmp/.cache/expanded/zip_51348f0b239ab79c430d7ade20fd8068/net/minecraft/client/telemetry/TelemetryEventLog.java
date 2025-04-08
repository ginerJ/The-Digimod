package net.minecraft.client.telemetry;

import com.mojang.logging.LogUtils;
import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executor;
import net.minecraft.util.eventlog.JsonEventLog;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TelemetryEventLog implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final JsonEventLog<TelemetryEventInstance> log;
   private final ProcessorMailbox<Runnable> mailbox;

   public TelemetryEventLog(FileChannel p_261731_, Executor p_262010_) {
      this.log = new JsonEventLog<>(TelemetryEventInstance.CODEC, p_261731_);
      this.mailbox = ProcessorMailbox.create(p_262010_, "telemetry-event-log");
   }

   public TelemetryEventLogger logger() {
      return (p_261508_) -> {
         this.mailbox.tell(() -> {
            try {
               this.log.write(p_261508_);
            } catch (IOException ioexception) {
               LOGGER.error("Failed to write telemetry event to log", (Throwable)ioexception);
            }

         });
      };
   }

   public void close() {
      this.mailbox.tell(() -> {
         IOUtils.closeQuietly((Closeable)this.log);
      });
      this.mailbox.close();
   }
}