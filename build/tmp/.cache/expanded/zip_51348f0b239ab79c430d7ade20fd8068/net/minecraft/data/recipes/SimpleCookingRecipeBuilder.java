package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class SimpleCookingRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final CookingBookCategory bookCategory;
   private final Item result;
   private final Ingredient ingredient;
   private final float experience;
   private final int cookingTime;
   private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
   @Nullable
   private String group;
   private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

   private SimpleCookingRecipeBuilder(RecipeCategory p_251345_, CookingBookCategory p_251607_, ItemLike p_252112_, Ingredient p_250362_, float p_251204_, int p_250189_, RecipeSerializer<? extends AbstractCookingRecipe> p_249915_) {
      this.category = p_251345_;
      this.bookCategory = p_251607_;
      this.result = p_252112_.asItem();
      this.ingredient = p_250362_;
      this.experience = p_251204_;
      this.cookingTime = p_250189_;
      this.serializer = p_249915_;
   }

   public static SimpleCookingRecipeBuilder generic(Ingredient p_250999_, RecipeCategory p_248815_, ItemLike p_249766_, float p_251320_, int p_248693_, RecipeSerializer<? extends AbstractCookingRecipe> p_250921_) {
      return new SimpleCookingRecipeBuilder(p_248815_, determineRecipeCategory(p_250921_, p_249766_), p_249766_, p_250999_, p_251320_, p_248693_, p_250921_);
   }

   public static SimpleCookingRecipeBuilder campfireCooking(Ingredient p_249393_, RecipeCategory p_249372_, ItemLike p_251516_, float p_252321_, int p_251916_) {
      return new SimpleCookingRecipeBuilder(p_249372_, CookingBookCategory.FOOD, p_251516_, p_249393_, p_252321_, p_251916_, RecipeSerializer.CAMPFIRE_COOKING_RECIPE);
   }

   public static SimpleCookingRecipeBuilder blasting(Ingredient p_252115_, RecipeCategory p_249421_, ItemLike p_251247_, float p_250383_, int p_250476_) {
      return new SimpleCookingRecipeBuilder(p_249421_, determineBlastingRecipeCategory(p_251247_), p_251247_, p_252115_, p_250383_, p_250476_, RecipeSerializer.BLASTING_RECIPE);
   }

   public static SimpleCookingRecipeBuilder smelting(Ingredient p_249223_, RecipeCategory p_251240_, ItemLike p_249551_, float p_249452_, int p_250496_) {
      return new SimpleCookingRecipeBuilder(p_251240_, determineSmeltingRecipeCategory(p_249551_), p_249551_, p_249223_, p_249452_, p_250496_, RecipeSerializer.SMELTING_RECIPE);
   }

   public static SimpleCookingRecipeBuilder smoking(Ingredient p_248930_, RecipeCategory p_250319_, ItemLike p_250377_, float p_252329_, int p_250482_) {
      return new SimpleCookingRecipeBuilder(p_250319_, CookingBookCategory.FOOD, p_250377_, p_248930_, p_252329_, p_250482_, RecipeSerializer.SMOKING_RECIPE);
   }

   public SimpleCookingRecipeBuilder unlockedBy(String p_126255_, CriterionTriggerInstance p_126256_) {
      this.advancement.addCriterion(p_126255_, p_126256_);
      return this;
   }

   public SimpleCookingRecipeBuilder group(@Nullable String p_176795_) {
      this.group = p_176795_;
      return this;
   }

   public Item getResult() {
      return this.result;
   }

   public void save(Consumer<FinishedRecipe> p_126263_, ResourceLocation p_126264_) {
      this.ensureValid(p_126264_);
      this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(p_126264_)).rewards(AdvancementRewards.Builder.recipe(p_126264_)).requirements(RequirementsStrategy.OR);
      p_126263_.accept(new SimpleCookingRecipeBuilder.Result(p_126264_, this.group == null ? "" : this.group, this.bookCategory, this.ingredient, this.result, this.experience, this.cookingTime, this.advancement, p_126264_.withPrefix("recipes/" + this.category.getFolderName() + "/"), this.serializer));
   }

   private static CookingBookCategory determineSmeltingRecipeCategory(ItemLike p_251938_) {
      if (p_251938_.asItem().isEdible()) {
         return CookingBookCategory.FOOD;
      } else {
         return p_251938_.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
      }
   }

   private static CookingBookCategory determineBlastingRecipeCategory(ItemLike p_249047_) {
      return p_249047_.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
   }

   private static CookingBookCategory determineRecipeCategory(RecipeSerializer<? extends AbstractCookingRecipe> p_251261_, ItemLike p_249582_) {
      if (p_251261_ == RecipeSerializer.SMELTING_RECIPE) {
         return determineSmeltingRecipeCategory(p_249582_);
      } else if (p_251261_ == RecipeSerializer.BLASTING_RECIPE) {
         return determineBlastingRecipeCategory(p_249582_);
      } else if (p_251261_ != RecipeSerializer.SMOKING_RECIPE && p_251261_ != RecipeSerializer.CAMPFIRE_COOKING_RECIPE) {
         throw new IllegalStateException("Unknown cooking recipe type");
      } else {
         return CookingBookCategory.FOOD;
      }
   }

   private void ensureValid(ResourceLocation p_126266_) {
      if (this.advancement.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + p_126266_);
      }
   }

   static class Result implements FinishedRecipe {
      private final ResourceLocation id;
      private final String group;
      private final CookingBookCategory category;
      private final Ingredient ingredient;
      private final Item result;
      private final float experience;
      private final int cookingTime;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;
      private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

      public Result(ResourceLocation p_252275_, String p_248665_, CookingBookCategory p_251944_, Ingredient p_249473_, Item p_252028_, float p_249206_, int p_251002_, Advancement.Builder p_249151_, ResourceLocation p_252090_, RecipeSerializer<? extends AbstractCookingRecipe> p_249537_) {
         this.id = p_252275_;
         this.group = p_248665_;
         this.category = p_251944_;
         this.ingredient = p_249473_;
         this.result = p_252028_;
         this.experience = p_249206_;
         this.cookingTime = p_251002_;
         this.advancement = p_249151_;
         this.advancementId = p_252090_;
         this.serializer = p_249537_;
      }

      public void serializeRecipeData(JsonObject p_126297_) {
         if (!this.group.isEmpty()) {
            p_126297_.addProperty("group", this.group);
         }

         p_126297_.addProperty("category", this.category.getSerializedName());
         p_126297_.add("ingredient", this.ingredient.toJson());
         p_126297_.addProperty("result", BuiltInRegistries.ITEM.getKey(this.result).toString());
         p_126297_.addProperty("experience", this.experience);
         p_126297_.addProperty("cookingtime", this.cookingTime);
      }

      public RecipeSerializer<?> getType() {
         return this.serializer;
      }

      public ResourceLocation getId() {
         return this.id;
      }

      @Nullable
      public JsonObject serializeAdvancement() {
         return this.advancement.serializeToJson();
      }

      @Nullable
      public ResourceLocation getAdvancementId() {
         return this.advancementId;
      }
   }
}