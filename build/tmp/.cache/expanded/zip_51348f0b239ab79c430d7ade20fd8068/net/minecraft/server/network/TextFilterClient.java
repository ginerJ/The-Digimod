package net.minecraft.server.network;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.thread.ProcessorMailbox;
import org.slf4j.Logger;

public class TextFilterClient implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
   private static final ThreadFactory THREAD_FACTORY = (p_10148_) -> {
      Thread thread = new Thread(p_10148_);
      thread.setName("Chat-Filter-Worker-" + WORKER_COUNT.getAndIncrement());
      return thread;
   };
   private static final String DEFAULT_ENDPOINT = "v1/chat";
   private final URL chatEndpoint;
   private final TextFilterClient.MessageEncoder chatEncoder;
   final URL joinEndpoint;
   final TextFilterClient.JoinOrLeaveEncoder joinEncoder;
   final URL leaveEndpoint;
   final TextFilterClient.JoinOrLeaveEncoder leaveEncoder;
   private final String authKey;
   final TextFilterClient.IgnoreStrategy chatIgnoreStrategy;
   final ExecutorService workerPool;

   private TextFilterClient(URL p_215275_, TextFilterClient.MessageEncoder p_215276_, URL p_215277_, TextFilterClient.JoinOrLeaveEncoder p_215278_, URL p_215279_, TextFilterClient.JoinOrLeaveEncoder p_215280_, String p_215281_, TextFilterClient.IgnoreStrategy p_215282_, int p_215283_) {
      this.authKey = p_215281_;
      this.chatIgnoreStrategy = p_215282_;
      this.chatEndpoint = p_215275_;
      this.chatEncoder = p_215276_;
      this.joinEndpoint = p_215277_;
      this.joinEncoder = p_215278_;
      this.leaveEndpoint = p_215279_;
      this.leaveEncoder = p_215280_;
      this.workerPool = Executors.newFixedThreadPool(p_215283_, THREAD_FACTORY);
   }

   private static URL getEndpoint(URI p_212246_, @Nullable JsonObject p_212247_, String p_212248_, String p_212249_) throws MalformedURLException {
      String s = getEndpointFromConfig(p_212247_, p_212248_, p_212249_);
      return p_212246_.resolve("/" + s).toURL();
   }

   private static String getEndpointFromConfig(@Nullable JsonObject p_215295_, String p_215296_, String p_215297_) {
      return p_215295_ != null ? GsonHelper.getAsString(p_215295_, p_215296_, p_215297_) : p_215297_;
   }

   @Nullable
   public static TextFilterClient createFromConfig(String p_143737_) {
      if (Strings.isNullOrEmpty(p_143737_)) {
         return null;
      } else {
         try {
            JsonObject jsonobject = GsonHelper.parse(p_143737_);
            URI uri = new URI(GsonHelper.getAsString(jsonobject, "apiServer"));
            String s = GsonHelper.getAsString(jsonobject, "apiKey");
            if (s.isEmpty()) {
               throw new IllegalArgumentException("Missing API key");
            } else {
               int i = GsonHelper.getAsInt(jsonobject, "ruleId", 1);
               String s1 = GsonHelper.getAsString(jsonobject, "serverId", "");
               String s2 = GsonHelper.getAsString(jsonobject, "roomId", "Java:Chat");
               int j = GsonHelper.getAsInt(jsonobject, "hashesToDrop", -1);
               int k = GsonHelper.getAsInt(jsonobject, "maxConcurrentRequests", 7);
               JsonObject jsonobject1 = GsonHelper.getAsJsonObject(jsonobject, "endpoints", (JsonObject)null);
               String s3 = getEndpointFromConfig(jsonobject1, "chat", "v1/chat");
               boolean flag = s3.equals("v1/chat");
               URL url = uri.resolve("/" + s3).toURL();
               URL url1 = getEndpoint(uri, jsonobject1, "join", "v1/join");
               URL url2 = getEndpoint(uri, jsonobject1, "leave", "v1/leave");
               TextFilterClient.JoinOrLeaveEncoder textfilterclient$joinorleaveencoder = (p_215310_) -> {
                  JsonObject jsonobject2 = new JsonObject();
                  jsonobject2.addProperty("server", s1);
                  jsonobject2.addProperty("room", s2);
                  jsonobject2.addProperty("user_id", p_215310_.getId().toString());
                  jsonobject2.addProperty("user_display_name", p_215310_.getName());
                  return jsonobject2;
               };
               TextFilterClient.MessageEncoder textfilterclient$messageencoder;
               if (flag) {
                  textfilterclient$messageencoder = (p_238214_, p_238215_) -> {
                     JsonObject jsonobject2 = new JsonObject();
                     jsonobject2.addProperty("rule", i);
                     jsonobject2.addProperty("server", s1);
                     jsonobject2.addProperty("room", s2);
                     jsonobject2.addProperty("player", p_238214_.getId().toString());
                     jsonobject2.addProperty("player_display_name", p_238214_.getName());
                     jsonobject2.addProperty("text", p_238215_);
                     jsonobject2.addProperty("language", "*");
                     return jsonobject2;
                  };
               } else {
                  String s4 = String.valueOf(i);
                  textfilterclient$messageencoder = (p_238220_, p_238221_) -> {
                     JsonObject jsonobject2 = new JsonObject();
                     jsonobject2.addProperty("rule_id", s4);
                     jsonobject2.addProperty("category", s1);
                     jsonobject2.addProperty("subcategory", s2);
                     jsonobject2.addProperty("user_id", p_238220_.getId().toString());
                     jsonobject2.addProperty("user_display_name", p_238220_.getName());
                     jsonobject2.addProperty("text", p_238221_);
                     jsonobject2.addProperty("language", "*");
                     return jsonobject2;
                  };
               }

               TextFilterClient.IgnoreStrategy textfilterclient$ignorestrategy = TextFilterClient.IgnoreStrategy.select(j);
               String s5 = Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.US_ASCII));
               return new TextFilterClient(url, textfilterclient$messageencoder, url1, textfilterclient$joinorleaveencoder, url2, textfilterclient$joinorleaveencoder, s5, textfilterclient$ignorestrategy, k);
            }
         } catch (Exception exception) {
            LOGGER.warn("Failed to parse chat filter config {}", p_143737_, exception);
            return null;
         }
      }
   }

   void processJoinOrLeave(GameProfile p_215303_, URL p_215304_, TextFilterClient.JoinOrLeaveEncoder p_215305_, Executor p_215306_) {
      p_215306_.execute(() -> {
         JsonObject jsonobject = p_215305_.encode(p_215303_);

         try {
            this.processRequest(jsonobject, p_215304_);
         } catch (Exception exception) {
            LOGGER.warn("Failed to send join/leave packet to {} for player {}", p_215304_, p_215303_, exception);
         }

      });
   }

   CompletableFuture<FilteredText> requestMessageProcessing(GameProfile p_10137_, String p_10138_, TextFilterClient.IgnoreStrategy p_10139_, Executor p_10140_) {
      return p_10138_.isEmpty() ? CompletableFuture.completedFuture(FilteredText.EMPTY) : CompletableFuture.supplyAsync(() -> {
         JsonObject jsonobject = this.chatEncoder.encode(p_10137_, p_10138_);

         try {
            JsonObject jsonobject1 = this.processRequestResponse(jsonobject, this.chatEndpoint);
            boolean flag = GsonHelper.getAsBoolean(jsonobject1, "response", false);
            if (flag) {
               return FilteredText.passThrough(p_10138_);
            } else {
               String s = GsonHelper.getAsString(jsonobject1, "hashed", (String)null);
               if (s == null) {
                  return FilteredText.fullyFiltered(p_10138_);
               } else {
                  JsonArray jsonarray = GsonHelper.getAsJsonArray(jsonobject1, "hashes");
                  FilterMask filtermask = this.parseMask(p_10138_, jsonarray, p_10139_);
                  return new FilteredText(p_10138_, filtermask);
               }
            }
         } catch (Exception exception) {
            LOGGER.warn("Failed to validate message '{}'", p_10138_, exception);
            return FilteredText.fullyFiltered(p_10138_);
         }
      }, p_10140_);
   }

   private FilterMask parseMask(String p_243283_, JsonArray p_243222_, TextFilterClient.IgnoreStrategy p_243237_) {
      if (p_243222_.isEmpty()) {
         return FilterMask.PASS_THROUGH;
      } else if (p_243237_.shouldIgnore(p_243283_, p_243222_.size())) {
         return FilterMask.FULLY_FILTERED;
      } else {
         FilterMask filtermask = new FilterMask(p_243283_.length());

         for(int i = 0; i < p_243222_.size(); ++i) {
            filtermask.setFiltered(p_243222_.get(i).getAsInt());
         }

         return filtermask;
      }
   }

   public void close() {
      this.workerPool.shutdownNow();
   }

   private void drainStream(InputStream p_10146_) throws IOException {
      byte[] abyte = new byte[1024];

      while(p_10146_.read(abyte) != -1) {
      }

   }

   private JsonObject processRequestResponse(JsonObject p_10128_, URL p_10129_) throws IOException {
      HttpURLConnection httpurlconnection = this.makeRequest(p_10128_, p_10129_);

      try (InputStream inputstream = httpurlconnection.getInputStream()) {
         if (httpurlconnection.getResponseCode() == 204) {
            return new JsonObject();
         } else {
            try {
               return Streams.parse(new JsonReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))).getAsJsonObject();
            } finally {
               this.drainStream(inputstream);
            }
         }
      }
   }

   private void processRequest(JsonObject p_10152_, URL p_10153_) throws IOException {
      HttpURLConnection httpurlconnection = this.makeRequest(p_10152_, p_10153_);

      try (InputStream inputstream = httpurlconnection.getInputStream()) {
         this.drainStream(inputstream);
      }

   }

   private HttpURLConnection makeRequest(JsonObject p_10157_, URL p_10158_) throws IOException {
      HttpURLConnection httpurlconnection = (HttpURLConnection)p_10158_.openConnection();
      httpurlconnection.setConnectTimeout(15000);
      httpurlconnection.setReadTimeout(2000);
      httpurlconnection.setUseCaches(false);
      httpurlconnection.setDoOutput(true);
      httpurlconnection.setDoInput(true);
      httpurlconnection.setRequestMethod("POST");
      httpurlconnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      httpurlconnection.setRequestProperty("Accept", "application/json");
      httpurlconnection.setRequestProperty("Authorization", "Basic " + this.authKey);
      httpurlconnection.setRequestProperty("User-Agent", "Minecraft server" + SharedConstants.getCurrentVersion().getName());
      OutputStreamWriter outputstreamwriter = new OutputStreamWriter(httpurlconnection.getOutputStream(), StandardCharsets.UTF_8);

      try (JsonWriter jsonwriter = new JsonWriter(outputstreamwriter)) {
         Streams.write(p_10157_, jsonwriter);
      } catch (Throwable throwable1) {
         try {
            outputstreamwriter.close();
         } catch (Throwable throwable) {
            throwable1.addSuppressed(throwable);
         }

         throw throwable1;
      }

      outputstreamwriter.close();
      int i = httpurlconnection.getResponseCode();
      if (i >= 200 && i < 300) {
         return httpurlconnection;
      } else {
         throw new TextFilterClient.RequestFailedException(i + " " + httpurlconnection.getResponseMessage());
      }
   }

   public TextFilter createContext(GameProfile p_10135_) {
      return new TextFilterClient.PlayerContext(p_10135_);
   }

   @FunctionalInterface
   public interface IgnoreStrategy {
      TextFilterClient.IgnoreStrategy NEVER_IGNORE = (p_10169_, p_10170_) -> {
         return false;
      };
      TextFilterClient.IgnoreStrategy IGNORE_FULLY_FILTERED = (p_10166_, p_10167_) -> {
         return p_10166_.length() == p_10167_;
      };

      static TextFilterClient.IgnoreStrategy ignoreOverThreshold(int p_143739_) {
         return (p_143742_, p_143743_) -> {
            return p_143743_ >= p_143739_;
         };
      }

      static TextFilterClient.IgnoreStrategy select(int p_143745_) {
         TextFilterClient.IgnoreStrategy textfilterclient$ignorestrategy;
         switch (p_143745_) {
            case -1:
               textfilterclient$ignorestrategy = NEVER_IGNORE;
               break;
            case 0:
               textfilterclient$ignorestrategy = IGNORE_FULLY_FILTERED;
               break;
            default:
               textfilterclient$ignorestrategy = ignoreOverThreshold(p_143745_);
         }

         return textfilterclient$ignorestrategy;
      }

      boolean shouldIgnore(String p_10172_, int p_10173_);
   }

   @FunctionalInterface
   interface JoinOrLeaveEncoder {
      JsonObject encode(GameProfile p_215318_);
   }

   @FunctionalInterface
   interface MessageEncoder {
      JsonObject encode(GameProfile p_215320_, String p_215321_);
   }

   class PlayerContext implements TextFilter {
      private final GameProfile profile;
      private final Executor streamExecutor;

      PlayerContext(GameProfile p_10179_) {
         this.profile = p_10179_;
         ProcessorMailbox<Runnable> processormailbox = ProcessorMailbox.create(TextFilterClient.this.workerPool, "chat stream for " + p_10179_.getName());
         this.streamExecutor = processormailbox::tell;
      }

      public void join() {
         TextFilterClient.this.processJoinOrLeave(this.profile, TextFilterClient.this.joinEndpoint, TextFilterClient.this.joinEncoder, this.streamExecutor);
      }

      public void leave() {
         TextFilterClient.this.processJoinOrLeave(this.profile, TextFilterClient.this.leaveEndpoint, TextFilterClient.this.leaveEncoder, this.streamExecutor);
      }

      public CompletableFuture<List<FilteredText>> processMessageBundle(List<String> p_10190_) {
         List<CompletableFuture<FilteredText>> list = p_10190_.stream().map((p_10195_) -> {
            return TextFilterClient.this.requestMessageProcessing(this.profile, p_10195_, TextFilterClient.this.chatIgnoreStrategy, this.streamExecutor);
         }).collect(ImmutableList.toImmutableList());
         return Util.sequenceFailFast(list).exceptionally((p_143747_) -> {
            return ImmutableList.of();
         });
      }

      public CompletableFuture<FilteredText> processStreamMessage(String p_10186_) {
         return TextFilterClient.this.requestMessageProcessing(this.profile, p_10186_, TextFilterClient.this.chatIgnoreStrategy, this.streamExecutor);
      }
   }

   public static class RequestFailedException extends RuntimeException {
      RequestFailedException(String p_10199_) {
         super(p_10199_);
      }
   }
}