package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.pathfinder.Path;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

public class SetClosestHomeAsWalkTarget {
   private static final int CACHE_TIMEOUT = 40;
   private static final int BATCH_SIZE = 5;
   private static final int RATE = 20;
   private static final int OK_DISTANCE_SQR = 4;

   public static BehaviorControl<PathfinderMob> create(float p_259960_) {
      Long2LongMap long2longmap = new Long2LongOpenHashMap();
      MutableLong mutablelong = new MutableLong(0L);
      return BehaviorBuilder.create((p_258633_) -> {
         return p_258633_.group(p_258633_.absent(MemoryModuleType.WALK_TARGET), p_258633_.absent(MemoryModuleType.HOME)).apply(p_258633_, (p_258626_, p_258627_) -> {
            return (p_258638_, p_258639_, p_258640_) -> {
               if (p_258638_.getGameTime() - mutablelong.getValue() < 20L) {
                  return false;
               } else {
                  PoiManager poimanager = p_258638_.getPoiManager();
                  Optional<BlockPos> optional = poimanager.findClosest((p_217376_) -> {
                     return p_217376_.is(PoiTypes.HOME);
                  }, p_258639_.blockPosition(), 48, PoiManager.Occupancy.ANY);
                  if (!optional.isEmpty() && !(optional.get().distSqr(p_258639_.blockPosition()) <= 4.0D)) {
                     MutableInt mutableint = new MutableInt(0);
                     mutablelong.setValue(p_258638_.getGameTime() + (long)p_258638_.getRandom().nextInt(20));
                     Predicate<BlockPos> predicate = (p_258644_) -> {
                        long i = p_258644_.asLong();
                        if (long2longmap.containsKey(i)) {
                           return false;
                        } else if (mutableint.incrementAndGet() >= 5) {
                           return false;
                        } else {
                           long2longmap.put(i, mutablelong.getValue() + 40L);
                           return true;
                        }
                     };
                     Set<Pair<Holder<PoiType>, BlockPos>> set = poimanager.findAllWithType((p_217372_) -> {
                        return p_217372_.is(PoiTypes.HOME);
                     }, predicate, p_258639_.blockPosition(), 48, PoiManager.Occupancy.ANY).collect(Collectors.toSet());
                     Path path = AcquirePoi.findPathToPois(p_258639_, set);
                     if (path != null && path.canReach()) {
                        BlockPos blockpos = path.getTarget();
                        Optional<Holder<PoiType>> optional1 = poimanager.getType(blockpos);
                        if (optional1.isPresent()) {
                           p_258626_.set(new WalkTarget(blockpos, p_259960_, 1));
                           DebugPackets.sendPoiTicketCountPacket(p_258638_, blockpos);
                        }
                     } else if (mutableint.getValue() < 5) {
                        long2longmap.long2LongEntrySet().removeIf((p_258629_) -> {
                           return p_258629_.getLongValue() < mutablelong.getValue();
                        });
                     }

                     return true;
                  } else {
                     return false;
                  }
               }
            };
         });
      });
   }
}