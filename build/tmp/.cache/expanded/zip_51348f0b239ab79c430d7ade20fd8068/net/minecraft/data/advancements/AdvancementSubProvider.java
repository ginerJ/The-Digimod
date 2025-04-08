package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public interface AdvancementSubProvider {
   static Advancement createPlaceholder(String p_267076_) {
      return Advancement.Builder.advancement().build(new ResourceLocation(p_267076_));
   }

   void generate(HolderLookup.Provider p_255901_, Consumer<Advancement> p_250888_);
}