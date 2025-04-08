package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public abstract class ItemStackTagFix extends DataFix {
   private final String name;
   private final Predicate<String> idFilter;

   public ItemStackTagFix(Schema p_216682_, String p_216683_, Predicate<String> p_216684_) {
      super(p_216682_, false);
      this.name = p_216683_;
      this.idFilter = p_216684_;
   }

   public final TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder<?> opticfinder1 = type.findField("tag");
      return this.fixTypeEverywhereTyped(this.name, type, (p_216688_) -> {
         Optional<Pair<String, String>> optional = p_216688_.getOptional(opticfinder);
         return optional.isPresent() && this.idFilter.test(optional.get().getSecond()) ? p_216688_.updateTyped(opticfinder1, (p_216690_) -> {
            return p_216690_.update(DSL.remainderFinder(), this::fixItemStackTag);
         }) : p_216688_;
      });
   }

   protected abstract <T> Dynamic<T> fixItemStackTag(Dynamic<T> p_216691_);
}