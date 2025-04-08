package net.minecraft.network;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.Packet;

public interface PacketSendListener {
   static PacketSendListener thenRun(final Runnable p_243267_) {
      return new PacketSendListener() {
         public void onSuccess() {
            p_243267_.run();
         }

         @Nullable
         public Packet<?> onFailure() {
            p_243267_.run();
            return null;
         }
      };
   }

   static PacketSendListener exceptionallySend(final Supplier<Packet<?>> p_243289_) {
      return new PacketSendListener() {
         @Nullable
         public Packet<?> onFailure() {
            return p_243289_.get();
         }
      };
   }

   default void onSuccess() {
   }

   @Nullable
   default Packet<?> onFailure() {
      return null;
   }
}