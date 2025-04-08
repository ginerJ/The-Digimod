package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PitcherCropBlock extends DoublePlantBlock implements BonemealableBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_4;
   public static final int MAX_AGE = 4;
   private static final int DOUBLE_PLANT_AGE_INTERSECTION = 3;
   private static final int BONEMEAL_INCREASE = 1;
   private static final VoxelShape FULL_UPPER_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 15.0D, 13.0D);
   private static final VoxelShape FULL_LOWER_SHAPE = Block.box(3.0D, -1.0D, 3.0D, 13.0D, 16.0D, 13.0D);
   private static final VoxelShape COLLISION_SHAPE_BULB = Block.box(5.0D, -1.0D, 5.0D, 11.0D, 3.0D, 11.0D);
   private static final VoxelShape COLLISION_SHAPE_CROP = Block.box(3.0D, -1.0D, 3.0D, 13.0D, 5.0D, 13.0D);
   private static final VoxelShape[] UPPER_SHAPE_BY_AGE = new VoxelShape[]{Block.box(3.0D, 0.0D, 3.0D, 13.0D, 11.0D, 13.0D), FULL_UPPER_SHAPE};
   private static final VoxelShape[] LOWER_SHAPE_BY_AGE = new VoxelShape[]{COLLISION_SHAPE_BULB, Block.box(3.0D, -1.0D, 3.0D, 13.0D, 14.0D, 13.0D), FULL_LOWER_SHAPE, FULL_LOWER_SHAPE, FULL_LOWER_SHAPE};

   public PitcherCropBlock(BlockBehaviour.Properties p_277780_) {
      super(p_277780_);
   }

   private boolean isMaxAge(BlockState p_277387_) {
      return p_277387_.getValue(AGE) >= 4;
   }

   public boolean isRandomlyTicking(BlockState p_277483_) {
      return p_277483_.getValue(HALF) == DoubleBlockHalf.LOWER && !this.isMaxAge(p_277483_);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_277448_) {
      return this.defaultBlockState();
   }

   public BlockState updateShape(BlockState p_277518_, Direction p_277700_, BlockState p_277660_, LevelAccessor p_277653_, BlockPos p_277982_, BlockPos p_278106_) {
      return !p_277518_.canSurvive(p_277653_, p_277982_) ? Blocks.AIR.defaultBlockState() : p_277518_;
   }

   public VoxelShape getCollisionShape(BlockState p_277609_, BlockGetter p_277398_, BlockPos p_278042_, CollisionContext p_277995_) {
      if (p_277609_.getValue(AGE) == 0) {
         return COLLISION_SHAPE_BULB;
      } else {
         return p_277609_.getValue(HALF) == DoubleBlockHalf.LOWER ? COLLISION_SHAPE_CROP : super.getCollisionShape(p_277609_, p_277398_, p_278042_, p_277995_);
      }
   }

   public boolean canSurvive(BlockState p_277671_, LevelReader p_277477_, BlockPos p_278085_) {
      if (!isLower(p_277671_)) {
         return super.canSurvive(p_277671_, p_277477_, p_278085_);
      } else {
         BlockPos below = p_278085_.below();
         boolean isSoil = this.mayPlaceOn(p_277477_.getBlockState(below), p_277477_, below);
         if (p_277671_.getBlock() == this) //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            isSoil = p_277477_.getBlockState(below).canSustainPlant(p_277477_, below, Direction.UP, this);
         return isSoil && sufficientLight(p_277477_, p_278085_) && (p_277671_.getValue(AGE) < 3 || isUpper(p_277477_.getBlockState(p_278085_.above())));
      }
   }

   protected boolean mayPlaceOn(BlockState p_277418_, BlockGetter p_277461_, BlockPos p_277608_) {
      return p_277418_.is(Blocks.FARMLAND);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_277573_) {
      p_277573_.add(AGE);
      super.createBlockStateDefinition(p_277573_);
   }

   public VoxelShape getShape(BlockState p_277602_, BlockGetter p_277617_, BlockPos p_278005_, CollisionContext p_277514_) {
      return p_277602_.getValue(HALF) == DoubleBlockHalf.UPPER ? UPPER_SHAPE_BY_AGE[Math.min(Math.abs(4 - (p_277602_.getValue(AGE) + 1)), UPPER_SHAPE_BY_AGE.length - 1)] : LOWER_SHAPE_BY_AGE[p_277602_.getValue(AGE)];
   }

   public void entityInside(BlockState p_279266_, Level p_279469_, BlockPos p_279119_, Entity p_279372_) {
      if (p_279372_ instanceof Ravager && p_279469_.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         p_279469_.destroyBlock(p_279119_, true, p_279372_);
      }

      super.entityInside(p_279266_, p_279469_, p_279119_, p_279372_);
   }

   public boolean canBeReplaced(BlockState p_277627_, BlockPlaceContext p_277759_) {
      return false;
   }

   public void setPlacedBy(Level p_277432_, BlockPos p_277632_, BlockState p_277479_, LivingEntity p_277805_, ItemStack p_277663_) {
   }

   public void randomTick(BlockState p_277950_, ServerLevel p_277589_, BlockPos p_277937_, RandomSource p_277887_) {
      float f = CropBlock.getGrowthSpeed(this, p_277589_, p_277937_);
      boolean flag = p_277887_.nextInt((int)(25.0F / f) + 1) == 0;
      if (flag) {
         this.grow(p_277589_, p_277950_, p_277937_, 1);
      }

   }

   private void grow(ServerLevel p_277975_, BlockState p_277349_, BlockPos p_277585_, int p_277498_) {
      int i = Math.min(p_277349_.getValue(AGE) + p_277498_, 4);
      if (this.canGrow(p_277975_, p_277585_, p_277349_, i)) {
         p_277975_.setBlock(p_277585_, p_277349_.setValue(AGE, Integer.valueOf(i)), 2);
         if (i >= 3) {
            BlockPos blockpos = p_277585_.above();
            p_277975_.setBlock(blockpos, copyWaterloggedFrom(p_277975_, p_277585_, this.defaultBlockState().setValue(AGE, Integer.valueOf(i)).setValue(HALF, DoubleBlockHalf.UPPER)), 3);
         }

      }
   }

   private static boolean canGrowInto(LevelReader p_290010_, BlockPos p_277823_) {
      BlockState blockstate = p_290010_.getBlockState(p_277823_);
      return blockstate.isAir() || blockstate.is(Blocks.PITCHER_CROP);
   }

   private static boolean sufficientLight(LevelReader p_290018_, BlockPos p_290011_) {
      return p_290018_.getRawBrightness(p_290011_, 0) >= 8 || p_290018_.canSeeSky(p_290011_);
   }

   private static boolean isLower(BlockState p_279488_) {
      return p_279488_.is(Blocks.PITCHER_CROP) && p_279488_.getValue(HALF) == DoubleBlockHalf.LOWER;
   }

   private static boolean isUpper(BlockState p_290013_) {
      return p_290013_.is(Blocks.PITCHER_CROP) && p_290013_.getValue(HALF) == DoubleBlockHalf.UPPER;
   }

   private boolean canGrow(LevelReader p_290007_, BlockPos p_290014_, BlockState p_290017_, int p_290008_) {
      return !this.isMaxAge(p_290017_) && sufficientLight(p_290007_, p_290014_) && (p_290008_ < 3 || canGrowInto(p_290007_, p_290014_.above()));
   }

   @Nullable
   private PitcherCropBlock.PosAndState getLowerHalf(LevelReader p_290009_, BlockPos p_290016_, BlockState p_290015_) {
      if (isLower(p_290015_)) {
         return new PitcherCropBlock.PosAndState(p_290016_, p_290015_);
      } else {
         BlockPos blockpos = p_290016_.below();
         BlockState blockstate = p_290009_.getBlockState(blockpos);
         return isLower(blockstate) ? new PitcherCropBlock.PosAndState(blockpos, blockstate) : null;
      }
   }

   public boolean isValidBonemealTarget(LevelReader p_277380_, BlockPos p_277500_, BlockState p_277715_, boolean p_277487_) {
      PitcherCropBlock.PosAndState pitchercropblock$posandstate = this.getLowerHalf(p_277380_, p_277500_, p_277715_);
      return pitchercropblock$posandstate == null ? false : this.canGrow(p_277380_, pitchercropblock$posandstate.pos, pitchercropblock$posandstate.state, pitchercropblock$posandstate.state.getValue(AGE) + 1);
   }

   public boolean isBonemealSuccess(Level p_277920_, RandomSource p_277594_, BlockPos p_277401_, BlockState p_277434_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_277717_, RandomSource p_277870_, BlockPos p_277836_, BlockState p_278034_) {
      PitcherCropBlock.PosAndState pitchercropblock$posandstate = this.getLowerHalf(p_277717_, p_277836_, p_278034_);
      if (pitchercropblock$posandstate != null) {
         this.grow(p_277717_, pitchercropblock$posandstate.state, pitchercropblock$posandstate.pos, 1);
      }
   }

   static record PosAndState(BlockPos pos, BlockState state) {
   }
}
