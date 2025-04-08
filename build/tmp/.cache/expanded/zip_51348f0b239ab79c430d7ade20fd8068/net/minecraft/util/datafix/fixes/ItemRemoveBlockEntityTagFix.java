package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.Set;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemRemoveBlockEntityTagFix extends DataFix {
   private final Set<String> items;

   public ItemRemoveBlockEntityTagFix(Schema p_242892_, boolean p_242905_, Set<String> p_242937_) {
      super(p_242892_, p_242905_);
      this.items = p_242937_;
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder<?> opticfinder1 = type.findField("tag");
      OpticFinder<?> opticfinder2 = opticfinder1.type().findField("BlockEntityTag");
      return this.fixTypeEverywhereTyped("ItemRemoveBlockEntityTagFix", type, (p_242866_) -> {
         Optional<Pair<String, String>> optional = p_242866_.getOptional(opticfinder);
         if (optional.isPresent() && this.items.contains(optional.get().getSecond())) {
            Optional<? extends Typed<?>> optional1 = p_242866_.getOptionalTyped(opticfinder1);
            if (optional1.isPresent()) {
               Typed<?> typed = optional1.get();
               Optional<? extends Typed<?>> optional2 = typed.getOptionalTyped(opticfinder2);
               if (optional2.isPresent()) {
                  Optional<? extends Dynamic<?>> optional3 = typed.write().result();
                  Dynamic<?> dynamic = optional3.isPresent() ? optional3.get() : typed.get(DSL.remainderFinder());
                  Dynamic<?> dynamic1 = dynamic.remove("BlockEntityTag");
                  Optional<? extends Pair<? extends Typed<?>, ?>> optional4 = opticfinder1.type().readTyped(dynamic1).result();
                  if (optional4.isEmpty()) {
                     return p_242866_;
                  }

                  return p_242866_.<Object, Object>set((OpticFinder<Object>)opticfinder1, (Typed<Object>)optional4.get().getFirst());
               }
            }
         }

         return p_242866_;
      });
   }
}