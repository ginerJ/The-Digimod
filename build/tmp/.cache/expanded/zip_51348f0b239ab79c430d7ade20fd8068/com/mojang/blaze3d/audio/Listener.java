package com.mojang.blaze3d.audio;

import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

@OnlyIn(Dist.CLIENT)
public class Listener {
   private float gain = 1.0F;
   private Vec3 position = Vec3.ZERO;

   public void setListenerPosition(Vec3 p_83740_) {
      this.position = p_83740_;
      AL10.alListener3f(4100, (float)p_83740_.x, (float)p_83740_.y, (float)p_83740_.z);
   }

   public Vec3 getListenerPosition() {
      return this.position;
   }

   public void setListenerOrientation(Vector3f p_254324_, Vector3f p_253810_) {
      AL10.alListenerfv(4111, new float[]{p_254324_.x(), p_254324_.y(), p_254324_.z(), p_253810_.x(), p_253810_.y(), p_253810_.z()});
   }

   public void setGain(float p_83738_) {
      AL10.alListenerf(4106, p_83738_);
      this.gain = p_83738_;
   }

   public float getGain() {
      return this.gain;
   }

   public void reset() {
      this.setListenerPosition(Vec3.ZERO);
      this.setListenerOrientation(new Vector3f(0.0F, 0.0F, -1.0F), new Vector3f(0.0F, 1.0F, 0.0F));
   }
}