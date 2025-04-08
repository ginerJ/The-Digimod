package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public class PosAlwaysTrueTest extends PosRuleTest {
   public static final Codec<PosAlwaysTrueTest> CODEC = Codec.unit(() -> {
      return PosAlwaysTrueTest.INSTANCE;
   });
   public static final PosAlwaysTrueTest INSTANCE = new PosAlwaysTrueTest();

   private PosAlwaysTrueTest() {
   }

   public boolean test(BlockPos p_230301_, BlockPos p_230302_, BlockPos p_230303_, RandomSource p_230304_) {
      return true;
   }

   protected PosRuleTestType<?> getType() {
      return PosRuleTestType.ALWAYS_TRUE_TEST;
   }
}