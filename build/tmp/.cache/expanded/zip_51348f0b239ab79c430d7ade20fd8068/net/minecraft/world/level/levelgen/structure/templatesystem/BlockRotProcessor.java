package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;

public class BlockRotProcessor extends StructureProcessor {
   public static final Codec<BlockRotProcessor> CODEC = RecordCodecBuilder.create((p_259016_) -> {
      return p_259016_.group(RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("rottable_blocks").forGetter((p_230291_) -> {
         return p_230291_.rottableBlocks;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("integrity").forGetter((p_230289_) -> {
         return p_230289_.integrity;
      })).apply(p_259016_, BlockRotProcessor::new);
   });
   private final Optional<HolderSet<Block>> rottableBlocks;
   private final float integrity;

   public BlockRotProcessor(HolderSet<Block> p_255622_, float p_256468_) {
      this(Optional.of(p_255622_), p_256468_);
   }

   public BlockRotProcessor(float p_74078_) {
      this(Optional.empty(), p_74078_);
   }

   private BlockRotProcessor(Optional<HolderSet<Block>> p_230284_, float p_230285_) {
      this.integrity = p_230285_;
      this.rottableBlocks = p_230284_;
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader p_74081_, BlockPos p_74082_, BlockPos p_74083_, StructureTemplate.StructureBlockInfo p_74084_, StructureTemplate.StructureBlockInfo p_74085_, StructurePlaceSettings p_74086_) {
      RandomSource randomsource = p_74086_.getRandom(p_74085_.pos());
      return (!this.rottableBlocks.isPresent() || p_74084_.state().is(this.rottableBlocks.get())) && !(randomsource.nextFloat() <= this.integrity) ? null : p_74085_;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.BLOCK_ROT;
   }
}