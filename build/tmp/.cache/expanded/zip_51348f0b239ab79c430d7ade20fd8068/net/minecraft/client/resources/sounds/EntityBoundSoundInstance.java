package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityBoundSoundInstance extends AbstractTickableSoundInstance {
   private final Entity entity;

   public EntityBoundSoundInstance(SoundEvent p_235080_, SoundSource p_235081_, float p_235082_, float p_235083_, Entity p_235084_, long p_235085_) {
      super(p_235080_, p_235081_, RandomSource.create(p_235085_));
      this.volume = p_235082_;
      this.pitch = p_235083_;
      this.entity = p_235084_;
      this.x = (double)((float)this.entity.getX());
      this.y = (double)((float)this.entity.getY());
      this.z = (double)((float)this.entity.getZ());
   }

   public boolean canPlaySound() {
      return !this.entity.isSilent();
   }

   public void tick() {
      if (this.entity.isRemoved()) {
         this.stop();
      } else {
         this.x = (double)((float)this.entity.getX());
         this.y = (double)((float)this.entity.getY());
         this.z = (double)((float)this.entity.getZ());
      }
   }
}