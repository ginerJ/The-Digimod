package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class DebugPackets {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static void sendGameTestAddMarker(ServerLevel p_133683_, BlockPos p_133684_, String p_133685_, int p_133686_, int p_133687_) {
      FriendlyByteBuf friendlybytebuf = new FriendlyByteBuf(Unpooled.buffer());
      friendlybytebuf.writeBlockPos(p_133684_);
      friendlybytebuf.writeInt(p_133686_);
      friendlybytebuf.writeUtf(p_133685_);
      friendlybytebuf.writeInt(p_133687_);
      sendPacketToAllPlayers(p_133683_, friendlybytebuf, ClientboundCustomPayloadPacket.DEBUG_GAME_TEST_ADD_MARKER);
   }

   public static void sendGameTestClearPacket(ServerLevel p_133675_) {
      FriendlyByteBuf friendlybytebuf = new FriendlyByteBuf(Unpooled.buffer());
      sendPacketToAllPlayers(p_133675_, friendlybytebuf, ClientboundCustomPayloadPacket.DEBUG_GAME_TEST_CLEAR);
   }

   public static void sendPoiPacketsForChunk(ServerLevel p_133677_, ChunkPos p_133678_) {
   }

   public static void sendPoiAddedPacket(ServerLevel p_133680_, BlockPos p_133681_) {
      sendVillageSectionsPacket(p_133680_, p_133681_);
   }

   public static void sendPoiRemovedPacket(ServerLevel p_133717_, BlockPos p_133718_) {
      sendVillageSectionsPacket(p_133717_, p_133718_);
   }

   public static void sendPoiTicketCountPacket(ServerLevel p_133720_, BlockPos p_133721_) {
      sendVillageSectionsPacket(p_133720_, p_133721_);
   }

   private static void sendVillageSectionsPacket(ServerLevel p_133723_, BlockPos p_133724_) {
   }

   public static void sendPathFindingPacket(Level p_133704_, Mob p_133705_, @Nullable Path p_133706_, float p_133707_) {
   }

   public static void sendNeighborsUpdatePacket(Level p_133709_, BlockPos p_133710_) {
   }

   public static void sendStructurePacket(WorldGenLevel p_133712_, StructureStart p_133713_) {
   }

   public static void sendGoalSelector(Level p_133700_, Mob p_133701_, GoalSelector p_133702_) {
      if (p_133700_ instanceof ServerLevel) {
         ;
      }
   }

   public static void sendRaids(ServerLevel p_133689_, Collection<Raid> p_133690_) {
   }

   public static void sendEntityBrain(LivingEntity p_133696_) {
   }

   public static void sendBeeInfo(Bee p_133698_) {
   }

   public static void sendGameEventInfo(Level p_237888_, GameEvent p_237889_, Vec3 p_237890_) {
   }

   public static void sendGameEventListenerInfo(Level p_179508_, GameEventListener p_179509_) {
   }

   public static void sendHiveInfo(Level p_179511_, BlockPos p_179512_, BlockState p_179513_, BeehiveBlockEntity p_179514_) {
   }

   private static void writeBrain(LivingEntity p_179499_, FriendlyByteBuf p_179500_) {
      Brain<?> brain = p_179499_.getBrain();
      long i = p_179499_.level().getGameTime();
      if (p_179499_ instanceof InventoryCarrier) {
         Container container = ((InventoryCarrier)p_179499_).getInventory();
         p_179500_.writeUtf(container.isEmpty() ? "" : container.toString());
      } else {
         p_179500_.writeUtf("");
      }

      p_179500_.writeOptional(brain.hasMemoryValue(MemoryModuleType.PATH) ? brain.getMemory(MemoryModuleType.PATH) : Optional.empty(), (p_237912_, p_237913_) -> {
         p_237913_.writeToStream(p_237912_);
      });
      if (p_179499_ instanceof Villager villager) {
         boolean flag = villager.wantsToSpawnGolem(i);
         p_179500_.writeBoolean(flag);
      } else {
         p_179500_.writeBoolean(false);
      }

      if (p_179499_.getType() == EntityType.WARDEN) {
         Warden warden = (Warden)p_179499_;
         p_179500_.writeInt(warden.getClientAngerLevel());
      } else {
         p_179500_.writeInt(-1);
      }

      p_179500_.writeCollection(brain.getActiveActivities(), (p_237909_, p_237910_) -> {
         p_237909_.writeUtf(p_237910_.getName());
      });
      Set<String> set = brain.getRunningBehaviors().stream().map(BehaviorControl::debugString).collect(Collectors.toSet());
      p_179500_.writeCollection(set, FriendlyByteBuf::writeUtf);
      p_179500_.writeCollection(getMemoryDescriptions(p_179499_, i), (p_237915_, p_237916_) -> {
         String s = StringUtil.truncateStringIfNecessary(p_237916_, 255, true);
         p_237915_.writeUtf(s);
      });
      if (p_179499_ instanceof Villager) {
         Set<BlockPos> set1 = Stream.of(MemoryModuleType.JOB_SITE, MemoryModuleType.HOME, MemoryModuleType.MEETING_POINT).map(brain::getMemory).flatMap(Optional::stream).map(GlobalPos::pos).collect(Collectors.toSet());
         p_179500_.writeCollection(set1, FriendlyByteBuf::writeBlockPos);
      } else {
         p_179500_.writeVarInt(0);
      }

      if (p_179499_ instanceof Villager) {
         Set<BlockPos> set2 = Stream.of(MemoryModuleType.POTENTIAL_JOB_SITE).map(brain::getMemory).flatMap(Optional::stream).map(GlobalPos::pos).collect(Collectors.toSet());
         p_179500_.writeCollection(set2, FriendlyByteBuf::writeBlockPos);
      } else {
         p_179500_.writeVarInt(0);
      }

      if (p_179499_ instanceof Villager) {
         Map<UUID, Object2IntMap<GossipType>> map = ((Villager)p_179499_).getGossips().getGossipEntries();
         List<String> list = Lists.newArrayList();
         map.forEach((p_237900_, p_237901_) -> {
            String s = DebugEntityNameGenerator.getEntityName(p_237900_);
            p_237901_.forEach((p_237896_, p_237897_) -> {
               list.add(s + ": " + p_237896_ + ": " + p_237897_);
            });
         });
         p_179500_.writeCollection(list, FriendlyByteBuf::writeUtf);
      } else {
         p_179500_.writeVarInt(0);
      }

   }

   private static List<String> getMemoryDescriptions(LivingEntity p_179496_, long p_179497_) {
      Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> map = p_179496_.getBrain().getMemories();
      List<String> list = Lists.newArrayList();

      for(Map.Entry<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> entry : map.entrySet()) {
         MemoryModuleType<?> memorymoduletype = entry.getKey();
         Optional<? extends ExpirableValue<?>> optional = entry.getValue();
         String s;
         if (optional.isPresent()) {
            ExpirableValue<?> expirablevalue = optional.get();
            Object object = expirablevalue.getValue();
            if (memorymoduletype == MemoryModuleType.HEARD_BELL_TIME) {
               long i = p_179497_ - (Long)object;
               s = i + " ticks ago";
            } else if (expirablevalue.canExpire()) {
               s = getShortDescription((ServerLevel)p_179496_.level(), object) + " (ttl: " + expirablevalue.getTimeToLive() + ")";
            } else {
               s = getShortDescription((ServerLevel)p_179496_.level(), object);
            }
         } else {
            s = "-";
         }

         list.add(BuiltInRegistries.MEMORY_MODULE_TYPE.getKey(memorymoduletype).getPath() + ": " + s);
      }

      list.sort(String::compareTo);
      return list;
   }

   private static String getShortDescription(ServerLevel p_179493_, @Nullable Object p_179494_) {
      if (p_179494_ == null) {
         return "-";
      } else if (p_179494_ instanceof UUID) {
         return getShortDescription(p_179493_, p_179493_.getEntity((UUID)p_179494_));
      } else if (p_179494_ instanceof LivingEntity) {
         Entity entity1 = (Entity)p_179494_;
         return DebugEntityNameGenerator.getEntityName(entity1);
      } else if (p_179494_ instanceof Nameable) {
         return ((Nameable)p_179494_).getName().getString();
      } else if (p_179494_ instanceof WalkTarget) {
         return getShortDescription(p_179493_, ((WalkTarget)p_179494_).getTarget());
      } else if (p_179494_ instanceof EntityTracker) {
         return getShortDescription(p_179493_, ((EntityTracker)p_179494_).getEntity());
      } else if (p_179494_ instanceof GlobalPos) {
         return getShortDescription(p_179493_, ((GlobalPos)p_179494_).pos());
      } else if (p_179494_ instanceof BlockPosTracker) {
         return getShortDescription(p_179493_, ((BlockPosTracker)p_179494_).currentBlockPosition());
      } else if (p_179494_ instanceof DamageSource) {
         Entity entity = ((DamageSource)p_179494_).getEntity();
         return entity == null ? p_179494_.toString() : getShortDescription(p_179493_, entity);
      } else if (!(p_179494_ instanceof Collection)) {
         return p_179494_.toString();
      } else {
         List<String> list = Lists.newArrayList();

         for(Object object : (Iterable)p_179494_) {
            list.add(getShortDescription(p_179493_, object));
         }

         return list.toString();
      }
   }

   private static void sendPacketToAllPlayers(ServerLevel p_133692_, FriendlyByteBuf p_133693_, ResourceLocation p_133694_) {
      Packet<?> packet = new ClientboundCustomPayloadPacket(p_133694_, p_133693_);

      for(ServerPlayer serverplayer : p_133692_.players()) {
         serverplayer.connection.send(packet);
      }

   }
}