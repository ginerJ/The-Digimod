package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice;
import java.util.function.UnaryOperator;

public class BlockEntityRenameFix extends DataFix {
   private final String name;
   private final UnaryOperator<String> nameChangeLookup;

   private BlockEntityRenameFix(Schema p_277450_, String p_278025_, UnaryOperator<String> p_277596_) {
      super(p_277450_, true);
      this.name = p_278025_;
      this.nameChangeLookup = p_277596_;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoice.TaggedChoiceType<String> taggedchoicetype = (TaggedChoice.TaggedChoiceType<String>)this.getInputSchema().findChoiceType(References.BLOCK_ENTITY);
      TaggedChoice.TaggedChoiceType<String> taggedchoicetype1 = (TaggedChoice.TaggedChoiceType<String>)this.getOutputSchema().findChoiceType(References.BLOCK_ENTITY);
      return this.fixTypeEverywhere(this.name, taggedchoicetype, taggedchoicetype1, (p_277946_) -> {
         return (p_277512_) -> {
            return p_277512_.mapFirst(this.nameChangeLookup);
         };
      });
   }

   public static DataFix create(Schema p_278009_, String p_277879_, UnaryOperator<String> p_277753_) {
      return new BlockEntityRenameFix(p_278009_, p_277879_, p_277753_);
   }
}