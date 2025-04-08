package net.minecraft.client.main;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.User;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.events.GameLoadTimesEvent;
import net.minecraft.core.UUIDUtil;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Main {
   static final Logger LOGGER = LogUtils.getLogger();

   @DontObfuscate
   public static void main(String[] p_129642_) {
      Stopwatch stopwatch = Stopwatch.createStarted(Ticker.systemTicker());
      Stopwatch stopwatch1 = Stopwatch.createStarted(Ticker.systemTicker());
      GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_TOTAL_TIME_MS, stopwatch);
      GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_PRE_WINDOW_MS, stopwatch1);
      SharedConstants.tryDetectVersion();
      SharedConstants.enableDataFixerOptimizations();
      OptionParser optionparser = new OptionParser();
      optionparser.allowsUnrecognizedOptions();
      optionparser.accepts("demo");
      optionparser.accepts("disableMultiplayer");
      optionparser.accepts("disableChat");
      optionparser.accepts("fullscreen");
      optionparser.accepts("checkGlErrors");
      OptionSpec<Void> optionspec = optionparser.accepts("jfrProfile");
      OptionSpec<String> optionspec1 = optionparser.accepts("quickPlayPath").withRequiredArg();
      OptionSpec<String> optionspec2 = optionparser.accepts("quickPlaySingleplayer").withRequiredArg();
      OptionSpec<String> optionspec3 = optionparser.accepts("quickPlayMultiplayer").withRequiredArg();
      OptionSpec<String> optionspec4 = optionparser.accepts("quickPlayRealms").withRequiredArg();
      OptionSpec<File> optionspec5 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
      OptionSpec<File> optionspec6 = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class);
      OptionSpec<File> optionspec7 = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      OptionSpec<String> optionspec8 = optionparser.accepts("proxyHost").withRequiredArg();
      OptionSpec<Integer> optionspec9 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080").ofType(Integer.class);
      OptionSpec<String> optionspec10 = optionparser.accepts("proxyUser").withRequiredArg();
      OptionSpec<String> optionspec11 = optionparser.accepts("proxyPass").withRequiredArg();
      OptionSpec<String> optionspec12 = optionparser.accepts("username").withRequiredArg().defaultsTo("Player" + Util.getMillis() % 1000L);
      OptionSpec<String> optionspec13 = optionparser.accepts("uuid").withRequiredArg();
      OptionSpec<String> optionspec14 = optionparser.accepts("xuid").withOptionalArg().defaultsTo("");
      OptionSpec<String> optionspec15 = optionparser.accepts("clientId").withOptionalArg().defaultsTo("");
      OptionSpec<String> optionspec16 = optionparser.accepts("accessToken").withRequiredArg().required();
      OptionSpec<String> optionspec17 = optionparser.accepts("version").withRequiredArg().required();
      OptionSpec<Integer> optionspec18 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
      OptionSpec<Integer> optionspec19 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
      OptionSpec<Integer> optionspec20 = optionparser.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
      OptionSpec<Integer> optionspec21 = optionparser.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
      OptionSpec<String> optionspec22 = optionparser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
      OptionSpec<String> optionspec23 = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
      OptionSpec<String> optionspec24 = optionparser.accepts("assetIndex").withRequiredArg();
      OptionSpec<String> optionspec25 = optionparser.accepts("userType").withRequiredArg().defaultsTo(User.Type.LEGACY.getName());
      OptionSpec<String> optionspec26 = optionparser.accepts("versionType").withRequiredArg().defaultsTo("release");
      OptionSpec<String> optionspec27 = optionparser.nonOptions();
      OptionSet optionset = optionparser.parse(p_129642_);
      List<String> list = optionset.valuesOf(optionspec27);
      if (!list.isEmpty()) {
         System.out.println("Completely ignored arguments: " + list);
      }

      String s = parseArgument(optionset, optionspec8);
      Proxy proxy = Proxy.NO_PROXY;
      if (s != null) {
         try {
            proxy = new Proxy(Type.SOCKS, new InetSocketAddress(s, parseArgument(optionset, optionspec9)));
         } catch (Exception exception) {
         }
      }

      final String s1 = parseArgument(optionset, optionspec10);
      final String s2 = parseArgument(optionset, optionspec11);
      if (!proxy.equals(Proxy.NO_PROXY) && stringHasValue(s1) && stringHasValue(s2)) {
         Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(s1, s2.toCharArray());
            }
         });
      }

      int i = parseArgument(optionset, optionspec18);
      int j = parseArgument(optionset, optionspec19);
      OptionalInt optionalint = ofNullable(parseArgument(optionset, optionspec20));
      OptionalInt optionalint1 = ofNullable(parseArgument(optionset, optionspec21));
      boolean flag = optionset.has("fullscreen");
      boolean flag1 = optionset.has("demo");
      boolean flag2 = optionset.has("disableMultiplayer");
      boolean flag3 = optionset.has("disableChat");
      String s3 = parseArgument(optionset, optionspec17);
      Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
      PropertyMap propertymap = GsonHelper.fromJson(gson, parseArgument(optionset, optionspec22), PropertyMap.class);
      PropertyMap propertymap1 = GsonHelper.fromJson(gson, parseArgument(optionset, optionspec23), PropertyMap.class);
      String s4 = parseArgument(optionset, optionspec26);
      File file1 = parseArgument(optionset, optionspec5);
      File file2 = optionset.has(optionspec6) ? parseArgument(optionset, optionspec6) : new File(file1, "assets/");
      File file3 = optionset.has(optionspec7) ? parseArgument(optionset, optionspec7) : new File(file1, "resourcepacks/");
      String s5 = optionset.has(optionspec13) ? optionspec13.value(optionset) : UUIDUtil.createOfflinePlayerUUID(optionspec12.value(optionset)).toString();
      String s6 = optionset.has(optionspec24) ? optionspec24.value(optionset) : null;
      String s7 = optionset.valueOf(optionspec14);
      String s8 = optionset.valueOf(optionspec15);
      String s9 = parseArgument(optionset, optionspec1);
      String s10 = parseArgument(optionset, optionspec2);
      String s11 = parseArgument(optionset, optionspec3);
      String s12 = parseArgument(optionset, optionspec4);
      if (optionset.has(optionspec)) {
         JvmProfiler.INSTANCE.start(Environment.CLIENT);
      }

      CrashReport.preload();
      GameLoadTimesEvent.INSTANCE.setBootstrapTime(Bootstrap.bootstrapDuration.get());
      net.minecraftforge.fml.loading.BackgroundWaiter.runAndTick(()->Bootstrap.bootStrap(), net.minecraftforge.fml.loading.FMLLoader.progressWindowTick);
      Bootstrap.validate();
      Util.startTimerHackThread();
      String s13 = optionspec25.value(optionset);
      User.Type user$type = User.Type.byName(s13);
      if (user$type == null) {
         LOGGER.warn("Unrecognized user type: {}", (Object)s13);
      }

      User user = new User(optionspec12.value(optionset), s5, optionspec16.value(optionset), emptyStringToEmptyOptional(s7), emptyStringToEmptyOptional(s8), user$type);
      GameConfig gameconfig = new GameConfig(new GameConfig.UserData(user, propertymap, propertymap1, proxy), new DisplayData(i, j, optionalint, optionalint1, flag), new GameConfig.FolderData(file1, file3, file2, s6), new GameConfig.GameData(flag1, s3, s4, flag2, flag3), new GameConfig.QuickPlayData(s9, s10, s11, s12));
      Thread thread = new Thread("Client Shutdown Thread") {
         public void run() {
            Minecraft minecraft1 = Minecraft.getInstance();
            if (minecraft1 != null) {
               IntegratedServer integratedserver = minecraft1.getSingleplayerServer();
               if (integratedserver != null) {
                  integratedserver.halt(true);
               }

            }
         }
      };
      thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      Runtime.getRuntime().addShutdownHook(thread);

      final Minecraft minecraft;
      try {
         Thread.currentThread().setName("Render thread");
         RenderSystem.initRenderThread();
         RenderSystem.beginInitialization();
         minecraft = new Minecraft(gameconfig);
         RenderSystem.finishInitialization();
      } catch (SilentInitException silentinitexception) {
         LOGGER.warn("Failed to create window: ", (Throwable)silentinitexception);
         return;
      } catch (Throwable throwable1) {
         CrashReport crashreport = CrashReport.forThrowable(throwable1, "Initializing game");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Initialization");
         NativeModuleLister.addCrashSection(crashreportcategory);
         Minecraft.fillReport((Minecraft)null, (LanguageManager)null, gameconfig.game.launchVersion, (Options)null, crashreport);
         Minecraft.crash(crashreport);
         return;
      }

      Thread thread1;
      if (minecraft.renderOnThread()) {
         thread1 = new Thread("Game thread") {
            public void run() {
               try {
                  RenderSystem.initGameThread(true);
                  minecraft.run();
               } catch (Throwable throwable2) {
                  Main.LOGGER.error("Exception in client thread", throwable2);
               }

            }
         };
         thread1.start();

         while(minecraft.isRunning()) {
         }
      } else {
         thread1 = null;

         try {
            RenderSystem.initGameThread(false);
            minecraft.run();
         } catch (Throwable throwable) {
            LOGGER.error("Unhandled game exception", throwable);
         }
      }

      BufferUploader.reset();

      try {
         minecraft.stop();
         if (thread1 != null) {
            thread1.join();
         }
      } catch (InterruptedException interruptedexception) {
         LOGGER.error("Exception during client thread shutdown", (Throwable)interruptedexception);
      } finally {
         minecraft.destroy();
      }

   }

   private static Optional<String> emptyStringToEmptyOptional(String p_195487_) {
      return p_195487_.isEmpty() ? Optional.empty() : Optional.of(p_195487_);
   }

   private static OptionalInt ofNullable(@Nullable Integer p_129635_) {
      return p_129635_ != null ? OptionalInt.of(p_129635_) : OptionalInt.empty();
   }

   @Nullable
   private static <T> T parseArgument(OptionSet p_129639_, OptionSpec<T> p_129640_) {
      try {
         return p_129639_.valueOf(p_129640_);
      } catch (Throwable throwable) {
         if (p_129640_ instanceof ArgumentAcceptingOptionSpec<T> argumentacceptingoptionspec) {
            List<T> list = argumentacceptingoptionspec.defaultValues();
            if (!list.isEmpty()) {
               return list.get(0);
            }
         }

         throw throwable;
      }
   }

   private static boolean stringHasValue(@Nullable String p_129637_) {
      return p_129637_ != null && !p_129637_.isEmpty();
   }

   static {
      System.setProperty("java.awt.headless", "true");
   }
}
