package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class OminousBannerRenameFix extends ItemStackTagFix {
   public OminousBannerRenameFix(Schema p_216694_) {
      super(p_216694_, "OminousBannerRenameFix", (p_216698_) -> {
         return p_216698_.equals("minecraft:white_banner");
      });
   }

   protected <T> Dynamic<T> fixItemStackTag(Dynamic<T> p_216696_) {
      Optional<? extends Dynamic<?>> optional = p_216696_.get("display").result();
      if (optional.isPresent()) {
         Dynamic<?> dynamic = optional.get();
         Optional<String> optional1 = dynamic.get("Name").asString().result();
         if (optional1.isPresent()) {
            String s = optional1.get();
            s = s.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
            dynamic = dynamic.set("Name", dynamic.createString(s));
         }

         return p_216696_.set("display", dynamic);
      } else {
         return p_216696_;
      }
   }
}