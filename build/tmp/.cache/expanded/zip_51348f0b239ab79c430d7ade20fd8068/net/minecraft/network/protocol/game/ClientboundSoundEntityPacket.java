package net.minecraft.network.protocol.game;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class ClientboundSoundEntityPacket implements Packet<ClientGamePacketListener> {
   private final Holder<SoundEvent> sound;
   private final SoundSource source;
   private final int id;
   private final float volume;
   private final float pitch;
   private final long seed;

   public ClientboundSoundEntityPacket(Holder<SoundEvent> p_263513_, SoundSource p_263511_, Entity p_263496_, float p_263519_, float p_263523_, long p_263532_) {
      this.sound = p_263513_;
      this.source = p_263511_;
      this.id = p_263496_.getId();
      this.volume = p_263519_;
      this.pitch = p_263523_;
      this.seed = p_263532_;
   }

   public ClientboundSoundEntityPacket(FriendlyByteBuf p_179419_) {
      this.sound = p_179419_.readById(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), SoundEvent::readFromNetwork);
      this.source = p_179419_.readEnum(SoundSource.class);
      this.id = p_179419_.readVarInt();
      this.volume = p_179419_.readFloat();
      this.pitch = p_179419_.readFloat();
      this.seed = p_179419_.readLong();
   }

   public void write(FriendlyByteBuf p_133428_) {
      p_133428_.writeId(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), this.sound, (p_263534_, p_263498_) -> {
         p_263498_.writeToNetwork(p_263534_);
      });
      p_133428_.writeEnum(this.source);
      p_133428_.writeVarInt(this.id);
      p_133428_.writeFloat(this.volume);
      p_133428_.writeFloat(this.pitch);
      p_133428_.writeLong(this.seed);
   }

   public Holder<SoundEvent> getSound() {
      return this.sound;
   }

   public SoundSource getSource() {
      return this.source;
   }

   public int getId() {
      return this.id;
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public long getSeed() {
      return this.seed;
   }

   public void handle(ClientGamePacketListener p_133425_) {
      p_133425_.handleSoundEntityEvent(this);
   }
}