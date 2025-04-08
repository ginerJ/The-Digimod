package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SculkShriekerBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty SHRIEKING = BlockStateProperties.SHRIEKING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final BooleanProperty CAN_SUMMON = BlockStateProperties.CAN_SUMMON;
   protected static final VoxelShape COLLIDER = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   public static final double TOP_Y = COLLIDER.max(Direction.Axis.Y);

   public SculkShriekerBlock(BlockBehaviour.Properties p_222159_) {
      super(p_222159_);
      this.registerDefaultState(this.stateDefinition.any().setValue(SHRIEKING, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(CAN_SUMMON, Boolean.valueOf(false)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_222211_) {
      p_222211_.add(SHRIEKING);
      p_222211_.add(WATERLOGGED);
      p_222211_.add(CAN_SUMMON);
   }

   public void stepOn(Level p_222177_, BlockPos p_222178_, BlockState p_222179_, Entity p_222180_) {
      if (p_222177_ instanceof ServerLevel serverlevel) {
         ServerPlayer serverplayer = SculkShriekerBlockEntity.tryGetPlayer(p_222180_);
         if (serverplayer != null) {
            serverlevel.getBlockEntity(p_222178_, BlockEntityType.SCULK_SHRIEKER).ifPresent((p_222163_) -> {
               p_222163_.tryShriek(serverlevel, serverplayer);
            });
         }
      }

      super.stepOn(p_222177_, p_222178_, p_222179_, p_222180_);
   }

   public void onRemove(BlockState p_222198_, Level p_222199_, BlockPos p_222200_, BlockState p_222201_, boolean p_222202_) {
      if (p_222199_ instanceof ServerLevel serverlevel) {
         if (p_222198_.getValue(SHRIEKING) && !p_222198_.is(p_222201_.getBlock())) {
            serverlevel.getBlockEntity(p_222200_, BlockEntityType.SCULK_SHRIEKER).ifPresent((p_222217_) -> {
               p_222217_.tryRespond(serverlevel);
            });
         }
      }

      super.onRemove(p_222198_, p_222199_, p_222200_, p_222201_, p_222202_);
   }

   public void tick(BlockState p_222187_, ServerLevel p_222188_, BlockPos p_222189_, RandomSource p_222190_) {
      if (p_222187_.getValue(SHRIEKING)) {
         p_222188_.setBlock(p_222189_, p_222187_.setValue(SHRIEKING, Boolean.valueOf(false)), 3);
         p_222188_.getBlockEntity(p_222189_, BlockEntityType.SCULK_SHRIEKER).ifPresent((p_222169_) -> {
            p_222169_.tryRespond(p_222188_);
         });
      }

   }

   public RenderShape getRenderShape(BlockState p_222219_) {
      return RenderShape.MODEL;
   }

   public VoxelShape getCollisionShape(BlockState p_222225_, BlockGetter p_222226_, BlockPos p_222227_, CollisionContext p_222228_) {
      return COLLIDER;
   }

   public VoxelShape getOcclusionShape(BlockState p_222221_, BlockGetter p_222222_, BlockPos p_222223_) {
      return COLLIDER;
   }

   public boolean useShapeForLightOcclusion(BlockState p_222232_) {
      return true;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos p_222213_, BlockState p_222214_) {
      return new SculkShriekerBlockEntity(p_222213_, p_222214_);
   }

   public BlockState updateShape(BlockState p_222204_, Direction p_222205_, BlockState p_222206_, LevelAccessor p_222207_, BlockPos p_222208_, BlockPos p_222209_) {
      if (p_222204_.getValue(WATERLOGGED)) {
         p_222207_.scheduleTick(p_222208_, Fluids.WATER, Fluids.WATER.getTickDelay(p_222207_));
      }

      return super.updateShape(p_222204_, p_222205_, p_222206_, p_222207_, p_222208_, p_222209_);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_222171_) {
      return this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(p_222171_.getLevel().getFluidState(p_222171_.getClickedPos()).getType() == Fluids.WATER));
   }

   public FluidState getFluidState(BlockState p_222230_) {
      return p_222230_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_222230_);
   }

   public void spawnAfterBreak(BlockState p_222192_, ServerLevel p_222193_, BlockPos p_222194_, ItemStack p_222195_, boolean p_222196_) {
      super.spawnAfterBreak(p_222192_, p_222193_, p_222194_, p_222195_, p_222196_);

   }

   @Override
   public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
      return silkTouchLevel == 0 ? 5 : 0;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_222173_, BlockState p_222174_, BlockEntityType<T> p_222175_) {
      return !p_222173_.isClientSide ? BaseEntityBlock.createTickerHelper(p_222175_, BlockEntityType.SCULK_SHRIEKER, (p_281134_, p_281135_, p_281136_, p_281137_) -> {
         VibrationSystem.Ticker.tick(p_281134_, p_281137_.getVibrationData(), p_281137_.getVibrationUser());
      }) : null;
   }
}
