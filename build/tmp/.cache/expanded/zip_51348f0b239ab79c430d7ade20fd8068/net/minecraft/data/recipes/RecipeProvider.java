package net.minecraft.data.recipes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public abstract class RecipeProvider implements DataProvider {
   protected final PackOutput.PathProvider recipePathProvider;
   protected final PackOutput.PathProvider advancementPathProvider;
   private static final Map<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>> SHAPE_BUILDERS = ImmutableMap.<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>>builder().put(BlockFamily.Variant.BUTTON, (p_176733_, p_176734_) -> {
      return buttonBuilder(p_176733_, Ingredient.of(p_176734_));
   }).put(BlockFamily.Variant.CHISELED, (p_248037_, p_248038_) -> {
      return chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, p_248037_, Ingredient.of(p_248038_));
   }).put(BlockFamily.Variant.CUT, (p_248026_, p_248027_) -> {
      return cutBuilder(RecipeCategory.BUILDING_BLOCKS, p_248026_, Ingredient.of(p_248027_));
   }).put(BlockFamily.Variant.DOOR, (p_176714_, p_176715_) -> {
      return doorBuilder(p_176714_, Ingredient.of(p_176715_));
   }).put(BlockFamily.Variant.CUSTOM_FENCE, (p_176708_, p_176709_) -> {
      return fenceBuilder(p_176708_, Ingredient.of(p_176709_));
   }).put(BlockFamily.Variant.FENCE, (p_248031_, p_248032_) -> {
      return fenceBuilder(p_248031_, Ingredient.of(p_248032_));
   }).put(BlockFamily.Variant.CUSTOM_FENCE_GATE, (p_176698_, p_176699_) -> {
      return fenceGateBuilder(p_176698_, Ingredient.of(p_176699_));
   }).put(BlockFamily.Variant.FENCE_GATE, (p_248035_, p_248036_) -> {
      return fenceGateBuilder(p_248035_, Ingredient.of(p_248036_));
   }).put(BlockFamily.Variant.SIGN, (p_176688_, p_176689_) -> {
      return signBuilder(p_176688_, Ingredient.of(p_176689_));
   }).put(BlockFamily.Variant.SLAB, (p_248017_, p_248018_) -> {
      return slabBuilder(RecipeCategory.BUILDING_BLOCKS, p_248017_, Ingredient.of(p_248018_));
   }).put(BlockFamily.Variant.STAIRS, (p_176674_, p_176675_) -> {
      return stairBuilder(p_176674_, Ingredient.of(p_176675_));
   }).put(BlockFamily.Variant.PRESSURE_PLATE, (p_248039_, p_248040_) -> {
      return pressurePlateBuilder(RecipeCategory.REDSTONE, p_248039_, Ingredient.of(p_248040_));
   }).put(BlockFamily.Variant.POLISHED, (p_248019_, p_248020_) -> {
      return polishedBuilder(RecipeCategory.BUILDING_BLOCKS, p_248019_, Ingredient.of(p_248020_));
   }).put(BlockFamily.Variant.TRAPDOOR, (p_176638_, p_176639_) -> {
      return trapdoorBuilder(p_176638_, Ingredient.of(p_176639_));
   }).put(BlockFamily.Variant.WALL, (p_248024_, p_248025_) -> {
      return wallBuilder(RecipeCategory.DECORATIONS, p_248024_, Ingredient.of(p_248025_));
   }).build();

   public RecipeProvider(PackOutput p_248933_) {
      this.recipePathProvider = p_248933_.createPathProvider(PackOutput.Target.DATA_PACK, "recipes");
      this.advancementPathProvider = p_248933_.createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
   }

   public CompletableFuture<?> run(CachedOutput p_254020_) {
      Set<ResourceLocation> set = Sets.newHashSet();
      List<CompletableFuture<?>> list = new ArrayList<>();
      this.buildRecipes((p_253413_) -> {
         if (!set.add(p_253413_.getId())) {
            throw new IllegalStateException("Duplicate recipe " + p_253413_.getId());
         } else {
            list.add(DataProvider.saveStable(p_254020_, p_253413_.serializeRecipe(), this.recipePathProvider.json(p_253413_.getId())));
            JsonObject jsonobject = p_253413_.serializeAdvancement();
            if (jsonobject != null) {
               var saveAdvancementFuture = saveAdvancement(p_254020_, p_253413_, jsonobject);
               if (saveAdvancementFuture != null)
                  list.add(saveAdvancementFuture);
            }

         }
      });
      return CompletableFuture.allOf(list.toArray((p_253414_) -> {
         return new CompletableFuture[p_253414_];
      }));
   }

   /**
    * Called every time a recipe is saved to also save the advancement JSON if it exists.
    *
    * @return A completable future that saves the advancement to disk, or null to cancel saving the advancement.
    */
   @org.jetbrains.annotations.Nullable
   protected CompletableFuture<?> saveAdvancement(CachedOutput output, FinishedRecipe finishedRecipe, JsonObject advancementJson) {
      return DataProvider.saveStable(output, advancementJson, this.advancementPathProvider.json(finishedRecipe.getAdvancementId()));
   }

   protected CompletableFuture<?> buildAdvancement(CachedOutput p_253674_, ResourceLocation p_254102_, Advancement.Builder p_253712_) {
      return DataProvider.saveStable(p_253674_, p_253712_.serializeToJson(), this.advancementPathProvider.json(p_254102_));
   }

   protected abstract void buildRecipes(Consumer<FinishedRecipe> p_251297_);

   protected void generateForEnabledBlockFamilies(Consumer<FinishedRecipe> p_249188_, FeatureFlagSet p_251836_) {
      BlockFamilies.getAllFamilies().filter((p_248034_) -> {
         return p_248034_.shouldGenerateRecipe(p_251836_);
      }).forEach((p_176624_) -> {
         generateRecipes(p_249188_, p_176624_);
      });
   }

   protected static void oneToOneConversionRecipe(Consumer<FinishedRecipe> p_176552_, ItemLike p_176553_, ItemLike p_176554_, @Nullable String p_176555_) {
      oneToOneConversionRecipe(p_176552_, p_176553_, p_176554_, p_176555_, 1);
   }

   protected static void oneToOneConversionRecipe(Consumer<FinishedRecipe> p_176557_, ItemLike p_176558_, ItemLike p_176559_, @Nullable String p_176560_, int p_176561_) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, p_176558_, p_176561_).requires(p_176559_).group(p_176560_).unlockedBy(getHasName(p_176559_), has(p_176559_)).save(p_176557_, getConversionRecipeName(p_176558_, p_176559_));
   }

   protected static void oreSmelting(Consumer<FinishedRecipe> p_250654_, List<ItemLike> p_250172_, RecipeCategory p_250588_, ItemLike p_251868_, float p_250789_, int p_252144_, String p_251687_) {
      oreCooking(p_250654_, RecipeSerializer.SMELTING_RECIPE, p_250172_, p_250588_, p_251868_, p_250789_, p_252144_, p_251687_, "_from_smelting");
   }

   protected static void oreBlasting(Consumer<FinishedRecipe> p_248775_, List<ItemLike> p_251504_, RecipeCategory p_248846_, ItemLike p_249735_, float p_248783_, int p_250303_, String p_251984_) {
      oreCooking(p_248775_, RecipeSerializer.BLASTING_RECIPE, p_251504_, p_248846_, p_249735_, p_248783_, p_250303_, p_251984_, "_from_blasting");
   }

   protected static void oreCooking(Consumer<FinishedRecipe> p_250791_, RecipeSerializer<? extends AbstractCookingRecipe> p_251817_, List<ItemLike> p_249619_, RecipeCategory p_251154_, ItemLike p_250066_, float p_251871_, int p_251316_, String p_251450_, String p_249236_) {
      for(ItemLike itemlike : p_249619_) {
         SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), p_251154_, p_250066_, p_251871_, p_251316_, p_251817_).group(p_251450_).unlockedBy(getHasName(itemlike), has(itemlike)).save(p_250791_, getItemName(p_250066_) + p_249236_ + "_" + getItemName(itemlike));
      }

   }

   protected static void netheriteSmithing(Consumer<FinishedRecipe> p_251614_, Item p_250046_, RecipeCategory p_248986_, Item p_250389_) {
      SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(p_250046_), Ingredient.of(Items.NETHERITE_INGOT), p_248986_, p_250389_).unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT)).save(p_251614_, getItemName(p_250389_) + "_smithing");
   }

   protected static void trimSmithing(Consumer<FinishedRecipe> p_285086_, Item p_285461_, ResourceLocation p_285044_) {
      SmithingTrimRecipeBuilder.smithingTrim(Ingredient.of(p_285461_), Ingredient.of(ItemTags.TRIMMABLE_ARMOR), Ingredient.of(ItemTags.TRIM_MATERIALS), RecipeCategory.MISC).unlocks("has_smithing_trim_template", has(p_285461_)).save(p_285086_, p_285044_);
   }

   protected static void twoByTwoPacker(Consumer<FinishedRecipe> p_248860_, RecipeCategory p_250881_, ItemLike p_252184_, ItemLike p_249710_) {
      ShapedRecipeBuilder.shaped(p_250881_, p_252184_, 1).define('#', p_249710_).pattern("##").pattern("##").unlockedBy(getHasName(p_249710_), has(p_249710_)).save(p_248860_);
   }

   protected static void threeByThreePacker(Consumer<FinishedRecipe> p_259036_, RecipeCategory p_259247_, ItemLike p_259376_, ItemLike p_259717_, String p_260308_) {
      ShapelessRecipeBuilder.shapeless(p_259247_, p_259376_).requires(p_259717_, 9).unlockedBy(p_260308_, has(p_259717_)).save(p_259036_);
   }

   protected static void threeByThreePacker(Consumer<FinishedRecipe> p_260012_, RecipeCategory p_259186_, ItemLike p_259360_, ItemLike p_259263_) {
      threeByThreePacker(p_260012_, p_259186_, p_259360_, p_259263_, getHasName(p_259263_));
   }

   protected static void planksFromLog(Consumer<FinishedRecipe> p_259712_, ItemLike p_259052_, TagKey<Item> p_259045_, int p_259471_) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, p_259052_, p_259471_).requires(p_259045_).group("planks").unlockedBy("has_log", has(p_259045_)).save(p_259712_);
   }

   protected static void planksFromLogs(Consumer<FinishedRecipe> p_259910_, ItemLike p_259193_, TagKey<Item> p_259818_, int p_259807_) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, p_259193_, p_259807_).requires(p_259818_).group("planks").unlockedBy("has_logs", has(p_259818_)).save(p_259910_);
   }

   protected static void woodFromLogs(Consumer<FinishedRecipe> p_126003_, ItemLike p_126004_, ItemLike p_126005_) {
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, p_126004_, 3).define('#', p_126005_).pattern("##").pattern("##").group("bark").unlockedBy("has_log", has(p_126005_)).save(p_126003_);
   }

   protected static void woodenBoat(Consumer<FinishedRecipe> p_126022_, ItemLike p_126023_, ItemLike p_126024_) {
      ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, p_126023_).define('#', p_126024_).pattern("# #").pattern("###").group("boat").unlockedBy("in_water", insideOf(Blocks.WATER)).save(p_126022_);
   }

   protected static void chestBoat(Consumer<FinishedRecipe> p_236372_, ItemLike p_236373_, ItemLike p_236374_) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, p_236373_).requires(Blocks.CHEST).requires(p_236374_).group("chest_boat").unlockedBy("has_boat", has(ItemTags.BOATS)).save(p_236372_);
   }

   protected static RecipeBuilder buttonBuilder(ItemLike p_176659_, Ingredient p_176660_) {
      return ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, p_176659_).requires(p_176660_);
   }

   protected static RecipeBuilder doorBuilder(ItemLike p_176671_, Ingredient p_176672_) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, p_176671_, 3).define('#', p_176672_).pattern("##").pattern("##").pattern("##");
   }

   protected static RecipeBuilder fenceBuilder(ItemLike p_176679_, Ingredient p_176680_) {
      int i = p_176679_ == Blocks.NETHER_BRICK_FENCE ? 6 : 3;
      Item item = p_176679_ == Blocks.NETHER_BRICK_FENCE ? Items.NETHER_BRICK : Items.STICK;
      return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, p_176679_, i).define('W', p_176680_).define('#', item).pattern("W#W").pattern("W#W");
   }

   protected static RecipeBuilder fenceGateBuilder(ItemLike p_176685_, Ingredient p_176686_) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, p_176685_).define('#', Items.STICK).define('W', p_176686_).pattern("#W#").pattern("#W#");
   }

   protected static void pressurePlate(Consumer<FinishedRecipe> p_176691_, ItemLike p_176692_, ItemLike p_176693_) {
      pressurePlateBuilder(RecipeCategory.REDSTONE, p_176692_, Ingredient.of(p_176693_)).unlockedBy(getHasName(p_176693_), has(p_176693_)).save(p_176691_);
   }

   protected static RecipeBuilder pressurePlateBuilder(RecipeCategory p_251447_, ItemLike p_251989_, Ingredient p_249211_) {
      return ShapedRecipeBuilder.shaped(p_251447_, p_251989_).define('#', p_249211_).pattern("##");
   }

   protected static void slab(Consumer<FinishedRecipe> p_248880_, RecipeCategory p_251848_, ItemLike p_249368_, ItemLike p_252133_) {
      slabBuilder(p_251848_, p_249368_, Ingredient.of(p_252133_)).unlockedBy(getHasName(p_252133_), has(p_252133_)).save(p_248880_);
   }

   protected static RecipeBuilder slabBuilder(RecipeCategory p_251707_, ItemLike p_251284_, Ingredient p_248824_) {
      return ShapedRecipeBuilder.shaped(p_251707_, p_251284_, 6).define('#', p_248824_).pattern("###");
   }

   protected static RecipeBuilder stairBuilder(ItemLike p_176711_, Ingredient p_176712_) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, p_176711_, 4).define('#', p_176712_).pattern("#  ").pattern("## ").pattern("###");
   }

   protected static RecipeBuilder trapdoorBuilder(ItemLike p_176721_, Ingredient p_176722_) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, p_176721_, 2).define('#', p_176722_).pattern("###").pattern("###");
   }

   protected static RecipeBuilder signBuilder(ItemLike p_176727_, Ingredient p_176728_) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, p_176727_, 3).group("sign").define('#', p_176728_).define('X', Items.STICK).pattern("###").pattern("###").pattern(" X ");
   }

   protected static void hangingSign(Consumer<FinishedRecipe> p_250663_, ItemLike p_252355_, ItemLike p_250437_) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, p_252355_, 6).group("hanging_sign").define('#', p_250437_).define('X', Items.CHAIN).pattern("X X").pattern("###").pattern("###").unlockedBy("has_stripped_logs", has(p_250437_)).save(p_250663_);
   }

   protected static void colorBlockWithDye(Consumer<FinishedRecipe> p_289666_, List<Item> p_289675_, List<Item> p_289672_, String p_289641_) {
      for(int i = 0; i < p_289675_.size(); ++i) {
         Item item = p_289675_.get(i);
         Item item1 = p_289672_.get(i);
         ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, item1).requires(item).requires(Ingredient.of(p_289672_.stream().filter((p_288265_) -> {
            return !p_288265_.equals(item1);
         }).map(ItemStack::new))).group(p_289641_).unlockedBy("has_needed_dye", has(item)).save(p_289666_, "dye_" + getItemName(item1));
      }

   }

   protected static void carpet(Consumer<FinishedRecipe> p_176717_, ItemLike p_176718_, ItemLike p_176719_) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, p_176718_, 3).define('#', p_176719_).pattern("##").group("carpet").unlockedBy(getHasName(p_176719_), has(p_176719_)).save(p_176717_);
   }

   protected static void bedFromPlanksAndWool(Consumer<FinishedRecipe> p_126074_, ItemLike p_126075_, ItemLike p_126076_) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, p_126075_).define('#', p_126076_).define('X', ItemTags.PLANKS).pattern("###").pattern("XXX").group("bed").unlockedBy(getHasName(p_126076_), has(p_126076_)).save(p_126074_);
   }

   protected static void banner(Consumer<FinishedRecipe> p_126082_, ItemLike p_126083_, ItemLike p_126084_) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, p_126083_).define('#', p_126084_).define('|', Items.STICK).pattern("###").pattern("###").pattern(" | ").group("banner").unlockedBy(getHasName(p_126084_), has(p_126084_)).save(p_126082_);
   }

   protected static void stainedGlassFromGlassAndDye(Consumer<FinishedRecipe> p_126086_, ItemLike p_126087_, ItemLike p_126088_) {
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, p_126087_, 8).define('#', Blocks.GLASS).define('X', p_126088_).pattern("###").pattern("#X#").pattern("###").group("stained_glass").unlockedBy("has_glass", has(Blocks.GLASS)).save(p_126086_);
   }

   protected static void stainedGlassPaneFromStainedGlass(Consumer<FinishedRecipe> p_126090_, ItemLike p_126091_, ItemLike p_126092_) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, p_126091_, 16).define('#', p_126092_).pattern("###").pattern("###").group("stained_glass_pane").unlockedBy("has_glass", has(p_126092_)).save(p_126090_);
   }

   protected static void stainedGlassPaneFromGlassPaneAndDye(Consumer<FinishedRecipe> p_126094_, ItemLike p_126095_, ItemLike p_126096_) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, p_126095_, 8).define('#', Blocks.GLASS_PANE).define('$', p_126096_).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").unlockedBy("has_glass_pane", has(Blocks.GLASS_PANE)).unlockedBy(getHasName(p_126096_), has(p_126096_)).save(p_126094_, getConversionRecipeName(p_126095_, Blocks.GLASS_PANE));
   }

   protected static void coloredTerracottaFromTerracottaAndDye(Consumer<FinishedRecipe> p_126098_, ItemLike p_126099_, ItemLike p_126100_) {
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, p_126099_, 8).define('#', Blocks.TERRACOTTA).define('X', p_126100_).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").unlockedBy("has_terracotta", has(Blocks.TERRACOTTA)).save(p_126098_);
   }

   protected static void concretePowder(Consumer<FinishedRecipe> p_126102_, ItemLike p_126103_, ItemLike p_126104_) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, p_126103_, 8).requires(p_126104_).requires(Blocks.SAND, 4).requires(Blocks.GRAVEL, 4).group("concrete_powder").unlockedBy("has_sand", has(Blocks.SAND)).unlockedBy("has_gravel", has(Blocks.GRAVEL)).save(p_126102_);
   }

   protected static void candle(Consumer<FinishedRecipe> p_176543_, ItemLike p_176544_, ItemLike p_176545_) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, p_176544_).requires(Blocks.CANDLE).requires(p_176545_).group("dyed_candle").unlockedBy(getHasName(p_176545_), has(p_176545_)).save(p_176543_);
   }

   protected static void wall(Consumer<FinishedRecipe> p_251034_, RecipeCategory p_251148_, ItemLike p_250499_, ItemLike p_249970_) {
      wallBuilder(p_251148_, p_250499_, Ingredient.of(p_249970_)).unlockedBy(getHasName(p_249970_), has(p_249970_)).save(p_251034_);
   }

   protected static RecipeBuilder wallBuilder(RecipeCategory p_249083_, ItemLike p_250754_, Ingredient p_250311_) {
      return ShapedRecipeBuilder.shaped(p_249083_, p_250754_, 6).define('#', p_250311_).pattern("###").pattern("###");
   }

   protected static void polished(Consumer<FinishedRecipe> p_251348_, RecipeCategory p_248719_, ItemLike p_250032_, ItemLike p_250021_) {
      polishedBuilder(p_248719_, p_250032_, Ingredient.of(p_250021_)).unlockedBy(getHasName(p_250021_), has(p_250021_)).save(p_251348_);
   }

   protected static RecipeBuilder polishedBuilder(RecipeCategory p_249131_, ItemLike p_251242_, Ingredient p_251412_) {
      return ShapedRecipeBuilder.shaped(p_249131_, p_251242_, 4).define('S', p_251412_).pattern("SS").pattern("SS");
   }

   protected static void cut(Consumer<FinishedRecipe> p_248712_, RecipeCategory p_252306_, ItemLike p_249686_, ItemLike p_251100_) {
      cutBuilder(p_252306_, p_249686_, Ingredient.of(p_251100_)).unlockedBy(getHasName(p_251100_), has(p_251100_)).save(p_248712_);
   }

   protected static ShapedRecipeBuilder cutBuilder(RecipeCategory p_250895_, ItemLike p_251147_, Ingredient p_251563_) {
      return ShapedRecipeBuilder.shaped(p_250895_, p_251147_, 4).define('#', p_251563_).pattern("##").pattern("##");
   }

   protected static void chiseled(Consumer<FinishedRecipe> p_250120_, RecipeCategory p_251604_, ItemLike p_251049_, ItemLike p_252267_) {
      chiseledBuilder(p_251604_, p_251049_, Ingredient.of(p_252267_)).unlockedBy(getHasName(p_252267_), has(p_252267_)).save(p_250120_);
   }

   protected static void mosaicBuilder(Consumer<FinishedRecipe> p_249200_, RecipeCategory p_248788_, ItemLike p_251925_, ItemLike p_252242_) {
      ShapedRecipeBuilder.shaped(p_248788_, p_251925_).define('#', p_252242_).pattern("#").pattern("#").unlockedBy(getHasName(p_252242_), has(p_252242_)).save(p_249200_);
   }

   protected static ShapedRecipeBuilder chiseledBuilder(RecipeCategory p_251755_, ItemLike p_249782_, Ingredient p_250087_) {
      return ShapedRecipeBuilder.shaped(p_251755_, p_249782_).define('#', p_250087_).pattern("#").pattern("#");
   }

   protected static void stonecutterResultFromBase(Consumer<FinishedRecipe> p_251589_, RecipeCategory p_248911_, ItemLike p_251265_, ItemLike p_250033_) {
      stonecutterResultFromBase(p_251589_, p_248911_, p_251265_, p_250033_, 1);
   }

   protected static void stonecutterResultFromBase(Consumer<FinishedRecipe> p_249145_, RecipeCategory p_250609_, ItemLike p_251254_, ItemLike p_249666_, int p_251462_) {
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(p_249666_), p_250609_, p_251254_, p_251462_).unlockedBy(getHasName(p_249666_), has(p_249666_)).save(p_249145_, getConversionRecipeName(p_251254_, p_249666_) + "_stonecutting");
   }

   protected static void smeltingResultFromBase(Consumer<FinishedRecipe> p_176740_, ItemLike p_176741_, ItemLike p_176742_) {
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(p_176742_), RecipeCategory.BUILDING_BLOCKS, p_176741_, 0.1F, 200).unlockedBy(getHasName(p_176742_), has(p_176742_)).save(p_176740_);
   }

   protected static void nineBlockStorageRecipes(Consumer<FinishedRecipe> p_249580_, RecipeCategory p_251203_, ItemLike p_251689_, RecipeCategory p_251376_, ItemLike p_248771_) {
      nineBlockStorageRecipes(p_249580_, p_251203_, p_251689_, p_251376_, p_248771_, getSimpleRecipeName(p_248771_), (String)null, getSimpleRecipeName(p_251689_), (String)null);
   }

   protected static void nineBlockStorageRecipesWithCustomPacking(Consumer<FinishedRecipe> p_250488_, RecipeCategory p_250885_, ItemLike p_251651_, RecipeCategory p_250874_, ItemLike p_248576_, String p_250171_, String p_249386_) {
      nineBlockStorageRecipes(p_250488_, p_250885_, p_251651_, p_250874_, p_248576_, p_250171_, p_249386_, getSimpleRecipeName(p_251651_), (String)null);
   }

   protected static void nineBlockStorageRecipesRecipesWithCustomUnpacking(Consumer<FinishedRecipe> p_250320_, RecipeCategory p_248979_, ItemLike p_249101_, RecipeCategory p_252036_, ItemLike p_250886_, String p_248768_, String p_250847_) {
      nineBlockStorageRecipes(p_250320_, p_248979_, p_249101_, p_252036_, p_250886_, getSimpleRecipeName(p_250886_), (String)null, p_248768_, p_250847_);
   }

   protected static void nineBlockStorageRecipes(Consumer<FinishedRecipe> p_250423_, RecipeCategory p_250083_, ItemLike p_250042_, RecipeCategory p_248977_, ItemLike p_251911_, String p_250475_, @Nullable String p_248641_, String p_252237_, @Nullable String p_250414_) {
      ShapelessRecipeBuilder.shapeless(p_250083_, p_250042_, 9).requires(p_251911_).group(p_250414_).unlockedBy(getHasName(p_251911_), has(p_251911_)).save(p_250423_, new ResourceLocation(p_252237_));
      ShapedRecipeBuilder.shaped(p_248977_, p_251911_).define('#', p_250042_).pattern("###").pattern("###").pattern("###").group(p_248641_).unlockedBy(getHasName(p_250042_), has(p_250042_)).save(p_250423_, new ResourceLocation(p_250475_));
   }

   protected static void copySmithingTemplate(Consumer<FinishedRecipe> p_267061_, ItemLike p_266974_, TagKey<Item> p_267283_) {
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, p_266974_, 2).define('#', Items.DIAMOND).define('C', p_267283_).define('S', p_266974_).pattern("#S#").pattern("#C#").pattern("###").unlockedBy(getHasName(p_266974_), has(p_266974_)).save(p_267061_);
   }

   protected static void copySmithingTemplate(Consumer<FinishedRecipe> p_266734_, ItemLike p_267133_, ItemLike p_267023_) {
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, p_267133_, 2).define('#', Items.DIAMOND).define('C', p_267023_).define('S', p_267133_).pattern("#S#").pattern("#C#").pattern("###").unlockedBy(getHasName(p_267133_), has(p_267133_)).save(p_266734_);
   }

   protected static void cookRecipes(Consumer<FinishedRecipe> p_126007_, String p_126008_, RecipeSerializer<? extends AbstractCookingRecipe> p_250529_, int p_126010_) {
      simpleCookingRecipe(p_126007_, p_126008_, p_250529_, p_126010_, Items.BEEF, Items.COOKED_BEEF, 0.35F);
      simpleCookingRecipe(p_126007_, p_126008_, p_250529_, p_126010_, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35F);
      simpleCookingRecipe(p_126007_, p_126008_, p_250529_, p_126010_, Items.COD, Items.COOKED_COD, 0.35F);
      simpleCookingRecipe(p_126007_, p_126008_, p_250529_, p_126010_, Items.KELP, Items.DRIED_KELP, 0.1F);
      simpleCookingRecipe(p_126007_, p_126008_, p_250529_, p_126010_, Items.SALMON, Items.COOKED_SALMON, 0.35F);
      simpleCookingRecipe(p_126007_, p_126008_, p_250529_, p_126010_, Items.MUTTON, Items.COOKED_MUTTON, 0.35F);
      simpleCookingRecipe(p_126007_, p_126008_, p_250529_, p_126010_, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35F);
      simpleCookingRecipe(p_126007_, p_126008_, p_250529_, p_126010_, Items.POTATO, Items.BAKED_POTATO, 0.35F);
      simpleCookingRecipe(p_126007_, p_126008_, p_250529_, p_126010_, Items.RABBIT, Items.COOKED_RABBIT, 0.35F);
   }

   protected static void simpleCookingRecipe(Consumer<FinishedRecipe> p_249398_, String p_249709_, RecipeSerializer<? extends AbstractCookingRecipe> p_251876_, int p_249258_, ItemLike p_250669_, ItemLike p_250224_, float p_252138_) {
      SimpleCookingRecipeBuilder.generic(Ingredient.of(p_250669_), RecipeCategory.FOOD, p_250224_, p_252138_, p_249258_, p_251876_).unlockedBy(getHasName(p_250669_), has(p_250669_)).save(p_249398_, getItemName(p_250224_) + "_from_" + p_249709_);
   }

   protected static void waxRecipes(Consumer<FinishedRecipe> p_176611_) {
      HoneycombItem.WAXABLES.get().forEach((p_248022_, p_248023_) -> {
         ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, p_248023_).requires(p_248022_).requires(Items.HONEYCOMB).group(getItemName(p_248023_)).unlockedBy(getHasName(p_248022_), has(p_248022_)).save(p_176611_, getConversionRecipeName(p_248023_, Items.HONEYCOMB));
      });
   }

   protected static void generateRecipes(Consumer<FinishedRecipe> p_176581_, BlockFamily p_176582_) {
      p_176582_.getVariants().forEach((p_176529_, p_176530_) -> {
         BiFunction<ItemLike, ItemLike, RecipeBuilder> bifunction = SHAPE_BUILDERS.get(p_176529_);
         ItemLike itemlike = getBaseBlock(p_176582_, p_176529_);
         if (bifunction != null) {
            RecipeBuilder recipebuilder = bifunction.apply(p_176530_, itemlike);
            p_176582_.getRecipeGroupPrefix().ifPresent((p_176601_) -> {
               recipebuilder.group(p_176601_ + (p_176529_ == BlockFamily.Variant.CUT ? "" : "_" + p_176529_.getName()));
            });
            recipebuilder.unlockedBy(p_176582_.getRecipeUnlockedBy().orElseGet(() -> {
               return getHasName(itemlike);
            }), has(itemlike));
            recipebuilder.save(p_176581_);
         }

         if (p_176529_ == BlockFamily.Variant.CRACKED) {
            smeltingResultFromBase(p_176581_, p_176530_, itemlike);
         }

      });
   }

   protected static Block getBaseBlock(BlockFamily p_176524_, BlockFamily.Variant p_176525_) {
      if (p_176525_ == BlockFamily.Variant.CHISELED) {
         if (!p_176524_.getVariants().containsKey(BlockFamily.Variant.SLAB)) {
            throw new IllegalStateException("Slab is not defined for the family.");
         } else {
            return p_176524_.get(BlockFamily.Variant.SLAB);
         }
      } else {
         return p_176524_.getBaseBlock();
      }
   }

   protected static EnterBlockTrigger.TriggerInstance insideOf(Block p_125980_) {
      return new EnterBlockTrigger.TriggerInstance(ContextAwarePredicate.ANY, p_125980_, StatePropertiesPredicate.ANY);
   }

   protected static InventoryChangeTrigger.TriggerInstance has(MinMaxBounds.Ints p_176521_, ItemLike p_176522_) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(p_176522_).withCount(p_176521_).build());
   }

   protected static InventoryChangeTrigger.TriggerInstance has(ItemLike p_125978_) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(p_125978_).build());
   }

   protected static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> p_206407_) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(p_206407_).build());
   }

   protected static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... p_126012_) {
      return new InventoryChangeTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, p_126012_);
   }

   protected static String getHasName(ItemLike p_176603_) {
      return "has_" + getItemName(p_176603_);
   }

   protected static String getItemName(ItemLike p_176633_) {
      return BuiltInRegistries.ITEM.getKey(p_176633_.asItem()).getPath();
   }

   protected static String getSimpleRecipeName(ItemLike p_176645_) {
      return getItemName(p_176645_);
   }

   protected static String getConversionRecipeName(ItemLike p_176518_, ItemLike p_176519_) {
      return getItemName(p_176518_) + "_from_" + getItemName(p_176519_);
   }

   protected static String getSmeltingRecipeName(ItemLike p_176657_) {
      return getItemName(p_176657_) + "_from_smelting";
   }

   protected static String getBlastingRecipeName(ItemLike p_176669_) {
      return getItemName(p_176669_) + "_from_blasting";
   }

   public final String getName() {
      return "Recipes";
   }
}
