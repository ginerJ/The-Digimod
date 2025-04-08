package net.minecraft.network.chat;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface ChatDecorator {
   ChatDecorator PLAIN = (p_236950_, p_236951_) -> {
      return CompletableFuture.completedFuture(p_236951_);
   };

   CompletableFuture<Component> decorate(@Nullable ServerPlayer p_236962_, Component p_236963_);
}