package net.minecraft.data.recipes.packs;

import java.util.function.Consumer;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

public class BundleRecipeProvider extends RecipeProvider {
   public BundleRecipeProvider(PackOutput p_248813_) {
      super(p_248813_);
   }

   protected void buildRecipes(Consumer<FinishedRecipe> p_250665_) {
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.BUNDLE).define('#', Items.RABBIT_HIDE).define('-', Items.STRING).pattern("-#-").pattern("# #").pattern("###").unlockedBy("has_string", has(Items.STRING)).save(p_250665_);
   }
}