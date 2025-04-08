package net.minecraft.world.item.crafting;

import net.minecraft.util.StringRepresentable;

public enum CraftingBookCategory implements StringRepresentable {
   BUILDING("building"),
   REDSTONE("redstone"),
   EQUIPMENT("equipment"),
   MISC("misc");

   public static final StringRepresentable.EnumCodec<CraftingBookCategory> CODEC = StringRepresentable.fromEnum(CraftingBookCategory::values);
   private final String name;

   private CraftingBookCategory(String p_249346_) {
      this.name = p_249346_;
   }

   public String getSerializedName() {
      return this.name;
   }
}