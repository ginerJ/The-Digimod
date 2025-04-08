package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.function.Function;

public class ChunkBedBlockEntityInjecterFix extends DataFix {
   public ChunkBedBlockEntityInjecterFix(Schema p_184825_, boolean p_184826_) {
      super(p_184825_, p_184826_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getOutputSchema().getType(References.CHUNK);
      Type<?> type1 = type.findFieldType("Level");
      Type<?> type2 = type1.findFieldType("TileEntities");
      if (!(type2 instanceof List.ListType<?> listtype)) {
         throw new IllegalStateException("Tile entity type is not a list type.");
      } else {
         return this.cap(type1, listtype);
      }
   }

   private <TE> TypeRewriteRule cap(Type<?> p_184834_, List.ListType<TE> p_184835_) {
      Type<TE> type = p_184835_.getElement();
      OpticFinder<?> opticfinder = DSL.fieldFinder("Level", p_184834_);
      OpticFinder<java.util.List<TE>> opticfinder1 = DSL.fieldFinder("TileEntities", p_184835_);
      int i = 416;
      return TypeRewriteRule.seq(this.fixTypeEverywhere("InjectBedBlockEntityType", (com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType<String>)this.getInputSchema().findChoiceType(References.BLOCK_ENTITY), (com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType<String>)this.getOutputSchema().findChoiceType(References.BLOCK_ENTITY), (p_184841_) -> {
         return (p_184837_) -> {
            return p_184837_;
         };
      }), this.fixTypeEverywhereTyped("BedBlockEntityInjecter", this.getOutputSchema().getType(References.CHUNK), (p_274912_) -> {
         Typed<?> typed = p_274912_.getTyped(opticfinder);
         Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
         int j = dynamic.get("xPos").asInt(0);
         int k = dynamic.get("zPos").asInt(0);
         java.util.List<TE> list = Lists.newArrayList(typed.getOrCreate(opticfinder1));
         java.util.List<? extends Dynamic<?>> list1 = dynamic.get("Sections").asList(Function.identity());

         for(int l = 0; l < list1.size(); ++l) {
            Dynamic<?> dynamic1 = list1.get(l);
            int i1 = dynamic1.get("Y").asInt(0);
            Streams.mapWithIndex(dynamic1.get("Blocks").asIntStream(), (p_274917_, p_274918_) -> {
               if (416 == (p_274917_ & 255) << 4) {
                  int j1 = (int)p_274918_;
                  int k1 = j1 & 15;
                  int l1 = j1 >> 8 & 15;
                  int i2 = j1 >> 4 & 15;
                  Map<Dynamic<?>, Dynamic<?>> map = Maps.newHashMap();
                  map.put(dynamic1.createString("id"), dynamic1.createString("minecraft:bed"));
                  map.put(dynamic1.createString("x"), dynamic1.createInt(k1 + (j << 4)));
                  map.put(dynamic1.createString("y"), dynamic1.createInt(l1 + (i1 << 4)));
                  map.put(dynamic1.createString("z"), dynamic1.createInt(i2 + (k << 4)));
                  map.put(dynamic1.createString("color"), dynamic1.createShort((short)14));
                  return map;
               } else {
                  return null;
               }
            }).forEachOrdered((p_274922_) -> {
               if (p_274922_ != null) {
                  list.add(type.read(dynamic1.createMap(p_274922_)).result().orElseThrow(() -> {
                     return new IllegalStateException("Could not parse newly created bed block entity.");
                  }).getFirst());
               }

            });
         }

         return !list.isEmpty() ? p_274912_.set(opticfinder, typed.set(opticfinder1, list)) : p_274912_;
      }));
   }
}