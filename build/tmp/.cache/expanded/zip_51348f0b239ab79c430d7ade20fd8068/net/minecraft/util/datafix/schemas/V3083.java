package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V3083 extends NamespacedSchema {
   public V3083(int p_216805_, Schema p_216806_) {
      super(p_216805_, p_216806_);
   }

   protected static void registerMob(Schema p_216810_, Map<String, Supplier<TypeTemplate>> p_216811_, String p_216812_) {
      p_216810_.register(p_216811_, p_216812_, () -> {
         return DSL.optionalFields("ArmorItems", DSL.list(References.ITEM_STACK.in(p_216810_)), "HandItems", DSL.list(References.ITEM_STACK.in(p_216810_)), "listener", DSL.optionalFields("event", DSL.optionalFields("game_event", References.GAME_EVENT_NAME.in(p_216810_))));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_216814_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_216814_);
      registerMob(p_216814_, map, "minecraft:allay");
      return map;
   }
}