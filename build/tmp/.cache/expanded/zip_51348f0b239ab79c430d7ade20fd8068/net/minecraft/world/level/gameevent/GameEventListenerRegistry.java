package net.minecraft.world.level.gameevent;

import net.minecraft.world.phys.Vec3;

public interface GameEventListenerRegistry {
   GameEventListenerRegistry NOOP = new GameEventListenerRegistry() {
      public boolean isEmpty() {
         return true;
      }

      public void register(GameEventListener p_251092_) {
      }

      public void unregister(GameEventListener p_251937_) {
      }

      public boolean visitInRangeListeners(GameEvent p_251260_, Vec3 p_249086_, GameEvent.Context p_249012_, GameEventListenerRegistry.ListenerVisitor p_252106_) {
         return false;
      }
   };

   boolean isEmpty();

   void register(GameEventListener p_249257_);

   void unregister(GameEventListener p_248758_);

   boolean visitInRangeListeners(GameEvent p_251001_, Vec3 p_249144_, GameEvent.Context p_249328_, GameEventListenerRegistry.ListenerVisitor p_250123_);

   @FunctionalInterface
   public interface ListenerVisitor {
      void visit(GameEventListener p_250787_, Vec3 p_251603_);
   }
}