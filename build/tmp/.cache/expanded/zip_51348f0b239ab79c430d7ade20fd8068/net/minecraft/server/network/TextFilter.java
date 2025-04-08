package net.minecraft.server.network;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TextFilter {
   TextFilter DUMMY = new TextFilter() {
      public void join() {
      }

      public void leave() {
      }

      public CompletableFuture<FilteredText> processStreamMessage(String p_143708_) {
         return CompletableFuture.completedFuture(FilteredText.passThrough(p_143708_));
      }

      public CompletableFuture<List<FilteredText>> processMessageBundle(List<String> p_143710_) {
         return CompletableFuture.completedFuture(p_143710_.stream().map(FilteredText::passThrough).collect(ImmutableList.toImmutableList()));
      }
   };

   void join();

   void leave();

   CompletableFuture<FilteredText> processStreamMessage(String p_10096_);

   CompletableFuture<List<FilteredText>> processMessageBundle(List<String> p_10097_);
}