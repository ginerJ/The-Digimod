package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.AbstractOptionSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.OptionEnum;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public final class OptionInstance<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final OptionInstance.Enum<Boolean> BOOLEAN_VALUES = new OptionInstance.Enum<>(ImmutableList.of(Boolean.TRUE, Boolean.FALSE), Codec.BOOL);
   public static final OptionInstance.CaptionBasedToString<Boolean> BOOLEAN_TO_STRING = (p_231544_, p_231545_) -> {
      return p_231545_ ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
   };
   private final OptionInstance.TooltipSupplier<T> tooltip;
   final Function<T, Component> toString;
   private final OptionInstance.ValueSet<T> values;
   private final Codec<T> codec;
   private final T initialValue;
   private final Consumer<T> onValueUpdate;
   final Component caption;
   T value;

   public static OptionInstance<Boolean> createBoolean(String p_231529_, boolean p_231530_, Consumer<Boolean> p_231531_) {
      return createBoolean(p_231529_, noTooltip(), p_231530_, p_231531_);
   }

   public static OptionInstance<Boolean> createBoolean(String p_231526_, boolean p_231527_) {
      return createBoolean(p_231526_, noTooltip(), p_231527_, (p_231548_) -> {
      });
   }

   public static OptionInstance<Boolean> createBoolean(String p_259291_, OptionInstance.TooltipSupplier<Boolean> p_260306_, boolean p_259985_) {
      return createBoolean(p_259291_, p_260306_, p_259985_, (p_231513_) -> {
      });
   }

   public static OptionInstance<Boolean> createBoolean(String p_259289_, OptionInstance.TooltipSupplier<Boolean> p_260210_, boolean p_259359_, Consumer<Boolean> p_259975_) {
      return createBoolean(p_259289_, p_260210_, BOOLEAN_TO_STRING, p_259359_, p_259975_);
   }

   public static OptionInstance<Boolean> createBoolean(String p_262002_, OptionInstance.TooltipSupplier<Boolean> p_261507_, OptionInstance.CaptionBasedToString<Boolean> p_262099_, boolean p_262136_, Consumer<Boolean> p_261984_) {
      return new OptionInstance<>(p_262002_, p_261507_, p_262099_, BOOLEAN_VALUES, p_262136_, p_261984_);
   }

   public OptionInstance(String p_260248_, OptionInstance.TooltipSupplier<T> p_259437_, OptionInstance.CaptionBasedToString<T> p_259148_, OptionInstance.ValueSet<T> p_259590_, T p_260067_, Consumer<T> p_259392_) {
      this(p_260248_, p_259437_, p_259148_, p_259590_, p_259590_.codec(), p_260067_, p_259392_);
   }

   public OptionInstance(String p_259964_, OptionInstance.TooltipSupplier<T> p_260354_, OptionInstance.CaptionBasedToString<T> p_259496_, OptionInstance.ValueSet<T> p_259090_, Codec<T> p_259043_, T p_259396_, Consumer<T> p_260147_) {
      this.caption = Component.translatable(p_259964_);
      this.tooltip = p_260354_;
      this.toString = (p_231506_) -> {
         return p_259496_.toString(this.caption, p_231506_);
      };
      this.values = p_259090_;
      this.codec = p_259043_;
      this.initialValue = p_259396_;
      this.onValueUpdate = p_260147_;
      this.value = this.initialValue;
   }

   public static <T> OptionInstance.TooltipSupplier<T> noTooltip() {
      return (p_258114_) -> {
         return null;
      };
   }

   public static <T> OptionInstance.TooltipSupplier<T> cachedConstantTooltip(Component p_231536_) {
      return (p_258116_) -> {
         return Tooltip.create(p_231536_);
      };
   }

   public static <T extends OptionEnum> OptionInstance.CaptionBasedToString<T> forOptionEnum() {
      return (p_231538_, p_231539_) -> {
         return p_231539_.getCaption();
      };
   }

   public AbstractWidget createButton(Options p_231508_, int p_231509_, int p_231510_, int p_231511_) {
      return this.createButton(p_231508_, p_231509_, p_231510_, p_231511_, (p_261336_) -> {
      });
   }

   public AbstractWidget createButton(Options p_261971_, int p_261486_, int p_261569_, int p_261677_, Consumer<T> p_261912_) {
      return this.values.createButton(this.tooltip, p_261971_, p_261486_, p_261569_, p_261677_, p_261912_).apply(this);
   }

   public T get() {
      return this.value;
   }

   public Codec<T> codec() {
      return this.codec;
   }

   public String toString() {
      return this.caption.getString();
   }

   public void set(T p_231515_) {
      T t = this.values.validateValue(p_231515_).orElseGet(() -> {
         LOGGER.error("Illegal option value " + p_231515_ + " for " + this.caption);
         return this.initialValue;
      });
      if (!Minecraft.getInstance().isRunning()) {
         this.value = t;
      } else {
         if (!Objects.equals(this.value, t)) {
            this.value = t;
            this.onValueUpdate.accept(this.value);
         }

      }
   }

   public OptionInstance.ValueSet<T> values() {
      return this.values;
   }

   @OnlyIn(Dist.CLIENT)
   public static record AltEnum<T>(List<T> values, List<T> altValues, BooleanSupplier altCondition, OptionInstance.CycleableValueSet.ValueSetter<T> valueSetter, Codec<T> codec) implements OptionInstance.CycleableValueSet<T> {
      public CycleButton.ValueListSupplier<T> valueListSupplier() {
         return CycleButton.ValueListSupplier.create(this.altCondition, this.values, this.altValues);
      }

      public Optional<T> validateValue(T p_231570_) {
         return (this.altCondition.getAsBoolean() ? this.altValues : this.values).contains(p_231570_) ? Optional.of(p_231570_) : Optional.empty();
      }

      public OptionInstance.CycleableValueSet.ValueSetter<T> valueSetter() {
         return this.valueSetter;
      }

      public Codec<T> codec() {
         return this.codec;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface CaptionBasedToString<T> {
      Component toString(Component p_231581_, T p_231582_);
   }

   @OnlyIn(Dist.CLIENT)
   public static record ClampingLazyMaxIntRange(int minInclusive, IntSupplier maxSupplier, int encodableMaxInclusive) implements OptionInstance.IntRangeBase, OptionInstance.SliderableOrCyclableValueSet<Integer> {
      public Optional<Integer> validateValue(Integer p_231590_) {
         return Optional.of(Mth.clamp(p_231590_, this.minInclusive(), this.maxInclusive()));
      }

      public int maxInclusive() {
         return this.maxSupplier.getAsInt();
      }

      public Codec<Integer> codec() {
         return ExtraCodecs.validate(Codec.INT, (p_276098_) -> {
            int i = this.encodableMaxInclusive + 1;
            return p_276098_.compareTo(this.minInclusive) >= 0 && p_276098_.compareTo(i) <= 0 ? DataResult.success(p_276098_) : DataResult.error(() -> {
               return "Value " + p_276098_ + " outside of range [" + this.minInclusive + ":" + i + "]";
            }, p_276098_);
         });
      }

      public boolean createCycleButton() {
         return true;
      }

      public CycleButton.ValueListSupplier<Integer> valueListSupplier() {
         return CycleButton.ValueListSupplier.create(IntStream.range(this.minInclusive, this.maxInclusive() + 1).boxed().toList());
      }

      public int minInclusive() {
         return this.minInclusive;
      }
   }

   @OnlyIn(Dist.CLIENT)
   interface CycleableValueSet<T> extends OptionInstance.ValueSet<T> {
      CycleButton.ValueListSupplier<T> valueListSupplier();

      default OptionInstance.CycleableValueSet.ValueSetter<T> valueSetter() {
         return OptionInstance::set;
      }

      default Function<OptionInstance<T>, AbstractWidget> createButton(OptionInstance.TooltipSupplier<T> p_261801_, Options p_261824_, int p_261649_, int p_262114_, int p_261536_, Consumer<T> p_261642_) {
         return (p_261343_) -> {
            return CycleButton.builder(p_261343_.toString).withValues(this.valueListSupplier()).withTooltip(p_261801_).withInitialValue(p_261343_.value).create(p_261649_, p_262114_, p_261536_, 20, p_261343_.caption, (p_261347_, p_261348_) -> {
               this.valueSetter().set(p_261343_, p_261348_);
               p_261824_.save();
               p_261642_.accept(p_261348_);
            });
         };
      }

      @OnlyIn(Dist.CLIENT)
      public interface ValueSetter<T> {
         void set(OptionInstance<T> p_231623_, T p_231624_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static record Enum<T>(List<T> values, Codec<T> codec) implements OptionInstance.CycleableValueSet<T> {
      public Optional<T> validateValue(T p_231632_) {
         return this.values.contains(p_231632_) ? Optional.of(p_231632_) : Optional.empty();
      }

      public CycleButton.ValueListSupplier<T> valueListSupplier() {
         return CycleButton.ValueListSupplier.create(this.values);
      }

      public Codec<T> codec() {
         return this.codec;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static record IntRange(int minInclusive, int maxInclusive) implements OptionInstance.IntRangeBase {
      public Optional<Integer> validateValue(Integer p_231645_) {
         return p_231645_.compareTo(this.minInclusive()) >= 0 && p_231645_.compareTo(this.maxInclusive()) <= 0 ? Optional.of(p_231645_) : Optional.empty();
      }

      public Codec<Integer> codec() {
         return Codec.intRange(this.minInclusive, this.maxInclusive + 1);
      }

      public int minInclusive() {
         return this.minInclusive;
      }

      public int maxInclusive() {
         return this.maxInclusive;
      }
   }

   @OnlyIn(Dist.CLIENT)
   interface IntRangeBase extends OptionInstance.SliderableValueSet<Integer> {
      int minInclusive();

      int maxInclusive();

      default double toSliderValue(Integer p_231663_) {
         return (double)Mth.map((float)p_231663_.intValue(), (float)this.minInclusive(), (float)this.maxInclusive(), 0.0F, 1.0F);
      }

      default Integer fromSliderValue(double p_231656_) {
         return Mth.floor(Mth.map(p_231656_, 0.0D, 1.0D, (double)this.minInclusive(), (double)this.maxInclusive()));
      }

      default <R> OptionInstance.SliderableValueSet<R> xmap(final IntFunction<? extends R> p_231658_, final ToIntFunction<? super R> p_231659_) {
         return new OptionInstance.SliderableValueSet<R>() {
            public Optional<R> validateValue(R p_231674_) {
               return IntRangeBase.this.validateValue(Integer.valueOf(p_231659_.applyAsInt(p_231674_))).map(p_231658_::apply);
            }

            public double toSliderValue(R p_231678_) {
               return IntRangeBase.this.toSliderValue(p_231659_.applyAsInt(p_231678_));
            }

            public R fromSliderValue(double p_231676_) {
               return p_231658_.apply(IntRangeBase.this.fromSliderValue(p_231676_));
            }

            public Codec<R> codec() {
               return IntRangeBase.this.codec().xmap(p_231658_::apply, p_231659_::applyAsInt);
            }
         };
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static record LazyEnum<T>(Supplier<List<T>> values, Function<T, Optional<T>> validateValue, Codec<T> codec) implements OptionInstance.CycleableValueSet<T> {
      public Optional<T> validateValue(T p_231689_) {
         return this.validateValue.apply(p_231689_);
      }

      public CycleButton.ValueListSupplier<T> valueListSupplier() {
         return CycleButton.ValueListSupplier.create(this.values.get());
      }

      public Codec<T> codec() {
         return this.codec;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static final class OptionInstanceSliderButton<N> extends AbstractOptionSliderButton {
      private final OptionInstance<N> instance;
      private final OptionInstance.SliderableValueSet<N> values;
      private final OptionInstance.TooltipSupplier<N> tooltipSupplier;
      private final Consumer<N> onValueChanged;

      OptionInstanceSliderButton(Options p_261713_, int p_261873_, int p_261656_, int p_261799_, int p_261893_, OptionInstance<N> p_262129_, OptionInstance.SliderableValueSet<N> p_261995_, OptionInstance.TooltipSupplier<N> p_261963_, Consumer<N> p_261829_) {
         super(p_261713_, p_261873_, p_261656_, p_261799_, p_261893_, p_261995_.toSliderValue(p_262129_.get()));
         this.instance = p_262129_;
         this.values = p_261995_;
         this.tooltipSupplier = p_261963_;
         this.onValueChanged = p_261829_;
         this.updateMessage();
      }

      protected void updateMessage() {
         this.setMessage(this.instance.toString.apply(this.instance.get()));
         this.setTooltip(this.tooltipSupplier.apply(this.values.fromSliderValue(this.value)));
      }

      protected void applyValue() {
         this.instance.set(this.values.fromSliderValue(this.value));
         this.options.save();
         this.onValueChanged.accept(this.instance.get());
      }
   }

   @OnlyIn(Dist.CLIENT)
   interface SliderableOrCyclableValueSet<T> extends OptionInstance.CycleableValueSet<T>, OptionInstance.SliderableValueSet<T> {
      boolean createCycleButton();

      default Function<OptionInstance<T>, AbstractWidget> createButton(OptionInstance.TooltipSupplier<T> p_261786_, Options p_262030_, int p_261940_, int p_262149_, int p_261495_, Consumer<T> p_261881_) {
         return this.createCycleButton() ? OptionInstance.CycleableValueSet.super.createButton(p_261786_, p_262030_, p_261940_, p_262149_, p_261495_, p_261881_) : OptionInstance.SliderableValueSet.super.createButton(p_261786_, p_262030_, p_261940_, p_262149_, p_261495_, p_261881_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   interface SliderableValueSet<T> extends OptionInstance.ValueSet<T> {
      double toSliderValue(T p_231732_);

      T fromSliderValue(double p_231731_);

      default Function<OptionInstance<T>, AbstractWidget> createButton(OptionInstance.TooltipSupplier<T> p_261993_, Options p_262177_, int p_261706_, int p_261683_, int p_261573_, Consumer<T> p_261969_) {
         return (p_261355_) -> {
            return new OptionInstance.OptionInstanceSliderButton<>(p_262177_, p_261706_, p_261683_, p_261573_, 20, p_261355_, this, p_261993_, p_261969_);
         };
      }
   }

   @FunctionalInterface
   @OnlyIn(Dist.CLIENT)
   public interface TooltipSupplier<T> {
      @Nullable
      Tooltip apply(T p_259319_);
   }

   @OnlyIn(Dist.CLIENT)
   public static enum UnitDouble implements OptionInstance.SliderableValueSet<Double> {
      INSTANCE;

      public Optional<Double> validateValue(Double p_231747_) {
         return p_231747_ >= 0.0D && p_231747_ <= 1.0D ? Optional.of(p_231747_) : Optional.empty();
      }

      public double toSliderValue(Double p_231756_) {
         return p_231756_;
      }

      public Double fromSliderValue(double p_231741_) {
         return p_231741_;
      }

      public <R> OptionInstance.SliderableValueSet<R> xmap(final DoubleFunction<? extends R> p_231751_, final ToDoubleFunction<? super R> p_231752_) {
         return new OptionInstance.SliderableValueSet<R>() {
            public Optional<R> validateValue(R p_231773_) {
               return UnitDouble.this.validateValue(p_231752_.applyAsDouble(p_231773_)).map(p_231751_::apply);
            }

            public double toSliderValue(R p_231777_) {
               return UnitDouble.this.toSliderValue(p_231752_.applyAsDouble(p_231777_));
            }

            public R fromSliderValue(double p_231775_) {
               return p_231751_.apply(UnitDouble.this.fromSliderValue(p_231775_));
            }

            public Codec<R> codec() {
               return UnitDouble.this.codec().xmap(p_231751_::apply, p_231752_::applyAsDouble);
            }
         };
      }

      public Codec<Double> codec() {
         return Codec.either(Codec.doubleRange(0.0D, 1.0D), Codec.BOOL).xmap((p_231743_) -> {
            return p_231743_.map((p_231760_) -> {
               return p_231760_;
            }, (p_231745_) -> {
               return p_231745_ ? 1.0D : 0.0D;
            });
         }, Either::left);
      }
   }

   @OnlyIn(Dist.CLIENT)
   interface ValueSet<T> {
      Function<OptionInstance<T>, AbstractWidget> createButton(OptionInstance.TooltipSupplier<T> p_231779_, Options p_231780_, int p_231781_, int p_231782_, int p_231783_, Consumer<T> p_261976_);

      Optional<T> validateValue(T p_231784_);

      Codec<T> codec();
   }
}