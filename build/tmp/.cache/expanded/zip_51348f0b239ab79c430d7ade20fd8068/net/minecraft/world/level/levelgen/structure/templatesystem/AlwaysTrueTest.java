package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class AlwaysTrueTest extends RuleTest {
   public static final Codec<AlwaysTrueTest> CODEC = Codec.unit(() -> {
      return AlwaysTrueTest.INSTANCE;
   });
   public static final AlwaysTrueTest INSTANCE = new AlwaysTrueTest();

   private AlwaysTrueTest() {
   }

   public boolean test(BlockState p_230248_, RandomSource p_230249_) {
      return true;
   }

   protected RuleTestType<?> getType() {
      return RuleTestType.ALWAYS_TRUE_TEST;
   }
}