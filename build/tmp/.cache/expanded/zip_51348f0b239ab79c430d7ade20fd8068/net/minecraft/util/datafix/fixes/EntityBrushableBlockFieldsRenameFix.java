package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class EntityBrushableBlockFieldsRenameFix extends NamedEntityFix {
   public EntityBrushableBlockFieldsRenameFix(Schema p_278044_) {
      super(p_278044_, false, "EntityBrushableBlockFieldsRenameFix", References.BLOCK_ENTITY, "minecraft:brushable_block");
   }

   public Dynamic<?> fixTag(Dynamic<?> p_277830_) {
      return this.renameField(this.renameField(p_277830_, "loot_table", "LootTable"), "loot_table_seed", "LootTableSeed");
   }

   private Dynamic<?> renameField(Dynamic<?> p_277783_, String p_277566_, String p_277732_) {
      Optional<? extends Dynamic<?>> optional = p_277783_.get(p_277566_).result();
      Optional<? extends Dynamic<?>> optional1 = optional.map((p_277534_) -> {
         return p_277783_.remove(p_277566_).set(p_277732_, p_277534_);
      });
      return DataFixUtils.orElse(optional1, p_277783_);
   }

   protected Typed<?> fix(Typed<?> p_277791_) {
      return p_277791_.update(DSL.remainderFinder(), this::fixTag);
   }
}