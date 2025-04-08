package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MipmapGenerator {
   private static final int ALPHA_CUTOUT_CUTOFF = 96;
   private static final float[] POW22 = Util.make(new float[256], (p_118058_) -> {
      for(int i = 0; i < p_118058_.length; ++i) {
         p_118058_[i] = (float)Math.pow((double)((float)i / 255.0F), 2.2D);
      }

   });

   private MipmapGenerator() {
   }

   public static NativeImage[] generateMipLevels(NativeImage[] p_251300_, int p_252326_) {
      if (p_252326_ + 1 <= p_251300_.length) {
         return p_251300_;
      } else {
         NativeImage[] anativeimage = new NativeImage[p_252326_ + 1];
         anativeimage[0] = p_251300_[0];
         boolean flag = hasTransparentPixel(anativeimage[0]);

         int maxMipmapLevel = net.minecraftforge.client.ForgeHooksClient.getMaxMipmapLevel(anativeimage[0].getWidth(), anativeimage[0].getHeight());
         for(int i = 1; i <= p_252326_; ++i) {
            if (i < p_251300_.length) {
               anativeimage[i] = p_251300_[i];
            } else {
               NativeImage nativeimage = anativeimage[i - 1];
               // Forge: Guard against invalid texture size, because we allow generating mipmaps regardless of texture sizes
               NativeImage nativeimage1 = new NativeImage(Math.max(1, nativeimage.getWidth() >> 1), Math.max(1, nativeimage.getHeight() >> 1), false);
               if (i <= maxMipmapLevel) {
               int j = nativeimage1.getWidth();
               int k = nativeimage1.getHeight();

               for(int l = 0; l < j; ++l) {
                  for(int i1 = 0; i1 < k; ++i1) {
                     nativeimage1.setPixelRGBA(l, i1, alphaBlend(nativeimage.getPixelRGBA(l * 2 + 0, i1 * 2 + 0), nativeimage.getPixelRGBA(l * 2 + 1, i1 * 2 + 0), nativeimage.getPixelRGBA(l * 2 + 0, i1 * 2 + 1), nativeimage.getPixelRGBA(l * 2 + 1, i1 * 2 + 1), flag));
                  }
               }
               }

               anativeimage[i] = nativeimage1;
            }
         }

         return anativeimage;
      }
   }

   private static boolean hasTransparentPixel(NativeImage p_252279_) {
      for(int i = 0; i < p_252279_.getWidth(); ++i) {
         for(int j = 0; j < p_252279_.getHeight(); ++j) {
            if (p_252279_.getPixelRGBA(i, j) >> 24 == 0) {
               return true;
            }
         }
      }

      return false;
   }

   private static int alphaBlend(int p_118049_, int p_118050_, int p_118051_, int p_118052_, boolean p_118053_) {
      if (p_118053_) {
         float f = 0.0F;
         float f1 = 0.0F;
         float f2 = 0.0F;
         float f3 = 0.0F;
         if (p_118049_ >> 24 != 0) {
            f += getPow22(p_118049_ >> 24);
            f1 += getPow22(p_118049_ >> 16);
            f2 += getPow22(p_118049_ >> 8);
            f3 += getPow22(p_118049_ >> 0);
         }

         if (p_118050_ >> 24 != 0) {
            f += getPow22(p_118050_ >> 24);
            f1 += getPow22(p_118050_ >> 16);
            f2 += getPow22(p_118050_ >> 8);
            f3 += getPow22(p_118050_ >> 0);
         }

         if (p_118051_ >> 24 != 0) {
            f += getPow22(p_118051_ >> 24);
            f1 += getPow22(p_118051_ >> 16);
            f2 += getPow22(p_118051_ >> 8);
            f3 += getPow22(p_118051_ >> 0);
         }

         if (p_118052_ >> 24 != 0) {
            f += getPow22(p_118052_ >> 24);
            f1 += getPow22(p_118052_ >> 16);
            f2 += getPow22(p_118052_ >> 8);
            f3 += getPow22(p_118052_ >> 0);
         }

         f /= 4.0F;
         f1 /= 4.0F;
         f2 /= 4.0F;
         f3 /= 4.0F;
         int i1 = (int)(Math.pow((double)f, 0.45454545454545453D) * 255.0D);
         int j1 = (int)(Math.pow((double)f1, 0.45454545454545453D) * 255.0D);
         int k1 = (int)(Math.pow((double)f2, 0.45454545454545453D) * 255.0D);
         int l1 = (int)(Math.pow((double)f3, 0.45454545454545453D) * 255.0D);
         if (i1 < 96) {
            i1 = 0;
         }

         return i1 << 24 | j1 << 16 | k1 << 8 | l1;
      } else {
         int i = gammaBlend(p_118049_, p_118050_, p_118051_, p_118052_, 24);
         int j = gammaBlend(p_118049_, p_118050_, p_118051_, p_118052_, 16);
         int k = gammaBlend(p_118049_, p_118050_, p_118051_, p_118052_, 8);
         int l = gammaBlend(p_118049_, p_118050_, p_118051_, p_118052_, 0);
         return i << 24 | j << 16 | k << 8 | l;
      }
   }

   private static int gammaBlend(int p_118043_, int p_118044_, int p_118045_, int p_118046_, int p_118047_) {
      float f = getPow22(p_118043_ >> p_118047_);
      float f1 = getPow22(p_118044_ >> p_118047_);
      float f2 = getPow22(p_118045_ >> p_118047_);
      float f3 = getPow22(p_118046_ >> p_118047_);
      float f4 = (float)((double)((float)Math.pow((double)(f + f1 + f2 + f3) * 0.25D, 0.45454545454545453D)));
      return (int)((double)f4 * 255.0D);
   }

   private static float getPow22(int p_118041_) {
      return POW22[p_118041_ & 255];
   }
}
