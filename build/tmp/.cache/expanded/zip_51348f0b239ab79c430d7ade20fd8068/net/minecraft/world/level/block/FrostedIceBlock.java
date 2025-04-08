package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class FrostedIceBlock extends IceBlock {
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
   private static final int NEIGHBORS_TO_AGE = 4;
   private static final int NEIGHBORS_TO_MELT = 2;

   public FrostedIceBlock(BlockBehaviour.Properties p_53564_) {
      super(p_53564_);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   public void randomTick(BlockState p_221238_, ServerLevel p_221239_, BlockPos p_221240_, RandomSource p_221241_) {
      this.tick(p_221238_, p_221239_, p_221240_, p_221241_);
   }

   public void tick(BlockState p_221233_, ServerLevel p_221234_, BlockPos p_221235_, RandomSource p_221236_) {
      if ((p_221236_.nextInt(3) == 0 || this.fewerNeigboursThan(p_221234_, p_221235_, 4)) && p_221234_.getMaxLocalRawBrightness(p_221235_) > 11 - p_221233_.getValue(AGE) - p_221233_.getLightBlock(p_221234_, p_221235_) && this.slightlyMelt(p_221233_, p_221234_, p_221235_)) {
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(Direction direction : Direction.values()) {
            blockpos$mutableblockpos.setWithOffset(p_221235_, direction);
            BlockState blockstate = p_221234_.getBlockState(blockpos$mutableblockpos);
            if (blockstate.is(this) && !this.slightlyMelt(blockstate, p_221234_, blockpos$mutableblockpos)) {
               p_221234_.scheduleTick(blockpos$mutableblockpos, this, Mth.nextInt(p_221236_, 20, 40));
            }
         }

      } else {
         p_221234_.scheduleTick(p_221235_, this, Mth.nextInt(p_221236_, 20, 40));
      }
   }

   private boolean slightlyMelt(BlockState p_53593_, Level p_53594_, BlockPos p_53595_) {
      int i = p_53593_.getValue(AGE);
      if (i < 3) {
         p_53594_.setBlock(p_53595_, p_53593_.setValue(AGE, Integer.valueOf(i + 1)), 2);
         return false;
      } else {
         this.melt(p_53593_, p_53594_, p_53595_);
         return true;
      }
   }

   public void neighborChanged(BlockState p_53579_, Level p_53580_, BlockPos p_53581_, Block p_53582_, BlockPos p_53583_, boolean p_53584_) {
      if (p_53582_.defaultBlockState().is(this) && this.fewerNeigboursThan(p_53580_, p_53581_, 2)) {
         this.melt(p_53579_, p_53580_, p_53581_);
      }

      super.neighborChanged(p_53579_, p_53580_, p_53581_, p_53582_, p_53583_, p_53584_);
   }

   private boolean fewerNeigboursThan(BlockGetter p_53566_, BlockPos p_53567_, int p_53568_) {
      int i = 0;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(Direction direction : Direction.values()) {
         blockpos$mutableblockpos.setWithOffset(p_53567_, direction);
         if (p_53566_.getBlockState(blockpos$mutableblockpos).is(this)) {
            ++i;
            if (i >= p_53568_) {
               return false;
            }
         }
      }

      return true;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53586_) {
      p_53586_.add(AGE);
   }

   public ItemStack getCloneItemStack(BlockGetter p_53570_, BlockPos p_53571_, BlockState p_53572_) {
      return ItemStack.EMPTY;
   }
}