package net.minecraft.world.level.block;

import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class SculkVeinBlock extends MultifaceBlock implements SculkBehaviour, SimpleWaterloggedBlock {
   private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private final MultifaceSpreader veinSpreader = new MultifaceSpreader(new SculkVeinBlock.SculkVeinSpreaderConfig(MultifaceSpreader.DEFAULT_SPREAD_ORDER));
   private final MultifaceSpreader sameSpaceSpreader = new MultifaceSpreader(new SculkVeinBlock.SculkVeinSpreaderConfig(MultifaceSpreader.SpreadType.SAME_POSITION));

   public SculkVeinBlock(BlockBehaviour.Properties p_222353_) {
      super(p_222353_);
      this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public MultifaceSpreader getSpreader() {
      return this.veinSpreader;
   }

   public MultifaceSpreader getSameSpaceSpreader() {
      return this.sameSpaceSpreader;
   }

   public static boolean regrow(LevelAccessor p_222364_, BlockPos p_222365_, BlockState p_222366_, Collection<Direction> p_222367_) {
      boolean flag = false;
      BlockState blockstate = Blocks.SCULK_VEIN.defaultBlockState();

      for(Direction direction : p_222367_) {
         BlockPos blockpos = p_222365_.relative(direction);
         if (canAttachTo(p_222364_, direction, blockpos, p_222364_.getBlockState(blockpos))) {
            blockstate = blockstate.setValue(getFaceProperty(direction), Boolean.valueOf(true));
            flag = true;
         }
      }

      if (!flag) {
         return false;
      } else {
         if (!p_222366_.getFluidState().isEmpty()) {
            blockstate = blockstate.setValue(WATERLOGGED, Boolean.valueOf(true));
         }

         p_222364_.setBlock(p_222365_, blockstate, 3);
         return true;
      }
   }

   public void onDischarged(LevelAccessor p_222359_, BlockState p_222360_, BlockPos p_222361_, RandomSource p_222362_) {
      if (p_222360_.is(this)) {
         for(Direction direction : DIRECTIONS) {
            BooleanProperty booleanproperty = getFaceProperty(direction);
            if (p_222360_.getValue(booleanproperty) && p_222359_.getBlockState(p_222361_.relative(direction)).is(Blocks.SCULK)) {
               p_222360_ = p_222360_.setValue(booleanproperty, Boolean.valueOf(false));
            }
         }

         if (!hasAnyFace(p_222360_)) {
            FluidState fluidstate = p_222359_.getFluidState(p_222361_);
            p_222360_ = (fluidstate.isEmpty() ? Blocks.AIR : Blocks.WATER).defaultBlockState();
         }

         p_222359_.setBlock(p_222361_, p_222360_, 3);
         SculkBehaviour.super.onDischarged(p_222359_, p_222360_, p_222361_, p_222362_);
      }
   }

   public int attemptUseCharge(SculkSpreader.ChargeCursor p_222369_, LevelAccessor p_222370_, BlockPos p_222371_, RandomSource p_222372_, SculkSpreader p_222373_, boolean p_222374_) {
      if (p_222374_ && this.attemptPlaceSculk(p_222373_, p_222370_, p_222369_.getPos(), p_222372_)) {
         return p_222369_.getCharge() - 1;
      } else {
         return p_222372_.nextInt(p_222373_.chargeDecayRate()) == 0 ? Mth.floor((float)p_222369_.getCharge() * 0.5F) : p_222369_.getCharge();
      }
   }

   private boolean attemptPlaceSculk(SculkSpreader p_222376_, LevelAccessor p_222377_, BlockPos p_222378_, RandomSource p_222379_) {
      BlockState blockstate = p_222377_.getBlockState(p_222378_);
      TagKey<Block> tagkey = p_222376_.replaceableBlocks();

      for(Direction direction : Direction.allShuffled(p_222379_)) {
         if (hasFace(blockstate, direction)) {
            BlockPos blockpos = p_222378_.relative(direction);
            BlockState blockstate1 = p_222377_.getBlockState(blockpos);
            if (blockstate1.is(tagkey)) {
               BlockState blockstate2 = Blocks.SCULK.defaultBlockState();
               p_222377_.setBlock(blockpos, blockstate2, 3);
               Block.pushEntitiesUp(blockstate1, blockstate2, p_222377_, blockpos);
               p_222377_.playSound((Player)null, blockpos, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0F, 1.0F);
               this.veinSpreader.spreadAll(blockstate2, p_222377_, blockpos, p_222376_.isWorldGeneration());
               Direction direction1 = direction.getOpposite();

               for(Direction direction2 : DIRECTIONS) {
                  if (direction2 != direction1) {
                     BlockPos blockpos1 = blockpos.relative(direction2);
                     BlockState blockstate3 = p_222377_.getBlockState(blockpos1);
                     if (blockstate3.is(this)) {
                        this.onDischarged(p_222377_, blockstate3, blockpos1, p_222379_);
                     }
                  }
               }

               return true;
            }
         }
      }

      return false;
   }

   public static boolean hasSubstrateAccess(LevelAccessor p_222355_, BlockState p_222356_, BlockPos p_222357_) {
      if (!p_222356_.is(Blocks.SCULK_VEIN)) {
         return false;
      } else {
         for(Direction direction : DIRECTIONS) {
            if (hasFace(p_222356_, direction) && p_222355_.getBlockState(p_222357_.relative(direction)).is(BlockTags.SCULK_REPLACEABLE)) {
               return true;
            }
         }

         return false;
      }
   }

   public BlockState updateShape(BlockState p_222384_, Direction p_222385_, BlockState p_222386_, LevelAccessor p_222387_, BlockPos p_222388_, BlockPos p_222389_) {
      if (p_222384_.getValue(WATERLOGGED)) {
         p_222387_.scheduleTick(p_222388_, Fluids.WATER, Fluids.WATER.getTickDelay(p_222387_));
      }

      return super.updateShape(p_222384_, p_222385_, p_222386_, p_222387_, p_222388_, p_222389_);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_222391_) {
      super.createBlockStateDefinition(p_222391_);
      p_222391_.add(WATERLOGGED);
   }

   public boolean canBeReplaced(BlockState p_222381_, BlockPlaceContext p_222382_) {
      return !p_222382_.getItemInHand().is(Items.SCULK_VEIN) || super.canBeReplaced(p_222381_, p_222382_);
   }

   public FluidState getFluidState(BlockState p_222394_) {
      return p_222394_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_222394_);
   }

   class SculkVeinSpreaderConfig extends MultifaceSpreader.DefaultSpreaderConfig {
      private final MultifaceSpreader.SpreadType[] spreadTypes;

      public SculkVeinSpreaderConfig(MultifaceSpreader.SpreadType... p_222402_) {
         super(SculkVeinBlock.this);
         this.spreadTypes = p_222402_;
      }

      public boolean stateCanBeReplaced(BlockGetter p_222405_, BlockPos p_222406_, BlockPos p_222407_, Direction p_222408_, BlockState p_222409_) {
         BlockState blockstate = p_222405_.getBlockState(p_222407_.relative(p_222408_));
         if (!blockstate.is(Blocks.SCULK) && !blockstate.is(Blocks.SCULK_CATALYST) && !blockstate.is(Blocks.MOVING_PISTON)) {
            if (p_222406_.distManhattan(p_222407_) == 2) {
               BlockPos blockpos = p_222406_.relative(p_222408_.getOpposite());
               if (p_222405_.getBlockState(blockpos).isFaceSturdy(p_222405_, blockpos, p_222408_)) {
                  return false;
               }
            }

            FluidState fluidstate = p_222409_.getFluidState();
            if (!fluidstate.isEmpty() && !fluidstate.is(Fluids.WATER)) {
               return false;
            } else if (p_222409_.is(BlockTags.FIRE)) {
               return false;
            } else {
               return p_222409_.canBeReplaced() || super.stateCanBeReplaced(p_222405_, p_222406_, p_222407_, p_222408_, p_222409_);
            }
         } else {
            return false;
         }
      }

      public MultifaceSpreader.SpreadType[] getSpreadTypes() {
         return this.spreadTypes;
      }

      public boolean isOtherBlockValidAsSource(BlockState p_222411_) {
         return !p_222411_.is(Blocks.SCULK_VEIN);
      }
   }
}