package net.minecraft.world.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public interface Instruments {
   int GOAT_HORN_RANGE_BLOCKS = 256;
   int GOAT_HORN_DURATION = 140;
   ResourceKey<Instrument> PONDER_GOAT_HORN = create("ponder_goat_horn");
   ResourceKey<Instrument> SING_GOAT_HORN = create("sing_goat_horn");
   ResourceKey<Instrument> SEEK_GOAT_HORN = create("seek_goat_horn");
   ResourceKey<Instrument> FEEL_GOAT_HORN = create("feel_goat_horn");
   ResourceKey<Instrument> ADMIRE_GOAT_HORN = create("admire_goat_horn");
   ResourceKey<Instrument> CALL_GOAT_HORN = create("call_goat_horn");
   ResourceKey<Instrument> YEARN_GOAT_HORN = create("yearn_goat_horn");
   ResourceKey<Instrument> DREAM_GOAT_HORN = create("dream_goat_horn");

   private static ResourceKey<Instrument> create(String p_220151_) {
      return ResourceKey.create(Registries.INSTRUMENT, new ResourceLocation(p_220151_));
   }

   static Instrument bootstrap(Registry<Instrument> p_220149_) {
      Registry.register(p_220149_, PONDER_GOAT_HORN, new Instrument(SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(0), 140, 256.0F));
      Registry.register(p_220149_, SING_GOAT_HORN, new Instrument(SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(1), 140, 256.0F));
      Registry.register(p_220149_, SEEK_GOAT_HORN, new Instrument(SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(2), 140, 256.0F));
      Registry.register(p_220149_, FEEL_GOAT_HORN, new Instrument(SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(3), 140, 256.0F));
      Registry.register(p_220149_, ADMIRE_GOAT_HORN, new Instrument(SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(4), 140, 256.0F));
      Registry.register(p_220149_, CALL_GOAT_HORN, new Instrument(SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(5), 140, 256.0F));
      Registry.register(p_220149_, YEARN_GOAT_HORN, new Instrument(SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(6), 140, 256.0F));
      return Registry.register(p_220149_, DREAM_GOAT_HORN, new Instrument(SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(7), 140, 256.0F));
   }
}