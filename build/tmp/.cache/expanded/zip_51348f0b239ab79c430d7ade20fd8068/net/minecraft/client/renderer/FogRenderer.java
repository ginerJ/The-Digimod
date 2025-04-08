package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class FogRenderer {
   private static final int WATER_FOG_DISTANCE = 96;
   private static final List<FogRenderer.MobEffectFogFunction> MOB_EFFECT_FOG = Lists.newArrayList(new FogRenderer.BlindnessFogFunction(), new FogRenderer.DarknessFogFunction());
   public static final float BIOME_FOG_TRANSITION_TIME = 5000.0F;
   private static float fogRed;
   private static float fogGreen;
   private static float fogBlue;
   private static int targetBiomeFog = -1;
   private static int previousBiomeFog = -1;
   private static long biomeChangedTime = -1L;

   public static void setupColor(Camera p_109019_, float p_109020_, ClientLevel p_109021_, int p_109022_, float p_109023_) {
      FogType fogtype = p_109019_.getFluidInCamera();
      Entity entity = p_109019_.getEntity();
      if (fogtype == FogType.WATER) {
         long i = Util.getMillis();
         int j = p_109021_.getBiome(BlockPos.containing(p_109019_.getPosition())).value().getWaterFogColor();
         if (biomeChangedTime < 0L) {
            targetBiomeFog = j;
            previousBiomeFog = j;
            biomeChangedTime = i;
         }

         int k = targetBiomeFog >> 16 & 255;
         int l = targetBiomeFog >> 8 & 255;
         int i1 = targetBiomeFog & 255;
         int j1 = previousBiomeFog >> 16 & 255;
         int k1 = previousBiomeFog >> 8 & 255;
         int l1 = previousBiomeFog & 255;
         float f = Mth.clamp((float)(i - biomeChangedTime) / 5000.0F, 0.0F, 1.0F);
         float f1 = Mth.lerp(f, (float)j1, (float)k);
         float f2 = Mth.lerp(f, (float)k1, (float)l);
         float f3 = Mth.lerp(f, (float)l1, (float)i1);
         fogRed = f1 / 255.0F;
         fogGreen = f2 / 255.0F;
         fogBlue = f3 / 255.0F;
         if (targetBiomeFog != j) {
            targetBiomeFog = j;
            previousBiomeFog = Mth.floor(f1) << 16 | Mth.floor(f2) << 8 | Mth.floor(f3);
            biomeChangedTime = i;
         }
      } else if (fogtype == FogType.LAVA) {
         fogRed = 0.6F;
         fogGreen = 0.1F;
         fogBlue = 0.0F;
         biomeChangedTime = -1L;
      } else if (fogtype == FogType.POWDER_SNOW) {
         fogRed = 0.623F;
         fogGreen = 0.734F;
         fogBlue = 0.785F;
         biomeChangedTime = -1L;
         RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
      } else {
         float f4 = 0.25F + 0.75F * (float)p_109022_ / 32.0F;
         f4 = 1.0F - (float)Math.pow((double)f4, 0.25D);
         Vec3 vec3 = p_109021_.getSkyColor(p_109019_.getPosition(), p_109020_);
         float f6 = (float)vec3.x;
         float f8 = (float)vec3.y;
         float f10 = (float)vec3.z;
         float f11 = Mth.clamp(Mth.cos(p_109021_.getTimeOfDay(p_109020_) * ((float)Math.PI * 2F)) * 2.0F + 0.5F, 0.0F, 1.0F);
         BiomeManager biomemanager = p_109021_.getBiomeManager();
         Vec3 vec31 = p_109019_.getPosition().subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
         Vec3 vec32 = CubicSampler.gaussianSampleVec3(vec31, (p_109033_, p_109034_, p_109035_) -> {
            return p_109021_.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(p_109033_, p_109034_, p_109035_).value().getFogColor()), f11);
         });
         fogRed = (float)vec32.x();
         fogGreen = (float)vec32.y();
         fogBlue = (float)vec32.z();
         if (p_109022_ >= 4) {
            float f12 = Mth.sin(p_109021_.getSunAngle(p_109020_)) > 0.0F ? -1.0F : 1.0F;
            Vector3f vector3f = new Vector3f(f12, 0.0F, 0.0F);
            float f16 = p_109019_.getLookVector().dot(vector3f);
            if (f16 < 0.0F) {
               f16 = 0.0F;
            }

            if (f16 > 0.0F) {
               float[] afloat = p_109021_.effects().getSunriseColor(p_109021_.getTimeOfDay(p_109020_), p_109020_);
               if (afloat != null) {
                  f16 *= afloat[3];
                  fogRed = fogRed * (1.0F - f16) + afloat[0] * f16;
                  fogGreen = fogGreen * (1.0F - f16) + afloat[1] * f16;
                  fogBlue = fogBlue * (1.0F - f16) + afloat[2] * f16;
               }
            }
         }

         fogRed += (f6 - fogRed) * f4;
         fogGreen += (f8 - fogGreen) * f4;
         fogBlue += (f10 - fogBlue) * f4;
         float f13 = p_109021_.getRainLevel(p_109020_);
         if (f13 > 0.0F) {
            float f14 = 1.0F - f13 * 0.5F;
            float f17 = 1.0F - f13 * 0.4F;
            fogRed *= f14;
            fogGreen *= f14;
            fogBlue *= f17;
         }

         float f15 = p_109021_.getThunderLevel(p_109020_);
         if (f15 > 0.0F) {
            float f18 = 1.0F - f15 * 0.5F;
            fogRed *= f18;
            fogGreen *= f18;
            fogBlue *= f18;
         }

         biomeChangedTime = -1L;
      }

      float f5 = ((float)p_109019_.getPosition().y - (float)p_109021_.getMinBuildHeight()) * p_109021_.getLevelData().getClearColorScale();
      FogRenderer.MobEffectFogFunction fogrenderer$mobeffectfogfunction = getPriorityFogFunction(entity, p_109020_);
      if (fogrenderer$mobeffectfogfunction != null) {
         LivingEntity livingentity = (LivingEntity)entity;
         f5 = fogrenderer$mobeffectfogfunction.getModifiedVoidDarkness(livingentity, livingentity.getEffect(fogrenderer$mobeffectfogfunction.getMobEffect()), f5, p_109020_);
      }

      if (f5 < 1.0F && fogtype != FogType.LAVA && fogtype != FogType.POWDER_SNOW) {
         if (f5 < 0.0F) {
            f5 = 0.0F;
         }

         f5 *= f5;
         fogRed *= f5;
         fogGreen *= f5;
         fogBlue *= f5;
      }

      if (p_109023_ > 0.0F) {
         fogRed = fogRed * (1.0F - p_109023_) + fogRed * 0.7F * p_109023_;
         fogGreen = fogGreen * (1.0F - p_109023_) + fogGreen * 0.6F * p_109023_;
         fogBlue = fogBlue * (1.0F - p_109023_) + fogBlue * 0.6F * p_109023_;
      }

      float f7;
      if (fogtype == FogType.WATER) {
         if (entity instanceof LocalPlayer) {
            f7 = ((LocalPlayer)entity).getWaterVision();
         } else {
            f7 = 1.0F;
         }
      } else {
         label86: {
            if (entity instanceof LivingEntity) {
               LivingEntity livingentity1 = (LivingEntity)entity;
               if (livingentity1.hasEffect(MobEffects.NIGHT_VISION) && !livingentity1.hasEffect(MobEffects.DARKNESS)) {
                  f7 = GameRenderer.getNightVisionScale(livingentity1, p_109020_);
                  break label86;
               }
            }

            f7 = 0.0F;
         }
      }

      if (fogRed != 0.0F && fogGreen != 0.0F && fogBlue != 0.0F) {
         float f9 = Math.min(1.0F / fogRed, Math.min(1.0F / fogGreen, 1.0F / fogBlue));
         fogRed = fogRed * (1.0F - f7) + fogRed * f9 * f7;
         fogGreen = fogGreen * (1.0F - f7) + fogGreen * f9 * f7;
         fogBlue = fogBlue * (1.0F - f7) + fogBlue * f9 * f7;
      }

      Vector3f fogColor = net.minecraftforge.client.ForgeHooksClient.getFogColor(p_109019_, p_109020_, p_109021_, p_109022_, p_109023_, fogRed, fogGreen, fogBlue);

      fogRed = fogColor.x();
      fogGreen = fogColor.y();
      fogBlue = fogColor.z();

      RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
   }

   public static void setupNoFog() {
      RenderSystem.setShaderFogStart(Float.MAX_VALUE);
   }

   @Nullable
   private static FogRenderer.MobEffectFogFunction getPriorityFogFunction(Entity p_234166_, float p_234167_) {
      if (p_234166_ instanceof LivingEntity livingentity) {
         return MOB_EFFECT_FOG.stream().filter((p_234171_) -> {
            return p_234171_.isEnabled(livingentity, p_234167_);
         }).findFirst().orElse((FogRenderer.MobEffectFogFunction)null);
      } else {
         return null;
      }
   }

   public static void setupFog(Camera p_234173_, FogRenderer.FogMode p_234174_, float p_234175_, boolean p_234176_, float p_234177_) {
      FogType fogtype = p_234173_.getFluidInCamera();
      Entity entity = p_234173_.getEntity();
      FogRenderer.FogData fogrenderer$fogdata = new FogRenderer.FogData(p_234174_);
      FogRenderer.MobEffectFogFunction fogrenderer$mobeffectfogfunction = getPriorityFogFunction(entity, p_234177_);
      if (fogtype == FogType.LAVA) {
         if (entity.isSpectator()) {
            fogrenderer$fogdata.start = -8.0F;
            fogrenderer$fogdata.end = p_234175_ * 0.5F;
         } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(MobEffects.FIRE_RESISTANCE)) {
            fogrenderer$fogdata.start = 0.0F;
            fogrenderer$fogdata.end = 3.0F;
         } else {
            fogrenderer$fogdata.start = 0.25F;
            fogrenderer$fogdata.end = 1.0F;
         }
      } else if (fogtype == FogType.POWDER_SNOW) {
         if (entity.isSpectator()) {
            fogrenderer$fogdata.start = -8.0F;
            fogrenderer$fogdata.end = p_234175_ * 0.5F;
         } else {
            fogrenderer$fogdata.start = 0.0F;
            fogrenderer$fogdata.end = 2.0F;
         }
      } else if (fogrenderer$mobeffectfogfunction != null) {
         LivingEntity livingentity = (LivingEntity)entity;
         MobEffectInstance mobeffectinstance = livingentity.getEffect(fogrenderer$mobeffectfogfunction.getMobEffect());
         if (mobeffectinstance != null) {
            fogrenderer$mobeffectfogfunction.setupFog(fogrenderer$fogdata, livingentity, mobeffectinstance, p_234175_, p_234177_);
         }
      } else if (fogtype == FogType.WATER) {
         fogrenderer$fogdata.start = -8.0F;
         fogrenderer$fogdata.end = 96.0F;
         if (entity instanceof LocalPlayer) {
            LocalPlayer localplayer = (LocalPlayer)entity;
            fogrenderer$fogdata.end *= Math.max(0.25F, localplayer.getWaterVision());
            Holder<Biome> holder = localplayer.level().getBiome(localplayer.blockPosition());
            if (holder.is(BiomeTags.HAS_CLOSER_WATER_FOG)) {
               fogrenderer$fogdata.end *= 0.85F;
            }
         }

         if (fogrenderer$fogdata.end > p_234175_) {
            fogrenderer$fogdata.end = p_234175_;
            fogrenderer$fogdata.shape = FogShape.CYLINDER;
         }
      } else if (p_234176_) {
         fogrenderer$fogdata.start = p_234175_ * 0.05F;
         fogrenderer$fogdata.end = Math.min(p_234175_, 192.0F) * 0.5F;
      } else if (p_234174_ == FogRenderer.FogMode.FOG_SKY) {
         fogrenderer$fogdata.start = 0.0F;
         fogrenderer$fogdata.end = p_234175_;
         fogrenderer$fogdata.shape = FogShape.CYLINDER;
      } else {
         float f = Mth.clamp(p_234175_ / 10.0F, 4.0F, 64.0F);
         fogrenderer$fogdata.start = p_234175_ - f;
         fogrenderer$fogdata.end = p_234175_;
         fogrenderer$fogdata.shape = FogShape.CYLINDER;
      }

      RenderSystem.setShaderFogStart(fogrenderer$fogdata.start);
      RenderSystem.setShaderFogEnd(fogrenderer$fogdata.end);
      RenderSystem.setShaderFogShape(fogrenderer$fogdata.shape);
      net.minecraftforge.client.ForgeHooksClient.onFogRender(p_234174_, fogtype, p_234173_, p_234177_, p_234175_, fogrenderer$fogdata.start, fogrenderer$fogdata.end, fogrenderer$fogdata.shape);
   }

   public static void levelFogColor() {
      RenderSystem.setShaderFogColor(fogRed, fogGreen, fogBlue);
   }

   @OnlyIn(Dist.CLIENT)
   static class BlindnessFogFunction implements FogRenderer.MobEffectFogFunction {
      public MobEffect getMobEffect() {
         return MobEffects.BLINDNESS;
      }

      public void setupFog(FogRenderer.FogData p_234181_, LivingEntity p_234182_, MobEffectInstance p_234183_, float p_234184_, float p_234185_) {
         float f = p_234183_.isInfiniteDuration() ? 5.0F : Mth.lerp(Math.min(1.0F, (float)p_234183_.getDuration() / 20.0F), p_234184_, 5.0F);
         if (p_234181_.mode == FogRenderer.FogMode.FOG_SKY) {
            p_234181_.start = 0.0F;
            p_234181_.end = f * 0.8F;
         } else {
            p_234181_.start = f * 0.25F;
            p_234181_.end = f;
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   static class DarknessFogFunction implements FogRenderer.MobEffectFogFunction {
      public MobEffect getMobEffect() {
         return MobEffects.DARKNESS;
      }

      public void setupFog(FogRenderer.FogData p_234194_, LivingEntity p_234195_, MobEffectInstance p_234196_, float p_234197_, float p_234198_) {
         if (!p_234196_.getFactorData().isEmpty()) {
            float f = Mth.lerp(p_234196_.getFactorData().get().getFactor(p_234195_, p_234198_), p_234197_, 15.0F);
            p_234194_.start = p_234194_.mode == FogRenderer.FogMode.FOG_SKY ? 0.0F : f * 0.75F;
            p_234194_.end = f;
         }
      }

      public float getModifiedVoidDarkness(LivingEntity p_234189_, MobEffectInstance p_234190_, float p_234191_, float p_234192_) {
         return p_234190_.getFactorData().isEmpty() ? 0.0F : 1.0F - p_234190_.getFactorData().get().getFactor(p_234189_, p_234192_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class FogData {
      public final FogRenderer.FogMode mode;
      public float start;
      public float end;
      public FogShape shape = FogShape.SPHERE;

      public FogData(FogRenderer.FogMode p_234204_) {
         this.mode = p_234204_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum FogMode {
      FOG_SKY,
      FOG_TERRAIN;
   }

   @OnlyIn(Dist.CLIENT)
   interface MobEffectFogFunction {
      MobEffect getMobEffect();

      void setupFog(FogRenderer.FogData p_234212_, LivingEntity p_234213_, MobEffectInstance p_234214_, float p_234215_, float p_234216_);

      default boolean isEnabled(LivingEntity p_234206_, float p_234207_) {
         return p_234206_.hasEffect(this.getMobEffect());
      }

      default float getModifiedVoidDarkness(LivingEntity p_234208_, MobEffectInstance p_234209_, float p_234210_, float p_234211_) {
         MobEffectInstance mobeffectinstance = p_234208_.getEffect(this.getMobEffect());
         if (mobeffectinstance != null) {
            if (mobeffectinstance.endsWithin(19)) {
               p_234210_ = 1.0F - (float)mobeffectinstance.getDuration() / 20.0F;
            } else {
               p_234210_ = 0.0F;
            }
         }

         return p_234210_;
      }
   }
}
