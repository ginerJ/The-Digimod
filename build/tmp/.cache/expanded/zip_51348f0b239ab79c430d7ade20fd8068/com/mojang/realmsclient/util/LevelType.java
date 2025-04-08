package com.mojang.realmsclient.util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum LevelType {
   DEFAULT(0, WorldPresets.NORMAL),
   FLAT(1, WorldPresets.FLAT),
   LARGE_BIOMES(2, WorldPresets.LARGE_BIOMES),
   AMPLIFIED(3, WorldPresets.AMPLIFIED);

   private final int index;
   private final Component name;

   private LevelType(int p_239483_, ResourceKey<WorldPreset> p_239484_) {
      this.index = p_239483_;
      this.name = Component.translatable(p_239484_.location().toLanguageKey("generator"));
   }

   public Component getName() {
      return this.name;
   }

   public int getDtoIndex() {
      return this.index;
   }
}