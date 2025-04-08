package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;

public class CountPlacement extends RepeatingPlacement {
   public static final Codec<CountPlacement> CODEC = IntProvider.codec(0, 256).fieldOf("count").xmap(CountPlacement::new, (p_191633_) -> {
      return p_191633_.count;
   }).codec();
   private final IntProvider count;

   private CountPlacement(IntProvider p_191627_) {
      this.count = p_191627_;
   }

   public static CountPlacement of(IntProvider p_191631_) {
      return new CountPlacement(p_191631_);
   }

   public static CountPlacement of(int p_191629_) {
      return of(ConstantInt.of(p_191629_));
   }

   protected int count(RandomSource p_226333_, BlockPos p_226334_) {
      return this.count.sample(p_226333_);
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.COUNT;
   }
}