package net.minecraft.world.level.levelgen.feature.rootplacers;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public abstract class RootPlacer {
   public static final Codec<RootPlacer> CODEC = BuiltInRegistries.ROOT_PLACER_TYPE.byNameCodec().dispatch(RootPlacer::type, RootPlacerType::codec);
   protected final IntProvider trunkOffsetY;
   protected final BlockStateProvider rootProvider;
   protected final Optional<AboveRootPlacement> aboveRootPlacement;

   protected static <P extends RootPlacer> Products.P3<RecordCodecBuilder.Mu<P>, IntProvider, BlockStateProvider, Optional<AboveRootPlacement>> rootPlacerParts(RecordCodecBuilder.Instance<P> p_225886_) {
      return p_225886_.group(IntProvider.CODEC.fieldOf("trunk_offset_y").forGetter((p_225897_) -> {
         return p_225897_.trunkOffsetY;
      }), BlockStateProvider.CODEC.fieldOf("root_provider").forGetter((p_225895_) -> {
         return p_225895_.rootProvider;
      }), AboveRootPlacement.CODEC.optionalFieldOf("above_root_placement").forGetter((p_225888_) -> {
         return p_225888_.aboveRootPlacement;
      }));
   }

   public RootPlacer(IntProvider p_225865_, BlockStateProvider p_225866_, Optional<AboveRootPlacement> p_225867_) {
      this.trunkOffsetY = p_225865_;
      this.rootProvider = p_225866_;
      this.aboveRootPlacement = p_225867_;
   }

   protected abstract RootPlacerType<?> type();

   public abstract boolean placeRoots(LevelSimulatedReader p_225879_, BiConsumer<BlockPos, BlockState> p_225880_, RandomSource p_225881_, BlockPos p_225882_, BlockPos p_225883_, TreeConfiguration p_225884_);

   protected boolean canPlaceRoot(LevelSimulatedReader p_225868_, BlockPos p_225869_) {
      return TreeFeature.validTreePos(p_225868_, p_225869_);
   }

   protected void placeRoot(LevelSimulatedReader p_225874_, BiConsumer<BlockPos, BlockState> p_225875_, RandomSource p_225876_, BlockPos p_225877_, TreeConfiguration p_225878_) {
      if (this.canPlaceRoot(p_225874_, p_225877_)) {
         p_225875_.accept(p_225877_, this.getPotentiallyWaterloggedState(p_225874_, p_225877_, this.rootProvider.getState(p_225876_, p_225877_)));
         if (this.aboveRootPlacement.isPresent()) {
            AboveRootPlacement aboverootplacement = this.aboveRootPlacement.get();
            BlockPos blockpos = p_225877_.above();
            if (p_225876_.nextFloat() < aboverootplacement.aboveRootPlacementChance() && p_225874_.isStateAtPosition(blockpos, BlockBehaviour.BlockStateBase::isAir)) {
               p_225875_.accept(blockpos, this.getPotentiallyWaterloggedState(p_225874_, blockpos, aboverootplacement.aboveRootProvider().getState(p_225876_, blockpos)));
            }
         }

      }
   }

   protected BlockState getPotentiallyWaterloggedState(LevelSimulatedReader p_225871_, BlockPos p_225872_, BlockState p_225873_) {
      if (p_225873_.hasProperty(BlockStateProperties.WATERLOGGED)) {
         boolean flag = p_225871_.isFluidAtPosition(p_225872_, (p_225890_) -> {
            return p_225890_.is(FluidTags.WATER);
         });
         return p_225873_.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(flag));
      } else {
         return p_225873_;
      }
   }

   public BlockPos getTrunkOrigin(BlockPos p_225892_, RandomSource p_225893_) {
      return p_225892_.above(this.trunkOffsetY.sample(p_225893_));
   }
}