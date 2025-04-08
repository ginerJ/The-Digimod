package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FrogspawnBlock extends Block {
   private static final int MIN_TADPOLES_SPAWN = 2;
   private static final int MAX_TADPOLES_SPAWN = 5;
   private static final int DEFAULT_MIN_HATCH_TICK_DELAY = 3600;
   private static final int DEFAULT_MAX_HATCH_TICK_DELAY = 12000;
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.5D, 16.0D);
   private static int minHatchTickDelay = 3600;
   private static int maxHatchTickDelay = 12000;

   public FrogspawnBlock(BlockBehaviour.Properties p_221177_) {
      super(p_221177_);
   }

   public VoxelShape getShape(BlockState p_221199_, BlockGetter p_221200_, BlockPos p_221201_, CollisionContext p_221202_) {
      return SHAPE;
   }

   public boolean canSurvive(BlockState p_221209_, LevelReader p_221210_, BlockPos p_221211_) {
      return mayPlaceOn(p_221210_, p_221211_.below());
   }

   public void onPlace(BlockState p_221227_, Level p_221228_, BlockPos p_221229_, BlockState p_221230_, boolean p_221231_) {
      p_221228_.scheduleTick(p_221229_, this, getFrogspawnHatchDelay(p_221228_.getRandom()));
   }

   private static int getFrogspawnHatchDelay(RandomSource p_221186_) {
      return p_221186_.nextInt(minHatchTickDelay, maxHatchTickDelay);
   }

   public BlockState updateShape(BlockState p_221213_, Direction p_221214_, BlockState p_221215_, LevelAccessor p_221216_, BlockPos p_221217_, BlockPos p_221218_) {
      return !this.canSurvive(p_221213_, p_221216_, p_221217_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_221213_, p_221214_, p_221215_, p_221216_, p_221217_, p_221218_);
   }

   public void tick(BlockState p_221194_, ServerLevel p_221195_, BlockPos p_221196_, RandomSource p_221197_) {
      if (!this.canSurvive(p_221194_, p_221195_, p_221196_)) {
         this.destroyBlock(p_221195_, p_221196_);
      } else {
         this.hatchFrogspawn(p_221195_, p_221196_, p_221197_);
      }
   }

   public void entityInside(BlockState p_221204_, Level p_221205_, BlockPos p_221206_, Entity p_221207_) {
      if (p_221207_.getType().equals(EntityType.FALLING_BLOCK)) {
         this.destroyBlock(p_221205_, p_221206_);
      }

   }

   private static boolean mayPlaceOn(BlockGetter p_221188_, BlockPos p_221189_) {
      FluidState fluidstate = p_221188_.getFluidState(p_221189_);
      FluidState fluidstate1 = p_221188_.getFluidState(p_221189_.above());
      return fluidstate.getType() == Fluids.WATER && fluidstate1.getType() == Fluids.EMPTY;
   }

   private void hatchFrogspawn(ServerLevel p_221182_, BlockPos p_221183_, RandomSource p_221184_) {
      this.destroyBlock(p_221182_, p_221183_);
      p_221182_.playSound((Player)null, p_221183_, SoundEvents.FROGSPAWN_HATCH, SoundSource.BLOCKS, 1.0F, 1.0F);
      this.spawnTadpoles(p_221182_, p_221183_, p_221184_);
   }

   private void destroyBlock(Level p_221191_, BlockPos p_221192_) {
      p_221191_.destroyBlock(p_221192_, false);
   }

   private void spawnTadpoles(ServerLevel p_221221_, BlockPos p_221222_, RandomSource p_221223_) {
      int i = p_221223_.nextInt(2, 6);

      for(int j = 1; j <= i; ++j) {
         Tadpole tadpole = EntityType.TADPOLE.create(p_221221_);
         if (tadpole != null) {
            double d0 = (double)p_221222_.getX() + this.getRandomTadpolePositionOffset(p_221223_);
            double d1 = (double)p_221222_.getZ() + this.getRandomTadpolePositionOffset(p_221223_);
            int k = p_221223_.nextInt(1, 361);
            tadpole.moveTo(d0, (double)p_221222_.getY() - 0.5D, d1, (float)k, 0.0F);
            tadpole.setPersistenceRequired();
            p_221221_.addFreshEntity(tadpole);
         }
      }

   }

   private double getRandomTadpolePositionOffset(RandomSource p_221225_) {
      double d0 = (double)(Tadpole.HITBOX_WIDTH / 2.0F);
      return Mth.clamp(p_221225_.nextDouble(), d0, 1.0D - d0);
   }

   @VisibleForTesting
   public static void setHatchDelay(int p_221179_, int p_221180_) {
      minHatchTickDelay = p_221179_;
      maxHatchTickDelay = p_221180_;
   }

   @VisibleForTesting
   public static void setDefaultHatchDelay() {
      minHatchTickDelay = 3600;
      maxHatchTickDelay = 12000;
   }
}