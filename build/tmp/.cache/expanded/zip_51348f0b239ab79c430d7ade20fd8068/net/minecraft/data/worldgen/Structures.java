package net.minecraft.data.worldgen;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.BuriedTreasureStructure;
import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidStructure;
import net.minecraft.world.level.levelgen.structure.structures.EndCityStructure;
import net.minecraft.world.level.levelgen.structure.structures.IglooStructure;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.structures.JungleTempleStructure;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftStructure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFossilStructure;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentStructure;
import net.minecraft.world.level.levelgen.structure.structures.OceanRuinStructure;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalStructure;
import net.minecraft.world.level.levelgen.structure.structures.ShipwreckStructure;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdStructure;
import net.minecraft.world.level.levelgen.structure.structures.SwampHutStructure;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionStructure;

public class Structures {
   private static Structure.StructureSettings structure(HolderSet<Biome> p_256015_, Map<MobCategory, StructureSpawnOverride> p_256297_, GenerationStep.Decoration p_255729_, TerrainAdjustment p_255865_) {
      return new Structure.StructureSettings(p_256015_, p_256297_, p_255729_, p_255865_);
   }

   private static Structure.StructureSettings structure(HolderSet<Biome> p_255731_, GenerationStep.Decoration p_256551_, TerrainAdjustment p_256463_) {
      return structure(p_255731_, Map.of(), p_256551_, p_256463_);
   }

   private static Structure.StructureSettings structure(HolderSet<Biome> p_256501_, TerrainAdjustment p_255704_) {
      return structure(p_256501_, Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, p_255704_);
   }

