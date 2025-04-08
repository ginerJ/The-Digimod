package net.minecraft.server.network;

import net.minecraft.SharedConstants;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;

public class ServerHandshakePacketListenerImpl implements ServerHandshakePacketListener {
   private static final Component IGNORE_STATUS_REASON = Component.translatable("disconnect.ignoring_status_request");
   private final MinecraftServer server;
   private final Connection connection;

   public ServerHandshakePacketListenerImpl(MinecraftServer p_9969_, Connection p_9970_) {
      this.server = p_9969_;
      this.connection = p_9970_;
   }

   public void handleIntention(ClientIntentionPacket p_9975_) {
      if (!net.minecraftforge.server.ServerLifecycleHooks.handleServerLogin(p_9975_, this.connection)) return;
      switch (p_9975_.getIntention()) {
         case LOGIN:
            this.connection.setProtocol(ConnectionProtocol.LOGIN);
            if (p_9975_.getProtocolVersion() != SharedConstants.getCurrentVersion().getProtocolVersion()) {
               Component component;
               if (p_9975_.getProtocolVersion() < 754) {
                  component = Component.translatable("multiplayer.disconnect.outdated_client", SharedConstants.getCurrentVersion().getName());
               } else {
                  component = Component.translatable("multiplayer.disconnect.incompatible", SharedConstants.getCurrentVersion().getName());
               }

               this.connection.send(new ClientboundLoginDisconnectPacket(component));
               this.connection.disconnect(component);
            } else {
               this.connection.setListener(new ServerLoginPacketListenerImpl(this.server, this.connection));
            }
            break;
         case STATUS:
            ServerStatus serverstatus = this.server.getStatus();
            if (this.server.repliesToStatus() && serverstatus != null) {
               this.connection.setProtocol(ConnectionProtocol.STATUS);
               this.connection.setListener(new ServerStatusPacketListenerImpl(serverstatus, this.connection, this.server.getStatusJson()));
            } else {
               this.connection.disconnect(IGNORE_STATUS_REASON);
            }
            break;
         default:
            throw new UnsupportedOperationException("Invalid intention " + p_9975_.getIntention());
      }

   }

   public void onDisconnect(Component p_9973_) {
   }

   public boolean isAcceptingMessages() {
      return this.connection.isConnected();
   }
}
