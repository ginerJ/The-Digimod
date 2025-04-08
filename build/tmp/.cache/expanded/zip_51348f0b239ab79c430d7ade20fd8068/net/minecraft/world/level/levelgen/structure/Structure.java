package net.minecraft.world.level.levelgen.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public abstract class Structure {
   public static final Codec<Structure> DIRECT_CODEC = BuiltInRegistries.STRUCTURE_TYPE.byNameCodec().dispatch(Structure::type, StructureType::codec);
   public static final Codec<Holder<Structure>> CODEC = RegistryFileCodec.create(Registries.STRUCTURE, DIRECT_CODEC);
   //Forge: Make this field private so that the redirect coremod can target it
   private final Structure.StructureSettings settings;

   public static <S extends Structure> RecordCodecBuilder<S, Structure.StructureSettings> settingsCodec(RecordCodecBuilder.Instance<S> p_226568_) {
      return Structure.StructureSettings.CODEC.forGetter((p_226595_) -> {
         return p_226595_.modifiableStructureInfo().getOriginalStructureInfo().structureSettings(); // FORGE: Patch codec to ignore field redirect coremods.
      });
   }

   public static <S extends Structure> Codec<S> simpleCodec(Function<Structure.StructureSettings, S> p_226608_) {
      return RecordCodecBuilder.create((p_226611_) -> {
         return p_226611_.group(settingsCodec(p_226611_)).apply(p_226611_, p_226608_);
      });
   }

   protected Structure(Structure.StructureSettings p_226558_) {
      this.settings = p_226558_;
      this.modifiableStructureInfo = new net.minecraftforge.common.world.ModifiableStructureInfo(new net.minecraftforge.common.world.ModifiableStructureInfo.StructureInfo(p_226558_)); // FORGE: cache original structure info on construction so we can bypass our field read coremods where necessary
   }

   public HolderSet<Biome> biomes() {
      return this.settings.biomes;
   }

   public Map<MobCategory, StructureSpawnOverride> spawnOverrides() {
      return this.settings.spawnOverrides;
   }

   public GenerationStep.Decoration step() {
      return this.settings.step;
   }

   public TerrainAdjustment terrainAdaptation() {
      return this.settings.terrainAdaptation;
   }

   public BoundingBox adjustBoundingBox(BoundingBox p_226570_) {
      return this.terrainAdaptation() != TerrainAdjustment.NONE ? p_226570_.inflatedBy(12) : p_226570_;
   }

   public StructureStart generate(RegistryAccess p_226597_, ChunkGenerator p_226598_, BiomeSource p_226599_, RandomState p_226600_, StructureTemplateManager p_226601_, long p_226602_, ChunkPos p_226603_, int p_226604_, LevelHeightAccessor p_226605_, Predicate<Holder<Biome>> p_226606_) {
      Structure.GenerationContext structure$generationcontext = new Structure.GenerationContext(p_226597_, p_226598_, p_226599_, p_226600_, p_226601_, p_226602_, p_226603_, p_226605_, p_226606_);
      Optional<Structure.GenerationStub> optional = this.findValidGenerationPoint(structure$generationcontext);
      if (optional.isPresent()) {
         StructurePiecesBuilder structurepiecesbuilder = optional.get().getPiecesBuilder();
         StructureStart structurestart = new StructureStart(this, p_226603_, p_226604_, structurepiecesbuilder.build());
         if (structurestart.isValid()) {
            return structurestart;
         }
      }

      return StructureStart.INVALID_START;
   }

   protected static Optional<Structure.GenerationStub> onTopOfChunkCenter(Structure.GenerationContext p_226586_, Heightmap.Types p_226587_, Consumer<StructurePiecesBuilder> p_226588_) {
      ChunkPos chunkpos = p_226586_.chunkPos();
      int i = chunkpos.getMiddleBlockX();
      int j = chunkpos.getMiddleBlockZ();
      int k = p_226586_.chunkGenerator().getFirstOccupiedHeight(i, j, p_226587_, p_226586_.heightAccessor(), p_226586_.randomState());
      return Optional.of(new Structure.GenerationStub(new BlockPos(i, k, j), p_226588_));
   }

   private static boolean isValidBiome(Structure.GenerationStub p_263042_, Structure.GenerationContext p_263005_) {
      BlockPos blockpos = p_263042_.position();
      return p_263005_.validBiome.test(p_263005_.chunkGenerator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock(blockpos.getX()), QuartPos.fromBlock(blockpos.getY()), QuartPos.fromBlock(blockpos.getZ()), p_263005_.randomState.sampler()));
   }

   public void afterPlace(WorldGenLevel p_226560_, StructureManager p_226561_, ChunkGenerator p_226562_, RandomSource p_226563_, BoundingBox p_226564_, ChunkPos p_226565_, PiecesContainer p_226566_) {
   }

   private static int[] getCornerHeights(Structure.GenerationContext p_226614_, int p_226615_, int p_226616_, int p_226617_, int p_226618_) {
      ChunkGenerator chunkgenerator = p_226614_.chunkGenerator();
      LevelHeightAccessor levelheightaccessor = p_226614_.heightAccessor();
      RandomState randomstate = p_226614_.randomState();
      return new int[]{chunkgenerator.getFirstOccupiedHeight(p_226615_, p_226617_, Heightmap.Types.WORLD_SURFACE_WG, levelheightaccessor, randomstate), chunkgenerator.getFirstOccupiedHeight(p_226615_, p_226617_ + p_226618_, Heightmap.Types.WORLD_SURFACE_WG, levelheightaccessor, randomstate), chunkgenerator.getFirstOccupiedHeight(p_226615_ + p_226616_, p_226617_, Heightmap.Types.WORLD_SURFACE_WG, levelheightaccessor, randomstate), chunkgenerator.getFirstOccupiedHeight(p_226615_ + p_226616_, p_226617_ + p_226618_, Heightmap.Types.WORLD_SURFACE_WG, levelheightaccessor, randomstate)};
   }

   protected static int getLowestY(Structure.GenerationContext p_226573_, int p_226574_, int p_226575_) {
      ChunkPos chunkpos = p_226573_.chunkPos();
      int i = chunkpos.getMinBlockX();
      int j = chunkpos.getMinBlockZ();
      return getLowestY(p_226573_, i, j, p_226574_, p_226575_);
   }

   protected static int getLowestY(Structure.GenerationContext p_226577_, int p_226578_, int p_226579_, int p_226580_, int p_226581_) {
      int[] aint = getCornerHeights(p_226577_, p_226578_, p_226580_, p_226579_, p_226581_);
      return Math.min(Math.min(aint[0], aint[1]), Math.min(aint[2], aint[3]));
   }

   /** @deprecated */
   @Deprecated
   protected BlockPos getLowestYIn5by5BoxOffset7Blocks(Structure.GenerationContext p_226583_, Rotation p_226584_) {
      int i = 5;
      int j = 5;
      if (p_226584_ == Rotation.CLOCKWISE_90) {
         i = -5;
      } else if (p_226584_ == Rotation.CLOCKWISE_180) {
         i = -5;
         j = -5;
      } else if (p_226584_ == Rotation.COUNTERCLOCKWISE_90) {
         j = -5;
      }

      ChunkPos chunkpos = p_226583_.chunkPos();
      int k = chunkpos.getBlockX(7);
      int l = chunkpos.getBlockZ(7);
      return new BlockPos(k, getLowestY(p_226583_, k, l, i, j), l);
   }

   protected abstract Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext p_226571_);

   public Optional<Structure.GenerationStub> findValidGenerationPoint(Structure.GenerationContext p_263060_) {
      return this.findGenerationPoint(p_263060_).filter((p_262911_) -> {
         return isValidBiome(p_262911_, p_263060_);
      });
   }

   public abstract StructureType<?> type();

   // FORGE START

   private final net.minecraftforge.common.world.ModifiableStructureInfo modifiableStructureInfo;

   /**
    * {@return Cache of original structure data and structure data modified by structure modifiers}
    * Modified structure data is set by server after datapacks and serverconfigs load.
    * Settings field reads are coremodded to redirect to this.
    **/
   public net.minecraftforge.common.world.ModifiableStructureInfo modifiableStructureInfo()
   {
      return this.modifiableStructureInfo;
   }

   /**
    * {@return The structure's settings, with modifications if called after modifiers are applied in server init.}
    */
   public StructureSettings getModifiedStructureSettings() {
      return this.modifiableStructureInfo().get().structureSettings();
   }

   // FORGE END

   public static record GenerationContext(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, BiomeSource biomeSource, RandomState randomState, StructureTemplateManager structureTemplateManager, WorldgenRandom random, long seed, ChunkPos chunkPos, LevelHeightAccessor heightAccessor, Predicate<Holder<Biome>> validBiome) {
      public GenerationContext(RegistryAccess p_226632_, ChunkGenerator p_226633_, BiomeSource p_226634_, RandomState p_226635_, StructureTemplateManager p_226636_, long p_226637_, ChunkPos p_226638_, LevelHeightAccessor p_226639_, Predicate<Holder<Biome>> p_226640_) {
         this(p_226632_, p_226633_, p_226634_, p_226635_, p_226636_, makeRandom(p_226637_, p_226638_), p_226637_, p_226638_, p_226639_, p_226640_);
      }

      private static WorldgenRandom makeRandom(long p_226654_, ChunkPos p_226655_) {
         WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
         worldgenrandom.setLargeFeatureSeed(p_226654_, p_226655_.x, p_226655_.z);
         return worldgenrandom;
      }
   }

   public static record GenerationStub(BlockPos position, Either<Consumer<StructurePiecesBuilder>, StructurePiecesBuilder> generator) {
      public GenerationStub(BlockPos p_226675_, Consumer<StructurePiecesBuilder> p_226676_) {
         this(p_226675_, Either.left(p_226676_));
      }

      public StructurePiecesBuilder getPiecesBuilder() {
         return this.generator.map((p_226681_) -> {
            StructurePiecesBuilder structurepiecesbuilder = new StructurePiecesBuilder();
            p_226681_.accept(structurepiecesbuilder);
            return structurepiecesbuilder;
         }, (p_226679_) -> {
            return p_226679_;
         });
      }
   }

   public static record StructureSettings(HolderSet<Biome> biomes, Map<MobCategory, StructureSpawnOverride> spawnOverrides, GenerationStep.Decoration step, TerrainAdjustment terrainAdaptation) {
      public static final MapCodec<Structure.StructureSettings> CODEC = RecordCodecBuilder.mapCodec((p_259014_) -> {
         return p_259014_.group(RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(Structure.StructureSettings::biomes), Codec.simpleMap(MobCategory.CODEC, StructureSpawnOverride.CODEC, StringRepresentable.keys(MobCategory.values())).fieldOf("spawn_overrides").forGetter(Structure.StructureSettings::spawnOverrides), GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(Structure.StructureSettings::step), TerrainAdjustment.CODEC.optionalFieldOf("terrain_adaptation", TerrainAdjustment.NONE).forGetter(Structure.StructureSettings::terrainAdaptation)).apply(p_259014_, Structure.StructureSettings::new);
      });
   }
}
