package net.minecraft.client.server;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IntegratedPlayerList extends PlayerList {
   private CompoundTag playerData;

   public IntegratedPlayerList(IntegratedServer p_251269_, LayeredRegistryAccess<RegistryLayer> p_250644_, PlayerDataStorage p_249022_) {
      super(p_251269_, p_250644_, p_249022_, 8);
      this.setViewDistance(10);
   }

   protected void save(ServerPlayer p_120011_) {
      if (this.getServer().isSingleplayerOwner(p_120011_.getGameProfile())) {
         this.playerData = p_120011_.saveWithoutId(new CompoundTag());
      }

      super.save(p_120011_);
   }

   public Component canPlayerLogin(SocketAddress p_120007_, GameProfile p_120008_) {
      return (Component)(this.getServer().isSingleplayerOwner(p_120008_) && this.getPlayerByName(p_120008_.getName()) != null ? Component.translatable("multiplayer.disconnect.name_taken") : super.canPlayerLogin(p_120007_, p_120008_));
   }

   public IntegratedServer getServer() {
      return (IntegratedServer)super.getServer();
   }

   public CompoundTag getSingleplayerData() {
      return this.playerData;
   }
}