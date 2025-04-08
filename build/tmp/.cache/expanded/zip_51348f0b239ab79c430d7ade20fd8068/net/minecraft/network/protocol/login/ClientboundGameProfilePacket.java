package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundGameProfilePacket implements Packet<ClientLoginPacketListener> {
   private final GameProfile gameProfile;

   public ClientboundGameProfilePacket(GameProfile p_134767_) {
      this.gameProfile = p_134767_;
   }

   public ClientboundGameProfilePacket(FriendlyByteBuf p_179814_) {
      this.gameProfile = p_179814_.readGameProfile();
   }

   public void write(FriendlyByteBuf p_134776_) {
      p_134776_.writeGameProfile(this.gameProfile);
   }

   public void handle(ClientLoginPacketListener p_134773_) {
      p_134773_.handleGameProfile(this);
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }
}