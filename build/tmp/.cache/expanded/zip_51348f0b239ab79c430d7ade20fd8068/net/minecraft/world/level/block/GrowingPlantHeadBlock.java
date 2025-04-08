package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class GrowingPlantHeadBlock extends GrowingPlantBlock implements BonemealableBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_25;
   public static final int MAX_AGE = 25;
   private final double growPerTickProbability;

   protected GrowingPlantHeadBlock(BlockBehaviour.Properties p_53928_, Direction p_53929_, VoxelShape p_53930_, boolean p_53931_, double p_53932_) {
      super(p_53928_, p_53929_, p_53930_, p_53931_);
      this.growPerTickProbability = p_53932_;
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   public BlockState getStateForPlacement(LevelAccessor p_53949_) {
      return this.defaultBlockState().setValue(AGE, Integer.valueOf(p_53949_.getRandom().nextInt(25)));
   }

   public boolean isRandomlyTicking(BlockState p_53961_) {
      return p_53961_.getValue(AGE) < 25;
   }

   public void randomTick(BlockState p_221350_, ServerLevel p_221351_, BlockPos p_221352_, RandomSource p_221353_) {
      if (p_221350_.getValue(AGE) < 25 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_221351_, p_221352_.relative(this.growthDirection), p_221351_.getBlockState(p_221352_.relative(this.growthDirection)),p_221353_.nextDouble() < this.growPerTickProbability)) {
         BlockPos blockpos = p_221352_.relative(this.growthDirection);
         if (this.canGrowInto(p_221351_.getBlockState(blockpos))) {
            p_221351_.setBlockAndUpdate(blockpos, this.getGrowIntoState(p_221350_, p_221351_.random));
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_221351_, blockpos, p_221351_.getBlockState(blockpos));
         }
      }

   }

   protected BlockState getGrowIntoState(BlockState p_221347_, RandomSource p_221348_) {
      return p_221347_.cycle(AGE);
   }

   public BlockState getMaxAgeState(BlockState p_187439_) {
      return p_187439_.setValue(AGE, Integer.valueOf(25));
   }

   public boolean isMaxAge(BlockState p_187441_) {
      return p_187441_.getValue(AGE) == 25;
   }

   protected BlockState updateBodyAfterConvertedFromHead(BlockState p_153329_, BlockState p_153330_) {
      return p_153330_;
   }

   public BlockState updateShape(BlockState p_53951_, Direction p_53952_, BlockState p_53953_, LevelAccessor p_53954_, BlockPos p_53955_, BlockPos p_53956_) {
      if (p_53952_ == this.growthDirection.getOpposite() && !p_53951_.canSurvive(p_53954_, p_53955_)) {
         p_53954_.scheduleTick(p_53955_, this, 1);
      }

      if (p_53952_ != this.growthDirection || !p_53953_.is(this) && !p_53953_.is(this.getBodyBlock())) {
         if (this.scheduleFluidTicks) {
            p_53954_.scheduleTick(p_53955_, Fluids.WATER, Fluids.WATER.getTickDelay(p_53954_));
         }

         return super.updateShape(p_53951_, p_53952_, p_53953_, p_53954_, p_53955_, p_53956_);
      } else {
         return this.updateBodyAfterConvertedFromHead(p_53951_, this.getBodyBlock().defaultBlockState());
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53958_) {
      p_53958_.add(AGE);
   }

   public boolean isValidBonemealTarget(LevelReader p_255931_, BlockPos p_256046_, BlockState p_256550_, boolean p_256181_) {
      return this.canGrowInto(p_255931_.getBlockState(p_256046_.relative(this.growthDirection)));
   }

   public boolean isBonemealSuccess(Level p_221343_, RandomSource p_221344_, BlockPos p_221345_, BlockState p_221346_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_221337_, RandomSource p_221338_, BlockPos p_221339_, BlockState p_221340_) {
      BlockPos blockpos = p_221339_.relative(this.growthDirection);
      int i = Math.min(p_221340_.getValue(AGE) + 1, 25);
      int j = this.getBlocksToGrowWhenBonemealed(p_221338_);

      for(int k = 0; k < j && this.canGrowInto(p_221337_.getBlockState(blockpos)); ++k) {
         p_221337_.setBlockAndUpdate(blockpos, p_221340_.setValue(AGE, Integer.valueOf(i)));
         blockpos = blockpos.relative(this.growthDirection);
         i = Math.min(i + 1, 25);
      }

   }

   protected abstract int getBlocksToGrowWhenBonemealed(RandomSource p_221341_);

   protected abstract boolean canGrowInto(BlockState p_53968_);

   protected GrowingPlantHeadBlock getHeadBlock() {
      return this;
   }
}
