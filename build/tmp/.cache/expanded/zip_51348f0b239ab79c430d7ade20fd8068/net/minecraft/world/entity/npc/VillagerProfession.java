package net.minecraft.world.entity.npc;

import com.google.common.collect.ImmutableSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public record VillagerProfession(String name, Predicate<Holder<PoiType>> heldJobSite, Predicate<Holder<PoiType>> acquirableJobSite, ImmutableSet<Item> requestedItems, ImmutableSet<Block> secondaryPoi, @Nullable SoundEvent workSound) {
   public static final Predicate<Holder<PoiType>> ALL_ACQUIRABLE_JOBS = (p_238239_) -> {
      return p_238239_.is(PoiTypeTags.ACQUIRABLE_JOB_SITE);
   };
   public static final VillagerProfession NONE = register("none", PoiType.NONE, ALL_ACQUIRABLE_JOBS, (SoundEvent)null);
   public static final VillagerProfession ARMORER = register("armorer", PoiTypes.ARMORER, SoundEvents.VILLAGER_WORK_ARMORER);
   public static final VillagerProfession BUTCHER = register("butcher", PoiTypes.BUTCHER, SoundEvents.VILLAGER_WORK_BUTCHER);
   public static final VillagerProfession CARTOGRAPHER = register("cartographer", PoiTypes.CARTOGRAPHER, SoundEvents.VILLAGER_WORK_CARTOGRAPHER);
   public static final VillagerProfession CLERIC = register("cleric", PoiTypes.CLERIC, SoundEvents.VILLAGER_WORK_CLERIC);
   public static final VillagerProfession FARMER = register("farmer", PoiTypes.FARMER, ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.BONE_MEAL), ImmutableSet.of(Blocks.FARMLAND), SoundEvents.VILLAGER_WORK_FARMER);
   public static final VillagerProfession FISHERMAN = register("fisherman", PoiTypes.FISHERMAN, SoundEvents.VILLAGER_WORK_FISHERMAN);
   public static final VillagerProfession FLETCHER = register("fletcher", PoiTypes.FLETCHER, SoundEvents.VILLAGER_WORK_FLETCHER);
   public static final VillagerProfession LEATHERWORKER = register("leatherworker", PoiTypes.LEATHERWORKER, SoundEvents.VILLAGER_WORK_LEATHERWORKER);
   public static final VillagerProfession LIBRARIAN = register("librarian", PoiTypes.LIBRARIAN, SoundEvents.VILLAGER_WORK_LIBRARIAN);
   public static final VillagerProfession MASON = register("mason", PoiTypes.MASON, SoundEvents.VILLAGER_WORK_MASON);
   public static final VillagerProfession NITWIT = register("nitwit", PoiType.NONE, PoiType.NONE, (SoundEvent)null);
   public static final VillagerProfession SHEPHERD = register("shepherd", PoiTypes.SHEPHERD, SoundEvents.VILLAGER_WORK_SHEPHERD);
   public static final VillagerProfession TOOLSMITH = register("toolsmith", PoiTypes.TOOLSMITH, SoundEvents.VILLAGER_WORK_TOOLSMITH);
   public static final VillagerProfession WEAPONSMITH = register("weaponsmith", PoiTypes.WEAPONSMITH, SoundEvents.VILLAGER_WORK_WEAPONSMITH);

   public String toString() {
      return this.name;
   }

   private static VillagerProfession register(String p_219644_, ResourceKey<PoiType> p_219645_, @Nullable SoundEvent p_219646_) {
      return register(p_219644_, (p_219668_) -> {
         return p_219668_.is(p_219645_);
      }, (p_219640_) -> {
         return p_219640_.is(p_219645_);
      }, p_219646_);
   }

   private static VillagerProfession register(String p_219654_, Predicate<Holder<PoiType>> p_219655_, Predicate<Holder<PoiType>> p_219656_, @Nullable SoundEvent p_219657_) {
      return register(p_219654_, p_219655_, p_219656_, ImmutableSet.of(), ImmutableSet.of(), p_219657_);
   }

   private static VillagerProfession register(String p_219648_, ResourceKey<PoiType> p_219649_, ImmutableSet<Item> p_219650_, ImmutableSet<Block> p_219651_, @Nullable SoundEvent p_219652_) {
      return register(p_219648_, (p_238234_) -> {
         return p_238234_.is(p_219649_);
      }, (p_238237_) -> {
         return p_238237_.is(p_219649_);
      }, p_219650_, p_219651_, p_219652_);
   }

   private static VillagerProfession register(String p_219659_, Predicate<Holder<PoiType>> p_219660_, Predicate<Holder<PoiType>> p_219661_, ImmutableSet<Item> p_219662_, ImmutableSet<Block> p_219663_, @Nullable SoundEvent p_219664_) {
      return Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, new ResourceLocation(p_219659_), new VillagerProfession(p_219659_, p_219660_, p_219661_, p_219662_, p_219663_, p_219664_));
   }
}