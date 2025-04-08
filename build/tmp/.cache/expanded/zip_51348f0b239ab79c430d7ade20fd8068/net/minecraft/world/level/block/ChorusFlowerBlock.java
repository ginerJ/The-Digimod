package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ChorusFlowerBlock extends Block {
   public static final int DEAD_AGE = 5;
   public static final IntegerProperty AGE = BlockStateProperties.AGE_5;
   private final ChorusPlantBlock plant;

   public ChorusFlowerBlock(ChorusPlantBlock p_51651_, BlockBehaviour.Properties p_51652_) {
      super(p_51652_);
      this.plant = p_51651_;
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   public void tick(BlockState p_220975_, ServerLevel p_220976_, BlockPos p_220977_, RandomSource p_220978_) {
      if (!p_220975_.canSurvive(p_220976_, p_220977_)) {
         p_220976_.destroyBlock(p_220977_, true);
      }

   }

   public boolean isRandomlyTicking(BlockState p_51696_) {
      return p_51696_.getValue(AGE) < 5;
   }

   public void randomTick(BlockState p_220980_, ServerLevel p_220981_, BlockPos p_220982_, RandomSource p_220983_) {
      BlockPos blockpos = p_220982_.above();
      if (p_220981_.isEmptyBlock(blockpos) && blockpos.getY() < p_220981_.getMaxBuildHeight()) {
         int i = p_220980_.getValue(AGE);
         if (i < 5 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_220981_, blockpos, p_220980_, true)) {
            boolean flag = false;
            boolean flag1 = false;
            BlockState blockstate = p_220981_.getBlockState(p_220982_.below());
            if (blockstate.is(Blocks.END_STONE)) {
               flag = true;
            } else if (blockstate.is(this.plant)) {
               int j = 1;

               for(int k = 0; k < 4; ++k) {
                  BlockState blockstate1 = p_220981_.getBlockState(p_220982_.below(j + 1));
                  if (!blockstate1.is(this.plant)) {
                     if (blockstate1.is(Blocks.END_STONE)) {
                        flag1 = true;
                     }
                     break;
                  }

                  ++j;
               }

               if (j < 2 || j <= p_220983_.nextInt(flag1 ? 5 : 4)) {
                  flag = true;
               }
            } else if (blockstate.isAir()) {
               flag = true;
            }

            if (flag && allNeighborsEmpty(p_220981_, blockpos, (Direction)null) && p_220981_.isEmptyBlock(p_220982_.above(2))) {
               p_220981_.setBlock(p_220982_, this.plant.getStateForPlacement(p_220981_, p_220982_), 2);
               this.placeGrownFlower(p_220981_, blockpos, i);
            } else if (i < 4) {
               int l = p_220983_.nextInt(4);
               if (flag1) {
                  ++l;
               }

               boolean flag2 = false;

               for(int i1 = 0; i1 < l; ++i1) {
                  Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(p_220983_);
                  BlockPos blockpos1 = p_220982_.relative(direction);
                  if (p_220981_.isEmptyBlock(blockpos1) && p_220981_.isEmptyBlock(blockpos1.below()) && allNeighborsEmpty(p_220981_, blockpos1, direction.getOpposite())) {
                     this.placeGrownFlower(p_220981_, blockpos1, i + 1);
                     flag2 = true;
                  }
               }

               if (flag2) {
                  p_220981_.setBlock(p_220982_, this.plant.getStateForPlacement(p_220981_, p_220982_), 2);
               } else {
                  this.placeDeadFlower(p_220981_, p_220982_);
               }
            } else {
               this.placeDeadFlower(p_220981_, p_220982_);
            }
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_220981_, p_220982_, p_220980_);
         }
      }
   }

   private void placeGrownFlower(Level p_51662_, BlockPos p_51663_, int p_51664_) {
      p_51662_.setBlock(p_51663_, this.defaultBlockState().setValue(AGE, Integer.valueOf(p_51664_)), 2);
      p_51662_.levelEvent(1033, p_51663_, 0);
   }

   private void placeDeadFlower(Level p_51659_, BlockPos p_51660_) {
      p_51659_.setBlock(p_51660_, this.defaultBlockState().setValue(AGE, Integer.valueOf(5)), 2);
      p_51659_.levelEvent(1034, p_51660_, 0);
   }

   private static boolean allNeighborsEmpty(LevelReader p_51698_, BlockPos p_51699_, @Nullable Direction p_51700_) {
      for(Direction direction : Direction.Plane.HORIZONTAL) {
         if (direction != p_51700_ && !p_51698_.isEmptyBlock(p_51699_.relative(direction))) {
            return false;
         }
      }

      return true;
   }

   public BlockState updateShape(BlockState p_51687_, Direction p_51688_, BlockState p_51689_, LevelAccessor p_51690_, BlockPos p_51691_, BlockPos p_51692_) {
      if (p_51688_ != Direction.UP && !p_51687_.canSurvive(p_51690_, p_51691_)) {
         p_51690_.scheduleTick(p_51691_, this, 1);
      }

      return super.updateShape(p_51687_, p_51688_, p_51689_, p_51690_, p_51691_, p_51692_);
   }

   public boolean canSurvive(BlockState p_51683_, LevelReader p_51684_, BlockPos p_51685_) {
      BlockState blockstate = p_51684_.getBlockState(p_51685_.below());
      if (!blockstate.is(this.plant) && !blockstate.is(Blocks.END_STONE)) {
         if (!blockstate.isAir()) {
            return false;
         } else {
            boolean flag = false;

            for(Direction direction : Direction.Plane.HORIZONTAL) {
               BlockState blockstate1 = p_51684_.getBlockState(p_51685_.relative(direction));
               if (blockstate1.is(this.plant)) {
                  if (flag) {
                     return false;
                  }

                  flag = true;
               } else if (!blockstate1.isAir()) {
                  return false;
               }
            }

            return flag;
         }
      } else {
         return true;
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51694_) {
      p_51694_.add(AGE);
   }

   public static void generatePlant(LevelAccessor p_220963_, BlockPos p_220964_, RandomSource p_220965_, int p_220966_) {
      p_220963_.setBlock(p_220964_, ((ChorusPlantBlock)Blocks.CHORUS_PLANT).getStateForPlacement(p_220963_, p_220964_), 2);
      growTreeRecursive(p_220963_, p_220964_, p_220965_, p_220964_, p_220966_, 0);
   }

   private static void growTreeRecursive(LevelAccessor p_220968_, BlockPos p_220969_, RandomSource p_220970_, BlockPos p_220971_, int p_220972_, int p_220973_) {
      ChorusPlantBlock chorusplantblock = (ChorusPlantBlock)Blocks.CHORUS_PLANT;
      int i = p_220970_.nextInt(4) + 1;
      if (p_220973_ == 0) {
         ++i;
      }

      for(int j = 0; j < i; ++j) {
         BlockPos blockpos = p_220969_.above(j + 1);
         if (!allNeighborsEmpty(p_220968_, blockpos, (Direction)null)) {
            return;
         }

         p_220968_.setBlock(blockpos, chorusplantblock.getStateForPlacement(p_220968_, blockpos), 2);
         p_220968_.setBlock(blockpos.below(), chorusplantblock.getStateForPlacement(p_220968_, blockpos.below()), 2);
      }

      boolean flag = false;
      if (p_220973_ < 4) {
         int l = p_220970_.nextInt(4);
         if (p_220973_ == 0) {
            ++l;
         }

         for(int k = 0; k < l; ++k) {
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(p_220970_);
            BlockPos blockpos1 = p_220969_.above(i).relative(direction);
            if (Math.abs(blockpos1.getX() - p_220971_.getX()) < p_220972_ && Math.abs(blockpos1.getZ() - p_220971_.getZ()) < p_220972_ && p_220968_.isEmptyBlock(blockpos1) && p_220968_.isEmptyBlock(blockpos1.below()) && allNeighborsEmpty(p_220968_, blockpos1, direction.getOpposite())) {
               flag = true;
               p_220968_.setBlock(blockpos1, chorusplantblock.getStateForPlacement(p_220968_, blockpos1), 2);
               p_220968_.setBlock(blockpos1.relative(direction.getOpposite()), chorusplantblock.getStateForPlacement(p_220968_, blockpos1.relative(direction.getOpposite())), 2);
               growTreeRecursive(p_220968_, blockpos1, p_220970_, p_220971_, p_220972_, p_220973_ + 1);
            }
         }
      }

      if (!flag) {
         p_220968_.setBlock(p_220969_.above(i), Blocks.CHORUS_FLOWER.defaultBlockState().setValue(AGE, Integer.valueOf(5)), 2);
      }

   }

   public void onProjectileHit(Level p_51654_, BlockState p_51655_, BlockHitResult p_51656_, Projectile p_51657_) {
      BlockPos blockpos = p_51656_.getBlockPos();
      if (!p_51654_.isClientSide && p_51657_.mayInteract(p_51654_, blockpos) && p_51657_.getType().is(EntityTypeTags.IMPACT_PROJECTILES)) {
         p_51654_.destroyBlock(blockpos, true, p_51657_);
      }

   }
}
