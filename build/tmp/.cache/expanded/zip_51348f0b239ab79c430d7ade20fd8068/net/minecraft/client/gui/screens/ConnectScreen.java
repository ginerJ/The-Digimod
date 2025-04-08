package net.minecraft.client.gui.screens;

import com.mojang.logging.LogUtils;
import io.netty.channel.ChannelFuture;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.client.quickplay.QuickPlay;
import net.minecraft.client.quickplay.QuickPlayLog;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ConnectScreen extends Screen {
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   static final Logger LOGGER = LogUtils.getLogger();
   private static final long NARRATION_DELAY_MS = 2000L;
   static final Component f_290019_ = Component.translatable("connect.aborted");
   public static final Component UNKNOWN_HOST_MESSAGE = Component.translatable("disconnect.genericReason", Component.translatable("disconnect.unknownHost"));
   @Nullable
   volatile Connection connection;
   @Nullable
   ChannelFuture f_290020_;
   volatile boolean aborted;
   final Screen parent;
   private Component status = Component.translatable("connect.connecting");
   private long lastNarration = -1L;
   final Component connectFailedTitle;

   private ConnectScreen(Screen p_279215_, Component p_279228_) {
      super(GameNarrator.NO_TITLE);
      this.parent = p_279215_;
      this.connectFailedTitle = p_279228_;
   }

   public static void startConnecting(Screen p_279473_, Minecraft p_279200_, ServerAddress p_279150_, ServerData p_279481_, boolean p_279117_) {
      if (p_279200_.screen instanceof ConnectScreen) {
         LOGGER.error("Attempt to connect while already connecting");
      } else {
         ConnectScreen connectscreen = new ConnectScreen(p_279473_, p_279117_ ? QuickPlay.ERROR_TITLE : CommonComponents.CONNECT_FAILED);
         p_279200_.clearLevel();
         p_279200_.prepareForMultiplayer();
         p_279200_.updateReportEnvironment(ReportEnvironment.thirdParty(p_279481_ != null ? p_279481_.ip : p_279150_.getHost()));
         p_279200_.quickPlayLog().setWorldData(QuickPlayLog.Type.MULTIPLAYER, p_279481_.ip, p_279481_.name);
         p_279200_.setScreen(connectscreen);
         connectscreen.connect(p_279200_, p_279150_, p_279481_);
      }
   }

   private void connect(final Minecraft p_251955_, final ServerAddress p_249536_, @Nullable final ServerData p_252078_) {
      LOGGER.info("Connecting to {}, {}", p_249536_.getHost(), p_249536_.getPort());
      Thread thread = new Thread("Server Connector #" + UNIQUE_THREAD_ID.incrementAndGet()) {
         public void run() {
            InetSocketAddress inetsocketaddress = null;

            try {
               if (ConnectScreen.this.aborted) {
                  return;
               }

               Optional<InetSocketAddress> optional = ServerNameResolver.DEFAULT.resolveAddress(p_249536_).map(ResolvedServerAddress::asInetSocketAddress);
               if (ConnectScreen.this.aborted) {
                  return;
               }

               if (!optional.isPresent()) {
                  ConnectScreen.LOGGER.error("Couldn't connect to server: Unknown host \"{}\"", p_249536_.getHost());
                  net.minecraftforge.network.DualStackUtils.logInitialPreferences();
                  p_251955_.execute(() -> {
                     p_251955_.setScreen(new DisconnectedScreen(ConnectScreen.this.parent, ConnectScreen.this.connectFailedTitle, ConnectScreen.UNKNOWN_HOST_MESSAGE));
                  });
                  return;
               }

               inetsocketaddress = optional.get();
               Connection connection;
               synchronized(ConnectScreen.this) {
                  if (ConnectScreen.this.aborted) {
                     return;
                  }

                  connection = new Connection(PacketFlow.CLIENTBOUND);
                  ConnectScreen.this.f_290020_ = Connection.m_290025_(inetsocketaddress, p_251955_.options.useNativeTransport(), connection);
               }

               ConnectScreen.this.f_290020_.syncUninterruptibly();
               synchronized(ConnectScreen.this) {
                  if (ConnectScreen.this.aborted) {
                     connection.disconnect(ConnectScreen.f_290019_);
                     return;
                  }

                  ConnectScreen.this.connection = connection;
               }

               ConnectScreen.this.connection.setListener(new ClientHandshakePacketListenerImpl(ConnectScreen.this.connection, p_251955_, p_252078_, ConnectScreen.this.parent, false, (Duration)null, ConnectScreen.this::updateStatus));
               ConnectScreen.this.connection.send(new ClientIntentionPacket(inetsocketaddress.getHostName(), inetsocketaddress.getPort(), ConnectionProtocol.LOGIN));
               ConnectScreen.this.connection.send(new ServerboundHelloPacket(p_251955_.getUser().getName(), Optional.ofNullable(p_251955_.getUser().getProfileId())));
            } catch (Exception exception2) {
               if (ConnectScreen.this.aborted) {
                  return;
               }

               Throwable throwable = exception2.getCause();
               Exception exception;
               if (throwable instanceof Exception exception1) {
                  exception = exception1;
               } else {
                  exception = exception2;
               }

               ConnectScreen.LOGGER.error("Couldn't connect to server", (Throwable)exception2);
               String s = inetsocketaddress == null ? exception.getMessage() : exception.getMessage().replaceAll(inetsocketaddress.getHostName() + ":" + inetsocketaddress.getPort(), "").replaceAll(inetsocketaddress.toString(), "");
               p_251955_.execute(() -> {
                  p_251955_.setScreen(new DisconnectedScreen(ConnectScreen.this.parent, ConnectScreen.this.connectFailedTitle, Component.translatable("disconnect.genericReason", s)));
               });
            }

         }
      };
      thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      thread.start();
   }

   private void updateStatus(Component p_95718_) {
      this.status = p_95718_;
   }

   public void tick() {
      if (this.connection != null) {
         if (this.connection.isConnected()) {
            this.connection.tick();
         } else {
            this.connection.handleDisconnection();
         }
      }

   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (p_289624_) -> {
         synchronized(this) {
            this.aborted = true;
            if (this.f_290020_ != null) {
               this.f_290020_.cancel(true);
               this.f_290020_ = null;
            }

            if (this.connection != null) {
               this.connection.disconnect(f_290019_);
            }
         }

         this.minecraft.setScreen(this.parent);
      }).bounds(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20).build());
   }

   public void render(GuiGraphics p_283201_, int p_95701_, int p_95702_, float p_95703_) {
      this.renderBackground(p_283201_);
      long i = Util.getMillis();
      if (i - this.lastNarration > 2000L) {
         this.lastNarration = i;
         this.minecraft.getNarrator().sayNow(Component.translatable("narrator.joining"));
      }

      p_283201_.drawCenteredString(this.font, this.status, this.width / 2, this.height / 2 - 50, 16777215);
      super.render(p_283201_, p_95701_, p_95702_, p_95703_);
   }
}
