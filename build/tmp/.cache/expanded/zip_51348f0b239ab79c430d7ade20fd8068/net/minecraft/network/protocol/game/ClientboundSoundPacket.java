package net.minecraft.network.protocol.game;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class ClientboundSoundPacket implements Packet<ClientGamePacketListener> {
   public static final float LOCATION_ACCURACY = 8.0F;
   private final Holder<SoundEvent> sound;
   private final SoundSource source;
   private final int x;
   private final int y;
   private final int z;
   private final float volume;
   private final float pitch;
   private final long seed;

   public ClientboundSoundPacket(Holder<SoundEvent> p_263366_, SoundSource p_263375_, double p_263378_, double p_263367_, double p_263394_, float p_263415_, float p_263399_, long p_263409_) {
      this.sound = p_263366_;
      this.source = p_263375_;
      this.x = (int)(p_263378_ * 8.0D);
      this.y = (int)(p_263367_ * 8.0D);
      this.z = (int)(p_263394_ * 8.0D);
      this.volume = p_263415_;
      this.pitch = p_263399_;
      this.seed = p_263409_;
   }

   public ClientboundSoundPacket(FriendlyByteBuf p_179422_) {
      this.sound = p_179422_.readById(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), SoundEvent::readFromNetwork);
      this.source = p_179422_.readEnum(SoundSource.class);
      this.x = p_179422_.readInt();
      this.y = p_179422_.readInt();
      this.z = p_179422_.readInt();
      this.volume = p_179422_.readFloat();
      this.pitch = p_179422_.readFloat();
      this.seed = p_179422_.readLong();
   }

   public void write(FriendlyByteBuf p_133457_) {
      p_133457_.writeId(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), this.sound, (p_263422_, p_263402_) -> {
         p_263402_.writeToNetwork(p_263422_);
      });
      p_133457_.writeEnum(this.source);
      p_133457_.writeInt(this.x);
      p_133457_.writeInt(this.y);
      p_133457_.writeInt(this.z);
      p_133457_.writeFloat(this.volume);
      p_133457_.writeFloat(this.pitch);
      p_133457_.writeLong(this.seed);
   }

   public Holder<SoundEvent> getSound() {
      return this.sound;
   }

   public SoundSource getSource() {
      return this.source;
   }

   public double getX() {
      return (double)((float)this.x / 8.0F);
   }

   public double getY() {
      return (double)((float)this.y / 8.0F);
   }

   public double getZ() {
      return (double)((float)this.z / 8.0F);
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

   public void handle(ClientGamePacketListener p_133454_) {
      p_133454_.handleSoundEvent(this);
   }
}