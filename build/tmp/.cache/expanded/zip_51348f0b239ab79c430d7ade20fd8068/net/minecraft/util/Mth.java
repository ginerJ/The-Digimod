package net.minecraft.util;

import java.util.Locale;
import java.util.UUID;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import net.minecraft.Util;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.math.NumberUtils;

public class Mth {
   private static final long UUID_VERSION = 61440L;
   private static final long UUID_VERSION_TYPE_4 = 16384L;
   private static final long UUID_VARIANT = -4611686018427387904L;
   private static final long UUID_VARIANT_2 = Long.MIN_VALUE;
   public static final float PI = (float)Math.PI;
   public static final float HALF_PI = ((float)Math.PI / 2F);
   public static final float TWO_PI = ((float)Math.PI * 2F);
   public static final float DEG_TO_RAD = ((float)Math.PI / 180F);
   public static final float RAD_TO_DEG = (180F / (float)Math.PI);
   public static final float EPSILON = 1.0E-5F;
   public static final float SQRT_OF_TWO = sqrt(2.0F);
   private static final float SIN_SCALE = 10430.378F;
   private static final float[] SIN = Util.make(new float[65536], (p_14077_) -> {
      for(int i = 0; i < p_14077_.length; ++i) {
         p_14077_[i] = (float)Math.sin((double)i * Math.PI * 2.0D / 65536.0D);
      }

   });
   private static final RandomSource RANDOM = RandomSource.createThreadSafe();
   private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
   private static final double ONE_SIXTH = 0.16666666666666666D;
   private static final int FRAC_EXP = 8;
   private static final int LUT_SIZE = 257;
   private static final double FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
   private static final double[] ASIN_TAB = new double[257];
   private static final double[] COS_TAB = new double[257];

   public static float sin(float p_14032_) {
      return SIN[(int)(p_14032_ * 10430.378F) & '\uffff'];
   }

   public static float cos(float p_14090_) {
      return SIN[(int)(p_14090_ * 10430.378F + 16384.0F) & '\uffff'];
   }

   public static float sqrt(float p_14117_) {
      return (float)Math.sqrt((double)p_14117_);
   }

   public static int floor(float p_14144_) {
      int i = (int)p_14144_;
      return p_14144_ < (float)i ? i - 1 : i;
   }

   public static int floor(double p_14108_) {
      int i = (int)p_14108_;
      return p_14108_ < (double)i ? i - 1 : i;
   }

   public static long lfloor(double p_14135_) {
      long i = (long)p_14135_;
      return p_14135_ < (double)i ? i - 1L : i;
   }

   public static float abs(float p_14155_) {
      return Math.abs(p_14155_);
   }

   public static int abs(int p_14041_) {
      return Math.abs(p_14041_);
   }

   public static int ceil(float p_14168_) {
      int i = (int)p_14168_;
      return p_14168_ > (float)i ? i + 1 : i;
   }

   public static int ceil(double p_14166_) {
      int i = (int)p_14166_;
      return p_14166_ > (double)i ? i + 1 : i;
   }

   public static int clamp(int p_14046_, int p_14047_, int p_14048_) {
      return Math.min(Math.max(p_14046_, p_14047_), p_14048_);
   }

   public static float clamp(float p_14037_, float p_14038_, float p_14039_) {
      return p_14037_ < p_14038_ ? p_14038_ : Math.min(p_14037_, p_14039_);
   }

   public static double clamp(double p_14009_, double p_14010_, double p_14011_) {
      return p_14009_ < p_14010_ ? p_14010_ : Math.min(p_14009_, p_14011_);
   }

   public static double clampedLerp(double p_14086_, double p_14087_, double p_14088_) {
      if (p_14088_ < 0.0D) {
         return p_14086_;
      } else {
         return p_14088_ > 1.0D ? p_14087_ : lerp(p_14088_, p_14086_, p_14087_);
      }
   }

   public static float clampedLerp(float p_144921_, float p_144922_, float p_144923_) {
      if (p_144923_ < 0.0F) {
         return p_144921_;
      } else {
         return p_144923_ > 1.0F ? p_144922_ : lerp(p_144923_, p_144921_, p_144922_);
      }
   }

