package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;

public record Instrument(Holder<SoundEvent> soundEvent, int useDuration, float range) {
   public static final Codec<Instrument> CODEC = RecordCodecBuilder.create((p_220089_) -> {
      return p_220089_.group(SoundEvent.CODEC.fieldOf("sound_event").forGetter(Instrument::soundEvent), ExtraCodecs.POSITIVE_INT.fieldOf("use_duration").forGetter(Instrument::useDuration), ExtraCodecs.POSITIVE_FLOAT.fieldOf("range").forGetter(Instrument::range)).apply(p_220089_, Instrument::new);
   });
}