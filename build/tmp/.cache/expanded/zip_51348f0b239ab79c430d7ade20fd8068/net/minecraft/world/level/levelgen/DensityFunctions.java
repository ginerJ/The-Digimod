package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import org.slf4j.Logger;

public final class DensityFunctions {
   private static final Codec<DensityFunction> CODEC = BuiltInRegistries.DENSITY_FUNCTION_TYPE.byNameCodec().dispatch((p_224053_) -> {
      return p_224053_.codec().codec();
   }, Function.identity());
   protected static final double MAX_REASONABLE_NOISE_VALUE = 1000000.0D;
   static final Codec<Double> NOISE_VALUE_CODEC = Codec.doubleRange(-1000000.0D, 1000000.0D);
   public static final Codec<DensityFunction> DIRECT_CODEC = Codec.either(NOISE_VALUE_CODEC, CODEC).xmap((p_224023_) -> {
      return p_224023_.map(DensityFunctions::constant, Function.identity());
   }, (p_224051_) -> {
      if (p_224051_ instanceof DensityFunctions.Constant densityfunctions$constant) {
         return Either.left(densityfunctions$constant.value());
      } else {
         return Either.right(p_224051_);
      }
   });

   public static Codec<? extends DensityFunction> bootstrap(Registry<Codec<? extends DensityFunction>> p_208343_) {
      register(p_208343_, "blend_alpha", DensityFunctions.BlendAlpha.CODEC);
      register(p_208343_, "blend_offset", DensityFunctions.BlendOffset.CODEC);
      register(p_208343_, "beardifier", DensityFunctions.BeardifierMarker.CODEC);
      register(p_208343_, "old_blended_noise", BlendedNoise.CODEC);

      for(DensityFunctions.Marker.Type densityfunctions$marker$type : DensityFunctions.Marker.Type.values()) {
         register(p_208343_, densityfunctions$marker$type.getSerializedName(), densityfunctions$marker$type.codec);
      }

      register(p_208343_, "noise", DensityFunctions.Noise.CODEC);
      register(p_208343_, "end_islands", DensityFunctions.EndIslandDensityFunction.CODEC);
      register(p_208343_, "weird_scaled_sampler", DensityFunctions.WeirdScaledSampler.CODEC);
      register(p_208343_, "shifted_noise", DensityFunctions.ShiftedNoise.CODEC);
      register(p_208343_, "range_choice", DensityFunctions.RangeChoice.CODEC);
      register(p_208343_, "shift_a", DensityFunctions.ShiftA.CODEC);
      register(p_208343_, "shift_b", DensityFunctions.ShiftB.CODEC);
      register(p_208343_, "shift", DensityFunctions.Shift.CODEC);
      register(p_208343_, "blend_density", DensityFunctions.BlendDensity.CODEC);
      register(p_208343_, "clamp", DensityFunctions.Clamp.CODEC);

      for(DensityFunctions.Mapped.Type densityfunctions$mapped$type : DensityFunctions.Mapped.Type.values()) {
         register(p_208343_, densityfunctions$mapped$type.getSerializedName(), densityfunctions$mapped$type.codec);
      }

      for(DensityFunctions.TwoArgumentSimpleFunction.Type densityfunctions$twoargumentsimplefunction$type : DensityFunctions.TwoArgumentSimpleFunction.Type.values()) {
         register(p_208343_, densityfunctions$twoargumentsimplefunction$type.getSerializedName(), densityfunctions$twoargumentsimplefunction$type.codec);
      }

      register(p_208343_, "spline", DensityFunctions.Spline.CODEC);
      register(p_208343_, "constant", DensityFunctions.Constant.CODEC);
      return register(p_208343_, "y_clamped_gradient", DensityFunctions.YClampedGradient.CODEC);
   }

   private static Codec<? extends DensityFunction> register(Registry<Codec<? extends DensityFunction>> p_224035_, String p_224036_, KeyDispatchDataCodec<? extends DensityFunction> p_224037_) {
      return Registry.register(p_224035_, p_224036_, p_224037_.codec());
   }

   static <A, O> KeyDispatchDataCodec<O> singleArgumentCodec(Codec<A> p_224025_, Function<A, O> p_224026_, Function<O, A> p_224027_) {
      return KeyDispatchDataCodec.of(p_224025_.fieldOf("argument").xmap(p_224026_, p_224027_));
   }

   static <O> KeyDispatchDataCodec<O> singleFunctionArgumentCodec(Function<DensityFunction, O> p_224043_, Function<O, DensityFunction> p_224044_) {
      return singleArgumentCodec(DensityFunction.HOLDER_HELPER_CODEC, p_224043_, p_224044_);
   }

