package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public abstract class StructurePoolElement {
   public static final Codec<StructurePoolElement> CODEC = BuiltInRegistries.STRUCTURE_POOL_ELEMENT.byNameCodec().dispatch("element_type", StructurePoolElement::getType, StructurePoolElementType::codec);
   private static final Holder<StructureProcessorList> EMPTY = Holder.direct(new StructureProcessorList(List.of()));
   @Nullable
   private volatile StructureTemplatePool.Projection projection;

   protected static <E extends StructurePoolElement> RecordCodecBuilder<E, StructureTemplatePool.Projection> projectionCodec() {
      return StructureTemplatePool.Projection.CODEC.fieldOf("projection").forGetter(StructurePoolElement::getProjection);
   }

   protected StructurePoolElement(StructureTemplatePool.Projection p_210471_) {
      this.projection = p_210471_;
   }

   public abstract Vec3i getSize(StructureTemplateManager p_227346_, Rotation p_227347_);

   public abstract List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager p_227351_, BlockPos p_227352_, Rotation p_227353_, RandomSource p_227354_);

   public abstract BoundingBox getBoundingBox(StructureTemplateManager p_227348_, BlockPos p_227349_, Rotation p_227350_);

   public abstract boolean place(StructureTemplateManager p_227336_, WorldGenLevel p_227337_, StructureManager p_227338_, ChunkGenerator p_227339_, BlockPos p_227340_, BlockPos p_227341_, Rotation p_227342_, BoundingBox p_227343_, RandomSource p_227344_, boolean p_227345_);

   public abstract StructurePoolElementType<?> getType();

   public void handleDataMarker(LevelAccessor p_227330_, StructureTemplate.StructureBlockInfo p_227331_, BlockPos p_227332_, Rotation p_227333_, RandomSource p_227334_, BoundingBox p_227335_) {
   }

   public StructurePoolElement setProjection(StructureTemplatePool.Projection p_210479_) {
      this.projection = p_210479_;
      return this;
   }

   public StructureTemplatePool.Projection getProjection() {
      StructureTemplatePool.Projection structuretemplatepool$projection = this.projection;
      if (structuretemplatepool$projection == null) {
         throw new IllegalStateException();
      } else {
         return structuretemplatepool$projection;
      }
   }

   public int getGroundLevelDelta() {
      return 1;
   }

   public static Function<StructureTemplatePool.Projection, EmptyPoolElement> empty() {
      return (p_210525_) -> {
         return EmptyPoolElement.INSTANCE;
      };
   }

   public static Function<StructureTemplatePool.Projection, LegacySinglePoolElement> legacy(String p_210508_) {
      return (p_255605_) -> {
         return new LegacySinglePoolElement(Either.left(new ResourceLocation(p_210508_)), EMPTY, p_255605_);
      };
   }

   public static Function<StructureTemplatePool.Projection, LegacySinglePoolElement> legacy(String p_210513_, Holder<StructureProcessorList> p_210514_) {
      return (p_210537_) -> {
         return new LegacySinglePoolElement(Either.left(new ResourceLocation(p_210513_)), p_210514_, p_210537_);
      };
   }

   public static Function<StructureTemplatePool.Projection, SinglePoolElement> single(String p_210527_) {
      return (p_255603_) -> {
         return new SinglePoolElement(Either.left(new ResourceLocation(p_210527_)), EMPTY, p_255603_);
      };
   }

   public static Function<StructureTemplatePool.Projection, SinglePoolElement> single(String p_210532_, Holder<StructureProcessorList> p_210533_) {
      return (p_210518_) -> {
         return new SinglePoolElement(Either.left(new ResourceLocation(p_210532_)), p_210533_, p_210518_);
      };
   }

   public static Function<StructureTemplatePool.Projection, FeaturePoolElement> feature(Holder<PlacedFeature> p_210503_) {
      return (p_210506_) -> {
         return new FeaturePoolElement(p_210503_, p_210506_);
      };
   }

   public static Function<StructureTemplatePool.Projection, ListPoolElement> list(List<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>> p_210520_) {
      return (p_210523_) -> {
         return new ListPoolElement(p_210520_.stream().map((p_210482_) -> {
            return p_210482_.apply(p_210523_);
         }).collect(Collectors.toList()), p_210523_);
      };
   }
}