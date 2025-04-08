package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import java.util.stream.Stream;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SmithingTransformRecipe implements SmithingRecipe {
   private final ResourceLocation id;
   final Ingredient template;
   final Ingredient base;
   final Ingredient addition;
   final ItemStack result;

   public SmithingTransformRecipe(ResourceLocation p_267143_, Ingredient p_266750_, Ingredient p_266787_, Ingredient p_267292_, ItemStack p_267031_) {
      this.id = p_267143_;
      this.template = p_266750_;
      this.base = p_266787_;
      this.addition = p_267292_;
      this.result = p_267031_;
   }

   public boolean matches(Container p_266855_, Level p_266781_) {
      return this.template.test(p_266855_.getItem(0)) && this.base.test(p_266855_.getItem(1)) && this.addition.test(p_266855_.getItem(2));
   }

   public ItemStack assemble(Container p_267036_, RegistryAccess p_266699_) {
      ItemStack itemstack = this.result.copy();
      CompoundTag compoundtag = p_267036_.getItem(1).getTag();
      if (compoundtag != null) {
         itemstack.setTag(compoundtag.copy());
      }

      return itemstack;
   }

   public ItemStack getResultItem(RegistryAccess p_267209_) {
      return this.result;
   }

   public boolean isTemplateIngredient(ItemStack p_267113_) {
      return this.template.test(p_267113_);
   }

   public boolean isBaseIngredient(ItemStack p_267276_) {
      return this.base.test(p_267276_);
   }

   public boolean isAdditionIngredient(ItemStack p_267260_) {
      return this.addition.test(p_267260_);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SMITHING_TRANSFORM;
   }

   public boolean isIncomplete() {
      return Stream.of(this.template, this.base, this.addition).anyMatch(net.minecraftforge.common.ForgeHooks::hasNoElements);
   }

   public static class Serializer implements RecipeSerializer<SmithingTransformRecipe> {
      public SmithingTransformRecipe fromJson(ResourceLocation p_266953_, JsonObject p_266720_) {
         Ingredient ingredient = Ingredient.fromJson(GsonHelper.getNonNull(p_266720_, "template"));
         Ingredient ingredient1 = Ingredient.fromJson(GsonHelper.getNonNull(p_266720_, "base"));
         Ingredient ingredient2 = Ingredient.fromJson(GsonHelper.getNonNull(p_266720_, "addition"));
         ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(p_266720_, "result"));
         return new SmithingTransformRecipe(p_266953_, ingredient, ingredient1, ingredient2, itemstack);
      }

      public SmithingTransformRecipe fromNetwork(ResourceLocation p_267117_, FriendlyByteBuf p_267316_) {
         Ingredient ingredient = Ingredient.fromNetwork(p_267316_);
         Ingredient ingredient1 = Ingredient.fromNetwork(p_267316_);
         Ingredient ingredient2 = Ingredient.fromNetwork(p_267316_);
         ItemStack itemstack = p_267316_.readItem();
         return new SmithingTransformRecipe(p_267117_, ingredient, ingredient1, ingredient2, itemstack);
      }

      public void toNetwork(FriendlyByteBuf p_266746_, SmithingTransformRecipe p_266927_) {
         p_266927_.template.toNetwork(p_266746_);
         p_266927_.base.toNetwork(p_266746_);
         p_266927_.addition.toNetwork(p_266746_);
         p_266746_.writeItem(p_266927_.result);
      }
   }
}
