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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class SingleItemRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final Item result;
   private final Ingredient ingredient;
   private final int count;
   private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
   @Nullable
   private String group;
   private final RecipeSerializer<?> type;

   public SingleItemRecipeBuilder(RecipeCategory p_251425_, RecipeSerializer<?> p_249762_, Ingredient p_251221_, ItemLike p_251302_, int p_250964_) {
      this.category = p_251425_;
      this.type = p_249762_;
      this.result = p_251302_.asItem();
      this.ingredient = p_251221_;
      this.count = p_250964_;
   }

   public static SingleItemRecipeBuilder stonecutting(Ingredient p_248596_, RecipeCategory p_250503_, ItemLike p_250269_) {
      return new SingleItemRecipeBuilder(p_250503_, RecipeSerializer.STONECUTTER, p_248596_, p_250269_, 1);
   }

   public static SingleItemRecipeBuilder stonecutting(Ingredient p_251375_, RecipeCategory p_248984_, ItemLike p_250105_, int p_249506_) {
      return new SingleItemRecipeBuilder(p_248984_, RecipeSerializer.STONECUTTER, p_251375_, p_250105_, p_249506_);
   }

   public SingleItemRecipeBuilder unlockedBy(String p_176810_, CriterionTriggerInstance p_176811_) {
      this.advancement.addCriterion(p_176810_, p_176811_);
      return this;
   }

   public SingleItemRecipeBuilder group(@Nullable String p_176808_) {
      this.group = p_176808_;
      return this;
   }

   public Item getResult() {
      return this.result;
   }

   public void save(Consumer<FinishedRecipe> p_126327_, ResourceLocation p_126328_) {
      this.ensureValid(p_126328_);
      this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(p_126328_)).rewards(AdvancementRewards.Builder.recipe(p_126328_)).requirements(RequirementsStrategy.OR);
      p_126327_.accept(new SingleItemRecipeBuilder.Result(p_126328_, this.type, this.group == null ? "" : this.group, this.ingredient, this.result, this.count, this.advancement, p_126328_.withPrefix("recipes/" + this.category.getFolderName() + "/")));
   }

   private void ensureValid(ResourceLocation p_126330_) {
      if (this.advancement.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + p_126330_);
      }
   }

   public static class Result implements FinishedRecipe {
      private final ResourceLocation id;
      private final String group;
      private final Ingredient ingredient;
      private final Item result;
      private final int count;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;
      private final RecipeSerializer<?> type;

      public Result(ResourceLocation p_126340_, RecipeSerializer<?> p_126341_, String p_126342_, Ingredient p_126343_, Item p_126344_, int p_126345_, Advancement.Builder p_126346_, ResourceLocation p_126347_) {
         this.id = p_126340_;
         this.type = p_126341_;
         this.group = p_126342_;
         this.ingredient = p_126343_;
         this.result = p_126344_;
         this.count = p_126345_;
         this.advancement = p_126346_;
         this.advancementId = p_126347_;
      }

      public void serializeRecipeData(JsonObject p_126349_) {
         if (!this.group.isEmpty()) {
            p_126349_.addProperty("group", this.group);
         }

         p_126349_.add("ingredient", this.ingredient.toJson());
         p_126349_.addProperty("result", BuiltInRegistries.ITEM.getKey(this.result).toString());
         p_126349_.addProperty("count", this.count);
      }

      public ResourceLocation getId() {
         return this.id;
      }

      public RecipeSerializer<?> getType() {
         return this.type;
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