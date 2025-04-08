package net.minecraft.nbt;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class NbtOps implements DynamicOps<Tag> {
   public static final NbtOps INSTANCE = new NbtOps();
   private static final String WRAPPER_MARKER = "";

   protected NbtOps() {
   }

   public Tag empty() {
      return EndTag.INSTANCE;
   }

   public <U> U convertTo(DynamicOps<U> p_128980_, Tag p_128981_) {
      switch (p_128981_.getId()) {
         case 0:
            return p_128980_.empty();
         case 1:
            return p_128980_.createByte(((NumericTag)p_128981_).getAsByte());
         case 2:
            return p_128980_.createShort(((NumericTag)p_128981_).getAsShort());
         case 3:
            return p_128980_.createInt(((NumericTag)p_128981_).getAsInt());
         case 4:
            return p_128980_.createLong(((NumericTag)p_128981_).getAsLong());
         case 5:
            return p_128980_.createFloat(((NumericTag)p_128981_).getAsFloat());
         case 6:
            return p_128980_.createDouble(((NumericTag)p_128981_).getAsDouble());
         case 7:
            return p_128980_.createByteList(ByteBuffer.wrap(((ByteArrayTag)p_128981_).getAsByteArray()));
         case 8:
            return p_128980_.createString(p_128981_.getAsString());
         case 9:
            return this.convertList(p_128980_, p_128981_);
         case 10:
            return this.convertMap(p_128980_, p_128981_);
         case 11:
            return p_128980_.createIntList(Arrays.stream(((IntArrayTag)p_128981_).getAsIntArray()));
         case 12:
            return p_128980_.createLongList(Arrays.stream(((LongArrayTag)p_128981_).getAsLongArray()));
         default:
            throw new IllegalStateException("Unknown tag type: " + p_128981_);
      }
   }

   public DataResult<Number> getNumberValue(Tag p_129030_) {
      if (p_129030_ instanceof NumericTag numerictag) {
         return DataResult.success(numerictag.getAsNumber());
      } else {
         return DataResult.error(() -> {
            return "Not a number";
         });
      }
   }

   public Tag createNumeric(Number p_128983_) {
      return DoubleTag.valueOf(p_128983_.doubleValue());
   }

   public Tag createByte(byte p_128963_) {
      return ByteTag.valueOf(p_128963_);
   }

   public Tag createShort(short p_129048_) {
      return ShortTag.valueOf(p_129048_);
   }

   public Tag createInt(int p_128976_) {
      return IntTag.valueOf(p_128976_);
   }

   public Tag createLong(long p_128978_) {
      return LongTag.valueOf(p_128978_);
   }

   public Tag createFloat(float p_128974_) {
      return FloatTag.valueOf(p_128974_);
   }

   public Tag createDouble(double p_128972_) {
      return DoubleTag.valueOf(p_128972_);
   }

   public Tag createBoolean(boolean p_129050_) {
      return ByteTag.valueOf(p_129050_);
   }

   public DataResult<String> getStringValue(Tag p_129061_) {
      if (p_129061_ instanceof StringTag stringtag) {
         return DataResult.success(stringtag.getAsString());
      } else {
         return DataResult.error(() -> {
            return "Not a string";
         });
      }
   }

   public Tag createString(String p_128985_) {
      return StringTag.valueOf(p_128985_);
   }

   public DataResult<Tag> mergeToList(Tag p_129041_, Tag p_129042_) {
      return createCollector(p_129041_).map((p_248053_) -> {
         return DataResult.success(p_248053_.accept(p_129042_).result());
      }).orElseGet(() -> {
         return DataResult.error(() -> {
            return "mergeToList called with not a list: " + p_129041_;
         }, p_129041_);
      });
   }

   public DataResult<Tag> mergeToList(Tag p_129038_, List<Tag> p_129039_) {
      return createCollector(p_129038_).map((p_248048_) -> {
         return DataResult.success(p_248048_.acceptAll(p_129039_).result());
      }).orElseGet(() -> {
         return DataResult.error(() -> {
            return "mergeToList called with not a list: " + p_129038_;
         }, p_129038_);
      });
   }

   public DataResult<Tag> mergeToMap(Tag p_129044_, Tag p_129045_, Tag p_129046_) {
      if (!(p_129044_ instanceof CompoundTag) && !(p_129044_ instanceof EndTag)) {
         return DataResult.error(() -> {
            return "mergeToMap called with not a map: " + p_129044_;
         }, p_129044_);
      } else if (!(p_129045_ instanceof StringTag)) {
         return DataResult.error(() -> {
            return "key is not a string: " + p_129045_;
         }, p_129044_);
      } else {
         CompoundTag compoundtag = new CompoundTag();
         if (p_129044_ instanceof CompoundTag) {
            CompoundTag compoundtag1 = (CompoundTag)p_129044_;
            compoundtag1.getAllKeys().forEach((p_129068_) -> {
               compoundtag.put(p_129068_, compoundtag1.get(p_129068_));
            });
         }

         compoundtag.put(p_129045_.getAsString(), p_129046_);
         return DataResult.success(compoundtag);
      }
   }

   public DataResult<Tag> mergeToMap(Tag p_129032_, MapLike<Tag> p_129033_) {
      if (!(p_129032_ instanceof CompoundTag) && !(p_129032_ instanceof EndTag)) {
         return DataResult.error(() -> {
            return "mergeToMap called with not a map: " + p_129032_;
         }, p_129032_);
      } else {
         CompoundTag compoundtag = new CompoundTag();
         if (p_129032_ instanceof CompoundTag) {
            CompoundTag compoundtag1 = (CompoundTag)p_129032_;
            compoundtag1.getAllKeys().forEach((p_129059_) -> {
               compoundtag.put(p_129059_, compoundtag1.get(p_129059_));
            });
         }

         List<Tag> list = Lists.newArrayList();
         p_129033_.entries().forEach((p_128994_) -> {
            Tag tag = p_128994_.getFirst();
            if (!(tag instanceof StringTag)) {
               list.add(tag);
            } else {
               compoundtag.put(tag.getAsString(), p_128994_.getSecond());
            }
         });
         return !list.isEmpty() ? DataResult.error(() -> {
            return "some keys are not strings: " + list;
         }, compoundtag) : DataResult.success(compoundtag);
      }
   }

   public DataResult<Stream<Pair<Tag, Tag>>> getMapValues(Tag p_129070_) {
      if (p_129070_ instanceof CompoundTag compoundtag) {
         return DataResult.success(compoundtag.getAllKeys().stream().map((p_129021_) -> {
            return Pair.of(this.createString(p_129021_), compoundtag.get(p_129021_));
         }));
      } else {
         return DataResult.error(() -> {
            return "Not a map: " + p_129070_;
         });
      }
   }

   public DataResult<Consumer<BiConsumer<Tag, Tag>>> getMapEntries(Tag p_129103_) {
      if (p_129103_ instanceof CompoundTag compoundtag) {
         return DataResult.success((p_129024_) -> {
            compoundtag.getAllKeys().forEach((p_178006_) -> {
               p_129024_.accept(this.createString(p_178006_), compoundtag.get(p_178006_));
            });
         });
      } else {
         return DataResult.error(() -> {
            return "Not a map: " + p_129103_;
         });
      }
   }

   public DataResult<MapLike<Tag>> getMap(Tag p_129105_) {
      if (p_129105_ instanceof final CompoundTag compoundtag) {
         return DataResult.success(new MapLike<Tag>() {
            @Nullable
            public Tag get(Tag p_129174_) {
               return compoundtag.get(p_129174_.getAsString());
            }

            @Nullable
            public Tag get(String p_129169_) {
               return compoundtag.get(p_129169_);
            }

            public Stream<Pair<Tag, Tag>> entries() {
               return compoundtag.getAllKeys().stream().map((p_129172_) -> {
                  return Pair.of(NbtOps.this.createString(p_129172_), compoundtag.get(p_129172_));
               });
            }

            public String toString() {
               return "MapLike[" + compoundtag + "]";
            }
         });
      } else {
         return DataResult.error(() -> {
            return "Not a map: " + p_129105_;
         });
      }
   }

   public Tag createMap(Stream<Pair<Tag, Tag>> p_129004_) {
      CompoundTag compoundtag = new CompoundTag();
      p_129004_.forEach((p_129018_) -> {
         compoundtag.put(p_129018_.getFirst().getAsString(), p_129018_.getSecond());
      });
      return compoundtag;
   }

   private static Tag tryUnwrap(CompoundTag p_251041_) {
      if (p_251041_.size() == 1) {
         Tag tag = p_251041_.get("");
         if (tag != null) {
            return tag;
         }
      }

      return p_251041_;
   }

   public DataResult<Stream<Tag>> getStream(Tag p_129108_) {
      if (p_129108_ instanceof ListTag listtag) {
         return listtag.getElementType() == 10 ? DataResult.success(listtag.stream().map((p_248049_) -> {
            return tryUnwrap((CompoundTag)p_248049_);
         })) : DataResult.success(listtag.stream());
      } else if (p_129108_ instanceof CollectionTag<?> collectiontag) {
         return DataResult.success(collectiontag.stream().map((p_129158_) -> {
            return p_129158_;
         }));
      } else {
         return DataResult.error(() -> {
            return "Not a list";
         });
      }
   }

   public DataResult<Consumer<Consumer<Tag>>> getList(Tag p_129110_) {
      if (p_129110_ instanceof ListTag listtag) {
         return listtag.getElementType() == 10 ? DataResult.success((p_248055_) -> {
            listtag.forEach((p_248051_) -> {
               p_248055_.accept(tryUnwrap((CompoundTag)p_248051_));
            });
         }) : DataResult.success(listtag::forEach);
      } else if (p_129110_ instanceof CollectionTag<?> collectiontag) {
         return DataResult.success(collectiontag::forEach);
      } else {
         return DataResult.error(() -> {
            return "Not a list: " + p_129110_;
         });
      }
   }

   public DataResult<ByteBuffer> getByteBuffer(Tag p_129132_) {
      if (p_129132_ instanceof ByteArrayTag bytearraytag) {
         return DataResult.success(ByteBuffer.wrap(bytearraytag.getAsByteArray()));
      } else {
         return DynamicOps.super.getByteBuffer(p_129132_);
      }
   }

   public Tag createByteList(ByteBuffer p_128990_) {
      return new ByteArrayTag(DataFixUtils.toArray(p_128990_));
   }

   public DataResult<IntStream> getIntStream(Tag p_129134_) {
      if (p_129134_ instanceof IntArrayTag intarraytag) {
         return DataResult.success(Arrays.stream(intarraytag.getAsIntArray()));
      } else {
         return DynamicOps.super.getIntStream(p_129134_);
      }
   }

   public Tag createIntList(IntStream p_129000_) {
      return new IntArrayTag(p_129000_.toArray());
   }

   public DataResult<LongStream> getLongStream(Tag p_129136_) {
      if (p_129136_ instanceof LongArrayTag longarraytag) {
         return DataResult.success(Arrays.stream(longarraytag.getAsLongArray()));
      } else {
         return DynamicOps.super.getLongStream(p_129136_);
      }
   }

   public Tag createLongList(LongStream p_129002_) {
      return new LongArrayTag(p_129002_.toArray());
   }

   public Tag createList(Stream<Tag> p_129052_) {
      return NbtOps.InitialListCollector.INSTANCE.acceptAll(p_129052_).result();
   }

   public Tag remove(Tag p_129035_, String p_129036_) {
      if (p_129035_ instanceof CompoundTag compoundtag) {
         CompoundTag compoundtag1 = new CompoundTag();
         compoundtag.getAllKeys().stream().filter((p_128988_) -> {
            return !Objects.equals(p_128988_, p_129036_);
         }).forEach((p_129028_) -> {
            compoundtag1.put(p_129028_, compoundtag.get(p_129028_));
         });
         return compoundtag1;
      } else {
         return p_129035_;
      }
   }

   public String toString() {
      return "NBT";
   }

   public RecordBuilder<Tag> mapBuilder() {
      return new NbtOps.NbtRecordBuilder();
   }

   private static Optional<NbtOps.ListCollector> createCollector(Tag p_249503_) {
      if (p_249503_ instanceof EndTag) {
         return Optional.of(NbtOps.InitialListCollector.INSTANCE);
      } else {
         if (p_249503_ instanceof CollectionTag) {
            CollectionTag<?> collectiontag = (CollectionTag)p_249503_;
            if (collectiontag.isEmpty()) {
               return Optional.of(NbtOps.InitialListCollector.INSTANCE);
            }

            if (collectiontag instanceof ListTag) {
               ListTag listtag = (ListTag)collectiontag;
               Optional optional;
               switch (listtag.getElementType()) {
                  case 0:
                     optional = Optional.of(NbtOps.InitialListCollector.INSTANCE);
                     break;
                  case 10:
                     optional = Optional.of(new NbtOps.HeterogenousListCollector(listtag));
                     break;
                  default:
                     optional = Optional.of(new NbtOps.HomogenousListCollector(listtag));
               }

               return optional;
            }

            if (collectiontag instanceof ByteArrayTag) {
               ByteArrayTag bytearraytag = (ByteArrayTag)collectiontag;
               return Optional.of(new NbtOps.ByteListCollector(bytearraytag.getAsByteArray()));
            }

            if (collectiontag instanceof IntArrayTag) {
               IntArrayTag intarraytag = (IntArrayTag)collectiontag;
               return Optional.of(new NbtOps.IntListCollector(intarraytag.getAsIntArray()));
            }

            if (collectiontag instanceof LongArrayTag) {
               LongArrayTag longarraytag = (LongArrayTag)collectiontag;
               return Optional.of(new NbtOps.LongListCollector(longarraytag.getAsLongArray()));
            }
         }

         return Optional.empty();
      }
   }

   static class ByteListCollector implements NbtOps.ListCollector {
      private final ByteArrayList values = new ByteArrayList();

      public ByteListCollector(byte p_249905_) {
         this.values.add(p_249905_);
      }

      public ByteListCollector(byte[] p_250457_) {
         this.values.addElements(0, p_250457_);
      }

      public NbtOps.ListCollector accept(Tag p_250723_) {
         if (p_250723_ instanceof ByteTag bytetag) {
            this.values.add(bytetag.getAsByte());
            return this;
         } else {
            return (new NbtOps.HeterogenousListCollector(this.values)).accept(p_250723_);
         }
      }

      public Tag result() {
         return new ByteArrayTag(this.values.toByteArray());
      }
   }

   static class HeterogenousListCollector implements NbtOps.ListCollector {
      private final ListTag result = new ListTag();

      public HeterogenousListCollector() {
      }

      public HeterogenousListCollector(Collection<Tag> p_249606_) {
         this.result.addAll(p_249606_);
      }

      public HeterogenousListCollector(IntArrayList p_250270_) {
         p_250270_.forEach((p_249166_) -> {
            this.result.add(wrapElement(IntTag.valueOf(p_249166_)));
         });
      }

      public HeterogenousListCollector(ByteArrayList p_248575_) {
         p_248575_.forEach((p_249160_) -> {
            this.result.add(wrapElement(ByteTag.valueOf(p_249160_)));
         });
      }

      public HeterogenousListCollector(LongArrayList p_249410_) {
         p_249410_.forEach((p_249754_) -> {
            this.result.add(wrapElement(LongTag.valueOf(p_249754_)));
         });
      }

      private static boolean isWrapper(CompoundTag p_252073_) {
         return p_252073_.size() == 1 && p_252073_.contains("");
      }

      private static Tag wrapIfNeeded(Tag p_252042_) {
         if (p_252042_ instanceof CompoundTag compoundtag) {
            if (!isWrapper(compoundtag)) {
               return compoundtag;
            }
         }

         return wrapElement(p_252042_);
      }

      private static CompoundTag wrapElement(Tag p_251263_) {
         CompoundTag compoundtag = new CompoundTag();
         compoundtag.put("", p_251263_);
         return compoundtag;
      }

      public NbtOps.ListCollector accept(Tag p_249045_) {
         this.result.add(wrapIfNeeded(p_249045_));
         return this;
      }

      public Tag result() {
         return this.result;
      }
   }

   static class HomogenousListCollector implements NbtOps.ListCollector {
      private final ListTag result = new ListTag();

      HomogenousListCollector(Tag p_249247_) {
         this.result.add(p_249247_);
      }

      HomogenousListCollector(ListTag p_249889_) {
         this.result.addAll(p_249889_);
      }

      public NbtOps.ListCollector accept(Tag p_248727_) {
         if (p_248727_.getId() != this.result.getElementType()) {
            return (new NbtOps.HeterogenousListCollector()).acceptAll(this.result).accept(p_248727_);
         } else {
            this.result.add(p_248727_);
            return this;
         }
      }

      public Tag result() {
         return this.result;
      }
   }

   static class InitialListCollector implements NbtOps.ListCollector {
      public static final NbtOps.InitialListCollector INSTANCE = new NbtOps.InitialListCollector();

      private InitialListCollector() {
      }

      public NbtOps.ListCollector accept(Tag p_251635_) {
         if (p_251635_ instanceof CompoundTag compoundtag) {
            return (new NbtOps.HeterogenousListCollector()).accept(compoundtag);
         } else if (p_251635_ instanceof ByteTag bytetag) {
            return new NbtOps.ByteListCollector(bytetag.getAsByte());
         } else if (p_251635_ instanceof IntTag inttag) {
            return new NbtOps.IntListCollector(inttag.getAsInt());
         } else if (p_251635_ instanceof LongTag longtag) {
            return new NbtOps.LongListCollector(longtag.getAsLong());
         } else {
            return new NbtOps.HomogenousListCollector(p_251635_);
         }
      }

      public Tag result() {
         return new ListTag();
      }
   }

   static class IntListCollector implements NbtOps.ListCollector {
      private final IntArrayList values = new IntArrayList();

      public IntListCollector(int p_250274_) {
         this.values.add(p_250274_);
      }

      public IntListCollector(int[] p_249489_) {
         this.values.addElements(0, p_249489_);
      }

      public NbtOps.ListCollector accept(Tag p_251372_) {
         if (p_251372_ instanceof IntTag inttag) {
            this.values.add(inttag.getAsInt());
            return this;
         } else {
            return (new NbtOps.HeterogenousListCollector(this.values)).accept(p_251372_);
         }
      }

      public Tag result() {
         return new IntArrayTag(this.values.toIntArray());
      }
   }

   interface ListCollector {
      NbtOps.ListCollector accept(Tag p_249030_);

      default NbtOps.ListCollector acceptAll(Iterable<Tag> p_249781_) {
         NbtOps.ListCollector nbtops$listcollector = this;

         for(Tag tag : p_249781_) {
            nbtops$listcollector = nbtops$listcollector.accept(tag);
         }

         return nbtops$listcollector;
      }

      default NbtOps.ListCollector acceptAll(Stream<Tag> p_249876_) {
         return this.acceptAll(p_249876_::iterator);
      }

      Tag result();
   }

   static class LongListCollector implements NbtOps.ListCollector {
      private final LongArrayList values = new LongArrayList();

      public LongListCollector(long p_249842_) {
         this.values.add(p_249842_);
      }

      public LongListCollector(long[] p_251409_) {
         this.values.addElements(0, p_251409_);
      }

      public NbtOps.ListCollector accept(Tag p_252167_) {
         if (p_252167_ instanceof LongTag longtag) {
            this.values.add(longtag.getAsLong());
            return this;
         } else {
            return (new NbtOps.HeterogenousListCollector(this.values)).accept(p_252167_);
         }
      }

      public Tag result() {
         return new LongArrayTag(this.values.toLongArray());
      }
   }

   class NbtRecordBuilder extends RecordBuilder.AbstractStringBuilder<Tag, CompoundTag> {
      protected NbtRecordBuilder() {
         super(NbtOps.this);
      }

      protected CompoundTag initBuilder() {
         return new CompoundTag();
      }

      protected CompoundTag append(String p_129186_, Tag p_129187_, CompoundTag p_129188_) {
         p_129188_.put(p_129186_, p_129187_);
         return p_129188_;
      }

      protected DataResult<Tag> build(CompoundTag p_129190_, Tag p_129191_) {
         if (p_129191_ != null && p_129191_ != EndTag.INSTANCE) {
            if (!(p_129191_ instanceof CompoundTag)) {
               return DataResult.error(() -> {
                  return "mergeToMap called with not a map: " + p_129191_;
               }, p_129191_);
            } else {
               CompoundTag compoundtag = (CompoundTag)p_129191_;
               CompoundTag compoundtag1 = new CompoundTag(Maps.newHashMap(compoundtag.entries()));

               for(Map.Entry<String, Tag> entry : p_129190_.entries().entrySet()) {
                  compoundtag1.put(entry.getKey(), entry.getValue());
               }

               return DataResult.success(compoundtag1);
            }
         } else {
            return DataResult.success(p_129190_);
         }
      }
   }
}