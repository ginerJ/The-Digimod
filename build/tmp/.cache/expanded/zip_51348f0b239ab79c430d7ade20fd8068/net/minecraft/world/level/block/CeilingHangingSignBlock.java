package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CeilingHangingSignBlock extends SignBlock {
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
   public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
   protected static final float AABB_OFFSET = 5.0F;
   protected static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
   private static final Map<Integer, VoxelShape> AABBS = Maps.newHashMap(ImmutableMap.of(0, Block.box(1.0D, 0.0D, 7.0D, 15.0D, 10.0D, 9.0D), 4, Block.box(7.0D, 0.0D, 1.0D, 9.0D, 10.0D, 15.0D), 8, Block.box(1.0D, 0.0D, 7.0D, 15.0D, 10.0D, 9.0D), 12, Block.box(7.0D, 0.0D, 1.0D, 9.0D, 10.0D, 15.0D)));

   public CeilingHangingSignBlock(BlockBehaviour.Properties p_250481_, WoodType p_248716_) {
      super(p_250481_.sound(p_248716_.hangingSignSoundType()), p_248716_);
      this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, Integer.valueOf(0)).setValue(ATTACHED, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public InteractionResult use(BlockState p_251161_, Level p_249327_, BlockPos p_248552_, Player p_248644_, InteractionHand p_251941_, BlockHitResult p_252016_) {
      BlockEntity $$7 = p_249327_.getBlockEntity(p_248552_);
      if ($$7 instanceof SignBlockEntity signblockentity) {
         ItemStack itemstack = p_248644_.getItemInHand(p_251941_);
         if (this.shouldTryToChainAnotherHangingSign(p_248644_, p_252016_, signblockentity, itemstack)) {
            return InteractionResult.PASS;
         }
      }

      return super.use(p_251161_, p_249327_, p_248552_, p_248644_, p_251941_, p_252016_);
   }

   private boolean shouldTryToChainAnotherHangingSign(Player p_278279_, BlockHitResult p_278273_, SignBlockEntity p_278236_, ItemStack p_278343_) {
      return !p_278236_.canExecuteClickCommands(p_278236_.isFacingFrontText(p_278279_), p_278279_) && p_278343_.getItem() instanceof HangingSignItem && p_278273_.getDirection().equals(Direction.DOWN);
   }

   public boolean canSurvive(BlockState p_248994_, LevelReader p_249061_, BlockPos p_249490_) {
      return p_249061_.getBlockState(p_249490_.above()).isFaceSturdy(p_249061_, p_249490_.above(), Direction.DOWN, SupportType.CENTER);
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_252121_) {
      Level level = p_252121_.getLevel();
      FluidState fluidstate = level.getFluidState(p_252121_.getClickedPos());
      BlockPos blockpos = p_252121_.getClickedPos().above();
      BlockState blockstate = level.getBlockState(blockpos);
      boolean flag = blockstate.is(BlockTags.ALL_HANGING_SIGNS);
      Direction direction = Direction.fromYRot((double)p_252121_.getRotation());
      boolean flag1 = !Block.isFaceFull(blockstate.getCollisionShape(level, blockpos), Direction.DOWN) || p_252121_.isSecondaryUseActive();
      if (flag && !p_252121_.isSecondaryUseActive()) {
         if (blockstate.hasProperty(WallHangingSignBlock.FACING)) {
            Direction direction1 = blockstate.getValue(WallHangingSignBlock.FACING);
            if (direction1.getAxis().test(direction)) {
               flag1 = false;
            }
         } else if (blockstate.hasProperty(ROTATION)) {
            Optional<Direction> optional = RotationSegment.convertToDirection(blockstate.getValue(ROTATION));
            if (optional.isPresent() && optional.get().getAxis().test(direction)) {
               flag1 = false;
            }
         }
      }

      int i = !flag1 ? RotationSegment.convertToSegment(direction.getOpposite()) : RotationSegment.convertToSegment(p_252121_.getRotation() + 180.0F);
      return this.defaultBlockState().setValue(ATTACHED, Boolean.valueOf(flag1)).setValue(ROTATION, Integer.valueOf(i)).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
   }

   public VoxelShape getShape(BlockState p_250564_, BlockGetter p_248998_, BlockPos p_249501_, CollisionContext p_248978_) {
      VoxelShape voxelshape = AABBS.get(p_250564_.getValue(ROTATION));
      return voxelshape == null ? SHAPE : voxelshape;
   }

   public VoxelShape getBlockSupportShape(BlockState p_254482_, BlockGetter p_253669_, BlockPos p_253916_) {
      return this.getShape(p_254482_, p_253669_, p_253916_, CollisionContext.empty());
   }

   public BlockState updateShape(BlockState p_251270_, Direction p_250331_, BlockState p_249591_, LevelAccessor p_251903_, BlockPos p_249685_, BlockPos p_251506_) {
      return p_250331_ == Direction.UP && !this.canSurvive(p_251270_, p_251903_, p_249685_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_251270_, p_250331_, p_249591_, p_251903_, p_249685_, p_251506_);
   }

   public float getYRotationDegrees(BlockState p_277758_) {
      return RotationSegment.convertToDegrees(p_277758_.getValue(ROTATION));
   }

   public BlockState rotate(BlockState p_251162_, Rotation p_250515_) {
      return p_251162_.setValue(ROTATION, Integer.valueOf(p_250515_.rotate(p_251162_.getValue(ROTATION), 16)));
   }

   public BlockState mirror(BlockState p_249682_, Mirror p_250199_) {
      return p_249682_.setValue(ROTATION, Integer.valueOf(p_250199_.mirror(p_249682_.getValue(ROTATION), 16)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_251174_) {
      p_251174_.add(ROTATION, ATTACHED, WATERLOGGED);
   }

   public BlockEntity newBlockEntity(BlockPos p_249338_, BlockState p_250706_) {
      return new HangingSignBlockEntity(p_249338_, p_250706_);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_279379_, BlockState p_279390_, BlockEntityType<T> p_279231_) {
      return createTickerHelper(p_279231_, BlockEntityType.HANGING_SIGN, SignBlockEntity::tick);
   }
}