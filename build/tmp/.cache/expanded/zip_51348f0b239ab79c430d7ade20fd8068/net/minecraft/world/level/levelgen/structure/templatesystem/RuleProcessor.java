package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class RuleProcessor extends StructureProcessor {
   public static final Codec<RuleProcessor> CODEC = ProcessorRule.CODEC.listOf().fieldOf("rules").xmap(RuleProcessor::new, (p_74306_) -> {
      return p_74306_.rules;
   }).codec();
   private final ImmutableList<ProcessorRule> rules;

   public RuleProcessor(List<? extends ProcessorRule> p_74296_) {
      this.rules = ImmutableList.copyOf(p_74296_);
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader p_74299_, BlockPos p_74300_, BlockPos p_74301_, StructureTemplate.StructureBlockInfo p_74302_, StructureTemplate.StructureBlockInfo p_74303_, StructurePlaceSettings p_74304_) {
      RandomSource randomsource = RandomSource.create(Mth.getSeed(p_74303_.pos()));
      BlockState blockstate = p_74299_.getBlockState(p_74303_.pos());

      for(ProcessorRule processorrule : this.rules) {
         if (processorrule.test(p_74303_.state(), blockstate, p_74302_.pos(), p_74303_.pos(), p_74301_, randomsource)) {
            return new StructureTemplate.StructureBlockInfo(p_74303_.pos(), processorrule.getOutputState(), processorrule.getOutputTag(randomsource, p_74303_.nbt()));
         }
      }

      return p_74303_;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.RULE;
   }
}