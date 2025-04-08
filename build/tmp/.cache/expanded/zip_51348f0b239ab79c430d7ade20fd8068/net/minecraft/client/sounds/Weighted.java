package net.minecraft.client.sounds;

import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface Weighted<T> {
   int getWeight();

   T getSound(RandomSource p_235268_);

   void preloadIfRequired(SoundEngine p_120456_);
}