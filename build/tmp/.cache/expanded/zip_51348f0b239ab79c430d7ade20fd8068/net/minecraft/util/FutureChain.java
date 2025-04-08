package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import org.slf4j.Logger;

public class FutureChain implements TaskChainer, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private CompletableFuture<?> head = CompletableFuture.completedFuture((Object)null);
   private final Executor checkedExecutor;
   private volatile boolean closed;

   public FutureChain(Executor p_242395_) {
      this.checkedExecutor = (p_248283_) -> {
         if (!this.closed) {
            p_242395_.execute(p_248283_);
         }

      };
   }

   public void append(TaskChainer.DelayedTask p_242381_) {
      this.head = this.head.thenComposeAsync((p_248281_) -> {
         return p_242381_.submit(this.checkedExecutor);
      }, this.checkedExecutor).exceptionally((p_242215_) -> {
         if (p_242215_ instanceof CompletionException completionexception) {
            p_242215_ = completionexception.getCause();
         }

         if (p_242215_ instanceof CancellationException cancellationexception) {
            throw cancellationexception;
         } else {
            LOGGER.error("Chain link failed, continuing to next one", p_242215_);
            return null;
         }
      });
   }

   public void close() {
      this.closed = true;
   }
}