package net.minecraft.data.loot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LimitCount;
import net.minecraft.world.level.storage.loot.functions.SetContainerContents;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public abstract class BlockLootSubProvider implements LootTableSubProvider {
   protected static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
   protected static final LootItemCondition.Builder HAS_NO_SILK_TOUCH = HAS_SILK_TOUCH.invert();
   protected static final LootItemCondition.Builder HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS));
   private static final LootItemCondition.Builder HAS_SHEARS_OR_SILK_TOUCH = HAS_SHEARS.or(HAS_SILK_TOUCH);
   private static final LootItemCondition.Builder HAS_NO_SHEARS_OR_SILK_TOUCH = HAS_SHEARS_OR_SILK_TOUCH.invert();
   protected final Set<Item> explosionResistant;
   protected final FeatureFlagSet enabledFeatures;
   protected final Map<ResourceLocation, LootTable.Builder> map;
   protected static final float[] NORMAL_LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
   private static final float[] NORMAL_LEAVES_STICK_CHANCES = new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F};

   protected BlockLootSubProvider(Set<Item> p_249153_, FeatureFlagSet p_251215_) {
      this(p_249153_, p_251215_, new HashMap<>());
   }

   protected BlockLootSubProvider(Set<Item> p_281507_, FeatureFlagSet p_283552_, Map<ResourceLocation, LootTable.Builder> p_282212_) {
      this.explosionResistant = p_281507_;
      this.enabledFeatures = p_283552_;
      this.map = p_282212_;
   }

   protected <T extends FunctionUserBuilder<T>> T applyExplosionDecay(ItemLike p_248695_, FunctionUserBuilder<T> p_248548_) {
      return (T)(!this.explosionResistant.contains(p_248695_.asItem()) ? p_248548_.apply(ApplyExplosionDecay.explosionDecay()) : p_248548_.unwrap());
   }

   protected <T extends ConditionUserBuilder<T>> T applyExplosionCondition(ItemLike p_249717_, ConditionUserBuilder<T> p_248851_) {
      return (T)(!this.explosionResistant.contains(p_249717_.asItem()) ? p_248851_.when(ExplosionCondition.survivesExplosion()) : p_248851_.unwrap());
   }

   public LootTable.Builder createSingleItemTable(ItemLike p_251912_) {
      return LootTable.lootTable().withPool(this.applyExplosionCondition(p_251912_, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_251912_))));
   }

   protected static LootTable.Builder createSelfDropDispatchTable(Block p_252253_, LootItemCondition.Builder p_248764_, LootPoolEntryContainer.Builder<?> p_249146_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_252253_).when(p_248764_).otherwise(p_249146_)));
   }

   protected static LootTable.Builder createSilkTouchDispatchTable(Block p_250203_, LootPoolEntryContainer.Builder<?> p_252089_) {
      return createSelfDropDispatchTable(p_250203_, HAS_SILK_TOUCH, p_252089_);
   }

   protected static LootTable.Builder createShearsDispatchTable(Block p_252195_, LootPoolEntryContainer.Builder<?> p_250102_) {
      return createSelfDropDispatchTable(p_252195_, HAS_SHEARS, p_250102_);
   }

   protected static LootTable.Builder createSilkTouchOrShearsDispatchTable(Block p_250539_, LootPoolEntryContainer.Builder<?> p_251459_) {
      return createSelfDropDispatchTable(p_250539_, HAS_SHEARS_OR_SILK_TOUCH, p_251459_);
   }

   protected LootTable.Builder createSingleItemTableWithSilkTouch(Block p_249305_, ItemLike p_251905_) {
      return createSilkTouchDispatchTable(p_249305_, this.applyExplosionCondition(p_249305_, LootItem.lootTableItem(p_251905_)));
   }

   protected LootTable.Builder createSingleItemTable(ItemLike p_251584_, NumberProvider p_249865_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(this.applyExplosionDecay(p_251584_, LootItem.lootTableItem(p_251584_).apply(SetItemCountFunction.setCount(p_249865_)))));
   }

   protected LootTable.Builder createSingleItemTableWithSilkTouch(Block p_251449_, ItemLike p_248558_, NumberProvider p_250047_) {
      return createSilkTouchDispatchTable(p_251449_, this.applyExplosionDecay(p_251449_, LootItem.lootTableItem(p_248558_).apply(SetItemCountFunction.setCount(p_250047_))));
   }

   protected static LootTable.Builder createSilkTouchOnlyTable(ItemLike p_252216_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().when(HAS_SILK_TOUCH).setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_252216_)));
   }

   protected LootTable.Builder createPotFlowerItemTable(ItemLike p_249395_) {
      return LootTable.lootTable().withPool(this.applyExplosionCondition(Blocks.FLOWER_POT, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Blocks.FLOWER_POT)))).withPool(this.applyExplosionCondition(p_249395_, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_249395_))));
   }

   protected LootTable.Builder createSlabItemTable(Block p_251313_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(this.applyExplosionDecay(p_251313_, LootItem.lootTableItem(p_251313_).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_251313_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE)))))));
   }

   protected <T extends Comparable<T> & StringRepresentable> LootTable.Builder createSinglePropConditionTable(Block p_252154_, Property<T> p_250272_, T p_250292_) {
      return LootTable.lootTable().withPool(this.applyExplosionCondition(p_252154_, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_252154_).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_252154_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(p_250272_, p_250292_))))));
   }

   protected LootTable.Builder createNameableBlockEntityTable(Block p_252291_) {
      return LootTable.lootTable().withPool(this.applyExplosionCondition(p_252291_, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_252291_).apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)))));
   }

   protected LootTable.Builder createShulkerBoxDrop(Block p_252164_) {
      return LootTable.lootTable().withPool(this.applyExplosionCondition(p_252164_, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_252164_).apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Lock", "BlockEntityTag.Lock").copy("LootTable", "BlockEntityTag.LootTable").copy("LootTableSeed", "BlockEntityTag.LootTableSeed")).apply(SetContainerContents.setContents(BlockEntityType.SHULKER_BOX).withEntry(DynamicLoot.dynamicEntry(ShulkerBoxBlock.CONTENTS))))));
   }

   protected LootTable.Builder createCopperOreDrops(Block p_251306_) {
      return createSilkTouchDispatchTable(p_251306_, this.applyExplosionDecay(p_251306_, LootItem.lootTableItem(Items.RAW_COPPER).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
   }

   protected LootTable.Builder createLapisOreDrops(Block p_251511_) {
      return createSilkTouchDispatchTable(p_251511_, this.applyExplosionDecay(p_251511_, LootItem.lootTableItem(Items.LAPIS_LAZULI).apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 9.0F))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
   }

   protected LootTable.Builder createRedstoneOreDrops(Block p_251906_) {
      return createSilkTouchDispatchTable(p_251906_, this.applyExplosionDecay(p_251906_, LootItem.lootTableItem(Items.REDSTONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 5.0F))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))));
   }

   protected LootTable.Builder createBannerDrop(Block p_249810_) {
      return LootTable.lootTable().withPool(this.applyExplosionCondition(p_249810_, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_249810_).apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Patterns", "BlockEntityTag.Patterns")))));
   }

   protected static LootTable.Builder createBeeNestDrop(Block p_250988_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().when(HAS_SILK_TOUCH).setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_250988_).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Bees", "BlockEntityTag.Bees")).apply(CopyBlockState.copyState(p_250988_).copy(BeehiveBlock.HONEY_LEVEL))));
   }

   protected static LootTable.Builder createBeeHiveDrop(Block p_248770_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_248770_).when(HAS_SILK_TOUCH).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Bees", "BlockEntityTag.Bees")).apply(CopyBlockState.copyState(p_248770_).copy(BeehiveBlock.HONEY_LEVEL)).otherwise(LootItem.lootTableItem(p_248770_))));
   }

   protected static LootTable.Builder createCaveVinesDrop(Block p_251070_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.GLOW_BERRIES)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_251070_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CaveVines.BERRIES, true))));
   }

   protected LootTable.Builder createOreDrop(Block p_250450_, Item p_249745_) {
      return createSilkTouchDispatchTable(p_250450_, this.applyExplosionDecay(p_250450_, LootItem.lootTableItem(p_249745_).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
   }

   protected LootTable.Builder createMushroomBlockDrop(Block p_249959_, ItemLike p_249315_) {
      return createSilkTouchDispatchTable(p_249959_, this.applyExplosionDecay(p_249959_, LootItem.lootTableItem(p_249315_).apply(SetItemCountFunction.setCount(UniformGenerator.between(-6.0F, 2.0F))).apply(LimitCount.limitCount(IntRange.lowerBound(0)))));
   }

   protected LootTable.Builder createGrassDrops(Block p_252139_) {
      return createShearsDispatchTable(p_252139_, this.applyExplosionDecay(p_252139_, LootItem.lootTableItem(Items.WHEAT_SEEDS).when(LootItemRandomChanceCondition.randomChance(0.125F)).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 2))));
   }

   public LootTable.Builder createStemDrops(Block p_250957_, Item p_249098_) {
      return LootTable.lootTable().withPool(this.applyExplosionDecay(p_250957_, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_249098_).apply(StemBlock.AGE.getPossibleValues(), (p_249795_) -> {
         return SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, (float)(p_249795_ + 1) / 15.0F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_250957_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, p_249795_)));
      }))));
   }

   public LootTable.Builder createAttachedStemDrops(Block p_249778_, Item p_250678_) {
      return LootTable.lootTable().withPool(this.applyExplosionDecay(p_249778_, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_250678_).apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, 0.53333336F))))));
   }

   protected static LootTable.Builder createShearsOnlyDrop(ItemLike p_250684_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(HAS_SHEARS).add(LootItem.lootTableItem(p_250684_)));
   }

   protected LootTable.Builder createMultifaceBlockDrops(Block p_249088_, LootItemCondition.Builder p_251535_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().add(this.applyExplosionDecay(p_249088_, LootItem.lootTableItem(p_249088_).when(p_251535_).apply(Direction.values(), (p_251536_) -> {
         return SetItemCountFunction.setCount(ConstantValue.exactly(1.0F), true).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_249088_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MultifaceBlock.getFaceProperty(p_251536_), true)));
      }).apply(SetItemCountFunction.setCount(ConstantValue.exactly(-1.0F), true)))));
   }

   protected LootTable.Builder createLeavesDrops(Block p_250088_, Block p_250731_, float... p_248949_) {
      return createSilkTouchOrShearsDispatchTable(p_250088_, this.applyExplosionCondition(p_250088_, LootItem.lootTableItem(p_250731_)).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, p_248949_))).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(HAS_NO_SHEARS_OR_SILK_TOUCH).add(this.applyExplosionDecay(p_250088_, LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, NORMAL_LEAVES_STICK_CHANCES))));
   }

   protected LootTable.Builder createOakLeavesDrops(Block p_249535_, Block p_251505_, float... p_250753_) {
      return this.createLeavesDrops(p_249535_, p_251505_, p_250753_).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(HAS_NO_SHEARS_OR_SILK_TOUCH).add(this.applyExplosionCondition(p_249535_, LootItem.lootTableItem(Items.APPLE)).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
   }

   protected LootTable.Builder createMangroveLeavesDrops(Block p_251103_) {
      return createSilkTouchOrShearsDispatchTable(p_251103_, this.applyExplosionDecay(Blocks.MANGROVE_LEAVES, LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, NORMAL_LEAVES_STICK_CHANCES)));
   }

   protected LootTable.Builder createCropDrops(Block p_249457_, Item p_248599_, Item p_251915_, LootItemCondition.Builder p_252202_) {
      return this.applyExplosionDecay(p_249457_, LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(p_248599_).when(p_252202_).otherwise(LootItem.lootTableItem(p_251915_)))).withPool(LootPool.lootPool().when(p_252202_).add(LootItem.lootTableItem(p_251915_).apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3)))));
   }

   protected static LootTable.Builder createDoublePlantShearsDrop(Block p_248678_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().when(HAS_SHEARS).add(LootItem.lootTableItem(p_248678_).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))));
   }

   protected LootTable.Builder createDoublePlantWithSeedDrops(Block p_248590_, Block p_248735_) {
      LootPoolEntryContainer.Builder<?> builder = LootItem.lootTableItem(p_248735_).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))).when(HAS_SHEARS).otherwise(this.applyExplosionCondition(p_248590_, LootItem.lootTableItem(Items.WHEAT_SEEDS)).when(LootItemRandomChanceCondition.randomChance(0.125F)));
      return LootTable.lootTable().withPool(LootPool.lootPool().add(builder).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_248590_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER))).when(LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(p_248590_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER).build()).build()), new BlockPos(0, 1, 0)))).withPool(LootPool.lootPool().add(builder).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_248590_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER))).when(LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(p_248590_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER).build()).build()), new BlockPos(0, -1, 0))));
   }

   protected LootTable.Builder createCandleDrops(Block p_250896_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(this.applyExplosionDecay(p_250896_, LootItem.lootTableItem(p_250896_).apply(List.of(2, 3, 4), (p_249985_) -> {
         return SetItemCountFunction.setCount(ConstantValue.exactly((float)p_249985_.intValue())).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_250896_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CandleBlock.CANDLES, p_249985_)));
      }))));
   }

   protected LootTable.Builder createPetalsDrops(Block p_273240_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(this.applyExplosionDecay(p_273240_, LootItem.lootTableItem(p_273240_).apply(IntStream.rangeClosed(1, 4).boxed().toList(), (p_272348_) -> {
         return SetItemCountFunction.setCount(ConstantValue.exactly((float)p_272348_.intValue())).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_273240_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PinkPetalsBlock.AMOUNT, p_272348_)));
      }))));
   }

   protected static LootTable.Builder createCandleCakeDrops(Block p_250280_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_250280_)));
   }

   public static LootTable.Builder noDrop() {
      return LootTable.lootTable();
   }

   protected abstract void generate();

   protected Iterable<Block> getKnownBlocks() {
      return BuiltInRegistries.BLOCK;
   }

   public void generate(BiConsumer<ResourceLocation, LootTable.Builder> p_249322_) {
      this.generate();
      Set<ResourceLocation> set = new HashSet<>();

      for(Block block : getKnownBlocks()) {
         if (block.isEnabled(this.enabledFeatures)) {
            ResourceLocation resourcelocation = block.getLootTable();
            if (resourcelocation != BuiltInLootTables.EMPTY && set.add(resourcelocation)) {
               LootTable.Builder loottable$builder = this.map.remove(resourcelocation);
               if (loottable$builder == null) {
                  throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", resourcelocation, BuiltInRegistries.BLOCK.getKey(block)));
               }

               p_249322_.accept(resourcelocation, loottable$builder);
            }
         }
      }

      if (!this.map.isEmpty()) {
         throw new IllegalStateException("Created block loot tables for non-blocks: " + this.map.keySet());
      }
   }

   protected void addNetherVinesDropTable(Block p_252269_, Block p_250696_) {
      LootTable.Builder loottable$builder = createSilkTouchOrShearsDispatchTable(p_252269_, LootItem.lootTableItem(p_252269_).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.33F, 0.55F, 0.77F, 1.0F)));
      this.add(p_252269_, loottable$builder);
      this.add(p_250696_, loottable$builder);
   }

   protected LootTable.Builder createDoorTable(Block p_252166_) {
      return this.createSinglePropConditionTable(p_252166_, DoorBlock.HALF, DoubleBlockHalf.LOWER);
   }

   protected void dropPottedContents(Block p_251064_) {
      this.add(p_251064_, (p_250193_) -> {
         return this.createPotFlowerItemTable(((FlowerPotBlock)p_250193_).getContent());
      });
   }

   protected void otherWhenSilkTouch(Block p_249932_, Block p_252053_) {
      this.add(p_249932_, createSilkTouchOnlyTable(p_252053_));
   }

   protected void dropOther(Block p_248885_, ItemLike p_251883_) {
      this.add(p_248885_, this.createSingleItemTable(p_251883_));
   }

   protected void dropWhenSilkTouch(Block p_250855_) {
      this.otherWhenSilkTouch(p_250855_, p_250855_);
   }

   protected void dropSelf(Block p_249181_) {
      this.dropOther(p_249181_, p_249181_);
   }

   protected void add(Block p_251966_, Function<Block, LootTable.Builder> p_251699_) {
      this.add(p_251966_, p_251699_.apply(p_251966_));
   }

   protected void add(Block p_250610_, LootTable.Builder p_249817_) {
      this.map.put(p_250610_.getLootTable(), p_249817_);
   }
}
