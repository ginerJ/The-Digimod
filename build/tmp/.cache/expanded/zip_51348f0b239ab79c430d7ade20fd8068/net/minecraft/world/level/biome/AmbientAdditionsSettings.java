package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public class AmbientAdditionsSettings {
   public static final Codec<AmbientAdditionsSettings> CODEC = RecordCodecBuilder.create((p_47382_) -> {
      return p_47382_.group(SoundEvent.CODEC.fieldOf("sound").forGetter((p_151642_) -> {
         return p_151642_.soundEvent;
      }), Codec.DOUBLE.fieldOf("tick_chance").forGetter((p_151640_) -> {
         return p_151640_.tickChance;
      })).apply(p_47382_, AmbientAdditionsSettings::new);
   });
   private final Holder<SoundEvent> soundEvent;
   private final double tickChance;

   public AmbientAdditionsSettings(Holder<SoundEvent> p_263329_, double p_263326_) {
      this.soundEvent = p_263329_;
      this.tickChance = p_263326_;
   }

   public Holder<SoundEvent> getSoundEvent() {
      return this.soundEvent;
   }

   public double getTickChance() {
      return this.tickChance;
   }
}