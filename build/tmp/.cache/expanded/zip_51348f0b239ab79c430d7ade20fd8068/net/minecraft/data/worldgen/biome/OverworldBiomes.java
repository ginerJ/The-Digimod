package net.minecraft.data.worldgen.biome;

import javax.annotation.Nullable;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class OverworldBiomes {
   // TODO: getAdditionalOverworldBiomes, likely in this class. -C

   protected static final int NORMAL_WATER_COLOR = 4159204;
   protected static final int NORMAL_WATER_FOG_COLOR = 329011;
   private static final int OVERWORLD_FOG_COLOR = 12638463;
   @Nullable
   private static final Music NORMAL_MUSIC = null;

   protected static int calculateSkyColor(float p_194844_) {
      float $$1 = p_194844_ / 3.0F;
      $$1 = Mth.clamp($$1, -1.0F, 1.0F);
      return Mth.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
   }

   private static Biome biome(boolean p_265746_, float p_265800_, float p_265276_, MobSpawnSettings.Builder p_265425_, BiomeGenerationSettings.Builder p_265371_, @Nullable Music p_265636_) {
      return biome(p_265746_, p_265800_, p_265276_, 4159204, 329011, (Integer)null, (Integer)null, p_265425_, p_265371_, p_265636_);
   }

   private static Biome biome(boolean p_273483_, float p_272621_, float p_273588_, int p_273605_, int p_272756_, @Nullable Integer p_272889_, @Nullable Integer p_272657_, MobSpawnSettings.Builder p_273300_, BiomeGenerationSettings.Builder p_272700_, @Nullable Music p_272996_) {
      BiomeSpecialEffects.Builder biomespecialeffects$builder = (new BiomeSpecialEffects.Builder()).waterColor(p_273605_).waterFogColor(p_272756_).fogColor(12638463).skyColor(calculateSkyColor(p_272621_)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(p_272996_);
      if (p_272889_ != null) {
         biomespecialeffects$builder.grassColorOverride(p_272889_);
      }

      if (p_272657_ != null) {
         biomespecialeffects$builder.foliageColorOverride(p_272657_);
      }

      return (new Biome.BiomeBuilder()).hasPrecipitation(p_273483_).temperature(p_272621_).downfall(p_273588_).specialEffects(biomespecialeffects$builder.build()).mobSpawnSettings(p_273300_.build()).generationSettings(p_272700_.build()).build();
   }

   private static void globalOverworldGeneration(BiomeGenerationSettings.Builder p_194870_) {
      BiomeDefaultFeatures.addDefaultCarversAndLakes(p_194870_);
      BiomeDefaultFeatures.addDefaultCrystalFormations(p_194870_);
      BiomeDefaultFeatures.addDefaultMonsterRoom(p_194870_);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(p_194870_);
      BiomeDefaultFeatures.addDefaultSprings(p_194870_);
      BiomeDefaultFeatures.addSurfaceFreezing(p_194870_);
   }

   public static Biome oldGrowthTaiga(HolderGetter<PlacedFeature> p_255849_, HolderGetter<ConfiguredWorldCarver<?>> p_256578_, boolean p_194877_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 8, 4, 4));
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 8, 2, 4));
      if (p_194877_) {
         BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      } else {
         BiomeDefaultFeatures.caveSpawns(mobspawnsettings$builder);
         BiomeDefaultFeatures.monsters(mobspawnsettings$builder, 100, 25, 100, false);
      }

      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_255849_, p_256578_);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addMossyStoneBlock(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addFerns(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, p_194877_ ? VegetationPlacements.TREES_OLD_GROWTH_SPRUCE_TAIGA : VegetationPlacements.TREES_OLD_GROWTH_PINE_TAIGA);
      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addGiantTaigaVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addCommonBerryBushes(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_OLD_GROWTH_TAIGA);
      return biome(true, p_194877_ ? 0.25F : 0.3F, 0.8F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }

   public static Biome sparseJungle(HolderGetter<PlacedFeature> p_255977_, HolderGetter<ConfiguredWorldCarver<?>> p_256531_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns(mobspawnsettings$builder);
      return baseJungle(p_255977_, p_256531_, 0.8F, false, true, false, mobspawnsettings$builder, Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SPARSE_JUNGLE));
   }

   public static Biome jungle(HolderGetter<PlacedFeature> p_256033_, HolderGetter<ConfiguredWorldCarver<?>> p_255651_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 40, 1, 2)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 2, 1, 3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 1, 1, 2));
      return baseJungle(p_256033_, p_255651_, 0.9F, false, false, true, mobspawnsettings$builder, Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JUNGLE));
   }

   public static Biome bambooJungle(HolderGetter<PlacedFeature> p_255817_, HolderGetter<ConfiguredWorldCarver<?>> p_256096_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 40, 1, 2)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 80, 1, 2)).addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 2, 1, 1));
      return baseJungle(p_255817_, p_256096_, 0.9F, true, false, true, mobspawnsettings$builder, Musics.createGameMusic(SoundEvents.MUSIC_BIOME_BAMBOO_JUNGLE));
   }

   private static Biome baseJungle(HolderGetter<PlacedFeature> p_285208_, HolderGetter<ConfiguredWorldCarver<?>> p_285276_, float p_285079_, boolean p_285393_, boolean p_285109_, boolean p_285122_, MobSpawnSettings.Builder p_285449_, Music p_285440_) {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_285208_, p_285276_);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      if (p_285393_) {
         BiomeDefaultFeatures.addBambooVegetation(biomegenerationsettings$builder);
      } else {
         if (p_285122_) {
            BiomeDefaultFeatures.addLightBambooVegetation(biomegenerationsettings$builder);
         }

         if (p_285109_) {
            BiomeDefaultFeatures.addSparseJungleTrees(biomegenerationsettings$builder);
         } else {
            BiomeDefaultFeatures.addJungleTrees(biomegenerationsettings$builder);
         }
      }

      BiomeDefaultFeatures.addWarmFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addJungleGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addJungleVines(biomegenerationsettings$builder);
      if (p_285109_) {
         BiomeDefaultFeatures.addSparseJungleMelons(biomegenerationsettings$builder);
      } else {
         BiomeDefaultFeatures.addJungleMelons(biomegenerationsettings$builder);
      }

      return biome(true, 0.95F, p_285079_, p_285449_, biomegenerationsettings$builder, p_285440_);
   }

   public static Biome windsweptHills(HolderGetter<PlacedFeature> p_255703_, HolderGetter<ConfiguredWorldCarver<?>> p_256239_, boolean p_194887_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 5, 4, 6));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_255703_, p_256239_);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      if (p_194887_) {
         BiomeDefaultFeatures.addMountainForestTrees(biomegenerationsettings$builder);
      } else {
         BiomeDefaultFeatures.addMountainTrees(biomegenerationsettings$builder);
      }

      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addExtraEmeralds(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addInfestedStone(biomegenerationsettings$builder);
      return biome(true, 0.2F, 0.3F, mobspawnsettings$builder, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome desert(HolderGetter<PlacedFeature> p_256064_, HolderGetter<ConfiguredWorldCarver<?>> p_255852_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.desertSpawns(mobspawnsettings$builder);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256064_, p_255852_);
      BiomeDefaultFeatures.addFossilDecoration(biomegenerationsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDesertVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDesertExtraVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDesertExtraDecoration(biomegenerationsettings$builder);
      return biome(false, 2.0F, 0.0F, mobspawnsettings$builder, biomegenerationsettings$builder, Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DESERT));
   }

   public static Biome plains(HolderGetter<PlacedFeature> p_256382_, HolderGetter<ConfiguredWorldCarver<?>> p_256173_, boolean p_194882_, boolean p_194883_, boolean p_194884_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256382_, p_256173_);
      globalOverworldGeneration(biomegenerationsettings$builder);
      if (p_194883_) {
         mobspawnsettings$builder.creatureGenerationProbability(0.07F);
         BiomeDefaultFeatures.snowySpawns(mobspawnsettings$builder);
         if (p_194884_) {
            biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, MiscOverworldPlacements.ICE_SPIKE);
            biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, MiscOverworldPlacements.ICE_PATCH);
         }
      } else {
         BiomeDefaultFeatures.plainsSpawns(mobspawnsettings$builder);
         BiomeDefaultFeatures.addPlainGrass(biomegenerationsettings$builder);
         if (p_194882_) {
            biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUNFLOWER);
         }
      }

      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      if (p_194883_) {
         BiomeDefaultFeatures.addSnowyTrees(biomegenerationsettings$builder);
         BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
         BiomeDefaultFeatures.addDefaultGrass(biomegenerationsettings$builder);
      } else {
         BiomeDefaultFeatures.addPlainVegetation(biomegenerationsettings$builder);
      }

      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      if (p_194882_) {
         biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE);
         biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN);
      } else {
         BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      }

      float f = p_194883_ ? 0.0F : 0.8F;
      return biome(true, f, p_194883_ ? 0.5F : 0.4F, mobspawnsettings$builder, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome mushroomFields(HolderGetter<PlacedFeature> p_255775_, HolderGetter<ConfiguredWorldCarver<?>> p_256480_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.mooshroomSpawns(mobspawnsettings$builder);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_255775_, p_256480_);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addMushroomFieldVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      return biome(true, 0.9F, 1.0F, mobspawnsettings$builder, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome savanna(HolderGetter<PlacedFeature> p_256294_, HolderGetter<ConfiguredWorldCarver<?>> p_256583_, boolean p_194879_, boolean p_194880_) {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256294_, p_256583_);
      globalOverworldGeneration(biomegenerationsettings$builder);
      if (!p_194879_) {
         BiomeDefaultFeatures.addSavannaGrass(biomegenerationsettings$builder);
      }

      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      if (p_194879_) {
         BiomeDefaultFeatures.addShatteredSavannaTrees(biomegenerationsettings$builder);
         BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
         BiomeDefaultFeatures.addShatteredSavannaGrass(biomegenerationsettings$builder);
      } else {
         BiomeDefaultFeatures.addSavannaTrees(biomegenerationsettings$builder);
         BiomeDefaultFeatures.addWarmFlowers(biomegenerationsettings$builder);
         BiomeDefaultFeatures.addSavannaExtraGrass(biomegenerationsettings$builder);
      }

      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 1, 2, 6)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 1, 1));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      if (p_194880_) {
         mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 8, 4, 4));
      }

      return biome(false, 2.0F, 0.0F, mobspawnsettings$builder, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome badlands(HolderGetter<PlacedFeature> p_256309_, HolderGetter<ConfiguredWorldCarver<?>> p_256430_, boolean p_194897_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256309_, p_256430_);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addExtraGold(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      if (p_194897_) {
         BiomeDefaultFeatures.addBadlandsTrees(biomegenerationsettings$builder);
      }

      BiomeDefaultFeatures.addBadlandGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addBadlandExtraVegetation(biomegenerationsettings$builder);
      return (new Biome.BiomeBuilder()).hasPrecipitation(false).temperature(2.0F).downfall(0.0F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(2.0F)).foliageColorOverride(10387789).grassColorOverride(9470285).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_BADLANDS)).build()).mobSpawnSettings(mobspawnsettings$builder.build()).generationSettings(biomegenerationsettings$builder.build()).build();
   }

   private static Biome baseOcean(MobSpawnSettings.Builder p_194872_, int p_194873_, int p_194874_, BiomeGenerationSettings.Builder p_194875_) {
      return biome(true, 0.5F, 0.5F, p_194873_, p_194874_, (Integer)null, (Integer)null, p_194872_, p_194875_, NORMAL_MUSIC);
   }

   private static BiomeGenerationSettings.Builder baseOceanGeneration(HolderGetter<PlacedFeature> p_256289_, HolderGetter<ConfiguredWorldCarver<?>> p_256514_) {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256289_, p_256514_);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addWaterTrees(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      return biomegenerationsettings$builder;
   }

   public static Biome coldOcean(HolderGetter<PlacedFeature> p_256141_, HolderGetter<ConfiguredWorldCarver<?>> p_255841_, boolean p_194900_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.oceanSpawns(mobspawnsettings$builder, 3, 4, 15);
      mobspawnsettings$builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 15, 1, 5));
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = baseOceanGeneration(p_256141_, p_255841_);
      biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, p_194900_ ? AquaticPlacements.SEAGRASS_DEEP_COLD : AquaticPlacements.SEAGRASS_COLD);
      BiomeDefaultFeatures.addDefaultSeagrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addColdOceanExtraVegetation(biomegenerationsettings$builder);
      return baseOcean(mobspawnsettings$builder, 4020182, 329011, biomegenerationsettings$builder);
   }

   public static Biome ocean(HolderGetter<PlacedFeature> p_256265_, HolderGetter<ConfiguredWorldCarver<?>> p_256537_, boolean p_255752_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.oceanSpawns(mobspawnsettings$builder, 1, 4, 10);
      mobspawnsettings$builder.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 1, 1, 2));
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = baseOceanGeneration(p_256265_, p_256537_);
      biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, p_255752_ ? AquaticPlacements.SEAGRASS_DEEP : AquaticPlacements.SEAGRASS_NORMAL);
      BiomeDefaultFeatures.addDefaultSeagrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addColdOceanExtraVegetation(biomegenerationsettings$builder);
      return baseOcean(mobspawnsettings$builder, 4159204, 329011, biomegenerationsettings$builder);
   }

   public static Biome lukeWarmOcean(HolderGetter<PlacedFeature> p_255660_, HolderGetter<ConfiguredWorldCarver<?>> p_256231_, boolean p_194906_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      if (p_194906_) {
         BiomeDefaultFeatures.oceanSpawns(mobspawnsettings$builder, 8, 4, 8);
      } else {
         BiomeDefaultFeatures.oceanSpawns(mobspawnsettings$builder, 10, 2, 15);
      }

      mobspawnsettings$builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.PUFFERFISH, 5, 1, 3)).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8)).addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 2, 1, 2));
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = baseOceanGeneration(p_255660_, p_256231_);
      biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, p_194906_ ? AquaticPlacements.SEAGRASS_DEEP_WARM : AquaticPlacements.SEAGRASS_WARM);
      if (p_194906_) {
         BiomeDefaultFeatures.addDefaultSeagrass(biomegenerationsettings$builder);
      }

      BiomeDefaultFeatures.addLukeWarmKelp(biomegenerationsettings$builder);
      return baseOcean(mobspawnsettings$builder, 4566514, 267827, biomegenerationsettings$builder);
   }

   public static Biome warmOcean(HolderGetter<PlacedFeature> p_256477_, HolderGetter<ConfiguredWorldCarver<?>> p_256024_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.PUFFERFISH, 15, 1, 3));
      BiomeDefaultFeatures.warmOceanSpawns(mobspawnsettings$builder, 10, 4);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = baseOceanGeneration(p_256477_, p_256024_).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.WARM_OCEAN_VEGETATION).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_WARM).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEA_PICKLE);
      return baseOcean(mobspawnsettings$builder, 4445678, 270131, biomegenerationsettings$builder);
   }

   public static Biome frozenOcean(HolderGetter<PlacedFeature> p_256482_, HolderGetter<ConfiguredWorldCarver<?>> p_256660_, boolean p_194909_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SQUID, 1, 1, 4)).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 15, 1, 5)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.POLAR_BEAR, 1, 1, 2));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 5, 1, 1));
      float f = p_194909_ ? 0.5F : 0.0F;
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256482_, p_256660_);
      BiomeDefaultFeatures.addIcebergs(biomegenerationsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addBlueIce(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addWaterTrees(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      return (new Biome.BiomeBuilder()).hasPrecipitation(true).temperature(f).temperatureAdjustment(Biome.TemperatureModifier.FROZEN).downfall(0.5F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(3750089).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(f)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).mobSpawnSettings(mobspawnsettings$builder.build()).generationSettings(biomegenerationsettings$builder.build()).build();
   }

   public static Biome forest(HolderGetter<PlacedFeature> p_255788_, HolderGetter<ConfiguredWorldCarver<?>> p_256461_, boolean p_194892_, boolean p_194893_, boolean p_194894_) {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_255788_, p_256461_);
      globalOverworldGeneration(biomegenerationsettings$builder);
      Music music;
      if (p_194894_) {
         music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FLOWER_FOREST);
         biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FOREST_FLOWERS);
      } else {
         music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FOREST);
         BiomeDefaultFeatures.addForestFlowers(biomegenerationsettings$builder);
      }

      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      if (p_194894_) {
         biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_FLOWER_FOREST);
         biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FLOWER_FOREST);
         BiomeDefaultFeatures.addDefaultGrass(biomegenerationsettings$builder);
      } else {
         if (p_194892_) {
            if (p_194893_) {
               BiomeDefaultFeatures.addTallBirchTrees(biomegenerationsettings$builder);
            } else {
               BiomeDefaultFeatures.addBirchTrees(biomegenerationsettings$builder);
            }
         } else {
            BiomeDefaultFeatures.addOtherBirchTrees(biomegenerationsettings$builder);
         }

         BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
         BiomeDefaultFeatures.addForestGrass(biomegenerationsettings$builder);
      }

      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(mobspawnsettings$builder);
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      if (p_194894_) {
         mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
      } else if (!p_194892_) {
         mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 5, 4, 4));
      }

      float f = p_194892_ ? 0.6F : 0.7F;
      return biome(true, f, p_194892_ ? 0.6F : 0.8F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }

   public static Biome taiga(HolderGetter<PlacedFeature> p_256177_, HolderGetter<ConfiguredWorldCarver<?>> p_255727_, boolean p_194912_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 8, 4, 4)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 8, 2, 4));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      float f = p_194912_ ? -0.5F : 0.25F;
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256177_, p_255727_);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addFerns(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addTaigaTrees(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addTaigaGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      if (p_194912_) {
         BiomeDefaultFeatures.addRareBerryBushes(biomegenerationsettings$builder);
      } else {
         BiomeDefaultFeatures.addCommonBerryBushes(biomegenerationsettings$builder);
      }

      return biome(true, f, p_194912_ ? 0.4F : 0.8F, p_194912_ ? 4020182 : 4159204, 329011, (Integer)null, (Integer)null, mobspawnsettings$builder, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome darkForest(HolderGetter<PlacedFeature> p_256140_, HolderGetter<ConfiguredWorldCarver<?>> p_256223_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(mobspawnsettings$builder);
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256140_, p_256223_);
      globalOverworldGeneration(biomegenerationsettings$builder);
      biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.DARK_FOREST_VEGETATION);
      BiomeDefaultFeatures.addForestFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addForestGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FOREST);
      return (new Biome.BiomeBuilder()).hasPrecipitation(true).temperature(0.7F).downfall(0.8F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(calculateSkyColor(0.7F)).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.DARK_FOREST).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(music).build()).mobSpawnSettings(mobspawnsettings$builder.build()).generationSettings(biomegenerationsettings$builder.build()).build();
   }

   public static Biome swamp(HolderGetter<PlacedFeature> p_256058_, HolderGetter<ConfiguredWorldCarver<?>> p_256016_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(mobspawnsettings$builder);
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 1, 1, 1));
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FROG, 10, 2, 5));
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256058_, p_256016_);
      BiomeDefaultFeatures.addFossilDecoration(biomegenerationsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addSwampClayDisk(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addSwampVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addSwampExtraVegetation(biomegenerationsettings$builder);
      biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_SWAMP);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SWAMP);
      return (new Biome.BiomeBuilder()).hasPrecipitation(true).temperature(0.8F).downfall(0.9F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(6388580).waterFogColor(2302743).fogColor(12638463).skyColor(calculateSkyColor(0.8F)).foliageColorOverride(6975545).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.SWAMP).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(music).build()).mobSpawnSettings(mobspawnsettings$builder.build()).generationSettings(biomegenerationsettings$builder.build()).build();
   }

   public static Biome mangroveSwamp(HolderGetter<PlacedFeature> p_256353_, HolderGetter<ConfiguredWorldCarver<?>> p_256103_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 1, 1, 1));
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FROG, 10, 2, 5));
      mobspawnsettings$builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8));
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256353_, p_256103_);
      BiomeDefaultFeatures.addFossilDecoration(biomegenerationsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addMangroveSwampDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addMangroveSwampVegetation(biomegenerationsettings$builder);
      biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_SWAMP);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SWAMP);
      return (new Biome.BiomeBuilder()).hasPrecipitation(true).temperature(0.8F).downfall(0.9F).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(3832426).waterFogColor(5077600).fogColor(12638463).skyColor(calculateSkyColor(0.8F)).foliageColorOverride(9285927).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.SWAMP).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(music).build()).mobSpawnSettings(mobspawnsettings$builder.build()).generationSettings(biomegenerationsettings$builder.build()).build();
   }

   public static Biome river(HolderGetter<PlacedFeature> p_256613_, HolderGetter<ConfiguredWorldCarver<?>> p_256581_, boolean p_194915_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = (new MobSpawnSettings.Builder()).addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SQUID, 2, 1, 4)).addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 5, 1, 5));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, p_194915_ ? 1 : 100, 1, 1));
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256613_, p_256581_);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addWaterTrees(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      if (!p_194915_) {
         biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_RIVER);
      }

      float f = p_194915_ ? 0.0F : 0.5F;
      return biome(true, f, 0.5F, p_194915_ ? 3750089 : 4159204, 329011, (Integer)null, (Integer)null, mobspawnsettings$builder, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome beach(HolderGetter<PlacedFeature> p_256157_, HolderGetter<ConfiguredWorldCarver<?>> p_255712_, boolean p_194889_, boolean p_194890_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      boolean flag = !p_194890_ && !p_194889_;
      if (flag) {
         mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.TURTLE, 5, 2, 5));
      }

      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256157_, p_255712_);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultFlowers(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      float f;
      if (p_194889_) {
         f = 0.05F;
      } else if (p_194890_) {
         f = 0.2F;
      } else {
         f = 0.8F;
      }

      return biome(true, f, flag ? 0.4F : 0.3F, p_194889_ ? 4020182 : 4159204, 329011, (Integer)null, (Integer)null, mobspawnsettings$builder, biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome theVoid(HolderGetter<PlacedFeature> p_256509_, HolderGetter<ConfiguredWorldCarver<?>> p_256544_) {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256509_, p_256544_);
      biomegenerationsettings$builder.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, MiscOverworldPlacements.VOID_START_PLATFORM);
      return biome(false, 0.5F, 0.5F, new MobSpawnSettings.Builder(), biomegenerationsettings$builder, NORMAL_MUSIC);
   }

   public static Biome meadowOrCherryGrove(HolderGetter<PlacedFeature> p_273564_, HolderGetter<ConfiguredWorldCarver<?>> p_273374_, boolean p_273710_) {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_273564_, p_273374_);
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(p_273710_ ? EntityType.PIG : EntityType.DONKEY, 1, 1, 2)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 2, 6)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 2, 2, 4));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addPlainGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      if (p_273710_) {
         BiomeDefaultFeatures.addCherryGroveVegetation(biomegenerationsettings$builder);
      } else {
         BiomeDefaultFeatures.addMeadowVegetation(biomegenerationsettings$builder);
      }

      BiomeDefaultFeatures.addExtraEmeralds(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addInfestedStone(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(p_273710_ ? SoundEvents.MUSIC_BIOME_CHERRY_GROVE : SoundEvents.MUSIC_BIOME_MEADOW);
      return p_273710_ ? biome(true, 0.5F, 0.8F, 6141935, 6141935, 11983713, 11983713, mobspawnsettings$builder, biomegenerationsettings$builder, music) : biome(true, 0.5F, 0.8F, 937679, 329011, (Integer)null, (Integer)null, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }

   public static Biome frozenPeaks(HolderGetter<PlacedFeature> p_255713_, HolderGetter<ConfiguredWorldCarver<?>> p_256092_) {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_255713_, p_256092_);
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 5, 1, 3));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addFrozenSprings(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addExtraEmeralds(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addInfestedStone(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FROZEN_PEAKS);
      return biome(true, -0.7F, 0.9F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }

   public static Biome jaggedPeaks(HolderGetter<PlacedFeature> p_256512_, HolderGetter<ConfiguredWorldCarver<?>> p_255908_) {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256512_, p_255908_);
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 5, 1, 3));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addFrozenSprings(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addExtraEmeralds(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addInfestedStone(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JAGGED_PEAKS);
      return biome(true, -0.7F, 0.9F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }

   public static Biome stonyPeaks(HolderGetter<PlacedFeature> p_256490_, HolderGetter<ConfiguredWorldCarver<?>> p_255694_) {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256490_, p_255694_);
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addExtraEmeralds(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addInfestedStone(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_STONY_PEAKS);
      return biome(true, 1.0F, 0.3F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }

   public static Biome snowySlopes(HolderGetter<PlacedFeature> p_255927_, HolderGetter<ConfiguredWorldCarver<?>> p_255982_) {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_255927_, p_255982_);
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 5, 1, 3));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addFrozenSprings(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addExtraEmeralds(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addInfestedStone(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SNOWY_SLOPES);
      return biome(true, -0.3F, 0.9F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }

   public static Biome grove(HolderGetter<PlacedFeature> p_256094_, HolderGetter<ConfiguredWorldCarver<?>> p_256431_) {
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256094_, p_256431_);
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals(mobspawnsettings$builder);
      mobspawnsettings$builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 8, 4, 4)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3)).addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 8, 2, 4));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addFrozenSprings(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addGroveTrees(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addExtraEmeralds(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addInfestedStone(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_GROVE);
      return biome(true, -0.2F, 0.8F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }

   public static Biome lushCaves(HolderGetter<PlacedFeature> p_255944_, HolderGetter<ConfiguredWorldCarver<?>> p_255654_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      mobspawnsettings$builder.addSpawn(MobCategory.AXOLOTLS, new MobSpawnSettings.SpawnerData(EntityType.AXOLOTL, 10, 4, 6));
      mobspawnsettings$builder.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8));
      BiomeDefaultFeatures.commonSpawns(mobspawnsettings$builder);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_255944_, p_255654_);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addPlainGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addLushCavesSpecialOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addLushCavesVegetationFeatures(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_LUSH_CAVES);
      return biome(true, 0.5F, 0.5F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }

   public static Biome dripstoneCaves(HolderGetter<PlacedFeature> p_256253_, HolderGetter<ConfiguredWorldCarver<?>> p_255644_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.dripstoneCavesSpawns(mobspawnsettings$builder);
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256253_, p_255644_);
      globalOverworldGeneration(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addPlainGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder, true);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addPlainVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDripstone(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DRIPSTONE_CAVES);
      return biome(true, 0.8F, 0.4F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }

   public static Biome deepDark(HolderGetter<PlacedFeature> p_256073_, HolderGetter<ConfiguredWorldCarver<?>> p_256212_) {
      MobSpawnSettings.Builder mobspawnsettings$builder = new MobSpawnSettings.Builder();
      BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder(p_256073_, p_256212_);
      biomegenerationsettings$builder.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE);
      biomegenerationsettings$builder.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND);
      biomegenerationsettings$builder.addCarver(GenerationStep.Carving.AIR, Carvers.CANYON);
      BiomeDefaultFeatures.addDefaultCrystalFormations(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMonsterRoom(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultUndergroundVariety(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addSurfaceFreezing(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addPlainGrass(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultOres(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultSoftDisks(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addPlainVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultMushrooms(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addDefaultExtraVegetation(biomegenerationsettings$builder);
      BiomeDefaultFeatures.addSculk(biomegenerationsettings$builder);
      Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DEEP_DARK);
      return biome(true, 0.8F, 0.4F, mobspawnsettings$builder, biomegenerationsettings$builder, music);
   }
}
