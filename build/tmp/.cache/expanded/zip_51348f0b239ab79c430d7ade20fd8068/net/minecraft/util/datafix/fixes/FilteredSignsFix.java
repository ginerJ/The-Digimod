package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class FilteredSignsFix extends NamedEntityFix {
   public FilteredSignsFix(Schema p_216666_) {
      super(p_216666_, false, "Remove filtered text from signs", References.BLOCK_ENTITY, "minecraft:sign");
   }

   protected Typed<?> fix(Typed<?> p_216668_) {
      return p_216668_.update(DSL.remainderFinder(), (p_216670_) -> {
         return p_216670_.remove("FilteredText1").remove("FilteredText2").remove("FilteredText3").remove("FilteredText4");
      });
   }
}