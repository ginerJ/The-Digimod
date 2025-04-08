package net.minecraft.world.level.gameevent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;

public class GameEventDispatcher {
   private final ServerLevel level;

   public GameEventDispatcher(ServerLevel p_251921_) {
      this.level = p_251921_;
   }

   public void post(GameEvent p_251754_, Vec3 p_250613_, GameEvent.Context p_251777_) {
      int i = p_251754_.getNotificationRadius();
      BlockPos blockpos = BlockPos.containing(p_250613_);
      int j = SectionPos.blockToSectionCoord(blockpos.getX() - i);
      int k = SectionPos.blockToSectionCoord(blockpos.getY() - i);
      int l = SectionPos.blockToSectionCoord(blockpos.getZ() - i);
      int i1 = SectionPos.blockToSectionCoord(blockpos.getX() + i);
      int j1 = SectionPos.blockToSectionCoord(blockpos.getY() + i);
      int k1 = SectionPos.blockToSectionCoord(blockpos.getZ() + i);
      List<GameEvent.ListenerInfo> list = new ArrayList<>();
      GameEventListenerRegistry.ListenerVisitor gameeventlistenerregistry$listenervisitor = (p_251272_, p_248685_) -> {
         if (p_251272_.getDeliveryMode() == GameEventListener.DeliveryMode.BY_DISTANCE) {
            list.add(new GameEvent.ListenerInfo(p_251754_, p_250613_, p_251777_, p_251272_, p_248685_));
         } else {
            p_251272_.handleGameEvent(this.level, p_251754_, p_251777_, p_250613_);
         }

      };
      boolean flag = false;

      for(int l1 = j; l1 <= i1; ++l1) {
         for(int i2 = l; i2 <= k1; ++i2) {
            ChunkAccess chunkaccess = this.level.getChunkSource().getChunkNow(l1, i2);
            if (chunkaccess != null) {
               for(int j2 = k; j2 <= j1; ++j2) {
                  flag |= chunkaccess.getListenerRegistry(j2).visitInRangeListeners(p_251754_, p_250613_, p_251777_, gameeventlistenerregistry$listenervisitor);
               }
            }
         }
      }

      if (!list.isEmpty()) {
         this.handleGameEventMessagesInQueue(list);
      }

      if (flag) {
         DebugPackets.sendGameEventInfo(this.level, p_251754_, p_250613_);
      }

   }

   private void handleGameEventMessagesInQueue(List<GameEvent.ListenerInfo> p_251433_) {
      Collections.sort(p_251433_);

      for(GameEvent.ListenerInfo gameevent$listenerinfo : p_251433_) {
         GameEventListener gameeventlistener = gameevent$listenerinfo.recipient();
         gameeventlistener.handleGameEvent(this.level, gameevent$listenerinfo.gameEvent(), gameevent$listenerinfo.context(), gameevent$listenerinfo.source());
      }

   }
}