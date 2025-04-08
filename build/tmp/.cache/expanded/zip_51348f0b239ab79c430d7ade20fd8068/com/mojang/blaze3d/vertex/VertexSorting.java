package com.mojang.blaze3d.vertex;

import com.google.common.primitives.Floats;
import it.unimi.dsi.fastutil.ints.IntArrays;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public interface VertexSorting {
   VertexSorting DISTANCE_TO_ORIGIN = byDistance(0.0F, 0.0F, 0.0F);
   VertexSorting ORTHOGRAPHIC_Z = byDistance((p_277433_) -> {
      return -p_277433_.z();
   });

   static VertexSorting byDistance(float p_277642_, float p_277654_, float p_278092_) {
      return byDistance(new Vector3f(p_277642_, p_277654_, p_278092_));
   }

   static VertexSorting byDistance(Vector3f p_277725_) {
      return byDistance(p_277725_::distanceSquared);
   }

   static VertexSorting byDistance(VertexSorting.DistanceFunction p_277530_) {
      return (p_278083_) -> {
         float[] afloat = new float[p_278083_.length];
         int[] aint = new int[p_278083_.length];

         for(int i = 0; i < p_278083_.length; aint[i] = i++) {
            afloat[i] = p_277530_.apply(p_278083_[i]);
         }

         IntArrays.mergeSort(aint, (p_277443_, p_277864_) -> {
            return Floats.compare(afloat[p_277864_], afloat[p_277443_]);
         });
         return aint;
      };
   }

   int[] sort(Vector3f[] p_277527_);

   @OnlyIn(Dist.CLIENT)
   public interface DistanceFunction {
      float apply(Vector3f p_277761_);
   }
}