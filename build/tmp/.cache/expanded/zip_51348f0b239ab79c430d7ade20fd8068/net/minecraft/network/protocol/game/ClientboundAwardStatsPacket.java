package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;

public class ClientboundAwardStatsPacket implements Packet<ClientGamePacketListener> {
   private final Object2IntMap<Stat<?>> stats;

   public ClientboundAwardStatsPacket(Object2IntMap<Stat<?>> p_131631_) {
      this.stats = p_131631_;
   }

   public ClientboundAwardStatsPacket(FriendlyByteBuf p_178592_) {
      this.stats = p_178592_.readMap(Object2IntOpenHashMap::new, (p_258209_) -> {
         StatType<?> stattype = p_258209_.readById(BuiltInRegistries.STAT_TYPE);
         return readStatCap(p_178592_, stattype);
      }, FriendlyByteBuf::readVarInt);
   }

   private static <T> Stat<T> readStatCap(FriendlyByteBuf p_237573_, StatType<T> p_237574_) {
      return p_237574_.get(p_237573_.readById(p_237574_.getRegistry()));
   }

   public void handle(ClientGamePacketListener p_131642_) {
      p_131642_.handleAwardStats(this);
   }

   public void write(FriendlyByteBuf p_131645_) {
      p_131645_.writeMap(this.stats, ClientboundAwardStatsPacket::writeStatCap, FriendlyByteBuf::writeVarInt);
   }

   private static <T> void writeStatCap(FriendlyByteBuf p_237570_, Stat<T> p_237571_) {
      p_237570_.writeId(BuiltInRegistries.STAT_TYPE, p_237571_.getType());
      p_237570_.writeId(p_237571_.getType().getRegistry(), p_237571_.getValue());
   }

   public Map<Stat<?>, Integer> getStats() {
      return this.stats;
   }
}