   public static void bootstrap(BootstapContext<Structure> p_256072_) {
      HolderGetter<Biome> holdergetter = p_256072_.lookup(Registries.BIOME);
      HolderGetter<StructureTemplatePool> holdergetter1 = p_256072_.lookup(Registries.TEMPLATE_POOL);
      p_256072_.register(BuiltinStructures.PILLAGER_OUTPOST, new JigsawStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_PILLAGER_OUTPOST), Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(EntityType.PILLAGER, 1, 1, 1)))), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN), holdergetter1.getOrThrow(PillagerOutpostPools.START), 7, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
      p_256072_.register(BuiltinStructures.MINESHAFT, new MineshaftStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_MINESHAFT), GenerationStep.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE), MineshaftStructure.Type.NORMAL));
      p_256072_.register(BuiltinStructures.MINESHAFT_MESA, new MineshaftStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_MINESHAFT_MESA), GenerationStep.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE), MineshaftStructure.Type.MESA));
      p_256072_.register(BuiltinStructures.WOODLAND_MANSION, new WoodlandMansionStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_WOODLAND_MANSION), TerrainAdjustment.NONE)));
      p_256072_.register(BuiltinStructures.JUNGLE_TEMPLE, new JungleTempleStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_JUNGLE_TEMPLE), TerrainAdjustment.NONE)));
      p_256072_.register(BuiltinStructures.DESERT_PYRAMID, new DesertPyramidStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_DESERT_PYRAMID), TerrainAdjustment.NONE)));
      p_256072_.register(BuiltinStructures.IGLOO, new IglooStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_IGLOO), TerrainAdjustment.NONE)));
      p_256072_.register(BuiltinStructures.SHIPWRECK, new ShipwreckStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_SHIPWRECK), TerrainAdjustment.NONE), false));
      p_256072_.register(BuiltinStructures.SHIPWRECK_BEACHED, new ShipwreckStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_SHIPWRECK_BEACHED), TerrainAdjustment.NONE), true));
      p_256072_.register(BuiltinStructures.SWAMP_HUT, new SwampHutStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_SWAMP_HUT), Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(EntityType.WITCH, 1, 1, 1))), MobCategory.CREATURE, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(EntityType.CAT, 1, 1, 1)))), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
      p_256072_.register(BuiltinStructures.STRONGHOLD, new StrongholdStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_STRONGHOLD), TerrainAdjustment.BURY)));
      p_256072_.register(BuiltinStructures.OCEAN_MONUMENT, new OceanMonumentStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_OCEAN_MONUMENT), Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(EntityType.GUARDIAN, 1, 2, 4))), MobCategory.UNDERGROUND_WATER_CREATURE, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, MobSpawnSettings.EMPTY_MOB_LIST), MobCategory.AXOLOTLS, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, MobSpawnSettings.EMPTY_MOB_LIST)), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
      p_256072_.register(BuiltinStructures.OCEAN_RUIN_COLD, new OceanRuinStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_OCEAN_RUIN_COLD), TerrainAdjustment.NONE), OceanRuinStructure.Type.COLD, 0.3F, 0.9F));
      p_256072_.register(BuiltinStructures.OCEAN_RUIN_WARM, new OceanRuinStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_OCEAN_RUIN_WARM), TerrainAdjustment.NONE), OceanRuinStructure.Type.WARM, 0.3F, 0.9F));
      p_256072_.register(BuiltinStructures.FORTRESS, new NetherFortressStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_NETHER_FORTRESS), Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, NetherFortressStructure.FORTRESS_ENEMIES)), GenerationStep.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.NONE)));
      p_256072_.register(BuiltinStructures.NETHER_FOSSIL, new NetherFossilStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_NETHER_FOSSIL), GenerationStep.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.BEARD_THIN), UniformHeight.of(VerticalAnchor.absolute(32), VerticalAnchor.belowTop(2))));
      p_256072_.register(BuiltinStructures.END_CITY, new EndCityStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_END_CITY), TerrainAdjustment.NONE)));
      p_256072_.register(BuiltinStructures.BURIED_TREASURE, new BuriedTreasureStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_BURIED_TREASURE), GenerationStep.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE)));
      p_256072_.register(BuiltinStructures.BASTION_REMNANT, new JigsawStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_BASTION_REMNANT), TerrainAdjustment.NONE), holdergetter1.getOrThrow(BastionPieces.START), 6, ConstantHeight.of(VerticalAnchor.absolute(33)), false));
      p_256072_.register(BuiltinStructures.VILLAGE_PLAINS, new JigsawStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_VILLAGE_PLAINS), TerrainAdjustment.BEARD_THIN), holdergetter1.getOrThrow(PlainVillagePools.START), 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
      p_256072_.register(BuiltinStructures.VILLAGE_DESERT, new JigsawStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_VILLAGE_DESERT), TerrainAdjustment.BEARD_THIN), holdergetter1.getOrThrow(DesertVillagePools.START), 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
      p_256072_.register(BuiltinStructures.VILLAGE_SAVANNA, new JigsawStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_VILLAGE_SAVANNA), TerrainAdjustment.BEARD_THIN), holdergetter1.getOrThrow(SavannaVillagePools.START), 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
      p_256072_.register(BuiltinStructures.VILLAGE_SNOWY, new JigsawStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_VILLAGE_SNOWY), TerrainAdjustment.BEARD_THIN), holdergetter1.getOrThrow(SnowyVillagePools.START), 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
      p_256072_.register(BuiltinStructures.VILLAGE_TAIGA, new JigsawStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_VILLAGE_TAIGA), TerrainAdjustment.BEARD_THIN), holdergetter1.getOrThrow(TaigaVillagePools.START), 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
      p_256072_.register(BuiltinStructures.RUINED_PORTAL_STANDARD, new RuinedPortalStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_STANDARD), TerrainAdjustment.NONE), List.of(new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.UNDERGROUND, 1.0F, 0.2F, false, false, true, false, 0.5F), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE, 0.5F, 0.2F, false, false, true, false, 0.5F))));
      p_256072_.register(BuiltinStructures.RUINED_PORTAL_DESERT, new RuinedPortalStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_DESERT), TerrainAdjustment.NONE), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.PARTLY_BURIED, 0.0F, 0.0F, false, false, false, false, 1.0F)));
      p_256072_.register(BuiltinStructures.RUINED_PORTAL_JUNGLE, new RuinedPortalStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_JUNGLE), TerrainAdjustment.NONE), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE, 0.5F, 0.8F, true, true, false, false, 1.0F)));
      p_256072_.register(BuiltinStructures.RUINED_PORTAL_SWAMP, new RuinedPortalStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_SWAMP), TerrainAdjustment.NONE), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR, 0.0F, 0.5F, false, true, false, false, 1.0F)));
      p_256072_.register(BuiltinStructures.RUINED_PORTAL_MOUNTAIN, new RuinedPortalStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_MOUNTAIN), TerrainAdjustment.NONE), List.of(new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.IN_MOUNTAIN, 1.0F, 0.2F, false, false, true, false, 0.5F), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE, 0.5F, 0.2F, false, false, true, false, 0.5F))));
      p_256072_.register(BuiltinStructures.RUINED_PORTAL_OCEAN, new RuinedPortalStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_OCEAN), TerrainAdjustment.NONE), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR, 0.0F, 0.8F, false, false, true, false, 1.0F)));
      p_256072_.register(BuiltinStructures.RUINED_PORTAL_NETHER, new RuinedPortalStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_NETHER), TerrainAdjustment.NONE), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.IN_NETHER, 0.5F, 0.0F, false, false, false, true, 1.0F)));
      p_256072_.register(BuiltinStructures.ANCIENT_CITY, new JigsawStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_ANCIENT_CITY), Arrays.stream(MobCategory.values()).collect(Collectors.toMap((p_236555_) -> {
         return p_236555_;
      }, (p_236551_) -> {
         return new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create());
      })), GenerationStep.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.BEARD_BOX), holdergetter1.getOrThrow(AncientCityStructurePieces.START), Optional.of(new ResourceLocation("city_anchor")), 7, ConstantHeight.of(VerticalAnchor.absolute(-27)), false, Optional.empty(), 116));
      p_256072_.register(BuiltinStructures.TRAIL_RUINS, new JigsawStructure(structure(holdergetter.getOrThrow(BiomeTags.HAS_TRAIL_RUINS), Map.of(), GenerationStep.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.BURY), holdergetter1.getOrThrow(TrailRuinsStructurePools.START), 7, ConstantHeight.of(VerticalAnchor.absolute(-15)), false, Heightmap.Types.WORLD_SURFACE_WG));
   }
}