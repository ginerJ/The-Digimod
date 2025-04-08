package net.minecraft.sounds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;

public class Music {
   public static final Codec<Music> CODEC = RecordCodecBuilder.create((p_11635_) -> {
      return p_11635_.group(SoundEvent.CODEC.fieldOf("sound").forGetter((p_144041_) -> {
         return p_144041_.event;
      }), Codec.INT.fieldOf("min_delay").forGetter((p_144039_) -> {
         return p_144039_.minDelay;
      }), Codec.INT.fieldOf("max_delay").forGetter((p_144037_) -> {
         return p_144037_.maxDelay;
      }), Codec.BOOL.fieldOf("replace_current_music").forGetter((p_144035_) -> {
         return p_144035_.replaceCurrentMusic;
      })).apply(p_11635_, Music::new);
   });
   private final Holder<SoundEvent> event;
   private final int minDelay;
   private final int maxDelay;
   private final boolean replaceCurrentMusic;

   public Music(Holder<SoundEvent> p_263426_, int p_263377_, int p_263383_, boolean p_263387_) {
      this.event = p_263426_;
      this.minDelay = p_263377_;
      this.maxDelay = p_263383_;
      this.replaceCurrentMusic = p_263387_;
   }

   public Holder<SoundEvent> getEvent() {
      return this.event;
   }

   public int getMinDelay() {
      return this.minDelay;
   }

   public int getMaxDelay() {
      return this.maxDelay;
   }

   public boolean replaceCurrentMusic() {
      return this.replaceCurrentMusic;
   }
}