   public static double absMax(double p_14006_, double p_14007_) {
      if (p_14006_ < 0.0D) {
         p_14006_ = -p_14006_;
      }

      if (p_14007_ < 0.0D) {
         p_14007_ = -p_14007_;
      }

      return Math.max(p_14006_, p_14007_);
   }

   public static int floorDiv(int p_14043_, int p_14044_) {
      return Math.floorDiv(p_14043_, p_14044_);
   }

   public static int nextInt(RandomSource p_216272_, int p_216273_, int p_216274_) {
      return p_216273_ >= p_216274_ ? p_216273_ : p_216272_.nextInt(p_216274_ - p_216273_ + 1) + p_216273_;
   }

   public static float nextFloat(RandomSource p_216268_, float p_216269_, float p_216270_) {
      return p_216269_ >= p_216270_ ? p_216269_ : p_216268_.nextFloat() * (p_216270_ - p_216269_) + p_216269_;
   }

   public static double nextDouble(RandomSource p_216264_, double p_216265_, double p_216266_) {
      return p_216265_ >= p_216266_ ? p_216265_ : p_216264_.nextDouble() * (p_216266_ - p_216265_) + p_216265_;
   }

   public static boolean equal(float p_14034_, float p_14035_) {
      return Math.abs(p_14035_ - p_14034_) < 1.0E-5F;
   }

   public static boolean equal(double p_14083_, double p_14084_) {
      return Math.abs(p_14084_ - p_14083_) < (double)1.0E-5F;
   }

   public static int positiveModulo(int p_14101_, int p_14102_) {
      return Math.floorMod(p_14101_, p_14102_);
   }

   public static float positiveModulo(float p_14092_, float p_14093_) {
      return (p_14092_ % p_14093_ + p_14093_) % p_14093_;
   }

   public static double positiveModulo(double p_14110_, double p_14111_) {
      return (p_14110_ % p_14111_ + p_14111_) % p_14111_;
   }

   public static boolean isMultipleOf(int p_265754_, int p_265543_) {
      return p_265754_ % p_265543_ == 0;
   }

   public static int wrapDegrees(int p_14099_) {
      int i = p_14099_ % 360;
      if (i >= 180) {
         i -= 360;
      }

      if (i < -180) {
         i += 360;
      }

      return i;
   }

   public static float wrapDegrees(float p_14178_) {
      float f = p_14178_ % 360.0F;
      if (f >= 180.0F) {
         f -= 360.0F;
      }

      if (f < -180.0F) {
         f += 360.0F;
      }

      return f;
   }

   public static double wrapDegrees(double p_14176_) {
      double d0 = p_14176_ % 360.0D;
      if (d0 >= 180.0D) {
         d0 -= 360.0D;
      }

      if (d0 < -180.0D) {
         d0 += 360.0D;
      }

      return d0;
   }

   public static float degreesDifference(float p_14119_, float p_14120_) {
      return wrapDegrees(p_14120_ - p_14119_);
   }

   public static float degreesDifferenceAbs(float p_14146_, float p_14147_) {
      return abs(degreesDifference(p_14146_, p_14147_));
   }

   public static float rotateIfNecessary(float p_14095_, float p_14096_, float p_14097_) {
      float f = degreesDifference(p_14095_, p_14096_);
      float f1 = clamp(f, -p_14097_, p_14097_);
      return p_14096_ - f1;
   }

   public static float approach(float p_14122_, float p_14123_, float p_14124_) {
      p_14124_ = abs(p_14124_);
      return p_14122_ < p_14123_ ? clamp(p_14122_ + p_14124_, p_14122_, p_14123_) : clamp(p_14122_ - p_14124_, p_14123_, p_14122_);
   }

   public static float approachDegrees(float p_14149_, float p_14150_, float p_14151_) {
      float f = degreesDifference(p_14149_, p_14150_);
      return approach(p_14149_, p_14149_ + f, p_14151_);
   }

   public static int getInt(String p_14060_, int p_14061_) {
      return NumberUtils.toInt(p_14060_, p_14061_);
   }

