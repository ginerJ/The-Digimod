package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class EndGatewayBlock extends BaseEntityBlock {
   public EndGatewayBlock(BlockBehaviour.Properties p_52999_) {
      super(p_52999_);
   }

   public BlockEntity newBlockEntity(BlockPos p_153193_, BlockState p_153194_) {
      return new TheEndGatewayBlockEntity(p_153193_, p_153194_);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153189_, BlockState p_153190_, BlockEntityType<T> p_153191_) {
      return createTickerHelper(p_153191_, BlockEntityType.END_GATEWAY, p_153189_.isClientSide ? TheEndGatewayBlockEntity::beamAnimationTick : TheEndGatewayBlockEntity::teleportTick);
   }

   public void animateTick(BlockState p_221097_, Level p_221098_, BlockPos p_221099_, RandomSource p_221100_) {
      BlockEntity blockentity = p_221098_.getBlockEntity(p_221099_);
      if (blockentity instanceof TheEndGatewayBlockEntity) {
         int i = ((TheEndGatewayBlockEntity)blockentity).getParticleAmount();

         for(int j = 0; j < i; ++j) {
            double d0 = (double)p_221099_.getX() + p_221100_.nextDouble();
            double d1 = (double)p_221099_.getY() + p_221100_.nextDouble();
            double d2 = (double)p_221099_.getZ() + p_221100_.nextDouble();
            double d3 = (p_221100_.nextDouble() - 0.5D) * 0.5D;
            double d4 = (p_221100_.nextDouble() - 0.5D) * 0.5D;
            double d5 = (p_221100_.nextDouble() - 0.5D) * 0.5D;
            int k = p_221100_.nextInt(2) * 2 - 1;
            if (p_221100_.nextBoolean()) {
               d2 = (double)p_221099_.getZ() + 0.5D + 0.25D * (double)k;
               d5 = (double)(p_221100_.nextFloat() * 2.0F * (float)k);
            } else {
               d0 = (double)p_221099_.getX() + 0.5D + 0.25D * (double)k;
               d3 = (double)(p_221100_.nextFloat() * 2.0F * (float)k);
            }

            p_221098_.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
         }

      }
   }

   public ItemStack getCloneItemStack(BlockGetter p_53003_, BlockPos p_53004_, BlockState p_53005_) {
      return ItemStack.EMPTY;
   }

   public boolean canBeReplaced(BlockState p_53012_, Fluid p_53013_) {
      return false;
   }
}