package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BrushableBlock extends BaseEntityBlock implements Fallable {
   private static final IntegerProperty DUSTED = BlockStateProperties.DUSTED;
   public static final int TICK_DELAY = 2;
   private final Block turnsInto;
   private final SoundEvent brushSound;
   private final SoundEvent brushCompletedSound;

   public BrushableBlock(Block p_277629_, BlockBehaviour.Properties p_277373_, SoundEvent p_278060_, SoundEvent p_277352_) {
      super(p_277373_);
      this.turnsInto = p_277629_;
      this.brushSound = p_278060_;
      this.brushCompletedSound = p_277352_;
      this.registerDefaultState(this.stateDefinition.any().setValue(DUSTED, Integer.valueOf(0)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_277623_) {
      p_277623_.add(DUSTED);
   }

   public RenderShape getRenderShape(BlockState p_277553_) {
      return RenderShape.MODEL;
   }

   public void onPlace(BlockState p_277817_, Level p_277984_, BlockPos p_277869_, BlockState p_277926_, boolean p_277736_) {
      p_277984_.scheduleTick(p_277869_, this, 2);
   }

   public BlockState updateShape(BlockState p_277801_, Direction p_277455_, BlockState p_277832_, LevelAccessor p_277473_, BlockPos p_278111_, BlockPos p_277904_) {
      p_277473_.scheduleTick(p_278111_, this, 2);
      return super.updateShape(p_277801_, p_277455_, p_277832_, p_277473_, p_278111_, p_277904_);
   }

   public void tick(BlockState p_277544_, ServerLevel p_277779_, BlockPos p_278019_, RandomSource p_277471_) {
      BlockEntity blockentity = p_277779_.getBlockEntity(p_278019_);
      if (blockentity instanceof BrushableBlockEntity brushableblockentity) {
         brushableblockentity.checkReset();
      }

      if (FallingBlock.isFree(p_277779_.getBlockState(p_278019_.below())) && p_278019_.getY() >= p_277779_.getMinBuildHeight()) {
         FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(p_277779_, p_278019_, p_277544_);
         fallingblockentity.disableDrop();
      }
   }

   public void onBrokenAfterFall(Level p_278097_, BlockPos p_277734_, FallingBlockEntity p_277539_) {
      Vec3 vec3 = p_277539_.getBoundingBox().getCenter();
      p_278097_.levelEvent(2001, BlockPos.containing(vec3), Block.getId(p_277539_.getBlockState()));
      p_278097_.gameEvent(p_277539_, GameEvent.BLOCK_DESTROY, vec3);
   }

   public void animateTick(BlockState p_277390_, Level p_277525_, BlockPos p_278107_, RandomSource p_277574_) {
      if (p_277574_.nextInt(16) == 0) {
         BlockPos blockpos = p_278107_.below();
         if (FallingBlock.isFree(p_277525_.getBlockState(blockpos))) {
            double d0 = (double)p_278107_.getX() + p_277574_.nextDouble();
            double d1 = (double)p_278107_.getY() - 0.05D;
            double d2 = (double)p_278107_.getZ() + p_277574_.nextDouble();
            p_277525_.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, p_277390_), d0, d1, d2, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   public @Nullable BlockEntity newBlockEntity(BlockPos p_277683_, BlockState p_277381_) {
      return new BrushableBlockEntity(p_277683_, p_277381_);
   }

   public Block getTurnsInto() {
      return this.turnsInto;
   }

   public SoundEvent getBrushSound() {
      return this.brushSound;
   }

   public SoundEvent getBrushCompletedSound() {
      return this.brushCompletedSound;
   }
}