package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FeatureFlagRemoveFix extends DataFix {
   private final String name;
   private final Set<String> flagsToRemove;

   public FeatureFlagRemoveFix(Schema p_277930_, String p_277628_, Set<String> p_277886_) {
      super(p_277930_, false);
      this.name = p_277628_;
      this.flagsToRemove = p_277886_;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(References.LEVEL), (p_277407_) -> {
         return p_277407_.update(DSL.remainderFinder(), this::fixTag);
      });
   }

   private <T> Dynamic<T> fixTag(Dynamic<T> p_277583_) {
      List<Dynamic<T>> list = p_277583_.get("removed_features").asStream().collect(Collectors.toCollection(ArrayList::new));
      Dynamic<T> dynamic = p_277583_.update("enabled_features", (p_278098_) -> {
         return DataFixUtils.orElse(p_278098_.asStreamOpt().result().map((p_277400_) -> {
            return p_277400_.filter((p_277338_) -> {
               Optional<String> optional = p_277338_.asString().result();
               if (optional.isEmpty()) {
                  return true;
               } else {
                  boolean flag = this.flagsToRemove.contains(optional.get());
                  if (flag) {
                     list.add(p_277583_.createString(optional.get()));
                  }

                  return !flag;
               }
            });
         }).map(p_277583_::createList), p_278098_);
      });
      if (!list.isEmpty()) {
         dynamic = dynamic.set("removed_features", p_277583_.createList(list.stream()));
      }

      return dynamic;
   }
}