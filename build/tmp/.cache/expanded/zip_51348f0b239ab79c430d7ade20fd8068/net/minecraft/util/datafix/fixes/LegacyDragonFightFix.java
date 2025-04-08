package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;

public class LegacyDragonFightFix extends DataFix {
   public LegacyDragonFightFix(Schema p_289761_) {
      super(p_289761_, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("LegacyDragonFightFix", this.getInputSchema().getType(References.LEVEL), (p_289787_) -> {
         return p_289787_.update(DSL.remainderFinder(), (p_289763_) -> {
            OptionalDynamic<?> optionaldynamic = p_289763_.get("DragonFight");
            if (optionaldynamic.result().isPresent()) {
               return p_289763_;
            } else {
               Dynamic<?> dynamic = p_289763_.get("DimensionData").get("1").get("DragonFight").orElseEmptyMap();
               return p_289763_.set("DragonFight", dynamic);
            }
         });
      });
   }
}