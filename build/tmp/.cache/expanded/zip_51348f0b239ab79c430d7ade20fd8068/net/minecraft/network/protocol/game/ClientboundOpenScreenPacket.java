package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.inventory.MenuType;

public class ClientboundOpenScreenPacket implements Packet<ClientGamePacketListener> {
   private final int containerId;
   private final MenuType<?> type;
   private final Component title;

   public ClientboundOpenScreenPacket(int p_132616_, MenuType<?> p_132617_, Component p_132618_) {
      this.containerId = p_132616_;
      this.type = p_132617_;
      this.title = p_132618_;
   }

   public ClientboundOpenScreenPacket(FriendlyByteBuf p_179011_) {
      this.containerId = p_179011_.readVarInt();
      this.type = p_179011_.readById(BuiltInRegistries.MENU);
      this.title = p_179011_.readComponent();
   }

   public void write(FriendlyByteBuf p_132627_) {
      p_132627_.writeVarInt(this.containerId);
      p_132627_.writeId(BuiltInRegistries.MENU, this.type);
      p_132627_.writeComponent(this.title);
   }

   public void handle(ClientGamePacketListener p_132624_) {
      p_132624_.handleOpenScreen(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   @Nullable
   public MenuType<?> getType() {
      return this.type;
   }

   public Component getTitle() {
      return this.title;
   }
}