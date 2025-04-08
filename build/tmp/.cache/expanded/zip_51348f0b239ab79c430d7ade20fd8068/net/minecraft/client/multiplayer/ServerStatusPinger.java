package net.minecraft.client.multiplayer;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ServerStatusPinger {
   static final Splitter SPLITTER = Splitter.on('\u0000').limit(6);
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component CANT_CONNECT_MESSAGE = Component.translatable("multiplayer.status.cannot_connect").withStyle((p_265659_) -> {
      return p_265659_.withColor(-65536);
   });
   private final List<Connection> connections = Collections.synchronizedList(Lists.newArrayList());

   public void pingServer(final ServerData p_105460_, final Runnable p_105461_) throws UnknownHostException {
      ServerAddress serveraddress = ServerAddress.parseString(p_105460_.ip);
      Optional<InetSocketAddress> optional = ServerNameResolver.DEFAULT.resolveAddress(serveraddress).map(ResolvedServerAddress::asInetSocketAddress);
      if (!optional.isPresent()) {
         this.onPingFailed(ConnectScreen.UNKNOWN_HOST_MESSAGE, p_105460_);
      } else {
         final InetSocketAddress inetsocketaddress = optional.get();
         final Connection connection = Connection.connectToServer(inetsocketaddress, false);
         this.connections.add(connection);
         p_105460_.motd = Component.translatable("multiplayer.status.pinging");
         p_105460_.ping = -1L;
         p_105460_.playerList = Collections.emptyList();
         connection.setListener(new ClientStatusPacketListener() {
            private boolean success;
            private boolean receivedPing;
            private long pingStart;

            public void handleStatusResponse(ClientboundStatusResponsePacket p_105489_) {
               if (this.receivedPing) {
                  connection.disconnect(Component.translatable("multiplayer.status.unrequested"));
               } else {
                  this.receivedPing = true;
                  ServerStatus serverstatus = p_105489_.status();
                  p_105460_.motd = serverstatus.description();
                  serverstatus.version().ifPresentOrElse((p_273307_) -> {
                     p_105460_.version = Component.literal(p_273307_.name());
                     p_105460_.protocol = p_273307_.protocol();
                  }, () -> {
                     p_105460_.version = Component.translatable("multiplayer.status.old");
                     p_105460_.protocol = 0;
                  });
                  serverstatus.players().ifPresentOrElse((p_273230_) -> {
                     p_105460_.status = ServerStatusPinger.formatPlayerCount(p_273230_.online(), p_273230_.max());
                     p_105460_.players = p_273230_;
                     if (!p_273230_.sample().isEmpty()) {
                        List<Component> list = new ArrayList<>(p_273230_.sample().size());

                        for(GameProfile gameprofile : p_273230_.sample()) {
                           list.add(Component.literal(gameprofile.getName()));
                        }

                        if (p_273230_.sample().size() < p_273230_.online()) {
                           list.add(Component.translatable("multiplayer.status.and_more", p_273230_.online() - p_273230_.sample().size()));
                        }

                        p_105460_.playerList = list;
                     } else {
                        p_105460_.playerList = List.of();
                     }

                  }, () -> {
                     p_105460_.status = Component.translatable("multiplayer.status.unknown").withStyle(ChatFormatting.DARK_GRAY);
                  });
                  serverstatus.favicon().ifPresent((p_272704_) -> {
                     if (!Arrays.equals(p_272704_.iconBytes(), p_105460_.getIconBytes())) {
                        p_105460_.setIconBytes(p_272704_.iconBytes());
                        p_105461_.run();
                     }

                  });
                  net.minecraftforge.client.ForgeHooksClient.processForgeListPingData(serverstatus, p_105460_);
                  this.pingStart = Util.getMillis();
                  connection.send(new ServerboundPingRequestPacket(this.pingStart));
                  this.success = true;
               }
            }

            public void handlePongResponse(ClientboundPongResponsePacket p_105487_) {
               long i = this.pingStart;
               long j = Util.getMillis();
               p_105460_.ping = j - i;
               connection.disconnect(Component.translatable("multiplayer.status.finished"));
            }

            public void onDisconnect(Component p_105485_) {
               if (!this.success) {
                  ServerStatusPinger.this.onPingFailed(p_105485_, p_105460_);
                  ServerStatusPinger.this.pingLegacyServer(inetsocketaddress, p_105460_);
               }

            }

            public boolean isAcceptingMessages() {
               return connection.isConnected();
            }
         });

         try {
            connection.send(new ClientIntentionPacket(serveraddress.getHost(), serveraddress.getPort(), ConnectionProtocol.STATUS));
            connection.send(new ServerboundStatusRequestPacket());
         } catch (Throwable throwable) {
            LOGGER.error("Failed to ping server {}", serveraddress, throwable);
         }

      }
   }

   void onPingFailed(Component p_171815_, ServerData p_171816_) {
      LOGGER.error("Can't ping {}: {}", p_171816_.ip, p_171815_.getString());
      p_171816_.motd = CANT_CONNECT_MESSAGE;
      p_171816_.status = CommonComponents.EMPTY;
   }

   void pingLegacyServer(final InetSocketAddress p_171812_, final ServerData p_171813_) {
      (new Bootstrap()).group(Connection.NETWORK_WORKER_GROUP.get()).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel p_105498_) {
            try {
               p_105498_.config().setOption(ChannelOption.TCP_NODELAY, true);
            } catch (ChannelException channelexception) {
            }

            p_105498_.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
               public void channelActive(ChannelHandlerContext p_105506_) throws Exception {
                  super.channelActive(p_105506_);
                  ByteBuf bytebuf = Unpooled.buffer();

                  try {
                     bytebuf.writeByte(254);
                     bytebuf.writeByte(1);
                     bytebuf.writeByte(250);
                     char[] achar = "MC|PingHost".toCharArray();
                     bytebuf.writeShort(achar.length);

                     for(char c0 : achar) {
                        bytebuf.writeChar(c0);
                     }

                     bytebuf.writeShort(7 + 2 * p_171812_.getHostName().length());
                     bytebuf.writeByte(127);
                     achar = p_171812_.getHostName().toCharArray();
                     bytebuf.writeShort(achar.length);

                     for(char c1 : achar) {
                        bytebuf.writeChar(c1);
                     }

                     bytebuf.writeInt(p_171812_.getPort());
                     p_105506_.channel().writeAndFlush(bytebuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                  } finally {
                     bytebuf.release();
                  }

               }

               protected void channelRead0(ChannelHandlerContext p_105503_, ByteBuf p_105504_) {
                  short short1 = p_105504_.readUnsignedByte();
                  if (short1 == 255) {
                     String s = new String(p_105504_.readBytes(p_105504_.readShort() * 2).array(), StandardCharsets.UTF_16BE);
                     String[] astring = Iterables.toArray(ServerStatusPinger.SPLITTER.split(s), String.class);
                     if ("\u00a71".equals(astring[0])) {
                        int i = Mth.getInt(astring[1], 0);
                        String s1 = astring[2];
                        String s2 = astring[3];
                        int j = Mth.getInt(astring[4], -1);
                        int k = Mth.getInt(astring[5], -1);
                        p_171813_.protocol = -1;
                        p_171813_.version = Component.literal(s1);
                        p_171813_.motd = Component.literal(s2);
                        p_171813_.status = ServerStatusPinger.formatPlayerCount(j, k);
                        p_171813_.players = new ServerStatus.Players(k, j, List.of());
                     }
                  }

                  p_105503_.close();
               }

               public void exceptionCaught(ChannelHandlerContext p_105511_, Throwable p_105512_) {
                  p_105511_.close();
               }
            });
         }
      }).channel(NioSocketChannel.class).connect(p_171812_.getAddress(), p_171812_.getPort());
   }

   static Component formatPlayerCount(int p_105467_, int p_105468_) {
      return Component.literal(Integer.toString(p_105467_)).append(Component.literal("/").withStyle(ChatFormatting.DARK_GRAY)).append(Integer.toString(p_105468_)).withStyle(ChatFormatting.GRAY);
   }

   public void tick() {
      synchronized(this.connections) {
         Iterator<Connection> iterator = this.connections.iterator();

         while(iterator.hasNext()) {
            Connection connection = iterator.next();
            if (connection.isConnected()) {
               connection.tick();
            } else {
               iterator.remove();
               connection.handleDisconnection();
            }
         }

      }
   }

   public void removeAll() {
      synchronized(this.connections) {
         Iterator<Connection> iterator = this.connections.iterator();

         while(iterator.hasNext()) {
            Connection connection = iterator.next();
            if (connection.isConnected()) {
               iterator.remove();
               connection.disconnect(Component.translatable("multiplayer.status.cancelled"));
            }
         }

      }
   }
}
