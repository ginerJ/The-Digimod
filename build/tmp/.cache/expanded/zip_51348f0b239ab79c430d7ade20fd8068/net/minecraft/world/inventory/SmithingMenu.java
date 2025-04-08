package net.minecraft.world.inventory;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SmithingMenu extends ItemCombinerMenu {
   public static final int TEMPLATE_SLOT = 0;
   public static final int BASE_SLOT = 1;
   public static final int ADDITIONAL_SLOT = 2;
   public static final int RESULT_SLOT = 3;
   public static final int TEMPLATE_SLOT_X_PLACEMENT = 8;
   public static final int BASE_SLOT_X_PLACEMENT = 26;
   public static final int ADDITIONAL_SLOT_X_PLACEMENT = 44;
   private static final int RESULT_SLOT_X_PLACEMENT = 98;
   public static final int SLOT_Y_PLACEMENT = 48;
   private final Level level;
   @Nullable
   private SmithingRecipe selectedRecipe;
   private final List<SmithingRecipe> recipes;

   public SmithingMenu(int p_40245_, Inventory p_40246_) {
      this(p_40245_, p_40246_, ContainerLevelAccess.NULL);
   }

   public SmithingMenu(int p_40248_, Inventory p_40249_, ContainerLevelAccess p_40250_) {
      super(MenuType.SMITHING, p_40248_, p_40249_, p_40250_);
      this.level = p_40249_.player.level();
      this.recipes = this.level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);
   }

   protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
      return ItemCombinerMenuSlotDefinition.create().withSlot(0, 8, 48, (p_266643_) -> {
         return this.recipes.stream().anyMatch((p_266642_) -> {
            return p_266642_.isTemplateIngredient(p_266643_);
         });
      }).withSlot(1, 26, 48, (p_286208_) -> {
         return this.recipes.stream().anyMatch((p_286206_) -> {
            return p_286206_.isBaseIngredient(p_286208_);
         });
      }).withSlot(2, 44, 48, (p_286207_) -> {
         return this.recipes.stream().anyMatch((p_286204_) -> {
            return p_286204_.isAdditionIngredient(p_286207_);
         });
      }).withResultSlot(3, 98, 48).build();
   }

   protected boolean isValidBlock(BlockState p_40266_) {
      return p_40266_.is(Blocks.SMITHING_TABLE);
   }

   protected boolean mayPickup(Player p_40268_, boolean p_40269_) {
      return this.selectedRecipe != null && this.selectedRecipe.matches(this.inputSlots, this.level);
   }

   protected void onTake(Player p_150663_, ItemStack p_150664_) {
      p_150664_.onCraftedBy(p_150663_.level(), p_150663_, p_150664_.getCount());
      this.resultSlots.awardUsedRecipes(p_150663_, this.getRelevantItems());
      this.shrinkStackInSlot(0);
      this.shrinkStackInSlot(1);
      this.shrinkStackInSlot(2);
      this.access.execute((p_40263_, p_40264_) -> {
         p_40263_.levelEvent(1044, p_40264_, 0);
      });
   }

   private List<ItemStack> getRelevantItems() {
      return List.of(this.inputSlots.getItem(0), this.inputSlots.getItem(1), this.inputSlots.getItem(2));
   }

   private void shrinkStackInSlot(int p_40271_) {
      ItemStack itemstack = this.inputSlots.getItem(p_40271_);
      if (!itemstack.isEmpty()) {
         itemstack.shrink(1);
         this.inputSlots.setItem(p_40271_, itemstack);
      }

   }

   public void createResult() {
      List<SmithingRecipe> list = this.level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, this.inputSlots, this.level);
      if (list.isEmpty()) {
         this.resultSlots.setItem(0, ItemStack.EMPTY);
      } else {
         SmithingRecipe smithingrecipe = list.get(0);
         ItemStack itemstack = smithingrecipe.assemble(this.inputSlots, this.level.registryAccess());
         if (itemstack.isItemEnabled(this.level.enabledFeatures())) {
            this.selectedRecipe = smithingrecipe;
            this.resultSlots.setRecipeUsed(smithingrecipe);
            this.resultSlots.setItem(0, itemstack);
         }
      }

   }

   public int getSlotToQuickMoveTo(ItemStack p_266739_) {
      return this.recipes.stream().map((p_266640_) -> {
         return findSlotMatchingIngredient(p_266640_, p_266739_);
      }).filter(Optional::isPresent).findFirst().orElse(Optional.of(0)).get();
   }

   private static Optional<Integer> findSlotMatchingIngredient(SmithingRecipe p_266790_, ItemStack p_266818_) {
      if (p_266790_.isTemplateIngredient(p_266818_)) {
         return Optional.of(0);
      } else if (p_266790_.isBaseIngredient(p_266818_)) {
         return Optional.of(1);
      } else {
         return p_266790_.isAdditionIngredient(p_266818_) ? Optional.of(2) : Optional.empty();
      }
   }

   public boolean canTakeItemForPickAll(ItemStack p_40257_, Slot p_40258_) {
      return p_40258_.container != this.resultSlots && super.canTakeItemForPickAll(p_40257_, p_40258_);
   }

   public boolean canMoveIntoInputSlots(ItemStack p_266846_) {
      return this.recipes.stream().map((p_266647_) -> {
         return findSlotMatchingIngredient(p_266647_, p_266846_);
      }).anyMatch(Optional::isPresent);
   }
}