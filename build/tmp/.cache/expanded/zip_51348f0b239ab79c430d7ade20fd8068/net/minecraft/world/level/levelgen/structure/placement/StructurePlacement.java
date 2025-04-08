package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public abstract class StructurePlacement {
   public static final Codec<StructurePlacement> CODEC = BuiltInRegistries.STRUCTURE_PLACEMENT.byNameCodec().dispatch(StructurePlacement::type, StructurePlacementType::codec);
   private static final int HIGHLY_ARBITRARY_RANDOM_SALT = 10387320;
   private final Vec3i locateOffset;
   private final StructurePlacement.FrequencyReductionMethod frequencyReductionMethod;
   private final float frequency;
   private final int salt;
   private final Optional<StructurePlacement.ExclusionZone> exclusionZone;

   protected static <S extends StructurePlacement> Products.P5<RecordCodecBuilder.Mu<S>, Vec3i, StructurePlacement.FrequencyReductionMethod, Float, Integer, Optional<StructurePlacement.ExclusionZone>> placementCodec(RecordCodecBuilder.Instance<S> p_227042_) {
      return p_227042_.group(Vec3i.offsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(StructurePlacement::locateOffset), StructurePlacement.FrequencyReductionMethod.CODEC.optionalFieldOf("frequency_reduction_method", StructurePlacement.FrequencyReductionMethod.DEFAULT).forGetter(StructurePlacement::frequencyReductionMethod), Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(StructurePlacement::frequency), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(StructurePlacement::salt), StructurePlacement.ExclusionZone.CODEC.optionalFieldOf("exclusion_zone").forGetter(StructurePlacement::exclusionZone));
   }

   protected StructurePlacement(Vec3i p_227028_, StructurePlacement.FrequencyReductionMethod p_227029_, float p_227030_, int p_227031_, Optional<StructurePlacement.ExclusionZone> p_227032_) {
      this.locateOffset = p_227028_;
      this.frequencyReductionMethod = p_227029_;
      this.frequency = p_227030_;
      this.salt = p_227031_;
      this.exclusionZone = p_227032_;
   }

   protected Vec3i locateOffset() {
      return this.locateOffset;
   }

   protected StructurePlacement.FrequencyReductionMethod frequencyReductionMethod() {
      return this.frequencyReductionMethod;
   }

   protected float frequency() {
      return this.frequency;
   }

   protected int salt() {
      return this.salt;
   }

   protected Optional<StructurePlacement.ExclusionZone> exclusionZone() {
      return this.exclusionZone;
   }

   public boolean isStructureChunk(ChunkGeneratorStructureState p_256635_, int p_255959_, int p_256065_) {
      if (!this.isPlacementChunk(p_256635_, p_255959_, p_256065_)) {
         return false;
      } else if (this.frequency < 1.0F && !this.frequencyReductionMethod.shouldGenerate(p_256635_.getLevelSeed(), this.salt, p_255959_, p_256065_, this.frequency)) {
         return false;
      } else {
         return !this.exclusionZone.isPresent() || !this.exclusionZone.get().isPlacementForbidden(p_256635_, p_255959_, p_256065_);
      }
   }

   protected abstract boolean isPlacementChunk(ChunkGeneratorStructureState p_256034_, int p_227046_, int p_227047_);

   public BlockPos getLocatePos(ChunkPos p_227040_) {
      return (new BlockPos(p_227040_.getMinBlockX(), 0, p_227040_.getMinBlockZ())).offset(this.locateOffset());
   }

   public abstract StructurePlacementType<?> type();

   private static boolean probabilityReducer(long p_227034_, int p_227035_, int p_227036_, int p_227037_, float p_227038_) {
      WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
      worldgenrandom.setLargeFeatureWithSalt(p_227034_, p_227035_, p_227036_, p_227037_);
      return worldgenrandom.nextFloat() < p_227038_;
   }

   private static boolean legacyProbabilityReducerWithDouble(long p_227049_, int p_227050_, int p_227051_, int p_227052_, float p_227053_) {
      WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
      worldgenrandom.setLargeFeatureSeed(p_227049_, p_227051_, p_227052_);
      return worldgenrandom.nextDouble() < (double)p_227053_;
   }

   private static boolean legacyArbitrarySaltProbabilityReducer(long p_227061_, int p_227062_, int p_227063_, int p_227064_, float p_227065_) {
      WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
      worldgenrandom.setLargeFeatureWithSalt(p_227061_, p_227063_, p_227064_, 10387320);
      return worldgenrandom.nextFloat() < p_227065_;
   }

   private static boolean legacyPillagerOutpostReducer(long p_227067_, int p_227068_, int p_227069_, int p_227070_, float p_227071_) {
      int i = p_227069_ >> 4;
      int j = p_227070_ >> 4;
      WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
      worldgenrandom.setSeed((long)(i ^ j << 4) ^ p_227067_);
      worldgenrandom.nextInt();
      return worldgenrandom.nextInt((int)(1.0F / p_227071_)) == 0;
   }

   /** @deprecated */
   @Deprecated
   public static record ExclusionZone(Holder<StructureSet> otherSet, int chunkCount) {
      public static final Codec<StructurePlacement.ExclusionZone> CODEC = RecordCodecBuilder.create((p_259015_) -> {
         return p_259015_.group(RegistryFileCodec.create(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC, false).fieldOf("other_set").forGetter(StructurePlacement.ExclusionZone::otherSet), Codec.intRange(1, 16).fieldOf("chunk_count").forGetter(StructurePlacement.ExclusionZone::chunkCount)).apply(p_259015_, StructurePlacement.ExclusionZone::new);
      });

      boolean isPlacementForbidden(ChunkGeneratorStructureState p_255745_, int p_255634_, int p_255892_) {
         return p_255745_.hasStructureChunkInRange(this.otherSet, p_255634_, p_255892_, this.chunkCount);
      }
   }

   @FunctionalInterface
   public interface FrequencyReducer {
      boolean shouldGenerate(long p_227099_, int p_227100_, int p_227101_, int p_227102_, float p_227103_);
   }

   public static enum FrequencyReductionMethod implements StringRepresentable {
      DEFAULT("default", StructurePlacement::probabilityReducer),
      LEGACY_TYPE_1("legacy_type_1", StructurePlacement::legacyPillagerOutpostReducer),
      LEGACY_TYPE_2("legacy_type_2", StructurePlacement::legacyArbitrarySaltProbabilityReducer),
      LEGACY_TYPE_3("legacy_type_3", StructurePlacement::legacyProbabilityReducerWithDouble);

      public static final Codec<StructurePlacement.FrequencyReductionMethod> CODEC = StringRepresentable.fromEnum(StructurePlacement.FrequencyReductionMethod::values);
      private final String name;
      private final StructurePlacement.FrequencyReducer reducer;

      private FrequencyReductionMethod(String p_227116_, StructurePlacement.FrequencyReducer p_227117_) {
         this.name = p_227116_;
         this.reducer = p_227117_;
      }

      public boolean shouldGenerate(long p_227120_, int p_227121_, int p_227122_, int p_227123_, float p_227124_) {
         return this.reducer.shouldGenerate(p_227120_, p_227121_, p_227122_, p_227123_, p_227124_);
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}