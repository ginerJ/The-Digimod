package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SculkSensorBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final int ACTIVE_TICKS = 30;
   public static final int COOLDOWN_TICKS = 10;
   public static final EnumProperty<SculkSensorPhase> PHASE = BlockStateProperties.SCULK_SENSOR_PHASE;
   public static final IntegerProperty POWER = BlockStateProperties.POWER;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   private static final float[] RESONANCE_PITCH_BEND = Util.make(new float[16], (p_277301_) -> {
      int[] aint = new int[]{0, 0, 2, 4, 6, 7, 9, 10, 12, 14, 15, 18, 19, 21, 22, 24};

      for(int i = 0; i < 16; ++i) {
         p_277301_[i] = NoteBlock.getPitchFromNote(aint[i]);
      }

   });

   public SculkSensorBlock(BlockBehaviour.Properties p_277588_) {
      super(p_277588_);
      this.registerDefaultState(this.stateDefinition.any().setValue(PHASE, SculkSensorPhase.INACTIVE).setValue(POWER, Integer.valueOf(0)).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_154396_) {
      BlockPos blockpos = p_154396_.getClickedPos();
      FluidState fluidstate = p_154396_.getLevel().getFluidState(blockpos);
      return this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
   }

   public FluidState getFluidState(BlockState p_154479_) {
      return p_154479_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_154479_);
   }

   public void tick(BlockState p_222137_, ServerLevel p_222138_, BlockPos p_222139_, RandomSource p_222140_) {
      if (getPhase(p_222137_) != SculkSensorPhase.ACTIVE) {
         if (getPhase(p_222137_) == SculkSensorPhase.COOLDOWN) {
            p_222138_.setBlock(p_222139_, p_222137_.setValue(PHASE, SculkSensorPhase.INACTIVE), 3);
            if (!p_222137_.getValue(WATERLOGGED)) {
               p_222138_.playSound((Player)null, p_222139_, SoundEvents.SCULK_CLICKING_STOP, SoundSource.BLOCKS, 1.0F, p_222138_.random.nextFloat() * 0.2F + 0.8F);
            }
         }

      } else {
         deactivate(p_222138_, p_222139_, p_222137_);
      }
   }

   public void stepOn(Level p_222132_, BlockPos p_222133_, BlockState p_222134_, Entity p_222135_) {
      if (!p_222132_.isClientSide() && canActivate(p_222134_) && p_222135_.getType() != EntityType.WARDEN) {
         BlockEntity blockentity = p_222132_.getBlockEntity(p_222133_);
         if (blockentity instanceof SculkSensorBlockEntity) {
            SculkSensorBlockEntity sculksensorblockentity = (SculkSensorBlockEntity)blockentity;
            if (p_222132_ instanceof ServerLevel) {
               ServerLevel serverlevel = (ServerLevel)p_222132_;
               if (sculksensorblockentity.getVibrationUser().canReceiveVibration(serverlevel, p_222133_, GameEvent.STEP, GameEvent.Context.of(p_222134_))) {
                  sculksensorblockentity.getListener().forceScheduleVibration(serverlevel, GameEvent.STEP, GameEvent.Context.of(p_222135_), p_222135_.position());
               }
            }
         }
      }

      super.stepOn(p_222132_, p_222133_, p_222134_, p_222135_);
   }

   public void onPlace(BlockState p_154471_, Level p_154472_, BlockPos p_154473_, BlockState p_154474_, boolean p_154475_) {
      if (!p_154472_.isClientSide() && !p_154471_.is(p_154474_.getBlock())) {
         if (p_154471_.getValue(POWER) > 0 && !p_154472_.getBlockTicks().hasScheduledTick(p_154473_, this)) {
            p_154472_.setBlock(p_154473_, p_154471_.setValue(POWER, Integer.valueOf(0)), 18);
         }

      }
   }

   public void onRemove(BlockState p_154446_, Level p_154447_, BlockPos p_154448_, BlockState p_154449_, boolean p_154450_) {
      if (!p_154446_.is(p_154449_.getBlock())) {
         if (getPhase(p_154446_) == SculkSensorPhase.ACTIVE) {
            updateNeighbours(p_154447_, p_154448_, p_154446_);
         }

         super.onRemove(p_154446_, p_154447_, p_154448_, p_154449_, p_154450_);
      }
   }

   public BlockState updateShape(BlockState p_154457_, Direction p_154458_, BlockState p_154459_, LevelAccessor p_154460_, BlockPos p_154461_, BlockPos p_154462_) {
      if (p_154457_.getValue(WATERLOGGED)) {
         p_154460_.scheduleTick(p_154461_, Fluids.WATER, Fluids.WATER.getTickDelay(p_154460_));
      }

      return super.updateShape(p_154457_, p_154458_, p_154459_, p_154460_, p_154461_, p_154462_);
   }

   private static void updateNeighbours(Level p_278067_, BlockPos p_277440_, BlockState p_277354_) {
      Block block = p_277354_.getBlock();
      p_278067_.updateNeighborsAt(p_277440_, block);
      p_278067_.updateNeighborsAt(p_277440_.below(), block);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos p_154466_, BlockState p_154467_) {
      return new SculkSensorBlockEntity(p_154466_, p_154467_);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_154401_, BlockState p_154402_, BlockEntityType<T> p_154403_) {
      return !p_154401_.isClientSide ? createTickerHelper(p_154403_, BlockEntityType.SCULK_SENSOR, (p_281130_, p_281131_, p_281132_, p_281133_) -> {
         VibrationSystem.Ticker.tick(p_281130_, p_281133_.getVibrationData(), p_281133_.getVibrationUser());
      }) : null;
   }

   public RenderShape getRenderShape(BlockState p_154477_) {
      return RenderShape.MODEL;
   }

   public VoxelShape getShape(BlockState p_154432_, BlockGetter p_154433_, BlockPos p_154434_, CollisionContext p_154435_) {
      return SHAPE;
   }

   public boolean isSignalSource(BlockState p_154484_) {
      return true;
   }

   public int getSignal(BlockState p_154437_, BlockGetter p_154438_, BlockPos p_154439_, Direction p_154440_) {
      return p_154437_.getValue(POWER);
   }

   public int getDirectSignal(BlockState p_279407_, BlockGetter p_279217_, BlockPos p_279190_, Direction p_279273_) {
      return p_279273_ == Direction.UP ? p_279407_.getSignal(p_279217_, p_279190_, p_279273_) : 0;
   }

   public static SculkSensorPhase getPhase(BlockState p_154488_) {
      return p_154488_.getValue(PHASE);
   }

   public static boolean canActivate(BlockState p_154490_) {
      return getPhase(p_154490_) == SculkSensorPhase.INACTIVE;
   }

   public static void deactivate(Level p_154408_, BlockPos p_154409_, BlockState p_154410_) {
      p_154408_.setBlock(p_154409_, p_154410_.setValue(PHASE, SculkSensorPhase.COOLDOWN).setValue(POWER, Integer.valueOf(0)), 3);
      p_154408_.scheduleTick(p_154409_, p_154410_.getBlock(), 10);
      updateNeighbours(p_154408_, p_154409_, p_154410_);
   }

   @VisibleForTesting
   public int getActiveTicks() {
      return 30;
   }

   public void activate(@Nullable Entity p_277529_, Level p_277340_, BlockPos p_277386_, BlockState p_277799_, int p_277993_, int p_278003_) {
      p_277340_.setBlock(p_277386_, p_277799_.setValue(PHASE, SculkSensorPhase.ACTIVE).setValue(POWER, Integer.valueOf(p_277993_)), 3);
      p_277340_.scheduleTick(p_277386_, p_277799_.getBlock(), this.getActiveTicks());
      updateNeighbours(p_277340_, p_277386_, p_277799_);
      tryResonateVibration(p_277529_, p_277340_, p_277386_, p_278003_);
      p_277340_.gameEvent(p_277529_, GameEvent.SCULK_SENSOR_TENDRILS_CLICKING, p_277386_);
      if (!p_277799_.getValue(WATERLOGGED)) {
         p_277340_.playSound((Player)null, (double)p_277386_.getX() + 0.5D, (double)p_277386_.getY() + 0.5D, (double)p_277386_.getZ() + 0.5D, SoundEvents.SCULK_CLICKING, SoundSource.BLOCKS, 1.0F, p_277340_.random.nextFloat() * 0.2F + 0.8F);
      }

   }

   public static void tryResonateVibration(@Nullable Entity p_279315_, Level p_277804_, BlockPos p_277458_, int p_277347_) {
      for(Direction direction : Direction.values()) {
         BlockPos blockpos = p_277458_.relative(direction);
         BlockState blockstate = p_277804_.getBlockState(blockpos);
         if (blockstate.is(BlockTags.VIBRATION_RESONATORS)) {
            p_277804_.gameEvent(VibrationSystem.getResonanceEventByFrequency(p_277347_), blockpos, GameEvent.Context.of(p_279315_, blockstate));
            float f = RESONANCE_PITCH_BEND[p_277347_];
            p_277804_.playSound((Player)null, blockpos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.0F, f);
         }
      }

   }

   public void animateTick(BlockState p_222148_, Level p_222149_, BlockPos p_222150_, RandomSource p_222151_) {
      if (getPhase(p_222148_) == SculkSensorPhase.ACTIVE) {
         Direction direction = Direction.getRandom(p_222151_);
         if (direction != Direction.UP && direction != Direction.DOWN) {
            double d0 = (double)p_222150_.getX() + 0.5D + (direction.getStepX() == 0 ? 0.5D - p_222151_.nextDouble() : (double)direction.getStepX() * 0.6D);
            double d1 = (double)p_222150_.getY() + 0.25D;
            double d2 = (double)p_222150_.getZ() + 0.5D + (direction.getStepZ() == 0 ? 0.5D - p_222151_.nextDouble() : (double)direction.getStepZ() * 0.6D);
            double d3 = (double)p_222151_.nextFloat() * 0.04D;
            p_222149_.addParticle(DustColorTransitionOptions.SCULK_TO_REDSTONE, d0, d1, d2, 0.0D, d3, 0.0D);
         }
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_154464_) {
      p_154464_.add(PHASE, POWER, WATERLOGGED);
   }

   public boolean hasAnalogOutputSignal(BlockState p_154481_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_154442_, Level p_154443_, BlockPos p_154444_) {
      BlockEntity blockentity = p_154443_.getBlockEntity(p_154444_);
      if (blockentity instanceof SculkSensorBlockEntity sculksensorblockentity) {
         return getPhase(p_154442_) == SculkSensorPhase.ACTIVE ? sculksensorblockentity.getLastVibrationFrequency() : 0;
      } else {
         return 0;
      }
   }

   public boolean isPathfindable(BlockState p_154427_, BlockGetter p_154428_, BlockPos p_154429_, PathComputationType p_154430_) {
      return false;
   }

   public boolean useShapeForLightOcclusion(BlockState p_154486_) {
      return true;
   }

   public void spawnAfterBreak(BlockState p_222142_, ServerLevel p_222143_, BlockPos p_222144_, ItemStack p_222145_, boolean p_222146_) {
      super.spawnAfterBreak(p_222142_, p_222143_, p_222144_, p_222145_, p_222146_);

   }

   @Override
   public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
      return silkTouchLevel == 0 ? 5 : 0;
   }
}
