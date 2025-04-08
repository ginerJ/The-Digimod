package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class OptionsAccessibilityOnboardFix extends DataFix {
   public OptionsAccessibilityOnboardFix(Schema p_265364_) {
      super(p_265364_, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsAccessibilityOnboardFix", this.getInputSchema().getType(References.OPTIONS), (p_265152_) -> {
         return p_265152_.update(DSL.remainderFinder(), (p_265786_) -> {
            return p_265786_.set("onboardAccessibility", p_265786_.createBoolean(false));
         });
      });
   }
}