package net.minecraft.world.level.gameevent;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class EuclideanGameEventListenerRegistry implements GameEventListenerRegistry {
   private final List<GameEventListener> listeners = Lists.newArrayList();
   private final Set<GameEventListener> listenersToRemove = Sets.newHashSet();
   private final List<GameEventListener> listenersToAdd = Lists.newArrayList();
   private boolean processing;
   private final ServerLevel level;
   private final int sectionY;
   private final EuclideanGameEventListenerRegistry.OnEmptyAction onEmptyAction;

   public EuclideanGameEventListenerRegistry(ServerLevel p_281505_, int p_283450_, EuclideanGameEventListenerRegistry.OnEmptyAction p_282325_) {
      this.level = p_281505_;
      this.sectionY = p_283450_;
      this.onEmptyAction = p_282325_;
   }

   public boolean isEmpty() {
      return this.listeners.isEmpty();
   }

   public void register(GameEventListener p_248767_) {
      if (this.processing) {
         this.listenersToAdd.add(p_248767_);
      } else {
         this.listeners.add(p_248767_);
      }

      DebugPackets.sendGameEventListenerInfo(this.level, p_248767_);
   }

   public void unregister(GameEventListener p_250006_) {
      if (this.processing) {
         this.listenersToRemove.add(p_250006_);
      } else {
         this.listeners.remove(p_250006_);
      }

      if (this.listeners.isEmpty()) {
         this.onEmptyAction.apply(this.sectionY);
      }

   }

   public boolean visitInRangeListeners(GameEvent p_251377_, Vec3 p_251445_, GameEvent.Context p_252317_, GameEventListenerRegistry.ListenerVisitor p_251422_) {
      this.processing = true;
      boolean flag = false;

      try {
         Iterator<GameEventListener> iterator = this.listeners.iterator();

         while(iterator.hasNext()) {
            GameEventListener gameeventlistener = iterator.next();
            if (this.listenersToRemove.remove(gameeventlistener)) {
               iterator.remove();
            } else {
               Optional<Vec3> optional = getPostableListenerPosition(this.level, p_251445_, gameeventlistener);
               if (optional.isPresent()) {
                  p_251422_.visit(gameeventlistener, optional.get());
                  flag = true;
               }
            }
         }
      } finally {
         this.processing = false;
      }

      if (!this.listenersToAdd.isEmpty()) {
         this.listeners.addAll(this.listenersToAdd);
         this.listenersToAdd.clear();
      }

      if (!this.listenersToRemove.isEmpty()) {
         this.listeners.removeAll(this.listenersToRemove);
         this.listenersToRemove.clear();
      }

      return flag;
   }

   private static Optional<Vec3> getPostableListenerPosition(ServerLevel p_249585_, Vec3 p_251333_, GameEventListener p_251051_) {
      Optional<Vec3> optional = p_251051_.getListenerSource().getPosition(p_249585_);
      if (optional.isEmpty()) {
         return Optional.empty();
      } else {
         double d0 = optional.get().distanceToSqr(p_251333_);
         int i = p_251051_.getListenerRadius() * p_251051_.getListenerRadius();
         return d0 > (double)i ? Optional.empty() : optional;
      }
   }

   @FunctionalInterface
   public interface OnEmptyAction {
      void apply(int p_282867_);
   }
}