package net.minecraft.world.entity.ai.behavior;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ValidateNearbyPoi {
   private static final int MAX_DISTANCE = 16;

   public static BehaviorControl<LivingEntity> create(Predicate<Holder<PoiType>> p_259460_, MemoryModuleType<GlobalPos> p_259635_) {
      return BehaviorBuilder.create((p_259215_) -> {
         return p_259215_.group(p_259215_.present(p_259635_)).apply(p_259215_, (p_259498_) -> {
            return (p_259843_, p_259259_, p_260036_) -> {
               GlobalPos globalpos = p_259215_.get(p_259498_);
               BlockPos blockpos = globalpos.pos();
               if (p_259843_.dimension() == globalpos.dimension() && blockpos.closerToCenterThan(p_259259_.position(), 16.0D)) {
                  ServerLevel serverlevel = p_259843_.getServer().getLevel(globalpos.dimension());
                  if (serverlevel != null && serverlevel.getPoiManager().exists(blockpos, p_259460_)) {
                     if (bedIsOccupied(serverlevel, blockpos, p_259259_)) {
                        p_259498_.erase();
                        p_259843_.getPoiManager().release(blockpos);
                        DebugPackets.sendPoiTicketCountPacket(p_259843_, blockpos);
                     }
                  } else {
                     p_259498_.erase();
                  }

                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }

   private static boolean bedIsOccupied(ServerLevel p_24531_, BlockPos p_24532_, LivingEntity p_24533_) {
      BlockState blockstate = p_24531_.getBlockState(p_24532_);
      return blockstate.is(BlockTags.BEDS) && blockstate.getValue(BedBlock.OCCUPIED) && !p_24533_.isSleeping();
   }
}