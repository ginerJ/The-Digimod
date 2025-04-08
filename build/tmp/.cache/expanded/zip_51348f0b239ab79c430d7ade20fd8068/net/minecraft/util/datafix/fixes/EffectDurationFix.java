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

public class EffectDurationFix extends DataFix {
   private static final Set<String> ITEM_TYPES = Set.of("minecraft:potion", "minecraft:splash_potion", "minecraft:lingering_potion", "minecraft:tipped_arrow");

   public EffectDurationFix(Schema p_267976_) {
      super(p_267976_, false);
   }

   protected TypeRewriteRule makeRule() {
      Schema schema = this.getInputSchema();
      Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder<?> opticfinder1 = type.findField("tag");
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("EffectDurationEntity", schema.getType(References.ENTITY), (p_268118_) -> {
         return p_268118_.update(DSL.remainderFinder(), this::updateEntity);
      }), this.fixTypeEverywhereTyped("EffectDurationPlayer", schema.getType(References.PLAYER), (p_268326_) -> {
         return p_268326_.update(DSL.remainderFinder(), this::updateEntity);
      }), this.fixTypeEverywhereTyped("EffectDurationItem", type, (p_268235_) -> {
         Optional<Pair<String, String>> optional = p_268235_.getOptional(opticfinder);
         if (optional.filter(ITEM_TYPES::contains).isPresent()) {
            Optional<? extends Typed<?>> optional1 = p_268235_.getOptionalTyped(opticfinder1);
            if (optional1.isPresent()) {
               Dynamic<?> dynamic = optional1.get().get(DSL.remainderFinder());
               Typed<?> typed = optional1.get().set(DSL.remainderFinder(), dynamic.update("CustomPotionEffects", this::fix));
               return p_268235_.set(opticfinder1, typed);
            }
         }

         return p_268235_;
      }));
   }

   private Dynamic<?> fixEffect(Dynamic<?> p_267989_) {
      return p_267989_.update("FactorCalculationData", (p_268051_) -> {
         int i = p_268051_.get("effect_changed_timestamp").asInt(-1);
         p_268051_ = p_268051_.remove("effect_changed_timestamp");
         int j = p_267989_.get("Duration").asInt(-1);
         int k = i - j;
         return p_268051_.set("ticks_active", p_268051_.createInt(k));
      });
   }

   private Dynamic<?> fix(Dynamic<?> p_268201_) {
      return p_268201_.createList(p_268201_.asStream().map(this::fixEffect));
   }

   private Dynamic<?> updateEntity(Dynamic<?> p_268005_) {
      p_268005_ = p_268005_.update("Effects", this::fix);
      p_268005_ = p_268005_.update("ActiveEffects", this::fix);
      return p_268005_.update("CustomPotionEffects", this::fix);
   }
}