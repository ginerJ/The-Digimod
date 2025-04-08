package net.minecraft.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.network.chat.Component;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class HttpUtil {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final ListeningExecutorService DOWNLOAD_EXECUTOR = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).setNameFormat("Downloader %d").build()));

   private HttpUtil() {
   }

   public static CompletableFuture<?> downloadTo(File p_216226_, URL p_216227_, Map<String, String> p_216228_, int p_216229_, @Nullable ProgressListener p_216230_, Proxy p_216231_) {
      return CompletableFuture.supplyAsync(() -> {
         HttpURLConnection httpurlconnection = null;
         InputStream inputstream = null;
         OutputStream outputstream = null;
         if (p_216230_ != null) {
            p_216230_.progressStart(Component.translatable("resourcepack.downloading"));
            p_216230_.progressStage(Component.translatable("resourcepack.requesting"));
         }

         try {
            try {
               byte[] abyte = new byte[4096];
               httpurlconnection = (HttpURLConnection)p_216227_.openConnection(p_216231_);
               httpurlconnection.setInstanceFollowRedirects(true);
               float f1 = 0.0F;
               float f = (float)p_216228_.entrySet().size();

               for(Map.Entry<String, String> entry : p_216228_.entrySet()) {
                  httpurlconnection.setRequestProperty(entry.getKey(), entry.getValue());
                  if (p_216230_ != null) {
                     p_216230_.progressStagePercentage((int)(++f1 / f * 100.0F));
                  }
               }

               inputstream = httpurlconnection.getInputStream();
               f = (float)httpurlconnection.getContentLength();
               int i = httpurlconnection.getContentLength();
               if (p_216230_ != null) {
                  p_216230_.progressStage(Component.translatable("resourcepack.progress", String.format(Locale.ROOT, "%.2f", f / 1000.0F / 1000.0F)));
               }

               if (p_216226_.exists()) {
                  long j = p_216226_.length();
                  if (j == (long)i) {
                     if (p_216230_ != null) {
                        p_216230_.stop();
                     }

                     return null;
                  }

                  LOGGER.warn("Deleting {} as it does not match what we currently have ({} vs our {}).", p_216226_, i, j);
                  FileUtils.deleteQuietly(p_216226_);
               } else if (p_216226_.getParentFile() != null) {
                  p_216226_.getParentFile().mkdirs();
               }

               outputstream = new DataOutputStream(new FileOutputStream(p_216226_));
               if (p_216229_ > 0 && f > (float)p_216229_) {
                  if (p_216230_ != null) {
                     p_216230_.stop();
                  }

                  throw new IOException("Filesize is bigger than maximum allowed (file is " + f1 + ", limit is " + p_216229_ + ")");
               }

               int k;
               while((k = inputstream.read(abyte)) >= 0) {
                  f1 += (float)k;
                  if (p_216230_ != null) {
                     p_216230_.progressStagePercentage((int)(f1 / f * 100.0F));
                  }

                  if (p_216229_ > 0 && f1 > (float)p_216229_) {
                     if (p_216230_ != null) {
                        p_216230_.stop();
                     }

                     throw new IOException("Filesize was bigger than maximum allowed (got >= " + f1 + ", limit was " + p_216229_ + ")");
                  }

                  if (Thread.interrupted()) {
                     LOGGER.error("INTERRUPTED");
                     if (p_216230_ != null) {
                        p_216230_.stop();
                     }

                     return null;
                  }

                  outputstream.write(abyte, 0, k);
               }

               if (p_216230_ != null) {
                  p_216230_.stop();
                  return null;
               }
            } catch (Throwable throwable) {
               LOGGER.error("Failed to download file", throwable);
               if (httpurlconnection != null) {
                  InputStream inputstream1 = httpurlconnection.getErrorStream();

                  try {
                     LOGGER.error("HTTP response error: {}", (Object)IOUtils.toString(inputstream1, StandardCharsets.UTF_8));
                  } catch (IOException ioexception) {
                     LOGGER.error("Failed to read response from server");
                  }
               }

               if (p_216230_ != null) {
                  p_216230_.stop();
                  return null;
               }
            }

            return null;
         } finally {
            IOUtils.closeQuietly(inputstream);
            IOUtils.closeQuietly(outputstream);
         }
      }, DOWNLOAD_EXECUTOR);
   }

   public static int getAvailablePort() {
      try (ServerSocket serversocket = new ServerSocket(0)) {
         return serversocket.getLocalPort();
      } catch (IOException ioexception) {
         return 25564;
      }
   }

   public static boolean isPortAvailable(int p_259872_) {
      if (p_259872_ >= 0 && p_259872_ <= 65535) {
         try {
            boolean flag;
            try (ServerSocket serversocket = new ServerSocket(p_259872_)) {
               flag = serversocket.getLocalPort() == p_259872_;
            }

            return flag;
         } catch (IOException ioexception) {
            return false;
         }
      } else {
         return false;
      }
   }
}