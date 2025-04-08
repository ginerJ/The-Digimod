package net.minecraft.world.item.crafting;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public interface RecipeType<T extends Recipe<?>> {
   RecipeType<CraftingRecipe> CRAFTING = register("crafting");
   RecipeType<SmeltingRecipe> SMELTING = register("smelting");
   RecipeType<BlastingRecipe> BLASTING = register("blasting");
   RecipeType<SmokingRecipe> SMOKING = register("smoking");
   RecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING = register("campfire_cooking");
   RecipeType<StonecutterRecipe> STONECUTTING = register("stonecutting");
   RecipeType<SmithingRecipe> SMITHING = register("smithing");

   static <T extends Recipe<?>> RecipeType<T> register(final String p_44120_) {
      return Registry.register(BuiltInRegistries.RECIPE_TYPE, new ResourceLocation(p_44120_), new RecipeType<T>() {
         public String toString() {
            return p_44120_;
         }
      });
   }

   public static <T extends Recipe<?>> RecipeType<T> simple(final ResourceLocation name) {
      final String toString = name.toString();
      return new RecipeType<T>() {
         @Override
         public String toString() {
            return toString;
         }
      };
   }
}
