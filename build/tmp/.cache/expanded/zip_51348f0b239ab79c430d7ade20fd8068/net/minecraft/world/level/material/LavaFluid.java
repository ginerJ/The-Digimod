package net.minecraft.world.level.material;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public abstract class LavaFluid extends FlowingFluid {
   public static final float MIN_LEVEL_CUTOFF = 0.44444445F;

   public Fluid getFlowing() {
      return Fluids.FLOWING_LAVA;
   }

   public Fluid getSource() {
      return Fluids.LAVA;
   }

   public Item getBucket() {
      return Items.LAVA_BUCKET;
   }

   public void animateTick(Level p_230567_, BlockPos p_230568_, FluidState p_230569_, RandomSource p_230570_) {
      BlockPos blockpos = p_230568_.above();
      if (p_230567_.getBlockState(blockpos).isAir() && !p_230567_.getBlockState(blockpos).isSolidRender(p_230567_, blockpos)) {
         if (p_230570_.nextInt(100) == 0) {
            double d0 = (double)p_230568_.getX() + p_230570_.nextDouble();
            double d1 = (double)p_230568_.getY() + 1.0D;
            double d2 = (double)p_230568_.getZ() + p_230570_.nextDouble();
            p_230567_.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            p_230567_.playLocalSound(d0, d1, d2, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + p_230570_.nextFloat() * 0.2F, 0.9F + p_230570_.nextFloat() * 0.15F, false);
         }

         if (p_230570_.nextInt(200) == 0) {
            p_230567_.playLocalSound((double)p_230568_.getX(), (double)p_230568_.getY(), (double)p_230568_.getZ(), SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 0.2F + p_230570_.nextFloat() * 0.2F, 0.9F + p_230570_.nextFloat() * 0.15F, false);
         }
      }

   }

   public void randomTick(Level p_230572_, BlockPos p_230573_, FluidState p_230574_, RandomSource p_230575_) {
      if (p_230572_.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
         int i = p_230575_.nextInt(3);
         if (i > 0) {
            BlockPos blockpos = p_230573_;

            for(int j = 0; j < i; ++j) {
               blockpos = blockpos.offset(p_230575_.nextInt(3) - 1, 1, p_230575_.nextInt(3) - 1);
               if (!p_230572_.isLoaded(blockpos)) {
                  return;
               }

               BlockState blockstate = p_230572_.getBlockState(blockpos);
               if (blockstate.isAir()) {
                  if (this.hasFlammableNeighbours(p_230572_, blockpos)) {
                     p_230572_.setBlockAndUpdate(blockpos, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(p_230572_, blockpos, p_230573_, Blocks.FIRE.defaultBlockState()));
                     return;
                  }
               } else if (blockstate.blocksMotion()) {
                  return;
               }
            }
         } else {
            for(int k = 0; k < 3; ++k) {
               BlockPos blockpos1 = p_230573_.offset(p_230575_.nextInt(3) - 1, 0, p_230575_.nextInt(3) - 1);
               if (!p_230572_.isLoaded(blockpos1)) {
                  return;
               }

               if (p_230572_.isEmptyBlock(blockpos1.above()) && this.isFlammable(p_230572_, blockpos1, Direction.UP)) {
                  p_230572_.setBlockAndUpdate(blockpos1.above(), net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(p_230572_, blockpos1.above(), p_230573_, Blocks.FIRE.defaultBlockState()));
               }
            }
         }

      }
   }

   private boolean hasFlammableNeighbours(LevelReader p_76228_, BlockPos p_76229_) {
      for(Direction direction : Direction.values()) {
         if (this.isFlammable(p_76228_, p_76229_.relative(direction), direction.getOpposite())) {
            return true;
         }
      }

      return false;
   }

   /** @deprecated Forge: use {@link LavaFluid#isFlammable(LevelReader,BlockPos,Direction)} instead */
   @Deprecated
   private boolean isFlammable(LevelReader p_76246_, BlockPos p_76247_) {
      return p_76247_.getY() >= p_76246_.getMinBuildHeight() && p_76247_.getY() < p_76246_.getMaxBuildHeight() && !p_76246_.hasChunkAt(p_76247_) ? false : p_76246_.getBlockState(p_76247_).ignitedByLava();
   }

   private boolean isFlammable(LevelReader level, BlockPos pos, Direction face) {
      return pos.getY() >= level.getMinBuildHeight() && pos.getY() < level.getMaxBuildHeight() && !level.hasChunkAt(pos) ? false : level.getBlockState(pos).isFlammable(level, pos, face);
   }

   @Nullable
   public ParticleOptions getDripParticle() {
      return ParticleTypes.DRIPPING_LAVA;
   }

   protected void beforeDestroyingBlock(LevelAccessor p_76216_, BlockPos p_76217_, BlockState p_76218_) {
      this.fizz(p_76216_, p_76217_);
   }

   public int getSlopeFindDistance(LevelReader p_76244_) {
      return p_76244_.dimensionType().ultraWarm() ? 4 : 2;
   }

   public BlockState createLegacyBlock(FluidState p_76249_) {
      return Blocks.LAVA.defaultBlockState().setValue(LiquidBlock.LEVEL, Integer.valueOf(getLegacyLevel(p_76249_)));
   }

   public boolean isSame(Fluid p_76231_) {
      return p_76231_ == Fluids.LAVA || p_76231_ == Fluids.FLOWING_LAVA;
   }

   public int getDropOff(LevelReader p_76252_) {
      return p_76252_.dimensionType().ultraWarm() ? 1 : 2;
   }

   public boolean canBeReplacedWith(FluidState p_76233_, BlockGetter p_76234_, BlockPos p_76235_, Fluid p_76236_, Direction p_76237_) {
      return p_76233_.getHeight(p_76234_, p_76235_) >= 0.44444445F && p_76236_.is(FluidTags.WATER);
   }

   public int getTickDelay(LevelReader p_76226_) {
      return p_76226_.dimensionType().ultraWarm() ? 10 : 30;
   }

   public int getSpreadDelay(Level p_76203_, BlockPos p_76204_, FluidState p_76205_, FluidState p_76206_) {
      int i = this.getTickDelay(p_76203_);
      if (!p_76205_.isEmpty() && !p_76206_.isEmpty() && !p_76205_.getValue(FALLING) && !p_76206_.getValue(FALLING) && p_76206_.getHeight(p_76203_, p_76204_) > p_76205_.getHeight(p_76203_, p_76204_) && p_76203_.getRandom().nextInt(4) != 0) {
         i *= 4;
      }

      return i;
   }

   private void fizz(LevelAccessor p_76213_, BlockPos p_76214_) {
      p_76213_.levelEvent(1501, p_76214_, 0);
   }

   protected boolean canConvertToSource(Level p_256295_) {
      return p_256295_.getGameRules().getBoolean(GameRules.RULE_LAVA_SOURCE_CONVERSION);
   }

   protected void spreadTo(LevelAccessor p_76220_, BlockPos p_76221_, BlockState p_76222_, Direction p_76223_, FluidState p_76224_) {
      if (p_76223_ == Direction.DOWN) {
         FluidState fluidstate = p_76220_.getFluidState(p_76221_);
         if (this.is(FluidTags.LAVA) && fluidstate.is(FluidTags.WATER)) {
            if (p_76222_.getBlock() instanceof LiquidBlock) {
               p_76220_.setBlock(p_76221_, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(p_76220_, p_76221_, p_76221_, Blocks.STONE.defaultBlockState()), 3);
            }

            this.fizz(p_76220_, p_76221_);
            return;
         }
      }

      super.spreadTo(p_76220_, p_76221_, p_76222_, p_76223_, p_76224_);
   }

   protected boolean isRandomlyTicking() {
      return true;
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   public Optional<SoundEvent> getPickupSound() {
      return Optional.of(SoundEvents.BUCKET_FILL_LAVA);
   }

   public static class Flowing extends LavaFluid {
      protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> p_76260_) {
         super.createFluidStateDefinition(p_76260_);
         p_76260_.add(LEVEL);
      }

      public int getAmount(FluidState p_76264_) {
         return p_76264_.getValue(LEVEL);
      }

      public boolean isSource(FluidState p_76262_) {
         return false;
      }
   }

   public static class Source extends LavaFluid {
      public int getAmount(FluidState p_76269_) {
         return 8;
      }

      public boolean isSource(FluidState p_76267_) {
         return true;
      }
   }
}
