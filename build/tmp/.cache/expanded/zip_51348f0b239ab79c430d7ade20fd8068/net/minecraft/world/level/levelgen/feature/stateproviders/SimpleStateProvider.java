package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleStateProvider extends BlockStateProvider {
   public static final Codec<SimpleStateProvider> CODEC = BlockState.CODEC.fieldOf("state").xmap(SimpleStateProvider::new, (p_68804_) -> {
      return p_68804_.state;
   }).codec();
   private final BlockState state;

   protected SimpleStateProvider(BlockState p_68801_) {
      this.state = p_68801_;
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.SIMPLE_STATE_PROVIDER;
   }

   public BlockState getState(RandomSource p_225963_, BlockPos p_225964_) {
      return this.state;
   }
}