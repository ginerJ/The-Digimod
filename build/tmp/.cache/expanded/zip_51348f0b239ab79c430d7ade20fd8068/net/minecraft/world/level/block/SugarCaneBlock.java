package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SugarCaneBlock extends Block implements net.minecraftforge.common.IPlantable {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
   protected static final float AABB_OFFSET = 6.0F;
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

   public SugarCaneBlock(BlockBehaviour.Properties p_57168_) {
      super(p_57168_);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState p_57193_, BlockGetter p_57194_, BlockPos p_57195_, CollisionContext p_57196_) {
      return SHAPE;
   }

   public void tick(BlockState p_222543_, ServerLevel p_222544_, BlockPos p_222545_, RandomSource p_222546_) {
      if (!p_222543_.canSurvive(p_222544_, p_222545_)) {
         p_222544_.destroyBlock(p_222545_, true);
      }

   }

   public void randomTick(BlockState p_222548_, ServerLevel p_222549_, BlockPos p_222550_, RandomSource p_222551_) {
      if (p_222549_.isEmptyBlock(p_222550_.above())) {
         int i;
         for(i = 1; p_222549_.getBlockState(p_222550_.below(i)).is(this); ++i) {
         }

         if (i < 3) {
            int j = p_222548_.getValue(AGE);
            if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_222549_, p_222550_, p_222548_, true)) {
            if (j == 15) {
               p_222549_.setBlockAndUpdate(p_222550_.above(), this.defaultBlockState());
               net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_222549_, p_222550_.above(), this.defaultBlockState());
               p_222549_.setBlock(p_222550_, p_222548_.setValue(AGE, Integer.valueOf(0)), 4);
            } else {
               p_222549_.setBlock(p_222550_, p_222548_.setValue(AGE, Integer.valueOf(j + 1)), 4);
            }
            }
         }
      }

   }

   public BlockState updateShape(BlockState p_57179_, Direction p_57180_, BlockState p_57181_, LevelAccessor p_57182_, BlockPos p_57183_, BlockPos p_57184_) {
      if (!p_57179_.canSurvive(p_57182_, p_57183_)) {
         p_57182_.scheduleTick(p_57183_, this, 1);
      }

      return super.updateShape(p_57179_, p_57180_, p_57181_, p_57182_, p_57183_, p_57184_);
   }

   public boolean canSurvive(BlockState p_57175_, LevelReader p_57176_, BlockPos p_57177_) {
      BlockState soil = p_57176_.getBlockState(p_57177_.below());
      if (soil.canSustainPlant(p_57176_, p_57177_.below(), Direction.UP, this)) return true;
      BlockState blockstate = p_57176_.getBlockState(p_57177_.below());
      if (blockstate.is(this)) {
         return true;
      } else {
         if (blockstate.is(BlockTags.DIRT) || blockstate.is(BlockTags.SAND)) {
            BlockPos blockpos = p_57177_.below();

            for(Direction direction : Direction.Plane.HORIZONTAL) {
               BlockState blockstate1 = p_57176_.getBlockState(blockpos.relative(direction));
               FluidState fluidstate = p_57176_.getFluidState(blockpos.relative(direction));
               if (p_57175_.canBeHydrated(p_57176_, p_57177_, fluidstate, blockpos.relative(direction)) || blockstate1.is(Blocks.FROSTED_ICE)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57186_) {
      p_57186_.add(AGE);
   }

   @Override
   public net.minecraftforge.common.PlantType getPlantType(BlockGetter world, BlockPos pos) {
       return net.minecraftforge.common.PlantType.BEACH;
   }

   @Override
   public BlockState getPlant(BlockGetter world, BlockPos pos) {
      return defaultBlockState();
   }
}
