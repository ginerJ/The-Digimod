package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class FilteredBooksFix extends ItemStackTagFix {
   public FilteredBooksFix(Schema p_216660_) {
      super(p_216660_, "Remove filtered text from books", (p_216664_) -> {
         return p_216664_.equals("minecraft:writable_book") || p_216664_.equals("minecraft:written_book");
      });
   }

   protected <T> Dynamic<T> fixItemStackTag(Dynamic<T> p_216662_) {
      return p_216662_.remove("filtered_title").remove("filtered_pages");
   }
}