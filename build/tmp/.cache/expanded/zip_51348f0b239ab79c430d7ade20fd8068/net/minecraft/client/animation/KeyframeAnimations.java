package net.minecraft.client.animation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class KeyframeAnimations {
   public static void animate(HierarchicalModel<?> p_232320_, AnimationDefinition p_232321_, long p_232322_, float p_232323_, Vector3f p_253861_) {
      float f = getElapsedSeconds(p_232321_, p_232322_);

      for(Map.Entry<String, List<AnimationChannel>> entry : p_232321_.boneAnimations().entrySet()) {
         Optional<ModelPart> optional = p_232320_.getAnyDescendantWithName(entry.getKey());
         List<AnimationChannel> list = entry.getValue();
         optional.ifPresent((p_232330_) -> {
            list.forEach((p_288241_) -> {
               Keyframe[] akeyframe = p_288241_.keyframes();
               int i = Math.max(0, Mth.binarySearch(0, akeyframe.length, (p_232315_) -> {
                  return f <= akeyframe[p_232315_].timestamp();
               }) - 1);
               int j = Math.min(akeyframe.length - 1, i + 1);
               Keyframe keyframe = akeyframe[i];
               Keyframe keyframe1 = akeyframe[j];
               float f1 = f - keyframe.timestamp();
               float f2;
               if (j != i) {
                  f2 = Mth.clamp(f1 / (keyframe1.timestamp() - keyframe.timestamp()), 0.0F, 1.0F);
               } else {
                  f2 = 0.0F;
               }

               keyframe1.interpolation().apply(p_253861_, f2, akeyframe, i, j, p_232323_);
               p_288241_.target().apply(p_232330_, p_253861_);
            });
         });
      }

   }

   private static float getElapsedSeconds(AnimationDefinition p_232317_, long p_232318_) {
      float f = (float)p_232318_ / 1000.0F;
      return p_232317_.looping() ? f % p_232317_.lengthInSeconds() : f;
   }

   public static Vector3f posVec(float p_253691_, float p_254046_, float p_254461_) {
      return new Vector3f(p_253691_, -p_254046_, p_254461_);
   }

   public static Vector3f degreeVec(float p_254402_, float p_253917_, float p_254397_) {
      return new Vector3f(p_254402_ * ((float)Math.PI / 180F), p_253917_ * ((float)Math.PI / 180F), p_254397_ * ((float)Math.PI / 180F));
   }

   public static Vector3f scaleVec(double p_253806_, double p_253647_, double p_254396_) {
      return new Vector3f((float)(p_253806_ - 1.0D), (float)(p_253647_ - 1.0D), (float)(p_254396_ - 1.0D));
   }
}