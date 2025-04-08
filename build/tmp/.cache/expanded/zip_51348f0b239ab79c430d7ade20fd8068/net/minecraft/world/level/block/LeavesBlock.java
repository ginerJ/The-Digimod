package net.minecraft.world.level.block;

import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LeavesBlock extends Block implements SimpleWaterloggedBlock, net.minecraftforge.common.IForgeShearable {
   public static final int DECAY_DISTANCE = 7;
   public static final IntegerProperty DISTANCE = BlockStateProperties.DISTANCE;
   public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private static final int TICK_DELAY = 1;

   public LeavesBlock(BlockBehaviour.Properties p_54422_) {
      super(p_54422_);
      this.registerDefaultState(this.stateDefinition.any().setValue(DISTANCE, Integer.valueOf(7)).setValue(PERSISTENT, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public VoxelShape getBlockSupportShape(BlockState p_54456_, BlockGetter p_54457_, BlockPos p_54458_) {
      return Shapes.empty();
   }

   public boolean isRandomlyTicking(BlockState p_54449_) {
      return p_54449_.getValue(DISTANCE) == 7 && !p_54449_.getValue(PERSISTENT);
   }

   public void randomTick(BlockState p_221379_, ServerLevel p_221380_, BlockPos p_221381_, RandomSource p_221382_) {
      if (this.decaying(p_221379_)) {
         dropResources(p_221379_, p_221380_, p_221381_);
         p_221380_.removeBlock(p_221381_, false);
      }

   }

   protected boolean decaying(BlockState p_221386_) {
      return !p_221386_.getValue(PERSISTENT) && p_221386_.getValue(DISTANCE) == 7;
   }

   public void tick(BlockState p_221369_, ServerLevel p_221370_, BlockPos p_221371_, RandomSource p_221372_) {
      p_221370_.setBlock(p_221371_, updateDistance(p_221369_, p_221370_, p_221371_), 3);
   }

   public int getLightBlock(BlockState p_54460_, BlockGetter p_54461_, BlockPos p_54462_) {
      return 1;
   }

   public BlockState updateShape(BlockState p_54440_, Direction p_54441_, BlockState p_54442_, LevelAccessor p_54443_, BlockPos p_54444_, BlockPos p_54445_) {
      if (p_54440_.getValue(WATERLOGGED)) {
         p_54443_.scheduleTick(p_54444_, Fluids.WATER, Fluids.WATER.getTickDelay(p_54443_));
      }

      int i = getDistanceAt(p_54442_) + 1;
      if (i != 1 || p_54440_.getValue(DISTANCE) != i) {
         p_54443_.scheduleTick(p_54444_, this, 1);
      }

      return p_54440_;
   }

   private static BlockState updateDistance(BlockState p_54436_, LevelAccessor p_54437_, BlockPos p_54438_) {
      int i = 7;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(Direction direction : Direction.values()) {
         blockpos$mutableblockpos.setWithOffset(p_54438_, direction);
         i = Math.min(i, getDistanceAt(p_54437_.getBlockState(blockpos$mutableblockpos)) + 1);
         if (i == 1) {
            break;
         }
      }

      return p_54436_.setValue(DISTANCE, Integer.valueOf(i));
   }

   private static int getDistanceAt(BlockState p_54464_) {
      return getOptionalDistanceAt(p_54464_).orElse(7);
   }

   public static OptionalInt getOptionalDistanceAt(BlockState p_277868_) {
      if (p_277868_.is(BlockTags.LOGS)) {
         return OptionalInt.of(0);
      } else {
         return p_277868_.hasProperty(DISTANCE) ? OptionalInt.of(p_277868_.getValue(DISTANCE)) : OptionalInt.empty();
      }
   }

   public FluidState getFluidState(BlockState p_221384_) {
      return p_221384_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_221384_);
   }

   public void animateTick(BlockState p_221374_, Level p_221375_, BlockPos p_221376_, RandomSource p_221377_) {
      if (p_221375_.isRainingAt(p_221376_.above())) {
         if (p_221377_.nextInt(15) == 1) {
            BlockPos blockpos = p_221376_.below();
            BlockState blockstate = p_221375_.getBlockState(blockpos);
            if (!blockstate.canOcclude() || !blockstate.isFaceSturdy(p_221375_, blockpos, Direction.UP)) {
               ParticleUtils.spawnParticleBelow(p_221375_, p_221376_, p_221377_, ParticleTypes.DRIPPING_WATER);
            }
         }
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54447_) {
      p_54447_.add(DISTANCE, PERSISTENT, WATERLOGGED);
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_54424_) {
      FluidState fluidstate = p_54424_.getLevel().getFluidState(p_54424_.getClickedPos());
      BlockState blockstate = this.defaultBlockState().setValue(PERSISTENT, Boolean.valueOf(true)).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
      return updateDistance(blockstate, p_54424_.getLevel(), p_54424_.getClickedPos());
   }
}
