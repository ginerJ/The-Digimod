package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.CombatTracker;

public class ClientboundPlayerCombatEndPacket implements Packet<ClientGamePacketListener> {
   private final int duration;

   public ClientboundPlayerCombatEndPacket(CombatTracker p_179040_) {
      this(p_179040_.getCombatDuration());
   }

   public ClientboundPlayerCombatEndPacket(int p_289544_) {
      this.duration = p_289544_;
   }

   public ClientboundPlayerCombatEndPacket(FriendlyByteBuf p_179042_) {
      this.duration = p_179042_.readVarInt();
   }

   public void write(FriendlyByteBuf p_179044_) {
      p_179044_.writeVarInt(this.duration);
   }

   public void handle(ClientGamePacketListener p_179048_) {
      p_179048_.handlePlayerCombatEnd(this);
   }
}