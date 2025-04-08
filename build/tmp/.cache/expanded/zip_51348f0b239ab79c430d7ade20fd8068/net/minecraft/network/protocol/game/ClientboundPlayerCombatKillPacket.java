package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundPlayerCombatKillPacket implements Packet<ClientGamePacketListener> {
   private final int playerId;
   private final Component message;

   public ClientboundPlayerCombatKillPacket(int p_289572_, Component p_289546_) {
      this.playerId = p_289572_;
      this.message = p_289546_;
   }

   public ClientboundPlayerCombatKillPacket(FriendlyByteBuf p_179069_) {
      this.playerId = p_179069_.readVarInt();
      this.message = p_179069_.readComponent();
   }

   public void write(FriendlyByteBuf p_179072_) {
      p_179072_.writeVarInt(this.playerId);
      p_179072_.writeComponent(this.message);
   }

   public void handle(ClientGamePacketListener p_179076_) {
      p_179076_.handlePlayerCombatKill(this);
   }

   public boolean isSkippable() {
      return true;
   }

   public int getPlayerId() {
      return this.playerId;
   }

   public Component getMessage() {
      return this.message;
   }
}