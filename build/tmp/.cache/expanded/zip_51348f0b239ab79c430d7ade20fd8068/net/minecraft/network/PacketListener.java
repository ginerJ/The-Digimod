package net.minecraft.network;

import net.minecraft.network.chat.Component;

public interface PacketListener {
   void onDisconnect(Component p_130552_);

   boolean isAcceptingMessages();

   default boolean shouldPropagateHandlingExceptions() {
      return true;
   }
}