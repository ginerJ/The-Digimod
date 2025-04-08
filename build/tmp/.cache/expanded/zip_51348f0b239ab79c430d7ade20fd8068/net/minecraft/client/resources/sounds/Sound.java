package net.minecraft.client.resources.sounds;

import javax.annotation.Nullable;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.SampledFloat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Sound implements Weighted<Sound> {
   public static final FileToIdConverter SOUND_LISTER = new FileToIdConverter("sounds", ".ogg");
   private final ResourceLocation location;
   private final SampledFloat volume;
   private final SampledFloat pitch;
   private final int weight;
   private final Sound.Type type;
   private final boolean stream;
   private final boolean preload;
   private final int attenuationDistance;

   public Sound(String p_235134_, SampledFloat p_235135_, SampledFloat p_235136_, int p_235137_, Sound.Type p_235138_, boolean p_235139_, boolean p_235140_, int p_235141_) {
      this.location = new ResourceLocation(p_235134_);
      this.volume = p_235135_;
      this.pitch = p_235136_;
      this.weight = p_235137_;
      this.type = p_235138_;
      this.stream = p_235139_;
      this.preload = p_235140_;
      this.attenuationDistance = p_235141_;
   }

   public ResourceLocation getLocation() {
      return this.location;
   }

   public ResourceLocation getPath() {
      return SOUND_LISTER.idToFile(this.location);
   }

   public SampledFloat getVolume() {
      return this.volume;
   }

   public SampledFloat getPitch() {
      return this.pitch;
   }

   public int getWeight() {
      return this.weight;
   }

   public Sound getSound(RandomSource p_235143_) {
      return this;
   }

   public void preloadIfRequired(SoundEngine p_119789_) {
      if (this.preload) {
         p_119789_.requestPreload(this);
      }

   }

   public Sound.Type getType() {
      return this.type;
   }

   public boolean shouldStream() {
      return this.stream;
   }

   public boolean shouldPreload() {
      return this.preload;
   }

   public int getAttenuationDistance() {
      return this.attenuationDistance;
   }

   public String toString() {
      return "Sound[" + this.location + "]";
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      FILE("file"),
      SOUND_EVENT("event");

      private final String name;

      private Type(String p_119809_) {
         this.name = p_119809_;
      }

      @Nullable
      public static Sound.Type getByName(String p_119811_) {
         for(Sound.Type sound$type : values()) {
            if (sound$type.name.equals(p_119811_)) {
               return sound$type;
            }
         }

         return null;
      }
   }
}