   public static int smallestEncompassingPowerOfTwo(int p_14126_) {
      int i = p_14126_ - 1;
      i |= i >> 1;
      i |= i >> 2;
      i |= i >> 4;
      i |= i >> 8;
      i |= i >> 16;
      return i + 1;
   }

   public static boolean isPowerOfTwo(int p_14153_) {
      return p_14153_ != 0 && (p_14153_ & p_14153_ - 1) == 0;
   }

   public static int ceillog2(int p_14164_) {
      p_14164_ = isPowerOfTwo(p_14164_) ? p_14164_ : smallestEncompassingPowerOfTwo(p_14164_);
      return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)p_14164_ * 125613361L >> 27) & 31];
   }

   public static int log2(int p_14174_) {
      return ceillog2(p_14174_) - (isPowerOfTwo(p_14174_) ? 0 : 1);
   }

   public static int color(float p_14160_, float p_14161_, float p_14162_) {
      return FastColor.ARGB32.color(0, floor(p_14160_ * 255.0F), floor(p_14161_ * 255.0F), floor(p_14162_ * 255.0F));
   }

   public static float frac(float p_14188_) {
      return p_14188_ - (float)floor(p_14188_);
   }

   public static double frac(double p_14186_) {
      return p_14186_ - (double)lfloor(p_14186_);
   }

   /** @deprecated */
   @Deprecated
   public static long getSeed(Vec3i p_14058_) {
      return getSeed(p_14058_.getX(), p_14058_.getY(), p_14058_.getZ());
   }

   /** @deprecated */
   @Deprecated
   public static long getSeed(int p_14131_, int p_14132_, int p_14133_) {
      long i = (long)(p_14131_ * 3129871) ^ (long)p_14133_ * 116129781L ^ (long)p_14132_;
      i = i * i * 42317861L + i * 11L;
      return i >> 16;
   }

   public static UUID createInsecureUUID(RandomSource p_216262_) {
      long i = p_216262_.nextLong() & -61441L | 16384L;
      long j = p_216262_.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
      return new UUID(i, j);
   }

   public static UUID createInsecureUUID() {
      return createInsecureUUID(RANDOM);
   }

   public static double inverseLerp(double p_14113_, double p_14114_, double p_14115_) {
      return (p_14113_ - p_14114_) / (p_14115_ - p_14114_);
   }

   public static float inverseLerp(float p_184656_, float p_184657_, float p_184658_) {
      return (p_184656_ - p_184657_) / (p_184658_ - p_184657_);
   }

   public static boolean rayIntersectsAABB(Vec3 p_144889_, Vec3 p_144890_, AABB p_144891_) {
      double d0 = (p_144891_.minX + p_144891_.maxX) * 0.5D;
      double d1 = (p_144891_.maxX - p_144891_.minX) * 0.5D;
      double d2 = p_144889_.x - d0;
      if (Math.abs(d2) > d1 && d2 * p_144890_.x >= 0.0D) {
         return false;
      } else {
         double d3 = (p_144891_.minY + p_144891_.maxY) * 0.5D;
         double d4 = (p_144891_.maxY - p_144891_.minY) * 0.5D;
         double d5 = p_144889_.y - d3;
         if (Math.abs(d5) > d4 && d5 * p_144890_.y >= 0.0D) {
            return false;
         } else {
            double d6 = (p_144891_.minZ + p_144891_.maxZ) * 0.5D;
            double d7 = (p_144891_.maxZ - p_144891_.minZ) * 0.5D;
            double d8 = p_144889_.z - d6;
            if (Math.abs(d8) > d7 && d8 * p_144890_.z >= 0.0D) {
               return false;
            } else {
               double d9 = Math.abs(p_144890_.x);
               double d10 = Math.abs(p_144890_.y);
               double d11 = Math.abs(p_144890_.z);
               double d12 = p_144890_.y * d8 - p_144890_.z * d5;
               if (Math.abs(d12) > d4 * d11 + d7 * d10) {
                  return false;
               } else {
                  d12 = p_144890_.z * d2 - p_144890_.x * d8;
                  if (Math.abs(d12) > d1 * d11 + d7 * d9) {
                     return false;
                  } else {
                     d12 = p_144890_.x * d5 - p_144890_.y * d2;
                     return Math.abs(d12) < d1 * d10 + d4 * d9;
                  }
               }
            }
         }
      }
   }

   public static double atan2(double p_14137_, double p_14138_) {
      double d0 = p_14138_ * p_14138_ + p_14137_ * p_14137_;
      if (Double.isNaN(d0)) {
         return Double.NaN;
      } else {
         boolean flag = p_14137_ < 0.0D;
         if (flag) {
            p_14137_ = -p_14137_;
         }

         boolean flag1 = p_14138_ < 0.0D;
         if (flag1) {
            p_14138_ = -p_14138_;
         }

         boolean flag2 = p_14137_ > p_14138_;
         if (flag2) {
            double d1 = p_14138_;
            p_14138_ = p_14137_;
            p_14137_ = d1;
         }

         double d9 = fastInvSqrt(d0);
         p_14138_ *= d9;
         p_14137_ *= d9;
         double d2 = FRAC_BIAS + p_14137_;
         int i = (int)Double.doubleToRawLongBits(d2);
         double d3 = ASIN_TAB[i];
         double d4 = COS_TAB[i];
         double d5 = d2 - FRAC_BIAS;
         double d6 = p_14137_ * d4 - p_14138_ * d5;
         double d7 = (6.0D + d6 * d6) * d6 * 0.16666666666666666D;
         double d8 = d3 + d7;
         if (flag2) {
            d8 = (Math.PI / 2D) - d8;
         }

         if (flag1) {
            d8 = Math.PI - d8;
         }

         if (flag) {
            d8 = -d8;
         }

         return d8;
      }
   }

   public static float invSqrt(float p_265060_) {
      return org.joml.Math.invsqrt(p_265060_);
   }

   public static double invSqrt(double p_265088_) {
      return org.joml.Math.invsqrt(p_265088_);
   }

   /** @deprecated */
   @Deprecated
   public static double fastInvSqrt(double p_14194_) {
      double d0 = 0.5D * p_14194_;
      long i = Double.doubleToRawLongBits(p_14194_);
      i = 6910469410427058090L - (i >> 1);
      p_14194_ = Double.longBitsToDouble(i);
      return p_14194_ * (1.5D - d0 * p_14194_ * p_14194_);
   }

   public static float fastInvCubeRoot(float p_14200_) {
      int i = Float.floatToIntBits(p_14200_);
      i = 1419967116 - i / 3;
      float f = Float.intBitsToFloat(i);
      f = 0.6666667F * f + 1.0F / (3.0F * f * f * p_14200_);
      return 0.6666667F * f + 1.0F / (3.0F * f * f * p_14200_);
   }

   public static int hsvToRgb(float p_14170_, float p_14171_, float p_14172_) {
      int i = (int)(p_14170_ * 6.0F) % 6;
      float f = p_14170_ * 6.0F - (float)i;
      float f1 = p_14172_ * (1.0F - p_14171_);
      float f2 = p_14172_ * (1.0F - f * p_14171_);
      float f3 = p_14172_ * (1.0F - (1.0F - f) * p_14171_);
      float f4;
      float f5;
      float f6;
      switch (i) {
         case 0:
            f4 = p_14172_;
            f5 = f3;
            f6 = f1;
            break;
         case 1:
            f4 = f2;
            f5 = p_14172_;
            f6 = f1;
            break;
         case 2:
            f4 = f1;
            f5 = p_14172_;
            f6 = f3;
            break;
         case 3:
            f4 = f1;
            f5 = f2;
            f6 = p_14172_;
            break;
         case 4:
            f4 = f3;
            f5 = f1;
            f6 = p_14172_;
            break;
         case 5:
            f4 = p_14172_;
            f5 = f1;
            f6 = f2;
            break;
         default:
            throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + p_14170_ + ", " + p_14171_ + ", " + p_14172_);
      }

      return FastColor.ARGB32.color(0, clamp((int)(f4 * 255.0F), 0, 255), clamp((int)(f5 * 255.0F), 0, 255), clamp((int)(f6 * 255.0F), 0, 255));
   }

   public static int murmurHash3Mixer(int p_14184_) {
      p_14184_ ^= p_14184_ >>> 16;
      p_14184_ *= -2048144789;
      p_14184_ ^= p_14184_ >>> 13;
      p_14184_ *= -1028477387;
      return p_14184_ ^ p_14184_ >>> 16;
   }

   public static int binarySearch(int p_14050_, int p_14051_, IntPredicate p_14052_) {
      int i = p_14051_ - p_14050_;

      while(i > 0) {
         int j = i / 2;
         int k = p_14050_ + j;
         if (p_14052_.test(k)) {
            i = j;
         } else {
            p_14050_ = k + 1;
            i -= j + 1;
         }
      }

      return p_14050_;
   }

   public static int lerpInt(float p_270245_, int p_270597_, int p_270301_) {
      return p_270597_ + floor(p_270245_ * (float)(p_270301_ - p_270597_));
   }

   public static float lerp(float p_14180_, float p_14181_, float p_14182_) {
      return p_14181_ + p_14180_ * (p_14182_ - p_14181_);
   }

   public static double lerp(double p_14140_, double p_14141_, double p_14142_) {
      return p_14141_ + p_14140_ * (p_14142_ - p_14141_);
   }

   public static double lerp2(double p_14013_, double p_14014_, double p_14015_, double p_14016_, double p_14017_, double p_14018_) {
      return lerp(p_14014_, lerp(p_14013_, p_14015_, p_14016_), lerp(p_14013_, p_14017_, p_14018_));
   }

   public static double lerp3(double p_14020_, double p_14021_, double p_14022_, double p_14023_, double p_14024_, double p_14025_, double p_14026_, double p_14027_, double p_14028_, double p_14029_, double p_14030_) {
      return lerp(p_14022_, lerp2(p_14020_, p_14021_, p_14023_, p_14024_, p_14025_, p_14026_), lerp2(p_14020_, p_14021_, p_14027_, p_14028_, p_14029_, p_14030_));
   }

   public static float catmullrom(float p_216245_, float p_216246_, float p_216247_, float p_216248_, float p_216249_) {
      return 0.5F * (2.0F * p_216247_ + (p_216248_ - p_216246_) * p_216245_ + (2.0F * p_216246_ - 5.0F * p_216247_ + 4.0F * p_216248_ - p_216249_) * p_216245_ * p_216245_ + (3.0F * p_216247_ - p_216246_ - 3.0F * p_216248_ + p_216249_) * p_216245_ * p_216245_ * p_216245_);
   }

   public static double smoothstep(double p_14198_) {
      return p_14198_ * p_14198_ * p_14198_ * (p_14198_ * (p_14198_ * 6.0D - 15.0D) + 10.0D);
   }

   public static double smoothstepDerivative(double p_144947_) {
      return 30.0D * p_144947_ * p_144947_ * (p_144947_ - 1.0D) * (p_144947_ - 1.0D);
   }

   public static int sign(double p_14206_) {
      if (p_14206_ == 0.0D) {
         return 0;
      } else {
         return p_14206_ > 0.0D ? 1 : -1;
      }
   }

   public static float rotLerp(float p_14190_, float p_14191_, float p_14192_) {
      return p_14191_ + p_14190_ * wrapDegrees(p_14192_ - p_14191_);
   }

   public static float triangleWave(float p_14157_, float p_14158_) {
      return (Math.abs(p_14157_ % p_14158_ - p_14158_ * 0.5F) - p_14158_ * 0.25F) / (p_14158_ * 0.25F);
   }

   public static float square(float p_14208_) {
      return p_14208_ * p_14208_;
   }

   public static double square(double p_144953_) {
      return p_144953_ * p_144953_;
   }

   public static int square(int p_144945_) {
      return p_144945_ * p_144945_;
   }

   public static long square(long p_184644_) {
      return p_184644_ * p_184644_;
   }

   public static double clampedMap(double p_144852_, double p_144853_, double p_144854_, double p_144855_, double p_144856_) {
      return clampedLerp(p_144855_, p_144856_, inverseLerp(p_144852_, p_144853_, p_144854_));
   }

   public static float clampedMap(float p_184632_, float p_184633_, float p_184634_, float p_184635_, float p_184636_) {
      return clampedLerp(p_184635_, p_184636_, inverseLerp(p_184632_, p_184633_, p_184634_));
   }

   public static double map(double p_144915_, double p_144916_, double p_144917_, double p_144918_, double p_144919_) {
      return lerp(inverseLerp(p_144915_, p_144916_, p_144917_), p_144918_, p_144919_);
   }

   public static float map(float p_184638_, float p_184639_, float p_184640_, float p_184641_, float p_184642_) {
      return lerp(inverseLerp(p_184638_, p_184639_, p_184640_), p_184641_, p_184642_);
   }

   public static double wobble(double p_144955_) {
      return p_144955_ + (2.0D * RandomSource.create((long)floor(p_144955_ * 3000.0D)).nextDouble() - 1.0D) * 1.0E-7D / 2.0D;
   }

   public static int roundToward(int p_144942_, int p_144943_) {
      return positiveCeilDiv(p_144942_, p_144943_) * p_144943_;
   }

   public static int positiveCeilDiv(int p_184653_, int p_184654_) {
      return -Math.floorDiv(-p_184653_, p_184654_);
   }

   public static int randomBetweenInclusive(RandomSource p_216288_, int p_216289_, int p_216290_) {
      return p_216288_.nextInt(p_216290_ - p_216289_ + 1) + p_216289_;
   }

   public static float randomBetween(RandomSource p_216284_, float p_216285_, float p_216286_) {
      return p_216284_.nextFloat() * (p_216286_ - p_216285_) + p_216285_;
   }

   public static float normal(RandomSource p_216292_, float p_216293_, float p_216294_) {
      return p_216293_ + (float)p_216292_.nextGaussian() * p_216294_;
   }

   public static double lengthSquared(double p_211590_, double p_211591_) {
      return p_211590_ * p_211590_ + p_211591_ * p_211591_;
   }

   public static double length(double p_184646_, double p_184647_) {
      return Math.sqrt(lengthSquared(p_184646_, p_184647_));
   }

   public static double lengthSquared(double p_211593_, double p_211594_, double p_211595_) {
      return p_211593_ * p_211593_ + p_211594_ * p_211594_ + p_211595_ * p_211595_;
   }

   public static double length(double p_184649_, double p_184650_, double p_184651_) {
      return Math.sqrt(lengthSquared(p_184649_, p_184650_, p_184651_));
   }

   public static int quantize(double p_184629_, int p_184630_) {
      return floor(p_184629_ / (double)p_184630_) * p_184630_;
   }

   public static IntStream outFromOrigin(int p_216296_, int p_216297_, int p_216298_) {
      return outFromOrigin(p_216296_, p_216297_, p_216298_, 1);
   }

   public static IntStream outFromOrigin(int p_216251_, int p_216252_, int p_216253_, int p_216254_) {
      if (p_216252_ > p_216253_) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "upperbound %d expected to be > lowerBound %d", p_216253_, p_216252_));
      } else if (p_216254_ < 1) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "steps expected to be >= 1, was %d", p_216254_));
      } else {
         return p_216251_ >= p_216252_ && p_216251_ <= p_216253_ ? IntStream.iterate(p_216251_, (p_216282_) -> {
            int i = Math.abs(p_216251_ - p_216282_);
            return p_216251_ - i >= p_216252_ || p_216251_ + i <= p_216253_;
         }, (p_216260_) -> {
            boolean flag = p_216260_ <= p_216251_;
            int i = Math.abs(p_216251_ - p_216260_);
            boolean flag1 = p_216251_ + i + p_216254_ <= p_216253_;
            if (!flag || !flag1) {
               int j = p_216251_ - i - (flag ? p_216254_ : 0);
               if (j >= p_216252_) {
                  return j;
               }
            }

            return p_216251_ + i + p_216254_;
         }) : IntStream.empty();
      }
   }

   static {
      for(int i = 0; i < 257; ++i) {
         double d0 = (double)i / 256.0D;
         double d1 = Math.asin(d0);
         COS_TAB[i] = Math.cos(d1);
         ASIN_TAB[i] = d1;
      }

   }
}