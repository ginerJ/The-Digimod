package net.minecraft.data.loot.packs;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.BeetrootBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarrotBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.PotatoBlock;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.LimitCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class VanillaBlockLoot extends BlockLootSubProvider {
   private static final float[] JUNGLE_LEAVES_SAPLING_CHANGES = new float[]{0.025F, 0.027777778F, 0.03125F, 0.041666668F, 0.1F};
   private static final Set<Item> EXPLOSION_RESISTANT = Stream.of(Blocks.DRAGON_EGG, Blocks.BEACON, Blocks.CONDUIT, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.CREEPER_HEAD, Blocks.DRAGON_HEAD, Blocks.PIGLIN_HEAD, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX).map(ItemLike::asItem).collect(Collectors.toSet());

   public VanillaBlockLoot() {
      super(EXPLOSION_RESISTANT, FeatureFlags.REGISTRY.allFlags());
   }

   protected void generate() {
      this.dropSelf(Blocks.GRANITE);
      this.dropSelf(Blocks.POLISHED_GRANITE);
      this.dropSelf(Blocks.DIORITE);
      this.dropSelf(Blocks.POLISHED_DIORITE);
      this.dropSelf(Blocks.ANDESITE);
      this.dropSelf(Blocks.POLISHED_ANDESITE);
      this.dropSelf(Blocks.DIRT);
      this.dropSelf(Blocks.COARSE_DIRT);
      this.dropSelf(Blocks.COBBLESTONE);
      this.dropSelf(Blocks.OAK_PLANKS);
      this.dropSelf(Blocks.SPRUCE_PLANKS);
      this.dropSelf(Blocks.BIRCH_PLANKS);
      this.dropSelf(Blocks.JUNGLE_PLANKS);
      this.dropSelf(Blocks.ACACIA_PLANKS);
      this.dropSelf(Blocks.DARK_OAK_PLANKS);
      this.dropSelf(Blocks.MANGROVE_PLANKS);
      this.dropSelf(Blocks.CHERRY_PLANKS);
      this.dropSelf(Blocks.BAMBOO_PLANKS);
      this.dropSelf(Blocks.BAMBOO_MOSAIC);
      this.add(Blocks.DECORATED_POT, this::createDecoratedPotTable);
      this.dropSelf(Blocks.OAK_SAPLING);
      this.dropSelf(Blocks.SPRUCE_SAPLING);
      this.dropSelf(Blocks.BIRCH_SAPLING);
      this.dropSelf(Blocks.JUNGLE_SAPLING);
      this.dropSelf(Blocks.ACACIA_SAPLING);
      this.dropSelf(Blocks.DARK_OAK_SAPLING);
      this.dropSelf(Blocks.CHERRY_SAPLING);
      this.dropSelf(Blocks.SAND);
      this.add(Blocks.SUSPICIOUS_SAND, noDrop());
      this.add(Blocks.SUSPICIOUS_GRAVEL, noDrop());
      this.dropSelf(Blocks.RED_SAND);
      this.dropSelf(Blocks.OAK_LOG);
      this.dropSelf(Blocks.SPRUCE_LOG);
      this.dropSelf(Blocks.BIRCH_LOG);
      this.dropSelf(Blocks.JUNGLE_LOG);
      this.dropSelf(Blocks.ACACIA_LOG);
      this.dropSelf(Blocks.DARK_OAK_LOG);
      this.dropSelf(Blocks.CHERRY_LOG);
      this.dropSelf(Blocks.BAMBOO_BLOCK);
      this.dropSelf(Blocks.STRIPPED_OAK_LOG);
      this.dropSelf(Blocks.STRIPPED_SPRUCE_LOG);
      this.dropSelf(Blocks.STRIPPED_BIRCH_LOG);
      this.dropSelf(Blocks.STRIPPED_JUNGLE_LOG);
      this.dropSelf(Blocks.STRIPPED_ACACIA_LOG);
      this.dropSelf(Blocks.STRIPPED_DARK_OAK_LOG);
      this.dropSelf(Blocks.STRIPPED_MANGROVE_LOG);
      this.dropSelf(Blocks.STRIPPED_CHERRY_LOG);
      this.dropSelf(Blocks.STRIPPED_BAMBOO_BLOCK);
      this.dropSelf(Blocks.STRIPPED_WARPED_STEM);
      this.dropSelf(Blocks.STRIPPED_CRIMSON_STEM);
      this.dropSelf(Blocks.OAK_WOOD);
      this.dropSelf(Blocks.SPRUCE_WOOD);
      this.dropSelf(Blocks.BIRCH_WOOD);
      this.dropSelf(Blocks.JUNGLE_WOOD);
      this.dropSelf(Blocks.ACACIA_WOOD);
      this.dropSelf(Blocks.DARK_OAK_WOOD);
      this.dropSelf(Blocks.MANGROVE_WOOD);
      this.dropSelf(Blocks.CHERRY_WOOD);
      this.dropSelf(Blocks.STRIPPED_OAK_WOOD);
      this.dropSelf(Blocks.STRIPPED_SPRUCE_WOOD);
      this.dropSelf(Blocks.STRIPPED_BIRCH_WOOD);
      this.dropSelf(Blocks.STRIPPED_JUNGLE_WOOD);
      this.dropSelf(Blocks.STRIPPED_ACACIA_WOOD);
      this.dropSelf(Blocks.STRIPPED_DARK_OAK_WOOD);
      this.dropSelf(Blocks.STRIPPED_MANGROVE_WOOD);
      this.dropSelf(Blocks.STRIPPED_CHERRY_WOOD);
      this.dropSelf(Blocks.STRIPPED_CRIMSON_HYPHAE);
      this.dropSelf(Blocks.STRIPPED_WARPED_HYPHAE);
      this.dropSelf(Blocks.SPONGE);
      this.dropSelf(Blocks.WET_SPONGE);
      this.dropSelf(Blocks.LAPIS_BLOCK);
      this.dropSelf(Blocks.SANDSTONE);
      this.dropSelf(Blocks.CHISELED_SANDSTONE);
      this.dropSelf(Blocks.CUT_SANDSTONE);
      this.dropSelf(Blocks.NOTE_BLOCK);
      this.dropSelf(Blocks.POWERED_RAIL);
      this.dropSelf(Blocks.DETECTOR_RAIL);
      this.dropSelf(Blocks.STICKY_PISTON);
      this.dropSelf(Blocks.PISTON);
      this.dropSelf(Blocks.WHITE_WOOL);
      this.dropSelf(Blocks.ORANGE_WOOL);
      this.dropSelf(Blocks.MAGENTA_WOOL);
      this.dropSelf(Blocks.LIGHT_BLUE_WOOL);
      this.dropSelf(Blocks.YELLOW_WOOL);
      this.dropSelf(Blocks.LIME_WOOL);
      this.dropSelf(Blocks.PINK_WOOL);
      this.dropSelf(Blocks.GRAY_WOOL);
      this.dropSelf(Blocks.LIGHT_GRAY_WOOL);
      this.dropSelf(Blocks.CYAN_WOOL);
      this.dropSelf(Blocks.PURPLE_WOOL);
      this.dropSelf(Blocks.BLUE_WOOL);
      this.dropSelf(Blocks.BROWN_WOOL);
      this.dropSelf(Blocks.GREEN_WOOL);
      this.dropSelf(Blocks.RED_WOOL);
      this.dropSelf(Blocks.BLACK_WOOL);
      this.dropSelf(Blocks.DANDELION);
      this.dropSelf(Blocks.POPPY);
      this.dropSelf(Blocks.TORCHFLOWER);
      this.dropSelf(Blocks.BLUE_ORCHID);
      this.dropSelf(Blocks.ALLIUM);
      this.dropSelf(Blocks.AZURE_BLUET);
      this.dropSelf(Blocks.RED_TULIP);
      this.dropSelf(Blocks.ORANGE_TULIP);
      this.dropSelf(Blocks.WHITE_TULIP);
      this.dropSelf(Blocks.PINK_TULIP);
      this.dropSelf(Blocks.OXEYE_DAISY);
      this.dropSelf(Blocks.CORNFLOWER);
      this.dropSelf(Blocks.WITHER_ROSE);
      this.dropSelf(Blocks.LILY_OF_THE_VALLEY);
      this.dropSelf(Blocks.BROWN_MUSHROOM);
      this.dropSelf(Blocks.RED_MUSHROOM);
      this.dropSelf(Blocks.GOLD_BLOCK);
      this.dropSelf(Blocks.IRON_BLOCK);
      this.dropSelf(Blocks.BRICKS);
      this.dropSelf(Blocks.MOSSY_COBBLESTONE);
      this.dropSelf(Blocks.OBSIDIAN);
      this.dropSelf(Blocks.CRYING_OBSIDIAN);
      this.dropSelf(Blocks.TORCH);
      this.dropSelf(Blocks.OAK_STAIRS);
      this.dropSelf(Blocks.MANGROVE_STAIRS);
      this.dropSelf(Blocks.BAMBOO_STAIRS);
      this.dropSelf(Blocks.BAMBOO_MOSAIC_STAIRS);
      this.dropSelf(Blocks.REDSTONE_WIRE);
      this.dropSelf(Blocks.DIAMOND_BLOCK);
      this.dropSelf(Blocks.CRAFTING_TABLE);
      this.dropSelf(Blocks.OAK_SIGN);
      this.dropSelf(Blocks.SPRUCE_SIGN);
      this.dropSelf(Blocks.BIRCH_SIGN);
      this.dropSelf(Blocks.ACACIA_SIGN);
      this.dropSelf(Blocks.JUNGLE_SIGN);
      this.dropSelf(Blocks.DARK_OAK_SIGN);
      this.dropSelf(Blocks.MANGROVE_SIGN);
      this.dropSelf(Blocks.CHERRY_SIGN);
      this.dropSelf(Blocks.BAMBOO_SIGN);
      this.dropSelf(Blocks.OAK_HANGING_SIGN);
      this.dropSelf(Blocks.SPRUCE_HANGING_SIGN);
      this.dropSelf(Blocks.BIRCH_HANGING_SIGN);
      this.dropSelf(Blocks.ACACIA_HANGING_SIGN);
      this.dropSelf(Blocks.CHERRY_HANGING_SIGN);
      this.dropSelf(Blocks.JUNGLE_HANGING_SIGN);
      this.dropSelf(Blocks.DARK_OAK_HANGING_SIGN);
      this.dropSelf(Blocks.MANGROVE_HANGING_SIGN);
      this.dropSelf(Blocks.CRIMSON_HANGING_SIGN);
      this.dropSelf(Blocks.WARPED_HANGING_SIGN);
      this.dropSelf(Blocks.BAMBOO_HANGING_SIGN);
      this.dropSelf(Blocks.LADDER);
      this.dropSelf(Blocks.RAIL);
      this.dropSelf(Blocks.COBBLESTONE_STAIRS);
      this.dropSelf(Blocks.LEVER);
      this.dropSelf(Blocks.STONE_PRESSURE_PLATE);
      this.dropSelf(Blocks.OAK_PRESSURE_PLATE);
      this.dropSelf(Blocks.SPRUCE_PRESSURE_PLATE);
      this.dropSelf(Blocks.BIRCH_PRESSURE_PLATE);
      this.dropSelf(Blocks.JUNGLE_PRESSURE_PLATE);
      this.dropSelf(Blocks.ACACIA_PRESSURE_PLATE);
      this.dropSelf(Blocks.DARK_OAK_PRESSURE_PLATE);
      this.dropSelf(Blocks.MANGROVE_PRESSURE_PLATE);
      this.dropSelf(Blocks.CHERRY_PRESSURE_PLATE);
      this.dropSelf(Blocks.BAMBOO_PRESSURE_PLATE);
      this.dropSelf(Blocks.REDSTONE_TORCH);
      this.dropSelf(Blocks.STONE_BUTTON);
      this.dropSelf(Blocks.CACTUS);
      this.dropSelf(Blocks.SUGAR_CANE);
      this.dropSelf(Blocks.JUKEBOX);
      this.dropSelf(Blocks.OAK_FENCE);
      this.dropSelf(Blocks.MANGROVE_FENCE);
      this.dropSelf(Blocks.BAMBOO_FENCE);
      this.dropSelf(Blocks.PUMPKIN);
      this.dropSelf(Blocks.NETHERRACK);
      this.dropSelf(Blocks.SOUL_SAND);
      this.dropSelf(Blocks.SOUL_SOIL);
      this.dropSelf(Blocks.BASALT);
      this.dropSelf(Blocks.POLISHED_BASALT);
      this.dropSelf(Blocks.SMOOTH_BASALT);
      this.dropSelf(Blocks.SOUL_TORCH);
      this.dropSelf(Blocks.CARVED_PUMPKIN);
      this.dropSelf(Blocks.JACK_O_LANTERN);
      this.dropSelf(Blocks.REPEATER);
      this.dropSelf(Blocks.OAK_TRAPDOOR);
      this.dropSelf(Blocks.SPRUCE_TRAPDOOR);
      this.dropSelf(Blocks.BIRCH_TRAPDOOR);
      this.dropSelf(Blocks.JUNGLE_TRAPDOOR);
      this.dropSelf(Blocks.ACACIA_TRAPDOOR);
      this.dropSelf(Blocks.DARK_OAK_TRAPDOOR);
      this.dropSelf(Blocks.MANGROVE_TRAPDOOR);
      this.dropSelf(Blocks.CHERRY_TRAPDOOR);
      this.dropSelf(Blocks.BAMBOO_TRAPDOOR);
      this.dropSelf(Blocks.STONE_BRICKS);
      this.dropSelf(Blocks.MOSSY_STONE_BRICKS);
      this.dropSelf(Blocks.CRACKED_STONE_BRICKS);
      this.dropSelf(Blocks.CHISELED_STONE_BRICKS);
      this.dropSelf(Blocks.IRON_BARS);
      this.dropSelf(Blocks.OAK_FENCE_GATE);
      this.dropSelf(Blocks.MANGROVE_FENCE_GATE);
      this.dropSelf(Blocks.BAMBOO_FENCE_GATE);
      this.dropSelf(Blocks.BRICK_STAIRS);
      this.dropSelf(Blocks.STONE_BRICK_STAIRS);
      this.dropSelf(Blocks.LILY_PAD);
      this.dropSelf(Blocks.NETHER_BRICKS);
      this.dropSelf(Blocks.NETHER_BRICK_FENCE);
      this.dropSelf(Blocks.NETHER_BRICK_STAIRS);
      this.dropSelf(Blocks.CAULDRON);
      this.dropSelf(Blocks.END_STONE);
      this.dropSelf(Blocks.REDSTONE_LAMP);
      this.dropSelf(Blocks.SANDSTONE_STAIRS);
      this.dropSelf(Blocks.TRIPWIRE_HOOK);
      this.dropSelf(Blocks.EMERALD_BLOCK);
      this.dropSelf(Blocks.SPRUCE_STAIRS);
      this.dropSelf(Blocks.BIRCH_STAIRS);
      this.dropSelf(Blocks.JUNGLE_STAIRS);
      this.dropSelf(Blocks.COBBLESTONE_WALL);
      this.dropSelf(Blocks.MOSSY_COBBLESTONE_WALL);
      this.dropSelf(Blocks.FLOWER_POT);
      this.dropSelf(Blocks.OAK_BUTTON);
      this.dropSelf(Blocks.SPRUCE_BUTTON);
      this.dropSelf(Blocks.BIRCH_BUTTON);
      this.dropSelf(Blocks.JUNGLE_BUTTON);
      this.dropSelf(Blocks.ACACIA_BUTTON);
      this.dropSelf(Blocks.DARK_OAK_BUTTON);
      this.dropSelf(Blocks.MANGROVE_BUTTON);
      this.dropSelf(Blocks.CHERRY_BUTTON);
      this.dropSelf(Blocks.BAMBOO_BUTTON);
      this.dropSelf(Blocks.SKELETON_SKULL);
      this.dropSelf(Blocks.WITHER_SKELETON_SKULL);
      this.dropSelf(Blocks.ZOMBIE_HEAD);
      this.dropSelf(Blocks.CREEPER_HEAD);
      this.dropSelf(Blocks.DRAGON_HEAD);
      this.dropSelf(Blocks.PIGLIN_HEAD);
      this.dropSelf(Blocks.ANVIL);
      this.dropSelf(Blocks.CHIPPED_ANVIL);
      this.dropSelf(Blocks.DAMAGED_ANVIL);
      this.dropSelf(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
      this.dropSelf(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
      this.dropSelf(Blocks.COMPARATOR);
      this.dropSelf(Blocks.DAYLIGHT_DETECTOR);
      this.dropSelf(Blocks.REDSTONE_BLOCK);
      this.dropSelf(Blocks.QUARTZ_BLOCK);
      this.dropSelf(Blocks.CHISELED_QUARTZ_BLOCK);
      this.dropSelf(Blocks.QUARTZ_PILLAR);
      this.dropSelf(Blocks.QUARTZ_STAIRS);
      this.dropSelf(Blocks.ACTIVATOR_RAIL);
      this.dropSelf(Blocks.WHITE_TERRACOTTA);
      this.dropSelf(Blocks.ORANGE_TERRACOTTA);
      this.dropSelf(Blocks.MAGENTA_TERRACOTTA);
      this.dropSelf(Blocks.LIGHT_BLUE_TERRACOTTA);
      this.dropSelf(Blocks.YELLOW_TERRACOTTA);
      this.dropSelf(Blocks.LIME_TERRACOTTA);
      this.dropSelf(Blocks.PINK_TERRACOTTA);
      this.dropSelf(Blocks.GRAY_TERRACOTTA);
      this.dropSelf(Blocks.LIGHT_GRAY_TERRACOTTA);
      this.dropSelf(Blocks.CYAN_TERRACOTTA);
      this.dropSelf(Blocks.PURPLE_TERRACOTTA);
      this.dropSelf(Blocks.BLUE_TERRACOTTA);
      this.dropSelf(Blocks.BROWN_TERRACOTTA);
      this.dropSelf(Blocks.GREEN_TERRACOTTA);
      this.dropSelf(Blocks.RED_TERRACOTTA);
      this.dropSelf(Blocks.BLACK_TERRACOTTA);
      this.dropSelf(Blocks.ACACIA_STAIRS);
      this.dropSelf(Blocks.DARK_OAK_STAIRS);
      this.dropSelf(Blocks.CHERRY_STAIRS);
      this.dropSelf(Blocks.SLIME_BLOCK);
      this.dropSelf(Blocks.IRON_TRAPDOOR);
      this.dropSelf(Blocks.PRISMARINE);
      this.dropSelf(Blocks.PRISMARINE_BRICKS);
      this.dropSelf(Blocks.DARK_PRISMARINE);
      this.dropSelf(Blocks.PRISMARINE_STAIRS);
      this.dropSelf(Blocks.PRISMARINE_BRICK_STAIRS);
      this.dropSelf(Blocks.DARK_PRISMARINE_STAIRS);
      this.dropSelf(Blocks.HAY_BLOCK);
      this.dropSelf(Blocks.WHITE_CARPET);
      this.dropSelf(Blocks.ORANGE_CARPET);
      this.dropSelf(Blocks.MAGENTA_CARPET);
      this.dropSelf(Blocks.LIGHT_BLUE_CARPET);
      this.dropSelf(Blocks.YELLOW_CARPET);
      this.dropSelf(Blocks.LIME_CARPET);
      this.dropSelf(Blocks.PINK_CARPET);
      this.dropSelf(Blocks.GRAY_CARPET);
      this.dropSelf(Blocks.LIGHT_GRAY_CARPET);
      this.dropSelf(Blocks.CYAN_CARPET);
      this.dropSelf(Blocks.PURPLE_CARPET);
      this.dropSelf(Blocks.BLUE_CARPET);
      this.dropSelf(Blocks.BROWN_CARPET);
      this.dropSelf(Blocks.GREEN_CARPET);
      this.dropSelf(Blocks.RED_CARPET);
      this.dropSelf(Blocks.BLACK_CARPET);
      this.dropSelf(Blocks.TERRACOTTA);
      this.dropSelf(Blocks.COAL_BLOCK);
      this.dropSelf(Blocks.RED_SANDSTONE);
      this.dropSelf(Blocks.CHISELED_RED_SANDSTONE);
      this.dropSelf(Blocks.CUT_RED_SANDSTONE);
      this.dropSelf(Blocks.RED_SANDSTONE_STAIRS);
      this.dropSelf(Blocks.SMOOTH_STONE);
      this.dropSelf(Blocks.SMOOTH_SANDSTONE);
      this.dropSelf(Blocks.SMOOTH_QUARTZ);
      this.dropSelf(Blocks.SMOOTH_RED_SANDSTONE);
      this.dropSelf(Blocks.SPRUCE_FENCE_GATE);
      this.dropSelf(Blocks.BIRCH_FENCE_GATE);
      this.dropSelf(Blocks.JUNGLE_FENCE_GATE);
      this.dropSelf(Blocks.ACACIA_FENCE_GATE);
      this.dropSelf(Blocks.DARK_OAK_FENCE_GATE);
      this.dropSelf(Blocks.CHERRY_FENCE_GATE);
      this.dropSelf(Blocks.SPRUCE_FENCE);
      this.dropSelf(Blocks.BIRCH_FENCE);
      this.dropSelf(Blocks.JUNGLE_FENCE);
      this.dropSelf(Blocks.ACACIA_FENCE);
      this.dropSelf(Blocks.DARK_OAK_FENCE);
      this.dropSelf(Blocks.CHERRY_FENCE);
      this.dropSelf(Blocks.END_ROD);
      this.dropSelf(Blocks.PURPUR_BLOCK);
      this.dropSelf(Blocks.PURPUR_PILLAR);
      this.dropSelf(Blocks.PURPUR_STAIRS);
      this.dropSelf(Blocks.END_STONE_BRICKS);
      this.dropSelf(Blocks.MAGMA_BLOCK);
      this.dropSelf(Blocks.NETHER_WART_BLOCK);
      this.dropSelf(Blocks.RED_NETHER_BRICKS);
      this.dropSelf(Blocks.BONE_BLOCK);
      this.dropSelf(Blocks.OBSERVER);
      this.dropSelf(Blocks.TARGET);
      this.dropSelf(Blocks.WHITE_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.ORANGE_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.MAGENTA_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.YELLOW_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.LIME_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.PINK_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.GRAY_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.CYAN_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.PURPLE_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.BLUE_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.BROWN_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.GREEN_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.RED_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.BLACK_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.WHITE_CONCRETE);
      this.dropSelf(Blocks.ORANGE_CONCRETE);
      this.dropSelf(Blocks.MAGENTA_CONCRETE);
      this.dropSelf(Blocks.LIGHT_BLUE_CONCRETE);
      this.dropSelf(Blocks.YELLOW_CONCRETE);
      this.dropSelf(Blocks.LIME_CONCRETE);
      this.dropSelf(Blocks.PINK_CONCRETE);
      this.dropSelf(Blocks.GRAY_CONCRETE);
      this.dropSelf(Blocks.LIGHT_GRAY_CONCRETE);
      this.dropSelf(Blocks.CYAN_CONCRETE);
      this.dropSelf(Blocks.PURPLE_CONCRETE);
      this.dropSelf(Blocks.BLUE_CONCRETE);
      this.dropSelf(Blocks.BROWN_CONCRETE);
      this.dropSelf(Blocks.GREEN_CONCRETE);
      this.dropSelf(Blocks.RED_CONCRETE);
      this.dropSelf(Blocks.BLACK_CONCRETE);
      this.dropSelf(Blocks.WHITE_CONCRETE_POWDER);
      this.dropSelf(Blocks.ORANGE_CONCRETE_POWDER);
      this.dropSelf(Blocks.MAGENTA_CONCRETE_POWDER);
      this.dropSelf(Blocks.LIGHT_BLUE_CONCRETE_POWDER);
      this.dropSelf(Blocks.YELLOW_CONCRETE_POWDER);
      this.dropSelf(Blocks.LIME_CONCRETE_POWDER);
      this.dropSelf(Blocks.PINK_CONCRETE_POWDER);
      this.dropSelf(Blocks.GRAY_CONCRETE_POWDER);
      this.dropSelf(Blocks.LIGHT_GRAY_CONCRETE_POWDER);
      this.dropSelf(Blocks.CYAN_CONCRETE_POWDER);
      this.dropSelf(Blocks.PURPLE_CONCRETE_POWDER);
      this.dropSelf(Blocks.BLUE_CONCRETE_POWDER);
      this.dropSelf(Blocks.BROWN_CONCRETE_POWDER);
      this.dropSelf(Blocks.GREEN_CONCRETE_POWDER);
      this.dropSelf(Blocks.RED_CONCRETE_POWDER);
      this.dropSelf(Blocks.BLACK_CONCRETE_POWDER);
      this.dropSelf(Blocks.KELP);
      this.dropSelf(Blocks.DRIED_KELP_BLOCK);
      this.dropSelf(Blocks.DEAD_TUBE_CORAL_BLOCK);
      this.dropSelf(Blocks.DEAD_BRAIN_CORAL_BLOCK);
      this.dropSelf(Blocks.DEAD_BUBBLE_CORAL_BLOCK);
      this.dropSelf(Blocks.DEAD_FIRE_CORAL_BLOCK);
      this.dropSelf(Blocks.DEAD_HORN_CORAL_BLOCK);
      this.dropSelf(Blocks.CONDUIT);
      this.dropSelf(Blocks.DRAGON_EGG);
      this.dropSelf(Blocks.BAMBOO);
      this.dropSelf(Blocks.POLISHED_GRANITE_STAIRS);
      this.dropSelf(Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
      this.dropSelf(Blocks.MOSSY_STONE_BRICK_STAIRS);
      this.dropSelf(Blocks.POLISHED_DIORITE_STAIRS);
      this.dropSelf(Blocks.MOSSY_COBBLESTONE_STAIRS);
      this.dropSelf(Blocks.END_STONE_BRICK_STAIRS);
      this.dropSelf(Blocks.STONE_STAIRS);
      this.dropSelf(Blocks.SMOOTH_SANDSTONE_STAIRS);
      this.dropSelf(Blocks.SMOOTH_QUARTZ_STAIRS);
      this.dropSelf(Blocks.GRANITE_STAIRS);
      this.dropSelf(Blocks.ANDESITE_STAIRS);
      this.dropSelf(Blocks.RED_NETHER_BRICK_STAIRS);
      this.dropSelf(Blocks.POLISHED_ANDESITE_STAIRS);
      this.dropSelf(Blocks.DIORITE_STAIRS);
      this.dropSelf(Blocks.BRICK_WALL);
      this.dropSelf(Blocks.PRISMARINE_WALL);
      this.dropSelf(Blocks.RED_SANDSTONE_WALL);
      this.dropSelf(Blocks.MOSSY_STONE_BRICK_WALL);
      this.dropSelf(Blocks.GRANITE_WALL);
      this.dropSelf(Blocks.STONE_BRICK_WALL);
      this.dropSelf(Blocks.NETHER_BRICK_WALL);
      this.dropSelf(Blocks.ANDESITE_WALL);
      this.dropSelf(Blocks.RED_NETHER_BRICK_WALL);
      this.dropSelf(Blocks.SANDSTONE_WALL);
      this.dropSelf(Blocks.END_STONE_BRICK_WALL);
      this.dropSelf(Blocks.DIORITE_WALL);
      this.dropSelf(Blocks.MUD_BRICK_WALL);
      this.dropSelf(Blocks.LOOM);
      this.dropSelf(Blocks.SCAFFOLDING);
      this.dropSelf(Blocks.HONEY_BLOCK);
      this.dropSelf(Blocks.HONEYCOMB_BLOCK);
      this.dropSelf(Blocks.RESPAWN_ANCHOR);
      this.dropSelf(Blocks.LODESTONE);
      this.dropSelf(Blocks.WARPED_STEM);
      this.dropSelf(Blocks.WARPED_HYPHAE);
      this.dropSelf(Blocks.WARPED_FUNGUS);
      this.dropSelf(Blocks.WARPED_WART_BLOCK);
      this.dropSelf(Blocks.CRIMSON_STEM);
      this.dropSelf(Blocks.CRIMSON_HYPHAE);
      this.dropSelf(Blocks.CRIMSON_FUNGUS);
      this.dropSelf(Blocks.SHROOMLIGHT);
      this.dropSelf(Blocks.CRIMSON_PLANKS);
      this.dropSelf(Blocks.WARPED_PLANKS);
      this.dropSelf(Blocks.WARPED_PRESSURE_PLATE);
      this.dropSelf(Blocks.WARPED_FENCE);
      this.dropSelf(Blocks.WARPED_TRAPDOOR);
      this.dropSelf(Blocks.WARPED_FENCE_GATE);
      this.dropSelf(Blocks.WARPED_STAIRS);
      this.dropSelf(Blocks.WARPED_BUTTON);
      this.dropSelf(Blocks.WARPED_SIGN);
      this.dropSelf(Blocks.CRIMSON_PRESSURE_PLATE);
      this.dropSelf(Blocks.CRIMSON_FENCE);
      this.dropSelf(Blocks.CRIMSON_TRAPDOOR);
      this.dropSelf(Blocks.CRIMSON_FENCE_GATE);
      this.dropSelf(Blocks.CRIMSON_STAIRS);
      this.dropSelf(Blocks.CRIMSON_BUTTON);
      this.dropSelf(Blocks.CRIMSON_SIGN);
      this.dropSelf(Blocks.NETHERITE_BLOCK);
      this.dropSelf(Blocks.ANCIENT_DEBRIS);
      this.dropSelf(Blocks.BLACKSTONE);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_BRICKS);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
      this.dropSelf(Blocks.BLACKSTONE_STAIRS);
      this.dropSelf(Blocks.BLACKSTONE_WALL);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
      this.dropSelf(Blocks.CHISELED_POLISHED_BLACKSTONE);
      this.dropSelf(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_STAIRS);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_BUTTON);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_WALL);
      this.dropSelf(Blocks.CHISELED_NETHER_BRICKS);
      this.dropSelf(Blocks.CRACKED_NETHER_BRICKS);
      this.dropSelf(Blocks.QUARTZ_BRICKS);
      this.dropSelf(Blocks.CHAIN);
      this.dropSelf(Blocks.WARPED_ROOTS);
      this.dropSelf(Blocks.CRIMSON_ROOTS);
      this.dropSelf(Blocks.MUD_BRICKS);
      this.dropSelf(Blocks.MUDDY_MANGROVE_ROOTS);
      this.dropSelf(Blocks.MUD_BRICK_STAIRS);
      this.dropSelf(Blocks.AMETHYST_BLOCK);
      this.dropSelf(Blocks.CALCITE);
      this.dropSelf(Blocks.TUFF);
      this.dropSelf(Blocks.TINTED_GLASS);
      this.dropWhenSilkTouch(Blocks.SCULK_SENSOR);
      this.dropWhenSilkTouch(Blocks.CALIBRATED_SCULK_SENSOR);
      this.dropWhenSilkTouch(Blocks.SCULK);
      this.dropWhenSilkTouch(Blocks.SCULK_CATALYST);
      this.add(Blocks.SCULK_VEIN, (p_252292_) -> {
         return this.createMultifaceBlockDrops(p_252292_, HAS_SILK_TOUCH);
      });
      this.dropWhenSilkTouch(Blocks.SCULK_SHRIEKER);
      this.dropWhenSilkTouch(Blocks.CHISELED_BOOKSHELF);
      this.dropSelf(Blocks.COPPER_BLOCK);
      this.dropSelf(Blocks.EXPOSED_COPPER);
      this.dropSelf(Blocks.WEATHERED_COPPER);
      this.dropSelf(Blocks.OXIDIZED_COPPER);
      this.dropSelf(Blocks.CUT_COPPER);
      this.dropSelf(Blocks.EXPOSED_CUT_COPPER);
      this.dropSelf(Blocks.WEATHERED_CUT_COPPER);
      this.dropSelf(Blocks.OXIDIZED_CUT_COPPER);
      this.dropSelf(Blocks.WAXED_COPPER_BLOCK);
      this.dropSelf(Blocks.WAXED_WEATHERED_COPPER);
      this.dropSelf(Blocks.WAXED_EXPOSED_COPPER);
      this.dropSelf(Blocks.WAXED_OXIDIZED_COPPER);
      this.dropSelf(Blocks.WAXED_CUT_COPPER);
      this.dropSelf(Blocks.WAXED_WEATHERED_CUT_COPPER);
      this.dropSelf(Blocks.WAXED_EXPOSED_CUT_COPPER);
      this.dropSelf(Blocks.WAXED_OXIDIZED_CUT_COPPER);
      this.dropSelf(Blocks.WAXED_CUT_COPPER_STAIRS);
      this.dropSelf(Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS);
      this.dropSelf(Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS);
      this.dropSelf(Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS);
      this.dropSelf(Blocks.CUT_COPPER_STAIRS);
      this.dropSelf(Blocks.EXPOSED_CUT_COPPER_STAIRS);
      this.dropSelf(Blocks.WEATHERED_CUT_COPPER_STAIRS);
      this.dropSelf(Blocks.OXIDIZED_CUT_COPPER_STAIRS);
      this.dropSelf(Blocks.LIGHTNING_ROD);
      this.dropSelf(Blocks.POINTED_DRIPSTONE);
      this.dropSelf(Blocks.DRIPSTONE_BLOCK);
      this.dropSelf(Blocks.SPORE_BLOSSOM);
      this.dropSelf(Blocks.FLOWERING_AZALEA);
      this.dropSelf(Blocks.AZALEA);
      this.dropSelf(Blocks.MOSS_CARPET);
      this.add(Blocks.PINK_PETALS, this.createPetalsDrops(Blocks.PINK_PETALS));
      this.dropSelf(Blocks.BIG_DRIPLEAF);
      this.dropSelf(Blocks.MOSS_BLOCK);
      this.dropSelf(Blocks.ROOTED_DIRT);
      this.dropSelf(Blocks.COBBLED_DEEPSLATE);
      this.dropSelf(Blocks.COBBLED_DEEPSLATE_STAIRS);
      this.dropSelf(Blocks.COBBLED_DEEPSLATE_WALL);
      this.dropSelf(Blocks.POLISHED_DEEPSLATE);
      this.dropSelf(Blocks.POLISHED_DEEPSLATE_STAIRS);
      this.dropSelf(Blocks.POLISHED_DEEPSLATE_WALL);
      this.dropSelf(Blocks.DEEPSLATE_TILES);
      this.dropSelf(Blocks.DEEPSLATE_TILE_STAIRS);
      this.dropSelf(Blocks.DEEPSLATE_TILE_WALL);
      this.dropSelf(Blocks.DEEPSLATE_BRICKS);
      this.dropSelf(Blocks.DEEPSLATE_BRICK_STAIRS);
      this.dropSelf(Blocks.DEEPSLATE_BRICK_WALL);
      this.dropSelf(Blocks.CHISELED_DEEPSLATE);
      this.dropSelf(Blocks.CRACKED_DEEPSLATE_BRICKS);
      this.dropSelf(Blocks.CRACKED_DEEPSLATE_TILES);
      this.dropSelf(Blocks.RAW_IRON_BLOCK);
      this.dropSelf(Blocks.RAW_COPPER_BLOCK);
      this.dropSelf(Blocks.RAW_GOLD_BLOCK);
      this.dropSelf(Blocks.OCHRE_FROGLIGHT);
      this.dropSelf(Blocks.VERDANT_FROGLIGHT);
      this.dropSelf(Blocks.PEARLESCENT_FROGLIGHT);
      this.dropSelf(Blocks.MANGROVE_ROOTS);
      this.dropSelf(Blocks.MANGROVE_LOG);
      this.dropSelf(Blocks.MUD);
      this.dropSelf(Blocks.PACKED_MUD);
      this.dropOther(Blocks.FARMLAND, Blocks.DIRT);
      this.dropOther(Blocks.TRIPWIRE, Items.STRING);
      this.dropOther(Blocks.DIRT_PATH, Blocks.DIRT);
      this.dropOther(Blocks.KELP_PLANT, Blocks.KELP);
      this.dropOther(Blocks.BAMBOO_SAPLING, Blocks.BAMBOO);
      this.dropOther(Blocks.WATER_CAULDRON, Blocks.CAULDRON);
      this.dropOther(Blocks.LAVA_CAULDRON, Blocks.CAULDRON);
      this.dropOther(Blocks.POWDER_SNOW_CAULDRON, Blocks.CAULDRON);
      this.dropOther(Blocks.BIG_DRIPLEAF_STEM, Blocks.BIG_DRIPLEAF);
      this.add(Blocks.STONE, (p_251015_) -> {
         return this.createSingleItemTableWithSilkTouch(p_251015_, Blocks.COBBLESTONE);
      });
      this.add(Blocks.DEEPSLATE, (p_249052_) -> {
         return this.createSingleItemTableWithSilkTouch(p_249052_, Blocks.COBBLED_DEEPSLATE);
      });
      this.add(Blocks.GRASS_BLOCK, (p_249779_) -> {
         return this.createSingleItemTableWithSilkTouch(p_249779_, Blocks.DIRT);
      });
      this.add(Blocks.PODZOL, (p_250966_) -> {
         return this.createSingleItemTableWithSilkTouch(p_250966_, Blocks.DIRT);
      });
      this.add(Blocks.MYCELIUM, (p_250352_) -> {
         return this.createSingleItemTableWithSilkTouch(p_250352_, Blocks.DIRT);
      });
      this.add(Blocks.TUBE_CORAL_BLOCK, (p_248608_) -> {
         return this.createSingleItemTableWithSilkTouch(p_248608_, Blocks.DEAD_TUBE_CORAL_BLOCK);
      });
      this.add(Blocks.BRAIN_CORAL_BLOCK, (p_251175_) -> {
         return this.createSingleItemTableWithSilkTouch(p_251175_, Blocks.DEAD_BRAIN_CORAL_BLOCK);
      });
      this.add(Blocks.BUBBLE_CORAL_BLOCK, (p_249280_) -> {
         return this.createSingleItemTableWithSilkTouch(p_249280_, Blocks.DEAD_BUBBLE_CORAL_BLOCK);
      });
      this.add(Blocks.FIRE_CORAL_BLOCK, (p_249630_) -> {
         return this.createSingleItemTableWithSilkTouch(p_249630_, Blocks.DEAD_FIRE_CORAL_BLOCK);
      });
      this.add(Blocks.HORN_CORAL_BLOCK, (p_248518_) -> {
         return this.createSingleItemTableWithSilkTouch(p_248518_, Blocks.DEAD_HORN_CORAL_BLOCK);
      });
      this.add(Blocks.CRIMSON_NYLIUM, (p_251277_) -> {
         return this.createSingleItemTableWithSilkTouch(p_251277_, Blocks.NETHERRACK);
      });
      this.add(Blocks.WARPED_NYLIUM, (p_252157_) -> {
         return this.createSingleItemTableWithSilkTouch(p_252157_, Blocks.NETHERRACK);
      });
      this.add(Blocks.BOOKSHELF, (p_250379_) -> {
         return this.createSingleItemTableWithSilkTouch(p_250379_, Items.BOOK, ConstantValue.exactly(3.0F));
      });
      this.add(Blocks.CLAY, (p_251028_) -> {
         return this.createSingleItemTableWithSilkTouch(p_251028_, Items.CLAY_BALL, ConstantValue.exactly(4.0F));
      });
      this.add(Blocks.ENDER_CHEST, (p_251671_) -> {
         return this.createSingleItemTableWithSilkTouch(p_251671_, Blocks.OBSIDIAN, ConstantValue.exactly(8.0F));
      });
      this.add(Blocks.SNOW_BLOCK, (p_251149_) -> {
         return this.createSingleItemTableWithSilkTouch(p_251149_, Items.SNOWBALL, ConstantValue.exactly(4.0F));
      });
      this.add(Blocks.CHORUS_PLANT, this.createSingleItemTable(Items.CHORUS_FRUIT, UniformGenerator.between(0.0F, 1.0F)));
      this.dropPottedContents(Blocks.POTTED_OAK_SAPLING);
      this.dropPottedContents(Blocks.POTTED_SPRUCE_SAPLING);
      this.dropPottedContents(Blocks.POTTED_BIRCH_SAPLING);
      this.dropPottedContents(Blocks.POTTED_JUNGLE_SAPLING);
      this.dropPottedContents(Blocks.POTTED_ACACIA_SAPLING);
      this.dropPottedContents(Blocks.POTTED_DARK_OAK_SAPLING);
      this.dropPottedContents(Blocks.POTTED_MANGROVE_PROPAGULE);
      this.dropPottedContents(Blocks.POTTED_CHERRY_SAPLING);
      this.dropPottedContents(Blocks.POTTED_FERN);
      this.dropPottedContents(Blocks.POTTED_DANDELION);
      this.dropPottedContents(Blocks.POTTED_POPPY);
      this.dropPottedContents(Blocks.POTTED_BLUE_ORCHID);
      this.dropPottedContents(Blocks.POTTED_ALLIUM);
      this.dropPottedContents(Blocks.POTTED_AZURE_BLUET);
      this.dropPottedContents(Blocks.POTTED_RED_TULIP);
      this.dropPottedContents(Blocks.POTTED_ORANGE_TULIP);
      this.dropPottedContents(Blocks.POTTED_WHITE_TULIP);
      this.dropPottedContents(Blocks.POTTED_PINK_TULIP);
      this.dropPottedContents(Blocks.POTTED_OXEYE_DAISY);
      this.dropPottedContents(Blocks.POTTED_CORNFLOWER);
      this.dropPottedContents(Blocks.POTTED_LILY_OF_THE_VALLEY);
      this.dropPottedContents(Blocks.POTTED_WITHER_ROSE);
      this.dropPottedContents(Blocks.POTTED_RED_MUSHROOM);
      this.dropPottedContents(Blocks.POTTED_BROWN_MUSHROOM);
      this.dropPottedContents(Blocks.POTTED_DEAD_BUSH);
      this.dropPottedContents(Blocks.POTTED_CACTUS);
      this.dropPottedContents(Blocks.POTTED_BAMBOO);
      this.dropPottedContents(Blocks.POTTED_CRIMSON_FUNGUS);
      this.dropPottedContents(Blocks.POTTED_WARPED_FUNGUS);
      this.dropPottedContents(Blocks.POTTED_CRIMSON_ROOTS);
      this.dropPottedContents(Blocks.POTTED_WARPED_ROOTS);
      this.dropPottedContents(Blocks.POTTED_AZALEA);
      this.dropPottedContents(Blocks.POTTED_FLOWERING_AZALEA);
      this.dropPottedContents(Blocks.POTTED_TORCHFLOWER);
      this.add(Blocks.OAK_SLAB, (p_251629_) -> {
         return this.createSlabItemTable(p_251629_);
      });
      this.add(Blocks.PETRIFIED_OAK_SLAB, (p_251237_) -> {
         return this.createSlabItemTable(p_251237_);
      });
      this.add(Blocks.SPRUCE_SLAB, (p_249325_) -> {
         return this.createSlabItemTable(p_249325_);
      });
      this.add(Blocks.BIRCH_SLAB, (p_249953_) -> {
         return this.createSlabItemTable(p_249953_);
      });
      this.add(Blocks.JUNGLE_SLAB, (p_251725_) -> {
         return this.createSlabItemTable(p_251725_);
      });
      this.add(Blocks.ACACIA_SLAB, (p_248946_) -> {
         return this.createSlabItemTable(p_248946_);
      });
      this.add(Blocks.DARK_OAK_SLAB, (p_249873_) -> {
         return this.createSlabItemTable(p_249873_);
      });
      this.add(Blocks.MANGROVE_SLAB, (p_250611_) -> {
         return this.createSlabItemTable(p_250611_);
      });
      this.add(Blocks.CHERRY_SLAB, (p_250363_) -> {
         return this.createSlabItemTable(p_250363_);
      });
      this.add(Blocks.BAMBOO_SLAB, (p_249255_) -> {
         return this.createSlabItemTable(p_249255_);
      });
      this.add(Blocks.BAMBOO_MOSAIC_SLAB, (p_249820_) -> {
         return this.createSlabItemTable(p_249820_);
      });
      this.add(Blocks.BRICK_SLAB, (p_250846_) -> {
         return this.createSlabItemTable(p_250846_);
      });
      this.add(Blocks.COBBLESTONE_SLAB, (p_249031_) -> {
         return this.createSlabItemTable(p_249031_);
      });
      this.add(Blocks.DARK_PRISMARINE_SLAB, (p_251560_) -> {
         return this.createSlabItemTable(p_251560_);
      });
      this.add(Blocks.NETHER_BRICK_SLAB, (p_250571_) -> {
         return this.createSlabItemTable(p_250571_);
      });
      this.add(Blocks.PRISMARINE_BRICK_SLAB, (p_250698_) -> {
         return this.createSlabItemTable(p_250698_);
      });
      this.add(Blocks.PRISMARINE_SLAB, (p_250985_) -> {
         return this.createSlabItemTable(p_250985_);
      });
      this.add(Blocks.PURPUR_SLAB, (p_250829_) -> {
         return this.createSlabItemTable(p_250829_);
      });
      this.add(Blocks.QUARTZ_SLAB, (p_251720_) -> {
         return this.createSlabItemTable(p_251720_);
      });
      this.add(Blocks.RED_SANDSTONE_SLAB, (p_249342_) -> {
         return this.createSlabItemTable(p_249342_);
      });
      this.add(Blocks.SANDSTONE_SLAB, (p_250259_) -> {
         return this.createSlabItemTable(p_250259_);
      });
      this.add(Blocks.CUT_RED_SANDSTONE_SLAB, (p_251501_) -> {
         return this.createSlabItemTable(p_251501_);
      });
      this.add(Blocks.CUT_SANDSTONE_SLAB, (p_250933_) -> {
         return this.createSlabItemTable(p_250933_);
      });
      this.add(Blocks.STONE_BRICK_SLAB, (p_248836_) -> {
         return this.createSlabItemTable(p_248836_);
      });
      this.add(Blocks.STONE_SLAB, (p_252142_) -> {
         return this.createSlabItemTable(p_252142_);
      });
      this.add(Blocks.SMOOTH_STONE_SLAB, (p_248657_) -> {
         return this.createSlabItemTable(p_248657_);
      });
      this.add(Blocks.POLISHED_GRANITE_SLAB, (p_249451_) -> {
         return this.createSlabItemTable(p_249451_);
      });
      this.add(Blocks.SMOOTH_RED_SANDSTONE_SLAB, (p_249500_) -> {
         return this.createSlabItemTable(p_249500_);
      });
      this.add(Blocks.MOSSY_STONE_BRICK_SLAB, (p_249589_) -> {
         return this.createSlabItemTable(p_249589_);
      });
      this.add(Blocks.POLISHED_DIORITE_SLAB, (p_251378_) -> {
         return this.createSlabItemTable(p_251378_);
      });
      this.add(Blocks.MOSSY_COBBLESTONE_SLAB, (p_251340_) -> {
         return this.createSlabItemTable(p_251340_);
      });
      this.add(Blocks.END_STONE_BRICK_SLAB, (p_252148_) -> {
         return this.createSlabItemTable(p_252148_);
      });
      this.add(Blocks.SMOOTH_SANDSTONE_SLAB, (p_248742_) -> {
         return this.createSlabItemTable(p_248742_);
      });
      this.add(Blocks.SMOOTH_QUARTZ_SLAB, (p_252320_) -> {
         return this.createSlabItemTable(p_252320_);
      });
      this.add(Blocks.GRANITE_SLAB, (p_249683_) -> {
         return this.createSlabItemTable(p_249683_);
      });
      this.add(Blocks.ANDESITE_SLAB, (p_249813_) -> {
         return this.createSlabItemTable(p_249813_);
      });
      this.add(Blocks.RED_NETHER_BRICK_SLAB, (p_250170_) -> {
         return this.createSlabItemTable(p_250170_);
      });
      this.add(Blocks.POLISHED_ANDESITE_SLAB, (p_249068_) -> {
         return this.createSlabItemTable(p_249068_);
      });
      this.add(Blocks.DIORITE_SLAB, (p_251963_) -> {
         return this.createSlabItemTable(p_251963_);
      });
      this.add(Blocks.CRIMSON_SLAB, (p_250291_) -> {
         return this.createSlabItemTable(p_250291_);
      });
      this.add(Blocks.WARPED_SLAB, (p_251741_) -> {
         return this.createSlabItemTable(p_251741_);
      });
      this.add(Blocks.BLACKSTONE_SLAB, (p_249431_) -> {
         return this.createSlabItemTable(p_249431_);
      });
      this.add(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, (p_249660_) -> {
         return this.createSlabItemTable(p_249660_);
      });
      this.add(Blocks.POLISHED_BLACKSTONE_SLAB, (p_249229_) -> {
         return this.createSlabItemTable(p_249229_);
      });
      this.add(Blocks.OXIDIZED_CUT_COPPER_SLAB, (p_251568_) -> {
         return this.createSlabItemTable(p_251568_);
      });
      this.add(Blocks.WEATHERED_CUT_COPPER_SLAB, (p_248799_) -> {
         return this.createSlabItemTable(p_248799_);
      });
      this.add(Blocks.EXPOSED_CUT_COPPER_SLAB, (p_249358_) -> {
         return this.createSlabItemTable(p_249358_);
      });
      this.add(Blocks.CUT_COPPER_SLAB, (p_252044_) -> {
         return this.createSlabItemTable(p_252044_);
      });
      this.add(Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, (p_251336_) -> {
         return this.createSlabItemTable(p_251336_);
      });
      this.add(Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, (p_251951_) -> {
         return this.createSlabItemTable(p_251951_);
      });
      this.add(Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, (p_250882_) -> {
         return this.createSlabItemTable(p_250882_);
      });
      this.add(Blocks.WAXED_CUT_COPPER_SLAB, (p_249866_) -> {
         return this.createSlabItemTable(p_249866_);
      });
      this.add(Blocks.COBBLED_DEEPSLATE_SLAB, (p_252348_) -> {
         return this.createSlabItemTable(p_252348_);
      });
      this.add(Blocks.POLISHED_DEEPSLATE_SLAB, (p_248980_) -> {
         return this.createSlabItemTable(p_248980_);
      });
      this.add(Blocks.DEEPSLATE_TILE_SLAB, (p_277247_) -> {
         return this.createSlabItemTable(p_277247_);
      });
      this.add(Blocks.DEEPSLATE_BRICK_SLAB, (p_277242_) -> {
         return this.createSlabItemTable(p_277242_);
      });
      this.add(Blocks.MUD_BRICK_SLAB, (p_277239_) -> {
         return this.createSlabItemTable(p_277239_);
      });
      this.add(Blocks.OAK_DOOR, (p_272365_) -> {
         return this.createDoorTable(p_272365_);
      });
      this.add(Blocks.SPRUCE_DOOR, (p_272370_) -> {
         return this.createDoorTable(p_272370_);
      });
      this.add(Blocks.BIRCH_DOOR, (p_272372_) -> {
         return this.createDoorTable(p_272372_);
      });
      this.add(Blocks.JUNGLE_DOOR, (p_272374_) -> {
         return this.createDoorTable(p_272374_);
      });
      this.add(Blocks.ACACIA_DOOR, (p_272357_) -> {
         return this.createDoorTable(p_272357_);
      });
      this.add(Blocks.DARK_OAK_DOOR, (p_272359_) -> {
         return this.createDoorTable(p_272359_);
      });
      this.add(Blocks.MANGROVE_DOOR, (p_272363_) -> {
         return this.createDoorTable(p_272363_);
      });
      this.add(Blocks.CHERRY_DOOR, (p_272367_) -> {
         return this.createDoorTable(p_272367_);
      });
      this.add(Blocks.BAMBOO_DOOR, (p_272361_) -> {
         return this.createDoorTable(p_272361_);
      });
      this.add(Blocks.WARPED_DOOR, (p_272376_) -> {
         return this.createDoorTable(p_272376_);
      });
      this.add(Blocks.CRIMSON_DOOR, (p_277245_) -> {
         return this.createDoorTable(p_277245_);
      });
      this.add(Blocks.IRON_DOOR, (p_277250_) -> {
         return this.createDoorTable(p_277250_);
      });
      this.add(Blocks.BLACK_BED, (p_249155_) -> {
         return this.createSinglePropConditionTable(p_249155_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.BLUE_BED, (p_249678_) -> {
         return this.createSinglePropConditionTable(p_249678_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.BROWN_BED, (p_250887_) -> {
         return this.createSinglePropConditionTable(p_250887_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.CYAN_BED, (p_251388_) -> {
         return this.createSinglePropConditionTable(p_251388_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.GRAY_BED, (p_249449_) -> {
         return this.createSinglePropConditionTable(p_249449_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.GREEN_BED, (p_251562_) -> {
         return this.createSinglePropConditionTable(p_251562_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.LIGHT_BLUE_BED, (p_249639_) -> {
         return this.createSinglePropConditionTable(p_249639_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.LIGHT_GRAY_BED, (p_248689_) -> {
         return this.createSinglePropConditionTable(p_248689_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.LIME_BED, (p_252284_) -> {
         return this.createSinglePropConditionTable(p_252284_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.MAGENTA_BED, (p_250939_) -> {
         return this.createSinglePropConditionTable(p_250939_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.PURPLE_BED, (p_250517_) -> {
         return this.createSinglePropConditionTable(p_250517_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.ORANGE_BED, (p_251619_) -> {
         return this.createSinglePropConditionTable(p_251619_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.PINK_BED, (p_250267_) -> {
         return this.createSinglePropConditionTable(p_250267_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.RED_BED, (p_252081_) -> {
         return this.createSinglePropConditionTable(p_252081_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.WHITE_BED, (p_251275_) -> {
         return this.createSinglePropConditionTable(p_251275_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.YELLOW_BED, (p_252316_) -> {
         return this.createSinglePropConditionTable(p_252316_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.LILAC, (p_250918_) -> {
         return this.createSinglePropConditionTable(p_250918_, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.add(Blocks.SUNFLOWER, (p_250043_) -> {
         return this.createSinglePropConditionTable(p_250043_, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.add(Blocks.PEONY, (p_248541_) -> {
         return this.createSinglePropConditionTable(p_248541_, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.add(Blocks.ROSE_BUSH, (p_250741_) -> {
         return this.createSinglePropConditionTable(p_250741_, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.add(Blocks.TNT, LootTable.lootTable().withPool(this.applyExplosionCondition(Blocks.TNT, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Blocks.TNT).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.TNT).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(TntBlock.UNSTABLE, false)))))));
      this.add(Blocks.COCOA, (p_250228_) -> {
         return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(this.applyExplosionDecay(p_250228_, LootItem.lootTableItem(Items.COCOA_BEANS).apply(SetItemCountFunction.setCount(ConstantValue.exactly(3.0F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_250228_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CocoaBlock.AGE, 2)))))));
      });
      this.add(Blocks.SEA_PICKLE, (p_248918_) -> {
         return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(this.applyExplosionDecay(Blocks.SEA_PICKLE, LootItem.lootTableItem(p_248918_).apply(List.of(2, 3, 4), (p_251952_) -> {
            return SetItemCountFunction.setCount(ConstantValue.exactly((float)p_251952_.intValue())).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_248918_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SeaPickleBlock.PICKLES, p_251952_)));
         }))));
      });
      this.add(Blocks.COMPOSTER, (p_250064_) -> {
         return LootTable.lootTable().withPool(LootPool.lootPool().add(this.applyExplosionDecay(p_250064_, LootItem.lootTableItem(Items.COMPOSTER)))).withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.BONE_MEAL)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_250064_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ComposterBlock.LEVEL, 8))));
      });
      this.add(Blocks.CAVE_VINES, (p_250233_) -> {
         return BlockLootSubProvider.createCaveVinesDrop(p_250233_);
      });
      this.add(Blocks.CAVE_VINES_PLANT, (p_249628_) -> {
         return BlockLootSubProvider.createCaveVinesDrop(p_249628_);
      });
      this.add(Blocks.CANDLE, (p_250477_) -> {
         return this.createCandleDrops(p_250477_);
      });
      this.add(Blocks.WHITE_CANDLE, (p_250214_) -> {
         return this.createCandleDrops(p_250214_);
      });
      this.add(Blocks.ORANGE_CANDLE, (p_250876_) -> {
         return this.createCandleDrops(p_250876_);
      });
      this.add(Blocks.MAGENTA_CANDLE, (p_249556_) -> {
         return this.createCandleDrops(p_249556_);
      });
      this.add(Blocks.LIGHT_BLUE_CANDLE, (p_250463_) -> {
         return this.createCandleDrops(p_250463_);
      });
      this.add(Blocks.YELLOW_CANDLE, (p_250078_) -> {
         return this.createCandleDrops(p_250078_);
      });
      this.add(Blocks.LIME_CANDLE, (p_250051_) -> {
         return this.createCandleDrops(p_250051_);
      });
      this.add(Blocks.PINK_CANDLE, (p_249078_) -> {
         return this.createCandleDrops(p_249078_);
      });
      this.add(Blocks.GRAY_CANDLE, (p_249133_) -> {
         return this.createCandleDrops(p_249133_);
      });
      this.add(Blocks.LIGHT_GRAY_CANDLE, (p_251324_) -> {
         return this.createCandleDrops(p_251324_);
      });
      this.add(Blocks.CYAN_CANDLE, (p_248584_) -> {
         return this.createCandleDrops(p_248584_);
      });
      this.add(Blocks.PURPLE_CANDLE, (p_251632_) -> {
         return this.createCandleDrops(p_251632_);
      });
      this.add(Blocks.BLUE_CANDLE, (p_250130_) -> {
         return this.createCandleDrops(p_250130_);
      });
      this.add(Blocks.BROWN_CANDLE, (p_250337_) -> {
         return this.createCandleDrops(p_250337_);
      });
      this.add(Blocks.GREEN_CANDLE, (p_250143_) -> {
         return this.createCandleDrops(p_250143_);
      });
      this.add(Blocks.RED_CANDLE, (p_252244_) -> {
         return this.createCandleDrops(p_252244_);
      });
      this.add(Blocks.BLACK_CANDLE, (p_248975_) -> {
         return this.createCandleDrops(p_248975_);
      });
      this.add(Blocks.BEACON, (p_251287_) -> {
         return this.createNameableBlockEntityTable(p_251287_);
      });
      this.add(Blocks.BREWING_STAND, (p_250976_) -> {
         return this.createNameableBlockEntityTable(p_250976_);
      });
      this.add(Blocks.CHEST, (p_250538_) -> {
         return this.createNameableBlockEntityTable(p_250538_);
      });
      this.add(Blocks.DISPENSER, (p_250717_) -> {
         return this.createNameableBlockEntityTable(p_250717_);
      });
      this.add(Blocks.DROPPER, (p_250178_) -> {
         return this.createNameableBlockEntityTable(p_250178_);
      });
      this.add(Blocks.ENCHANTING_TABLE, (p_251962_) -> {
         return this.createNameableBlockEntityTable(p_251962_);
      });
      this.add(Blocks.FURNACE, (p_250520_) -> {
         return this.createNameableBlockEntityTable(p_250520_);
      });
      this.add(Blocks.HOPPER, (p_252043_) -> {
         return this.createNameableBlockEntityTable(p_252043_);
      });
      this.add(Blocks.TRAPPED_CHEST, (p_248545_) -> {
         return this.createNameableBlockEntityTable(p_248545_);
      });
      this.add(Blocks.SMOKER, (p_250001_) -> {
         return this.createNameableBlockEntityTable(p_250001_);
      });
      this.add(Blocks.BLAST_FURNACE, (p_250727_) -> {
         return this.createNameableBlockEntityTable(p_250727_);
      });
      this.add(Blocks.BARREL, (p_249831_) -> {
         return this.createNameableBlockEntityTable(p_249831_);
      });
      this.dropSelf(Blocks.CARTOGRAPHY_TABLE);
      this.dropSelf(Blocks.FLETCHING_TABLE);
      this.dropSelf(Blocks.GRINDSTONE);
      this.dropSelf(Blocks.LECTERN);
      this.dropSelf(Blocks.SMITHING_TABLE);
      this.dropSelf(Blocks.STONECUTTER);
      this.add(Blocks.BELL, this::createSingleItemTable);
      this.add(Blocks.LANTERN, this::createSingleItemTable);
      this.add(Blocks.SOUL_LANTERN, this::createSingleItemTable);
      this.add(Blocks.SHULKER_BOX, (p_251205_) -> {
         return this.createShulkerBoxDrop(p_251205_);
      });
      this.add(Blocks.BLACK_SHULKER_BOX, (p_248609_) -> {
         return this.createShulkerBoxDrop(p_248609_);
      });
      this.add(Blocks.BLUE_SHULKER_BOX, (p_248838_) -> {
         return this.createShulkerBoxDrop(p_248838_);
      });
      this.add(Blocks.BROWN_SHULKER_BOX, (p_250222_) -> {
         return this.createShulkerBoxDrop(p_250222_);
      });
      this.add(Blocks.CYAN_SHULKER_BOX, (p_250378_) -> {
         return this.createShulkerBoxDrop(p_250378_);
      });
      this.add(Blocks.GRAY_SHULKER_BOX, (p_250962_) -> {
         return this.createShulkerBoxDrop(p_250962_);
      });
      this.add(Blocks.GREEN_SHULKER_BOX, (p_248769_) -> {
         return this.createShulkerBoxDrop(p_248769_);
      });
      this.add(Blocks.LIGHT_BLUE_SHULKER_BOX, (p_250955_) -> {
         return this.createShulkerBoxDrop(p_250955_);
      });
      this.add(Blocks.LIGHT_GRAY_SHULKER_BOX, (p_251370_) -> {
         return this.createShulkerBoxDrop(p_251370_);
      });
      this.add(Blocks.LIME_SHULKER_BOX, (p_250906_) -> {
         return this.createShulkerBoxDrop(p_250906_);
      });
      this.add(Blocks.MAGENTA_SHULKER_BOX, (p_250875_) -> {
         return this.createShulkerBoxDrop(p_250875_);
      });
      this.add(Blocks.ORANGE_SHULKER_BOX, (p_250718_) -> {
         return this.createShulkerBoxDrop(p_250718_);
      });
      this.add(Blocks.PINK_SHULKER_BOX, (p_248646_) -> {
         return this.createShulkerBoxDrop(p_248646_);
      });
      this.add(Blocks.PURPLE_SHULKER_BOX, (p_252160_) -> {
         return this.createShulkerBoxDrop(p_252160_);
      });
      this.add(Blocks.RED_SHULKER_BOX, (p_248952_) -> {
         return this.createShulkerBoxDrop(p_248952_);
      });
      this.add(Blocks.WHITE_SHULKER_BOX, (p_248577_) -> {
         return this.createShulkerBoxDrop(p_248577_);
      });
      this.add(Blocks.YELLOW_SHULKER_BOX, (p_249015_) -> {
         return this.createShulkerBoxDrop(p_249015_);
      });
      this.add(Blocks.BLACK_BANNER, (p_251605_) -> {
         return this.createBannerDrop(p_251605_);
      });
      this.add(Blocks.BLUE_BANNER, (p_249719_) -> {
         return this.createBannerDrop(p_249719_);
      });
      this.add(Blocks.BROWN_BANNER, (p_249234_) -> {
         return this.createBannerDrop(p_249234_);
      });
      this.add(Blocks.CYAN_BANNER, (p_248917_) -> {
         return this.createBannerDrop(p_248917_);
      });
      this.add(Blocks.GRAY_BANNER, (p_251436_) -> {
         return this.createBannerDrop(p_251436_);
      });
      this.add(Blocks.GREEN_BANNER, (p_248899_) -> {
         return this.createBannerDrop(p_248899_);
      });
      this.add(Blocks.LIGHT_BLUE_BANNER, (p_251366_) -> {
         return this.createBannerDrop(p_251366_);
      });
      this.add(Blocks.LIGHT_GRAY_BANNER, (p_249288_) -> {
         return this.createBannerDrop(p_249288_);
      });
      this.add(Blocks.LIME_BANNER, (p_250643_) -> {
         return this.createBannerDrop(p_250643_);
      });
      this.add(Blocks.MAGENTA_BANNER, (p_251547_) -> {
         return this.createBannerDrop(p_251547_);
      });
      this.add(Blocks.ORANGE_BANNER, (p_249883_) -> {
         return this.createBannerDrop(p_249883_);
      });
      this.add(Blocks.PINK_BANNER, (p_251753_) -> {
         return this.createBannerDrop(p_251753_);
      });
      this.add(Blocks.PURPLE_BANNER, (p_249693_) -> {
         return this.createBannerDrop(p_249693_);
      });
      this.add(Blocks.RED_BANNER, (p_250920_) -> {
         return this.createBannerDrop(p_250920_);
      });
      this.add(Blocks.WHITE_BANNER, (p_250557_) -> {
         return this.createBannerDrop(p_250557_);
      });
      this.add(Blocks.YELLOW_BANNER, (p_249072_) -> {
         return this.createBannerDrop(p_249072_);
      });
      this.add(Blocks.PLAYER_HEAD, (p_250163_) -> {
         return LootTable.lootTable().withPool(this.applyExplosionCondition(p_250163_, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_250163_).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("SkullOwner", "SkullOwner").copy("note_block_sound", String.format(Locale.ROOT, "%s.%s", "BlockEntityTag", "note_block_sound"))))));
      });
      this.add(Blocks.BEE_NEST, (p_250755_) -> {
         return createBeeNestDrop(p_250755_);
      });
      this.add(Blocks.BEEHIVE, (p_250779_) -> {
         return createBeeHiveDrop(p_250779_);
      });
      this.add(Blocks.OAK_LEAVES, (p_280934_) -> {
         return this.createOakLeavesDrops(p_280934_, Blocks.OAK_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      this.add(Blocks.SPRUCE_LEAVES, (p_280940_) -> {
         return this.createLeavesDrops(p_280940_, Blocks.SPRUCE_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      this.add(Blocks.BIRCH_LEAVES, (p_280937_) -> {
         return this.createLeavesDrops(p_280937_, Blocks.BIRCH_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      this.add(Blocks.JUNGLE_LEAVES, (p_249084_) -> {
         return this.createLeavesDrops(p_249084_, Blocks.JUNGLE_SAPLING, JUNGLE_LEAVES_SAPLING_CHANGES);
      });
      this.add(Blocks.ACACIA_LEAVES, (p_280939_) -> {
         return this.createLeavesDrops(p_280939_, Blocks.ACACIA_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      this.add(Blocks.DARK_OAK_LEAVES, (p_280933_) -> {
         return this.createOakLeavesDrops(p_280933_, Blocks.DARK_OAK_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      this.add(Blocks.CHERRY_LEAVES, (p_280938_) -> {
         return this.createLeavesDrops(p_280938_, Blocks.CHERRY_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      this.add(Blocks.AZALEA_LEAVES, (p_280936_) -> {
         return this.createLeavesDrops(p_280936_, Blocks.AZALEA, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      this.add(Blocks.FLOWERING_AZALEA_LEAVES, (p_280935_) -> {
         return this.createLeavesDrops(p_280935_, Blocks.FLOWERING_AZALEA, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      LootItemCondition.Builder lootitemcondition$builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.BEETROOTS).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BeetrootBlock.AGE, 3));
      this.add(Blocks.BEETROOTS, this.createCropDrops(Blocks.BEETROOTS, Items.BEETROOT, Items.BEETROOT_SEEDS, lootitemcondition$builder));
      LootItemCondition.Builder lootitemcondition$builder1 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.WHEAT).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, 7));
      this.add(Blocks.WHEAT, this.createCropDrops(Blocks.WHEAT, Items.WHEAT, Items.WHEAT_SEEDS, lootitemcondition$builder1));
      LootItemCondition.Builder lootitemcondition$builder2 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.CARROTS).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CarrotBlock.AGE, 7));
      LootItemCondition.Builder lootitemcondition$builder3 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.MANGROVE_PROPAGULE).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MangrovePropaguleBlock.AGE, 4));
      this.add(Blocks.MANGROVE_PROPAGULE, this.applyExplosionDecay(Blocks.MANGROVE_PROPAGULE, LootTable.lootTable().withPool(LootPool.lootPool().when(lootitemcondition$builder3).add(LootItem.lootTableItem(Items.MANGROVE_PROPAGULE)))));
      this.add(Blocks.TORCHFLOWER_CROP, this.applyExplosionDecay(Blocks.TORCHFLOWER_CROP, LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.TORCHFLOWER_SEEDS)))));
      this.dropSelf(Blocks.SNIFFER_EGG);
      this.add(Blocks.PITCHER_CROP, (p_277240_) -> {
         return this.createPitcherCropLoot();
      });
      this.dropSelf(Blocks.PITCHER_PLANT);
      this.add(Blocks.PITCHER_PLANT, this.applyExplosionDecay(Blocks.PITCHER_PLANT, LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.PITCHER_PLANT).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.PITCHER_PLANT).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)))))));
      this.add(Blocks.CARROTS, this.applyExplosionDecay(Blocks.CARROTS, LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.CARROT))).withPool(LootPool.lootPool().when(lootitemcondition$builder2).add(LootItem.lootTableItem(Items.CARROT).apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))))));
      LootItemCondition.Builder lootitemcondition$builder4 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.POTATOES).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PotatoBlock.AGE, 7));
      this.add(Blocks.POTATOES, this.applyExplosionDecay(Blocks.POTATOES, LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.POTATO))).withPool(LootPool.lootPool().when(lootitemcondition$builder4).add(LootItem.lootTableItem(Items.POTATO).apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3)))).withPool(LootPool.lootPool().when(lootitemcondition$builder4).add(LootItem.lootTableItem(Items.POISONOUS_POTATO).when(LootItemRandomChanceCondition.randomChance(0.02F))))));
      this.add(Blocks.SWEET_BERRY_BUSH, (p_249159_) -> {
         return this.applyExplosionDecay(p_249159_, LootTable.lootTable().withPool(LootPool.lootPool().when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SWEET_BERRY_BUSH).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 3))).add(LootItem.lootTableItem(Items.SWEET_BERRIES)).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))).withPool(LootPool.lootPool().when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SWEET_BERRY_BUSH).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 2))).add(LootItem.lootTableItem(Items.SWEET_BERRIES)).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))));
      });
      this.add(Blocks.BROWN_MUSHROOM_BLOCK, (p_249169_) -> {
         return this.createMushroomBlockDrop(p_249169_, Blocks.BROWN_MUSHROOM);
      });
      this.add(Blocks.RED_MUSHROOM_BLOCK, (p_248785_) -> {
         return this.createMushroomBlockDrop(p_248785_, Blocks.RED_MUSHROOM);
      });
      this.add(Blocks.COAL_ORE, (p_250359_) -> {
         return this.createOreDrop(p_250359_, Items.COAL);
      });
      this.add(Blocks.DEEPSLATE_COAL_ORE, (p_250095_) -> {
         return this.createOreDrop(p_250095_, Items.COAL);
      });
      this.add(Blocks.EMERALD_ORE, (p_249260_) -> {
         return this.createOreDrop(p_249260_, Items.EMERALD);
      });
      this.add(Blocks.DEEPSLATE_EMERALD_ORE, (p_251355_) -> {
         return this.createOreDrop(p_251355_, Items.EMERALD);
      });
      this.add(Blocks.NETHER_QUARTZ_ORE, (p_252000_) -> {
         return this.createOreDrop(p_252000_, Items.QUARTZ);
      });
      this.add(Blocks.DIAMOND_ORE, (p_249219_) -> {
         return this.createOreDrop(p_249219_, Items.DIAMOND);
      });
      this.add(Blocks.DEEPSLATE_DIAMOND_ORE, (p_251035_) -> {
         return this.createOreDrop(p_251035_, Items.DIAMOND);
      });
      this.add(Blocks.COPPER_ORE, (p_249767_) -> {
         return this.createCopperOreDrops(p_249767_);
      });
      this.add(Blocks.DEEPSLATE_COPPER_ORE, (p_251228_) -> {
         return this.createCopperOreDrops(p_251228_);
      });
      this.add(Blocks.IRON_ORE, (p_250898_) -> {
         return this.createOreDrop(p_250898_, Items.RAW_IRON);
      });
      this.add(Blocks.DEEPSLATE_IRON_ORE, (p_249875_) -> {
         return this.createOreDrop(p_249875_, Items.RAW_IRON);
      });
      this.add(Blocks.GOLD_ORE, (p_249323_) -> {
         return this.createOreDrop(p_249323_, Items.RAW_GOLD);
      });
      this.add(Blocks.DEEPSLATE_GOLD_ORE, (p_250687_) -> {
         return this.createOreDrop(p_250687_, Items.RAW_GOLD);
      });
      this.add(Blocks.NETHER_GOLD_ORE, (p_251177_) -> {
         return createSilkTouchDispatchTable(p_251177_, this.applyExplosionDecay(p_251177_, LootItem.lootTableItem(Items.GOLD_NUGGET).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 6.0F))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
      });
      this.add(Blocks.LAPIS_ORE, (p_250507_) -> {
         return this.createLapisOreDrops(p_250507_);
      });
      this.add(Blocks.DEEPSLATE_LAPIS_ORE, (p_251637_) -> {
         return this.createLapisOreDrops(p_251637_);
      });
      this.add(Blocks.COBWEB, (p_250546_) -> {
         return createSilkTouchOrShearsDispatchTable(p_250546_, this.applyExplosionCondition(p_250546_, LootItem.lootTableItem(Items.STRING)));
      });
      this.add(Blocks.DEAD_BUSH, (p_249226_) -> {
         return createShearsDispatchTable(p_249226_, this.applyExplosionDecay(p_249226_, LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))));
      });
      this.add(Blocks.NETHER_SPROUTS, (p_251652_) -> {
         return BlockLootSubProvider.createShearsOnlyDrop(p_251652_);
      });
      this.add(Blocks.SEAGRASS, (p_250181_) -> {
         return BlockLootSubProvider.createShearsOnlyDrop(p_250181_);
      });
      this.add(Blocks.VINE, (p_249748_) -> {
         return BlockLootSubProvider.createShearsOnlyDrop(p_249748_);
      });
      this.add(Blocks.GLOW_LICHEN, (p_249543_) -> {
         return this.createMultifaceBlockDrops(p_249543_, HAS_SHEARS);
      });
      this.add(Blocks.HANGING_ROOTS, (p_252041_) -> {
         return BlockLootSubProvider.createShearsOnlyDrop(p_252041_);
      });
      this.add(Blocks.SMALL_DRIPLEAF, (p_250387_) -> {
         return BlockLootSubProvider.createShearsOnlyDrop(p_250387_);
      });
      this.add(Blocks.MANGROVE_LEAVES, (p_250055_) -> {
         return this.createMangroveLeavesDrops(p_250055_);
      });
      this.add(Blocks.TALL_SEAGRASS, createDoublePlantShearsDrop(Blocks.SEAGRASS));
      this.add(Blocks.LARGE_FERN, (p_248803_) -> {
         return this.createDoublePlantWithSeedDrops(p_248803_, Blocks.FERN);
      });
      this.add(Blocks.TALL_GRASS, (p_251424_) -> {
         return this.createDoublePlantWithSeedDrops(p_251424_, Blocks.GRASS);
      });
      this.add(Blocks.MELON_STEM, (p_249349_) -> {
         return this.createStemDrops(p_249349_, Items.MELON_SEEDS);
      });
      this.add(Blocks.ATTACHED_MELON_STEM, (p_251518_) -> {
         return this.createAttachedStemDrops(p_251518_, Items.MELON_SEEDS);
      });
      this.add(Blocks.PUMPKIN_STEM, (p_252178_) -> {
         return this.createStemDrops(p_252178_, Items.PUMPKIN_SEEDS);
      });
      this.add(Blocks.ATTACHED_PUMPKIN_STEM, (p_250849_) -> {
         return this.createAttachedStemDrops(p_250849_, Items.PUMPKIN_SEEDS);
      });
      this.add(Blocks.CHORUS_FLOWER, (p_249769_) -> {
         return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(this.applyExplosionCondition(p_249769_, LootItem.lootTableItem(p_249769_)).when(LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS))));
      });
      this.add(Blocks.FERN, (p_250650_) -> {
         return this.createGrassDrops(p_250650_);
      });
      this.add(Blocks.GRASS, (p_249038_) -> {
         return this.createGrassDrops(p_249038_);
      });
      this.add(Blocks.GLOWSTONE, (p_251150_) -> {
         return createSilkTouchDispatchTable(p_251150_, this.applyExplosionDecay(p_251150_, LootItem.lootTableItem(Items.GLOWSTONE_DUST).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)).apply(LimitCount.limitCount(IntRange.range(1, 4)))));
      });
      this.add(Blocks.MELON, (p_251856_) -> {
         return createSilkTouchDispatchTable(p_251856_, this.applyExplosionDecay(p_251856_, LootItem.lootTableItem(Items.MELON_SLICE).apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 7.0F))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)).apply(LimitCount.limitCount(IntRange.upperBound(9)))));
      });
      this.add(Blocks.REDSTONE_ORE, (p_249321_) -> {
         return this.createRedstoneOreDrops(p_249321_);
      });
      this.add(Blocks.DEEPSLATE_REDSTONE_ORE, (p_249777_) -> {
         return this.createRedstoneOreDrops(p_249777_);
      });
      this.add(Blocks.SEA_LANTERN, (p_250026_) -> {
         return createSilkTouchDispatchTable(p_250026_, this.applyExplosionDecay(p_250026_, LootItem.lootTableItem(Items.PRISMARINE_CRYSTALS).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)).apply(LimitCount.limitCount(IntRange.range(1, 5)))));
      });
      this.add(Blocks.NETHER_WART, (p_250097_) -> {
         return LootTable.lootTable().withPool(this.applyExplosionDecay(p_250097_, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Items.NETHER_WART).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_250097_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(NetherWartBlock.AGE, 3)))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_250097_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(NetherWartBlock.AGE, 3)))))));
      });
      this.add(Blocks.SNOW, (p_251108_) -> {
         return LootTable.lootTable().withPool(LootPool.lootPool().when(LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS)).add(AlternativesEntry.alternatives(AlternativesEntry.alternatives(SnowLayerBlock.LAYERS.getPossibleValues(), (p_252097_) -> {
            return LootItem.lootTableItem(Items.SNOWBALL).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_251108_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, p_252097_))).apply(SetItemCountFunction.setCount(ConstantValue.exactly((float)p_252097_.intValue())));
         }).when(HAS_NO_SILK_TOUCH), AlternativesEntry.alternatives(SnowLayerBlock.LAYERS.getPossibleValues(), (p_251216_) -> {
            return (LootPoolEntryContainer.Builder<?>)(p_251216_ == 8 ? LootItem.lootTableItem(Blocks.SNOW_BLOCK) : LootItem.lootTableItem(Blocks.SNOW).apply(SetItemCountFunction.setCount(ConstantValue.exactly((float)p_251216_.intValue()))).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_251108_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, p_251216_))));
         }))));
      });
      this.add(Blocks.GRAVEL, (p_248523_) -> {
         return createSilkTouchDispatchTable(p_248523_, this.applyExplosionCondition(p_248523_, LootItem.lootTableItem(Items.FLINT).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F)).otherwise(LootItem.lootTableItem(p_248523_))));
      });
      this.add(Blocks.CAMPFIRE, (p_248528_) -> {
         return createSilkTouchDispatchTable(p_248528_, this.applyExplosionCondition(p_248528_, LootItem.lootTableItem(Items.CHARCOAL).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))));
      });
      this.add(Blocks.GILDED_BLACKSTONE, (p_250428_) -> {
         return createSilkTouchDispatchTable(p_250428_, this.applyExplosionCondition(p_250428_, LootItem.lootTableItem(Items.GOLD_NUGGET).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F))).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F)).otherwise(LootItem.lootTableItem(p_250428_))));
      });
      this.add(Blocks.SOUL_CAMPFIRE, (p_248898_) -> {
         return createSilkTouchDispatchTable(p_248898_, this.applyExplosionCondition(p_248898_, LootItem.lootTableItem(Items.SOUL_SOIL).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))));
      });
      this.add(Blocks.AMETHYST_CLUSTER, (p_252201_) -> {
         return createSilkTouchDispatchTable(p_252201_, LootItem.lootTableItem(Items.AMETHYST_SHARD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(4.0F))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)).when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.CLUSTER_MAX_HARVESTABLES))).otherwise(this.applyExplosionDecay(p_252201_, LootItem.lootTableItem(Items.AMETHYST_SHARD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))))));
      });
      this.dropWhenSilkTouch(Blocks.SMALL_AMETHYST_BUD);
      this.dropWhenSilkTouch(Blocks.MEDIUM_AMETHYST_BUD);
      this.dropWhenSilkTouch(Blocks.LARGE_AMETHYST_BUD);
      this.dropWhenSilkTouch(Blocks.GLASS);
      this.dropWhenSilkTouch(Blocks.WHITE_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.ORANGE_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.MAGENTA_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.YELLOW_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.LIME_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.PINK_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.GRAY_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.CYAN_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.PURPLE_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.BLUE_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.BROWN_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.GREEN_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.RED_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.BLACK_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.WHITE_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.ORANGE_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.MAGENTA_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.YELLOW_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.LIME_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.PINK_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.GRAY_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.CYAN_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.PURPLE_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.BLUE_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.BROWN_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.GREEN_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.RED_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.BLACK_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.ICE);
      this.dropWhenSilkTouch(Blocks.PACKED_ICE);
      this.dropWhenSilkTouch(Blocks.BLUE_ICE);
      this.dropWhenSilkTouch(Blocks.TURTLE_EGG);
      this.dropWhenSilkTouch(Blocks.MUSHROOM_STEM);
      this.dropWhenSilkTouch(Blocks.DEAD_TUBE_CORAL);
      this.dropWhenSilkTouch(Blocks.DEAD_BRAIN_CORAL);
      this.dropWhenSilkTouch(Blocks.DEAD_BUBBLE_CORAL);
      this.dropWhenSilkTouch(Blocks.DEAD_FIRE_CORAL);
      this.dropWhenSilkTouch(Blocks.DEAD_HORN_CORAL);
      this.dropWhenSilkTouch(Blocks.TUBE_CORAL);
      this.dropWhenSilkTouch(Blocks.BRAIN_CORAL);
      this.dropWhenSilkTouch(Blocks.BUBBLE_CORAL);
      this.dropWhenSilkTouch(Blocks.FIRE_CORAL);
      this.dropWhenSilkTouch(Blocks.HORN_CORAL);
      this.dropWhenSilkTouch(Blocks.DEAD_TUBE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.DEAD_BRAIN_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.DEAD_BUBBLE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.DEAD_FIRE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.DEAD_HORN_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.TUBE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.BRAIN_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.BUBBLE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.FIRE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.HORN_CORAL_FAN);
      this.otherWhenSilkTouch(Blocks.INFESTED_STONE, Blocks.STONE);
      this.otherWhenSilkTouch(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE);
      this.otherWhenSilkTouch(Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICKS);
      this.otherWhenSilkTouch(Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
      this.otherWhenSilkTouch(Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
      this.otherWhenSilkTouch(Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS);
      this.otherWhenSilkTouch(Blocks.INFESTED_DEEPSLATE, Blocks.DEEPSLATE);
      this.addNetherVinesDropTable(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT);
      this.addNetherVinesDropTable(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT);
      this.add(Blocks.CAKE, noDrop());
      this.add(Blocks.CANDLE_CAKE, createCandleCakeDrops(Blocks.CANDLE));
      this.add(Blocks.WHITE_CANDLE_CAKE, createCandleCakeDrops(Blocks.WHITE_CANDLE));
      this.add(Blocks.ORANGE_CANDLE_CAKE, createCandleCakeDrops(Blocks.ORANGE_CANDLE));
      this.add(Blocks.MAGENTA_CANDLE_CAKE, createCandleCakeDrops(Blocks.MAGENTA_CANDLE));
      this.add(Blocks.LIGHT_BLUE_CANDLE_CAKE, createCandleCakeDrops(Blocks.LIGHT_BLUE_CANDLE));
      this.add(Blocks.YELLOW_CANDLE_CAKE, createCandleCakeDrops(Blocks.YELLOW_CANDLE));
      this.add(Blocks.LIME_CANDLE_CAKE, createCandleCakeDrops(Blocks.LIME_CANDLE));
      this.add(Blocks.PINK_CANDLE_CAKE, createCandleCakeDrops(Blocks.PINK_CANDLE));
      this.add(Blocks.GRAY_CANDLE_CAKE, createCandleCakeDrops(Blocks.GRAY_CANDLE));
      this.add(Blocks.LIGHT_GRAY_CANDLE_CAKE, createCandleCakeDrops(Blocks.LIGHT_GRAY_CANDLE));
      this.add(Blocks.CYAN_CANDLE_CAKE, createCandleCakeDrops(Blocks.CYAN_CANDLE));
      this.add(Blocks.PURPLE_CANDLE_CAKE, createCandleCakeDrops(Blocks.PURPLE_CANDLE));
      this.add(Blocks.BLUE_CANDLE_CAKE, createCandleCakeDrops(Blocks.BLUE_CANDLE));
      this.add(Blocks.BROWN_CANDLE_CAKE, createCandleCakeDrops(Blocks.BROWN_CANDLE));
      this.add(Blocks.GREEN_CANDLE_CAKE, createCandleCakeDrops(Blocks.GREEN_CANDLE));
      this.add(Blocks.RED_CANDLE_CAKE, createCandleCakeDrops(Blocks.RED_CANDLE));
      this.add(Blocks.BLACK_CANDLE_CAKE, createCandleCakeDrops(Blocks.BLACK_CANDLE));
      this.add(Blocks.FROSTED_ICE, noDrop());
      this.add(Blocks.SPAWNER, noDrop());
      this.add(Blocks.FIRE, noDrop());
      this.add(Blocks.SOUL_FIRE, noDrop());
      this.add(Blocks.NETHER_PORTAL, noDrop());
      this.add(Blocks.BUDDING_AMETHYST, noDrop());
      this.add(Blocks.POWDER_SNOW, noDrop());
      this.add(Blocks.FROGSPAWN, noDrop());
      this.add(Blocks.REINFORCED_DEEPSLATE, noDrop());
      this.add(Blocks.SUSPICIOUS_SAND, noDrop());
      this.add(Blocks.SUSPICIOUS_GRAVEL, noDrop());
   }

   private LootTable.Builder createDecoratedPotTable(Block p_277929_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(DynamicLoot.dynamicEntry(DecoratedPotBlock.SHERDS_DYNAMIC_DROP_ID).when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.BREAKS_DECORATED_POTS))).when(HAS_NO_SILK_TOUCH).otherwise(LootItem.lootTableItem(p_277929_).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("sherds", "BlockEntityTag.sherds")))));
   }

   private LootTable.Builder createPitcherCropLoot() {
      return this.applyExplosionDecay(Blocks.PITCHER_CROP, LootTable.lootTable().withPool(LootPool.lootPool().add(AlternativesEntry.alternatives(PitcherCropBlock.AGE.getPossibleValues(), (p_277248_) -> {
         LootItemBlockStatePropertyCondition.Builder lootitemblockstatepropertycondition$builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.PITCHER_CROP).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
         LootItemBlockStatePropertyCondition.Builder lootitemblockstatepropertycondition$builder1 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.PITCHER_CROP).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PitcherCropBlock.AGE, p_277248_));
         return p_277248_ == 4 ? LootItem.lootTableItem(Items.PITCHER_PLANT).when(lootitemblockstatepropertycondition$builder1).when(lootitemblockstatepropertycondition$builder).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))) : LootItem.lootTableItem(Items.PITCHER_POD).when(lootitemblockstatepropertycondition$builder1).when(lootitemblockstatepropertycondition$builder).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)));
      }))));
   }
}