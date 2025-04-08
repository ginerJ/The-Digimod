package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FungusBlock extends BushBlock implements BonemealableBlock {
   protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 9.0D, 12.0D);
   private static final double BONEMEAL_SUCCESS_PROBABILITY = 0.4D;
   private final Block requiredBlock;
   private final ResourceKey<ConfiguredFeature<?, ?>> feature;

   public FungusBlock(BlockBehaviour.Properties p_259749_, ResourceKey<ConfiguredFeature<?, ?>> p_259087_, Block p_260223_) {
      super(p_259749_);
      this.feature = p_259087_;
      this.requiredBlock = p_260223_;
   }

   public VoxelShape getShape(BlockState p_53618_, BlockGetter p_53619_, BlockPos p_53620_, CollisionContext p_53621_) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState p_53623_, BlockGetter p_53624_, BlockPos p_53625_) {
      return p_53623_.is(BlockTags.NYLIUM) || p_53623_.is(Blocks.MYCELIUM) || p_53623_.is(Blocks.SOUL_SOIL) || super.mayPlaceOn(p_53623_, p_53624_, p_53625_);
   }

   private Optional<? extends Holder<ConfiguredFeature<?, ?>>> getFeature(LevelReader p_256589_) {
      return p_256589_.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(this.feature);
   }

   public boolean isValidBonemealTarget(LevelReader p_256655_, BlockPos p_256553_, BlockState p_256213_, boolean p_256270_) {
      BlockState blockstate = p_256655_.getBlockState(p_256553_.below());
      return blockstate.is(this.requiredBlock);
   }

   public boolean isBonemealSuccess(Level p_221248_, RandomSource p_221249_, BlockPos p_221250_, BlockState p_221251_) {
      return (double)p_221249_.nextFloat() < 0.4D;
   }

   public void performBonemeal(ServerLevel p_221243_, RandomSource p_221244_, BlockPos p_221245_, BlockState p_221246_) {
      this.getFeature(p_221243_).ifPresent((p_256352_) -> {
         var event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(p_221243_, p_221244_, p_221245_, p_256352_);
         if (event.getResult().equals(net.minecraftforge.eventbus.api.Event.Result.DENY)) return;
         event.getFeature().value().place(p_221243_, p_221243_.getChunkSource().getGenerator(), p_221244_, p_221245_);
      });
   }
}
