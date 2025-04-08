package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface RecipeSerializer<T extends Recipe<?>> extends net.minecraftforge.common.extensions.IForgeRecipeSerializer<T> {
   RecipeSerializer<ShapedRecipe> SHAPED_RECIPE = register("crafting_shaped", new ShapedRecipe.Serializer());
   RecipeSerializer<ShapelessRecipe> SHAPELESS_RECIPE = register("crafting_shapeless", new ShapelessRecipe.Serializer());
   RecipeSerializer<ArmorDyeRecipe> ARMOR_DYE = register("crafting_special_armordye", new SimpleCraftingRecipeSerializer<>(ArmorDyeRecipe::new));
   RecipeSerializer<BookCloningRecipe> BOOK_CLONING = register("crafting_special_bookcloning", new SimpleCraftingRecipeSerializer<>(BookCloningRecipe::new));
   RecipeSerializer<MapCloningRecipe> MAP_CLONING = register("crafting_special_mapcloning", new SimpleCraftingRecipeSerializer<>(MapCloningRecipe::new));
   RecipeSerializer<MapExtendingRecipe> MAP_EXTENDING = register("crafting_special_mapextending", new SimpleCraftingRecipeSerializer<>(MapExtendingRecipe::new));
   RecipeSerializer<FireworkRocketRecipe> FIREWORK_ROCKET = register("crafting_special_firework_rocket", new SimpleCraftingRecipeSerializer<>(FireworkRocketRecipe::new));
   RecipeSerializer<FireworkStarRecipe> FIREWORK_STAR = register("crafting_special_firework_star", new SimpleCraftingRecipeSerializer<>(FireworkStarRecipe::new));
   RecipeSerializer<FireworkStarFadeRecipe> FIREWORK_STAR_FADE = register("crafting_special_firework_star_fade", new SimpleCraftingRecipeSerializer<>(FireworkStarFadeRecipe::new));
   RecipeSerializer<TippedArrowRecipe> TIPPED_ARROW = register("crafting_special_tippedarrow", new SimpleCraftingRecipeSerializer<>(TippedArrowRecipe::new));
   RecipeSerializer<BannerDuplicateRecipe> BANNER_DUPLICATE = register("crafting_special_bannerduplicate", new SimpleCraftingRecipeSerializer<>(BannerDuplicateRecipe::new));
   RecipeSerializer<ShieldDecorationRecipe> SHIELD_DECORATION = register("crafting_special_shielddecoration", new SimpleCraftingRecipeSerializer<>(ShieldDecorationRecipe::new));
   RecipeSerializer<ShulkerBoxColoring> SHULKER_BOX_COLORING = register("crafting_special_shulkerboxcoloring", new SimpleCraftingRecipeSerializer<>(ShulkerBoxColoring::new));
   RecipeSerializer<SuspiciousStewRecipe> SUSPICIOUS_STEW = register("crafting_special_suspiciousstew", new SimpleCraftingRecipeSerializer<>(SuspiciousStewRecipe::new));
   RecipeSerializer<RepairItemRecipe> REPAIR_ITEM = register("crafting_special_repairitem", new SimpleCraftingRecipeSerializer<>(RepairItemRecipe::new));
   RecipeSerializer<SmeltingRecipe> SMELTING_RECIPE = register("smelting", new SimpleCookingSerializer<>(SmeltingRecipe::new, 200));
   RecipeSerializer<BlastingRecipe> BLASTING_RECIPE = register("blasting", new SimpleCookingSerializer<>(BlastingRecipe::new, 100));
   RecipeSerializer<SmokingRecipe> SMOKING_RECIPE = register("smoking", new SimpleCookingSerializer<>(SmokingRecipe::new, 100));
   RecipeSerializer<CampfireCookingRecipe> CAMPFIRE_COOKING_RECIPE = register("campfire_cooking", new SimpleCookingSerializer<>(CampfireCookingRecipe::new, 100));
   RecipeSerializer<StonecutterRecipe> STONECUTTER = register("stonecutting", new SingleItemRecipe.Serializer<>(StonecutterRecipe::new));
   RecipeSerializer<SmithingTransformRecipe> SMITHING_TRANSFORM = register("smithing_transform", new SmithingTransformRecipe.Serializer());
   RecipeSerializer<SmithingTrimRecipe> SMITHING_TRIM = register("smithing_trim", new SmithingTrimRecipe.Serializer());
   RecipeSerializer<DecoratedPotRecipe> DECORATED_POT_RECIPE = register("crafting_decorated_pot", new SimpleCraftingRecipeSerializer<>(DecoratedPotRecipe::new));

   // Forge: use fromJson with IContext if you need the context
   T fromJson(ResourceLocation p_44103_, JsonObject p_44104_);

   @org.jetbrains.annotations.Nullable
   T fromNetwork(ResourceLocation p_44105_, FriendlyByteBuf p_44106_);

   void toNetwork(FriendlyByteBuf p_44101_, T p_44102_);

   static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String p_44099_, S p_44100_) {
      return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, p_44099_, p_44100_);
   }
}