   static <O> KeyDispatchDataCodec<O> doubleFunctionArgumentCodec(BiFunction<DensityFunction, DensityFunction, O> p_224039_, Function<O, DensityFunction> p_224040_, Function<O, DensityFunction> p_224041_) {
      return KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((p_224049_) -> {
         return p_224049_.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument1").forGetter(p_224040_), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument2").forGetter(p_224041_)).apply(p_224049_, p_224039_);
      }));
   }

   static <O> KeyDispatchDataCodec<O> makeCodec(MapCodec<O> p_224029_) {
      return KeyDispatchDataCodec.of(p_224029_);
   }

   private DensityFunctions() {
   }

   public static DensityFunction interpolated(DensityFunction p_208282_) {
      return new DensityFunctions.Marker(DensityFunctions.Marker.Type.Interpolated, p_208282_);
   }

   public static DensityFunction flatCache(DensityFunction p_208362_) {
      return new DensityFunctions.Marker(DensityFunctions.Marker.Type.FlatCache, p_208362_);
   }

   public static DensityFunction cache2d(DensityFunction p_208374_) {
      return new DensityFunctions.Marker(DensityFunctions.Marker.Type.Cache2D, p_208374_);
   }

   public static DensityFunction cacheOnce(DensityFunction p_208381_) {
      return new DensityFunctions.Marker(DensityFunctions.Marker.Type.CacheOnce, p_208381_);
   }

   public static DensityFunction cacheAllInCell(DensityFunction p_208388_) {
      return new DensityFunctions.Marker(DensityFunctions.Marker.Type.CacheAllInCell, p_208388_);
   }

   public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> p_208337_, @Deprecated double p_208338_, double p_208339_, double p_208340_, double p_208341_) {
      return mapFromUnitTo(new DensityFunctions.Noise(new DensityFunction.NoiseHolder(p_208337_), p_208338_, p_208339_), p_208340_, p_208341_);
   }

   public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> p_208332_, double p_208333_, double p_208334_, double p_208335_) {
      return mappedNoise(p_208332_, 1.0D, p_208333_, p_208334_, p_208335_);
   }

   public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> p_208328_, double p_208329_, double p_208330_) {
      return mappedNoise(p_208328_, 1.0D, 1.0D, p_208329_, p_208330_);
   }

   public static DensityFunction shiftedNoise2d(DensityFunction p_208297_, DensityFunction p_208298_, double p_208299_, Holder<NormalNoise.NoiseParameters> p_208300_) {
      return new DensityFunctions.ShiftedNoise(p_208297_, zero(), p_208298_, p_208299_, 0.0D, new DensityFunction.NoiseHolder(p_208300_));
   }

   public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> p_208323_) {
      return noise(p_208323_, 1.0D, 1.0D);
   }

   public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> p_208369_, double p_208370_, double p_208371_) {
      return new DensityFunctions.Noise(new DensityFunction.NoiseHolder(p_208369_), p_208370_, p_208371_);
   }

   public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> p_208325_, double p_208326_) {
      return noise(p_208325_, 1.0D, p_208326_);
   }

   public static DensityFunction rangeChoice(DensityFunction p_208288_, double p_208289_, double p_208290_, DensityFunction p_208291_, DensityFunction p_208292_) {
      return new DensityFunctions.RangeChoice(p_208288_, p_208289_, p_208290_, p_208291_, p_208292_);
   }

   public static DensityFunction shiftA(Holder<NormalNoise.NoiseParameters> p_208367_) {
      return new DensityFunctions.ShiftA(new DensityFunction.NoiseHolder(p_208367_));
   }

   public static DensityFunction shiftB(Holder<NormalNoise.NoiseParameters> p_208379_) {
      return new DensityFunctions.ShiftB(new DensityFunction.NoiseHolder(p_208379_));
   }

   public static DensityFunction shift(Holder<NormalNoise.NoiseParameters> p_208386_) {
      return new DensityFunctions.Shift(new DensityFunction.NoiseHolder(p_208386_));
   }

   public static DensityFunction blendDensity(DensityFunction p_208390_) {
      return new DensityFunctions.BlendDensity(p_208390_);
   }

   public static DensityFunction endIslands(long p_208272_) {
      return new DensityFunctions.EndIslandDensityFunction(p_208272_);
   }

   public static DensityFunction weirdScaledSampler(DensityFunction p_208316_, Holder<NormalNoise.NoiseParameters> p_208317_, DensityFunctions.WeirdScaledSampler.RarityValueMapper p_208318_) {
      return new DensityFunctions.WeirdScaledSampler(p_208316_, new DensityFunction.NoiseHolder(p_208317_), p_208318_);
   }

   public static DensityFunction add(DensityFunction p_208294_, DensityFunction p_208295_) {
      return DensityFunctions.TwoArgumentSimpleFunction.create(DensityFunctions.TwoArgumentSimpleFunction.Type.ADD, p_208294_, p_208295_);
   }

   public static DensityFunction mul(DensityFunction p_208364_, DensityFunction p_208365_) {
      return DensityFunctions.TwoArgumentSimpleFunction.create(DensityFunctions.TwoArgumentSimpleFunction.Type.MUL, p_208364_, p_208365_);
   }

   public static DensityFunction min(DensityFunction p_208376_, DensityFunction p_208377_) {
      return DensityFunctions.TwoArgumentSimpleFunction.create(DensityFunctions.TwoArgumentSimpleFunction.Type.MIN, p_208376_, p_208377_);
   }

   public static DensityFunction max(DensityFunction p_208383_, DensityFunction p_208384_) {
      return DensityFunctions.TwoArgumentSimpleFunction.create(DensityFunctions.TwoArgumentSimpleFunction.Type.MAX, p_208383_, p_208384_);
   }

   public static DensityFunction spline(CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> p_224021_) {
      return new DensityFunctions.Spline(p_224021_);
   }

   public static DensityFunction zero() {
      return DensityFunctions.Constant.ZERO;
   }

   public static DensityFunction constant(double p_208265_) {
      return new DensityFunctions.Constant(p_208265_);
   }

   public static DensityFunction yClampedGradient(int p_208267_, int p_208268_, double p_208269_, double p_208270_) {
      return new DensityFunctions.YClampedGradient(p_208267_, p_208268_, p_208269_, p_208270_);
   }

   public static DensityFunction map(DensityFunction p_208313_, DensityFunctions.Mapped.Type p_208314_) {
      return DensityFunctions.Mapped.create(p_208314_, p_208313_);
   }

   private static DensityFunction mapFromUnitTo(DensityFunction p_208284_, double p_208285_, double p_208286_) {
      double d0 = (p_208285_ + p_208286_) * 0.5D;
      double d1 = (p_208286_ - p_208285_) * 0.5D;
      return add(constant(d0), mul(constant(d1), p_208284_));
   }

   public static DensityFunction blendAlpha() {
      return DensityFunctions.BlendAlpha.INSTANCE;
   }

   public static DensityFunction blendOffset() {
      return DensityFunctions.BlendOffset.INSTANCE;
   }

   public static DensityFunction lerp(DensityFunction p_208302_, DensityFunction p_208303_, DensityFunction p_208304_) {
      if (p_208303_ instanceof DensityFunctions.Constant densityfunctions$constant) {
         return lerp(p_208302_, densityfunctions$constant.value, p_208304_);
      } else {
         DensityFunction densityfunction = cacheOnce(p_208302_);
         DensityFunction densityfunction1 = add(mul(densityfunction, constant(-1.0D)), constant(1.0D));
         return add(mul(p_208303_, densityfunction1), mul(p_208304_, densityfunction));
      }
   }

   public static DensityFunction lerp(DensityFunction p_224031_, double p_224032_, DensityFunction p_224033_) {
      return add(mul(p_224031_, add(p_224033_, constant(-p_224032_))), constant(p_224032_));
   }

   static record Ap2(DensityFunctions.TwoArgumentSimpleFunction.Type type, DensityFunction argument1, DensityFunction argument2, double minValue, double maxValue) implements DensityFunctions.TwoArgumentSimpleFunction {
      public double compute(DensityFunction.FunctionContext p_208410_) {
         double d0 = this.argument1.compute(p_208410_);
         double d1;
         switch (this.type) {
            case ADD:
               d1 = d0 + this.argument2.compute(p_208410_);
               break;
            case MAX:
               d1 = d0 > this.argument2.maxValue() ? d0 : Math.max(d0, this.argument2.compute(p_208410_));
               break;
            case MIN:
               d1 = d0 < this.argument2.minValue() ? d0 : Math.min(d0, this.argument2.compute(p_208410_));
               break;
            case MUL:
               d1 = d0 == 0.0D ? 0.0D : d0 * this.argument2.compute(p_208410_);
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return d1;
      }

      public void fillArray(double[] p_208414_, DensityFunction.ContextProvider p_208415_) {
         this.argument1.fillArray(p_208414_, p_208415_);
         switch (this.type) {
            case ADD:
               double[] adouble = new double[p_208414_.length];
               this.argument2.fillArray(adouble, p_208415_);

               for(int k = 0; k < p_208414_.length; ++k) {
                  p_208414_[k] += adouble[k];
               }
               break;
            case MAX:
               double d3 = this.argument2.maxValue();

               for(int l = 0; l < p_208414_.length; ++l) {
                  double d4 = p_208414_[l];
                  p_208414_[l] = d4 > d3 ? d4 : Math.max(d4, this.argument2.compute(p_208415_.forIndex(l)));
               }
               break;
            case MIN:
               double d2 = this.argument2.minValue();

               for(int j = 0; j < p_208414_.length; ++j) {
                  double d1 = p_208414_[j];
                  p_208414_[j] = d1 < d2 ? d1 : Math.min(d1, this.argument2.compute(p_208415_.forIndex(j)));
               }
               break;
            case MUL:
               for(int i = 0; i < p_208414_.length; ++i) {
                  double d0 = p_208414_[i];
                  p_208414_[i] = d0 == 0.0D ? 0.0D : d0 * this.argument2.compute(p_208415_.forIndex(i));
               }
         }

      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208412_) {
         return p_208412_.apply(DensityFunctions.TwoArgumentSimpleFunction.create(this.type, this.argument1.mapAll(p_208412_), this.argument2.mapAll(p_208412_)));
      }

      public double minValue() {
         return this.minValue;
      }

      public double maxValue() {
         return this.maxValue;
      }

      public DensityFunctions.TwoArgumentSimpleFunction.Type type() {
         return this.type;
      }

      public DensityFunction argument1() {
         return this.argument1;
      }

      public DensityFunction argument2() {
         return this.argument2;
      }
   }

   protected static enum BeardifierMarker implements DensityFunctions.BeardifierOrMarker {
      INSTANCE;

      public double compute(DensityFunction.FunctionContext p_208515_) {
         return 0.0D;
      }

      public void fillArray(double[] p_208517_, DensityFunction.ContextProvider p_208518_) {
         Arrays.fill(p_208517_, 0.0D);
      }

      public double minValue() {
         return 0.0D;
      }

      public double maxValue() {
         return 0.0D;
      }
   }

   public interface BeardifierOrMarker extends DensityFunction.SimpleFunction {
      KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(DensityFunctions.BeardifierMarker.INSTANCE));

      default KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static enum BlendAlpha implements DensityFunction.SimpleFunction {
      INSTANCE;

      public static final KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

      public double compute(DensityFunction.FunctionContext p_208536_) {
         return 1.0D;
      }

      public void fillArray(double[] p_208538_, DensityFunction.ContextProvider p_208539_) {
         Arrays.fill(p_208538_, 1.0D);
      }

      public double minValue() {
         return 1.0D;
      }

      public double maxValue() {
         return 1.0D;
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   static record BlendDensity(DensityFunction input) implements DensityFunctions.TransformerWithContext {
      static final KeyDispatchDataCodec<DensityFunctions.BlendDensity> CODEC = DensityFunctions.singleFunctionArgumentCodec(DensityFunctions.BlendDensity::new, DensityFunctions.BlendDensity::input);

      public double transform(DensityFunction.FunctionContext p_208553_, double p_208554_) {
         return p_208553_.getBlender().blendDensity(p_208553_, p_208554_);
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208556_) {
         return p_208556_.apply(new DensityFunctions.BlendDensity(this.input.mapAll(p_208556_)));
      }

      public double minValue() {
         return Double.NEGATIVE_INFINITY;
      }

      public double maxValue() {
         return Double.POSITIVE_INFINITY;
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public DensityFunction input() {
         return this.input;
      }
   }

   protected static enum BlendOffset implements DensityFunction.SimpleFunction {
      INSTANCE;

      public static final KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

      public double compute(DensityFunction.FunctionContext p_208573_) {
         return 0.0D;
      }

      public void fillArray(double[] p_208575_, DensityFunction.ContextProvider p_208576_) {
         Arrays.fill(p_208575_, 0.0D);
      }

      public double minValue() {
         return 0.0D;
      }

      public double maxValue() {
         return 0.0D;
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static record Clamp(DensityFunction input, double minValue, double maxValue) implements DensityFunctions.PureTransformer {
      private static final MapCodec<DensityFunctions.Clamp> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208597_) -> {
         return p_208597_.group(DensityFunction.DIRECT_CODEC.fieldOf("input").forGetter(DensityFunctions.Clamp::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min").forGetter(DensityFunctions.Clamp::minValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max").forGetter(DensityFunctions.Clamp::maxValue)).apply(p_208597_, DensityFunctions.Clamp::new);
      });
      public static final KeyDispatchDataCodec<DensityFunctions.Clamp> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      public double transform(double p_208595_) {
         return Mth.clamp(p_208595_, this.minValue, this.maxValue);
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208599_) {
         return new DensityFunctions.Clamp(this.input.mapAll(p_208599_), this.minValue, this.maxValue);
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public DensityFunction input() {
         return this.input;
      }

      public double minValue() {
         return this.minValue;
      }

      public double maxValue() {
         return this.maxValue;
      }
   }

   static record Constant(double value) implements DensityFunction.SimpleFunction {
      static final KeyDispatchDataCodec<DensityFunctions.Constant> CODEC = DensityFunctions.singleArgumentCodec(DensityFunctions.NOISE_VALUE_CODEC, DensityFunctions.Constant::new, DensityFunctions.Constant::value);
      static final DensityFunctions.Constant ZERO = new DensityFunctions.Constant(0.0D);

      public double compute(DensityFunction.FunctionContext p_208615_) {
         return this.value;
      }

      public void fillArray(double[] p_208617_, DensityFunction.ContextProvider p_208618_) {
         Arrays.fill(p_208617_, this.value);
      }

      public double minValue() {
         return this.value;
      }

      public double maxValue() {
         return this.value;
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static final class EndIslandDensityFunction implements DensityFunction.SimpleFunction {
      public static final KeyDispatchDataCodec<DensityFunctions.EndIslandDensityFunction> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(new DensityFunctions.EndIslandDensityFunction(0L)));
      private static final float ISLAND_THRESHOLD = -0.9F;
      private final SimplexNoise islandNoise;

      public EndIslandDensityFunction(long p_208630_) {
         RandomSource randomsource = new LegacyRandomSource(p_208630_);
         randomsource.consumeCount(17292);
         this.islandNoise = new SimplexNoise(randomsource);
      }

      private static float getHeightValue(SimplexNoise p_224063_, int p_224064_, int p_224065_) {
         int i = p_224064_ / 2;
         int j = p_224065_ / 2;
         int k = p_224064_ % 2;
         int l = p_224065_ % 2;
         float f = 100.0F - Mth.sqrt((float)(p_224064_ * p_224064_ + p_224065_ * p_224065_)) * 8.0F;
         f = Mth.clamp(f, -100.0F, 80.0F);

         for(int i1 = -12; i1 <= 12; ++i1) {
            for(int j1 = -12; j1 <= 12; ++j1) {
               long k1 = (long)(i + i1);
               long l1 = (long)(j + j1);
               if (k1 * k1 + l1 * l1 > 4096L && p_224063_.getValue((double)k1, (double)l1) < (double)-0.9F) {
                  float f1 = (Mth.abs((float)k1) * 3439.0F + Mth.abs((float)l1) * 147.0F) % 13.0F + 9.0F;
                  float f2 = (float)(k - i1 * 2);
                  float f3 = (float)(l - j1 * 2);
                  float f4 = 100.0F - Mth.sqrt(f2 * f2 + f3 * f3) * f1;
                  f4 = Mth.clamp(f4, -100.0F, 80.0F);
                  f = Math.max(f, f4);
               }
            }
         }

         return f;
      }

      public double compute(DensityFunction.FunctionContext p_208633_) {
         return ((double)getHeightValue(this.islandNoise, p_208633_.blockX() / 8, p_208633_.blockZ() / 8) - 8.0D) / 128.0D;
      }

      public double minValue() {
         return -0.84375D;
      }

      public double maxValue() {
         return 0.5625D;
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   @VisibleForDebug
   public static record HolderHolder(Holder<DensityFunction> function) implements DensityFunction {
      public double compute(DensityFunction.FunctionContext p_208641_) {
         return this.function.value().compute(p_208641_);
      }

      public void fillArray(double[] p_208645_, DensityFunction.ContextProvider p_208646_) {
         this.function.value().fillArray(p_208645_, p_208646_);
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208643_) {
         return p_208643_.apply(new DensityFunctions.HolderHolder(new Holder.Direct<>(this.function.value().mapAll(p_208643_))));
      }

      public double minValue() {
         return this.function.isBound() ? this.function.value().minValue() : Double.NEGATIVE_INFINITY;
      }

      public double maxValue() {
         return this.function.isBound() ? this.function.value().maxValue() : Double.POSITIVE_INFINITY;
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         throw new UnsupportedOperationException("Calling .codec() on HolderHolder");
      }
   }

   protected static record Mapped(DensityFunctions.Mapped.Type type, DensityFunction input, double minValue, double maxValue) implements DensityFunctions.PureTransformer {
      public static DensityFunctions.Mapped create(DensityFunctions.Mapped.Type p_208672_, DensityFunction p_208673_) {
         double d0 = p_208673_.minValue();
         double d1 = transform(p_208672_, d0);
         double d2 = transform(p_208672_, p_208673_.maxValue());
         return p_208672_ != DensityFunctions.Mapped.Type.ABS && p_208672_ != DensityFunctions.Mapped.Type.SQUARE ? new DensityFunctions.Mapped(p_208672_, p_208673_, d1, d2) : new DensityFunctions.Mapped(p_208672_, p_208673_, Math.max(0.0D, d0), Math.max(d1, d2));
      }

      private static double transform(DensityFunctions.Mapped.Type p_208669_, double p_208670_) {
         double d1;
         switch (p_208669_) {
            case ABS:
               d1 = Math.abs(p_208670_);
               break;
            case SQUARE:
               d1 = p_208670_ * p_208670_;
               break;
            case CUBE:
               d1 = p_208670_ * p_208670_ * p_208670_;
               break;
            case HALF_NEGATIVE:
               d1 = p_208670_ > 0.0D ? p_208670_ : p_208670_ * 0.5D;
               break;
            case QUARTER_NEGATIVE:
               d1 = p_208670_ > 0.0D ? p_208670_ : p_208670_ * 0.25D;
               break;
            case SQUEEZE:
               double d0 = Mth.clamp(p_208670_, -1.0D, 1.0D);
               d1 = d0 / 2.0D - d0 * d0 * d0 / 24.0D;
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return d1;
      }

      public double transform(double p_208665_) {
         return transform(this.type, p_208665_);
      }

      public DensityFunctions.Mapped mapAll(DensityFunction.Visitor p_208677_) {
         return create(this.type, this.input.mapAll(p_208677_));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return this.type.codec;
      }

      public DensityFunction input() {
         return this.input;
      }

      public double minValue() {
         return this.minValue;
      }

      public double maxValue() {
         return this.maxValue;
      }

      static enum Type implements StringRepresentable {
         ABS("abs"),
         SQUARE("square"),
         CUBE("cube"),
         HALF_NEGATIVE("half_negative"),
         QUARTER_NEGATIVE("quarter_negative"),
         SQUEEZE("squeeze");

         private final String name;
         final KeyDispatchDataCodec<DensityFunctions.Mapped> codec = DensityFunctions.singleFunctionArgumentCodec((p_208700_) -> {
            return DensityFunctions.Mapped.create(this, p_208700_);
         }, DensityFunctions.Mapped::input);

         private Type(String p_208697_) {
            this.name = p_208697_;
         }

         public String getSerializedName() {
            return this.name;
         }
      }
   }

   protected static record Marker(DensityFunctions.Marker.Type type, DensityFunction wrapped) implements DensityFunctions.MarkerOrMarked {
      public double compute(DensityFunction.FunctionContext p_208712_) {
         return this.wrapped.compute(p_208712_);
      }

      public void fillArray(double[] p_208716_, DensityFunction.ContextProvider p_208717_) {
         this.wrapped.fillArray(p_208716_, p_208717_);
      }

      public double minValue() {
         return this.wrapped.minValue();
      }

      public double maxValue() {
         return this.wrapped.maxValue();
      }

      public DensityFunctions.Marker.Type type() {
         return this.type;
      }

      public DensityFunction wrapped() {
         return this.wrapped;
      }

      static enum Type implements StringRepresentable {
         Interpolated("interpolated"),
         FlatCache("flat_cache"),
         Cache2D("cache_2d"),
         CacheOnce("cache_once"),
         CacheAllInCell("cache_all_in_cell");

         private final String name;
         final KeyDispatchDataCodec<DensityFunctions.MarkerOrMarked> codec = DensityFunctions.singleFunctionArgumentCodec((p_208740_) -> {
            return new DensityFunctions.Marker(this, p_208740_);
         }, DensityFunctions.MarkerOrMarked::wrapped);

         private Type(String p_208737_) {
            this.name = p_208737_;
         }

         public String getSerializedName() {
            return this.name;
         }
      }
   }

   public interface MarkerOrMarked extends DensityFunction {
      DensityFunctions.Marker.Type type();

      DensityFunction wrapped();

      default KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return this.type().codec;
      }

      default DensityFunction mapAll(DensityFunction.Visitor p_224070_) {
         return p_224070_.apply(new DensityFunctions.Marker(this.type(), this.wrapped().mapAll(p_224070_)));
      }
   }

   static record MulOrAdd(DensityFunctions.MulOrAdd.Type specificType, DensityFunction input, double minValue, double maxValue, double argument) implements DensityFunctions.PureTransformer, DensityFunctions.TwoArgumentSimpleFunction {
      public DensityFunctions.TwoArgumentSimpleFunction.Type type() {
         return this.specificType == DensityFunctions.MulOrAdd.Type.MUL ? DensityFunctions.TwoArgumentSimpleFunction.Type.MUL : DensityFunctions.TwoArgumentSimpleFunction.Type.ADD;
      }

      public DensityFunction argument1() {
         return DensityFunctions.constant(this.argument);
      }

      public DensityFunction argument2() {
         return this.input;
      }

      public double transform(double p_208759_) {
         double d0;
         switch (this.specificType) {
            case MUL:
               d0 = p_208759_ * this.argument;
               break;
            case ADD:
               d0 = p_208759_ + this.argument;
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return d0;
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208761_) {
         DensityFunction densityfunction = this.input.mapAll(p_208761_);
         double d0 = densityfunction.minValue();
         double d1 = densityfunction.maxValue();
         double d2;
         double d3;
         if (this.specificType == DensityFunctions.MulOrAdd.Type.ADD) {
            d2 = d0 + this.argument;
            d3 = d1 + this.argument;
         } else if (this.argument >= 0.0D) {
            d2 = d0 * this.argument;
            d3 = d1 * this.argument;
         } else {
            d2 = d1 * this.argument;
            d3 = d0 * this.argument;
         }

         return new DensityFunctions.MulOrAdd(this.specificType, densityfunction, d2, d3, this.argument);
      }

      public DensityFunction input() {
         return this.input;
      }

      public double minValue() {
         return this.minValue;
      }

      public double maxValue() {
         return this.maxValue;
      }

      static enum Type {
         MUL,
         ADD;
      }
   }

   protected static record Noise(DensityFunction.NoiseHolder noise, double xzScale, double yScale) implements DensityFunction {
      public static final MapCodec<DensityFunctions.Noise> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208798_) -> {
         return p_208798_.group(DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(DensityFunctions.Noise::noise), Codec.DOUBLE.fieldOf("xz_scale").forGetter(DensityFunctions.Noise::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(DensityFunctions.Noise::yScale)).apply(p_208798_, DensityFunctions.Noise::new);
      });
      public static final KeyDispatchDataCodec<DensityFunctions.Noise> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      public double compute(DensityFunction.FunctionContext p_208800_) {
         return this.noise.getValue((double)p_208800_.blockX() * this.xzScale, (double)p_208800_.blockY() * this.yScale, (double)p_208800_.blockZ() * this.xzScale);
      }

      public void fillArray(double[] p_224079_, DensityFunction.ContextProvider p_224080_) {
         p_224080_.fillAllDirectly(p_224079_, this);
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_224077_) {
         return p_224077_.apply(new DensityFunctions.Noise(p_224077_.visitNoise(this.noise), this.xzScale, this.yScale));
      }

      public double minValue() {
         return -this.maxValue();
      }

      public double maxValue() {
         return this.noise.maxValue();
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   interface PureTransformer extends DensityFunction {
      DensityFunction input();

      default double compute(DensityFunction.FunctionContext p_208817_) {
         return this.transform(this.input().compute(p_208817_));
      }

      default void fillArray(double[] p_208819_, DensityFunction.ContextProvider p_208820_) {
         this.input().fillArray(p_208819_, p_208820_);

         for(int i = 0; i < p_208819_.length; ++i) {
            p_208819_[i] = this.transform(p_208819_[i]);
         }

      }

      double transform(double p_208815_);
   }

   static record RangeChoice(DensityFunction input, double minInclusive, double maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange) implements DensityFunction {
      public static final MapCodec<DensityFunctions.RangeChoice> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208837_) -> {
         return p_208837_.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(DensityFunctions.RangeChoice::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min_inclusive").forGetter(DensityFunctions.RangeChoice::minInclusive), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max_exclusive").forGetter(DensityFunctions.RangeChoice::maxExclusive), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("when_in_range").forGetter(DensityFunctions.RangeChoice::whenInRange), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("when_out_of_range").forGetter(DensityFunctions.RangeChoice::whenOutOfRange)).apply(p_208837_, DensityFunctions.RangeChoice::new);
      });
      public static final KeyDispatchDataCodec<DensityFunctions.RangeChoice> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      public double compute(DensityFunction.FunctionContext p_208839_) {
         double d0 = this.input.compute(p_208839_);
         return d0 >= this.minInclusive && d0 < this.maxExclusive ? this.whenInRange.compute(p_208839_) : this.whenOutOfRange.compute(p_208839_);
      }

      public void fillArray(double[] p_208843_, DensityFunction.ContextProvider p_208844_) {
         this.input.fillArray(p_208843_, p_208844_);

         for(int i = 0; i < p_208843_.length; ++i) {
            double d0 = p_208843_[i];
            if (d0 >= this.minInclusive && d0 < this.maxExclusive) {
               p_208843_[i] = this.whenInRange.compute(p_208844_.forIndex(i));
            } else {
               p_208843_[i] = this.whenOutOfRange.compute(p_208844_.forIndex(i));
            }
         }

      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208841_) {
         return p_208841_.apply(new DensityFunctions.RangeChoice(this.input.mapAll(p_208841_), this.minInclusive, this.maxExclusive, this.whenInRange.mapAll(p_208841_), this.whenOutOfRange.mapAll(p_208841_)));
      }

      public double minValue() {
         return Math.min(this.whenInRange.minValue(), this.whenOutOfRange.minValue());
      }

      public double maxValue() {
         return Math.max(this.whenInRange.maxValue(), this.whenOutOfRange.maxValue());
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static record Shift(DensityFunction.NoiseHolder offsetNoise) implements DensityFunctions.ShiftNoise {
      static final KeyDispatchDataCodec<DensityFunctions.Shift> CODEC = DensityFunctions.singleArgumentCodec(DensityFunction.NoiseHolder.CODEC, DensityFunctions.Shift::new, DensityFunctions.Shift::offsetNoise);

      public double compute(DensityFunction.FunctionContext p_208864_) {
         return this.compute((double)p_208864_.blockX(), (double)p_208864_.blockY(), (double)p_208864_.blockZ());
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_224087_) {
         return p_224087_.apply(new DensityFunctions.Shift(p_224087_.visitNoise(this.offsetNoise)));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public DensityFunction.NoiseHolder offsetNoise() {
         return this.offsetNoise;
      }
   }

   protected static record ShiftA(DensityFunction.NoiseHolder offsetNoise) implements DensityFunctions.ShiftNoise {
      static final KeyDispatchDataCodec<DensityFunctions.ShiftA> CODEC = DensityFunctions.singleArgumentCodec(DensityFunction.NoiseHolder.CODEC, DensityFunctions.ShiftA::new, DensityFunctions.ShiftA::offsetNoise);

      public double compute(DensityFunction.FunctionContext p_208884_) {
         return this.compute((double)p_208884_.blockX(), 0.0D, (double)p_208884_.blockZ());
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_224093_) {
         return p_224093_.apply(new DensityFunctions.ShiftA(p_224093_.visitNoise(this.offsetNoise)));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public DensityFunction.NoiseHolder offsetNoise() {
         return this.offsetNoise;
      }
   }

   protected static record ShiftB(DensityFunction.NoiseHolder offsetNoise) implements DensityFunctions.ShiftNoise {
      static final KeyDispatchDataCodec<DensityFunctions.ShiftB> CODEC = DensityFunctions.singleArgumentCodec(DensityFunction.NoiseHolder.CODEC, DensityFunctions.ShiftB::new, DensityFunctions.ShiftB::offsetNoise);

      public double compute(DensityFunction.FunctionContext p_208904_) {
         return this.compute((double)p_208904_.blockZ(), (double)p_208904_.blockX(), 0.0D);
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_224099_) {
         return p_224099_.apply(new DensityFunctions.ShiftB(p_224099_.visitNoise(this.offsetNoise)));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public DensityFunction.NoiseHolder offsetNoise() {
         return this.offsetNoise;
      }
   }

   interface ShiftNoise extends DensityFunction {
      DensityFunction.NoiseHolder offsetNoise();

      default double minValue() {
         return -this.maxValue();
      }

      default double maxValue() {
         return this.offsetNoise().maxValue() * 4.0D;
      }

      default double compute(double p_208918_, double p_208919_, double p_208920_) {
         return this.offsetNoise().getValue(p_208918_ * 0.25D, p_208919_ * 0.25D, p_208920_ * 0.25D) * 4.0D;
      }

      default void fillArray(double[] p_224103_, DensityFunction.ContextProvider p_224104_) {
         p_224104_.fillAllDirectly(p_224103_, this);
      }
   }

   protected static record ShiftedNoise(DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, DensityFunction.NoiseHolder noise) implements DensityFunction {
      private static final MapCodec<DensityFunctions.ShiftedNoise> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208943_) -> {
         return p_208943_.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_x").forGetter(DensityFunctions.ShiftedNoise::shiftX), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_y").forGetter(DensityFunctions.ShiftedNoise::shiftY), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_z").forGetter(DensityFunctions.ShiftedNoise::shiftZ), Codec.DOUBLE.fieldOf("xz_scale").forGetter(DensityFunctions.ShiftedNoise::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(DensityFunctions.ShiftedNoise::yScale), DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(DensityFunctions.ShiftedNoise::noise)).apply(p_208943_, DensityFunctions.ShiftedNoise::new);
      });
      public static final KeyDispatchDataCodec<DensityFunctions.ShiftedNoise> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      public double compute(DensityFunction.FunctionContext p_208945_) {
         double d0 = (double)p_208945_.blockX() * this.xzScale + this.shiftX.compute(p_208945_);
         double d1 = (double)p_208945_.blockY() * this.yScale + this.shiftY.compute(p_208945_);
         double d2 = (double)p_208945_.blockZ() * this.xzScale + this.shiftZ.compute(p_208945_);
         return this.noise.getValue(d0, d1, d2);
      }

      public void fillArray(double[] p_208956_, DensityFunction.ContextProvider p_208957_) {
         p_208957_.fillAllDirectly(p_208956_, this);
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208947_) {
         return p_208947_.apply(new DensityFunctions.ShiftedNoise(this.shiftX.mapAll(p_208947_), this.shiftY.mapAll(p_208947_), this.shiftZ.mapAll(p_208947_), this.xzScale, this.yScale, p_208947_.visitNoise(this.noise)));
      }

      public double minValue() {
         return -this.maxValue();
      }

      public double maxValue() {
         return this.noise.maxValue();
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   public static record Spline(CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> spline) implements DensityFunction {
      private static final Codec<CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate>> SPLINE_CODEC = CubicSpline.codec(DensityFunctions.Spline.Coordinate.CODEC);
      private static final MapCodec<DensityFunctions.Spline> DATA_CODEC = SPLINE_CODEC.fieldOf("spline").xmap(DensityFunctions.Spline::new, DensityFunctions.Spline::spline);
      public static final KeyDispatchDataCodec<DensityFunctions.Spline> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      public double compute(DensityFunction.FunctionContext p_211715_) {
         return (double)this.spline.apply(new DensityFunctions.Spline.Point(p_211715_));
      }

      public double minValue() {
         return (double)this.spline.minValue();
      }

      public double maxValue() {
         return (double)this.spline.maxValue();
      }

      public void fillArray(double[] p_211722_, DensityFunction.ContextProvider p_211723_) {
         p_211723_.fillAllDirectly(p_211722_, this);
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_211717_) {
         return p_211717_.apply(new DensityFunctions.Spline(this.spline.mapAll((p_224119_) -> {
            return p_224119_.mapAll(p_211717_);
         })));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public static record Coordinate(Holder<DensityFunction> function) implements ToFloatFunction<DensityFunctions.Spline.Point> {
         public static final Codec<DensityFunctions.Spline.Coordinate> CODEC = DensityFunction.CODEC.xmap(DensityFunctions.Spline.Coordinate::new, DensityFunctions.Spline.Coordinate::function);

         public String toString() {
            Optional<ResourceKey<DensityFunction>> optional = this.function.unwrapKey();
            if (optional.isPresent()) {
               ResourceKey<DensityFunction> resourcekey = optional.get();
               if (resourcekey == NoiseRouterData.CONTINENTS) {
                  return "continents";
               }

               if (resourcekey == NoiseRouterData.EROSION) {
                  return "erosion";
               }

               if (resourcekey == NoiseRouterData.RIDGES) {
                  return "weirdness";
               }

               if (resourcekey == NoiseRouterData.RIDGES_FOLDED) {
                  return "ridges";
               }
            }

            return "Coordinate[" + this.function + "]";
         }

         public float apply(DensityFunctions.Spline.Point p_224130_) {
            return (float)this.function.value().compute(p_224130_.context());
         }

         public float minValue() {
            return this.function.isBound() ? (float)this.function.value().minValue() : Float.NEGATIVE_INFINITY;
         }

         public float maxValue() {
            return this.function.isBound() ? (float)this.function.value().maxValue() : Float.POSITIVE_INFINITY;
         }

         public DensityFunctions.Spline.Coordinate mapAll(DensityFunction.Visitor p_224128_) {
            return new DensityFunctions.Spline.Coordinate(new Holder.Direct<>(this.function.value().mapAll(p_224128_)));
         }
      }

      public static record Point(DensityFunction.FunctionContext context) {
      }
   }

   interface TransformerWithContext extends DensityFunction {
      DensityFunction input();

      default double compute(DensityFunction.FunctionContext p_209065_) {
         return this.transform(p_209065_, this.input().compute(p_209065_));
      }

      default void fillArray(double[] p_209069_, DensityFunction.ContextProvider p_209070_) {
         this.input().fillArray(p_209069_, p_209070_);

         for(int i = 0; i < p_209069_.length; ++i) {
            p_209069_[i] = this.transform(p_209070_.forIndex(i), p_209069_[i]);
         }

      }

      double transform(DensityFunction.FunctionContext p_209066_, double p_209067_);
   }

   interface TwoArgumentSimpleFunction extends DensityFunction {
      Logger LOGGER = LogUtils.getLogger();

      static DensityFunctions.TwoArgumentSimpleFunction create(DensityFunctions.TwoArgumentSimpleFunction.Type p_209074_, DensityFunction p_209075_, DensityFunction p_209076_) {
         double d0 = p_209075_.minValue();
         double d1 = p_209076_.minValue();
         double d2 = p_209075_.maxValue();
         double d3 = p_209076_.maxValue();
         if (p_209074_ == DensityFunctions.TwoArgumentSimpleFunction.Type.MIN || p_209074_ == DensityFunctions.TwoArgumentSimpleFunction.Type.MAX) {
            boolean flag = d0 >= d3;
            boolean flag1 = d1 >= d2;
            if (flag || flag1) {
               LOGGER.warn("Creating a " + p_209074_ + " function between two non-overlapping inputs: " + p_209075_ + " and " + p_209076_);
            }
         }

         double d6;
         switch (p_209074_) {
            case ADD:
               d6 = d0 + d1;
               break;
            case MAX:
               d6 = Math.max(d0, d1);
               break;
            case MIN:
               d6 = Math.min(d0, d1);
               break;
            case MUL:
               d6 = d0 > 0.0D && d1 > 0.0D ? d0 * d1 : (d2 < 0.0D && d3 < 0.0D ? d2 * d3 : Math.min(d0 * d3, d2 * d1));
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         double d5 = d6;
         switch (p_209074_) {
            case ADD:
               d6 = d2 + d3;
               break;
            case MAX:
               d6 = Math.max(d2, d3);
               break;
            case MIN:
               d6 = Math.min(d2, d3);
               break;
            case MUL:
               d6 = d0 > 0.0D && d1 > 0.0D ? d2 * d3 : (d2 < 0.0D && d3 < 0.0D ? d0 * d1 : Math.max(d0 * d1, d2 * d3));
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         double d4 = d6;
         if (p_209074_ == DensityFunctions.TwoArgumentSimpleFunction.Type.MUL || p_209074_ == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD) {
            if (p_209075_ instanceof DensityFunctions.Constant) {
               DensityFunctions.Constant densityfunctions$constant1 = (DensityFunctions.Constant)p_209075_;
               return new DensityFunctions.MulOrAdd(p_209074_ == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD ? DensityFunctions.MulOrAdd.Type.ADD : DensityFunctions.MulOrAdd.Type.MUL, p_209076_, d5, d4, densityfunctions$constant1.value);
            }

            if (p_209076_ instanceof DensityFunctions.Constant) {
               DensityFunctions.Constant densityfunctions$constant = (DensityFunctions.Constant)p_209076_;
               return new DensityFunctions.MulOrAdd(p_209074_ == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD ? DensityFunctions.MulOrAdd.Type.ADD : DensityFunctions.MulOrAdd.Type.MUL, p_209075_, d5, d4, densityfunctions$constant.value);
            }
         }

         return new DensityFunctions.Ap2(p_209074_, p_209075_, p_209076_, d5, d4);
      }

      DensityFunctions.TwoArgumentSimpleFunction.Type type();

      DensityFunction argument1();

      DensityFunction argument2();

      default KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return this.type().codec;
      }

      public static enum Type implements StringRepresentable {
         ADD("add"),
         MUL("mul"),
         MIN("min"),
         MAX("max");

         final KeyDispatchDataCodec<DensityFunctions.TwoArgumentSimpleFunction> codec = DensityFunctions.doubleFunctionArgumentCodec((p_209092_, p_209093_) -> {
            return DensityFunctions.TwoArgumentSimpleFunction.create(this, p_209092_, p_209093_);
         }, DensityFunctions.TwoArgumentSimpleFunction::argument1, DensityFunctions.TwoArgumentSimpleFunction::argument2);
         private final String name;

         private Type(String p_209089_) {
            this.name = p_209089_;
         }

         public String getSerializedName() {
            return this.name;
         }
      }
   }

   protected static record WeirdScaledSampler(DensityFunction input, DensityFunction.NoiseHolder noise, DensityFunctions.WeirdScaledSampler.RarityValueMapper rarityValueMapper) implements DensityFunctions.TransformerWithContext {
      private static final MapCodec<DensityFunctions.WeirdScaledSampler> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208438_) -> {
         return p_208438_.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(DensityFunctions.WeirdScaledSampler::input), DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(DensityFunctions.WeirdScaledSampler::noise), DensityFunctions.WeirdScaledSampler.RarityValueMapper.CODEC.fieldOf("rarity_value_mapper").forGetter(DensityFunctions.WeirdScaledSampler::rarityValueMapper)).apply(p_208438_, DensityFunctions.WeirdScaledSampler::new);
      });
      public static final KeyDispatchDataCodec<DensityFunctions.WeirdScaledSampler> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      public double transform(DensityFunction.FunctionContext p_208440_, double p_208441_) {
         double d0 = this.rarityValueMapper.mapper.get(p_208441_);
         return d0 * Math.abs(this.noise.getValue((double)p_208440_.blockX() / d0, (double)p_208440_.blockY() / d0, (double)p_208440_.blockZ() / d0));
      }

      public DensityFunction mapAll(DensityFunction.Visitor p_208443_) {
         return p_208443_.apply(new DensityFunctions.WeirdScaledSampler(this.input.mapAll(p_208443_), p_208443_.visitNoise(this.noise), this.rarityValueMapper));
      }

      public double minValue() {
         return 0.0D;
      }

      public double maxValue() {
         return this.rarityValueMapper.maxRarity * this.noise.maxValue();
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public DensityFunction input() {
         return this.input;
      }

      public static enum RarityValueMapper implements StringRepresentable {
         TYPE1("type_1", NoiseRouterData.QuantizedSpaghettiRarity::getSpaghettiRarity3D, 2.0D),
         TYPE2("type_2", NoiseRouterData.QuantizedSpaghettiRarity::getSphaghettiRarity2D, 3.0D);

         public static final Codec<DensityFunctions.WeirdScaledSampler.RarityValueMapper> CODEC = StringRepresentable.fromEnum(DensityFunctions.WeirdScaledSampler.RarityValueMapper::values);
         private final String name;
         final Double2DoubleFunction mapper;
         final double maxRarity;

         private RarityValueMapper(String p_208470_, Double2DoubleFunction p_208471_, double p_208472_) {
            this.name = p_208470_;
            this.mapper = p_208471_;
            this.maxRarity = p_208472_;
         }

         public String getSerializedName() {
            return this.name;
         }
      }
   }

   static record YClampedGradient(int fromY, int toY, double fromValue, double toValue) implements DensityFunction.SimpleFunction {
      private static final MapCodec<DensityFunctions.YClampedGradient> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208494_) -> {
         return p_208494_.group(Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2).fieldOf("from_y").forGetter(DensityFunctions.YClampedGradient::fromY), Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2).fieldOf("to_y").forGetter(DensityFunctions.YClampedGradient::toY), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("from_value").forGetter(DensityFunctions.YClampedGradient::fromValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("to_value").forGetter(DensityFunctions.YClampedGradient::toValue)).apply(p_208494_, DensityFunctions.YClampedGradient::new);
      });
      public static final KeyDispatchDataCodec<DensityFunctions.YClampedGradient> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      public double compute(DensityFunction.FunctionContext p_208496_) {
         return Mth.clampedMap((double)p_208496_.blockY(), (double)this.fromY, (double)this.toY, this.fromValue, this.toValue);
      }

      public double minValue() {
         return Math.min(this.fromValue, this.toValue);
      }

      public double maxValue() {
         return Math.max(this.fromValue, this.toValue);
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }
}