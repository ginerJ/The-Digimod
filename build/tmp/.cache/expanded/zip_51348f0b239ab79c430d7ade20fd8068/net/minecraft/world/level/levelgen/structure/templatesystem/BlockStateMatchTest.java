package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateMatchTest extends RuleTest {
   public static final Codec<BlockStateMatchTest> CODEC = BlockState.CODEC.fieldOf("block_state").xmap(BlockStateMatchTest::new, (p_74099_) -> {
      return p_74099_.blockState;
   }).codec();
   private final BlockState blockState;

   public BlockStateMatchTest(BlockState p_74093_) {
      this.blockState = p_74093_;
   }

   public boolean test(BlockState p_230293_, RandomSource p_230294_) {
      return p_230293_ == this.blockState;
   }

   protected RuleTestType<?> getType() {
      return RuleTestType.BLOCKSTATE_TEST;
   }
}