package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.UnaryOperator;

public class CriteriaRenameFix extends DataFix {
   private final String name;
   private final String advancementId;
   private final UnaryOperator<String> conversions;

   public CriteriaRenameFix(Schema p_216585_, String p_216586_, String p_216587_, UnaryOperator<String> p_216588_) {
      super(p_216585_, false);
      this.name = p_216586_;
      this.advancementId = p_216587_;
      this.conversions = p_216588_;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(References.ADVANCEMENTS), (p_216590_) -> {
         return p_216590_.update(DSL.remainderFinder(), this::fixAdvancements);
      });
   }

   private Dynamic<?> fixAdvancements(Dynamic<?> p_216594_) {
      return p_216594_.update(this.advancementId, (p_216599_) -> {
         return p_216599_.update("criteria", (p_216601_) -> {
            return p_216601_.updateMapValues((p_216592_) -> {
               return p_216592_.mapFirst((p_216603_) -> {
                  return DataFixUtils.orElse(p_216603_.asString().map((p_216597_) -> {
                     return p_216603_.createString(this.conversions.apply(p_216597_));
                  }).result(), p_216603_);
               });
            });
         });
      });
   }
}