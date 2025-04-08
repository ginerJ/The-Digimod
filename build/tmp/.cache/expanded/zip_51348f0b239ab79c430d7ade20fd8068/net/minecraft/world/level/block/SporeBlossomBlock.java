package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SporeBlossomBlock extends Block {
   private static final VoxelShape SHAPE = Block.box(2.0D, 13.0D, 2.0D, 14.0D, 16.0D, 14.0D);
   private static final int ADD_PARTICLE_ATTEMPTS = 14;
   private static final int PARTICLE_XZ_RADIUS = 10;
   private static final int PARTICLE_Y_MAX = 10;

   public SporeBlossomBlock(BlockBehaviour.Properties p_154697_) {
      super(p_154697_);
   }

   public boolean canSurvive(BlockState p_154709_, LevelReader p_154710_, BlockPos p_154711_) {
      return Block.canSupportCenter(p_154710_, p_154711_.above(), Direction.DOWN) && !p_154710_.isWaterAt(p_154711_);
   }

   public BlockState updateShape(BlockState p_154713_, Direction p_154714_, BlockState p_154715_, LevelAccessor p_154716_, BlockPos p_154717_, BlockPos p_154718_) {
      return p_154714_ == Direction.UP && !this.canSurvive(p_154713_, p_154716_, p_154717_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_154713_, p_154714_, p_154715_, p_154716_, p_154717_, p_154718_);
   }

   public void animateTick(BlockState p_222503_, Level p_222504_, BlockPos p_222505_, RandomSource p_222506_) {
      int i = p_222505_.getX();
      int j = p_222505_.getY();
      int k = p_222505_.getZ();
      double d0 = (double)i + p_222506_.nextDouble();
      double d1 = (double)j + 0.7D;
      double d2 = (double)k + p_222506_.nextDouble();
      p_222504_.addParticle(ParticleTypes.FALLING_SPORE_BLOSSOM, d0, d1, d2, 0.0D, 0.0D, 0.0D);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int l = 0; l < 14; ++l) {
         blockpos$mutableblockpos.set(i + Mth.nextInt(p_222506_, -10, 10), j - p_222506_.nextInt(10), k + Mth.nextInt(p_222506_, -10, 10));
         BlockState blockstate = p_222504_.getBlockState(blockpos$mutableblockpos);
         if (!blockstate.isCollisionShapeFullBlock(p_222504_, blockpos$mutableblockpos)) {
            p_222504_.addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, (double)blockpos$mutableblockpos.getX() + p_222506_.nextDouble(), (double)blockpos$mutableblockpos.getY() + p_222506_.nextDouble(), (double)blockpos$mutableblockpos.getZ() + p_222506_.nextDouble(), 0.0D, 0.0D, 0.0D);
         }
      }

   }

   public VoxelShape getShape(BlockState p_154699_, BlockGetter p_154700_, BlockPos p_154701_, CollisionContext p_154702_) {
      return SHAPE;
   }
}