package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FarmBlock extends Block {
   public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE;
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);
   public static final int MAX_MOISTURE = 7;

   public FarmBlock(BlockBehaviour.Properties p_53247_) {
      super(p_53247_);
      this.registerDefaultState(this.stateDefinition.any().setValue(MOISTURE, Integer.valueOf(0)));
   }

   public BlockState updateShape(BlockState p_53276_, Direction p_53277_, BlockState p_53278_, LevelAccessor p_53279_, BlockPos p_53280_, BlockPos p_53281_) {
      if (p_53277_ == Direction.UP && !p_53276_.canSurvive(p_53279_, p_53280_)) {
         p_53279_.scheduleTick(p_53280_, this, 1);
      }

      return super.updateShape(p_53276_, p_53277_, p_53278_, p_53279_, p_53280_, p_53281_);
   }

   public boolean canSurvive(BlockState p_53272_, LevelReader p_53273_, BlockPos p_53274_) {
      BlockState blockstate = p_53273_.getBlockState(p_53274_.above());
      return !blockstate.isSolid() || blockstate.getBlock() instanceof FenceGateBlock || blockstate.getBlock() instanceof MovingPistonBlock;
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_53249_) {
      return !this.defaultBlockState().canSurvive(p_53249_.getLevel(), p_53249_.getClickedPos()) ? Blocks.DIRT.defaultBlockState() : super.getStateForPlacement(p_53249_);
   }

   public boolean useShapeForLightOcclusion(BlockState p_53295_) {
      return true;
   }

   public VoxelShape getShape(BlockState p_53290_, BlockGetter p_53291_, BlockPos p_53292_, CollisionContext p_53293_) {
      return SHAPE;
   }

   public void tick(BlockState p_221134_, ServerLevel p_221135_, BlockPos p_221136_, RandomSource p_221137_) {
      if (!p_221134_.canSurvive(p_221135_, p_221136_)) {
         turnToDirt((Entity)null, p_221134_, p_221135_, p_221136_);
      }

   }

   public void randomTick(BlockState p_221139_, ServerLevel p_221140_, BlockPos p_221141_, RandomSource p_221142_) {
      int i = p_221139_.getValue(MOISTURE);
      if (!isNearWater(p_221140_, p_221141_) && !p_221140_.isRainingAt(p_221141_.above())) {
         if (i > 0) {
            p_221140_.setBlock(p_221141_, p_221139_.setValue(MOISTURE, Integer.valueOf(i - 1)), 2);
         } else if (!shouldMaintainFarmland(p_221140_, p_221141_)) {
            turnToDirt((Entity)null, p_221139_, p_221140_, p_221141_);
         }
      } else if (i < 7) {
         p_221140_.setBlock(p_221141_, p_221139_.setValue(MOISTURE, Integer.valueOf(7)), 2);
      }

   }

   public void fallOn(Level p_153227_, BlockState p_153228_, BlockPos p_153229_, Entity p_153230_, float p_153231_) {
      if (!p_153227_.isClientSide && net.minecraftforge.common.ForgeHooks.onFarmlandTrample(p_153227_, p_153229_, Blocks.DIRT.defaultBlockState(), p_153231_, p_153230_)) { // Forge: Move logic to Entity#canTrample
         turnToDirt(p_153230_, p_153228_, p_153227_, p_153229_);
      }

      super.fallOn(p_153227_, p_153228_, p_153229_, p_153230_, p_153231_);
   }

   public static void turnToDirt(@Nullable Entity p_270981_, BlockState p_270402_, Level p_270568_, BlockPos p_270551_) {
      BlockState blockstate = pushEntitiesUp(p_270402_, Blocks.DIRT.defaultBlockState(), p_270568_, p_270551_);
      p_270568_.setBlockAndUpdate(p_270551_, blockstate);
      p_270568_.gameEvent(GameEvent.BLOCK_CHANGE, p_270551_, GameEvent.Context.of(p_270981_, blockstate));
   }

   private static boolean shouldMaintainFarmland(BlockGetter p_279219_, BlockPos p_279209_) {
      BlockState plant = p_279219_.getBlockState(p_279209_.above());
      BlockState state = p_279219_.getBlockState(p_279209_);
      return plant.getBlock() instanceof net.minecraftforge.common.IPlantable && state.canSustainPlant(p_279219_, p_279209_, Direction.UP, (net.minecraftforge.common.IPlantable)plant.getBlock());
   }

   private static boolean isNearWater(LevelReader p_53259_, BlockPos p_53260_) {
      BlockState state = p_53259_.getBlockState(p_53260_);
      for(BlockPos blockpos : BlockPos.betweenClosed(p_53260_.offset(-4, 0, -4), p_53260_.offset(4, 1, 4))) {
         if (state.canBeHydrated(p_53259_, p_53260_, p_53259_.getFluidState(blockpos), blockpos)) {
            return true;
         }
      }

      return net.minecraftforge.common.FarmlandWaterManager.hasBlockWaterTicket(p_53259_, p_53260_);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53283_) {
      p_53283_.add(MOISTURE);
   }

   public boolean isPathfindable(BlockState p_53267_, BlockGetter p_53268_, BlockPos p_53269_, PathComputationType p_53270_) {
      return false;
   }
}
