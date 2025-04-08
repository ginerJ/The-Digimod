package net.minecraft.network.chat;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

public class MessageSignatureCache {
   public static final int NOT_FOUND = -1;
   private static final int DEFAULT_CAPACITY = 128;
   private final MessageSignature[] entries;

   public MessageSignatureCache(int p_250894_) {
      this.entries = new MessageSignature[p_250894_];
   }

   public static MessageSignatureCache createDefault() {
      return new MessageSignatureCache(128);
   }

   public int pack(MessageSignature p_254157_) {
      for(int i = 0; i < this.entries.length; ++i) {
         if (p_254157_.equals(this.entries[i])) {
            return i;
         }
      }

      return -1;
   }

   @Nullable
   public MessageSignature unpack(int p_253967_) {
      return this.entries[p_253967_];
   }

   public void push(PlayerChatMessage p_248938_) {
      List<MessageSignature> list = p_248938_.signedBody().lastSeen().entries();
      ArrayDeque<MessageSignature> arraydeque = new ArrayDeque<>(list.size() + 1);
      arraydeque.addAll(list);
      MessageSignature messagesignature = p_248938_.signature();
      if (messagesignature != null) {
         arraydeque.add(messagesignature);
      }

      this.push(arraydeque);
   }

   @VisibleForTesting
   void push(List<MessageSignature> p_248560_) {
      this.push(new ArrayDeque<>(p_248560_));
   }

   private void push(ArrayDeque<MessageSignature> p_251419_) {
      Set<MessageSignature> set = new ObjectOpenHashSet<>(p_251419_);

      for(int i = 0; !p_251419_.isEmpty() && i < this.entries.length; ++i) {
         MessageSignature messagesignature = this.entries[i];
         this.entries[i] = p_251419_.removeLast();
         if (messagesignature != null && !set.contains(messagesignature)) {
            p_251419_.addFirst(messagesignature);
         }
      }

   }
}