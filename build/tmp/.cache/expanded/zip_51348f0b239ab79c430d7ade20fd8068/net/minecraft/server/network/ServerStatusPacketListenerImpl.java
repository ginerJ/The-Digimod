package net.minecraft.server.network;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerStatusPacketListener;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;

public class ServerStatusPacketListenerImpl implements ServerStatusPacketListener {
   private static final Component DISCONNECT_REASON = Component.translatable("multiplayer.status.request_handled");
   private final ServerStatus status;
   private final @org.jetbrains.annotations.Nullable String statusCache; // FORGE: cache status JSON
   private final Connection connection;
   private boolean hasRequestedStatus;

   public ServerStatusPacketListenerImpl(ServerStatus p_272864_, Connection p_273586_) {
      this(p_272864_, p_273586_, null);
   }
   public ServerStatusPacketListenerImpl(ServerStatus p_272864_, Connection p_273586_, @org.jetbrains.annotations.Nullable String statusCache) {
      this.status = p_272864_;
      this.connection = p_273586_;
      this.statusCache = statusCache;
   }

   public void onDisconnect(Component p_10091_) {
   }

   public boolean isAcceptingMessages() {
      return this.connection.isConnected();
   }

   public void handleStatusRequest(ServerboundStatusRequestPacket p_10095_) {
      if (this.hasRequestedStatus) {
         this.connection.disconnect(DISCONNECT_REASON);
      } else {
         this.hasRequestedStatus = true;
         this.connection.send(new ClientboundStatusResponsePacket(this.status, this.statusCache));
      }
   }

   public void handlePingRequest(ServerboundPingRequestPacket p_10093_) {
      this.connection.send(new ClientboundPongResponsePacket(p_10093_.getTime()));
      this.connection.disconnect(DISCONNECT_REASON);
   }
}
