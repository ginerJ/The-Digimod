package net.minecraft.data.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public abstract class EntityLootSubProvider implements LootTableSubProvider {
   protected static final EntityPredicate.Builder ENTITY_ON_FIRE = EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true).build());
   private static final Set<EntityType<?>> SPECIAL_LOOT_TABLE_TYPES = ImmutableSet.of(EntityType.PLAYER, EntityType.ARMOR_STAND, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER);
   private final FeatureFlagSet allowed;
   private final FeatureFlagSet required;
   private final Map<EntityType<?>, Map<ResourceLocation, LootTable.Builder>> map = Maps.newHashMap();

   protected EntityLootSubProvider(FeatureFlagSet p_251971_) {
      this(p_251971_, p_251971_);
   }

   protected EntityLootSubProvider(FeatureFlagSet p_266989_, FeatureFlagSet p_267138_) {
      this.allowed = p_266989_;
      this.required = p_267138_;
   }

   protected static LootTable.Builder createSheepTable(ItemLike p_249422_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_249422_))).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootTableReference.lootTableReference(EntityType.SHEEP.getDefaultLootTable())));
   }

   public abstract void generate();

   protected java.util.stream.Stream<EntityType<?>> getKnownEntityTypes() {
      return BuiltInRegistries.ENTITY_TYPE.stream();
   }

   public void generate(BiConsumer<ResourceLocation, LootTable.Builder> p_251751_) {
      this.generate();
      Set<ResourceLocation> set = Sets.newHashSet();
      this.getKnownEntityTypes().map(EntityType::builtInRegistryHolder).forEach((p_249003_) -> {
         EntityType<?> entitytype = p_249003_.value();
         if (entitytype.isEnabled(this.allowed)) {
            if (canHaveLootTable(entitytype)) {
               Map<ResourceLocation, LootTable.Builder> map = this.map.remove(entitytype);
               ResourceLocation resourcelocation = entitytype.getDefaultLootTable();
               if (!resourcelocation.equals(BuiltInLootTables.EMPTY) && entitytype.isEnabled(this.required) && (map == null || !map.containsKey(resourcelocation))) {
                  throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", resourcelocation, p_249003_.key().location()));
               }

               if (map != null) {
                  map.forEach((p_250376_, p_250972_) -> {
                     if (!set.add(p_250376_)) {
                        throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", p_250376_, p_249003_.key().location()));
                     } else {
                        p_251751_.accept(p_250376_, p_250972_);
                     }
                  });
               }
            } else {
               Map<ResourceLocation, LootTable.Builder> map1 = this.map.remove(entitytype);
               if (map1 != null) {
                  throw new IllegalStateException(String.format(Locale.ROOT, "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot", map1.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(",")), p_249003_.key().location()));
               }
            }

         }
      });
      if (!this.map.isEmpty()) {
         throw new IllegalStateException("Created loot tables for entities not supported by datapack: " + this.map.keySet());
      }
   }

   protected boolean canHaveLootTable(EntityType<?> p_249029_) {
      return SPECIAL_LOOT_TABLE_TYPES.contains(p_249029_) || p_249029_.getCategory() != MobCategory.MISC;
   }

   protected LootItemCondition.Builder killedByFrog() {
      return DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of(EntityType.FROG)));
   }

   protected LootItemCondition.Builder killedByFrogVariant(FrogVariant p_249403_) {
      return DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of(EntityType.FROG).subPredicate(EntitySubPredicate.variant(p_249403_))));
   }

   protected void add(EntityType<?> p_248740_, LootTable.Builder p_249440_) {
      this.add(p_248740_, p_248740_.getDefaultLootTable(), p_249440_);
   }

   protected void add(EntityType<?> p_252130_, ResourceLocation p_251706_, LootTable.Builder p_249357_) {
      this.map.computeIfAbsent(p_252130_, (p_251466_) -> {
         return new HashMap();
      }).put(p_251706_, p_249357_);
   }
}
