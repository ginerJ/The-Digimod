package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class MultifaceBlock extends Block {
   private static final float AABB_OFFSET = 1.0F;
   private static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
   private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
   private static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
   private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
   private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
   private static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION = Util.make(Maps.newEnumMap(Direction.class), (p_153923_) -> {
      p_153923_.put(Direction.NORTH, NORTH_AABB);
      p_153923_.put(Direction.EAST, EAST_AABB);
      p_153923_.put(Direction.SOUTH, SOUTH_AABB);
      p_153923_.put(Direction.WEST, WEST_AABB);
      p_153923_.put(Direction.UP, UP_AABB);
      p_153923_.put(Direction.DOWN, DOWN_AABB);
   });
   protected static final Direction[] DIRECTIONS = Direction.values();
   private final ImmutableMap<BlockState, VoxelShape> shapesCache;
   private final boolean canRotate;
   private final boolean canMirrorX;
   private final boolean canMirrorZ;

   public MultifaceBlock(BlockBehaviour.Properties p_153822_) {
      super(p_153822_);
      this.registerDefaultState(getDefaultMultifaceState(this.stateDefinition));
      this.shapesCache = this.getShapeForEachState(MultifaceBlock::calculateMultifaceShape);
      this.canRotate = Direction.Plane.HORIZONTAL.stream().allMatch(this::isFaceSupported);
      this.canMirrorX = Direction.Plane.HORIZONTAL.stream().filter(Direction.Axis.X).filter(this::isFaceSupported).count() % 2L == 0L;
      this.canMirrorZ = Direction.Plane.HORIZONTAL.stream().filter(Direction.Axis.Z).filter(this::isFaceSupported).count() % 2L == 0L;
   }

   public static Set<Direction> availableFaces(BlockState p_221585_) {
      if (!(p_221585_.getBlock() instanceof MultifaceBlock)) {
         return Set.of();
      } else {
         Set<Direction> set = EnumSet.noneOf(Direction.class);

         for(Direction direction : Direction.values()) {
            if (hasFace(p_221585_, direction)) {
               set.add(direction);
            }
         }

         return set;
      }
   }

   public static Set<Direction> unpack(byte p_221570_) {
      Set<Direction> set = EnumSet.noneOf(Direction.class);

      for(Direction direction : Direction.values()) {
         if ((p_221570_ & (byte)(1 << direction.ordinal())) > 0) {
            set.add(direction);
         }
      }

      return set;
   }

   public static byte pack(Collection<Direction> p_221577_) {
      byte b0 = 0;

      for(Direction direction : p_221577_) {
         b0 = (byte)(b0 | 1 << direction.ordinal());
      }

      return b0;
   }

   protected boolean isFaceSupported(Direction p_153921_) {
      return true;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153917_) {
      for(Direction direction : DIRECTIONS) {
         if (this.isFaceSupported(direction)) {
            p_153917_.add(getFaceProperty(direction));
         }
      }

   }

   public BlockState updateShape(BlockState p_153904_, Direction p_153905_, BlockState p_153906_, LevelAccessor p_153907_, BlockPos p_153908_, BlockPos p_153909_) {
      if (!hasAnyFace(p_153904_)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         return hasFace(p_153904_, p_153905_) && !canAttachTo(p_153907_, p_153905_, p_153909_, p_153906_) ? removeFace(p_153904_, getFaceProperty(p_153905_)) : p_153904_;
      }
   }

   public VoxelShape getShape(BlockState p_153851_, BlockGetter p_153852_, BlockPos p_153853_, CollisionContext p_153854_) {
      return this.shapesCache.get(p_153851_);
   }

   public boolean canSurvive(BlockState p_153888_, LevelReader p_153889_, BlockPos p_153890_) {
      boolean flag = false;

      for(Direction direction : DIRECTIONS) {
         if (hasFace(p_153888_, direction)) {
            BlockPos blockpos = p_153890_.relative(direction);
            if (!canAttachTo(p_153889_, direction, blockpos, p_153889_.getBlockState(blockpos))) {
               return false;
            }

            flag = true;
         }
      }

      return flag;
   }

   public boolean canBeReplaced(BlockState p_153848_, BlockPlaceContext p_153849_) {
      return hasAnyVacantFace(p_153848_);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_153824_) {
      Level level = p_153824_.getLevel();
      BlockPos blockpos = p_153824_.getClickedPos();
      BlockState blockstate = level.getBlockState(blockpos);
      return Arrays.stream(p_153824_.getNearestLookingDirections()).map((p_153865_) -> {
         return this.getStateForPlacement(blockstate, level, blockpos, p_153865_);
      }).filter(Objects::nonNull).findFirst().orElse((BlockState)null);
   }

   public boolean isValidStateForPlacement(BlockGetter p_221572_, BlockState p_221573_, BlockPos p_221574_, Direction p_221575_) {
      if (this.isFaceSupported(p_221575_) && (!p_221573_.is(this) || !hasFace(p_221573_, p_221575_))) {
         BlockPos blockpos = p_221574_.relative(p_221575_);
         return canAttachTo(p_221572_, p_221575_, blockpos, p_221572_.getBlockState(blockpos));
      } else {
         return false;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockState p_153941_, BlockGetter p_153942_, BlockPos p_153943_, Direction p_153944_) {
      if (!this.isValidStateForPlacement(p_153942_, p_153941_, p_153943_, p_153944_)) {
         return null;
      } else {
         BlockState blockstate;
         if (p_153941_.is(this)) {
            blockstate = p_153941_;
         } else if (this.isWaterloggable() && p_153941_.getFluidState().isSourceOfType(Fluids.WATER)) {
            blockstate = this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true));
         } else {
            blockstate = this.defaultBlockState();
         }

         return blockstate.setValue(getFaceProperty(p_153944_), Boolean.valueOf(true));
      }
   }

   public BlockState rotate(BlockState p_153895_, Rotation p_153896_) {
      return !this.canRotate ? p_153895_ : this.mapDirections(p_153895_, p_153896_::rotate);
   }

   public BlockState mirror(BlockState p_153892_, Mirror p_153893_) {
      if (p_153893_ == Mirror.FRONT_BACK && !this.canMirrorX) {
         return p_153892_;
      } else {
         return p_153893_ == Mirror.LEFT_RIGHT && !this.canMirrorZ ? p_153892_ : this.mapDirections(p_153892_, p_153893_::mirror);
      }
   }

   private BlockState mapDirections(BlockState p_153911_, Function<Direction, Direction> p_153912_) {
      BlockState blockstate = p_153911_;

      for(Direction direction : DIRECTIONS) {
         if (this.isFaceSupported(direction)) {
            blockstate = blockstate.setValue(getFaceProperty(p_153912_.apply(direction)), p_153911_.getValue(getFaceProperty(direction)));
         }
      }

      return blockstate;
   }

   public static boolean hasFace(BlockState p_153901_, Direction p_153902_) {
      BooleanProperty booleanproperty = getFaceProperty(p_153902_);
      return p_153901_.hasProperty(booleanproperty) && p_153901_.getValue(booleanproperty);
   }

   public static boolean canAttachTo(BlockGetter p_153830_, Direction p_153831_, BlockPos p_153832_, BlockState p_153833_) {
      return Block.isFaceFull(p_153833_.getBlockSupportShape(p_153830_, p_153832_), p_153831_.getOpposite()) || Block.isFaceFull(p_153833_.getCollisionShape(p_153830_, p_153832_), p_153831_.getOpposite());
   }

   private boolean isWaterloggable() {
      return this.stateDefinition.getProperties().contains(BlockStateProperties.WATERLOGGED);
   }

   private static BlockState removeFace(BlockState p_153898_, BooleanProperty p_153899_) {
      BlockState blockstate = p_153898_.setValue(p_153899_, Boolean.valueOf(false));
      return hasAnyFace(blockstate) ? blockstate : Blocks.AIR.defaultBlockState();
   }

   public static BooleanProperty getFaceProperty(Direction p_153934_) {
      return PROPERTY_BY_DIRECTION.get(p_153934_);
   }

   private static BlockState getDefaultMultifaceState(StateDefinition<Block, BlockState> p_153919_) {
      BlockState blockstate = p_153919_.any();

      for(BooleanProperty booleanproperty : PROPERTY_BY_DIRECTION.values()) {
         if (blockstate.hasProperty(booleanproperty)) {
            blockstate = blockstate.setValue(booleanproperty, Boolean.valueOf(false));
         }
      }

      return blockstate;
   }

   private static VoxelShape calculateMultifaceShape(BlockState p_153959_) {
      VoxelShape voxelshape = Shapes.empty();

      for(Direction direction : DIRECTIONS) {
         if (hasFace(p_153959_, direction)) {
            voxelshape = Shapes.or(voxelshape, SHAPE_BY_DIRECTION.get(direction));
         }
      }

      return voxelshape.isEmpty() ? Shapes.block() : voxelshape;
   }

   protected static boolean hasAnyFace(BlockState p_153961_) {
      return Arrays.stream(DIRECTIONS).anyMatch((p_221583_) -> {
         return hasFace(p_153961_, p_221583_);
      });
   }

   private static boolean hasAnyVacantFace(BlockState p_153963_) {
      return Arrays.stream(DIRECTIONS).anyMatch((p_221580_) -> {
         return !hasFace(p_153963_, p_221580_);
      });
   }

   public abstract MultifaceSpreader getSpreader();
}