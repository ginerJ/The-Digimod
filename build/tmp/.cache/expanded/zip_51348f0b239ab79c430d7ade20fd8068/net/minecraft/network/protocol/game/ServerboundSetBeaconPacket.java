package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffect;

public class ServerboundSetBeaconPacket implements Packet<ServerGamePacketListener> {
   private final Optional<MobEffect> primary;
   private final Optional<MobEffect> secondary;

   public ServerboundSetBeaconPacket(Optional<MobEffect> p_237989_, Optional<MobEffect> p_237990_) {
      this.primary = p_237989_;
      this.secondary = p_237990_;
   }

   public ServerboundSetBeaconPacket(FriendlyByteBuf p_179749_) {
      this.primary = p_179749_.readOptional((p_258214_) -> {
         return p_258214_.readById(BuiltInRegistries.MOB_EFFECT);
      });
      this.secondary = p_179749_.readOptional((p_258215_) -> {
         return p_258215_.readById(BuiltInRegistries.MOB_EFFECT);
      });
   }

   public void write(FriendlyByteBuf p_134486_) {
      p_134486_.writeOptional(this.primary, (p_258216_, p_258217_) -> {
         p_258216_.writeId(BuiltInRegistries.MOB_EFFECT, p_258217_);
      });
      p_134486_.writeOptional(this.secondary, (p_258218_, p_258219_) -> {
         p_258218_.writeId(BuiltInRegistries.MOB_EFFECT, p_258219_);
      });
   }

   public void handle(ServerGamePacketListener p_134483_) {
      p_134483_.handleSetBeaconPacket(this);
   }

   public Optional<MobEffect> getPrimary() {
      return this.primary;
   }

   public Optional<MobEffect> getSecondary() {
      return this.secondary;
   }
}