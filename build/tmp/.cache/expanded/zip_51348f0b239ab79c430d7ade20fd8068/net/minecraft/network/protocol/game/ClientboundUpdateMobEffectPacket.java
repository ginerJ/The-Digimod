package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class ClientboundUpdateMobEffectPacket implements Packet<ClientGamePacketListener> {
   private static final int FLAG_AMBIENT = 1;
   private static final int FLAG_VISIBLE = 2;
   private static final int FLAG_SHOW_ICON = 4;
   private final int entityId;
   private final MobEffect effect;
   private final byte effectAmplifier;
   private final int effectDurationTicks;
   private final byte flags;
   @Nullable
   private final MobEffectInstance.FactorData factorData;

   public ClientboundUpdateMobEffectPacket(int p_133611_, MobEffectInstance p_133612_) {
      this.entityId = p_133611_;
      this.effect = p_133612_.getEffect();
      this.effectAmplifier = (byte)(p_133612_.getAmplifier() & 255);
      this.effectDurationTicks = p_133612_.getDuration();
      byte b0 = 0;
      if (p_133612_.isAmbient()) {
         b0 = (byte)(b0 | 1);
      }

      if (p_133612_.isVisible()) {
         b0 = (byte)(b0 | 2);
      }

      if (p_133612_.showIcon()) {
         b0 = (byte)(b0 | 4);
      }

      this.flags = b0;
      this.factorData = p_133612_.getFactorData().orElse((MobEffectInstance.FactorData)null);
   }

   public ClientboundUpdateMobEffectPacket(FriendlyByteBuf p_179466_) {
      this.entityId = p_179466_.readVarInt();
      this.effect = p_179466_.readById(BuiltInRegistries.MOB_EFFECT);
      this.effectAmplifier = p_179466_.readByte();
      this.effectDurationTicks = p_179466_.readVarInt();
      this.flags = p_179466_.readByte();
      this.factorData = p_179466_.readNullable((p_266628_) -> {
         return p_266628_.readWithCodec(NbtOps.INSTANCE, MobEffectInstance.FactorData.CODEC);
      });
   }

   public void write(FriendlyByteBuf p_133621_) {
      p_133621_.writeVarInt(this.entityId);
      p_133621_.writeId(BuiltInRegistries.MOB_EFFECT, this.effect);
      p_133621_.writeByte(this.effectAmplifier);
      p_133621_.writeVarInt(this.effectDurationTicks);
      p_133621_.writeByte(this.flags);
      p_133621_.writeNullable(this.factorData, (p_266629_, p_266630_) -> {
         p_266629_.writeWithCodec(NbtOps.INSTANCE, MobEffectInstance.FactorData.CODEC, p_266630_);
      });
   }

   public void handle(ClientGamePacketListener p_133618_) {
      p_133618_.handleUpdateMobEffect(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public MobEffect getEffect() {
      return this.effect;
   }

   public byte getEffectAmplifier() {
      return this.effectAmplifier;
   }

   public int getEffectDurationTicks() {
      return this.effectDurationTicks;
   }

   public boolean isEffectVisible() {
      return (this.flags & 2) == 2;
   }

   public boolean isEffectAmbient() {
      return (this.flags & 1) == 1;
   }

   public boolean effectShowsIcon() {
      return (this.flags & 4) == 4;
   }

   @Nullable
   public MobEffectInstance.FactorData getFactorData() {
      return this.factorData;
   }
}