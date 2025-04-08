package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface StringRepresentable {
   int PRE_BUILT_MAP_THRESHOLD = 16;

   String getSerializedName();

   static <E extends Enum<E> & StringRepresentable> StringRepresentable.EnumCodec<E> fromEnum(Supplier<E[]> p_216440_) {
      return fromEnumWithMapping(p_216440_, (p_275327_) -> {
         return p_275327_;
      });
   }

   static <E extends Enum<E> & StringRepresentable> StringRepresentable.EnumCodec<E> fromEnumWithMapping(Supplier<E[]> p_275615_, Function<String, String> p_275259_) {
      E[] ae = p_275615_.get();
      if (ae.length > 16) {
         Map<String, E> map = Arrays.stream(ae).collect(Collectors.toMap((p_274905_) -> {
            return p_275259_.apply(p_274905_.getSerializedName());
         }, (p_274903_) -> {
            return p_274903_;
         }));
         return new StringRepresentable.EnumCodec<>(ae, (p_216438_) -> {
            return (E)(p_216438_ == null ? null : map.get(p_216438_));
         });
      } else {
         return new StringRepresentable.EnumCodec<>(ae, (p_274908_) -> {
            for(E e : ae) {
               if (p_275259_.apply(e.getSerializedName()).equals(p_274908_)) {
                  return e;
               }
            }

            return (E)null;
         });
      }
   }

   static Keyable keys(final StringRepresentable[] p_14358_) {
      return new Keyable() {
         public <T> Stream<T> keys(DynamicOps<T> p_184758_) {
            return Arrays.stream(p_14358_).map(StringRepresentable::getSerializedName).map(p_184758_::createString);
         }
      };
   }

   /** @deprecated */
   @Deprecated
   public static class EnumCodec<E extends Enum<E> & StringRepresentable> implements Codec<E> {
      private final Codec<E> codec;
      private final Function<String, E> resolver;

      public EnumCodec(E[] p_216447_, Function<String, E> p_216448_) {
         this.codec = ExtraCodecs.orCompressed(ExtraCodecs.stringResolverCodec((p_216461_) -> {
            return p_216461_.getSerializedName();
         }, p_216448_), ExtraCodecs.idResolverCodec((p_216454_) -> {
            return p_216454_.ordinal();
         }, (p_216459_) -> {
            return (E)(p_216459_ >= 0 && p_216459_ < p_216447_.length ? p_216447_[p_216459_] : null);
         }, -1));
         this.resolver = p_216448_;
      }

      public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> p_216463_, T p_216464_) {
         return this.codec.decode(p_216463_, p_216464_);
      }

      public <T> DataResult<T> encode(E p_216450_, DynamicOps<T> p_216451_, T p_216452_) {
         return this.codec.encode(p_216450_, p_216451_, p_216452_);
      }

      @Nullable
      public E byName(@Nullable String p_216456_) {
         return this.resolver.apply(p_216456_);
      }

      public E byName(@Nullable String p_263077_, E p_263115_) {
         return Objects.requireNonNullElse(this.byName(p_263077_), p_263115_);
      }
   }
}